package jamsa.rcp.downloader;

public interface IConstants {
	public static final String M_PREFIX = "";

	public static final String M_TASK = M_PREFIX + "task";

	public static final String USER_HOME = System.getProperty("user.home");

	public static final String JAVA_IO_TMPDIR = System
			.getProperty("java.io.tmpdir");

	// .replaceAll("//", "/").replaceAll("\\\\", "\\")

	public static final String FILE_SEPARATOR = System
			.getProperty("file.separator");

	public static final String OS_NAME = System.getProperty("os.name");

	public static final String OS_ARCH = System.getProperty("os.arch");

	public static final String OS_VERSION = System.getProperty("os.version");

	public static final String USER_NAME = System.getProperty("user.name");

	public static final String PATH_SEPARATOR = System
			.getProperty("path.separator");

	/*
	 * java.version Java ����ʱ�����汾 java.vendor Java ����ʱ������Ӧ�� java.vendor.url Java
	 * ��Ӧ�̵� URL java.home Java ��װĿ¼ java.vm.specification.version Java ������淶�汾
	 * java.vm.specification.vendor Java ������淶��Ӧ�� java.vm.specification.name
	 * Java ������淶���� java.vm.version Java �����ʵ�ְ汾 java.vm.vendor Java �����ʵ�ֹ�Ӧ��
	 * java.vm.name Java �����ʵ������ java.specification.version Java ����ʱ�����淶�汾
	 * java.specification.vendor Java ����ʱ�����淶��Ӧ�� java.specification.name Java
	 * ����ʱ�����淶���� java.class.version Java ���ʽ�汾�� java.class.path Java ��·��
	 * java.library.path ���ؿ�ʱ������·���б� java.io.tmpdir Ĭ�ϵ���ʱ�ļ�·�� java.compiler Ҫʹ�õ�
	 * JIT ������������ java.ext.dirs һ��������չĿ¼��·�� os.name ����ϵͳ������ os.arch ����ϵͳ�ļܹ�
	 * os.version ����ϵͳ�İ汾 file.separator �ļ��ָ������� UNIX ϵͳ���ǡ�/���� path.separator
	 * ·���ָ������� UNIX ϵͳ���ǡ�:���� line.separator �зָ������� UNIX ϵͳ���ǡ�/n���� user.name
	 * �û����˻����� user.home �û�����Ŀ¼ user.dir �û��ĵ�ǰ����Ŀ¼
	 */

}