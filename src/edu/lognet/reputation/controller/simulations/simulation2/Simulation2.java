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

/**
 * @author lvanni
 */

public class Simulation2 extends Simulation1 implements Runnable {

	private Map<String, UserGUIStatus> userFromPosition;
	private Map<String, UserGUIStatus> positionFromUser;

	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public Simulation2(int interactionNumber, int serviceNumber,
			int totalUserNumber, int goodUser, int badUser, int dataLostPercent, int choosingStrategy) {
		super(interactionNumber, serviceNumber, totalUserNumber, goodUser,
				badUser, dataLostPercent, choosingStrategy);
		userFromPosition = new HashMap<String, UserGUIStatus>();
		positionFromUser = new HashMap<String, UserGUIStatus>();
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
		int i = 0; 
		for(int x = 0 ; x < Simulator.SIMULATION_SIZE ; x = x+Simulator.SIMULATION_PRECISION) {
			for(int y = 0 ; y < Simulator.SIMULATION_SIZE ; y = y+Simulator.SIMULATION_PRECISION) {
				putUserGUI(new UserGUIStatus(users.get(i), x, y));
				i++;
			}
		}
	}
	
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
					UserGUIStatus provider = getPositionFromUser(((User)interaction.getProvider()).getId());
					UserGUIStatus consumer = getPositionFromUser(((User)interaction.getConsumer()).getId());
					int px = provider.getX();
					int py = provider.getY();
					int cx = consumer.getX();
					int cy = consumer.getY();
					
					// MOVING X
					if(cx < px){
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
					if(cy < py){
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
					UserGUIStatus user1 = getUserFromPosition(px, py);
					user1.setX(provider.getX());
					user1.setY(provider.getY());
					provider.setX(px);
					provider.setY(py);
					putUserGUI(user1);
					putUserGUI(provider);
					
					UserGUIStatus user2 = getUserFromPosition(cx, cy);
					user2.setX(consumer.getX());
					user2.setY(consumer.getY());
					consumer.setX(cx);
					consumer.setY(cy);
					putUserGUI(user2);
					putUserGUI(consumer);
				}
//				try {
//					Thread.sleep(5);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
			}
			
			// PRINT RESULT
			extractData(interactions);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized Map<String, UserGUIStatus> getUserGUIList() {
		return new HashMap<String, UserGUIStatus>(userFromPosition);
	}
	
	public synchronized UserGUIStatus getPositionFromUser(String userID) {
		return new UserGUIStatus(userFromPosition.get(userID));
	}
	
	public synchronized UserGUIStatus getUserFromPosition(int x, int y) {
		return new UserGUIStatus(positionFromUser.get("x" + x + "y" + y));
	}

	public synchronized void putUserGUI(UserGUIStatus userGUI) {
		this.userFromPosition.put(userGUI.getUser().getId(), userGUI);
		this.positionFromUser.put("x" + userGUI.getX() + "y" + userGUI.getY(), userGUI);
	}
}
