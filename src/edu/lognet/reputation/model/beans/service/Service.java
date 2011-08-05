package edu.lognet.reputation.model.beans.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.lognet.reputation.model.beans.user.IProvider;
import edu.lognet.reputation.model.beans.user.IRater;

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
		return providers;
	}
	
	public List<IRater> getRaters(IProvider provider) {
		return raters.get(provider);
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
