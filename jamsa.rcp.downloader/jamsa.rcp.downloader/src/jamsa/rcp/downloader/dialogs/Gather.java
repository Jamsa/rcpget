package jamsa.rcp.downloader.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Gather extends Shell {

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			Gather shell = new Gather(display, SWT.SHELL_TRIM);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell
	 * @param display
	 * @param style
	 */
	public Gather(Display display, int style) {
		super(display, style);
		createContents();
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents() {
		setText("SWT Application");
		setSize(500, 375);
		//
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
