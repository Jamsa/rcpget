package jamsa.rcp.downloader.views;

import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.utils.Logger;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

/**
 * 任务详细信息视图
 * @author 朱杰
 *
 */
public class TaskInfoView extends ViewPart {
	private static final Logger logger = new Logger(TaskInfoView.class);
	public static final String ID = "jamsa.rcp.downloader.views.TaskInfoView";

	private TableViewer tableViewer;

	private Task task;

	private ISelectionListener selectionListener = new ISelectionListener() {

		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection incoming = (IStructuredSelection) selection;
				if (incoming.size() == 1 && incoming.getFirstElement() instanceof Task) {
					task = (Task) incoming.getFirstElement();
					tableViewer.setInput(task);
					logger.info("当前选中任务：" + task.getFileName());
				}
			}
		}

	};

	@Override
	public void createPartControl(Composite parent) {
		tableViewer = new TableViewer(parent, SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		TableColumn column = new TableColumn(table, SWT.NONE);
		column.setText("属性");
		column.setWidth(100);

		column = new TableColumn(table, SWT.NONE);
		column.setText("值");
		column.setWidth(300);
		// table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tableViewer.setLabelProvider(new ViewLabelProvider());
		tableViewer.setContentProvider(new ViewContentProvider());
		// tableViewer.setInput(task);
		// 监听选择变化
		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(selectionListener);
	}

	public void dispose() {
		// 关闭时移除对TaskTableView的监听
		getSite().getWorkbenchWindow().getSelectionService()
				.removeSelectionListener(selectionListener);
		super.dispose();
	}

	@Override
	public void setFocus() {
		tableViewer.getTable().setFocus();

	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Object[]) {
				Object[] props = (Object[]) element;
				if (props.length == 2) {
					switch (columnIndex) {
					case 0:
						return String.valueOf(props[0]);
					case 1:
						return String.valueOf(props[1]);
					case 2:
						return String.valueOf(props[2]);
					case 3:
						return String.valueOf(props[3]);
					case 4:
						return String.valueOf(props[4]);
					case 5:
						return String.valueOf(props[5]);
					default:
						return "";
						// break;
					}
				}
			}
			return null;
		}

	}

	class ViewContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Task) {
				Task task = (Task) inputElement;
				Object[] result = new Object[6];
				result[0] = new String[] { "文件名", task.getFileName() };
				result[1] = new String[] { "大小", task.getFileSize() + "" };
				result[2] = new String[] { "URL", task.getFileUrl() };
				result[3] = new String[] { "保存位置", task.getFilePath() };
				result[4] = new String[] { "文件类型", task.getFileType() };
				result[5] = new String[] { "备注", task.getMemo() };
				
				return result;
			}
			return null;
		}

		public void dispose() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			tableViewer.refresh();
		}

	}

}
