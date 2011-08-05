package edu.lognet.reputation.model.beans.experience;

/**
 * 
 * @author lvanni
 *
 */
public class Experience {
	
	/* --------------------------------------------------------- */
	/* Attributes */
	/* --------------------------------------------------------- */
	private double feedback;
	private long lastExp; // timestamp
	
	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public Experience(double feedback,long lastExp) {
		this.feedback = feedback;
		this.lastExp = lastExp;
	}
	
	public Experience(double feedback) {
		this(feedback, System.currentTimeMillis());
	}

	/* --------------------------------------------------------- */
	/* GETTER & SETTER */
	/* --------------------------------------------------------- */
	public double getFeedback() {
		return feedback;
	}

	public void setFeedback(double feedback) {
		this.feedback = feedback;
	}

	public long getLastExp() {
		return lastExp;
	}

	public void setLastExp(long lastExp) {
		this.lastExp = lastExp;
	}
	
}
