package com.kevin.tiertagger.config;

import com.kevin.tiertagger.TierCache;
import com.kevin.tiertagger.TierTagger;
import com.kevin.tiertagger.model.GameMode;
import com.kevin.tiertagger.tierlist.PlayerSearchScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.uku3lig.ukulib.config.option.*;
import net.uku3lig.ukulib.config.option.widget.ButtonTab;
import net.uku3lig.ukulib.config.screen.TabbedConfigScreen;

import java.util.List;
import java.util.stream.Collectors;

public class TTConfigScreen extends TabbedConfigScreen<TierTaggerConfig> {
    public TTConfigScreen(Screen parent) {
        super("TierTagger Config", parent, TierTagger.getManager());
    }

    @Override
    protected Tab[] getTabs(TierTaggerConfig config) {
        return new Tab[]{new MainSettingsTab(), new ColorsTab()};
    }

    public class MainSettingsTab extends ButtonTab<TierTaggerConfig> {
        public MainSettingsTab() {
            super("tiertagger.config", TTConfigScreen.this.manager);
        }

        @Override
        protected WidgetCreator[] getWidgets(TierTaggerConfig config) {
            return new WidgetCreator[]{
                    CyclingOption.ofBoolean("tiertagger.config.enabled", config.isEnabled(), config::setEnabled),
                    CyclingOption.ofTranslatableEnum("tiertagger.config.gamemode", GameMode.class, config.getGameMode(), config::setGameMode),
                    CyclingOption.ofBoolean("tiertagger.config.retired", config.isShowRetired(), config::setShowRetired),
                    CyclingOption.ofTranslatableEnum("tiertagger.config.highest", TierTaggerConfig.HighestMode.class, config.getHighestMode(), config::setHighestMode, SimpleOption.constantTooltip(Text.translatable("tiertagger.config.highest.desc"))),
                    CyclingOption.ofTranslatableEnum("tiertagger.config.statistic", TierTaggerConfig.Statistic.class, config.getShownStatistic(), config::setShownStatistic),
                    new SimpleButton("tiertagger.clear", b -> TierCache.clearCache()),
                    new ScreenOpenButton("tiertagger.config.search", PlayerSearchScreen::new)
            };
        }
    }

    public class ColorsTab extends ButtonTab<TierTaggerConfig> {
        protected ColorsTab() {
            super("tiertagger.colors", TTConfigScreen.this.manager);
        }

        @Override
        protected WidgetCreator[] getWidgets(TierTaggerConfig config) {
            List<ColorOption> tiers = config.getTierColors().entrySet().stream()
                    .map(e -> new ColorOption(e.getKey(), e.getValue(), val -> config.getTierColors().put(e.getKey(), val)))
                    .collect(Collectors.toList());

            tiers.addFirst(new ColorOption("tiertagger.colors.retired", config.getRetiredColor(), config::setRetiredColor));

            return tiers.toArray(WidgetCreator[]::new);
        }
    }
}
