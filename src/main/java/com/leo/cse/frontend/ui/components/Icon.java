package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Icon extends Component {

	protected Image image;

	public Icon(BufferedImage image, int x, int y) {
		super(image.toString(), x, y, 0, 0);
		this.image = image;
	}

	@Override
	public int getWidth() {
		if (image == null)
			return 0;
		return image.getWidth(null);
	}

	@Override
	public int getHeight() {
		if (image == null)
			return 0;
		return image.getHeight(null);
	}

	@Override
	public void render(Graphics g, Rectangle viewport) {
		g.drawImage(image, x, y, null);
	}

}
