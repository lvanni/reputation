package edu.lognet.reputation.model.user;



import edu.lognet.reputation.model.service.Service;

/**
 * Represent a machine in the system
 *
 * @author Romain Fritz, Laurent Vanni, Thao Nguyen
 */
public class Machine extends User {
	
	public static double IDEAL_BANDWIDTH = 1024;
	public static double IDEAL_UPTIME = 10000000;

	public Machine(String id, String name, int age, Service currentService,
			providerType pType, raterType rType, collusionGroup cGroup,
			double bandwidth, boolean is_available,int uptime, int resourceAvailable, int frequencyOfFluctuation) {
		super(id, name, age, currentService, pType, rType, cGroup,0,
				resourceAvailable, frequencyOfFluctuation);
		double Qos = 0;
		if((bandwidth/IDEAL_BANDWIDTH) > 1){
			Qos += 0.4;
		}
		if((uptime/IDEAL_UPTIME)> 1){
			Qos += 0.4;
		}
		if(is_available){
			Qos +=0.2;
		}
		this.setQoS(Qos);
	}

}
