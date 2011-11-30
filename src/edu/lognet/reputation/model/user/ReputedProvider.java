package edu.lognet.reputation.model.user;

/**
 * Represent a reputed provider
 * 
 * @author lvanni
 */
public class ReputedProvider implements Comparable<ReputedProvider> {
	
	/* --------------------------------------------------------- */
	/* Attributes */
	/* --------------------------------------------------------- */
	private IProvider provider;
	private double reputation;
	/** statistic factor used when a provider is chosen */
	private double statisticFactor;
	/** To print the percent */
	private double statisticFeedBack;

	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public ReputedProvider(IProvider provider, double reputation) {
		this.provider = provider;
		this.reputation = reputation;
	}

	/* --------------------------------------------------------- */
	/* public methods */
	/* --------------------------------------------------------- */
	@Override
	public int compareTo(ReputedProvider o) {
		if (reputation == o.getReputation()) {
			return 0;
		} else if (reputation < o.getReputation()) {
			return -1;
		} else {
			return 1;
		}
	}

	@Override
	public String toString() {
		return provider.toString() + "\t\t" + reputation + "\t\t"
				+ statisticFeedBack + "%";
	}

	/* --------------------------------------------------------- */
	/* Getter & Setter */
	/* --------------------------------------------------------- */
	public double getReputation() {
		return reputation;
	}

	public IProvider getProvider() {
		return provider;
	}

	public double getStatisticFactor() {
		return statisticFactor;
	}

	public void setStatisticFactor(double statistic) {
		this.statisticFactor = statistic;
	}

	public void setStatisticFeedBack(double statisticFeedBack) {
		this.statisticFeedBack = statisticFeedBack;
	}
}