package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;

import com.leo.cse.frontend.FrontUtils;

public class Label extends Component {

	private String text;

	public Label(String text, int x, int y) {
		super(x, y, 0, 0);
		this.text = text;
	}

	@Override
	public void render(Graphics g) {
		FrontUtils.drawString(g, text, x, y);
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
	}

}
