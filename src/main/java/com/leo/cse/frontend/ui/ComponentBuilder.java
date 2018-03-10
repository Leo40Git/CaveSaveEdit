package com.leo.cse.frontend.ui;

import javax.swing.JComponent;

import com.leo.cse.backend.profile.ProfileManager;

public class ComponentBuilder {

	public static JComponent buildComponentForField(String field) {
		JComponent ret = null;
		@SuppressWarnings("unused")
		boolean multi = ProfileManager.fieldHasIndexes(field);
		Class<?> type = ProfileManager.getFieldType(field);
		if (type == Boolean.class) {
			// TODO
		} else if (type == Byte.class) {
			// TODO
		} else if (type == Short.class) {
			// TODO
		} else if (type == Integer.class) {
			// TODO
		} else if (type == Long.class) {
			// TODO
		} else
			throw new RuntimeException("Can't create component for field of type: " + type);
		return ret;
	}

}
