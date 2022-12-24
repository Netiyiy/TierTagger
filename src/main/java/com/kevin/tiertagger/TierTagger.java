package com.kevin.tiertagger;

import net.fabricmc.api.ModInitializer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public class TierTagger implements ModInitializer {

    private static final TiersManager tiers = new TiersManager();

    @Override
    public void onInitialize() {
        System.out.println("Hello World!");
        tiers.tiersLoader();
    }

    public static Text appendTier(Text text) {

        MutableText tier = getPlayerTier(text.getString());
        if (tier != null) {
            tier.append(Text.literal(" | ").styled(s -> s.withColor(Formatting.GRAY)));
            return tier.append(text);
        }

        return text;
    }

    @Nullable
    private static MutableText getPlayerTier(String username) {

        if (tiers.playerTiers.containsKey(username)) {
            String foundTier = tiers.playerTiers.get(username);
            MutableText tier = Text.literal(foundTier);
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
            case "Ht1" -> 0xFF0000; // red
            case "Lt1" -> 0xFFB6C1; // light pink
            case "Ht2" -> 0xFFA500; // orange
            case "Lt2" -> 0xFFE4B5; // light orange
            case "Ht3" -> 0xDAA520; // goldenrod
            case "Lt3" -> 0xEEE8AA; // pale goldenrod
            case "Ht4" -> 0x006400; // dark green
            case "Lt4" -> 0x90EE90; // light green
            case "Ht5" -> 0x808080; // grey
            case "Lt5" -> 0xD3D3D3; // pale grey
            default -> 0xD3D3D3; // DEFAULT: pale grey
        };
    }



}
