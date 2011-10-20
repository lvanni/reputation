package edu.lognet.reputation.controller.core;

import java.util.ArrayList;

public class ClusteredData {
	private ArrayList<Double> rawData;
	private double coarsenParam = 0.2;
	private double refineParam = 0.5;
	private int iniNumClusters = 2;
	private double majorMean, mean, standardDeviation;	
	private int numberClusters;
	private ArrayList<Double> means;
	private ArrayList<Integer> weights;//Mean and weight values corresponding to clusters 
	private ArrayList<ArrayList<Double>> clusters;//List of double values in a cluster

	public ClusteredData(ArrayList<Double> ratings) {
		rawData = ratings;
		majorMean = kMeanAlgo(rawData, coarsenParam, refineParam, iniNumClusters);		
	}
	
	public ClusteredData(ArrayList<Double> ratings, double coarsenParam, double refineParam, int k) {
		rawData = ratings;
		this.coarsenParam = coarsenParam;
		this.refineParam = refineParam;
		this.iniNumClusters = k;
		majorMean = kMeanAlgo(rawData, this.coarsenParam, this.refineParam, this.iniNumClusters);
	}
	
	private double kMeanAlgo(ArrayList<Double> ratings, double coarsenParam, double refineParam, int k) {
		ArrayList<Double> temp;
		int ini,i;		
		means= new ArrayList<Double>();
		weights = new ArrayList<Integer>();
		clusters = new ArrayList<ArrayList<Double>>();
		if (k < ratings.size()) {
			ini = k;
		} else {
			ini = ratings.size();
		}
		for (i = 0; i < ini; i++) { //initial number of clusters = ini
			temp = new ArrayList<Double>();
			temp.add(ratings.get(i));
			clusters.add(i, temp);
			means.add(i, ratings.get(i));
			weights.add(i, 1);
		}
		//Debug
//		System.out.print("\nclusters before merged: " + clusters.toString() + "\nwith size = " + clusters.size());
//		System.out.print("\nmeans before merged: " + means.toString() + "\nmean size = " + means.size());
//		System.out.print("\nratingsize= " + ratings.size() + "\nini = " + ini + "\nk = " + k);		
		//end
		mergeClusters(clusters, means, weights, coarsenParam);
		double aRating, dis, disMin;//disMin to get the nearest cluster
		int l,j = 0;
		if (k < ratings.size()) {
			for (i = k; i < ratings.size(); i++) {//process the rest of ratings, not all				
				disMin = 2;//to activate the first assignment so that j is always assigned
				aRating = ratings.get(i);//processing with this rating
				for (Double mean : means) {
					dis = Math.abs(aRating - mean);
					if (dis < disMin) {
						disMin = dis;
						j = means.indexOf(mean);
					}
				}
				l = clusters.size();
				if (disMin > refineParam) {//create new cluster
					temp = new ArrayList<Double>();
					temp.add(aRating);
					clusters.add(l, temp);
					means.add(l, ratings.get(i));
					weights.add(l, 1);
					l++;
				} else {//joint the rating to the closest cluster
					//Debug
//					System.out.print("\n clusters = " + clusters.toString() + "\nclustersize=" + clusters.size());
//					System.out.print("\nmeans = " + means.toString() + "\nmeansize=" + means.size());
					//end
					clusters.get(j).add(aRating);
					Double temp1 = (means.get(j) * weights.get(j) + aRating) / (double)(weights.get(j) + 1);
					means.set(j, temp1);
					Integer temp2 = weights.get(j) + 1;
					weights.set(j, temp2);
					mergeClusters(clusters, means, weights, coarsenParam);
				}
			}
		}
		boolean moving = true;
		int t;
		while (moving) {
			t = l = 0;
			for (Double rating : ratings) {
				disMin = 2;//Maximum of disMin = 1				
				for (i = 0; i < means.size(); i++) {
					dis = Math.abs(rating - means.get(i));
					if (dis < disMin) {
						disMin = dis;
						j = i;//j is the closest cluster to this rating
					}
					if (clusters.get(i).contains(rating)) {
						l = i;//l is the cluster that this rating belongs to
					}
				}//j & l are always set after this for
				if (j != l) {
					clusters.get(j).add(rating);
					Double temp1 = (means.get(j) * weights.get(j) + rating)
							/ (double)(weights.get(j) + 1);
					means.set(j, temp1);
					Integer temp2 = weights.get(j) + 1;
					weights.set(j, temp2);
					clusters.get(l).remove(rating);
					temp1 = (means.get(l) * weights.get(l) - rating)
							/ (double)(weights.get(l) - 1);
					means.set(l, temp1);
					temp2 = weights.get(l) - 1;
					weights.set(l, temp2);
					t++;//t=number of moves, max=number of ratings
				}
			}
			if (t == 0) {
				moving = false;
			}
			//Debug
//			System.out.print("\nI'm stuck in kmeans algo with moving clusters\n"+clusters.toString());
			//end
		}
		t = 0;//actually not necessary
		for (i = 0; i < weights.size(); i++) {			
			if (weights.get(i) > t) {
				t = weights.get(i);
				j = i;
			}			
		}
		return means.get(j);
	}
	
