package com.kevin.tiertagger.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.Comparator;

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

    private static final GameMode[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(GameMode::getId)).toArray(GameMode[]::new);

    private final int id;
    private final String translationKey;

    public static GameMode byId(int id) {
        return VALUES[MathHelper.floorMod(id, VALUES.length)];
    }
}
