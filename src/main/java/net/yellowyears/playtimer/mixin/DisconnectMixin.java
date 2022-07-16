package net.yellowyears.playtimer.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.yellowyears.playtimer.GuiPlayTime;

@Mixin(MinecraftClient.class)
public abstract class DisconnectMixin {

    @Inject(method="disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at=@At(value = "TAIL"))
    private void onDisconnect(Screen screen, CallbackInfo cb) {
        GuiPlayTime.removeInstance();
    }

}
