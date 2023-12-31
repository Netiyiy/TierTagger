package com.kevin.tiertagger;

import com.kevin.tiertagger.model.PlayerInfo;

import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TierCache {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Map<UUID, Optional<PlayerInfo>> TIERS = new HashMap<>();

    public static Optional<PlayerInfo> getPlayerInfo(UUID uuid) {
        return TIERS.computeIfAbsent(uuid, u -> {
            PlayerInfo.get(CLIENT, uuid).thenAccept(info -> TIERS.put(uuid, Optional.ofNullable(info)));
            return Optional.empty();
        });
    }

    public static void clearCache() {
        TIERS.clear();
    }
}
