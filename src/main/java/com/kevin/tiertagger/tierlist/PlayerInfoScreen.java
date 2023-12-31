package com.kevin.tiertagger.tierlist;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kevin.tiertagger.TierCache;
import com.kevin.tiertagger.model.PlayerInfo;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Setter
public class PlayerInfoScreen extends Screen {
    private static final Map<String, UUID> uuidCache = new HashMap<>();

    private static final Map<String, String> MODE_NAMES = Map.of(
            "vanilla", "☄ Vanilla",
            "sword", "\uD83D\uDDE1 Sword",
            "uhc", "\uD83E\uDEA3 UHC",
            "pot", "\uD83E\uDDEA Pot",
            "neth_pot", "❤ NethPot",
            "smp", "\uD83D\uDD25 SMP",
            "axe", "\uD83E\uDE93 Axe"
    );

    private final Screen parent;

    private String player;
    private Identifier texture;
    private PlayerInfo info;

    public PlayerInfoScreen(Screen parent, String player, PlayerInfo info) {
        super(Text.of("Player Info"));
        this.parent = parent;
        this.player = player;
        this.info = info;
    }

    @Override
    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> MinecraftClient.getInstance().setScreen(parent))
                .dimensions(this.width / 2 - 100, this.height - 27, 200, 20)
                .build());

        this.fetchTexture(this.player).thenAccept(this::setTexture);

        if (this.info == null) {
            fetchUUID(player).thenApply(u -> TierCache.getPlayerInfo(u).orElseThrow()).thenAccept(this::setInfo).exceptionally(t -> {
                log.error("Could not fetch player info", t);
                return null;
            });
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, this.player + "'s profile", this.width / 2, 20, 0xFFFFFF);

        if (this.texture != null && this.info != null) {
            context.drawTexture(texture, this.width / 2 - 65, (this.height - 135) / 2, 0, 0, 60, 135, 60, 135);

            int rankingHeight = this.info.rankings().size() * 10;
            int infoHeight = 55; // 4 lines of text (10 px tall) + 5 px padding
            int startY = (this.height - infoHeight - rankingHeight) / 2;

            context.drawTextWithShadow(this.textRenderer, getRegionText(this.info), this.width / 2 + 5, startY, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, getPointsText(this.info), this.width / 2 + 5, startY + 15, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, getRankText(this.info), this.width / 2 + 5, startY + 30, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "Rankings:", this.width / 2 + 5, startY + 45, 0xFFFFFF);

            int rankingY = startY + infoHeight;
            for (PlayerInfo.NamedRanking namedRanking : this.info.getSortedTiers()) {
                String mode = namedRanking.name();
                PlayerInfo.Ranking ranking = namedRanking.ranking();

                context.drawTextWithShadow(this.textRenderer, formatTier(mode, ranking), this.width / 2 + 5, rankingY, 0xFFFFFF);
                rankingY += 10;
            }
        } else {
            context.drawCenteredTextWithShadow(this.textRenderer, "Loading... (or player not found)", this.width / 2, this.height / 2, 0xFFFFFF);
        }
    }

    @Override
    public void renderBackground(DrawContext context) {
        super.renderBackground(context);

        if (MinecraftClient.getInstance().world == null) {
            context.setShaderColor(0.125F, 0.125F, 0.125F, 1.0F);
            context.drawTexture(
                    Screen.OPTIONS_BACKGROUND_TEXTURE,
                    0,
                    32,
                    this.width,
                    this.height - 32.0f,
                    this.width,
                    (this.height - 32) - 32,
                    32,
                    32
            );
            context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            context.setShaderColor(0.25F, 0.25F, 0.25F, 1.0F);
            context.drawTexture(Screen.OPTIONS_BACKGROUND_TEXTURE, 0, 0, 0.0F, 0.0F, this.width, 32, 32, 32);
            context.drawTexture(Screen.OPTIONS_BACKGROUND_TEXTURE, 0, this.height - 32, 0.0F, this.height - 32.0f, this.width, 32, 32, 32);
            context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            context.fillGradient(RenderLayer.getGuiOverlay(), 0, 32, this.width, 32 + 4, -16777216, 0, 0);
            context.fillGradient(RenderLayer.getGuiOverlay(), 0, this.height - 32 - 4, this.width, this.height - 32, 0, -16777216, 0);
        }
    }

    @Override
    public void close() {
        if (this.client != null) this.client.setScreen(parent);
    }

    private boolean textureExists(Identifier texture) {
        return MinecraftClient.getInstance().getTextureManager().getOrDefault(texture, null) != null;
    }

    private CompletableFuture<Identifier> fetchTexture(String user) {
        String username = user.toLowerCase();
        Identifier tex = new Identifier("tiertagger", "player_" + username);

        if (textureExists(tex)) return CompletableFuture.completedFuture(tex);

        return fetchUUID(username)
                .thenApply(uuid -> {
                    TextureManager texManager = MinecraftClient.getInstance().getTextureManager();

                    try {
                        URL url = new URL(" https://crafatar.com/renders/body/" + uuid + "?overlay");
                        try (InputStream stream = url.openStream()) {
                            NativeImage image = NativeImage.read(stream);
                            texManager.registerTexture(tex, new NativeImageBackedTexture(image));
                            return tex;
                        }
                    } catch (IOException e) {
                        log.error("Could not fetch head texture", e);
                        throw new CompletionException(e);
                    }
                });
    }

    public static CompletableFuture<UUID> fetchUUID(String user) {
        String username = user.toLowerCase();

        if (uuidCache.containsKey(username)) {
            return CompletableFuture.completedFuture(uuidCache.get(username));
        }

        HttpRequest uuidReq = HttpRequest.newBuilder(URI.create("https://api.mojang.com/users/profiles/minecraft/" + username))
                .GET().build();

        return HttpClient.newHttpClient().sendAsync(uuidReq, HttpResponse.BodyHandlers.ofString())
                .thenApply(res -> {
                    if (res.statusCode() != 200) {
                        log.error("Error while getting UUID of " + username + ": " + res.statusCode() + " " + res.body());
                        throw new CompletionException(new NoSuchElementException("Error while getting UUID of " + username));
                    }

                    JsonObject json = new Gson().fromJson(res.body(), JsonObject.class);
                    String uuid = json.get("id").getAsString();

                    uuid(uuid).ifPresent(u -> uuidCache.put(username, u));

                    return uuidCache.get(username);
                });
    }

    private static Optional<UUID> uuid(String u) {
        try {
            long most = Long.parseUnsignedLong(u.substring(0, 16), 16);
            long least = Long.parseUnsignedLong(u.substring(16), 16);
            return Optional.of(new UUID(most, least));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Text formatTier(String mode, PlayerInfo.Ranking tier) {
        MutableText tierText = getTierText(tier.tier(), tier.pos(), tier.retired());

        if (tier.peakTier() != null && tier.peakPos() != null && tier.peakTier() <= tier.tier() && tier.peakPos() < tier.pos()) {
            tierText = tierText.append(Text.literal(" (peak: ").styled(s -> s.withColor(Formatting.GRAY)))
                    .append(getTierText(tier.peakTier(), tier.peakPos(), tier.retired()))
                    .append(Text.literal(")").styled(s -> s.withColor(Formatting.GRAY)));
        }

        return Text.empty()
                .append(Text.literal(MODE_NAMES.getOrDefault(mode, mode) + ": ").styled(s -> s.withColor(Formatting.GRAY)))
                .append(tierText);
    }

    private MutableText getTierText(int tier, int pos, boolean retired) {
        StringBuilder text = new StringBuilder();
        if (retired) text.append("R");
        text.append(pos == 0 ? "H" : "L").append("T").append(tier);

        int color;
        if (retired) {
            color = 0xa177ff;
        } else {
            color = pos == 0 ? 0x3e71f4 : 0x80b5ff;
        }

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
            case 1 -> 0xffd700;
            case 2 -> 0xc0c0c0;
            case 3 -> 0xcd7f32;
            default -> 0xffffff;
        };

        return Text.empty()
                .append(Text.literal("Global rank: "))
                .append(Text.literal("#" + info.overall()).styled(s -> s.withColor(color)));
    }
}
