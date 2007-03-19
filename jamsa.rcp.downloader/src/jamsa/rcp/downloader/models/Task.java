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
 * 下载任务对象
 * 
 * @author 朱杰
 * 
 */
public class Task extends Observable implements IConsoleWriter,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9206186502703680217L;

	/**
	 * 任务状态常量
	 */
	public static final int STATUS_STOP = 0;

	public static final int STATUS_RUNNING = 1;

	public static final int STATUS_FINISHED = 3;

	public static final int STATUS_ERROR = 4;

	// 文件名
	private String fileName;

	// 下载地址
	private String fileUrl;

	// 引用页
	private String pageUrl;

	// 文件保存路径
	private String filePath;

	// 所属分类
	private Category category;

	// 文件大小
	private long fileSize;

	// 文件类型
	private String fileType = "";

	// 开始时间
	private long beginTime;

	// 完成时间
	private long finishTime;

	// 状态
	private int status;

	// 耗时s
	private long totalTime;

	// 完成大小
	private long finishedSize;

	// 即时速度
	private long speed;

	// 备注
	private String memo;

	// 下载线程数量
	private int blocks = 5;

	// 线程信息
	private List splitters = new ArrayList(10);

	// 是否被删除
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
	 * 设置任务状态，通知监听者
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
	 * 获取任务平均速度(k/s)
	 * 
	 * @return
	 */
	public long getAverageSpeed() {
		if (totalTime != 0)
			return this.finishedSize / totalTime;
		return 0;
	}

	/**
	 * 重置任务
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
	 * 重置线程信息
	 * 
	 */
	public void resetSplitters() {
		splitters.clear();
	}

	/**
	 * 终端消息
	 */
	private Map messages = Collections.synchronizedMap(new HashMap(6));
	
	

	public Map getMessages() {
		return messages;
	}

	/**
	 * 向线程终端输出日志
	 * 
	 * @param threadName
	 *            线程标识
	 * @param message
	 *            日志内容
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
		notifyObservers(new String[] { "线程：" + threadName, message });
	}

}
