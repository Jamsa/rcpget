package jamsa.rcp.downloader.models;

import jamsa.rcp.downloader.http.HttpClientUtils;
import jamsa.rcp.downloader.utils.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Properties;

/**
 * 下载线程
 * 
 * @author 朱杰
 * 
 */
public class DownloadThread extends Thread {
	private static final Logger logger = new Logger(DownloadThread.class);

	// 5秒后重试
	private static int RETRY_DELAY = 2000;

	// 重试10次，如果为0则一直重试下去
	private static int RETRY_TIMES = 10;

	// 文件对象
	private RandomAccessFile file;

	// 任务对象
	private Task task;

	private TaskSplitter splitter;

	// 状态
	private boolean runn = false;

	// 是否处于wait状态
	private boolean wait = false;

	/**
	 * 构造器
	 * 
	 * @param task
	 *            任务对象
	 * @param file
	 *            下载数据将写入的文件
	 * @param splitter
	 *            本纯种下载的块信息
	 */
	public DownloadThread(Task task, RandomAccessFile file,
			TaskSplitter splitter) {
		this.file = file;
		this.task = task;
		this.splitter = splitter;
	}

	/**
	 * 获取下载线程状态
	 * 
	 * @return
	 */
	public boolean isRunn() {
		return runn;
	}

	/**
	 * 设置下载线程状态
	 * 
	 * @param runn
	 */
	public void setRunn(boolean runn) {
		this.runn = runn;
	}

	/**
	 * 获取线程完成量，这个量并不只是本次启动后完成的量
	 * 
	 * @return
	 */
	public long getFinishedSize() {
		return this.splitter.getFinished();
	}

	public InputStream getInputStream() throws IOException {
		HttpURLConnection conn = null;
		Properties prop = new Properties();
		prop.put("User-Agent", "RCP Get");

		if (splitter.getEndPos() != 0) {
			prop.put("RANGE", "bytes=" + splitter.getStartPos() + "-"
					+ splitter.getEndPos());
		} else {
			prop.put("RANGE", "bytes=" + splitter.getStartPos() + "-");
		}

		try {
			while (conn == null) {
				conn = HttpClientUtils.getHttpURLConnection(task.getFileUrl(),
						RETRY_TIMES, RETRY_DELAY, prop, task, splitter
								.getName());
				// 如果conn为null则等待，其它线程完成时将唤醒这些等侍状态的线程
				if (conn == null) {
					task.writeMessage(splitter.getName(), "连接被拒绝，等侍其它线程!");
					wait = true;
					wait();
					task.writeMessage(splitter.getName(), "连接被唤醒！重新连接！");
					wait = false;
				}
			}
		} catch (Exception e) {
			runn = false;
			wait = false;
			task.writeMessage(splitter.getName(), e.getLocalizedMessage());
			return null;
		}
		return conn.getInputStream();
	}

	public void run() {
		runn = true;
		InputStream input = null;
		try {
			input = getInputStream();
			if (input == null) {
				return;
			}

			task.writeMessage(splitter.getName(), "开始读取数据...");
			// 每次从流中读取大小
			int size = 0;
			// 流缓存
			byte[] buf = new byte[2048];
			while ((size = input.read(buf, 0, buf.length)) > 0
					&& runn
					&& !this.isInterrupted()
					&& (((splitter.getFinished() + splitter.getStartPos()) < splitter
							.getEndPos()) || splitter.getEndPos() == 0)) {// 结束位置为0表示大小未知
				int pos = Integer.parseInt((splitter.getStartPos() + splitter
						.getFinished())
						+ "");
				// 写入文件
				synchronized (file) {
					file.seek(pos);
					file.write(buf, 0, size);
				}
				// 修改线程任务完成量
				splitter.setFinished(splitter.getFinished() + size);
				// 清空流缓存
				Arrays.fill(buf, (byte) 0);
				sleep(10);
			}
			logger.info(splitter.getName() + "线程任务完成!");
			task.writeMessage(splitter.getName(), "线程任务完成!");
			if (!this.isInterrupted())
				notifyAll();
		} catch (IOException e) {
			task.writeMessage(splitter.getName(), "流操作异常："
					+ e.getLocalizedMessage());
			
			return;
		} catch (InterruptedException e) {
			task.writeMessage(splitter.getName(), "线程被中断！");
			
			return;
		} finally {
			wait = false;
			runn = false;
			try {
				if (input != null)
					input.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
