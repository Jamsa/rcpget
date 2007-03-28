package jamsa.rcp.downloader;

import jamsa.rcp.downloader.dialogs.DockerDialog;
import jamsa.rcp.downloader.preference.PreferenceManager;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {
	private TrayItem trayItem;

	private ApplicationActionBarAdvisor actionBarAdvisor;

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		this.actionBarAdvisor = new ApplicationActionBarAdvisor(configurer);
		return this.actionBarAdvisor;
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(640, 480));
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		configurer.setShowMenuBar(true);
		configurer.setTitle("RCP Get");
		
	}

	private void hookMinimize(final IWorkbenchWindow window) {
		window.getShell().addShellListener(new ShellAdapter() {
			public void shellIconified(ShellEvent e) {
				if (PreferenceManager.getInstance().getMinimizeToTray())
					window.getShell().setVisible(false);
			}
		});
		trayItem.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event event) {
				if (PreferenceManager.getInstance().getMinimizeToTray()) {
					Shell shell = window.getShell();
					if (!shell.isVisible()) {
						shell.setVisible(true);
						window.getShell().setMinimized(false);
					}
				}
			}
		});
	}

	private void hookPopupMenu(final IWorkbenchWindow window) {
		trayItem.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				MenuManager trayMenu = new MenuManager();
				Menu menu = trayMenu.createContextMenu(window.getShell());
				actionBarAdvisor.fillTrayItem(trayMenu);
				menu.setVisible(true);
			}
		});
	}

	public void postWindowOpen() {

		final IWorkbenchWindow window = getWindowConfigurer().getWindow();
//		window.getShell().setMaximized(true);
		trayItem = initTaskItem(window);
		if (trayItem != null) {
			hookPopupMenu(window);
			hookMinimize(window);
		}
		
		
//		Display display = Display.getDefault();
//		DockerDialog shell = new DockerDialog(display,SWT.NORMAL);// SWT.NO_TRIM SWT.NONE
//		shell.setSize(50, 50);
//		
//		shell.open();
//		shell.layout();
//		while (!shell.isDisposed()) {
//			if (!display.readAndDispatch())
//				display.sleep();
//		}
		
		// Gather gather = new Gather(window.getShell().getDisplay(),SWT.NONE);
		// gather.open();
	}

	private TrayItem initTaskItem(IWorkbenchWindow window) {
		final Tray tray = window.getShell().getDisplay().getSystemTray();
		if (tray == null)
			return null;
		TrayItem trayItem = new TrayItem(tray, SWT.NONE);
		Image trayImage = AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, IImageKeys.RCP_GET).createImage();
		trayItem.setImage(trayImage);
		trayItem.setToolTipText("RCP Get");
		return trayItem;
	}
}
