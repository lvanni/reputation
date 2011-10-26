package edu.lognet.reputation.model.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.lognet.reputation.controller.core.Gaussian;
import edu.lognet.reputation.controller.simulations.Simulation;
import edu.lognet.reputation.model.experience.Credibility;
import edu.lognet.reputation.model.experience.Experience;
import edu.lognet.reputation.model.service.Service;

public class User extends AbstractUser implements IProvider, IConsumer, IRater {

	public static enum collusionGroup {
		C1, C2, C3
	}

	public static enum victimGroup {
		V1, V2, V3
	}

	/* --------------------------------------------------------- */
	/* Attributes */
	/* --------------------------------------------------------- */
	private Service currentService;
	private double QoS;
	private Map<Service, Map<IProvider, Experience>> experiences;
	private Map<Service, Map<IRater, Credibility>> credibilityOfRater;
	private double reputedScore;
	private providerType myProviderType;
	private raterType myRaterType;
	private collusionGroup collusionCode;
	private victimGroup victimCode;
	private double ratingTol;

	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public User(String id, String name, int age, Service currentService,
			double QoS, providerType pType, raterType rType,
			collusionGroup cGroup, victimGroup vGroup) {
		super(id, name, age);
		this.currentService = currentService;
		this.QoS = QoS;
		this.experiences = new HashMap<Service, Map<IProvider, Experience>>();
		this.credibilityOfRater = new HashMap<Service, Map<IRater, Credibility>>();
		this.reputedScore = 0;
		this.myProviderType = pType;
		this.myRaterType = rType;
		this.collusionCode = cGroup;
		this.victimCode = vGroup;
		if (rType.compareTo(raterType.HONEST) == 0) {
			this.ratingTol = 0.0;
		} else if (rType.compareTo(raterType.RANDOM) == 0) {
			this.ratingTol = 1.0; // difference is any random number [0,1)
		} else { // DISHONEST and COLLUSIVE
			this.ratingTol = 0.5;
		}

	}

	public User(String id, String name, int age, Service service,
			providerType pType, double qoS) {
		super(id, name, age);
		this.currentService = service;
		this.QoS = qoS;
		this.experiences = new HashMap<Service, Map<IProvider, Experience>>();
		this.credibilityOfRater = new HashMap<Service, Map<IRater, Credibility>>();
		this.reputedScore = 0;
		this.myProviderType = pType;
		this.myRaterType = raterType.HONEST;
		this.collusionCode = null;
		this.victimCode = null;
		ratingTol = 0.0;// don't know raterType, so be HONEST
	}

	/* --------------------------------------------------------- */
	/* Implements IConsumer */
	/* --------------------------------------------------------- */
	/**
	 * @param service
	 * @return Map<IRater, Credibility>
	 */
	public Map<IRater, Credibility> getCredibilityOfRater(Service service) {
		Map<IRater, Credibility> cor;
		if ((cor = credibilityOfRater.get(service)) == null) {
			cor = new HashMap<IRater, Credibility>();
			this.credibilityOfRater.put(service, cor);
		}
		return cor;
	}

	/**
	 * @param provider
	 * @param service
	 * @return Experience
	 */
	public Experience getConsumerExp(IProvider provider, Service service) {
		if (experiences.get(service) == null) {
			return null;
		} else {
			return experiences.get(service).get(provider);
		}
	}

	/**
	 * @param provider
	 * @param service
	 */
	public void setConsumerExp(IProvider provider, Service service,
			Experience experience) {
		if (experiences.get(service) == null) {
			experiences.put(service, new HashMap<IProvider, Experience>());
		}
		experiences.get(service).put(provider, experience);
	}

