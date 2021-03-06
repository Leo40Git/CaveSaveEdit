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
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.leo.cse.backend.BackendLogger;
import com.leo.cse.backend.StrTools;
import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.exe.ExeLoadListener;
import com.leo.cse.backend.profile.ProfileListener;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.frontend.ui.SaveEditorPanel;

public class Main extends JFrame implements ExeLoadListener, ProfileListener {

	private static final long serialVersionUID = -5073541927297432013L;

	public static final Logger LOGGER = LogManager.getLogger("CSE");

	public static final Dimension WINDOW_SIZE = new Dimension(870, 734);
	public static final Version VERSION = new Version("4.0.3");
	public static final String UPDATE_CHECK_SITE = "https://raw.githubusercontent.com/Leo40Git/CaveSaveEdit/master/.version";
	public static final String DOWNLOAD_SITE = "https://github.com/Leo40Git/CaveSaveEdit/releases/";
	public static final Color COLOR_BG = new Color(0, 0, 25);
	public static final Color COLOR_BG_B = COLOR_BG.brighter();
	public static final Color COLOR_BG_B2 = COLOR_BG_B.brighter();

	public static final Supplier<Boolean> TRUE_SUPPLIER = () -> true;
	public static final Supplier<Boolean> FALSE_SUPPLIER = () -> false;

	public static Main window;
	public static Color lineColor;

	private static Thread repaintThread;
	private static AtomicBoolean keepRepainting;

	private static class ProfileLoadInstruction {
		public File file;
		public boolean record;

		public ProfileLoadInstruction(File file, boolean record) {
			this.file = file;
			this.record = record;
		}
	}

