package jamsa.rcp.downloader.views.actions;

import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.utils.Logger;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * 运行任务动作
 * @author 朱杰
 * @deprecated
 * @see RunTaskAction
 */
public class RunTaskAction extends ActionDelegate implements
		IViewActionDelegate {
	Logger logger = new Logger(this.getClass());

	// private IViewPart view;
	private Task task;

	public void init(IViewPart view) {
		// this.view = view;
	}

	public void selectionChanged(IAction action, ISelection incoming) {
		if (incoming instanceof IStructuredSelection) {
			IStructuredSelection selections = (IStructuredSelection) incoming;
			if (selections.size() == 1) {
				task = (Task) selections.getFirstElement();
			}
		}
	}

	public void run(IAction action) {
		logger.info("运行任务" + task.getFileName());
	}

}
