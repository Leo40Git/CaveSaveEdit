package com.leo.cse.frontend.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.leo.cse.backend.Profile;
import com.leo.cse.frontend.Defines;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class FlagsUI extends Component implements IScrollable {

	private static final int FLAGS_PER_SCROLL = 23;

	private Supplier<Integer> sSup;
	private Consumer<Integer> update;

	public FlagsUI(Supplier<Integer> sSup, Consumer<Integer> update) {
		super(0, 0, Main.WINDOW_SIZE.width, Main.WINDOW_SIZE.height - 33);
		this.sSup = sSup;
		this.update = update;
	}

	private boolean flagIsValid(int id) {
		if (Defines.getSpecial("VarHack")) {
			boolean ret = (id < 5999 || id > 7999);
			if (Defines.getSpecial("PhysVarHack"))
				ret &= (id < 5632 || id > 5888);
			return ret;
		} else if (Defines.getSpecial("MimHack")) {
			return (id < 7968 || id > 7993);
		}
		return true;
	}

	private String getFlagDescription(int id) {
		if (Defines.getSpecial("VarHack")) {
			if (id >= 5999 && id <= 7999)
				return Defines.get("Flag.VarHack");
			if (Defines.getSpecial("PhysVarHack"))
				if (id >= 5632 && id <= 5888)
					return Defines.get("Flag.PhysVarHack");
		} else if (Defines.getSpecial("MimHack")) {
			if (id >= 7968 && id <= 7993)
				return Defines.get("Flag.MimHack");
		}
		return Defines.get("Flag", id);
	}

	@Override
	public void render(Graphics g) {
		final Dimension winSize = Main.window.getActualSize(true);
		final boolean[] flags = Profile.getFlags();
		final int xx = 4;
		int yy = 16;
		for (int i = sSup.get(); i < Math.min(flags.length, sSup.get() + FLAGS_PER_SCROLL); i++) {
			Image chkImage = Resources.checkboxDisabled;
			if (flagIsValid(i))
				chkImage = (Profile.getFlag(i) ? Resources.checkboxOn : Resources.checkboxOff);
			g.drawImage(chkImage, xx - 2, yy - 12, null);
			g.drawString(FrontUtils.padLeft(Integer.toString(i), "0", 4), xx + 16, yy);
			g.drawString(getFlagDescription(i), xx + 42, yy);
			yy += 18;
		}
		g.drawLine(winSize.width - 20, 0, winSize.width - 20, winSize.height);
		g.drawLine(winSize.width - 20, 20, winSize.width, 20);
		g.drawImage(Resources.arrowUp, winSize.width - 14, 6, null);
		g.drawLine(winSize.width - 20, winSize.height - 22, winSize.width, winSize.height - 22);
		g.drawLine(winSize.width - 20, winSize.height - 2, winSize.width, winSize.height - 2);
		g.drawImage(Resources.arrowDown, winSize.width - 14, winSize.height - 15, null);
		g.drawRect(winSize.width - 18,
				22 + (int) (((float) sSup.get() / (flags.length - FLAGS_PER_SCROLL)) * (winSize.height - 62)), 16, 16);
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		final Dimension winSize = Main.window.getActualSize(true);
		final boolean[] flags = Profile.getFlags();
		if (x >= winSize.width - 20) {
			int amount = 1;
			if (shiftDown)
				amount *= 10;
			if (ctrlDown)
				amount *= 100;
			if (FrontUtils.pointInRectangle(x, y, winSize.width - 20, 0, 20, 20))
				update.accept(Math.max(sSup.get() - amount, 0));
			else if (FrontUtils.pointInRectangle(x, y, winSize.width - 20, winSize.height - 22, 20, 20))
				update.accept(Math.min(sSup.get() + amount, flags.length - FLAGS_PER_SCROLL));
		} else {
			final int xx = 4;
			int yy = 16;
			for (int i = sSup.get(); i < Math.min(flags.length, sSup.get() + FLAGS_PER_SCROLL); i++) {
				if (flagIsValid(i) && FrontUtils.pointInRectangle(x, y, xx, yy - 16, 16, 16)) {
					Profile.setFlag(i, !flags[i]);
					break;
				}
				yy += 18;
			}
		}
	}

	@Override
	public void onScroll(int rotations, boolean shiftDown, boolean ctrlDown) {
		update.accept(Math.max(0, Math.min(sSup.get() + (rotations * (shiftDown ? 10 : 1)) * (ctrlDown ? 100 : 1),
				Profile.getFlags().length - FLAGS_PER_SCROLL)));
	}

}
