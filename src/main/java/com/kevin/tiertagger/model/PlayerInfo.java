package com.kevin.tiertagger.model;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Data
@SuppressWarnings("ClassCanBeRecord") // records don't work with the current version of gson
public final class PlayerInfo {
    private static final String ENDPOINT = "https://mctiers.com/api/profile/%s";

    // other fields stripped out for conciseness
    private final Map<String, Ranking> rankings;

    @Data
    public static final class Ranking {
        private final int tier;
        private final int pos;
    }

    public static CompletableFuture<PlayerInfo> get(HttpClient client, UUID uuid) {
        URI formattedEndpoint = URI.create(ENDPOINT.formatted(uuidStr(uuid)));
        final HttpRequest request = HttpRequest.newBuilder(formattedEndpoint).GET().build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(s -> new Gson().fromJson(s, PlayerInfo.class));
    }

    @Getter
    @AllArgsConstructor
    public enum PointInfo {
        COMBAT_MASTER("Combat Master", 0xFBB03B, 0xFFD13A),
        COMBAT_ACE("Combat Ace", 0xCD285C, 0xD65474),
        COMBAT_SPECIALIST("Combat Specialist", 0xAD78D8, 0xC7A3E8),
        COMBAT_CADET("Combat Cadet", 0x9291D9, 0xADACE2),
        COMBAT_NOVICE("Combat Novice", 0x9291D9, 0xFFFFFF),
        ROOKIE("Rookie", 0x6C7178, 0x8B979C),
        UNRANKED("Unranked", 0xFFFFFF, 0xFFFFFF);

        private final String title;
        private final int color;
        private final int accentColor;
    }

    private static String uuidStr(UUID uuid) {
        return uuid.toString().replace("-", "");
    }
}