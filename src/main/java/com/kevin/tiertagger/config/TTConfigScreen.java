package com.kevin.tiertagger.config;

import com.kevin.tiertagger.tierlist.PlayerInfoScreen;
import com.kevin.tiertagger.TierTagger;
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
        return new SimpleOption[] {
                SimpleOption.ofBoolean("tiertagger.config.enabled", config.isEnabled(), config::setEnabled),
                new SimpleOption<>("tiertagger.config.gamemode", SimpleOption.emptyTooltip(), SimpleOption.enumValueText(),
                        new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(GameMode.values()), Codec.INT.xmap(GameMode::byId, GameMode::getId)),
                        config.getGameMode(), config::setGameMode),
                SimpleOption.ofBoolean("tiertagger.config.unranked", config.isShowUnranked(), config::setShowUnranked),
                Ukutils.createOpenButton("h", p -> new PlayerInfoScreen(p, "Marlowww")),
        };
    }

    @Override
    public void removed() {
        super.removed();
        TierTagger.reloadTiers();
    }
}
