package jamsa.rcp.downloader.http;

import jamsa.rcp.downloader.views.DefaultConsoleWriter;
import jamsa.rcp.downloader.views.IConsoleWriter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Properties;

public class HttpClientUtils {

	/**
	 * ��ȡԶ���ļ���Ϣ
	 * 
	 * @param urlString
	 * @param retryTimes
	 * @param retryDelay
	 * @param properties
	 * @param writer
	 * @param label
	 * @return
	 * @throws Exception
	 */
	public static RemoteFileInfo getRemoteFileInfo(String urlString,
			int retryTimes, int retryDelay, Properties properties,
			IConsoleWriter writer, String label) {
		if (writer == null)
			writer = new DefaultConsoleWriter();
		HttpURLConnection conn = getHttpURLConnection(urlString, retryTimes,
				retryDelay, properties, writer, label);
		if (conn == null)
			return null;
		return getRemoteSiteFile(conn, writer, label);
	}

	/**
	 * ��ȡԶ���ļ���Ϣ
	 * 
	 * @param conn
	 * @param writer
	 * @param label
	 * @return
	 */
	public static RemoteFileInfo getRemoteSiteFile(HttpURLConnection conn,
			IConsoleWriter writer, String label) {
		if (writer == null)
			writer = new DefaultConsoleWriter();
		RemoteFileInfo result = new RemoteFileInfo();
		URL url = conn.getURL();
		result.setHostname(url.getHost());
		result.setPort(url.getPort());

		String fileName = url.getFile();
		int start = fileName.lastIndexOf("/") + 1;
		int end = fileName.indexOf("?");
		if (end > start)
			result.setFileName(fileName.substring(start, end));
		else
			result.setFileName(fileName.substring(start, fileName.length()));
		// if (fileName.length() > start)

		writer.writeMessage("Task", "Զ���ļ���" + result.getFileName());

		String contentLength = null;
		String header = null;
		for (int i = 1;; i++) {
			header = conn.getHeaderFieldKey(i);
			if (header != null) {
				if (header.equals("Content-Length")) {
					contentLength = conn.getHeaderField(header);
					break;
				}
			} else
				break;
		}
		if (contentLength != null) {
			try {
				result.setFileSize(Long.parseLong(contentLength));
				writer.writeMessage("Task", "Զ���ļ���С" + result.getFileSize());
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	/**
	 * ��ȡԶ��Http����
	 * 
	 * @param urlString
	 *            ���ӵ�ַ
	 * @param retryTimes
	 *            ���Դ���
	 * @param retryDelay
	 *            ������ʱʱ��
	 * @param properties
	 *            ��������
	 * @param writer
	 *            ��־����ӿڣ�Ϊnullʱ�����������̨
	 * @return HttpURLConnection����
	 * @throws Exception
	 *             ������Ӵ������׳�����������쳣
	 */
	public static HttpURLConnection getHttpURLConnection(String urlString,
			int retryTimes, int retryDelay, Properties properties,
			IConsoleWriter writer, String label) {
		if (writer == null)
			writer = new DefaultConsoleWriter();
		if (label == null)
			label = "HttpURLConnection";

		// ���Ӽ�����
		int count = 0;
		HttpURLConnection conn = null;
		try {
			while (conn == null && retryTimes >= count) {
				count++;
				writer.writeMessage(label, "��" + count + "������...");
				URL url = new URL(urlString);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("User-Agent", "Mozilla/5.0");

				if (properties != null && !properties.isEmpty()) {
					for (Iterator it = properties.keySet().iterator(); it
							.hasNext();) {
						String key = (String) it.next();
						conn.setRequestProperty(key, String.valueOf(properties
								.get(key)));
					}
				}

				conn.setRequestMethod("GET");
				int code = conn.getResponseCode();
				printResponseHeader(conn, writer, label);

				// ��鷵����
				if (code == HttpURLConnection.HTTP_BAD_METHOD) {
					// �����������
					conn.setRequestMethod("POST");
					code = conn.getResponseCode();
					printResponseHeader(conn, writer, label);
				}

				// ���Ӵ���ʱ����conn����Ϊnull����������
				if (code >= 400) {
					printResponseHeader(conn, writer, label);
					conn = null;
				}

				// ���̲���������
				if (conn == null) {
					writer.writeMessage(label, "����ʧ��," + retryDelay / 1000
							+ "�������...");
					Thread.sleep(retryDelay);
				} else
					writer.writeMessage(label, "���ӳɹ���");
			}

		} catch (MalformedURLException e) {
			// throw new Exception("URL����" + e.getLocalizedMessage(), e);
			e.printStackTrace();
			writer.writeMessage(label, "URL����" + e.getLocalizedMessage());
		} catch (IOException e) {
			e.printStackTrace();
			// throw new Exception("I/O����" + e.getLocalizedMessage(), e);
			writer.writeMessage(label, "I/O����" + e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
			writer.writeMessage(label, "����" + e.getLocalizedMessage());
			// throw e;
		}

		// ������ص�Ϊnull���ʾ���ӱ��ܾ�
		return conn;

	}

	/**
	 * ��ȡ������
	 * 
	 * @param urlString
	 * @param retryTimes
	 * @param retryDelay
	 * @param properties
	 * @param writer
	 * @param label
	 * @return
	 */
	public static InputStream getInputStream(String urlString, int retryTimes,
			int retryDelay, Properties properties, IConsoleWriter writer,
			String label) {
		InputStream ret = null;
		HttpURLConnection conn = HttpClientUtils.getHttpURLConnection(
				urlString, retryTimes, retryDelay, properties, writer, label);
		if (conn != null) {
			try {
				ret = conn.getInputStream();
			} catch (Exception e) {
				writer.writeMessage(label, e.getLocalizedMessage());
			}
		}

		return ret;
	}

	/**
	 * ��ӡHttp��Ӧͷ
	 * 
	 * @param conn
	 * @param writer
	 * @param label
	 */
	public static void printResponseHeader(URLConnection conn,
			IConsoleWriter writer, String label) {
		for (Iterator iter = conn.getHeaderFields().keySet().iterator(); iter
				.hasNext();) {
			String key = (String) iter.next();
			writer.writeMessage(label, key + ":" + conn.getHeaderField(key));
		}
	}
}
