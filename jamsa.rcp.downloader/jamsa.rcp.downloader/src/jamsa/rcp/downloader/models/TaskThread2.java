package jamsa.rcp.downloader.models;

import jamsa.rcp.downloader.utils.FileUtils;
import jamsa.rcp.downloader.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ���߳������е������߳�,���ڿ������������߳�
 * 
 * @author ���
 * 
 */
public class TaskThread2 extends Thread {
	private static Logger logger = new Logger(TaskThread2.class);

	// ÿ������С
	private static final long BLOCK_MIN_SIZE = 100000;

	private static final String FILENAME_DOWNLOAD_SUFFIX = ".GET";

	private static final String FILENAME_SUFFIX = "_1";

	private Task task;

	public TaskThread2(Task task) {
		this.task = task;
	}

	// �޸�����״̬
	private void changeStatus(int status) {
		task.setStatus(status);
	}

	// Ĭ�Ϸֳ�5������
	// private int block = 5;

	// �����߳�
	private List threads = new ArrayList(10);

	// �Զ��ָ�����
	private void split() {
		// Ĭ�Ϸֳ�5������
		int block = task.getBlocks();
		long fileSize = task.getFileSize();

		// �������δ�ָ����Ҫ�ָ�����
		if (task.getSplitters().isEmpty()) {
			task.writeMessage("Task", "�ָ�����");
			// �����õĿ��
			long blockSize = fileSize / block;

			// ���ÿ��Ĵ�С��С����С�����ƣ�����С�����ƽ��зָ�
			if (blockSize < BLOCK_MIN_SIZE) {
				for (int i = 0; i < ++block; i++) {
					boolean finished = false;// �ָ����
					long startPos = i * BLOCK_MIN_SIZE;
					long endPos = (i + 1) * BLOCK_MIN_SIZE;
					// �������λ�ã����ڻ�����ļ���С�����ٷ��µĿ�
					if (endPos >= fileSize) {
						endPos = fileSize;
						finished = true;
					}

					task.addSplitter(new TaskSplitter(startPos, endPos, 0, task
							.getSplitters().size()
							+ ""));
					if (finished)
						break;
				}

				return;
			}

			// �����ķָ����(ÿ���С�����ڻ��ߵ�����С�������

			// long blockSize = fileSize / block;
			for (int i = 0; i < (block - 1); i++) {
				task.addSplitter(new TaskSplitter(i * blockSize, (i + 1)
						* blockSize, 0, task.getSplitters().size() + ""));
			}
			task.addSplitter(new TaskSplitter((block - 1) * blockSize,
					fileSize, 0, task.getSplitters().size() + ""));
			return;
		} else if (!task.getSplitters().isEmpty()
				&& task.getSplitters().size() < block && fileSize > 0) {// fileSizeΪ��ı�ʾ�ļ���Сδ֪�����ָܷ�
			// ����Ѿ��ָ���������������µĿ�����(������5���̵߳���Ϊ��10���̣߳��������������߳�!!
			task.writeMessage("Task", "Ϊ�����ӵ������̷߳����");
			// ���������߳�
			for (int i = 0; i < (block - task.getSplitters().size()); i++) {
				List addedSplitters = new ArrayList();
				for (Iterator it = task.getSplitters().iterator(); it.hasNext();) {
					TaskSplitter splitter = (TaskSplitter) it.next();
					// �߳�δ�����
					long unfinished = (splitter.getEndPos()
							- splitter.getStartPos() - splitter.getFinished());
					// ���δ���������������С����ָ�
					long spliteBlock = unfinished / 2;
					if (spliteBlock > BLOCK_MIN_SIZE) {
						long newEndPos = splitter.getStartPos()
								+ splitter.getFinished() + spliteBlock;

						TaskSplitter newSplitter = new TaskSplitter(newEndPos,
								splitter.getEndPos(), 0, task.getSplitters()
										.size()
										+ "");

						splitter.setEndPos(newEndPos);
						addedSplitters.add(newSplitter);
					}
				}

				// �������ӵ����ؿ飬���ӵ�������
				for (Iterator it = addedSplitters.iterator(); it.hasNext();) {
					TaskSplitter splitter = (TaskSplitter) it.next();
					task.addSplitter(splitter);
				}
			}
			return;
		} else if (!task.getSplitters().isEmpty()
				&& task.getSplitters().size() < block
				&& task.getSplitters().size() != 1 && task.getFileSize() == 0) {
			// ֻ�ܵ��߳����أ��Ҵ�Сδ֪
			TaskSplitter splitter = new TaskSplitter(0, 0, 0, task
					.getSplitters().size()
					+ "");
		} else {
			task.writeMessage("Task", "�Ѿ��ָ��������");
		}
	}

