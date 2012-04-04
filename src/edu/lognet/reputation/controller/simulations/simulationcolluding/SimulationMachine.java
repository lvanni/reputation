package edu.lognet.reputation.controller.simulations.simulationcolluding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.lognet.reputation.controller.simulations.Simulation;
import edu.lognet.reputation.controller.simulations.simulation2.Simulation2;
import edu.lognet.reputation.model.Interaction;
import edu.lognet.reputation.model.service.Service;
import edu.lognet.reputation.model.user.Machine;
import edu.lognet.reputation.model.user.User;

/**
 * Represent the machine simulation
 * 
 * @author Romain Fritz , Laurent Vanni, Thao Nguyen
 */
public class SimulationMachine extends Simulation2{

	public SimulationMachine(int interactionNumber, int serviceNumber,
			int totalUserNumber, int goodUser, int badUser,
			int dataLostPercent, int choosingStrategy) {
		super(interactionNumber, serviceNumber, totalUserNumber, goodUser, badUser,
				dataLostPercent, choosingStrategy);

	}

	@Override
	public List<User> getUserSet(List<Service> services) {
		// Random Factors
		Random randomGenerator = new Random();

		if (Simulation.LOG_ENABLED == 1) {
			System.out.println("INFO: User List");
			System.out.println("\tId\t\tName\t\tAge\t\tService\t\tQoS");
		}

		// init
		List<User> users = new ArrayList<User>();
		// for temporary counting
		int good = 0;
		int goodTB = 0;
		int fluctuate = 0;
		int bad = 0;
		int badTG = 0;
		int[] counter = { 0, 0, 0 };
		int honestR = 0;
		int dishonestR = 0;
		int randomR = 0;

		int bandwidth = 0;
		int uptime = 0;
		boolean isAvailable = true;

		// start Creating
		for (int i = 0; i < userNumber; i++) {
			int age = randomGenerator.nextInt(50) + 18; // user age E [18 ; 68)
			User.providerType pType;
			User.raterType rType;
			User.collusionGroup cGroup = null;
			Service service = services.get(randomGenerator
					.nextInt(serviceNumber));

			int rand = randomGenerator.nextInt(100);
			// create goodUser, QoS>70%
			if ((((100 * good) / userNumber) < goodUser)
					&& (rand < (goodUser + normalUser / 5))) {
				//QoS = (randomGenerator.nextInt(30) + 71.0) / 100;
				bandwidth = randomGenerator.nextInt(500000)+500000;
				uptime = randomGenerator.nextInt(500000)+500000;
				isAvailable= true;
				good++;
				pType = User.providerType.GOOD;
			}
			// create goodTurnBadUser
			else if ((((100 * goodTB) / userNumber) < goodTurnBadUser)
					&& (rand >= (goodUser + normalUser / 5))
					&& (rand < (goodUser + goodTurnBadUser + 2 * normalUser / 5))) {
				bandwidth = randomGenerator.nextInt(500000)+500000;
				uptime = randomGenerator.nextInt(500000)+500000;
				isAvailable= true;
				good++;
				goodTB++;
				pType = User.providerType.GOODTURNSBAD;
			}
			// create fluctuateUser whose QoS is dynamic and set later,
			// initially can be random
			else if ((((100 * fluctuate) / userNumber) < fluctuateUser)
					&& (rand >= (goodUser + goodTurnBadUser + 2 * normalUser / 5))
					&& (rand < (goodUser + goodTurnBadUser + fluctuateUser + 3 * normalUser / 5))) {
				if (randomGenerator.nextBoolean()) {
					bandwidth = randomGenerator.nextInt(500000)+500000;
					uptime = randomGenerator.nextInt(500000)+500000;
					isAvailable= true;
					good++;
				} else {
					bandwidth = randomGenerator.nextInt(500000);
					uptime = randomGenerator.nextInt(500000);
					isAvailable= true;
					good++;
				}
				fluctuate++;
				pType = User.providerType.FLUCTUATE;
			}
			// Bad users, QoS<=0.4
			else if ((((100 * bad) / userNumber) < badUser)
					&& (rand >= (goodUser + goodTurnBadUser + fluctuateUser + 3 * normalUser / 5))
					&& (rand < (goodUser + goodTurnBadUser + fluctuateUser
							+ badUser + 4 * normalUser / 5))) {
				bandwidth = randomGenerator.nextInt(500000);
				uptime = randomGenerator.nextInt(500000);
				isAvailable= true;
				bad++;
				pType = User.providerType.BAD;
			}
			// badTurnGoodUser
			else if ((((100 * badTG) / userNumber) < badTurnGoodUser)
					&& (rand >= (goodUser + goodTurnBadUser + fluctuateUser
							+ badUser + 4 * normalUser / 5))
							&& (rand < (goodUser + goodTurnBadUser + fluctuateUser
									+ badUser + badTurnGoodUser + normalUser))) {
				bandwidth = randomGenerator.nextInt(500000);
				uptime = randomGenerator.nextInt(500000);
				isAvailable= true;
				badTG++;
				pType = User.providerType.BADTURNSGOOD;
			}
			// normal, size can be bigger than it's supposed to be and then some
			// other group is fewer than it's supposed to be.
			else {
				bandwidth = randomGenerator.nextInt(500000);
				uptime = randomGenerator.nextInt(500000);
				isAvailable= true;
				pType = User.providerType.NORMAL;
			}
			// GOOD->GOODTURNSBAD->FLUCTUATE->BAD->BADTURNSGOOD->NORMAL
			// HONEST->DISHONEST->RANDOM->COLLUSIVE(C1-C2-C3)
			// The order of raterType assigning doesn't matter to providers
			// since
			// raterType and providerType are independent
			// CREATE RATER TYPE
			if (((100 * honestR) / userNumber) < honestRater) {
				honestR++;
				rType = User.raterType.HONEST;
			}
			// dishonestRater
			else if (((100 * dishonestR) / userNumber) < dishonestRater) {
				dishonestR++;
				rType = User.raterType.DISHONEST;
			}
			// randomRater
			else if (((100 * randomR) / userNumber) < randomRater) {
				randomR++;
				rType = User.raterType.RANDOM;
			}
			// collusiveRater
			else {
				rType = User.raterType.COLLUSIVE;
				int sizeOfEachGroup = collusiveRater / collusiveGroupNum;// in %
				// assign collusion group from C1 to C3
				if ((100 * counter[0] / userNumber) < sizeOfEachGroup) {
					counter[0]++;
					cGroup = User.collusionGroup.C1;
				} else if (((100 * counter[1] / userNumber) < sizeOfEachGroup)
						|| (collusiveGroupNum == 2)) {
					counter[1]++;
					cGroup = User.collusionGroup.C2;
				} else {
					counter[2]++;
					cGroup = User.collusionGroup.C3;
				}
			}
			Machine m = new Machine("u" + i, "user" + i, age, service, pType, rType, cGroup, bandwidth, isAvailable, uptime, resourceAvailable, fluctuate);
			users.add(m);
			// ADD THE USER TO THE PROVIDERS LIST OF THE SERVICE
			service.addProdiver(m);

			// LOG
			if (Simulation.LOG_ENABLED == 1) {
				System.out.println(m);
			}
		}
		return users;

	}
	
	@Override
	public void run(){
		// SETUP
		try{
		setup();
		// COMPUTE
		List<Interaction> interactions = new ArrayList<Interaction>();
		for (int i = 0; i < getInteractionNumber(); i++) {
			Interaction interaction = createInteraction(i);
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
