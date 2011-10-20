package edu.lognet.reputation.experiments;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.lognet.reputation.controller.Reputation;
import edu.lognet.reputation.model.Interaction;
import edu.lognet.reputation.model.beans.experience.Credibility;
import edu.lognet.reputation.model.beans.experience.Experience;
import edu.lognet.reputation.model.beans.service.Service;
import edu.lognet.reputation.model.beans.user.IConsumer;
import edu.lognet.reputation.model.beans.user.IProvider;
import edu.lognet.reputation.model.beans.user.IRater;
import edu.lognet.reputation.model.beans.user.User;

/**
 * @author lvanni
 */

public class Experiment1 extends AbstractExperiment {

	private static double observanceTolDefault = 0.01;// by default, equal to

	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public Experiment1(int interactionNumber, int serviceNumber,
			int totalUserNumber, int goodUser, int badUser, int dataLostPercent, int choosingStrategy) {
		super(interactionNumber, serviceNumber, totalUserNumber, goodUser,
				badUser, dataLostPercent, choosingStrategy);
	}

	/* --------------------------------------------------------- */
	/* Implements IExperiment */
	/* --------------------------------------------------------- */
	/**
	 * Starting the Experiements
	 * 
	 * @throws IOException
	 */
	public void start() throws IOException {
		// CREATE THE RANDOM FACTOR
		Random randomGenerator = new Random();

		// CREATE THE SERVICES
		List<Service> services = getServiceSet();

		// CREATE THE USERS
		List<User> users = getUserSet(services);

		// CREATE THE RESULT FILE
		FileWriter fstream = new FileWriter("result.xls");
		BufferedWriter out = new BufferedWriter(fstream);
		out.write("ExpParameters \n");
		out.write("InteractionNum \t ServiceNum \t UserNum \t Good% \t Bad% \t DataLost%");
		out.write("\n"+getInteractionNumber()+"\t"+getServiceSet().size()+"\t"+getUserNumber()+"\t"+getGoodUser()+"\t"+getBadUser()+"\t"+getDataLostPercent());
				
		// Launch the interaction set
		List<Interaction> interactions = new ArrayList<Interaction>();
		for (int i = 0; i < getInteractionNumber(); i++) {
			if (AbstractExperiment.LOG_ENABLED == 1) {
				System.out.println("\nINFO: BEGIN INTRACTION " + (i + 1) + "/"
						+ getInteractionNumber());
			}
			// CHOOSE A RANDOM CONSUMER (AN USER WHO CONSUME A SERVICE)
			IConsumer consumer = users
					.get(randomGenerator.nextInt(users.size()));/*
																 * get a random
																 * number in [0,
																 * size)
																 */

			// CHOOSE A RANDOM SERVICE
			Service service = services.get(randomGenerator.nextInt(services
					.size()));

			// GET THE PROVIDER LIST OF THIS SERVICE
			List<IProvider> providers = service.getProviders();
			// exclude consumer from the list
			for (IProvider provider : providers) {
				if (provider == consumer) {
					providers.remove(provider);
				}
			}

			// THE CONSUMER CHOOSE A PROVIDER
			Map<IRater, Credibility> raterListOfChosenProvider = new HashMap<IRater, Credibility>();
			IProvider chosenProvider = consumer.chooseProvider(providers,
					service, getDataLostPercent(), raterListOfChosenProvider, getChoosingStrategy());

			if (chosenProvider != null) {
				if (AbstractExperiment.LOG_ENABLED == 1) {
					System.out.println("INFO: " + ((User) consumer).getName()
							+ " choose provider "
							+ ((User) chosenProvider).getName());
				}
				// COMPUTE RATING DATA LOST
				List<IRater> raters = service.getRaters(chosenProvider);
				// exclude consumer from the list
				for (IRater rater : raters) {
					if (rater == consumer) {
						raters.remove(rater);
					}
				}
				double db = raters.size();
				double dataLost = 0;
				if (db != 0) {
					dataLost = 1 - raterListOfChosenProvider.size() / db;
				}
				// ADJUST THE CONSUMER EXPERIENCE INCLUDING INFO & RATING(FB)
				double perEval = generatePerEval(chosenProvider,
						observanceTolDefault);
				double feedback = generateFeedback(consumer, chosenProvider,
						perEval);
				Experience oldExp = consumer.getConsumerExp(chosenProvider,
						service);
				Experience newExp = new Experience(feedback, perEval,
						chosenProvider.getReputedScore());
				Experience currentExp = Reputation.adjustExperience(oldExp,
						newExp);
				consumer.setConsumerExp(chosenProvider, service, currentExp);

				// ADJUST CREDIBILITY is included in IConsumer.chooseProvider()
				// but haven't been set
				// So set adjusted Cred and update useful factors here
				Reputation.updateUsefulFactor(consumer, service,
						chosenProvider, raterListOfChosenProvider);

				// UPDATE THE INTERACTION LIST
				Interaction interaction = new Interaction(chosenProvider,
						consumer, service, currentExp.getFeedback(),
						currentExp.getPerEval(), currentExp.getPreRepScore(),
						dataLost);
				interactions.add(interaction);

				// UPDATE THE RATER LIST IF CONSUMER NOT IN
				boolean exist = false;
				for (IRater rater : service.getRaters(chosenProvider)) {
					if (rater == consumer) {
						exist = true;
					}
				}
				if (!exist) {
					service.addRater(chosenProvider, (IRater) consumer);
				}
				System.out.print("\n interaction: " + interaction + "\n");
			}
			
			if (AbstractExperiment.LOG_ENABLED == 1) {
				System.out.println("INFO: END INTERACTION " + (i + 1) + "/"
						+ getInteractionNumber());
			}
		}

		// Process UserList & InteractionList to get data for graph
		int[] numberOfUser = new int[10];
		int[] numberChosenAsProvider = new int[10];
		int[] numberActAsConsumer = new int[10];
		double[] sumDifferenceOverConsumer = new double[10];
		int[] numberSatisfiedOverConsumer = new int[10];
		int[] numberUnSatisfiedOverConsumer = new int[10];
		double sumDataLost = 0;
		int[] percentProvision = new int[10];// G1
		double[] averageDifference = new double[10];// G2
		int[] percentSatisfied = new int[10];// G3
		int[] percentUnsatisfied = new int[10];// G3
		double dataLost, temp;
		int numLoseAll = 0;
		// initiate values
		for (int i = 0; i < 10; i++) {
			numberOfUser[i] = 0;
			numberChosenAsProvider[i] = 0;
			numberActAsConsumer[i] = 0;
			numberSatisfiedOverConsumer[i] = 0;
			numberUnSatisfiedOverConsumer[i] = 0;
			sumDifferenceOverConsumer[i] = 0;
		}
		for (User user : users) {
			// QoS has two decimal digits
			temp = user.getQoS();
			if (temp < 0.1) {
				numberOfUser[0]++;
			} else if (temp < 0.2) {
				numberOfUser[1]++;
			} else if (temp < 0.3) {
				numberOfUser[2]++;
			} else if (temp < 0.4) {
				numberOfUser[3]++;
			} else if (temp < 0.5) {
				numberOfUser[4]++;
			} else if (temp < 0.6) {
				numberOfUser[5]++;
			} else if (temp < 0.7) {
				numberOfUser[6]++;
			} else if (temp < 0.8) {
				numberOfUser[7]++;
			} else if (temp < 0.9) {
				numberOfUser[8]++;
			} else if (temp < 1) {
				numberOfUser[9]++;
			}
		}
		for (Interaction interaction : interactions) {
			// for dataLost
			temp = interaction.getDataLost();
			if (temp == 1) {
				numLoseAll++;
			}
			sumDataLost = sumDataLost + temp;
			// for numProvision
			temp = interaction.getProvider().getQoS();
			if (temp < 0.1) {
				numberChosenAsProvider[0]++;
			} else if (temp < 0.2) {
				numberChosenAsProvider[1]++;
			} else if (temp < 0.3) {
				numberChosenAsProvider[2]++;
			} else if (temp < 0.4) {
				numberChosenAsProvider[3]++;
			} else if (temp < 0.5) {
				numberChosenAsProvider[4]++;
			} else if (temp < 0.6) {
				numberChosenAsProvider[5]++;
			} else if (temp < 0.7) {
				numberChosenAsProvider[6]++;
			} else if (temp < 0.8) {
				numberChosenAsProvider[7]++;
			} else if (temp < 0.9) {
				numberChosenAsProvider[8]++;
			} else if (temp < 1) {
				numberChosenAsProvider[9]++;
			}
			// for averageDifference & percentSatisfied
			temp = ((IProvider) interaction.getConsumer()).getQoS();
			if (temp < 0.1) {
				numberActAsConsumer[0]++;
				sumDifferenceOverConsumer[0] = sumDifferenceOverConsumer[0]
						+ Math.abs(interaction.getPerEval()
								- interaction.getEstimatedScore());
				if (interaction.getPerEval() > 0.5) {
					numberSatisfiedOverConsumer[0]++;
				} else
					numberUnSatisfiedOverConsumer[0]++;
			} else if (temp < 0.2) {
				numberActAsConsumer[1]++;
				sumDifferenceOverConsumer[1] = sumDifferenceOverConsumer[1]
						+ Math.abs(interaction.getPerEval()
								- interaction.getEstimatedScore());
				if (interaction.getPerEval() > 0.5) {
					numberSatisfiedOverConsumer[1]++;
				} else
					numberUnSatisfiedOverConsumer[1]++;
			} else if (temp < 0.3) {
				numberActAsConsumer[2]++;
				sumDifferenceOverConsumer[2] = sumDifferenceOverConsumer[2]
						+ Math.abs(interaction.getPerEval()
								- interaction.getEstimatedScore());
				if (interaction.getPerEval() > 0.5) {
					numberSatisfiedOverConsumer[2]++;
				} else
					numberUnSatisfiedOverConsumer[2]++;
			} else if (temp < 0.4) {
				numberActAsConsumer[3]++;
				sumDifferenceOverConsumer[3] = sumDifferenceOverConsumer[3]
						+ Math.abs(interaction.getPerEval()
								- interaction.getEstimatedScore());
				if (interaction.getPerEval() > 0.5) {
					numberSatisfiedOverConsumer[3]++;
				} else
					numberUnSatisfiedOverConsumer[3]++;
			} else if (temp < 0.5) {
				numberActAsConsumer[4]++;
				sumDifferenceOverConsumer[4] = sumDifferenceOverConsumer[4]
						+ Math.abs(interaction.getPerEval()
								- interaction.getEstimatedScore());
				if (interaction.getPerEval() > 0.5) {
					numberSatisfiedOverConsumer[4]++;
				} else
					numberUnSatisfiedOverConsumer[4]++;
			} else if (temp < 0.6) {
				numberActAsConsumer[5]++;
				sumDifferenceOverConsumer[5] = sumDifferenceOverConsumer[5]
						+ Math.abs(interaction.getPerEval()
								- interaction.getEstimatedScore());
				if (interaction.getPerEval() > 0.5) {
					numberSatisfiedOverConsumer[5]++;
				} else
					numberUnSatisfiedOverConsumer[5]++;
			} else if (temp < 0.7) {
				numberActAsConsumer[6]++;
				sumDifferenceOverConsumer[6] = sumDifferenceOverConsumer[6]
						+ Math.abs(interaction.getPerEval()
								- interaction.getEstimatedScore());
				if (interaction.getPerEval() > 0.5) {
					numberSatisfiedOverConsumer[6]++;
				} else
					numberUnSatisfiedOverConsumer[6]++;
			} else if (temp < 0.8) {
				numberActAsConsumer[7]++;
				sumDifferenceOverConsumer[7] = sumDifferenceOverConsumer[7]
						+ Math.abs(interaction.getPerEval()
								- interaction.getEstimatedScore());
				if (interaction.getPerEval() > 0.5) {
					numberSatisfiedOverConsumer[7]++;
				} else
					numberUnSatisfiedOverConsumer[7]++;
			} else if (temp < 0.9) {
				numberActAsConsumer[8]++;
				sumDifferenceOverConsumer[8] = sumDifferenceOverConsumer[8]
						+ Math.abs(interaction.getPerEval()
								- interaction.getEstimatedScore());
				if (interaction.getPerEval() > 0.5) {
					numberSatisfiedOverConsumer[8]++;
				} else
					numberUnSatisfiedOverConsumer[8]++;
			} else if (temp < 1) {
				numberActAsConsumer[9]++;
				sumDifferenceOverConsumer[9] = sumDifferenceOverConsumer[9]
						+ Math.abs(interaction.getPerEval()
								- interaction.getEstimatedScore());
				if (interaction.getPerEval() > 0.5) {
					numberSatisfiedOverConsumer[9]++;
				} else
					numberUnSatisfiedOverConsumer[9]++;
			}
			for (int i = 0; i < 10; i++) {
				if (numberActAsConsumer[i] != 0) {
					averageDifference[i] = sumDifferenceOverConsumer[i] * 100
							/ numberActAsConsumer[i];
					percentSatisfied[i] = numberSatisfiedOverConsumer[i]
							* 100
							/ (numberSatisfiedOverConsumer[i] + numberUnSatisfiedOverConsumer[i]);
					percentUnsatisfied[i] = 100 - percentSatisfied[i];

				}
			}
		}
		// DataLost
		dataLost = sumDataLost * 100 / (double) interactions.size();
		out.write("\nPercentage data lost: \t" + dataLost + "\nnumLoseAll = "
				+ numLoseAll);
		// percentProvision
		for (int i = 0; i < 10; i++) {
			percentProvision[i] = numberChosenAsProvider[i] * 100
					/ interactions.size();
		}
		out.write("\nQoS as provider(<) \t NumUser \t NumActualProvision \t NumActualConsuming \t PercentProvision \t AverageDiff(in percent) as consumer \t PercentSatisfied \t PercentUnsatisfied");
		for (int i = 0; i < 10; i++) {
			out.write("\n" + 0.1 * (i + 1) + "\t" + numberOfUser[i] + "\t"
					+ numberChosenAsProvider[i] + "\t" + numberActAsConsumer[i]
					+ "\t" + percentProvision[i] + "\t" + averageDifference[i]
					+ "\t" + percentSatisfied[i] + "\t" + percentUnsatisfied[i]);
		}
		out.close();

		// PRINT ALL THE INTERACTION
		if (AbstractExperiment.LOG_ENABLED == 1) {
			System.out.println("INFO: Interaction List");
			System.out.println("\tService\t\tProvider\tConsumer\tFeedback");
			/*
			 * for(Interaction interaction : interactions){
			 * System.out.println(interaction); }
			 */
		}
	}

	private double generateFeedback(IConsumer consumer, IProvider provider,
			double perEval) {
		if (consumer.getMyRaterType() == User.raterType.HONEST) {
			return perEval;
		}
		double rating;
		Random randomGenerator = new Random();
		if (consumer.getMyRaterType() == User.raterType.DISHONEST) {
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
		} else if (consumer.getMyRaterType() == User.raterType.RANDOM) {// ratingTol=1
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
			return rating;
		}
	}

	private double generatePerEval(IProvider provider,
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
