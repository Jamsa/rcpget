package jamsa.rcp.downloader.models;

import jamsa.rcp.downloader.http.HttpClientUtils;
import jamsa.rcp.downloader.http.RemoteFileInfo;
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

	public TaskThread2(Task task) {
		this.task = task;
		this.taskModel = TaskModel.getInstance();
	}

	// 修改任务状态
	private void changeStatus(int status) {
		task.setStatus(status);
	}

	// 下载线程
	private List threads = new ArrayList(10);

	public void run() {
		task.writeMessage("Task", "任务启动");
		task.getMessages().clear();
		// 修改任务状态
		changeStatus(Task.STATUS_RUNNING);
		taskModel.updateTask(task);
		try {
			RemoteFileInfo remoteFile = HttpClientUtils.getRemoteFileInfo(task
					.getFileUrl(), 5, 5000, new Properties(), task, "Task");
			if (task.getFileSize() > 0
					&& task.getFileSize() != remoteFile.getFileSize()) {
				task.writeMessage("Task", "文件大小不一致，重新下载！");
				task.reset();
				task.setStatus(Task.STATUS_RUNNING);
			}
			task.setFileSize(remoteFile.getFileSize());

			// 如果文件还未下载，并且远程文件名与本地文件名不一致，则修改本地文件名
			if (task.getFinishedSize() == 0
					&& !StringUtils.isEmpty(remoteFile.getFileName())
					&& !task.getFileName().equals(remoteFile.getFileName())) {
				task.setFileName(remoteFile.getFileName());
			}
		} catch (Exception e) {
			task.setStatus(Task.STATUS_ERROR);
			taskModel.updateTask(task);
			return;
		}

		task.checkBlocks();
		taskModel.updateTask(task);

		// 临时文件对象
		File file = task.getTempSavedFile();
		RandomAccessFile savedFile = null;

		try {
			savedFile = new RandomAccessFile(file, "rw");
			task.writeMessage("Task", "打开文件" + file);
			for (Iterator iter = task.getSplitters().iterator(); iter.hasNext();) {
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
			// 检查线程状态
			while (task.getStatus() == Task.STATUS_RUNNING
					&& !this.isInterrupted()) {
				long currentSize = 0;
				boolean finished = true;
				
				for(Iterator it = task.getSplitters().iterator();it.hasNext();){
					TaskSplitter splitter = (TaskSplitter)it.next();
					currentSize += splitter.getFinished();
					if(splitter.isRun()){
						finished = false;
					}else{
						//如果发现未完成的就启动它 TODO:需要添加更多的检查
						if(!splitter.isFinish()){
							DownloadThread t = new DownloadThread(task, savedFile, splitter);
							threads.add(t);
							t.start();
							task.writeMessage("Task", "启动下载线程" + splitter.getName());
							Thread.sleep(500);
						}
					}
				}

				long current = System.currentTimeMillis();
				long timeDiff = current - lastTime;
				task.setTotalTime(task.getTotalTime() + timeDiff);
				lastTime = current;

				task.setFinishedSize(currentSize);
				long sizeDiff = currentSize - lastSize;
				lastSize = currentSize;

				// long sizeDiff = task.getFinishedSize() - lastSize;
				if (timeDiff > 0) {
					task.setSpeed(sizeDiff / timeDiff);
					lastSize = task.getFinishedSize();
				}

				if (finished)
					task.setStatus(Task.STATUS_FINISHED);

				taskModel.updateTask(task);
				// checkTaskStatus();
				sleep(1000);
			}

			/**
			 * 任务线程被停止或者打断时中断所有下载线程
			 */
			if (task.getStatus() == Task.STATUS_STOP || this.isInterrupted()) {
				logger.info("下载停止");
				task.writeMessage("Task", "下载停止");
				changeStatus(Task.STATUS_STOP);
				// 停止所有下载线程
				for(Iterator it = task.getSplitters().iterator();it.hasNext();){
					TaskSplitter splitter = (TaskSplitter)it.next();
					splitter.setRun(false);
				}
				return;
			}

			task.setFinishTime(System.currentTimeMillis());
			savedFile.close();
			// renameSavedFile(file);
			task.renameSavedFile();

			changeStatus(Task.STATUS_FINISHED);

			logger.info("下载完成");
			task.writeMessage("Task", "下载完成");
		} catch (Exception e) {
			e.printStackTrace();
			changeStatus(Task.STATUS_ERROR);

		} finally {
			taskModel.updateTask(task);
		}

	}

}
