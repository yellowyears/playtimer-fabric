package be.bjarno.playtimer.mixin;

import be.bjarno.playtimer.GuiPlayTime;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class GuiMixin {

	@Inject(method="render", at=@At("HEAD"))
	private void beforeRenderDebugScreen(MatrixStack stack, float f, CallbackInfo ci) {
		GuiPlayTime.getInstance().render(stack);
	}

}