package com.kevin.tiertagger.config;

import com.kevin.tiertagger.TierTagger;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.CyclingOption;
import net.minecraft.client.option.Option;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.uku3lig.ukulib.config.screen.AbstractConfigScreen;
import net.uku3lig.ukulib.utils.Ukutils;

public class TTConfigScreen extends AbstractConfigScreen<TierTaggerConfig> {
    public TTConfigScreen(Screen parent) {
        super(parent, Text.of("TierTagger Config"), TierTagger.getManager());
    }

    @Override
    protected Option[] getOptions(TierTaggerConfig config) {
        return new Option[] {
                CyclingOption.create("tiertagger.config.enabled", opt -> config.isEnabled(), (opt, option, b) -> config.setEnabled(b)),
                CyclingOption.create("tiertagger.config.gamemode", GameMode.values(), m -> new TranslatableText(m.getTranslationKey()),
                        opt -> config.getGameMode(), (opt, option, gameMode) -> config.setGameMode(gameMode)),
                CyclingOption.create("tiertagger.config.unranked", opt -> config.isShowUnranked(), (opt, option, b) -> config.setShowUnranked(b)),
                Ukutils.createButton("tiertagger.clear", s -> TierTagger.clearCache()),
        };
    }
}
