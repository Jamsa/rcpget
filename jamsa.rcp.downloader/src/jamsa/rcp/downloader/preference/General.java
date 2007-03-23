package jamsa.rcp.downloader.preference;

import jamsa.rcp.downloader.Activator;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * 首选项 常规设置
 * 
 * @author Jamsa
 * 
 */
public class General extends PreferencePage implements IWorkbenchPreferencePage {
	private PreferenceManager pm;

	private Button minimizeToTrayButton;

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		minimizeToTrayButton = new Button(container, SWT.CHECK);
		minimizeToTrayButton.setText("最小化到任务栏");

		setControl();
		return container;
	}

	public boolean performOk() {
		pm.setMinimizeToTray(minimizeToTrayButton.getSelection());
		return true;
	}

	private void setControl() {
		minimizeToTrayButton.setSelection(pm.getMinimizeToTray());
	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		pm = PreferenceManager.getInstance();

	}

}
