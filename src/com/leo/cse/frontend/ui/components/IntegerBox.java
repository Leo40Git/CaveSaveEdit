package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.JOptionPane;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;

public class IntegerBox extends Component {

	protected Supplier<Integer> vSup;
	protected Function<Integer, Integer> update;
	protected String description;

	public IntegerBox(int x, int y, int width, int height, Supplier<Integer> vSup, Function<Integer, Integer> update,
			String description) {
		super(x, y, width, height);
		this.vSup = vSup;
		this.update = update;
		this.description = description;
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Main.COLOR_BG);
		g.fillRect(x, y, width, height - 1);
		g.setColor(Main.lineColor);
		g.drawRect(x, y, width, height - 1);
		FrontUtils.drawString(g, Integer.toUnsignedString(vSup.get()), x + 3, y - 1);
	}

	@Override
	public boolean onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		String nVal = JOptionPane.showInputDialog(Main.window, "Enter new value for " + description + ":",
				Integer.toUnsignedString(vSup.get()));
		if (nVal == null)
			return true;
		try {
			update.apply(Integer.parseUnsignedInt(nVal));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(Main.window, "Input \"" + nVal + "\" was not a valid number!",
					"Error while parsing input!", JOptionPane.ERROR_MESSAGE);
		}
		return true;
	}

}
