package com.kevin.tiertagger.config;

import com.kevin.tiertagger.TierCache;
import com.kevin.tiertagger.TierTagger;
import com.kevin.tiertagger.model.GameMode;
import com.kevin.tiertagger.tierlist.PlayerSearchScreen;
import com.mojang.serialization.Codec;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.uku3lig.ukulib.config.screen.AbstractConfigScreen;
import net.uku3lig.ukulib.utils.Ukutils;

import java.util.Arrays;

public class TTConfigScreen extends AbstractConfigScreen<TierTaggerConfig> {
    public TTConfigScreen(Screen parent) {
        super(parent, Text.of("TierTagger Config"), TierTagger.getManager());
    }

    @Override
    protected SimpleOption<?>[] getOptions(TierTaggerConfig config) {
        return new SimpleOption[]{
                SimpleOption.ofBoolean("tiertagger.config.enabled", config.isEnabled(), config::setEnabled),
                new SimpleOption<>("tiertagger.config.gamemode", SimpleOption.emptyTooltip(), SimpleOption.enumValueText(),
                        new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(GameMode.values()), Codec.INT.xmap(GameMode::byId, GameMode::getId)),
                        config.getGameMode(), config::setGameMode),
                SimpleOption.ofBoolean("tiertagger.config.retired", config.isShowRetired(), config::setShowRetired),
                new SimpleOption<>("tiertagger.config.highest", SimpleOption.constantTooltip(Text.translatable("tiertagger.config.highest.desc")), SimpleOption.enumValueText(),
                        new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(TierTaggerConfig.HighestMode.values()), Codec.INT.xmap(TierTaggerConfig.HighestMode::byId, TierTaggerConfig.HighestMode::getId)),
                        config.getHighestMode(), config::setHighestMode),
                new SimpleOption<>("tiertagger.config.statistic", SimpleOption.emptyTooltip(), SimpleOption.enumValueText(),
                        new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(TierTaggerConfig.Statistic.values()), Codec.INT.xmap(TierTaggerConfig.Statistic::byId, TierTaggerConfig.Statistic::getId)),
                        config.getShownStatistic(), config::setShownStatistic),
                SimpleOption.ofBoolean("tiertagger.config.icons", config.isShowIcons(), config::setShowIcons),
                Ukutils.createButton("tiertagger.clear", s -> TierCache.clearCache()),
                Ukutils.createOpenButton("tiertagger.config.search", PlayerSearchScreen::new),
        };
    }
}
