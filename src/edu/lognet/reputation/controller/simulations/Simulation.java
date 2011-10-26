package edu.lognet.reputation.controller.simulations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.lognet.reputation.controller.core.Reputation;
import edu.lognet.reputation.model.Interaction;
import edu.lognet.reputation.model.experience.Credibility;
import edu.lognet.reputation.model.service.Service;
import edu.lognet.reputation.model.user.IConsumer;
import edu.lognet.reputation.model.user.IProvider;
import edu.lognet.reputation.model.user.IRater;
import edu.lognet.reputation.model.user.ReputedProvider;
import edu.lognet.reputation.model.user.User;

public abstract class Simulation {

	/* --------------------------------------------------------- */
	/* Attributes */
	/* --------------------------------------------------------- */
	public static int LOG_ENABLED = 0;

	private int dataLostPercent;
	private int interactionNumber;
	private int serviceNumber;
	private int userNumber;
	private int goodUser; /* in %, feedback > 0.75 */
	private int badUser; /* in %, feedback < 0.25 */
	private int choosingStrategy;

	protected List<Service> services = getServiceSet();
	protected List<User> users = getUserSet(services);

	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public Simulation(int interactionNumber, int serviceNumber,
			int totalUserNumber, int goodUser, int badUser,
			int dataLostPercent, int choosingStrategy) {
		this.interactionNumber = interactionNumber;
		this.serviceNumber = serviceNumber;
		this.userNumber = totalUserNumber;
		this.goodUser = goodUser;
		this.badUser = badUser;
		this.dataLostPercent = dataLostPercent;
		this.choosingStrategy = choosingStrategy;
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

		// init
		List<User> users = new ArrayList<User>();
		if (Simulation.LOG_ENABLED == 1) {
			System.out.println("INFO: User List");
			System.out.println("\tId\t\tName\t\tAge\t\tService\t\tQoS");
		}

		// start Creating
		for (int i = 0; i < userNumber; i++) {
			int age = randomGenerator.nextInt(50) + 18; // user age E [18 ; 68)
			User.providerType pType;
			Service service = services.get(randomGenerator
					.nextInt(serviceNumber));
			double QoS = 0.0;

			int rand = randomGenerator.nextInt(100);
			if (rand < goodUser) {
				QoS = (randomGenerator.nextInt(25) + 75.0) / 100;
				pType = User.providerType.GOOD;
			} else if (rand >= goodUser && rand < goodUser + badUser) {
				QoS = (randomGenerator.nextInt(25) + 1.0) / 100;
				pType = User.providerType.BAD;
			} else {
				QoS = (randomGenerator.nextInt(50) + 25.0) / 100;
				pType = User.providerType.NORMAL;
			}
			User user = new User("u" + i, "user" + i, age, service, pType, QoS);
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
			List<IProvider> providers, Service service, Map<IProvider, Map<IRater, Credibility>> tempRaterSetTable) {
	
		List<ReputedProvider> reputedProviderList = new ArrayList<ReputedProvider>();

		for (IProvider provider : providers) {

			List<IRater> raters = service.getRaters(provider);
			for (IRater rater : raters) {
				if ((rater == consumer)
						|| (Math.random() * 100 < dataLostPercent)) {
					raters.remove(rater);
				}
			}

			tempRaterSetTable.put(provider, new HashMap<IRater, Credibility>());
			Reputation repCalculator = new Reputation();
			
			double reputation = repCalculator.getReputation(service, provider,
					raters, consumer, tempRaterSetTable);

			reputedProviderList.add(new ReputedProvider(provider, reputation));
		}

		Collections.sort(reputedProviderList);
		
		return reputedProviderList;
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

	public int getBadUser() {
		return badUser;
	}

	public void setBadUser(int badUser) {
		this.badUser = badUser;
	}

	public int getDataLostPercent() {
		return dataLostPercent;
	}

	public void setDataLostPercent(int dataLostPercent) {
		this.dataLostPercent = dataLostPercent;
	}

	public int getChoosingStrategy() {
		return choosingStrategy;
	}

}
