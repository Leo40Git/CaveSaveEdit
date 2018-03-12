package com.leo.cse.frontend.ui.panels;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import java.awt.SystemColor;
import java.awt.Font;
import javax.swing.SwingConstants;

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
	private JLabel lblMapView;

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

		JPanel pnlMapViewPH = new JPanel();
		pnlMapViewPH.setBackground(SystemColor.controlShadow);
		pnlMapViewPH.setBounds(192, 186, 640, 480);
		add(pnlMapViewPH);
		pnlMapViewPH.setLayout(null);
		
		lblMapView = new JLabel("MapView");
		lblMapView.setVerticalAlignment(SwingConstants.TOP);
		lblMapView.setFont(lblMapView.getFont().deriveFont(lblMapView.getFont().getStyle() | Font.BOLD, 24f));
		lblMapView.setBounds(10, 11, 260, 148);
		pnlMapViewPH.add(lblMapView);

	}
}
