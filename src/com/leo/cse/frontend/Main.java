package com.leo.cse.frontend;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Supplier;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.profile.Profile;
import com.leo.cse.backend.profile.ProfileChangeListener;
import com.leo.cse.frontend.ui.SaveEditorPanel;

public class Main extends JFrame implements ProfileChangeListener {

	private static final long serialVersionUID = -5073541927297432013L;

	public static final Dimension WINDOW_SIZE = new Dimension(867, 682 + 33);
	public static final Version VERSION = new Version("1.0.5");
	public static final String UPDATE_CHECK_SITE = "https://raw.githubusercontent.com/Leo40Git/CaveSaveEdit/master/.version";
	public static final String DOWNLOAD_SITE = "http://www.purple.com/";
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
	}

	private void initPanel() {
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
			// unload existing exe
			ExeData.unload();
			// try to load exe
			try {
				ExeData.load(new File(file.getAbsoluteFile().getParent() + "/" + MCI.get("Game.ExeName") + ".exe"));
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("EXE loading failed.");
			}
			try {
				Profile.read(file);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(Main.window,
						"An error occured while loading the profile file:\n" + e.getMessage(),
						"Could not load profile file!", JOptionPane.ERROR_MESSAGE);
				return;
			} finally {
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

	private static class MyPrintStream extends PrintStream {

		private PrintStream consoleOut;

		public MyPrintStream(File file, boolean err) throws FileNotFoundException {
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

	private static class Loading extends JFrame {
		private static final long serialVersionUID = 1L;

		private String loadString = "Checking for updates...";

		public void setLoadString(String loadString) {
			this.loadString = loadString;
		}

		public Loading() {
			final Dimension win = new Dimension(200, 80);
			setPreferredSize(win);
			setMaximumSize(win);
			setMinimumSize(win);
			setUndecorated(true);
			add(new JPanel() {
				private static final long serialVersionUID = 1L;

				@Override
				protected void paintComponent(Graphics g) {
					Graphics2D g2d = (Graphics2D) g;
					g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					g2d.setColor(COLOR_BG);
					g2d.fillRect(0, 0, win.width, win.height);
					g2d.setColor(Color.white);
					g2d.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
					FrontUtils.drawStringCentered(g2d, loadString, win.width / 2, win.height / 2, true);
				}
			});
			pack();
			setLocationRelativeTo(null);
			setVisible(true);
		}
	}

	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
		File log = new File("cse.log");
		if (log.exists())
			log.delete();
		try {
			log.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			System.setOut(new MyPrintStream(log, false));
			System.setErr(new MyPrintStream(log, true));
		} catch (FileNotFoundException e1) {
			System.exit(1);
		}
		FrontUtils.initSwing();
		Loading loadFrame = new Loading();
		File verFile = new File(System.getProperty("user.dir") + "/temp.version");
		boolean downloadFailed = false;
		try {
			FrontUtils.downloadFile(UPDATE_CHECK_SITE, verFile);
		} catch (IOException e1) {
			System.err.println("Update check failed: attempt to download caused exception");
			e1.printStackTrace();
			downloadFailed = true;
		}
		if (!downloadFailed) {
			if (verFile.exists()) {
				try (FileReader fr = new FileReader(verFile); BufferedReader reader = new BufferedReader(fr)) {
					Version check = new Version(reader.readLine());
					if (VERSION.compareTo(check) < 0) {
						System.out.println("Update check successful: have update");
						int result = JOptionPane.showConfirmDialog(null, "A new update is available: " + check
								+ "\nClick \"Yes\" to go to the download site, click \"No\" to continue to the save editor.",
								"New update!", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
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
					}
				} catch (IOException e) {
					System.err.println("Update check failed: attempt to read downloaded file caused exception");
					e.printStackTrace();
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
		}
		SwingUtilities.invokeLater(() -> {
			loadFrame.setLoadString("Loading...");
			loadFrame.repaint();
		});
		Config.init();
		lineColor = Config.getColor(Config.KEY_LINE_COLOR, Color.white);
		encoding = Config.get(Config.KEY_ENCODING, "Cp943C");
		/// TODO Fix NPCs
		// ExeData.setLoadNpc(Config.getBoolean(Config.KEY_LOAD_NPCS, true));
		ExeData.setLoadNpc(false);
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
			window.initPanel();
			loadFrame.dispose();
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
	}

}
