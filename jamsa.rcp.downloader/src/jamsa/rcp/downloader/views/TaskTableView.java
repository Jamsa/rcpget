package jamsa.rcp.downloader.views;

import jamsa.rcp.downloader.Activator;
import jamsa.rcp.downloader.Messages;
import jamsa.rcp.downloader.RCPGetActionFactory;
import jamsa.rcp.downloader.models.Category;
import jamsa.rcp.downloader.models.CategoryModel;
import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.models.TaskModel;
import jamsa.rcp.downloader.models.TaskThreadManager;
import jamsa.rcp.downloader.monitor.ClipBoardMonitor;
import jamsa.rcp.downloader.monitor.IClipboardChangeListener;
import jamsa.rcp.downloader.preference.PreferenceManager;
import jamsa.rcp.downloader.utils.Logger;
import jamsa.rcp.downloader.utils.StringUtils;
import jamsa.rcp.downloader.wizards.TaskWizard;

import java.util.Observable;
import java.util.Observer;

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
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
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

	public static final String ID = TaskTableView.class.getName();// "jamsa.rcp.downloader.views.TaskTableView";

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
					logger.info("当前选中分类：" + category.getName()); //$NON-NLS-1$
				}
			}
		}
	};

	/**
	 * 拖放支持
	 * 
	 */
	private void createDNDSupport() {
		int ops = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transfers = new Transfer[] { TextTransfer.getInstance() };
		tableViewer.addDropSupport(ops, transfers, new ViewerDropAdapter(
				tableViewer) {
			@Override
			public boolean performDrop(Object data) {
				String url = String.valueOf(data);
				url = StringUtils.getURLString(url);
				if (url.startsWith("http")) { //$NON-NLS-1$
					// Clipboard clipboard = new
					// Clipboard(Display.getCurrent());
					// TextTransfer textTransfer = TextTransfer.getInstance();
					// clipboard.setContents(new Object[] { url },
					// new Transfer[] { textTransfer });
					// 与监视剪贴板不同，播放的链接不需要严格的文件类型检查
					openWizard(url);
				}

				return true;
			}

			@Override
			public boolean validateDrop(Object target, int operation,
					TransferData transferType) {
				return true;
			}
		});
	}

	private void openWizard(String text) {
		if (!StringUtils.isEmpty(text)) {
			try {
				Task task = new Task();
				task.setFileUrl(text);
				TaskWizard wizard = new TaskWizard(task, false);
				WizardDialog dialog = new WizardDialog(tableViewer.getControl()
						.getShell(), wizard);
				dialog.open();
			} catch (Exception e) {
				return;
			}
			IWorkbenchWindow window = Activator.getDefault().getWorkbench()
					.getActiveWorkbenchWindow();
			if (window.getShell().getMinimized()) {
				window.getShell().setMinimized(false);
			}
			window.getShell().setActive();
			window.getShell().moveAbove(null);
		}
	}

	/**
	 * 监视剪贴板数据变化
	 * 
	 */
	private void listenerClipboard() {
		ClipBoardMonitor.getInstance().addClipboardChangeListener(
				new IClipboardChangeListener() {
					public void clipboardChange(String text) {
						String types[] = PreferenceManager.getInstance()
								.getMonitorFileType().split(";"); //$NON-NLS-1$
						// 此处不需要检查文件类型
						text = StringUtils.getURLString(text, types);
						// text = StringUtils.getURLString(text);
						if (!TaskModel.getInstance().isExist(text))
							openWizard(text);
					}

				});
		ClipBoardMonitor.getInstance().start();
	}

	@Override
	public void createPartControl(Composite parent) {
		display = parent.getDisplay();
		tableViewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.MULTI);

		createDNDSupport();
		listenerClipboard();

		Table table = tableViewer.getTable();
		TableColumn column = new TableColumn(table, SWT.NONE);
		column.setText(Messages.TaskTableView_Status);
		column.setWidth(100);

		column = new TableColumn(table, SWT.NONE);
		column.setText(Messages.TaskTableView_FileName);
		column.setWidth(100);

		column = new TableColumn(table, SWT.NONE);
		column.setText(Messages.TaskTableView_FileSize);
		column.setWidth(100);

		column = new TableColumn(table, SWT.NONE);
		column.setText(Messages.TaskTableView_FinishedPercent);
		column.setWidth(100);

		column = new TableColumn(table, SWT.NONE);
		column.setText(Messages.TaskTableView_FinishedSize);
		column.setWidth(100);

		column = new TableColumn(table, SWT.NONE);
		column.setText(Messages.TaskTableView_Speed);
		column.setWidth(100);

		column = new TableColumn(table, SWT.NONE);
		column.setText(Messages.TaskTableView_AverageSpeed);
		column.setWidth(100);

		column = new TableColumn(table, SWT.NONE);
		column.setText(Messages.TaskTableView_Time);
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
							TaskThreadManager.getInstance().start(task);
							return;
						}
						if (task.getStatus() == Task.STATUS_RUNNING) {
							TaskThreadManager.getInstance().stop(task);
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
		// mgr.setRemoveAllWhenShown(true);
		// mgr.addMenuListener(new IMenuListener() {
		// public void menuAboutToShow(IMenuManager manager) {
		// fillContextMenu(manager);
		// }
		// });
		fillContextMenu(mgr);
		Menu menu = mgr.createContextMenu(tableViewer.getControl());
		tableViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(mgr, tableViewer);
	}

	private boolean contextMenu = false;

	private void fillContextMenu(IMenuManager manager) {
		if (!contextMenu) {
			IWorkbenchWindow window = getSite().getWorkbenchWindow();
			manager.add(RCPGetActionFactory.NEW_TASK.create(window));
			manager.add(RCPGetActionFactory.RUN_TASK.create(window));
			manager.add(RCPGetActionFactory.STOP_TASK.create(window));
			manager.add(RCPGetActionFactory.RESTART_TASK.create(window));
			manager.add(new Separator());
			manager.add(RCPGetActionFactory.DELETE_TASK.create(window));
			manager.add(RCPGetActionFactory.RESTORE_TASK.create(window));
			manager.add(new Separator());
			manager.add(RCPGetActionFactory.COPY_URL.create(window));
			manager.add(RCPGetActionFactory.MODIFY_TASK.create(window));
			contextMenu = true;
		}
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
						return Messages.TaskTableView_Error;
					case Task.STATUS_FINISHED:
						return Messages.TaskTableView_Finish;
					case Task.STATUS_RUNNING:
						return Messages.TaskTableView_Running;
					case Task.STATUS_STOP:
						return Messages.TaskTableView_Stop;
					default:
						return ""; //$NON-NLS-1$
					}
				case 1:
					return task.getFileName();
				case 2:
					if (task.getFileSize() == 0)
						return Messages.TaskTableView_UnknownSize;
					else
						return task.getFileSize() / 1000 + "k"; //$NON-NLS-1$
				case 3:
					if (task.getFileSize() == 0 || task.getFinishedSize() == 0)
						return ""; //$NON-NLS-1$
					else
						return (task.getFinishedSize() * 100)
								/ task.getFileSize() + "%"; //$NON-NLS-1$
				case 4:
					if (task.getFinishedSize() == 0)
						return ""; //$NON-NLS-1$
					else
						return task.getFinishedSize() / 1000 + "k"; //$NON-NLS-1$
				case 5:
					if (task.getSpeed() == 0)
						return ""; //$NON-NLS-1$
					else
						return task.getSpeed() + "k/s"; //$NON-NLS-1$
				case 6:
					if (task.getAverageSpeed() == 0)
						return ""; //$NON-NLS-1$
					else
						return task.getAverageSpeed() + "k/s"; //$NON-NLS-1$
				case 7:
					if (task.getTotalTime() >= 0)
						return task.getTotalTime() / 1000 + "s"; //$NON-NLS-1$
					else
						return ""; //$NON-NLS-1$
				default:
					return ""; //$NON-NLS-1$
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
