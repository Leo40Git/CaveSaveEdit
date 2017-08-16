package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class ScrollBar extends Component implements IDraggable, IScrollable {

	protected int scrollbarY;

	public ScrollBar(int x, int y, int height) {
		super(x, y, 20, height);
		limitScroll();
	}

	protected void limitScroll() {
		scrollbarY = Math.max(y + 2 + width, Math.min(y + height - 18 - width, scrollbarY));
	}

	public int getValue() {
		int sby = scrollbarY;
		sby -= y + 2 + width;
		sby = (int) (((float) sby / (height - width * 3)) * 100);
		return sby;
	}

	@Override
	public void onScroll(int rotations, boolean shiftDown, boolean ctrlDown) {
		if (shiftDown)
			rotations *= 10;
		if (ctrlDown)
			rotations *= 100;
		scrollbarY += rotations;
		limitScroll();
	}

	@Override
	public boolean onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		if (y < this.y + width) {

		} else if (y > this.y + height) {

		} else
			scrollbarY = y - 26;
		limitScroll();
		return false;
	}

	@Override
	public void onDrag(int x, int y) {
		if (y < this.y + width || y > this.y + height)
			return;
		scrollbarY = y - 26;
		limitScroll();
	}

	@Override
	public void onDragEnd(int px, int py) {
		onDrag(x, y);
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Main.lineColor);
		g.drawRect(x, y, width, height);
		g.drawLine(x, y + width, x + width, y + width);
		g.drawLine(x, y + height - width, x + width, y + height - width);
		g.drawRect(x + 2, scrollbarY, 16, 16);
		g.setFont(Resources.font);
		FrontUtils.drawString(g, "scrollbarY=" + scrollbarY + "\nvalue=" + getValue(), x, y + height + 20);
	}

}
