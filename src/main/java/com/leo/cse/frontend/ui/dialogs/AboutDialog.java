package com.leo.cse.frontend.ui.dialogs;

import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.components.Button;
import com.leo.cse.frontend.ui.components.Icon;
import com.leo.cse.frontend.ui.components.Label;

public class AboutDialog extends BaseDialog {

	public AboutDialog() {
		super("About CaveSaveEdit v" + Main.VERSION, 422, 106);
		addComponent(new Icon(Resources.icon, 4, 4));
		addComponent(new Label("CaveSaveEdit version " + Main.VERSION + "\nWritten by Leo40Story"
				+ "\nBased on Kapow's profile specs (http://www.cavestory.org/guides/profile.txt)"
				+ "\nUI sprites by zxin" + "\nEarly testers: zxin and gamemanj/20kdc", 38, 0));
		addComponent(new Button("Check for Updates", 130, 80, 192, 17, () -> {
			Main.updateCheck(true, true);
		}));
	}

}
