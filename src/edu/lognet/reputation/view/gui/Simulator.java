package edu.lognet.reputation.view.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Simulator {

	private Color red = new Color(null, 255, 0, 0);
	
	private final Shell shell;
	private Display display;
	
	private final Label address, zip, city, time, separator2, badUser, dataLost;
	private Text addressText, zipText, cityText, hourText, badUserText, dataLostText;

	public Simulator() {
		
		display = Display.getDefault();
		shell = new Shell(display);

		/* Init the shell */
		shell.setText("Reputation Simulation");
		FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		shell.setLayout(layout);

		// Configuration
		address = new Label(shell, SWT.NONE);
		address.setText("Interaction Number: ");
		FormData addressFormData = new FormData();
		addressFormData.top = new FormAttachment(0, 0);
		addressFormData.left = new FormAttachment(0, 0);
		address.setLayoutData(addressFormData);

		addressText = new Text(shell, SWT.BORDER);
		FormData addressTextFormData = new FormData();
		addressTextFormData.width = 160;
		addressTextFormData.height = 15;
		addressTextFormData.top = new FormAttachment(0, 0);
		addressTextFormData.left = new FormAttachment(0, 150);
		addressText.setLayoutData(addressTextFormData);

		zip = new Label(shell, SWT.NONE);
		zip.setText("Service Number: ");
		FormData zipFormData = new FormData();
		zipFormData.top = new FormAttachment(addressText, 0);
		zipFormData.left = new FormAttachment(0, 0);
		zip.setLayoutData(zipFormData);

		zipText = new Text(shell, SWT.BORDER);
		FormData zipTextFormData = new FormData();
		zipTextFormData.width = 160;
		zipTextFormData.height = 15;
		zipTextFormData.top = new FormAttachment(addressText, 0);
		zipTextFormData.left = new FormAttachment(0, 150);
		zipText.setLayoutData(zipTextFormData);

		city = new Label(shell, SWT.NONE);
		city.setText("Total User Number: ");
		FormData cityFormData = new FormData();
		cityFormData.top = new FormAttachment(zipText, 0);
		cityFormData.left = new FormAttachment(0, 0);
		city.setLayoutData(cityFormData);

		cityText = new Text(shell, SWT.BORDER);
		FormData cityTextFormData = new FormData();
		cityTextFormData.width = 160;
		cityTextFormData.height = 15;
		cityTextFormData.top = new FormAttachment(zipText, 0);
		cityTextFormData.left = new FormAttachment(0, 150);
		cityText.setLayoutData(cityTextFormData);

		time = new Label(shell, SWT.NONE);
		time.setText("Good User (%) : ");
		FormData timeFormData = new FormData();
		timeFormData.top = new FormAttachment(cityText, 0);
		timeFormData.left = new FormAttachment(0, 0);
		time.setLayoutData(timeFormData);

		hourText = new Text(shell, SWT.BORDER);
		FormData hourTextFormData = new FormData();
		hourTextFormData.width = 160;
		hourTextFormData.height = 15;
		hourTextFormData.top = new FormAttachment(cityText, 0);
		hourTextFormData.left = new FormAttachment(0, 150);
		hourText.setLayoutData(hourTextFormData);
		
		badUser = new Label(shell, SWT.NONE);
		badUser.setText("Bad User (%) : ");
		FormData badUserFormData = new FormData();
		badUserFormData.top = new FormAttachment(hourText, 0);
		badUserFormData.left = new FormAttachment(0, 0);
		badUser.setLayoutData(badUserFormData);

		badUserText = new Text(shell, SWT.BORDER);
		FormData badUserTextFormData = new FormData();
		badUserTextFormData.width = 160;
		badUserTextFormData.height = 15;
		badUserTextFormData.top = new FormAttachment(hourText, 0);
		badUserTextFormData.left = new FormAttachment(0, 150);
		badUserText.setLayoutData(badUserTextFormData);
		
		dataLost = new Label(shell, SWT.NONE);
		dataLost.setText("Data Lost (%) : ");
		FormData dataLostFormData = new FormData();
		dataLostFormData.top = new FormAttachment(badUserText, 0);
		dataLostFormData.left = new FormAttachment(0, 0);
		dataLost.setLayoutData(dataLostFormData);

		dataLostText = new Text(shell, SWT.BORDER);
		FormData dataLostTextFormData = new FormData();
		dataLostTextFormData.width = 160;
		dataLostTextFormData.height = 15;
		dataLostTextFormData.top = new FormAttachment(badUserText, 0);
		dataLostTextFormData.left = new FormAttachment(0, 150);
		dataLostText.setLayoutData(dataLostTextFormData);
		
		// VIEW
		final Composite composite = new Composite(shell, SWT.BORDER);
		FormData compositeFormData = new FormData();
		compositeFormData.width = 800;
		compositeFormData.height = 600;
		compositeFormData.top = new FormAttachment(0, 0);
		compositeFormData.left = new FormAttachment(0, 400);
		composite.setLayoutData(compositeFormData);
		FormLayout compositeFormLayout = new FormLayout();
		composite.setLayout(compositeFormLayout);

		// SEPARATOR
		separator2 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL
				| SWT.LINE_SOLID);
		FormData separator1FormData = new FormData();
		separator1FormData.width = 380;
		separator1FormData.top = new FormAttachment(dataLostText, 10);
		separator2.setLayoutData(separator1FormData);

		// button "SEND"
		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText("Start");
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
		FormData okFormData = new FormData();
		okFormData.width = 80;
		okFormData.top = new FormAttachment(separator2, 10);
		okFormData.left = new FormAttachment(0, 100);
		okButton.setLayoutData(okFormData);
		shell.setDefaultButton(okButton);

		// button "CLEAR"
		final Button clearButton = new Button(shell, SWT.PUSH);
		clearButton.setText("Stop");
		clearButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			}
		});
		FormData clearFormData = new FormData();
		clearFormData.width = 80;
		clearFormData.top = new FormAttachment(separator2, 10);
		clearFormData.left = new FormAttachment(okButton, 5);
		clearButton.setLayoutData(clearFormData);
		shell.pack();
	}

	public void start() {
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	public static void main(String args[]) {
		new Simulator().start();
	}
}
