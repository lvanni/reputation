package edu.lognet.reputation.controller.core.dev;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.lognet.reputation.controller.core.IReputationSystem;
import edu.lognet.reputation.model.experience.Credibility;
import edu.lognet.reputation.model.experience.Experience;
import edu.lognet.reputation.model.service.Service;
import edu.lognet.reputation.model.user.IConsumer;
import edu.lognet.reputation.model.user.IProvider;
import edu.lognet.reputation.model.user.IRater;
import edu.lognet.reputation.model.user.User;

/**
 * 
 * @author lvanni
 *
 */
public class ReputationSystem implements IReputationSystem {

	/* --------------------------------------------------------- */
	/* Attributes */
	/* --------------------------------------------------------- */
	private static double preRepScoreDefault=0.5;
	private static double usefulnessDefault = 0.5, credDefault = 0.5;
	private int pessiFactorDefault = 2;
	private static double usefulTolerance = 0.2;
	private Map<IRater, Double> temporalFactor = new HashMap<IRater, Double>();
	
	/* --------------------------------------------------------- */
	/* private Methods */
	/* --------------------------------------------------------- */
	/**
	 * 
	 * @param service
	 * @param provider
	 * @param raters
	 * @param consumer
	 */
	private void computeTimeEffect(Service service, IProvider provider,
			List<IRater> raters, IConsumer consumer) {
		double temp;		
		int i = 0;
		if (consumer.getConsumerExp(provider, service)!=null) {
			IRater[] raterArray = new IRater[raters.size()+1];
			for (IRater rater : raters) {
				raterArray[i] = rater;
				i++;
			}	
			raterArray[i] = (IRater) consumer;
			IRater[] sortedArray = mergeSort(raterArray, service, provider, consumer);
			for (int j = 0; j < sortedArray.length; j++) {
				temp = 1 / ((double) j + 1);
				temporalFactor.put(sortedArray[j], temp);
			}
		} else {
			IRater[] raterArray = new IRater[raters.size()];
			for (IRater rater : raters) {
				raterArray[i] = rater;
				i++;
			}			
			IRater[] sortedArray = mergeSort(raterArray, service, provider, consumer);
			for (int j = 0; j < sortedArray.length; j++) {
				temp = 1 / ((double) j + 1);
				temporalFactor.put(sortedArray[j], temp);
			}
		}		
	}

	/**
	 * 
	 * @param raterArray
	 * @param service
	 * @param provider
	 * @param consumer
	 * @return
	 */
	private IRater[] mergeSort(IRater[] raterArray, Service service,
			IProvider provider, IConsumer consumer) {
		Experience consumerOldExperience = consumer.getConsumerExp(provider, service);
		if (raterArray.length > 1) {
			int elementsInA1 = raterArray.length / 2;
			int elementsInA2 = raterArray.length - elementsInA1;
			IRater arr1[] = new IRater[elementsInA1];
			IRater arr2[] = new IRater[elementsInA2];

			for (int i = 0; i < elementsInA1; i++)
				arr1[i] = raterArray[i];

			for (int i = elementsInA1; i < elementsInA1 + elementsInA2; i++)
				arr2[i - elementsInA1] = raterArray[i];

			arr1 = mergeSort(arr1, service, provider, consumer);
			arr2 = mergeSort(arr2, service, provider, consumer);

			int i = 0, j = 0, k = 0;
			long date1, date2;

			while (arr1.length != j && arr2.length != k) {// increase j, k to
															// the length of
															// arr1 or arr2
				if (arr1[j]==consumer) {//Consumer is here
					date1 = consumerOldExperience.getLastTimeExp();
				} else {
					date1 = arr1[j].getConsumerExp(provider, service).getLastTimeExp();
				}
				if (arr2[k]==consumer) {//consumer is here
					date2 = consumerOldExperience.getLastTimeExp();
				} else {
					date2 = arr2[k].getConsumerExp(provider, service).getLastTimeExp();
				}
				if (date1 <= date2) {// (arr1[j] <= arr2[k])
					raterArray[i] = arr1[j];
					i++;
					j++;
				} else {
					raterArray[i] = arr2[k];
					i++;
					k++;
				}
			}
			// Insert the other array
			while (arr1.length != j) {
				raterArray[i] = arr1[j];
				i++;
				j++;
			}
			while (arr2.length != k) {
				raterArray[i] = arr2[k];
				i++;
				k++;
			}
		}
		return raterArray;
	}
	
