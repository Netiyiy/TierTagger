package com.kevin.tiertagger.config;

import com.kevin.tiertagger.TierCache;
import com.kevin.tiertagger.TierTagger;
import com.kevin.tiertagger.model.GameMode;
import com.kevin.tiertagger.tierlist.PlayerSearchScreen;
import net.minecraft.client.gui.screen.Screen;
import net.uku3lig.ukulib.config.option.CyclingOption;
import net.uku3lig.ukulib.config.option.ScreenOpenButton;
import net.uku3lig.ukulib.config.option.SimpleButton;
import net.uku3lig.ukulib.config.option.WidgetCreator;
import net.uku3lig.ukulib.config.screen.AbstractConfigScreen;

public class TTConfigScreen extends AbstractConfigScreen<TierTaggerConfig> {
    public TTConfigScreen(Screen parent) {
        super("TierTagger Config", parent, TierTagger.getManager());
    }

    @Override
    protected WidgetCreator[] getWidgets(TierTaggerConfig config) {
        return new WidgetCreator[] {
                CyclingOption.ofBoolean("tiertagger.config.enabled", config.isEnabled(), config::setEnabled),
                CyclingOption.ofTranslatableEnum("tiertagger.config.gamemode", GameMode.class, config.getGameMode(), config::setGameMode),
                CyclingOption.ofBoolean("tiertagger.config.unranked", config.isShowUnranked(), config::setShowUnranked),
                CyclingOption.ofBoolean("tiertagger.config.retired", config.isShowRetired(), config::setShowRetired),
                CyclingOption.ofTranslatableEnum("tiertagger.config.statistic", TierTaggerConfig.Statistic.class, config.getShownStatistic(), config::setShownStatistic),
                new SimpleButton("tiertagger.clear", b -> TierCache.clearCache()),
                new ScreenOpenButton("tiertagger.config.search", PlayerSearchScreen::new)
        };
    }
}
