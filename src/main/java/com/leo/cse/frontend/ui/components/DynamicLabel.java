package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;
import java.util.function.Supplier;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class DynamicLabel extends Component {
	
	protected Supplier<String> textSup;

	public DynamicLabel(Supplier<String> textSup, int x, int y) {
		super(x, y, 0, 0);
		this.textSup = textSup;
	}
	
	@Override
	public void render(Graphics g) {
		g.setColor(Main.lineColor);
		g.setFont(Resources.font);
		FrontUtils.drawString(g, textSup.get(), x, y);
	}

}
