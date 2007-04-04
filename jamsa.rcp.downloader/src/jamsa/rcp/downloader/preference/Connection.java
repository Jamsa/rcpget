package jamsa.rcp.downloader.preference;

import jamsa.rcp.downloader.Activator;
import jamsa.rcp.downloader.Messages;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * 首选项 网络连接设置
 * 
 * @author 朱杰
 * 
 */
public class Connection extends PreferencePage implements
		IWorkbenchPreferencePage {
	private PreferenceManager pm;

	private Spinner retryTimesSpinner;

	private Spinner retryDelaySpinner;

	private Spinner maxRunTasksSpinner;

	private Spinner timeoutSpinner;

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		final Group timeoutGroup = new Group(container, SWT.NONE);
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		// gridData.widthHint = 302;
		timeoutGroup.setLayoutData(gridData);
		timeoutGroup.setText(Messages.Preference_Connection_Connection);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		timeoutGroup.setLayout(gridLayout);

		final Label retryDelayLabel = new Label(timeoutGroup, SWT.NONE);
		// retryDelayLabel.setLayoutData(new GridData(125, SWT.DEFAULT));
		retryDelayLabel.setText(Messages.Preference_Connection_RetryDelay);

		retryDelaySpinner = new Spinner(timeoutGroup, SWT.BORDER);
		retryDelaySpinner.setMaximum(10);
		retryDelaySpinner.setMinimum(1);

		final Label sLabel = new Label(timeoutGroup, SWT.NONE);
		sLabel.setText(Messages.Preference_Connection_Second);

		final Label retryTimesLabel = new Label(timeoutGroup, SWT.NONE);
		retryTimesLabel.setText(Messages.Preference_Connection_RetryTimes);

		retryTimesSpinner = new Spinner(timeoutGroup, SWT.BORDER);
		retryTimesSpinner.setMaximum(10);
		retryTimesSpinner.setMinimum(1);
		new Label(timeoutGroup, SWT.NONE);

		final Label timeoutLabel = new Label(timeoutGroup, SWT.NONE);
		timeoutLabel.setText(Messages.Preference_Connection_Timeout);

		timeoutSpinner = new Spinner(timeoutGroup, SWT.BORDER);
		timeoutSpinner.setMaximum(5);
		timeoutSpinner.setMinimum(1);

		final Label label_1 = new Label(timeoutGroup, SWT.NONE);
		label_1.setText(Messages.Preference_Connection_Second);

		final Group limitGroup = new Group(container, SWT.NONE);
		limitGroup
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		limitGroup.setText(Messages.Preference_Connection_Limit);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		limitGroup.setLayout(gridLayout_1);

		final Label maxTasksLabel = new Label(limitGroup, SWT.NONE);
		maxTasksLabel.setLayoutData(new GridData(125, SWT.DEFAULT));
		maxTasksLabel.setText(Messages.Preference_Connection_MaxRunTasks);

		maxRunTasksSpinner = new Spinner(limitGroup, SWT.BORDER);
		maxRunTasksSpinner.setMaximum(15);
		maxRunTasksSpinner.setMinimum(1);

		setControl();
		return container;
	}

	private void setControl() {
		int delay = pm.getRetryDelay();
		retryDelaySpinner.setSelection(delay);
		int retry = pm.getRetryTimes();
		retryTimesSpinner.setSelection(retry);
		int maxTasks = pm.getMaxRunTasks();
		maxRunTasksSpinner.setSelection(maxTasks);
		int timeout = pm.getTimeout();
		timeoutSpinner.setSelection(timeout);
	}

	// protected void performApply() {
	// this.performOk();
	// }

	protected void performDefaults() {
		pm.setConnectionToDefault();
		setControl();
	}

	public boolean performOk() {
		pm.setMaxRunTasks(maxRunTasksSpinner.getSelection());
		pm.setRetryDelay(retryDelaySpinner.getSelection());
		pm.setRetryTimes(retryTimesSpinner.getSelection());
		pm.setTimeout(timeoutSpinner.getSelection());
		return true;
	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		pm = PreferenceManager.getInstance();
	}

}
