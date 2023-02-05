package com.kevin.tiertagger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kevin.tiertagger.util.Tier;
import com.kevin.tiertagger.util.Gamemode;
import com.kevin.tiertagger.util.TieredPlayer;

import java.io.*;
import java.util.*;

public class TierManager {

    private final Map<String, TieredPlayer> playerTiers = new HashMap<>();

    public void tiersLoader() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("playerTiers.json");
        if (stream == null) {
            throw new IllegalArgumentException("playerTiers.json not found!");
        } else {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            JsonObject data = new Gson().fromJson(reader, JsonObject.class);

            for (String player : data.keySet()) {
                HashMap<Gamemode, Tier> tier = new HashMap<>();
                tier.put(Gamemode.VANILLA, getTierFromString(String.valueOf(data.get(player))));
                playerTiers.put(player, new TieredPlayer(tier));
            }

            if (playerTiers.isEmpty()) {
                throw new NullPointerException("Json parsing failed, please contact netiyiy#2023 on Discord");
            }
        }
    }

    public Map<String, TieredPlayer> getPlayerTiers() {
        return playerTiers;
    }

    public boolean isPlayerTiered(String username) {
        return playerTiers.containsKey(username);
    }

    public Tier getTierFromString(String string) {

        string = string.replace("\"", "");
        /*                 ^
         Made a mistake in my json formatting,
         don't want to have to redo it again
        */

        return switch (string) {
            case "HT1" -> Tier.HT1;
            case "LT1" -> Tier.LT1;
            case "HT2" -> Tier.HT2;
            case "LT2" -> Tier.LT2;
            case "HT3" -> Tier.HT3;
            case "LT3" -> Tier.LT3;
            case "HT4" -> Tier.HT4;
            case "LT4" -> Tier.LT4;
            case "HT5" -> Tier.HT5;
            default -> Tier.LT5;
        };
    }

}
