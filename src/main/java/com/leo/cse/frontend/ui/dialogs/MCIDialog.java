package com.leo.cse.frontend.ui.dialogs;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.frontend.Config;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.ui.SaveEditorPanel;
import com.leo.cse.frontend.ui.components.Button;
import com.leo.cse.frontend.ui.components.DynamicLabel;

public class MCIDialog extends BaseDialog {

	public MCIDialog() {
		super("MCI Settings", 380, 134);
		addComponent(new DynamicLabel(() -> {
			return "Current MCI for:\n" + MCI.get("Meta.Name") + "\nBy:\n" + MCI.get("Meta.Author")
					+ "\nSpecial support:\n" + MCI.getSpecials();
		}, 4, 0));
		addComponent(new Button("Load MCI file", 14, 94, 356, 17, () -> {
			if (loadMCI()) {
				SaveEditorPanel.panel.addComponents();
				Main.window.repaint();
			}
		}));
		addComponent(new Button("Load default MCI file", 14, 114, 356, 17, () -> {
			try {
				MCI.readDefault();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(Main.window,
						"An error occured while loading the default MCI file:\n" + e.getMessage(),
						"Could not load default MCI file!", JOptionPane.ERROR_MESSAGE);
			}
		}));
	}

	private boolean ret = false;

	private boolean loadMCI() {
		int returnVal = FrontUtils.openFileChooser("Open MCI file", new FileNameExtensionFilter("MCI Files", "mci"),
				new File(Config.get(Config.KEY_LAST_MCI_FILE, System.getProperty("user.dir"))), false, false);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			final File file = FrontUtils.getSelectedFile();
			if (!file.exists())
				return false;
			SaveEditorPanel.panel.setLoading(true);
			Main.window.repaint();
			ret = true;
			SwingUtilities.invokeLater(() -> {
				try {
					MCI.read(file);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(Main.window,
							"An error occured while loading the MCI file:\n" + e.getMessage(),
							"Could not load MCI file!", JOptionPane.ERROR_MESSAGE);
					ret = false;
					return;
				} finally {
					try {
						ExeData.reload();
					} catch (IOException ignore) {
					}
					Config.set(Config.KEY_LAST_MCI_FILE, file.getAbsolutePath());
					SwingUtilities.invokeLater(() -> {
						SaveEditorPanel.panel.setLoading(false);
						SaveEditorPanel.panel.addComponents();
						Main.window.repaint();
					});
				}
			});
			return ret;
		} else
			return false;
	}

}
