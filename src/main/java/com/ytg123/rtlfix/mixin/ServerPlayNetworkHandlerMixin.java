package com.ytg123.rtlfix.mixin;

import com.ytg123.rtlfix.CommonMain;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static java.lang.Character.UnicodeBlock;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "onGameMessage(Lnet/minecraft/network/packet/c2s/play/ChatMessageC2SPacket;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcastChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void beforeMessage(ChatMessageC2SPacket packet, CallbackInfo ci, String string) {
        if (!CommonMain.blackListedPlayers.contains(player)) {
            String[] splitstring = string.split("");
            boolean reverse = false;
            for (char c : string.toCharArray()) {
                if (UnicodeBlock.of(c).equals(UnicodeBlock.HEBREW) || UnicodeBlock.of(c)
                        .equals(UnicodeBlock.ARABIC) || UnicodeBlock.of(c)
                        .equals(UnicodeBlock.ARABIC_EXTENDED_A) || UnicodeBlock.of(c)
                        .equals(UnicodeBlock.ARABIC_MATHEMATICAL_ALPHABETIC_SYMBOLS) || UnicodeBlock.of(c)
                        .equals(UnicodeBlock.ARABIC_PRESENTATION_FORMS_A) || UnicodeBlock.of(c)
                        .equals(UnicodeBlock.ARABIC_PRESENTATION_FORMS_B) || UnicodeBlock.of(c)
                        .equals(UnicodeBlock.ARABIC_SUPPLEMENT)) {
                    reverse = true;
                    break;
                }
            }
            if (reverse) {
                String[] newS = new String[splitstring.length];
                for (int i = 0; i < splitstring.length; i++) {
                    String temp = splitstring[i];
                    newS[i] = splitstring[splitstring.length - i - 1];
                    newS[splitstring.length - i - 1] = temp;
                }
                Text text = new TranslatableText("chat.type.text", new Object[]{this.player.getDisplayName(), String.join("", newS)});
                server.getPlayerManager().broadcastChatMessage(text, MessageType.CHAT, this.player.getUuid());
                ci.cancel();
            }
        }
    }
}