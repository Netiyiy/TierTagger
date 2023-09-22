package com.kevin.tiertagger;

import com.kevin.tiertagger.config.TierTaggerConfig;
import com.kevin.tiertagger.model.TierList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.uku3lig.ukulib.config.ConfigManager;
import org.jetbrains.annotations.Nullable;

import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class TierTagger implements ModInitializer {
    @Getter
    private static final ConfigManager<TierTaggerConfig> manager = ConfigManager.createDefault(TierTaggerConfig.class, "tiertagger");
    @Getter
    private static final HttpClient client = HttpClient.newHttpClient();

    private static final Map<UUID, String> tiers = new HashMap<>();

    @Override
    public void onInitialize() {
        reloadTiers();
    }

    public static void reloadTiers() {
        String mode = manager.getConfig().getGameMode().name().toLowerCase(Locale.ROOT);
        TierList.get(client, mode)
                .thenAccept(tl -> {
                    tiers.clear();
                    tiers.putAll(tl.getTiers());
                    log.info("Reloaded {} tiers! {} loaded.", mode, tiers.size());
                })
                .exceptionally(t -> {
                    log.error("Could not reload the tiers!", t);
                    return null;
                });
    }

    public static Text appendTier(PlayerEntity player, Text text) {

        MutableText tier = getPlayerTier(player.getUuid());
        if (tier != null) {
            tier.append(Text.of(" | ").copy().styled(s -> s.withColor(Formatting.GRAY)));
            return tier.append(text);
        }

        return text;
    }

    @Nullable
    private static MutableText getPlayerTier(UUID uuid) {
        if (tiers.containsKey(uuid)) {
            String foundTier = tiers.get(uuid);
            return Text.literal(foundTier).styled(s -> s.withColor(getTierColor(foundTier)));
        } else if (manager.getConfig().isShowUnranked()) {
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