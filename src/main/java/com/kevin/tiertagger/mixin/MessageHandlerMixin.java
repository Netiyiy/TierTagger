package com.kevin.tiertagger.mixin;

import com.kevin.tiertagger.TierTagger;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Instant;
import java.util.HashMap;

@Mixin(MessageHandler.class)
public class MessageHandlerMixin {
    @Inject(method = "processChatMessageInternal(Lnet/minecraft/network/message/MessageType$Parameters;Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/text/Text;Lcom/mojang/authlib/GameProfile;ZLjava/time/Instant;)Z",
            at = @At(value = "HEAD", target = "Lnet/minecraft/client/network/message/MessageHandler;processChatMessageInternal(Lnet/minecraft/network/message/MessageType$Parameters;Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/text/Text;Lcom/mojang/authlib/GameProfile;ZLjava/time/Instant;)Z"))
    public void saveMessage(MessageType.Parameters params, SignedMessage message, Text decorated, GameProfile sender, boolean onlyShowSecureChat, Instant receptionTimestamp, CallbackInfoReturnable<Boolean> cir){
        TierTagger.messagesWithAuthor.put(sender, message);
        if(TierTagger.messagesWithAuthor.size() > 100){
            int excessMessages = TierTagger.messagesWithAuthor.size() - 100;
            GameProfile[] array;
            try {
                array = (GameProfile[]) TierTagger.messagesWithAuthor.keySet().toArray();
                for (int i = 0; i < excessMessages; i++) {
                    TierTagger.messagesWithAuthor.remove(array[i]);
                }
            } catch (ClassCastException ignored){

            }
        }
    }
}