	/**
	 * @param providers
	 * @param service
	 * @param dataLostPercent
	 * @param credibilityOfRater
	 * @param choosingStrategy
	 */
	public IProvider chooseProvider(List<ReputedProvider> reputedProviderList,
			Service service, int dataLostPercent, int choosingStrategy) {

		if (Simulation.LOG_ENABLED == 1) {
			System.out.println("INFO: " + getName() + ".chooseProvider("
					+ service.getName() + ")");
			System.out.println("INFO: Provider list:");
			System.out
					.println("\tId\t\tName\t\tAge\t\tService\t\tQoS\t\tReputation\tStatistic");
		}

		IProvider chosenProvider = null;
		double db;
		int size = reputedProviderList.size();

		switch (choosingStrategy) {
		case 1: { // choose highest
			db = reputedProviderList.get(size - 1).getReputation();
			chosenProvider = reputedProviderList.get(size - 1).getProvider();
			break;
		}
		case 2: { // crop the provider list first before applying Gaussian
					// choosing
			int index = 0;
			db = reputedProviderList.get(size - 1).getReputation();

			while (index < size) {
				double temp = reputedProviderList.get(index).getReputation();
				if (Math.abs(db - temp) > 0.5) {
					index++;
				} else {
					break;
				}
			}

			List<ReputedProvider> croppedProvList = new ArrayList<ReputedProvider>();
			for (int i = index; i < size; i++) {
				croppedProvList.add(reputedProviderList.get(i));
			}

			size = croppedProvList.size();
			Gaussian gaussian = new Gaussian(Math.sqrt(size) / 2, 0.0);
			double[] d = new double[size];
			double sum = 0.0;

			for (int x = 0; x < size; x++) {
				d[x] = gaussian.getY(x - size + 1) * 100;
			}

			Map<Double, Double> percentMap = new HashMap<Double, Double>();
			for (int i = 0; i < size; i++) {
				sum += d[i];
			}

			double percentSum = 0.0;
			for (int i = 0; i < size; i++) {
				double percent = (100 * d[i]) / sum;
				percentMap.put(d[i], percent);
				croppedProvList.get(i).setStatisticFeedBack(percent);
				percentSum += percent;
				croppedProvList.get(i).setStatisticFactor(percentSum);
			}

			if (Simulation.LOG_ENABLED == 1) {
				for (ReputedProvider reputedProvider : croppedProvList) {
					System.out.println(reputedProvider);
				}
			}

			double statisticFactor = Math.random() * 100;
			int i = size / 2;
			while (size != 0) {
				if (croppedProvList.get(i).getStatisticFactor() == statisticFactor) {
					break;
				} else if (croppedProvList.get(i).getStatisticFactor() < statisticFactor) {
					if (size % 2 == 1) {
						i += size / 4 + 1;
					} else {
						i += size / 4;
					}
				} else {
					i -= size / 4;
				}
				size /= 2;
			}

			if (Simulation.LOG_ENABLED == 1) {
				System.out.println("INFO: "
						+ getName()
						+ " has choosen "
						+ ((User) croppedProvList.get(i).getProvider())
								.getName());
			}

			db = croppedProvList.get(i).getReputation();
			chosenProvider = croppedProvList.get(i).getProvider();
			break;
		}

		case 3: { // UPDATE THE STATISTIC FACTOR OF EACH PROVIDER
			Gaussian gaussian = new Gaussian(Math.sqrt(size) / 2, 0.0);
			double[] d = new double[size];
			double sum = 0.0;
			for (int x = 0; x < size; x++) {
				d[x] = gaussian.getY(x - size + 1) * 100; // take the left
															// side of
															// Gaussian
															// distribution
															// because
															// provider list
															// is ascending
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

			if (Simulation.LOG_ENABLED == 1) {
				for (ReputedProvider reputedProvider : reputedProviderList) {
					System.out.println(reputedProvider);
				}
			}

			double statisticFactor = Math.random() * 100;
			int i = size / 2;
			while (size != 0) {
				if (reputedProviderList.get(i).getStatisticFactor() == statisticFactor) {
					break;
				} else if (reputedProviderList.get(i).getStatisticFactor() < statisticFactor) {
					if (size % 2 == 1) {
						i += size / 4 + 1;
					} else {
						i += size / 4;
					}
				} else {
					i -= size / 4;
				}
				size /= 2;
			}

			if (Simulation.LOG_ENABLED == 1) {
				System.out.println("INFO: "
						+ getName()
						+ " has choosen "
						+ ((User) reputedProviderList.get(i).getProvider())
								.getName());
			}

			db = reputedProviderList.get(i).getReputation();
			chosenProvider = reputedProviderList.get(i).getProvider();
			break;
		}
		default: {
			db = reputedProviderList.get(size - 1).getReputation();
			chosenProvider = reputedProviderList.get(size - 1).getProvider();
			break;
		}
		}

		chosenProvider.setReputedScore(db);// bring out the functions of
											// ReputedProvider to IProvider
		return chosenProvider;
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
	public raterType getMyRaterType() {
		return myRaterType;
	}

	@Override
	public double getRatingTol() {
		return ratingTol;
	}

	@Override
	public collusionGroup getCollusionCode() {
		return collusionCode;
	}

	@Override
	public victimGroup getVictimCode() {
		return victimCode;
	}

	public providerType getMyProviderType() {
		return myProviderType;
	}

	public void setMyProviderType(providerType myProviderType) {
		this.myProviderType = myProviderType;
	}

	@Override
	public Service getProvidedService() {
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

	@Override
	public void setReputedScore(double db) {
		reputedScore = db;
	}

	@Override
	public double getReputedScore() {
		return reputedScore;
	}
}
