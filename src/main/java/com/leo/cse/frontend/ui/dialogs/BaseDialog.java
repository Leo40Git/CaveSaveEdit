package com.leo.cse.frontend.ui.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.components.Component;
import com.leo.cse.frontend.ui.components.IScrollable;

public class BaseDialog extends Dialog {

	protected String title, message;
	protected int width, height;
	protected boolean wantsToClose;
	protected IScrollable scrollable;
	private boolean quitHover;
	private List<Component> comps;

	public BaseDialog(String title, String message, int width, int height) {
		this.title = title;
		this.message = message;
		this.width = width;
		this.height = height + 18;
		comps = new ArrayList<>();
	}

	public BaseDialog(String title, int width, int height) {
		this(title, null, width, height);
	}

	protected void addComponent(Component comp) {
		comps.add(comp);
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
		g.setColor(Main.lineColor);
		if (message != null)
			FrontUtils.drawString(g, message, x + 4, y + 22);
		final int yOff = y + 18;
		g.translate(x, yOff);
		for (Component comp : comps)
			comp.render(g);
		g.translate(-x, -yOff);
	}

	@Override
	public void onClick(int x, int y, boolean shift, boolean ctrl) {
		final int wx = getWindowX(), wy = getWindowY(false);
		if (FrontUtils.pointInRectangle(x, y, wx + width - 16, wy + 2, 14, 14)) {
			wantsToClose = true;
			return;
		}
		x -= wx;
		y -= wy + 18;
		if (x < 0 || y < 0)
			return;
		for (Component comp : comps) {
			final int rx = comp.getX(), ry = comp.getY(), rw = comp.getWidth(), rh = comp.getHeight();
			if (FrontUtils.pointInRectangle(x, y, rx, ry, rw, rh)) {
				comp.onClick(x, y, shift, ctrl);
				comp.updateHover(x, y, true);
				break;
			}
		}
	}

	@Override
	public boolean wantsToClose() {
		return wantsToClose;
	}

	@Override
	public void updateHover(int x, int y) {
		final int wx = getWindowX(), wy = getWindowY(false);
		quitHover = false;
		if (FrontUtils.pointInRectangle(x, y, wx + width - 16, wy + 2, 14, 14)) {
			quitHover = true;
			return;
		}
		x -= wx;
		y -= wy + 18;
		if (x < 0 || y < 0)
			return;
		for (Component comp : comps) {
			final int rx = comp.getX(), ry = comp.getY(), rw = comp.getWidth(), rh = comp.getHeight();
			boolean hover = FrontUtils.pointInRectangle(x, y, rx, ry, rw, rh);
			comp.updateHover(x, y, hover);
		}
	}
	
	@Override
	public void onScroll(int rotations, boolean shift, boolean ctrl) {
		if (scrollable != null)
			scrollable.onScroll(rotations, shift, ctrl);
	}

}
