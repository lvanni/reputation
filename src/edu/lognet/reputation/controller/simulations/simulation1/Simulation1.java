package edu.lognet.reputation.controller.simulations.simulation1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.lognet.reputation.controller.simulations.Simulation;
import edu.lognet.reputation.model.Interaction;
import edu.lognet.reputation.model.experience.Credibility;
import edu.lognet.reputation.model.experience.Experience;
import edu.lognet.reputation.model.service.Service;
import edu.lognet.reputation.model.user.IConsumer;
import edu.lognet.reputation.model.user.IProvider;
import edu.lognet.reputation.model.user.IProvider.providerType;
import edu.lognet.reputation.model.user.IRater;
import edu.lognet.reputation.model.user.IRater.raterType;
import edu.lognet.reputation.model.user.ReputedProvider;
import edu.lognet.reputation.model.user.User;

/**
 * Represent the first type of simulation:
 * Allow to extract datas of each interation for plotting
 * @author Laurent Vanni, Thao Nguyen
 */
public class Simulation1 extends Simulation {

	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public Simulation1(int interactionNumber, int serviceNumber,
			int totalUserNumber, int goodUser, int goodTurnBadUser,
			int fluctuateUser, int frequencyOfFluctuation, int badUser,
			int badTurnGoodUser, int honestRater, int dishonestRater,
			int randomRater, int collusiveGroups, int resourceAvailable,
			int dataLost, int choosingStrategy) {
		super(interactionNumber, serviceNumber, totalUserNumber, goodUser,
				goodTurnBadUser, fluctuateUser, frequencyOfFluctuation,
				badUser, badTurnGoodUser, honestRater, dishonestRater,
				randomRater, collusiveGroups, resourceAvailable, dataLost,
				choosingStrategy);
	}

	/* --------------------------------------------------------- */
	/* extends AbstractExperiment */
	/* --------------------------------------------------------- */
	/**
	 * @see Simulation#setup()
	 */
	protected void setup() throws IOException {
		// CREATE THE SERVICES
		services = getServiceSet();

		// CREATE THE USERS
		users = getUserSet(services);
	}

