package com.leo.cse.frontend.ui.panels;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.github.lgooddatepicker.components.DateTimePicker;
import com.leo.cse.frontend.ui.components.MapView;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import java.awt.Color;

public class GeneralPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JComboBox<String> cbMap;
	private JComboBox<String> cbSong;
	private JLabel lblPosition;
	private JLabel labelPosSep;
	private JLabel lblDirection;
	private JRadioButton rdbtnDirL;
	private JRadioButton rdbtnDirR;
	private final ButtonGroup btgDirection = new ButtonGroup();
	private JSpinner spnHealthCur;
	private JSpinner spnHealthMax;
	private DateTimePicker dtpModifyDate;
	private JRadioButton rdbtnDiffO;
	private JRadioButton rdbtnDiffE;
	private JRadioButton rdbtnDiffH;
	private final ButtonGroup btgDifficulty = new ButtonGroup();
	private JCheckBox chkBeatHell;
	private MapView mapView;
	private JPanel pnlPlusOnly;

	/**
	 * Create the panel.
	 */
	public GeneralPanel() {
		setLayout(null);

		JLabel lblMap = new JLabel("Map:");
		lblMap.setBounds(10, 14, 46, 14);
		add(lblMap);

		JLabel lblSong = new JLabel("Song:");
		lblSong.setBounds(10, 45, 46, 14);
		add(lblSong);

		cbMap = new JComboBox<>();
		cbMap.setEnabled(false);
		cbMap.setBounds(66, 11, 320, 20);
		add(cbMap);

		cbSong = new JComboBox<>();
		cbSong.setEnabled(false);
		cbSong.setBounds(66, 42, 320, 20);
		add(cbSong);

		lblPosition = new JLabel("Position:");
		lblPosition.setBounds(10, 76, 46, 14);
		add(lblPosition);

		JSpinner spnPosX = new JSpinner();
		spnPosX.setEnabled(false);
		spnPosX.setModel(
				new SpinnerNumberModel(new Short((short) 0), new Short((short) 0), null, new Short((short) 1)));
		spnPosX.setBounds(66, 73, 80, 20);
		add(spnPosX);

		JSpinner spnPosY = new JSpinner();
		spnPosY.setEnabled(false);
		spnPosY.setBounds(166, 73, 80, 20);
		add(spnPosY);

		labelPosSep = new JLabel(",");
		labelPosSep.setBounds(156, 76, 14, 14);
		add(labelPosSep);

		lblDirection = new JLabel("Direction:");
		lblDirection.setBounds(10, 101, 46, 14);
		add(lblDirection);

		rdbtnDirL = new JRadioButton("Left");
		rdbtnDirL.setEnabled(false);
		btgDirection.add(rdbtnDirL);
		rdbtnDirL.setBounds(66, 97, 60, 23);
		add(rdbtnDirL);

		rdbtnDirR = new JRadioButton("Right");
		rdbtnDirR.setEnabled(false);
		btgDirection.add(rdbtnDirR);
		rdbtnDirR.setBounds(128, 97, 60, 23);
		add(rdbtnDirR);

		JLabel lblHealth = new JLabel("Health:");
		lblHealth.setBounds(10, 130, 46, 14);
		add(lblHealth);

		spnHealthCur = new JSpinner();
		spnHealthCur.setEnabled(false);
		spnHealthCur.setBounds(66, 127, 80, 20);
		add(spnHealthCur);

		spnHealthMax = new JSpinner();
		spnHealthMax.setEnabled(false);
		spnHealthMax.setBounds(166, 127, 80, 20);
		add(spnHealthMax);

		JLabel labelHealthSep = new JLabel("/");
		labelHealthSep.setBounds(156, 130, 14, 14);
		add(labelHealthSep);

		JLabel lblTimePlayed = new JLabel("Time Played:");
		lblTimePlayed.setBounds(10, 161, 70, 14);
		add(lblTimePlayed);

		JSpinner spinner = new JSpinner();
		spinner.setEnabled(false);
		spinner.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		spinner.setBounds(90, 158, 160, 20);
		add(spinner);
		
		pnlPlusOnly = new JPanel();
		pnlPlusOnly.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Cave Story+ Only", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		pnlPlusOnly.setBounds(679, 14, 309, 107);
		add(pnlPlusOnly);
		pnlPlusOnly.setLayout(null);
		
		dtpModifyDate = new DateTimePicker();
		dtpModifyDate.setEnabled(false);
		dtpModifyDate.setBounds(82, 16, 221, 23);
		pnlPlusOnly.add(dtpModifyDate);
		
		JLabel lblModifyDate = new JLabel("Modify Date:");
		lblModifyDate.setBounds(6, 19, 66, 14);
		pnlPlusOnly.add(lblModifyDate);
		
		rdbtnDiffO = new JRadioButton("Original");
		rdbtnDiffO.setBounds(82, 46, 70, 23);
		pnlPlusOnly.add(rdbtnDiffO);
		btgDifficulty.add(rdbtnDiffO);
		rdbtnDiffO.setEnabled(false);
		
		rdbtnDiffE = new JRadioButton("Easy");
		rdbtnDiffE.setBounds(154, 46, 60, 23);
		pnlPlusOnly.add(rdbtnDiffE);
		btgDifficulty.add(rdbtnDiffE);
		rdbtnDiffE.setEnabled(false);
		
		rdbtnDiffH = new JRadioButton("Hard");
		rdbtnDiffH.setBounds(216, 46, 54, 23);
		pnlPlusOnly.add(rdbtnDiffH);
		btgDifficulty.add(rdbtnDiffH);
		rdbtnDiffH.setEnabled(false);
		
		JLabel lblDifficulty = new JLabel("Difficulty:");
		lblDifficulty.setBounds(22, 50, 46, 14);
		pnlPlusOnly.add(lblDifficulty);
		
		chkBeatHell = new JCheckBox("Beat Bloodstained Sanctuary?");
		chkBeatHell.setBounds(82, 73, 188, 23);
		pnlPlusOnly.add(chkBeatHell);
		chkBeatHell.setEnabled(false);
		
		mapView = new MapView();
		mapView.setEnabled(false);
		mapView.setBounds(192, 186, 640, 480);
		add(mapView);

	}
}
