package jamsa.rcp.downloader.models;

import jamsa.rcp.downloader.utils.FileUtils;
import jamsa.rcp.downloader.views.IConsoleWriter;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;

/**
 * �����������
 * 
 * @author ���
 * 
 */
public class Task extends Observable implements IConsoleWriter, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9206186502703680217L;

	/**
	 * ����״̬����
	 */
	public static final int STATUS_STOP = 0;

	public static final int STATUS_RUNNING = 1;

	public static final int STATUS_FINISHED = 3;

	public static final int STATUS_ERROR = 4;

	// ÿ������С 100K
	private static final long BLOCK_MIN_SIZE = 100000;

	// ������ʱ�ļ���չ��
	public static final String FILENAME_DOWNLOAD_SUFFIX = ".GET";

	// �����ļ�����ͻʱ����ӵ����κ�׺
	public static final String FILENAME_SUFFIX = "_1";

	// �ļ���
	private String fileName;

	// ���ص�ַ
	private String fileUrl;

	// ����ҳ
	private String pageUrl;

	// �ļ�����·��
	private String filePath;

	// ��������
	private Category category;

	// �ļ���С
	private long fileSize;

	// �ļ�����
	private String fileType = "";

	// ��ʼʱ��
	private long beginTime;

	// ���ʱ��
	private long finishTime;

	// ״̬
	private int status;

	// ��ʱs
	private long totalTime;

	// ��ɴ�С
	private long finishedSize;

	// ��ʱ�ٶ�
	private long speed;

	// ��ע
	private String memo;

	// �����߳�����
	private int blocks = 5;

	// �߳���Ϣ
	private List splitters = new ArrayList(10);

	// �Ƿ�ɾ��
	private boolean deleted = false;

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public long getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(long beginTime) {
		this.beginTime = beginTime;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {

		this.fileSize = fileSize;

	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public long getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public long getFinishedSize() {
		return finishedSize;
	}

	public void setFinishedSize(long finishedSize) {
		this.finishedSize = finishedSize;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	public long getSpeed() {
		return speed;
	}

	public void setSpeed(long speed) {
		this.speed = speed;
	}

	public List getSplitters() {
		return splitters;
	}

	public void setSplitters(List splitters) {
		this.splitters = splitters;
	}

	public int getBlocks() {
		return blocks;
	}

	public void setBlocks(int blocks) {
		if (blocks <= 0)
			return;
		else {
			if (blocks > this.blocks)
				for (int i = 0; i < (blocks - this.blocks); i++)
					this.addSplitter();

			if (blocks < this.blocks)
				for (int i = 0; i < (this.blocks - blocks); i++)
					this.removeSplitter();

		}
		// this.blocks = blocks;
	}

	public void addSplitter(TaskSplitter splitter) {
		this.splitters.add(splitter);
	}

	public void removeSplitter(TaskSplitter splitter) {
		this.splitters.remove(splitter);
	}

	public int getStatus() {
		return status;
	}

	/**
	 * ��������״̬��֪ͨ������
	 * 
	 * @param status
	 */
	public void setStatus(int status) {
		if (this.beginTime == 0 && status == STATUS_RUNNING)
			beginTime = System.currentTimeMillis();

		if (this.status != status) {
			this.status = status;
			this.setChanged();
			this.notifyObservers();
		}
	}

	/**
	 * ��ȡ����ƽ���ٶ�(k/s)
	 * 
	 * @return
	 */
	public long getAverageSpeed() {
		if (totalTime != 0)
			return this.finishedSize / totalTime;
		return 0;
	}

	/**
	 * ��������
	 * 
	 */
	public void reset() {
		this.resetSplitters();
		this.setBeginTime(0);
		this.setFileSize(0);
		this.setFinishedSize(0);
		this.setTotalTime(0);
		this.setStatus(Task.STATUS_STOP);
	}

	/**
	 * �����߳���Ϣ
	 * 
	 */
	public void resetSplitters() {
		splitters.clear();
	}

	/**
	 * �ն���Ϣ
	 */
	private Map messages = Collections.synchronizedMap(new HashMap(6));

	public Map getMessages() {
		return messages;
	}

	/**
	 * ���߳��ն������־
	 * 
	 * @param threadName
	 *            �̱߳�ʶ
	 * @param message
	 *            ��־����
	 */
	public void writeMessage(String threadName, String message) {
		if (messages.get(threadName) == null) {
			List msgs = new ArrayList(20);
			msgs.add(message);
			messages.put(threadName, msgs);
		} else {
			List msgs = (List) messages.get(threadName);
			if (msgs.size() >= 20)
				msgs.clear();
			msgs.add(message);
		}
		setChanged();
		notifyObservers(new String[] { "�̣߳�" + threadName, message });
	}

	/**
	 * ���飬Ӧ����ÿ������֮ǰ����
	 */
	public void checkBlocks() {
		// �����û�зָ������ָ�
		if (this.splitters.isEmpty()) {
			this.split();
			return;
		}
	}

	/**
	 * ���������
	 * 
	 * @return
	 */
	public TaskSplitter removeSplitter() {
		// TODO: ��Ӽ��������Ĵ���
		return null;
	}

	/**
	 * ����µ������
	 * 
	 * @return �����ӵ��������󣬻���null(��ʾδ��ӣ������ļ�δ��ɲ���̫С)
	 */
	public TaskSplitter addSplitter() {
		TaskSplitter ret = null;
		// ����ļ���û���Զ��ָ��ָ�
		if (getSplitters().isEmpty())
			this.split();

		// �������δ�ָ����ֻ��һ�飬���ٷָ�
		if (getSplitters().isEmpty() || getSplitters().size() == 1)
			return null;

		// �����ڵĿ��з�����µĿ�����
		for (Iterator it = getSplitters().iterator(); it.hasNext();) {
			TaskSplitter splitter = (TaskSplitter) it.next();

			// �߳�δ�����
			long unfinished = (splitter.getEndPos() - splitter.getStartPos() - splitter
					.getFinished());

			// ���δ�������2������С����ָ�
			long spliteBlock = unfinished / 2;

			if (spliteBlock > BLOCK_MIN_SIZE) {
				long newEndPos = splitter.getStartPos()
						+ splitter.getFinished() + spliteBlock;

				ret = new TaskSplitter(newEndPos, splitter.getEndPos(), 0,
						getSplitters().size() + "");
				break;
			}
		}

		addSplitter(ret);
		this.blocks++;

		return ret;
	}

	/**
	 * �Զ��ָ�����
	 * 
	 */
	private void split() {
		// ������
		int block = this.blocks;
		// �ļ���С
		long fileSize = this.fileSize;

		// ����ļ���Сδ֪�����߿�����Ϊ�㣬��ֻ��һ��
		if (fileSize == 0 || block == 0) {
			TaskSplitter splitter = new TaskSplitter(0, 0, 0, getSplitters()
					.size()
					+ "");
			this.addSplitter(splitter);
			this.blocks = 1;
			return;
		}

		// �������δ�ָ����Ҫ�ָ�����
		if (getSplitters().isEmpty()) {
			writeMessage("Task", "�ָ�����");

			// �����õĿ��
			long blockSize = fileSize / block;
			// ���ÿ��Ĵ�С��С����С�����ƣ�����С�����ƽ��зָ�
			if (blockSize < BLOCK_MIN_SIZE) {
				this.blocks=0;
				for (int i = 0; i < ++block; i++) {

					boolean finished = false;// �ָ����
					long startPos = i * BLOCK_MIN_SIZE;
					long endPos = (i + 1) * BLOCK_MIN_SIZE;

					// �������λ�ã����ڻ�����ļ���С�����ٷ��µĿ�
					if (endPos >= fileSize) {
						endPos = fileSize;
						finished = true;
					}
					addSplitter(new TaskSplitter(startPos, endPos, 0,
							getSplitters().size() + ""));
					this.blocks++;
					if (finished)
						break;
				}

				return;
			}

			// �����ķָ����(ÿ���С�����ڻ��ߵ�����С�������
			this.blocks=0;
			for (int i = 0; i < (block - 1); i++) {
				addSplitter(new TaskSplitter(i * blockSize,
						(i + 1) * blockSize, 0, getSplitters().size() + ""));
				this.blocks++;
			}
			addSplitter(new TaskSplitter((block - 1) * blockSize, fileSize, 0,
					getSplitters().size() + ""));
			return;
		}
	}

	/**
	 * ������ɺ��������ļ�
	 * 
	 * @param savedFile
	 */
	public void renameSavedFile() {
		String finalFileName = getFilePath() + File.separator + getFileName();
		while (FileUtils.existsFile(finalFileName)) {
			String name = getFileName();
			int length = name.length();
			int idx = name.lastIndexOf(".");
			name = name.substring(0, idx) + FILENAME_SUFFIX
					+ name.substring(idx, length);
			setFileName(name);

			finalFileName = getFilePath() + File.separator + getFileName();
		}
		getTempSavedFile().renameTo(new File(finalFileName));
	}

	/**
	 * ��ȡ�������ʱ�ļ���
	 * 
	 * @return
	 */
	public File getTempSavedFile() {
		// �����ļ�����Ŀ¼
		FileUtils.createDirectory(getFilePath());
		writeMessage("Task", "���/����Ŀ¼" + getFilePath());
		String fileName = getFilePath() + File.separator + getFileName();

		// ����ļ��Ƿ��Ѿ�����
		while (FileUtils.existsFile(fileName)) {
			String name = getFileName();
			int length = name.length();
			int idx = name.lastIndexOf(".");
			name = name.substring(0, idx) + FILENAME_SUFFIX
					+ name.substring(idx, length);
			setFileName(name);
			fileName = getFilePath() + File.separator + getFileName();
		}
		// �޸������ļ���
		fileName += FILENAME_DOWNLOAD_SUFFIX;
		return new File(fileName);
	}

}
