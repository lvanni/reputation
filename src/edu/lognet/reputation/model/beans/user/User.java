package edu.lognet.reputation.model.beans.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.lognet.reputation.controller.Gaussian;
import edu.lognet.reputation.controller.Reputation;
import edu.lognet.reputation.experiments.AbstractExperiment;
import edu.lognet.reputation.model.beans.experience.Credibility;
import edu.lognet.reputation.model.beans.experience.Experience;
import edu.lognet.reputation.model.beans.service.Service;

public class User extends AbstractUser implements IProvider, IConsumer, IRater {

	/* --------------------------------------------------------- */
	/* Attributes */
	/* --------------------------------------------------------- */
	private Service currentService;
	private double QoS;
	private Map<Service, Map<IProvider, Experience>> experiences;
	private Map<Service, Map<IRater, Credibility>> credibilityOfRater;

	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public User(String id, String name, int age, Service currentService,
			double QoS) {
		super(id, name, age);
		this.currentService = currentService;
		this.QoS = QoS;
		this.experiences = new HashMap<Service, Map<IProvider, Experience>>();
		this.credibilityOfRater = new HashMap<Service, Map<IRater, Credibility>>();
	}

	/* --------------------------------------------------------- */
	/* Implements IConsumer */
	/* --------------------------------------------------------- */
	@Override
	public Map<IRater, Credibility> getCredibilityOfRater(Service service) {
		Map<IRater, Credibility> cor;
		if ((cor = credibilityOfRater.get(service)) == null) { // init
			cor = new HashMap<IRater, Credibility>();
			this.credibilityOfRater.put(service, cor);
		}
		return cor;
	}

	@Override
	public Experience getConsumerExp(IProvider provider, Service service) {
		if (experiences.get(service) == null) {
			return null;
		} else {
			return experiences.get(service).get(provider);
		}
	}
		
	@Override
	public void setConsumerExp(IProvider provider, Service service,
			Experience experience) {
		if (experiences.get(service) == null) {
			experiences.put(service, new HashMap<IProvider, Experience>());
		} 
		experiences.get(service).put(provider, experience);
	}

	@Override
	public IProvider chooseProvider(List<IProvider> providers, Service service, int dataLostPercent) {
		if (providers.size() != 0) {
			if (AbstractExperiment.LOG_ENABLED == 1) {
				System.out.println("INFO: " + getName() + ".chooseProvider("
						+ service.getName() + ")");
				System.out.println("INFO: Provider list:");
				System.out
						.println("\tId\t\tName\t\tAge\t\tService\t\tQoS\t\tReputation\tStatistic");
			}

			// GET ALL THE REPUTATION OF THE PROVIDERS - THEN CREATE THE
			// ReputedProvider List
			List<ReputedProvider> reputedProviderList = new ArrayList<ReputedProvider>();
			for (IProvider provider : providers) {
				List<IRater> raters = service.getRaters(provider);
				// DATA LOST PERCENT
				for(IRater rater : raters) {
					if(Math.random()*100 < dataLostPercent){
						raters.remove(rater);
					}
				}
				Map<IRater, Credibility> cor = getCredibilityOfRater(service);
				Experience consumerOldExperience = getConsumerExp(provider,
						service);
				double reputation = Reputation.getReputation(raters,
						consumerOldExperience, cor);
				reputedProviderList.add(new ReputedProvider(provider,
						reputation));
			}
			// SORT THE ReputedProvider LIST
			Collections.sort(reputedProviderList);

			// UPDATE THE STATISTIC FACTOR OF EACH PROVIDER
			int size = reputedProviderList.size();
			Gaussian gaussian = new Gaussian(Math.sqrt(size) / 2, 0.0);
			double[] d = new double[size];
			double sum = 0.0;
			for (int x = 0; x < size; x++) {
				d[x] = gaussian.getY(x) * 100;
			}
			Map<Double, Double> percentMap = new HashMap<Double, Double>();
			for (int i = 0; i < size; i++) {
				sum += d[i];
			}
			double percentSum = 0.0;
			for (int i = 0; i < size; i++) {
				double percent = (100 * d[i]) / sum;
				percentMap.put(d[i], percent);
				reputedProviderList.get(i).setStatisticFeedBack(percent);
				percentSum += percent;
				reputedProviderList.get(i).setStatisticFactor(percentSum);
			}

			// LOG
			if (AbstractExperiment.LOG_ENABLED == 1) {
				for (ReputedProvider reputedProvider : reputedProviderList) {
					System.out.println(reputedProvider);
				}
			}

			// CHOOSE THE PROVIDER
			double statisticFactor = Math.random() * 100;
			// Dichotomic search
			int i = size / 2;
			size /= 2;
			while (size != 0) {
				if (reputedProviderList.get(i).getStatisticFactor() == statisticFactor) {
					break;
				} else if (reputedProviderList.get(i).getStatisticFactor() < statisticFactor) {
					i += size / 2;
				} else {
					i -= size / 2;
				}
				size /= 2;
			}
			if (AbstractExperiment.LOG_ENABLED == 1) {
				System.out.println("INFO: "
						+ getName()
						+ " has choosen "
						+ ((User) reputedProviderList.get(i).getProvider())
								.getName());
			}

			return reputedProviderList.get(i).getProvider();
		} else {
			if (AbstractExperiment.LOG_ENABLED == 1) {
				System.out
						.println("INFO: no provider for " + service.getName());
			}
		}
		return null;
	}

	/* --------------------------------------------------------- */
	/* Implements IProvider */
	/* --------------------------------------------------------- */
	/**
	 * Represent a reputed provider
	 * 
	 * @author lvanni
	 */
	private class ReputedProvider implements Comparable<ReputedProvider> {
		private IProvider provider;
		private double reputation;
		/** statistic factor used when a provider is chosen */
		private double statisticFactor;
		/** To print the percent */
		private double statisticFeedBack;

		public ReputedProvider(IProvider provider, double reputation) {
			this.provider = provider;
			this.reputation = reputation;
		}

		@Override
		public int compareTo(ReputedProvider o) {
			if (reputation == o.getReputation()) {
				return 0;
			} else if (reputation < o.getReputation()) {
				return -1;
			} else {
				return 1;
			}
		}

		@Override
		public String toString() {
			return provider.toString() + "\t\t" + reputation + "\t\t"
					+ statisticFeedBack + "%";
		}

		/* ------- GETTER & SETTER -------- */
		public double getReputation() {
			return reputation;
		}

		public IProvider getProvider() {
			return provider;
		}

		public double getStatisticFactor() {
			return statisticFactor;
		}

		public void setStatisticFactor(double statistic) {
			this.statisticFactor = statistic;
		}

		public void setStatisticFeedBack(double statisticFeedBack) {
			this.statisticFeedBack = statisticFeedBack;
		}
	}

	@Override
	public Service getProvidedService() {
		// TODO Auto-generated method stub
		return currentService;
	}

	@Override
	public void setProvidedService(Service providedService) {
		this.currentService = providedService;
	}

	@Override
	public double getQoS() {
		return QoS;
	}

	@Override
	public void setQoS(double QoS) {
		this.QoS = QoS;
	}

	/* --------------------------------------------------------- */
	/* Others Override Methods */
	/* --------------------------------------------------------- */
	@Override
	public String toString() {
		return "\t" + getId() + "\t\t" + getName() + "\t\t" + getAge() + "\t\t"
				+ getProvidedService().getId() + "\t\t" + getQoS();
	}

	@Override
	public int compareTo(IProvider o) {
		// The comparaison is based on the reputation factor
		return 0;
	}
}
