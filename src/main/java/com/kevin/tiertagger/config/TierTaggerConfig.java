package com.kevin.tiertagger.config;

import com.kevin.tiertagger.model.GameMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;
import net.uku3lig.ukulib.config.IConfig;

import java.util.function.IntFunction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TierTaggerConfig implements IConfig<TierTaggerConfig> {
    private boolean enabled = true;
    private GameMode gameMode = GameMode.VANILLA;
    private boolean showRetired = true;
    private HighestMode highestMode = HighestMode.NOT_FOUND;
    private Statistic shownStatistic = Statistic.TIER;
    private boolean showIcons = true;

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
    public enum Statistic implements TranslatableOption {
        TIER(0, "tiertagger.stat.tier"),
        RANK(1, "tiertagger.stat.rank"),
        ;

        private static final IntFunction<Statistic> GETTER = ValueLists.createIdToValueFunction(Statistic::getId, values(), ValueLists.OutOfBoundsHandling.WRAP);

        private final int id;
        private final String translationKey;

        public static Statistic byId(int id) {
            return GETTER.apply(id);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum HighestMode implements TranslatableOption {
        NEVER(0, "tiertagger.highest.never"),
        NOT_FOUND(1, "tiertagger.highest.not_found"),
        ALWAYS(2, "tiertagger.highest.always"),
        ;

        private static final IntFunction<HighestMode> GETTER = ValueLists.createIdToValueFunction(HighestMode::getId, values(), ValueLists.OutOfBoundsHandling.WRAP);

        private final int id;
        private final String translationKey;

        public static HighestMode byId(int id) {
            return GETTER.apply(id);
        }
    }
}
