package jamsa.rcp.downloader.actions;

import jamsa.rcp.downloader.Activator;
import jamsa.rcp.downloader.Messages;
import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.models.TaskModel;
import jamsa.rcp.downloader.models.TaskThreadManager;

import java.util.Iterator;
import java.util.Observable;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * 停止任务动作
 * 
 * @author 朱杰
 * 
 */
public class DeleteTaskAction extends BaseTaskAction {
	public static final String ID = DeleteTaskAction.class.getName();

	public DeleteTaskAction(IWorkbenchWindow window, String label) {
		super(window, label);
		setId(ID);
		setText(label);
//		setToolTipText("删除选中任务");
	}

	private boolean someTaskRun() {
		for (Iterator it = tasks.iterator(); it.hasNext();) {
			Task task = (Task) it.next();
			if (task.getStatus() == Task.STATUS_RUNNING)
				return true;
		}
		return false;
	}

	private static final String DELETE_FILE_IN_DELETE_TASK = "DELETE_FILE_IN_DELETE_TASK"; //$NON-NLS-1$

	public void run() {
		// 一般情况，且任务不是在回收站中
		if (!someTaskRun() && !((Task) tasks.get(0)).isDeleted()) {
			TaskModel.getInstance().deleteTask(tasks);
			return;
		}

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		boolean confirm = false;
		boolean deleteFile = false;
		MessageDialogWithToggle dialog = null;
		store.setValue(DELETE_FILE_IN_DELETE_TASK, ""); //$NON-NLS-1$
		// 任务正在运行时的删除
		if (someTaskRun() && !((Task) tasks.get(0)).isDeleted()) {
			confirm = MessageDialog.openConfirm(window.getShell(), Messages.DeleteTaskAction_DeleteTask,
					Messages.DeleteTaskAction_DeleteRunningTaskConfirm);
			if (confirm)
				TaskThreadManager.getInstance().stop(tasks);
		}

		// 如果是已经被删除的任务就要提示是否要删除文件
		if (((Task) tasks.get(0)).isDeleted()) {
			dialog = MessageDialogWithToggle.openOkCancelConfirm(window
					.getShell(), Messages.DeleteTaskAction_DeleteTask, Messages.DeleteTaskAction_DeleteTaskConfirm, Messages.DeleteTaskAction_DeleteTaskFile, false, store,
					DELETE_FILE_IN_DELETE_TASK);
			confirm = dialog.getReturnCode() == MessageDialogWithToggle.OK;
			deleteFile = MessageDialogWithToggle.ALWAYS.equals(store
					.getString(DELETE_FILE_IN_DELETE_TASK));
		}

		if (confirm) {
			TaskModel.getInstance().deleteTask(tasks, deleteFile);
		}
	}

	public void update(Observable o, Object arg) {
//		if (tasks.isEmpty()) {
//			setEnabled(false);
//			return;
//		}
		if (this.tasks != null)
			setEnabled(true);
		else
			setEnabled(false);
	}

}
