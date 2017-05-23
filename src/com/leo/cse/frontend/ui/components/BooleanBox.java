package com.leo.cse.frontend.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Resources;

public class BooleanBox extends Component {

	private boolean disabled;
	private String label;
	private Supplier<Boolean> vSup;
	private Function<Boolean, Boolean> update;

	public BooleanBox(String label, int x, int y, Supplier<Boolean> vSup, Function<Boolean, Boolean> update) {
		super(x, y, 15, 15);
		this.label = label;
		this.vSup = vSup;
		this.update = update;
	}

	@Override
	public void render(Graphics g) {
		String t = label;
		if (t.contains(".")) {
			t = MCI.get(t);
			if (label.equals(t)) {
				disabled = true;
				return;
			} else
				disabled = false;
		}
		g.setColor(Color.black);
		g.setFont(Resources.font);
		g.drawImage((vSup.get() ? Resources.checkboxOn : Resources.checkboxOff), x, y, null);
		FrontUtils.drawString(g, t, x + 18, y - 3);
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		if (disabled)
			return;
		update.apply(!vSup.get());
	}

}
