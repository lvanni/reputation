package edu.lognet.reputation.model.user;

import edu.lognet.reputation.model.experience.Experience;
import edu.lognet.reputation.model.service.Service;



/**
 * Represent a Rater in the simulator
 * A Rater is an user who give a rating on an existing provider
 * @author lvanni
 *
 */
public interface IRater {
	
	public static enum raterType {
		HONEST,
		DISHONEST,
		RANDOM,
		COLLUSIVE
	}
	
	/**
	 * Get the rating of the rater in a given service for a certain provider
	 * @param service
	 * @return
	 */
	public Experience getConsumerExp(IProvider provider, Service service);

	/**
	 * @TODO
	 * @return
	 */
	public raterType getRaterType();
}
