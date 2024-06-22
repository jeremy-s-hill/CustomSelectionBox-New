package me.shedaniel.csb.mixin;

import me.shedaniel.csb.gui.CSBSettingsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayerEntity {
    
    @Shadow @Final private MinecraftClient client;
    
    @Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
    public void sendChatMessage(String message, CallbackInfo ci) {
        String[] split = message.toLowerCase().split(" ");
        if (split.length > 0 && split[0].contentEquals("csbconfig")) {
            client.setScreen(new CSBSettingsScreen(client.currentScreen instanceof ChatScreen ? null : client.currentScreen));
            ci.cancel();
        }
    }
    
}
