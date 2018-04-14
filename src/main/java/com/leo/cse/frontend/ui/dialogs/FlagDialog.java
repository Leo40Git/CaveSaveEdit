package com.leo.cse.frontend.ui.dialogs;

import com.leo.cse.backend.profile.NormalProfile;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.frontend.ui.components.box.BooleanBox;
import com.leo.cse.frontend.ui.components.box.ShortBox;
import com.leo.cse.frontend.ui.components.list.FlagList;
import com.leo.cse.frontend.ui.components.visual.Label;

public class FlagDialog extends BaseDialog {

	private short flag;

	public FlagDialog() {
		super("Set flag...", 720, 58);
		addComponent(new Label("Flag ID:", 4, 2));
		addComponent(new ShortBox(44, 4, 28, 16, () -> flag, t -> {
			t = (short) Math.max(0, Math.min(7999, t));
			flag = t;
			return t;
		}, "flag ID", 4));
		addComponent(new Label("State:", 76, 2));
		BooleanBox flagBox = new BooleanBox("", false, 106, 4, () -> {
			return (boolean) ProfileManager.getField(NormalProfile.FIELD_FLAGS, flag);
		}, (Boolean newVal) -> {
			if (FlagList.isFlagValid(flag)) {
				ProfileManager.setField(NormalProfile.FIELD_FLAGS, flag, newVal);
				return newVal;
			}
			return false;
		});
		flagBox.setEnabled(() -> {
			return FlagList.isFlagValid(flag);
		});
		addComponent(flagBox);
		addComponent(new Label(() -> {
			return "Description:\n" + FlagList.getFlagDesc(flag);
		}, 4, 18));
	}

}
