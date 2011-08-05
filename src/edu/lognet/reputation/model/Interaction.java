package edu.lognet.reputation.model;

import edu.lognet.reputation.model.beans.service.Service;
import edu.lognet.reputation.model.beans.user.IConsumer;
import edu.lognet.reputation.model.beans.user.IProvider;
import edu.lognet.reputation.model.beans.user.User;

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
	
	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public Interaction(IProvider provider, IConsumer consumer, Service service, double feedback) {
		this.provider = provider;
		this.consumer = consumer;
		this.service = service;
		this.feedback = feedback;
	}
	
	/* --------------------------------------------------------- */
	/* Others Override Methods */
	/* --------------------------------------------------------- */
	@Override
	public String toString() {
		return "\t" + service.getId() + "\t\t" + ((User) provider).getId() + "\t\t" + ((User) consumer).getId() + "\t\t" + feedback;
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
}
