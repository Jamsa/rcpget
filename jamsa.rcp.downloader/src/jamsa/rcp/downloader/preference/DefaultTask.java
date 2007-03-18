package jamsa.rcp.downloader.preference;

import jamsa.rcp.downloader.Activator;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * 首选项
 * 默认任务属性
 * @author Jamsa
 *
 */
public class DefaultTask extends PreferencePage implements
IWorkbenchPreferencePage{
	private PreferenceManager pm;

	private Text savePathText;
	private Combo categoryCombo;
	private Button definedButton;
	private Button memoLastButton;
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		final Group categoryGroup = new Group(container, SWT.NONE);
		categoryGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		categoryGroup.setText("默认分类");
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 6;
		categoryGroup.setLayout(gridLayout);

		definedButton = new Button(categoryGroup, SWT.RADIO);
		definedButton.setSelection(true);
		definedButton.setText("设定值");

		final Label categoryLabel = new Label(categoryGroup, SWT.NONE);
		categoryLabel.setText("分类");

		categoryCombo = new Combo(categoryGroup, SWT.NONE);
		categoryCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Label savePathLabel = new Label(categoryGroup, SWT.NONE);
		savePathLabel.setText("目录");

		savePathText = new Text(categoryGroup, SWT.BORDER);
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.widthHint = 160;
		savePathText.setLayoutData(gridData);

		final Button selectButton = new Button(categoryGroup, SWT.NONE);
		selectButton.setText("...");

		memoLastButton = new Button(categoryGroup, SWT.RADIO);
		memoLastButton.setText("记住上次的设置");
		new Label(categoryGroup, SWT.NONE);
		new Label(categoryGroup, SWT.NONE);
		new Label(categoryGroup, SWT.NONE);
		new Label(categoryGroup, SWT.NONE);
		new Label(categoryGroup, SWT.NONE);

		final Group startGroup = new Group(container, SWT.NONE);
		startGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		startGroup.setText("任务启动");
		startGroup.setLayout(new GridLayout());

		final Button autoButton = new Button(startGroup, SWT.RADIO);
		autoButton.setSelection(true);
		autoButton.setText("立即");

		final Button manualButton = new Button(startGroup, SWT.RADIO);
		manualButton.setText("手工");
		
		setControl();
		return container;
	}

	private void setControl() {
		String type = pm.getDefaultCategoryType();
		if(type.equals(IPreferenceValues.TASK_DEFAULT_CATEGORY_TYPE_DEFINE)){
			definedButton.setSelection(true);
			memoLastButton.setSelection(false);
		}else{
			definedButton.setSelection(false);
			memoLastButton.setSelection(true);
		}
	}

	protected void performDefaults() {
		pm.setConnectionDefault();
		setControl();
	}

	public boolean performOk() {
		// pm.setRetryDelay(Integer.parseInt(retryDelayText.getText().trim()));
		// pm.setMaxRunTasks(Integer.parseInt(maxTasksText.getText().trim()));
		return true;
	}

	
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		pm = PreferenceManager.getInstance();
	}

}
