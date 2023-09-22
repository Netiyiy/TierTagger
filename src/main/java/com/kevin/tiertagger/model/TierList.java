package com.kevin.tiertagger.model;

import com.google.gson.Gson;
import com.google.gson.JsonPrimitive;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record TierList(List<List<List<JsonPrimitive>>> rankings, Map<String, ShortPlayerInfo> players) {
    public record ShortPlayerInfo(String name, String region, int points) {
    }

    private static final String ENDPOINT = "https://mctiers.com/api/tier/%s?count=32767";

    public static CompletableFuture<TierList> get(HttpClient client, String mode) {
        URI formattedEndpoint = URI.create(ENDPOINT.formatted(mode));
        final HttpRequest request = HttpRequest.newBuilder(formattedEndpoint).GET().build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(s -> new Gson().fromJson(s, TierList.class));
    }

    public Map<UUID, String> getTiers() {
        HashMap<UUID, String> map = new HashMap<>();

        for (int tier = 0; tier < rankings.size(); tier++) {
            List<List<JsonPrimitive>> tierList = rankings.get(tier);
            for (List<JsonPrimitive> player : tierList) {
                UUID uuid = uuid(player.get(0).getAsString());
                boolean high = player.get(1).getAsByte() == 0;
                String tierString = "%cT%d".formatted(high ? 'H' : 'L', tier + 1);
                map.put(uuid, tierString);
            }
        }

        return map;
    }

    private static UUID uuid(String s) {
        long mostSig = Long.parseUnsignedLong(s.substring(0, 16), 16);
        long leastSig = Long.parseUnsignedLong(s.substring(16), 16);
        return new UUID(mostSig, leastSig);
    }
}
