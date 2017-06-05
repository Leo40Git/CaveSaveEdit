package com.leo.cse.frontend.ui;

import java.util.function.Supplier;

import com.leo.cse.frontend.ui.components.FlagsUI;

public class FlagsTab extends EditorTab {

	public FlagsTab() {
		super("Flags");
		compList.add(new FlagsUI(new Supplier<Integer>() {
			@Override
			public Integer get() {
				return SaveEditorPanel.flagScroll;
			}
		}, (Integer t) -> {
			SaveEditorPanel.flagScroll = t;
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
