package jamsa.rcp.downloader.views.actions;

import jamsa.rcp.downloader.dialogs.CategoryDialog;
import jamsa.rcp.downloader.models.Category;
import jamsa.rcp.downloader.models.CategoryModel;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * 分类视图中的添加分类动作
 * @author 朱杰
 *
 */
public class AddCategoryAction extends ActionDelegate implements
		IViewActionDelegate {
	public static final String ID = "jamsa.rcp.downloader.views.actions.AddCategoryAction";

	private Category category;

	private IViewPart view;

	public void init(IViewPart view) {
		this.view = view;
	}

	public void selectionChanged(IAction action, ISelection incoming) {
		if (incoming instanceof IStructuredSelection) {
			IStructuredSelection selections = (IStructuredSelection) incoming;
			if (selections.size() == 1) {
				category = (Category) selections.getFirstElement();
				if (CategoryModel.getInstance().isAllowAddChild(category))
					action.setEnabled(true);
				else
					action.setEnabled(false);

			}
		}
	}

	public void run(IAction action) {
		CategoryDialog dialog = new CategoryDialog(view.getViewSite()
				.getShell(), this.category,new Category());
		dialog.open();
	}

}
