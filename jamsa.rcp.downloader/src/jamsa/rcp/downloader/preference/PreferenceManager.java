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
			this.initDefault();
	}

	private void initDefault() {
		store.setDefault(IPreferenceConstants.INIT, true);

		store.setDefault(IPreferenceConstants.NETWORK_MAX_RUNTASKS, 15);
		store.setDefault(IPreferenceConstants.NETWORK_RETRY_DELAY, 5);
		store.setDefault(IPreferenceConstants.NETWORK_RETRY_TIMES, 5);

		store.setDefault(IPreferenceConstants.TASK_DEFAULT_CATEGORY, CategoryModel
				.getInstance().getFinished().getName());
		store.setDefault(IPreferenceConstants.TASK_DEFAULT_CATEGORY_TYPE,
				IPreferenceConstants.TASK_DEFAULT_CATEGORY_TYPE_LAST);
		store.setDefault(IPreferenceConstants.TASK_DEFAULT_START_METHOD,
				Task.START_AUTO);
		store.setDefault(IPreferenceConstants.TASK_DEFAULT_SAVEPATH, "C:\\temp");

	}

	/**
	 * 恢复所有选项为默认值
	 * 
	 */
	public void setToDefault() {
		setConnectionDefault();
		setTaskDefault();
		setGeneralDefault();
	}

	/**
	 * 恢复默认任务属性为默认值
	 * 
	 */
	public void setTaskDefault() {
		store.setToDefault(IPreferenceConstants.NETWORK_MAX_RUNTASKS);
		store.setToDefault(IPreferenceConstants.NETWORK_RETRY_DELAY);
		store.setToDefault(IPreferenceConstants.NETWORK_RETRY_TIMES);
	}

	/**
	 * 恢复连接参数为默认值
	 * 
	 */
	public void setConnectionDefault() {
		store.setToDefault(IPreferenceConstants.TASK_DEFAULT_CATEGORY);
		store.setToDefault(IPreferenceConstants.TASK_DEFAULT_CATEGORY_TYPE);
		store.setToDefault(IPreferenceConstants.TASK_DEFAULT_START_METHOD);
		store.setToDefault(IPreferenceConstants.TASK_DEFAULT_SAVEPATH);
	}

	public void setGeneralDefault() {
		store.setDefault(IPreferenceConstants.MINIMIZE_TO_TRAY, true);
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

	public void setRetryDelay(int value) {
		store.setValue(IPreferenceConstants.NETWORK_RETRY_DELAY, value);
	}

	public int getRetryTimes() {
		int result = store.getInt(IPreferenceConstants.NETWORK_RETRY_TIMES);
		return result;
	}

	public void setRetryTimes(int value) {
		store.setValue(IPreferenceConstants.NETWORK_RETRY_TIMES, value);
	}

	public boolean getMinimizeToTray() {
		return store.getBoolean(IPreferenceConstants.MINIMIZE_TO_TRAY);
	}

	public void setMinimizeToTray(boolean value) {
		store.setValue(IPreferenceConstants.MINIMIZE_TO_TRAY, value);
	}

	/**
	 * 返回默认下载分类
	 * 
	 * @return
	 */
	public String getDefaultCategory() {
		String result = store.getString(IPreferenceConstants.TASK_DEFAULT_CATEGORY);
		result = (StringUtils.isEmpty(result)) ? store
				.getDefaultString(IPreferenceConstants.TASK_DEFAULT_CATEGORY)
				: result;
		return result;
	}

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

	public void setDefaultCategoryType(String value) {
		store.setValue(IPreferenceConstants.TASK_DEFAULT_CATEGORY_TYPE, value);
	}

	/**
	 * 获取默认保存的路径
	 * 
	 * @return
	 */
	public String getDefaultSavePath() {
		String result = store.getString(IPreferenceConstants.TASK_DEFAULT_SAVEPATH);
		result = (StringUtils.isEmpty(result)) ? store
				.getDefaultString(IPreferenceConstants.TASK_DEFAULT_SAVEPATH)
				: result;
		return result;
	}

	public void setDefaultSavePath(String value) {
		store.setValue(IPreferenceConstants.TASK_DEFAULT_SAVEPATH, value);
	}

	/**
	 * 返回
	 * 
	 * @return
	 */
	public int getStartTaskMethod() {
		return store.getInt(IPreferenceConstants.TASK_DEFAULT_START_METHOD);
	}

	public void setStartTaskMethod(int value) {
		store.setValue(IPreferenceConstants.TASK_DEFAULT_START_METHOD, value);
	}
}
