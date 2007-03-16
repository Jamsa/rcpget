package jamsa.rcp.downloader.preference;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class General extends PreferencePage implements
IWorkbenchPreferencePage{

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		return container;
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}

}
