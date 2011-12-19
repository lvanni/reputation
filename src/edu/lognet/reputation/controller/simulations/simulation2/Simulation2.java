package edu.lognet.reputation.controller.simulations.simulation2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.lognet.reputation.controller.simulations.Simulation;
import edu.lognet.reputation.controller.simulations.simulation1.Simulation1;
import edu.lognet.reputation.model.Interaction;
import edu.lognet.reputation.model.user.User;
import edu.lognet.reputation.view.gui.Simulator;
import edu.lognet.reputation.view.gui.UserGUI;

/**
 * Represent the second type of simulation:
 * The result show a segregation model based on the
 * reputation system
 * @author Laurent Vanni, Thao Nguyen
 */
public class Simulation2 extends Simulation1 implements Runnable {

	/* --------------------------------------------------------- */
	/* Attributes */
	/* --------------------------------------------------------- */
	private Map<String, UserGUI> userGUI;
	private int interactionCount = 0;

	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public Simulation2(int interactionNumber, int serviceNumber,
			int totalUserNumber, int goodUser, int badUser, int dataLostPercent, int choosingStrategy) {
		super(interactionNumber, serviceNumber, totalUserNumber, goodUser,0,0,0,
				badUser, 0, 100, 0, 0, 0, 100, dataLostPercent, choosingStrategy);
		userGUI = new HashMap<String, UserGUI>();
	}

	/* --------------------------------------------------------- */
	/* private methods */
	/* --------------------------------------------------------- */
	/**
	 * 
	 * @param user1
	 * @param user2
	 * @return
	 */
	private double getDistance(UserGUI user1, UserGUI user2) {
		int xa = user1.getX();
		int xb = user2.getX();
		int ya = user1.getY();
		int yb = user2.getY();
		int minDeltax = Math.min(Math.abs(xa-xb), Simulator.SIMULATION_SIZE-Math.abs(xa-xb));
		int minDeltay = Math.min(Math.abs(ya-yb), Simulator.SIMULATION_SIZE-Math.abs(ya-yb));		
		return Math.sqrt(Math.pow(minDeltax, 2) + Math.pow(minDeltay, 2));
	}

	/**
	 * 
	 * @param interaction
	 */
	private void movePeer(Interaction interaction){
		Map<String, UserGUI> userGUI = getUserGUIList();
		UserGUI provider = userGUI.get(((User)interaction.getProvider()).getId());
		UserGUI consumer = userGUI.get(((User)interaction.getConsumer()).getId());
		int px = provider.getX();
		int py = provider.getY();
		int cx = consumer.getX();
		int cy = consumer.getY();

		// MOVING X
		double distance = getDistance(provider, consumer);
		if(cx<px && distance>Simulator.SIMULATION_SIZE/(2*Simulator.SIMULATION_PRECISION)){
			cx = (cx + Simulator.SIMULATION_PRECISION) % Simulator.SIMULATION_SIZE;
			px -= Simulator.SIMULATION_PRECISION;
			if(px < 0) {
				px = Simulator.SIMULATION_SIZE + px;
			}
		} else {
			px = (px + Simulator.SIMULATION_PRECISION) % Simulator.SIMULATION_SIZE;
			cx -= Simulator.SIMULATION_PRECISION;
			if(cx < 0) {
				cx = Simulator.SIMULATION_SIZE + cx;
			}
		}
		
		//		 MOVING Y
		if(cy<py && distance>Simulator.SIMULATION_SIZE/(2*Simulator.SIMULATION_PRECISION)){
			cy = (cy + Simulator.SIMULATION_PRECISION) % Simulator.SIMULATION_SIZE;
			py -= Simulator.SIMULATION_PRECISION;
			if(py < 0) {
				py = Simulator.SIMULATION_SIZE + py;
			}
		} else {
			py = (py + Simulator.SIMULATION_PRECISION) % Simulator.SIMULATION_SIZE;
			cy -= Simulator.SIMULATION_PRECISION;
			if(cy < 0) {
				cy = Simulator.SIMULATION_SIZE + cy;
			}
		}	

		// COLOR CHANGE
		if(interaction.getFeedback()>0.5) {
			if(provider.getG() <= 253){
				provider.setG(provider.getG()+2);
			}
			if(consumer.getG() <= 253){
				consumer.setG(consumer.getG()+2);
			}
		} else {
			if(provider.getR() <= 253){
				provider.setR(provider.getR()+2);
			}
			if(consumer.getR() <= 253){
				consumer.setR(consumer.getR()+2);
			}
		}

		// EXCHANGE POSITION
		UserGUI user1 = userGUI.get("x" + px + "y" + py); //get the user in the new position of provider
		if(user1 != null) {
			user1.setX(provider.getX());
			user1.setY(provider.getY()); //set user in provider's new position to provider's old one
			provider.setX(px);
			provider.setY(py); //set provider to new position
			putUserGUI(user1);
			putUserGUI(provider);
			userGUI = getUserGUIList(); // get updated userGUI, maybe unnecessary because getUserGUIList() makes a shallow copy 
		}

		UserGUI user2 = userGUI.get("x" + cx + "y" + cy); //user2=user in consumer's new position
		if(user2 != null) {
			user2.setX(consumer.getX());
			user2.setY(consumer.getY()); //move user2 to consumer's old position
			consumer.setX(cx);
			consumer.setY(cy); //move consumer to new position
			putUserGUI(user2);
			putUserGUI(consumer); //update userGUI
		}

	}

	/* --------------------------------------------------------- */
	/* protected methods */
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
		int i = 0; 
		for(int x = 0 ; x < Simulator.SIMULATION_SIZE ; x = x+Simulator.SIMULATION_PRECISION) {
			for(int y = 0 ; y < Simulator.SIMULATION_SIZE ; y = y+Simulator.SIMULATION_PRECISION) {
				if(i < users.size()) {
					putUserGUI(new UserGUI(users.get(i), x, y));
					i++;
				}
			}
		}
	}

	/* --------------------------------------------------------- */
	/* public methods */
	/* --------------------------------------------------------- */
	public void run() {
		try {
			// SETUP
			setup();

			// COMPUTE
			final List<Interaction> interactions = new ArrayList<Interaction>();
			for (int i = 0; i < getInteractionNumber(); i++) {
				Interaction interaction = createInteraction(i);
				if(interaction != null) {
					interactions.add(interaction);
					movePeer(interaction);
					setInteractionCount(interactionCount+1);
				} else {
					i--;
				}
			}

			// PRINT RESULT
			extractData(interactions);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* --------------------------------------------------------- */
	/* synchronized methods */
	/* --------------------------------------------------------- */
	public synchronized Map<String, UserGUI> getUserGUIList() {
		return new HashMap<String, UserGUI>(userGUI);
	}

	public synchronized void putUserGUI(UserGUI userGUI) {
		this.userGUI.put(userGUI.getUser().getId(), userGUI);
		this.userGUI.put("x" + userGUI.getX() + "y" + userGUI.getY(), userGUI);
	}

	public synchronized int getInteractionCount() {
		return interactionCount;
	}

	public synchronized void setInteractionCount(int interactionCount) {
		this.interactionCount = interactionCount;
	}
}
