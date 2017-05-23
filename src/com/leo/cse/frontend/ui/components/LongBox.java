package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.JOptionPane;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;

public class LongBox extends Component {

	private Supplier<Long> vSup;
	private Function<Long, Long> update;
	private String description;

	public LongBox(int x, int y, int width, int height, Supplier<Long> vSup, Function<Long, Long> update,
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
		g.setColor(Main.customColor);
		g.drawRect(x, y, width, height);
		FrontUtils.drawString(g, Long.toString(vSup.get()), x + 3, y);
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		String nVal = JOptionPane.showInputDialog(Main.window, "Enter new value for " + description + ":",
				Long.toUnsignedString(vSup.get()));
		if (nVal == null)
			return;
		try {
			update.apply(Long.parseUnsignedLong(nVal));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(Main.window, "Input was not a valid number!", "Error while parsing input!",
					JOptionPane.ERROR_MESSAGE);
		}
	}

}
