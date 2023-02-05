package com.kevin.tiertagger;

import com.kevin.tiertagger.util.Gamemode;
import com.kevin.tiertagger.util.TieredPlayer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TierTagger implements ModInitializer {

    private static TierManager tierManager;
    private static TierText tierText;

    @Override
    public void onInitialize() {
        tierManager = new TierManager();
        tierText = new TierText();
        tierManager.tiersLoader();
        System.out.println("TierTagger by Ooh_Netiyiy has been initialized");
    }

    public static Text appendTier(PlayerEntity player, Text username) {

        if (tierManager.isPlayerTiered(username.asString())) {
            TieredPlayer tieredPlayer = tierManager.getPlayerTiers().get(player.getEntityName());
            MutableText tag = tierText.getTier(tieredPlayer.getTier(Gamemode.VANILLA));
            if (tag != null) {
                tag.append(Text.of(" | ").copy().styled(s -> s.withColor(Formatting.GRAY)));
                return tag.append(username);
            }
        }
        return username;
    }

}
