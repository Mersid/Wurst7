package net.wurstclient.hacks;

import net.minecraft.network.Packet;
import net.wurstclient.Category;
import net.wurstclient.events.PacketInputListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;

import java.util.Map;
import java.util.TreeMap;

public class ServerPacketLoggerHack extends Hack implements PacketInputListener, UpdateListener {

	private final Map<String, Integer> packetCountMap = new TreeMap<>();
	private long enableTime;

	private final CheckboxSetting clearMap = new CheckboxSetting(
			"Clear packet map on disable", "As the name implies.", false);

	public ServerPacketLoggerHack()
	{
		super("PacketLogger", "Logs packets sent from the server.");
		setCategory(Category.OTHER);
		addSetting(clearMap);
	}

	@Override
	public void onEnable()
	{
		EVENTS.add(PacketInputListener.class, this);
		EVENTS.add(UpdateListener.class, this);
		enableTime = System.currentTimeMillis();
	}

	@Override
	protected void onDisable()
	{
		EVENTS.remove(PacketInputListener.class, this);
		EVENTS.remove(UpdateListener.class, this);

		System.out.println(dumpMap());
		System.out.println("Scan time: " + ((System.currentTimeMillis() - enableTime) / 1000f));
		System.out.println("Ticks passed: " + ticks);

		if (clearMap.isChecked())
			packetCountMap.clear();
		ticks = 0;
	}

	@Override
	public void onReceivedPacket(PacketInputEvent event)
	{
		addPacket(event.getPacket());
	}

	private void addPacket(Packet<?> packet)
	{
		String packetName = packet.getClass().getName();
		int packetCount = packetCountMap.get(packetName) == null ? 0 : packetCountMap.get(packetName);
		packetCountMap.put(packetName, packetCount + 1);
	}

	public String dumpMap()
	{
		StringBuilder stringBuilder = new StringBuilder();
		for (Map.Entry<String, Integer> set : packetCountMap.entrySet())
		{
			stringBuilder.append(set.getKey()).append(": ").append(set.getValue()).append("\n");
		}
		return stringBuilder.toString();
	}

	int ticks;
	@Override
	public void onUpdate()
	{
		ticks++;
	}
}
