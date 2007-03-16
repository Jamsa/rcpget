package jamsa.rcp.downloader.models;

import jamsa.rcp.downloader.Activator;
import jamsa.rcp.downloader.utils.FileUtils;
import jamsa.rcp.downloader.utils.Logger;
import jamsa.rcp.downloader.utils.Md5Encrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

/**
 * 下载任务模型
 * 
 * @author 朱杰
 * 
 */
public class TaskModel extends Observable {
	private static Logger logger = new Logger(TaskModel.class);

	/**
	 * 保存有所有任务的容器
	 */
	private Map tasks = Collections.synchronizedMap(new HashMap(10));

	/**
	 * 当前选中任务
	 * 
	 * private Task currentSelect;
	 * 
	 * public Task getCurrentSelect() { return currentSelect; }
	 * 
	 * public void setCurrentSelect(Task currentSelect) { this.currentSelect =
	 * currentSelect; setChanged(); this.notifyObservers(this.currentSelect); }
	 */

	private TaskModel() {
		loadTasks();
	}

	private static TaskModel instance = new TaskModel();

	public static TaskModel getInstance() {
		return instance;
	}

	// TODO 需要添加参数控制是否持久化任务列表以减少IO次数
	public void addTask(Task task) {
		tasks.put(task.getFileUrl(), task);
		this.setChanged();
		this.notifyObservers(task);
		this.saveTasks();
		logger.info("通知任务列表观察者");
	}

	public void updateTask(Task task) {
		this.addTask(task);
	}

	public void deleteTask(Task task) {
		if (task.isDeleted())
			tasks.remove(task.getFileUrl());
		else {
			task.setDeleted(true);
		}
		this.setChanged();
		this.notifyObservers(task);
		this.saveTasks();
	}

	public void restoreTask(Task task) {
		task.setDeleted(false);
		// task.setStatus(Task.STATUS_RUNNING);
		this.setChanged();
		this.notifyObservers(task);
		this.saveTasks();
	}

	/**
	 * 获取所有任务
	 * 
	 * @return
	 */
	public Task[] getTasks() {
		Task[] result = new Task[tasks.size()];
		int index = 0;
		for (Iterator iter = tasks.keySet().iterator(); iter.hasNext();) {
			result[index++] = (Task) tasks.get(iter.next());
		}
		return result;
	}

	/**
	 * 获取分类下的任务
	 * 
	 * @param categoryName
	 * @return
	 */
	public Task[] getTasks(String categoryName) {
		List result = new ArrayList();
		CategoryModel categoryModel = CategoryModel.getInstance();
		Category running = categoryModel.getRunning();
		Category root = categoryModel.getRoot();
		Category trash = categoryModel.getTrash();

		for (Iterator it = tasks.keySet().iterator(); it.hasNext();) {
			Task task = (Task) tasks.get(it.next());
			// 如果是要获取处于运行状态的任务
			if ((categoryName.equals(running.getName()) || categoryName
					.equals(root.getName()))
					&& (task.getStatus() == Task.STATUS_RUNNING
							|| task.getStatus() == Task.STATUS_STOP || task
							.getStatus() == Task.STATUS_ERROR)
					&& !task.isDeleted()) {
				result.add(task);
				continue;
			}

			if (categoryName.equals(trash.getName()) && task.isDeleted()) {
				result.add(task);
				continue;
			}

			if (task.getCategory().getName() == categoryName
					&& task.getStatus() == Task.STATUS_FINISHED
					&& !task.isDeleted()) {
				result.add(task);
				continue;
			}
		}
		if (result.isEmpty())
			return null;
		return (Task[]) result.toArray(new Task[] {});
	}

	public Task[] getTasks(Category category) {
		return this.getTasks(category.getName());
	}

	private File getTasksDirectory() {
		return Activator.getDefault().getStateLocation().append("tasks")
				.toFile();
	}

	public void loadTasks() {
		_loadTasks();
	}

