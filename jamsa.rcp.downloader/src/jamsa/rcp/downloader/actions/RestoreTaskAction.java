package jamsa.rcp.downloader.actions;

import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.models.TaskModel;

import java.util.Iterator;
import java.util.Observable;

import org.eclipse.ui.IWorkbenchWindow;

/**
 * 还原任务动作
 * 
 * @author 朱杰
 * 
 */
public class RestoreTaskAction extends BaseTaskAction {
	public static final String ID = RestoreTaskAction.class.getName();

	public RestoreTaskAction(IWorkbenchWindow window, String label) {
		super(window, label);
		setId(ID);
		setText(label);
		setToolTipText("还原");

	}

	public void run() {
		TaskModel.getInstance().restoreTask(tasks);
	}

	public void update(Observable o, Object arg) {
		boolean enable = false;

		if (tasks != null && !tasks.isEmpty()) {
			for (Iterator it = tasks.iterator(); it.hasNext();) {
				Task task = (Task) it.next();
				if (TaskModel.getInstance().isAllowRestore(task)) {
					enable = true;
					break;
				}
			}
		}

		setEnabled(enable);
	}
}
