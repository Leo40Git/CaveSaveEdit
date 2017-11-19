package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.function.Supplier;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class Label extends Component {

	private String text;
	private Supplier<String> textSup;
	private boolean center;

	public Label(String text, int x, int y, boolean center) {
		super(text, x, y, 0, 0);
		this.text = text;
		this.center = center;
	}
	
	public Label(Supplier<String> textSup, int x, int y, boolean center) {
		super(textSup.get(), x, y, 0, 0);
		this.textSup = textSup;
		this.center = center;
	}

	public Label(String text, int x, int y) {
		this(text, x, y, false);
	}
	
	public Label(Supplier<String> textSup, int x, int y) {
		this(textSup, x, y, false);
	}

	@Override
	public void render(Graphics g, Rectangle viewport) {
		if (textSup != null)
			text = textSup.get();
		g.setColor(Main.lineColor);
		g.setFont(Resources.font);
		if (center)
			FrontUtils.drawStringCentered(g, text, x, y, false, false);
		else
			FrontUtils.drawString(g, text, x, y);
	}

}
