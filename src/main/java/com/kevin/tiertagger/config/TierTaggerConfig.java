package com.kevin.tiertagger.config;

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
    private boolean showUnranked = false;
    private boolean showRetired = true;
    private Statistic shownStatistic = Statistic.TIER;
    
    // the field was renamed to do a little trolling and force it setting to the default value in players' config
    // previous name(s): apiUrl
    private String baseUrl = "https://api.uku3lig.net/tiers";

    @Getter
    @AllArgsConstructor
    public enum GameMode implements TranslatableOption {
        SWORD(0, "Sword", "sword"),
        VANILLA(1, "Vanilla", "vanilla"),
        AXE(2, "Axe", "axe"),
        POT(3, "Pot", "pot"),
        NETH_POT(4, "NethPot", "neth_pot"),
        UHC(5, "UHC", "uhc"),
        SMP(6, "SMP", "smp"),
        ;

        private final int id;
        private final String translationKey;
        private final String apiKey;
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
}
