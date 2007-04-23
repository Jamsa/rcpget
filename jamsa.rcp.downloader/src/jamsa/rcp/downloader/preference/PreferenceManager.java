package jamsa.rcp.downloader.preference;

import jamsa.rcp.downloader.Activator;
import jamsa.rcp.downloader.models.CategoryModel;
import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.utils.StringUtils;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * 首选项数据模型
 * 
 * @author 朱杰
 * 
 */
public class PreferenceManager {
	private static PreferenceManager instance;

	private IPreferenceStore store;

	public static PreferenceManager getInstance() {
		if (instance == null) {
			instance = new PreferenceManager();
		}
		return instance;
	}

	private PreferenceManager() {
		store = Activator.getDefault().getPreferenceStore();
	}

	/**
	 * 检查默认的首选项设置 如果不存在则设置成默认值
	 * 
	 */
	public void checkPreference() {
		// Boolean inited = store.getDefaultBoolean(IPreferenceKeys.INIT);
		// if (inited == null || !inited.booleanValue())
		this.setDefault();
		store.setDefault(IPreferenceConstants.INIT, true);
	}

	/**
	 * 设置所有首选项的默认值
	 * 
	 */
	private void setDefault() {
		setConnectionDefault();
		setTaskDefault();
		setGeneralDefault();
		setMonitorDefault();
	}

	/**
	 * 恢复所有选项为默认值
	 * 
	 */
	public void setToDefault() {
		setConnectionToDefault();
		setTaskToDefault();
		setGeneralToDefault();
		setMonitorToDefault();
	}

	/**
	 * 设置剪贴板监视默认值
	 * 
	 */
	public void setMonitorDefault() {
		store.setDefault(IPreferenceConstants.MONITOR_CLIPBOARD, true);
		store.setDefault(IPreferenceConstants.MONITOR_FILE_TYPE,
				IPreferenceConstants.MONITOR_DEFAULT_FILE_TYPE);
	}

	/**
	 * 恢复所剪贴板设置到默认值
	 * 
	 */
	public void setMonitorToDefault() {
		store.setToDefault(IPreferenceConstants.MONITOR_CLIPBOARD);
		store.setToDefault(IPreferenceConstants.MONITOR_FILE_TYPE);
	}

	/**
	 * 设置任务属性默认值
	 * 
	 */
	public void setTaskDefault() {
		store.setDefault(IPreferenceConstants.TASK_DEFAULT_CATEGORY,
				CategoryModel.getInstance().getFinished().getName());
		store.setDefault(IPreferenceConstants.TASK_DEFAULT_CATEGORY_TYPE,
				IPreferenceConstants.TASK_DEFAULT_CATEGORY_TYPE_LAST);
		store.setDefault(IPreferenceConstants.TASK_DEFAULT_START_METHOD,
				Task.START_AUTO);
		store
				.setDefault(IPreferenceConstants.TASK_DEFAULT_SAVEPATH,
						"C:\\temp");
	}

	/**
	 * 恢复默认任务属性为默认值
	 * 
	 */
	public void setTaskToDefault() {
		store.setToDefault(IPreferenceConstants.TASK_DEFAULT_CATEGORY);
		store.setToDefault(IPreferenceConstants.TASK_DEFAULT_CATEGORY_TYPE);
		store.setToDefault(IPreferenceConstants.TASK_DEFAULT_START_METHOD);
		store.setToDefault(IPreferenceConstants.TASK_DEFAULT_SAVEPATH);
	}

	/**
	 * 设置连接参数为默认值
	 * 
	 */
	public void setConnectionDefault() {
		store.setDefault(IPreferenceConstants.NETWORK_MAX_RUNTASKS, 15);
		store.setDefault(IPreferenceConstants.NETWORK_RETRY_DELAY, 5);
		store.setDefault(IPreferenceConstants.NETWORK_RETRY_TIMES, 5);
		store.setDefault(IPreferenceConstants.NETWORK_TIMEOUT, 2);
	}

	/**
	 * 恢复连接参数为默认值
	 * 
	 */
	public void setConnectionToDefault() {
		store.setToDefault(IPreferenceConstants.NETWORK_MAX_RUNTASKS);
		store.setToDefault(IPreferenceConstants.NETWORK_RETRY_DELAY);
		store.setToDefault(IPreferenceConstants.NETWORK_RETRY_TIMES);
		store.setToDefault(IPreferenceConstants.NETWORK_TIMEOUT);
	}

	/**
	 * 设置常规选项的默认值
	 * 
	 */
	public void setGeneralDefault() {
		store.setDefault(IPreferenceConstants.MINIMIZE_TO_TRAY, false);
	}

	/**
	 * 恢复常规选项为默认值
	 * 
	 */
	public void setGeneralToDefault() {
		store.setToDefault(IPreferenceConstants.MINIMIZE_TO_TRAY);
	}

	/**
	 * 是否监视剪贴板
	 * 
	 * @return
	 */
	public boolean isMonitorClipboard() {
		return store.getBoolean(IPreferenceConstants.MONITOR_CLIPBOARD);
	}

	/**
	 * 设置是否监视剪贴板
	 * 
	 * @param value
	 */
	public void setMonitorClipboard(boolean value) {
		store.setValue(IPreferenceConstants.MONITOR_CLIPBOARD, value);
	}

