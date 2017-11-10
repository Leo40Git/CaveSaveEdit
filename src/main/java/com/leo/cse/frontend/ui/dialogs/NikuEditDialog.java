package com.leo.cse.frontend.ui.dialogs;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.leo.cse.backend.niku.NikuListener;
import com.leo.cse.backend.niku.NikuRecord;
import com.leo.cse.frontend.Config;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.ui.components.Button;

public class NikuEditDialog extends BaseDialog implements NikuListener {

	private static final FileNameExtensionFilter NIKU_FILTER = new FileNameExtensionFilter("290.rec file", "rec");
	
	public NikuEditDialog() {
		super("Edit 290.rec", 160, 56);
		addComponent(new Button("Load 290.rec", 2, 2, 100, 17, () -> {
			loadNiku();
		}));
		addComponent(new Button("Save 290.rec", 2, 38, 100, 17, () -> {
			saveNiku();
		}));
	}

	private void loadNiku() {
		File dir = new File(Config.get(Config.KEY_LAST_PROFIE, System.getProperty("user.dir")));
		if (!dir.exists())
			dir = new File(System.getProperty("user.dir"));
		int result = FrontUtils.openFileChooser("Load 290.rec...", NIKU_FILTER, dir, false, false);
		if (result == JFileChooser.APPROVE_OPTION)
			try {
				NikuRecord.read(FrontUtils.getSelectedFile());
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(Main.window, "An error occured while loading the 290.rec file:\n" + e,
						"Could not load 290.rec file!", JOptionPane.ERROR_MESSAGE);
				return;
			}
	}

	private void saveNiku() {
		if (NikuRecord.getFile() != null)
			try {
				NikuRecord.write(null);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(Main.window, "An error occured while saving the 290.rec file:\n" + e,
						"Could not save 290.rec file!", JOptionPane.ERROR_MESSAGE);
				return;
			}
	}

	@Override
	public void onChange(int oldValue, int newValue) {
		
	}

}
