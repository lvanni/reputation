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
 * @author lvanni
 */

public class Simulation2 extends Simulation1 implements Runnable {

	private Map<String, UserGUI> userGUI;
	private int interactionCount = 0;

	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public Simulation2(int interactionNumber, int serviceNumber,
			int totalUserNumber, int goodUser, int badUser, int dataLostPercent, int choosingStrategy) {
		super(interactionNumber, serviceNumber, totalUserNumber, goodUser,
				badUser, dataLostPercent, choosingStrategy);
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
		return Math.sqrt(Math.pow(xb-xa, 2) + Math.pow(yb-ya, 2));
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
		
//		System.out.println("Provider:");
//		System.out.println("\t-QoS: " + interaction.getProvider().getQoS());
//		System.out.println("\t-x: " + provider.getX());
//		System.out.println("\t-y: " + provider.getY());
//		System.out.println("\t-color: " + provider.getColor());
//		System.out.println("Consumer:");
//		System.out.println("\t-x: " + consumer.getX());
//		System.out.println("\t-y: " + consumer.getY());
//		System.out.println("\t-color: " + consumer.getColor());
//		System.out.println("Interaction feedback = " + interaction.getFeedback());

		// MOVING X
		double distance = getDistance(provider, consumer);
		if((interaction.getFeedback()>0.5 && cx<px && distance>Simulator.SIMULATION_SIZE/(2*Simulator.SIMULATION_PRECISION)) || 
				(interaction.getFeedback()<=0.5 && cx>=px && distance>Simulator.SIMULATION_SIZE/(2*Simulator.SIMULATION_PRECISION))){
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

		// MOVING Y
		if((interaction.getFeedback()>0.5 && cy<py && distance>Simulator.SIMULATION_SIZE/(2*Simulator.SIMULATION_PRECISION)) || 
				(interaction.getFeedback()<=0.5 && cy>=py && distance>Simulator.SIMULATION_SIZE/(2*Simulator.SIMULATION_PRECISION))){
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

		// EXCHANGE POSITION
		UserGUI user1 = userGUI.get("x" + px + "y" + py);
//		System.out.println("provider move to");
//		System.out.println("\t-x: " + user1.getX());
//		System.out.println("\t-y: " + user1.getY());
//		System.out.println("\t-color: " + user1.getColor());
		
		user1.setX(provider.getX());
		user1.setY(provider.getY());
		provider.setX(px);
		provider.setY(py);
		putUserGUI(user1);
		putUserGUI(provider);
		userGUI = getUserGUIList(); // update userGUI

		UserGUI user2 = userGUI.get("x" + cx + "y" + cy);
//		System.out.println("consumer move to");
//		System.out.println("\t-x: " + user2.getX());
//		System.out.println("\t-y: " + user2.getY());
//		System.out.println("\t-color: " + user2.getColor());
		
		user2.setX(consumer.getX());
		user2.setY(consumer.getY());
		consumer.setX(cx);
		consumer.setY(cy);
		putUserGUI(user2);
		putUserGUI(consumer);
		
//		System.out.println("\n-----\n");
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
			List<Interaction> interactions = new ArrayList<Interaction>();
			int i = 0;
			while (i < getInteractionNumber()) {
				Interaction interaction = createInteraction(i);
				if(interaction != null) {
					interactions.add(interaction);
					movePeer(interaction);
					i++;
					setInteractionCount(interactionCount+1);
				}
//				else {
//					System.out.println("interaction null");
//				}
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
