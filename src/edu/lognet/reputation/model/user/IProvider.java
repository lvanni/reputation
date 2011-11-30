package edu.lognet.reputation.model.user;

import edu.lognet.reputation.model.service.Service;
import edu.lognet.reputation.model.user.IProvider.providerType;
import edu.lognet.reputation.model.user.User.collusionGroup;
import edu.lognet.reputation.model.user.User.victimGroup;

/**
 * 
 * @author lvanni
 *
 */
public interface IProvider {
	
	public static enum providerType {
		GOOD,
		GOODTURNSBAD,
		FLUCTUATE,
		BAD,
		BADTURNSGOOD,		
		NORMAL
	}

	/**
	 * @return the provided Service
	 */
	public Service getProvidedService();

	/**
	 * Set the Service provided by the user
	 * @param providedService
	 */
	public void setProvidedService(Service providedService);
	
	/**
	 * @return the provided Service
	 */
	public double getQoS();

	/**
	 * Set the Service provided by the user
	 * @param providedService
	 */
	public void setQoS(double QoS);

	public void setReputedScore(double db);

	public collusionGroup getCollusionCode();

	public victimGroup getVictimCode();

	public double getReputedScore();

	public void increaseNumProvison();

	public int getNumProvision();

	public int getResourceAvailable();

	public void changeBehaviour();

	public double getInitQoS();

	public providerType getProviderType();
}
