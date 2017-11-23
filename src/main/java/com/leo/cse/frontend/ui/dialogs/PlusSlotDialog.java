package com.leo.cse.frontend.ui.dialogs;

import com.leo.cse.frontend.ui.components.PlusSlots;

public class PlusSlotDialog extends BaseDialog {

	public PlusSlotDialog() {
		super("Select file", 306, 284, false);
		addComponent(new PlusSlots(this));
	}

}
