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
 * �����߳�
 * 
 * @author ���
 * 
 */
public class DownloadThread extends Thread {
	private static final Logger logger = new Logger(DownloadThread.class);

	// 5�������
	private static int RETRY_DELAY = 2000;

	// ����10�Σ����Ϊ0��һֱ������ȥ
	private static int RETRY_TIMES = 10;

	// �ļ�����
	private RandomAccessFile file;

	// �������
	private Task task;

	private TaskSplitter splitter;

	// ״̬
	private boolean runn = false;

	/**
	 * ������
	 * 
	 * @param task
	 *            �������
	 * @param file
	 *            �������ݽ�д����ļ�
	 * @param splitter
	 *            ���������صĿ���Ϣ
	 */
	public DownloadThread(Task task, RandomAccessFile file,
			TaskSplitter splitter) {
		this.file = file;
		this.task = task;
		this.splitter = splitter;
	}

	/**
	 * ��ȡ�����߳�״̬
	 * 
	 * @return
	 */
	public boolean isRunn() {
		return runn;
	}

	/**
	 * ���������߳�״̬
	 * 
	 * @param runn
	 */
	public void setRunn(boolean runn) {
		this.runn = runn;
	}

	/**
	 * ��ȡ�߳�����������������ֻ�Ǳ�����������ɵ���
	 * 
	 * @return
	 */
	public long getFinishedSize() {
		return this.splitter.getFinished();
	}

	/**
	 * ����HttpURL �Զ�������������
	 * 
	 * @param url
	 * @return
	 */
	private HttpURLConnection getConnection(String httpUrl) {
		URL url = null;
		try {
			url = new URL(httpUrl);
		} catch (MalformedURLException e) {
			task.writeMessage(this.splitter.getName(), "δ֪Э�飡");
			return null;
		}

		HttpURLConnection conn = null;
		// ���Լ�����
		int times = 0;

		while ((conn == null || RETRY_TIMES == 0 )//|| times <= RETRY_TIMES bug ��������ԣ�������������񽫲������
				&& !this.isInterrupted()) {
			try {
				conn = (HttpURLConnection) url.openConnection();
				// ����User-Agent
				conn.setRequestProperty("User-Agent", "RCP Get");

				// ���öϵ������Ŀ�ʼ�ͽ���λ��
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
					task.writeMessage(this.splitter.getName(), "���Ӵ���"
							+ RETRY_DELAY / 1000 + "������Ե�" + times + "��...");
					Thread.sleep(2000);
				}

			} catch (IOException e) {
				task.writeMessage(this.splitter.getName(), "���Ӵ������Ե�" + times
						+ "��...");
			} catch (InterruptedException e) {
				if (conn != null) {
					conn.disconnect();
				}
				return null;
			}

			// ��ӡ��Ӧͷ
			task.writeMessage(this.splitter.getName(), "��" + times + "������");

		}
		return conn;

	}

	// ��ʾHttpͷ��Ϣ
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
				task.writeMessage(splitter.getName(), "����ʧ��!");
				return;
			}else{
				task.writeMessage(this.splitter.getName(), "���ӳɹ���");
			}
			// ���������
			input = conn.getInputStream();
			task.writeMessage(splitter.getName(), "��ʼ��ȡ����...");
			
			// ÿ�δ����ж�ȡ��С
			int size = 0;
			// ������
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
				}
				// �޸��߳����������
				splitter.setFinished(splitter.getFinished() + size);
				// ���������
				Arrays.fill(buf, (byte) 0);
				sleep(10);
			}
			runn = false;
			logger.info(splitter.getName() + "�߳��������!");
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
			logger.info(splitter.getName() + ": ֹͣ");
			task.writeMessage(splitter.getName(), "ֹͣ");
		}
	}
}
