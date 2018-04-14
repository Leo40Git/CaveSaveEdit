package com.leo.cse.frontend.ui.components.list;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.exe.MapInfo;
import com.leo.cse.backend.profile.NormalProfile;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.components.Component;

public class MapFlagList extends Component {

	public static final String DESC_NONE = "(unknown map)";

	public static String getMapFlagName(int id) {
		String mapName = null;
		if (ExeData.isLoaded()) {
			if (isMapFlagValid(id)) {
				MapInfo mi = ExeData.getMapInfo(id);
				mapName = mi.getFileName() + " - " + mi.getMapName();
			}
		} else
			mapName = MCI.get("Map", id);
		if (mapName == null)
			return DESC_NONE;
		return mapName;
	}

	public static boolean isMapFlagValid(int id) {
		if (id > 127)
			return false;
		if (ExeData.isLoaded()) {
			if (ExeData.getMapInfoCount() - 1 < id)
				return false;
		} else {
			if (MCI.getNullable("Map", id) == null)
				return false;
		}
		return true;
	}

	private static class MapFlag {
		private int id;
		private boolean hover;

		public MapFlag(int id, boolean hover) {
			this.id = id;
			this.hover = hover;
		}

		public MapFlag(int id) {
			this(id, false);
		}

		public int getId() {
			return id;
		}

		public boolean isHover() {
			return hover;
		}

		public void setHover(boolean hover) {
			this.hover = hover;
		}
	}

	protected List<MapFlag> shownFlags;

	protected void createShownFlags() {
		if (shownFlags == null)
			shownFlags = new ArrayList<>();
	}

	public void calculateShownFlags() {
		createShownFlags();
		shownFlags.clear();
		for (int i = 0; i < 127; i++)
			if (isMapFlagValid(i))
				shownFlags.add(new MapFlag(i));
	}

	public void recalculateHeight() {
		height = (shownFlags.size() + 1) * 24;
	}

	public MapFlagList() {
		super("MapFlagList", 0, 0, Main.WINDOW_SIZE.width, 0);
		calculateShownFlags();
	}

	@Override
	public void render(Graphics g, Rectangle viewport) {
		recalculateHeight();
		g.setColor(Main.lineColor);
		final int x = 4;
		g.setFont(Resources.fontS);
		FrontUtils.drawString(g, "State", x - 2, 4);
		g.setFont(Resources.font);
		g.drawLine(x + 20, 0, x + 20, height);
		FrontUtils.drawString(g, "ID", x + 24, 2);
		g.drawLine(x + 44, 0, x + 44, height);
		FrontUtils.drawString(g, "Map Name", x + 48, 2);
		int y = 28;
		g.drawLine(0, y - 4, viewport.width, y - 4);
		for (MapFlag flag : shownFlags) {
			if (y + 16 < viewport.getY()) {
				y += 24;
				continue;
			}
			int id = flag.getId();
			if (flag.isHover())
				g.setColor(new Color(Main.lineColor.getRed(), Main.lineColor.getGreen(), Main.lineColor.getBlue(), 31));
			else
				g.setColor(Main.COLOR_BG);
			g.fillRect(x, y, 16, 16);
			BufferedImage chkImage = Resources.checkboxDisabled;
			if (isMapFlagValid(id))
				chkImage = ((boolean) ProfileManager.getField(NormalProfile.FIELD_MAP_FLAGS, id) ? Resources.checkboxOn
						: Resources.checkboxOff);
			g.drawImage(chkImage, x, y, null);
			g.setColor(Main.lineColor);
			FrontUtils.drawString(g, FrontUtils.padLeft(Integer.toUnsignedString(id), "0", 3), x + 24, y - 2);
			FrontUtils.drawString(g, getMapFlagName(id), x + 48, y - 2);
			y += 24;
			if (y > viewport.getY() + viewport.getHeight())
				break;
			g.drawLine(0, y - 4, viewport.width, y - 4);
		}
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		calculateShownFlags();
		recalculateHeight();
		final int fx = 4;
		int fy = 28;
		for (MapFlag flag : shownFlags) {
			int id = flag.getId();
			if (!isMapFlagValid(id)) {
				fy += 24;
				continue;
			}
			if (FrontUtils.pointInRectangle(x, y, fx, fy, 16, 16)) {
				boolean value = (boolean) ProfileManager.getField(NormalProfile.FIELD_MAP_FLAGS, id);
				ProfileManager.setField(NormalProfile.FIELD_MAP_FLAGS, id, !value);
				break;
			}
			fy += 24;
		}
	}

	@Override
	public void updateHover(int x, int y, boolean hover) {
		super.updateHover(x, y, hover);
		calculateShownFlags();
		recalculateHeight();
		final int fx = 4;
		int fy = 28;
		for (MapFlag flag : shownFlags) {
			boolean fHover = false;
			if (hover && FrontUtils.pointInRectangle(x, y - 18, fx, fy, 16, 16))
				fHover = true;
			flag.setHover(fHover);
			fy += 24;
		}
	}

}
