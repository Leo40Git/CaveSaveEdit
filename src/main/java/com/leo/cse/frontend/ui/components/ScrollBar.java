package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class ScrollBar extends Component implements IDraggable, IScrollable {

	public static final int WIDTH = 20;

	protected int scrollbarY;
	protected boolean scrolling;

	public ScrollBar(int x, int y, int height) {
		super(x, y, WIDTH, height);
		limitScroll();
	}

	protected void limitScroll() {
		scrollbarY = Math.max(y + 2 + width, Math.min(y + height - 18 - width, scrollbarY));
	}

	public float getValue() {
		float sby = scrollbarY;
		sby -= y + 2 + width;
		sby = (float) sby / (height - width * 3);
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
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		scrolling = false;
		int amount = 1;
		if (shiftDown)
			amount *= 10;
		if (ctrlDown)
			amount *= 100;
		if (y < this.y + width)
			scrollbarY -= amount;
		else if (y > this.y + height - width)
			scrollbarY += amount;
		else if (y > this.y + width && y < this.y + height - width)
			onDrag(x, y + 17);
		limitScroll();
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
		if (!scrolling)
			if (y < this.y + width || y > this.y + height - width)
				return;
		scrolling = true;
		scrollbarY = y;
		limitScroll();
	}

	@Override
	public void onDragEnd(int px, int py) {
		scrolling = false;
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
