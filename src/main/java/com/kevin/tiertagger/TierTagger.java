package com.kevin.tiertagger;

import com.kevin.tiertagger.config.TierTaggerConfig;
import com.kevin.tiertagger.model.PlayerInfo;
import com.mojang.brigadier.context.CommandContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.uku3lig.ukulib.config.ConfigManager;
import net.uku3lig.ukulib.utils.PlayerArgumentType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

@Slf4j
public class TierTagger implements ModInitializer {
    @Getter
    private static final ConfigManager<TierTaggerConfig> manager = ConfigManager.create(TierTaggerConfig.class, "tiertagger");

    @Override
    public void onInitialize() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registry) -> dispatcher.register(
                literal("tiertagger")
                        .then(argument("player", PlayerArgumentType.player())
                                .executes(TierTagger::displayTierInfo))));
    }

    public static Text appendTier(PlayerEntity player, Text text) {
        MutableText following = switch (manager.getConfig().getShownStatistic()) {
            case TIER -> getPlayerTier(player.getUuid());
            case RANK -> TierCache.getPlayerInfo(player.getUuid())
                    .map(i -> Text.literal("#" + i.getOverall()))
                    .orElse(null);
        };

        if (following != null) {
            following.append(Text.literal(" | ").formatted(Formatting.GRAY));
            return following.append(text);
        }

        return text;
    }

    @Nullable
    private static MutableText getPlayerTier(UUID uuid) {
        String mode = manager.getConfig().getGameMode().getApiKey();

        return TierCache.getPlayerInfo(uuid)
                .map(i -> i.getRankings().get(mode))
                .map(TierTagger::getTierText)
                .map(t -> Text.literal(t).styled(s -> s.withColor(getTierColor(t))))
                .orElse(null);
    }

    @Nullable
    private static String getTierText(PlayerInfo.Ranking ranking) {
        if (ranking.isRetired() && ranking.getPeakTier() != null && ranking.getPeakPos() != null) {
            if (!manager.getConfig().isShowRetired()) {
                return null; // don't show retired
            } else {
                return "R" + (ranking.getPeakPos() == 0 ? "H" : "L") + "T" + ranking.getPeakTier();
            }
        } else {
            return (ranking.getPos() == 0 ? "H" : "L") + "T" + ranking.getTier();
        }
    }

    private static int displayTierInfo(CommandContext<FabricClientCommandSource> ctx) {
        PlayerArgumentType.PlayerSelector selector = ctx.getArgument("player", PlayerArgumentType.PlayerSelector.class);

        Optional<PlayerInfo> info = ctx.getSource().getWorld().getPlayers().stream()
                .filter(p -> p.getEntityName().equalsIgnoreCase(selector.name()) || p.getUuidAsString().equalsIgnoreCase(selector.name()))
                .findFirst()
                .map(Entity::getUuid)
                .flatMap(TierCache::getPlayerInfo);

        if (info.isPresent()) {
            ctx.getSource().sendFeedback(printPlayerInfo(info.get()));
        } else {
            ctx.getSource().sendFeedback(Text.of("[TierTagger] Searching..."));
            TierCache.searchPlayer(selector.name())
                    .thenAccept(p -> ctx.getSource().sendFeedback(printPlayerInfo(p)))
                    .exceptionally(t -> {
                        ctx.getSource().sendError(Text.of("Could not find player " + selector.name()));
                        log.error("MIGUEL", t);
                        return null;
                    });
        }

        return 0;
    }

    private static Text printPlayerInfo(PlayerInfo info) {
        MutableText text = Text.empty().append("=== Rankings for " + info.getName() + " ===");

        info.getRankings().forEach((m, r) -> {
            String tier = getTierText(r);

            if (tier != null) {
                Text tierText = Text.literal(tier).styled(s -> s.withColor(getTierColor(tier)));
                text.append(Text.literal("\n" + m + ": ").append(tierText));
            }
        });

        return text;
    }

    private static int getTierColor(String tier) {
        if (tier.startsWith("R")) {
            return 0x662B99; // ourple
        }

        return switch (tier) {
            case "HT1" -> 0xFF0000; // red
            case "LT1" -> 0xFFB6C1; // light pink
            case "HT2" -> 0xFFA500; // orange
            case "LT2" -> 0xFFE4B5; // light orange
            case "HT3" -> 0xDAA520; // goldenrod
            case "LT3" -> 0xEEE8AA; // pale goldenrod
            case "HT4" -> 0x006400; // dark green
            case "LT4" -> 0x90EE90; // light green
            case "HT5" -> 0x808080; // grey
            case "LT5" -> 0xD3D3D3; // pale grey
            default -> 0xD3D3D3; // DEFAULT: pale grey
        };
    }
}