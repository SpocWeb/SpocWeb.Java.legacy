/*
 * File Name: StatisticsFloat.java
 * Created on: 09.02.2004
 *
 */
package math.vector.statistic;

import math.matrix.MatrixDouble;
import math.matrix.MatrixInt;
import math.vector.HunterFloat;
import math.vector.VectorDouble;
import math.vector.VectorFloat;
import math.vector.VectorInt;
import streamIO.Assert;
import streamIO.Log;
import streamIO.integer.random.IStreamIn_Bound_Int;
import streamIO.integer.random.RandomFast;
import streamIO.integer.random.RandomQuick;
import streamIO.object.IStreamIn;
import streamIO.real.FilterIn_FloatByFunction;
import streamIO.real.IStreamIn_Float;
import streamIO.real.random.RandomGauss;
import function.IFloatFunction;
import function.byref.ByRefDouble;
import function.byref.combinatoric.ProbFuncs;
import function.derive.ring.body.BetaI;
import function.derive.ring.body.GammaP;
import function.derive.ring.body.Gauss;
import function.vector.AFloatVectorField;
import function.vector.IFloatVectorField;

/**
 * Static utility of hypothesis tests and contingency-table statistics over {@code float}
 * data sets: Student's t (same mean), F-test (same variance), chi-square (goodness of fit,
 * cross-tabulation), and 1D/2D Kolmogorov-Smirnov tests.
 *
 * <p>Title: StatisticsFloat<p>
 * Description:
 * Purpose:
 * Collects Methods for statistical Analysis:
 * It is important to understand that Statistics never prove something; 
 * they only disprove Assumptions about a Model 
 * (on which the expected Values and Variances are based). 
 * Additionally the Disproval is only given with a limited Certainty. 
 * 
 * Most Methods assume a normally distributed (Gaussian) Distribution 
 * and thus are not really well suited for broadly varying or nonlinear Correlations, 
 * because they either need a very strong Localization (only a single Mean is used) 
 * or an elliptic (linear) Distribution, but cannot be fitted to a bent Distribution! 
 * Very longish Distributions should first be transformed into a linear Distribution
 * and then analyzed statistically. 
 * Alternatively, the statistical Methods should consider local Means and not global Means. 
 * 
 * TODO: extend the 2Dim Contingency Analysis to an nDim Analysis! 
 * TODO: by applying log to either or both x and y Values, 
 * you can extend linear Correlation to Polynoms and Exponentials
 * TODO: implement a Rank Correlation based on a simple Rank 
 * (without Ties) as the Inverse of the Index.  
 * TODO: convert RankCorrelation so that it also works with Data of Offset 0
 * TODO: apply RankCorrelation without Metrics, only based on an Order Relation
 *
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:52:49Z
 * digest: e3ef5980c41dfcea2b3a6ae68b5b14c1f9fe7c9f64bfc3bb621613438a34fbd1
 * stale: false
 * -->
 */
