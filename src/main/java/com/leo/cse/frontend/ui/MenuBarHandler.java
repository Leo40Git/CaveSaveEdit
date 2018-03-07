package com.leo.cse.frontend.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.leo.cse.frontend.Resources;

public class MenuBarHandler implements ActionListener {

	private boolean plusMode;

	// TODO Code menu bar functions
	public MenuBarHandler(JFrame window) {
		JMenuBar mb = new JMenuBar();
		addFileMenu(mb);
		// TODO Other menus
		window.setJMenuBar(mb);
	}

	private JMenuItem mFile_ChangeFile, mFile_UnloadProfile, mFile_RunExe, mFile_UnloadExe, mFile_Save, mFile_SaveAs;

	private void addFileMenu(JMenuBar mb) {
		JMenu mFile = new JMenu("File");
		JMenuItem mFileItem;
		mFileItem = new JMenuItem("New Profile");
		mFileItem.addActionListener(this);
		mFileItem.setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.CTRL_DOWN_MASK));
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.NEW_PROFILE));
		mFile.add(mFileItem);
		mFileItem = new JMenuItem("Load Profile");
		mFileItem.addActionListener(this);
		mFileItem.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.LOAD_PROFILE));
		mFile.add(mFileItem);
		mFile_ChangeFile = mFileItem = new JMenuItem("Change File");
		mFileItem.addActionListener(this);
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.PLUS_CHANGE_FILE));
		mFileItem.setEnabled(false);
		mFile.add(mFileItem);
		mFile_UnloadProfile = mFileItem = new JMenuItem("Unload Profile");
		mFileItem.addActionListener(this);
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.UNLOAD_PROFILE));
		mFileItem.setEnabled(false);
		mFile.add(mFileItem);
		mFileItem = new JMenuItem("Load Game/Mod");
		mFileItem.addActionListener(this);
		mFileItem.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.LOAD_EXE));
		mFile.add(mFileItem);
		mFile_RunExe = mFileItem = new JMenuItem("Run Game/Mod");
		mFileItem.addActionListener(this);
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.RUN_EXE));
		mFileItem.setEnabled(false);
		mFile.add(mFileItem);
		mFile_UnloadExe = mFileItem = new JMenuItem("Unload Game/Mod");
		mFileItem.addActionListener(this);
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.UNLOAD_EXE));
		mFileItem.setEnabled(false);
		mFile.add(mFileItem);
		mFile_Save = mFileItem = new JMenuItem("Save");
		mFileItem.addActionListener(this);
		mFileItem.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.SAVE));
		mFileItem.setEnabled(false);
		mFile.add(mFileItem);
		mFile_SaveAs = mFileItem = new JMenuItem("Save As...");
		mFileItem.addActionListener(this);
		mFileItem.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.SAVE_AS));
		mFileItem.setEnabled(false);
		mFile.add(mFileItem);
		mFileItem = new JMenuItem("Settings");
		mFileItem.addActionListener(this);
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.SETTINGS));
		mFile.add(mFileItem);
		mFileItem = new JMenuItem("About");
		mFileItem.addActionListener(this);
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.ABOUT));
		mFile.add(mFileItem);
		mFileItem = new JMenuItem("Quit");
		mFileItem.addActionListener(this);
		mFileItem.setIcon(Resources.getIcon(Resources.Icon.QUIT));
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
	}

}
