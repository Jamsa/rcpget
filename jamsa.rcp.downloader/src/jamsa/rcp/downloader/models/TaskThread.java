package jamsa.rcp.downloader.models;

import jamsa.rcp.downloader.utils.FileUtils;
import jamsa.rcp.downloader.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * Http单线程下载类
 * 
 * @author 朱杰
 * @deprecated
 */
public class TaskThread extends Thread {
	Logger logger = new Logger(this.getClass());

	private Task task;

	public TaskThread(Task task) {
		this.task = task;
	}

	// public void start() {
	// if (task.getStatus() != Task.STATUS_RUNNING) {
	// synchronized (this) {
	// if (task.getFileSize() == 0)
	// task.setFileSize(getFileSize());
	// // this.run();
	// new Thread(this).start();
	// }
	// }
	// }

	// public void stop() {
	// this.changeStatus(Task.STATUS_STOP);
	// }
	//
	// public String toString() {
	// return super.toString() + "\nFileName: " + task.getFileName()
	// + "\nURL: " + task.getFileUrl();
	// }

	private void changeStatus(int status) {
		task.setStatus(status);
	}

	public void run() {
		changeStatus(Task.STATUS_RUNNING);
		TaskModel.getInstance().updateTask(task);

		task.setFileSize(getFileSize());

		// 保存界面刷新时间
		long refreshTime = System.currentTimeMillis();

		// 保存每次读取耗时
		long cycleTime = System.currentTimeMillis();

		byte[] buf = new byte[1024];
		// 保存每次读取的数据大小
		int readSize;

		// task.setStatus(Task.STATUS_RUNNING);
		FileUtils.createDirectory(task.getFilePath());
		String fileName = task.getFilePath() + File.separator
				+ task.getFileName();

		RandomAccessFile savedFile = null;
		HttpURLConnection httpConnection = null;
		InputStream input = null;
		try {
			savedFile = new RandomAccessFile(fileName, "rw");
			long pos = task.getFinishedSize();
			// 定位文件指针到pos位置
			savedFile.seek(pos);

			URL url = new URL(task.getFileUrl());
			httpConnection = (HttpURLConnection) url.openConnection();

			// 设置User-Agent
			httpConnection.setRequestProperty("User-Agent", "RCP Get");
			// 设置断点续传的开始位置
			httpConnection.setRequestProperty("RANGE", "bytes=" + pos + "-");
			// 获得输入流
			input = httpConnection.getInputStream();

			// 从输入流中读入字节流，然后写到文件中
			while ((readSize = input.read(buf, 0, buf.length)) > 0
					&& task.getStatus() == Task.STATUS_RUNNING
					&& !this.isInterrupted()) {
				savedFile.write(buf, 0, readSize);
				Arrays.fill(buf, (byte) 0);

				// ======每次循环中都要计算的数据=======
				// 设置已经下载的大小
				task.setFinishedSize(task.getFinishedSize() + readSize);
				long current = System.currentTimeMillis();
				long costTime = current - cycleTime;
				if (costTime > 0) {
					// 设置总耗时
					task.setTotalTime(task.getTotalTime() + costTime);
					cycleTime = current;
				}
				// ======每次循环中都要计算的数据=======

				// 1秒左右刷新一次界面
				// if ((current - refreshTime) > 1000) {
				if (task.getTotalTime() != 0)
					task.setSpeed(task.getFinishedSize() / task.getTotalTime());
				refreshTime = current;
				TaskModel.getInstance().updateTask(task);
				// }
				Thread.yield();
			}

			if (task.getStatus() == Task.STATUS_STOP) {
				logger.info("下载停止");
				return;
			}
			if (this.isInterrupted()) {
				logger.info("下载中断");
				changeStatus(Task.STATUS_STOP);
				return;
			}

			changeStatus(Task.STATUS_FINISHED);
			// task.setStatus(Task.STATUS_FINISHED);
			logger.info("下载完成");

		} catch (Exception e) {
			e.printStackTrace();
			changeStatus(Task.STATUS_ERROR);
			// task.setStatus(Task.STATUS_ERROR);
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

		// HttpURLConnection conn = null;
		// BufferedInputStream bis = null;
		// FileOutputStream fos = null;
		// try {
		// URL url = new URL(task.getFileUrl());
		// conn = (HttpURLConnection) url.openConnection();
		// bis = new BufferedInputStream(conn.getInputStream());
		// String fullName = task.getFilePath() + File.separator
		// + task.getFileName();
		// File file = new File(fullName);
		//
		// fos = new FileOutputStream(file);
		// byte[] buf = new byte[1024];
		//
		// // 获取文件大小
		// task.setFileSize(this.getFileSize());
		//
		// // 保存界面刷新时间
		// long refresh = System.currentTimeMillis();
		//
		// // 保存每次读取耗时
		// long timeCountStart = System.currentTimeMillis();
		//
		// // 保存每次读取的数据大小
		// int size = 0;
		// // 保存文件
		// while ((size = bis.read(buf)) != -1
		// && (task.getStatus() == Task.STATUS_RUNNING)) {
		// fos.write(buf, 0, size);
		// Arrays.fill(buf, (byte) 0);
		// task.setFinishedSize(task.getFinishedSize() + size);
		//
		// // 1秒左右刷新一次界面
		// if ((System.currentTimeMillis() - refresh) > 1000) {
		// if (task.getTotalTime() != 0)
		// task.setSpeed(task.getFinishedSize()
		// / task.getTotalTime());
		// refresh = System.currentTimeMillis();
		// TaskModel.getInstance().updateCategory(task);
		// }
		//
		// long timeCountStop = System.currentTimeMillis();
		// long diff = timeCountStop - timeCountStart;
		// if (diff > 0) {
		// // 设置总耗时
		// task.setTotalTime(task.getTotalTime() + diff);
		// timeCountStart = timeCountStop;
		// }
		// Thread.yield();
		// }
		// fos.flush();
		// fos.close();
		// bis.close();
		// conn.disconnect();
		//
		// // 检查是否为用户取消
		// if (this.task.getStatus() == Task.STATUS_STOP) {
		// return;
		// }
		//
		// // 下载完成
		// this.task.setStatus(Task.STATUS_FINISHED);
		// } catch (MalformedURLException e) {
		// e.printStackTrace();
		// this.task.setStatus(Task.STATUS_ERROR);
		// } catch (IOException ioe) {
		// ioe.printStackTrace();
		// this.task.setStatus(Task.STATUS_ERROR);
		// } finally {
		// if (conn != null)
		// conn.disconnect();
		// if (bis != null)
		// try {
		// bis.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// if (fos != null)
		// try {
		// fos.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
	}

	// public void changeStatus(int status) {
	// this.task.setStatus(status);
	// this.setChanged();
	// Logger.info("观察者数量：" + this.countObservers());
	// this.notifyObservers(this.task);
	// }

	// public int getStatus() {
	// return this.task.getStatus();
	// }

	// 获得文件长度
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

		// Utility.log(nFileLength);
		return nFileLength;
	}

}
