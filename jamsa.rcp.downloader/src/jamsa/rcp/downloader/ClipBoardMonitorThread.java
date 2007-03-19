package jamsa.rcp.downloader;

import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.utils.StringUtils;
import jamsa.rcp.downloader.wizards.TaskWizard;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;

public class ClipBoardMonitorThread extends Thread {
	private static ClipBoardMonitorThread instance = new ClipBoardMonitorThread();

	public static ClipBoardMonitorThread getInstance() {
		return instance;
	}

	private boolean run = true;

	public void run() {
		Display display = null;
		Clipboard clipboard = null;
		while(display!=null && clipboard!=null){
			display = Activator.getDefault().getWorkbench().getDisplay();
			clipboard = new Clipboard(display);
		}
		TextTransfer textTransfer = TextTransfer.getInstance();
		while (run && !this.isInterrupted()) {
			String textData = (String) clipboard.getContents(textTransfer);
			if (!StringUtils.isEmpty(textData)) {
				if (textData.startsWith("http")) {
					display.asyncExec(new Runnable() {
						public void run() {
							TaskWizard wizard = new TaskWizard(new Task(),
									false);
							WizardDialog dialog = new WizardDialog(null, wizard);
							dialog.open();
						}
					});
				}
			}
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				run = false;
			}
		}
		// if (textData.startsWith("http://")) {
		// fileUrlText.setText(textData);
		// }
	}

	public boolean isRun() {
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}
}
