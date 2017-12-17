package com.leo.cse.frontend;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Supplier;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.leo.cse.backend.StrTools;
import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.exe.ExeLoadListener;
import com.leo.cse.backend.profile.ProfileListener;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.frontend.ui.SaveEditorPanel;

public class Main extends JFrame implements ExeLoadListener, ProfileListener {

	private static final long serialVersionUID = -5073541927297432013L;

	public static final Dimension WINDOW_SIZE = new Dimension(867, 686);
	public static final Version VERSION = new Version("3.2");
	public static final String UPDATE_CHECK_SITE = "https://raw.githubusercontent.com/Leo40Git/CaveSaveEdit/master/.version";
	public static final String DOWNLOAD_SITE = "https://github.com/Leo40Git/CaveSaveEdit/releases/";
	public static final Color COLOR_BG = new Color(0, 0, 25);

	public static final Supplier<Boolean> TRUE_SUPPLIER = new Supplier<Boolean>() {
		public Boolean get() {
			return true;
		}
	};
	public static final Supplier<Boolean> FALSE_SUPPLIER = new Supplier<Boolean>() {
		public Boolean get() {
			return false;
		}
	};

	public static Main window;
	public static Color lineColor;

	private static class ConfirmCloseWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			Main.close();
		}
	}

	public static void close(boolean reboot) {
		if (ProfileManager.isLoaded() && ProfileManager.isModified()) {
			int sel = JOptionPane.showConfirmDialog(window, "Save profile?", "Unsaved changes detected",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (sel == JOptionPane.YES_OPTION)
				try {
					ProfileManager.save();
				} catch (IOException e) {
					e.printStackTrace();
				}
			else if (sel == JOptionPane.CANCEL_OPTION)
				return;
		} else if (!reboot) {
			int sel = JOptionPane.showConfirmDialog(window, "Are you sure you want to close the editor?",
					"Quit CaveSaveEditor?", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if (sel != JOptionPane.YES_OPTION)
				return;
		}
		if (reboot) {
			window.dispose();
			ProfileManager.removeListener(window);
			ProfileManager.removeListener(SaveEditorPanel.panel);
			ProfileManager.unload();
			ExeData.removeListener(window);
			ExeData.unload();
			window = null;
			SaveEditorPanel.panel = null;
			System.gc();
			Main.main(new String[0]);
		} else {
			Config.setColor(Config.KEY_LINE_COLOR, lineColor);
			Config.set(Config.KEY_ENCODING, ExeData.getEncoding());
			SaveEditorPanel.panel.saveSettings();
			System.exit(0);
		}
	}

	public static void close() {
		close(false);
	}

	public Main() {
		ProfileManager.addListener(this);
		ExeData.addListener(this);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new ConfirmCloseWindowListener());
		setTitle(this);
		setIconImage(Resources.icon);
		setUndecorated(true);
		setBackground(new Color(0, 0, 0, 0));
	}

	private void initPanel() {
		SaveEditorPanel sep = new SaveEditorPanel();
		add(sep);
		addKeyListener(sep);
		addMouseListener(sep);
		addMouseMotionListener(sep);
		addMouseWheelListener(sep);
		ProfileManager.addListener(sep);
		Dimension winSize = new Dimension(WINDOW_SIZE);
		winSize.width += 32;
		winSize.height += 48;
		setMaximumSize(winSize);
		setMinimumSize(winSize);
		setPreferredSize(winSize);
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
			File newExe = new File(file.getAbsoluteFile().getParent() + "/" + MCI.get("Game.ExeName") + ".exe");
			if (newExe.exists()) {
				// unload existing exe
				ExeData.unload();
				// try to load exe
				try {
					ExeData.load(new File(file.getAbsoluteFile().getParent() + "/" + MCI.get("Game.ExeName") + ".exe"));
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("EXE loading failed.");
					JOptionPane.showMessageDialog(Main.window, "An error occured while loading the executable:\n" + e,
							"Could not load executable!", JOptionPane.ERROR_MESSAGE);
				}
			}
			try {
				ProfileManager.load(file);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(Main.window, "An error occured while loading the profile file:\n" + e,
						"Could not load profile file!", JOptionPane.ERROR_MESSAGE);
				return;
			} finally {
				System.out.println("loaded profile " + ProfileManager.getLoadedFile());
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
		if (ProfileManager.isLoaded()) {
			File prof = ProfileManager.getLoadedFile();
			if (prof == null)
				window.setTitle("CaveSaveEdit - UNSAVED*");
			else
				window.setTitle("CaveSaveEdit - " + prof.getAbsolutePath() + (ProfileManager.isModified() ? "*" : ""));
		} else
			window.setTitle("CaveSaveEdit");
	}

	private static class FNCPrintStream extends PrintStream {

		private PrintStream consoleOut;

		public FNCPrintStream(OutputStream file, boolean err) throws FileNotFoundException {
			super(file);
			if (err)
				consoleOut = new PrintStream(new FileOutputStream(FileDescriptor.err));
			else
				consoleOut = new PrintStream(new FileOutputStream(FileDescriptor.out));
		}

		@Override
		public void write(int b) {
			synchronized (this) {
				super.write(b);
				consoleOut.write(b);
			}
		}

		@Override
		public void write(byte[] buf, int off, int len) {
			synchronized (this) {
				super.write(buf, off, len);
				consoleOut.write(buf, off, len);
			}
		}
	}

	public static class LoadFrame extends JFrame {
		private static final long serialVersionUID = 5562200728043308281L;

		private String loadString = "Checking for updates...";

		public void setLoadString(String loadString) {
			this.loadString = loadString;
		}

		private class LoadFramePanel extends JPanel {
			private static final long serialVersionUID = 191674646820485865L;

			private Color trans;
			private Dimension size;

			public LoadFramePanel(Color trans, Dimension win) {
				this.trans = trans;
				this.size = win;
			}

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setBackground(trans);
				g2d.clearRect(0, 0, getWidth(), getHeight());
				FrontUtils.drawNineSlice(g2d, Resources.shadow, 0, 0, getWidth(), getHeight());
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2d.setColor(COLOR_BG);
				g2d.fillRect(16, 16, size.width - 32, size.height - 32);
				g2d.setColor(Color.white);
				g2d.drawRect(16, 16, size.width - 32, size.height - 32);
				g2d.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
				FrontUtils.drawStringCentered(g2d, loadString, size.width / 2, size.height / 2, true, false);
			}
		}

		public LoadFrame() {
			final Dimension win = new Dimension(232, 112);
			setIconImage(Resources.icon);
			setPreferredSize(win);
			setMaximumSize(win);
			setMinimumSize(win);
			setUndecorated(true);
			final Color trans = new Color(0, 0, 0, 0);
			setBackground(trans);
			add(new LoadFramePanel(trans, win));
			pack();
			setLocationRelativeTo(null);
			setVisible(true);
			requestFocus();
		}
	}

	public static void resourceError(Throwable e) {
		e.printStackTrace();
		JOptionPane.showMessageDialog(null,
				"Could not load resources!\nPlease report this error to the programmer.\nAn exception has occured:\n"
						+ e,
				"Could not load resources", JOptionPane.ERROR_MESSAGE);
		System.exit(1);
	}

	public static LoadFrame updateCheck(boolean disposeOfLoadFrame, boolean showUpToDate) {
		LoadFrame loadFrame = new LoadFrame();
		File verFile = new File(System.getProperty("user.dir") + "/temp.version");
		System.out.println("Update check: starting");
		try {
			FrontUtils.downloadFile(UPDATE_CHECK_SITE, verFile);
		} catch (IOException e1) {
			System.err.println("Update check failed: attempt to download caused exception");
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, "The update check has failed!\nAre you not connected to the internet?",
					"Update check failed", JOptionPane.ERROR_MESSAGE);
		}
		if (verFile.exists()) {
			System.out.println("Update check: reading version");
			try (FileReader fr = new FileReader(verFile); BufferedReader reader = new BufferedReader(fr);) {
				Version check = new Version(reader.readLine());
				if (VERSION.compareTo(check) < 0) {
					System.out.println("Update check successful: have update");
					JPanel panel = new JPanel();
					panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
					panel.add(new JLabel("A new update is available: " + check));
					panel.add(new JLabel("Changelog:"));
					final String defaultCl = "None.";
					String cl = defaultCl;
					while (reader.ready()) {
						if (defaultCl.equals(cl))
							cl = reader.readLine();
						else
							cl += "\n" + reader.readLine();
					}
					JTextArea chglog = new JTextArea(cl);
					chglog.setEditable(false);
					chglog.setFont(Resources.font);
					JScrollPane scrollChglog = new JScrollPane(chglog);
					scrollChglog.setAlignmentX(0.1f);
					panel.add(scrollChglog);
					panel.add(new JLabel(
							"Click \"Yes\" to go to the download site, click \"No\" to continue to the save editor."));
					int result = JOptionPane.showConfirmDialog(null, panel, "New update!", JOptionPane.YES_NO_OPTION,
							JOptionPane.PLAIN_MESSAGE);
					if (result == JOptionPane.YES_OPTION) {
						URI dlSite = new URI(DOWNLOAD_SITE);
						if (Desktop.isDesktopSupported())
							Desktop.getDesktop().browse(dlSite);
						else
							JOptionPane.showMessageDialog(null,
									"Sadly, we can't browse to the download site for you on this platform. :(\nHead to\n"
											+ dlSite + "\nto get the newest update!",
									"Operation not supported...", JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					}
				} else {
					System.out.println("Update check successful: up to date");
					if (showUpToDate) {
						JOptionPane.showMessageDialog(null,
								"You are using the most up to date version of CaveSaveEdit! Have fun!", "Up to date!",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			} catch (IOException e) {
				System.err.println("Update check failed: attempt to read downloaded file caused exception");
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"The update check has failed!\nAn exception occured while reading update check results:\n" + e,
						"Update check failed", JOptionPane.ERROR_MESSAGE);
			} catch (URISyntaxException e1) {
				System.out.println("Browse to download site failed: bad URI syntax");
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "Failed to browse to the download site...",
						"Well, this is awkward.", JOptionPane.ERROR_MESSAGE);
			} finally {
				verFile.delete();
			}
		} else
			System.err.println("Update check failed: downloaded file doesn't exist");
		if (disposeOfLoadFrame) {
			loadFrame.dispose();
			return null;
		}
		return loadFrame;
	}

	public static void main(String[] args) {
		if (GraphicsEnvironment.isHeadless()) {
			System.out.println("Headless mode is enabled!\nCaveSaveEdit cannot run in headless mode!");
			System.exit(0);
		}
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
		File log = new File("cse.log");
		if (log.exists())
			log.delete();
		try {
			log.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileOutputStream logOut = null;
		try {
			logOut = new FileOutputStream(log);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		try {
			System.setOut(new FNCPrintStream(logOut, false));
			System.setErr(new FNCPrintStream(logOut, true));
		} catch (FileNotFoundException e1) {
			System.exit(1);
		}
		Config.init();
		final String nolaf = "nolaf";
		if (new File(System.getProperty("user.dir") + "/" + nolaf).exists())
			System.out.println("No L&F file detected, skipping setting Look & Feel");
		else
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
		try {
			Resources.loadWindow();
		} catch (Exception e) {
			resourceError(e);
		}
		LoadFrame loadFrame;
		final String skipuc = "skipuc";
		boolean skipucF = new File(System.getProperty("user.dir") + "/" + skipuc).exists();
		boolean skipucR = Config.getBoolean(Config.KEY_SKIP_UPDATE_CHECK, false);
		if (skipucR) {
			Config.setBoolean(Config.KEY_SKIP_UPDATE_CHECK, false);
			skipucF = skipucR;
		}
		if (skipucF) {
			System.out.println("Update check: skip file detected, skipping");
			loadFrame = new LoadFrame();
		} else {
			loadFrame = updateCheck(false, false);
		}
		SwingUtilities.invokeLater(() -> {
			loadFrame.setLoadString("Loading...");
			loadFrame.repaint();
		});
		lineColor = Config.getColor(Config.KEY_LINE_COLOR, Color.white);
		ExeData.setEncoding(Config.get(Config.KEY_ENCODING, StrTools.DEFAULT_ENCODING));
		ExeData.setLoadNpc(Config.getBoolean(Config.KEY_LOAD_NPCS, true));
		// Profile.setNoUndo(false);
		try {
			Resources.loadUI();
			Resources.colorImages(lineColor);
			MCI.readDefault();
		} catch (Exception e) {
			resourceError(e);
		}
		SwingUtilities.invokeLater(() -> {
			window = new Main();
			window.initPanel();
			loadFrame.dispose();
			window.setVisible(true);
			window.requestFocus();
			SwingUtilities.invokeLater(() -> {
				File p = new File(System.getProperty("user.dir") + "/Profile.dat");
				if (p.exists())
					loadProfile(p);
			});
		});
	}

	@Override
	public void onChange(String field, int id, Object oldValue, Object newValue) {
		setTitle(this);
	}

	@Override
	public void preLoad(boolean plusMode) {
		if (plusMode) {
			try {
				MCI.readPlus();
			} catch (Exception e) {
				resourceError(e);
			}
		} else if (MCI.isPlus()) {
			try {
				MCI.readDefault();
			} catch (Exception e) {
				resourceError(e);
			}
		}
	}

	@Override
	public void load(boolean plusMode) {
	}

	@Override
	public void postLoad(boolean plusMode) {
		try {
			ProfileManager.reload();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void unload() {
	}

}
