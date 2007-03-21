package jamsa.rcp.downloader;

import jamsa.rcp.downloader.views.CategoryTreeView;
import jamsa.rcp.downloader.views.ConsoleView;
import jamsa.rcp.downloader.views.TaskInfoView;
import jamsa.rcp.downloader.views.TaskTableView;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		layout.addView(CategoryTreeView.ID, IPageLayout.LEFT, 0.3f, layout
				.getEditorArea());
		// layout.addStandaloneView(CategoryTreeView.ID, false,
		// IPageLayout.LEFT,
		// 0.4f, layout.getEditorArea());
		// layout.addStandaloneView(TaskTableView.ID, false, IPageLayout.TOP,
		// 0.7f, layout.getEditorArea());
		layout.addView(TaskTableView.ID, IPageLayout.TOP, 0.7f, layout
				.getEditorArea());
		IFolderLayout folderLayout = layout.createFolder("folder", IPageLayout.BOTTOM, 0.4f, layout
				.getEditorArea());
		folderLayout.addView(TaskInfoView.ID);
		folderLayout.addView(ConsoleView.ID);
		layout.setEditorAreaVisible(false);
	}
}
