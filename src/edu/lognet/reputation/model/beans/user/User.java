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
	public static enum providerType {
		GOOD,
		GOODTURNSBAD,
		BADTURNSGOOD,
		FLUCTUATE,
		BAD,
		NORMAL
	}
	public static enum raterType {
		HONEST,
		DISHONEST,
		RANDOM,
		COLLUSIVE
	}
	public static enum collusionGroup {
		C1,
		C2,
		C3
	}
	public static enum victimGroup {
		V1,
		V2,
		V3
	}
	/* --------------------------------------------------------- */
	/* Attributes */
	/* --------------------------------------------------------- */
	private Service currentService;
	private double QoS;
	private Map<Service, Map<IProvider, Experience>> experiences;
	private Map<Service, Map<IRater, Credibility>> credibilityOfRater;
	private double reputedScore;//used only once, temporary to bring out ReputedProvider
	private providerType myProviderType;
	private raterType myRaterType;
	private collusionGroup collusionCode;
	private victimGroup victimCode;
	private Integer resourceAvail;///////
	private double initialQoS;///////////
	private double ratingTol;

	//Thao added
	//Experience includes rating, besides perEval & Timestamp
	//Credibility includes raters' info: cred, usefulnessFactors, and numberSubmissions
	//Thao end

	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public User(String id, String name, int age, Service currentService,
			double QoS, providerType pType, raterType rType, collusionGroup cGroup, victimGroup vGroup) {
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
		this.resourceAvail = 0;
		this.initialQoS = QoS;
		if (rType.compareTo(raterType.HONEST)==0) {
			this.ratingTol = 0.0;
		} else if (rType.compareTo(raterType.RANDOM)==0) {
			this.ratingTol = 1.0;//difference is any random number [0,1)
		} else {//DISHONEST and COLLUSIVE
			this.ratingTol = 0.5;
		} 
		
	}

	public User(String id, String name, int age, Service service, providerType pType, double qoS) {
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
		this.resourceAvail = 0;
		this.initialQoS = qoS;
		ratingTol = 0.0;//don't know raterType, so be HONEST		
	}

	/* --------------------------------------------------------- */
	/* Implements IConsumer */
	/* --------------------------------------------------------- */
	@Override
	public Map<IRater, Credibility> getCredibilityOfRater(Service service) {
		Map<IRater, Credibility> cor;
		if ((cor = credibilityOfRater.get(service)) == null) { // init the table not Cred for each rater
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
	public IProvider chooseProvider(List<IProvider> providers, Service service, int dataLostPercent, Map<IRater, Credibility> acceptedRaterSet, int choosingStrategy) {
		//acceptedRaterSet is used to store raterSet for the chosen provider
		if (AbstractExperiment.LOG_ENABLED == 1) {
			System.out.println("INFO: " + getName() + ".chooseProvider("
					+ service.getName() + ")");
			System.out.println("INFO: Provider list:");
			System.out
			.println("\tId\t\tName\t\tAge\t\tService\t\tQoS\t\tReputation\tStatistic");
		}
		IProvider chosenProvider;
		if (providers.size() != 0) {
			// GET ALL THE REPUTATION OF THE PROVIDERS - THEN CREATE THE
			// ReputedProvider List
			List<ReputedProvider> reputedProviderList = new ArrayList<ReputedProvider>();
			//tempRaterSetTable to get short list of raters for all providers, so that keep the list for the chosen provider
			//raters is temporary variable for short list of raters for a provider
			Map<IProvider, Map<IRater, Credibility>> tempRaterSetTable = new HashMap<IProvider, Map<IRater, Credibility>>();
			for (IProvider provider : providers) {
				//raters always != null, has been initiated
				List<IRater> raters = service.getRaters(provider);
				// DATA LOST PERCENT
				System.out.print("\n"+provider + "\n" + "Rater before: " + raters);
				for(IRater rater : raters) {
					//Thao added
					//Remove me from rater list
					//Math.random() = 0.7900395454653136 example
					if((rater == this)||(Math.random()*100 < dataLostPercent)){
						raters.remove(rater);
					}				
					//Thao end. 0<random()<1?
					//Thao commented
					/*
					if(Math.random()*100 < dataLostPercent){
						raters.remove(rater);
					}
					*/
				}
				System.out.print("\n"+"Rater after: " + raters);
				tempRaterSetTable.put(provider, new HashMap<IRater, Credibility>());
				//Thao added
				//adjustCred() is already included in getRep() and cor here is old cor, should not be touched.
				//Thao end
				//cor always != null but can be an empty table, consumerOldExp can = null
				Reputation repCalculator = new Reputation();
				//tempRaterSetTable has been initiated for this provider
				double reputation = repCalculator.getReputation(service, provider, raters, (IConsumer) this, tempRaterSetTable);
				
				//cor is still the same, new creds is stored in tempRaterSetTable
				reputedProviderList.add(new ReputedProvider(provider,
						reputation));
			}
			// SORT THE ReputedProvider LIST into ascending order, equal relation is kept
			Collections.sort(reputedProviderList);
			System.out.print("\n"+reputedProviderList+"\n");
			
			double db;
			int size = reputedProviderList.size();//size>0
			switch (choosingStrategy) {
			case 1: {
				//choose highest
				db = reputedProviderList.get(size-1).getReputation();
				chosenProvider =  reputedProviderList.get(size-1).getProvider();
				break;
			}
			case 2: {
				//crop the provider list first before applying Gaussian choosing
				int index = 0;
				//repScore of highest provider
				db = reputedProviderList.get(size-1).getReputation();
				while (index < size) {
					double temp = reputedProviderList.get(index).getReputation();
					if (Math.abs(db-temp)>0.5) {
						index++;
					} else break;
				}
				//take the list from index up to (size-1)
				List<ReputedProvider> croppedProvList = new ArrayList<ReputedProvider>();
				for (int i=index; i<size; i++) {
					croppedProvList.add(reputedProviderList.get(i));//not really copy, just referring	
				}
				
				//apply Gaussian to croppedProvList
				size = croppedProvList.size();
				System.out.print("\n"+croppedProvList+"\n");
				
				// UPDATE THE STATISTIC FACTOR OF EACH PROVIDER
				Gaussian gaussian = new Gaussian(Math.sqrt(size) / 2, 0.0);
				double[] d = new double[size];
				double sum = 0.0;
				for (int x = 0; x < size; x++) {
					d[x] = gaussian.getY(x-size+1) * 100;//take the left side of Gaussian distribution because provider list is ascending
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

				// LOG
				if (AbstractExperiment.LOG_ENABLED == 1) {
					for (ReputedProvider reputedProvider : croppedProvList) {
						System.out.println(reputedProvider);
					}
				}

				// CHOOSE THE PROVIDER
				double statisticFactor = Math.random() * 100;
				// Dichotomic search: find provider having StaFactor closest to the random value
				int i = size / 2;
				//size /= 2;
				while (size != 0) {
					if (croppedProvList.get(i).getStatisticFactor() == statisticFactor) {
						//Thao test
						System.out.print("\n equal random");
						//Thao end
						break;
					} else if (croppedProvList.get(i).getStatisticFactor() < statisticFactor) {
						//size is odd
						if (size % 2 == 1) {
							i += size / 4 + 1;
						} else {
							i += size/4;
						}
					} else {
						i -= size / 4;
					}
					size /= 2;
				}
				if (AbstractExperiment.LOG_ENABLED == 1) {
					System.out.println("INFO: "
							+ getName()
							+ " has choosen "
							+ ((User) croppedProvList.get(i).getProvider())
							.getName());
				}
				db = croppedProvList.get(i).getReputation();
				chosenProvider =  croppedProvList.get(i).getProvider();
				break;
			}
			case 3: {
				// UPDATE THE STATISTIC FACTOR OF EACH PROVIDER
				Gaussian gaussian = new Gaussian(Math.sqrt(size) / 2, 0.0);
				double[] d = new double[size];
				double sum = 0.0;
				for (int x = 0; x < size; x++) {
					d[x] = gaussian.getY(x-size+1) * 100;//take the left side of Gaussian distribution because provider list is ascending
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
				// Dichotomic search: find provider having StaFactor closest to the random value
				int i = size / 2;
				//size /= 2;
				while (size != 0) {
					if (reputedProviderList.get(i).getStatisticFactor() == statisticFactor) {
						//Thao test
						System.out.print("\n equal random");
						//Thao end
						break;
					} else if (reputedProviderList.get(i).getStatisticFactor() < statisticFactor) {
						//size is odd
						if (size % 2 == 1) {
							i += size / 4 + 1;
						} else {
							i += size/4;
						}
					} else {
						i -= size / 4;
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
				db = reputedProviderList.get(i).getReputation();
				chosenProvider =  reputedProviderList.get(i).getProvider();
				break;
			}
			default: {
				//choose highest
				db = reputedProviderList.get(size-1).getReputation();
				chosenProvider =  reputedProviderList.get(size-1).getProvider();
				break;
			}			
			}
			chosenProvider.setReputedScore(db);//bring out the functions of ReputedProvider to IProvider
			acceptedRaterSet.putAll(tempRaterSetTable.get(chosenProvider));			
		} else {
			if (AbstractExperiment.LOG_ENABLED == 1) {
				System.out
				.println("INFO: no provider for " + service.getName());
			}
			chosenProvider = null;
			acceptedRaterSet = null;
		}		
		return chosenProvider;
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
	
}
