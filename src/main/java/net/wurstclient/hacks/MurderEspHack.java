package net.wurstclient.hacks;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.wurstclient.Category;
import net.wurstclient.WurstClient;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.util.FakePlayerEntity;
import net.wurstclient.util.RenderUtils;
import net.wurstclient.util.RotationUtils;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MurderEspHack extends Hack implements UpdateListener, RenderListener {

	private World world;
	private List<String> playerNames;
	private final List<String> murderWeapons = getMurderWeapons();
	private final List<PlayerEntity> players = new ArrayList<>();
	private final List<PlayerEntity> murderers = new ArrayList<>();
	private final List<PlayerEntity> detectives = new ArrayList<>();

	private int playerBox;


	private final CheckboxSetting broadcastToSelf = new CheckboxSetting(
			"Broadcast murderers to self", "Lists the murderers in the chat. Only you can see it.", false);

	private final CheckboxSetting broadcastToAll = new CheckboxSetting(
			"Broadcast murderers to public", "Automatically lists  the murderers are to the public chat.", false);

	private final CheckboxSetting broadcastToFriends = new CheckboxSetting(
			"Broadcast murderers to friends",
			"Automatically lists the murderers to all friends in the same match using /msg. \n" +
			"This might look very suspicious if you have multiple friends on!", false);


	public MurderEspHack()
	{
		super("MurderESP", "Attempts to detect murderers in Hypixel's Murder Mystery minigame.");
		setCategory(Category.RENDER);

		addSetting(broadcastToSelf);
		addSetting(broadcastToAll);
		addSetting(broadcastToFriends);
	}

	@Override
	public void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(RenderListener.class, this);
		WURST.getHax().playerEspHack.setEnabled(false);
		clearPlayers();

		playerBox = GL11.glGenLists(1);
		GL11.glNewList(playerBox, GL11.GL_COMPILE);
		Box bb = new Box(-0.5, 0, -0.5, 0.5, 1, 0.5);
		RenderUtils.drawOutlinedBox(bb);
		GL11.glEndList();
	}

	@Override
	public void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(RenderListener.class, this);
		clearPlayers();

		GL11.glDeleteLists(playerBox, 1);
		playerBox = 0;
	}

	@Override
	public void onUpdate()
	{
		checkWorldUpdated();
		updatePlayers();
		updatePlayerNames();

		for (PlayerEntity player : players)
		{
			// Test if murderer
			if (murderWeapons.contains(player.getEquippedStack(EquipmentSlot.MAINHAND).getItem().toString()) && !murderers.contains(player))
			{
				murderers.add(player);
				detectives.remove(player);

				if (broadcastToSelf.isChecked())
					ChatUtils.message("\u00a76MurderESP: \u00a7c" + player.getEntityName() + " \u00a7r is a murderer!");

				if (broadcastToFriends.isChecked())
					broadcastMurdererToFriends(player);

				if (broadcastToAll.isChecked())
					broadcastMurdererToAll(player);

			}
			else if (player.getEquippedStack(EquipmentSlot.MAINHAND).getItem() instanceof BowItem)
			{
				detectives.add(player);
			}
		}

	}

	@Override
	public void onRender(float partialTicks)
	{
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(2);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glPushMatrix();
		RenderUtils.applyRenderOffset();

		// draw boxes
		renderBoxes(partialTicks);

		renderTracers(partialTicks);

		GL11.glPopMatrix();

		// GL resets
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}

	private void renderBoxes(double partialTicks)
	{
		double extraSize = 0.1; //boxSize.getSelected().extraSize;

		for(PlayerEntity e : players)
		{
			GL11.glPushMatrix();

			GL11.glTranslated(e.prevX + (e.getX() - e.prevX) * partialTicks,
					e.prevY + (e.getY() - e.prevY) * partialTicks,
					e.prevZ + (e.getZ() - e.prevZ) * partialTicks);

			GL11.glScaled(e.getWidth() + extraSize, e.getHeight() + extraSize,
					e.getWidth() + extraSize);

			// set color
			setGl11Color4f(e);

			GL11.glCallList(playerBox);

			GL11.glPopMatrix();
		}
	}

	private void renderTracers(double partialTicks)
	{
		Vec3d start =
				RotationUtils.getClientLookVec().add(RenderUtils.getCameraPos());

		GL11.glBegin(GL11.GL_LINES);
		for(PlayerEntity e : players)
		{
			Vec3d end = e.getBoundingBox().getCenter()
					.subtract(new Vec3d(e.getX(), e.getY(), e.getZ()).subtract(e.prevX, e.prevY, e.prevZ).multiply(1 - partialTicks));

			// set color
			setGl11Color4f(e);

			GL11.glVertex3d(start.x, start.y, start.z);
			GL11.glVertex3d(end.x, end.y, end.z);
		}
		GL11.glEnd();
	}

	/*
	Sets the GL11 color based on the nae of the PlayerEntity in question
	 */
	private void setGl11Color4f(PlayerEntity e)
	{
		if (WURST.getFriends().contains(e.getEntityName()))
			GL11.glColor4f(0, 1, 0, 0.5F);
		else if (detectives.stream().anyMatch(p -> p.getEntityName().contains(e.getEntityName())))
			GL11.glColor4f(0, 0, 1, 0.5F);
		else if (murderers.stream().anyMatch(p -> p.getEntityName().contains(e.getEntityName())))
			GL11.glColor4f(1, 0, 0, 0.5F);
		else if (playerNames.stream().anyMatch(n -> n.contains(e.getEntityName())))
			GL11.glColor4f(1, 1, 1, 0.5F);
		else  // Most likely NPC player characters.
			GL11.glColor4f(1, 0.5F, 0, 0.5F);
	}

	private void clearPlayers()
	{
		murderers.clear();
		detectives.clear();
		System.out.println("Cleared murderers and detectives list.");
	}

	/*
	Checks if world was the same object as the last check. If not, run the function specified.
	 */
	private void checkWorldUpdated()
	{
		if (WurstClient.MC.player == null)
			return;

		if (world != WurstClient.MC.player.world)
		{
			world = WurstClient.MC.player.world;
			clearPlayers();
		}
	}

	private void updatePlayers()
	{
		// Code copied from PlayerEspHack
		PlayerEntity player = MC.player;
		ClientWorld world = MC.world;

		players.clear();
		Stream<AbstractClientPlayerEntity> stream = world.getPlayers()
				.parallelStream().filter(e -> !e.removed && e.getHealth() > 0)
				.filter(e -> e != player)
				.filter(e -> !(e instanceof FakePlayerEntity))
				.filter(e -> Math.abs(e.getY() - MC.player.getY()) <= 1e6);

		players.addAll(stream.collect(Collectors.toList()));
	}

	private void updatePlayerNames()
	{
		playerNames = MinecraftClient.getInstance().getNetworkHandler().getPlayerList().stream()
				.map(e -> e.getProfile().getName()).collect(Collectors.toList());
	}

	private void broadcastMurdererToFriends(PlayerEntity murderer)
	{
		for (String playerName : playerNames)
		{
			if (WURST.getFriends().contains(playerName))
			{
				say("/msg " + playerName + " " + murderer.getEntityName() + " is a murderer!");
			}
		}
	}

	private void broadcastMurdererToAll(PlayerEntity murderer)
	{
		say(murderer.getEntityName() + " is a murderer!");
	}

	// Sends a chat message to the server
	private void say(String message)
	{
		MC.getNetworkHandler().sendPacket(new ChatMessageC2SPacket(message));
	}

	private List<String> getMurderWeapons()
	{
		List<String> weaponList = new ArrayList<>();
		weaponList.add("iron_sword");
		weaponList.add("stone_sword");
		weaponList.add("iron_shovel");
		weaponList.add("stick");
		weaponList.add("wooden_axe");
		weaponList.add("wooden_sword");
		weaponList.add("dead_bush");
		weaponList.add("stone_shovel");
		weaponList.add("blaze_rod");
		weaponList.add("diamond_shovel");
		weaponList.add("feather");
		weaponList.add("pumpkin_pie");
		weaponList.add("golden_pickaxe");
		weaponList.add("apple");
		weaponList.add("name_tag");
		weaponList.add("sponge");
		weaponList.add("carrot_on_a_stick");
		weaponList.add("bone");
		weaponList.add("carrot");
		weaponList.add("golden_carrot");
		weaponList.add("cookie");
		weaponList.add("diamond_axe");
		weaponList.add("rose_bush");
		weaponList.add("golden_sword");
		weaponList.add("diamond_sword");
		weaponList.add("diamond_hoe");
		weaponList.add("shears");
		weaponList.add("salmon");
		weaponList.add("redstone_torch");
		weaponList.add("oak_boat");

		return weaponList;
	}
}
