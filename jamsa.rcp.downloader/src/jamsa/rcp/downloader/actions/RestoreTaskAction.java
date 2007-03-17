package jamsa.rcp.downloader.actions;

import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.models.TaskModel;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

/**
 * ��ԭ������
 * 
 * @author ���
 * 
 */
public class RestoreTaskAction extends Action implements ISelectionListener,
		ActionFactory.IWorkbenchAction, Observer {
	public static final String ID = DeleteTaskAction.class.getName();

	private IWorkbenchWindow window;

	private Task task;

	public RestoreTaskAction(IWorkbenchWindow window, String label) {
		setId(ID);
		setText(label);
		setToolTipText("��ԭ");
		this.window = window;
		try {
			window.getSelectionService().addSelectionListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setEnabled(false);
	}

	public void run() {
		TaskModel.getInstance().restoreTask(task);
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection incoming = (IStructuredSelection) selection;
			if (incoming.size() == 1
					&& incoming.getFirstElement() instanceof Task) {
				Task newTask = (Task) incoming.getFirstElement();
				// if (newTask != this.ta) {
				if (this.task != null)
					this.task.deleteObserver(this);
				this.task = newTask;
				this.update(null, null);
				this.task.addObserver(this);
				// }
			}
		} else {
			setEnabled(false);
		}
	}

	public void update(Observable o, Object arg) {
		// �����߳�״̬�޸Ĳ˵�״̬
		if (this.task.isDeleted())
			setEnabled(true);
		else
			setEnabled(false);
	}

	public void dispose() {
		window.getSelectionService().removeSelectionListener(this);
	}
}
