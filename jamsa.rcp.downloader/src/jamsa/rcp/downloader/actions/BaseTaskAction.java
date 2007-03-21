package jamsa.rcp.downloader.actions;

import jamsa.rcp.downloader.models.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

public abstract class BaseTaskAction extends Action implements
		ISelectionListener, ActionFactory.IWorkbenchAction, Observer {
	protected IWorkbenchWindow window;

	protected List tasks = new ArrayList(5);

	public BaseTaskAction(IWorkbenchWindow window, String label) {
		this.window = window;
		try {
			window.getSelectionService().addSelectionListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setEnabled(false);
	}

	protected void addObserver() {
		if (tasks != null && !tasks.isEmpty()) {
			for (Iterator it = tasks.iterator(); it.hasNext();) {
				Task task = (Task) it.next();
				task.addObserver(this);
			}
		}
	}

	protected void deleteObserver() {
		if (tasks != null && !tasks.isEmpty()) {
			for (Iterator it = tasks.iterator(); it.hasNext();) {
				Task task = (Task) it.next();
				task.deleteObserver(this);
			}
		}
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection incoming = (IStructuredSelection) selection;
			// 适应于多选
			if (incoming.size() > 0
					&& incoming.getFirstElement() instanceof Task) {
				this.deleteObserver();
				this.tasks = incoming.toList();
				this.addObserver();
				this.update(null, null);
			} else {
				this.tasks = new ArrayList();
				this.setEnabled(false);
			}
		} else {
			this.tasks = new ArrayList();
			this.setEnabled(false);
		}
	}

	public void dispose() {
		window.getSelectionService().removeSelectionListener(this);

	}

	// public void update(Observable o, Object arg) {
	// // TODO Auto-generated method stub
	//		
	// }

}
