package jamsa.rcp.downloader.views.actions;

import jamsa.rcp.downloader.Messages;
import jamsa.rcp.downloader.models.Category;
import jamsa.rcp.downloader.models.CategoryModel;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * 分类视图中的删除分类动作
 * @author 朱杰
 *
 */
public class DeleteCategoryAction extends ActionDelegate implements
		IViewActionDelegate {
	public static final String ID = "jamsa.rcp.downloader.views.actions.DeleteCategoryAction"; //$NON-NLS-1$

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
				if (CategoryModel.getInstance().isAllowDelete(category))
					action.setEnabled(true);
				else
					action.setEnabled(false);
			}
		}
	}

	public void run(IAction action) {
		boolean confirm = MessageDialog.openConfirm(view.getViewSite()
				.getShell(), Messages.DeleteCategoryAction_DeleteCategory, Messages.DeleteCategoryAction_DeleteCategoryConfirm);
		if (confirm)
			CategoryModel.getInstance().deleteCategory(category);
	}

}
