package net.wurstclient.hacks;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShape;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.SliderSetting;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.stream.Stream;

public class BridgeAssistHack extends Hack implements UpdateListener {

	private SliderSetting boxSize = new SliderSetting("Box Size Reduction", "Size of the hitbox to reduce from the player's default for the collision detection.\n" +
			"Smaller values means you will sneak closer to the edge.", 0.2, 0, 0.45, 0.05, SliderSetting.ValueDisplay.DECIMAL);

	public BridgeAssistHack()
	{
		super("BridgeAssist", "Helps you be a pro bridge builder! Automatically sneak if you are close to an edge.\n" +
				"Pair this with SafeWalk for extra security.");

		addSetting(boxSize);
	}

	@Override
	public void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
	}

	@Override
	public void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
	}

	@Override
	public void onUpdate()
	{
		if (isNearEdge() && !MC.player.isSneaking())
		{
			MC.options.keySneak.setPressed(true);
		}
		else if (!isNearEdge() && !isSneakActuallyPressed())
		{
			MC.options.keySneak.setPressed(false);
		}
	}

	/**
	 * Checks if a player is near an edge.
	 * @return true if a player is near an edge, false otherwise
	 */
	private boolean isNearEdge()
	{
		if(!MC.player.isOnGround() || MC.options.keyJump.isPressed())
			return false;


		Box detectionHitBox = MC.player.getBoundingBox().offset(0, -0.5, 0).expand(-boxSize.getValue(), 0, -boxSize.getValue());
		Stream<VoxelShape> collisions = MC.world.getBlockCollisions(MC.player, detectionHitBox);

		return collisions != null && collisions.count() == 0;
	}

	/**
	 * Uses {@link InputUtil#isKeyPressed(long, int)} to determine if the sneak key is being held (actual key press, as opposed to synthetic ones generated by Wurst).
	 * Because the currently bound key is a private field, this method uses reflection.
	 * @return Whether the sneak key is actually being pressed
	 */
	private boolean isSneakActuallyPressed()
	{
		try {
			// Probably more effective to use Mixins to Shadow a field...
			Field boundKeyField = KeyBinding.class.getDeclaredField("boundKey");
			boundKeyField.setAccessible(true);

			return InputUtil.isKeyPressed(MC.getWindow().getHandle(), ((InputUtil.Key)boundKeyField.get(MC.options.keySneak)).getCode());
		}
		catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
	}
}
