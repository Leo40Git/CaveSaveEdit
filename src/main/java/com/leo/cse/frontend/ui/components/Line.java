package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;
import java.awt.Rectangle;

import com.leo.cse.frontend.Main;

public class Line extends Component {

	public Line(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	@Override
	public void render(Graphics g, Rectangle viewport) {
		g.setColor(Main.lineColor);
		g.drawLine(x, y, x + width, y + height);
	}

}
