package com.kevin.tiertagger.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.math.MathHelper;
import net.uku3lig.ukulib.config.IConfig;

import java.util.Arrays;
import java.util.Comparator;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TierTaggerConfig implements IConfig<TierTaggerConfig> {
    private boolean enabled = true;
    private GameMode gameMode = GameMode.VANILLA;
    private boolean showUnranked = false;
    private boolean showRetired = true;
    private Statistic shownStatistic = Statistic.TIER;

    // === internal stuff ===

    /**
     * <p>the field was renamed to do a little trolling and force it setting to the default value in players' config</p>
     * <p>previous name(s): {@code apiUrl}</p>
     */
    private String baseUrl = "https://api.uku3lig.net/tiers";

    @Override
    public TierTaggerConfig defaultConfig() {
        return new TierTaggerConfig();
    }

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

        private static final GameMode[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(GameMode::getId)).toArray(GameMode[]::new);

        private final int id;
        private final String translationKey;
        private final String apiKey;

        public static GameMode byId(int id) {
            return VALUES[MathHelper.floorMod(id, VALUES.length)];
        }
    }


    @Getter
    @AllArgsConstructor
    public enum Statistic implements TranslatableOption {
        TIER(0, "tiertagger.stat.tier"),
        RANK(1, "tiertagger.stat.rank"),
        ;

        private static final Statistic[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(Statistic::getId)).toArray(Statistic[]::new);

        private final int id;
        private final String translationKey;

        public static Statistic byId(int id) {
            return VALUES[MathHelper.floorMod(id, VALUES.length)];
        }
    }
}
