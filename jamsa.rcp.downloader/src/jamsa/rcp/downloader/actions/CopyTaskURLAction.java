package jamsa.rcp.downloader.actions;

import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.utils.StringUtils;
import jamsa.rcp.downloader.wizards.TaskWizard;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

/**
 * 修改任务动作
 * 
 * @author 朱杰
 * 
 */
public class CopyTaskURLAction extends Action implements ISelectionListener,
		ActionFactory.IWorkbenchAction, Observer {
	public static final String ID = CopyTaskURLAction.class.getName();

	private IWorkbenchWindow window;

	private Task task;

	// private TaskThreadsManager threadManager =
	// TaskThreadsManager.getInstance();

	public CopyTaskURLAction(IWorkbenchWindow window, String label) {
		setId(ID);
		setText(label);
		setToolTipText("复制URL");
		this.window = window;
		try {
			window.getSelectionService().addSelectionListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setEnabled(false);
	}

	public void run() {
		Clipboard clipboard = new Clipboard(window.getShell().getDisplay());
		TextTransfer textTransfer = TextTransfer.getInstance();
		clipboard.setContents(new Object[]{task.getFileUrl()}, new TextTransfer[]{textTransfer});
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection incoming = (IStructuredSelection) selection;
			if (incoming.size() == 1
					&& incoming.getFirstElement() instanceof Task) {
				Task newTask = (Task) incoming.getFirstElement();
				if (newTask != this.task) {
					if (this.task != null)
						this.task.deleteObserver(this);
					this.task = newTask;
					this.update(null, null);
					this.task.addObserver(this);
				}
				// this.task = (Task) incoming.getFirstElement();
			}
		} else {
			setEnabled(false);
		}
	}

	public void dispose() {
		window.getSelectionService().removeSelectionListener(this);
	}

	public void update(Observable o, Object arg) {
		// 根据线程状态修改菜单状态
//		if (this.task.getStatus() == Task.STATUS_RUNNING)//|| this.task.getStatus() == Task.STATUS_FINISHED)
//			setEnabled(false);
//		else
			setEnabled(true);
	}
}
