package jamsa.rcp.downloader.actions;

import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.models.TaskModel;
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
 * 重启任务动作
 * 
 * @author 朱杰
 * 
 */
public class RestartTaskAction extends Action implements ISelectionListener,
		ActionFactory.IWorkbenchAction, Observer {
	public static final String ID = "jamsa.rcp.downloader.actions.RestartTaskAction";

	private IWorkbenchWindow window;

	private Task task;

	public RestartTaskAction(IWorkbenchWindow window, String label) {
		setId(ID);
		setText(label);
		setToolTipText("重新下载");
		this.window = window;
		try {
			window.getSelectionService().addSelectionListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setEnabled(false);
	}

	public void run() {
		TaskModel.getInstance().deleteTask(task, true);
		TaskModel.getInstance().addTask(task);
		TaskThreadsManager.getInstance().restart(task);
		// setEnabled(false);
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
			}
		} else {
			setEnabled(false);
		}

	}

	public void dispose() {
		window.getSelectionService().removeSelectionListener(this);
	}

	public void update(Observable o, Object arg) {
		// 根据线程状态修改菜单状态
		if (this.task.getStatus() == Task.STATUS_FINISHED)
			setEnabled(true);
		else
			setEnabled(false);
	}
}
