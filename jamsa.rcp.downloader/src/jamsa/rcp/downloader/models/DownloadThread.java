package jamsa.rcp.downloader.models;

import jamsa.rcp.downloader.utils.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Iterator;

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

	/**
	 * 连接HttpURL 自动处理连接重试
	 * 
	 * @param url
	 * @return
	 */
	private HttpURLConnection getConnection(String httpUrl) {
		URL url = null;
		try {
			url = new URL(httpUrl);
		} catch (MalformedURLException e) {
			task.writeMessage(this.splitter.getName(), "未知协议！");
			return null;
		}

		HttpURLConnection conn = null;
		// 重试计数器
		int times = 0;

		while ((conn == null || RETRY_TIMES == 0 )//|| times <= RETRY_TIMES bug 如果不重试，则这个下载任务将不能完成
				&& !this.isInterrupted()) {
			try {
				conn = (HttpURLConnection) url.openConnection();
				// 设置User-Agent
				conn.setRequestProperty("User-Agent", "RCP Get");

				// 设置断点续传的开始和结束位置
				if (splitter.getEndPos() != 0) {
					conn.setRequestProperty("RANGE", "bytes="
							+ splitter.getStartPos() + "-"
							+ splitter.getEndPos());
				} else {
					conn.setRequestProperty("RANGE", "bytes="
							+ splitter.getStartPos() + "-");
				}
				times++;
				this.printResponseHeader(conn);
				if (conn.getHeaderField("Connection").equals("close")) {
					if (conn != null) {
						conn.disconnect();
						conn = null;
					}
				}
				if (conn == null) {
					task.writeMessage(this.splitter.getName(), "连接错误！"
							+ RETRY_DELAY / 1000 + "秒后，重试第" + times + "次...");
					Thread.sleep(2000);
				}

			} catch (IOException e) {
				task.writeMessage(this.splitter.getName(), "连接错误！重试第" + times
						+ "次...");
			} catch (InterruptedException e) {
				if (conn != null) {
					conn.disconnect();
				}
				return null;
			}

			// 打印响应头
			task.writeMessage(this.splitter.getName(), "第" + times + "次连接");

		}
		return conn;

	}

	// 显示Http头信息
	private void printResponseHeader(URLConnection conn) {
		for (Iterator iter = conn.getHeaderFields().keySet().iterator(); iter
				.hasNext();) {
			String key = (String) iter.next();
			logger.info(this.getName() + ": " + key + " : "
					+ conn.getHeaderField(key));
			task.writeMessage(splitter.getName(), key + " : "
					+ conn.getHeaderField(key));
		}
	}

	public void run() {
		runn = true;
		HttpURLConnection conn = null;
		InputStream input = null;
		try {
			conn = (HttpURLConnection) this.getConnection(task.getFileUrl());
			if (conn == null) {
				task.writeMessage(splitter.getName(), "连接失败!");
				return;
			}else{
				task.writeMessage(this.splitter.getName(), "连接成功！");
			}
			// 获得输入流
			input = conn.getInputStream();
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
			runn = false;
			logger.info(splitter.getName() + "线程任务完成!");
			task.writeMessage(splitter.getName(), "线程任务完成!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (conn != null) {
				conn.disconnect();
			}
			logger.info(splitter.getName() + ": 停止");
			task.writeMessage(splitter.getName(), "停止");
		}
	}
}
