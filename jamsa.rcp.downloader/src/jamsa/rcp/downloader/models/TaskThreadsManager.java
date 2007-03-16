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
public class TaskThreadsManager {
	Logger logger = new Logger(this.getClass());

	private Map threads = Collections.synchronizedMap(new HashMap());

	private TaskThreadsManager() {
	}

	private static TaskThreadsManager instance = new TaskThreadsManager();

	public static TaskThreadsManager getInstance() {
		return instance;
	}

	public void start(Task task) {
		// 旧的单线程下载
		// TaskThread thread = (TaskThread) threads.get(task.getFileUrl());
		// if (thread == null || !thread.isAlive()) {
		// thread = new TaskThread(task);
		// }
		TaskThread2 thread = (TaskThread2) threads.get(task.getFileUrl());
		if (thread == null || !thread.isAlive()) {
			thread = new TaskThread2(task);
		}
		thread.start();
		threads.put(task.getFileUrl(), thread);
		logger.debug("启动任务" + thread);
	}

	public void restart(Task task) {
		task.reset();
		this.start(task);
	}

	public void stop(Task task) {
		// TaskThread thread = (TaskThread) threads.get(task.getFileUrl());
		TaskThread2 thread = (TaskThread2) threads.get(task.getFileUrl());
		task.setStatus(Task.STATUS_STOP);
		if (thread != null) {
			// thread.interrupt();
			logger.debug("停止任务" + thread);
			threads.remove(task.getFileUrl());
		}
	}

}
