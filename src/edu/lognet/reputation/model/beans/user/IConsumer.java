package edu.lognet.reputation.model.beans.user;

import java.util.List;
import java.util.Map;

import edu.lognet.reputation.model.beans.experience.Credibility;
import edu.lognet.reputation.model.beans.experience.Experience;
import edu.lognet.reputation.model.beans.service.Service;

/**
 * 
 * @author lvanni
 *
 */
public interface IConsumer {
	
	/**
	 * Choose a provider in a providerList according to his reputation
	 * @param providers
	 */
	public IProvider chooseProvider(List<IProvider> providers, Service service, int dataLostPercent);
	
	/**
	 * Get the last experience of the consumer related to a provider in a given service 
	 * @param provider
	 * @param service
	 * @return Experience
	 */
	public Experience getConsumerExp(IProvider provider, Service service);
	
	/**
	 * Set the experience of the consumer related to a provider in a given service 
	 * @param provider
	 * @param service
	 * @param experience
	 */
	public void setConsumerExp(IProvider provider, Service service, Experience experience);
	
	/**
	 * Get the credibility of a Rater in a given service (for a given consumer)
	 * @param raters
	 * @return
	 */
	public Map<IRater, Credibility> getCredibilityOfRater(Service service);
}
