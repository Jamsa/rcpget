package jamsa.rcp.downloader.utils;

import java.net.URL;

/**
 * 字符串处理工具类
 * 
 * @author 朱杰
 * 
 */
public class StringUtils {
	private static Logger logger = new Logger(StringUtils.class);

	/**
	 * 检查字符串是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return (str == null || "".equals(str) || str.matches("\\s+"));// str.trim().equals("")
		// ||
	}

	/**
	 * 从url字符串中获取正确的url，不正确时返回""
	 * 
	 * @param url
	 * @return
	 */
	public static String getURLString(String url) {
		String ret = url.trim();
		if (StringUtils.isEmpty(url)) {
			return "";
		}

		int end = ret.indexOf(' ');
		if (end > 1) {
			ret = ret.substring(0, end);
		}
		try {
			URL u = new URL(ret);
			ret = String.valueOf(u);
		} catch (Exception e) {
			logger.info(url + "不是一个URL地址");
			ret = "";
		}
		return ret;
	}

	/**
	 * 获取url字符串，字符串必须以suffixies中的某个元素结束，否则返回为""
	 * 
	 * @param url
	 * @param suffixies
	 * @return
	 */
	public static String getURLString(String url, String[] suffixies) {
		String ret = getURLString(url);
		boolean match = false;
		if (suffixies != null) {
			for (int i = 0; i < suffixies.length; i++) {
				String suffix = suffixies[i];
				if (ret.endsWith(suffix)) {
					match = true;
					break;
				}
			}
		}
		if (!match)
			ret = "";
		return ret;
	}
}