	public void _loadTasks() {
		File directory = getTasksDirectory();
		if (directory.exists()) {
			File[] files = directory.listFiles();
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				try {
					FileInputStream fis = new FileInputStream(file);
					ObjectInputStream ois = new ObjectInputStream(fis);
					Task task = (Task) ois.readObject();
					tasks.put(task.getFileUrl(), task);
					ois.close();
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		}
	}

	/**
	 * 改变持久化方案
	 * 
	 */
	public void _saveTasks() {
		if (tasks.isEmpty())
			return;
		File directory = getTasksDirectory();
		if (!directory.exists())
			FileUtils.createDirectory(directory.getAbsolutePath());
		for (Iterator it = tasks.values().iterator(); it.hasNext();) {
			Task task = (Task) it.next();
			_saveTask(task);
		}

	}

	public synchronized void _saveTask(Task task) {
		if (tasks.isEmpty())
			return;
		File directory = getTasksDirectory();
		if (!directory.exists())
			FileUtils.createDirectory(directory.getAbsolutePath());
		try {
			String fileName = getTasksDirectory().getAbsolutePath()
					+ File.separator + Md5Encrypt.MD5Encode(task.getFileUrl());
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(task);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveTasks() {
		_saveTasks();
	}

	// =================================XML处理部分======================================

	/**
	 * 任务XML标签定义
	 */
	public static final String TAG_TASKS = "Tasks";

	public static final String TAG_TASK = "Task";

	public static final String TAG_FILENAME = "FileName";

	public static final String TAG_FILEURL = "FileUrl";

	public static final String TAG_FILEPATH = "FilePath";

	public static final String TAG_CATEGORY = "Category";

	public static final String TAG_PAGEURL = "PageUrl";

	public static final String TAG_FILESIZE = "FileSize";

	public static final String TAG_FILETYPE = "FileType";

	public static final String TAG_BEGINTIME = "BeginTime";

	public static final String TAG_FINISHTIME = "FinishTime";

	public static final String TAG_STATUS = "Status";

	public static final String TAG_TOTALTIME = "TotalTime";

	public static final String TAG_FINISHEDSIZE = "FinishedSize";

	public static final String TAG_MEMO = "Memo";

	public static final String TAG_SPEED = "Speed";

	public static final String TAG_BLOCKS = "Blocks";

	public static final String TAG_DELETEED = "Deleted";

	// 任务块信息XML标签定义
	public static final String TAG_SPLITTERS = "Splitters";

	public static final String TAG_NAME = "Name";

	public static final String TAG_SPLITTER = "Splitter";

	public static final String TAG_STARTPOS = "StartPos";

	public static final String TAG_ENDPOS = "EndPos";

	public static final String TAG_FINISHED = "Finished";

	/**
	 * 获取任务列表文件
	 * 
	 * @return
	 */
	private File getTasksFile() {
		return Activator.getDefault().getStateLocation().append("task.xml")
				.toFile();
	}

	/**
	 * 从XML中读取任务
	 * 
	 */
	private void loadTasks_xml() {
		FileReader reader = null;
		try {
			File file = getTasksFile();
			reader = new FileReader(file);
			loadTasks(XMLMemento.createReadRoot(reader));
			logger.info("从XML中读取任务列表完成");
		} catch (FileNotFoundException e) {
			logger.warn("任务列表不存在", e);
		} catch (Exception e) {
			logger.error("读取任务列表发生错误", e);
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
				logger.error("任务列表文件关闭时发生错误", e);
			}
		}
	}

	/**
	 * XML->任务解析
	 * 
	 * @param memento
	 */
	private void loadTasks(XMLMemento memento) {
		IMemento[] children = memento.getChildren(TAG_TASK);
		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				Task task = new Task();
				task.setBeginTime(Long.parseLong(children[i]
						.getString(TAG_BEGINTIME)));
				task.setBlocks(Integer.parseInt(children[i]
						.getString(TAG_BLOCKS)));

				task.setFileName(children[i].getString(TAG_FILENAME));
				task.setFilePath(children[i].getString(TAG_FILEPATH));
				task.setFileSize(Long.parseLong(children[i]
						.getString(TAG_FILESIZE)));
				task.setFileType(children[i].getString(TAG_FILETYPE));
				task.setFileUrl(children[i].getString(TAG_FILEURL));
				task.setFinishedSize(Long.parseLong(children[i]
						.getString(TAG_FINISHEDSIZE)));
				task.setFinishTime(Long.parseLong(children[i]
						.getString(TAG_FINISHTIME)));

				// task.setMemo(children[i].getString(TAG_MEMO));
				task.setMemo(children[i].getTextData() == null ? ""
						: children[i].getTextData());

				task.setPageUrl(children[i].getString(TAG_PAGEURL));
				task.setSpeed(Long.parseLong(children[i].getString(TAG_SPEED)));
				task
						.setStatus(Integer.parseInt(children[i]
								.getString(TAG_STATUS)) == Task.STATUS_FINISHED ? Task.STATUS_FINISHED
								: Task.STATUS_STOP);

				// 如果分类未找到，则根据任务状态 放到默认分类下
				Category category = CategoryModel.getInstance().getCategory(
						children[i].getString(TAG_CATEGORY));
				if (category == null) {
					CategoryModel categoryModel = CategoryModel.getInstance();
					if (task.getStatus() == Task.STATUS_FINISHED)
						task.setCategory(categoryModel.getFinished());
					else {
						task.setCategory(categoryModel.getRunning());
					}
				} else {
					task.setCategory(category);
				}

				task.setDeleted(Boolean.parseBoolean(children[i]
						.getString(TAG_DELETEED)));

				task.setTotalTime(Long.parseLong(children[i]
						.getString(TAG_TOTALTIME)));

				IMemento splittersNode = children[i].getChild(TAG_SPLITTERS);
				if (splittersNode != null) {
					IMemento[] splitters = splittersNode
							.getChildren(TAG_SPLITTER);
					if (splitters != null) {
						for (int j = 0; j < splitters.length; j++) {
							long start = Long.parseLong(splitters[j]
									.getString(TAG_STARTPOS));
							long end = Long.parseLong(splitters[j]
									.getString(TAG_ENDPOS));
							long finished = Long.parseLong(splitters[j]
									.getString(TAG_FINISHED));
							String name = splitters[j].getString(TAG_NAME);
							TaskSplitter s = new TaskSplitter(start, end,
									finished, name);
							task.addSplitter(s);
						}
					}
				}

				tasks.put(task.getFileUrl(), task);
			}
		}
	}

