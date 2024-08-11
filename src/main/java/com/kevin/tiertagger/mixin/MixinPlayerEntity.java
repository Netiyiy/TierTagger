package com.kevin.tiertagger.mixin;

import com.kevin.tiertagger.TierTagger;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {
    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    public void prependTier(CallbackInfoReturnable<Text> cir) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        Text modified = TierTagger.appendTier(self, cir.getReturnValue());
        cir.setReturnValue(modified);
    }
}
