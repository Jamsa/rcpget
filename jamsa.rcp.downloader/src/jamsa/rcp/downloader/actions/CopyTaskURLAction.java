package jamsa.rcp.downloader.actions;

import jamsa.rcp.downloader.IConstants;
import jamsa.rcp.downloader.models.Task;

import java.util.Iterator;
import java.util.Observable;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * 修改任务动作
 * 
 * @author 朱杰
 * 
 */
public class CopyTaskURLAction extends BaseTaskAction {
	public static final String ID = CopyTaskURLAction.class.getName();

	public CopyTaskURLAction(IWorkbenchWindow window, String label) {
		super(window, label);
		setId(ID);
		setText(label);
		setToolTipText("复制URL");
	}

	public void run() {
		Clipboard clipboard = new Clipboard(window.getShell().getDisplay());
		TextTransfer textTransfer = TextTransfer.getInstance();
		StringBuffer urls = new StringBuffer("");
		for (Iterator it = tasks.iterator(); it.hasNext();) {
			Task task = (Task) it.next();
			urls.append(task.getFileUrl()).append(IConstants.LINE_SEPARATOR);
		}

		clipboard.setContents(new Object[] { String.valueOf(urls) },
				new TextTransfer[] { textTransfer });
	}

	public void update(Observable o, Object arg) {
//		if (tasks.isEmpty()) {
//			setEnabled(false);
//			return;
//		}
		setEnabled(true);
	}
}
