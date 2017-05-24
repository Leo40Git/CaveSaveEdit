package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class Label extends Component {

	private String text;

	public Label(String text, int x, int y) {
		super(x, y, 0, 0);
		this.text = text;
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Main.lineColor);
		g.setFont(Resources.font);
		FrontUtils.drawString(g, text, x, y);
	}

	@Override
	public boolean onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		return false;
	}

}
