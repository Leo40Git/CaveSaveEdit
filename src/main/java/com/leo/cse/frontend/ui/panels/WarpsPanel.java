package com.leo.cse.frontend.ui.panels;

import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.backend.profile.NormalProfile;
import com.leo.cse.backend.profile.Profile.ProfileFieldException;
import com.leo.cse.backend.profile.ProfileManager;
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
					try {
						return (Integer) ProfileManager.getField(NormalProfile.FIELD_WARP_LOCATION);
					} catch (ProfileFieldException e) {
						e.printStackTrace();
					}
					return 0;
				}
			}, new Function<Integer, Integer>() {
				@Override
				public Integer apply(Integer t) {
					try {
						ProfileManager.setField(NormalProfile.FIELD_WARP_LOCATION, t);
						return t;
					} catch (ProfileFieldException e) {
						e.printStackTrace();
					}
					return -1;
				}
			}, "WarpLoc", "warp " + (i + 1) + " location", new Supplier<Boolean>() {
				@Override
				public Boolean get() {
					return true;
				}
			}));
		}
	}

}
