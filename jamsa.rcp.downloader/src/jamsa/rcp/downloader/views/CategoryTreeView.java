package jamsa.rcp.downloader.views;

import jamsa.rcp.downloader.models.Category;
import jamsa.rcp.downloader.models.CategoryModel;
import jamsa.rcp.downloader.views.actions.AddCategoryAction;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;

/**
 * ���ط�����ͼ
 * 
 * @author ���
 * 
 */
public class CategoryTreeView extends ViewPart {
	public static final String ID = "jamsa.rcp.downloader.views.CategoryTreeView";

	// ���ṹģ��
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
		// treeViewer = new TreeViewer(parent, | SWT.MULTI
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
		createContextMenu(parent);
	}

	private void createContextMenu(Composite parent) {
		MenuManager mgr = new MenuManager();
		mgr.setRemoveAllWhenShown(true);
		mgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		Menu menu = mgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(mgr, treeViewer);
	}

	private void fillContextMenu(IMenuManager manager) {
//	getViewSite().getActionBars().getMenuManager().getItems();
//		IContributionItem[] items = getViewSite().getActionBars().getMenuManager().getItems();
//		for (int i = 0; i < items.length; i++) {
//			IAction item = (IAction)items[i];
//			manager.add(item.);
//		}
		
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