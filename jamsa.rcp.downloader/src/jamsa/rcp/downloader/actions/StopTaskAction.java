package jamsa.rcp.downloader.actions;

import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.models.TaskThreadManager;

import java.util.Iterator;
import java.util.Observable;

import org.eclipse.ui.IWorkbenchWindow;

/**
 * 停止任务动作
 * 
 * @author 朱杰
 * 
 */
public class StopTaskAction extends BaseTaskAction {
	public static final String ID = StopTaskAction.class.getName();

	public StopTaskAction(IWorkbenchWindow window, String label) {
		super(window, label);
		setId(ID);
		setText(label);
		setToolTipText("停止任务");

	}

	public void run() {
		TaskThreadManager.getInstance().stop(tasks);
	}

	public void update(Observable o, Object arg) {
//		if (tasks.isEmpty()) {
//			setEnabled(false);
//			return;
//		}

		boolean enable = false;

		// 只要有一个处于运行状态就可以用
		if (tasks != null && !tasks.isEmpty()) {
			for (Iterator it = tasks.iterator(); it.hasNext();) {
				Task task = (Task) it.next();
				if (TaskThreadManager.getInstance().isAllowStop(task)) {
					enable = true;
					break;
				}
			}
		}

		setEnabled(enable);
	}
}
