package com.kevin.tiertagger.modmenu;

import com.kevin.tiertagger.TierTagger;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.concurrent.atomic.AtomicReference;

public class ModMenuEntry implements ModMenuApi {

    public static AtomicReference<Gamemode> gamemode = new AtomicReference<>(Gamemode.VANILLA);
    public static boolean showUnranked;

    private enum Gamemode {
        SWORD,
        VANILLA,
        AXE,
        POT,
        NETHPOT,
        UHC,
        SMP;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.of("Tier Settings"));
            ConfigCategory general = builder.getOrCreateCategory(Text.of("Tier Settings"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            general.addEntry(entryBuilder.startEnumSelector(Text.of("Gamemode"), Gamemode.class, gamemode.get())
                    .setDefaultValue(Gamemode.VANILLA)
                    .setEnumNameProvider(gamemodeEnum -> {
                        MutableText buttonText = (MutableText) Text.of(formatStringToButton(gamemodeEnum.toString()));
                        return buttonText.setStyle(buttonText.getStyle().withColor(getColor((Gamemode) gamemodeEnum)));
                    })
                    .setSaveConsumer(gamemode::set)
                    .build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.of("Show Unranked Players"), showUnranked)
                    .setDefaultValue(false)
                    .setSaveConsumer(newValue -> showUnranked = newValue)
                    .build());
            TierTagger.reloadTiers();
            return builder.build();
        };
    }

    public static String formatStringToButton(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        if (input.equalsIgnoreCase("UHC")) return "UHC";
        if (input.equalsIgnoreCase("SMP")) return "SMP";
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    private Formatting getColor(Gamemode gamemode) {
        return switch(gamemode) {
            case SWORD -> Formatting.AQUA;
            case VANILLA -> Formatting.LIGHT_PURPLE;
            case AXE -> Formatting.GREEN;
            case POT -> Formatting.DARK_AQUA;
            case NETHPOT -> Formatting.DARK_PURPLE;
            case UHC -> Formatting.GOLD;
            case SMP -> Formatting.DARK_GREEN;
        };
    }

}