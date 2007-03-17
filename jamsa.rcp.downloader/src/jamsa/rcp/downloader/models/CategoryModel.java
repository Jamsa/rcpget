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
 * ���ط���ģ��
 * 
 * @author ���
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
	 * ����Ĭ�ϵ����ط���
	 * 
	 */
	private void createOtherCategory() {
		Category child = new Category();
		child.setName("���");
		child.setPath(SAVE_PATH + IConstants.FILE_SEPARATOR + "software");
		finished.addChild(child);
		categories.put(child.getName(), child);

		child = new Category();
		child.setName("����");
		child.setPath(SAVE_PATH + IConstants.FILE_SEPARATOR + "music");
		finished.addChild(child);
		categories.put(child.getName(), child);

		Category child_child = new Category();
		child_child.setName("����");
		child_child.setPath(SAVE_PATH + IConstants.FILE_SEPARATOR + "music"
				+ IConstants.FILE_SEPARATOR + "pop");
		child.addChild(child_child);
		categories.put(child_child.getName(), child_child);

		child_child = new Category();
		child_child.setName("��ʿ");
		child_child.setPath(SAVE_PATH + IConstants.FILE_SEPARATOR + "music"
				+ IConstants.FILE_SEPARATOR + "jazz");
		child.addChild(child_child);
		categories.put(child_child.getName(), child_child);

		child = new Category();
		child.setName("�鼮");
		child.setPath(SAVE_PATH + IConstants.FILE_SEPARATOR + "books");
		finished.addChild(child);
		categories.put(child.getName(), child);
	}

	/**
	 * ������������ط���
	 * 
	 */
	private void createDefaultCategory() {
		root = new Category();
		root.setName("RCP Get");
		root.setPath(SAVE_PATH);
		rootCategories.put(root.getName(), root);

		running = new Category();
		running.setName("��������");
		running.setPath(SAVE_PATH);
		root.addChild(running);

		finished = new Category();
		finished.setName("������");
		finished.setPath(SAVE_PATH);
		root.addChild(finished);
		// categories.put(finished.getName(), finished);

		trash = new Category();
		trash.setName("����վ");
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
	 * ����ӷ���
	 * 
	 * @param category
	 *            ����
	 * @param parentCategory
	 *            ������
	 */
	public void addCategory(Category category, Category parentCategory) {
		// ���ѡ������ķ��࣬����ӵ� ����� ������
		if (parentCategory == root || parentCategory == running
				|| parentCategory == trash) {
			parentCategory = finished;
		}
		parentCategory.addChild(category);
		this.addCategory(category);
	}

	/**
	 * ��ӷ���
	 * 
	 * @param category
	 */
	public void addCategory(Category category) {
		// rootCategories.put(category.getName(), category);
		categories.put(category.getName(), category);
		this.setChanged();
		this.notifyObservers(category);
		logger.info("���/�޸����µ����ط���");
		this.saveCategories();
	}

	public void updateCategory(Category category) {
		this.addCategory(category);
	}

	/**
	 * �ݹ�ɾ������
	 * 
	 * @param category
	 */
	private void _deleteCategory(Category category) {
		// ��Щ���಻����ɾ��
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
	 * ɾ������
	 * 
	 * @param category
	 */
	public void deleteCategory(Category category) {
		// rootCategories.remove(category.getName());
		_deleteCategory(category);

		this.setChanged();
		this.notifyObservers(category);
		logger.info("ɾ�����µ����ط���");
		this.saveCategories();
	}

	/**
	 * ��Mapת�������
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
	 * ��ȡ��������������
	 * 
	 * @return �����������ڵ�����
	 */
	public Category[] getRootCategories() {
		return _getCategories(rootCategories);
	}

	/**
	 * �Ƿ���������ӷ���
	 * 
	 * @param category
	 * @return
	 */
	public boolean isAllowAddChild(Category category) {
		return isAllowAddChild(category.getName());
	}

	/**
	 * �Ƿ���������ӷ���
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
	 * �Ƿ�����ɾ���÷���
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
	 * �Ƿ�����ɾ���÷���
	 * 
	 * @param category
	 * @return
	 */
	public boolean isAllowDelete(Category category) {
		return this.isAllowDelete(category.getName());
	}

	/**
	 * ��ȡ�������ļ��ķ���
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
			logger.info("��ȡ���з���");
		} catch (FileNotFoundException e) {
			createOtherCategory();
			saveCategories();
		} catch (Exception e) {
			logger.error("��ȡ���з��෢������", e);
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
				logger.error("�رշ����ļ���������", e);
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
				// �ݹ�����¼�
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
			// ȷ���������������з��������
			if (category.getParent() != null) {
				IMemento node = memento.createChild(TAG_CATEGORY);
				node.putString(TAG_PARENT, category.getParent().getName());
				node.putString(TAG_NAME, category.getName());
				node.putString(TAG_PATH, category.getPath());
			}
		}
	}

}
