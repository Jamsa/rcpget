package jamsa.rcp.downloader.models;

import jamsa.rcp.downloader.http.HttpClientUtils;
import jamsa.rcp.downloader.preference.PreferenceManager;
import jamsa.rcp.downloader.utils.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Properties;

/**
 * �����߳�
 * 
 * @author ���
 * 
 */
public class DownloadThread extends Thread {
	private static final Logger logger = new Logger(DownloadThread.class);

	private PreferenceManager pm;

	// �ļ�����
	private RandomAccessFile file;
	

	// �������
	private Task task;

	/**
	 * ��ǰ���ؿ�
	 */
	private TaskSplitter splitter;

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
		pm = PreferenceManager.getInstance();
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
	 * ��ȡԶ���ļ�������
	 * 
	 * @return
	 */
	public InputStream getInputStream() {
		Properties prop = new Properties();
		// prop.put("User-Agent", "RCP Get");
		prop.put("User-Agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");

		if (splitter.getEndPos() != 0) {
			prop.put("RANGE", "bytes=" + (splitter.getStartPos()+splitter.getFinished()) + "-"
					+ splitter.getEndPos());
		} else {
			prop.put("RANGE", "bytes=" + splitter.getStartPos() + "-");
		}
		return HttpClientUtils.getInputStream(task.getFileUrl(), pm
				.getRetryTimes(), pm.getRetryDelay() * 1000,
				pm.getTimeout() * 1000, prop, "GET", task, splitter.getName());
	}

	public void run() {
		splitter.setRun(true);
		InputStream input = getInputStream();
		if (input == null) {
			task.writeMessage(splitter.getName(), "��ȡ����Զ���ļ������������߳���ֹ!");
			splitter.setRun(false);
			return;
		}
		try {
			task.writeMessage(splitter.getName(), "��ʼ��ȡ����...");
			// ÿ�δ����ж�ȡ��С
			int size = 0;
			// ������
			byte[] buf = new byte[2048];
			while ((size = input.read(buf, 0, buf.length)) > 0
					&& splitter.isRun()
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
			if (splitter.isFinish()) {
				logger.info(splitter.getName() + "�߳��������!");
				task.writeMessage(splitter.getName(), "�߳��������!");
			} else {
				logger.info(splitter.getName() + "�߳�ֹͣ!");
				task.writeMessage(splitter.getName(), "�߳�ֹͣ!");
			}
		} catch (IOException e) {
			task.writeMessage(splitter.getName(), "�������쳣��"
					+ e.getLocalizedMessage());
			return;
		} catch (InterruptedException e) {
			task.writeMessage(splitter.getName(), "�̱߳��жϣ�");
			return;
		} finally {
			splitter.setRun(false);
			try {
				if (input != null)
					input.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
