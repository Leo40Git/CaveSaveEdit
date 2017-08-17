package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;
import java.util.function.Function;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

class RadioBox extends Component {

	private String label;
	private RadioBoxes parent;
	private int id;
	private boolean small;
	private Function<Integer, Boolean> check;

	public RadioBox(String label, int x, int y, RadioBoxes parent, int id, boolean small, Function<Integer, Boolean> check) {
		super(x, y, (small ? 8 : 16), (small ? 8 : 16));
		this.label = label;
		this.parent = parent;
		this.id = id;
		this.small = small;
		this.check = check;
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Main.lineColor);
		g.setFont(Resources.font);
		boolean enabled = check.apply(id);
		if (small) {
			if (enabled)
				g.drawImage((parent.isSelected(id) ? Resources.radioOnS : Resources.radioOffS), x, y, null);
			else
				g.drawImage(Resources.radioDisabledS, x, y, null);
			FrontUtils.drawString(g, label, x + 10, y - 6);
		} else {
			if (enabled)
				g.drawImage((parent.isSelected(id) ? Resources.radioOn : Resources.radioOff), x, y, null);
			else
				g.drawImage(Resources.radioDisabled, x, y, null);
			FrontUtils.drawString(g, label, x + 18, y - 2);
		}
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		if (!check.apply(id))
			return;
		parent.setSelected(id);
	}

}
