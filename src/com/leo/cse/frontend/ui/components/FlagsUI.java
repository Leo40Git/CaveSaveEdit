package com.leo.cse.frontend.ui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.leo.cse.backend.profile.Profile;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.SaveEditorPanel;
import com.leo.cse.frontend.ui.dialogs.FlagDialog;

public class FlagsUI extends Component implements IScrollable, IDraggable {

	private static final int FLAGS_PER_SCROLL = 35;

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

	public static boolean flagIsValid(int id) {
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

	public static String getFlagDescription(int id) {
		if (id <= 10)
			return MCI.get("Flag.Engine");
		if (MCI.getSpecial("VarHack")) {
			if (id >= 6000 && id <= 8000)
				return MCI.get("Flag.VarHack");
			if (MCI.getSpecial("PhysVarHack"))
				if (id >= 5632 && id <= 5888)
					return MCI.get("Flag.PhysVarHack");
		} else if (MCI.getSpecial("MimHack")) {
			if (id >= 7968 && id <= 7993)
				return MCI.get("Flag.MimHack");
		} else if (MCI.getSpecial("BuyHack")) {
			if (id >= 7968 && id <= 7993)
				return MCI.get("Flag.BuyHack");
		}
		if (id == MCI.getInteger("Flag.SaveID", 431))
			return MCI.get("Flag.Save");
		return MCI.get("Flag", id);
	}

	private void calculateShownFlags() {
		if (shownFlags == null)
			shownFlags = new ArrayList<Integer>();
		shownFlags.clear();
		for (int i = 0; i < Profile.getFlags().length; i++)
			if ((!huSup.get() || !MCI.get("Flag.None").equals(getFlagDescription(i)))
					&& (!hsSup.get() || flagIsValid(i)))
				shownFlags.add(i);
	}

	@Override
	public void render(Graphics g) {
		calculateShownFlags();
		final Dimension winSize = Main.window.getActualSize(true);
		final int xx = 4;
		final int shownFlagsNum = Math.min(shownFlags.size(), sSup.get() + FLAGS_PER_SCROLL);
		int yy = 14;
		for (int i = sSup.get(); i < shownFlagsNum; i++) {
			final int flagId = shownFlags.get(i);
			Image chkImage = Resources.checkboxDisabled;
			if (flagIsValid(flagId))
				chkImage = (Profile.getFlag(flagId) ? Resources.checkboxOn : Resources.checkboxOff);
			g.drawImage(chkImage, xx - 2, yy - 12, null);
			FrontUtils.drawString(g, FrontUtils.padLeft(Integer.toString(flagId), "0", 4), xx + 16, yy - 14);
			FrontUtils.drawString(g, getFlagDescription(flagId), xx + 42, yy - 14);
			yy += 18;
		}
		final int cy = 16 + 18 * FLAGS_PER_SCROLL - 6;
		g.setColor(Main.lineColor);
		g.drawImage((huSup.get() ? Resources.checkboxOn : Resources.checkboxOff), xx - 2, cy - 5, null);
		FrontUtils.drawString(g, "Hide Undefined Flags?", xx + 16, cy - 7);
		g.drawImage((hsSup.get() ? Resources.checkboxOn : Resources.checkboxOff), xx + 148, cy - 5, null);
		FrontUtils.drawString(g, "Hide System Flags?", xx + 166, cy - 7);
		g.drawRect(xx + 296, cy - 5, 200, 15);
		FrontUtils.drawStringCentered(g, "Set specific flag...", xx + 396, cy - 7);
		FrontUtils.drawString(g, "Shift - x10 scroll, Control - x100 scroll, Shift+Ctrl - x1000 scroll", xx + 552,
				cy - 7);
		g.setColor(Main.COLOR_BG);
		g.fillRect(winSize.width - 20, 1, 20, 18);
		g.setColor(Main.lineColor);
		g.drawLine(winSize.width - 20, 0, winSize.width - 20, winSize.height - 22);
		g.drawLine(winSize.width - 20, 20, winSize.width, 20);
		g.drawImage(Resources.arrowUp, winSize.width - 13, 6, null);
		g.setColor(Main.COLOR_BG);
		g.fillRect(winSize.width - 19, winSize.height - 38, 21, 18);
		g.setColor(Main.lineColor);
		g.drawLine(winSize.width - 20, winSize.height - 42, winSize.width, winSize.height - 42);
		g.drawLine(0, winSize.height - 22, winSize.width, winSize.height - 22);
		g.drawImage(Resources.arrowDown, winSize.width - 13, winSize.height - 35, null);
		g.setColor(Main.COLOR_BG);
		g.fillRect(winSize.width - 19, 21, 18, winSize.height - 63);
		g.setColor(Main.lineColor);
		if (shownFlagsNum > FLAGS_PER_SCROLL - 1)
			g.drawRect(winSize.width - 18,
					22 + (int) (((float) sSup.get() / (shownFlags.size() - FLAGS_PER_SCROLL)) * (winSize.height - 82)),
					16, 16);
	}

	@Override
	public boolean onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		calculateShownFlags();
		final Dimension winSize = Main.window.getActualSize(true);
		if (x >= winSize.width - 20) {
			int amount = 1;
			if (shiftDown)
				amount *= 10;
			if (ctrlDown)
				amount *= 100;
			if (FrontUtils.pointInRectangle(x, y, winSize.width - 20, 0, 20, 18))
				sUpdate.accept(Math.max(sSup.get() - amount, 0));
			else if (FrontUtils.pointInRectangle(x, y, winSize.width - 20, winSize.height - 42, 20, 18))
				sUpdate.accept(Math.max(0,
						Math.min(sSup.get() + amount, shownFlags.size() - (shownFlags.size() - FLAGS_PER_SCROLL))));
		} else {
			final int xx = 4;
			int yy = 14;
			for (int i = sSup.get(); i < Math.min(shownFlags.size(), sSup.get() + FLAGS_PER_SCROLL); i++) {
				final int flagId = shownFlags.get(i);
				if (flagIsValid(flagId) && FrontUtils.pointInRectangle(x, y, xx, yy - 16, 16, 16)) {
					Profile.setFlag(flagId, !Profile.getFlag(flagId));
					break;
				}
				yy += 18;
			}
			final int cy = 16 + 18 * FLAGS_PER_SCROLL - 6;
			if (FrontUtils.pointInRectangle(x, y, xx, cy - 7, 16, 16)) {
				huUpdate.accept(!huSup.get());
				calculateShownFlags();
				sUpdate.accept(Math.max(0, Math.min(sSup.get(), shownFlags.size() - FLAGS_PER_SCROLL)));
			}
			if (FrontUtils.pointInRectangle(x, y, xx + 148, cy - 7, 16, 16)) {
				hsUpdate.accept(!hsSup.get());
				calculateShownFlags();
				sUpdate.accept(Math.max(0, Math.min(sSup.get(), shownFlags.size() - FLAGS_PER_SCROLL)));
			}
			if (FrontUtils.pointInRectangle(x, y, xx + 296, cy - 7, 200, 15)) {
				SaveEditorPanel.panel.addDialogBox(new FlagDialog());
			}
		}
		return false;
	}

	@Override
	public void onScroll(int rotations, boolean shiftDown, boolean ctrlDown) {
		sUpdate.accept(Math.max(0, Math.min(sSup.get() + (rotations * (shiftDown ? 10 : 1)) * (ctrlDown ? 100 : 1),
				shownFlags.size() - FLAGS_PER_SCROLL)));
	}

	@Override
	public void onDrag(int x, int y) {
		// TODO Scrollbar functionality
	}

	@Override
	public void onDragEnd(int px, int py) {
		// TODO Scrollbar functionality
	}

}