	/**
	 * @see Simulation#createInteraction(int i)
	 */
	protected Interaction createInteraction(int i) {
		Interaction interaction = null;

		// CREATE THE RANDOM FACTOR
		Random randomGenerator = new Random();

		if (Simulation.LOG_ENABLED == 1) {
			System.out.println("\nINFO: BEGIN INTRACTION " + (i + 1) + "/"
					+ getInteractionNumber());
		}

		// CHOOSE A RANDOM CONSUMER (AN USER WHO CONSUME A SERVICE)
		IConsumer consumer = users.get(randomGenerator.nextInt(users.size()));

		// CHOOSE A RANDOM SERVICE
		Service service = services
				.get(randomGenerator.nextInt(services.size()));

		// GET THE PROVIDER LIST OF THIS SERVICE
		List<IProvider> providers = service.getProviders();
		// exclude consumer and providers running out of resource from the list
		for (IProvider provider : providers) {
			if ((provider == consumer)
					|| (provider.getNumProvision() == provider
							.getResourceAvailable())) {
				providers.remove(provider);
			}
		}

		if (providers.size() == 0) { // NO PROVIDER FOR THIS SERVICE
			if (Simulation.LOG_ENABLED == 1) {
				System.out.println("No provider for the service" + service);
			}
			return null;
		}
		
		// THE CONSUMER CHOOSE A PROVIDER (according to the strategy choosen)
		Map<IRater, Credibility> credibilityOfRatersForChosenProvider = new HashMap<IRater, Credibility>();
		Map<IProvider, Map<IRater, Credibility>> credibilityOfRaterMap = new HashMap<IProvider, Map<IRater, Credibility>>();

		List<ReputedProvider> reputedProvider = getReputedProviderList(
				consumer, providers, service, credibilityOfRaterMap);
		IProvider chosenProvider = consumer.chooseProvider(reputedProvider,
				service, getDataLostPercent(), getChosenStrategy());

		credibilityOfRatersForChosenProvider.putAll(credibilityOfRaterMap
				.get(chosenProvider));

		if (chosenProvider != null) {
			if (Simulation.LOG_ENABLED == 1) {
				System.out.println("INFO: " + ((User) consumer).getName()
						+ " choose provider "
						+ ((User) chosenProvider).getName());
			}

			// TRACE RATING DATA LOST
			List<IRater> fullRaterList = service.getRaters(chosenProvider);
			// exclude consumer from the list
			for (IRater rater : fullRaterList) {
				if (rater == consumer) {
					fullRaterList.remove(rater);
				}
			}
			double db = fullRaterList.size();
			double dataLost = 0;
			if (db != 0) {
				dataLost = 1 - credibilityOfRatersForChosenProvider.size() / db;
			}

			double perEval = generatePerEval(chosenProvider);
			
			// GENERATE A FEEDBACK
			double feedback = generateFeedback(consumer, chosenProvider, perEval);
			
			// ADJUST THE CONSUMER EXPERIENCE INCLUDING INFO & RATING(FeedBack)
			Experience oldExp = consumer
					.getConsumerExp(chosenProvider, service);
			Experience newExp;
			if (oldExp == null) {
				newExp = new Experience(feedback, perEval,
						chosenProvider.getReputedScore(), 0);
			} else {
				newExp = new Experience(feedback, perEval,
						chosenProvider.getReputedScore(),
						oldExp.getNumUses() + 1);
			}
			Experience currentExp = adjustExperience(oldExp, newExp);
			consumer.setConsumerExp(chosenProvider, service, currentExp);

			// ADJUST CREDIBILITY
			reputationSystem.updateUsefulFactor(consumer, service, chosenProvider, credibilityOfRatersForChosenProvider, feedback);

			// UPDATE THE INTERACTION
			interaction = new Interaction(chosenProvider, consumer, service,
					currentExp.getFeedback(), currentExp.getPerEval(),
					currentExp.getPreRepScore(), dataLost,
					credibilityOfRatersForChosenProvider);

			// UPDATE THE RATER LIST IF CONSUMER NOT IN
			boolean exist = false;
			for (IRater rater : service.getRaters(chosenProvider)) {
				if (rater == consumer) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				service.addRater(chosenProvider, (IRater) consumer);
			}

			// UPDATE numProvision OF THE PROVIDER and
			// MODIFY CHOSEN PROVIDER's QOS ACCORDING TO ITS pTYPE
			chosenProvider.increaseNumProvison();
			chosenProvider.changeBehaviour();
		}

		if (Simulation.LOG_ENABLED == 1) {
			System.out.println("INFO: END INTERACTION " + (i + 1) + "/"
					+ getInteractionNumber());
		}
		return interaction;
	}

