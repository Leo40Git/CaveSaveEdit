package com.leo.cse.frontend;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.function.Supplier;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.profile.Profile;
import com.leo.cse.backend.profile.ProfileChangeListener;
import com.leo.cse.frontend.ui.SaveEditorPanel;

public class Main extends JFrame implements ProfileChangeListener {

	private static final long serialVersionUID = -5073541927297432013L;

	public static final Dimension WINDOW_SIZE = new Dimension(867, 682 + 33);
	public static final String VERSION = "1.0.1";
	public static final Color COLOR_BG = new Color(0, 0, 25);

	public static final Supplier<Boolean> FALSE_SUPPLIER = new Supplier<Boolean>() {
		public Boolean get() {
			return false;
		}
	};

	public static Main window;
	public static Color lineColor;
	public static String encoding;

	private static class ConfirmCloseWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			Main.close(false);
		}
	}

	public static void close(boolean alwaysShowDialog) {
		if (Profile.isLoaded() && Profile.isModified()) {
			int sel = JOptionPane.showConfirmDialog(window,
					"Are you sure you want to close the editor?\nUnsaved changes will be lost!",
					"Unsaved changes detected", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (sel == JOptionPane.CANCEL_OPTION)
				return;
		} else if (alwaysShowDialog) {
			int sel = JOptionPane.showConfirmDialog(window, "Are you sure you want to close the editor?",
					"Quit CaveSaveEditor?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if (sel == JOptionPane.CANCEL_OPTION)
				return;
		}
		Config.setColor(Config.KEY_LINE_COLOR, lineColor);
		Config.set(Config.KEY_ENCODING, encoding);
		SaveEditorPanel.panel.saveSettings();
		System.exit(0);
	}

	public Main() {
		Profile.addListener(this);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new ConfirmCloseWindowListener());
		setTitle(this);
		setBackground(COLOR_BG);
		setIconImage(Resources.icon);
		SaveEditorPanel sep = new SaveEditorPanel();
		add(sep);
		addKeyListener(sep);
		addMouseListener(sep);
		addMouseMotionListener(sep);
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
		if (SaveEditorPanel.panel != null)
			SaveEditorPanel.panel.setLoading(true);
		window.repaint();
		SwingUtilities.invokeLater(() -> {
			try {
				Profile.read(file);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(Main.window,
						"An error occured while loading the profile file:\n" + e.getMessage(),
						"Could not load profile file!", JOptionPane.ERROR_MESSAGE);
				return;
			} finally {
				// unload existing exe
				ExeData.unload();
				// try to load exe
				try {
					ExeData.load(new File(
							Profile.getFile().getAbsoluteFile().getParent() + "/" + MCI.get("Game.ExeName") + ".exe"));
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("EXE loading failed.");
				}
				Config.set(Config.KEY_LAST_PROFIE, file.getAbsolutePath());
				SwingUtilities.invokeLater(() -> {
					if (SaveEditorPanel.panel != null)
						SaveEditorPanel.panel.setLoading(false);
					window.repaint();
				});
			}
		});
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
			System.out.println("Headless mode is enabled!\nCaveSaveEdit cannot run in headless mode!");
			System.exit(0);
		}
		final String nolaf = "nolaf";
		if (!new File(System.getProperty("user.dir") + "/" + nolaf).exists()) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Could not set Look & Feel!\nPlease add a file named \"" + nolaf
						+ "\" (all lowercase, no extension) to the application folder, and then restart the application.",
						"Could not set Look & Feel", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		}
		Config.init();
		lineColor = Config.getColor(Config.KEY_LINE_COLOR, Color.white);
		encoding = Config.get(Config.KEY_ENCODING, "UTF-8");
		ExeData.setLoadNpc(Config.getBoolean(Config.KEY_LOAD_NPCS, true));
		try {
			Resources.load();
			Resources.colorImages(lineColor);
			MCI.readDefault();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Could not load resources!\nPlease report this error to the programmer.",
					"Could not load resources", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		SwingUtilities.invokeLater(() -> {
			window = new Main();
			window.setVisible(true);
			SwingUtilities.invokeLater(() -> {
				File p = new File(System.getProperty("user.dir") + "/Profile.dat");
				if (p.exists())
					loadProfile(p);
			});
		});
	}

	@Override
	public void onChange(String field, int id) {
		setTitle(this);
		String msg = "";
		if (field == Profile.EVENT_SAVE)
			msg = "profile saved";
		else if (field == Profile.EVENT_LOAD)
			msg = "profile loaded";
		else {
			if (id > -1)
				field = String.format(field, id);
			msg = "field \"" + field + "\" modified";
		}
		System.out.println(msg);
	}

}
