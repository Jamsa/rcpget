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
	 * 获取远程Http连接
	 * 
	 * @param urlString
	 *            连接地址
	 * @param retryTimes
	 *            重试次数
	 * @param retryDelay
	 *            重试延时时间
	 * @param properties
	 *            请求属性
	 * @param writer
	 *            日志输出接口，为null时将输出到控制台
	 * @return HttpURLConnection对象
	 * @throws Exception
	 *             如果连接错误则抛出经过处理的异常
	 */
	public static HttpURLConnection getHttpURLConnection(String urlString,
			int retryTimes, int retryDelay, Properties properties,
			IConsoleWriter writer, String label) throws Exception {
		if (writer == null)
			writer = new DefaultConsoleWriter();
		if (label == null)
			label = "HttpURLConnection";

		// 连接计数器
		int count = 0;
		HttpURLConnection conn = null;
		try {
			while (conn == null && retryTimes >= count) {
				count++;
				writer.writeMessage(label, "第" + count + "次连接！");
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

				// 检查返回码
				if (code == HttpURLConnection.HTTP_BAD_METHOD) {
					// 如果返回码是
					conn.setRequestMethod("POST");
					code = conn.getResponseCode();
					printResponseHeader(conn, writer, label);
				}

				// if (code != HttpURLConnection.HTTP_OK)
				// throw new Exception("连接错误!错误代码：" + code);
				if (code >= 400)
					throw new Exception("连接错误！错误代码：" + code);

				if (conn.getHeaderField("Connection").equals("close")) {
					writer.writeMessage(label, "连接被远程主机关闭！");
					if (conn != null) {
						conn.disconnect();
						conn = null;
					}
				}

				// 等侍并重新连接
				if (conn == null)
					Thread.sleep(retryDelay);
				else
					writer.writeMessage(label, "连接成功！");
			}

			// 如果返回的为null则表示连接被拒绝
			return conn;
		} catch (MalformedURLException e) {
			throw new Exception("URL错误：" + e.getLocalizedMessage(), e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("I/O错误：" + e.getLocalizedMessage(), e);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 打印Http响应头
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
