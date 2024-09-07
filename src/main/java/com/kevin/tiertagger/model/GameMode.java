package com.kevin.tiertagger.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.TranslatableOption;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum GameMode implements TranslatableOption {
    SWORD(0, "Sword", "sword", "\uD83D\uDDE1", TextColor.fromRgb(0xa4fdf0)),
    VANILLA(1, "Vanilla", "vanilla", "✦", TextColor.fromFormatting(Formatting.LIGHT_PURPLE)),
    AXE(2, "Axe", "axe", "\uD83E\uDE93", TextColor.fromFormatting(Formatting.GREEN)),
    POT(3, "Pot", "pot", "\uD83E\uDDEA", TextColor.fromRgb(0xff0000)),
    NETH_POT(4, "NethPot", "neth_pot", "☠", TextColor.fromRgb(0x7d4a40)),
    UHC(5, "UHC", "uhc", "❤", TextColor.fromFormatting(Formatting.RED)),
    SMP(6, "SMP", "smp", "⛨", TextColor.fromRgb(0xeccb45)),
    ;

    private final int id;
    private final String translationKey;
    private final String apiKey;
    private final String icon;
    private final TextColor iconColor;

    // stored in memory to avoid the cost of calling the method every time
    private static final GameMode[] values = values();

    public MutableText formatted() {
        return Text.literal(this.icon + " " + this.translationKey).styled(s -> s.withColor(this.iconColor));
    }

    public static Optional<GameMode> byKey(String key) {
        return Arrays.stream(values).filter(m -> m.apiKey.equalsIgnoreCase(key)).findFirst();
    }

    public static class Deserializer implements JsonDeserializer<GameMode> {
        @Override
        public GameMode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return byKey(json.getAsString()).orElse(null);
        }
    }
}
