package jamsa.rcp.downloader.models;

import jamsa.rcp.downloader.utils.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * �����̹߳�����
 * 
 * @author ���
 * 
 */
public class TaskThreadManager {
	Logger logger = new Logger(this.getClass());

	private Map threads = Collections.synchronizedMap(new HashMap());

	private TaskThreadManager() {
	}

	private static TaskThreadManager instance = new TaskThreadManager();

	public static TaskThreadManager getInstance() {
		return instance;
	}

	/**
	 * ������������
	 * 
	 * @param task
	 */
	public void start(Task task) {
		if (!isAllowStart(task))
			return;
		TaskThread2 thread = (TaskThread2) threads.get(task.getFileUrl());
		if (thread == null || !thread.isAlive()) {
			thread = new TaskThread2(task);
		}
		threads.put(task.getFileUrl(), thread);
		thread.start();
		logger.debug("��������" + thread);
		TaskModel.getInstance().updateTask(task);
	}

	public boolean isAllowStart(Task task) {
		int status = task.getStatus();
		if ((status == Task.STATUS_ERROR || status == Task.STATUS_STOP)
				&& !task.isDeleted()) {
			return true;
		}
		return false;
	}

	public void start(List tasks) {
		for (Iterator it = tasks.iterator(); it.hasNext();) {
			Task task = (Task) it.next();
			if (isAllowStart(task)) {
				this.start(task);
			}
		}
	}

	/**
	 * ������������
	 * 
	 * @param task
	 */
	public void restart(Task task) {
		TaskModel.getInstance().deleteTask(task, true);
		task.setDeleted(false);
		TaskModel.getInstance().addTask(task);
		task.reset();
		this.start(task);
		TaskModel.getInstance().updateTask(task);
	}

	public boolean isAllowRestart(Task task) {
		if (task.getStatus() == Task.STATUS_FINISHED && !task.isDeleted())
			return true;
		return false;
	}

	public void restart(List tasks) {
		for (Iterator it = tasks.iterator(); it.hasNext();) {
			Task task = (Task) it.next();
			if (isAllowRestart(task))
				restart(task);
		}
	}

	/**
	 * ֹͣ��������
	 * 
	 * @param task
	 */
	public void stop(Task task) {
		TaskThread2 thread = (TaskThread2) threads.get(task.getFileUrl());
		task.setStatus(Task.STATUS_STOP);
		if (thread != null) {
			logger.debug("ֹͣ����" + thread);
			threads.remove(task.getFileUrl());
			thread.interrupt();
		}
		TaskModel.getInstance().updateTask(task);
	}

	public boolean isAllowStop(Task task) {
		int status = task.getStatus();
		if (status == Task.STATUS_RUNNING) {
			return true;
		}
		return false;
	}

	public void stop(List tasks) {
		for (Iterator it = tasks.iterator(); it.hasNext();) {
			Task task = (Task) it.next();
			if (isAllowStop(task)) {
				this.stop(task);
			}
		}
	}

}
