package com.leo.cse.frontend.ui.dialogs;

import java.awt.Graphics;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class AboutDialog extends BaseDialog {

	public AboutDialog() {
		super("About CaveSaveEdit v" + Main.VERSION, 232, 84);
	}
	
	@Override
	public void render(Graphics g) {
		super.render(g);
		final int x = getWindowX(), y = getWindowY();
		g.drawImage(Resources.icon, x + 4, y + 4, null);
		g.setColor(Main.lineColor);
		FrontUtils.drawString(g, "CaveSaveEdit version " + Main.VERSION + "\nBuild " + Main.BUILD + "\nWritten by Leo40Story\nUI sprites by zxin\nEarly testers: zxin and gamemanj/20kdc", x + 38, y);
	}

}
