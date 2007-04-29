package jamsa.rcp.downloader.models;

import java.util.Observable;

/**
 * Task类直接继承了Observable类，不再使用TaskInfoModel了
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
