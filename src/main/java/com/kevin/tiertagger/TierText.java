package com.kevin.tiertagger;

import com.kevin.tiertagger.util.Tier;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.HashMap;

public class TierText {

    private final HashMap<Tier, MutableText> TIER_TEXT = new HashMap<>();

    public TierText() {
        TIER_TEXT.put(Tier.HT1, colorText(Text.of("HT1").copy(), 0xFF0000));
        TIER_TEXT.put(Tier.LT1, colorText(Text.of("LT1").copy(), 0xFFB6C1));
        TIER_TEXT.put(Tier.HT2, colorText(Text.of("HT2").copy(), 0xFFA500));
        TIER_TEXT.put(Tier.LT2, colorText(Text.of("LT2").copy(), 0xFFE4B5));
        TIER_TEXT.put(Tier.HT3, colorText(Text.of("HT3").copy(), 0xDAA520));
        TIER_TEXT.put(Tier.LT3, colorText(Text.of("LT3").copy(), 0xEEE8AA));
        TIER_TEXT.put(Tier.HT4, colorText(Text.of("HT4").copy(), 0x006400));
        TIER_TEXT.put(Tier.LT4, colorText(Text.of("LT4").copy(), 0x90EE90));
        TIER_TEXT.put(Tier.HT5, colorText(Text.of("HT5").copy(), 0x808080));
        TIER_TEXT.put(Tier.LT5, colorText(Text.of("LT5").copy(), 0xD3D3D3));
    }

    public MutableText getTier(Tier tier) {
        return TIER_TEXT.get(tier);
    }

    public MutableText colorText(MutableText text, int color) {
        return text.styled(s -> s.withColor(color));
    }

}
