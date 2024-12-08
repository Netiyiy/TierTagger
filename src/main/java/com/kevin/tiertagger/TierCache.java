package com.kevin.tiertagger;

import com.kevin.tiertagger.model.PlayerInfo;
import com.kevin.tiertagger.model.TierList;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class TierCache {
    private static final Map<UUID, Optional<PlayerInfo>> TIERS = new ConcurrentHashMap<>();

    /**
     * <p>whether to fetch info about players that are not in the initial database queried from {@code /all}.</p>
     * <p>it's useful to set this to false when {@code /all} returns <i>all</i> the players contained in the database.</p>
     * <p>this used to be a config value, but it's better to leave that choice fully to the server</p>
     * <p>default value is {@code true} to retain some sort of backwards compatibility</p>
     */
    private static final AtomicBoolean FETCH_UNKNOWN = new AtomicBoolean(true);

    public static void init() {
        TierList.get(TierTagger.getClient()).thenAccept(list -> {
            Map<UUID, Optional<PlayerInfo>> players = list.players().stream().collect(Collectors.toMap(p -> parseUUID(p.uuid()), Optional::of));
            Map<UUID, Optional<PlayerInfo>> unknown = list.unknown().stream().collect(Collectors.toMap(u -> u, u -> Optional.empty()));

            TIERS.putAll(players);
            TIERS.putAll(unknown);

            if (list.fetchUnknown() != null) {
                FETCH_UNKNOWN.set(list.fetchUnknown());
                if (!list.fetchUnknown()) {
                    TierTagger.getLogger().warn("The remote API set `fetchUnknown` to false! Make sure you are using a tierlist that supports this feature!");
                }
            }

            TierTagger.getLogger().info("Loaded {} players and {} unknown", players.size(), unknown.size());
        });
    }

    public static Optional<PlayerInfo> getPlayerInfo(UUID uuid) {
        if (FETCH_UNKNOWN.get()) {
            return TIERS.computeIfAbsent(uuid, u -> {
                if (uuid.version() == 4) {
                    PlayerInfo.get(TierTagger.getClient(), uuid).thenAccept(info -> TIERS.put(uuid, Optional.ofNullable(info)));
                }

                return Optional.empty();
            });
        } else {
            return TIERS.getOrDefault(uuid, Optional.empty());
        }
    }

    public static CompletableFuture<PlayerInfo> searchPlayer(String query) {
        return PlayerInfo.search(TierTagger.getClient(), query).thenApply(p -> {
            UUID uuid = parseUUID(p.uuid());
            TIERS.put(uuid, Optional.of(p));
            return p;
        });
    }

    public static void clearCache() {
        TIERS.clear();
    }

    private static UUID parseUUID(String uuid) {
        try {
            return UUID.fromString(uuid);
        } catch (Exception e) {
            long mostSignificant = Long.parseUnsignedLong(uuid.substring(0, 16), 16);
            long leastSignificant = Long.parseUnsignedLong(uuid.substring(16), 16);
            return new UUID(mostSignificant, leastSignificant);
        }
    }

    private TierCache() {
    }
}
