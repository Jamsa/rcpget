package jamsa.rcp.downloader.utils;

/**
 * 日志工具类
 * 
 * @author 朱杰
 * 
 */
public class Logger {
	private String className;

	public Logger(Class clazz) {
		this.className = clazz.getName();
	}

	public Logger(String className) {
		this.className = className;
	}

	/**
	 * 默认日志级别
	 */
	private static final int LEVEL = 0;

//	private static final int TRACE = 1;

	private static final int DEBUG = 2;

	private static final int INFO = 3;

	private static final int WARN = 4;

	private static final int ERROR = 5;

	private static final int FATAL = 6;

	// public void trace(Object message) {
	// if (TRACE >= LEVEL)
	// System.out.println("trace: " + className + ":"
	// + String.valueOf(message));
	//	}

	public void debug(Object message) {
		if (DEBUG >= LEVEL)
			System.out.println("debug: " + className + ": "
					+ String.valueOf(message));
	}

	public void info(Object message) {
		if (INFO >= LEVEL)
			System.out.println("info: " + className + ": "
					+ String.valueOf(message));
	}

	public void warn(Object message) {
		if (WARN >= LEVEL)
			System.out.println("warn: " + className + ": "
					+ String.valueOf(message));
	}

	public void warn(Object message, Throwable e) {
		if (WARN >= LEVEL) {
			System.out.println("warn: " + className + ": "
					+ String.valueOf(message));
			System.out.println("warn: " + className + ": " + e.getMessage());
			// e.printStackTrace();
		}
	}

	public void error(Object message) {
		if (ERROR >= LEVEL)
			System.out.println("error: " + className + ": "
					+ String.valueOf(message));
	}

	public void error(Object message, Throwable e) {
		if (ERROR >= LEVEL) {
			System.out.println("error: " + className + ": "
					+ String.valueOf(message));
			e.printStackTrace();
		}
	}

	public void fatal(Object message) {
		if (FATAL >= LEVEL)
			System.out.println("fatal: " + className + ": "
					+ String.valueOf(message));
	}

	public void fatal(Object message, Throwable e) {
		if (FATAL >= LEVEL) {
			System.out.println("fatal: " + className + ": "
					+ String.valueOf(message));
			e.printStackTrace();
		}
	}
}
