package com.leo.cse.frontend.ui;

import java.util.ArrayList;
import java.util.List;

import com.leo.cse.frontend.ui.components.Component;

public abstract class EditorTab {
	
	public enum ID {
		GENERAL, INVENTORY, WARPS, FLAGS, VARIABLES;
	}
	
	protected String label;
	protected List<Component> compList;
	
	protected EditorTab(String label) {
		this.label = label;
		compList = new ArrayList<>();
	}
	
	public String getLabel() {
		return label;
	}
	
	public List<Component> getComponents() {
		return compList;
	}

}
