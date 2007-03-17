package jamsa.rcp.downloader.actions;

import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.models.TaskModel;
import jamsa.rcp.downloader.models.TaskThreadsManager;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

/**
 * 停止任务动作
 * 
 * @author 朱杰
 * 
 */
public class DeleteTaskAction extends Action implements ISelectionListener,
		ActionFactory.IWorkbenchAction, Observer {
	public static final String ID = DeleteTaskAction.class.getName();

	private IWorkbenchWindow window;

	private Task task;

	public DeleteTaskAction(IWorkbenchWindow window, String label) {
		setId(ID);
		setText(label);
		setToolTipText("删除选中任务");
		this.window = window;
		try {
			window.getSelectionService().addSelectionListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setEnabled(false);
	}

	public void run() {
		if (task.getStatus() == Task.STATUS_RUNNING && !task.isDeleted()) {
			boolean confirm = MessageDialog.openConfirm(window.getShell(),
					"删除任务", "要删除正在运行的任务吗？");
			if (confirm)
				TaskThreadsManager.getInstance().stop(task);
		}
		// 如果是已经被删除的任务就要提示是否要删除文件
		boolean deleteFile = false;
		if (task.isDeleted()) {
			deleteFile = MessageDialog.openConfirm(window.getShell(), "删除任务",
					"同时删除文件？");
		}

		TaskModel.getInstance().deleteTask(task, deleteFile);
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

	public void update(Observable o, Object arg) {
		// 根据线程状态修改菜单状态
		// if (this.task.getStatus() == Task.STATUS_RUNNING)
		// setEnabled(false);
		// else
		// setEnabled(true);
		if (this.task != null)
			setEnabled(true);
		else
			setEnabled(false);
	}

	public void dispose() {
		window.getSelectionService().removeSelectionListener(this);
	}
}
