

import jamsa.rcp.downloader.utils.EncodeUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

public class Test {
	private static void printMap(Map map) {
		for (Iterator it = map.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			System.out.println(key + ":" + map.get(key));
		}
	}
	public static void maina(String[] args) throws Exception{
		System.out.println(URLEncoder.encode("中 文", "GBK"));
		System.out.println(URLEncoder.encode(" ", "iso8859-1"));
		System.out.println(EncodeUtils.encodeURL("中文"));
		System.out.println(EncodeUtils.encodeURL(" "));
		String tmp = "http://www.linuxfans.org/nuke/software/"+URLEncoder.encode("Net Sharer","utf-8")+"/netsharer-0.4.3.tar.gz";
		System.out.println(tmp);
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		//http://www.linuxfans.org/nuke/modules.php?name=Site_Downloads&op=mydown&did=4707
		//String urlString = "http://www.linuxfans.org/nuke/modules.php?name=Site_Downloads&op=mydown&did=4783";//4783
		String urlString = "http://php.tech.sina.com.cn/download/d_load.php?d_id=7877&down_id=151542";
		urlString = EncodeUtils.encodeURL(urlString);
		URL url = new URL(urlString);
		System.out.println("第一次：" + url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		HttpURLConnection.setFollowRedirects(true);
		Map req = conn.getRequestProperties();
		System.out.println("第一次请求头：");
		printMap(req);
		
		conn.connect();
		System.out.println("第一次响应：");
		System.out.println(conn.getResponseMessage());
		int code = conn.getResponseCode();
		System.out.println("第一次code:" + code);
		printMap(conn.getHeaderFields());
		System.out.println(conn.getURL().getFile());

		if (code == 404 && !(conn.getURL() + "").equals(urlString)) {
//			String tmp = conn.getURL() + "";
//			tmp = URLDecoder.decode(tmp,"iso-8859-1");
			//在线音乐播放脚本
			//String tmp = new String(conn.getURL().toString().getBytes("iso-8859-1"),"gbk");
			//tmp = EncodeUtils.encodeURL(tmp, "gbk");
			//String tmp = "http://www.linuxfans.org/nuke/software/km169"+URLEncoder.encode("在线音乐播放脚本","GBK")+"/km169.tar.bz2";
			//String tmp = "http://www.linuxfans.org/nuke/software/km169在线音乐播放脚本/km169.tar.bz2";
			//String tmp = "http://www.linuxfans.org/nuke/software/km169"+URLEncoder.encode("在线音乐播放脚本","gbk")+"/km169.tar.bz2";
			//String tmp = "http://www.linuxfans.org/nuke/software/km169在线音乐播放脚本/km169.tar.bz2";
			//String tmp = "http://www.linuxfans.org/nuke/software/Net%20Sharer/netsharer-0.4.3.tar.gz";
			System.out.println(conn.getURL());
			String tmp = URLEncoder.encode(conn.getURL().toString(),"gbk");
			System.out.println(URLEncoder.encode("在线音乐播放脚本","GBK"));
			System.out.println(tmp);
			url = new URL(tmp);
			System.out.println("第二次：" + url);
			conn = (HttpURLConnection) url.openConnection();
			System.out.println("第二次响应：");
			System.out.println("code:" + code);
			printMap(conn.getHeaderFields());
		}
	}

}
