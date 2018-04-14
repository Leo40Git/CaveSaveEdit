package com.leo.cse.frontend.ui.dialogs;

import com.leo.cse.frontend.ui.components.special.PlusSlots;

public class PlusSlotDialog extends BaseDialog {

	public PlusSlotDialog(boolean closeButton) {
		super("Select file", 536, 398, closeButton);
		addComponent(new PlusSlots(this));
	}

}
