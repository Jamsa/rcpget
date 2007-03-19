package jamsa.rcp.downloader.models;

import jamsa.rcp.downloader.http.HttpClientUtils;
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

	public InputStream getInputStream() {
		Properties prop = new Properties();
		prop.put("User-Agent", "RCP Get");

		if (splitter.getEndPos() != 0) {
			prop.put("RANGE", "bytes=" + splitter.getStartPos() + "-"
					+ splitter.getEndPos());
		} else {
			prop.put("RANGE", "bytes=" + splitter.getStartPos() + "-");
		}
		return HttpClientUtils.getInputStream(task.getFileUrl(), 5, 5000, prop,
				task, splitter.getName());
	}

	public void run() {
		runn = true;
		InputStream input = getInputStream();

		if (input == null) {
			runn = false;
			return;
		}
		try {
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
			logger.info(splitter.getName() + "�߳��������!");
			task.writeMessage(splitter.getName(), "�߳��������!");
		} catch (IOException e) {
			task.writeMessage(splitter.getName(), "�������쳣��"
					+ e.getLocalizedMessage());
			return;
		} catch (InterruptedException e) {
			task.writeMessage(splitter.getName(), "�̱߳��жϣ�");

			return;
		} finally {
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
