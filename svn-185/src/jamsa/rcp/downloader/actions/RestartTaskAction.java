package jamsa.rcp.downloader.actions;

import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.models.TaskThreadManager;

import java.util.Iterator;
import java.util.Observable;

import org.eclipse.ui.IWorkbenchWindow;

/**
 * 重启任务动作
 * 
 * @author 朱杰
 * 
 */
public class RestartTaskAction extends BaseTaskAction {
	public static final String ID = RestartTaskAction.class.getName();

	public RestartTaskAction(IWorkbenchWindow window, String label) {
		super(window, label);
		setId(ID);
		setText(label);
//		setToolTipText("重新下载");
	}

	public void run() {
		TaskThreadManager.getInstance().restart(tasks);
	}

	public void update(Observable o, Object arg) {
//		if (tasks.isEmpty()) {
//			setEnabled(false);
//			return;
//		}
		
		boolean enable = false;

		if (tasks != null && !tasks.isEmpty()) {
			for (Iterator it = tasks.iterator(); it.hasNext();) {
				Task task = (Task) it.next();
				if (TaskThreadManager.getInstance().isAllowRestart(task)) {
					enable = true;
					break;
				}
			}
		}

		setEnabled(enable);
	}
}
