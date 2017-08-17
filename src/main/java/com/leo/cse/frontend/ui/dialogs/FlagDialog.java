package com.leo.cse.frontend.ui.dialogs;

import java.awt.Graphics;
import java.awt.Image;
import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.backend.profile.Profile;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.components.FlagList;
import com.leo.cse.frontend.ui.components.ShortBox;

public class FlagDialog extends BaseDialog {

	private ShortBox sbox;
	private short flag;

	public FlagDialog() {
		super("Set flag...", 720, 58);
		sbox = new ShortBox(44, 4, 28, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				return flag;
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				t = (short) Math.max(0, Math.min(7999, t));
				flag = t;
				return t;
			}
		}, "flag ID", 4);
	}

	@Override
	public void render(Graphics g) {
		super.render(g);
		final int x = getWindowX(), y = getWindowY();
		g.setColor(Main.lineColor);
		FrontUtils.drawString(g, "Flag ID:", x + 4, y + 2);
		g.translate(x, y);
		sbox.render(g);
		g.translate(-x, -y);
		FrontUtils.drawString(g, "State: ", x + 76, y + 2);
		Image chkImage = Resources.checkboxDisabled;
		if (FlagList.isFlagValid(flag))
			chkImage = (Profile.getFlag(flag) ? Resources.checkboxOn : Resources.checkboxOff);
		g.drawImage(chkImage, x + 106, y + 4, null);
		FrontUtils.drawString(g, "Description:\n" + FlagList.getFlagDesc(flag), x + 4, y + 18);
	}

	@Override
	public boolean onClick(int x, int y) {
		if (super.onClick(x, y))
			return true;
		final int wx = getWindowX(), wy = getWindowY();
		if (FrontUtils.pointInRectangle(x, y, wx + sbox.getX(), wy + sbox.getY(), sbox.getWidth(), sbox.getHeight()))
			sbox.onClick(x, y, false, false);
		if (FrontUtils.pointInRectangle(x, y, wx + 106, wy + 4, 16, 16))
			if (FlagList.isFlagValid(flag)) {
				Profile.setFlag(flag, !Profile.getFlag(flag));
				Main.setTitle(Main.window);
			}
		return false;
	}

}