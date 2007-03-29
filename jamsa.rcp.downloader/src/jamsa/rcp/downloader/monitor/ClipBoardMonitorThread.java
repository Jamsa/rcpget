package jamsa.rcp.downloader.monitor;

import jamsa.rcp.downloader.utils.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;

/**
 * ¼ôÌù°å¼àÊÓÏß³Ì
 * 
 * @author Öì½Ü
 * @deprecated
 * @see ClipBoardMonitor
 */
public class ClipBoardMonitorThread extends Thread {
	private Display display;

	private Clipboard clipboard;

	private TextTransfer textTransfer;

	private boolean run = true;

	private String last = "";

	private List listeners = new ArrayList();

	private void notifyListener(String text) {
		for (Iterator it = listeners.iterator(); it.hasNext();) {
			IClipboardChangeListener listener = (IClipboardChangeListener) it.next();
			listener.clipboardChange(text);
		}
	}

	public ClipBoardMonitorThread(Display display) {
		this.display = display;
		this.clipboard = new Clipboard(display);
		this.textTransfer = TextTransfer.getInstance();
	}

	public void checkClipBoard() {
		display.asyncExec(new Runnable() {
			public void run() {
				String textData = (String) clipboard.getContents(textTransfer);
				if (!StringUtils.isEmpty(textData) && !textData.equals(last)) {
					last = textData;
					notifyListener(last);
					System.out.println("¼ôÌù°å¸Ä±ä" + last);
				}
			}
		});
	}

	public void run() {
		while (run && !this.isInterrupted()) {
			checkClipBoard();
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				run = false;
			}
		}
	}

	public boolean isRun() {
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}
}
