package com.leo.cse.frontend.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.leo.cse.backend.Profile;
import com.leo.cse.frontend.Defines;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class FlagsUI extends Component implements IScrollable {

	private static final int FLAGS_PER_SCROLL = 22;

	private Supplier<Integer> sSup;
	private Consumer<Integer> sUpdate;
	private Supplier<Boolean> huSup;
	private Consumer<Boolean> huUpdate;
	private Supplier<Boolean> hsSup;
	private Consumer<Boolean> hsUpdate;

	private List<Integer> shownFlags;

	public FlagsUI(Supplier<Integer> sSup, Consumer<Integer> sUpdate, Supplier<Boolean> hSup, Consumer<Boolean> hUpdate,
			Supplier<Boolean> hsSup, Consumer<Boolean> hsUpdate) {
		super(0, 0, Main.WINDOW_SIZE.width, Main.WINDOW_SIZE.height - 33);
		this.sSup = sSup;
		this.sUpdate = sUpdate;
		this.huSup = hSup;
		this.huUpdate = hUpdate;
		this.hsSup = hsSup;
		this.hsUpdate = hsUpdate;
	}

	private boolean flagIsValid(int id) {
		if (id <= 10)
			return false;
		if (Defines.getSpecial("VarHack")) {
			boolean ret = (id < 5999 || id > 7999);
			if (Defines.getSpecial("PhysVarHack"))
				ret &= (id < 5632 || id > 5888);
			return ret;
		} else if (Defines.getSpecial("MimHack")) {
			return (id < 7968 || id > 7995);
		}
		return true;
	}

	private String getFlagDescription(int id) {
		if (id <= 10)
			return Defines.get("Flag.Engine");
		if (Defines.getSpecial("VarHack")) {
			if (id >= 6000 && id <= 8000)
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

	private void calculateShownFlags() {
		if (shownFlags == null)
			shownFlags = new ArrayList<Integer>();
		shownFlags.clear();
		for (int i = 0; i < Profile.getFlags().length; i++)
			if ((!huSup.get() || !Defines.get("Flag.None").equals(getFlagDescription(i)))
					&& (!hsSup.get() || flagIsValid(i)))
				shownFlags.add(i);
	}

	@Override
	public void render(Graphics g) {
		calculateShownFlags();
		final Dimension winSize = Main.window.getActualSize(true);
		final int xx = 4;
		int yy = 16;
		for (int i = sSup.get(); i < Math.min(shownFlags.size(), sSup.get() + FLAGS_PER_SCROLL); i++) {
			final int flagId = shownFlags.get(i);
			Image chkImage = Resources.checkboxDisabled;
			if (flagIsValid(flagId))
				chkImage = (Profile.getFlag(flagId) ? Resources.checkboxOn : Resources.checkboxOff);
			g.drawImage(chkImage, xx - 2, yy - 12, null);
			g.drawString(FrontUtils.padLeft(Integer.toString(flagId), "0", 4), xx + 16, yy);
			g.drawString(getFlagDescription(flagId), xx + 42, yy);
			yy += 18;
		}
		g.drawImage((huSup.get() ? Resources.checkboxOn : Resources.checkboxOff), xx - 2,
				16 + 18 * FLAGS_PER_SCROLL - 7, null);
		g.drawString("Hide Undefined Flags?", xx + 16, 16 + 18 * FLAGS_PER_SCROLL + 5);
		g.drawImage((hsSup.get() ? Resources.checkboxOn : Resources.checkboxOff), xx + 148,
				16 + 18 * FLAGS_PER_SCROLL - 7, null);
		g.drawString("Hide System Flags?", xx + 166, 16 + 18 * FLAGS_PER_SCROLL + 5);
		g.drawLine(winSize.width - 20, 0, winSize.width - 20, winSize.height - 20);
		g.drawLine(winSize.width - 20, 20, winSize.width, 20);
		g.drawImage(Resources.arrowUp, winSize.width - 14, 6, null);
		g.drawLine(winSize.width - 20, winSize.height - 40, winSize.width, winSize.height - 40);
		g.drawLine(0, winSize.height - 20, winSize.width, winSize.height - 20);
		g.drawImage(Resources.arrowDown, winSize.width - 14, winSize.height - 33, null);
		g.drawRect(winSize.width - 18,
				22 + (int) (((float) sSup.get() / (shownFlags.size() - FLAGS_PER_SCROLL)) * (winSize.height - 80)), 16,
				16);
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		calculateShownFlags();
		final Dimension winSize = Main.window.getActualSize(true);
		if (x >= winSize.width - 20) {
			int amount = 1;
			if (shiftDown)
				amount *= 10;
			if (ctrlDown)
				amount *= 100;
			if (FrontUtils.pointInRectangle(x, y, winSize.width - 20, 0, 20, 20))
				sUpdate.accept(Math.max(sSup.get() - amount, 0));
			else if (FrontUtils.pointInRectangle(x, y, winSize.width - 20, winSize.height - 40, 20, 20))
				sUpdate.accept(Math.min(sSup.get() + amount, shownFlags.size() - FLAGS_PER_SCROLL));
		} else {
			final int xx = 4;
			int yy = 16;
			for (int i = sSup.get(); i < Math.min(shownFlags.size(), sSup.get() + FLAGS_PER_SCROLL); i++) {
				final int flagId = shownFlags.get(i);
				if (flagIsValid(flagId) && FrontUtils.pointInRectangle(x, y, xx, yy - 16, 16, 16)) {
					Profile.setFlag(flagId, !Profile.getFlag(flagId));
					break;
				}
				yy += 18;
			}
			if (FrontUtils.pointInRectangle(x, y, xx, 16 + 18 * FLAGS_PER_SCROLL - 9, 16, 16)) {
				huUpdate.accept(!huSup.get());
				calculateShownFlags();
				sUpdate.accept(Math.min(sSup.get(), shownFlags.size() - FLAGS_PER_SCROLL));
			}
			if (FrontUtils.pointInRectangle(x, y, xx + 148, 16 + 18 * FLAGS_PER_SCROLL - 9, 16, 16)) {
				hsUpdate.accept(!hsSup.get());
				calculateShownFlags();
				sUpdate.accept(Math.min(sSup.get(), shownFlags.size() - FLAGS_PER_SCROLL));
			}
		}
	}

	@Override
	public void onScroll(int rotations, boolean shiftDown, boolean ctrlDown) {
		sUpdate.accept(Math.max(0, Math.min(sSup.get() + (rotations * (shiftDown ? 10 : 1)) * (ctrlDown ? 100 : 1),
				shownFlags.size() - FLAGS_PER_SCROLL)));
	}

}
