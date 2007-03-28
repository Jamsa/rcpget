package jamsa.rcp.downloader.preference;

import jamsa.rcp.downloader.Activator;
import jamsa.rcp.downloader.models.CategoryModel;
import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.utils.StringUtils;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * ��ѡ������ģ��
 * 
 * @author ���
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
	 * ���Ĭ�ϵ���ѡ������ ��������������ó�Ĭ��ֵ
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
	 * �ָ�����ѡ��ΪĬ��ֵ
	 * 
	 */
	public void setToDefault() {
		setConnectionDefault();
		setTaskDefault();
		setGeneralDefault();
	}

	/**
	 * �ָ�Ĭ����������ΪĬ��ֵ
	 * 
	 */
	public void setTaskDefault() {
		store.setToDefault(IPreferenceConstants.NETWORK_MAX_RUNTASKS);
		store.setToDefault(IPreferenceConstants.NETWORK_RETRY_DELAY);
		store.setToDefault(IPreferenceConstants.NETWORK_RETRY_TIMES);
	}

	/**
	 * �ָ����Ӳ���ΪĬ��ֵ
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
	 * ��ȡ���ͬʱ������������
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
	 * ������������������ʱ
	 * 
	 * @return
	 */
	public int getRetryDelay() {
		int result = store.getInt(IPreferenceConstants.NETWORK_RETRY_DELAY);
		return result;
		// ����Ҫȥ�ж��Ƿ���ֵ��û��ʱ���Զ���ȡ��Ĭ��ֵ
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
	 * ����Ĭ�����ط���
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
	 * �������ط�������ͣ�Ϊ�̶���ֵ���Ǽ�¼���ϴ����õ�ֵ
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
	 * ��ȡĬ�ϱ����·��
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
	 * ����
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
