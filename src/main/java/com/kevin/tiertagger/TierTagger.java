package com.kevin.tiertagger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kevin.tiertagger.modmenu.ModMenuEntry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class TierTagger implements ModInitializer {

    private static final Map<String, String> tiers = new HashMap<>();

    @Override
    public void onInitialize() {
        reloadTiers();
    }

    public static void reloadTiers() {
        final URI ENDPOINT = URI.create("https://api.uku3lig.net/tiers/" + ModMenuEntry.gamemode.toString());
        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder(ENDPOINT).GET().build();
        tiers.clear();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(s -> {
                    JsonObject o = new Gson().fromJson(s, JsonObject.class);
                    o.entrySet().forEach(e -> tiers.put(e.getKey(), e.getValue().getAsString()));
                })
                .whenComplete((s, t) -> System.out.println(tiers.get("Ooh_Netiyiy") + " | Ooh_Netiyiy"));
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
        if (tiers.containsKey(username)) {
            String foundTier = tiers.get(username);
            MutableText tier = Text.of(foundTier).copy();
            if (username.equals("Ooh_Netiyiy")) {
                tier.styled(s -> s.withColor(TextColor.parse("#A020F0")));
            } else {
                int color = getTierColor(foundTier);
                tier.styled(s -> s.withColor(color));
            }
            return tier;
        } else if (ModMenuEntry.showUnranked) {
            return Text.of("?").copy();
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

}