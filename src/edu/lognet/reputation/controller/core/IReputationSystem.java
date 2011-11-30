package edu.lognet.reputation.controller.core;

import java.util.List;
import java.util.Map;

import edu.lognet.reputation.model.experience.Credibility;
import edu.lognet.reputation.model.service.Service;
import edu.lognet.reputation.model.user.IConsumer;
import edu.lognet.reputation.model.user.IProvider;
import edu.lognet.reputation.model.user.IRater;

public interface IReputationSystem {

	/**
	 * 
	 * @param service
	 * @param provider
	 * @param raters
	 * @param consumer
	 * @param credibilityOfRaterMap
	 * @return
	 */
	public double getReputation(Service service, IProvider provider,
			List<IRater> raters, IConsumer consumer,
			Map<IProvider, Map<IRater, Credibility>> credibilityOfRaterMap);

	/**
	 * 
	 * @param consumer
	 * @param service
	 * @param provider
	 * @param credibilityOfRater
	 */
	public void updateUsefulFactor(IConsumer consumer, Service service,
			IProvider provider, Map<IRater, Credibility> credibilityOfRater);

	/**
	 * 
	 * @param consumer
	 * @param provider
	 * @param perEval
	 * @return
	 */
	public double generateFeedback(IConsumer consumer, IProvider provider,
			double perEval);

	/**
	 * 
	 * @param provider
	 * @param observanceTolDefault2
	 * @return
	 */
	public double generatePerEval(IProvider provider,
			double observanceTolDefault2);
}
