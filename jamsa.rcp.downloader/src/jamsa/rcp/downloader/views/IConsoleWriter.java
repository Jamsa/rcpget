package jamsa.rcp.downloader.views;

/**
 * ��ConsoleViewer�����־�Ľӿ�
 * 
 * @author Jamsa
 * 
 */
public interface IConsoleWriter {
	/**
	 * �����־
	 * 
	 * @param threadName
	 *            ��־��������
	 * @param message
	 *            Ҫ���������
	 */
	public void writeMessage(String threadName, String message);
}
