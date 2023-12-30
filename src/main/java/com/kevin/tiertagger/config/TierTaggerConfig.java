package com.kevin.tiertagger.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.uku3lig.ukulib.config.IConfig;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TierTaggerConfig implements IConfig<TierTaggerConfig> {
    private String apiUrl = "https://mctiers.com/api";
    private boolean enabled = true;
    private GameMode gameMode = GameMode.VANILLA;
    private boolean showUnranked = false;

    @Override
    public TierTaggerConfig defaultConfig() {
        return new TierTaggerConfig();
    }
}
