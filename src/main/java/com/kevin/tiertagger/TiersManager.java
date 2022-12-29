package com.kevin.tiertagger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.util.*;

public class TiersManager {

    public Map<String, String> playerTiers;

    public void tiersLoader() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("playerTiers.json");
        if (stream == null) {
            throw new IllegalArgumentException("playerTiers.json not found!");
        } else {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            JsonObject data = new Gson().fromJson(reader, JsonObject.class);

            playerTiers = new HashMap<>();

            for (String player : data.keySet()) {
                playerTiers.put(player, String.valueOf(data.get(player)));
            }
        }
    }
}
