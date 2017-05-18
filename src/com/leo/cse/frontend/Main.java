package com.leo.cse.frontend;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.leo.cse.backend.Profile;

public class Main extends JFrame implements MouseListener {

	private static final long serialVersionUID = -5073541927297432013L;

	public static final Dimension WINDOW_SIZE = new Dimension(867, 452 + 33);
	public static final String VERSION = "1.0";
	public static final Color BG_COLOR = Color.decode("0xF0F0F0");

	public static Main window;

	private static class ConfirmCloseWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			if (Profile.isLoaded() && Profile.isModified()) {
				int sel = JOptionPane.showConfirmDialog(window,
						"Are you sure you want to close the editor?\nUnsaved changes will be lost!",
						"Unsaved changes detected", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (sel == JOptionPane.NO_OPTION)
					return;
			}
			System.exit(0);
		}
	}

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
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new ConfirmCloseWindowListener());
		setTitle(this);
		setBackground(BG_COLOR);
		setIconImage(Resources.icon);
		SaveEditorPanel sep = new SaveEditorPanel();
		add(sep);
		addMouseListener(sep);
		addMouseListener(this);
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
			setTitle(window);
			window.repaint();
			JOptionPane.showMessageDialog(Main.window, "The profile file was loaded successfully.",
					"Profile loaded successfully", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public static void setTitle(Main window) {
		if (window == null)
			window = Main.window;
		if (Profile.isLoaded() && Profile.getFile() != null)
			window.setTitle(
					"CaveSaveEdit - " + Profile.getFile().getAbsolutePath() + (Profile.isModified() ? "*" : ""));
		else
			window.setTitle("CaveSaveEdit");
	}

	public static void main(String[] args) {
		if (GraphicsEnvironment.isHeadless()) {
			System.err.println("Headless mode is enabled!\nCaveSaveEdit cannot run in headless mode!");
			System.exit(0);
		}
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(() -> {
			window = new Main();
			window.setVisible(true);
			File p = new File("Profile.dat");
			if (p.exists())
				loadProfile(p);
		});
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		setTitle(window);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}
