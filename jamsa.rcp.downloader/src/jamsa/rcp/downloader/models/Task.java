package jamsa.rcp.downloader.models;

import jamsa.rcp.downloader.views.IConsoleWriter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

/**
 * �����������
 * 
 * @author ���
 * 
 */
public class Task extends Observable implements IConsoleWriter,Serializable{
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
		this.blocks = blocks;
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
		if(messages.get(threadName)==null){
			List msgs = new ArrayList(20);
			msgs.add(message);
			messages.put(threadName, msgs);
		}else{
			List msgs = (List)messages.get(threadName);
			if(msgs.size()>=20)
				msgs.clear();
			msgs.add(message);
		}
		setChanged();
		notifyObservers(new String[] { "�̣߳�" + threadName, message });
	}

}
