package com.kevin.tiertagger;

import com.kevin.tiertagger.config.TierTaggerConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.uku3lig.ukulib.config.ConfigManager;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Slf4j
public class TierTagger implements ModInitializer {
    @Getter
    private static final ConfigManager<TierTaggerConfig> manager = ConfigManager.create(TierTaggerConfig.class, "tiertagger");

    @Override
    public void onInitialize() {
    }

    public static Text appendTier(PlayerEntity player, Text text) {
        MutableText following = getPlayerTier(player.getUuid());

        if (following != null) {
            following.append(new LiteralText(" | ").formatted(Formatting.GRAY));
            return following.append(text);
        }

        return text;
    }

    @Nullable
    private static MutableText getPlayerTier(UUID uuid) {
        String mode = manager.getConfig().getGameMode().getApiKey();

        return TierCache.getPlayerInfo(uuid)
                .map(i -> i.getRankings().get(mode))
                .map(r -> (r.getPos() == 0 ? "H" : "L") + "T" + r.getTier())
                .map(t -> new LiteralText(t).styled(s -> s.withColor(getTierColor(t))))
                .orElse(null);
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