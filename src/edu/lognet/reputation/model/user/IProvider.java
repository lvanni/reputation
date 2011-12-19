package edu.lognet.reputation.model.user;

import edu.lognet.reputation.model.service.Service;
import edu.lognet.reputation.model.user.User.collusionGroup;
import edu.lognet.reputation.model.user.User.victimGroup;

/**
 * Represent a Provider in the simulator
 * @author lvanni
 *
 */
public interface IProvider {
	
	/**
	 * @TODO
	 */
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

	/**
	 * @TODO
	 * @param db
	 */
	public void setReputedScore(double db);

	/**
	 * @TODO
	 * @return
	 */
	public collusionGroup getCollusionCode();

	/**
	 * @TODO
	 * @return
	 */
	public victimGroup getVictimCode();

	/**
	 * @TODO
	 * @return
	 */
	public double getReputedScore();

	/**
	 * @TODO
	 */
	public void increaseNumProvison();

	/**
	 * @TODO
	 * @return
	 */
	public int getNumProvision();

	/**
	 * @TODO
	 * @return
	 */
	public int getResourceAvailable();

	/**
	 * @TODO
	 */
	public void changeBehaviour();

	/**
	 * @TODO
	 * @return
	 */
	public double getInitQoS();

	/**
	 * @TODO
	 * @return
	 */
	public providerType getProviderType();
}
