package jamsa.rcp.downloader.utils;

import jamsa.rcp.downloader.IConstants;

/**
 * 编码处理工具类
 * 
 * @author 朱杰
 * 
 */
public class EncodeUtils {

	/**
	 * 将字符串进行URL编码
	 * 
	 * @param text
	 * @param charset
	 *            字符集，如果未指定则按{@link #DEFAULT_CHARSET}进行处理
	 * @return 经过编码的URL字符串
	 */
	public static String encodeURL(String text, String charset) {
		// if (charset == null)
		// charset = System.getProperty("file.encoding");
		if (StringUtils.isEmpty(charset))
			charset = IConstants.DEFAULT_ENCODING;
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c >= 0 && c <= 255) {
				result.append(c);
			} else {
				// try {
				// result.append(URLEncoder.encode(c + "", charset));
				// } catch (Exception e) {
				//
				// }
				byte[] b = new byte[0];
				try {
					b = Character.toString(c).getBytes(charset);
				} catch (Exception ex) {
				}

				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0)
						k += 256;
					result.append("%" + Integer.toHexString(k).toUpperCase());
				}

			}
		}

		return result.toString();
	}

	public static String encodeURL(String text) {
		return encodeURL(text, null);
	}
}
