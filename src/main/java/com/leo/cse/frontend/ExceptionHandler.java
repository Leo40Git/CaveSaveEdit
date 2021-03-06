package com.leo.cse.frontend;

import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JOptionPane;

public class ExceptionHandler implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		t.interrupt();
		Main.LOGGER.fatal("Uncaught exception in thread \"" + t.getName() + "\"", e);
		JOptionPane.showMessageDialog(null, "An uncaught exception has occured in thread \"" + t.getName() + "\":\n" + e
				+ "\nPlease send the error log (\"cse.log\") to the developer,\nalong with a description of what you did leading up to the exception.",
				"Uncaught exception!", JOptionPane.ERROR_MESSAGE);
		System.exit(1);
	}

}
