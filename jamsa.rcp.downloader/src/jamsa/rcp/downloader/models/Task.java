package jamsa.rcp.downloader.models;

import jamsa.rcp.downloader.utils.FileUtils;
import jamsa.rcp.downloader.views.IConsoleWriter;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

/**
 * 下载任务对象
 * 
 * @author 朱杰
 * 
 */
public class Task extends Observable implements IConsoleWriter, Serializable {
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

	// 每个块最小 100K
	private static final long BLOCK_MIN_SIZE = 50000;

	// 下载临时文件扩展名
	public static final String FILENAME_DOWNLOAD_SUFFIX = ".GET";

	// 下载文件名冲突时，添加的修饰后缀
	public static final String FILENAME_SUFFIX = "_1";

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

	// 启动方式
	private int start = 0;

	public static final int START_AUTO = 0;

	public static final int START_MANUAL = 1;

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
		// if (blocks <= 0)
		// return;
		// else {
		// if (blocks > this.blocks)
		// for (int i = 0; i < (blocks - this.blocks); i++)
		// this.addSplitter();
		//
		// if (blocks < this.blocks)
		// for (int i = 0; i < (this.blocks - blocks); i++)
		// this.removeSplitter();
		//
		// }
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
			// 如果状态为错误或者停止
			if (this.status == STATUS_ERROR || this.status == this.STATUS_STOP) {
				this._stopAllSplitters();
			}
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
	private Map messages = Collections.synchronizedMap(new LinkedHashMap(6));

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
		notifyObservers(new String[] { "线程：" + threadName, message });
	}

	/**
	 * 清除消息
	 * 
	 */
	public void clearMessage() {
		messages.clear();
		setChanged();
		notifyObservers();
	}

	/**
	 * 块检查，应该在每次下载之前调用
	 */
	public void checkBlocks() {
		// 如果还没有分割过，则分割
		if (this.splitters.isEmpty()) {
			this.split();
			return;
		}

		// 如果块数量不一致,splitters大于blocks是正常的,因为用户有可能减少下载线程
		if (this.splitters.size() < this.blocks) {
			int diff = this.blocks - this.splitters.size();
			for (int i = 0; i < Math.abs(diff); i++) {
				this.addSplitter();
			}
		}
	}

	/**
	 * 减少任务块
	 * 
	 * @return
	 */
	public TaskSplitter removeSplitter() {
		for (Iterator it = splitters.iterator(); it.hasNext();) {
			TaskSplitter splitter = (TaskSplitter) it.next();
			if (!splitter.isFinish() && splitter.isRun()) {
				splitter.setRun(true);
				return splitter;
			}
		}
		return null;
	}

	/**
	 * 添加新的任务块
	 * 
	 * @return 新增加的任务块对象，或者null(表示未添加，由于文件未完成部分太小)
	 */
	public TaskSplitter addSplitter() {
		TaskSplitter ret = null;
		// 如果文件还没有自动分割，则分割
		if (getSplitters().isEmpty())
			this.split();

		// 如果还是未分割或者只有一块，则不再分割
		if (getSplitters().isEmpty() || getSplitters().size() == 1)
			return null;

		// 从现在的块中分离出新的块任务
		for (Iterator it = getSplitters().iterator(); it.hasNext();) {
			TaskSplitter splitter = (TaskSplitter) it.next();

			// 线程未完成量
			long unfinished = (splitter.getEndPos() - splitter.getStartPos() - splitter
					.getFinished());

			// 如果未完成量除2大于最小块则分割
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
	 * 按blocks属性自动分割任务
	 * 
	 */
	private void split() {
		// 块数量
		int block = this.blocks;
		// 文件大小
		long fileSize = this.fileSize;

		// 如果文件大小未知，或者块数量为零，则只分一块
		if (fileSize == 0 || block == 0) {
			TaskSplitter splitter = new TaskSplitter(0, 0, 0, getSplitters()
					.size()
					+ "");
			this.addSplitter(splitter);
			this.blocks = 1;
			return;
		}

		// 如果任务未分割过则要分割任务
		if (getSplitters().isEmpty()) {
			writeMessage("Task", "分割任务");

			// 按设置的块分
			long blockSize = fileSize / block;
			// 如果每块的大小，小于最小块限制，则按最小块限制进行分割
			if (blockSize < BLOCK_MIN_SIZE) {
				this.blocks = 0;
				for (int i = 0; i < ++block; i++) {

					boolean finished = false;// 分割完成
					long startPos = i * BLOCK_MIN_SIZE;
					long endPos = (i + 1) * BLOCK_MIN_SIZE;

					// 如果结束位置，大于或等于文件大小，则不再分新的块
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

			// 正常的分割情况(每块大小，大于或者等于最小块的限制
			this.blocks = 0;
			for (int i = 0; i < (block - 1); i++) {
				addSplitter(new TaskSplitter(i * blockSize,
						(i + 1) * blockSize, 0, getSplitters().size() + ""));
				this.blocks++;
			}
			addSplitter(new TaskSplitter((block - 1) * blockSize, fileSize, 0,
					getSplitters().size() + ""));
			this.blocks++;
			return;
		}
	}

	/**
	 * 下载完成后，重命名文件
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
	 * 获取保存的临时文件名
	 * 
	 * @return
	 */
	public File getTempSavedFile() {
		// 创建文件保存目录
		FileUtils.createDirectory(getFilePath());
		writeMessage("Task", "检查/创建目录" + getFilePath());
		String fileName = getFilePath() + File.separator + getFileName();

		// 检查文件是否已经存在
		while (FileUtils.existsFile(fileName)) {
			String name = getFileName();
			int length = name.length();
			int idx = name.lastIndexOf(".");
			name = name.substring(0, idx) + FILENAME_SUFFIX
					+ name.substring(idx, length);
			setFileName(name);
			fileName = getFilePath() + File.separator + getFileName();
		}
		// 修改任务文件名
		fileName += FILENAME_DOWNLOAD_SUFFIX;
		return new File(fileName);
	}

	/**
	 * 获取处于运行状态的块数量
	 * 
	 * @return
	 */
	public int getRunBlocks() {
		int ret = 0;
		for (Iterator iter = this.splitters.iterator(); iter.hasNext();) {
			TaskSplitter splitter = (TaskSplitter) iter.next();
			if (splitter.isRun())
				ret++;
		}
		return ret;
	}

	/**
	 * 获取一个未完成的块
	 * 
	 * @return
	 */
	public TaskSplitter getUnfinishedSplitter() {
		// 如果正在运行的块数量小于,任务设置的块数量
		if (getRunBlocks() < blocks) {
			// 检查是否有线程未完成且没有运行
			for (Iterator iter = splitters.iterator(); iter.hasNext();) {
				TaskSplitter s = (TaskSplitter) iter.next();
				if (!s.isFinish() && !s.isRun()) {
					return s;
				}
			}
		}

		return null;
	}

	private void _stopAllSplitters() {
		for (Iterator it = splitters.iterator(); it.hasNext();) {
			TaskSplitter splitter = (TaskSplitter) it.next();
			splitter.setRun(false);
		}
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

}
