package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.JOptionPane;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;

public class DefineBox extends IntegerBox {

	protected Supplier<Boolean> sSup;
	protected String type;

	public DefineBox(int x, int y, int width, int height, Supplier<Integer> vSup, Function<Integer, Integer> update,
			Supplier<Boolean> sSup, String type, String description) {
		super(x, y, width, height, vSup, update, description);
		this.sSup = sSup;
		this.type = type;
	}

	public DefineBox(int x, int y, int width, int height, Supplier<Integer> vSup, Function<Integer, Integer> update,
			String type, String description) {
		this(x, y, width, height, vSup, update, Main.FALSE_SUPPLIER, type, description);
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Main.COLOR_BG);
		g.fillRect(x, y, width, height);
		g.setColor(Main.lineColor);
		g.drawRect(x, y, width, height);
		FrontUtils.drawString(g, vSup.get() + " - " + MCI.get(type, vSup.get()), x + 3, y);
	}

	@Override
	public boolean onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		Map<Integer, String> map = MCI.getAll(type);
		if (sSup.get())
			map = FrontUtils.sortMapByValue(map);
		for (Map.Entry<Integer, String> entry : map.entrySet())
			entry.setValue(entry.getKey() + " - " + entry.getValue());
		String nVal = (String) JOptionPane.showInputDialog(Main.window, "", "Select " + description,
				JOptionPane.PLAIN_MESSAGE, null, map.values().toArray(new String[] {}),
				vSup.get() + " - " + MCI.get(type, vSup.get()));
		if (nVal == null)
			return true;
		nVal = nVal.substring(nVal.indexOf('-') + 2);
		int i = MCI.getId(type, nVal);
		if (i == -1) {
			JOptionPane.showMessageDialog(Main.window, "Value \"" + nVal + "\" is unknown!", "Unknown value",
					JOptionPane.ERROR_MESSAGE);
		}
		update.apply(i);
		return true;
	}

}
