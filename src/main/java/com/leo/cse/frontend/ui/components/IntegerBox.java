package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.JOptionPane;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class IntegerBox extends InputBox {

	protected Supplier<Integer> vSup;
	protected Function<Integer, Integer> update;
	protected String description;
	protected int padLength;

	public IntegerBox(int x, int y, int width, int height, Supplier<Integer> vSup, Function<Integer, Integer> update,
			String description, int padLength) {
		super(description, x, y, width, height);
		this.vSup = vSup;
		this.update = update;
		this.description = description;
		this.padLength = padLength;
	}

	public IntegerBox(int x, int y, int width, int height, Supplier<Integer> vSup, Function<Integer, Integer> update,
			String description) {
		this(x, y, width, height, vSup, update, description, -1);
	}

	@Override
	public void render(Graphics g, Rectangle viewport) {
		super.render(g, viewport);
		String str = Integer.toUnsignedString(vSup.get());
		if (padLength > 0)
			str = FrontUtils.padLeft(str, "0", padLength);
		g.setFont(Resources.font);
		FrontUtils.drawString(g, str, x + 3, y - 1, !enabled.get());
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		if (!enabled.get())
			return;
		String nVal = JOptionPane.showInputDialog(Main.window, "Enter new value for " + description + ":",
				Integer.toUnsignedString(vSup.get()));
		if (nVal == null)
			return;
		try {
			update.apply(Integer.parseUnsignedInt(nVal));
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(Main.window, "Input \"" + nVal + "\" was not a valid number!",
					"Error while parsing input!", JOptionPane.ERROR_MESSAGE);
		}
	}

}
