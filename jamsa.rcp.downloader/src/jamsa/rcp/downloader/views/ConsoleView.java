package jamsa.rcp.downloader.views;

import jamsa.rcp.downloader.models.Task;
import jamsa.rcp.downloader.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

/**
 * 线程终端视图
 * 
 * @author 朱杰
 * 
 */
public class ConsoleView extends ViewPart {
	class ListLabelProvider extends LabelProvider {
		public String getText(Object element) {
			return String.valueOf(element);
		}

		public Image getImage(Object element) {
			return null;
		}
	}

	class ListContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof List) {
				List msgs = (List) inputElement;
				return msgs.toArray();
			}
			return null;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private static final Logger logger = new Logger(ConsoleView.class);

	public static final String ID = ConsoleView.class.getName();

	private CTabFolder tabFolder;

	private Map tabItems = new HashMap(5);

	private Map listViewers = new HashMap(5);

	private Map messages = new HashMap(5);

	private Task task;

	@Override
	public void createPartControl(Composite parent) {
		tabFolder = new CTabFolder(parent, SWT.NONE);
		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(selectionListener);

	}

	/**
	 * 在选择了一个新的任务后，将该任务的终端日志队列中的数据显示出来
	 * 
	 * @param messages
	 */
	private void writeMessages(Map messages) {
		for (Iterator it = messages.keySet().iterator(); it.hasNext();) {
			String key = String.valueOf(it.next());
			List msgs = (List) messages.get(key);
			for (Iterator iter = msgs.iterator(); iter.hasNext();) {
				String msg = (String) iter.next();
				addMessage("线程：" + key, msg);
			}
		}
	}

	/**
	 * 创建线程终端
	 * 
	 * @param name
	 *            终端名称
	 */
	private void createTabItem(String name) {
		CTabItem item = new CTabItem(tabFolder, SWT.NONE);
		item.setText(name);
		tabItems.put(name, item);
		final ListViewer listViewer = new ListViewer(tabFolder, SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.BORDER);
		listViewers.put(name, listViewer);
		listViewer.setLabelProvider(new ListLabelProvider());
		listViewer.setContentProvider(new ListContentProvider());
		if (messages.get(name) == null)
			messages.put(name, new ArrayList());
		List msgs = (List) messages.get(name);
		// msgs.add(name);
		listViewer.setInput(msgs);
		item.setControl(listViewer.getList());
		tabFolder.setSelection(item);

	}

	private void addMessage(String name, String msg) {
		if (listViewers.get(name) != null) {
			ListViewer listViewer = (ListViewer) listViewers.get(name);
			List msgs = (List) messages.get(name);
			msgs.add(msg);
			// msgs.add(0, msg);
			listViewer.refresh();
			listViewer.scrollDown(0, 1);
		} else {
			createTabItem(name);
			addMessage(name, msg);
		}
	}

	/**
	 * 监听任务消息
	 */
	private Observer msgObserver = new Observer() {
		public void update(Observable o, final Object arg) {
			tabFolder.getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (arg instanceof String[]) {
						String[] msg = (String[]) arg;
						if (msg.length == 2) {
							String threadName = msg[0];
							String message = msg[1];
							addMessage(threadName, message);
						}
					}
				}
			});

		}

	};

	private ISelectionListener selectionListener = new ISelectionListener() {

		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection incoming = (IStructuredSelection) selection;
				if (incoming.size() == 1
						&& incoming.getFirstElement() instanceof Task) {
					Task newTask = (Task) incoming.getFirstElement();
					if (newTask != task) {
						if (task != null) {
							task.deleteObserver(msgObserver);
						}
						newTask.addObserver(msgObserver);
						task = newTask;
						disposeTabItems();

						writeMessages(task.getMessages());
					}
					logger.info("当前选中任务：" + task.getFileName());
				}
			}
		}

	};

	private void disposeTabItems() {
		for (Iterator it = tabItems.keySet().iterator(); it.hasNext();) {
			String key = String.valueOf(it.next());
			CTabItem item = (CTabItem) tabItems.get(key);
			item.dispose();
		}
		tabItems.clear();
		listViewers.clear();
		messages.clear();
	}

	public void dispose() {
//		super.dispose();
		disposeTabItems();
		if (this.task != null)
			task.deleteObserver(msgObserver);
		getSite().getWorkbenchWindow().getSelectionService()
				.removeSelectionListener(selectionListener);
	}

	@Override
	public void setFocus() {
		tabFolder.setFocus();

	}

}
