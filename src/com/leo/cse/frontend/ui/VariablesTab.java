package com.leo.cse.frontend.ui;

import java.awt.Dimension;
import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.backend.Profile;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.ui.components.BooleanBox;
import com.leo.cse.frontend.ui.components.Label;
import com.leo.cse.frontend.ui.components.ShortBox;

public class VariablesTab extends EditorTab {

	public VariablesTab() {
		super("Variables");
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
						24 + i * 16));
				compList.add(new ShortBox(j * width + 40, 24 + i * 16, width - 44, 16, new Supplier<Short>() {
					@Override
					public Short get() {
						return Profile.getVariable(vi2);
					}
				}, new Function<Short, Short>() {
					@Override
					public Short apply(Short t) {
						Profile.setVariable(vi2, t);
						return t;
					}
				}, "variable " + varId));
				varId++;
			}
		}
		if (MCI.getSpecial("PhysVarHack")) {
			final String[] pvl = { "Max Walk Speed", "Max Fall Speed", "Gravity", "Alt Gravity", "Walk Accel",
					"Jump Control", "Friction", "Jump Force" };
			compList.add(new Label("Physics Variables:", 4, 264));
			varId = 0;
			int label = 0;
			boolean labelWater = false;
			for (int i = 0; i < 4; i += 2) {
				for (int j = 0; j < 8; j++) {
					if (varId > 15)
						break;
					final int vi2 = varId;
					compList.add(new Label(pvl[label] + (labelWater ? " (W)" : "") + ":", j * width + 2, 284 + i * 16));
					compList.add(new ShortBox(j * width + 2, 300 + i * 16, width - 6, 16, new Supplier<Short>() {
						@Override
						public Short get() {
							return Profile.getPhysVariable(vi2);
						}
					}, new Function<Short, Short>() {
						@Override
						public Short apply(Short t) {
							Profile.setPhysVariable(vi2, t);
							return t;
						}
					}, (labelWater ? "water " : "") + pvl[label].toLowerCase()));
					varId++;
					label++;
					if (label > 7) {
						label = 0;
						labelWater = true;
					}
				}
			}
			compList.add(new Label("(W) - Water physics variable", 4, 350));
			compList.add(
					new BooleanBox("Water doesn't cause splash and trigger air timer", 4, 374, new Supplier<Boolean>() {
						@Override
						public Boolean get() {
							return (Profile.getPhysVariable(16) == 1 ? true : false);
						}
					}, new Function<Boolean, Boolean>() {
						@Override
						public Boolean apply(Boolean t) {
							Profile.setPhysVariable(16, (short) (t ? 1 : 0));
							return t;
						}
					}));
		}
	}

}
