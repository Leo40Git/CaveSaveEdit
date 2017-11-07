package com.leo.cse.frontend.ui.components;

import java.awt.Color;
import java.awt.Graphics;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class Button extends InputBox {

	private String label;
	private Runnable onClick;

	public Button(String label, int x, int y, int width, int height, Runnable onClick) {
		super(x, y, width, height);
		this.label = label;
		this.onClick = onClick;
	}

	@Override
	public void render(Graphics g) {
		super.render(g);
		if (!enabled.get()) {
			Color lc2 = new Color(Main.lineColor.getRed(), Main.lineColor.getGreen(), Main.lineColor.getBlue(), 31);
			g.setColor(lc2);
			g.fillRect(x, y, width, height - 1);
		}
		g.setColor(Main.lineColor);
		g.setFont(Resources.font);
		FrontUtils.drawStringCentered(g, label, x + width / 2, y - (20 - height) / 2, false);
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		if (!enabled.get())
			return;
		onClick.run();
	}

}
