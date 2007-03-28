package jamsa.rcp.downloader.models;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String urlString = "http://cz2.onlinedown.net/down/fgcn_101.zip";
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		Runtime.getRuntime().exec("notepad.exe"); 

//		connection.setRequestProperty("User-Agent:", "Mozilla/4.0");
		int code = connection.getResponseCode();
		System.out.println("code:"+code);
		
		System.out.println("url:" + connection.getURL());
		Map map = connection.getHeaderFields();
		for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
			String key = String.valueOf(iter.next());
			List list = (List) map.get(key);
			System.out.println(key+":"+list);
//			for (Iterator it = list.iterator(); it.hasNext();) {
//				Object obj = (Object) it.next();
//				System.out.println(key+":"+obj);
//			}
		}
		System.out.println(connection.getResponseCode());
	}

}
