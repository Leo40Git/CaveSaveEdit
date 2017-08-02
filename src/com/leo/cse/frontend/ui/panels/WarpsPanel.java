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
			final int i2 = i, yy = 148 + i * 50;
			final int xx = 246;
			compList.add(new Label("Warp Slot " + (i + 1) + ":", xx, yy + 17));
			compList.add(new WarpBox(xx + 64, yy, 120, 48, i2));
			compList.add(new Label("Location:", xx + 188, yy + 17));
			compList.add(new DefineBox(xx + 238, yy + 17, 120, 16, new Supplier<Integer>() {
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
			}, "WarpLoc", "warp " + (i + 1) + " location", new Supplier<Boolean>() {
				@Override
				public Boolean get() {
					// return Profile.getWarp(i2).getId() != 0;
					return true;
				}
			}));
		}
	}

}
