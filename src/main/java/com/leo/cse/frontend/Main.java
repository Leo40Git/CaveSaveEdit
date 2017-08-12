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

import com.carrotlord.string.StrTools;
import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.profile.Profile;
import com.leo.cse.backend.profile.ProfileChangeListener;
import com.leo.cse.frontend.ui.SaveEditorPanel;

public class Main extends JFrame implements ProfileChangeListener {

	private static final long serialVersionUID = -5073541927297432013L;

	public static final Dimension WINDOW_SIZE = new Dimension(867, 686);
	public static final Version VERSION = new Version("1.0.7.3");
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
	public static String encoding;

	private static class ConfirmCloseWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			Main.close();
		}
	}

	public static void close() {
		if (Profile.isLoaded() && Profile.isModified()) {
			int sel = JOptionPane.showConfirmDialog(window, "Save profile?", "Unsaved changes detected",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (sel == JOptionPane.YES_OPTION)
				try {
					Profile.write();
				} catch (IOException e) {
					e.printStackTrace();
				}
			else if (sel == JOptionPane.CANCEL_OPTION)
				return;
		} else {
			int sel = JOptionPane.showConfirmDialog(window, "Are you sure you want to close the editor?",
					"Quit CaveSaveEditor?", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if (sel != JOptionPane.YES_OPTION)
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

		public MyPrintStream(File file) throws FileNotFoundException {
			super(file);
			consoleOut = new PrintStream(new FileOutputStream(FileDescriptor.err));
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
			final Dimension win = new Dimension(232, 112);
			setIconImage(Resources.icon);
			setPreferredSize(win);
			setMaximumSize(win);
			setMinimumSize(win);
			setUndecorated(true);
			final Color trans = new Color(0, 0, 0, 0);
			setBackground(trans);
			add(new JPanel() {
				private static final long serialVersionUID = 1L;

				@Override
				protected void paintComponent(Graphics g) {
					Graphics2D g2d = (Graphics2D) g;
					g2d.setBackground(trans);
					g2d.clearRect(0, 0, getWidth(), getHeight());
					FrontUtils.drawNineSlice(g2d, Resources.shadow, 0, 0, getWidth(), getHeight());
					g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					g2d.setColor(COLOR_BG);
					g2d.fillRect(16, 16, win.width - 32, win.height - 32);
					g2d.setColor(Color.white);
					g2d.drawRect(16, 16, win.width - 32, win.height - 32);
					g2d.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
					FrontUtils.drawStringCentered(g2d, loadString, win.width / 2, win.height / 2, true);
				}
			});
			pack();
			setLocationRelativeTo(null);
			setVisible(true);
			requestFocus();
		}
	}

	private static void resourceError(Throwable e) {
		e.printStackTrace();
		JOptionPane.showMessageDialog(null, "Could not load resources!\nPlease report this error to the programmer.",
				"Could not load resources", JOptionPane.ERROR_MESSAGE);
		System.exit(1);
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
		try {
			System.setErr(new MyPrintStream(log));
		} catch (FileNotFoundException e1) {
			System.exit(1);
		}
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
		Loading loadFrame = new Loading();
		File verFile = new File(System.getProperty("user.dir") + "/temp.version");
		final String skipuc = "skipuc";
		if (new File(System.getProperty("user.dir") + "/" + skipuc).exists()) {
			System.out.println("Update check: skip file detected, skipping");
		} else {
			System.out.println("Update check: starting");
			try {
				FrontUtils.downloadFile(UPDATE_CHECK_SITE, verFile);
			} catch (IOException e1) {
				System.err.println("Update check failed: attempt to download caused exception");
				e1.printStackTrace();
			}
			if (verFile.exists()) {
				System.out.println("Update check: reading version");
				try (FileReader fr = new FileReader(verFile); BufferedReader reader = new BufferedReader(fr)) {
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
						int result = JOptionPane.showConfirmDialog(null, panel, "New update!",
								JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
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
		encoding = Config.get(Config.KEY_ENCODING, StrTools.DEFAULT_ENCODING);
		/// TODO Fix NPCs
		// ExeData.setLoadNpc(Config.getBoolean(Config.KEY_LOAD_NPCS, true));
		ExeData.setLoadNpc(false);
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
	public void onChange(String field, int id) {
		setTitle(this);
	}

}
