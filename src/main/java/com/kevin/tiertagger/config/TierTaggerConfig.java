package com.kevin.tiertagger.config;

import com.google.gson.internal.LinkedTreeMap;
import com.kevin.tiertagger.model.GameMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.util.TranslatableOption;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TierTaggerConfig implements Serializable {
    private boolean enabled = true;
    private GameMode gameMode = GameMode.VANILLA;
    private boolean showRetired = true;
    private HighestMode highestMode = HighestMode.NOT_FOUND;
    private Statistic shownStatistic = Statistic.TIER;
    private int retiredColor = 0x662B99;
    // note: this is a GSON internal class. this *might* break in the future
    private LinkedTreeMap<String, Integer> tierColors = defaultColors();

    // === internal stuff ===

    /**
     * <p>the field was renamed to do a little trolling and force it setting to the default value in players' config</p>
     * <p>previous name(s): {@code apiUrl}</p>
     */
    private String baseUrl = "https://api.uku3lig.net/tiers";

    private static LinkedTreeMap<String, Integer> defaultColors() {
        LinkedTreeMap<String, Integer> colors = new LinkedTreeMap<>();
        colors.put("HT1", 0xFF0000); // red
        colors.put("LT1", 0xFFB6C1); // light pink
        colors.put("HT2", 0xFFA500); // orange
        colors.put("LT2", 0xFFE4B5); // light orange
        colors.put("HT3", 0xDAA520); // goldenrod
        colors.put("LT3", 0xEEE8AA); // pale goldenrod
        colors.put("HT4", 0x006400); // dark green
        colors.put("LT4", 0x90EE90); // light green
        colors.put("HT5", 0x808080); // grey
        colors.put("LT5", 0xD3D3D3); // pale grey

        return colors;
    }

    @Getter
    @AllArgsConstructor
    public enum Statistic implements TranslatableOption {
        TIER(0, "tiertagger.stat.tier"),
        RANK(1, "tiertagger.stat.rank"),
        ;

        private final int id;
        private final String translationKey;
    }

    @Getter
    @AllArgsConstructor
    public enum HighestMode implements TranslatableOption {
        NEVER(0, "tiertagger.highest.never"),
        NOT_FOUND(1, "tiertagger.highest.not_found"),
        ALWAYS(2, "tiertagger.highest.always"),
        ;

        private final int id;
        private final String translationKey;
    }
}
