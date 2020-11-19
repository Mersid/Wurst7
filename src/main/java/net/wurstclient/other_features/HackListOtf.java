/*
 * Copyright (c) 2014-2020 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.other_features;

import net.wurstclient.DontBlock;
import net.wurstclient.SearchTags;
import net.wurstclient.other_feature.OtherFeature;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"ArrayList", "ModList", "CheatList", "mod list", "array list",
	"hack list", "cheat list"})
@DontBlock
public final class HackListOtf extends OtherFeature
{
	private final EnumSetting<Mode> mode = new EnumSetting<>("Mode",
		"\u00a7lAuto\u00a7r mode renders the whole list if it\n"
			+ "fits onto the screen.\n"
			+ "\u00a7lCount\u00a7r mode only renders the number\n"
			+ "of active hacks.\n"
			+ "\u00a7lHidden\u00a7r mode renders nothing.",
		Mode.values(), Mode.AUTO);
	
	private final EnumSetting<Position> position =
		new EnumSetting<>("Position", Position.values(), Position.LEFT);
	
	private final CheckboxSetting animations =
		new CheckboxSetting("Animations", true);
	
	public final SliderSetting offsetX = new SliderSetting("UI Offset X", "Offsets the UI in the x axis", 0, -20, 300, 1, ValueDisplay.INTEGER);
	public final SliderSetting offsetY = new SliderSetting("UI Offset Y", "Offsets the UI in the y axis", 0, -20, 300, 1, ValueDisplay.INTEGER);
	
	public HackListOtf()
	{
		super("HackList", "Shows a list of active hacks on the screen.\n"
			+ "The \u00a7lLeft\u00a7r position should only be used while TabGui is\n"
			+ "disabled.");
		
		addSetting(mode);
		addSetting(position);
		addSetting(animations);
		addSetting(offsetX);
		addSetting(offsetY);
	}
	
	public Mode getMode()
	{
		return mode.getSelected();
	}
	
	public Position getPosition()
	{
		return position.getSelected();
	}
	
	public boolean isAnimations()
	{
		return animations.isChecked();
	}
	
	public static enum Mode
	{
		AUTO("Auto"),
		
		COUNT("Count"),
		
		HIDDEN("Hidden");
		
		private final String name;
		
		private Mode(String name)
		{
			this.name = name;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
	
	public static enum Position
	{
		LEFT("Left"),
		
		RIGHT("Right");
		
		private final String name;
		
		private Position(String name)
		{
			this.name = name;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}
