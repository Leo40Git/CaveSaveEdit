package com.leo.cse.frontend.ui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JOptionPane;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class DefineBox extends IntegerBox {

	protected Supplier<Boolean> sSup;
	protected Function<Integer, BufferedImage> iSup;
	protected String type;

	public DefineBox(int x, int y, int width, int height, Supplier<Integer> vSup, Function<Integer, Integer> update,
			Supplier<Boolean> sSup, Function<Integer, BufferedImage> iSup, String type, String description,
			Supplier<Boolean> enabled) {
		super(x, y, width, height, vSup, update, description);
		this.sSup = sSup;
		this.iSup = iSup;
		this.type = type;
		this.enabled = enabled;
	}

	public DefineBox(int x, int y, int width, int height, Supplier<Integer> vSup, Function<Integer, Integer> update,
			Supplier<Boolean> sSup, String type, String description, Supplier<Boolean> enabled) {
		this(x, y, width, height, vSup, update, sSup, null, type, description, enabled);
	}

	public DefineBox(int x, int y, int width, int height, Supplier<Integer> vSup, Function<Integer, Integer> update,
			Supplier<Boolean> sSup, String type, String description) {
		this(x, y, width, height, vSup, update, sSup, type, description, Main.TRUE_SUPPLIER);
	}

	public DefineBox(int x, int y, int width, int height, Supplier<Integer> vSup, Function<Integer, Integer> update,
			String type, String description, Supplier<Boolean> enabled) {
		this(x, y, width, height, vSup, update, Main.FALSE_SUPPLIER, type, description, enabled);
	}

	public DefineBox(int x, int y, int width, int height, Supplier<Integer> vSup, Function<Integer, Integer> update,
			String type, String description) {
		this(x, y, width, height, vSup, update, Main.FALSE_SUPPLIER, type, description);
	}

	public void setImageSupplier(Function<Integer, BufferedImage> iSup) {
		this.iSup = iSup;
	}

	@Override
	public void render(Graphics g, Rectangle viewport) {
		boolean bEnabled = enabled.get();
		if (hover && bEnabled)
			g.setColor(new Color(Main.lineColor.getRed(), Main.lineColor.getGreen(), Main.lineColor.getBlue(), 31));
		else
			g.setColor(Main.COLOR_BG);
		g.fillRect(x, y, width, height - 1);
		g.setColor(Main.lineColor);
		g.drawRect(x, y, width, height - 1);
		if (!bEnabled) {
			Color lc2 = new Color(Main.lineColor.getRed(), Main.lineColor.getGreen(), Main.lineColor.getBlue(), 31);
			g.setColor(lc2);
			FrontUtils.drawCheckeredGrid(g, x + 1, y + 1, width - 1, height - 2);
		}
		g.setColor(Main.lineColor);
		FrontUtils.drawString(g, vSup.get() + " - " + MCI.get(type, vSup.get()), x + 3, y - 1, !bEnabled);
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		if (!enabled.get())
			return;
		Map<Integer, String> map = MCI.getAll(type);
		if (sSup.get())
			map = FrontUtils.sortMapByValue(map);
		for (Map.Entry<Integer, String> entry : map.entrySet())
			entry.setValue(entry.getKey() + " - " + entry.getValue());
		DefaultListCellRenderer listRender = new DefaultListCellRenderer();
		if (iSup != null)
			listRender = new DefineListCellRenderer(type, iSup);
		String nVal = FrontUtils.showSelectionDialog(Main.window, "Select " + description, map.values(),
				vSup.get() + " - " + MCI.get(type, vSup.get()), listRender);
		if (nVal == null)
			return;
		nVal = nVal.substring(nVal.indexOf('-') + 2);
		int i = MCI.getId(type, nVal);
		if (i == -1) {
			JOptionPane.showMessageDialog(Main.window, "Value \"" + nVal + "\" is unknown!", "Unknown value",
					JOptionPane.ERROR_MESSAGE);
		}
		update.apply(i);
	}

	static class DefineListCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1L;

		private String type;
		private Function<Integer, BufferedImage> iSup;

		public DefineListCellRenderer(String type, Function<Integer, BufferedImage> iSup) {
			this.type = type;
			this.iSup = iSup;
		}

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			if (!(value instanceof String))
				throw new RuntimeException();
			System.out.println("DefineListCellRenderer");
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			String nVal = (String) value;
			nVal = nVal.substring(nVal.indexOf('-') + 2);
			int i = MCI.getId(type, nVal);
			if (i != -1) {
				setIcon(Resources.createIconFromImage(iSup.apply(i)));
				if (isSelected)
					setBackground(Main.COLOR_BG_B2);
				else if (cellHasFocus)
					setBackground(Main.COLOR_BG_B);
				else
					setBackground(Main.COLOR_BG);
				setForeground(Main.lineColor);
				setOpaque(true);
			}
			return this;
		}

	}

}
