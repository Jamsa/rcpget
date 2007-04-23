package jamsa.rcp.downloader;

import org.eclipse.osgi.util.NLS;

/**
 * 国际化处理
 * 
 * @author 朱杰
 * 
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "messages";

	//==Category Dialog==
	public static String CategoryDialog_Text;

	public static String CategoryDialog_CanNotCreateDirectory;

	public static String CategoryDialog_Existed;

	public static String CategoryDialog_NameCanNotNull;

	public static String CategoryDialog_PathCanNotNull;

	public static String CategoryDialog_SavePath;

	public static String CategoryDialog_Select;

	public static String CategoryDialog_Setting;
	//==Category Dialog==

	public static String ConsoleView_Thread;

	public static String CopyTaskURLAction_ToolTipText;

	public static String DeleteCategoryAction_DeleteCategory;

	public static String DeleteCategoryAction_DeleteCategoryConfirm;

	public static String DeleteTaskAction_DeleteRunningTaskConfirm;

	public static String DeleteTaskAction_DeleteTask;

	public static String DeleteTaskAction_DeleteTaskConfirm;

	public static String DeleteTaskAction_DeleteTaskFile;

	public static String DeleteTaskAction_ToolTipText;

	public static String EmptyTrashAction_DeleteTrashFile;

	public static String EmptyTrashAction_EmptyTrash;

	public static String EmptyTrashAction_EmptyTrashConfirm;

	public static String ModifyTaskAction_ToolTipText;

	// =====Action Message=====
	public static String NewTaskAction_text;

	public static String NewTaskAction_ToolTipText;

	public static String Preference_Connection_Connection;

	public static String Preference_Connection_Limit;

	public static String Preference_Connection_MaxRunTasks;

	public static String Preference_Connection_RetryDelay;

	public static String Preference_Connection_RetryTimes;

	public static String Preference_Connection_Second;

	public static String Preference_Connection_Timeout;

	public static String Preference_DefaultTask_Auto;

	public static String Preference_DefaultTask_Category;

	public static String Preference_DefaultTask_DefaultCategory;

	public static String Preference_DefaultTask_Directory;

	public static String Preference_DefaultTask_LastSelectCategory;

	public static String Preference_DefaultTask_Manual;

	public static String Preference_DefaultTask_RunTask;

	public static String Preference_DefaultTask_SettingCategory;

	public static String Preference_General_MininizeToSystemTray;

	public static String Preference_Monitor_MonitorClipboard;

	public static String Preference_Monitor_MonitorFileType;

	public static String RestartTaskAction_ToolTipText;

	public static String RestoreTaskAction_ToolTipText;

	public static String RunTaskAction_text;

	public static String ModifyTaskAction_text;

	public static String RunTaskAction_ToolTipText;

	public static String StopTaskAction_text;

	public static String RestartTaskAction_text;

	public static String DeleteTaskAction_text;

	public static String RestoreTaskAction_text;

	public static String CopyTaskURLAction_text;

	public static String About_text;

	public static String Quit_text;

	public static String Preference_text;

	// =====Action Message=====

	// ===Menu===
	public static String File_text;

	public static String StopTaskAction_ToolTipText;

	public static String TaskInfoView_AverageSpeed;

	public static String TaskInfoView_Error;

	public static String TaskInfoView_FileName;

	public static String TaskInfoView_FileSize;

	public static String TaskInfoView_FileType;

	public static String TaskInfoView_Finish;

	public static String TaskInfoView_Key;

	public static String TaskInfoView_Memo;

	public static String TaskInfoView_Running;

	public static String TaskInfoView_SavePath;

	public static String TaskInfoView_Status;

	public static String TaskInfoView_Stop;

	public static String TaskInfoView_TotalTime;

	public static String TaskInfoView_URL;

	public static String TaskInfoView_Value;

	public static String TaskTableView_AverageSpeed;

	public static String TaskTableView_Error;

	public static String TaskTableView_FileName;

	public static String TaskTableView_FileSize;

	public static String TaskTableView_Finish;

	public static String TaskTableView_FinishedPercent;

	public static String TaskTableView_FinishedSize;

	public static String TaskTableView_Running;

	public static String TaskTableView_Speed;

	public static String TaskTableView_Status;

	public static String TaskTableView_Stop;

	public static String TaskTableView_Time;

	public static String TaskTableView_UnknownSize;

	public static String Window_text;

	public static String Help_text;
	// ===Menu===
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

}