	/* --------------------------------------------------------- */
	/* public Methods */
	/* --------------------------------------------------------- */
	/**
	 * 
	 * @param service
	 * @param provider
	 * @param raters
	 * @param consumer
	 * @param credibilityOfRaterMap
	 * @return
	 */
	public double getReputation(Service service, IProvider provider, List<IRater> raters, IConsumer consumer, Map<IProvider, Map<IRater, Credibility>> credibilityOfRaterMap) {
		//Thao added IConsumer		 
		//My info on this service, for this provider is in Experience
		//cor always != null but can be an empty table, should not be touched
		//consumerOldExp can = null
		//Raters already exclude me, is the short list and is included in raterSetTable
		//raterSetTable != null, the Map corresponding to the provider != null, but Creds for raters hasn't been initiated
		
		Experience consumerOldExperience = consumer.getConsumerExp(provider, service);
		Map<IRater, Credibility> credibilityOfRater = consumer.getCredibilityOfRater(service);
		if (raters.size()==0) {
			if (consumerOldExperience ==null){
				return preRepScoreDefault;
			} else
				return consumerOldExperience.getPerEval();			
		} else {
			double preRepScore;
			if (consumerOldExperience==null){
				//set preRepScore to default, and no perEval
				preRepScore = preRepScoreDefault;
			} else {
				//use preRepScore as normal
				preRepScore = consumerOldExperience.getPreRepScore();
			}
			//GET RATINGS
			//raters != null, cor always != null but can be an empty table if consumer never use ratings before
			//raterSetTable is used to store adjusted creds corresponding to each provider
			this.adjustCredibility(service, provider, raters, credibilityOfRater, preRepScore, credibilityOfRaterMap);
			//raters not include me
			this.computeTimeEffect(service, provider, raters, consumer);
			//Start computing RepScore			
			double roundedAssess = 0;
			double eval, d, cred;
			double numerator = 0, denominator = 0;
			for (IRater rater : raters) {
				cred = credibilityOfRaterMap.get(provider).get(rater).getCredibility();
				numerator = numerator + rater.getConsumerExp(provider, service).getFeedback()
							* cred * temporalFactor.get(rater);
				//RateWeb way on denominator
				//denominator = denominator + cred;
				//My way on denominator
				denominator = denominator + cred*temporalFactor.get(rater);				
			}
			if (consumerOldExperience != null) {
				eval = consumerOldExperience.getPerEval();
				//RateWeb way on denominator
				//denominator++;
				//My way on denominator
				denominator = denominator + temporalFactor.get(consumer);
				d = (numerator + eval * temporalFactor.get(consumer))
						/ (denominator);// weight on personal evaluation equals 1; Consumer is marked as null
				roundedAssess = Math.round(d * 100) / (double) 100;// to take 2 decimal
															// digits only			
			} else {
				d = numerator / denominator;
				roundedAssess = Math.round(d * 100) / (double) 100;// to take 2 decimal
															// digits only
				}
			return roundedAssess;		
			}	
		}
	
	/**
	 * Return the reputation of a 
	 * @param raters
	 * @param consumerOldExperience
	 * @param cor
	 * @return
	 */
	public static Experience adjustExperience(Experience oldExp, Experience newExp) {
		return newExp;
	}
	
