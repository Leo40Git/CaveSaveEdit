package com.leo.cse.frontend.ui.panels;

import java.util.function.Supplier;

import com.leo.cse.frontend.ui.SaveEditorPanel;
import com.leo.cse.frontend.ui.components.FlagsUI;

public class FlagsPanel extends Panel {
	
	private int flagScroll;

	public FlagsPanel() {
		super();
		compList.add(new FlagsUI(new Supplier<Integer>() {
			@Override
			public Integer get() {
				return flagScroll;
			}
		}, (Integer t) -> {
			flagScroll = t;
		}, new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return SaveEditorPanel.hideSystemFlags;
			}
		}, (Boolean t) -> {
			SaveEditorPanel.hideSystemFlags = t;
		}, new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return SaveEditorPanel.hideUndefinedFlags;
			}
		}, (Boolean t) -> {
			SaveEditorPanel.hideUndefinedFlags = t;
		}));
	}

}
