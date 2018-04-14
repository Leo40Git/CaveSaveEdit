package com.leo.cse.frontend.ui.components.visual;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.function.Supplier;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.components.Component;

public class Label extends Component {

	private String text;
	private Supplier<String> textSup;
	private boolean center;
	private int size, lastSize;
	private Font font;

	public static final int SIZE_SMALL = 0, SIZE_NORMAL = 1, SIZE_LARGE = 2;

	public Label(String text, int x, int y, boolean center, int size) {
		super(text, x, y, 0, 0);
		this.text = text;
		this.center = center;
		this.size = size;
	}

	public Label(Supplier<String> textSup, int x, int y, boolean center, int size) {
		super(textSup.get(), x, y, 0, 0);
		this.textSup = textSup;
		this.center = center;
		this.size = size;
	}

	public Label(String text, int x, int y, boolean center) {
		this(text, x, y, center, SIZE_NORMAL);
	}

	public Label(Supplier<String> textSup, int x, int y, boolean center) {
		this(textSup, x, y, center, SIZE_NORMAL);
	}

	public Label(String text, int x, int y) {
		this(text, x, y, false);
	}

	public Label(Supplier<String> textSup, int x, int y) {
		this(textSup, x, y, false);
	}

	private void updateFont() {
		if (lastSize != size) {
			Font newFont = font;
			switch (size) {
			case SIZE_SMALL:
				newFont = Resources.fontS;
				break;
			case SIZE_NORMAL:
				newFont = Resources.font;
				break;
			case SIZE_LARGE:
				newFont = Resources.fontL;
				break;
			default:
				break;
			}
			font = newFont;
			lastSize = size;
		}
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		lastSize = this.size;
		this.size = size;
		updateFont();
	}

	@Override
	public void render(Graphics g, Rectangle viewport) {
		if (textSup != null)
			text = textSup.get();
		g.setColor(Main.lineColor);
		updateFont();
		g.setFont(font);
		if (center)
			FrontUtils.drawStringCentered(g, text, x, y, false, false);
		else
			FrontUtils.drawString(g, text, x, y);
	}

}
