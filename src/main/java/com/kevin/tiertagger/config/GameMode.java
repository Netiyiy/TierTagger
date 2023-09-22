package com.kevin.tiertagger.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.TranslatableOption;

@AllArgsConstructor
@Getter
public enum GameMode implements TranslatableOption {
    SWORD(0, "Sword"),
    VANILLA(1, "Vanilla"),
    AXE(2, "Axe"),
    POT(3, "Pot"),
    NETH_POT(4, "NethPot"),
    UHC(5, "UHC"),
    SMP(6, "SMP"),
    ;

    private final int id;
    private final String translationKey;
}
