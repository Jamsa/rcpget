package jamsa.rcp.downloader.views;

import jamsa.rcp.downloader.Activator;
import jamsa.rcp.downloader.IImageKeys;
import jamsa.rcp.downloader.models.Category;
import jamsa.rcp.downloader.models.CategoryModel;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * 下载分类视图
 * 
 * @author 朱杰
 * 
 */
public class CategoryTreeView extends ViewPart {
	public static final String ID = CategoryTreeView.class.getName();

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
//		getViewSite().getActionBars().getToolBarManager().getItems();
		IContributionItem[] items = getViewSite().getActionBars()
				.getMenuManager().getItems();
		// manager.findMenuUsingPath("category");
		for (int i = 0; i < items.length; i++) {
			manager.add(items[i]);
		}
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

		public Image getImage(Object element) {
			if (element instanceof Category) {
				Category category = (Category) element;
				if (category == CategoryModel.getInstance().getRoot())
					return AbstractUIPlugin.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, IImageKeys.RCP_GET)
							.createImage();
				if (category == CategoryModel.getInstance().getTrash())
					return AbstractUIPlugin.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, IImageKeys.TRASH)
							.createImage();

			}
			return AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IImageKeys.CATEGORY).createImage();
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
