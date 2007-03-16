package jamsa.rcp.downloader.utils;

/**
 * 字符串处理工具类
 * 
 * @author 朱杰
 * 
 */
public class StringUtils {
	public static boolean isEmpty(String str) {
		return (str == null || str.trim().equals(""));
	}
}
