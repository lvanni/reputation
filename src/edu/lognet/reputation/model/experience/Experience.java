package edu.lognet.reputation.model.experience;


/**
 * 
 * @author lvanni
 *
 */
public class Experience {
	
	/* --------------------------------------------------------- */
	/* Attributes */
	/* --------------------------------------------------------- */
	private double feedback;//as rating
	private long lastTimeExp; // timestamp
	//Thao added
	private double perEval;//equal to feedback if user honest
	private double preRepScore;//having similar timestamp to feedback.
	private int numUses;//number of times using the service provided by the provider
	//Thao end
	
	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	//Thao added
	public Experience(double feedback, long lastExp, double perEval, double repScore, int numUses){
		this.feedback = feedback;
		this.lastTimeExp = lastExp;
		this.perEval = perEval;
		this.preRepScore = repScore;
		this.numUses = numUses;
	}
	
	public Experience(double feedback, double perEval, double repScore, int numUses){
		this(feedback, System.currentTimeMillis(), perEval, repScore, numUses);
	}
	//Thao end

	/* --------------------------------------------------------- */
	/* GETTER & SETTER */
	/* --------------------------------------------------------- */
	//Thao added
	public void setPreRepScore(double repScore){
		this.preRepScore = repScore;
	}
	
	public double getPreRepScore(){
		return preRepScore;
	}
	
	public double getPerEval(){
		return perEval;
	}
	//Thao end
	
	public double getFeedback() {
		return feedback;
	}

	public void setFeedback(double feedback) {
		this.feedback = feedback;
	}

	public long getLastTimeExp() {
		return lastTimeExp;
	}

	public void setLastTimeExp(long lastExp) {
		this.lastTimeExp = lastExp;
	}

	public void setNumUses(int numUses){
		this.numUses = numUses;
	}
	
	public int getNumUses(){
		return numUses;
	}

}
