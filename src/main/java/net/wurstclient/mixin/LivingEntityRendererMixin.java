/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.wurstclient.WurstClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin <T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {
	protected LivingEntityRendererMixin(EntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Redirect(at = @At(value = "INVOKE",
		target = "Lnet/minecraft/entity/LivingEntity;isInvisibleTo(Lnet/minecraft/entity/player/PlayerEntity;)Z",
		ordinal = 0),
		method = {
			"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"})
	private boolean canWurstSeePlayer(LivingEntity e, PlayerEntity player)
	{
		if(WurstClient.INSTANCE.getHax().trueSightHack.isEnabled())
			return false;
		
		return e.isInvisibleTo(player);
	}


	// This replaces net.minecraft.client.render.entity.LivingEntity.getRenderLayer
	@Redirect(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getRenderLayer(Lnet/minecraft/entity/LivingEntity;ZZ)Lnet/minecraft/client/render/RenderLayer;",
					ordinal = 0
			),
			method = {
					"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
			}
	)
	private RenderLayer customGetRenderLayer(LivingEntityRenderer<T, M> livingEntityRenderer, T livingEntity, boolean showBody, boolean translucent)
	{
		Identifier identifier = livingEntityRenderer.getTexture(livingEntity);

		if (WurstClient.INSTANCE.getHax().playerHideHack.isEnabled())
			return null;

		// Copied/pasted from LivingEntityRenderer
		if (translucent) {
			return RenderLayer.getEntityTranslucent(identifier);
		} else if (showBody) {
			return livingEntityRenderer.getModel().getLayer(identifier);
		} else {
			return livingEntity.isGlowing() ? RenderLayer.getOutline(identifier) : null;
		}
	}
}