	/**
	 * @see AbstractExperiment#extractData(List<Interaction> interactions)
	 */
	protected void extractData(List<Interaction> interactions)
			throws IOException {
		// CREATE THE RESULT FILE
		FileWriter fstream = new FileWriter("result.xls");
		BufferedWriter out = new BufferedWriter(fstream);
		out.write("ExpParameters \n");
		out.write("InteractionNum \t ServiceNum \t UserNum \n");
		out.write(getInteractionNumber() + "\t" + getServiceSet().size() + "\t"
				+ getUserNumber() + "\n");
		out.write("Good% \t GoodTurnsBad% \t Fluctuate% \t Bad% \t BadTurnsGood% \t Normal% \n");
		out.write(getGoodUser() + "\t" + getGoodTurnBadUser() + "\t"
				+ getFluctuateUser() + "\t" + getBadUser() + "\t"
				+ getBadTurnGoodUser() + "\t" + getNormalUser() + "\n");
		out.write("ResourceAvail \t" + getResourceAvailable() + "\n");
		out.write("FrequencyOfFluctuation \t" + getFrequencyOfFluctuation()
				+ "\n");
		out.write("Honest% \t Dishonest% \t Random% \t Collusive% \t Collusion% \t CollusionGroupNum \n");
		out.write(getHonestRater() + "\t" + getDishonestRater() + "\t"
				+ getRandomRater() + "\t" + getCollusiveRater() + "\t"
				+ getCollusiveGroupNum() + "\n");
		out.write("Order of creating providers: \t random with probs \n");
		out.write("\t G \t GTB \t F \t B \t BTG \t when 1 of them enough \t -> Normal \n");
		out.write("Order of creating raters: \t \t Honest \t Dishonest \t Random \t Collusive \t (C1->C2->C3) \n");
		out.write("Service [0,NumSer-1] \t randomly assigned to user \n");
		out.write("Strategy chosen:\t " + getChosenStrategy() + "\n");
		out.write("DataLost% (Input) \t" + getDataLostPercent() + "\n");

		// Process ServiceList & InteractionList to get data for graph
		int[] numUserInitQoS = new int[10];
		int[] numUserPType = new int[providerType.values().length];
		int[] numChosenAsProvider1 = new int[providerType.values().length];
		int[] numChosenAsProvider2 = new int[providerType.values().length];
		int[] numChosenAsProvider = new int[providerType.values().length];
		double[] aveDiffAsProviderSeenByConsumers = new double[providerType
				.values().length];
		double[] sumDiffAsProvider = new double[providerType.values().length];
		int[] numConsuming = new int[providerType.values().length];
		double[] aveDiffAsConsumer = new double[providerType.values().length];
		double[] sumDiffAsConsumer = new double[providerType.values().length];
		int[] numSatisfiedOverConsumer = new int[providerType.values().length];
		int[] numUnSatisfiedOverConsumer = new int[providerType.values().length];
		int[] percentProvision = new int[providerType.values().length];
		List<User> providerNeverChosen = new ArrayList<User>();
		int[] numProviderNeverChosen = new int[providerType.values().length];
		// initiate values
		for (int i = 0; i < 10; i++) {
			numUserInitQoS[i] = 0;
		}
		for (providerType pType : providerType.values()) {
			numUserPType[pType.ordinal()] = 0;
			numChosenAsProvider1[pType.ordinal()] = 0;
			numChosenAsProvider2[pType.ordinal()] = 0;
			numChosenAsProvider[pType.ordinal()] = 0;
			aveDiffAsProviderSeenByConsumers[pType.ordinal()] = 0;
			sumDiffAsProvider[pType.ordinal()] = 0;
			numConsuming[pType.ordinal()] = 0;
			aveDiffAsConsumer[pType.ordinal()] = 0;
			sumDiffAsConsumer[pType.ordinal()] = 0;
			numSatisfiedOverConsumer[pType.ordinal()] = 0;
			numUnSatisfiedOverConsumer[pType.ordinal()] = 0;
			numProviderNeverChosen[pType.ordinal()] = 0;
		}
		for (User user : users) {
			providerNeverChosen.add(user);// not really copy, just referring
		}

		// Table 1
		int totalUserTable1 = 0;
		for (int i = 0; i < 10; i++) {
			for (Service service : services) {
				numUserInitQoS[i] = numUserInitQoS[i]
						+ service.getNumUserInitQoS()[i];
			}
			totalUserTable1 = totalUserTable1 + numUserInitQoS[i];
		}

		// Table 2
		int totalUserTable2 = 0;
		for (providerType pType : providerType.values()) {
			for (Service service : services) {
				// enum ordinal starts with 0
				numUserPType[pType.ordinal()] = numUserPType[pType.ordinal()]
						+ service.getNumUserPType()[pType.ordinal()];
			}
			totalUserTable2 = totalUserTable2 + numUserPType[pType.ordinal()];
		}

		// Table 5: RaterType
		int[] numRaterType = new int[raterType.values().length];
		int[] numConsumeByRater = new int[raterType.values().length];
		int[] numAsRater = new int[raterType.values().length];
		double[] sumCredOverRTypeAndTrnx = new double[raterType.values().length];
		// initiate
		for (raterType rType : raterType.values()) {
			numRaterType[rType.ordinal()] = 0;
			numConsumeByRater[rType.ordinal()] = 0;
			numAsRater[rType.ordinal()] = 0;
			sumCredOverRTypeAndTrnx[rType.ordinal()] = 0;
		}
		for (User user : users) {
			numRaterType[user.getRaterType().ordinal()]++;
		}

		double temp, sumDataLost = 0;
		int numLoseAll = 0, interactionCounter = 0;
		providerType pTypeTemp;
		raterType rTypeTemp;
		// for numAsRater
		Map<IRater, Credibility> credibilityOfRatersForChosenProvider;
		double[][] averageCredPerUserPerTrnx = new double[raterType.values().length][10];
		for (Interaction interaction : interactions) {
			// for dataLost
			temp = interaction.getDataLost();
			if (temp == 1) {
				numLoseAll++;
			}
			sumDataLost = sumDataLost + temp;
			// for numProvision & averageDifferenceAsProvider
			interactionCounter++;
			if (interactionCounter < interactions.size() / 2) {// first half
				pTypeTemp = interaction.getProvider().getProviderType();
				numChosenAsProvider1[pTypeTemp.ordinal()]++;
				sumDiffAsProvider[pTypeTemp.ordinal()] = sumDiffAsProvider[pTypeTemp
						.ordinal()]
						+ Math.abs(interaction.getPerEval()
								- interaction.getEstimatedScore());
			} else { // second half
				pTypeTemp = interaction.getProvider().getProviderType();
				numChosenAsProvider2[pTypeTemp.ordinal()]++;
				sumDiffAsProvider[pTypeTemp.ordinal()] = sumDiffAsProvider[pTypeTemp
						.ordinal()]
						+ Math.abs(interaction.getPerEval()
								- interaction.getEstimatedScore());
			}
			// for numConsuming & AverageDiffAsConsumer & percentSatisfied
			pTypeTemp = ((IProvider) interaction.getConsumer())
					.getProviderType();
			numConsuming[pTypeTemp.ordinal()]++;
			rTypeTemp = interaction.getConsumer().getRaterType();
			numConsumeByRater[rTypeTemp.ordinal()]++;
			sumDiffAsConsumer[pTypeTemp.ordinal()] = sumDiffAsConsumer[pTypeTemp
					.ordinal()]
					+ Math.abs(interaction.getPerEval()
							- interaction.getEstimatedScore());
			if (interaction.getPerEval() > 0.5) {
				numSatisfiedOverConsumer[pTypeTemp.ordinal()]++;
			} else
				numUnSatisfiedOverConsumer[pTypeTemp.ordinal()]++;
			// for provider never chosen
			providerNeverChosen.remove(interaction.getProvider());
			// for averageCred
			credibilityOfRatersForChosenProvider = interaction
					.getCredibilityOfRatersForChosenProvider();
			for (IRater rater : credibilityOfRatersForChosenProvider.keySet()) {
				numAsRater[rater.getRaterType().ordinal()]++;
				sumCredOverRTypeAndTrnx[rater.getRaterType().ordinal()] = sumCredOverRTypeAndTrnx[rater
						.getRaterType().ordinal()]
						+ credibilityOfRatersForChosenProvider.get(rater)
								.getCredibility();
			}
			if ((interactionCounter % (interactions.size() / 10)) == 0) {
				for (raterType rType : raterType.values()) {
					averageCredPerUserPerTrnx[rType.ordinal()][(interactionCounter * 10 / interactions
							.size()) - 1] = Math
							.round(sumCredOverRTypeAndTrnx[rType.ordinal()]
									* 100 / numAsRater[rType.ordinal()])
							/ (double) 100;
				}
			}
		}// end interaction process
		int[] percentSatisfied = new int[providerType.values().length];
		int[] percentUnsatisfied = new int[providerType.values().length];
		double[] provisionPerUser = new double[providerType.values().length];
		double[] consumingPerUser = new double[providerType.values().length];
		double[] consumeByRaterPerUser = new double[raterType.values().length];
		double[] numAsRaterPerUser = new double[raterType.values().length];
		for (providerType pType : providerType.values()) {
			numChosenAsProvider[pType.ordinal()] = numChosenAsProvider1[pType
					.ordinal()] + numChosenAsProvider2[pType.ordinal()];
			provisionPerUser[pType.ordinal()] = numChosenAsProvider[pType
					.ordinal()] / (double) numUserPType[pType.ordinal()];
			consumingPerUser[pType.ordinal()] = numConsuming[pType.ordinal()]
					/ (double) numUserPType[pType.ordinal()];
			if (numChosenAsProvider[pType.ordinal()] != 0) {
				aveDiffAsProviderSeenByConsumers[pType.ordinal()] = sumDiffAsProvider[pType
						.ordinal()]
						* 100
						/ numChosenAsProvider[pType.ordinal()];
			}
			if (numConsuming[pType.ordinal()] != 0) {
				aveDiffAsConsumer[pType.ordinal()] = sumDiffAsConsumer[pType
						.ordinal()] * 100 / numConsuming[pType.ordinal()];
				percentSatisfied[pType.ordinal()] = numSatisfiedOverConsumer[pType
						.ordinal()]
						* 100
						/ (numSatisfiedOverConsumer[pType.ordinal()] + numUnSatisfiedOverConsumer[pType
								.ordinal()]);
				percentUnsatisfied[pType.ordinal()] = 100 - percentSatisfied[pType
						.ordinal()];
			}
		}
		// for provider never chosen
		for (IProvider provider : providerNeverChosen) {
			numProviderNeverChosen[provider.getProviderType().ordinal()]++;
		}

		// for numConsumeByRaterPerUser
		for (raterType rType : raterType.values()) {
			consumeByRaterPerUser[rType.ordinal()] = numConsumeByRater[rType
					.ordinal()] / (double) numRaterType[rType.ordinal()];
			numAsRaterPerUser[rType.ordinal()] = numAsRater[rType.ordinal()]
					/ (double) numRaterType[rType.ordinal()];
		}

		out.write("--------\t Checked values \t -------- \n");
		// DataLost
		double dataLost = sumDataLost * 100 / (double) interactions.size();
		out.write("Data lost%: \t" + dataLost + "\t\t numLoseAll = "
				+ numLoseAll + "\n");
		// User population
		// Table1
		out.write("Table 1 \t NumUsers \n");
		out.write("\t initialQoS \t 0,1 \t 0,2 \t 0,3 \t 0,4 \t 0,5 \t 0,6 \t 0,7 \t 0,8 \t 0,9 \t 1 \t Total \n");
		out.write("Service");
		for (Service service : services) {
			out.write("\t" + service.getId() + "\t"
					+ service.getNumUserInitQoS()[0] + "\t"
					+ service.getNumUserInitQoS()[1] + "\t"
					+ service.getNumUserInitQoS()[2] + "\t"
					+ service.getNumUserInitQoS()[3] + "\t"
					+ service.getNumUserInitQoS()[4] + "\t"
					+ service.getNumUserInitQoS()[5] + "\t"
					+ service.getNumUserInitQoS()[6] + "\t"
					+ service.getNumUserInitQoS()[7] + "\t"
					+ service.getNumUserInitQoS()[8] + "\t"
					+ service.getNumUserInitQoS()[9] + "\t"
					+ service.getProviders().size() + "\n");
		}
		out.write("\t Total \t" + numUserInitQoS[0] + "\t" + numUserInitQoS[1]
				+ "\t" + numUserInitQoS[2] + "\t" + numUserInitQoS[3] + "\t"
				+ numUserInitQoS[4] + "\t" + numUserInitQoS[5] + "\t"
				+ numUserInitQoS[6] + "\t" + numUserInitQoS[7] + "\t"
				+ numUserInitQoS[8] + "\t" + numUserInitQoS[9] + "\t"
				+ totalUserTable1 + "\n");
		// Table2
		out.write("Table 2 \t NumUsers \n");
		out.write("\t ProviderType \t");
		for (providerType pType : providerType.values()) {
			out.write(pType.name() + "\t");
		}
		out.write("Total \n");
		out.write("Service");
		for (Service service : services) {
			out.write("\t" + service.getId());
			for (providerType pType : providerType.values()) {
				out.write("\t" + service.getNumUserPType()[pType.ordinal()]);// enum
																				// ordinal
																				// starts
																				// from
																				// 0
			}
			out.write("\t" + service.getProviders().size() + "\n");
		}
		out.write("\t Sum \t");
		for (providerType pType : providerType.values()) {
			out.write(numUserPType[pType.ordinal()] + "\t");
		}
		out.write(totalUserTable2 + "\n");
		out.write("\t % \t");
		for (providerType pType : providerType.values()) {
			out.write(numUserPType[pType.ordinal()] * 100 / totalUserTable2
					+ "\t");
		}
		out.write("\n");

		// Table 3: percentProvision
		out.write("Table 3 \t Process interactionlist \n");
		for (providerType pType : providerType.values()) {
			percentProvision[pType.ordinal()] = numChosenAsProvider[pType
					.ordinal()] * 100 / interactions.size();
		}
		out.write("\t PType \t NumProvision in 50% first interactions \t NumProvision in 50% second interactions \t NumProvision \t NumProvison/User \t PercentProvision(%) \t AveDiffAsProviderSeenByConsumer(x100) \t NumConsuming \t NumConsuming/User \t AveDiffAsConsumer(x100) \t PercentSatisfied \t PercentUnsatisfied \n");
		for (providerType pType : providerType.values()) {
			out.write("\t" + pType.toString() + "\t"
					+ numChosenAsProvider1[pType.ordinal()] + "\t"
					+ numChosenAsProvider2[pType.ordinal()] + "\t"
					+ numChosenAsProvider[pType.ordinal()] + "\t"
					+ provisionPerUser[pType.ordinal()] + "\t"
					+ percentProvision[pType.ordinal()] + "\t"
					+ aveDiffAsProviderSeenByConsumers[pType.ordinal()] + "\t"
					+ numConsuming[pType.ordinal()] + "\t"
					+ consumingPerUser[pType.ordinal()] + "\t"
					+ aveDiffAsConsumer[pType.ordinal()] + "\t"
					+ percentSatisfied[pType.ordinal()] + "\t"
					+ percentUnsatisfied[pType.ordinal()] + "\n");
		}

		// Table 4: never chosen providers (as small as possible)
		out.write("Table 4 \t Never chosen providers \t (as small as possible) \n");
		for (providerType pType : providerType.values()) {
			out.write("\t" + pType.toString());
		}
		out.write("\t Total \n");
		out.write("NumUser \t");
		for (providerType pType : providerType.values()) {
			out.write(numProviderNeverChosen[pType.ordinal()] + "\t");
		}
		out.write(providerNeverChosen.size() + "\n");

		// Table 5: RaterType
		out.write("Table 5 \t RaterType and Credibilities \n");
		out.write("\t RaterType \t NumUserRaterType \t NumConsumeRaterType \t NumConsumeRaterType/User \t NumAsRater \t NumAsRater/User");
		out.write("\t AveCred/User/Interaction from all consumer");
		for (int i = 0; i < 10; i++) {
			out.write("\t After first " + (i + 1) * 10 + "% of trxn");
		}
		out.write("\n");
		for (raterType rType : raterType.values()) {
			out.write("\t" + rType.toString() + "\t"
					+ numRaterType[rType.ordinal()] + "\t"
					+ numConsumeByRater[rType.ordinal()] + "\t"
					+ consumeByRaterPerUser[rType.ordinal()] + "\t"
					+ numAsRater[rType.ordinal()] + "\t"
					+ numAsRaterPerUser[rType.ordinal()]);
			out.write("\t");
			for (int i = 0; i < 10; i++) {
				out.write("\t" + averageCredPerUserPerTrnx[rType.ordinal()][i]);
			}
			out.write("\n");
		}

		out.close();

		// PRINT ALL THE INTERACTION
		if (Simulation.LOG_ENABLED == 1) {
			System.out.println("INFO: Interaction List");
			System.out.println("\tService\t\tProvider\tConsumer\tFeedback");
			for (Interaction interaction : interactions) {
				System.out.println(interaction);
			}
		}
	}

	/**
	 * Starting the Experiements
	 * 
	 * @throws IOException
	 */
	public void start() throws IOException {
		// SETUP
		setup();

		// COMPUTE
		List<Interaction> interactions = new ArrayList<Interaction>();
		for (int i = 0; i < getInteractionNumber(); i++) {
			Interaction interaction = createInteraction(i);
			if (interaction != null) {
				interactions.add(interaction);
			} else
				i--;
		}

		// PRINT RESULT
		extractData(interactions);
	}

}
