package com.kevin.tiertagger;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TierTagger implements ModInitializer {

    private static final TiersManager tiers = new TiersManager();
    private static final Logger logger = LoggerFactory.getLogger("TierTagger");

    @Override
    public void onInitialize() {
        tiers.loadTiers();
    }

    public static Text appendTier(PlayerEntity player, Text text) {

        MutableText tier = getPlayerTier(player.getEntityName());
        if (tier != null) {
            tier.append(Text.of(" | ").copy().styled(s -> s.withColor(Formatting.GRAY)));
            return tier.append(text);
        }

        return text;
    }

    @Nullable
    private static MutableText getPlayerTier(String username) {
        if (tiers.playerTiers.containsKey(username)) {
            String foundTier = tiers.playerTiers.get(username);
            MutableText tier = Text.of(foundTier).copy();
            if (username.equals("Ooh_Netiyiy")) {
                tier.styled(s -> s.withColor(TextColor.parse("#A020F0")));
            } else {
                int color = getTierColor(foundTier);
                tier.styled(s -> s.withColor(color));
            }
            return tier;
        } else {
            return null;
        }
    }

    private static int getTierColor(String tier) {

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

    public static Logger getLogger() {
        return logger;
    }

}
