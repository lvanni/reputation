package edu.lognet.reputation.model.beans.experience;

import edu.lognet.reputation.model.beans.service.Service;
import edu.lognet.reputation.model.beans.user.IConsumer;
import edu.lognet.reputation.model.beans.user.IRater;

/**
 * Represent the credibility of a Rater in a given service (for a given
 * consumer)
 * 
 * @author lvanni
 */
public class Credibility {
	/* --------------------------------------------------------- */
	/* Attributes */
	/* --------------------------------------------------------- */
	private double credibility;
	private IConsumer consumer;
	private IRater rater;
	private Service service;

	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public Credibility(double credibility, IConsumer consumer, IRater rater,
			Service service) {
		this.credibility = credibility;
		this.consumer = consumer;
		this.rater = rater;
		this.service = service;
	}

	/* --------------------------------------------------------- */
	/* GETTER & SETTER */
	/* --------------------------------------------------------- */
	public double getCredibility() {
		return credibility;
	}

	public void setCredibility(double credibility) {
		this.credibility = credibility;
	}

	public IConsumer getConsumer() {
		return consumer;
	}

	public void setConsumer(IConsumer consumer) {
		this.consumer = consumer;
	}

	public IRater getRater() {
		return rater;
	}

	public void setRater(IRater rater) {
		this.rater = rater;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

}
