package com.kevin.tiertagger.config;

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

    // === internal stuff ===

    /**
     * <p>the field was renamed to do a little trolling and force it setting to the default value in players' config</p>
     * <p>previous name(s): {@code apiUrl}</p>
     */
    private String baseUrl = "https://api.uku3lig.net/tiers";
    /**
     * <p>whether to fetch info about players that are not in the initial database queried from {@code /all}.</p>
     * <p>it's useful to set this to false when {@code /all} returns <i>all</i> the players contained in the database.</p>
     */
    private boolean fetchUnknown = true;

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
