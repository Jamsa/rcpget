package jamsa.rcp.downloader.actions;

import jamsa.rcp.downloader.Activator;
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
 * ֹͣ������
 * 
 * @author ���
 * 
 */
public class DeleteTaskAction extends BaseTaskAction {
	public static final String ID = DeleteTaskAction.class.getName();

	public DeleteTaskAction(IWorkbenchWindow window, String label) {
		super(window, label);
		setId(ID);
		setText(label);
		setToolTipText("ɾ��ѡ������");
	}

	private boolean someTaskRun() {
		for (Iterator it = tasks.iterator(); it.hasNext();) {
			Task task = (Task) it.next();
			if (task.getStatus() == Task.STATUS_RUNNING)
				return true;
		}
		return false;
	}

	private static final String DELETE_FILE_IN_DELETE_TASK = "DELETE_FILE_IN_DELETE_TASK";

	public void run() {
		// һ����������������ڻ���վ��
		if (!someTaskRun() && !((Task) tasks.get(0)).isDeleted()) {
			TaskModel.getInstance().deleteTask(tasks);
			return;
		}

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		boolean confirm = false;
		boolean deleteFile = false;
		MessageDialogWithToggle dialog = null;
		store.setValue(DELETE_FILE_IN_DELETE_TASK, "");
		// ������������ʱ��ɾ��
		if (someTaskRun() && !((Task) tasks.get(0)).isDeleted()) {
			confirm = MessageDialog.openConfirm(window.getShell(), "ɾ������",
					"Ҫɾ���������е�������");
			if (confirm)
				TaskThreadManager.getInstance().stop(tasks);
		}

		// ������Ѿ���ɾ���������Ҫ��ʾ�Ƿ�Ҫɾ���ļ�
		if (((Task) tasks.get(0)).isDeleted()) {
			dialog = MessageDialogWithToggle.openOkCancelConfirm(window
					.getShell(), "ɾ������", "ȷ��Ҫɾ����������", "ͬʱɾ���ļ�", false, store,
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
		if (this.tasks != null)
			setEnabled(true);
		else
			setEnabled(false);
	}

}
