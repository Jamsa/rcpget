package jamsa.rcp.downloader.preference;

import jamsa.rcp.downloader.Activator;
import jamsa.rcp.downloader.Messages;
import jamsa.rcp.downloader.models.Category;
import jamsa.rcp.downloader.models.CategoryModel;
import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.utils.StringUtils;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
 * 首选项 默认任务属性
 * 
 * @author 朱杰
 * 
 */
public class DefaultTask extends PreferencePage implements
		IWorkbenchPreferencePage {
	private PreferenceManager pm;
	private CategoryModel cm;

	private Text savePathText;

	private Combo categoryCombo;

	private Button definedButton;

	private Button memoLastButton;

	private Button autoButton;

	private Button manualButton;
	
	

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		final Group categoryGroup = new Group(container, SWT.NONE);
		categoryGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		categoryGroup.setText(Messages.Preference_DefaultTask_DefaultCategory);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 6;
		categoryGroup.setLayout(gridLayout);

		definedButton = new Button(categoryGroup, SWT.RADIO);
		definedButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {

				categoryCombo.setEnabled(true);
				savePathText.setEnabled(true);

			}

			public void widgetSelected(SelectionEvent e) {
				this.widgetDefaultSelected(e);
			}

		});
		definedButton.setSelection(true);
		definedButton.setText(Messages.Preference_DefaultTask_SettingCategory);

		final Label categoryLabel = new Label(categoryGroup, SWT.NONE);
		categoryLabel.setText(Messages.Preference_DefaultTask_Category);

		categoryCombo = new Combo(categoryGroup, SWT.NONE);
		categoryCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String categoryName = categoryCombo.getText();
				if(!StringUtils.isEmpty(categoryName)){
					Category category = cm.getCategory(categoryName);
					savePathText.setText(category.getPath());
				}
			}
		});
		categoryCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));

		final Label savePathLabel = new Label(categoryGroup, SWT.NONE);
		savePathLabel.setText(Messages.Preference_DefaultTask_Directory);

		savePathText = new Text(categoryGroup, SWT.BORDER);
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		gridData.widthHint = 160;
		savePathText.setLayoutData(gridData);

		final Button selectButton = new Button(categoryGroup, SWT.NONE);
		selectButton.setText("..."); //$NON-NLS-1$

		memoLastButton = new Button(categoryGroup, SWT.RADIO);
		memoLastButton.setText(Messages.Preference_DefaultTask_LastSelectCategory);
		memoLastButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				categoryCombo.setEnabled(false);
				savePathText.setEnabled(false);
			}

			public void widgetSelected(SelectionEvent e) {
				this.widgetDefaultSelected(e);
			}

		});
		new Label(categoryGroup, SWT.NONE);
		new Label(categoryGroup, SWT.NONE);
		new Label(categoryGroup, SWT.NONE);
		new Label(categoryGroup, SWT.NONE);
		new Label(categoryGroup, SWT.NONE);

		final Group startGroup = new Group(container, SWT.NONE);
		startGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		startGroup.setText(Messages.Preference_DefaultTask_RunTask);
		startGroup.setLayout(new GridLayout());

		autoButton = new Button(startGroup, SWT.RADIO);
		autoButton.setSelection(true);
		autoButton.setText(Messages.Preference_DefaultTask_Auto);

		manualButton = new Button(startGroup, SWT.RADIO);
		manualButton.setText(Messages.Preference_DefaultTask_Manual);

		setControlValue();
		return container;
	}

	private void setControlValue() {
		String type = pm.getDefaultCategoryType();
		if (type.equals(IPreferenceConstants.TASK_DEFAULT_CATEGORY_TYPE_DEFINE)) {
			definedButton.setSelection(true);
			memoLastButton.setSelection(false);
			categoryCombo.setEnabled(true);
			savePathText.setEnabled(true);
		} else {
			definedButton.setSelection(false);
			memoLastButton.setSelection(true);
			categoryCombo.setEnabled(false);
			savePathText.setEnabled(false);
		}
		
		String[] items = cm.getAllowSaveCategoryNames();
		categoryCombo.setItems(items);
		
		for(int i=0;i<items.length;i++){
			if(pm.getDefaultCategory().equals(items[i])){
				categoryCombo.select(i);
				break;
			}
		}
		
		savePathText.setText(pm.getDefaultSavePath());
	}

	protected void performDefaults() {
		super.performDefaults();
		pm.setConnectionToDefault();
		setControlValue();
	}

	public boolean performOk() {
		String category = categoryCombo.getText();
		if (!StringUtils.isEmpty(category)) {
			pm.setDefaultCategory(category.trim());
		}
		
		String savePath = savePathText.getText();
		if (!StringUtils.isEmpty(savePath)) {
			pm.setDefaultSavePath(savePath.trim());
		}
		
		if(definedButton.getSelection()){
			pm.setDefaultCategoryType(IPreferenceConstants.TASK_DEFAULT_CATEGORY_TYPE_DEFINE);
		}else{
			pm.setDefaultCategoryType(IPreferenceConstants.TASK_DEFAULT_CATEGORY_TYPE_LAST);
		}

		if (autoButton.getSelection()) {
			pm.setStartTaskMethod(Task.START_AUTO);
		} else {
			pm.setStartTaskMethod(Task.START_MANUAL);
		}
		return true;
	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		pm = PreferenceManager.getInstance();
		cm = CategoryModel.getInstance();
	}

}
