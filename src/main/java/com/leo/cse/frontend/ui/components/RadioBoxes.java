package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.frontend.FrontUtils;

public class RadioBoxes extends Component {
	
	@FunctionalInterface
	public interface RadioBoxEnabledCheck {
		public boolean check(int index);
	}

	private int number;
	private String[] labels;
	private Supplier<Integer> sSup;
	private Function<Integer, Integer> update;
	private List<RadioBox> radioBoxes;

	public RadioBoxes(int x, int y, int width, int number, String[] labels, Supplier<Integer> sSup,
			Function<Integer, Integer> update, boolean small, RadioBoxEnabledCheck check) {
		super(x, y, width, (small ? 8 : 16));
		this.number = number;
		this.labels = labels;
		this.sSup = sSup;
		this.update = update;
		radioBoxes = new ArrayList<RadioBox>();
		for (int i = 0; i < number; i++)
			radioBoxes.add(new RadioBox(this.labels[i], x + i * (this.width / number), y, this, i, small, check));
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
	public boolean onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		for (RadioBox comp : radioBoxes) {
			final int rx = comp.getX(), ry = comp.getY(), rw = comp.getWidth(), rh = comp.getHeight();
			if (FrontUtils.pointInRectangle(x, y, rx, ry, rw, rh)) {
				comp.onClick(x, y, shiftDown, ctrlDown);
			}
		}
		return false;
	}

}