public class StatisticsFloat 
implements IFloatFunction { 
	
	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(StatisticsFloat.class);
	
	////////////////////////////////////////////////////////////////////////////
	
	/**	
	 * O(n�) Algorithm for continuously summing up a scalar Distance Measure. 
	 * This could be done on Streams, but would require continuous resetting.  
	 * @param x the Data Vectors
	 * @return Array filled with the Sum of all Lorentz-weighed Points.  
	 */
	final static public float[] NEIGHBORS(final float[][] x) {
		return NEIGHBORS(null, x.length, x, x[x.length>>1].length); }
	
	/**	
	 * O(n�) Algorithm for continuously summing up a scalar Distance Measure. 
	 * This could be done on Streams, but would require continuous resetting.  
	 * @param x the Data Vectors
	 * @param numPoints the Number of Vectors to consider 
	 * @param numDims the Number of Items in each Vector (must be the same!)
	 * @return the Array ret, filled with the Sum of all Lorentz-weighed Points.  
	 */
	final static public float[] NEIGHBORS(float[] ret, final float[][] x) {
		return NEIGHBORS(ret, x.length, x, x[x.length>>1].length); }
	
	/**	
	 * O(n�) Algorithm for continuously summing up a scalar Distance Measure. 
	 * This could be done on Streams, but would require continuous resetting.  
	 * @param x the Data Vectors
	 * @param numPoints the Number of Vectors to consider 
	 * @param numDims the Number of Items in each Vector (must be the same!)
	 * @return the Array ret, filled with the Sum of all Lorentz-weighed Points.
	 * This can be used to eliminate Outliers, which have very few Neighbors (least Percentiles)
	 */
	final static public float[] NEIGHBORS(float[] ret, final int numPoints, final float[][] x, final int numDims) {
		if (ret == null) 
			ret =  new float[numPoints]; 
		for (int i = numPoints; --i >= 0;) 
			ret[i] = (float) SUM_LORENTZ_DIST(x, i, x[i], numDims); 
		return ret; 
	}
	
	/**
	 * @param x Vectors to calculate Distances from 
	 * @param x_length the Length of Vectors to use
	 * @param y the Vector to calculate Distances for
	 * @param numDims the Number of Dimensions of the Vectors x and y
	 * @return the Sum of the Lorentz-Distances from the given Vector y to the Vectory x
	 */
	private static double SUM_LORENTZ_DIST(final float[][] x, int x_length, final float[] y, final int numDims) {
		if (x_length <= 0)
			return 0; 
		double sum = 1/(1+VectorFloat.DIST_SQR(y, x[0], numDims)); //Optimization 
		for (int j = x_length; --j > 0;) 
			sum += 1/(1+VectorFloat.DIST_SQR(y, x[j], numDims));
		return sum;
	}

	/**	
	 * O(n�) Algorithm for continuously summing up the Gradient to the next Neigbors using a scalar Distance Measure. 
	 * This could be done on Streams, but would require continuous resetting.  
	 * @param x the Data Vectors
	 * @param xCol the x Column to use
	 * @param yCol the y Column to use
	 * @param n the Number of Items in each Vector (must be the same!)
	 * @return the Array ret, filled with the Sum of all Gradients of all Points. 
	 * This can be used to visualize 3D Scatter Plots 
	 * by giving each Point a small Plane Element oriented normal to this Gradient 
	 * used for Impressions of Shading and Reflection. 
	 */
	final static public float[][] NEIGHBOR_GRADIENT(float[][] ret, final int numPoints, final float[][] x, final int numDims) {
		if (ret == null) 
			ret =  new float[numPoints][numDims]; 
		final float[] diff =  new float[numDims]; 
		for (int i = numPoints; --i >= 0;) 
			NEIGHBOR_GRADIENT(x, i, numDims, ret[i], diff);
		return ret; 
	}
	
	/**
	 * @param x
	 * @param i the Length of the Vectors to use
	 * @param numDims the Number of Dimensions of each Vector x[i]
	 * @param grad optional Vector to sum up the Gradient in
	 * @param diff optional WorkSpace, used to calculate the Difference of the Vectors  
	 */
	private static float[] NEIGHBOR_GRADIENT(final float[][] x, int i, int numDims, float[] grad, float[] diff) {
		if (grad == null)
			grad =  new float[numDims]; 
		if (diff == null)
			diff =  new float[numDims]; 
		final float[] x_i = x[i]; 
		for (int j = i; --j >= 0;) 
			VectorFloat.addProdAt(grad, diff, 1/(1+VectorFloat.DIFF_NORM_SQR(x_i, x[j], numDims, diff))); 
		return grad; 
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	/** contingency table analysis using Entropy Measure (14.4)
	 * 
	 * @return the total Entropy of the Matrix
	 * @param nn the Matrix to analyze 
	 * @param entropies on Return filled with the Entropies of the RowSums and ColSums  
	 */
	final static public double CROSS_TAB_ENTROPY(final int[][] nn, final double[] entropies) {
		return CROSS_TAB_ENTROPY(nn, (int[]) null, null, entropies); 
	}
	
	/** contingency table analysis using entropy measure (14.4)
	 * 
	 * @param nn the Matrix to analyze 
	 * @param dimsNotEmpty 
	 * on  Input: the Number of Rows and Columns to consider
	 * on Output: the Number of Rows and Columns with nonzero Sum  
	 * @param sums the Row and Column Sums
	 * @param entropies on Return filled with the Entropies of the RowSums and ColSums  
	 * @return the total Entropy of the Matrix
	 */
	final static public double CROSS_TAB_ENTROPY(final int[][] nn, int[] dimsNotEmpty
	, int[][] sums, final double[] entropies) {
		if (sums == null) {
			sums =  new int[2][]; }
		if (dimsNotEmpty == null) {
			dimsNotEmpty =  new int[] {nn.length, nn[nn.length >> 1].length}; }
		final int numRows = dimsNotEmpty[0];
		final int numCols = dimsNotEmpty[1];
		final long sum = MatrixInt.SUM_ROWS_COLS(nn, dimsNotEmpty, sums);
		final int[] rowSums = sums[0]; 
		final int[] colSums = sums[1]; 
		entropies[0]=(float)VectorInt.ENTROPY(rowSums, numRows, sum);
		entropies[1]=(float)VectorInt.ENTROPY(colSums, numCols, sum);

		double e = MatrixInt.ENTROPY(nn, numRows, numCols, sum);
		return e;
	}
	
	/** contingency table analysis using chi-square (14.4)
	 * 
	 * @return the Probability for a Correlation between the Colums and the Rows 
	 * based on the Null Hypothesis that both are uncorrelated. 
	 * @param nn the Matrix to analyze
	 */
	final static public double CROSS_TAB_PROBABILITY(final int[][] nn) {
		return CROSS_TAB_PROBABILITY(nn, null, null, null, null, null, null, null);
	}
	
	/** contingency table analysis using chi-square (14.4)
	 * 
	 * @return the Probability for a Correlation between the Colums and the Rows 
	 * based on the Null Hypothesis that both are uncorrelated. 
	 * @param nn the Matrix to analyze
	 * @param dimsNotEmpty 
	 * on  Input: the Number of Rows and Columns to consider
	 * on Output: the Number of Rows and Columns with nonzero Sum
	 */
	final static public double CROSS_TAB_PROBABILITY(final int[][] nn, final int[] dimsNotEmpty) {
		return CROSS_TAB_PROBABILITY(nn, dimsNotEmpty, null, null, null, null, null, null);
	}
	
	/** contingency table analysis using chi-square (14.4)
	 * 
	 * @return the Probability for a Correlation between the Colums and the Rows 
	 * based on the Null Hypothesis that both are uncorrelated. 
	 * @param nn the Matrix to analyze
	 * @param dimsNotEmpty 
	 * on  Input: the Number of Rows and Columns to consider
	 * on Output: the Number of Rows and Columns with nonzero Sum
	 * @param maxDeviation if not null, returns the Position of the maximum Deviation 
	 * @param sums if not null, returns the Row and Column Sums of this Matrix  
	 * @param chisq if not null, returns the Chi� Sum
	 * @param degreesOfFreedom the Degrees of Freedom calculated 
	 * @param cramrv if not null, returns Cramer's V Parameter
	 * @param ccc if not null, returns the Contingency Coefficient
	 */
	final static public double CROSS_TAB_PROBABILITY(final int[][] nn, int[] dimsNotEmpty
	, final int[] maxDeviation, int[][] sums, final long[] degreesOfFreedom_, final float[] chiSqr_, final float[] cramrv, final float[] ccc) {
		//Calculating the Averages...
		if (sums == null) {
			sums =  new int[2][]; }
		if (dimsNotEmpty == null) {
			dimsNotEmpty = new int[] {nn.length, nn[nn.length >> 1].length}; }
		final int numRows = dimsNotEmpty[0];
		final int numCols = dimsNotEmpty[1];
		final long sum = MatrixInt.SUM_ROWS_COLS(nn, dimsNotEmpty, sums);
		final int[] rowSums = sums[0]; 
		final int[] colSums = sums[1]; 
		double chiSqr = CROSS_TAB_CHI_SQR(nn, maxDeviation, numRows, numCols, sum, rowSums, colSums);
		if (chiSqr_ != null) { chiSqr_[0] = (float)chiSqr; }
		long degreesOfFreedom=(dimsNotEmpty[0]-1)*(dimsNotEmpty[1]-1);
		if (degreesOfFreedom_ != null)  { degreesOfFreedom_[0] = degreesOfFreedom; }
		final int minij = dimsNotEmpty[0] < dimsNotEmpty[1] ? dimsNotEmpty[0]-1 : dimsNotEmpty[1]-1;
		if (cramrv != null) { cramrv[0]=(float)Math.sqrt(chiSqr/(sum*minij)); } //these two Strength Values are normed to [0,1] 
		if (ccc != null) { ccc[0]=(float)Math.sqrt(chiSqr/(chiSqr+sum)); } //but intermediate Values can't be interpreted! 
		return GammaP.PROBABILITY_CHI_SQR(degreesOfFreedom, chiSqr);
	}
	
	/**
	 * Computes the chi-square sum of deviations of {@code nn} from the independent
	 * distribution its row and column sums imply.
	 * @param nn the Matrix to analyze
	 * @param maxDeviation if not null, filled with the Position of the maximum Deviation
	 * @param numRows the Number of Rows to consider
	 * @param numCols the Number of Columns to consider
	 * @param sum the total Sum of Elements = Sum(rowSums) = Sum(colSums)
	 * @param rowSums the Sums of the Rows
	 * @param colSums the Sums of the Columns
	 * @return the Chi� Sum of Deviations from a hypothetic independent Distribution
	 * derived from the Row and Column Sums.
	 */
	public static double CROSS_TAB_CHI_SQR(
		final int[][] nn,
		int[] maxDeviation,
		final int numRows,
		final int numCols,
		final long sum,
		final int[] rowSums,
		final int[] colSums) {
		float maxDiff = 0; 
		double chiSqr=0; //use double Accuracy for summing up! 
		for (int i=numRows; --i>=0; ) {
			final int[] row = nn[i];
			final float expectedRow = rowSums[i]/(float)sum;
			for (int j=Math.min(numCols, row.length); --j >= 0; ) {
				final float expected=colSums[j]*expectedRow; //if Dimensions were independent
				final float temp=row[j]-expected;
				if ((expected == 0) && (temp == 0)) {
					continue; } //avoid NaN = 0/0
				final float diff = temp*temp/expected;
				if (maxDeviation != null) {
					if (maxDiff < diff) {
						maxDiff = diff; 
						maxDeviation[0] = i; 
						maxDeviation[1] = j;
					}
				}
				chiSqr += diff; 
			}
		}
		return chiSqr;
	}
	
	/** Student's t-test for same means in the Case of paired (and possibly correlated) data (14.2)
	 */
	final static public double PROB_SAME_MEAN_CORRELATED(
	final float[] data1,
	final float[] data2) {
		if (data1.length != data2.length) {
			throw new RuntimeException("The Sample Sizes must be the same: #data1="+data1.length+" #data2="+data2.length); }
		return PROB_SAME_MEAN_CORRELATED(data1.length, data1, data2);
	}
	
	/** Student's t-test for same means in the Case of paired (and possibly correlated) data (14.2)
	 */
	final static public double PROB_SAME_MEAN_CORRELATED(
		final int length,
		final float[] data1,
		final float[] data2) {
		final double mean1 = VectorFloat.SUM(data1, 0, length) / length;
		final double mean2 = VectorFloat.SUM(data2, 0, length) / length;
		final double var1 = VectorFloat.MOMENT(data1, 2, 0, length, mean1);
		final double var2 = VectorFloat.MOMENT(data2, 2, 0, length, mean2);
		final double cov = VectorFloat.COVARIANCE(length, data1, mean1, data2, mean2);
		return PROB_SAME_MEAN_CORRELATED(length, cov, mean1, var1, mean2, var2);
	}
	
	/** Student's t-test for same means in the Case of paired (and possibly correlated) data (14.2)
	 * The Effect is that the total Variance is to be modified by the CoVariance:
	 * d = (+1,-1)*(X1, X2) 
	 * sd�=(+1,-1)*|sd1 cov|*|+1| = sd1-2cov+sd2
	 *             |cov sd2| |-1|
	 * Because the Sample Size n is typically small, the Student Distribution is used,
	 * instead of the Gaussian Normal Distribution.
	 * 
	 * Similarly also the Sum of two Distributions / Samples can be described by: 
	 * d = (1,1)*(X1, X2) 
	 * sd�=(1,1)*|sd1 cov|*|1| = sd1+2cov+sd2
	 *           |cov sd2| |1|
	 */
	final static public double PROB_SAME_MEAN_CORRELATED(
		final int n,
		final double cov,
		final double ave1, final double var1, 
		final double ave2, final double var2) {
		final double degreesOfFreedom = n - 1; 
		final double sd = Math.sqrt((var1 + var2 - 2 * cov) / n);
		final double t = (ave1 - ave2) / sd; 
		return BetaI.PROBABILITY_STUDENT_T(degreesOfFreedom, t); 
	}
	
	/**
	 * Student's t-test for equal means assuming equal (pooled) variances.
	 * @return the Probability for same (unknown) Mean and same (unknown) Variance
	 * from the Parameters of two Distribution (Distributed like Student's t)
	 *
	 * @param n1  #Elements in the first Sample
	 * @param mean1 measured Mean of the first Sample
	 * @param var1  measured Variance of the first Sample
	 * @param n2  #Elements of the second Sample
	 * @param mean2 measured Mean of the second Sample
	 * @param var2  measured Variance of the second Sample
	 */
	final static public double PROB_SAME_MEAN_VAR(
		final int n1, final double mean1, final double var1,
		final int n2, final double mean2, final double var2) {
		final int degreesOfFreedom = n1 + n2 - 2;
		final double svar = ((n1 - 1) * var1 + (n2 - 1) * var2) / degreesOfFreedom;
		final double t = (mean1 - mean2) / Math.sqrt(svar * (1./n1 + 1./n2));
		L.l(t).l(degreesOfFreedom);
		return BetaI.PROBABILITY_STUDENT_T(degreesOfFreedom, t);
	}
	
	/** Student's t-test for same means in the case of unequal variances (14.2)
	 * for scalar Data. 
	 * 
	 * The Variance for the Difference of two Sample's Means xm-ym 
	 * from Distributions X(x, sx) and Y(y, sy) is sx�+sy� 
	 * so independent Variances add up as independent Dimensions. 
	 * 
	 * @return the Probability for same (unknown) Mean 
	 * from the Parameters of two Distribution (Distributed like Student's t)  
	 * 
	 * @param n1  #Elements in the first Sample 
	 * @param mean1 measured Mean of the first Sample
	 * @param var1  measured Variance of the first Sample
	 * @param n2  #Elements of the second Sample
	 * @param mean2 measured Mean of the second Sample
	 * @param var2  measured Variance of the second Sample
	 */
	final static public double PROB_SAME_MEAN
	( final int n1, final double mean1, final double var1
	, final int n2, final double mean2, final double var2) {
		final double t = (mean1 - mean2) / Math.sqrt(var1 / n1 + var2 / n2);
		final double degreesOfFreedom //double harmonic Mean...   
		= ByRefDouble.SQR(var1 / n1 + var2 / n2) //...weighted by the Ratio of Variances
		/(ByRefDouble.SQR(var1 / n1) / (n1 - 1) 
		+ ByRefDouble.SQR(var2 / n2) / (n2 - 1));
		L.l(t).l(degreesOfFreedom);
		return BetaI.PROBABILITY_STUDENT_T(degreesOfFreedom, t);
	}
	
	/** Student's t-test for same means in the case of unequal variances (14.2)
	 * for Vector Data (Vector Distributions)
	 * t� = (m1-m2) * (v1/n1+v2/n2)^-1 * (m1-m2)
	 * 
	 * This can also be used to Partition Data Sets along a Dimension 
	 * to see whether this Dimension does not significantly change the Means. 
	 * In this Case this Dimension is redundant and can be eliminated.  
	 * 
	 * @return the Probability for same (unknown) Mean 
	 * from the Parameters of two Distribution (Distributed like Student's t)  
	 * 
	 * @param n1  #Elements in the first Sample 
	 * @param mean1 measured Mean of the first Sample
	 * @param var1  measured Variance of the first Sample
	 * @param n2  #Elements of the second Sample
	 * @param mean2 measured Mean of the second Sample
	 * @param var2  measured Variance of the second Sample
	 */
	final static public double PROB_SAME_MEAN
	( final int n1, final double[] mean1, final double[][] coVar1
	, final int n2, final double[] mean2, final double[][] coVar2) {
		final double[] dMean = VectorDouble.SUB(mean1, mean2); 
		//avoid biLinear Combination: coVar1/n1 + coVar2/n2
		final double[][] weighVar = MatrixDouble.ADD_PROD_AT(coVar1, coVar2, ((double) n1)/n2);
		final MatrixDouble matrix = new MatrixDouble(weighVar); 
		final double[] solution = VectorDouble.COPY(dMean);
		matrix.solveAt(solution); //solve the System A*w=v for the right Side w
		final double prod = VectorDouble.MAP(solution, dMean); //d * (A^-1 * d)
		final double t = Math.sqrt(prod); //need to use the double-sided Test!
		//TODO: unclear how to calculate var: Trace (since positive definite), Squared Sum or whatever)
		final double var1 = MatrixDouble.TRACE(coVar1); 
		final double var2 = MatrixDouble.TRACE(coVar2); 
		final double degreesOfFreedom //double harmonic Mean...   
		= ByRefDouble.SQR(var1 / n1 + var2 / n2) //...weighted by the Ratio of Variances
		/(ByRefDouble.SQR(var1 / n1) / (n1 - 1) 
		+ ByRefDouble.SQR(var2 / n2) / (n2 - 1));
		L.l(t).l(degreesOfFreedom);
		return BetaI.PROBABILITY_STUDENT_T(degreesOfFreedom, t);
	}
	
	/** Student's t-test for same means in the case of unequal variances (14.2)
	 * @return the Probability for same (unknown) Mean 
	 * from the Parameters of two Distribution (Distributed like Student's t)  
	 * 
	 * @param data1 first Data Set 
	 * @param data2 second Data Set 
	 */
	final static public double PROB_SAME_MEAN(
	final float[] data1, final float[] data2) {
		return PROB_SAME_MEAN(data1, data1.length, data2, data2.length); }
	
	/** Student's t-test for same means in the case of unequal variances (14.2)
	 * @return the Probability for same (unknown) Mean 
	 * from the Parameters of two Distribution (Distributed like Student's t)  
	 * 
	 * @param data1 first Data Set 
	 * @param n1 number of Items to consider from the first Data Set  
	 * @param data2 second Data Set 
	 * @param n2 number of Items to consider from the second Data Set 
	 */
	final static public double PROB_SAME_MEAN(
		final float[] data1, final int n1,
		final float[] data2, final int n2) {
		final double mean1 = VectorFloat.SUM(data1, 0, n1) / n1;
		final double mean2 = VectorFloat.SUM(data2, 0, n2) / n2;
		final double var1 = VectorFloat.MOMENT(data1, 2, 0, n1, mean1);
		final double var2 = VectorFloat.MOMENT(data2, 2, 0, n2, mean2);
		return PROB_SAME_MEAN(n1, mean1, var1, n2, mean2, var2);
	}
	
	/** Student's t-test for same means in the case of unequal variances (14.2)
	 * 
	 * @param data1 first Data Set 
	 * @param data2 second Data Set 
	 * @return the Probability 
	 */
	final static public double PROB_SAME_MEAN_VAR(
	final float[] data1, final float[] data2) {
		return PROB_SAME_MEAN_VAR(data1, data1.length, data2, data2.length); }
	
	/** Student's t-test for same means in the case of equal variances (14.2)
	 * 
	 * @param data1 first Data Set 
	 * @param n1 number of Items to consider from the first Data Set  
	 * @param data2 second Data Set 
	 * @param n2 number of Items to consider from the second Data Set 
	 * @return the Probability 
	 */
	final static public double PROB_SAME_MEAN_VAR(
		final float[] data1, final int n1,
		final float[] data2, final int n2) {
		final double mean1 = VectorFloat.SUM(data1, 0, n1) / n1;
		final double mean2 = VectorFloat.SUM(data2, 0, n2) / n2;
		final double var1 = VectorFloat.MOMENT(data1, 2, 0, n1, mean1);
		final double var2 = VectorFloat.MOMENT(data2, 2, 0, n2, mean2);
		return PROB_SAME_MEAN_VAR(n1, mean1, var1, n2, mean2, var2);
	}
	
	/** 
	 * Student's t Parameter is calculated, but not returned
	 * @return the Probability that arg has the same Mean as this Vector 
	 * @param arg the Vector to compare with 
	 * @return the Probability that this Vector and arg have the same Mean
	 */
	final static public double PROB_SAME_MEAN(final VectorFloat ths, final VectorFloat arg) {
		final double thisMean = ths.getMean();
		final double thisVar  = ths.getVariance(thisMean);
		final double argMean  = arg.getMean();
		final double argVar   = arg.getVariance(thisMean);
		final int thisN = ths.getInt() - 1;
		final int argN  = arg.getInt() - 1;
		return PROB_SAME_MEAN(thisN, thisMean, thisVar, argN, argMean, argVar);
	}
	
	/** 
	 * Student's t Parameter is calculated, but not returned
	 * @return the Probability that arg has the same Mean as this Vector 
	 * @param arg the Vector to compare with 
	 * @return the Probability that this Vector and arg have the same Mean
	 */
	final static public double PROB_SAME_MEAN_VAR(final VectorFloat ths, final VectorFloat arg) {
		final double thisMean = ths.getMean();
		final double thisVar = ths.getVariance(thisMean);
		final double argMean = arg.getMean();
		final double argVar = arg.getVariance(thisMean);
		final int thisN = ths.getInt() - 1;
		final int argN = arg.getInt() - 1;
		return PROB_SAME_MEAN_VAR(thisN, thisMean, thisVar, argN, argMean, argVar);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**	F-test for difference of variances (14.2) 
	 * Testing of Hypothesis that both Variances are equal
	 */
	final static public double PROB_SAME_VARIANCE (
	final float[] data1, 
	final float[] data2) { return PROB_SAME_VARIANCE(
		data1, data1.length,  
		data2, data2.length);
	}

	/**	F-test for difference of variances (14.2) 
	 * Testing of Hypothesis that both Variances are equal
	 */
	final static public double PROB_SAME_VARIANCE (
	final float[] data1, int n1, 
	final float[] data2, int n2) {
		final double mean1 = VectorFloat.SUM(data1, 0, n1) / n1;
		final double mean2 = VectorFloat.SUM(data2, 0, n2) / n2;
		final double var1 = VectorFloat.MOMENT(data1, 2, 0, n1, mean1);
		final double var2 = VectorFloat.MOMENT(data2, 2, 0, n2, mean2);
		return PROB_SAME_VARIANCE(n1, mean1, var1, n2, mean2, var2);
	}

	/**	F-test for difference of variances (14.2) 
	 * Testing of Hypothesis that both Variances are equal
	 */
	final static public double PROB_SAME_VARIANCE (
	final int n1, final double ave1, final double var1, 
	final int n2, final double ave2, final double var2) {
		final double df1,df2;
		final double f; //>1
		if (var1 > var2) {
			f=var1/var2;
			df1=n1-1;
			df2=n2-1;
		} else {
			f=var2/var1;
			df1=n2-1;
			df2=n1-1;
		}
		//double the Prob. for 2-sided Tests
		final double prob = 2*BetaI.PROBABILITY_FISHER_F(df1, df2, f); //
		if (prob > 1) { //two-sided Test 
			return 2-prob; } 
		return prob; 
	}
	
	/**	chi-square test for difference between data and model (14.3)
	 * This is usually more accurate than simply checking for the same Mean and Variance, 
	 * because it considers all Elements in the Sample (but not their Sequence) 
	 * 
	 * @param bins the actual Frequencies of Events 
	 * @param eBins the expected Frequencies of Events
	 * @param numBins the Number of Bins
	 * @param numConstraints The Number of Constraints applied on the Model, 
	 * e.g. by fitting the total expected to the actual Frequencies    
	 * @return the Probability that the given Data stems from a Source described by the Model. 
	 */
	final static public double PROB_SAMPLE_FROM_MODEL(final int bins[], final float eBins[], final int numConstraints) {
		if (bins.length != eBins.length) {
			throw new RuntimeException("The Number of Bins must be the same: #bins="+bins.length+" #eBins="+eBins.length); }
		return PROB_SAMPLE_FROM_MODEL(bins, eBins, eBins.length, numConstraints); 
	}
	
	/**	chi-square test for the Probility that the data is from a uniform Distribution
	 * 
	 * @param aBins the actual   Frequencies of Events 
	 * @param numConstraints The Number of Constraints applied on the Model, 
	 * e.g. by fitting the total expected to the actual Frequencies 
	 * @return the Probability that the given Data stems from a Source with uniform Distribution.
	 */
	final static public double PROB_SAMPLE_FROM_UNIFORM(final int bins[]) {
		return PROB_SAMPLE_FROM_UNIFORM(bins, bins.length); }
	
	/**	chi-square test for the Probility that the data is from a uniform Distribution
	 * 
	 * @param aBins the actual   Frequencies of Events 
	 * @param numBins the Number of Bins to consider
	 * @param numConstraints The Number of Constraints applied on the Model, 
	 * e.g. by fitting the total expected to the actual Frequencies 
	 * @return the Probability that the given Data stems from a Source with uniform Distribution. 
	 * Rises from [0,0] through [numBins, 0.5] to [Infinity, 1]. 
	 * Any Value significantly different from 50% should be considered suspiciously, 
	 * since it indicates a Fit that is better than to expect or too bad to fit! 
	 */
	final static public double PROB_SAMPLE_FROM_UNIFORM(final int aBins[], final int numBins) {
		final int numConstraints = 1;
		final long num = VectorInt.SUM(aBins, numBins);
		double chiSqr = (numBins*VectorInt.NORM_SQR(aBins,numBins)-num*num)/(double)num; //0;
		/* final float eBins =  num/(float)numBins; 
		for (int j=numBins; --j >= 0; ) {
			if (aBins[j] <= 0) 
				throw new RuntimeException("actual   Frequency not positive: aBins["+j+"]="+aBins[j]); 
			final float temp = aBins[j]-eBins; //for numerical Stability
			chiSqr += temp*temp; //
		}
		chiSqr/=eBins;
		*/
		L.n("chi-squared:").l(chiSqr);
		final int degreesOfFreedom=numBins-numConstraints;
		return GammaP.PROBABILITY_CHI_SQR(degreesOfFreedom, chiSqr);
	}
	
	/**	chi-square test for difference between data and model (14.3)
	 * This is usually more accurate than simply checking for the same Mean and Variance, 
	 * because it considers all Elements in the Sample (but not their Sequence) 
	 * 
	 * @param aBins the actual   Frequencies of Events 
	 * @param eBins the expected Frequencies of Events
	 * @param numBins the Number of Bins to consider
	 * @param numConstraints The Number of Constraints applied on the Model, 
	 * e.g. by fitting the total expected to the actual Frequencies    
	 * @return the Probability that the given Data stems from a Source described by the Model. 
	 * It falls from 1 to 0 with increasing Chi�. 
	 */
	final static public double PROB_SAMPLE_FROM_MODEL(final int aBins[], final float eBins[]
	, final int numBins, final int numConstraints) {
		double chiSqr = 0;
		for (int j=numBins; --j >= 0; ) {
			if (eBins[j] <= 0) 
				throw new RuntimeException("expected Frequency not positive: eBins["+j+"]="+eBins[j]); 
			if (aBins[j] <= 0) 
				throw new RuntimeException("actual   Frequency not positive: aBins["+j+"]="+aBins[j]); 
			final double diff = aBins[j]-eBins[j];
			chiSqr += diff*diff/eBins[j]; //this formula cannot simply use the SqrDiff
		}
		L.n("chi-squared:").l(chiSqr);
		final int degreesOfFreedom = numBins - numConstraints;
		return GammaP.PROBABILITY_CHI_SQR(degreesOfFreedom, chiSqr);
	}
	
	/**	chi-square test for difference between the Bin Counts of two Samples (14.3) 
	 * This is usually more accurate than simply checking for the same Mean and Variance, 
	 * because it considers all Elements in the Sample (but not their Sequence) 
	 * 
	 * The Frequencies don't need to be normed, so that Sum(bins1) = Sum(bins2), 
	 * but if normed, the Degrees of Freedom decreases by 1. 
	 * @param bins1 the actual Frequencies of Events 
	 * @param bins2 the actual Frequencies of Events 
	 * @param numBins the Number of Bins
	 * @param numConstraints The Number of Constraints applied on the Model, 
	 * e.g. by fitting the total Sum of both Frequencies 
	 * @return the Probability that both Samples stems the same Source. 
	 */
	final static public double PROB_SAMPLES_SAME(final int bins1[], final int bins2[], final int numConstraints) {
		if (bins1.length != bins2.length) {
			throw new RuntimeException("The Number of Bins must be the same: #bins1="+bins1.length+" #bins2="+bins2.length); }
		return PROB_SAMPLES_SAME(bins1, bins2, bins1.length, numConstraints); }
	
	/**	chi-square test for difference between the Bin Counts of two Samples (14.3) 
	 * This is usually more accurate than simply checking for the same Mean and Variance, 
	 * because it considers all Elements in the Sample 
	 * (but not their Sequence! Use Correlated Checks for this!) 
	 * 
	 * The Frequencies don't need to be normed, so that Sum(bins1) = Sum(bins2), 
	 * but if normed, the Degrees of Freedom decreases by 1. 
	 * @param bins1 the actual Frequencies of Events 
	 * @param bins2 the actual Frequencies of Events 
	 * @param numBins the Number of Bins
	 * @param numConstraints The Number of Constraints applied on the Model, 
	 * e.g. by fitting the total Sum of both Frequencies 
	 * @return the Probability that both Samples stems the same Source. 
	 */
	final static public double PROB_SAMPLES_SAME(final int bins1[], final int bins2[]
	, final int numBins, final int numConstraints) {
		int degreesOfFreedom=numBins-numConstraints;
		double chiSqr = 0;
		for (int j=numBins; --j>=0; ) {
			if ((bins1[j] == 0) && 
				(bins2[j] == 0))
				--degreesOfFreedom;
			else {
				final double   temp= bins1[j]-bins2[j];
				chiSqr += temp*temp/(bins1[j]+bins2[j]);
			}
		}
		return GammaP.PROBABILITY_CHI_SQR(degreesOfFreedom, chiSqr);
	}
	
	/** counts the given Data points by 2D quadrants, defined by (x,y)
	 * @see #PROB_2D_SAMPLES_SAME(float[], float[], int, float[], float[], int) 
	 * uses this Method. 
	 * 
	 * @param x	the Intersection of the x Axis
	 * @param y	the Intersection of the y Axis
	 * @param xx the corresponding x Data Samples
	 * @param yy the corresponding y Data Samples
	 * @param counts the Counts in the four Quadrants (add up to xx.length)
	 */
	final static public void COUNT_QUADRANTS
	( final float x, final float y
	, final float xx[], final float yy[], final int[] counts) {
		COUNT_QUADRANTS(x, y, xx, yy, 0, yy.length, counts); }
	
	/** counts the given Data points by 2D quadrants, defined by (x,y)
	 * @see #PROB_2D_SAMPLES_SAME(float[], float[], int, float[], float[], int) 
	 * uses this Method. 
	 * 
	 * @param x	the Intersection of the x Axis
	 * @param y	the Intersection of the y Axis
	 * @param xx the corresponding x Data Samples
	 * @param yy the corresponding y Data Samples
	 * @param counts the Counts in the four Quadrants (add up to stop-start)
	 */
	final static public void COUNT_QUADRANTS
	( final float x, final float y
	, final float xx[], final float yy[], final int start, final int stop
	, final int[] counts) {
		int na,nb,nc,nd;
		na=nb=nc=nd=0;
		for (int k=stop; --k>=start;) {
			if (yy[k] > y) {
				if (xx[k] > x) { ++na; } else { ++nb; } 
			} else {
				if (xx[k] > x) { ++nd; } else { ++nc; } 
			}
		}
		counts[0]=na;
		counts[1]=nb;
		counts[2]=nc;
		counts[3]=nd;
	}
	
	/** counts the given Data points by 2D quadrants, defined by (x,y)
	 * @see #PROB_2D_SAMPLES_SAME(float[], float[], int, float[], float[], int) 
	 * uses this Method. 
	 * 
	 * @param x	the Intersection of the x Axis
	 * @param y	the Intersection of the y Axis
	 * @param xx the corresponding x Data Samples
	 * @param yy the corresponding y Data Samples
	 * @param counts the Counts in the four Quadrants (add up to stop-start)
	 */
	final static public void COUNT_QUADRANTS( final float x, final float y
	, final float xy[][], final int xCol, final int yCol
	, final int[] counts) {
		COUNT_QUADRANTS(x, y, xy, xCol, yCol, 0, xy.length, counts); }
	
	/** counts the given Data points by 2D quadrants, defined by (x,y)
	 * @see #PROB_2D_SAMPLES_SAME(float[], float[], int, float[], float[], int) 
	 * uses this Method. 
	 * 
	 * @param x	the Intersection of the x Axis
	 * @param y	the Intersection of the y Axis
	 * @param xx the corresponding x Data Samples
	 * @param yy the corresponding y Data Samples
	 * @param counts the Counts in the four Quadrants (add up to stop-start)
	 */
	final static public void COUNT_QUADRANTS
	( final float x, final float y
	, final float xy[][], final int xCol, final int yCol
	, final int start, final int stop, final int[] counts) {
		int na,nb,nc,nd;
		na=nb=nc=nd=0;
		for (int k=stop; --k>=start;) {
			if (xy[k][yCol] > y) {
				if (xy[k][xCol] > x) { ++na; } else { ++nb; } 
			} else {
				if (xy[k][xCol] > x) { ++nd; } else { ++nc; } 
			}
		}
		counts[0]=na;
		counts[1]=nb;
		counts[2]=nc;
		counts[3]=nd;
	}
	
	/** Kolmogorov-Smirnov Test in two Dimensions, Data vs. Data (14.7)
	 * This is usually more accurate than simply checking for the same Mean and Variance, 
	 * because it considers all Elements in the Sample (but not their Sequence) 
	 * @param x1 x-Coordinates of the first Sample 
	 * @param y1 y-Coordinates of the first Sample 
	 * @param x2 x-Coordinates of the second Sample 
	 * @param y2 y-Coordinates of the second Sample 
	 * @return the Probability that both Samples are from the same Distribution (Null Hypothesis)
	 */
	final static public double PROB_2D_SAMPLES_SAME
	( float[] x1, float[] y1, int start1, int stop1
	, float[] x2, float[] y2, int start2, int stop2) {
		//first use Points in (x1,y1) as Origins...
		float d1 = MAX_2D_DIFF(x1, y1, start1, stop1, x2, y2, start2, stop2);
		//...then use Points in (x2,y2) as Origins
		float d2 = MAX_2D_DIFF(x2, y2, start2, stop2, x1, y1, start1, stop1);
		final float d=0.5f*(d1+d2);
		final int num1 = stop1-start1; 
		final int num2 = stop2-start2; 
		final double sqen=Math.sqrt(num1*num2/(float)(num1+num2));
		double r1 = Correlation.CORRELATION(x1,y1,start1,stop1);
		double r2 = Correlation.CORRELATION(x2,y2,start2,stop2);
		double rr=Math.sqrt(1-0.5*(r1*r1+r2*r2));
		double x=d*sqen/(1+rr*(0.25-0.75/sqen));
		return ProbFuncs.pKvSvCum(x);
	}

	/** Kolmogorov-Smirnov Test in two Dimensions, Data vs. Data (14.7)
	 * This is usually more accurate than simply checking for the same Mean and Variance, 
	 * because it considers all Elements in the Sample (but not their Sequence) 
	 * @param xy1 Coordinates of the first Sample 
	 * @param xy2 Coordinates of the second Sample 
	 * @return the Probability that both Samples are from the same Distribution (Null Hypothesis)
	 */
	final static public double PROB_2D_SAMPLES_SAME
	( float[][] xy1, int xCol1, int yCol1
	, float[][] xy2, int xCol2, int yCol2) {
		return PROB_2D_SAMPLES_SAME(xy1, xCol1, yCol1, 0, xy1.length, xy2, xCol2, yCol2, 0, xy2.length); }

	/** Kolmogorov-Smirnov Test in two Dimensions, Data vs. Data (14.7)
	 * This is usually more accurate than simply checking for the same Mean and Variance, 
	 * because it considers all Elements in the Sample (but not their Sequence) 
	 * @param xy1 Coordinates of the first Sample 
	 * @param xy2 Coordinates of the second Sample 
	 * @return the Probability that both Samples are from the same Distribution (Null Hypothesis)
	 */
	final static public double PROB_2D_SAMPLES_SAME
	( float[][] xy1, int xCol1, int yCol1, int start1, int stop1
	, float[][] xy2, int xCol2, int yCol2, int start2, int stop2) {
		//first use Points in (x1,y1) as Origins...
		float d1 = MAX_2D_DIFF(xy1, xCol1, yCol1, start1, stop1, xy2, xCol2, yCol2, start2, stop2);
		//...then use Points in (x2,y2) as Origins
		float d2 = MAX_2D_DIFF(xy2, xCol2, yCol2, start2, stop2, xy1, xCol1, yCol1, start1, stop1);
		final float d=0.5f*(d1+d2);
		final int num1 = stop1-start1; 
		final int num2 = stop2-start2; 
		final double sqen=Math.sqrt(num1*num2/(float)(num1+num2));
		double r1 = Correlation.CORRELATION(xy1,xCol1,yCol1,start1,stop1);
		double r2 = Correlation.CORRELATION(xy2,xCol2,yCol2,start2,stop2);
		double rr=Math.sqrt(1-0.5*(r1*r1+r2*r2));
		double x=d*sqen/(1+rr*(0.25-0.75/sqen));
		return ProbFuncs.pKvSvCum(x);
	}

	/** O(n�) Algorithm  
	 * @return the maximum absolute Difference in Quadrant Counts
	 * when partitioning one Distribution by the other
	 */
	final static public float MAX_2D_DIFF(float[] x1, float[] y1, float[] x2, float[] y2) {
		return MAX_2D_DIFF(x1, y1, 0, x1.length, x2, y2, 0, x2.length); }

	/** O(n�) Algorithm with n = #of Points  
	 * @return the maximum absolute Difference in Quadrant Counts
	 * when partitioning one Distribution by the other
	 */
	final static public float MAX_2D_DIFF(
		float[] x1, float[] y1, int start1, int stop1, 
		float[] x2,	float[] y2, int start2, int stop2) {
		final int[] counts1 = new int[4];  
		final int[] counts2 = new int[4];  
		final float n1Inv = 1f/(stop1-start1); 
		final float n2Inv = 1f/(stop2-start2); 
		float d1=0;
		for (int j=stop1; --j>=start1;) {
			COUNT_QUADRANTS(x1[j],y1[j],x1,y1,start1,stop1,counts1);
			COUNT_QUADRANTS(x1[j],y1[j],x2,y2,start2,stop2,counts2); 
			//Check Distances in all 4 Quadrants
			d1=Math.max(d1,Math.abs(counts1[0]*n1Inv-counts2[0]*n2Inv));
			d1=Math.max(d1,Math.abs(counts1[1]*n1Inv-counts2[1]*n2Inv));
			d1=Math.max(d1,Math.abs(counts1[2]*n1Inv-counts2[2]*n2Inv));
			d1=Math.max(d1,Math.abs(counts1[3]*n1Inv-counts2[3]*n2Inv));
		}
		return d1;
	}

	/** O(n�) Algorithm  
	 * @return the maximum absolute Difference in Quadrant Counts
	 * when partitioning one Distribution by the other
	 */
	final static public float MAX_2D_DIFF
	( float[][] xy1, int xCol1, int yCol1
	, float[][] xy2, int xCol2, int yCol2) {
		return MAX_2D_DIFF(xy1, xCol1, yCol1, 0, xy1.length, xy2, xCol2, yCol2, 0, xy2.length);
	}

	/** O(n�) Algorithm  
	 * @return the maximum absolute Difference in Quadrant Counts
	 * when partitioning one Distribution by the other
	 */
	final static public float MAX_2D_DIFF
	( float[][] xy1, int xCol1, int yCol1, int start1, int stop1
	, float[][] xy2, int xCol2, int yCol2, int start2, int stop2) {
		final int[] counts1 = new int[4]; 
		final int[] counts2 = new int[4]; 
		final float n1Inv = 1f/(stop1-start1); 
		final float n2Inv = 1f/(stop2-start2); 
		float d1=0;
		for (int j=stop1; --j>=start1;) {
			final float[] xy1_j = xy1[j];  
			COUNT_QUADRANTS(xy1_j[xCol1], xy1_j[yCol1], xy1,xCol1,yCol1,start1,stop1,counts1);
			COUNT_QUADRANTS(xy1_j[xCol1], xy1_j[yCol1], xy2,xCol2,yCol2,start2,stop2,counts2); 
			//Check Distances in all 4 Quadrants
			d1=Math.max(d1,Math.abs(counts1[0]*n1Inv-counts2[0]*n2Inv));
			d1=Math.max(d1,Math.abs(counts1[1]*n1Inv-counts2[1]*n2Inv));
			d1=Math.max(d1,Math.abs(counts1[2]*n1Inv-counts2[2]*n2Inv));
			d1=Math.max(d1,Math.abs(counts1[3]*n1Inv-counts2[3]*n2Inv));
		}
		return d1;
	}
	
	/** create two perpendicular Distributions, 
	 * add varying Levels of Noise to it
	 * and test for the Probability that both are from the same Source
	 */
	private static final void testSampleFrom2dModel(){
		final int NMAX = 2000; 
		RandomQuick.RANDOM.randomize();
		final float[][] xy=new float[NMAX][2];
		final int ntrial = 10;
		L.n("Sample Size:").l(xy.length); 
		L.n("#Trials:").l(ntrial);
		int iMin = -1; double minProb = Double.POSITIVE_INFINITY;
		int iMax = -1; double maxProb = Double.NEGATIVE_INFINITY;
		for (int jtrial=0;jtrial<=ntrial;jtrial++) {
			final float factor = jtrial/(float)ntrial;
			L.n("Factor of Non-Uniformity (0 to 1):").l(factor);
			for (int j=xy.length; --j>=0; ) {
				float u=RandomQuick.NEXT_FLOAT();
				u*=((1-factor)+u*factor);
				xy[j][0]=2*u-1;
				float v=RandomQuick.NEXT_FLOAT();
				v*=((1-factor)+v*factor);
				xy[j][1]=2*v-1;
			}
			final double prob = PROB_2D_SAMPLE_FROM_MODEL(new HyperCubeShare(), xy, 0, 1);
			if (minProb > prob) {
				minProb = prob; iMin = jtrial; }
			if (maxProb < prob) {
				maxProb = prob; iMax = jtrial; }
			L.l("	prob=").l(prob);
		}
		Assert.EQUALS(10, iMin);
		Assert.EQUALS(0, iMax);
		Assert.EQUALS(0, minProb); //at least with more than 100 Points!!
		Assert.IS_TRUE(maxProb > 0.04112149711228663);
	}
	
	/** create two perpendicular Distributions, 
	 * add varying Levels of Noise to it
	 * and test for the Probability that both are from the same Source
	 */
	private static final void testSampleFrom2dDistribution(){
		final int NMAX = 3000; 
		RandomGauss.RANDOM.randomize();
		final float[][] xy1= new float[NMAX][2];
		final float[][] xy2= new float[NMAX][2];
		final int ntrial = 20;
		L.n("Sample 1 Size:").l(xy1.length); 
		L.n("Sample 2 Size:").l(xy2.length); 
		L.n("#Trials:").l(ntrial);
		int iMin = -1; double minProb = Double.POSITIVE_INFINITY;
		int iMax = -1; double maxProb = Double.NEGATIVE_INFINITY;
		for (int jtrial=0; jtrial<=ntrial; jtrial++) {
		    final float ratio = (2f*jtrial)/ntrial; //straight Lines for shrink = 0, max. Mingling for shrink = 1, then dropping again 
			for (int j=xy1.length; --j>=0; ) {
			    final float u=RandomGauss.NEXT_FLOAT();
			    final float v=RandomGauss.NEXT_FLOAT()*ratio;
				xy1[j][0]=u+v; //rotate by 45�
				xy1[j][1]=u-v;
			}
			for (int j=xy2.length; --j>=0; ) {	//perpendicular!
			    final float u=RandomGauss.NEXT_FLOAT()*ratio;
			    final float v=RandomGauss.NEXT_FLOAT();
				xy2[j][0]=u+v; //rotate by 45�
				xy2[j][1]=u-v;
			}
			final double prob = PROB_2D_SAMPLES_SAME(xy1, 0, 1, xy2, 0, 1);
			if (minProb > prob) {
				minProb = prob; iMin = jtrial; }
			if (maxProb < prob) {
				maxProb = prob; iMax = jtrial; }
			L.n("ratio= ").l(ratio).l("	prob= ").l(prob);
		}
		Assert.EQUALS( 0, iMin);
		Assert.EQUALS(10, iMax);
		Assert.EQUALS( 0, minProb); //at least with more than 100 Points!!
		Assert.IS_TRUE(maxProb > 0.033);
	}
	
	/**	Kolmogorov-Smirnov test of data against model (14.7)
	 * This is usually more accurate than simply checking for the same Mean and Variance, 
	 * because it considers all Elements in the Sample (but not their Sequence) 
	 * 
	 * @param data Sample 
	 * @param n number of Elements to consider in the Sample
	 * @return the Probability that both Samples are from the same Distribution (Null Hypothesis)
	 */
	final static public double PROB_2D_SAMPLE_FROM_MODEL(final IFloatVectorField model
	, final float xy[][], final int xCol, final int yCol) {
		return PROB_2D_SAMPLE_FROM_MODEL(model, xy, xCol, yCol, 0, xy.length); }
	
	/**	Kolmogorov-Smirnov test of data against model (14.7)
	 * This is usually more accurate than simply checking for the same Mean and Variance, 
	 * because it considers all Elements in the Sample (but not their Sequence) 
	 * 
	 * @param data Sample 
	 * @param n number of Elements to consider in the Sample
	 * @return the Probability that both Samples are from the same Distribution (Null Hypothesis)
	 */
	final static public double PROB_2D_SAMPLE_FROM_MODEL(final IFloatVectorField model
	, final float xy[][], final int xCol, final int yCol, final int start, final int stop) {
		final int[] counts = new int[4]; 		
		final float[] shares = new float[4]; 
		final float n1 = stop-start;
		final float n1Inv = 1/n1;
		 
		float d1=0;
		for (int j=stop; --j>=start; ) {
			final float[] xy_j = xy[j];
			COUNT_QUADRANTS(xy_j[xCol], xy_j[yCol], xy, xCol, yCol, start, stop, counts);
			model.map(xy_j,shares);
			d1=Math.max(d1, Math.abs(counts[0]*n1Inv-shares[0]));
			d1=Math.max(d1, Math.abs(counts[1]*n1Inv-shares[1]));
			d1=Math.max(d1, Math.abs(counts[2]*n1Inv-shares[2]));
			d1=Math.max(d1, Math.abs(counts[3]*n1Inv-shares[3]));
		}
		final double r1 = Correlation.CORRELATION(xy,0,1,start,stop);
		final double sqen=Math.sqrt(n1);
		final double rr=Math.sqrt(1-r1*r1);
		final double x = d1*sqen/(1+rr*(0.25-0.75/sqen));
		return ProbFuncs.pKvSvCum(x);
	}
	
	/**	Kolmogorov-Smirnov test of data against model (14.3)
	 * This is usually more accurate than simply checking for the same Mean and Variance, 
	 * because it considers all Elements in the Sample (but not their Sequence) 
	 * 
	 * @param data Sample 
	 * @param n number of Elements to consider in the Sample
	 * @return the Probability that both Samples are from the same Distribution (Null Hypothesis)
	 */
	final static public double PROB_SAMPLE_FROM_DISTRIBUTION(final float data[], final IFloatFunction func) {
		return PROB_SAMPLE_FROM_DISTRIBUTION(data, data.length, func); }
	
	/**	Kolmogorov-Smirnov test of data against model (14.3)
	 * This is usually more accurate than simply checking for the same Mean and Variance, 
	 * because it considers all Elements in the Sample (but not their Sequence) 
	 * 
	 * @param data Sample, sorted during Processing 
	 * @param n number of Elements to consider in the Sample
	 * @return the Probability that both Samples are from the same Distribution (Null Hypothesis)
	 */
	final static public double PROB_SAMPLE_FROM_DISTRIBUTION(final float data[], final int n, final IFloatFunction func) {
		HunterFloat.SORT(data, n-1, 0);
		final float en=n;
		float xOld=0;
		float dMax = 0;
		for (int j=n; --j>=0; ) {
			final float xNew=j/en;
			final float ff=func.Map(data[j]);
			final float d=Math.max(Math.abs(xOld-ff), Math.abs(xNew-ff));
			if (dMax < d) {
				dMax = d; } 
			xOld=xNew;
		}
		final double sqRtN = Math.sqrt(en);
		final double x=(sqRtN+0.12+0.11/sqRtN)*dMax;
		return ProbFuncs.pKvSvCum(x);
	}
	
	/**	Kolmogorov-Smirnov test between two data sets (14.3)
	 * This is usually more accurate than simply checking for the same Mean and Variance, 
	 * because it considers all Elements in the Sample (but not their Sequence) 
	 * 
	 * @param data1 first  Sample, sorted during Processing  
	 * @param n1 number of Elements to consider in the first  Sample 
	 * @param data2 second Sample, sorted during Processing  
	 * @param n2 number of Elements to consider in the second Sample
	 * @return the Probability that both Samples are from the same Distribution (Null Hypothesis)
	 */
	final static public double PROB_SAMPLES_SAME(final float[] data1, final float[] data2) {
		return PROB_SAMPLES_SAME(data1, data1.length, data2, data2.length); }
	
	/**	O(nLog(n)) Kolmogorov-Smirnov test between two data sets (14.3)
	 * This is usually more accurate than simply checking for the same Mean and Variance, 
	 * because it considers all Elements in the Sample (but not their Sequence) 
	 * 
	 * @param data1 first Sample 
	 * @param n1 number of Elements to consider in the first Sample 
	 * @param data2 second Sample 
	 * @param n2 number of Elements to consider in the second Sample
	 * @return the Probability that both Samples are from the same Distribution (Null Hypothesis)
	 */
	final static public double PROB_SAMPLES_SAME(final float[] data1, final int n1, final float[] data2, final int n2) {
		//Sorting prepares the Data so it can be tested in one Sweep!
		float fn1=0; final float en1 = n1; HunterFloat.SORT(data1, n1-1, 0); //Arrays.sort(data1, 0, n1);
		float fn2=0; final float en2 = n2; HunterFloat.SORT(data2, n2-1, 0); //Arrays.sort(data2, 0, n2);
		float dMax = 0;
		for (int j1=0, j2=0; (j1 < n1) && (j2 < n2); ) {
			final float d1=data1[j1];
			final float d2=data2[j2]; 
			if (d1 <= d2) { fn1=j1++/en1; } 
			if (d2 <= d1) { fn2=j2++/en2; } 
			final float d = Math.abs(fn2-fn1);
			if (dMax < d) 
				dMax = d; 
		}
		final double sqRtN = Math.sqrt(en1*en2/(en1+en2));
		final double x=(sqRtN+0.12+0.11/sqRtN)*dMax;
		return ProbFuncs.pKvSvCum(x);
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Implementation of IFloatFunction for testKolmogorovSmirnov()
	/////////////////////////////////////////////////////////////////////////////////////
	
    /** Reports that this function is strictly ascending, as required by the K-S test model role.
     * @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_ASC_STRICT; }

	/** Maps {@code x} through the Gaussian cumulative distribution, rescaled to [-1, 1].
	 * @see function.IFloatFunction#Map(double)	 */
	public double Map(final double x) { return Gauss.pGaussCum(x)*2-1; }

	/** Maps {@code arg} through the Gaussian cumulative distribution, rescaled to [-1, 1].
	 * @see function.IFloatFunction#Map(float)	 */
	public float Map(float arg) { return (float) Map((double) arg); }

	/** tests a Distribution against the Model	 */
	private static final void testKolmogorovSmirnov(){
		final int NSTEP = 5;
		final int NPTS = 5000; //warum stirbt QuickSort???
		final float EPS = 0.1f;
		
		final float[] data0=new float[1+NPTS];
		final float[] data1=new float[1+NPTS];
		L.n("variance ratio").l("	k-s statistic").l("	probability");
		final RandomQuick ran0 = new RandomQuick(); 
		final IStreamIn_Float ran = new RandomGauss((IStreamIn_Bound_Int)ran0);
		for(int k = 100; --k >= 0;) {
			ran0.randomize(); 
			for (int j=data1.length; --j>=0; ) {
				data0[j]=Math.abs(ran.nextFloat()); // RandomGauss.NEXT_FLOAT()); 
			} 
			for (int i=-NSTEP; ++i<NSTEP;) {
				final float varnce=1+i*EPS;
				final float factr=(float) Math.sqrt(varnce);
				for (int j=data1.length; --j>=0; ) {
					data1[j]=factr*Math.abs(ran.nextFloat()); // RandomGauss.NEXT_FLOAT()); 
				} 
				final double prob1 = PROB_SAMPLE_FROM_DISTRIBUTION(data1, NPTS, new StatisticsFloat());// Gauss());
				final double prob2 = PROB_SAMPLES_SAME(data1, NPTS, data0, NPTS);// Gauss());
				L.n().l(varnce).l(prob1).l(prob2);
			}
		}
	}
	
	/** tests the Testing of discrete Data agains a Model	 */ 
	private static final void testChiSquare() {
		final int NBINS = 10;
		final int NPTS = 200000;

		final float[] expected=new float[1+NBINS];
		for (int i=0; i<=NBINS; i++) { //fitted to the actual Numbers
			expected[i]=(float) (3*NPTS/NBINS*Math.exp(-3*(i-0.5)/NBINS)); }
		
		final int[] bins1 = fillRandomExpo(NBINS, NPTS);
		final int[] bins2 = fillRandomExpo(NBINS, NPTS);
		
		final double probModel = PROB_SAMPLE_FROM_MODEL(bins1, expected, NBINS, 0);
		final double probData = PROB_SAMPLES_SAME(bins1, bins2, NBINS, 0);

		L.n("expected").l("	observed[1]").l("	observed[2]");
		for (int i=0; i<=NBINS; i++) {
			L.n().l(expected[i]).l(bins1[i]).l(bins2[i]); } 
		L.n("probability of same Model:").l(probModel);
		L.n("probability of same Source:").l(probData);
	}

	/** fills the given Array with random Exponential distributed Numbers 	 */
	private static final int[] fillRandomExpo(final int NBINS, final int NPTS) {
		final int[] bins1=new int[1+NBINS]; 
		//for (int j=1; j<=NBINS; j++) { bins1[j]=0; } 
		for (int i=0; i<=NPTS; i++) {
			final float x=-FilterIn_FloatByFunction.RANDOM_FLOAT_EXPONENTIAL();
			final int ibin=(int) ((x*NBINS)/3)+1;
			if (ibin <= NBINS) {
				++bins1[ibin]; } 
		}
		return bins1; 
	}
	
	private static final void testFisher() {
		L.n().n("performing Fisher's F Test to compare Variances:");
		final int NPTS = 100000;
		final int MPTS = 50000;
		final float EPS = 0.01f;
		final int NVAL = 5;
		
		float factor,vrnce; 
		
		final float[] data1=new float[1+NPTS];
		final float[] data2=new float[1+MPTS];
		final float[] data3=new float[1+MPTS];
		
		// Generate two gaussian distributions with different variances 
		for (int j=data2.length; --j>=0; ) { data2[j]=RandomGauss.NEXT_FLOAT(); } 
		for (int j=data1.length; --j>=0; ) { data1[j]=RandomGauss.NEXT_FLOAT(); } 
		double prob = PROB_SAME_VARIANCE(data1, data2);
		L.n("Probability on Data1 and Data2:").l(prob);
		prob = PROB_SAME_VARIANCE(data1, data1); 
		L.n("Probability on same Data1:").l(prob);
		Assert.EQUALS(1, (float) prob); 
		prob = PROB_SAME_VARIANCE(data2, data2);
		L.n("Probability on same Data2:").l(prob);
		Assert.EQUALS(1, (float) prob); 
		L.n("Variance 1").l(1);
		L.n("Variance 2").l("	Ratio").l("	Probability");
		int iMax = 0; 
		double maxVal = 0; 
		for (int i=-NVAL; ++i <= NVAL; ) {
			vrnce  = 1+i*EPS;
			factor = (float) Math.sqrt(vrnce);
			for (int j=data3.length; --j>=0; ) { 
				data3[j]=factor*data1[j]; } ////otherwise the Sample Differences already have a too large Influence!
			prob = PROB_SAME_VARIANCE(data1, data3);
			L.n().l(vrnce).l(prob);
			if (maxVal < prob) {
				maxVal = prob; iMax = i;  
			}
		}
		Assert.EQUALS(0, iMax); 
	}
	
	private static final void testProbSameMean() {
		final int NPTS = 51200; //The Distributions are more interesting for smaller Numbers, 
		final int MPTS = 102400; //but then the Maximum may not fall on the 0 Index! 
		final float EPS = 0.02f;
		final int NSHFT = 5;
		final double VAR1 = 1;
		final double VAR2 = 4;

		final float[] data1 = new float[1 + NPTS];
		final float[] data2 = new float[1 + MPTS];
		// Generate gaussian distributed data
		final RandomFast ran1 = new RandomFast();
		ran1.randomize();
		final RandomGauss ran = new RandomGauss((IStreamIn_Bound_Int) ran1);
		L.n("shift").l("\tt").l("\tprobability");
		final float stdDev1 = (float) Math.sqrt(VAR1);
		final float stdDev2 = (float) Math.sqrt(VAR2);
		for (int i = data1.length; --i >= 0;) {
			data1[i] = stdDev1 * ran.nextFloat();
		}
		for (int i = data2.length; --i >= 0;) {
			data2[i] = stdDev2 * ran.nextFloat() + NSHFT * EPS;
		} //data1[i]; } //
		L.n().l("identical: ").l(PROB_SAME_MEAN(data1, data1));
		L.n().l("identical: ").l(PROB_SAME_MEAN(data2, data2));
		int maxIndex = -1;
		double maxProb = 0;
		for (int i = -NSHFT; i <= NSHFT; i++) {
			L.n().l(i * EPS);
			double prob = PROB_SAME_MEAN_VAR(data1, data2);
			L.l(prob);
			prob = PROB_SAME_MEAN(data1, data2);
			L.l(prob);
			if (maxProb < prob) {
				maxProb = prob;
				maxIndex = i;
			}
			for (int j = 1; j <= NPTS; j++) {
				data1[j] += EPS;
			}
		}
		Assert.EQUALS(0, maxIndex);
	}

	private static final void testProbSameMeanCorrelated() {
		final int NPTS = 50000; //broader Peak for smaller Values e.g. 5000
		final float EPS = 0.01f;
		final int NSHFT = 11;
		final float ANOISE = 0.3f;

		final float[] data1 = new float[1 + NPTS];
		final float[] data2 = new float[1 + NPTS];
		final float[] data3 = new float[1 + NPTS];
		L.n("Correlated Data:").l("Uncorrelated Data:");
		L.n("Shift").l("t").l("Probability").l("t").l("Probability");
		final RandomFast ran1 = new RandomFast();
		ran1.randomize();
		final RandomGauss ran = new RandomGauss((IStreamIn_Bound_Int) ran1);
		final float offset = NSHFT * EPS;
		for (int j = data1.length; --j >= 0;) {
			data1[j] = ran.nextFloat() + offset; //Zero in
			data2[j] = data1[j] + ANOISE * ran.nextFloat() - offset; //offset the Distributions
			data3[j] = ran.nextFloat();
			data3[j] += ANOISE * ran.nextFloat();
		}
		int maxIndex1 = -1;
		double maxProb1 = 0;
		int maxIndex2 = -1;
		double maxProb2 = 0;
		for (int i = -NSHFT; ++i <= NSHFT;) {
			for (int j = 1; j <= NPTS; j++) {
				data2[j] += EPS;
				data3[j] += EPS;
			}
			final double prob1 = PROB_SAME_MEAN_CORRELATED(NPTS, data1, data2);
			final double prob2 = PROB_SAME_MEAN_CORRELATED(NPTS, data1, data3);
			if (maxProb1 < prob1) {
				maxProb1 = prob1;
				maxIndex1 = i;
			}
			if (maxProb2 < prob2) {
				maxProb2 = prob2;
				maxIndex2 = i;
			}
			L.n().l(i).l(prob1).l(prob2);
		}
		Assert.EQUALS(0, maxIndex1);
		Assert.EQUALS(0, maxIndex2);
	}

	/** tests Contingency Table Analysis	 */	
	private static final void testContingency() {
		L.n().n("testing Contingency Table Analysis: "); 
		//Accidental Deaths by Month and Type (1979) for Contingency Table Analysis
		final String[] months = {"", "jan","feb","mar","apr","may","jun","jul","aug","sep","oct","nov","dec"};
		final String[] fates  = {"", "Motor Vehicle", "Falls", 
			"Drowning", "Fires", "Choking", "Fire-arms","Poisons", "Gas-poison", "Other"};
		final int[][] deaths = { {},
			{0,3298,3304,4241,4291,4594,4710,4914,4942,4861,4914,4563,4892}, 
			{0,1150,1034,1089,1126,1142,1100,1112,1099,1114,1079, 999,1181}, 
			{0, 180, 190, 370, 530, 800,1130,1320, 990, 580, 320, 250, 212}, 
			{0, 874, 768, 630, 516, 385, 324, 277, 272, 271, 381, 533, 760},
			{0, 299, 264, 258, 247, 273, 269, 251, 269, 271, 279, 297, 266},
			{0, 168, 142, 122, 140, 153, 142, 147, 160, 162, 172, 266, 230},
			{0, 298, 277, 346, 263, 253, 239, 268, 228, 240, 260, 252, 241},
			{0, 267, 193, 144, 127,  70,  63,  55,  53,  60, 118, 150, 172},
			{0,1264,1234,1172,1220,1547,1339,1419,1453,1359,1308,1264,1246}
		};

		final float[] ccc    = new float[1];
		final float[] chisq  = new float[1];
		final float[] cramrv = new float[1];
		final long [] df     = new long [1];
		
		L.n().l(months);
		for (int i=1; i<fates.length; i++) {
			L.n(fates[i]);
			for (int j=1; j<deaths.length; j++) {
				L.l(deaths[i][j]); } 
		}
		final int[] maxDev = new int[2]; 
		final float prob = (float) CROSS_TAB_PROBABILITY(deaths, null, null, null, df, chisq, cramrv, ccc);
		L.n("Do the Fates depend on the Season (resp. Seasons depend on the Fates?)");
		Assert.EQUALS(5026.2964f, chisq[0], "chi-squared");
		Assert.EQUALS(  88,df[0], "degrees of freedom");
		Assert.EQUALS(1, prob   , "probability for a Dependency");
		Assert.EQUALS(0.0772363f, cramrv[0], "cramer-v");
		Assert.EQUALS(0.213424f , ccc[0], "contingency coeff.");
		L.n("most unusual Position: row:").l(maxDev[0]).l(" col:").l(maxDev[1]);
		L.n("Interpretation: There is a very significant Dependency of Fates from the Season"); 
		L.n("Especially of Deaths by ").l(fates[maxDev[0]]).l(" in ").l(months[maxDev[1]]);
		//Calculation of Significance
		//these absolute Entropy Values are not very helpful
		final double[] entropies = new double[2];
		final float h = (float) CROSS_TAB_ENTROPY(deaths, entropies);
		final float hx = (float) entropies[0];
		final float hy = (float) entropies[1]; 
		final float hygx=h-hx; 
		final float hxgy=h-hy; 
		//only these normed Entropy Ratios give away Dependencies
		final float dygx=1-hygx/hy;  
		final float dxgy=1-hxgy/hx;
		final float dxy = 2*((hx+hy-h)/(hx+hy)); //The weighed Mean between both Dependencies
		Assert.EQUALS(4.036837f   ,    h, "Total Entropy of Table");
		Assert.EQUALS(1.5780629f  ,   hx, "Entropy of x-Distribution");
		Assert.EQUALS(2.482004f   ,   hy, "Entropy of y-Distribution");
		Assert.EQUALS(2.458774f   , hygx, "Entropy of y given x");
		Assert.EQUALS(1.5548332f  , hxgy, "Entropy of x given y");
		Assert.EQUALS(0.009359308f, dygx, "weak Dependency of y on x");
		Assert.EQUALS(0.014720401f, dxgy, "weak Dependency of x on y");
		Assert.EQUALS(0.011442964f,  dxy, "weak Symmetrical Dependency");
		L.n("Interpretation: The Dependency is quite weak though!"); 
	}
	
	/** tests Contingency Table Analysis	 */	
	private static final void testContingency2() {
		L.n().n("testing Contingency Table Analysis: "); 
		//Accidental Deaths by Month and Type (1979) for Contingency Table Analysis
		final int[] rows = {1,2,3,4,5,6,7};
		final int[] cols = {9,8,7,6,5};
		final int[][] values = new int[rows.length][cols.length];

		final float[] ccc = new float[1];
		final float[] chisq = new float[1];
		final float[] cramrv = new float[1];
		final long [] df = new long[1];
		
		for (int i=0; i<rows.length; i++) {
			for (int j=0; j<cols.length; j++) {
				values[i][j] = rows[i] * cols[j]; } 
		}
		final float prob = (float) CROSS_TAB_PROBABILITY(values, null, null, null, df, chisq, cramrv, ccc);
		Assert.EQUALS((rows.length-1)*(cols.length-1), df[0], "degrees of freedom");
		Assert.EQUALS(0          , chisq[0] , "chi-squared");
		Assert.EQUALS(0          , prob     , "probability");
		Assert.EQUALS(7.588226E-9, cramrv[0], "cramer-v");
		Assert.EQUALS(1.3143194E-8, ccc[0]  , "contingency coeff.");
		
		//Calculation of Significance
		//these absolute Entropy Values are not very helpful
		final double[] entropies = new double[2];
		final float h = (float) CROSS_TAB_ENTROPY(values, entropies);
		final float hx = (float) entropies[0];
		final float hy = (float) entropies[1]; 
		final float hygx=h-hx;
		final float hxgy=h-hy;
		//only these normed Entropy Ratios give away Dependencies
		final float dygx=1-hygx/hy;
		final float dxgy=1-hxgy/hx;
		final float dxy = 2*((hx+hy-h)/(hx+hy));
		Assert.EQUALS(1.8091179f ,   hx, "Entropy of x-Distribution");
		Assert.EQUALS(1.5887859f ,   hy, "Entropy of y-Distribution");
		Assert.EQUALS(3.3979044f ,    h, "Total Entropy of Table");
		Assert.EQUALS(1.5887865f , hygx, "Entropy of y given x");
		Assert.EQUALS(1.8091185f , hxgy, "Entropy of x given y");
		Assert.EQUALS(0          , dygx, "no Dependency of y on x");
		Assert.EQUALS(0          , dxgy, "no Dependency of x on y");
		Assert.EQUALS(0          ,  dxy, "no Symmetrical Dependency");
	}
	
	private static final boolean testUniformRandom() {
		final int numBins = 500; 
		final int numPnts = 100*numBins;  
		final int[] bins = new int[numBins];
		final RandomQuick ran1 = new RandomQuick();
		ran1.randomize();
		for(int i = numPnts; --i >= 0;)
			++bins[ran1.nextInt(numBins)];
		//returns 0,5 when Chi�=numBins
		final double prob = PROB_SAMPLE_FROM_UNIFORM(bins)-.5;
		return (Math.abs(prob) < .45); //only 10% Rejection! 
	}
	
	/** tests all Methods of this Class 	 */
	final static public void testIt() throws Exception {
		int numFailures = 0; //to be sure... 
		for (int i = 20; --i >= 0;) //...you have to create Statistics from Statistics.
			if (! testUniformRandom())
				++numFailures; 
		Assert.IS_TRUE(numFailures >= 0); 
		Assert.IS_TRUE(numFailures <= 4); 
		testKolmogorovSmirnov();
		testSampleFrom2dDistribution(); 
		testSampleFrom2dModel();
		testProbSameMean();
		testProbSameMeanCorrelated();
		testFisher();
		testChiSquare();
		testContingency(); 
		testContingency2(); 
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws Exception {
		testIt();
	}
	
}

/**
 * Maps a 2D point to the four quadrant shares of a [-1,+1]&sup2; cube it selects, as a test
 * model for {@link StatisticsFloat#PROB_2D_SAMPLE_FROM_MODEL}.
 *
 * <p>Title: HyperCubeShare<p>
 * Description:
 * Implementation of IFloatVectorField
 * to return the Shares of a [-1,+1]� Cube
 * that the given Vector selects
 * for Testing.
 *
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:52:49Z
 * digest: 59f810c9af4480b89d0c0d1bc28ab2e57bd44753f0081dbf463698af816da14d
 * stale: false
 * -->
 */
class HyperCubeShare
extends AFloatVectorField {

	/**quadrant probabilities 
	 * @see function.vector.AFloatVectorField#Map(double[], double[])	 */
	public double[] map(final double[] xy, final double[] ret) {
		final double qa=Math.min(2,Math.max(0,1-xy[0]));
		final double qb=Math.min(2,Math.max(0,1-xy[1]));
		final double qc=2-qa; //Math.min(2,Math.max(0,xy[0]+1));//
		final double qd=2-qb; //Math.min(2,Math.max(0,xy[1]+1));//
		ret[0]=0.25*qa*qb;
		ret[1]=0.25*qb*qc;
		ret[2]=0.25*qc*qd;
		ret[3]=0.25*qd*qa;
		return ret;
	}
	
	/**quadrant probabilities 
	 * @see function.vector.AFloatVectorField#Map(float[], float[])	 */
	public float[] map(final float[] xy, final float[] ret) {
		final float qa=Math.min(2,Math.max(0,1-xy[0]));
		final float qb=Math.min(2,Math.max(0,1-xy[1]));
		final float qc=2-qa; //Math.min(2,Math.max(0,xy[0]+1));//
		final float qd=2-qb; //Math.min(2,Math.max(0,xy[1]+1));//
		ret[0]=0.25f*qa*qb;
		ret[1]=0.25f*qb*qc;
		ret[2]=0.25f*qc*qd;
		ret[3]=0.25f*qd*qa;
		return ret;
	}
	
}
