package com.leo.cse.frontend.ui.panels;

import java.awt.Dimension;

import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.ui.SaveEditorPanel;
import com.leo.cse.frontend.ui.components.Button;
import com.leo.cse.frontend.ui.components.Label;
import com.leo.cse.frontend.ui.components.Line;
import com.leo.cse.frontend.ui.components.MapFlagList;
import com.leo.cse.frontend.ui.components.ScrollBar;
import com.leo.cse.frontend.ui.components.ScrollWrapper;
import com.leo.cse.frontend.ui.dialogs.MapFlagDialog;

public class MapFlagsPanel extends Panel {
	
	private MapFlagList flagList;
	private ScrollWrapper flagListWrap;

	public MapFlagsPanel() {
		super();
		final Dimension winSize = Main.WINDOW_SIZE;
		flagList = new MapFlagList();
		flagListWrap = new ScrollWrapper(flagList, 0, 0, winSize.width - 21, winSize.height - 55);
		compList.add(flagListWrap);
		compList.add(new Line(0, winSize.height - 54, winSize.width - 21, 0));
		compList.add(new Button("Set map flag...", 2, winSize.height - 52, 130, 16, () -> {
			SaveEditorPanel.panel.addDialogBox(new MapFlagDialog());
		}));
		compList.add(new Label("Shift - x10 scroll, Control - x100 scroll, Shift+Ctrl - x1000 scroll", 562,
				winSize.height - 54));
	}

	@Override
	public ScrollBar getGlobalScrollbar() {
		return flagListWrap.getScrollbar();
	}

}
