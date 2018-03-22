package com.leo.cse.frontend;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;

import com.leo.cse.backend.StrTools;
import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.exe.ExeLoadListener;
import com.leo.cse.backend.profile.ProfileListener;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.frontend.ui.MenuBarHandler;
import com.leo.cse.frontend.ui.panels.EditorPanel;

public class Main extends JFrame implements ExeLoadListener, ProfileListener {

	private static final long serialVersionUID = -5073541927297432013L;

	public static final Dimension WINDOW_SIZE = new Dimension(1024, 760);
	public static final Version VERSION = new Version("4.0");
	public static final String UPDATE_CHECK_SITE = "https://raw.githubusercontent.com/Leo40Git/CaveSaveEdit/master/.version";
	public static final String DOWNLOAD_SITE = "https://github.com/Leo40Git/CaveSaveEdit/releases/";

	private static ExecutorService exeLoad;
	private static ExeLoadFrame exeLoadFrame;

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
	private static MenuBarHandler windowMBH;

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
			// ProfileManager.removeListener(SaveEditorPanel.panel);
			ProfileManager.unload();
			ExeData.removeListener(window);
			// ExeData.removeListener(SaveEditorPanel.panel);
			ExeData.unload();
			window = null;
			// SaveEditorPanel.panel = null;
			System.gc();
			Main.main(new String[0]);
		} else {
			Config.set(Config.KEY_ENCODING, ExeData.getEncoding());
			// SaveEditorPanel.panel.saveSettings();
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
		setIconImage(Resources.appIcon);
		/*
		setUndecorated(true);
		setBackground(new Color(0, 0, 0, 0));
		*/
	}

	private void initPanel() {
		/*
		SaveEditorPanel sep = new SaveEditorPanel();
		add(sep);
		ProfileManager.addListener(sep);
		ExeData.addListener(sep);
		*/
		windowMBH = new MenuBarHandler(this);
		JTabbedPane tPane = new JTabbedPane();
		setupTabbedPane(tPane);
		add(tPane);
		setMaximumSize(WINDOW_SIZE);
		setMinimumSize(WINDOW_SIZE);
		setPreferredSize(WINDOW_SIZE);
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
	}

	private void setupTabbedPane(JTabbedPane tPane) {
		List<EditorPanel> panels = new ArrayList<>();
		// TODO Add panels
		for (EditorPanel panel : panels)
			tPane.addTab(panel.getTitle(), Resources.getIcon(panel.getIcon()), panel, panel.getTip());
	}

	public static void loadProfile(File file, boolean record) {
		/*
		if (SaveEditorPanel.panel != null)
			SaveEditorPanel.panel.setLoading(true);
		*/
		window.repaint();
		SwingUtilities.invokeLater(() -> {
			if (Config.getBoolean(Config.KEY_AUTOLOAD_EXE, true)) {
				File newExe = new File(file.getAbsoluteFile().getParent() + "/" + MCI.get("Game.ExeName") + ".exe");
				if (newExe.exists())
					loadExe(newExe);
			}
			try {
				ProfileManager.load(file);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Profile loading failed.");
				JOptionPane.showMessageDialog(Main.window, "An error occured while loading the profile file:\n" + e,
						"Could not load profile file!", JOptionPane.ERROR_MESSAGE);
				return;
			} finally {
				System.out.println("loaded profile " + ProfileManager.getLoadedFile());
				if (record)
					Config.set(Config.KEY_LAST_PROFILE, file.getAbsolutePath());
				SwingUtilities.invokeLater(() -> {
					window.repaint();
				});
			}
		});
	}

	public static void loadExe(File exe) {
		if (exeLoad == null)
			exeLoad = Executors.newSingleThreadExecutor();
		exeLoad.submit(() -> loadExeOnCurrentThread(exe));
	}

	public static void loadExeOnCurrentThread(File exe) {
		String tname = Thread.currentThread().getName();
		window.setEnabled(false);
		System.out.println("Starting EXE loading on thread " + tname);
		// unload existing exe
		ExeData.unload();
		// try to load exe
		try {
			ExeData.load(exe);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("EXE loading failed.");
			JOptionPane.showMessageDialog(Main.window, "An error occured while loading the executable:\n" + e,
					"Could not load executable!", JOptionPane.ERROR_MESSAGE);
		}
		window.setEnabled(true);
		window.requestFocus();
		System.out.println("Finished EXE loading on thread " + tname);
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

	public static class InitLoadFrame extends JDialog {

		private static final long serialVersionUID = 1L;

		private JLabel loadLabel;

		public void setLoadString(String loadString) {
			loadLabel.setText(loadString);
		}

		public InitLoadFrame() {
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			setUndecorated(true);
			final Dimension size = new Dimension(320, 120);
			setPreferredSize(size);
			setMaximumSize(size);
			setMinimumSize(size);
			setResizable(false);
			loadLabel = new JLabel("Checking for updates...");
			loadLabel.setFont(loadLabel.getFont().deriveFont(Font.BOLD, 20));
			loadLabel.setHorizontalAlignment(SwingConstants.CENTER);
			loadLabel.setVerticalAlignment(SwingConstants.CENTER);
			loadLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			loadLabel.setOpaque(true);
			add(loadLabel);
			pack();
			setLocationRelativeTo(null);
			setIconImage(Resources.appIcon);
			setVisible(true);
			requestFocus();
		}

	}

	public static class ExeLoadFrame extends JDialog {

		private static final long serialVersionUID = 1L;

		private JLabel loadLabel;
		private int loadProgress, loadTotal;
		private JProgressBar loadProgressBar;
		private JLabel subLabel;
		private int subProgress, subTotal;
		private JProgressBar subProgressBar;

		public void setLoadString(String loadString) {
			loadLabel.setText(loadString);
		}

		public void setLoadProgress(int progress, int total) {
			loadProgress = progress;
			loadTotal = total;
			loadProgressBar.setValue(loadProgress);
			loadProgressBar.setMaximum(loadTotal);
		}

		public void setSubString(String subString) {
			subLabel.setText(subString);
		}

		public void setSubProgress(int progress, int total) {
			subProgress = progress;
			subTotal = total;
			subProgressBar.setValue(subProgress);
			subProgressBar.setMaximum(subTotal);
		}

		public ExeLoadFrame() {
			Font labelFont;
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			setUndecorated(true);
			final Dimension size = new Dimension(480, 80);
			setPreferredSize(size);
			setMaximumSize(size);
			setMinimumSize(size);
			setResizable(false);
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			loadLabel = new JLabel("...");
			loadLabel.setFont(labelFont = loadLabel.getFont().deriveFont(Font.PLAIN, 12));
			panel.add(loadLabel);
			loadProgressBar = new JProgressBar();
			setLoadProgress(0, 1);
			panel.add(loadProgressBar);
			subLabel = new JLabel("...");
			subLabel.setFont(labelFont);
			panel.add(subLabel);
			subProgressBar = new JProgressBar();
			setSubProgress(0, 1);
			panel.add(subProgressBar);
			add(panel);
			pack();
			setLocationRelativeTo(null);
			setIconImage(Resources.appIcon);
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

	public static InitLoadFrame updateCheck(boolean disposeOfLoadFrame, boolean showUpToDate) {
		InitLoadFrame loadFrame = new InitLoadFrame();
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
		InitLoadFrame loadFrame;
		final String skipuc = "skipuc";
		boolean skipucF = new File(System.getProperty("user.dir") + "/" + skipuc).exists();
		boolean skipucR = Config.getBoolean(Config.KEY_SKIP_UPDATE_CHECK, false);
		if (skipucR) {
			Config.setBoolean(Config.KEY_SKIP_UPDATE_CHECK, false);
			skipucF = skipucR;
		}
		if (skipucF) {
			System.out.println("Update check: skip file detected, skipping");
			loadFrame = new InitLoadFrame();
		} else {
			loadFrame = updateCheck(false, false);
		}
		SwingUtilities.invokeLater(() -> {
			loadFrame.setLoadString("Loading...");
			loadFrame.repaint();
		});
		ExeData.setEncoding(Config.get(Config.KEY_ENCODING, StrTools.DEFAULT_ENCODING));
		ExeData.setLoadNpc(Config.getBoolean(Config.KEY_LOAD_NPCS, true));
		// Profile.setNoUndo(false);
		try {
			Resources.loadUI();
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
			/*
			SwingUtilities.invokeLater(() -> {
				File p = new File(System.getProperty("user.dir") + "/Profile.dat");
				if (p.exists())
					loadProfile(p, false);
			});
			*/
		});
	}

	@Override
	public void onChange(String field, int id, Object oldValue, Object newValue) {
		setTitle(this);
		switch (field) {
		case ProfileManager.EVENT_LOAD:
			windowMBH.setProfileLoaded(true);
			break;
		case ProfileManager.EVENT_UNLOAD:
			windowMBH.setProfileLoaded(false);
			break;
		}
	}

	@Override
	public void onEvent(String event, String loadName, int loadId, int loadIdMax) {
		if (exeLoadFrame == null)
			exeLoadFrame = new ExeLoadFrame();
		// System.out.println("EVENT! " + event + "," + loadName + "," + loadId + "," +
		// loadIdMax);
		exeLoadFrame.setLoadProgress(loadId, loadIdMax);
		boolean plusMode = ExeData.isPlusMode();
		switch (event) {
		case ExeData.EVENT_PRELOAD:
			windowMBH.setPlusMode(plusMode);
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
			exeLoadFrame.setLoadString("Preparing to load...");
			break;
		case ExeData.EVENT_EXE_STRING:
			exeLoadFrame.setLoadString("Reading game string no. " + loadId);
			break;
		case ExeData.EVENT_NPC_TBL:
			exeLoadFrame.setLoadString("Reading " + loadName + " for entity " + loadId);
			break;
		case ExeData.EVENT_MAP_DATA:
			exeLoadFrame.setLoadString("Reading data for map " + loadId);
			break;
		case ExeData.EVENT_MAP_INFO:
			exeLoadFrame.setLoadString("Loading " + loadName + " for map " + loadId);
			break;
		case ExeData.EVENT_POSTLOAD:
			try {
				ProfileManager.reload();
			} catch (IOException e) {
				e.printStackTrace();
			}
			exeLoadFrame.dispose();
			exeLoadFrame = null;
			windowMBH.setExeLoaded(true);
			break;
		case ExeData.EVENT_UNLOAD:
			exeLoadFrame.dispose();
			exeLoadFrame = null;
			windowMBH.setExeLoaded(false);
			break;
		default:
			break;
		}
	}

	@Override
	public void onSubevent(String event, String loadName, int loadId, int loadIdMax) {
		exeLoadFrame.setSubProgress(loadId, loadIdMax);
		switch (event) {
		case ExeData.SUBEVENT_IMAGE:
			exeLoadFrame.setSubString("<html>Loading image:<br>" + loadName + "</html>");
			break;
		case ExeData.SUBEVENT_PXA:
			exeLoadFrame.setSubString("<html>Loading PXA file:<br>" + loadName + "</html>");
			break;
		case ExeData.SUBEVENT_END:
		default:
			break;
		}
	}

}
