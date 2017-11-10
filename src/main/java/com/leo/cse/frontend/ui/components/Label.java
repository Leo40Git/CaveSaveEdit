package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class Label extends Component {

	private String text;
	private boolean center;

	public Label(String text, int x, int y, boolean center) {
		super(x, y, 0, 0);
		this.text = text;
		this.center = center;
	}

	public Label(String text, int x, int y) {
		this(text, x, y, false);
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Main.lineColor);
		g.setFont(Resources.font);
		if (center)
			FrontUtils.drawStringCentered(g, text, x, y, false, false);
		else
			FrontUtils.drawString(g, text, x, y);
	}

}
