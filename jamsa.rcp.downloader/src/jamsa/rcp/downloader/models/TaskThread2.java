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
 * ���߳������е������߳�,���ڿ������������߳�
 * 
 * @author ���
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

	// �޸�����״̬
	private void changeStatus(int status) {
		task.setStatus(status);
	}

	// �����߳�
	private List threads = new ArrayList(10);

	public void run() {
		task.writeMessage("Task", "��������");
		task.getMessages().clear();
		// �޸�����״̬
		changeStatus(Task.STATUS_RUNNING);
		taskModel.updateTask(task);
		try {
			RemoteFileInfo remoteFile = HttpClientUtils.getRemoteFileInfo(task
					.getFileUrl(), 5, 5000, new Properties(), task, "Task");
			if (task.getFileSize() > 0
					&& task.getFileSize() != remoteFile.getFileSize()) {
				task.writeMessage("Task", "�ļ���С��һ�£��������أ�");
				task.reset();
				task.setStatus(Task.STATUS_RUNNING);
			}
			task.setFileSize(remoteFile.getFileSize());

			// ����ļ���δ���أ�����Զ���ļ����뱾���ļ�����һ�£����޸ı����ļ���
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

		// ��ʱ�ļ�����
		File file = task.getTempSavedFile();
		RandomAccessFile savedFile = null;

		try {
			savedFile = new RandomAccessFile(file, "rw");
			task.writeMessage("Task", "���ļ�" + file);
			for (Iterator iter = task.getSplitters().iterator(); iter.hasNext();) {
				TaskSplitter s = (TaskSplitter) iter.next();
				if (!s.isFinish()) {
					DownloadThread t = new DownloadThread(task, savedFile, s);
					threads.add(t);
					t.start();
					task.writeMessage("Task", "���������߳�" + s.getName());
					Thread.sleep(500);
				}
			}

			long lastTime = System.currentTimeMillis();
			long lastSize = task.getFinishedSize();
			// ����߳�״̬
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
						//�������δ��ɵľ������� TODO:��Ҫ��Ӹ���ļ��
						if(!splitter.isFinish()){
							DownloadThread t = new DownloadThread(task, savedFile, splitter);
							threads.add(t);
							t.start();
							task.writeMessage("Task", "���������߳�" + splitter.getName());
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
			 * �����̱߳�ֹͣ���ߴ��ʱ�ж����������߳�
			 */
			if (task.getStatus() == Task.STATUS_STOP || this.isInterrupted()) {
				logger.info("����ֹͣ");
				task.writeMessage("Task", "����ֹͣ");
				changeStatus(Task.STATUS_STOP);
				// ֹͣ���������߳�
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

			logger.info("�������");
			task.writeMessage("Task", "�������");
		} catch (Exception e) {
			e.printStackTrace();
			changeStatus(Task.STATUS_ERROR);

		} finally {
			taskModel.updateTask(task);
		}

	}

}
