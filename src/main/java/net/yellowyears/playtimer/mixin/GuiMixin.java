package net.yellowyears.playtimer.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.yellowyears.playtimer.GuiPlayTime;

@Mixin(InGameHud.class)
public abstract class GuiMixin {

	@Inject(method="render", at=@At("HEAD"))
	private void beforeRenderDebugScreen(DrawContext context, float f, CallbackInfo ci) {
		GuiPlayTime.getInstance().render(context);
	}

}