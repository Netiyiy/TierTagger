package com.kevin.tiertagger.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.TranslatableOption;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum GameMode implements TranslatableOption {
    SWORD(0, "Sword", "sword", "\uD83D\uDDE1", Formatting.AQUA),
    VANILLA(1, "Vanilla", "vanilla", "☄", Formatting.DARK_PURPLE),
    AXE(2, "Axe", "axe", "\uD83E\uDE93", Formatting.AQUA),
    POT(3, "Pot", "pot", "\uD83E\uDDEA", Formatting.DARK_AQUA),
    NETH_POT(4, "NethPot", "neth_pot", "❤", Formatting.DARK_RED),
    UHC(5, "UHC", "uhc", "\uD83E\uDEA3", Formatting.GRAY),
    SMP(6, "SMP", "smp", "\uD83D\uDD25", Formatting.GOLD),
    ;

    private final int id;
    private final String translationKey;
    private final String apiKey;
    private final String icon;
    private final Formatting iconColor;

    public static Optional<GameMode> byKey(String key) {
        return Arrays.stream(values()).filter(m -> m.apiKey.equalsIgnoreCase(key)).findFirst();
    }

    public MutableText getIconText() {
        return Text.literal(this.icon).formatted(this.iconColor);
    }

    public static class Deserializer implements JsonDeserializer<GameMode> {
        @Override
        public GameMode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return byKey(json.getAsString()).orElse(null);
        }
    }
}
