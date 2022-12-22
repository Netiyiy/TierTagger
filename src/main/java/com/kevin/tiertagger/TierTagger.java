package com.kevin.tiertagger;

import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class TierTagger implements ModInitializer {

    public static final HashMap<String, Integer> TIER_COLORS = new HashMap<>() {{
        put("Ht1", 0xFF0000); // red
        put("Lt1", 0xFFB6C1); // light pink
        put("Ht2", 0xFFA500); // orange
        put("Lt2", 0xFFE4B5); // light orange
        put("Ht3", 0xDAA520); // goldenrod
        put("Lt3", 0xEEE8AA); // pale goldenrod
        put("Ht4", 0x006400); // dark green
        put("Lt4", 0x90EE90); // light green
        put("Ht5", 0x808080); // grey
        put("Lt5", 0xD3D3D3); // pale grey
    }};

    private static final TiersManager tiers = new TiersManager();

    @Override
    public void onInitialize() {
        System.out.println("Hello World!");
        tiers.tiersLoader();
    }

    public static Text appendTier(Text text) {

        MutableText tier = getPlayerTier(text.toString());
        MutableText username = text.copy();

        if (tier != null) {
            tier.append(Text.literal(" | ").styled(s -> s.withColor(Formatting.GRAY)));
            return tier.append(username);
        }

        return username;
    }

    @Nullable
    private static MutableText getPlayerTier(String username) {
        int color = getTierColor(username);
        MutableText tier = (MutableText) Text.of(tiers.playerTiers.get(username));
        if (tier == null) {
            return null;
        } else {
            return tier.styled(style -> style.withColor(color));
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
