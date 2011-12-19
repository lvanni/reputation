package edu.lognet.reputation.model.experience;

import edu.lognet.reputation.model.service.Service;
import edu.lognet.reputation.model.user.IConsumer;
import edu.lognet.reputation.model.user.IRater;

/**
 * Represent the credibility of a Rater in a given service (for a given
 * consumer)
 * 
 * @author Laurent Vanni, Thao Nguyen
 */
public class Credibility {
	/* --------------------------------------------------------- */
	/* Attributes */
	/* --------------------------------------------------------- */
	private double credibility;
	private IConsumer consumer;
	private IRater rater;
	private Service service;
	//Thao added: maybe IConsumer, IRater & Service above are unnecessary
	private double usefulnessFactor;
	private int numberSubmission;
	//Thao end

	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public Credibility(double credibility, double useful, int num, IConsumer consumer, IRater rater,
			Service service) {
		this.credibility = credibility;
		this.consumer = consumer;
		this.rater = rater;
		this.service = service;
		this.usefulnessFactor = useful;
		this.numberSubmission = num;
	}
	
	//Short constructor, used for temporary raterSetTable in calculating temporary creds for users
	public Credibility(double credibility) {
		this.credibility = credibility;		
	}
	
	/* --------------------------------------------------------- */
	/* GETTER & SETTER */
	/* --------------------------------------------------------- */
	//Thao added
	public double getUsefulnessFactor(){
		return usefulnessFactor;
	}	
	public int getNumberSubmission(){
		return numberSubmission;
	}
	//Thao end
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

	public void setUsefulnessFactor(double newuseful) {
		this.usefulnessFactor = newuseful;
	}

	public void setNumberSubmissions(int number) {
		this.numberSubmission = number;
	}

}
