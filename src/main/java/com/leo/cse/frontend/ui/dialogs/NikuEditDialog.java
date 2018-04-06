package com.leo.cse.frontend.ui.dialogs;

import java.awt.event.KeyEvent;
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
		super("Edit 290.rec", 224, 146);
		NikuEdit ne = new NikuEdit(48, 4);
		scrollable = ne;
		addComponent(ne);
		addComponent(new Button("New", 32, 24, 48, 17, () -> {
			newNiku();
		}));
		addComponent(new Button("Load", 84, 24, 48, 17, () -> {
			loadNiku();
		}));
		addComponent(new Button("Unload", 136, 24, 48, 17, () -> {
			unloadNiku();
		}));
		addComponent(new Button("Save", 32, 43, 74, 17, () -> {
			saveNiku();
		}));
		addComponent(new Button("Save As", 110, 43, 74, 17, () -> {
			saveNikuAs();
		}));
		Button btnUndo = new Button("Undo", 32, 61, 74, 17, () -> {
			NikuRecord.undo();
		});
		btnUndo.setEnabled(() -> {
			return NikuRecord.canUndo();
		});
		addComponent(btnUndo);
		Button btnRedo = new Button("Redo", 110, 61, 74, 17, () -> {
			NikuRecord.redo();
		});
		btnRedo.setEnabled(() -> {
			return NikuRecord.canRedo();
		});
		addComponent(btnRedo);
		addComponent(new Label(
				"Scroll to add/remove seconds\nShift+scroll to add/remove minutes\nCtrl+scroll to add/remove tenths of seconds\nClick to enter number",
				112, 79, true));
	}

	private void newNiku() {
		if (NikuRecord.isLoaded() && NikuRecord.isModified()) {
			int sel = JOptionPane.showConfirmDialog(Main.window,
					"Are you sure you want to create a new 290.rec file?\nUnsaved changes will be lost!",
					"Unsaved changes detected", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (sel == JOptionPane.CANCEL_OPTION)
				return;
		}
		NikuRecord.create();
	}

	private void loadNiku() {
		if (NikuRecord.isLoaded() && NikuRecord.isModified()) {
			int sel = JOptionPane.showConfirmDialog(Main.window,
					"Are you sure you want to load a new 290.rec file?\nUnsaved changes will be lost!",
					"Unsaved changes detected", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (sel == JOptionPane.CANCEL_OPTION)
				return;
		}
		File dir = new File(Config.get(Config.KEY_LAST_NIKU, System.getProperty("user.dir")));
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
			} finally {
				Config.set(Config.KEY_LAST_NIKU, dir.getAbsolutePath());
			}
	}

	private void unloadNiku() {
		if (NikuRecord.isLoaded() && NikuRecord.isModified()) {
			int sel = JOptionPane.showConfirmDialog(Main.window,
					"Are you sure you want to unload the 290.rec file?\nUnsaved changes will be lost!",
					"Unsaved changes detected", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (sel == JOptionPane.CANCEL_OPTION)
				return;
		}
		NikuRecord.unload();
	}

	private void saveNiku() {
		if (!NikuRecord.isLoaded())
			return;
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
		if (!NikuRecord.isLoaded())
			return;
		File dir = new File(Config.get(Config.KEY_LAST_NIKU, System.getProperty("user.dir")));
		if (!dir.exists())
			dir = new File(System.getProperty("user.dir"));
		int result = FrontUtils.openFileChooser("Save 290.rec...", NIKU_FILTER, dir, false, true);
		if (result == JFileChooser.APPROVE_OPTION)
			try {
				File file = FrontUtils.getSelectedFile();
				if (file.exists()) {
					int confirmVal = JOptionPane.showConfirmDialog(Main.window,
							"Are you sure you want to overwrite this file?", "Overwrite confirmation",
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if (confirmVal != JOptionPane.YES_OPTION)
						return;
				}
				NikuRecord.save(file);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(Main.window, "An error occured while saving the 290.rec file:\n" + e,
						"Could not save 290.rec file!", JOptionPane.ERROR_MESSAGE);
				return;
			} finally {
				Config.set(Config.KEY_LAST_NIKU, dir.getAbsolutePath());
			}
	}

	@Override
	public void onKey(int code, boolean shift, boolean ctrl) {
		switch (code) {
		case KeyEvent.VK_O:
			if (ctrl) {
				loadNiku();
			}
			break;
		case KeyEvent.VK_S:
			if (ctrl) {
				if (shift)
					saveNikuAs();
				else {
					saveNiku();
				}
			}
			break;
		case KeyEvent.VK_Z:
			if (ctrl) {
				if (shift)
					NikuRecord.redo();
				else
					NikuRecord.undo();
			}
			break;
		case KeyEvent.VK_Y:
			if (ctrl) {
				NikuRecord.redo();
			}
			break;
		default:
			break;
		}
	}

}
