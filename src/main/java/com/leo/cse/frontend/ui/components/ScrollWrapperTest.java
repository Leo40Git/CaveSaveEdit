package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;

import javax.swing.JOptionPane;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class ScrollWrapperTest extends Component {

	public ScrollWrapperTest(int x, int y, int width) {
		super(x, y, width, 8000);
	}

	@Override
	public void render(Graphics g) {
		g.setFont(Resources.font);
		g.setColor(Main.lineColor);
		FrontUtils.drawString(g, "hello there! (scroll down)", x + 4, y + 4);
		FrontUtils.drawString(g, "this is a test for the ScrollWrapper! (scroll down)", x + 4, y + 2004);
		FrontUtils.drawString(g, "button incoming! (scroll down)", x + 4, y + 4004);
		g.drawRect(x + 4, y + 6004, 40, 17);
		FrontUtils.drawString(g, "click me!", x + 6, y + 6004);
		FrontUtils.drawString(g, "hi :)", x + 4, y + 7982);
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		if (FrontUtils.pointInRectangle(x, y, this.x, this.y + 6004, 40, 17))
			JOptionPane.showMessageDialog(Main.window, "you did the click!", "ScrollWrapperTest says...",
					JOptionPane.INFORMATION_MESSAGE);
	}

}
