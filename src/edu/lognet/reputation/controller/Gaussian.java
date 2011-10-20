package edu.lognet.reputation.controller;

import java.util.HashMap;
import java.util.Map;

/**
 * Return A gaussian Curve
 * 
 * @author lvanni
 */
public class Gaussian {

	/* --------------------------------------------------------- */
	/* Attributes */
	/* --------------------------------------------------------- */
	private double stdDeviation;
	private double variance;
	private double mean;

	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
	public Gaussian(double stdDeviation, double mean) {
		this.stdDeviation = stdDeviation;
		this.variance = Math.pow(stdDeviation, 2);
		this.mean = mean;
	}

	/* --------------------------------------------------------- */
	/* public methods */
	/* --------------------------------------------------------- */
	/**
	 * y = f(x)
	 * @param x
	 * @return y
	 */
	public double getY(double x) {
		/* Thao commented
		//Laurent's code
		return Math.pow(
				Math.exp(-(((x - mean) * (x - mean)) / ((2 * variance)))),
				1 / (stdDeviation * Math.sqrt(2 * Math.PI))); // Curve Equation
		*/
		return (1 / (stdDeviation * Math.sqrt(2 * Math.PI))*
				Math.exp(-(((x - mean) * (x - mean)) / ((2 * variance))))); // Curve Equation		
	}

	/* --------------------------------------------------------- */
	/* Main - Tests */
	/* --------------------------------------------------------- */
	public static void main(String args[]) {

		int size = 150; // size of the curve

		double[] d = new double[size];
		double sum = 0.0;
		Gaussian gaussian = new Gaussian(2, 0.0);
		for (int x = 0; x < size; x++) {
			d[x] = gaussian.getY(x) * 100;
		}
		Map<Double, Double> percent = new HashMap<Double, Double>();
		for (int i = 0; i < size; i++) {
			sum += d[i];
		}

		for (int i = 0; i < size; i++) {
			percent.put(d[i], (100 * d[i]) / sum);
			for (int j = 0; j < d[i]; j++) {
				System.out.print("*");
			}
			System.out.println(" d[" + i + "] = " + d[i] + " = "
					+ percent.get(d[i]) + "%");
		}
		System.out.println("\nSomme : " + sum + " = 100%");
		System.out.println("Random value : " + Math.random() * percent.get(d[0]));
	}
}

/* An other way to do gaussian curve */
// int n = 1000;
// double[] d = new double[n];
// double sum = 0.0;
// for(int i=0 ; i<n ; i++){
// while((d[i] = r.nextGaussian()) < 0){};
// }
// Arrays.sort(d);
//
// Map<Double, Double> percent = new HashMap<Double, Double>();
// for(int i=0 ; i<n ; i++){
// sum += d[i];
// }
//
// for(int i=0 ; i<n ; i++){
// percent.put(d[i], (100*d[i])/sum);
// System.out.println("d[" + i + "] = " + d[i] + " = " + percent.get(d[i]) +
// "%");
// }
// System.out.println("\nSomme : " + sum + " = 100%");