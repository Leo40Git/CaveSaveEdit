package com.leo.cse.frontend.components;

import java.awt.Graphics;
import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.frontend.Defines;
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
		if (t.contains("."))
			t = Defines.get(t);
		if (t == null) {
			disabled = true;
			return;
		} else
			disabled = false;
		g.drawImage((vSup.get() ? Resources.checkboxOff : Resources.checkboxOn), x, y, null);
		FrontUtils.drawString(g, t, x + 17, y - 3);
	}

	@Override
	public void onClick(int x, int y) {
		if (disabled)
			return;
		update.apply(!vSup.get());
	}

}
