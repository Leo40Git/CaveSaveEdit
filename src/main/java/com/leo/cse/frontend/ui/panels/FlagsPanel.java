package com.leo.cse.frontend.ui.panels;

import java.awt.Dimension;

import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.ui.components.Line;
import com.leo.cse.frontend.ui.components.ScrollBar;

public class FlagsPanel extends Panel {
	
	private ScrollBar scrollBar;
	
	public FlagsPanel() {
		super();
		final Dimension winSize = Main.WINDOW_SIZE;
		scrollBar = new ScrollBar(winSize.width - 21, 0, winSize.height - 53);
		compList.add(scrollBar);
		compList.add(new Line(0, winSize.height - 53, winSize.width - 21, 0));
	}
	
	@Override
	public ScrollBar getGlobalScrollbar() {
		return scrollBar;
	}

}
