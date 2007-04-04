package jamsa.rcp.downloader.http;

import jamsa.rcp.downloader.IConstants;
import jamsa.rcp.downloader.utils.Logger;
import jamsa.rcp.downloader.utils.StringUtils;
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
	private static Logger logger = new Logger(HttpClientUtils.class);

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
			int retryTimes, int retryDelay, int timeout, Properties properties,
			String method, IConsoleWriter writer, String label) {
		if (writer == null)
			writer = new DefaultConsoleWriter();
		HttpURLConnection conn = getHttpURLConnection(urlString, retryTimes,
				retryDelay, timeout, properties, method, writer, label);
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

		writer.writeMessage("Task", "Զ���ļ���" + result.getFileName());
		result.setFileSize(conn.getContentLength());
		writer.writeMessage("Task", "Զ���ļ���С" + result.getFileSize());

		logger.info("Զ���ļ���Ϣ��\n" + result);
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
	 * @param timeout
	 *            ��ʱʱ��
	 * @param properties
	 *            ��������
	 * @param method
	 *            ����ʽ��GET,POST,HEAD...
	 * @param writer
	 *            ��־����ӿڣ�Ϊnullʱ�����������̨
	 * @return HttpURLConnection����
	 * @throws Exception
	 *             ������Ӵ������׳�����������쳣
	 */
	public static HttpURLConnection getHttpURLConnection(String urlString,
			int retryTimes, int retryDelay, int timeout, Properties properties,
			String method, IConsoleWriter writer, String label) {
		if (writer == null)
			writer = new DefaultConsoleWriter();
		if (label == null)
			label = "HttpURLConnection";

		// ���Ӽ�����
		int count = 0;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlString);
			while (conn == null && retryTimes >= count) {
				count++;
				writer.writeMessage(label, "��" + count + "������...");
				logger.info("��" + count + "������...");
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(timeout);
				conn.setRequestMethod(method);

				for (Iterator it = properties.keySet().iterator(); it.hasNext();) {
					String key = String.valueOf(it.next());
					conn.setRequestProperty(key, String.valueOf(properties
							.get(key)));
				}

				printRequestInfo(conn, writer, label);
				// conn.connect();
				int code = conn.getResponseCode();
				printResponseInfo(conn, writer, label);

				String newURLString = String.valueOf(conn.getURL());

				if (code == 404 && !urlString.equals(newURLString)) {
					String encode = conn.getContentEncoding();
					if (StringUtils.isEmpty(encode))
						encode = IConstants.DEFAULT_ENCODING;
					url = new URL(new String(newURLString.getBytes(encode),
							IConstants.FILE_ENCODING));
					writer.writeMessage(label, "�ض��򵽣�" + url);
					logger.info("�����ض���" + url);
					conn = null;
					continue;
				}

				// ���Ӵ���ʱ����conn����Ϊnull����������
				if (code >= 400) {
					conn = null;
				}

				// ���̲���������
				if (conn == null) {
					writer.writeMessage(label, "����ʧ��," + retryDelay / 1000
							+ "�������...");
					logger.info("����ʧ��," + retryDelay / 1000 + "�������...");
					Thread.sleep(retryDelay);
				} else {
					writer.writeMessage(label, "���ӳɹ���");
					logger.info("���ӳɹ���");
				}

			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			writer.writeMessage(label, "URL����" + e.getLocalizedMessage());
			logger.error("URL����", e);
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			writer.writeMessage(label, "I/O����" + e.getLocalizedMessage());
			logger.error("I/O����", e);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			writer.writeMessage(label, "����" + e.getLocalizedMessage());
			logger.error("���Ӵ���", e);
			return null;
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
			int retryDelay, int timeout, Properties properties, String method,
			IConsoleWriter writer, String label) {
		InputStream ret = null;
		HttpURLConnection conn = HttpClientUtils.getHttpURLConnection(
				urlString, retryTimes, retryDelay, timeout, properties, method,
				writer, label);
		if (conn != null) {
			try {
				ret = conn.getInputStream();
			} catch (Exception e) {
				writer.writeMessage(label, e.getLocalizedMessage());
				logger.error("��ȡ��������������", e);
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
	public static void printResponseInfo(URLConnection conn,
			IConsoleWriter writer, String label) {
		logger.info("Http��Ӧ��Ϣ��");
		for (Iterator iter = conn.getHeaderFields().keySet().iterator(); iter
				.hasNext();) {
			String key = (String) iter.next();
			writer.writeMessage(label, key + ":" + conn.getHeaderField(key));
			logger.info(key + ":" + conn.getHeaderField(key));
		}
	}

	/**
	 * ��ӡHttp������Ϣ
	 * 
	 * @param conn
	 * @param writer
	 * @param label
	 */
	public static void printRequestInfo(HttpURLConnection conn,
			IConsoleWriter writer, String label) {
		logger.info("Http������Ϣ��");
		logger.info("RequestMethod:" + conn.getRequestMethod());
		for (Iterator iter = conn.getRequestProperties().keySet().iterator(); iter
				.hasNext();) {
			String key = (String) iter.next();
			writer.writeMessage(label, key + ":" + conn.getHeaderField(key));
			logger.info(key + ":" + conn.getHeaderField(key));
		}
	}
}
