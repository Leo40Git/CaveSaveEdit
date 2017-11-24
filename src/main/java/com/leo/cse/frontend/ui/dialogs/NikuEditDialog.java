package com.leo.cse.frontend.ui.dialogs;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.leo.cse.backend.niku.NikuRecord;
import com.leo.cse.frontend.Config;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.ui.components.Button;
import com.leo.cse.frontend.ui.components.Label;
import com.leo.cse.frontend.ui.components.NikuEdit;

public class NikuEditDialog extends BaseDialog {

	private static final FileNameExtensionFilter NIKU_FILTER = new FileNameExtensionFilter("290.rec file", "rec");

	public NikuEditDialog() {
		super("Edit 290.rec", 224, 128);
		NikuEdit ne = new NikuEdit(48, 4);
		scrollable = ne;
		addComponent(ne);
		addComponent(new Button("Load", 32, 24, 72, 17, () -> {
			loadNiku();
		}));
		addComponent(new Button("Save", 112, 24, 72, 17, () -> {
			saveNiku();
		}));
		addComponent(new Button("Reset to 0", 32, 43, 72, 17, () -> {
			NikuRecord.setValue(0);
		}));
		addComponent(new Button("Save As", 112, 43, 72, 17, () -> {
			saveNikuAs();
		}));
		addComponent(new Label(
				"Scroll to add/remove seconds\nShift+scroll to add/remove minutes\nCtrl+scroll to add/remove tenths of seconds\nClick to enter number",
				112, 61, true));
	}

	private void loadNiku() {
		File dir = new File(Config.get(Config.KEY_LAST_PROFIE, System.getProperty("user.dir")));
		if (!dir.exists())
			dir = new File(System.getProperty("user.dir"));
		int result = FrontUtils.openFileChooser("Load 290.rec...", NIKU_FILTER, dir, false, false);
		if (result == JFileChooser.APPROVE_OPTION)
			try {
				NikuRecord.load(FrontUtils.getSelectedFile());
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(Main.window, "An error occured while loading the 290.rec file:\n" + e,
						"Could not load 290.rec file!", JOptionPane.ERROR_MESSAGE);
				return;
			}
	}

	private void saveNiku() {
		if (NikuRecord.getFile() == null)
			saveNikuAs();
		else
			try {
				NikuRecord.save(null);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(Main.window, "An error occured while saving the 290.rec file:\n" + e,
						"Could not save 290.rec file!", JOptionPane.ERROR_MESSAGE);
				return;
			}
	}

	private void saveNikuAs() {
		File dir = new File(Config.get(Config.KEY_LAST_PROFIE, System.getProperty("user.dir")));
		if (!dir.exists())
			dir = new File(System.getProperty("user.dir"));
		int result = FrontUtils.openFileChooser("Save 290.rec...", NIKU_FILTER, dir, false, true);
		if (result == JFileChooser.APPROVE_OPTION)
			try {
				NikuRecord.save(FrontUtils.getSelectedFile());
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(Main.window, "An error occured while saving the 290.rec file:\n" + e,
						"Could not save 290.rec file!", JOptionPane.ERROR_MESSAGE);
				return;
			}
	}

}
