package edu.lognet.reputation.view.gui;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.lognet.reputation.controller.simulations.simulation2.Simulation2;
import edu.lognet.reputation.controller.simulations.simulation2.UserGUIStatus;

public class Simulator {

	/* --------------------------------------------------------- */
	/* Attributes */
	/* --------------------------------------------------------- */
	private static final int TIMER_INTERVAL = 500;

	private final Shell shell;
	private Display display;

	private final Label interactionNumberLabel, serviceNumberLabel, totalUserNumberLabel, goodUserLabel, badUserLabel, dataLostLabel, separator;
	private Text interactionNumber, serviceNumber, totalUserNumber, goodUser, badUser, dataLost;

	private Canvas canvas;

	private Runnable runnable;

	private	Simulation2 simulation = null;

	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
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
		interactionNumberLabel = new Label(shell, SWT.NONE);
		interactionNumberLabel.setText("Interaction Number: ");
		FormData addressFormData = new FormData();
		addressFormData.top = new FormAttachment(0, 0);
		addressFormData.left = new FormAttachment(0, 0);
		interactionNumberLabel.setLayoutData(addressFormData);

		interactionNumber = new Text(shell, SWT.BORDER);
		FormData addressTextFormData = new FormData();
		addressTextFormData.width = 160;
		addressTextFormData.height = 15;
		addressTextFormData.top = new FormAttachment(0, 0);
		addressTextFormData.left = new FormAttachment(0, 150);
		interactionNumber.setLayoutData(addressTextFormData);

		serviceNumberLabel = new Label(shell, SWT.NONE);
		serviceNumberLabel.setText("Service Number: ");
		FormData zipFormData = new FormData();
		zipFormData.top = new FormAttachment(interactionNumber, 0);
		zipFormData.left = new FormAttachment(0, 0);
		serviceNumberLabel.setLayoutData(zipFormData);

		serviceNumber = new Text(shell, SWT.BORDER);
		FormData serviceNumberFormData = new FormData();
		serviceNumberFormData.width = 160;
		serviceNumberFormData.height = 15;
		serviceNumberFormData.top = new FormAttachment(interactionNumber, 0);
		serviceNumberFormData.left = new FormAttachment(0, 150);
		serviceNumber.setLayoutData(serviceNumberFormData);

		totalUserNumberLabel = new Label(shell, SWT.NONE);
		totalUserNumberLabel.setText("Total User Number: ");
		FormData totalUserNumberLabelFormData = new FormData();
		totalUserNumberLabelFormData.top = new FormAttachment(serviceNumber, 0);
		totalUserNumberLabelFormData.left = new FormAttachment(0, 0);
		totalUserNumberLabel.setLayoutData(totalUserNumberLabelFormData);

		totalUserNumber = new Text(shell, SWT.BORDER);
		totalUserNumber.setEnabled(false);
		totalUserNumber.setText("100000");
		FormData totalUserNumberFormData = new FormData();
		totalUserNumberFormData.width = 160;
		totalUserNumberFormData.height = 15;
		totalUserNumberFormData.top = new FormAttachment(serviceNumber, 0);
		totalUserNumberFormData.left = new FormAttachment(0, 150);
		totalUserNumber.setLayoutData(totalUserNumberFormData);

		goodUserLabel = new Label(shell, SWT.NONE);
		goodUserLabel.setText("Good User (%) : ");
		FormData timeFormData = new FormData();
		timeFormData.top = new FormAttachment(totalUserNumber, 0);
		timeFormData.left = new FormAttachment(0, 0);
		goodUserLabel.setLayoutData(timeFormData);

		goodUser = new Text(shell, SWT.BORDER);
		FormData hourTextFormData = new FormData();
		hourTextFormData.width = 160;
		hourTextFormData.height = 15;
		hourTextFormData.top = new FormAttachment(totalUserNumber, 0);
		hourTextFormData.left = new FormAttachment(0, 150);
		goodUser.setLayoutData(hourTextFormData);

		badUserLabel = new Label(shell, SWT.NONE);
		badUserLabel.setText("Bad User (%) : ");
		FormData badUserFormData = new FormData();
		badUserFormData.top = new FormAttachment(goodUser, 0);
		badUserFormData.left = new FormAttachment(0, 0);
		badUserLabel.setLayoutData(badUserFormData);

