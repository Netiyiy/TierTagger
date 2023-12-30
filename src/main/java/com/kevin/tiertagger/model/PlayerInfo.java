package com.kevin.tiertagger.model;

import com.google.gson.Gson;
import com.kevin.tiertagger.TierTagger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record PlayerInfo(Map<String, Ranking> rankings) {
    public record Ranking(int tier, int pos) {
    }

    public static CompletableFuture<PlayerInfo> get(HttpClient client, UUID uuid) {
        String endpoint = TierTagger.getManager().getConfig().getApiUrl() + "/profile/" + uuidStr(uuid);
        final HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint)).GET().build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(s -> new Gson().fromJson(s, PlayerInfo.class));
    }

    private static String uuidStr(UUID uuid) {
        return Long.toUnsignedString(uuid.getMostSignificantBits(), 16) + Long.toUnsignedString(uuid.getLeastSignificantBits(), 16);
    }
}
