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
import org.eclipse.ui.internal.ShowViewMenu;
import org.eclipse.ui.plugin.AbstractUIPlugin;

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
		quitAction.setText("退出(&X)");
		quitAction.setImageDescriptor(AbstractUIPlugin
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						IImageKeys.QUIT));
		register(quitAction);

		aboutAction = ActionFactory.ABOUT.create(window);
		aboutAction.setText("关于(&A)");
		aboutAction.setImageDescriptor(AbstractUIPlugin
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						IImageKeys.ABOUT));
		register(aboutAction);

		prefAction = ActionFactory.PREFERENCES.create(window);
		prefAction.setText("首选项(&P)");
		register(prefAction);

		newTaskAction = new NewTaskAction(window, "新建任务(&N)");
		newTaskAction.setImageDescriptor(AbstractUIPlugin
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						IImageKeys.NEW_TASK));
		register(newTaskAction);

		modifyTaskAction = new ModifyTaskAction(window, "修改任务(&M)");
		modifyTaskAction.setImageDescriptor(AbstractUIPlugin
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						IImageKeys.MODIFY_TASK));
		register(modifyTaskAction);

		runTaskAction = new RunTaskAction(window, "运行任务(&S)");
		runTaskAction.setImageDescriptor(AbstractUIPlugin
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						IImageKeys.RUN_TASK));
		register(runTaskAction);

		restartTaskAction = new RestartTaskAction(window, "重新下载(&R)");
		restartTaskAction.setImageDescriptor(AbstractUIPlugin
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						IImageKeys.RESTART_TASK));
		register(restartTaskAction);

		stopTaskAction = new StopTaskAction(window, "停止任务(&Z)");
		stopTaskAction.setImageDescriptor(AbstractUIPlugin
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						IImageKeys.STOP_TASK));
		register(stopTaskAction);

		deleteTaskAction = new DeleteTaskAction(window, "删除任务(&D)");
		deleteTaskAction.setImageDescriptor(AbstractUIPlugin
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						IImageKeys.DELETE_TASK));
		register(deleteTaskAction);

		restoreTaskAction = new RestoreTaskAction(window, "还原(&R)");
		restoreTaskAction.setImageDescriptor(AbstractUIPlugin
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						IImageKeys.RESTORE_TASK));
		register(restoreTaskAction);

		viewList = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
		
	}

	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager("文件(&F)",
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

		MenuManager windowMenu = new MenuManager("窗口(&W)",
				IWorkbenchActionConstants.M_WINDOW);
		menuBar.add(windowMenu);
		windowMenu.add(prefAction);
		windowMenu.add(viewList);

		MenuManager helpMenu = new MenuManager("帮助(&H)",
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
		trayItem.add(aboutAction);
		trayItem.add(quitAction);
	}

}
