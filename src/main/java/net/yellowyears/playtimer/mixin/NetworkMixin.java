package net.yellowyears.playtimer.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.yellowyears.playtimer.GuiPlayTime;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class NetworkMixin {

    @Inject(method="onStatistics", at=@At("TAIL"))
    public void afterStatisticsUpdate(StatisticsS2CPacket packet, CallbackInfo cb) {
        GuiPlayTime.getInstance().checkStats();
    }

}
