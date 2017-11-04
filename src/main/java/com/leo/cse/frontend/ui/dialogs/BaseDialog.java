package com.leo.cse.frontend.ui.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class BaseDialog extends Dialog {

	protected String title, message;
	protected int width, height;
	protected boolean wantsToClose;
	private boolean quitHover;

	public BaseDialog(String title, String message, int width, int height) {
		this.title = title;
		this.message = message;
		this.width = width;
		this.height = height + 18;
	}

	public BaseDialog(String title, int width, int height) {
		this(title, null, width, height);
	}

	protected int getWindowX() {
		final Dimension winSize = Main.window.getActualSize(false);
		return winSize.width / 2 - width / 2;
	}

	protected int getWindowY(boolean excludeHead) {
		final Dimension winSize = Main.window.getActualSize(false);
		return winSize.height / 2 - height / 2 - (excludeHead ? 0 : 18);
	}

	protected int getWindowY() {
		return getWindowY(true);
	}

	@Override
	public void render(Graphics g) {
		final int x = getWindowX(), y = getWindowY(false);
		FrontUtils.drawNineSlice(g, Resources.shadow, x - 16, y - 16, width + 32, height + 32);
		g.setColor(Main.COLOR_BG);
		g.fillRect(x, y, width, height);
		g.setColor(Main.lineColor);
		g.drawRect(x, y, width, height);
		FrontUtils.drawString(g, title, x + 4, y);
		g.drawLine(x, y + 18, x + width, y + 18);
		if (quitHover)
			g.setColor(new Color(Main.lineColor.getRed(), Main.lineColor.getGreen(), Main.lineColor.getBlue(), 31));
		else
			g.setColor(Main.COLOR_BG);
		g.fillRect(x + width - 16, y + 2, 14, 14);
		g.drawImage(Resources.dialogClose, x + width - 16, y + 2, null);
		if (message != null)
			FrontUtils.drawString(g, message, x + 4, y + 22);
	}

	@Override
	public void onClick(int x, int y) {
		final int wx = getWindowX(), wy = getWindowY(false);
		if (FrontUtils.pointInRectangle(x, y, wx + width - 16, wy + 2, 14, 14))
			wantsToClose = true;
	}

	@Override
	public boolean wantsToClose() {
		return wantsToClose;
	}

	@Override
	public void updateHover(int x, int y) {
		final int wx = getWindowX(), wy = getWindowY(false);
		quitHover = false;
		if (FrontUtils.pointInRectangle(x, y, wx + width - 16, wy + 2, 14, 14))
			quitHover = true;
	}

}
