package com.kevin.tiertagger.util;

import java.util.HashMap;

public class TieredPlayer {

    private final HashMap<Gamemode, Tier> TIERS;

    public TieredPlayer(HashMap<Gamemode, Tier> tier) {
        this.TIERS = tier;
    }

    public Tier getTier(Gamemode tierType) {
        return TIERS.get(tierType);
    }

}
