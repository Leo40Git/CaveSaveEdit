package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;
import java.util.function.Supplier;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class Button extends Component {

	private String label;
	private Supplier<Boolean> onClick;

	public Button(String label, int x, int y, int width, int height, Supplier<Boolean> onClick) {
		super(x, y, width, height);
		this.label = label;
		this.onClick = onClick;
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Main.COLOR_BG);
		g.fillRect(x, y, width, height);
		g.setColor(Main.lineColor);
		g.drawRect(x, y, width, height);
		g.setFont(Resources.font);
		FrontUtils.drawStringCentered(g, label, x + width / 2, y);
	}

	@Override
	public boolean onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		return onClick.get();
	}

}
