package jamsa.rcp.downloader.preference;

import jamsa.rcp.downloader.Activator;
import jamsa.rcp.downloader.Messages;
import jamsa.rcp.downloader.monitor.ClipBoardMonitor;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * 首选项 剪贴板监视设置
 * 
 * @author 朱杰
 * 
 */
public class Monitor extends PreferencePage implements IWorkbenchPreferencePage {
	private PreferenceManager pm;

	private Text fileextText;

	private Button monitorButton;

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		monitorButton = new Button(container, SWT.CHECK);
		monitorButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		monitorButton.setText(Messages.Preference_Monitor_MonitorClipboard);

		final Group group = new Group(container, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		group.setText(Messages.Preference_Monitor_MonitorFileType);
		group.setLayout(new GridLayout());

		fileextText = new Text(group, SWT.BORDER);
		fileextText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));

		setControlValue();
		return container;
	}

	private void setControlValue() {
		monitorButton.setSelection(pm.isMonitorClipboard());
		fileextText.setText(pm.getMonitorFileType());
	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		pm = PreferenceManager.getInstance();
	}

	protected void performDefaults() {
		super.performDefaults();
		pm.setMonitorToDefault();
		setControlValue();
	}

	public boolean performOk() {
		boolean monitor = monitorButton.getSelection();
		if (monitor)
			ClipBoardMonitor.getInstance().start();
		else
			ClipBoardMonitor.getInstance().stop();
		pm.setMonitorClipboard(monitor);
		pm.setMonitorFileType(fileextText.getText().trim());
		return true;
	}

}
