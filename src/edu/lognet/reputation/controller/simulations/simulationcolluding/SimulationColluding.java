package edu.lognet.reputation.controller.simulations.simulationcolluding;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.lognet.reputation.controller.simulations.simulation2.Simulation2;
import edu.lognet.reputation.model.Interaction;
import edu.lognet.reputation.model.service.Service;
import edu.lognet.reputation.model.user.User;
import edu.lognet.reputation.view.gui.UserGUI;

/**
 * Represent the colluding attack simulation
 * we add a group of bad peers in the simulator
 * at a given time to change the behaviour of the system
 * 
 * @author Romain FRITZ, Laurent Vanni, Thao Nguyen
 */
public class SimulationColluding extends Simulation2{

	private int numberOfNewPeers; //number of peers of the colluding group
	private int injectionInteractionPercent; //when the group is injected


	public SimulationColluding(int interactionNumber, int serviceNumber,
			int totalUserNumber, int goodUser, int badUser,
			int dataLostPercent, int choosingStrategy,int numberOfNewPeers,int injectionInteractionPercent) {
		super(interactionNumber, serviceNumber, totalUserNumber, goodUser, badUser,
				dataLostPercent, choosingStrategy);
		this.numberOfNewPeers = numberOfNewPeers;
		this.injectionInteractionPercent = injectionInteractionPercent;
	}

	/**
	 * create bad/dishonest users for colluding attack
	 */
	public void createNewBadUser(){
		Random randomGenerator = new Random();
		int age=0;
		double QoS =0;
		Service service = null;
		User user = null;
		UserGUI userGUIb;
		for(int i=0;i<numberOfNewPeers;i++){
			age = randomGenerator.nextInt(50) + 18; // user age E [18 ; 68)
			setUserNumber(getUserNumber()+1); //increment user number
			QoS = (randomGenerator.nextInt(40) + 1.0) / 100;
			service = services.get(0); //collusive attack only on one service
			user = new User("u" + getUserNumber(), "user" + getUserNumber(), age, service,User.providerType.BAD,
					User.raterType.DISHONEST, User.collusionGroup.C1, QoS, getResourceAvailable(),0);
			service.addProdiver(user);
			//for GUI
			userGUIb = new UserGUI(user,1000,1000);
			putUserGUI(userGUIb);
			//for engine
			users.add(user);
		}
	}

	/**
	 * Starting the Experiements
	 * 
	 * @throws IOException
	 */
	public void start(){

	}

	@Override
	public void run(){
		// SETUP
		try{
		setup();
		int interactionNum = (getInteractionNumber()*injectionInteractionPercent)/100;
		// COMPUTE
		List<Interaction> interactions = new ArrayList<Interaction>();
		for (int i = 0; i < getInteractionNumber(); i++) {
			Interaction interaction = createInteraction(i);
			if(i == interactionNum){
				System.out.println("colluding injection");
				createNewBadUser();
			}
			if (interaction != null) {
				interactions.add(interaction);
				movePeer(interaction);
				setInteractionCount(getInteractionCount()+1);
			} else
				i--;
		}
		System.err.println(getUserNumber());

		// PRINT RESULT
		extractData(interactions);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
