package edu.lognet.reputation.controller.simulations.simulationcolluding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.lognet.reputation.controller.simulations.simulation2.Simulation2;
import edu.lognet.reputation.model.Interaction;
import edu.lognet.reputation.model.service.Service;
import edu.lognet.reputation.model.user.User;
import edu.lognet.reputation.view.gui.UserGUI;

public class SimulationMilking extends Simulation2 {
	
	private int numberOfNewPeers;
	private int turnGoodToBadInteraction;
	private ArrayList<User> milkingUsersList;

	public SimulationMilking(int interactionNumber, int serviceNumber,
			int totalUserNumber, int goodUser, int badUser,
			int dataLostPercent, int choosingStrategy,int numberOfNewPeers,int turnGoodToBadInteraction) {
		super(interactionNumber, serviceNumber, totalUserNumber, goodUser, badUser,
				dataLostPercent, choosingStrategy);
		this.numberOfNewPeers = numberOfNewPeers;
		this.turnGoodToBadInteraction = turnGoodToBadInteraction;
		milkingUsersList = new ArrayList<User>();
	
	}
	
	private void createNewGoodPeers(){
		Random randomGenerator = new Random();
		int age=0;
		double QoS =0;
		Service service = null;
		User user = null;
		UserGUI userGUIb;
		for(int i=0;i<numberOfNewPeers;i++){
			age = randomGenerator.nextInt(50) + 18; // user age E [18 ; 68)
			setUserNumber(getUserNumber()+1); //increment user number
			QoS = (randomGenerator.nextInt(30) + 71.0) / 100;
			service = services.get(0); //collusive attack only on one service
			user = new User("u" + getUserNumber(), "user" + getUserNumber(), age, service,User.providerType.GOOD,
					User.raterType.HONEST, User.collusionGroup.C1, QoS, getResourceAvailable(),0);
			//for GUI
			userGUIb = new UserGUI(user,1000,1000);
			putUserGUI(userGUIb);
			service.addProdiver(user);
			//for engine
			users.add(user);
			//for milking
			milkingUsersList.add(user);
		}
	}
	
	private void changePeerState(){
		Random randomGenerator = new Random();
		for(int i=0;i<milkingUsersList.size();i++){
			milkingUsersList.get(i).setQoS((randomGenerator.nextInt(30) + 1.0)/100);
		}
	}
	
	public void run(){
		// SETUP
		try{
		setup();
		System.out.println(turnGoodToBadInteraction);
		int interactionNum = (getInteractionNumber()* turnGoodToBadInteraction)/100;
		// COMPUTE
		List<Interaction> interactions = new ArrayList<Interaction>();
		createNewGoodPeers();
		for (int i = 0; i < getInteractionNumber(); i++) {
			Interaction interaction = createInteraction(i);
			if(i == interactionNum){
				System.out.println("Peers turn");
				changePeerState();
			}
			if (interaction != null) {
				interactions.add(interaction);
				movePeer(interaction);
				setInteractionCount(getInteractionCount()+1);
			} else
				i--;
		}

		// PRINT RESULT
		extractData(interactions);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
