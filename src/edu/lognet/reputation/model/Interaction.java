package edu.lognet.reputation.model;

import edu.lognet.reputation.model.service.Service;
import edu.lognet.reputation.model.user.IConsumer;
import edu.lognet.reputation.model.user.IProvider;
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
	//Thao end
		
	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	//Thao added
	public Interaction(IProvider provider, IConsumer consumer, Service service, double feedback, double perEval, double repScore, double dataLost) {
		this.provider = provider;
		this.consumer = consumer;
		this.service = service;
		this.feedback = feedback;
		this.personalEval = perEval;
		this.estimatedRepScore = repScore;
		this.dataLost = dataLost;
	}
	//Thao end
	
	/* --------------------------------------------------------- */
	/* Others Override Methods */
	/* --------------------------------------------------------- */
	@Override
	public String toString() {
		//Thao commented
		//return "\t" + service.getId() + "\t\t" + ((User) provider).getId() + "\t\t" + ((User) consumer).getId() + "\t\t" + feedback;
		//Thao added
		return "\t" + service.getId() + "\t\t" + ((User) provider).getId() + "\t\t" + ((User) consumer).getId() + "\t\t" + feedback + "\t\t" + personalEval + "\t\t" + estimatedRepScore + "\t\t" + dataLost;
		//Thao end
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
}
