package com.kevin.tiertagger;

import com.kevin.tiertagger.model.PlayerInfo;
import com.kevin.tiertagger.model.TierList;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TierCache {
    private static final Map<UUID, Optional<PlayerInfo>> TIERS = new HashMap<>();

    public static void init() {
        TierList.get(TierTagger.getClient()).thenAccept(list -> {
            Map<UUID, Optional<PlayerInfo>> players = list.getPlayers().stream().collect(Collectors.toMap(p -> UUID.fromString(p.getUuid()), Optional::of));
            Map<UUID, Optional<PlayerInfo>> unknown = list.getUnknown().stream().collect(Collectors.toMap(u -> u, u -> Optional.empty()));

            TIERS.putAll(players);
            TIERS.putAll(unknown);

            TierTagger.getLogger().info("Loaded {} players and {} unknown", players.size(), unknown.size());
        });
    }

    public static Optional<PlayerInfo> getPlayerInfo(UUID uuid) {
        return TIERS.computeIfAbsent(uuid, u -> {
            if (uuid.version() == 4) {
                PlayerInfo.get(TierTagger.getClient(), uuid).thenAccept(info -> TIERS.put(uuid, Optional.ofNullable(info)));
            }

            return Optional.empty();
        });
    }

    public static CompletableFuture<PlayerInfo> searchPlayer(String query) {
        return PlayerInfo.search(TierTagger.getClient(), query).thenApply(p -> {
            UUID uuid = fromStr(p.getUuid());
            TIERS.put(uuid, Optional.of(p));
            return p;
        });
    }

    public static void clearCache() {
        TIERS.clear();
    }

    private static UUID fromStr(String uuid) {
        long mostSignificant = Long.parseUnsignedLong(uuid.substring(0, 16), 16);
        long leastSignificant = Long.parseUnsignedLong(uuid.substring(16), 16);
        return new UUID(mostSignificant, leastSignificant);
    }

    private TierCache() {
    }
}
