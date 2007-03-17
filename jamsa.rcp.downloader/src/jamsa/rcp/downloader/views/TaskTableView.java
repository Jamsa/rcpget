package jamsa.rcp.downloader.views;

import jamsa.rcp.downloader.RCPGetActionFactory;
import jamsa.rcp.downloader.models.Category;
import jamsa.rcp.downloader.models.CategoryModel;
import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.models.TaskModel;
import jamsa.rcp.downloader.models.TaskThreadsManager;
import jamsa.rcp.downloader.utils.Logger;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.ViewPart;

/**
 * 下载任务列表视图
 * 
 * @author 朱杰
 * 
 */
public class TaskTableView extends ViewPart {
	Logger logger = new Logger(this.getClass());

	public static final String ID = "jamsa.rcp.downloader.views.TaskTableView";

	private TableViewer tableViewer;

	private Display display;

	private TaskModel model = TaskModel.getInstance();

	private Category category;

	private ISelectionListener selectionListener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection incoming = (IStructuredSelection) selection;

				if (incoming.size() == 1
						&& incoming.getFirstElement() instanceof Category) {
					category = (Category) incoming.getFirstElement();
					if (category != null)
						tableViewer.setInput(model.getTasks(category));
					else
						tableViewer.setInput(model.getTasks(CategoryModel
								.getInstance().getRunning()));
					logger.info("当前选中分类：" + category.getName());
				}
			}
		}
	};

	@Override
	public void createPartControl(Composite parent) {
		display = parent.getDisplay();
		tableViewer = new TableViewer(parent, SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		TableColumn column = new TableColumn(table, SWT.NONE);
		column.setText("状态");
		column.setWidth(100);

		column = new TableColumn(table, SWT.NONE);
		column.setText("文件名");
		column.setWidth(100);

		column = new TableColumn(table, SWT.NONE);
		column.setText("文件大小");
		column.setWidth(100);

		column = new TableColumn(table, SWT.NONE);
		column.setText("进度");
		column.setWidth(100);

		column = new TableColumn(table, SWT.NONE);
		column.setText("已完成");
		column.setWidth(100);

		column = new TableColumn(table, SWT.NONE);
		column.setText("即时速度");
		column.setWidth(100);

		column = new TableColumn(table, SWT.NONE);
		column.setText("平均速度");
		column.setWidth(100);

		column = new TableColumn(table, SWT.NONE);
		column.setText("用时");
		column.setWidth(100);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setLabelProvider(new ViewLabelProvider());
		tableViewer.setContentProvider(new ViewContentProvider());
		tableViewer.setInput(model.getTasks(CategoryModel.getInstance()
				.getRunning()));

		// 任务双击事件
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				if (event.getSelection() != null
						&& event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection incoming = (IStructuredSelection) event
							.getSelection();
					if (!incoming.isEmpty()
							&& incoming.getFirstElement() instanceof Task) {
						Task task = (Task) incoming.getFirstElement();
						if (task.getStatus() == Task.STATUS_ERROR
								|| task.getStatus() == Task.STATUS_STOP) {
							TaskThreadsManager.getInstance().start(task);
							return;
						}
						if (task.getStatus() == Task.STATUS_RUNNING) {
							TaskThreadsManager.getInstance().stop(task);
							return;
						}

					}
				}
			}

		});

		// 提供选择服务
		getSite().setSelectionProvider(tableViewer);

		// 任务列表监听分类选择改变事件
		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(selectionListener);

		model.addObserver(new Observer() {
			public void update(Observable o, Object arg) {
				display.asyncExec(new Runnable() {
					public void run() {
						tableViewer.setInput(model.getTasks(category));
					}
				});
			}
		});

		createContextMenu(parent);
	}

	private void createContextMenu(Composite parent) {
		MenuManager mgr = new MenuManager();
		mgr.setRemoveAllWhenShown(true);
		mgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		Menu menu = mgr.createContextMenu(tableViewer.getControl());
		tableViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(mgr, tableViewer);
	}

	private void fillContextMenu(IMenuManager manager) {
		IWorkbenchWindow window = getSite().getWorkbenchWindow();
		manager.add(RCPGetActionFactory.NEW_TASK.create(window));
		manager.add(RCPGetActionFactory.RUN_TASK.create(window));
		manager.add(RCPGetActionFactory.STOP_TASK.create(window));
		manager.add(RCPGetActionFactory.RESTART_TASK.create(window));
		manager.add(new Separator());
		manager.add(RCPGetActionFactory.DELETE_TASK.create(window));
		manager.add(RCPGetActionFactory.RESTORE_TASK.create(window));
	}

	public void dispose() {
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
			if (element instanceof Task) {
				Task task = (Task) element;
				switch (columnIndex) {
				case 0:
					switch (task.getStatus()) {
					case Task.STATUS_ERROR:
						return "错误";
					case Task.STATUS_FINISHED:
						return "已完成";
					case Task.STATUS_RUNNING:
						return "运行中";
					case Task.STATUS_STOP:
						return "停止";
					default:
						return "";
					}
				case 1:
					return task.getFileName();
				case 2:
					if (task.getFileSize() == 0)
						return "未知大小";
					else
						return task.getFileSize() / 1000 + "k";
				case 3:
					if (task.getFileSize() == 0 || task.getFinishedSize() == 0)
						return "";
					else
						return (task.getFinishedSize() * 100)
								/ task.getFileSize() + "%";
				case 4:
					if (task.getFinishedSize() == 0)
						return "";
					else
						return task.getFinishedSize() / 1000 + "k";
				case 5:
					if (task.getSpeed() == 0)
						return "";
					else
						return task.getSpeed() + "k/s";
				case 6:
					if (task.getAverageSpeed() == 0)
						return "";
					else
						return task.getAverageSpeed() + "k/s";
				case 7:
					if (task.getTotalTime() >= 0)
						return task.getTotalTime() / 1000 + "s";
					else
						return "";
				default:
					return "";
					// break;
				}
			}
			return null;
		}

	}

	class ViewContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Task[]) {
				return (Task[]) inputElement;
			}
			return null;
		}

		public void dispose() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			viewer.refresh();
		}

	}

}
