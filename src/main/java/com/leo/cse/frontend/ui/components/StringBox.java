package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.JOptionPane;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;

public class StringBox extends InputBox {
	
	protected Supplier<String> vSup;
	protected Function<String, String> update;
	protected String description;

	public StringBox(int x, int y, int width, int height, Supplier<String> vSup, Function<String, String> update, String description) {
		super(x, y, width, height);
		this.vSup = vSup;
		this.update = update;
		this.description = description;
	}
	
	@Override
	public void render(Graphics g, Rectangle viewport) {
		super.render(g, viewport);
		FrontUtils.drawString(g, vSup.get(), x + 3, y - 1, !enabled.get());
	}
	
	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		if (!enabled.get())
			return;
		String nVal = JOptionPane.showInputDialog(Main.window, "Enter new value for " + description + ":", vSup.get());
		if (nVal == null)
			return;
			update.apply(nVal);
	}

}