	/**
	 * 监视文件类型
	 * 
	 * @return
	 */
	public String getMonitorFileType() {
		return store.getString(IPreferenceConstants.MONITOR_FILE_TYPE);
	}

	/**
	 * 设置监视文件的类型
	 * 
	 * @param value
	 */
	public void setMonitorFileType(String value) {
		store.setValue(IPreferenceConstants.MONITOR_FILE_TYPE, value);
	}

	/**
	 * 获取最大同时运行任务数量
	 * 
	 * @return
	 */
	public int getMaxRunTasks() {
		int result = store.getInt(IPreferenceConstants.NETWORK_MAX_RUNTASKS);
		return result;
		// return result == 0 ? store
		// .getDefaultInt(IPreferenceKeys.NETWORK_MAX_RUNTASKS) : result;
	}

	/**
	 * 设置最大同时运行任务的数量
	 * 
	 * @param value
	 */
	public void setMaxRunTasks(int value) {
		store.setValue(IPreferenceConstants.NETWORK_MAX_RUNTASKS, value);
	}

	/**
	 * 返回网络连接重试延时
	 * 
	 * @return
	 */
	public int getRetryDelay() {
		int result = store.getInt(IPreferenceConstants.NETWORK_RETRY_DELAY);
		return result;
		// 不需要去判断是否有值，没有时将自动获取到默认值
		// return result == 0 ? store
		// .getDefaultInt(IPreferenceKeys.NETWORK_RETRY_DELAY) : result;
	}

	/**
	 * 设置网络连接重试延时
	 * 
	 * @param value
	 */
	public void setRetryDelay(int value) {
		store.setValue(IPreferenceConstants.NETWORK_RETRY_DELAY, value);
	}

	/**
	 * 获取网络连接重试次数
	 * 
	 * @return
	 */
	public int getRetryTimes() {
		int result = store.getInt(IPreferenceConstants.NETWORK_RETRY_TIMES);
		return result;
	}

	/**
	 * 设置网络连接重试次数
	 * 
	 * @param value
	 */
	public void setRetryTimes(int value) {
		store.setValue(IPreferenceConstants.NETWORK_RETRY_TIMES, value);
	}

	/**
	 * 获取超时设置
	 * 
	 * @return
	 */
	public int getTimeout() {
		return store.getInt(IPreferenceConstants.NETWORK_TIMEOUT);
	}

	/**
	 * 设置超时时间
	 * 
	 * @param value
	 */
	public void setTimeout(int value) {
		store.setValue(IPreferenceConstants.NETWORK_TIMEOUT, value);
	}

	/**
	 * 是否最小化到任务栏
	 * 
	 * @return
	 */
	public boolean isMinimizeToTray() {
		return store.getBoolean(IPreferenceConstants.MINIMIZE_TO_TRAY);
	}

	/**
	 * 设置是否最小化到任务栏
	 * 
	 * @param value
	 */
	public void setMinimizeToTray(boolean value) {
		store.setValue(IPreferenceConstants.MINIMIZE_TO_TRAY, value);
	}

	/**
	 * 返回默认下载分类
	 * 
	 * @return
	 */
	public String getDefaultCategory() {
		String result = store
				.getString(IPreferenceConstants.TASK_DEFAULT_CATEGORY);
		result = (StringUtils.isEmpty(result)) ? store
				.getDefaultString(IPreferenceConstants.TASK_DEFAULT_CATEGORY)
				: result;
		return result;
	}

	/**
	 * 设置默认下载分类
	 * 
	 * @param value
	 */
	public void setDefaultCategory(String value) {
		store.setValue(IPreferenceConstants.TASK_DEFAULT_CATEGORY, value);
	}

	/**
	 * 返回下载分类的类型：为固定的值还是记录下上次设置的值
	 * 
	 * @return
	 */
	public String getDefaultCategoryType() {
		String result = store
				.getString(IPreferenceConstants.TASK_DEFAULT_CATEGORY_TYPE);
		result = (StringUtils.isEmpty(result)) ? store
				.getDefaultString(IPreferenceConstants.TASK_DEFAULT_CATEGORY_TYPE)
				: result;
		return result;
	}

	/**
	 * 设置默认下载分类的类型
	 * 
	 * @param value
	 */
	public void setDefaultCategoryType(String value) {
		store.setValue(IPreferenceConstants.TASK_DEFAULT_CATEGORY_TYPE, value);
	}

	/**
	 * 获取默认保存的路径
	 * 
	 * @return
	 */
	public String getDefaultSavePath() {
		String result = store
				.getString(IPreferenceConstants.TASK_DEFAULT_SAVEPATH);
		result = (StringUtils.isEmpty(result)) ? store
				.getDefaultString(IPreferenceConstants.TASK_DEFAULT_SAVEPATH)
				: result;
		return result;
	}

	/**
	 * 设置缺省文件保存路径
	 * 
	 * @param value
	 */
	public void setDefaultSavePath(String value) {
		store.setValue(IPreferenceConstants.TASK_DEFAULT_SAVEPATH, value);
	}

	/**
	 * 获取任务启动的方式
	 * 
	 * @return
	 */
	public int getStartTaskMethod() {
		return store.getInt(IPreferenceConstants.TASK_DEFAULT_START_METHOD);
	}

	/**
	 * 设置任务启动的方式
	 * 
	 * @param value
	 */
	public void setStartTaskMethod(int value) {
		store.setValue(IPreferenceConstants.TASK_DEFAULT_START_METHOD, value);
	}
}
