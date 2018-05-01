package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
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
		super("ScrollWrapper[wrapped=" + wrapped.toString() + "]", x, y, width + ScrollBar.WIDTH, height);
		this.wrapped = wrapped;
		if (wrapped instanceof IScrollable)
			wrappedS = (IScrollable) wrapped;
		if (wrapped instanceof IDraggable)
			wrappedD = (IDraggable) wrapped;
		scrollbar = new ScrollBar(x + width, y, height + 1);
	}

	@Override
	public void onDrag(int px, int py, boolean shiftDown, boolean ctrlDown) {
		if (px >= width - ScrollBar.WIDTH || scrolling) {
			scrolling = true;
			scrollbar.onDrag(px, py, shiftDown, ctrlDown);
		} else if (wrappedD != null)
			wrappedD.onDrag(px, py, shiftDown, ctrlDown);
	}

	@Override
	public void onDragEnd(int px, int py, boolean shiftDown, boolean ctrlDown) {
		if (scrolling)
			scrollbar.onDragEnd(px, py, shiftDown, ctrlDown);
		if (wrappedD != null)
			wrappedD.onDragEnd(px, py, shiftDown, ctrlDown);
		scrolling = false;
	}

	@Override
	public void onScroll(int rotations, boolean shiftDown, boolean ctrlDown) {
		if (wrappedS == null)
			scrollbar.onScroll(rotations, shiftDown, ctrlDown);
		else {
			if (x > width - ScrollBar.WIDTH)
				scrollbar.onScroll(rotations, shiftDown, ctrlDown);
			else
				wrappedS.onScroll(rotations, shiftDown, ctrlDown);
		}
	}

	protected BufferedImage wrapSurf;

	@Override
	public void render(Graphics g, Rectangle viewport) {
		final int wsWidth = wrapped.getWidth(), wsHeight = wrapped.getHeight();
		final int scrollOff = (int) ((height - wsHeight) * scrollbar.getValue());
		final Rectangle wViewport = new Rectangle(wrapped.getX(), wrapped.getY() - scrollOff,
				width - scrollbar.getWidth(), height);
		if (wrapSurf == null)
			wrapSurf = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D wg = (Graphics2D) wrapSurf.getGraphics();
		wg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		wg.setColor(Main.COLOR_BG);
		wg.fillRect(0, 0, wsWidth, height);
		wg.translate(-wrapped.getX(), -wrapped.getY() + scrollOff);
		wrapped.render(wg, wViewport);
		wg.dispose();
		g.drawImage(wrapSurf, x, y + 1, null);
		scrollbar.render(g, viewport);
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

	@Override
	public void updateHover(int x, int y, boolean hover, boolean shiftDown, boolean ctrlDown) {
		super.updateHover(x, y, hover, shiftDown, ctrlDown);
		scrollbar.updateHover(x, y, (x > width - ScrollBar.WIDTH), shiftDown, ctrlDown);
		int yo = (int) ((height - wrapped.getHeight()) * scrollbar.getValue());
		wrapped.updateHover(x, y - yo, hover, shiftDown, ctrlDown);
	}

	public ScrollBar getScrollbar() {
		return scrollbar;
	}

}
