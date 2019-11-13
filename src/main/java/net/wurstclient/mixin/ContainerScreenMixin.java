package net.wurstclient.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.gui.screen.ingame.ContainerScreen54;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(ContainerScreen54.class)
public class ContainerScreenMixin {
/*
	@Inject(at = {@At("HEAD")},
			method = {"activate(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Z"})
	*/

	@Inject(at = {@At("TAIL")},
			method = {"render(IIF)V"})
	protected void onRender(int int_1, int int_2, float float_1, CallbackInfo ci)
	{
		System.out.println("Hello world!");
	}
}
