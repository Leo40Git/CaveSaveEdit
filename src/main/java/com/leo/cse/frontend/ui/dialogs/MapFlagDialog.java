package com.leo.cse.frontend.ui.dialogs;

import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.backend.profile.NormalProfile;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.frontend.ui.components.BooleanBox;
import com.leo.cse.frontend.ui.components.Label;
import com.leo.cse.frontend.ui.components.MapFlagList;
import com.leo.cse.frontend.ui.components.ShortBox;

public class MapFlagDialog extends BaseDialog {

	private short flag;

	public MapFlagDialog() {
		super("Set map flag...", 720, 38);
		addComponent(new Label("Flag ID:", 4, 2));
		addComponent(new ShortBox(44, 4, 24, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				return flag;
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				t = (short) Math.max(0, Math.min(127, t));
				flag = t;
				return t;
			}
		}, "flag ID", 3));
		addComponent(new Label("State:", 76, 2));
		BooleanBox flagBox = new BooleanBox("", false, 106, 4, () -> {
			return (boolean) ProfileManager.getField(NormalProfile.FIELD_MAP_FLAGS, flag);
		}, (Boolean newVal) -> {
			if (MapFlagList.isMapFlagValid(flag)) {
				ProfileManager.setField(NormalProfile.FIELD_MAP_FLAGS, flag, newVal);
				return newVal;
			}
			return false;
		});
		flagBox.setEnabled(() -> {
			return MapFlagList.isMapFlagValid(flag);
		});
		addComponent(flagBox);
		addComponent(new Label(() -> {
			return "For map: " + MapFlagList.getMapFlagName(flag);
		}, 4, 18));
	}

}
