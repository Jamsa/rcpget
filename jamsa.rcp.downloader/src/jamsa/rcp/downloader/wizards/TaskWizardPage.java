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
 * 任务向导页
 * 
 * @author 朱杰
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
	 * 数据校验监听器
	 */
	private ModifyListener validateListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			validate();
		}

	};

	/**
	 * 构造器
	 * 
	 * @param task
	 *            任务对象
	 * @param isModify
	 *            是否为修改任务
	 */
	public TaskWizardPage(Task task, boolean isModify) {
		super("下载任务");
		setTitle("下载任务");
		setDescription("下载任务");
		setPageComplete(false);
		setPageComplete(true);

		this.task = task;
		this.isModify = isModify;
	}

	/**
	 * 根据url决定保存的文件名
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
	 * 输入元素数据校验
	 * 
	 * @return
	 */
	public boolean validate() {
		setErrorMessage(null);
		if (StringUtils.isEmpty(this.fileUrlText.getText())) {
			setErrorMessage("请填写URL");
			setPageComplete(false);
			return false;
		}

		if (!this.fileUrlText.getText().trim().startsWith("http")) {
			setErrorMessage("Alpha版本，暂只支持Http协议");
			setPageComplete(false);
			return false;
		}

		if (StringUtils.isEmpty(fileNameText.getText())) {
			setErrorMessage("请填写文件名");
			setPageComplete(false);
			return false;
		}

		if (StringUtils.isEmpty(categoryCombo.getText())) {
			setErrorMessage("请选择任务分类");
			setPageComplete(false);
			return false;
		}

		if (StringUtils.isEmpty(savePathCombo.getText())) {
			setErrorMessage("请选择或填写文件保存路径");
			setPageComplete(false);
			return false;
		}

//		if (StringUtils.isEmpty(blocksCombo.getText())) {
//			setErrorMessage("请选择下载线程数量");
//			setPageComplete(false);
//			return false;
//		}
//
//		if (Integer.parseInt(blocksCombo.getText()) < task.getBlocks()) {
//			setErrorMessage("不允许减少下载线程数量");
//			setPageComplete(false);
//			return false;
//
//		}

		this.setPageComplete(true);
		return true;
	}

	/**
	 * 给所有输入元素添加校验监听器
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
	 * 将数据写入到Task对象中
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
	 * 设置输入元素的值
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
		categoryLabel.setText("分类");

		categoryCombo = new Combo(group_1, SWT.READ_ONLY);
		categoryCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));

		final Button addCategoryButton = new Button(group_1, SWT.NONE);
		addCategoryButton.setText("添加(&A)");

		final Label savePathLabel = new Label(group_1, SWT.NONE);
		savePathLabel.setText("保存目录");

		savePathCombo = new Combo(group_1, SWT.NONE);
		savePathCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));

		final Button selectSavePathButton = new Button(group_1, SWT.NONE);

		selectSavePathButton.setText("选择(&S)");

		final Label fileNameLabel = new Label(group_1, SWT.NONE);
		fileNameLabel.setText("文件名");

		fileNameText = new Text(group_1, SWT.BORDER);
		fileNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));

		final Group group_2 = new Group(container, SWT.NONE);
		group_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 2;
		group_2.setLayout(gridLayout_2);

		final Label memoLabel = new Label(group_2, SWT.NONE);
		memoLabel.setText("备注");

		memoText = new Text(group_2, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);
		final GridData gridData_1 = new GridData(SWT.FILL, SWT.FILL, true,
				false);
		gridData_1.heightHint = 50;
		memoText.setLayoutData(gridData_1);

		new Label(group_1, SWT.NONE);

		final Label blocksLabel = new Label(group_1, SWT.NONE);
		blocksLabel.setText("线程数量");

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
		// 选择保存目录
		selectSavePathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(shell);
				dialog.setFilterPath(savePathCombo.getText());
				savePathCombo.setText(dialog.open());
			}
		});

		// 监听url修改事件
		fileUrlText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				fileNameText.setText(getFileName(fileUrlText.getText()));

			}

		});

		// 从剪贴板粘贴url
		TextTransfer textTransfer = TextTransfer.getInstance();
		String textData = (String) clipboard.getContents(textTransfer);

		if (!StringUtils.isEmpty(textData) && textData.startsWith("http://")) {
			textData = textData.trim();
			fileUrlText.setText(textData);
		}

		// 设置分类选择框
		categoryCombo.setItems(CategoryModel.getInstance()
				.getAllowSaveCategoryNames());

		// 监听分类选择
		categoryCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				savePathCombo.setText(CategoryModel.getInstance().getCategory(
						categoryCombo.getText()).getPath());
			}

		});

		// 选中第一条记录
		categoryCombo.select(1);

		addValidateListener();
	}

}
