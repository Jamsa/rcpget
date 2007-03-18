package jamsa.rcp.downloader.preference;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class Connection extends PreferencePage implements
IWorkbenchPreferencePage{

	private Text maxTasksText;
	private Text retryDelayText;
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		final Group timeoutGroup = new Group(container, SWT.NONE);
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gridData.widthHint = 302;
		timeoutGroup.setLayoutData(gridData);
		timeoutGroup.setText("超时");
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		timeoutGroup.setLayout(gridLayout);

		final Label retryDelayLabel = new Label(timeoutGroup, SWT.NONE);
		retryDelayLabel.setLayoutData(new GridData(125, SWT.DEFAULT));
		retryDelayLabel.setText("重试等侍时间");

		retryDelayText = new Text(timeoutGroup, SWT.BORDER);
		retryDelayText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Label sLabel = new Label(timeoutGroup, SWT.NONE);
		sLabel.setText("秒");

		final Group limitGroup = new Group(container, SWT.NONE);
		limitGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		limitGroup.setText("限制");
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		limitGroup.setLayout(gridLayout_1);

		final Label maxTasksLabel = new Label(limitGroup, SWT.NONE);
		maxTasksLabel.setLayoutData(new GridData(125, SWT.DEFAULT));
		maxTasksLabel.setText("最多同时运行任务数量");

		maxTasksText = new Text(limitGroup, SWT.BORDER);
		maxTasksText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		return container;
	}
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}

}
