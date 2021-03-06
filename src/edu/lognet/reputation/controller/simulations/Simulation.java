package edu.lognet.reputation.controller.simulations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.lognet.reputation.controller.core.DefaultReputationSystem;
import edu.lognet.reputation.controller.core.IReputationSystem;
import edu.lognet.reputation.model.Interaction;
import edu.lognet.reputation.model.experience.Credibility;
import edu.lognet.reputation.model.experience.Experience;
import edu.lognet.reputation.model.service.Service;
import edu.lognet.reputation.model.user.IConsumer;
import edu.lognet.reputation.model.user.IProvider;
import edu.lognet.reputation.model.user.IRater;
import edu.lognet.reputation.model.user.ReputedProvider;
import edu.lognet.reputation.model.user.User;

/**
 * Represent the engine of the simulator
 * @author Laurent Vanni, Thao Nguyen
 *
 */
public abstract class Simulation {

	/* --------------------------------------------------------- */
	/* Attributes */
	/* --------------------------------------------------------- */
	public static int LOG_ENABLED = 0;
	
	protected IReputationSystem reputationSystem;

	private int interactionNumber;
	protected int serviceNumber;
	protected int userNumber;

	protected int goodUser; /* in %, feedback >= 0.75 */
	protected int badUser;  /* in %, 0<feedback <= 0.25 */
	protected int goodTurnBadUser;
	protected int fluctuateUser;
	protected int frequencyOfFluctuation;
	protected int badTurnGoodUser;
	protected int normalUser;

	protected int honestRater;
	protected int dishonestRater;
	protected int randomRater;
	protected int collusiveRater;
	protected int collusiveGroupNum;

	protected int resourceAvailable;
	private int dataLostPercent;
	private int personalWeight;
	private int chosenStrategy;
	private int maxProvListSize;

	protected List<Service> services = getServiceSet();
	protected List<User> users = getUserSet(services);
	
	private static double observanceTolDefault = 0.01;

	/* --------------------------------------------------------- */
	/* Constructors */ 
	/* --------------------------------------------------------- */
	public Simulation(int interactionNumber2, int serviceNumber2,
			int totalUserNumber, int goodUser2, int goodTurnBadUser,
			int fluctuateUser, int frequencyOfFluctuation, int badUser2,
			int badTurnGoodUser, int honestRater, int dishonestRater,
			int randomRater, int collusiveGroupNum, int resourceAvailable,
			int dataLost, int personalWeight, int choosingStrategy, int maxProvListSize) {
		
		// CREATE YOUR REPUTATION ALGORITHM BY IMPLEMENTING: edu.lognet.reputation.controller.core.IReputationSystem
		this.reputationSystem = new DefaultReputationSystem();
		
		this.interactionNumber = interactionNumber2;
		this.serviceNumber = serviceNumber2;
		this.userNumber = totalUserNumber;

		this.goodUser = goodUser2;
		this.goodTurnBadUser = goodTurnBadUser;
		this.fluctuateUser = fluctuateUser;
		this.frequencyOfFluctuation = frequencyOfFluctuation;
		this.badUser = badUser2;
		this.badTurnGoodUser = badTurnGoodUser;
		this.normalUser = 100 - (goodUser2 + goodTurnBadUser + fluctuateUser
				+ badUser2 + badTurnGoodUser);

		this.honestRater = honestRater;
		this.dishonestRater = dishonestRater;
		this.randomRater = randomRater;
		this.collusiveRater = 100 - (honestRater + dishonestRater + randomRater);
		this.collusiveGroupNum = collusiveGroupNum;

		this.resourceAvailable = resourceAvailable * interactionNumber2 / 100;
		this.dataLostPercent = dataLost;
		this.personalWeight = personalWeight;
		this.chosenStrategy = choosingStrategy;
		this.maxProvListSize = maxProvListSize;
	}

	/* --------------------------------------------------------- */
	/* public Methods */
	/* --------------------------------------------------------- */
	/**
	 * 
	 * @return
	 */
	public List<Service> getServiceSet() {
		List<Service> services = new ArrayList<Service>();
		for (int i = 0; i < serviceNumber; i++) {
			Service service = new Service("s" + i, "service" + i);
			services.add(service);
		}
		return services;
	}

	/**
	 * 
	 * @param services
	 * @return
	 */
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

