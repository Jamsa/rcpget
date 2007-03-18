package jamsa.rcp.downloader.preference;

public interface IPreferenceValues {
	// 自动启动
	public static final String TASK_START_METHOD_AUTO = "TASK_START_METHOD_AUTO";

	// 手工启动
	public static final String TASK_START_METHOD_MANUAL = "TASK_START_METHOD_MANUAL";
	
	//分类为上次设定的值
	public static final String TASK_DEFAULT_CATEGORY_TYPE_LAST = "TASK_DEFAULT_CATEGORY_TYPE_LAST";
	
	//分类为设置的值
	public static final String TASK_DEFAULT_CATEGORY_TYPE_DEFINE = "TASK_DEFAULT_CATEGORY_TYPE_DEFINE";
}
