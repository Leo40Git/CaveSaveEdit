package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class Button extends InputBox {

	private String label;
	private BufferedImage icon;
	private Runnable onClick;

	public Button(String label, BufferedImage icon, int x, int y, int width, int height, Runnable onClick) {
		super(x, y, width, height);
		this.label = label;
		this.icon = icon;
		this.onClick = onClick;
	}

	public Button(String label, int x, int y, int width, int height, Runnable onClick) {
		this(label, null, x, y, width, height, onClick);
	}

	@Override
	public void render(Graphics g) {
		super.render(g);
		g.drawImage(icon, x + 1, y + 1, null);
		g.setColor(Main.lineColor);
		g.setFont(Resources.font);
		FrontUtils.drawStringCentered(g, label, x + width / 2, y - (20 - height) / 2, !enabled.get());
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		if (!enabled.get())
			return;
		onClick.run();
	}

}
