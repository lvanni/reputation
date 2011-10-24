package edu.lognet.reputation.controller.simulations.simulation2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.lognet.reputation.controller.simulations.Simulation;
import edu.lognet.reputation.controller.simulations.simulation1.Simulation1;

/**
 * @author lvanni
 */

public class Simulation2 extends Simulation1 implements Runnable {

	private Map<String, UserGUIStatus> userGUI;
	private int squareSize;

	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public Simulation2(int interactionNumber, int serviceNumber,
			int totalUserNumber, int goodUser, int badUser, int dataLostPercent, int choosingStrategy) {
		super(interactionNumber, serviceNumber, totalUserNumber, goodUser,
				badUser, dataLostPercent, choosingStrategy);
		userGUI = new HashMap<String, UserGUIStatus>();
		squareSize = 0;
	}
	
	/* --------------------------------------------------------- */
	/* extends AbstractExperiment */
	/* --------------------------------------------------------- */
	/**
	 * @see Simulation#setup()
	 */
	protected void setup() throws IOException {
		// CREATE THE SERVICES
		services = getServiceSet();
		
		// CREATE THE USERS
		users = getUserSet(services);
		
		// Draw all the user into a square
		squareSize = (int) Math.sqrt(getUserNumber());
		int i = 0; 
		for(int x = 0 ; x < squareSize ; x = x+4) {
			for(int y = 0 ; y < squareSize ; y = y+4) {
				putUserGUI(new UserGUIStatus(users.get(i), x, y));
				i++;
			}
		}
		
		// start the simulation
//		super.start();
	}
	
	public void run() {
		try {
			setup();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized Map<String, UserGUIStatus> getUserGUIList() {
		return new HashMap<String, UserGUIStatus>(userGUI);
	}
	
	public synchronized UserGUIStatus getUserGUI(String userID) {
		return new UserGUIStatus(userGUI.get(userID));
	}

	public synchronized void putUserGUI(UserGUIStatus userGUI) {
		this.userGUI.put(userGUI.getUser().getId(), userGUI);
	}
}
