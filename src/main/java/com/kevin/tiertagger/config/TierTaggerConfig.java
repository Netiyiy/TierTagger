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
    private boolean showHighest = true;
    private Statistic shownStatistic = Statistic.TIER;

    // the field was renamed to do a little trolling and force it setting to the default value in players' config
    // previous name(s): apiUrl
    private String baseUrl = "https://api.uku3lig.net/tiers";

    @Getter
    @AllArgsConstructor
    public enum Statistic implements TranslatableOption {
        TIER(0, "tiertagger.stat.tier"),
        RANK(1, "tiertagger.stat.rank"),
        ;

        private final int id;
        private final String translationKey;
    }
}
