package edu.lognet.reputation.controller.simulations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.lognet.reputation.model.user.UserGUIStatus;

/**
 * @author lvanni
 */

public class Simulation2 extends Simulation1 implements Runnable {

	private List<UserGUIStatus> userGUI;
	private int squareSize;

	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public Simulation2(int interactionNumber, int serviceNumber,
			int totalUserNumber, int goodUser, int badUser, int dataLostPercent, int choosingStrategy) {
		super(interactionNumber, serviceNumber, totalUserNumber, goodUser,
				badUser, dataLostPercent, choosingStrategy);
		userGUI = new ArrayList<UserGUIStatus>();
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
		for(int x = 0 ; x < squareSize ; x++) {
			for(int y = 0 ; y < squareSize ; y++) {
				userGUI.add(new UserGUIStatus(users.get(i), x, y));
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

	public List<UserGUIStatus> getUserGUI() {
		return userGUI;
	}

	public void setUserGUI(List<UserGUIStatus> userGUI) {
		this.userGUI = userGUI;
	}
}
