package com.leo.cse.frontend.ui.dialogs;

import java.util.function.Consumer;

import com.leo.cse.frontend.ui.components.box.Button;
import com.leo.cse.frontend.ui.components.visual.Label;

public class ConfirmDialog extends BaseDialog {

	public ConfirmDialog(String title, String message, Consumer<Boolean> callback) {
		super(title, 280, 42, false);
		addComponent(new Label(message, 140, 2, true));
		addComponent(new Button("Yes", 74, 22, 54, 16, () -> {
			callback.accept(true);
			requestClose();
		}));
		addComponent(new Button("No", 144, 22, 54, 16, () -> {
			callback.accept(false);
			requestClose();
		}));
	}

}
