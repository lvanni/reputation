package edu.lognet.reputation.model.config;

/**
 * This class represent the configuration of an Experiment
 * @author Laurent Vanni, Thao Nguyen
 *
 */
public class Configuration {

	/* --------------------------------------------------------- */
	/* Attributes */
	/* --------------------------------------------------------- */
	private int interactionNumber;
	private int serviceNumber;
	private int totalUserNumber;
	private int goodUser;
	private int badUser;
	private int dataLost;
	private int choosingStrategy;
	
	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public Configuration(int interactionNumber, int serviceNumber,
			int totalUserNumber, int goodUser, int badUser, int dataLost,
			int choosingStrategy) {
		super();
		this.interactionNumber = interactionNumber;
		this.serviceNumber = serviceNumber;
		this.totalUserNumber = totalUserNumber;
		this.goodUser = goodUser;
		this.badUser = badUser;
		this.dataLost = dataLost;
		this.choosingStrategy = choosingStrategy;
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

	public int getServiceNumber() {
		return serviceNumber;
	}

	public void setServiceNumber(int serviceNumber) {
		this.serviceNumber = serviceNumber;
	}

	public int getTotalUserNumber() {
		return totalUserNumber;
	}

	public void setTotalUserNumber(int totalUserNumber) {
		this.totalUserNumber = totalUserNumber;
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

	public int getDataLost() {
		return dataLost;
	}

	public void setDataLost(int dataLost) {
		this.dataLost = dataLost;
	}

	public int getChoosingStrategy() {
		return choosingStrategy;
	}

	public void setChoosingStrategy(int choosingStrategy) {
		this.choosingStrategy = choosingStrategy;
	}
}
