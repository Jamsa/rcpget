package jamsa.rcp.downloader;

import jamsa.rcp.downloader.actions.CopyTaskURLAction;
import jamsa.rcp.downloader.actions.DeleteTaskAction;
import jamsa.rcp.downloader.actions.ModifyTaskAction;
import jamsa.rcp.downloader.actions.NewTaskAction;
import jamsa.rcp.downloader.actions.RestartTaskAction;
import jamsa.rcp.downloader.actions.RestoreTaskAction;
import jamsa.rcp.downloader.actions.RunTaskAction;
import jamsa.rcp.downloader.actions.StopTaskAction;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public abstract class RCPGetActionFactory extends ActionFactory {

	protected RCPGetActionFactory(String actionId) {
		super(actionId);
	}

	public static final ActionFactory NEW_TASK = new ActionFactory(
			NewTaskAction.ID) {
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			IWorkbenchAction action = new NewTaskAction(window,
					Messages.NewTaskAction_text);//$NON-NLS-N$"新建任务"
			action.setId(getId());
			action.setToolTipText(Messages.NewTaskAction_ToolTipText);
			action.setImageDescriptor(AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IImageKeys.NEW_TASK));
			return action;
		}
	};

	public static final ActionFactory RUN_TASK = new ActionFactory(
			RunTaskAction.ID) {
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			IWorkbenchAction action = new RunTaskAction(window,
					Messages.RunTaskAction_text);//$NON-NLS-N$"运行任务"
			action.setId(getId());
			action.setToolTipText(Messages.RunTaskAction_ToolTipText);
			action.setImageDescriptor(AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IImageKeys.RUN_TASK));
			return action;
		}
	};

	public static final ActionFactory MODIFY_TASK = new ActionFactory(
			ModifyTaskAction.ID) {
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			IWorkbenchAction action = new ModifyTaskAction(window,
					Messages.ModifyTaskAction_text);// $NLS$"任务属性"
			action.setId(getId());
			action.setToolTipText(Messages.ModifyTaskAction_ToolTipText);
			action.setImageDescriptor(AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IImageKeys.MODIFY_TASK));
			return action;
		}
	};

	public static final ActionFactory STOP_TASK = new ActionFactory(
			StopTaskAction.ID) {
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			IWorkbenchAction action = new StopTaskAction(window,
					Messages.StopTaskAction_text);//$NON-NLS-N$"停止任务"
			action.setId(getId());
			action.setToolTipText(Messages.StopTaskAction_ToolTipText);
			action.setImageDescriptor(AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IImageKeys.STOP_TASK));
			return action;
		}
	};

	public static final ActionFactory RESTART_TASK = new ActionFactory(
			RestartTaskAction.ID) {
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			IWorkbenchAction action = new RestartTaskAction(window,
					Messages.RestartTaskAction_text);//$NON-NLS-N$"重新下载"
			action.setId(getId());
			action.setToolTipText(Messages.RestartTaskAction_ToolTipText);
			action.setImageDescriptor(AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IImageKeys.RESTART_TASK));
			return action;
		}
	};

	public static final ActionFactory DELETE_TASK = new ActionFactory(
			DeleteTaskAction.ID) {
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			IWorkbenchAction action = new DeleteTaskAction(window,
					Messages.DeleteTaskAction_text);//$NON-NLS-N$"删除任务"
			action.setId(getId());
			action.setToolTipText(Messages.DeleteTaskAction_ToolTipText);
			action.setImageDescriptor(AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IImageKeys.DELETE_TASK));
			return action;
		}
	};

	public static final ActionFactory RESTORE_TASK = new ActionFactory(
			RestoreTaskAction.ID) {
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			IWorkbenchAction action = new RestoreTaskAction(window,
					Messages.RestoreTaskAction_text);//$NON-NLS-N$"恢复任务"
			action.setId(getId());
			action.setToolTipText(Messages.RestoreTaskAction_ToolTipText);
			action.setImageDescriptor(AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IImageKeys.RESTORE_TASK));
			return action;
		}
	};

	public static final ActionFactory COPY_URL = new ActionFactory(
			CopyTaskURLAction.ID) {
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			IWorkbenchAction action = new CopyTaskURLAction(window,
					Messages.CopyTaskURLAction_text);//$NON-NLS-N$"复制URL"
			action.setId(getId());
			action.setToolTipText(Messages.CopyTaskURLAction_ToolTipText);
			return action;
		}
	};

	public static final ActionFactory ABOUT = new ActionFactory("about") { //$NON-NLS-1$
		public IWorkbenchAction create(IWorkbenchWindow window) {
			IWorkbenchAction action = ActionFactory.ABOUT.create(window);
			if (action != null) {
				action.setText(Messages.About_text);//$NON-NLS-N$"关于(&A)"
				action.setImageDescriptor(AbstractUIPlugin
						.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
								IImageKeys.ABOUT));
			}

			return action;
		}
	};

	public static final ActionFactory QUIT = new ActionFactory("quit") { //$NON-NLS-1$
		public IWorkbenchAction create(IWorkbenchWindow window) {
			IWorkbenchAction action = ActionFactory.QUIT.create(window);
			if (action != null) {
				action.setText(Messages.Quit_text);//$NON-NLS-N$"退出(&Q)"
				action.setImageDescriptor(AbstractUIPlugin
						.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
								IImageKeys.QUIT));
			}

			return action;
		}
	};

	public static final ActionFactory PREFERENCES = new ActionFactory(
			"preferences") { //$NON-NLS-1$
		public IWorkbenchAction create(IWorkbenchWindow window) {
			IWorkbenchAction action = ActionFactory.PREFERENCES.create(window);
			if (action != null) {
				action.setText(Messages.Preference_text);//$NON-NLS-N$"首选项(&P)"
			}
			return action;
		}
	};

}
