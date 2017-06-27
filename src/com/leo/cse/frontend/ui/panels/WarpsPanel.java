package com.leo.cse.frontend.ui.panels;

import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.backend.profile.Profile;
import com.leo.cse.frontend.ui.components.DefineBox;
import com.leo.cse.frontend.ui.components.Label;
import com.leo.cse.frontend.ui.components.WarpBox;

public class WarpsPanel extends Panel {

	public WarpsPanel() {
		super();
		for (int i = 0; i < 7; i++) {
			final int i2 = i, yy = 4 + i * 50;
			compList.add(new Label("Warp Slot " + (i + 1) + ":", 4, yy + 17));
			compList.add(new WarpBox(68, yy, 120, 48, i2));
			compList.add(new Label("Location:", 192, yy + 17));
			compList.add(new DefineBox(242, yy + 17, 120, 16, new Supplier<Integer>() {
				@Override
				public Integer get() {
					return Profile.getWarp(i2).getLocation();
				}
			}, new Function<Integer, Integer>() {
				@Override
				public Integer apply(Integer t) {
					Profile.getWarp(i2).setLocation(t);
					return t;
				}
			}, "WarpLoc", "warp " + (i + 1) + "'s location"));
		}
	}

}
