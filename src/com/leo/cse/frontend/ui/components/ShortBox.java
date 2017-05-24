package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.JOptionPane;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;

public class ShortBox extends Component {

	private Supplier<Short> vSup;
	private Function<Short, Short> update;
	private String description;

	public ShortBox(int x, int y, int width, int height, Supplier<Short> vSup, Function<Short, Short> update,
			String description) {
		super(x, y, width, height);
		this.vSup = vSup;
		this.update = update;
		this.description = description;
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Main.COLOR_BG);
		g.fillRect(x, y, width, height);
		g.setColor(Main.lineColor);
		g.drawRect(x, y, width, height);
		FrontUtils.drawString(g, Short.toString(vSup.get()), x + 3, y);
	}

	@Override
	public boolean onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		String nVal = JOptionPane.showInputDialog(Main.window, "Enter new value for " + description + ":", vSup.get());
		if (nVal == null)
			return true;
		try {
			update.apply(Short.parseShort(nVal));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(Main.window, "Input was not a valid number!", "Error while parsing input!",
					JOptionPane.ERROR_MESSAGE);
		}
		return true;
	}

}
