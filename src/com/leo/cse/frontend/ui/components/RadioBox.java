package com.leo.cse.frontend.ui.components;

import java.awt.Color;
import java.awt.Graphics;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Resources;

class RadioBox extends Component {

	private String label;
	private RadioBoxes parent;
	private int id;
	private boolean small;

	public RadioBox(String label, int x, int y, RadioBoxes parent, int id, boolean small) {
		super(x, y, (small ? 8 : 16), (small ? 8 : 16));
		this.label = label;
		this.parent = parent;
		this.id = id;
		this.small = small;
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.black);
		g.setFont(Resources.font);
		if (small) {
			g.drawImage((parent.isSelected(id) ? Resources.radioOnS : Resources.radioOffS), x, y, null);
			FrontUtils.drawString(g, label, x + 10, y - 6);
		} else {
			g.drawImage((parent.isSelected(id) ? Resources.radioOn : Resources.radioOff), x, y, null);
			FrontUtils.drawString(g, label, x + 18, y - 2);
		}
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		parent.setSelected(id);
	}

}
