package jamsa.rcp.downloader.wizards;

import jamsa.rcp.downloader.models.CategoryModel;
import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.models.TaskModel;
import jamsa.rcp.downloader.preference.IPreferenceConstants;
import jamsa.rcp.downloader.preference.PreferenceManager;
import jamsa.rcp.downloader.utils.StringUtils;

import java.net.URL;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

/**
 * ������ҳ
 * 
 * @author ���
 * 
 */
public class TaskWizardPage extends WizardPage {
	private Task task;

	private Text memoText;

	private Text fileNameText;

	private Combo savePathCombo;

	private Combo categoryCombo;

	private Text fileUrlText;

	private Spinner blocksSpinner;

	private boolean isModify = false;

	private Button autoStartButton;

	private Button manualStartButton;

	private PreferenceManager pm;

	/**
	 * ����У�������
	 */
	private ModifyListener validateListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			validate();
		}

	};

	/**
	 * ������
	 * 
	 * @param task
	 *            �������
	 * @param isModify
	 *            �Ƿ�Ϊ�޸�����
	 */
	public TaskWizardPage(Task task, boolean isModify) {
		super("��������");
		setTitle("��������");
		setDescription("������������");
		setPageComplete(false);
		setPageComplete(true);
		this.task = task;
		this.isModify = isModify;

		pm = PreferenceManager.getInstance();
	}

	/**
	 * ����url����������ļ���
	 * 
	 * @param url
	 * @return
	 */
	private String getFileName(String url) {
		try {
			URL u = new URL(url);
			String name = u.getFile();
			int start = name.lastIndexOf("/") + 1;
			int end = name.indexOf("?");
			if (end > start)
				return name.substring(start, end);
			if (name.length() > start)
				return name.substring(start, name.length());
		} catch (Exception e) {
			// e.printStackTrace();
		}

		return "index.html";
	}

	/**
	 * ����Ԫ������У��
	 * 
	 * @return
	 */
	public boolean validate() {
		setErrorMessage(null);

		if (StringUtils.isEmpty(this.fileUrlText.getText())) {
			setErrorMessage("����дURL");
			setPageComplete(false);
			return false;
		}

		if (!isModify
				&& TaskModel.getInstance().isExist(
						this.fileUrlText.getText().trim())) {
			setErrorMessage("�������Ѵ���");
			setPageComplete(false);
			return false;
		}

		if (!this.fileUrlText.getText().trim().startsWith("http")) {
			setErrorMessage("��֧�ֵ�Э��");
			setPageComplete(false);
			return false;
		}

		if (StringUtils.isEmpty(fileNameText.getText())) {
			setErrorMessage("����д�ļ���");
			setPageComplete(false);
			return false;
		}

		if (StringUtils.isEmpty(categoryCombo.getText())) {
			setErrorMessage("��ѡ���������");
			setPageComplete(false);
			return false;
		}

		if (StringUtils.isEmpty(savePathCombo.getText())) {
			setErrorMessage("��ѡ�����д�ļ�����·��");
			setPageComplete(false);
			return false;
		}

		// if (StringUtils.isEmpty(blocksCombo.getText())) {
		// setErrorMessage("��ѡ�������߳�����");
		// setPageComplete(false);
		// return false;
		// }
		//
		// if (Integer.parseInt(blocksCombo.getText()) < task.getBlocks()) {
		// setErrorMessage("��������������߳�����");
		// setPageComplete(false);
		// return false;
		//
		// }

		this.setPageComplete(true);
		return true;
	}

	/**
	 * ����������Ԫ�����У�������
	 * 
	 */
	private void addValidateListener() {
		memoText.addModifyListener(validateListener);
		fileNameText.addModifyListener(validateListener);
		savePathCombo.addModifyListener(validateListener);
		categoryCombo.addModifyListener(validateListener);
		fileUrlText.addModifyListener(validateListener);
	}

	/**
	 * ������д�뵽Task������
	 * 
	 */
	public void saveTask() {
		task.setFileUrl(fileUrlText.getText().trim());
		task.setFileName(fileNameText.getText().trim());
		task.setCategory(CategoryModel.getInstance().getCategory(
				categoryCombo.getText().trim()));
		task.setFilePath(savePathCombo.getText().trim());
		task.setMemo(memoText.getText().trim() + "");
		task.setBlocks(blocksSpinner.getSelection());
		if (autoStartButton.getSelection())
			task.setStart(Task.START_AUTO);
		else
			task.setStart(Task.START_MANUAL);

		if (pm.getDefaultCategoryType().equals(
				IPreferenceConstants.TASK_DEFAULT_CATEGORY_TYPE_LAST)) {
			pm.setDefaultCategoryType(task.getCategory().getName());
			pm.setDefaultSavePath(task.getCategory().getPath());
		}
	}

	/**
	 * ��������Ԫ�ص�ֵ
	 * 
	 */
	private void setControlValue(Composite parent) {

		// ѡ�е�һ����¼
		categoryCombo.select(1);

		memoText.setText(task.getMemo() == null ? "" : task.getMemo());

		fileNameText.setText(task.getFileName() == null ? "" : task
				.getFileName());

		fileUrlText.setText(task.getFileUrl() == null ? "" : task.getFileUrl());

		blocksSpinner.setSelection(task.getBlocks());

		// savePathCombo.setText(task.getFilePath() == null ? "" : task
		// .getFilePath());

		String[] items = categoryCombo.getItems();
		if (items != null && task.getCategory() != null) {
			for (int i = 0; i < items.length; i++) {
				String item = items[i];
				if (item.trim().equals(task.getCategory().getName())) {
					categoryCombo.select(i);
					break;
				}
			}
		} else {
			categoryCombo.select(1);
		}

		if (task.getStart() == Task.START_AUTO) {
			autoStartButton.setSelection(true);
			manualStartButton.setSelection(false);
		} else {
			autoStartButton.setSelection(false);
			manualStartButton.setSelection(true);
		}

		if (isModify) {
			fileNameText.setEnabled(false);
			fileUrlText.setEnabled(false);
			categoryCombo.setEnabled(false);
			savePathCombo.setEnabled(false);
			autoStartButton.setEnabled(false);
			manualStartButton.setEnabled(false);
		} else {
			// �Ӽ�����ճ��url
			// Clipboard clipboard = new Clipboard(parent.getDisplay());
			// TextTransfer textTransfer = TextTransfer.getInstance();
			// String textData = (String) clipboard.getContents(textTransfer);
			//
			// if (!StringUtils.isEmpty(textData)
			// && textData.startsWith("http://")) {
			// textData = textData.trim();
			// textData = textData.split(" ")[0];
			// textData = textData.split("\n")[0];
			// fileUrlText.setText(textData);
			// }

			if (pm.getStartTaskMethod() == Task.START_AUTO) {
				autoStartButton.setSelection(true);
				manualStartButton.setSelection(false);
			} else {
				autoStartButton.setSelection(false);
				manualStartButton.setSelection(true);
			}

			String categoryName = pm.getDefaultCategory();

			String[] categories = categoryCombo.getItems();
			for (int i = 0; i < categories.length; i++) {
				if (categories[i].equals(categoryName)) {
					categoryCombo.select(i);
					break;
				}
			}
			String savePath = pm.getDefaultSavePath();
			savePathCombo.setText(savePath);
		}

	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setControl(container);

		final Shell shell = parent.getShell();
		final GridLayout gridLayout_3 = new GridLayout();
		gridLayout_3.numColumns = 3;
		container.setLayout(gridLayout_3);

		// �ļ���ַ
		final Label fileUrlLabel = new Label(container, SWT.NONE);
		fileUrlLabel.setLayoutData(new GridData());
		fileUrlLabel.setText("URL");

		fileUrlText = new Text(container, SWT.BORDER);
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1);
		// gridData.widthHint = 410;
		fileUrlText.setLayoutData(gridData);

		// ����url�޸��¼�
		fileUrlText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				fileNameText.setText(getFileName(fileUrlText.getText()));
			}

		});

		// ���ط���
		final Label categoryLabel = new Label(container, SWT.NONE);
		categoryLabel.setLayoutData(new GridData());
		categoryLabel.setText("����");

		categoryCombo = new Combo(container, SWT.READ_ONLY);
		final GridData gridData_1 = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		// gridData_1.widthHint = 241;
		categoryCombo.setLayoutData(gridData_1);

		// ���÷���ѡ���
		categoryCombo.setItems(CategoryModel.getInstance()
				.getAllowSaveCategoryNames());

		// ��������ѡ��
		categoryCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				savePathCombo.setText(CategoryModel.getInstance().getCategory(
						categoryCombo.getText()).getPath());
			}

		});

		final Button addCategoryButton = new Button(container, SWT.NONE);
		addCategoryButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false));
		addCategoryButton.setText("���(&A)");

		// ����Ŀ¼
		final Label savePathLabel = new Label(container, SWT.NONE);
		savePathLabel.setLayoutData(new GridData());
		savePathLabel.setText("����Ŀ¼");

		savePathCombo = new Combo(container, SWT.NONE);
		savePathCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));

		final Button selectSavePathButton = new Button(container, SWT.NONE);
		selectSavePathButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false));

		selectSavePathButton.setText("ѡ��(&S)");
		// ѡ�񱣴�Ŀ¼
		selectSavePathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(shell);
				dialog.setFilterPath(savePathCombo.getText());
				savePathCombo.setText(dialog.open());
			}
		});

		// �ļ���
		final Label fileNameLabel = new Label(container, SWT.NONE);
		fileNameLabel.setLayoutData(new GridData());
		fileNameLabel.setText("�ļ���");

		fileNameText = new Text(container, SWT.BORDER);
		fileNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		new Label(container, SWT.NONE);

		// �߳�����
		final Label blocksLabel = new Label(container, SWT.NONE);
		blocksLabel.setLayoutData(new GridData());
		blocksLabel.setText("�߳�����");

		blocksSpinner = new Spinner(container, SWT.BORDER);
		blocksSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		blocksSpinner.setMinimum(1);
		blocksSpinner.setMaximum(10);
		blocksSpinner.setSelection(5);
		new Label(container, SWT.NONE);

		// ��ע
		final Group memoGroup = new Group(container, SWT.NONE);
		memoGroup.setText("��ע");
		final GridData gridData_2 = new GridData(SWT.FILL, SWT.FILL, false,
				true, 2, 1);
		memoGroup.setLayoutData(gridData_2);
		memoGroup.setLayout(new GridLayout());

		memoText = new Text(memoGroup, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);
		final GridData gridData_3 = new GridData(SWT.FILL, SWT.FILL, true,
				true, 2, 1);
		memoText.setLayoutData(gridData_3);

		// ������ʽ
		final Group startGroup = new Group(container, SWT.NONE);
		startGroup.setText("������ʽ");
		startGroup
				.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));
		startGroup.setLayout(new GridLayout());

		autoStartButton = new Button(startGroup, SWT.RADIO);
		autoStartButton.setSelection(true);
		autoStartButton.setText("����");

		manualStartButton = new Button(startGroup, SWT.RADIO);
		manualStartButton.setText("�ֶ�");

		addValidateListener();
		this.setControlValue(parent);
		// if (!isModify)
		// setPageComplete(false);

	}

}
