package com.kevin.tiertagger.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

import java.util.function.IntFunction;

@AllArgsConstructor
@Getter
public enum GameMode implements TranslatableOption {
    SWORD(0, "Sword"),
    VANILLA(1, "Vanilla"),
    AXE(2, "Axe"),
    POT(3, "Pot"),
    NETHPOT(4, "NethPot"),
    UHC(5, "UHC"),
    SMP(6, "SMP"),
    ;

    private static final IntFunction<GameMode> GETTER = ValueLists.createIdToValueFunction(GameMode::getId, values(), ValueLists.OutOfBoundsHandling.WRAP);

    private final int id;
    private final String translationKey;

    public static GameMode byId(int id) {
        return GETTER.apply(id);
    }
}
