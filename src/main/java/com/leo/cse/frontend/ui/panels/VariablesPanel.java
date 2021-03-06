package com.leo.cse.frontend.ui.panels;

import java.awt.Dimension;

import com.leo.cse.backend.profile.NormalProfile;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.ui.components.box.BooleanBox;
import com.leo.cse.frontend.ui.components.box.ShortBox;
import com.leo.cse.frontend.ui.components.visual.Label;

public class VariablesPanel extends Panel {

	public VariablesPanel() {
		super();
		final Dimension winSize = Main.WINDOW_SIZE;
		compList.add(new Label("Variables:", 4, 4));
		final int width = winSize.width / 8;
		int varId = 0;
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 8; j++) {
				if (varId < 8 && varId != 6) {
					varId++;
					j--;
					continue;
				}
				if (varId > 123)
					break;
				final int vi2 = varId + 1;
				compList.add(new Label("V" + FrontUtils.padLeft(Integer.toString(varId), "0", 3) + ":", j * width + 2,
						22 + i * 17));
				compList.add(new ShortBox(j * width + 40, 24 + i * 17, width - 44, 16, () -> {
					return (Short) ProfileManager.getField(NormalProfile.FIELD_VARIABLES, vi2);
				}, t -> {
					ProfileManager.setField(NormalProfile.FIELD_VARIABLES, vi2, t);
					return t;
				}, "variable " + varId));
				varId++;
			}
		}
		if (!MCI.getSpecial("PhysVarHack"))
			return;
		final String[] pvl = { "Max Walk Speed", "Max Fall Speed", "Gravity", "Alt Gravity", "Walk Accel",
				"Jump Control", "Friction", "Jump Force" };
		compList.add(new Label("Physics Variables:", 4, 284));
		varId = 0;
		int label = 0;
		boolean labelWater = false;
		for (int i = 0; i < 4; i += 2) {
			for (int j = 0; j < 8; j++) {
				if (varId > 15)
					break;
				final int vi2 = varId;
				compList.add(new Label(pvl[label] + (labelWater ? " (W):" : ":"), j * width + 2, 300 + i * 16));
				compList.add(new ShortBox(j * width + 2, 316 + i * 16, width - 6, 16, () -> {
					return (Short) ProfileManager.getField(NormalProfile.FIELD_PHYSICS_VARIABLES, vi2);
				}, t -> {
					ProfileManager.setField(NormalProfile.FIELD_PHYSICS_VARIABLES, vi2, t);
					return t;
				}, (labelWater ? "underwater " : "") + pvl[label].toLowerCase()));
				varId++;
				label++;
				if (label > 7) {
					label = 0;
					labelWater = true;
				}
			}
		}
		compList.add(new Label("(W) - Underwater physics variable", 4, 362));
		compList.add(new BooleanBox("Water doesn't cause splash and trigger air timer", false, 4, 386, () -> {
			Short val = (Short) ProfileManager.getField(NormalProfile.FIELD_PHYSICS_VARIABLES, 16);
			return (val == 1 ? true : false);
		}, t -> {
			ProfileManager.setField(NormalProfile.FIELD_PHYSICS_VARIABLES, 16, (t ? 1 : 0));
			return t;
		}));
	}

}
