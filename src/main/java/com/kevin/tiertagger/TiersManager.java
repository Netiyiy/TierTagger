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

            for (String tierGet : data.keySet()) {
                String tierSet = tierGet.substring(0, 1).toUpperCase() + tierGet.substring(1);
                extractStringList(data, tierGet).forEach(player -> playerTiers.put(player, tierSet));
            }

            for (String player : playerTiers.keySet()) {
                System.out.println(player + ": " + playerTiers.get(player) + ", Works!");
                return;
            }
        }
    }



    private List<String> extractStringList(JsonObject data, String key) {
        List<String> list = new ArrayList<>();
        data.getAsJsonArray(key).forEach(element -> list.add(element.getAsString()));
        return list;
    }

}
