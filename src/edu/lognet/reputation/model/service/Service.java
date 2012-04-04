package edu.lognet.reputation.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.lognet.reputation.controller.simulations.Simulation;
import edu.lognet.reputation.model.user.IProvider;
import edu.lognet.reputation.model.user.IProvider.providerType;
import edu.lognet.reputation.model.user.IRater;

/**
 * Represent a Service to consume
 * @author Laurent Vanni, Thao Nguyen
 *
 */
public class Service {
	/* --------------------------------------------------------- */
	/* Attributes */
	/* --------------------------------------------------------- */
	private String id;
	private String name;
	private List<IProvider> providers;
	private Map<IProvider, List<IRater>> raters;
	
	//For checking population
	private int[] numUserInitQoS = new int[10];
	private int[] numUserPType = new int[providerType.values().length];

	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public Service(String id, String name) {
		this.id = id;
		this.name = name;
		providers = new ArrayList<IProvider>();
		raters = new HashMap<IProvider, List<IRater>>();
		//For checking population
		for (int i = 0; i < 10; i++) {
			numUserInitQoS[i] = 0;	
		}
		for (providerType pType:providerType.values()) {
			numUserPType[pType.ordinal()] = 0;			
		}
	}

	/* --------------------------------------------------------- */
	/* GETTER AND SETTER */
	/* --------------------------------------------------------- */
	public void addProdiver(IProvider provider){
		providers.add(provider);
		// init raterList
		raters.put(provider, new ArrayList<IRater>());
		//For checking population
		double temp = provider.getInitQoS();
		if (temp <= 0.1) {
			numUserInitQoS[0]++;
		} else if (temp <= 0.2) {
			numUserInitQoS[1]++;
		} else if (temp <= 0.3) {
			numUserInitQoS[2]++;
		} else if (temp <= 0.4) {
			numUserInitQoS[3]++;
		} else if (temp <= 0.5) {
			numUserInitQoS[4]++;
		} else if (temp <= 0.6) {
			numUserInitQoS[5]++;
		} else if (temp <= 0.7) {
			numUserInitQoS[6]++;
		} else if (temp <= 0.8) {
			numUserInitQoS[7]++;
		} else if (temp <= 0.9) {
			numUserInitQoS[8]++;
		} else if (temp <= 1) {
			numUserInitQoS[9]++;
		}	 
		providerType pType = provider.getProviderType();
		if (pType==providerType.GOOD) {
			numUserPType[0]++;
		} else if (pType==providerType.GOODTURNSBAD) {
			numUserPType[1]++;
		} else if (pType==providerType.FLUCTUATE) {
			numUserPType[2]++;
		} else if (pType==providerType.BAD) {
			numUserPType[3]++;
		} else if (pType==providerType.BADTURNSGOOD) {
			numUserPType[4]++;
		} else numUserPType[5]++;
	}
	
	public List<IProvider> getProviders(){
		List<IProvider> returnProviders = new CopyOnWriteArrayList<IProvider>(providers);
		if (Simulation.LOG_ENABLED == 1) {
			System.out.println("Providers for the service "+id+" are: "+providers);
			System.out.println("CopyList of providers: "+returnProviders);
		}
		return returnProviders;
	}
	
	public List<IRater> getRaters(IProvider provider) {
		List<IRater> returnRaters = new CopyOnWriteArrayList<IRater>(raters.get(provider));
		return returnRaters;
	}

	public void addRater(IProvider provider, IRater rater) {
		this.raters.get(provider).add(rater);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int[] getNumUserInitQoS() {
		return numUserInitQoS;
	}
	
	public int[] getNumUserPType(){
		return numUserPType;
	}	
}