		// start Creating
		for (int i = 0; i < userNumber; i++) {
			int age = randomGenerator.nextInt(50) + 18; // user age E [18 ; 68)
			User.providerType pType;
			User.raterType rType;
			User.collusionGroup cGroup = null;
			Service service = services.get(randomGenerator
					.nextInt(serviceNumber));
			double QoS = 0.0;

			// Assign randomly but not exact in % of users
			// especially if totalNumberUser is small
			/*
			 * int rand = randomGenerator.nextInt(100); if (rand < goodUser) {
			 * QoS = (randomGenerator.nextInt(25) + 75.0) / 100; pType =
			 * User.providerType.GOOD; } else if (rand >= goodUser && rand <
			 * goodUser + badUser) { QoS = (randomGenerator.nextInt(25) + 1.0) /
			 * 100; pType = User.providerType.BAD; } else { QoS =
			 * (randomGenerator.nextInt(50) + 25.0) / 100; pType =
			 * User.providerType.NORMAL; }
			 */

			int rand = randomGenerator.nextInt(100);
			// create goodUser, QoS>70%
			if ((((100 * good) / userNumber) < goodUser)
					&& (rand < (goodUser + normalUser / 5))) {
				QoS = (randomGenerator.nextInt(30) + 71.0) / 100;
				good++;
				pType = User.providerType.GOOD;
			}
			// create goodTurnBadUser
			else if ((((100 * goodTB) / userNumber) < goodTurnBadUser)
					&& (rand >= (goodUser + normalUser / 5))
					&& (rand < (goodUser + goodTurnBadUser + 2 * normalUser / 5))) {
				QoS = (randomGenerator.nextInt(30) + 71.0) / 100;
				goodTB++;
				pType = User.providerType.GOODTURNSBAD;
			}
			// create fluctuateUser whose QoS is dynamic and set later,
			// initially can be random
			else if ((((100 * fluctuate) / userNumber) < fluctuateUser)
					&& (rand >= (goodUser + goodTurnBadUser + 2 * normalUser / 5))
					&& (rand < (goodUser + goodTurnBadUser + fluctuateUser + 3 * normalUser / 5))) {
				if (randomGenerator.nextBoolean()) {
					QoS = (randomGenerator.nextInt(30) + 71.0) / 100;
				} else {
					QoS = (randomGenerator.nextInt(40) + 1.0) / 100;
				}
				fluctuate++;
				pType = User.providerType.FLUCTUATE;
			}

			// Bad users, QoS<=0.4
			else if ((((100 * bad) / userNumber) < badUser)
					&& (rand >= (goodUser + goodTurnBadUser + fluctuateUser + 3 * normalUser / 5))
					&& (rand < (goodUser + goodTurnBadUser + fluctuateUser
							+ badUser + 4 * normalUser / 5))) {
				QoS = (randomGenerator.nextInt(40) + 1.0) / 100;
				bad++;
				pType = User.providerType.BAD;
			}
			// badTurnGoodUser
			else if ((((100 * badTG) / userNumber) < badTurnGoodUser)
					&& (rand >= (goodUser + goodTurnBadUser + fluctuateUser
							+ badUser + 4 * normalUser / 5))
					&& (rand < (goodUser + goodTurnBadUser + fluctuateUser
							+ badUser + badTurnGoodUser + normalUser))) {
				QoS = (randomGenerator.nextInt(40) + 1.0) / 100;
				badTG++;
				pType = User.providerType.BADTURNSGOOD;
			}
			// normal, size can be bigger than it's supposed to be and then some
			// other group is fewer than it's supposed to be.
			else {

				QoS = (randomGenerator.nextInt(30) + 41.0) / 100;
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

			User user = new User("u" + i, "user" + i, age, service, pType,
					rType, cGroup, QoS, resourceAvailable,
					frequencyOfFluctuation);
			users.add(user);

			// ADD THE USER TO THE PROVIDERS LIST OF THE SERVICE
			service.addProdiver(user);

			// LOG
			if (Simulation.LOG_ENABLED == 1) {
				System.out.println(user);
			}
		}
		return users;
	}

	/**
	 * 
	 * @param providers
	 * @param service
	 * @return
	 */
	public List<ReputedProvider> getReputedProviderList(IConsumer consumer,
			List<IProvider> providers, Service service,
			Map<IProvider, Map<IRater, Credibility>> credibilityOfRaterMap) {

		List<ReputedProvider> reputedProviderList = new ArrayList<ReputedProvider>();

		for (IProvider provider : providers) {
			// raters always != null, has been initiated
			List<IRater> raters = service.getRaters(provider);
			for (IRater rater : raters) {
				if ((rater == consumer)
						|| (Math.random() * 100 < dataLostPercent)) {
					raters.remove(rater);
				}
			}

			credibilityOfRaterMap.put(provider,
					new HashMap<IRater, Credibility>());

			double reputation = reputationSystem.getReputation(service, provider,
					raters, consumer, credibilityOfRaterMap, personalWeight);

			reputedProviderList.add(new ReputedProvider(provider, reputation));
		}
		// SORT THE ReputedProvider LIST into ascending order, equal relation is
		// kept
		Collections.sort(reputedProviderList);

		return reputedProviderList;
	}
	
