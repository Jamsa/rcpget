package jamsa.rcp.downloader.monitor;

import jamsa.rcp.downloader.utils.Logger;
import jamsa.rcp.downloader.utils.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

/**
 * 剪贴板监视器
 * 
 * @author 朱杰
 * 
 */
public class ClipBoardMonitor {
	private static Logger log = new Logger(ClipBoardMonitor.class);

	private static final int INTERVAL = 500;

	private Display display;

	private Clipboard clipboard;

	TextTransfer textTransfer;

	private boolean run = false;

	private String last;

	private static ClipBoardMonitor instance = new ClipBoardMonitor();

	private List listeners = new ArrayList();

	private void notifyListeners(String text) {
		for (Iterator it = listeners.iterator(); it.hasNext();) {
			IClipboardChangeListener listener = (IClipboardChangeListener) it
					.next();
			listener.clipboardChange(text);
		}
	}

	private ClipBoardMonitor() {
		this.display = Display.getDefault();
		this.clipboard = new Clipboard(display);
		this.textTransfer = TextTransfer.getInstance();
	}

	UIJob job = new UIJob("ClipBoardMonitorUIJob") {
		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			String textData = (String) clipboard.getContents(textTransfer);
			if (!StringUtils.isEmpty(textData) && !textData.equals(last)) {
				last = textData;
				notifyListeners(last);
			}
			if (run)
				this.schedule(INTERVAL);
			return Status.OK_STATUS;
		}
	};

	public void start() {
		if (!run) {
			run = true;
			// 如果job已经存在就不需要再次启动。防止用户反复启用或者禁用监视引起的启动多个监视任务
			if (job.getState() == Job.NONE) {
				job.schedule(INTERVAL);
			}
		}
	}

	public void stop() {
		if (run) {
			run = false;
		}
	}

	public static ClipBoardMonitor getInstance() {
		return instance;
	}

	public void addClipboardChangeListener(IClipboardChangeListener listener) {
		this.listeners.add(listener);
	}

	public void removeClipboardChangeListener(IClipboardChangeListener listener) {
		this.listeners.remove(listener);
	}

}
