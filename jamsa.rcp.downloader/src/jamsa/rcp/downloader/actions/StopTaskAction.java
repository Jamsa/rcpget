package jamsa.rcp.downloader.actions;

import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.models.TaskThreadManager;

import java.util.Iterator;
import java.util.Observable;

import org.eclipse.ui.IWorkbenchWindow;

/**
 * ֹͣ������
 * 
 * @author ���
 * 
 */
public class StopTaskAction extends BaseTaskAction {
	public static final String ID = StopTaskAction.class.getName();

	public StopTaskAction(IWorkbenchWindow window, String label) {
		super(window, label);
		setId(ID);
		setText(label);
		setToolTipText("ֹͣ����");

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

		// ֻҪ��һ����������״̬�Ϳ�����
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
