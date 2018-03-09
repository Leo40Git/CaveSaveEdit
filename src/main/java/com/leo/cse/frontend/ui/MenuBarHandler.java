package com.leo.cse.frontend.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.profile.NormalProfile;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.profile.ProfileManager.ProfileFieldException;
import com.leo.cse.frontend.Config;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class MenuBarHandler implements ActionListener {

	public static final String ACTION_FILE_NEW_PROFILE = "file.new_profile";
	public static final String ACTION_FILE_LOAD_PROFILE = "file.load_profile";
	public static final String ACTION_FILE_CHANGE_SLOT = "file.change_slot";
	public static final String ACTION_FILE_UNLOAD_PROFILE = "file.unload_profile";
	// ---------------
	public static final String ACTION_FILE_LOAD_EXE = "file.load_exe";
	public static final String ACTION_FILE_RUN_EXE = "file.run_exe";
	public static final String ACTION_FILE_UNLOAD_EXE = "file.unload_exe";
	// ---------------
	public static final String ACTION_FILE_SAVE = "file.save";
	public static final String ACTION_FILE_SAVE_AS = "file.save_as";
	// ---------------
	public static final String ACTION_FILE_SETTINGS = "file.settings";
	public static final String ACTION_FILE_ABOUT = "file.about";
	public static final String ACTION_FILE_QUIT = "file.quit";
	// ---------------
	// public static final String ACTION_FILE_GEN_PROF_MAP =
	// "file.generate_profile_map";

	private boolean plusMode;

	// TODO Code menu bar functions
	public MenuBarHandler(JFrame window) {
		JMenuBar mb = new JMenuBar();
		addFileMenu(mb);
		// TODO Other menus
		window.setJMenuBar(mb);
	}

	private void setAction(JMenuItem item, String actionCmd) {
		item.addActionListener(this);
		item.setActionCommand(actionCmd);
	}

	private JMenuItem mFile_ChangeSlot, mFile_UnloadProfile, mFile_RunExe, mFile_UnloadExe, mFile_Save, mFile_SaveAs;

	private void addFileMenu(JMenuBar mb) {
		JMenu mFile = new JMenu("File");
		JMenuItem mFileItem;
		mFileItem = new JMenuItem("New Profile");
		setAction(mFileItem, ACTION_FILE_NEW_PROFILE);
		mFileItem.setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.CTRL_DOWN_MASK));
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.NEW_PROFILE));
		mFile.add(mFileItem);
		mFileItem = new JMenuItem("Load Profile");
		setAction(mFileItem, ACTION_FILE_LOAD_PROFILE);
		mFileItem.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.LOAD_PROFILE));
		mFile.add(mFileItem);
		mFile_ChangeSlot = mFileItem = new JMenuItem("Change Slot");
		setAction(mFileItem, ACTION_FILE_CHANGE_SLOT);
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.PLUS_CHANGE_SLOT));
		mFileItem.setEnabled(false);
		mFile.add(mFileItem);
		mFile_UnloadProfile = mFileItem = new JMenuItem("Unload Profile");
		setAction(mFileItem, ACTION_FILE_UNLOAD_PROFILE);
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.UNLOAD_PROFILE));
		mFileItem.setEnabled(false);
		mFile.add(mFileItem);
		mFile.addSeparator();

		// ---------------

		mFileItem = new JMenuItem("Load Game/Mod");
		setAction(mFileItem, ACTION_FILE_LOAD_EXE);
		mFileItem.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.LOAD_EXE));
		mFile.add(mFileItem);
		mFile_RunExe = mFileItem = new JMenuItem("Run Game/Mod");
		setAction(mFileItem, ACTION_FILE_RUN_EXE);
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.RUN_EXE));
		mFileItem.setEnabled(false);
		mFile.add(mFileItem);
		mFile_UnloadExe = mFileItem = new JMenuItem("Unload Game/Mod");
		setAction(mFileItem, ACTION_FILE_UNLOAD_EXE);
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.UNLOAD_EXE));
		mFileItem.setEnabled(false);
		mFile.add(mFileItem);
		mFile.addSeparator();

		// ---------------

		mFile_Save = mFileItem = new JMenuItem("Save");
		setAction(mFileItem, ACTION_FILE_SAVE);
		mFileItem.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.SAVE));
		mFileItem.setEnabled(false);
		mFile.add(mFileItem);
		mFile_SaveAs = mFileItem = new JMenuItem("Save As...");
		setAction(mFileItem, ACTION_FILE_SAVE_AS);
		mFileItem.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.SAVE_AS));
		mFileItem.setEnabled(false);
		mFile.add(mFileItem);
		mFile.addSeparator();

		// ---------------

		mFileItem = new JMenuItem("Settings");
		setAction(mFileItem, ACTION_FILE_SETTINGS);
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.SETTINGS));
		mFile.add(mFileItem);
		mFileItem = new JMenuItem("About");
		setAction(mFileItem, ACTION_FILE_ABOUT);
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.ABOUT));
		mFile.add(mFileItem);
		mFileItem = new JMenuItem("Quit");
		setAction(mFileItem, ACTION_FILE_QUIT);
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.QUIT));
		mFile.add(mFileItem);
		/*
		mFile.addSeparator();
		
		// ---------------
		
		mFileItem = new JMenuItem("Generate profile map");
		setAction(mFileItem, ACTION_FILE_GEN_PROF_MAP);
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.EMPTY));
		mFile.add(mFileItem);
		*/
		mb.add(mFile);
	}

	public void setProfileLoaded(boolean profileLoaded) {
		mFile_ChangeSlot.setEnabled(profileLoaded && plusMode);
		mFile_UnloadProfile.setEnabled(profileLoaded);
		mFile_Save.setEnabled(profileLoaded);
		mFile_SaveAs.setEnabled(profileLoaded);
	}

	public void setExeLoaded(boolean exeLoaded) {
		mFile_RunExe.setEnabled(exeLoaded);
		mFile_UnloadExe.setEnabled(exeLoaded);
	}

	public void setPlusMode(boolean plusMode) {
		this.plusMode = plusMode;
		mFile_ChangeSlot.setEnabled(plusMode);
	}

	private static final FileFilter[] MOD_FILE_FILTERS;

	static {
		MOD_FILE_FILTERS = new FileFilter[2];
		MOD_FILE_FILTERS[0] = new FileNameExtensionFilter("Executables", "exe");
		MOD_FILE_FILTERS[1] = new FileNameExtensionFilter("CS+ stage.tbl", "tbl");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		File dir;
		int returnVal;
		switch (e.getActionCommand()) {
		// File Menu
		case ACTION_FILE_NEW_PROFILE:
			if (ProfileManager.isLoaded() && ProfileManager.isModified()) {
				int sel = JOptionPane.showConfirmDialog(Main.window,
						"Are you sure you want to create a new profile?\nUnsaved changes will be lost!",
						"Unsaved changes detected", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if (sel == JOptionPane.CANCEL_OPTION)
					break;
			}
			ProfileManager.create();
			break;
		case ACTION_FILE_LOAD_PROFILE:
			if (ProfileManager.isLoaded() && ProfileManager.isModified()) {
				int sel = JOptionPane.showConfirmDialog(Main.window,
						"Are you sure you want to load a new profile?\nUnsaved changes will be lost!",
						"Unsaved changes detected", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if (sel == JOptionPane.CANCEL_OPTION)
					break;
			}
			dir = new File(Config.get(Config.KEY_LAST_PROFILE, System.getProperty("user.dir")));
			if (!dir.exists())
				dir = new File(System.getProperty("user.dir"));
			returnVal = FrontUtils.openFileChooser("Open profile", new FileNameExtensionFilter("Profile Files", "dat"),
					dir, true, false);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				Main.loadProfile(FrontUtils.getSelectedFile());
			}
			break;
		case ACTION_FILE_CHANGE_SLOT:
			// TODO Create change slot dialog
			break;
		case ACTION_FILE_UNLOAD_PROFILE:
			if (ProfileManager.isLoaded() && ProfileManager.isModified()) {
				int sel = JOptionPane.showConfirmDialog(Main.window,
						"Are you sure you want to unload the profile?\nUnsaved changes will be lost!",
						"Unsaved changes detected", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if (sel == JOptionPane.CANCEL_OPTION)
					break;
			}
			ProfileManager.unload();
			break;
		// ---------------
		case ACTION_FILE_LOAD_EXE:
			int type = 0;
			dir = new File(Config.get(Config.KEY_LAST_MOD, System.getProperty("user.dir")));
			if (!dir.exists())
				dir = new File(System.getProperty("user.dir"));
			if (dir.getAbsolutePath().endsWith(".tbl"))
				type = 1;
			returnVal = FrontUtils.openFileChooser("Open game/mod", MOD_FILE_FILTERS, type, dir, false, false);
			if (returnVal == JFileChooser.APPROVE_OPTION)
				Main.loadExe(FrontUtils.getSelectedFile());
			break;
		case ACTION_FILE_RUN_EXE:
			if (ExeData.isPlusMode()) {
				// Launch CS+ via Steam
				String path = System.getenv("programfiles(x86)");
				if (path == null) {
					path = System.getenv("programfiles");
				}
				try {
					Runtime.getRuntime().exec(path + "/Steam/Steam.exe -applaunch 200900");
				} catch (IOException ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(Main.window,
							"Could not run game! The following exception occured:\n" + ex, "Could not run game",
							JOptionPane.ERROR_MESSAGE);
				}
			} else {
				// Freeware Cave Story, just run the exe
				try {
					Runtime.getRuntime().exec(ExeData.getBase().getAbsolutePath());
				} catch (IOException ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(Main.window,
							"Could not run game! The following exception occured:\n" + ex, "Could not run game",
							JOptionPane.ERROR_MESSAGE);
				}
			}
			break;
		case ACTION_FILE_UNLOAD_EXE:
			ExeData.unload();
			break;
		// ---------------
		case ACTION_FILE_SAVE:
			if (!canSave())
				break;
			if (ProfileManager.getLoadedFile() != null) {
				setSavedFlag();
				try {
					ProfileManager.save();
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(Main.window, "An error occured while saving the profile file:\n" + e1,
							"Could not save profile file!", JOptionPane.ERROR_MESSAGE);
				}
				break;
			}
		// fallthrough to ACTION_FILE_SAVE_AS ("Save As...")
		case ACTION_FILE_SAVE_AS:
			if (!canSave())
				return;
			returnVal = FrontUtils.openFileChooser("Save profile", new FileNameExtensionFilter("Profile Files", "dat"),
					new File(Config.get(Config.KEY_LAST_PROFILE, System.getProperty("user.dir"))), false, true);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = FrontUtils.getSelectedFile();
				if (file.exists()) {
					int confirmVal = JOptionPane.showConfirmDialog(Main.window,
							"Are you sure you want to overwrite this file?", "Overwrite confirmation",
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if (confirmVal != JOptionPane.YES_OPTION)
						return;
				}
				setSavedFlag();
				try {
					ProfileManager.save(file);
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(Main.window, "An error occured while saving the profile file:\n" + e1,
							"Could not save profile file!", JOptionPane.ERROR_MESSAGE);
					return;
				} finally {
					Config.set(Config.KEY_LAST_PROFILE, file.getAbsolutePath());
				}
			}
			break;
		// ---------------
		case ACTION_FILE_SETTINGS:
			// TODO Create settings dialog
			break;
		case ACTION_FILE_ABOUT:
			// TODO Create about dialog
			break;
		case ACTION_FILE_QUIT:
			Main.close();
			break;
		/*
		// ---------------
		case ACTION_FILE_GEN_PROF_MAP:
			generateProfileMap(NormalProfile.class, new File("normal.pmp"));
			generateProfileMap(PlusProfile.class, new File("plus.pmp"));
			break;
		*/
		default:
			break;
		}
	}

	private void setSavedFlag() {
		// force save flag to be on
		try {
			ProfileManager.setField(NormalProfile.FIELD_FLAGS, MCI.getInteger("Flag.SaveID", 431), true);
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	private boolean canSave() {
		if (!ProfileManager.isLoaded()) {
			JOptionPane.showMessageDialog(Main.window, "There is no profile to save!\nPlease load a profile.",
					"No profile to save", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	/*
	private void generateProfileMap(Class<? extends Profile> classToMap, File output) {
		ProfileManager.unload();
		ProfileManager.setClass(classToMap.getName());
		ProfileManager.create();
		List<String> fields = ProfileManager.getAllFields();
		try (FileWriter fw = new FileWriter(output); BufferedWriter bw = new BufferedWriter(fw);) {
			bw.write("Map for " + classToMap.getName() + ":");
			bw.newLine();
			for (String field : fields) {
				String out = field;
				if (ProfileManager.fieldHasIndexes(field)) {
					out += "[min=" + ProfileManager.getFieldMinimumIndex(field) + ",max="
							+ ProfileManager.getFieldMaximumIndex(field) + "]";
				}
				Class<?> type = ProfileManager.getFieldType(field);
				if (type.isArray())
					out += ":" + type.getComponentType().getSimpleName() + "[]";
				else
					out += ":" + type.getSimpleName();
				bw.write(out);
				bw.newLine();
			}
		} catch (IOException | ProfileFieldException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(Main.window,
					"An error occured while generating the profile map for " + classToMap.getSimpleName() + ":\n" + e,
					"Profile map generation failed!", JOptionPane.ERROR_MESSAGE);
		}
		ProfileManager.unload();
		JOptionPane.showMessageDialog(Main.window,
				"Profile map for " + classToMap.getSimpleName() + " generated successfully!\nOutputted to:\n"
						+ output.getAbsolutePath(),
				"Profile map generation successful!", JOptionPane.INFORMATION_MESSAGE);
	}
	*/

}