	private static void mergeClusters(ArrayList<ArrayList<Double>> clusters,
			ArrayList<Double> means, ArrayList<Integer> weights, double coarsenParam) {
		int[] pair = new int[2];//Cluster pair having minimum distance, separated by ","
		boolean moving = true;
		//System.out.println("\nClusters before loop: " + clusters);
		int i,j;
		double disMin;
		while (moving) {			
			disMin = 2;
			//There are k(k-1)/2 distances between k means
			for (i = 0; i < means.size(); i++) {
				for (j = i+1; j < means.size(); j++) {
					double dis = Math.abs(means.get(i) - means.get(j));
					if (dis < disMin) {
						disMin = dis;					
						pair[0] = i;
						pair[1] = j;
					}					
				}				
			}
//			System.out.print("\ndisMin: " + disMin + "pair: " + pair + "\ncoarsenPar=" + coarsenParam);
			if (disMin < coarsenParam) {				
				i = pair[0];
				j = pair[1];				
				//For test
				//System.out.println("\nBefore merge = " + clusters.get(i));
				//end
				clusters.get(i).addAll(clusters.get(j));
				//For test
				//System.out.println("\nAfter merge = " + clusters.get(i));
				//end
				double temp1 = weights.get(i) + weights.get(j);
				double temp2 = (means.get(i)*weights.get(i) + means.get(j)*weights.get(j))/temp1;
				means.set(i, temp2);
				weights.set(i, (int) temp1);				
				for (i = j; i < clusters.size(); i++) {
					if (i != (clusters.size() - 1)) {
						clusters.set(i, clusters.get(i+1));
						means.set(i, means.get(i+1));
						weights.set(i, weights.get(i+1));
					} else {
						clusters.remove(i);
						means.remove(i);
						weights.remove(i);
					}
				}
			} else {
				moving = false;
			}
			//Debug
			//System.out.print("\nI'm stuck in while loop in MergeCluster, still merging clusters");
			//end
		}
	}
	
	public double getMajorMean() {
		return majorMean;
	}

	public double getMean() {
		Double sum = (double) 0;
		for (int i = 0; i < rawData.size(); i++) {
			sum = sum + rawData.get(i);
		}
		mean = sum/(double)rawData.size();
		return mean;
	}

	public int getNumberClusters() {
		numberClusters = clusters.size();
		return numberClusters;
	}
	
	public double getStandardDeviation() {
		Double mean2 = (double) 0;
		double temp;
		for (int i = 0; i < rawData.size(); i++) {
			temp = rawData.get(i)*rawData.get(i);
			mean2 = mean2 + temp;
		}
		mean2 = mean2/(double)rawData.size();
		temp = this.getMean();
		//(mean2 - temp*temp) is supposed to always >0
		standardDeviation = Math.sqrt(Math.abs(mean2 - temp*temp));
		return standardDeviation;
	}
}