	private static ProfileLoadInstruction profLoadInstruct = null;

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
					LOGGER.error("Failed to save profile", e);
				}
			else if (sel == JOptionPane.CANCEL_OPTION)
				return;
		} else if (!reboot) {
			int sel = JOptionPane.showConfirmDialog(window, "Are you sure you want to close the editor?",
					"Quit CaveSaveEditor?", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if (sel != JOptionPane.YES_OPTION)
				return;
		}
		keepRepainting.set(false);
		try {
			repaintThread.join();
		} catch (InterruptedException e) {
			LOGGER.error("Repaint thread kill interrupted", e);
		}
		if (reboot) {
			window.dispose();
			ProfileManager.removeAllListeners();
			ProfileManager.unload();
			ExeData.removeAllListeners();
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
		setIconImages(Resources.appIcons);
	}

	private void initPanel() {
		SaveEditorPanel sep = new SaveEditorPanel();
		add(sep);
		addKeyListener(sep);
		addMouseListener(sep);
		addMouseMotionListener(sep);
		addMouseWheelListener(sep);
		ProfileManager.addListener(sep);
		ExeData.addListener(sep);
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

	public static void loadExe(File file, boolean record) {
		if (SaveEditorPanel.panel != null)
			SaveEditorPanel.panel.setLoading(true);
		window.repaint();
		Thread exeLoadThread = new Thread(() -> {
			boolean succ = true;
			try {
				ExeData.load(file);
			} catch (Exception e) {
				LOGGER.error("Executable loading failed.", e);
				JOptionPane.showMessageDialog(Main.window, "An error occured while loading the executable:\n" + e,
						"Could not load executable!", JOptionPane.ERROR_MESSAGE);
				succ = false;
			}
			if (succ) {
				LOGGER.info("Loaded executable: " + ExeData.getBase());
				if (record)
					Config.set(Config.KEY_LAST_MOD, file.getAbsolutePath());
				String pExt = ExeData.getExeString(ExeData.STRING_PROFILE_NAME);
				int pExtD = pExt.lastIndexOf('.');
				if (pExtD > -1) {
					pExt = pExt.substring(pExtD + 1, pExt.length());
					SaveEditorPanel.panel.setProfileExt(pExt);
				}
				SwingUtilities.invokeLater(() -> {
					window.repaint();
				});
			}
			try {
				Thread.currentThread().join();
			} catch (InterruptedException e) {
				LOGGER.error("EXE loading thread suicide interrupted", e);
			}
		}, "ExeLoad");
		exeLoadThread.start();
	}

	public static void loadExe(File file) {
		loadExe(file, true);
	}

	private static void loadProfile0(File file, boolean record) {
		try {
			ProfileManager.load(file);
		} catch (Exception e) {
			LOGGER.error("Profile loading failed.", e);
			JOptionPane.showMessageDialog(Main.window, "An error occured while loading the profile file:\n" + e,
					"Could not load profile file!", JOptionPane.ERROR_MESSAGE);
			return;
		} finally {
			LOGGER.info("Loaded profile " + ProfileManager.getLoadedFile());
			if (record)
				Config.set(Config.KEY_LAST_PROFILE, file.getAbsolutePath());
			SwingUtilities.invokeLater(() -> {
				window.repaint();
			});
		}
	}

	public static void loadProfile(File file, boolean record) {
		if (SaveEditorPanel.panel != null)
			SaveEditorPanel.panel.setLoading(true);
		window.repaint();
		SwingUtilities.invokeLater(() -> {
			if (Config.getBoolean(Config.KEY_AUTOLOAD_EXE, true)) {
				profLoadInstruct = new ProfileLoadInstruction(file, record);
				File newExe = new File(file.getAbsoluteFile().getParent() + "/" + MCI.get("Game.ExeName") + ".exe");
				if (newExe.exists()) {
					// unload existing exe
					ExeData.unload();
					// try to load exe
					loadExe(newExe, false);
				}
			} else
				loadProfile0(file, record);
		});
	}

	public static void loadProfile(File file) {
		loadProfile(file, true);
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
				size = win;
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
			setIconImages(Resources.appIcons);
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
		LOGGER.error("Could not load resources", e);
		JOptionPane.showMessageDialog(null,
				"Could not load resources!\nPlease report this error to the programmer.\nAn exception has occured:\n"
						+ e,
				"Could not load resources", JOptionPane.ERROR_MESSAGE);
		System.exit(1);
	}

	public static LoadFrame updateCheck(boolean disposeOfLoadFrame, boolean showUpToDate) {
		LoadFrame loadFrame = new LoadFrame();
		File verFile = new File(System.getProperty("user.dir") + "/temp.version");
		LOGGER.info("Update check: starting");
		try {
			FrontUtils.downloadFile(UPDATE_CHECK_SITE, verFile);
		} catch (IOException e1) {
			LOGGER.info("Update check failed: attempt to download caused exception", e1);
			JOptionPane.showMessageDialog(null, "The update check has failed!\nAre you not connected to the internet?",
					"Update check failed", JOptionPane.ERROR_MESSAGE);
		}
		if (verFile.exists()) {
			LOGGER.info("Update check: reading version");
			try (FileReader fr = new FileReader(verFile); BufferedReader reader = new BufferedReader(fr);) {
				Version check = new Version(reader.readLine());
				if (VERSION.compareTo(check) < 0) {
					LOGGER.info("Update check successful: have update");
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
							try {
								Desktop.getDesktop().browse(dlSite);
							} catch (IOException e) {
								LOGGER.error("Browse to download site failed: I/O error", e);
								JOptionPane.showMessageDialog(null, "Failed to browse to the download site...",
										"Well, this is awkward.", JOptionPane.ERROR_MESSAGE);
							}
						else
							JOptionPane.showMessageDialog(null,
									"Sadly, we can't browse to the download site for you on this platform. :(\nHead to\n"
											+ dlSite + "\nto get the newest update!",
									"Operation not supported...", JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					}
				} else {
					LOGGER.info("Update check successful: up to date");
					if (showUpToDate) {
						JOptionPane.showMessageDialog(null,
								"You are using the most up to date version of CaveSaveEdit! Have fun!", "Up to date!",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			} catch (IOException e) {
				LOGGER.error("Update check failed: attempt to read downloaded file caused exception", e);
				JOptionPane.showMessageDialog(null,
						"The update check has failed!\nAn exception occured while reading update check results:\n" + e,
						"Update check failed", JOptionPane.ERROR_MESSAGE);
			} catch (URISyntaxException e) {
				LOGGER.error("Update check failed: bad URI syntax", e);
			} finally {
				verFile.delete();
			}
		} else
			LOGGER.error("Update check failed: downloaded file doesn't exist");
		if (disposeOfLoadFrame) {
			loadFrame.dispose();
			return null;
		}
		return loadFrame;
	}

	private static class Log4JBackendLogger implements BackendLogger.IBackendLogger {

		private Logger l;

		public Log4JBackendLogger(Logger l) {
			this.l = l;
		}

		@Override
		public void trace(String message, Throwable t) {
			l.trace(message, t);
		}

		@Override
		public void info(String message, Throwable t) {
			l.info(message, t);
		}

		@Override
		public void warn(String message, Throwable t) {
			l.warn(message, t);
		}

		@Override
		public void error(String message, Throwable t) {
			l.error(message, t);
		}

		@Override
		public void fatal(String message, Throwable t) {
			l.fatal(message, t);
		}

	}

	public static void main(String[] args) {
		if (GraphicsEnvironment.isHeadless()) {
			System.out.println("Headless mode is enabled!\nCaveSaveEdit cannot run in headless mode!");
			System.exit(0);
		}
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
		BackendLogger.setImpl(new Log4JBackendLogger(LOGGER));
		Config.init();
		final String nolaf = "nolaf";
		if (new File(System.getProperty("user.dir") + "/" + nolaf).exists())
			LOGGER.trace("No L&F file detected, skipping setting Look & Feel");
		else
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException e) {
				LOGGER.error("Could not set Look & Feel", e);
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
			LOGGER.trace("Update check: skip file detected, skipping");
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
			if (Config.getBoolean(Config.KEY_AUTOLOAD_PROFILE, true))
				SwingUtilities.invokeLater(() -> {
					File p = new File(System.getProperty("user.dir") + "/Profile.dat");
					if (p.exists())
						loadProfile(p, false);
				});
			keepRepainting = new AtomicBoolean(true);
			repaintThread = new Thread(() -> {
				while (keepRepainting.get()) {
					window.repaint();
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						LOGGER.error("Repaint thread sleep interrupted", e);
					}
				}
			}, "repaint");
			repaintThread.start();
		});
	}

	@Override
	public void onChange(String field, int id, Object oldValue, Object newValue) {
		setTitle(this);
	}

	@Override
	public void onEvent(String event, String loadName, int loadId, int loadIdMax) {
		boolean plusMode = ExeData.isPlusMode();
		switch (event) {
		case ExeData.EVENT_PRELOAD:
			if (plusMode)
				try {
					MCI.readPlus();
				} catch (Exception e) {
					resourceError(e);
				}
			else if (MCI.isPlus())
				try {
					MCI.readDefault();
				} catch (Exception e) {
					resourceError(e);
				}
			break;
		case ExeData.EVENT_POSTLOAD:
			if (profLoadInstruct != null) {
				final ProfileLoadInstruction profLoadInstruct2 = profLoadInstruct;
				profLoadInstruct = null;
				SwingUtilities.invokeLater(() -> {
					loadProfile0(profLoadInstruct2.file, profLoadInstruct2.record);
				});
			} else {
				try {
					ProfileManager.reload();
				} catch (IOException e) {
					LOGGER.error("Failed to reload profile", e);
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onSubevent(String event, String loadName, int loadId, int loadIdMax) {
	}

}
