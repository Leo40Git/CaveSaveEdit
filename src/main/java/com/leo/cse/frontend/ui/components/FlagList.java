package com.leo.cse.frontend.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.leo.cse.backend.profile.NormalProfile;
import com.leo.cse.backend.profile.Profile.ProfileFieldException;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class FlagList extends Component {

	public static final String DESC_NONE = "(no description)";
	public static final String DESC_ENGINE = "(engine flag)";
	public static final String DESC_MIM = "(<MIM data)";
	public static final String DESC_VAR = "(<VAR data)";
	public static final String DESC_PHY = "(<PHY data)";
	public static final String DESC_BUY = "(<BUY data)";

	public static String getFlagDesc(int id) {
		if (id <= 10)
			return DESC_ENGINE;
		if (MCI.getSpecial("VarHack")) {
			if (id >= 6000 && id <= 8000)
				return DESC_VAR;
			if (MCI.getSpecial("PhysVarHack"))
				if (id >= 5632 && id <= 5888)
					return DESC_PHY;
		} else if (MCI.getSpecial("MimHack")) {
			if (id >= 7968 && id <= 7993)
				return DESC_MIM;
		} else if (MCI.getSpecial("BuyHack")) {
			if (id >= 7968 && id <= 7993)
				return DESC_BUY;
		}
		String ret = MCI.getNullable("Flag", id);
		return (ret == null ? DESC_NONE : ret);
	}

	public static boolean isFlagValid(int id) {
		if (id <= 10)
			return false;
		if (MCI.getSpecial("VarHack")) {
			boolean ret = (id < 5999 || id > 7999);
			if (MCI.getSpecial("PhysVarHack"))
				ret &= (id < 5632 || id > 5888);
			return ret;
		} else if (MCI.getSpecial("MimHack")) {
			return (id < 7968 || id > 7993);
		} else if (MCI.getSpecial("BuyHack")) {
			return (id < 7968 || id > 7999);
		}
		if (id == MCI.getInteger("Flag.SaveID", 431))
			return false;
		return true;
	}

	protected Supplier<Boolean> huSup;
	protected Supplier<Boolean> hsSup;

	private static class Flag {
		private int id;
		private boolean hover;

		public Flag(int id, boolean hover) {
			this.id = id;
			this.hover = hover;
		}
		
		public Flag(int id) {
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

	protected List<Flag> shownFlags;
	
	protected void createShownFlags() {
		if (shownFlags == null)
			shownFlags = new ArrayList<>();
	}

	public void calculateShownFlags() {
		createShownFlags();
		shownFlags.clear();
		for (int i = 0; i < 8000; i++)
			if ((!huSup.get() || !DESC_NONE.equals(getFlagDesc(i))) && (!hsSup.get() || isFlagValid(i)))
				shownFlags.add(new Flag(i));
	}

	public FlagList(Supplier<Boolean> huSup, Supplier<Boolean> hsSup) {
		super(0, 0, Main.WINDOW_SIZE.width, 0);
		this.huSup = huSup;
		this.hsSup = hsSup;
		calculateShownFlags();
	}

	@Override
	public void render(Graphics g) {
		height = shownFlags.size() * 17 + 1;
		final int x = 4;
		int y = 1;
		g.setFont(Resources.font);
		for (Flag flag : shownFlags) {
			int id = flag.getId();
			if (flag.isHover())
				g.setColor(new Color(Main.lineColor.getRed(), Main.lineColor.getGreen(), Main.lineColor.getBlue(), 31));
			else
				g.setColor(Main.COLOR_BG);
			g.fillRect(x, y, 16, 16);
			BufferedImage chkImage = Resources.checkboxDisabled;
			if (isFlagValid(id))
				try {
					chkImage = ((boolean) ProfileManager.getField(NormalProfile.FIELD_FLAGS, id) ? Resources.checkboxOn
							: Resources.checkboxOff);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
			g.drawImage(chkImage, x, y, null);
			g.setColor(Main.lineColor);
			FrontUtils.drawString(g, FrontUtils.padLeft(Integer.toUnsignedString(id), "0", 4), x + 18, y - 2);
			FrontUtils.drawString(g, getFlagDesc(id), x + 46, y - 2);
			y += 17;
		}
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		calculateShownFlags();
		height = shownFlags.size() * 17 + 1;
		final int fx = 4;
		int fy = 1;
		for (Flag flag : shownFlags) {
			int id = flag.getId();
			if (!isFlagValid(id))
				continue;
			if (FrontUtils.pointInRectangle(x, y, fx, fy, 16, 16)) {
				try {
					boolean value = (boolean) ProfileManager.getField(NormalProfile.FIELD_FLAGS, id);
					ProfileManager.setField(NormalProfile.FIELD_FLAGS, id, !value);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				break;
			}
			fy += 17;
		}
	}

	@Override
	public void updateHover(int x, int y, boolean hover) {
		super.updateHover(x, y, hover);
		calculateShownFlags();
		height = shownFlags.size() * 17 + 1;
		final int fx = 4;
		int fy = 1;
		for (Flag flag : shownFlags) {
			boolean fHover = false;
			if (hover && FrontUtils.pointInRectangle(x, y - 17, fx, fy, 16, 16))
				fHover = true;
			flag.setHover(fHover);
			fy += 17;
		}
	}

}
