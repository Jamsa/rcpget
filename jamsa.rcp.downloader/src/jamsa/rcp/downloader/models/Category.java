package jamsa.rcp.downloader.models;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 下载分类
 * 
 * @author 朱杰
 * 
 */
public class Category implements Serializable {
	// 父分类
	private Category parent;

	// 子分类
	private Map children = new LinkedHashMap(0);

	// 分类名称
	private String name;

	// 分类文件保存路径
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
