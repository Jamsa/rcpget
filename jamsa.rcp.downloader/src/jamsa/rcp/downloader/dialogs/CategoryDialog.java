package jamsa.rcp.downloader.dialogs;

import jamsa.rcp.downloader.models.Category;
import jamsa.rcp.downloader.models.CategoryModel;
import jamsa.rcp.downloader.utils.FileUtils;
import jamsa.rcp.downloader.utils.StringUtils;

import java.io.File;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * �������öԻ���
 * 
 * @author ���
 * 
 */
public class CategoryDialog extends TitleAreaDialog {

	private Text directoryText;

	private Text categoryText;

	private Shell parentShell;

	private Category parentCategory;

	private Category category;

	public CategoryDialog(Shell parentShell, Category parentCategory,
			Category category) {
		super(parentShell);
		this.parentShell = parentShell;
		this.parentCategory = parentCategory;
		this.category = category;
	}

	protected Control createDialogArea(Composite parent) {
		// super.createDialogArea(parent);
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		// container.setSize(320, 240);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		container.setLayout(gridLayout);

		final Label categoryLabel = new Label(container, SWT.NONE);
		categoryLabel.setText("��������");

		categoryText = new Text(container, SWT.BORDER);

		final GridData gridData_1 = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		// gridData_1.widthHint = 365;
		categoryText.setLayoutData(gridData_1);
		new Label(container, SWT.NONE);

		final Label directoryLabel = new Label(container, SWT.NONE);
		directoryLabel.setText("����Ŀ¼");

		directoryText = new Text(container, SWT.BORDER);
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		// gridData.widthHint = 118;
		directoryText.setLayoutData(gridData);

		final Button selectButton = new Button(container, SWT.NONE);

		selectButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(parentShell);
				dialog.setFilterPath(directoryText.getText());
				directoryText.setText(dialog.open());
			}
		});
		selectButton.setLayoutData(new GridData());
		selectButton.setText("ѡ��(&S)");

		setTitle("��������");

		if (category.getName() != null)
			categoryText.setText(category.getName());
		if (category.getPath() != null)
			directoryText.setText(category.getPath());

		categoryText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String path = categoryText.getText();
				if (path != null) {
					path = path.trim();
					if (!parentCategory.getPath().endsWith(File.separator))
						path = File.separator + path;
					directoryText.setText(parentCategory.getPath()
							+ File.separator + path);
				}
			}
		});

		return parent;
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("��������");
	}

	private boolean validate() {
		String directory = directoryText.getText().trim();
		String categoryName = categoryText.getText().trim();

		if (StringUtils.isEmpty(categoryName)) {
			setErrorMessage("����������Ϊ��");
			return false;
		}
		if (StringUtils.isEmpty(directory)) {
			setErrorMessage("����·������Ϊ��");
			return false;
		}

		if (CategoryModel.getInstance().contains(categoryName)
				&& CategoryModel.getInstance().getCategory(categoryName) != category) {
			setErrorMessage("�÷����Ѵ���");
			return false;
		}

		// ���������Ŀ¼
		if (!FileUtils.existsDirectory(directory)) {
			// ����Ŀ¼
			FileUtils.createDirectory(directory);

			if (!FileUtils.existsDirectory(directory)) {
				setErrorMessage("���ܴ�������Ŀ¼");
				return false;
			}
		}

		return true;
	}

	protected void okPressed() {
		if (validate()) {
			// Category category = new Category();
			category.setPath(directoryText.getText().trim());
			category.setName(categoryText.getText().trim());
			CategoryModel.getInstance().addCategory(category, parentCategory);

			setReturnCode(OK);
			close();
		}
	}

	// public Text getCategoryText() {
	// return categoryText;
	// }
	//
	// public Text getDirectoryText() {
	// return directoryText;
	// }
}
