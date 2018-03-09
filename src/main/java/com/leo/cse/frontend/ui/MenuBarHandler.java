package com.leo.cse.frontend.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import com.leo.cse.backend.profile.NormalProfile;
import com.leo.cse.backend.profile.PlusProfile;
import com.leo.cse.backend.profile.Profile;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.profile.ProfileManager.ProfileFieldException;
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
	public static final String ACTION_FILE_GEN_PROF_MAP = "file.generate_profile_map";

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

	private JMenuItem mFile_ChangeFile, mFile_UnloadProfile, mFile_RunExe, mFile_UnloadExe, mFile_Save, mFile_SaveAs;

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
		mFile_ChangeFile = mFileItem = new JMenuItem("Change Slot");
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
		mFile.addSeparator();
		mFileItem = new JMenuItem("Generate profile map");
		setAction(mFileItem, ACTION_FILE_GEN_PROF_MAP);
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.EMPTY));
		mFile.add(mFileItem);
		mb.add(mFile);
	}

	public void setProfileLoaded(boolean profileLoaded) {
		mFile_ChangeFile.setEnabled(profileLoaded && plusMode);
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
		mFile_ChangeFile.setEnabled(plusMode);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		// File Menu
		case ACTION_FILE_GEN_PROF_MAP:
			generateProfileMap(NormalProfile.class, new File("normal.pmp"));
			generateProfileMap(PlusProfile.class, new File("plus.pmp"));
			break;
		default:
			break;
		}
	}

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

}
