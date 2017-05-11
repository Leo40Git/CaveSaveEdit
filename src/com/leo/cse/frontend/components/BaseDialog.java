package com.leo.cse.frontend.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class BaseDialog extends DialogBox {

	protected String title, message;
	protected int width, height;

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
		final Dimension winSize = Main.window.getActualSize();
		return winSize.width / 2 - width / 2;
	}

	protected int getWindowY(boolean excludeHead) {
		final Dimension winSize = Main.window.getActualSize();
		return winSize.height / 2 - height / 2 - (excludeHead ? 0 : 18);
	}

	protected int getWindowY() {
		return getWindowY(true);
	}

	@Override
	public void render(Graphics g) {
		final int x = getWindowX(), y = getWindowY(false);
		g.clearRect(x, y, width, height);
		g.setColor(Color.black);
		g.drawRect(x, y, width, height);
		FrontUtils.drawString(g, title, x + 4, y);
		g.drawLine(x, y + 18, x + width, y + 18);
		g.drawImage(Resources.dialogClose, x + width - 16, y + 2, null);
		if (message != null)
			FrontUtils.drawString(g, message, x + 4, y + 22);
	}

	@Override
	public boolean onClick(int x, int y) {
		final int wx = getWindowX(), wy = getWindowY(false);
		if (FrontUtils.pointInRectangle(x, y, wx + width - 16, wy + 2, 14, 14))
			return true;
		return false;
	}

}
