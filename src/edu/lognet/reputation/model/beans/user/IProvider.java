package edu.lognet.reputation.model.beans.user;

import edu.lognet.reputation.model.beans.service.Service;

/**
 * 
 * @author lvanni
 *
 */
public interface IProvider extends Comparable<IProvider> {
	
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
}
