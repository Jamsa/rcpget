package jamsa.rcp.downloader;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction quitAction;

	private IWorkbenchAction aboutAction;

	private IContributionItem viewList;

	private IWorkbenchAction prefAction;

	private IWorkbenchAction newTaskAction;

	private IWorkbenchAction runTaskAction;

	private IWorkbenchAction restartTaskAction;

	private IWorkbenchAction stopTaskAction;

	private IWorkbenchAction modifyTaskAction;

	private IWorkbenchAction deleteTaskAction;

	private IWorkbenchAction restoreTaskAction;

	// private RunTaskAction runTaskAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(IWorkbenchWindow window) {
		quitAction = RCPGetActionFactory.QUIT.create(window);
		register(quitAction);

		aboutAction = RCPGetActionFactory.ABOUT.create(window);
		register(aboutAction);

		prefAction = RCPGetActionFactory.PREFERENCES.create(window);
		register(prefAction);

		newTaskAction = RCPGetActionFactory.NEW_TASK.create(window);
		register(newTaskAction);

		modifyTaskAction = RCPGetActionFactory.MODIFY_TASK.create(window);
		register(modifyTaskAction);

		runTaskAction = RCPGetActionFactory.RUN_TASK.create(window);
		register(runTaskAction);

		restartTaskAction = RCPGetActionFactory.RESTART_TASK.create(window);
		register(restartTaskAction);

		stopTaskAction = RCPGetActionFactory.STOP_TASK.create(window);
		register(stopTaskAction);

		deleteTaskAction = RCPGetActionFactory.DELETE_TASK.create(window);
		register(deleteTaskAction);

		restoreTaskAction = RCPGetActionFactory.RESTORE_TASK.create(window);
		register(restoreTaskAction);

		viewList = ContributionItemFactory.VIEWS_SHORTLIST.create(window);

	}

	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager("ÎÄ¼þ(&F)",
				IWorkbenchActionConstants.M_FILE);
		menuBar.add(fileMenu);
		fileMenu.add(newTaskAction);
		fileMenu.add(modifyTaskAction);
		fileMenu.add(new Separator());
		fileMenu.add(runTaskAction);
		fileMenu.add(restartTaskAction);
		fileMenu.add(stopTaskAction);
		fileMenu.add(deleteTaskAction);
		fileMenu.add(restoreTaskAction);
		fileMenu.add(new Separator());
		fileMenu.add(quitAction);

		// MenuManager taskMenu = new MenuManager("&Task", IConstants.M_TASK);
		// taskMenu.add(new Separator());
		// menuBar.add(taskMenu);

		MenuManager windowMenu = new MenuManager("´°¿Ú(&W)",
				IWorkbenchActionConstants.M_WINDOW);
		menuBar.add(windowMenu);
		windowMenu.add(prefAction);
		windowMenu.add(viewList);

		MenuManager helpMenu = new MenuManager("°ïÖú(&H)",
				IWorkbenchActionConstants.M_HELP);
		menuBar.add(helpMenu);
		helpMenu.add(aboutAction);

	}

	protected void fillCoolBar(ICoolBarManager coolBar) {
		IToolBarManager toolbar = new ToolBarManager(coolBar.getStyle());
		coolBar.add(toolbar);
		toolbar.add(newTaskAction);
		toolbar.add(runTaskAction);
		toolbar.add(stopTaskAction);
		toolbar.add(restartTaskAction);
		toolbar.add(new Separator());
		toolbar.add(deleteTaskAction);
		toolbar.add(restoreTaskAction);
		toolbar.add(new Separator());
		toolbar.add(aboutAction);
		toolbar.add(quitAction);
	}

	protected void fillTrayItem(IMenuManager trayItem) {
		trayItem.add(newTaskAction);
		trayItem.add(prefAction);
		trayItem.add(aboutAction);
		trayItem.add(quitAction);
	}

}
