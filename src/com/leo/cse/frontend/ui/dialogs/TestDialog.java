package com.leo.cse.frontend.ui.dialogs;

import java.awt.Dimension;
import java.awt.Graphics;

import com.leo.cse.backend.Profile;
import com.leo.cse.frontend.data.CSData;

public class TestDialog extends BaseDialog {

	public TestDialog(Dimension winSize) {
		super("TEST!!!", winSize.width - 1, winSize.height - 18);
	}

	@Override
	public void render(Graphics g) {
		super.render(g);
		int x = getWindowX(), y = getWindowY();
		//g.drawImage(CSData.getMyChar(), x + 1, y + 1, null);
		//y += CSData.getMyChar().getHeight();
		g.drawImage(CSData.getImg(CSData.getMapInfo(Profile.getMap()).getTileset()), x + 1, y + 1, null);
	}

}
