package jamsa.rcp.downloader.models;

import jamsa.rcp.downloader.utils.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * �����̹߳�����
 * 
 * @author ���
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
		// �ɵĵ��߳�����
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
		logger.debug("��������" + thread);
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
			logger.debug("ֹͣ����" + thread);
			threads.remove(task.getFileUrl());
		}
	}

}
