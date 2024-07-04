package com.kevin.tiertagger.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.kevin.tiertagger.TierTagger;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

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
    // other fields stripped out for conciseness
    private final String uuid;
    private final String name;
    private final Map<String, Ranking> rankings;
    private final int overall;

    @Data
    public static final class Ranking {
        private final int tier;
        private final int pos;

        @Nullable
        @SerializedName("peak_tier")
        private final Integer peakTier;

        @Nullable
        @SerializedName("peak_pos")
        private final Integer peakPos;

        private final boolean retired;
    }

    public static CompletableFuture<PlayerInfo> get(HttpClient client, UUID uuid) {
        String endpoint = TierTagger.getManager().getConfig().getBaseUrl() + "/profile/" + uuid.toString().replace("-", "");
        final HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint)).GET().build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(s -> new Gson().fromJson(s, PlayerInfo.class))
                .whenComplete((i, t) -> {
                    if (t != null) TierTagger.getLogger().warn("Error getting player info ({})", uuid, t);
                });
    }

    public static CompletableFuture<PlayerInfo> search(HttpClient client, String query) {
        String endpoint = TierTagger.getManager().getConfig().getBaseUrl() + "/search_profile/" + query;
        final HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint)).GET().build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(s -> new Gson().fromJson(s, PlayerInfo.class))
                .whenComplete((i, t) -> {
                    if (t != null) TierTagger.getLogger().warn("Error searching player {}", query, t);
                });
    }
}