	public void run() {
		// �޸�����״̬
		changeStatus(Task.STATUS_RUNNING);
		task.writeMessage("Task", "��������");

		// ��ȡ�ļ���С
		long fileSize = getFileSize();

		if (fileSize < 0) {
			logger.info("�޷���ȡ�ļ���С");
			task.writeMessage("Task", "�޷���ȡ�ļ���С");
			task.setFileSize(0);
			// return;
		}

		if (task.getFileSize() > 0 && task.getFileSize() != fileSize) {
			logger.info("�ļ���С��һ�£��������أ�");
			task.writeMessage("Task", "�ļ���С��һ�£��������أ�");
			task.reset();
			task.setStatus(Task.STATUS_RUNNING);
			// return;
		}

		task.setFileSize(fileSize);
		task.writeMessage("Task", "��ȡ�ļ���С: " + fileSize);

		// ���δ��������ʼʱ�䣬������
		if (task.getBeginTime() == 0)
			task.setBeginTime(System.currentTimeMillis());

		split();
		// ��������״̬
		TaskModel.getInstance().updateTask(task);

		// �����ļ�����Ŀ¼
		FileUtils.createDirectory(task.getFilePath());
		task.writeMessage("Task", "���/����Ŀ¼" + task.getFilePath());
		String fileName = task.getFilePath() + File.separator
				+ task.getFileName();

//		// ����ļ��Ƿ��Ѿ�����
//		while (FileUtils.existsFile(fileName)
//				&& FileUtils.existsFile(fileName + FILENAME_DOWNLOAD_SUFFIX)) {
//			task.setFileName(fileName.substring(0, fileName.indexOf("."))
//					+ FILENAME_SUFFIX
//					+ fileName.substring(fileName.indexOf("."), fileName
//							.length()));
//		}
//		fileName = fileName + FILENAME_DOWNLOAD_SUFFIX;

		RandomAccessFile savedFile = null;

		HttpURLConnection httpConnection = null;
		InputStream input = null;
		try {
			savedFile = new RandomAccessFile(fileName, "rw");
			task.writeMessage("Task", "���ļ�" + fileName);
			// savedFile.setLength(fileSize);
			for (Iterator iter = task.getSplitters().iterator(); iter.hasNext();) {
				TaskSplitter s = (TaskSplitter) iter.next();
				DownloadThread t = new DownloadThread(task, savedFile, s);
				threads.add(t);
				t.start();
				task.writeMessage("Task", "���������߳�" + t.getName());
				Thread.sleep(500);
			}

			long lastTime = System.currentTimeMillis();
			long lastSize = task.getFinishedSize();
			// ����߳�״̬
			while (task.getStatus() == Task.STATUS_RUNNING
					&& !this.isInterrupted()) {
				long currentSize = 0;
				boolean finished = true;
				for (Iterator iter = threads.iterator(); iter.hasNext();) {
					DownloadThread thread = (DownloadThread) iter.next();
					currentSize += thread.getFinishedSize();
					if (thread.isRunn() && thread.isAlive()) {
						finished = false;
						// break;
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

				TaskModel.getInstance().updateTask(task);
				sleep(1000);
			}

			if (task.getStatus() == Task.STATUS_STOP || this.isInterrupted()) {
				logger.info("����ֹͣ");
				task.writeMessage("Task", "����ֹͣ");
				changeStatus(Task.STATUS_STOP);
				// �ж����������߳�
				for (Iterator iter = threads.iterator(); iter.hasNext();) {
					DownloadThread downThread = (DownloadThread) iter.next();
					downThread.interrupt();
				}
				return;
			}
			// if (this.isInterrupted()) {
			// logger.info("�����ж�");
			// changeStatus(Task.STATUS_STOP);
			// return;
			// }

			task.setFinishTime(System.currentTimeMillis());

//			String lastFileName = fileName.substring(0, fileName
//					.lastIndexOf(FILENAME_DOWNLOAD_SUFFIX));
//			while (FileUtils.existsFile(lastFileName)) {
//				lastFileName = lastFileName.substring(0, lastFileName
//						.lastIndexOf("."))
//						+ FILENAME_SUFFIX + lastFileName.substring(
//								+ lastFileName.lastIndexOf("."), lastFileName
//								.length());
//			}
//			task.setFileName(lastFileName);
//			FileUtils.renameFile(fileName, lastFileName);

			changeStatus(Task.STATUS_FINISHED);

			logger.info("�������");
			task.writeMessage("Task", "�������");
		} catch (Exception e) {
			e.printStackTrace();
			changeStatus(Task.STATUS_ERROR);
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			if (savedFile != null) {
				try {
					savedFile.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (httpConnection != null)
				httpConnection.disconnect();
			TaskModel.getInstance().updateTask(task);
		}

	}

	// ����ļ�����
	public long getFileSize() {
		int nFileLength = -1;
		try {
			URL url = new URL(task.getFileUrl());
			HttpURLConnection httpConnection = (HttpURLConnection) url
					.openConnection();
			httpConnection.setRequestProperty("User-Agent", "RCP Get");

			int responseCode = httpConnection.getResponseCode();
			if (responseCode >= 400) {
				// processErrorCode(responseCode);
				return -2; // -2 represent access is error
			}

			String sHeader;
			for (int i = 1;; i++) {
				sHeader = httpConnection.getHeaderFieldKey(i);
				logger.info(sHeader);
				if (sHeader != null) {
					if (sHeader.equals("Content-Length")) {
						nFileLength = Integer.parseInt(httpConnection
								.getHeaderField(sHeader));
						break;
					}
				} else
					break;
			}
			httpConnection.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("�ļ���С��" + nFileLength);
		return nFileLength;
	}

}