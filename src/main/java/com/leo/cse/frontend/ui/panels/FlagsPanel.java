package com.leo.cse.frontend.ui.panels;

import java.awt.Dimension;
import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.ui.SaveEditorPanel;
import com.leo.cse.frontend.ui.components.BooleanBox;
import com.leo.cse.frontend.ui.components.Button;
import com.leo.cse.frontend.ui.components.FlagList;
import com.leo.cse.frontend.ui.components.Label;
import com.leo.cse.frontend.ui.components.Line;
import com.leo.cse.frontend.ui.components.ScrollBar;
import com.leo.cse.frontend.ui.components.ScrollWrapper;
import com.leo.cse.frontend.ui.dialogs.FlagDialog;

public class FlagsPanel extends Panel {

	private FlagList flagList;
	private ScrollWrapper flagListWrap;

	public FlagsPanel() {
		super();
		final Dimension winSize = Main.WINDOW_SIZE;
		final Supplier<Boolean> huSup = new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return SaveEditorPanel.hideUndefinedFlags;
			}
		}, hsSup = new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return SaveEditorPanel.hideSystemFlags;
			}
		};
		flagList = new FlagList(huSup, hsSup);
		flagListWrap = new ScrollWrapper(flagList, 0, 0, winSize.width - 21, winSize.height - 55);
		compList.add(flagListWrap);
		compList.add(new Line(0, winSize.height - 54, winSize.width - 21, 0));
		compList.add(new BooleanBox("Hide undefined flags?", false, 2, winSize.height - 52, huSup,
				new Function<Boolean, Boolean>() {
					@Override
					public Boolean apply(Boolean t) {
						SaveEditorPanel.hideUndefinedFlags = t;
						flagList.calculateShownFlags();
						return t;
					}
				}));
		compList.add(
				new BooleanBox("Hide system flags?", false, 132, winSize.height - 52, hsSup, new Function<Boolean, Boolean>() {
					@Override
					public Boolean apply(Boolean t) {
						SaveEditorPanel.hideSystemFlags = t;
						flagList.calculateShownFlags();
						return t;
					}
				}));
		compList.add(new Button("Set flag...", 262, winSize.height - 52, 130, 16, () -> {
			SaveEditorPanel.panel.addDialogBox(new FlagDialog());
		}));
		compList.add(new Label("Shift - x10 scroll, Control - x100 scroll, Shift+Ctrl - x1000 scroll", 562,
				winSize.height - 54));
	}

	@Override
	public ScrollBar getGlobalScrollbar() {
		return flagListWrap.getScrollbar();
	}

}
