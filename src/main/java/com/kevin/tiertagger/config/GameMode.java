package com.kevin.tiertagger.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

import java.util.function.IntFunction;

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

    private static final IntFunction<GameMode> GETTER = ValueLists.createIdToValueFunction(GameMode::getId, values(), ValueLists.OutOfBoundsHandling.WRAP);

    private final int id;
    private final String translationKey;
    private final String apiKey;

    public static GameMode byId(int id) {
        return GETTER.apply(id);
    }
}
