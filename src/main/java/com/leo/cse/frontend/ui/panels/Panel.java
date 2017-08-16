package com.leo.cse.frontend.ui.panels;

import java.util.ArrayList;
import java.util.List;

import com.leo.cse.frontend.ui.components.Component;
import com.leo.cse.frontend.ui.components.ScrollBar;

public abstract class Panel {
	
	protected List<Component> compList;
	
	protected Panel() {
		compList = new ArrayList<>();
	}
	
	public List<Component> getComponents() {
		return compList;
	}
	
	public ScrollBar getGlobalScrollbar() {
		return null;
	}

}
