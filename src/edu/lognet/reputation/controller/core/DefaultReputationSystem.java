package edu.lognet.reputation.controller.core;

import java.util.List;
import java.util.Map;

import edu.lognet.reputation.model.experience.Credibility;
import edu.lognet.reputation.model.service.Service;
import edu.lognet.reputation.model.user.IConsumer;
import edu.lognet.reputation.model.user.IProvider;
import edu.lognet.reputation.model.user.IRater;

public class DefaultReputationSystem implements IReputationSystem {

	@Override
	public double getReputation(Service service, IProvider provider,
			List<IRater> raters, IConsumer consumer,
			Map<IProvider, Map<IRater, Credibility>> credibilityOfRaterMap) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void updateUsefulFactor(IConsumer consumer, Service service,
			IProvider provider, Map<IRater, Credibility> credibilityOfRater) {
		// TODO Auto-generated method stub
	}

}
