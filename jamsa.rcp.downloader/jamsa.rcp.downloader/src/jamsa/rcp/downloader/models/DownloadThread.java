package jamsa.rcp.downloader.models;

import jamsa.rcp.downloader.utils.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Iterator;

/**
 * �����߳�
 * 
 * @author ���
 * 
 */
public class DownloadThread extends Thread {
	private static final Logger logger = new Logger(DownloadThread.class);

	// 5�������
	private static int RETRY_DELAY = 5000;

	// ����10�Σ����Ϊ0��һֱ������ȥ
	private static int RETRY_TIMES = 10;

	// �ļ�����
	private RandomAccessFile file;

	// �������
	private Task task;

	private TaskSplitter splitter;

	// ״̬
	private boolean runn = false;

	public DownloadThread(Task task, RandomAccessFile file,
			TaskSplitter splitter) {
		this.file = file;
		this.task = task;
		this.splitter = splitter;
	}

	public boolean isRunn() {
		return runn;
	}

	public void setRunn(boolean runn) {
		this.runn = runn;
	}

	public long getFinishedSize() {
		return this.splitter.getFinished();
	}

	/**
	 * ����HttpURL �Զ�������������
	 * 
	 * @param url
	 * @return
	 */
	private URLConnection openConnection(URL url) {
		URLConnection conn = null;
		int times = 0;

		while (conn == null && (RETRY_TIMES == 0 || times <= RETRY_TIMES)
				&& !this.isInterrupted()) {
			try {
				conn = url.openConnection();
				times++;
			} catch (IOException e) {
				task.writeMessage(this.splitter.getName(), "���Ӵ������Ե�" + times
						+ "��...");
			}
		}
		task.writeMessage(this.splitter.getName(), "���ӳɹ���");
		return conn;

	}

	// ��ʾHttpͷ��Ϣ
	public void printResponseHeader(URLConnection conn) {
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
			URL url = new URL(task.getFileUrl());

			// conn = (HttpURLConnection) url.openConnection();
			conn = (HttpURLConnection) this.openConnection(url);
			if(conn==null){
				task.writeMessage(splitter.getName(), "����ʧ��!");
				return;
			}

			// ����User-Agent
			conn.setRequestProperty("User-Agent", "RCP Get");

			// ���öϵ������Ŀ�ʼ�ͽ���λ��
			if (splitter.getEndPos() != 0) {
				conn.setRequestProperty("RANGE", "bytes="
						+ splitter.getStartPos() + "-" + splitter.getEndPos());
			} else {
				conn.setRequestProperty("RANGE", "bytes="
						+ splitter.getStartPos() + "-");
			}
			// ���������
			input = conn.getInputStream();

			// ��ӡ��Ӧͷ
			this.printResponseHeader(conn);

			logger.info(this.getName() + ": ��ʼ��ȡ����...");
			task.writeMessage(splitter.getName(), "��ʼ��ȡ����...");

			// ÿ�δ����ж�ȡ��С
			int size = 0;
			// ����ȡ����
			byte[] buf = new byte[2048];
			while ((size = input.read(buf, 0, buf.length)) > 0
					&& runn
					&& !this.isInterrupted()
					&& (((splitter.getFinished() + splitter.getStartPos()) < splitter
							.getEndPos()) || splitter.getEndPos() == 0)) {// ����λ��Ϊ0��ʾ��Сδ֪

				int pos = Integer.parseInt((splitter.getStartPos() + splitter
						.getFinished())
						+ "");
				// д���ļ�
				synchronized (file) {
					file.seek(pos);
					file.write(buf, 0, size);
					// logger.info("д��"+size+"byte");
				}
				// �޸��߳����������
				splitter.setFinished(splitter.getFinished() + size);
				// ���������
				Arrays.fill(buf, (byte) 0);
				sleep(10);
			}
			runn = false;

			logger.info(this.getName() + "�߳��������!");
			task.writeMessage(splitter.getName(), "�߳��������!");
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
			logger.info(this.getName() + ": ֹͣ");
			task.writeMessage(splitter.getName(), "ֹͣ");
		}
	}
}
