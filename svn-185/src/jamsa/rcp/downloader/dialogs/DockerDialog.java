package jamsa.rcp.downloader.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class DockerDialog extends Shell {

	/**
	 * Create the shell
	 * 
	 * @param display
	 * @param style
	 */
	public DockerDialog(Display display, int style) {
		super(display, style);
		createContents();
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents() {
		final Button button = new Button(this, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dispose();
			}
		});
		button.setText("button");
		button.setBounds(0, 0, 48, 22);
		// getShell().setVisible(false);

		Clipboard clipboard = new Clipboard(getDisplay());
		TextTransfer textTransfer = TextTransfer.getInstance();

		String last = (String) clipboard.getContents(textTransfer);
		while (!this.isDisposed()) {
			String current = (String) clipboard.getContents(textTransfer);
			if (!current.equals(last)) {
				System.out.println("剪贴板发生改变！");
				last = current;
			}
			getDisplay().sleep();
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
