package com.leo.cse.frontend.ui.panels;

import javax.swing.JPanel;

import com.leo.cse.frontend.Resources.Icon;

public abstract class EditorPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private String title;
	private Icon icon;
	private String tip;

	public EditorPanel(String title, Icon icon, String tip) {
		this.title = title;
		this.icon = icon;
		this.tip = tip;
	}

	public String getTitle() {
		return title;
	}

	public Icon getIcon() {
		return icon;
	}
	
	public String getTip() {
		return tip;
	}

}
