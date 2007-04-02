package jamsa.rcp.downloader.preference;

import jamsa.rcp.downloader.Activator;

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
 * @author Jamsa
 * 
 */
public class Connection extends PreferencePage implements
		IWorkbenchPreferencePage {
	private PreferenceManager pm;

	private Spinner retryTimesSpinner;

	private Spinner retryDelaySpinner;

	private Spinner maxRunTasksSpinner;

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		final Group timeoutGroup = new Group(container, SWT.NONE);
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		gridData.widthHint = 302;
		timeoutGroup.setLayoutData(gridData);
		timeoutGroup.setText("连接");
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		timeoutGroup.setLayout(gridLayout);

		final Label retryDelayLabel = new Label(timeoutGroup, SWT.NONE);
		retryDelayLabel.setLayoutData(new GridData(125, SWT.DEFAULT));
		retryDelayLabel.setText("重试等侍时间");

		retryDelaySpinner = new Spinner(timeoutGroup, SWT.BORDER);
		retryDelaySpinner.setMaximum(10);
		retryDelaySpinner.setMinimum(1);

		final Label sLabel = new Label(timeoutGroup, SWT.NONE);
		sLabel.setText("秒");

		final Label retryTimesLabel = new Label(timeoutGroup, SWT.NONE);
		retryTimesLabel.setText("重试次数");

		retryTimesSpinner = new Spinner(timeoutGroup, SWT.BORDER);
		retryTimesSpinner.setMaximum(10);
		retryTimesSpinner.setMinimum(1);
		new Label(timeoutGroup, SWT.NONE);

		final Group limitGroup = new Group(container, SWT.NONE);
		limitGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		limitGroup.setText("限制");
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		limitGroup.setLayout(gridLayout_1);

		final Label maxTasksLabel = new Label(limitGroup, SWT.NONE);
		maxTasksLabel.setLayoutData(new GridData(125, SWT.DEFAULT));
		maxTasksLabel.setText("最多同时运行任务数量");

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
		return true;
	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		pm = PreferenceManager.getInstance();
	}

}