	/**
	 * 
	 * @param provider 
	 * @param service 
	 * @param raters
	 * @param credibilityOfRater
	 * @param raterSetTable 
	 * @param experience
	 * @return
	 */
	public void adjustCredibility(Service service, IProvider provider, List<IRater> raters, Map<IRater, Credibility> credibilityOfRater, double preRepScore, Map<IProvider, Map<IRater, Credibility>> raterSetTable) {
		//raters != null
		//credibilityOfRaterList(cor) always != null but can be an empty table, should not be touched
		//consumerOldExp can = null
		//Experience includes preRepScore  
		//raterSetTable has been initiated for this provider, but not for raters, used to store temporary new creds
		double deltaMajor, deltaPreAssess, normalizedFactor;
		double euclideanDis, raterCred, newRaterCred;
		double rating;
		ArrayList<Double> arrayDouble = new ArrayList<Double>();//for clustering job
		double temp;
		//raterList already excludes consumer from rating set, so just add
		for (IRater rater : raters) {
			arrayDouble.add(rater.getConsumerExp(provider, service).getFeedback());
		}
		ClusteredData clusteredData = null;
		if (arrayDouble.size() > 0) {
			clusteredData = new ClusteredData(arrayDouble);
		}
		//cor.get(rater) can = null
		for (IRater rater : raters) {// adjust raters' credibility
			if (credibilityOfRater.get(rater)==null){
				raterCred = credDefault;
			} else {
				raterCred = credibilityOfRater.get(rater).getCredibility();
			}
			rating = rater.getConsumerExp(provider, service).getFeedback();
			euclideanDis = Math.abs(clusteredData.getMajorMean()
					- rating);
			if (euclideanDis < clusteredData.getStandardDeviation()) {
				deltaMajor = 1 - euclideanDis
						/ clusteredData.getStandardDeviation();
			} else {
				if (euclideanDis != (double) 0) {
					deltaMajor = 1 - clusteredData.getStandardDeviation()
							/ euclideanDis;
				} else {
					deltaMajor = (double) 1;
				}
			}
			deltaPreAssess = (double) 1;// Because use only 1 value of
										// previous reputation score, not
										// taking into account of time
			normalizedFactor = raterCred
					* (1 - Math.abs(rating
							- clusteredData.getMajorMean()));
			double discrepancy1, discrepancy2;
			discrepancy1 = Math.abs(rating
					- clusteredData.getMajorMean());// =euclideanDis
			discrepancy2 = Math.abs(rating
					- preRepScore);
			if (discrepancy1 < 0.1) {// two values are similar: rating is
										// similar to MajorMean
				if (discrepancy2 < 0.1) {// rating is similar to
											// preAssessedReps
					temp = raterCred
							+ normalizedFactor
							* (Math.abs(deltaMajor + deltaPreAssess) / (double) pessiFactorDefault);
					newRaterCred = Math.min((double) 1, temp);
				} else {
					temp = raterCred + normalizedFactor
							* (deltaMajor / (double) pessiFactorDefault);
					newRaterCred = Math.min((double) 1, temp);
				}
			} else {
				if (discrepancy2 < 0.1) {
					temp = raterCred - normalizedFactor
							* (deltaPreAssess / (double) pessiFactorDefault);
					newRaterCred = Math.max((double) 0, temp);
				} else {
					temp = raterCred
							- normalizedFactor
							* (Math.abs(deltaMajor + deltaPreAssess) / (double) pessiFactorDefault);
					newRaterCred = Math.max((double) 0, temp);
				}
			}
			if (credibilityOfRater.get(rater)==null) {
				newRaterCred = newRaterCred * usefulnessDefault;
			} else {
				newRaterCred = newRaterCred * credibilityOfRater.get(rater).getUsefulnessFactor();
			}
			newRaterCred = Math.round(newRaterCred * 100) / (double) 100;
			raterSetTable.get(provider).put(rater, new Credibility(newRaterCred));
			}
		}

