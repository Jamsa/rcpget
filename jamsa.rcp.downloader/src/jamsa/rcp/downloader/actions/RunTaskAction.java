package jamsa.rcp.downloader.actions;

import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.models.TaskThreadManager;

import java.util.Iterator;
import java.util.Observable;

import org.eclipse.ui.IWorkbenchWindow;

/**
 * 运行任务动作
 * 
 * @author 朱杰
 * 
 */
public class RunTaskAction extends BaseTaskAction {
	public static final String ID = RunTaskAction.class.getName();

	public RunTaskAction(IWorkbenchWindow window, String label) {
		super(window,label);
		setId(ID);
		setText(label);
		setToolTipText("运行任务");
	}

	public void run() {
		TaskThreadManager.getInstance().start(tasks);
	}

	public void update(Observable o, Object arg) {
//		if (tasks.isEmpty()) {
//			setEnabled(false);
//			return;
//		}
		
		boolean enable = false;

		// 只要有一个处于停止或者错误状态就可以启动
		if (tasks != null && !tasks.isEmpty()) {
			for (Iterator it = tasks.iterator(); it.hasNext();) {
				Task task = (Task) it.next();
				if (TaskThreadManager.getInstance().isAllowStart(task)){
					enable = true;
					break;
				}
			}
		}

		setEnabled(enable);
	}

}
