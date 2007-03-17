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
 * 运行任务动作
 * 
 * @author 朱杰
 * 
 */
public class RunTaskAction extends Action implements ISelectionListener,
		ActionFactory.IWorkbenchAction, Observer {
	public static final String ID = RunTaskAction.class.getName();

	private IWorkbenchWindow window;

	// private Task[] tasks;

	private Task task;

	// private TaskThreadsManager threadManager =
	// TaskThreadsManager.getInstance();

	public RunTaskAction(IWorkbenchWindow window, String label) {
		setId(ID);
		setText(label);
		setToolTipText("运行任务");

		this.window = window;
		try {
			window.getSelectionService().addSelectionListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setEnabled(false);
	}

	public void run() {
		TaskThreadsManager.getInstance().start(task);
		// setEnabled(false);
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection incoming = (IStructuredSelection) selection;
			// 适应于多选
			// if(incoming.size()>0 && incoming.toArray() instanceof Task[]){
			// for (int i = 0; i < tasks.length; i++) {
			// Task task = tasks[i];
			// task.deleteObserver(this);
			// }
			// boolean enable = false;
			// for (Iterator it = incoming.iterator(); it.hasNext();) {
			// Task task = (Task) it.next();
			// task.addObserver(this);
			//					
			// //只要有一个任务是停止状态，这个动作就可用
			// if (task.getStatus() == Task.STATUS_STOP
			// || task.getStatus() == Task.STATUS_ERROR){
			// enable = true;
			// }
			// }
			// setEnabled(enable);
			// }
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
		if (this.task.getStatus() == Task.STATUS_RUNNING
				|| this.task.getStatus() == Task.STATUS_FINISHED)
			setEnabled(false);
		else
			setEnabled(true);
	}
}
