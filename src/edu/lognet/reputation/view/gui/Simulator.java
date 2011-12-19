package edu.lognet.reputation.view.gui;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
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

/**
 * Represent the GUI of the simulator
 * @author Laurent Vanni, Thao Nguyen
 *
 */
public class Simulator {

	/* --------------------------------------------------------- */
	/* Attributes */
	/* --------------------------------------------------------- */
	private static final int TIMER_INTERVAL = 10;
	public static final int SIMULATION_SIZE = 400;
	public static final int SIMULATION_PRECISION = 8;

	private final Shell shell;
	private Display display;

	private final Label interactionNumberLabel, serviceNumberLabel, totalUserNumberLabel, goodUserLabel, badUserLabel, dataLostLabel, separator, counterLabel, counter, errorLabel;
	private Text interactionNumber, serviceNumber, totalUserNumber, goodUser, badUser, dataLost;
	private final Button strategy1, strategy2, strategy3;

	private Canvas canvas;

	private Runnable view;
	private Thread controller;
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
		interactionNumber.setText("10000");
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
		serviceNumber.setText("1000");
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
		totalUserNumber.setText((SIMULATION_SIZE/Simulator.SIMULATION_PRECISION) * (SIMULATION_SIZE/Simulator.SIMULATION_PRECISION) + "");
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
		goodUser.setText("50");
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
		badUser.setText("50");
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
		dataLost.setText("0");
		FormData dataLostTextFormData = new FormData();
		dataLostTextFormData.width = 160;
		dataLostTextFormData.height = 15;
		dataLostTextFormData.top = new FormAttachment(badUser, 0);
		dataLostTextFormData.left = new FormAttachment(0, 150);
		dataLost.setLayoutData(dataLostTextFormData);

