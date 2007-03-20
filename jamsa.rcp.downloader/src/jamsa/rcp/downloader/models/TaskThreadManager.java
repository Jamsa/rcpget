package jamsa.rcp.downloader.models;

import jamsa.rcp.downloader.utils.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务线程管理器
 * 
 * @author 朱杰
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
	 * 启动下载任务
	 * @param task
	 */
	public void start(Task task) {
		TaskThread2 thread = (TaskThread2) threads.get(task.getFileUrl());
		if (thread == null || !thread.isAlive()) {
			thread = new TaskThread2(task);
		}
		threads.put(task.getFileUrl(), thread);
		thread.start();
		logger.debug("启动任务" + thread);
		TaskModel.getInstance().updateTask(task);
	}

	/**
	 * 重启下载任务
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

	/**
	 * 停止下载任务
	 * @param task
	 */
	public void stop(Task task) {
		TaskThread2 thread = (TaskThread2) threads.get(task.getFileUrl());
		task.setStatus(Task.STATUS_STOP);
		if (thread != null) {
			logger.debug("停止任务" + thread);
			threads.remove(task.getFileUrl());
		}
		TaskModel.getInstance().updateTask(task);
	}

}
