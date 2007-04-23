package jamsa.rcp.downloader.wizards;

import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.models.TaskModel;
import jamsa.rcp.downloader.models.TaskThreadManager;
import jamsa.rcp.downloader.utils.Logger;

import org.eclipse.jface.wizard.Wizard;

/**
 * 任务向导
 * 
 * @author 朱杰
 * 
 */
public class TaskWizard extends Wizard {
	/**
	 * 向导的状态，防止打开多个向导
	 */
	private static boolean open = false;

	Logger logger = new Logger(this.getClass());

	private Task task;

	private boolean isModify = false;

	public TaskWizard(Task task, boolean isModify) throws Exception {
		if (open) {
//			throw new Exception("向导已经打开！");
			return;
		}
		this.task = task;
		this.isModify = isModify;
		open = true;
	}

	private TaskWizardPage taskWizardPage;

	private TaskModel taskModel = TaskModel.getInstance();

	public void addPages() {
		taskWizardPage = new TaskWizardPage(this.task, this.isModify);
		addPage(taskWizardPage);
	}

	public boolean canFinish() {
		return (taskWizardPage.isPageComplete());
	}

	@Override
	public boolean performFinish() {
		taskWizardPage.saveTask();
		taskModel.addTask(task);
		if (!isModify) {
			if (task.getStart() == Task.START_AUTO)
				TaskThreadManager.getInstance().start(task);
		}
		logger.info("添加了新任务");
		return true;
	}

	public static boolean isOpen() {
		return open;
	}

	public void dispose() {
		open = false;
		super.dispose();
	}

}
