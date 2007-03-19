package jamsa.rcp.downloader.http;

import jamsa.rcp.downloader.views.DefaultConsoleWriter;
import jamsa.rcp.downloader.views.IConsoleWriter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Properties;

public class HttpClientUtils {

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
			IConsoleWriter writer, String label) throws Exception {
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
				writer.writeMessage(label, "��" + count + "�����ӣ�");
				URL url = new URL(urlString);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("User-Agent", "RCP Get");

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

				// if (code != HttpURLConnection.HTTP_OK)
				// throw new Exception("���Ӵ���!������룺" + code);
				if (code >= 400)
					throw new Exception("���Ӵ��󣡴�����룺" + code);

				if (conn.getHeaderField("Connection").equals("close")) {
					writer.writeMessage(label, "���ӱ�Զ�������رգ�");
					if (conn != null) {
						conn.disconnect();
						conn = null;
					}
				}

				// ���̲���������
				if (conn == null)
					Thread.sleep(retryDelay);
				else
					writer.writeMessage(label, "���ӳɹ���");
			}

			// ������ص�Ϊnull���ʾ���ӱ��ܾ�
			return conn;
		} catch (MalformedURLException e) {
			throw new Exception("URL����" + e.getLocalizedMessage(), e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("I/O����" + e.getLocalizedMessage(), e);
		} catch (Exception e) {
			throw e;
		}
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
