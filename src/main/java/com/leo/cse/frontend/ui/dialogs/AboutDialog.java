package com.leo.cse.frontend.ui.dialogs;

import java.awt.Graphics;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class AboutDialog extends BaseDialog {

	public AboutDialog() {
		super("About CaveSaveEdit v" + Main.VERSION, 422, 106);
	}

	@Override
	public void render(Graphics g) {
		super.render(g);
		final int x = getWindowX(), y = getWindowY();
		g.drawImage(Resources.icon, x + 4, y + 4, null);
		g.setColor(Main.lineColor);
		FrontUtils.drawString(g,
				"CaveSaveEdit version " + Main.VERSION
						+ "\nWritten by Leo40Story\nBased on Kapow's profile specs (http://www.cavestory.org/guides/profile.txt)\nUI sprites by zxin\nEarly testers: zxin and gamemanj/20kdc",
				x + 38, y);
		g.drawRect(x + 136, y + 80, 192, 17);
		FrontUtils.drawStringCentered(g, "Check for Updates", x + 232, y + 80);
	}

	@Override
	public boolean onClick(int x, int y) {
		if (super.onClick(x, y))
			return true;
		final int wx = getWindowX(), wy = getWindowY();
		if (FrontUtils.pointInRectangle(x, y, wx + 136, wy + 64, 192, 17))
			Main.updateCheck(true, true);
		return false;
	}

}
