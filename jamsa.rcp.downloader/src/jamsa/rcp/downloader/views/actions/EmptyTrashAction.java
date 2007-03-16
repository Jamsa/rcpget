package jamsa.rcp.downloader.views.actions;

import jamsa.rcp.downloader.models.Category;
import jamsa.rcp.downloader.models.CategoryModel;
import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.models.TaskModel;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * ��ջ���վ
 * 
 * @author ���
 * 
 */
public class EmptyTrashAction extends ActionDelegate implements
		IViewActionDelegate {
	public static final String ID = EmptyTrashAction.class.getName();

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
				if (CategoryModel.getInstance().getTrash() == category) {
					Task[] trashs = TaskModel.getInstance().getTasks(category);
					if(trashs!=null && trashs.length>0)
						action.setEnabled(true);
					else
						action.setEnabled(false);
				} else
					action.setEnabled(false);

			}
		}
	}

	public void run(IAction action) {
		boolean confirm = MessageDialog.openConfirm(view.getViewSite()
				.getShell(), "��ջ���վ", "ȷ��Ҫ��ջ���վ��");
		if (confirm)
			TaskModel.getInstance().emptyTrash();
	}

}
