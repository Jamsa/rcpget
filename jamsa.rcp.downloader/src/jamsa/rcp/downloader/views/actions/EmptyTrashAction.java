package jamsa.rcp.downloader.views.actions;

import jamsa.rcp.downloader.Activator;
import jamsa.rcp.downloader.models.Category;
import jamsa.rcp.downloader.models.CategoryModel;
import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.models.TaskModel;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * 清空回收站
 * 
 * @author 朱杰
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
					if (trashs != null && trashs.length > 0)
						action.setEnabled(true);
					else
						action.setEnabled(false);
				} else
					action.setEnabled(false);

			}
		}
	}

	private static final String DELETE_FILE_IN_TRASH = "DELETE_FILE_IN_TRASH";

	public void run(IAction action) {
		// boolean confirm = MessageDialog.openConfirm(view.getViewSite()
		// .getShell(), "清空回收站", "要同时清空回收站中的文件吗？");
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setValue(DELETE_FILE_IN_TRASH, "");
		MessageDialogWithToggle dialog = MessageDialogWithToggle
				.openOkCancelConfirm(view.getViewSite().getShell(), "清空回收站",
						"确定要清空回收站吗？", "同时删除文件", false, store,
						DELETE_FILE_IN_TRASH);
		boolean confirm = dialog.getReturnCode() == MessageDialogWithToggle.OK;
		boolean deleteFile = MessageDialogWithToggle.ALWAYS.equals(store
				.getString(DELETE_FILE_IN_TRASH));
		if (confirm) {
			TaskModel.getInstance().emptyTrash(deleteFile);
		}
	}

}