	/**
	 * 
	 * @param consumer
	 * @param service
	 * @param provider
	 * @param credibilityOfRater
	 */
	public void updateUsefulFactor(IConsumer consumer, Service service, IProvider provider, Map<IRater, Credibility> credibilityOfRater) {
		// to update useful factors and number submission only
		Map<IRater, Credibility> credibilityOfRaterToUpdate = consumer.getCredibilityOfRater(service);//old creds
		//this rater set can be bigger or smaller than raterList
		//cor always != null but can be an empty table, consumerOldExp can = null		
		double correct = consumer.getConsumerExp(provider, service).getPerEval();
		double dif, useful, newuseful, credValue;
		int number; 
		for (IRater rater : credibilityOfRater.keySet()) {//rater always different from me customer
			dif = Math.abs(correct
					- rater.getConsumerExp(provider, service).getFeedback());
			if (credibilityOfRaterToUpdate.get(rater)!=null) {
				useful = credibilityOfRaterToUpdate.get(rater).getUsefulnessFactor();
				number = credibilityOfRaterToUpdate.get(rater).getNumberSubmission();					
			} else {
				useful = usefulnessDefault;
				number = 0;
			}			
			// update usefulnessFactors and numberSubmissions
			if (dif > usefulTolerance) {//rating not useful
				newuseful = (useful * number) / (double) (number + 1);
				newuseful = Math.round(newuseful * 100) / (double) 100;// to
																		// get
																		// only
																		// 2
																		// decimal
																		// digits				
			} else {//rating is useful
				newuseful = (useful * number + 1) / (double) (number + 1);
				newuseful = Math.round(newuseful * 100) / (double) 100;// to
																		// get
																		// only
																		// 2
																		// decimal
																		// digits
			}
			number++;
			credValue = credibilityOfRater.get(rater).getCredibility();
			if (credibilityOfRaterToUpdate.get(rater)!=null) {//exist old cred
				credibilityOfRaterToUpdate.get(rater).setCredibility(credValue);
				credibilityOfRaterToUpdate.get(rater).setUsefulnessFactor(newuseful);
				credibilityOfRaterToUpdate.get(rater).setNumberSubmissions(number);
			} else {
				Credibility credObject = new Credibility(credValue, newuseful, number, consumer, rater, service); 
				credibilityOfRaterToUpdate.put(rater, credObject);
			}
		}
	}

	
	/**
	 * 
	 * @param consumer
	 * @param provider
	 * @param perEval
	 * @return
	 */
	public double generateFeedback(IConsumer consumer, IProvider provider,
			double perEval) {
		if (consumer.getRaterType() == User.raterType.HONEST) {
			return perEval;
		}
		double rating;
		Random randomGenerator = new Random();
		if (consumer.getRaterType() == User.raterType.DISHONEST) {//ratingTol=0.5
			if (perEval > 0.7) {// the provider is supposed to be GOOD
				rating = Math.max(
						(double) 0,
						Math.round(perEval * 100 - consumer.getRatingTol()
								* 100)
								/ (double) 100);
			} else if (perEval < 0.4) {// BAD provider
				rating = Math.min(
						(double) 1,
						Math.round(perEval * 100 + consumer.getRatingTol()
								* 100)
								/ (double) 100);
			} else if (randomGenerator.nextBoolean()) {// mean plus for NORMAL
														// provider
				rating = Math.min(
						(double) 1,
						Math.round(perEval * 100 + consumer.getRatingTol()
								* 100)
								/ (double) 100);
			} else {// mean minus for NORMAL provider
				rating = Math.max(
						(double) 0,
						Math.round(perEval * 100 - consumer.getRatingTol()
								* 100)
								/ (double) 100);
			}
			return rating;
		} else if (consumer.getRaterType() == User.raterType.RANDOM) {// ratingTol=1
			if (randomGenerator.nextBoolean()) {// mean plus
				rating = Math.min(
						(double) 1,
						Math.round(perEval
								* 100
								+ randomGenerator.nextInt((int) (consumer
										.getRatingTol() * 100)))
								/ (double) 100);
			} else {// minus
				rating = Math.max(
						(double) 0,
						Math.round(perEval
								* 100
								- randomGenerator.nextInt((int) (consumer
										.getRatingTol() * 100)))
								/ (double) 100);
			}
			return rating;
		} else {// =raterType.COLLUSIVE
			if (consumer.getCollusionCode() == provider.getCollusionCode()) {
				rating = 1;// give the highest rating to my collusion
				return rating;
			} 
			//bad rating for all the others
			else {
				rating = 0;
			}
			
			/*Not implement victimCode
			if (provider.getVictimCode() == null) {// not my victim, then be
													// honest
				rating = perEval;
				return rating;
			}
			if (consumer.getCollusionCode().ordinal() == provider
					.getVictimCode().ordinal()) {
				rating = 0;
			} else {// telling the truth
				rating = perEval;
			}
			*/
			return rating;
		}
	}

	/**
	 * 
	 * @param provider
	 * @param observanceTolDefault2
	 * @return
	 */
	public double generatePerEval(IProvider provider,
			double observanceTolDefault2) {
		Random randomGenerator = new Random();
		double value;
		if (randomGenerator.nextBoolean()) {
			value = Math.min(
					(double) 1,
					provider.getQoS()
							+ randomGenerator.nextInt((int) (Math
									.round(observanceTolDefault2 * 100)))
							/ (double) 100);
		} else {
			value = Math.max(
					(double) 0,
					provider.getQoS()
							- randomGenerator.nextInt((int) (Math
									.round(observanceTolDefault2 * 100)))
							/ (double) 100);
		}
		// value already has 2 decimal digits
		double eval = Math.round(value * 100) / (double) 100;
		return eval;
	}
}
