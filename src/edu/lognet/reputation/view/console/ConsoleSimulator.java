package edu.lognet.reputation.view.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.lognet.reputation.controller.simulations.Simulation;
import edu.lognet.reputation.controller.simulations.simulation1.Simulation1;

/**
 * Represent a Console UI for the simulator
 * @author lvanni
 * 
 */
public class ConsoleSimulator {

	/*
	 * Starting the Console user interface
	 */
	public static void main(String args[]) {

		// INIT
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);

		System.out
		.println("                 _____                  _        _   _             			 \n"
				+ "                |  __ \\                | |      | | (_)            	 		 \n"
				+ " _ __ ___  _   _| |__) |___ _ __  _   _| |_ __ _| |_ _  ___  _ __  			 \n"
				+ "| '_ ` _ \\| | | |  _  // _ \\ '_ \\| | | | __/ _` | __| |/ _ \\| '_ \\ 		 \n"
				+ "| | | | | | |_| | | \\ \\  __/ |_) | |_| | || (_| | |_| | (_) | | | |		 \n"
				+ "|_| |_| |_|\\__, |_|  \\_\\___| .__/ \\__,_|\\__\\__,_|\\__|_|\\___/|_| |_|	 \n"
				+ "            __/ |          | |                                      			 \n"
				+ "           |___/           |_|       										 \n");

		int chx = 0;
		Simulation experiment = null;
		while(true){
			System.out.print("\n\n" +
					"1) setup \n" +
					"2) start \n" +
					(Simulation.LOG_ENABLED == 0 ? "3) enable log \n" :  "3) disable log \n") +
					"4) quit \n"  +
					"------> ");
			try {
				chx = Integer.parseInt(in.readLine());
				System.out.println();
				switch(chx) {
				case 1 : 
					System.out.print("Interaction Number : ");
					int interactionNumber = Integer.parseInt(in.readLine());
					System.out.print("Service Number : ");
					int serviceNumber = Integer.parseInt(in.readLine());
					System.out.print("User Number: ");
					int totalUserNumber = Integer.parseInt(in.readLine());
					
					int goodUser=0, goodTurnBadUser=0, fluctuateUser=0, frequencyOfFluctuation=0;
					int badUser=0, badTurnGoodUser=0;
					System.out.print("Good Provider Percent : ");
					goodUser = Integer.parseInt(in.readLine());
					if (goodUser<100) {
						System.out.print("GoodTurnsBad Provider Percent : ");
						goodTurnBadUser = Integer.parseInt(in.readLine());
						if ((goodUser+goodTurnBadUser)<100) {
							System.out.print("Fluctuate Provider Percent : ");
							fluctuateUser = Integer.parseInt(in.readLine());
							System.out.print("Frequency of Fluctuate Provider (% of resourceAvailable): ");
							frequencyOfFluctuation = Integer.parseInt(in.readLine());
							if ((goodUser+goodTurnBadUser+fluctuateUser)<100) {
								System.out.print("Bad Provider Percent : ");
								badUser = Integer.parseInt(in.readLine());
								if ((goodUser+goodTurnBadUser+fluctuateUser+badUser)<100) {
									System.out.print("BadTurnsGood Provider Percent : ");
									badTurnGoodUser = Integer.parseInt(in.readLine());
									System.out.print("The rest is Normal Provider Percent\n");
								}
							}
						}						
					}
					
					System.out.print("resourceAvailable (% of TotalTrnx) of each user : ");
					int resourceAvailable = Integer.parseInt(in.readLine());					
					
					System.out.print("Honest Rater Percent : ");
					int honestRater = Integer.parseInt(in.readLine());
					int dishonestRater=0, randomRater=0, collusiveGroupNum=0;
					if (honestRater<100) {
						System.out.print("Dishonest Rater Percent : ");
						dishonestRater = Integer.parseInt(in.readLine());
						if ((honestRater+dishonestRater)<100) {
							System.out.print("Random Rater Percent : ");
							randomRater = Integer.parseInt(in.readLine());
							if ((honestRater+dishonestRater+randomRater)<100) {
								System.out.print("The rest is Collusive Rater Percent \n");
								System.out.print("Number of collusive groups (even size) [1,3]: ");
								collusiveGroupNum = Integer.parseInt(in.readLine());
							}							
						}
					}
					
					System.out.print("Data Lost Percent : ");
					int dataLost = Integer.parseInt(in.readLine());					
					System.out.print("Choosing strategy : ");
					System.out.print("\n\t1) choosing provider with highest score");
					System.out.print("\n\t2) cropping provider list before choosing randomly on weighted Statistic Factor");
					System.out.print("\n\t3) choosing randomly on weighted Statistic Factor");
					System.out.print("\n------> ");
					int choosingStrategy = Integer.parseInt(in.readLine());
					experiment = new Simulation1(interactionNumber, serviceNumber, totalUserNumber, goodUser, goodTurnBadUser, fluctuateUser, frequencyOfFluctuation, badUser, badTurnGoodUser, honestRater, dishonestRater, randomRater, collusiveGroupNum, resourceAvailable, dataLost, choosingStrategy);
					System.out.println("INFO: Experiment Created! Ready to start...");
					break;
				case 2 : 
					if(experiment != null) {
						experiment.start();
						System.out.println("INFO: Experiment Success!");
					} else {
						System.out.println("ERR: You must setup the experiment first!");
					}
					break;
				case 3 : 
					Simulation.LOG_ENABLED = Simulation.LOG_ENABLED ^ 1;
					break;
				case 4 :
					System.out.println("bye bye...");
					System.exit(0);
				default : break;
				}
			} catch (NumberFormatException e) {
				System.out.println("ERR: unknown command!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
