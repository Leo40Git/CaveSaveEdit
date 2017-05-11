package com.leo.cse.frontend.components;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.frontend.FrontUtils;

public class RadioBoxes extends Component {

	private int number;
	private Supplier<Integer> sSup;
	private Function<Integer, Integer> update;
	private List<RadioBox> radioBoxes;

	public RadioBoxes(int x, int y, int width, int number, Supplier<Integer> sSup, Function<Integer, Integer> update) {
		super(x, y, width, 8);
		this.number = number;
		this.sSup = sSup;
		this.update = update;
		radioBoxes = new ArrayList<RadioBox>();
		for (int i = 0; i < number; i++)
			radioBoxes.add(new RadioBox(x + i * (this.width / number), y, this, i));
	}

	public boolean isSelected(int id) {
		if (id < 0 || id > number)
			return false;
		return sSup.get() == id;
	}

	public void setSelected(int id) {
		update.apply(id);
	}

	@Override
	public void render(Graphics g) {
		for (RadioBox comp : radioBoxes)
			comp.render(g);
	}

	@Override
	public void onClick(int x, int y) {
		for (RadioBox comp : radioBoxes) {
			final int rx = comp.getX(), ry = comp.getY(), rw = comp.getWidth(), rh = comp.getHeight();
			if (FrontUtils.pointInRectangle(x, y, rx, ry, rw, rh)) {
				comp.onClick(x, y);
			}
		}
	}

}
