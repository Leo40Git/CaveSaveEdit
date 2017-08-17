package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import com.leo.cse.frontend.Main;

public class ScrollWrapper extends Component implements IScrollable, IDraggable {

	protected Component wrapped;
	protected IScrollable wrappedS;
	protected IDraggable wrappedD;
	protected ScrollBar scrollbar;
	protected boolean scrolling;

	public ScrollWrapper(Component wrapped, int x, int y, int width, int height) {
		super(x, y, width + ScrollBar.WIDTH, height);
		this.wrapped = wrapped;
		if (wrapped instanceof IScrollable)
			wrappedS = (IScrollable) wrapped;
		if (wrapped instanceof IDraggable)
			wrappedD = (IDraggable) wrapped;
		scrollbar = new ScrollBar(x + width, y, height);
	}

	@Override
	public void onDrag(int px, int py) {
		if (px >= width - ScrollBar.WIDTH || scrolling) {
			scrolling = true;
			scrollbar.onDrag(px, py);
		} else if (wrappedD != null)
			wrappedD.onDrag(px, py);
	}

	@Override
	public void onDragEnd(int px, int py) {
		scrollbar.onDragEnd(px, py);
		if (wrappedD != null)
			wrappedD.onDragEnd(px, py);
		scrolling = false;
	}

	@Override
	public void onScroll(int rotations, boolean shiftDown, boolean ctrlDown) {
		scrollbar.onScroll(rotations, shiftDown, ctrlDown);
		if (wrappedS != null)
			wrappedS.onScroll(rotations, shiftDown, ctrlDown);
	}

	protected BufferedImage wrapSurf;

	@Override
	public void render(Graphics g) {
		final int wsWidth = wrapped.getWidth(), wsHeight = wrapped.getHeight();
		if (wrapSurf == null)
			wrapSurf = new BufferedImage(wsWidth, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D wg = (Graphics2D) wrapSurf.getGraphics();
		wg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		wg.setColor(Main.COLOR_BG);
		wg.fillRect(0, 0, wsWidth, height);
		wg.translate(-wrapped.getX(), -wrapped.getY() + (int) ((height - wsHeight) * scrollbar.getValue()));
		wrapped.render(wg);
		wg.dispose();
		g.drawImage(wrapSurf, x, y + 1, null);
		scrollbar.render(g);
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		if (x > width - ScrollBar.WIDTH)
			scrollbar.onClick(x, y, shiftDown, ctrlDown);
		else {
			int yo = (int) ((height - wrapped.getHeight()) * scrollbar.getValue());
			wrapped.onClick(x, y - yo, shiftDown, ctrlDown);
		}
	}

	public ScrollBar getScrollbar() {
		return scrollbar;
	}

}
