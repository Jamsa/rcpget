package jamsa.rcp.downloader.dialogs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DeleteConfirmDialog extends MessageDialog{

	public DeleteConfirmDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage,
				dialogImageType, dialogButtonLabels, defaultIndex);
		
	}
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		
		return container;
	}

}
