package jamsa.rcp.downloader.wizards;

import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.models.TaskModel;
import jamsa.rcp.downloader.utils.Logger;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.Wizard;

/**
 * 任务向导
 * 
 * @author 朱杰
 * 
 */
public class TaskWizard extends Wizard {
	Logger logger = new Logger(this.getClass());

	private Task task;

	private boolean isModify = false;

	public TaskWizard(Task task, boolean isModify) {
		this.task = task;
		this.isModify = isModify;
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
		// Task task = new Task();
		// task.setCategory(CategoryModel.getInstance().getCategory(
		// taskWizardPage.getCategoryCombo().getText()));
		// task.setFilePath(taskWizardPage.getSavePathCombo().getText().trim());//.replaceAll("\n",
		// "").replace("\r", ""));
		// task.setFileName(taskWizardPage.getFileNameText().getText().trim());//.replaceAll("\n",
		// "").replace("\r", ""));
		// task.setFileUrl(taskWizardPage.getFileUrlText().getText().trim());//.replaceAll("\n",
		// "").replace("\r", ""));
		// task.setMemo(taskWizardPage.getMemoText().getText());
		// task.setBlocks(Integer.parseInt(taskWizardPage.getBlocksCombo().getText().trim()));
		taskWizardPage.saveTask();
		taskModel.addTask(task);
		logger.info("添加了新任务");
		return true;
	}

}
