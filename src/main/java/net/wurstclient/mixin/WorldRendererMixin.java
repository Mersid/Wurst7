package net.wurstclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.wurstclient.WurstClient;
import net.wurstclient.events.PartialRenderListener;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@Inject(at = @At(
				value = "FIELD",
				target = "Lnet/minecraft/client/render/WorldRenderer;transparencyShader:Lnet/minecraft/client/gl/ShaderEffect;",
				opcode = Opcodes.GETFIELD,
				ordinal = 0),
			method = "render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/util/math/Matrix4f;)V",
			cancellable = true
	)
	public void onRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline,
	                     Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci)
	{
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		PartialRenderListener.PartialRenderEvent partialRenderEvent = new PartialRenderListener.PartialRenderEvent(tickDelta);
		WurstClient.INSTANCE.getEventManager().fire(partialRenderEvent);
		RenderSystem.enableBlend();
		RenderSystem.disableDepthTest();
	}
}
