package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

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
		int amount = 1;
		if (shiftDown)
			amount *= 10;
		if (ctrlDown)
			amount *= 100;
		if (y < this.y + width)
			scrollbarY -= amount;
		else if (y > this.y + height - width)
			scrollbarY += amount;
		limitScroll();
		return false;
	}

	@Override
	public void onKey(int code, boolean shiftDown, boolean ctrlDown) {
		if (code == KeyEvent.VK_HOME)
			scrollbarY = 0;
		else if (code == KeyEvent.VK_END)
			scrollbarY = Integer.MAX_VALUE;
		limitScroll();
	}

	@Override
	public void onDrag(int x, int y) {
		y -= 25;
		if (y < this.y + width || y > this.y + height - width)
			return;
		scrollbarY = y;
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
		g.drawImage(Resources.arrowUp, x + 6, y + 6, null);
		g.drawImage(Resources.arrowDown, x + 6, y + height - width + 6, null);
	}

}
