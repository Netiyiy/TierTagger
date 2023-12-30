package com.kevin.tiertagger.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.TranslatableOption;

@AllArgsConstructor
@Getter
public enum GameMode implements TranslatableOption {
    SWORD(0, "Sword", "sword"),
    VANILLA(1, "Vanilla", "vanilla"),
    AXE(2, "Axe", "axe"),
    POT(3, "Pot", "pot"),
    NETH_POT(4, "NethPot", "neth_pot"),
    UHC(5, "UHC", "uhc"),
    SMP(6, "SMP", "smp"),
    ;

    private final int id;
    private final String translationKey;
    private final String apiKey;
}
