package edu.lognet.reputation.model.user;

import java.util.List;
import java.util.Map;

import edu.lognet.reputation.model.experience.Credibility;
import edu.lognet.reputation.model.experience.Experience;
import edu.lognet.reputation.model.service.Service;
import edu.lognet.reputation.model.user.User.collusionGroup;

/**
 * Represent a Consumer in the simulator
 * @author lvanni
 *
 */
public interface IConsumer {
	
	/**
	 * Choose a provider in a providerList according to his reputation
	 * @param <ReputedProvider>
	 * @param providers
	 * @param choosingStrategy 
	 * @return 
	 */
	public IProvider chooseProvider(List<ReputedProvider> reputedProviderList,
			Service service, int dataLostPercent, int choosingStrategy);

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

	/**
	 * 
	 * @return
	 */
	public User.raterType getRaterType();

	/**
	 * 
	 * @return
	 */
	public double getRatingTol();

	/**
	 * 
	 * @return
	 */
	public collusionGroup getCollusionCode();

}
