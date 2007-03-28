package jamsa.rcp.downloader.models;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ���ط���
 * 
 * @author ���
 * 
 */
public class Category implements Serializable {
	// ������
	private Category parent;

	// �ӷ���
	private Map children = new LinkedHashMap(0);

	// ��������
	private String name;

	// �����ļ�����·��
	private String path;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Map getChildren() {
		return children;
	}

	public void setChildren(Map children) {
		this.children = children;
	}

	public void addChild(Category child) {
		child.setParent(this);
		children.put(child.getName(), child);
	}

	public void removeChild(Category child) {
		children.remove(child.getName());
		child.setParent(null);
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		// if (parent != null)
		// parent.addChild(this);
		this.parent = parent;
	}

}
