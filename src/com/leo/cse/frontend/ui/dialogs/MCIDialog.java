package com.leo.cse.frontend.ui.dialogs;

import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.leo.cse.backend.ExeData;
import com.leo.cse.frontend.Config;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.MCI.MCIException;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.ui.SaveEditorPanel;

public class MCIDialog extends BaseDialog {

	public MCIDialog() {
		super("MCI Settings", 300, 114);
	}

	@Override
	public void render(Graphics g) {
		super.render(g);
		final int x = getWindowX(), y = getWindowY();
		g.setColor(Main.lineColor);
		FrontUtils.drawString(g, "Current MCI for:\n" + MCI.get("Meta.Name") + "\nBy:\n" + MCI.get("Meta.Author")
				+ "\nSpecial support:\n" + MCI.getSpecials(), x + 4, y);
		g.setColor(Main.COLOR_BG);
		g.fillRect(x + 1, y + height - 34, 299, 16);
		g.setColor(Main.lineColor);
		g.drawRect(x, y + height - 35, width, 17);
		g.drawLine(x + width - 150, y + height - 18, x + width - 150, y + height - 34);
		FrontUtils.drawStringCentered(g, "Load MCI file", x + width / 4, y + height - 36);
		FrontUtils.drawStringCentered(g, "Load default MCI file", x + width - 150 + width / 4, y + height - 36);
	}

	@Override
	public boolean onClick(int x, int y) {
		if (super.onClick(x, y))
			return true;
		final int wx = getWindowX(), wy = getWindowY(false);
		if (FrontUtils.pointInRectangle(x, y, wx, wy + height - 18, 150, 16)) {
			if (loadMCI()) {
				SaveEditorPanel.panel.addComponents();
				Main.window.repaint();
			}
			return false;
		} else if (FrontUtils.pointInRectangle(x, y, wx + width - 150, wy + height - 18, 150, 16)) {
			try {
				MCI.readDefault();
			} catch (IOException | MCIException e) {
				JOptionPane.showMessageDialog(Main.window,
						"An error occured while loading the default MCI file:\n" + e.getMessage(),
						"Could not load default MCI file!", JOptionPane.ERROR_MESSAGE);
			}
		}
		return false;
	}

	private boolean ret;

	private boolean loadMCI() {
		int returnVal = FrontUtils.openFileChooser("Open MCI file", new FileNameExtensionFilter("MCI Files", "mci"),
				new File(Config.get(Config.KEY_LAST_MCI_FILE, System.getProperty("user.dir"))), false);
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
				} catch (IOException | MCIException e) {
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
