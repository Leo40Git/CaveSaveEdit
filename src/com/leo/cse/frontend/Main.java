package com.leo.cse.frontend;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.leo.cse.backend.Profile;

public class Main extends JFrame {

	private static final long serialVersionUID = -5073541927297432013L;

	public static final Dimension WINDOW_SIZE = new Dimension(867, 452 + 33);
	public static final String VERSION = "1.0";

	public static Main window;

	public Main() {
		try {
			Resources.load();
			Defines.readDefault();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Could not load resources!\nPlease report this error to the programmer.",
					"Could not load resources", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("CaveSaveEdit");
		setBackground(Color.white);
		setIconImage(Resources.icon);
		SaveEditorPanel sep = new SaveEditorPanel();
		add(sep);
		addMouseListener(sep);
		addMouseWheelListener(sep);
		setMaximumSize(WINDOW_SIZE);
		setMinimumSize(WINDOW_SIZE);
		setPreferredSize(WINDOW_SIZE);
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
	}

	public Dimension getActualSize(boolean excludeHeadFoot) {
		final Insets i = getInsets();
		return new Dimension(WINDOW_SIZE.width - (i.left + i.right),
				WINDOW_SIZE.height - (i.top + i.bottom) - (excludeHeadFoot ? 33 : 0));
	}

	public Dimension getActualSize() {
		return getActualSize(true);
	}

	public static void loadProfile(File file) {
		try {
			Profile.read(file);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(Main.window,
					"An error occured while loading the profile file:\n" + e.getMessage(),
					"Could not load profile file!", JOptionPane.ERROR_MESSAGE);
			return;
		} finally {
			JOptionPane.showMessageDialog(Main.window, "The profile file was loaded successfully.",
					"Profile loaded successfully", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(() -> {
			window = new Main();
			window.setVisible(true);
			File p = new File("./Profile.dat");
			if (p.exists())
				loadProfile(p);
		});
	}

}
