package net.wurstclient.mixin;

import net.minecraft.client.gui.screen.ingame.ContainerScreen54;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContainerScreen54.class)
public class ContainerScreenMixin {

	@Inject(at = {@At("TAIL")},
			method = {"render(IIF)V"})
	protected void onRender(int int_1, int int_2, float float_1, CallbackInfo ci)
	{
		System.out.println("Hello world!");
	}
}
