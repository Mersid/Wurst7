package net.wurstclient.hacks;

import net.wurstclient.Category;
import net.wurstclient.hack.Hack;

public class PlayerHideHack extends Hack {

	public PlayerHideHack() {
		super("PlayerHide", "Hides players and other entities. Useful for some minigames.");
		setCategory(Category.RENDER);
	}
}
