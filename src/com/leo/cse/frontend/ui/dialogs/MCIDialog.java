package com.leo.cse.frontend.ui.dialogs;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.leo.cse.frontend.Config;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.data.CSData;
import com.leo.cse.frontend.ui.SaveEditorPanel;

public class MCIDialog extends BaseDialog {

	public MCIDialog() {
		super("MCI Settings", 300, 104);
	}

	@Override
	public void render(Graphics g) {
		super.render(g);
		final int x = getWindowX(), y = getWindowY();
		FrontUtils.drawString(g, "Current MCI for:\n" + MCI.get("Meta.Name") + "\nBy:\n" + MCI.get("Meta.Author")
				+ "\nSpecial support:\n" + MCI.getSpecials(), x + 4, y);
		g.setColor(Color.white);
		g.fillRect(x + 1, y + height - 34, 299, 16);
		g.setColor(Color.black);
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
			boolean ret = loadMCI();
			if (ret)
				SaveEditorPanel.panel.addComponents();
			return ret;
		} else if (FrontUtils.pointInRectangle(x, y, wx + width - 150, wy + height - 18, 150, 16)) {
			try {
				MCI.readDefault();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(Main.window,
						"An error occured while loading the default MCI file:\n" + e.getMessage(),
						"Could not load default MCI file!", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			return true;
		}
		return false;
	}

	private boolean loadMCI() {
		if (SaveEditorPanel.fc == null)
			SaveEditorPanel.fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("MCI Files", "mci");
		SaveEditorPanel.fc.setFileFilter(filter);
		SaveEditorPanel.fc.setCurrentDirectory(new File(Config.get(Config.KEY_LAST_DEFINES, ".")));
		int returnVal = SaveEditorPanel.fc.showOpenDialog(Main.window);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = SaveEditorPanel.fc.getSelectedFile();
			try {
				MCI.read(file);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(Main.window,
						"An error occured while loading the MCI file:\n" + e.getMessage(), "Could not load MCI file!",
						JOptionPane.ERROR_MESSAGE);
				return false;
			} finally {
				try {
					CSData.load();
				} catch (IOException ignore) {
				}
				Config.set(Config.KEY_LAST_DEFINES, file.getAbsolutePath());
				JOptionPane.showMessageDialog(Main.window, "The MCI file was loaded successfully.",
						"MCI loaded successfully", JOptionPane.INFORMATION_MESSAGE);
			}
		} else
			return false;
		return true;
	}

}
