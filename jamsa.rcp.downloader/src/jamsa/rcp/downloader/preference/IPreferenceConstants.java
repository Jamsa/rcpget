package jamsa.rcp.downloader.preference;

/**
 * 首选项常量定义
 * 
 * @author 朱杰
 * 
 */
public interface IPreferenceConstants {
	public static String INIT = "INIT";

	// 默认下载分类类型(指定分类或者记录上次选中的分类)
	public static final String TASK_DEFAULT_CATEGORY_TYPE = "TASK_DEFAULT_CATEGORY_TYPE";

	public static final String TASK_DEFAULT_CATEGORY_TYPE_LAST = "TASK_DEFAULT_CATEGORY_TYPE_LAST";

	public static final String TASK_DEFAULT_CATEGORY_TYPE_DEFINE = "TASK_DEFAULT_CATEGORY_TYPE_DEFINE";

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

	// 最多重试次数
	public static final String NETWORK_RETRY_TIMES = "NETWORK_RETRY_TIMES";

	// 最小化到任务栏
	public static final String MINIMIZE_TO_TRAY = "MINIMIZE_TO_TRAY";
	
	public static final String MONITOR_CLIPBOARD = "MONITOR_CLIPBOARD";
}
