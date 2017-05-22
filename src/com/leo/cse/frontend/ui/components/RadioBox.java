package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Resources;

class RadioBox extends Component {

	private String label;
	private RadioBoxes parent;
	private int id;

	public RadioBox(String label, int x, int y, RadioBoxes parent, int id) {
		super(x, y, 8, 8);
		this.label = label;
		this.parent = parent;
		this.id = id;
	}

	@Override
	public void render(Graphics g) {
		g.drawImage((parent.isSelected(id) ? Resources.radioOn : Resources.radioOff), x, y, null);
		FrontUtils.drawString(g, label, x + 10, y - 6);
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		parent.setSelected(id);
	}

}
