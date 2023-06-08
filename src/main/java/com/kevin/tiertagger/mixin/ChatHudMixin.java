package com.kevin.tiertagger.mixin;

import com.google.common.collect.Lists;
import com.kevin.tiertagger.TierTagger;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

import static com.kevin.tiertagger.TierTagger.getPlayerTier;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Inject(
             method = "render(Lnet/minecraft/client/gui/DrawContext;III)V",
             at = @At(value = "HEAD",
                     target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/gui/DrawContext;III)V")
             )

    public void onRender(DrawContext context, int currentTick, int mouseX, int mouseY, CallbackInfo ci){

    }

    @ModifyArg(method = "render(Lnet/minecraft/client/gui/DrawContext;III)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;III)I"),
            index = 1)
    private OrderedText appendTier(OrderedText content) {
        return ((OrderedText) Text.literal(getTier(content) + ": " + content.toString()));
    }

    public String getTier(OrderedText content){
  
        final String[] result = new String[1];
        TierTagger.messagesWithAuthor.keySet().forEach(x -> {
            if (TierTagger.messagesWithAuthor.get(x).equals(content.toString())) {
                result[0] = (Objects.requireNonNull(getPlayerTier(TierTagger.messagesWithAuthor.get(x).toString())).toString());
            }

        });
        if(result != null){
            return result[0];
        } else {
            return "UN";
        }
    }
}

//Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I
//Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;III)I
