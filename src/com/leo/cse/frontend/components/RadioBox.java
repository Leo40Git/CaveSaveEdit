package com.leo.cse.frontend.components;

import java.awt.Graphics;

import com.leo.cse.frontend.Resources;

class RadioBox extends Component {
	
	private RadioBoxes parent;
	private int id;

	public RadioBox(int x, int y, RadioBoxes parent, int id) {
		super(x, y, 8, 8);
		this.parent = parent;
		this.id = id;
	}

	@Override
	public void render(Graphics g) {
		g.drawImage((parent.isSelected(id) ? Resources.radioOn : Resources.radioOff), x, y, null);
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		parent.setSelected(id);
	}

}
