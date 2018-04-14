package com.leo.cse.frontend.ui.dialogs;

import com.leo.cse.backend.profile.NormalProfile;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.frontend.ui.SaveEditorPanel;
import com.leo.cse.frontend.ui.components.box.BooleanBox;
import com.leo.cse.frontend.ui.components.box.MapBox;
import com.leo.cse.frontend.ui.components.list.MapFlagList;
import com.leo.cse.frontend.ui.components.visual.Label;

public class MapFlagDialog extends BaseDialog {

	private int flag;

	public MapFlagDialog() {
		super("Set map flag...", 342, 24);
		addComponent(new Label("Map ID:", 4, 2));
		addComponent(new MapBox(44, 4, 240, 16, () -> flag, t -> {
			t = Math.max(0, Math.min(127, t));
			flag = t;
			return t;
		}, () -> SaveEditorPanel.sortMapsAlphabetically));
		addComponent(new Label("State:", 292, 2));
		BooleanBox flagBox = new BooleanBox("", false, 322, 4, () -> {
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
	}

}
