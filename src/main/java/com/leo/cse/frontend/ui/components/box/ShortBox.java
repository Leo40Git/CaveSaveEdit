package com.leo.cse.frontend.ui.components.box;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.JOptionPane;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class ShortBox extends InputBox {

	private Supplier<Short> vSup;
	private Function<Short, Short> update;
	private String description;
	private int padLength;

	public ShortBox(int x, int y, int width, int height, Supplier<Short> vSup, Function<Short, Short> update,
			String description, int padLength) {
		super(description, x, y, width, height);
		this.vSup = vSup;
		this.update = update;
		this.description = description;
		this.padLength = padLength;
	}

	public ShortBox(int x, int y, int width, int height, Supplier<Short> vSup, Function<Short, Short> update,
			String description) {
		this(x, y, width, height, vSup, update, description, -1);
	}

	@Override
	public void render(Graphics g, Rectangle viewport) {
		super.render(g, viewport);
		String str = Short.toString(vSup.get());
		if (padLength > 0)
			str = FrontUtils.padLeft(str, "0", padLength);
		g.setFont(Resources.font);
		FrontUtils.drawString(g, str, x + 3, y - 1, !enabled.get());
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		if (!enabled.get())
			return;
		String nVal = JOptionPane.showInputDialog(Main.window, "Enter new value for " + description + ":", vSup.get());
		if (nVal == null)
			return;
		try {
			update.apply(Short.parseShort(nVal));
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(Main.window, "Input was not a valid number!", "Error while parsing input!",
					JOptionPane.ERROR_MESSAGE);
		}
	}

}
