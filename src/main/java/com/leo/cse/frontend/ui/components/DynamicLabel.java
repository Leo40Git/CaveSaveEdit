package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.function.Supplier;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class DynamicLabel extends Component {

	protected Supplier<String> textSup;
	private boolean center;

	public DynamicLabel(Supplier<String> textSup, int x, int y, boolean center) {
		super(x, y, 0, 0);
		this.textSup = textSup;
		this.center = center;
	}

	public DynamicLabel(Supplier<String> textSup, int x, int y) {
		this(textSup, x, y, false);
	}

	@Override
	public void render(Graphics g, Rectangle viewport) {
		g.setColor(Main.lineColor);
		g.setFont(Resources.font);
		String text = textSup.get();
		if (center)
			FrontUtils.drawStringCentered(g, text, x, y, false, false);
		else
			FrontUtils.drawString(g, text, x, y);
	}

}
