package com.leo.cse.frontend.ui.dialogs;

import java.awt.Color;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.frontend.Config;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.SaveEditorPanel;
import com.leo.cse.frontend.ui.components.BooleanBox;
import com.leo.cse.frontend.ui.components.Button;
import com.leo.cse.frontend.ui.components.Label;
import com.leo.cse.frontend.ui.components.StringBox;

public class SettingsDialog extends BaseDialog {

	private static final byte[] TEST_STRING = new byte[] { (byte) 'T', (byte) 'e', (byte) 's', (byte) 't' };

	public SettingsDialog() {
		super("Settings", 300, 104);
		addComponent(new Button("MCI Settings", Resources.miscIcons[0], 4, 4, 292, 17, () -> {
			SaveEditorPanel.panel.addDialogBox(new MCIDialog());
		}));
		addComponent(new Button("Change Line Color", Resources.miscIcons[1], 4, 23, 292, 17, () -> {
			setLineColor();
		}));
		addComponent(new BooleanBox("Load NPCs?", 4, 43, () -> {
			return ExeData.doLoadNpc();
		}, (Boolean newVal) -> {
			ExeData.setLoadNpc(newVal);
			SaveEditorPanel.panel.setLoading(true);
			Main.window.repaint();
			SwingUtilities.invokeLater(() -> {
				try {
					ExeData.reload();
				} catch (IOException ignore) {
				} finally {
					Config.setBoolean(Config.KEY_LOAD_NPCS, ExeData.doLoadNpc());
					SwingUtilities.invokeLater(() -> {
						SaveEditorPanel.panel.setLoading(false);
						SaveEditorPanel.panel.addComponents();
						Main.window.repaint();
					});
				}
			});
			return newVal;
		}));
		addComponent(new Label("Encoding:", 4, 62));
		addComponent(new StringBox(54, 63, 242, 17, () -> {
			return ExeData.getEncoding();
		}, (String newVal) -> {
			try {
				new String(TEST_STRING, newVal);
			} catch (UnsupportedEncodingException e1) {
				JOptionPane.showMessageDialog(SaveEditorPanel.panel, "Encoding \"" + newVal + "\" is unsupported!",
						"Unsupported encoding", JOptionPane.ERROR_MESSAGE);
				return newVal;
			}
			ExeData.setEncoding(newVal);
			try {
				ExeData.reload();
			} catch (IOException ignore) {
			}
			return newVal;
		}, "encoding"));
		addComponent(new Button("Wipe Settings", 4, 83, 292, 17, () -> {
			int sel = JOptionPane.showConfirmDialog(Main.window,
					"Are you sure you want to wipe all settings?\nThis will restart the editor WITHOUT saving changes!",
					"Wipe Settings?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (sel != JOptionPane.YES_OPTION)
				return;
			Config.wipe();
			Config.init();
			Main.close(true);
		}));
	}

	private void setLineColor() {
		Color temp = FrontUtils.showColorChooserDialog(SaveEditorPanel.panel, "Select new line color", Main.lineColor,
				false);
		if (temp != null) {
			Main.lineColor = temp;
			Resources.colorImages(Main.lineColor);
		}
	}

}
