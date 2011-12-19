package edu.lognet.reputation.model;

import java.util.Map;

import edu.lognet.reputation.model.experience.Credibility;
import edu.lognet.reputation.model.service.Service;
import edu.lognet.reputation.model.user.IConsumer;
import edu.lognet.reputation.model.user.IProvider;
import edu.lognet.reputation.model.user.IRater;
import edu.lognet.reputation.model.user.User;

/**
 * Represent an Interaction One to One (producer/consumer)
 * @author lvanni
 *
 */
public class Interaction {
	
	/* --------------------------------------------------------- */
	/* Attributes */
	/* --------------------------------------------------------- */
	private IProvider provider;
	private IConsumer consumer;
	private Service service;
	private double feedback;
	//Thao added
	private double personalEval;
	private double estimatedRepScore;
	private double dataLost;
	private Map<IRater, Credibility> credibilityOfRatersForChosenProvider;
	//Thao end
		
	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public Interaction(IProvider provider, IConsumer consumer, Service service, double feedback, double perEval, double repScore, double dataLost, Map<IRater, Credibility> credibilityOfRatersForChosenProvider) {
		this.provider = provider;
		this.consumer = consumer;
		this.service = service;
		this.feedback = feedback;
		this.personalEval = perEval;
		this.estimatedRepScore = repScore;
		this.dataLost = dataLost;
		this.credibilityOfRatersForChosenProvider = credibilityOfRatersForChosenProvider;
	}
	
	/* --------------------------------------------------------- */
	/* Others Override Methods */
	/* --------------------------------------------------------- */
	@Override
	public String toString() {
		return "\t" + service.getId() + "\t\t" + ((User) provider).getId() + "\t\t" + ((User) consumer).getId() + "\t\t" + feedback + "\t\t" + personalEval + "\t\t" + estimatedRepScore + "\t\t" + dataLost +"\t\t" + credibilityOfRatersForChosenProvider;
	}

	/* --------------------------------------------------------- */
	/* GETTER AND SETTER */
	/* --------------------------------------------------------- */
	public IProvider getProvider() {
		return provider;
	}

	public void setProvider(IProvider provider) {
		this.provider = provider;
	}

	public IConsumer getConsumer() {
		return consumer;
	}

	public void setConsumer(IConsumer consumer) {
		this.consumer = consumer;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public double getFeedback() {
		return feedback;
	}

	public void setFeedback(double feedback) {
		this.feedback = feedback;
	}

	public double getDataLost() {
		return dataLost;
	}

	public double getPerEval() {
		return personalEval;
	}

	public double getEstimatedScore() {
		return estimatedRepScore;
	}
	
	public Map<IRater, Credibility> getCredibilityOfRatersForChosenProvider() {
		return credibilityOfRatersForChosenProvider;
	}
}
