package jamsa.rcp.downloader.views;

/**
 * 向ConsoleViewer输出日志的接口
 * 
 * @author Jamsa
 * 
 */
public interface IConsoleWriter {
	/**
	 * 输出日志
	 * 
	 * @param threadName
	 *            日志分类名称
	 * @param message
	 *            要输出的内容
	 */
	public void writeMessage(String threadName, String message);
}
