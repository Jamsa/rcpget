package jamsa.rcp.downloader.models;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String urlString = "http://www.greendown.cn/Download.asp?ID=7838";
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//		Map map = connection.getRequestProperties();
//		for (Iterator it = map.keySet().iterator(); it.hasNext();) {
//			Object key = it.next();
//			System.out.println(map.get(key));
//
//		}
//		for (Iterator it = map.keySet().iterator(); it.hasNext();) {
//			Object key = it.next();
//			System.out.println(map.get(key));
//
//		}
		System.out.println(connection.getURL());
		
		System.out.println(connection.getResponseCode() + " "
				+ connection.getResponseMessage());
		Map map = connection.getRequestProperties();
		
		System.out.println(connection.getURL());
		url = connection.getURL();
		System.out.println(url);

	}

}
