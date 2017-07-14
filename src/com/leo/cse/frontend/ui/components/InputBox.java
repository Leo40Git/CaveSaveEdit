package com.leo.cse.frontend.ui.components;

import java.awt.Color;
import java.awt.Graphics;

import com.leo.cse.frontend.Main;

public abstract class InputBox extends Component {

	public InputBox(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Main.COLOR_BG);
		g.fillRect(x, y, width, height - 1);
		g.setColor(Main.lineColor);
		g.drawRect(x, y, width, height - 1);
		if (!enabled.get()) {
			Color lc2 = new Color(Main.lineColor.getRed(), Main.lineColor.getGreen(), Main.lineColor.getBlue(), 31);
			g.setColor(lc2);
			g.fillRect(x, y, width, height - 1);
		}
	}

}
