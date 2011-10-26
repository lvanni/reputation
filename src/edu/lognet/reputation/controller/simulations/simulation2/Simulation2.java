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

	private Map<String, UserGUI> userFromPosition;
	private Map<String, UserGUI> positionFromUser;
	private int interactionCount = 0;

	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public Simulation2(int interactionNumber, int serviceNumber,
			int totalUserNumber, int goodUser, int badUser, int dataLostPercent, int choosingStrategy) {
		super(interactionNumber, serviceNumber, totalUserNumber, goodUser,
				badUser, dataLostPercent, choosingStrategy);
		userFromPosition = new HashMap<String, UserGUI>();
		positionFromUser = new HashMap<String, UserGUI>();
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
		UserGUI provider = getPositionFromUser(((User)interaction.getProvider()).getId());
		UserGUI consumer = getPositionFromUser(((User)interaction.getConsumer()).getId());
		int px = provider.getX();
		int py = provider.getY();
		int cx = consumer.getX();
		int cy = consumer.getY();

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
		UserGUI user1 = getUserFromPosition(px, py);
		user1.setX(provider.getX());
		user1.setY(provider.getY());
		provider.setX(px);
		provider.setY(py);
		putUserGUI(user1);
		putUserGUI(provider);

		UserGUI user2 = getUserFromPosition(cx, cy);
		user2.setX(consumer.getX());
		user2.setY(consumer.getY());
		consumer.setX(cx);
		consumer.setY(cy);
		putUserGUI(user2);
		putUserGUI(consumer);
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
			for (int i = 0; i < getInteractionNumber(); i++) {
				Interaction interaction = createInteraction(i);
				if(interaction != null) {
					interactions.add(interaction);
					movePeer(interaction);
				}
				interactionCount++;
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
		return new HashMap<String, UserGUI>(userFromPosition);
	}

	public synchronized UserGUI getPositionFromUser(String userID) {
		return new UserGUI(userFromPosition.get(userID));
	}

	public synchronized UserGUI getUserFromPosition(int x, int y) {
		return new UserGUI(positionFromUser.get("x" + x + "y" + y));
	}

	public synchronized void putUserGUI(UserGUI userGUI) {
		this.userFromPosition.put(userGUI.getUser().getId(), userGUI);
		this.positionFromUser.put("x" + userGUI.getX() + "y" + userGUI.getY(), userGUI);
	}

	public synchronized int getInteractionCount() {
		return interactionCount;
	}

	public synchronized void setInteractionCount(int interactionCount) {
		this.interactionCount = interactionCount;
	}


}
