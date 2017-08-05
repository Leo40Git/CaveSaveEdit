package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class BooleanBox extends Component {

	private boolean missingMCI;
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
				missingMCI = true;
				return;
			} else
				missingMCI = false;
		}
		g.setColor(Main.lineColor);
		g.setFont(Resources.font);
		BufferedImage chkImage = (vSup.get() ? Resources.checkboxOn : Resources.checkboxOff);
		if (!enabled.get() || missingMCI)
			chkImage = Resources.checkboxDisabled;
		g.drawImage(chkImage, x, y, null);
		FrontUtils.drawString(g, t, x + 18, y - 2);
	}

	@Override
	public boolean onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		if (!enabled.get() || missingMCI)
			return false;
		update.apply(!vSup.get());
		return false;
	}

}