		// CHECKBOX
		strategy1 = new Button(shell, SWT.CHECK);
		strategy1.setSelection(true);
		strategy1.setText("strategy1");
		strategy1.setSelection(true);
		FormData strategy1FormData = new FormData();
		strategy1FormData.top = new FormAttachment(dataLost, 0);
		strategy1FormData.left = new FormAttachment(0, 0);
		strategy1.setLayoutData(strategy1FormData);
		strategy1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				strategy1.setSelection(true);
				strategy2.setSelection(false);
				strategy3.setSelection(false);
			}
		});

		strategy2 = new Button(shell, SWT.CHECK);
		strategy2.setSelection(true);
		strategy2.setText("strategy2");
		strategy2.setSelection(false);
		FormData strategy2FormData = new FormData();
		strategy2FormData.top = new FormAttachment(strategy1, 0);
		strategy2FormData.left = new FormAttachment(0, 0);
		strategy2.setLayoutData(strategy2FormData);
		strategy2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				strategy1.setSelection(false);
				strategy2.setSelection(true);
				strategy3.setSelection(false);
			}
		});

		strategy3 = new Button(shell, SWT.CHECK);
		strategy3.setSelection(true);
		strategy3.setText("strategy3");
		strategy3.setSelection(false);
		FormData strategy3FormData = new FormData();
		strategy3FormData.top = new FormAttachment(strategy2, 0);
		strategy3FormData.left = new FormAttachment(0, 0);
		strategy3.setLayoutData(strategy3FormData);
		strategy3.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				strategy1.setSelection(false);
				strategy2.setSelection(false);
				strategy3.setSelection(true);
			}
		});
		
		// ERROR LABEL
		errorLabel = new Label(shell, SWT.NONE);
		errorLabel.setForeground(new Color(display, 255, 0, 0));
		FormData errorLabelFormData = new FormData();
		errorLabelFormData.width = 280;
		errorLabelFormData.height = 20;
		errorLabelFormData.top = new FormAttachment(strategy2, 0);
		errorLabelFormData.left = new FormAttachment(0, 100);
		errorLabel.setLayoutData(errorLabelFormData);

		// VIEW
		canvas = new Canvas(shell, SWT.BORDER);
		FormData canvasFormData = new FormData();
		canvasFormData.width = SIMULATION_SIZE;
		canvasFormData.height = SIMULATION_SIZE;
		canvasFormData.top = new FormAttachment(0, 0);
		canvasFormData.left = new FormAttachment(0, 400);
		canvas.setLayoutData(canvasFormData);
		canvas.setBackground(display.getSystemColor(SWT.COLOR_BLACK));

		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				if(simulation != null) {
					counter.setText(simulation.getInteractionCount() + "");
					Map <String, UserGUI> userGUIStatus = simulation.getUserGUIList();
					for (String userId : userGUIStatus.keySet()) {
						UserGUI userGUI = userGUIStatus.get(userId);
						event.gc.setBackground(new Color(event.display, userGUI.getR(), userGUI.getG(), userGUI.getB()));
						event.gc.fillRectangle(userGUI.getX(), userGUI.getY(), SIMULATION_PRECISION, SIMULATION_PRECISION);
					}
				}
			}
		});
		
		// SEPARATOR
		separator = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL
				| SWT.LINE_SOLID);
		FormData separator1FormData = new FormData();
		separator1FormData.width = 380;
		separator1FormData.top = new FormAttachment(strategy3, 10);
		separator.setLayoutData(separator1FormData);

		counterLabel = new Label(shell, SWT.NONE);
		counterLabel.setText("Nb interaction: ");
		FormData counterLabelFormData = new FormData();
		counterLabelFormData.top = new FormAttachment(separator, 10);
		counterLabelFormData.left = new FormAttachment(0, 0);
		counterLabel.setLayoutData(counterLabelFormData);

		counter = new Label(shell, SWT.NONE);
		counter.setText("0");
		FormData counterFormData = new FormData();
		counterFormData.width = 160;
		counterFormData.height = 15;
		counterFormData.top = new FormAttachment(separator, 10);
		counterFormData.left = new FormAttachment(0, 100);
		counter.setLayoutData(counterFormData);

		// button "Start"
		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText("Start");
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				errorLabel.setText("");

				try {
					// get the parameters
					int strategy = 1;
					if(strategy2.getSelection()){
						strategy = 2;
					} else if(strategy3.getSelection()){
						strategy = 3;
					}

					int in = Integer.parseInt(interactionNumber.getText()); 
					int sn = Integer.parseInt(serviceNumber.getText());
					int tn = Integer.parseInt(totalUserNumber.getText()); 
					int gu = Integer.parseInt(goodUser.getText()); 
					int bu = Integer.parseInt(badUser.getText()); 
					int dl = Integer.parseInt(dataLost.getText());
					
					if(in == 0 || sn == 0 || tn == 0) {
						throw new NumberFormatException();
					} 

					// Create the simulation
					simulation = new Simulation2(in, sn, tn, gu, bu, dl, strategy);

					// Launch the simulation
					controller = new Thread(simulation);
					controller.start();

					// Launch the timer
					display.timerExec(TIMER_INTERVAL, view);
				} catch (NumberFormatException ex) {
					errorLabel.setText("Err: invalid parameter!");
				}
			}
		});
		FormData okFormData = new FormData();
		okFormData.width = 80;
		okFormData.top = new FormAttachment(counter, 10);
		okFormData.left = new FormAttachment(0, 100);
		okButton.setLayoutData(okFormData);
		shell.setDefaultButton(okButton);

		// button "Stop"
		final Button clearButton = new Button(shell, SWT.PUSH);
		clearButton.setText("Stop");
		clearButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				errorLabel.setText("");
				display.timerExec(-1, view);
				if(controller != null){
					controller.stop();
				}
			}
		});
		FormData clearFormData = new FormData();
		clearFormData.width = 80;
		clearFormData.top = new FormAttachment(counter, 10);
		clearFormData.left = new FormAttachment(okButton, 5);
		clearButton.setLayoutData(clearFormData);

		// Set up the timer for the animation
		view = new Runnable() {
			public void run() 
			{
				System.out.println("ok");
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
			System.out.println("ok");
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.timerExec(-1, view);
		if(controller != null){
			controller.stop();
		}
		display.dispose();
	}

	/* --------------------------------------------------------- */
	/* main */
	/* --------------------------------------------------------- */
	public static void main(String args[]) {
		new Simulator().start();
	}
}
