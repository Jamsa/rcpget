package jamsa.rcp.downloader;

import jamsa.rcp.downloader.actions.DeleteTaskAction;
import jamsa.rcp.downloader.actions.ModifyTaskAction;
import jamsa.rcp.downloader.actions.NewTaskAction;
import jamsa.rcp.downloader.actions.RestartTaskAction;
import jamsa.rcp.downloader.actions.RestoreTaskAction;
import jamsa.rcp.downloader.actions.RunTaskAction;
import jamsa.rcp.downloader.actions.StopTaskAction;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction quitAction;

	private IWorkbenchAction aboutAction;

	private IContributionItem viewList;

	private IWorkbenchAction prefAction;

	private NewTaskAction newTaskAction;

	private RunTaskAction runTaskAction;

	private RestartTaskAction restartTaskAction;

	private StopTaskAction stopTaskAction;

	private ModifyTaskAction modifyTaskAction;
	
	private DeleteTaskAction deleteTaskAction;
	private RestoreTaskAction restoreTaskAction;

	// private RunTaskAction runTaskAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(IWorkbenchWindow window) {
		quitAction = ActionFactory.QUIT.create(window);
		register(quitAction);
		aboutAction = ActionFactory.ABOUT.create(window);
		register(aboutAction);
		prefAction = ActionFactory.PREFERENCES.create(window);
		register(prefAction);

		newTaskAction = new NewTaskAction(window, "&New");
		register(newTaskAction);
		
		modifyTaskAction = new ModifyTaskAction(window, "&Modify");
		register(modifyTaskAction);

		runTaskAction = new RunTaskAction(window, "&Run");
		register(runTaskAction);

		restartTaskAction = new RestartTaskAction(window, "&Restart");
		register(restartTaskAction);

		stopTaskAction = new StopTaskAction(window, "&Stop");
		register(stopTaskAction);
		
		deleteTaskAction = new DeleteTaskAction(window,"&Delete");
		register(deleteTaskAction);
		
		restoreTaskAction = new RestoreTaskAction(window,"&Restore");
		register(restoreTaskAction);

		viewList = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
	}

	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager("&File",
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

		MenuManager windowMenu = new MenuManager("&Window",
				IWorkbenchActionConstants.M_WINDOW);
		menuBar.add(windowMenu);
		windowMenu.add(prefAction);
		windowMenu.add(viewList);

		MenuManager helpMenu = new MenuManager("&Help",
				IWorkbenchActionConstants.M_HELP);
		menuBar.add(helpMenu);
		helpMenu.add(aboutAction);

	}

	protected void fillCoolBar(ICoolBarManager coolBar) {
		IToolBarManager toolbar = new ToolBarManager(coolBar.getStyle());
		coolBar.add(toolbar);
	}

}
