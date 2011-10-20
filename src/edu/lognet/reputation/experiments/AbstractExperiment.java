package edu.lognet.reputation.experiments;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.lognet.reputation.model.beans.service.Service;
import edu.lognet.reputation.model.beans.user.User;

public abstract class AbstractExperiment implements IExperiment {

	/* --------------------------------------------------------- */
	/* Attributes */
	/* --------------------------------------------------------- */
	public static int LOG_ENABLED = 0;
	
	private int dataLostPercent;
	private int interactionNumber;
	private int serviceNumber;
	private int userNumber;
	private int goodUser; /*in %, feedback > 0.75 */
	private int badUser; /*in %, feedback < 0.25 */
	private int choosingStrategy;

	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public AbstractExperiment(int interactionNumber, int serviceNumber,
			int totalUserNumber, int goodUser, int badUser, int dataLostPercent, int choosingStrategy) {
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
	public List<Service> getServiceSet() {
		List<Service> services = new ArrayList<Service>();
		for (int i = 0; i < serviceNumber; i++) {
			Service service = new Service("s" + i, "service" + i);
			services.add(service);
		}
		return services;
	}

	public List<User> getUserSet(List<Service> services) {
		// Random Factors
		Random randomGenerator = new Random();

		// init
		List<User> users = new ArrayList<User>();
		int good = 0;
		int bad = 0;
		if (AbstractExperiment.LOG_ENABLED == 1) {
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
			if (((100 * good) / userNumber) < goodUser) {
				QoS = (randomGenerator.nextInt(25) + 75.0) / 100;
				good++;
				pType = User.providerType.GOOD;
			} else if (((100 * bad) / userNumber) < badUser) {
				QoS = (randomGenerator.nextInt(25) + 1.0) / 100;
				bad++;
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
			if (AbstractExperiment.LOG_ENABLED == 1) {
				System.out.println(user);
			}
		}
		return users;
	}

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
