package jamsa.rcp.downloader.models;

import jamsa.rcp.downloader.Activator;
import jamsa.rcp.downloader.IConstants;
import jamsa.rcp.downloader.utils.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

/**
 * 下载分类模型
 * 
 * @author 朱杰
 * 
 */
public class CategoryModel extends Observable {
	private static Logger logger = new Logger(CategoryModel.class);

	private static final String SAVE_PATH = IConstants.JAVA_IO_TMPDIR;

	private Map rootCategories = new HashMap();

	private Map categories = new HashMap();

	private Category root;

	private Category running;

	private Category finished;

	private Category trash;

	private static CategoryModel instance = new CategoryModel();

	public Category getTrash() {
		return trash;
	}

	public Category getRoot() {
		return root;
	}

	public Category getRunning() {
		return running;
	}

	public Category getFinished() {
		return finished;
	}

	/**
	 * 创建默认的下载分类
	 * 
	 */
	private void createOtherCategory() {
		Category child = new Category();
		child.setName("软件");
		child.setPath(SAVE_PATH + IConstants.FILE_SEPARATOR + "software");
		finished.addChild(child);
		categories.put(child.getName(), child);

		child = new Category();
		child.setName("音乐");
		child.setPath(SAVE_PATH + IConstants.FILE_SEPARATOR + "music");
		finished.addChild(child);
		categories.put(child.getName(), child);

		Category child_child = new Category();
		child_child.setName("流行");
		child_child.setPath(SAVE_PATH + IConstants.FILE_SEPARATOR + "music"
				+ IConstants.FILE_SEPARATOR + "pop");
		child.addChild(child_child);
		categories.put(child_child.getName(), child_child);

		child_child = new Category();
		child_child.setName("爵士");
		child_child.setPath(SAVE_PATH + IConstants.FILE_SEPARATOR + "music"
				+ IConstants.FILE_SEPARATOR + "jazz");
		child.addChild(child_child);
		categories.put(child_child.getName(), child_child);

		child = new Category();
		child.setName("书籍");
		child.setPath(SAVE_PATH + IConstants.FILE_SEPARATOR + "books");
		finished.addChild(child);
		categories.put(child.getName(), child);
	}

	/**
	 * 创建必须的下载分类
	 * 
	 */
	private void createDefaultCategory() {
		root = new Category();
		root.setName("RCP Get");
		root.setPath(SAVE_PATH);
		rootCategories.put(root.getName(), root);

		running = new Category();
		running.setName("正在下载");
		running.setPath(SAVE_PATH);
		root.addChild(running);

		finished = new Category();
		finished.setName("已下载");
		finished.setPath(SAVE_PATH);
		root.addChild(finished);
		// categories.put(finished.getName(), finished);

		trash = new Category();
		trash.setName("回收站");
		trash.setPath(SAVE_PATH);
		root.addChild(trash);
	}

	private CategoryModel() {
		createDefaultCategory();
		// createOtherCategory();
		loadCategories();
	}

	public static CategoryModel getInstance() {
		return instance;
	}

	/**
	 * 添加子分类
	 * 
	 * @param category
	 *            分类
	 * @param parentCategory
	 *            父分类
	 */
	public void addCategory(Category category, Category parentCategory) {
		// 如果选中下面的分类，则添加到 已完成 分类下
		if (parentCategory == root || parentCategory == running
				|| parentCategory == trash) {
			parentCategory = finished;
		}
		parentCategory.addChild(category);
		this.addCategory(category);
	}

	/**
	 * 添加分类
	 * 
	 * @param category
	 */
	public void addCategory(Category category) {
		// rootCategories.put(category.getName(), category);
		categories.put(category.getName(), category);
		this.setChanged();
		this.notifyObservers(category);
		logger.info("添加/修改了新的下载分类");
		this.saveCategories();
	}

	public void updateCategory(Category category) {
		this.addCategory(category);
	}

	/**
	 * 递归删除分类
	 * 
	 * @param category
	 */
	private void _deleteCategory(Category category) {
		// 这些分类不允许删除
		if (category == root || category == running || category == trash
				|| category == finished) {
			return;
		}

		if (category.getParent() != null)
			category.getParent().removeChild(category);
		Task[] tasks = TaskModel.getInstance().getTasks(category);
		if (tasks != null) {
			for (int i = 0; i < tasks.length; i++) {
				Task task = tasks[i];
				task.setCategory(category.getParent());
				TaskModel.getInstance().updateTask(task);
			}
		}

		categories.remove(category.getName());

		if (category.getChildren() != null) {
			for (Iterator it = category.getChildren().values().iterator(); it
					.hasNext();) {
				Category child = (Category) it.next();
				_deleteCategory(child);
			}
		}
	}

	/**
	 * 删除分类
	 * 
	 * @param category
	 */
	public void deleteCategory(Category category) {
		// rootCategories.remove(category.getName());
		_deleteCategory(category);

		this.setChanged();
		this.notifyObservers(category);
		logger.info("删除了新的下载分类");
		this.saveCategories();
	}

