package com.leo.cse.frontend.ui.dialogs;

import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.backend.profile.NormalProfile;
import com.leo.cse.backend.profile.Profile.ProfileFieldException;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.frontend.ui.components.BooleanBox;
import com.leo.cse.frontend.ui.components.DynamicLabel;
import com.leo.cse.frontend.ui.components.FlagList;
import com.leo.cse.frontend.ui.components.Label;
import com.leo.cse.frontend.ui.components.ShortBox;

public class FlagDialog extends BaseDialog {

	private short flag;

	public FlagDialog() {
		super("Set flag...", 720, 58);
		addComponent(new Label("Flag ID:", 4, 2));
		addComponent(new ShortBox(44, 4, 28, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				return flag;
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				t = (short) Math.max(0, Math.min(7999, t));
				flag = t;
				return t;
			}
		}, "flag ID", 4));
		addComponent(new Label("State:", 76, 2));
		BooleanBox flagBox = new BooleanBox("", 106, 4, () -> {
			try {
				return (boolean) ProfileManager.getField(NormalProfile.FIELD_FLAGS, flag);
			} catch (ProfileFieldException e) {
				e.printStackTrace();
			}
			return false;
		}, (Boolean newVal) -> {
			if (FlagList.isFlagValid(flag)) {
				try {
					ProfileManager.setField(NormalProfile.FIELD_FLAGS, flag, newVal);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return newVal;
			}
			return false;
		});
		flagBox.setEnabled(() -> {
			return FlagList.isFlagValid(flag);
		});
		addComponent(flagBox);
		addComponent(new DynamicLabel(() -> {
			return "Description:\n" + FlagList.getFlagDesc(flag);
		}, 4, 18));
	}

}
