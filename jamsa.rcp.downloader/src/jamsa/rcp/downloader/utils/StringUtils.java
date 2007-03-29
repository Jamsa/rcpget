package jamsa.rcp.downloader.utils;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * �ַ�����������
 * 
 * @author ���
 * 
 */
public class StringUtils {
	public static void main(String[] args) {
		Pattern p = Pattern.compile("[a-z]+");
		Matcher m = p.matcher("aaaa");
		System.out.println(Pattern.matches("aaaa", "aaaa"));
		System.out.println(m.matches());
		System.out.println("#" + m.group() + "#");

		System.out.println(isEmpty("dfaf\t\t\tdfa"));
		System.out
				.println(getURLString("http://431431.42342.4232.com/fdasdfa.html   \t\t\tfasfa"));
	}

	/**
	 * ����ַ����Ƿ�Ϊ��
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return (str == null || "".equals(str) || str.matches("\\s+"));// str.trim().equals("")
		// ||
	}

	/**
	 * ��url�ַ����л�ȡ��ȷ��url������ȷʱ����""
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
			e.printStackTrace();
			ret = "";
		}
		return ret;
	}

	/**
	 * ��ȡurl�ַ������ַ���������suffixies�е�ĳ��Ԫ�ؽ��������򷵻�Ϊ""
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
