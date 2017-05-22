package com.leo.cse.frontend.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.JOptionPane;

import com.leo.cse.frontend.Defines;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;

public class DefineBox extends IntegerBox {

	private String type;

	public DefineBox(int x, int y, int width, int height, Supplier<Integer> vSup, Function<Integer, Integer> update,
			String type, String description) {
		super(x, y, width, height, vSup, update, description);
		this.type = type;
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(x, y, width, height);
		g.setColor(Color.black);
		g.drawRect(x, y, width, height);
		FrontUtils.drawString(g, Defines.get(type, vSup.get()), x + 3, y);
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		String nVal = (String) JOptionPane.showInputDialog(Main.window, "", "Select " + description,
				JOptionPane.PLAIN_MESSAGE, null, Defines.getAll(type).values().toArray(new String[] {}),
				Defines.get(type, vSup.get()));
		if (nVal == null)
			return;
		int i = Defines.getId(type, nVal);
		if (i == -1) {
			JOptionPane.showMessageDialog(Main.window, "Value \"" + nVal + "\" is unknown!", "Unknown value",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		update.apply(i);
	}

}
