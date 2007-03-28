package jamsa.rcp.downloader.models;

import jamsa.rcp.downloader.http.HttpClientUtils;
import jamsa.rcp.downloader.http.RemoteFileInfo;
import jamsa.rcp.downloader.preference.PreferenceManager;
import jamsa.rcp.downloader.utils.Logger;
import jamsa.rcp.downloader.utils.StringUtils;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * 多线程下载中的任务线程,用于控制其它下载线程
 * 
 * @author 朱杰
 * 
 */
public class TaskThread2 extends Thread {
	private static Logger logger = new Logger(TaskThread2.class);

	private Task task;

	private TaskModel taskModel;

	private PreferenceManager preferenceManager;

	public TaskThread2(Task task) {
		this.task = task;
		this.taskModel = TaskModel.getInstance();
		preferenceManager = PreferenceManager.getInstance();
	}

	// 修改任务状态
	private void changeStatus(int status) {
		task.setStatus(status);
	}

	// 下载线程
	private List threads = new ArrayList(10);

	/**
	 * 验证远程文件与本地文件的一致性
	 * 
	 * @param remoteFileName
	 *            远端文件名
	 * @param remoteFileSize
	 *            远端文件大小
	 */
	private void checkFile(String remoteFileName, long remoteFileSize) {
		if (task.getFileSize() > 0 && task.getFileSize() != remoteFileSize) {
			task.writeMessage("Task", "文件大小不一致，重新下载！");
			task.reset();
			task.setStatus(Task.STATUS_RUNNING);
		}

		task.setFileSize(remoteFileSize);

		// 如果文件还未下载，并且远程文件名与本地文件名不一致，则修改本地文件名
		if (task.getFinishedSize() == 0 && !StringUtils.isEmpty(remoteFileName)
				&& !task.getFileName().equals(remoteFileName)) {
			task.setFileName(remoteFileName);
		}
	}

	/**
	 * 启动
	 * 
	 */
	public void runUnfinishedSplitters() {

	}

	public void run() {
		task.writeMessage("Task", "任务启动");
		task.getMessages().clear();
		// 修改任务状态
		changeStatus(Task.STATUS_RUNNING);

		if (task.getBeginTime() == 0)
			task.setBeginTime(System.currentTimeMillis());

		taskModel.updateTask(task);

		RemoteFileInfo remoteFile = HttpClientUtils.getRemoteFileInfo(task
				.getFileUrl(), preferenceManager.getRetryTimes(), preferenceManager.getRetryDelay() * 1000,
				new Properties(), task, "Task");
		if (remoteFile == null) {
			task.writeMessage("Task", "无法获取目标文件信息！");
			task.setStatus(Task.STATUS_ERROR);
			taskModel.updateTask(task);
			return;
		}

		checkFile(remoteFile.getFileName(), remoteFile.getFileSize());
		task.checkBlocks();
		taskModel.updateTask(task);

		// 临时文件
		File file = task.getTempSavedFile();
		RandomAccessFile savedFile = null;
		try {
			savedFile = new RandomAccessFile(file, "rw");
			task.writeMessage("Task", "打开临时文件" + file);

			int ct = 0;// 启动的线程数量
			for (Iterator iter = task.getSplitters().iterator(); iter.hasNext();) {
				ct++;
				if (ct > task.getBlocks())// 如果启动的线程数量大于设置的数量，则不再启动新的线程
					break;

				TaskSplitter s = (TaskSplitter) iter.next();
				if (!s.isFinish()) {
					DownloadThread t = new DownloadThread(task, savedFile, s);
					threads.add(t);
					t.start();
					task.writeMessage("Task", "启动下载线程" + s.getName());
					Thread.sleep(500);
				}
			}

			long lastTime = System.currentTimeMillis();
			long lastSize = task.getFinishedSize();
			int lastRunBlocks = task.getRunBlocks();
			// 检查线程状态
			while (task.getStatus() == Task.STATUS_RUNNING
					&& !this.isInterrupted()) {
				long currentSize = 0;
				boolean finished = true;

				// 检查各个块的状态,计算总完成量
				for (Iterator it = task.getSplitters().iterator(); it.hasNext();) {
					TaskSplitter splitter = (TaskSplitter) it.next();
					currentSize += splitter.getFinished();
					// if (splitter.isRun()) {
					// finished = false;
					// }
					// 只要有一个块未下载完,则不能设置为完成状态
					if (!splitter.isFinish()) {
						finished = false;
					}
				}

				// 总耗时计算
				long current = System.currentTimeMillis();
				long timeDiff = current - lastTime;
				task.setTotalTime(task.getTotalTime() + timeDiff);
				lastTime = current;

				task.setFinishedSize(currentSize);
				// 计算即时速度
				long sizeDiff = currentSize - lastSize;
				lastSize = currentSize;
				if (timeDiff > 0) {
					task.setSpeed(sizeDiff / timeDiff);
					lastSize = task.getFinishedSize();
				}

				if (finished)
					task.setStatus(Task.STATUS_FINISHED);

				taskModel.updateTask(task);
				// checkTaskStatus();

				// 检查是否有未完成的任务块,有则启动
				TaskSplitter s = task.getUnfinishedSplitter();
				if (s != null) {
					DownloadThread t = new DownloadThread(task, savedFile, s);
					threads.add(t);
					t.start();
					task.writeMessage("Task", "启动下载线程" + s.getName());
				}

				sleep(1000);
			}

			/**
			 * 任务线程被停止或者打断时中断所有下载线程
			 */
			if (task.getStatus() == Task.STATUS_STOP || this.isInterrupted()) {
				logger.info("下载停止");
				task.writeMessage("Task", "下载停止");
				changeStatus(Task.STATUS_STOP);
				return;
			}

			task.setFinishTime(System.currentTimeMillis());
			savedFile.close();
			task.renameSavedFile();
			changeStatus(Task.STATUS_FINISHED);
			logger.info("下载完成");
			task.writeMessage("Task", "下载完成");
		} catch (Exception e) {
			e.printStackTrace();
			changeStatus(Task.STATUS_ERROR);
		} finally {
			if (savedFile != null) {
				try {
					savedFile.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			task.clearMessage();
			taskModel.updateTask(task);
		}

	}

}
