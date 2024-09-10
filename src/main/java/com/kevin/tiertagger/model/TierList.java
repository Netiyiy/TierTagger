package com.kevin.tiertagger.model;

import com.google.gson.annotations.SerializedName;
import com.kevin.tiertagger.TierTagger;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record TierList(List<PlayerInfo> players, List<UUID> unknown, @SerializedName("fetch_unknown") @Nullable Boolean fetchUnknown) {
    public static CompletableFuture<TierList> get(HttpClient client) {
        String endpoint = TierTagger.getManager().getConfig().getBaseUrl() + "/all";
        final HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint)).GET().build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(s -> TierTagger.GSON.fromJson(s, TierList.class))
                .whenComplete((i, t) -> {
                    if (t != null) TierTagger.getLogger().warn("Error fetching tier list", t);
                });
    }
}
