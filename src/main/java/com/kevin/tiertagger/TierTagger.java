package com.kevin.tiertagger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class TierTagger implements ModInitializer {
    public ClientReceiveMessageEvents.AllowChat listener;
    public static final URI ENDPOINT = URI.create("https://api.uku3lig.net/tiers/vanilla");
    public static MessageData lastMsg;
    private static final Map<String, String> tiers = new HashMap<>();
    public static long hue = 0;

    @Override
    public void onInitialize() {
        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder(ENDPOINT).GET().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(s -> {
                    JsonObject o = new Gson().fromJson(s, JsonObject.class);
                    o.entrySet().forEach(e -> tiers.put(e.getKey(), e.getValue().getAsString()));
                })
                .whenComplete((s, t) -> System.out.println("success"));
    }

    public static Text appendTier(PlayerEntity player, Text text) {

        MutableText tier = getPlayerTier(player.getEntityName());
        if (tier != null) {
            tier.append(Text.of(" | ").copy().styled(s -> s.withColor(getTierColor(getPlayerTier((player.getName().getString())).getString()))));
            return tier.append(text);
        }

        return text;
    }

//  Ooh_Netiyiy
    @Nullable
    public static MutableText getPlayerTier(String username) {
        if (tiers.containsKey(username)) {
            String foundTier = tiers.get(username);
            MutableText tier = Text.of(foundTier).copy();

            if(username.equals("Ooh_Netiyiy")){
                tier.styled(s -> s.withColor((int) ((hue))));
                return tier.append(": ");
            }
            if(username.equals("cire3")){
                return Text.of("").copy().styled(s -> s.withColor(0xDAA520)); //w color;
            }
            int color = getTierColor(foundTier);
            tier.styled(s -> s.withColor(color));
            return tier.append(": ");
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

    public record MessageData(GameProfile gp, Text tx){}
}
