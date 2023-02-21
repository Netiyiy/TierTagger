package com.kevin.tiertagger;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TiersManager implements HttpResponse.BodyHandler<String> {

    public Map<String, String> playerTiers = new HashMap<>();
    private final long MAX_ATTEMPTS = 3;

    public void loadTiers() {
        loadTiers(0);
    }

    public void loadTiers(int currentAttempt) {
        playerTiers.clear();
        // Starting a new thread is expensive however we only do this once
        new Thread(() -> {
            String url = "https://tiers.pvphub.co/api/list/vanilla";
            try {
                InputStream stream = new URL(url).openStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                JsonArray data = new Gson().fromJson(reader, JsonArray.class);

                for (int i = 0; i < data.size(); i++) {
                    JsonObject entry = data.get(i).getAsJsonObject();

                    if (!entry.has("username") || entry.get("username").isJsonNull())
                        continue;

                    String username = entry.get("username").getAsString();

                    // Some records on the API exist only to record a banned player
                    if (!entry.has("tierType") || entry.get("tierType").isJsonNull())
                        continue;

                    String tierType = entry.get("tierType").getAsString();
                    int tier = entry.get("tier").getAsInt();

                    String formattedTier;
                    if (tierType.toUpperCase(Locale.ROOT).equals("HIGH")) {
                        formattedTier = "HT" + tier;
                    } else {
                        formattedTier = "LT" + tier;
                    }
                    playerTiers.put(username, formattedTier);
                }
                TierTagger.getLogger().info("Loaded " + playerTiers.size() + " tier list entries!");
                reader.close();
                stream.close();
            } catch (JsonIOException | IOException | JsonSyntaxException e) {
                TierTagger.getLogger().warn("Unable to load the tier list data from '" + url + "'!");
                if (currentAttempt + 1 > MAX_ATTEMPTS) {
                    TierTagger.getLogger().info("Max attempts reached. ");
                    return;
                }

                TierTagger.getLogger().info("Retrying in 30s");
                try {
                    Thread.sleep(30000);
                    loadTiers(currentAttempt + 1);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }).start();
    }

    @Override
    public HttpResponse.BodySubscriber<String> apply(HttpResponse.ResponseInfo responseInfo) {
        return null;
    }
}
