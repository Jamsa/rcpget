package jamsa.rcp.downloader.views;

import jamsa.rcp.downloader.models.Category;
import jamsa.rcp.downloader.models.CategoryModel;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * 下载分类视图
 * @author 朱杰
 *
 */
public class CategoryTreeView extends ViewPart {
	public static final String ID = "jamsa.rcp.downloader.views.CategoryTreeView";

	// 树结构模型
	private CategoryModel model = CategoryModel.getInstance();

	public CategoryModel getModel() {
		return model;
	}

	public void setModel(CategoryModel model) {
		this.model = model;

	}

	private TreeViewer treeViewer;

	@Override
	public void createPartControl(Composite parent) {
		// treeViewer = new TreeViewer(parent,  | SWT.MULTI
		// | SWT.BORDER);

		treeViewer = new TreeViewer(parent, SWT.NONE);
		treeViewer.setLabelProvider(new ViewLabelProvider());
		treeViewer.setContentProvider(new ViewContentProvider());

		treeViewer.setInput(model);

		getSite().setSelectionProvider(treeViewer);

		treeViewer.expandAll();

		this.model.addObserver(new Observer() {
			public void update(Observable o, Object arg) {
				treeViewer.refresh();
			}
		});
	}

	@Override
	public void setFocus() {
		this.treeViewer.getTree().setFocus();

	}

	class ViewLabelProvider extends LabelProvider {
		public String getText(Object element) {
			if (element instanceof Category) {
				Category category = (Category) element;
				return category.getName();
			}
			return super.getText(element);
		}
	}

	class ViewContentProvider implements ITreeContentProvider {
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof Category) {
				Category category = (Category) parentElement;
				return category.getChildren().values().toArray(
						new Category[category.getChildren().size()]);
			}
			return null;
		}

		public Object getParent(Object element) {
			if (element instanceof Category) {
				Category category = (Category) element;
				return category.getParent();
			}
			return null;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof Category) {
				Category category = (Category) element;
				return !category.getChildren().isEmpty();
			}
			return false;
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof CategoryModel) {
				CategoryModel model = (CategoryModel) inputElement;
				return model.getRootCategories();
			}
			return null;
		}

		public void dispose() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

	}

}
