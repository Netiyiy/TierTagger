package com.kevin.tiertagger.mixin;

import com.kevin.tiertagger.TierTagger;
import com.mojang.authlib.GameProfile;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * A mixin used to cache the metadata of the most recent message
 * received by the client. This is used in
 *
 * to provide more accurate timestamp data, the correct player
 * name, and the player's UUID.
 */
@Mixin(MessageHandler.class)
public abstract class MessageHandlerMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onChatMessage", at = @At("HEAD"))
    private void tiertagger$cacheChatData(SignedMessage message, GameProfile sender, MessageType.Parameters params, CallbackInfo ci) {
        client.options.getOnlyShowSecureChat().setValue(false);

        TierTagger.lastMsg = new TierTagger.MessageData(sender, message.getContent());
    }

    @SuppressWarnings("DataFlowIssue") // Formatting.strip is nullable, but IntelliJ doesn't realize the null is only from the parameter
    @Inject(method = "onGameMessage", at = @At("HEAD"))
    private void tiertagger$cacheGameData(Text message, boolean overlay, CallbackInfo ci) {
        String text = Objects.requireNonNullElse(message.getString(), "");
        if( Pattern.matches("^<[a-zA-Z0-9_]{3,16}> .+$", text) ) {

            String name = StringUtils.substringBetween(SharedConstants.stripInvalidChars(Formatting.strip( text )), "<", ">");
            UUID uuid = client.getSocialInteractionsManager().getUuid(name);

            TierTagger.lastMsg = new TierTagger.MessageData(new GameProfile(uuid, name), Objects.requireNonNullElse(message, Text.literal("")));
        }
    }
}
