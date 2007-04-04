package jamsa.rcp.downloader.actions;

import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.wizards.TaskWizard;

import java.util.Observable;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * 修改任务动作
 * 
 * @author 朱杰
 * 
 */
public class ModifyTaskAction extends BaseTaskAction {
	public static final String ID = ModifyTaskAction.class.getName();

	public ModifyTaskAction(IWorkbenchWindow window, String label) {
		super(window, label);
		setId(ID);
		setText(label);
//		setToolTipText("修改任务");

	}

	public void run() {
		Task task = (Task) tasks.get(0);
		try {
			TaskWizard wizard = new TaskWizard(task, true);
			WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
			dialog.open();
		} catch (Exception e) {
		}
	}

	public void update(Observable o, Object arg) {
		if (tasks.isEmpty()) {
			setEnabled(false);
			return;
		}

		if (tasks.size() > 1) {
			setEnabled(false);
			return;
		}

		Task task = (Task) tasks.get(0);
		if (task.getStatus() == Task.STATUS_RUNNING)// || this.task.getStatus()
			// == Task.STATUS_FINISHED)
			setEnabled(false);
		else
			setEnabled(true);
	}
}
