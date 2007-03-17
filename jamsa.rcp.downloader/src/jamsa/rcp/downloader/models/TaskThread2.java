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
 * 多线程下载中的任务线程,用于控制其它下载线程
 * 
 * @author 朱杰
 * 
 */
public class TaskThread2 extends Thread {
	private static Logger logger = new Logger(TaskThread2.class);

	// 每个块最小
	private static final long BLOCK_MIN_SIZE = 100000;

	public static final String FILENAME_DOWNLOAD_SUFFIX = ".GET";

	public static final String FILENAME_SUFFIX = "_1";

	private Task task;

	public TaskThread2(Task task) {
		this.task = task;
	}

	// 修改任务状态
	private void changeStatus(int status) {
		task.setStatus(status);
	}

	// 下载线程
	private List threads = new ArrayList(10);

	/**
	 * 自动分割任务
	 * 
	 */
	private void split() {
		// 默认分成5块下载
		int block = task.getBlocks();
		long fileSize = task.getFileSize();

		// 如果任务未分割过则要分割任务
		if (task.getSplitters().isEmpty()) {
			task.writeMessage("Task", "分割任务");
			// 按设置的块分
			long blockSize = fileSize / block;

			// 如果每块的大小，小于最小块限制，则按最小块限制进行分割
			if (blockSize < BLOCK_MIN_SIZE) {
				for (int i = 0; i < ++block; i++) {
					boolean finished = false;// 分割完成
					long startPos = i * BLOCK_MIN_SIZE;
					long endPos = (i + 1) * BLOCK_MIN_SIZE;
					// 如果结束位置，大于或等于文件大小，则不再分新的块
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

			// 正常的分割情况(每块大小，大于或者等于最小块的限制

			// long blockSize = fileSize / block;
			for (int i = 0; i < (block - 1); i++) {
				task.addSplitter(new TaskSplitter(i * blockSize, (i + 1)
						* blockSize, 0, task.getSplitters().size() + ""));
			}
			task.addSplitter(new TaskSplitter((block - 1) * blockSize,
					fileSize, 0, task.getSplitters().size() + ""));
			return;
		} else if (!task.getSplitters().isEmpty()
				&& task.getSplitters().size() < block && fileSize > 0) {// fileSize为零的表示文件大小未知，不能分割
			// 如果已经分割过，但又设置了新的块数量(比如由5个线程调整为了10个线程）。不允许减少线程!!
			task.writeMessage("Task", "为新增加的下载线程分配块");
			// 添加下载线程
			for (int i = 0; i < (block - task.getSplitters().size()); i++) {
				List addedSplitters = new ArrayList();
				for (Iterator it = task.getSplitters().iterator(); it.hasNext();) {
					TaskSplitter splitter = (TaskSplitter) it.next();
					// 线程未完成量
					long unfinished = (splitter.getEndPos()
							- splitter.getStartPos() - splitter.getFinished());
					// 如果未完成量除２大于最小块则分割
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

				// 将新增加的下载块，添加到任务中
				for (Iterator it = addedSplitters.iterator(); it.hasNext();) {
					TaskSplitter splitter = (TaskSplitter) it.next();
					task.addSplitter(splitter);
				}
			}
			return;
		} else if (!task.getSplitters().isEmpty()
				&& task.getSplitters().size() < block
				&& task.getSplitters().size() != 1 && task.getFileSize() == 0) {
			// 只能单线程下载，且大小未知
			TaskSplitter splitter = new TaskSplitter(0, 0, 0, task
					.getSplitters().size()
					+ "");
		} else {
			task.writeMessage("Task", "已经分割过的任务");
		}
	}

	public void run() {
		task.writeMessage("Task", "任务启动");
		// 修改任务状态
		changeStatus(Task.STATUS_RUNNING);
		setFileSize();
		setBeginTime();
		split();
		File file = getSavedFile();
		TaskModel.getInstance().updateTask(task);

		RandomAccessFile savedFile = null;

		HttpURLConnection httpConnection = null;
		InputStream input = null;
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

			/**
			 * 任务线程被停止或者打断时中断所有下载线程
			 */
			if (task.getStatus() == Task.STATUS_STOP || this.isInterrupted()) {
				logger.info("下载停止");
				task.writeMessage("Task", "下载停止");
				changeStatus(Task.STATUS_STOP);
				// 中断所有下载线程
				for (Iterator iter = threads.iterator(); iter.hasNext();) {
					DownloadThread downThread = (DownloadThread) iter.next();
					downThread.interrupt();
				}
				return;
			}

			task.setFinishTime(System.currentTimeMillis());
			savedFile.close();
			renameSavedFile(file);

			changeStatus(Task.STATUS_FINISHED);
			logger.info("下载完成");
			task.writeMessage("Task", "下载完成");
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

	/**
	 * 下载完成后，重命名文件
	 * 
	 * @param savedFile
	 */
	private void renameSavedFile(File savedFile) {
		String finalFileName = task.getFilePath() + File.separator
				+ task.getFileName();
		while (FileUtils.existsFile(finalFileName)) {
			String name = task.getFileName();
			int length = name.length();
			int idx = name.lastIndexOf(".");
			name = name.substring(0, idx) + FILENAME_SUFFIX
					+ name.substring(idx, length);
			task.setFileName(name);

			finalFileName = task.getFilePath() + File.separator
					+ task.getFileName();
		}
		savedFile.renameTo(new File(finalFileName));
	}

	/**
	 * 获取保存的临时文件名
	 * 
	 * @return
	 */
	private File getSavedFile() {
		// 创建文件保存目录
		FileUtils.createDirectory(task.getFilePath());
		task.writeMessage("Task", "检查/创建目录" + task.getFilePath());
		String fileName = task.getFilePath() + File.separator
				+ task.getFileName();

		// 检查文件是否已经存在
		while (FileUtils.existsFile(fileName)) {
			String name = task.getFileName();
			int length = name.length();
			int idx = name.lastIndexOf(".");
			name = name.substring(0, idx) + FILENAME_SUFFIX
					+ name.substring(idx, length);
			task.setFileName(name);
			fileName = task.getFilePath() + File.separator + task.getFileName();
		}
		// 修改任务文件名
		fileName += FILENAME_DOWNLOAD_SUFFIX;
		return new File(fileName);
	}

	/**
	 * 设置任务开始时间
	 * 
	 */
	private void setBeginTime() {
		// 如果未设置任务开始时间，则设置
		if (task.getBeginTime() == 0)
			task.setBeginTime(System.currentTimeMillis());
	}

	/**
	 * 设置任务文件大小
	 * 
	 */
	private void setFileSize() {
		// 获取文件大小
		long fileSize = getRemoteFileSize();

		if (fileSize < 0) {
			logger.info("无法获取文件大小");
			task.writeMessage("Task", "无法获取文件大小");
			task.setFileSize(0);
			// return;
		}

		if (task.getFileSize() > 0 && task.getFileSize() != fileSize) {
			logger.info("文件大小不一致，重新下载！");
			task.writeMessage("Task", "文件大小不一致，重新下载！");
			task.reset();
			task.setStatus(Task.STATUS_RUNNING);
			// return;
		}

		task.setFileSize(fileSize);
		task.writeMessage("Task", "获取文件大小: " + fileSize);
	}

	/**
	 * 获取远程文件的大小
	 * 
	 * @return
	 */
	private long getRemoteFileSize() {
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

		logger.info("文件大小：" + nFileLength);
		return nFileLength;
	}

}
