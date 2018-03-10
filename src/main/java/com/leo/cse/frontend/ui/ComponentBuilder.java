package com.leo.cse.frontend.ui;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;

import com.leo.cse.backend.profile.ProfileManager;

/**
 * Utility class for constructing components to modify fields.
 * 
 * @author Leo
 *
 */
public class ComponentBuilder {

	/**
	 * Build component for modifying a field.
	 * 
	 * @param field
	 *            field to modify
	 * @param index
	 *            index to modify
	 * @param title
	 *            title of component (only appliacable if type of field is
	 *            {@link Boolean})
	 * @return component for modifying field
	 */
	public static JComponent buildComponentForField(String field, int index, String title) {
		JComponent ret = null;
		Class<?> type = ProfileManager.getFieldType(field);
		if (type == Boolean.class) {
			boolean state = (Boolean) ProfileManager.getField(field, index);
			JCheckBox chk = new JCheckBox(title, state);
			chk.addActionListener((ActionEvent e) -> {
				ProfileManager.setField(field, index, chk.isSelected());
			});
			ret = chk;
		} else if (type == Byte.class) {
			byte val = (Byte) ProfileManager.getField(field, index);
			JSpinner spn = new JSpinner();
			spn.setModel(new SpinnerNumberModel(val, 0, 0xFF, 1l));
			spn.addChangeListener((ChangeEvent e) -> {
				ProfileManager.setField(field, index, spn.getValue());
			});
			ret = spn;
		} else if (type == Short.class) {
			short val = (Short) ProfileManager.getField(field, index);
			JSpinner spn = new JSpinner();
			spn.setModel(new SpinnerNumberModel(val, 0, 0xFFFF, 1l));
			spn.addChangeListener((ChangeEvent e) -> {
				ProfileManager.setField(field, index, spn.getValue());
			});
			ret = spn;
		} else if (type == Integer.class) {
			int val = (Integer) ProfileManager.getField(field, index);
			JSpinner spn = new JSpinner();
			spn.setModel(new SpinnerNumberModel(val, 0, 0xFFFFFFFF, 1l));
			spn.addChangeListener((ChangeEvent e) -> {
				ProfileManager.setField(field, index, spn.getValue());
			});
			ret = spn;
		} else if (type == Long.class) {
			int val = (Integer) ProfileManager.getField(field, index);
			JSpinner spn = new JSpinner();
			spn.setModel(new SpinnerNumberModel(val, 0, 0xFFFFFFFFFFFFFFFFl, 1l));
			spn.addChangeListener((ChangeEvent e) -> {
				ProfileManager.setField(field, index, spn.getValue());
			});
			ret = spn;
		} else
			throw new RuntimeException("Can't build component for field of type: " + type);
		return ret;
	}

}
