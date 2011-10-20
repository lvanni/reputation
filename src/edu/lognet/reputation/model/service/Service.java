package edu.lognet.reputation.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.lognet.reputation.model.user.IProvider;
import edu.lognet.reputation.model.user.IRater;

/**
 * 
 * @author lvanni
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
	
	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public Service(String id, String name) {
		this.id = id;
		this.name = name;
		providers = new ArrayList<IProvider>();
		raters = new HashMap<IProvider, List<IRater>>();
	}

	/* --------------------------------------------------------- */
	/* GETTER AND SETTER */
	/* --------------------------------------------------------- */
	public void addProdiver(IProvider provider){
		providers.add(provider);
		// init raterList
		raters.put(provider, new ArrayList<IRater>());
	}
	
	public List<IProvider> getProviders(){
		List<IProvider> returnProviders = new CopyOnWriteArrayList<IProvider>(providers);
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
	
}
