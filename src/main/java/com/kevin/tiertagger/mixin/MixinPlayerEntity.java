package com.kevin.tiertagger.mixin;

import com.kevin.tiertagger.TierTagger;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {
    @ModifyReturnValue(method = "getDisplayName", at = @At("RETURN"))
    public Text prependTier(Text original) {
        if (TierTagger.getManager().getConfig().isEnabled()) {
            PlayerEntity self = (PlayerEntity) (Object) this;
            return TierTagger.appendTier(self, original);
        } else {
            return original;
        }
    }
}
