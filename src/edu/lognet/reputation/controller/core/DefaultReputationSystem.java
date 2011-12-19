package edu.lognet.reputation.controller.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.lognet.reputation.model.experience.Credibility;
import edu.lognet.reputation.model.service.Service;
import edu.lognet.reputation.model.user.IConsumer;
import edu.lognet.reputation.model.user.IProvider;
import edu.lognet.reputation.model.user.IRater;

/**
 * Implementation of a reputation system based on the average of values
 * @author Laurent Vanni, Thao Nguyen
 *
 */
public class DefaultReputationSystem implements IReputationSystem {

	/* --------------------------------------------------------- */
	/* Attributes */
	/* --------------------------------------------------------- */
	private Map<Service, Map<IProvider, Double>> reputation;
	private static Double defaultReputationValue = 0.5;

	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public DefaultReputationSystem() {
		reputation = new HashMap<Service, Map<IProvider,Double>>();
	}
	
	/* --------------------------------------------------------- */
	/* Overrided Methods */
	/* --------------------------------------------------------- */
	@Override
	public double getReputation(Service service, IProvider provider,
			List<IRater> raters, IConsumer consumer,
			Map<IProvider, Map<IRater, Credibility>> credibilityOfRaterMap) {
		
		Map<IProvider, Double> serviceRep = reputation.get(service);
		if(serviceRep == null){
			serviceRep = new HashMap<IProvider, Double>();
			reputation.put(service, serviceRep);
		}
		Double providerRepuration = serviceRep.get(provider);
		if(providerRepuration == null) {
			providerRepuration = defaultReputationValue;
			serviceRep.put(provider, providerRepuration);
		}
		return providerRepuration;
	}

	@Override
	public void updateUsefulFactor(IConsumer consumer, Service service,
			IProvider provider, Map<IRater, Credibility> credibilityOfRater, double feedback) {
		Map<IProvider, Double> serviceRep = reputation.get(service);
		if(serviceRep == null){
			serviceRep = new HashMap<IProvider, Double>();
			reputation.put(service, serviceRep);
		}
		Double providerRepuration = serviceRep.get(provider);
		if(providerRepuration == null) {
			providerRepuration = defaultReputationValue;
		} else {
			providerRepuration = (providerRepuration + feedback) / 2;
		}
		serviceRep.put(provider, providerRepuration);
	}

}
