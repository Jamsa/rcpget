package jamsa.rcp.downloader.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * �ļ�����������
 * 
 * @author ���
 * 
 */
public class FileUtils {

	/**
	 * �ݹ鴴��Ŀ¼
	 * 
	 * @param path
	 */
	private static void createDirectory(File path) {
		if (!path.exists()) {
			if (!path.mkdir())
				createDirectory(new File(path.getParent()));
		}
	}

	/**
	 * ����Ŀ¼
	 * 
	 * @param path
	 */
	public static void createDirectory(String path) {
		File directory = new File(path);
		while (!directory.exists()) {
			createDirectory(directory);
		}
	}

	/**
	 * ���Ŀ¼�Ƿ��Ѿ�����
	 * 
	 * @param path
	 * @return
	 */
	public static boolean existsDirectory(String path) {
		File directory = new File(path);
		return directory.exists();
	}

	/**
	 * ����ļ��Ƿ��Ѿ�����
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean existsFile(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}

	/**
	 * ����Ƿ�Ϊ�ļ�
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isFile(String fileName) {
		File file = new File(fileName);
		return file.isFile();
	}

	/**
	 * ��ȡ�ļ�
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String readFile(File file) throws IOException {
		StringBuffer result = new StringBuffer();
		FileInputStream fis = new FileInputStream(file);
		byte buf[] = new byte[1024];
		while (fis.read(buf) != -1) {
			result.append((new String(buf, "iso8859-1")).trim());
			Arrays.fill(buf, (byte) 0);
		}
		fis.close();
		return String.valueOf(result);
	}

	/**
	 * д���ļ�
	 * 
	 * @param file
	 * @param source
	 * @throws IOException
	 */
	public static void writeFile(File file, String source) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		byte buf[] = buf = source.getBytes("iso8859-1");
		fos.write(buf);
		fos.flush();
		fos.close();
	}

	/**
	 * �޸��ļ���
	 * 
	 * @param fileName
	 *            �ļ�����
	 * @param destName
	 *            Ŀ������
	 */
	public static void renameFile(String fileName, String destName) {
		File orign = new File(fileName);
		File dest = new File(destName);
		if (orign.exists() && !dest.exists()) {
			orign.renameTo(dest);
		}
	}
}
