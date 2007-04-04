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
		this.setDefault();
		store.setDefault(IPreferenceConstants.INIT, true);
	}

	/**
	 * ����������ѡ���Ĭ��ֵ
	 * 
	 */
	private void setDefault() {
		setConnectionDefault();
		setTaskDefault();
		setGeneralDefault();
		setMonitorDefault();
	}

	/**
	 * �ָ�����ѡ��ΪĬ��ֵ
	 * 
	 */
	public void setToDefault() {
		setConnectionToDefault();
		setTaskToDefault();
		setGeneralToDefault();
		setMonitorToDefault();
	}

	/**
	 * ���ü��������Ĭ��ֵ
	 * 
	 */
	public void setMonitorDefault() {
		store.setDefault(IPreferenceConstants.MONITOR_CLIPBOARD, true);
		store.setDefault(IPreferenceConstants.MONITOR_FILE_TYPE,
				IPreferenceConstants.MONITOR_DEFAULT_FILE_TYPE);
	}

	/**
	 * �ָ������������õ�Ĭ��ֵ
	 * 
	 */
	public void setMonitorToDefault() {
		store.setToDefault(IPreferenceConstants.MONITOR_CLIPBOARD);
		store.setToDefault(IPreferenceConstants.MONITOR_FILE_TYPE);
	}

	/**
	 * ������������Ĭ��ֵ
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
	 * �ָ�Ĭ����������ΪĬ��ֵ
	 * 
	 */
	public void setTaskToDefault() {
		store.setToDefault(IPreferenceConstants.TASK_DEFAULT_CATEGORY);
		store.setToDefault(IPreferenceConstants.TASK_DEFAULT_CATEGORY_TYPE);
		store.setToDefault(IPreferenceConstants.TASK_DEFAULT_START_METHOD);
		store.setToDefault(IPreferenceConstants.TASK_DEFAULT_SAVEPATH);
	}

	/**
	 * �������Ӳ���ΪĬ��ֵ
	 * 
	 */
	public void setConnectionDefault() {
		store.setDefault(IPreferenceConstants.NETWORK_MAX_RUNTASKS, 15);
		store.setDefault(IPreferenceConstants.NETWORK_RETRY_DELAY, 5);
		store.setDefault(IPreferenceConstants.NETWORK_RETRY_TIMES, 5);
		store.setDefault(IPreferenceConstants.NETWORK_TIMEOUT, 2);
	}

	/**
	 * �ָ����Ӳ���ΪĬ��ֵ
	 * 
	 */
	public void setConnectionToDefault() {
		store.setToDefault(IPreferenceConstants.NETWORK_MAX_RUNTASKS);
		store.setToDefault(IPreferenceConstants.NETWORK_RETRY_DELAY);
		store.setToDefault(IPreferenceConstants.NETWORK_RETRY_TIMES);
		store.setToDefault(IPreferenceConstants.NETWORK_TIMEOUT);
	}

	/**
	 * ���ó���ѡ���Ĭ��ֵ
	 * 
	 */
	public void setGeneralDefault() {
		store.setDefault(IPreferenceConstants.MINIMIZE_TO_TRAY, false);
	}

	/**
	 * �ָ�����ѡ��ΪĬ��ֵ
	 * 
	 */
	public void setGeneralToDefault() {
		store.setToDefault(IPreferenceConstants.MINIMIZE_TO_TRAY);
	}

	/**
	 * �Ƿ���Ӽ�����
	 * 
	 * @return
	 */
	public boolean isMonitorClipboard() {
		return store.getBoolean(IPreferenceConstants.MONITOR_CLIPBOARD);
	}

	/**
	 * �����Ƿ���Ӽ�����
	 * 
	 * @param value
	 */
	public void setMonitorClipboard(boolean value) {
		store.setValue(IPreferenceConstants.MONITOR_CLIPBOARD, value);
	}

	/**
	 * �����ļ�����
	 * 
	 * @return
	 */
	public String getMonitorFileType() {
		return store.getString(IPreferenceConstants.MONITOR_FILE_TYPE);
	}

	/**
	 * ���ü����ļ�������
	 * 
	 * @param value
	 */
	public void setMonitorFileType(String value) {
		store.setValue(IPreferenceConstants.MONITOR_FILE_TYPE, value);
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

	/**
	 * �������ͬʱ�������������
	 * 
	 * @param value
	 */
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

	/**
	 * ������������������ʱ
	 * 
	 * @param value
	 */
	public void setRetryDelay(int value) {
		store.setValue(IPreferenceConstants.NETWORK_RETRY_DELAY, value);
	}

	/**
	 * ��ȡ�����������Դ���
	 * 
	 * @return
	 */
	public int getRetryTimes() {
		int result = store.getInt(IPreferenceConstants.NETWORK_RETRY_TIMES);
		return result;
	}

	/**
	 * ���������������Դ���
	 * 
	 * @param value
	 */
	public void setRetryTimes(int value) {
		store.setValue(IPreferenceConstants.NETWORK_RETRY_TIMES, value);
	}

	/**
	 * ��ȡ��ʱ����
	 * 
	 * @return
	 */
	public int getTimeout() {
		return store.getInt(IPreferenceConstants.NETWORK_TIMEOUT);
	}

	/**
	 * ���ó�ʱʱ��
	 * 
	 * @param value
	 */
	public void setTimeout(int value) {
		store.setValue(IPreferenceConstants.NETWORK_TIMEOUT, value);
	}

	/**
	 * �Ƿ���С����������
	 * 
	 * @return
	 */
	public boolean isMinimizeToTray() {
		return store.getBoolean(IPreferenceConstants.MINIMIZE_TO_TRAY);
	}

	/**
	 * �����Ƿ���С����������
	 * 
	 * @param value
	 */
	public void setMinimizeToTray(boolean value) {
		store.setValue(IPreferenceConstants.MINIMIZE_TO_TRAY, value);
	}

	/**
	 * ����Ĭ�����ط���
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
	 * ����Ĭ�����ط���
	 * 
	 * @param value
	 */
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

	/**
	 * ����Ĭ�����ط��������
	 * 
	 * @param value
	 */
	public void setDefaultCategoryType(String value) {
		store.setValue(IPreferenceConstants.TASK_DEFAULT_CATEGORY_TYPE, value);
	}

	/**
	 * ��ȡĬ�ϱ����·��
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
	 * ����ȱʡ�ļ�����·��
	 * 
	 * @param value
	 */
	public void setDefaultSavePath(String value) {
		store.setValue(IPreferenceConstants.TASK_DEFAULT_SAVEPATH, value);
	}

	/**
	 * ��ȡ���������ķ�ʽ
	 * 
	 * @return
	 */
	public int getStartTaskMethod() {
		return store.getInt(IPreferenceConstants.TASK_DEFAULT_START_METHOD);
	}

	/**
	 * �������������ķ�ʽ
	 * 
	 * @param value
	 */
	public void setStartTaskMethod(int value) {
		store.setValue(IPreferenceConstants.TASK_DEFAULT_START_METHOD, value);
	}
}
