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

	private Text text_1;
	private Text text;
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		final Group timeoutGroup = new Group(container, SWT.NONE);
		timeoutGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		timeoutGroup.setText("TimeOut");
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		timeoutGroup.setLayout(gridLayout);

		final Label retryAfterLabel = new Label(timeoutGroup, SWT.NONE);
		retryAfterLabel.setText("Retry after");

		text = new Text(timeoutGroup, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Label sLabel = new Label(timeoutGroup, SWT.NONE);
		sLabel.setText("s");

		final Group limitGroup = new Group(container, SWT.NONE);
		limitGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		limitGroup.setText("Limit");
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		limitGroup.setLayout(gridLayout_1);

		final Label maxinumTasksLabel = new Label(limitGroup, SWT.NONE);
		maxinumTasksLabel.setLayoutData(new GridData(97, SWT.DEFAULT));
		maxinumTasksLabel.setText("Maxinum tasks");

		text_1 = new Text(limitGroup, SWT.BORDER);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		return container;
	}
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}

}
