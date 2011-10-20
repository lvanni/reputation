package edu.lognet.reputation.model.beans.user;

import edu.lognet.reputation.model.beans.experience.Experience;
import edu.lognet.reputation.model.beans.service.Service;



/**
 * 
 * @author lvanni
 *
 */
public interface IRater {
	
	/**
	 * Get the rating of the rater in a given service for a certain provider
	 * @param service
	 * @return
	 */
	//Thao added
	public Experience getConsumerExp(IProvider provider, Service service);
	//Thao end
}