	/**
	 * 将Map转变成数组
	 * 
	 * @param categories
	 * @return
	 */
	private Category[] _getCategories(Map categories) {
		Category[] result = new Category[categories.size()];
		int index = 0;
		// result[index++] = finished;
		for (Iterator iter = categories.keySet().iterator(); iter.hasNext();) {
			String name = String.valueOf(iter.next());
			result[index++] = (Category) categories.get(name);
		}
		return result;
	}

	/**
	 * 获取分类树根级分类
	 * 
	 * @return 分类树根级节点数组
	 */
	public Category[] getRootCategories() {
		return _getCategories(rootCategories);
	}

	/**
	 * 是否允许添加子分类
	 * 
	 * @param category
	 * @return
	 */
	public boolean isAllowAddChild(Category category) {
		return isAllowAddChild(category.getName());
	}

	/**
	 * 是否允许添加子分类
	 * 
	 * @param categoryName
	 * @return
	 */
	public boolean isAllowAddChild(String categoryName) {
		return categories.keySet().contains(categoryName)
				|| categoryName.equals(finished.getName());
	}

	public boolean isAllowModify(String categoryName) {
		return isAllowDelete(categoryName);
	}

	public boolean isAllowModify(Category category) {
		return isAllowDelete(category);
	}

	/**
	 * 是否允许删除该分类
	 * 
	 * @param categoryName
	 * @return
	 */
	public boolean isAllowDelete(String categoryName) {
		if (categories.keySet().contains(categoryName))
			return true;
		return false;
	}

	/**
	 * 是否允许删除该分类
	 * 
	 * @param category
	 * @return
	 */
	public boolean isAllowDelete(Category category) {
		return this.isAllowDelete(category.getName());
	}

	/**
	 * 获取允许保存文件的分类
	 * 
	 * @return
	 */
	public Category[] getAllowSaveCategories() {

		return _getCategories(categories);
	}

	public boolean contains(Category category) {
		return rootCategories.containsKey(category.getName());
	}

	public boolean contains(String categoryName) {
		// return rootCategories.containsKey(categoryName);
		return (categories.containsKey(categoryName)
				|| root.getName().equals(categoryName)
				|| finished.getName().equals(categoryName)
				|| trash.getName().equals(categoryName) || running.getName()
				.equals(categoryName));
	}

	public Category getCategory(String categoryName) {
		return (Category) categories.get(categoryName);
	}

	public String[] getAllowSaveCategoryNames() {
		String[] result = new String[categories.size() + 1];
		int index = 0;
		result[index++] = finished.getName();
		for (Iterator iter = categories.keySet().iterator(); iter.hasNext();) {
			result[index++] = String.valueOf(iter.next());
		}
		return result;
		// return (String[]) categories.keySet().toArray(new String[] {});
	}

	public static final String TAG_CATEGORIES = "Categories";

	public static final String TAG_CATEGORY = "Category";

	public static final String TAG_NAME = "Name";

	public static final String TAG_PATH = "Path";

	public static final String TAG_PARENT = "Parent";

	private File getCategoriesFile() {
		return Activator.getDefault().getStateLocation().append("category.xml")
				.toFile();
	}

	private void loadCategories() {
		FileReader reader = null;
		try {
			File file = getCategoriesFile();
			reader = new FileReader(file);
			loadCategories(XMLMemento.createReadRoot(reader));
			logger.info("读取所有分类");
		} catch (FileNotFoundException e) {
			createOtherCategory();
			saveCategories();
		} catch (Exception e) {
			logger.error("读取所有分类发生错误", e);
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
				logger.error("关闭分类文件发生错误", e);
			}
		}
	}

	private void loadCategories(XMLMemento memento) {
		IMemento[] children = memento.getChildren(TAG_CATEGORY);
		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				Category category = new Category();
				category.setName(children[i].getString(TAG_NAME));
				category.setPath(children[i].getString(TAG_PATH));
				category.setParent((Category) categories.get(children[i]
						.getString(TAG_PARENT)));
				if (children[i].getString(TAG_PARENT)
						.equals(finished.getName())) {
					finished.addChild(category);
					// category.setParent(finished);
				}
				categories.put(category.getName(), category);
				// 递归查找下级
				loadCategories((XMLMemento) children[i]);
			}
		}
	}

	public void saveCategories() {
		if (categories.isEmpty())
			return;
		XMLMemento memento = XMLMemento.createWriteRoot(TAG_CATEGORIES);
		saveCategories(memento);
		FileWriter writer = null;
		try {
			writer = new FileWriter(getCategoriesFile());
			memento.save(writer);
		} catch (IOException e) {

		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {

			}
		}
	}

	private void saveCategories(XMLMemento memento) {
		for (Iterator iter = categories.keySet().iterator(); iter.hasNext();) {
			String name = String.valueOf(iter.next());
			Category category = (Category) categories.get(name);
			// 确保分类是属于已有分类下面的
			if (category.getParent() != null) {
				IMemento node = memento.createChild(TAG_CATEGORY);
				node.putString(TAG_PARENT, category.getParent().getName());
				node.putString(TAG_NAME, category.getName());
				node.putString(TAG_PATH, category.getPath());
			}
		}
	}

}
