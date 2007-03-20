package jamsa.rcp.downloader.wizards;

import jamsa.rcp.downloader.models.CategoryModel;
import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.utils.StringUtils;

import java.net.URL;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
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
		setDescription("��������");
		setPageComplete(false);
		setPageComplete(true);

		this.task = task;
		this.isModify = isModify;
	}

	/**
	 * ����url����������ļ���
	 * 
	 * @param url
	 * @return
	 */
	private String getFileName(String url) {
		// String fileUrl = url.trim();
		// int fileNameIndex = fileUrl.lastIndexOf("/");
		// int paramIndex = fileUrl.indexOf('?');
		// int length = fileUrl.length();
		// if (fileNameIndex > 0 && fileNameIndex + 1 < length && paramIndex <=
		// 0)
		// return fileUrl.substring(fileNameIndex + 1, fileUrl.length());
		// if (fileNameIndex > 0 && fileNameIndex + 1 < length && paramIndex >
		// 0)
		// return fileUrl.substring(fileNameIndex + 1, paramIndex);

		try {
			URL u = new URL(url);
			String name = u.getFile();
			int start = name.lastIndexOf("/") + 1;
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

		if (!this.fileUrlText.getText().trim().startsWith("http")) {
			setErrorMessage("Alpha�汾����ֻ֧��HttpЭ��");
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

//		if (StringUtils.isEmpty(blocksCombo.getText())) {
//			setErrorMessage("��ѡ�������߳�����");
//			setPageComplete(false);
//			return false;
//		}
//
//		if (Integer.parseInt(blocksCombo.getText()) < task.getBlocks()) {
//			setErrorMessage("��������������߳�����");
//			setPageComplete(false);
//			return false;
//
//		}

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
	}

	/**
	 * ��������Ԫ�ص�ֵ
	 * 
	 */
	private void setControl() {
		memoText.setText(task.getMemo() == null ? "" : task.getMemo());

		fileNameText.setText(task.getFileName() == null ? "" : task
				.getFileName());

		fileUrlText.setText(task.getFileUrl() == null ? "" : task.getFileUrl());
		//blocksCombo.select(task.getBlocks() == 0 ? 1 : (task.getBlocks() - 1));
		blocksSpinner.setSelection(task.getBlocks());
		savePathCombo.setText(task.getFilePath() == null ? "" : task
				.getFilePath());
		String[] items = categoryCombo.getItems();
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				String item = items[i];
				if (item.trim().equals(task.getCategory().getName())) {
					categoryCombo.select(i);
					break;
				}
			}
		}
		if (isModify) {
			fileNameText.setEnabled(false);
			fileUrlText.setEnabled(false);
			categoryCombo.setEnabled(false);
			savePathCombo.setEnabled(false);
		}

	}

	public void createControl(Composite parent) {
		Clipboard clipboard = new Clipboard(parent.getDisplay());
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		setControl(container);

		final Group group = new Group(container, SWT.NONE);
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false,
				false);
		gridData.widthHint = 475;
		group.setLayoutData(gridData);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		group.setLayout(gridLayout);

		final Label fileUrlLabel = new Label(group, SWT.NONE);
		fileUrlLabel.setText("URL");

		fileUrlText = new Text(group, SWT.BORDER);
		fileUrlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));

		final Group group_1 = new Group(container, SWT.NONE);
		group_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 3;
		group_1.setLayout(gridLayout_1);

		final Label categoryLabel = new Label(group_1, SWT.NONE);
		categoryLabel.setText("����");

		categoryCombo = new Combo(group_1, SWT.READ_ONLY);
		categoryCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));

		final Button addCategoryButton = new Button(group_1, SWT.NONE);
		addCategoryButton.setText("���(&A)");

		final Label savePathLabel = new Label(group_1, SWT.NONE);
		savePathLabel.setText("����Ŀ¼");

		savePathCombo = new Combo(group_1, SWT.NONE);
		savePathCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));

		final Button selectSavePathButton = new Button(group_1, SWT.NONE);

		selectSavePathButton.setText("ѡ��(&S)");

		final Label fileNameLabel = new Label(group_1, SWT.NONE);
		fileNameLabel.setText("�ļ���");

		fileNameText = new Text(group_1, SWT.BORDER);
		fileNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));

		final Group group_2 = new Group(container, SWT.NONE);
		group_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 2;
		group_2.setLayout(gridLayout_2);

		final Label memoLabel = new Label(group_2, SWT.NONE);
		memoLabel.setText("��ע");

		memoText = new Text(group_2, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);
		final GridData gridData_1 = new GridData(SWT.FILL, SWT.FILL, true,
				false);
		gridData_1.heightHint = 50;
		memoText.setLayoutData(gridData_1);

		new Label(group_1, SWT.NONE);

		final Label blocksLabel = new Label(group_1, SWT.NONE);
		blocksLabel.setText("�߳�����");

		blocksSpinner = new Spinner(group_1, SWT.BORDER);
		blocksSpinner.setMinimum(1);
		blocksSpinner.setMaximum(10);
		blocksSpinner.setLayoutData(new GridData());

//		for (int i = 1; i < 11; i++) {
//			blocksCombo.add(i + "", i - 1);
//		}
		blocksSpinner.setSelection(5);
		new Label(group_1, SWT.NONE);

		this.setControl();

		final Shell shell = parent.getShell();
		// ѡ�񱣴�Ŀ¼
		selectSavePathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(shell);
				dialog.setFilterPath(savePathCombo.getText());
				savePathCombo.setText(dialog.open());
			}
		});

		// ����url�޸��¼�
		fileUrlText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				fileNameText.setText(getFileName(fileUrlText.getText()));

			}

		});

		// �Ӽ�����ճ��url
		TextTransfer textTransfer = TextTransfer.getInstance();
		String textData = (String) clipboard.getContents(textTransfer);

		if (!StringUtils.isEmpty(textData) && textData.startsWith("http://")) {
			textData = textData.trim();
			fileUrlText.setText(textData);
		}

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

		// ѡ�е�һ����¼
		categoryCombo.select(1);

		addValidateListener();
	}

}
