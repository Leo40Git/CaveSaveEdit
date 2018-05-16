package com.leo.cse.backend;

public class BackendLogger {

	public interface IBackendLogger {
		public void trace(String message, Throwable t);

		public void info(String message, Throwable t);

		public void warn(String message, Throwable t);

		public void error(String message, Throwable t);

		public void fatal(String message, Throwable t);

		public default void trace(String message) {
			trace(message, null);
		}

		public default void info(String message) {
			info(message, null);
		}

		public default void warn(String message) {
			warn(message, null);
		}

		public default void error(String message) {
			error(message, null);
		}

		public default void fatal(String message) {
			fatal(message, null);
		}
	}

	private static class NullBackendLogger implements IBackendLogger {

		public static final NullBackendLogger INSTANCE = new NullBackendLogger();

		@Override
		public void trace(String message, Throwable t) {
		}

		@Override
		public void info(String message, Throwable t) {
		}

		@Override
		public void warn(String message, Throwable t) {
		}

		@Override
		public void error(String message, Throwable t) {
		}

		@Override
		public void fatal(String message, Throwable t) {
		}

	}

	private static IBackendLogger impl = NullBackendLogger.INSTANCE;

	public static void setImpl(IBackendLogger impl) {
		if (impl == null)
			impl = NullBackendLogger.INSTANCE;
		BackendLogger.impl = impl;
	}

	public static void trace(String message, Throwable t) {
		impl.trace(message, t);
	}

	public static void info(String message, Throwable t) {
		impl.info(message, t);
	}

	public static void warn(String message, Throwable t) {
		impl.warn(message, t);
	}

	public static void error(String message, Throwable t) {
		impl.error(message, t);
	}

	public static void fatal(String message, Throwable t) {
		impl.trace(message, t);
	}

	public static void trace(String message) {
		impl.trace(message);
	}

	public static void info(String message) {
		impl.info(message);
	}

	public static void warn(String message) {
		impl.warn(message);
	}

	public static void error(String message) {
		impl.error(message);
	}

	public static void fatal(String message) {
		impl.trace(message);
	}

}
