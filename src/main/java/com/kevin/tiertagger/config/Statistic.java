package com.kevin.tiertagger.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.Comparator;

@Getter
@AllArgsConstructor
public enum Statistic implements TranslatableOption {
    TIER(0, "tiertagger.stat.tier"),
    RANK(1, "tiertagger.stat.rank"),
    ;

    private static final Statistic[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(Statistic::getId)).toArray(Statistic[]::new);

    private final int id;
    private final String translationKey;

    public static Statistic byId(int id) {
        return VALUES[MathHelper.floorMod(id, VALUES.length)];
    }
}
