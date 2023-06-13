package com.kevin.tiertagger.mixin;

import com.kevin.tiertagger.TierTagger;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {
    @Inject(at = @At(value = "HEAD",
            target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V")
            , method = "tick()V")
    private void tiertagger$onTick(CallbackInfo ci)
    {
        TierTagger.tick++;
    }
}