		badUser = new Text(shell, SWT.BORDER);
		FormData badUserTextFormData = new FormData();
		badUserTextFormData.width = 160;
		badUserTextFormData.height = 15;
		badUserTextFormData.top = new FormAttachment(goodUser, 0);
		badUserTextFormData.left = new FormAttachment(0, 150);
		badUser.setLayoutData(badUserTextFormData);

		dataLostLabel = new Label(shell, SWT.NONE);
		dataLostLabel.setText("Data Lost (%) : ");
		FormData dataLostFormData = new FormData();
		dataLostFormData.top = new FormAttachment(badUser, 0);
		dataLostFormData.left = new FormAttachment(0, 0);
		dataLostLabel.setLayoutData(dataLostFormData);

		dataLost = new Text(shell, SWT.BORDER);
		FormData dataLostTextFormData = new FormData();
		dataLostTextFormData.width = 160;
		dataLostTextFormData.height = 15;
		dataLostTextFormData.top = new FormAttachment(badUser, 0);
		dataLostTextFormData.left = new FormAttachment(0, 150);
		dataLost.setLayoutData(dataLostTextFormData);

		// VIEW
		canvas = new Canvas(shell, SWT.BORDER);
		FormData canvasFormData = new FormData();
		canvasFormData.width = 400;
		canvasFormData.height = 400;
		canvasFormData.top = new FormAttachment(0, 0);
		canvasFormData.left = new FormAttachment(0, 400);
		canvas.setLayoutData(canvasFormData);
		canvas.setBackground(display.getSystemColor(SWT.COLOR_BLACK));

		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				if(simulation != null) {
					Map <String, UserGUIStatus> userGUIStatus = simulation.getUserGUIList();
					for (String userId : userGUIStatus.keySet()) {
						UserGUIStatus userGUI = userGUIStatus.get(userId);
						event.gc.setBackground(event.display.getSystemColor(userGUI.getColor()));
//						event.gc.drawPoint(userGUI.getX(), userGUI.getY());
						event.gc.fillRectangle(userGUI.getX(), userGUI.getY(), 4, 4);
					}
				}
			}
		});

		// SEPARATOR
		separator = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL
				| SWT.LINE_SOLID);
		FormData separator1FormData = new FormData();
		separator1FormData.width = 380;
		separator1FormData.top = new FormAttachment(dataLost, 10);
		separator.setLayoutData(separator1FormData);

		// button "SEND"
		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText("Start");
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				// Create the simulation
				simulation = new Simulation2(
						Integer.parseInt(interactionNumber.getText()), 
						Integer.parseInt(serviceNumber.getText()), 
						Integer.parseInt(totalUserNumber.getText()), 
						Integer.parseInt(goodUser.getText()), 
						Integer.parseInt(badUser.getText()), 
						Integer.parseInt(dataLost.getText()), 
						3);

				// Launch the simulation
				new Thread(simulation).start();

				// Launch the timer
				display.timerExec(TIMER_INTERVAL, runnable);
			}
		});
		FormData okFormData = new FormData();
		okFormData.width = 80;
		okFormData.top = new FormAttachment(separator, 10);
		okFormData.left = new FormAttachment(0, 100);
		okButton.setLayoutData(okFormData);
		shell.setDefaultButton(okButton);

		// button "CLEAR"
		final Button clearButton = new Button(shell, SWT.PUSH);
		clearButton.setText("Stop");
		clearButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				display.timerExec(-1, runnable);
			}
		});
		FormData clearFormData = new FormData();
		clearFormData.width = 80;
		clearFormData.top = new FormAttachment(separator, 10);
		clearFormData.left = new FormAttachment(okButton, 5);
		clearButton.setLayoutData(clearFormData);

		// Set up the timer for the animation
		runnable = new Runnable() {
			public void run() {
				animate();
				display.timerExec(TIMER_INTERVAL, this);
			}
		};

		shell.pack();
	}

	/* --------------------------------------------------------- */
	/* public Methods */
	/* --------------------------------------------------------- */
	/**
	 * Animates the next frame
	 */
	public void animate() {
		// Force a redraw
		canvas.redraw();
	}

	public void start() {
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.timerExec(-1, runnable);
		display.dispose();
	}

	/* --------------------------------------------------------- */
	/* main */
	/* --------------------------------------------------------- */
	public static void main(String args[]) {
		new Simulator().start();
	}
}
