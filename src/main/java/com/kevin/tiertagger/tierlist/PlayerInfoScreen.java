package com.kevin.tiertagger.tierlist;

import com.kevin.tiertagger.TierCache;
import com.kevin.tiertagger.TierTagger;
import com.kevin.tiertagger.model.GameMode;
import com.kevin.tiertagger.model.PlayerInfo;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.uku3lig.ukulib.config.screen.CloseableScreen;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

@Setter
public class PlayerInfoScreen extends CloseableScreen {
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private String player;
    private Identifier texture;
    private PlayerInfo info;
    private boolean everythingIsAwesome = true;

    public PlayerInfoScreen(Screen parent, String player) {
        super(Text.of("Player Info"), parent);
        this.player = player;
    }

    @Override
    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> MinecraftClient.getInstance().setScreen(parent))
                .dimensions(this.width / 2 - 100, this.height - 27, 200, 20)
                .build());

        this.fetchTexture(this.player).thenAccept(this::setTexture);

        if (this.info == null) {
            TierCache.searchPlayer(this.player).thenAccept(this::setInfo)
                    .whenComplete((v, t) -> {
                        if (t != null) {
                            this.everythingIsAwesome = false;
                        }
                    });
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        String name = this.info == null ? this.player : this.info.name();
        context.drawCenteredTextWithShadow(this.textRenderer, name + "'s profile", this.width / 2, 20, 0xFFFFFF);

        if (this.texture != null && this.info != null) {
            context.drawTexture(texture, this.width / 2 - 65, (this.height - 144) / 2, 0, 0, 60, 144, 60, 144);

            int rankingHeight = this.info.rankings().size() * 10;
            int infoHeight = 56; // 4 lines of text (10 px tall) + 6 px padding
            int startY = (this.height - infoHeight - rankingHeight) / 2;

            context.drawTextWithShadow(this.textRenderer, getRegionText(this.info), this.width / 2 + 5, startY, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, getPointsText(this.info), this.width / 2 + 5, startY + 15, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, getRankText(this.info), this.width / 2 + 5, startY + 30, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "Rankings:", this.width / 2 + 5, startY + 45, 0xFFFFFF);

            int rankingY = startY + infoHeight;
            for (PlayerInfo.NamedRanking namedRanking : this.info.getSortedTiers()) {
                // ugly "fix" to avoid crashes if upstream doesn't have the right names
                if (namedRanking.mode() == null) continue;

                TextWidget text = new TextWidget(formatTier(namedRanking.mode(), namedRanking.ranking()), this.textRenderer);
                text.setX(this.width / 2 + 5);
                text.setY(rankingY);

                String date = DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneOffset.UTC).format(Instant.ofEpochSecond(namedRanking.ranking().attained()));
                Text tooltipText = Text.literal("Attained: " + date + "\nPoints: " + points(namedRanking.ranking())).formatted(Formatting.GRAY);
                text.setTooltip(Tooltip.of(tooltipText));

                this.addDrawableChild(text);
                rankingY += 11;
            }
        } else {
            String text = this.everythingIsAwesome ? "Loading..." : "Unknown player";
            context.drawCenteredTextWithShadow(this.textRenderer, text, this.width / 2, this.height / 2, 0xFFFFFF);
        }
    }

    private boolean textureExists(Identifier texture) {
        return MinecraftClient.getInstance().getTextureManager().getOrDefault(texture, null) != null;
    }

    private CompletableFuture<Identifier> fetchTexture(String user) {
        String username = user.toLowerCase();
        Identifier tex = Identifier.of(TierTagger.MOD_ID, "player_" + username);

        if (textureExists(tex)) return CompletableFuture.completedFuture(tex);

        TextureManager texManager = MinecraftClient.getInstance().getTextureManager();
        HttpRequest req = HttpRequest.newBuilder(URI.create("https://mc-heads.net/body/" + username + "/240")).GET().build();

        return HTTP_CLIENT.sendAsync(req, HttpResponse.BodyHandlers.ofByteArray()).thenApply(r -> {
            if (r.statusCode() == 200) {
                try {
                    NativeImage image = NativeImage.read(r.body());
                    texManager.registerTexture(tex, new NativeImageBackedTexture(image));
                } catch (IOException e) {
                    TierTagger.getLogger().error("Failed to register head texture", e);
                }
            } else {
                TierTagger.getLogger().error("Could not fetch head texture: {} {}", r.statusCode(), new String(r.body()));
            }

            return tex;
        });
    }

    private Text formatTier(@NotNull GameMode mode, PlayerInfo.Ranking tier) {
        MutableText tierText = getTierText(tier.tier(), tier.pos(), tier.retired());

        if (tier.comparablePeak() < tier.comparableTier()) {
            // warning caused by potential NPE by unboxing of peak{Tier,Pos} which CANNOT happen, see impl of comparablePeak
            // noinspection DataFlowIssue
            tierText = tierText.append(Text.literal(" (peak: ").styled(s -> s.withColor(Formatting.GRAY)))
                    .append(getTierText(tier.peakTier(), tier.peakPos(), tier.retired()))
                    .append(Text.literal(")").styled(s -> s.withColor(Formatting.GRAY)));
        }

        return Text.empty()
                .append(mode.formatted())
                .append(Text.literal(": ").formatted(Formatting.GRAY))
                .append(tierText);
    }

    private MutableText getTierText(int tier, int pos, boolean retired) {
        StringBuilder text = new StringBuilder();
        if (retired) text.append("R");
        text.append(pos == 0 ? "H" : "L").append("T").append(tier);

        int color = TierTagger.getTierColor(text.toString());
        return Text.literal(text.toString()).styled(s -> s.withColor(color));
    }

    private Text getRegionText(PlayerInfo info) {
        return Text.empty()
                .append(Text.literal("Region: "))
                .append(Text.literal(info.region()).styled(s -> s.withColor(info.getRegionColor())));
    }

    private Text getPointsText(PlayerInfo info) {
        PlayerInfo.PointInfo pointInfo = info.getPointInfo();

        return Text.empty()
                .append(Text.literal("Points: "))
                .append(Text.literal(info.points() + " ").styled(s -> s.withColor(pointInfo.getColor())))
                .append(Text.literal("(" + pointInfo.getTitle() + ")").styled(s -> s.withColor(pointInfo.getAccentColor())));
    }

    private Text getRankText(PlayerInfo info) {
        int color = switch (info.overall()) {
            case 1 -> 0xe5ba43;
            case 2 -> 0x808c9c;
            case 3 -> 0xb56326;
            default -> 0x1e2634;
        };

        return Text.empty()
                .append(Text.literal("Global rank: "))
                .append(Text.literal("#" + info.overall()).styled(s -> s.withColor(color)));
    }

    private int points(PlayerInfo.Ranking ranking) {
        String tier = getTierText(ranking.tier(), ranking.pos(), false).getString();

        return switch (tier) {
            case "HT1" -> 60;
            case "LT1" -> 45;
            case "HT2" -> 30;
            case "LT2" -> 20;
            case "HT3" -> 10;
            case "LT3" -> 6;
            case "HT4" -> 4;
            case "LT4" -> 3;
            case "HT5" -> 2;
            case "LT5" -> 1;
            default -> 0;
        };
    }
}