	/**
	 * 保存任务状态至XML文件中
	 * 
	 */
	public synchronized void saveTasks_xml() {
		if (tasks.isEmpty())
			return;
		XMLMemento memento = XMLMemento.createWriteRoot(TAG_TASKS);
		saveTaks(memento);
		FileWriter writer = null;
		try {
			writer = new FileWriter(getTasksFile());
			memento.save(writer);
			logger.info("任务列表保存到XML中");
		} catch (IOException e) {
			logger.error("保存任务列表出错", e);
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				logger.error("任务列表文件关闭时发生错误", e);
			}
		}
	}

	/**
	 * 任务->XML解析
	 * 
	 * @param memento
	 */
	private void saveTaks(XMLMemento memento) {
		for (Iterator iter = tasks.keySet().iterator(); iter.hasNext();) {
			String url = String.valueOf(iter.next());
			Task task = (Task) tasks.get(url);
			if (task != null) {
				IMemento node = memento.createChild(TAG_TASK);
				node.putString(TAG_BEGINTIME, task.getBeginTime() + "");
				node.putString(TAG_BLOCKS, task.getBlocks() + "");
				node.putString(TAG_CATEGORY, task.getCategory().getName());
				node.putString(TAG_DELETEED, task.isDeleted() + "");
				node.putString(TAG_FILENAME, task.getFileName());
				node.putString(TAG_FILEPATH, task.getFilePath());
				node.putString(TAG_FILESIZE, task.getFileSize() + "");
				node.putString(TAG_FILETYPE, task.getFileType());
				node.putString(TAG_FILEURL, task.getFileUrl());
				node.putString(TAG_FINISHEDSIZE, task.getFinishedSize() + "");
				node.putString(TAG_FINISHTIME, task.getFinishTime() + "");
				// node.putString(TAG_MEMO, task.getMemo());
				node.putTextData(task.getMemo() == null ? "" : task.getMemo());
				node.putString(TAG_PAGEURL, task.getPageUrl());
				node.putString(TAG_SPEED, task.getSpeed() + "");
				node.putString(TAG_STATUS, task.getStatus() + "");
				node.putString(TAG_TOTALTIME, task.getTotalTime() + "");

				IMemento splittersNode = node.createChild(TAG_SPLITTERS);
				for (Iterator iterator = task.getSplitters().iterator(); iterator
						.hasNext();) {
					TaskSplitter splitter = (TaskSplitter) iterator.next();
					IMemento splitterNode = splittersNode
							.createChild(TAG_SPLITTER);
					splitterNode.putString(TAG_STARTPOS, splitter.getStartPos()
							+ "");
					splitterNode.putString(TAG_ENDPOS, splitter.getEndPos()
							+ "");
					splitterNode.putString(TAG_FINISHED, splitter.getFinished()
							+ "");
					splitterNode.putString(TAG_NAME, splitter.getName());
				}

			}
		}
	}

}