	/**
	 * 
	 * @param consumer
	 * @param provider
	 * @param perEval
	 * @return
	 */
	public double generateFeedback(IConsumer consumer, IProvider provider,
			double perEval) {
		if (consumer.getRaterType() == User.raterType.HONEST) {
			return perEval;
		}
		double rating;
		Random randomGenerator = new Random();
		if (consumer.getRaterType() == User.raterType.DISHONEST) {//ratingTol=0.5
			if (perEval > 0.7) {// the provider is supposed to be GOOD
				rating = Math.max(
						(double) 0,
						Math.round(perEval * 100 - consumer.getRatingTol()
								* 100)
								/ (double) 100);
			} else if (perEval <= 0.4) {// BAD provider
				rating = Math.min(
						(double) 1,
						Math.round(perEval * 100 + consumer.getRatingTol()
								* 100)
								/ (double) 100);
			} else if (randomGenerator.nextBoolean()) {// mean plus for NORMAL
														// provider
				rating = Math.min(
						(double) 1,
						Math.round(perEval * 100 + consumer.getRatingTol()
								* 100)
								/ (double) 100);
			} else {// mean minus for NORMAL provider
				rating = Math.max(
						(double) 0,
						Math.round(perEval * 100 - consumer.getRatingTol()
								* 100)
								/ (double) 100);
			}
			return rating;
		} else if (consumer.getRaterType() == User.raterType.RANDOM) {// ratingTol=1
			if (randomGenerator.nextBoolean()) {// mean plus
				rating = Math.min(
						(double) 1,
						Math.round(perEval
								* 100
								+ randomGenerator.nextInt((int) (consumer
										.getRatingTol() * 100)))
								/ (double) 100);
			} else {// minus
				rating = Math.max(
						(double) 0,
						Math.round(perEval
								* 100
								- randomGenerator.nextInt((int) (consumer
										.getRatingTol() * 100)))
								/ (double) 100);
			}
			return rating;
		} else {// =raterType.COLLUSIVE
			if (consumer.getCollusionCode() == provider.getCollusionCode()) {
				rating = 1;// give the highest rating to my collusion
				return rating;
			} 
			//bad rating for all the others
			else {
				rating = 0;
			}
			
			/*Not implement victimCode
			if (provider.getVictimCode() == null) {// not my victim, then be
													// honest
				rating = perEval;
				return rating;
			}
			if (consumer.getCollusionCode().ordinal() == provider
					.getVictimCode().ordinal()) {
				rating = 0;
			} else {// telling the truth
				rating = perEval;
			}
			*/
			return rating;
		}
	}

	/**
	 * 
	 * @param provider
	 * @return
	 */
	public double generatePerEval(IProvider provider) {
		Random randomGenerator = new Random();
		double value;
		if (randomGenerator.nextBoolean()) {
			value = Math.min(
					(double) 1,
					provider.getQoS()
							+ randomGenerator.nextInt((int) (Math
									.round(observanceTolDefault * 100)))
							/ (double) 100);
		} else {
			value = Math.max(
					(double) 0,
					provider.getQoS()
							- randomGenerator.nextInt((int) (Math
									.round(observanceTolDefault * 100)))
							/ (double) 100);
		}
		// value already has 2 decimal digits
		double eval = Math.round(value * 100) / (double) 100;
		return eval;
	}
	
	/**
	 * 
	 * @param oldExp
	 * @param newExp
	 * @return
	 */
	public static Experience adjustExperience(Experience oldExp, Experience newExp) {
		return newExp;
	}
	

	/**
	 * Start the experiements
	 */
	public abstract void start() throws IOException;

	/**
	 * 
	 * @throws IOException
	 */
	protected abstract void setup() throws IOException;

	/**
	 * 
	 * @param i
	 * @return
	 */
	protected abstract Interaction createInteraction(int i);

	/**
	 * 
	 * @param interactions
	 * @throws IOException
	 */
	protected abstract void extractData(List<Interaction> interactions)
			throws IOException;

	/* --------------------------------------------------------- */
	/* GETTER & SETTER */
	/* --------------------------------------------------------- */
	public int getInteractionNumber() {
		return interactionNumber;
	}

	public void setInteractionNumber(int interactionNumber) {
		this.interactionNumber = interactionNumber;
	}

	public int getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(int totalUserNumber) {
		this.userNumber = totalUserNumber;
	}

	public int getGoodUser() {
		return goodUser;
	}

	public void setGoodUser(int goodUser) {
		this.goodUser = goodUser;
	}

	public int getGoodTurnBadUser() {
		return goodTurnBadUser;
	}

	public int getFluctuateUser() {
		return fluctuateUser;
	}

	public int getBadUser() {
		return badUser;
	}

	public void setBadUser(int badUser) {
		this.badUser = badUser;
	}

	public int getBadTurnGoodUser() {
		return badTurnGoodUser;
	}

	public int getNormalUser() {
		return normalUser;
	}

	public int getResourceAvailable() {
		return resourceAvailable;
	}

	public int getFrequencyOfFluctuation() {
		return frequencyOfFluctuation;
	}

	public int getHonestRater() {
		return honestRater;
	}

	public int getDishonestRater() {
		return dishonestRater;
	}

	public int getRandomRater() {
		return randomRater;
	}

	public int getCollusiveRater() {
		return collusiveRater;
	}

	public int getCollusiveGroupNum() {
		return collusiveGroupNum;
	}

	public int getDataLostPercent() {
		return dataLostPercent;
	}

	public void setDataLostPercent(int dataLostPercent) {
		this.dataLostPercent = dataLostPercent;
	}

	public int getChosenStrategy() {
		return chosenStrategy;
	}

	public int getPersonalWeight() {
		return personalWeight;
	}

	public int getMaxProvListSize() {
		return maxProvListSize;
	}
}
