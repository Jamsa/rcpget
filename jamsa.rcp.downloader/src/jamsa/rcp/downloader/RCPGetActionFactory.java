package jamsa.rcp.downloader;

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
			IWorkbenchAction action = new NewTaskAction(window, "�½�����");
			action.setId(getId());
			action.setToolTipText(action.getText());
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
			IWorkbenchAction action = new RunTaskAction(window, "��������");
			action.setId(getId());
			action.setToolTipText(action.getText());
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
			IWorkbenchAction action = new ModifyTaskAction(window, "�޸�����");
			action.setId(getId());
			action.setToolTipText(action.getText());
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
			IWorkbenchAction action = new StopTaskAction(window, "ֹͣ����");
			action.setId(getId());
			action.setToolTipText(action.getText());
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
			IWorkbenchAction action = new RestartTaskAction(window, "��������");
			action.setId(getId());
			action.setToolTipText(action.getText());
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
			IWorkbenchAction action = new DeleteTaskAction(window, "ɾ������");
			action.setId(getId());
			action.setToolTipText(action.getText());
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
			IWorkbenchAction action = new RestoreTaskAction(window, "�ָ�����");
			action.setId(getId());
			action.setToolTipText(action.getText());
			action.setImageDescriptor(AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IImageKeys.RESTORE_TASK));
			return action;
		}
	};

	public static final ActionFactory ABOUT = new ActionFactory("about") {
		public IWorkbenchAction create(IWorkbenchWindow window) {
			IWorkbenchAction action = ActionFactory.ABOUT.create(window);
			if (action != null) {
				action.setText("����(&A)");
				action.setImageDescriptor(AbstractUIPlugin
						.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
								IImageKeys.ABOUT));
			}

			return action;
		}
	};

	public static final ActionFactory QUIT = new ActionFactory("quit") {
		public IWorkbenchAction create(IWorkbenchWindow window) {
			IWorkbenchAction action = ActionFactory.QUIT.create(window);
			if (action != null) {
				action.setText("�˳�(&Q)");
				action.setImageDescriptor(AbstractUIPlugin
						.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
								IImageKeys.QUIT));
			}

			return action;
		}
	};

	public static final ActionFactory PREFERENCES = new ActionFactory(
			"preferences") {
		public IWorkbenchAction create(IWorkbenchWindow window) {
			IWorkbenchAction action = ActionFactory.PREFERENCES.create(window);
			if (action != null) {
				action.setText("��ѡ��(&P)");
			}
			return action;
		}
	};

}
