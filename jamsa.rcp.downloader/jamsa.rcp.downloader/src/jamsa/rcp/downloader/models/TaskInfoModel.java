package jamsa.rcp.downloader.models;

import java.util.Observable;

/**
 * Task��ֱ�Ӽ̳���Observable�࣬����ʹ��TaskInfoModel��
 * @author Administrator
 * @deprecated
 */
public class TaskInfoModel extends Observable {
	private Task task;

	public void setTask(Task task) {
		this.task = task;
		this.setChanged();
		this.notifyObservers(task);
	}
	
	
}
