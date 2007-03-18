package jamsa.rcp.downloader.preference;

public interface IPreferenceKeys {
	public static String INIT = "INIT";
	// 默认下载分类类型(指定分类或者记录上次选中的分类)
	public static final String TASK_DEFAULT_CATEGORY_TYPE = "TASK_DEFAULT_CATEGORY_TYPE";

	// 默认分类
	public static final String TASK_DEFAULT_CATEGORY = "TASK_DEFAULT_CATEGORY";

	// 默认保存路径
	public static final String TASK_DEFAULT_SAVEPATH = "TASK_DEFAULT_SAVEPATH";

	// 任务默认启动方式
	public static final String TASK_DEFAULT_START_METHOD = "TASK_DEFAULT_START";

	// 重试延时
	public static final String NETWORK_RETRY_DELAY = "NETWORK_RETRY_DELAY";

	// 最多同时运行任务数量
	public static final String NETWORK_MAX_RUNTASKS = "NETWORK_MAX_RUNTASKS";
}
