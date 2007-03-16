package jamsa.rcp.downloader.actions;

import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.wizards.TaskWizard;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * 新建任务动作
 * @author 朱杰
 *
 */
public class NewTaskAction extends Action {
	private final IWorkbenchWindow window;

	public NewTaskAction(IWorkbenchWindow window, String label) {
		this.window = window;
		setText(label);
		setId(this.getClass().getName());
	}

	public void run() {
		TaskWizard wizard = new TaskWizard(new Task(),false);
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.open();
	}
}
