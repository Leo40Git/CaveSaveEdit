package com.leo.cse.frontend.ui.panels;

import java.util.ArrayList;
import java.util.List;

import com.leo.cse.frontend.ui.components.Component;

public abstract class Panel {
	
	protected List<Component> compList;
	
	protected Panel() {
		compList = new ArrayList<>();
	}
	
	public List<Component> getComponents() {
		return compList;
	}

}
