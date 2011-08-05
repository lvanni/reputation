package edu.lognet.reputation.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.lognet.reputation.model.beans.experience.Credibility;
import edu.lognet.reputation.model.beans.experience.Experience;
import edu.lognet.reputation.model.beans.service.Service;
import edu.lognet.reputation.model.beans.user.IRater;

/**
 * 
 * @author lvanni
 *
 */
public class Reputation {

	/**
	 * Return the reputation of a 
	 * @param raters
	 * @param consumerOldExperience
	 * @param cor
	 * @return
	 */
	public static double getReputation(List<IRater> raters, Experience consumerOldExperience, Map<IRater, Credibility> cor) {
		return 0.5;
	}
	
	/**
	 * Return the reputation of a 
	 * @param raters
	 * @param consumerOldExperience
	 * @param cor
	 * @return
	 */
	public static Experience adjustExperience(Experience oldExp, Experience newExp) {
		return new Experience(newExp.getFeedback());
	}
	
	/**
	 * 
	 * @param raters
	 * @param credibilityOfRater
	 * @param experience
	 * @return
	 */
	public static Map<IRater, Credibility> adjustCredibility(List<IRater> raters, Map<IRater, Credibility> credibilityOfRater, Experience experience) {
		if(credibilityOfRater == null){ // New service used: not yet CoR
			credibilityOfRater = new HashMap<IRater, Credibility>();
		}
		for(IRater rater : raters){
			if(credibilityOfRater.get(rater) == null){ // New Rater
				credibilityOfRater.get(rater).setCredibility(experience.getFeedback());
			} else {
				// TODO ADJUST THE CREDIBILITY
				credibilityOfRater.get(rater).setCredibility(experience.getFeedback());
			}
		}
		return credibilityOfRater;
	}
}
