package jamsa.rcp.downloader.actions;

import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.models.TaskThreadsManager;

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
 * ֹͣ������
 * 
 * @author ���
 * 
 */
public class StopTaskAction extends Action implements ISelectionListener,
		ActionFactory.IWorkbenchAction, Observer {
	public static final String ID = "jamsa.rcp.downloader.actions.StopTaskAction";

	private IWorkbenchWindow window;

	// private TaskThreadsManager threadManager =
	// TaskThreadsManager.getInstance();

	private Task task;

	public StopTaskAction(IWorkbenchWindow window, String label) {
		setId(ID);
		setText(label);
		setToolTipText("&Stop the selected task.");
		this.window = window;
		try {
			window.getSelectionService().addSelectionListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setEnabled(false);
	}

	public void run() {
		TaskThreadsManager.getInstance().stop(task);
		// this.setEnabled(false);
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection incoming = (IStructuredSelection) selection;
			if (incoming.size() == 1
					&& incoming.getFirstElement() instanceof Task) {
				Task newTask = (Task) incoming.getFirstElement();
				if (newTask != this.task) {
					if (this.task != null)
						this.task.deleteObserver(this);
					this.task = newTask;
					this.update(null, null);
					this.task.addObserver(this);
				}
				// task = (Task) incoming.getFirstElement();
				// if (task.getStatus() == Task.STATUS_RUNNING)
				// setEnabled(true);
				// else
				// setEnabled(false);
			}
		} else {
			setEnabled(false);
		}
		// setEnabled(true);

	}

	public void update(Observable o, Object arg) {
		// �����߳�״̬�޸Ĳ˵�״̬
		if (this.task.getStatus() == Task.STATUS_RUNNING)
			setEnabled(true);
		else
			setEnabled(false);
	}

	public void dispose() {
		window.getSelectionService().removeSelectionListener(this);
	}
}
