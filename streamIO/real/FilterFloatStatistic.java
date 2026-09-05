/*
 * File Name: FilterFloatStatistic.java
 * Created on: 03.02.2004
 *
 */
package streamIO.real;

import streamIO.Assert;
import streamIO.Log;
import streamIO.integer.random.IStreamIn_Bound_Int;
import streamIO.integer.random.RandomLong;
import streamIO.real.random.RandomGauss;
import function.IFloatFunction;

/**
 * Bidirectional filter that accumulates running average, variance, skewness and kurtosis
 * from a stream of values as they pass through it.
 *
 * <p>Bidirectional Filter,
 * collecting statistical Information (Average and Variance) on the Way, 
 * i.e. whenever a Number is read from this Object (working as IStreamIn_Float) 
 * or when a Number is written into this Object (working as IStreamOutFloat). 
 * 
 * This Implementation is not thread-safe. 
 * A synchronizing Object should be provided
 * to coordinate between adding Numbers and reading the Statistics!  
 * 
 * Higher Level Moments like 
 * Skewness Sum(N, (x[n]-x)^3), which indicates asymmetric Tails and 
 * Curtosis Sum(N, (x[n]-x)^4), which indicates heavier Tails than Base 
 * are numerically not stable and zero when the Variance is zero.  
 * 
 * To calculate Moments that are more robust, requires to store the whole Input Stream: 
 * The Mode is the most probable Value of a Distribution 
 * and is most useful when there is a single, sharp Peak. 
 * The Median is the Value for which higher and lower Values are equally probable, 
 * ignoring the Size of the Difference. 
 * It minimizes the Mean Absolute Deviation for any Distribution, so that 
 * aDev(X)*N = Sum(N, |x[n]-x|) becomes minimal for x = xMedian 
 * but the Average x is often used too, since for calculating the Median
 * you have to store the whole Dataset. 
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
 * mtime: 2026-09-05T11:14:58Z
 * digest: b1b7bd989b0f858b49297a43888540b0ec12c5d7dd0e7219ad69e052ebe9e864
 * stale: false
 * tags: [code/statistics, code/running_statistics]
 * concepts: [Running Statistics Filter]
 * facets: {layer: infrastructure, status: legacy, complexity: medium}
 * -->
 */
public class FilterFloatStatistic 
extends FilterFloatByFunction {

	/** Logger for Testing, modify Threshold for switching Logging */
	protected static Log L = new Log(FilterFloatStatistic.class);

	/////////////////////////////////////////////////////////////////////////////////////
	// Member Variables	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Index at which the Offset should be re-initialized next	 */
	long reNorm = Long.MAX_VALUE; 

	/**Should be chosen close to the Average.  
	 * The Offset allows to sum up many Values 
	 * with minimum Extinction or accumulating Errors. 
	 * Especially the Variance is renormed successfully! 
	 */
	double offSet; // = Double.NaN; //faster to test boolean Variables 
	
	/** Sum of the Values	 */	
	double sum;
	 
	/** (squared) Variance of the Values	 */	
	double var;
	
	/** Absolute Variance of the Values	 */
	double abs;
	
	/** (cubed) Skewness of the Values	 */	
	double skew; 
	
	/** (quadruped) Curtosis of the Values	 */	
	double curt; 
	
	////////////////////////////////////////////////////////////////////////////
	// Constructors
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Creates a statistics filter reading from {@code InStream_}, with an explicit offset guess.
	 * @param InStream_ the source stream to collect statistics from
	 * @param offset_ the initial guess of the mean, seeding the running sums
	 * @param mapper_ optional function mapping each value before it is accumulated
	 */
	public FilterFloatStatistic(final IStreamIn_Float InStream_, double offset_, final IFloatFunction mapper_) {
		super(InStream_, mapper_); this.offSet = offset_; }

	/** Creates a statistics filter writing to {@code OutStream_}, with an explicit offset guess.
	 * @param OutStream_ the destination stream for filtered output
	 * @param offset_ the initial guess of the mean, seeding the running sums
	 * @param mapper_ optional function mapping each value before it is accumulated
	 */
	public FilterFloatStatistic(final IStreamOutFloat OutStream_, final double offset_, final IFloatFunction mapper_) {
		super(OutStream_, mapper_); this.offSet = offset_; }

	/** Creates a statistics filter reading from {@code InStream_}, with no mapping function.
	 * @param InStream_ the source stream to collect statistics from
	 * @param offset_ the initial guess of the mean, seeding the running sums
	 */
	public FilterFloatStatistic(final IStreamIn_Float InStream_, final double offset_) {
		super(InStream_); this.offSet = offset_; }

	/** Creates a statistics filter writing to {@code OutStream_}, with no mapping function.
	 * @param OutStream_ the destination stream for filtered output
	 * @param offset_ the initial guess of the mean, seeding the running sums
	 */
	public FilterFloatStatistic(final IStreamOutFloat OutStream_, final double offset_) {
		super(OutStream_); this.offSet = offset_; }

	/** Creates a statistics filter reading from {@code InStream_}, offsetting to the first item read.
	 * @param InStream_ the source stream to collect statistics from
	 */
	public FilterFloatStatistic(final IStreamIn_Float InStream_) {
		super(InStream_); reNorm = 0; }

	/** Creates a statistics filter writing to {@code OutStream_}, offsetting to the first item written.
	 * @param OutStream_ the destination stream for filtered output
	 */
	public FilterFloatStatistic(final IStreamOutFloat OutStream_) {
		super(OutStream_); reNorm = 0;  }

	/** Constructor, receiving the Offset 	 */
	public FilterFloatStatistic(final double offset_) {
		super((IStreamOutFloat) null); this.offSet = offset_; }

	/** Empty Constructor, defaulting the Offset to the first Item in the Stream 	 */
	public FilterFloatStatistic() {
		super((IStreamOutFloat) null); reNorm = 0;  }

	/////////////////////////////////////////////////////////////////////////////////////
	// statistic Methods (need to be synchronized!)	
	/////////////////////////////////////////////////////////////////////////////////////
	
	//To calculate the Median, you have to keep track of all the "middle" Elements. 
	//This is too much Overhead. 
	//public double getMedian() { return sum+count*offSet; }

	/** Returns the sum of all elements seen so far, corrected for the running offset.
	 * @return the Sum of all Elements so far
	 * Sum(N, x[n]-o) = Sum(N, x[n]) - o*N
	 */
	public double getSum() { return sum+count*offSet; }

	/** Returns the arithmetic mean of all elements seen so far.
	 * @return the Average of all Elements so far
	 * x - o = Avg(N, x[n]-o) = Sum(N, x[n]-o)/N
	 */
	public double getAverage() { return sum/count+offSet; }

	/** Returns an upper-bound estimate of the mean absolute deviation.
	 * @return the absolute Deviation so far
	 * This cannot be calculated very well 
	 * without taking the whole Dataset into Account, 
	 * because the absolute Value is not analytical: 
	 * A very crude upper Bound for the absolute Deviation is:
	 * since |x[n]-o| + |o-x| >= |(x[n]-o) + (o-x)| == |x[n]-x| 
	 * Sum(N, x[n]-o)/N + |x-o| >= AbsDev(X)/N
	 */	
	public double getAbsDev() { return (abs+Math.abs(sum))/count; }
	
	/** Returns the sample variance of all elements seen so far, re-normalizing the running sums first.
	 * @return the Variance of all Elements so far.
	 * The Estimation of the Mean eats up one Degree of Freedom,
	 * so the Estimate of the Variance has one Parameter less.  
	 * Var(x)*(N-1) = 
	 * Sum(N, (x[n]-Avg(N, x[n]))�) = Sum(N, (x[n]-x)�)   (...with x = Avg(i, x[i]) )
	 * Sum(N,([x[n]-o]+[o-x])�) =
	 * Sum(N,[x[n]-o]� + 2*[x[n]-o][o-x] + [o-x]�) =
	 * Sum(N,[x[n]-o]�) + 2*Sum(N,[x[n]-o])[o-x] + N*[o-x]� =
	 * Sum(N,[x[n]-o]�) - 2*Sum(N,[x[n]-o])[x-o] + N*[x-o]� =  (...with Sum(N, x[n]-o)/N = [x-o])
	 * Sum(N,[x[n]-o]�) - 2*N*[x-o][x-o] + N*[x-o]� = 
	 * Sum(N,[x[n]-o]�) - N*[x-o]� =  
	 * Sum(N,[x[n]-o]�) - Sum(N, x[n]-o)�/N 
	 */	
	public double getVariance() {
		reNorm(); //the other getter Methods rely on this reNorm()!
		return var/(count-1);  //since the Members are reNormed
		//return GET_VARIANCE(var, sum, count)/(count-1);
	}

	/** Computes the sum-of-squares variance term from raw first- and second-moment sums.
	 * @return the Variance of all Elements so far
	 *
	 * @param sumSqr the sum of squared, offset-corrected values
	 * @param sum the sum of offset-corrected values
	 * @param count the number of values summed
	 * @return the uncorrected (not divided by count-1) variance term
	 */
	final static public double GET_VARIANCE(final double sumSqr, final double sum, final long count) {
		return sumSqr - sum*sum/count; }

	/** Returns the standard deviation of all elements seen so far.
	 * @return the Standard Deviation of all Elements so far = SqRt(Var(x))
	 */
	public double getStdDev() {
		return Math.sqrt(getVariance()); }
	
	/** Returns the dimensionless skewness (asymmetry) of all elements seen so far.
	 * @return the Skewness of all Elements so far
	 * It is made dimensionless by scaling it with the Variance
	 * to be an Indicator for the Shape. 
	 * positive Values indicate a larger positive Tail, while
	 * negative Values indicate a larger negative Tail 
	 *   
	 * Skew(x)*N = 
	 * Sum(N, (x[n]-Avg(N, x[n]))�) = Sum(N, (x[n]-x)�)   (...with x = Avg(i, x[i]) )
	 * Sum(N,([x[n]-o]+[o-x])�) =
	 * Sum(N,([x[n]-o]�+3[x[n]-o]�[o-x]+3[x[n]-o][o-x]�+[o-x]�) = 
	 * Sum(N,([x[n]-o]�)) + 3*Sum(N,([x[n]-o]�)[o-x] + 3*Sum(N,([x[n]-o])[o-x]� + N*[o-x]� = 
	 * Sum(N,([x[n]-o]�)) - 3*Sum(N,([x[n]-o]�)[x-o] + 3*Sum(N,([x[n]-o])[x-o]� - N*[x-o]� = 
	 * Sum(N,([x[n]-o]�)) - 3*Sum(N,([x[n]-o]�)[x-o] + 3*N*[x-o][x-o]� - N*[x-o]� = 
	 * Sum(N,([x[n]-o]�)) - 3*Sum(N,([x[n]-o]�)[x-o] + 2*N*[x-o]� = (...with Sum(N, x[n]-o)/N = [x-o] = sum/count)
	 * Sum(N,([x[n]-o]�)) - 3*Sum(N,([x[n]-o]�)*Sum(N, x[n]-o)/N + 2*N*Sum(N, x[n]-o)�/N�
	 * Sum(N,([x[n]-o]�)) - 3*Sum(N,([x[n]-o]�)*Sum(N, x[n]-o)/N + 2*Sum(N, x[n]-o)�/N�
	 * 
	 */
	public double getSkewness() {
		final double variance = getVariance(); 
		if (variance == 0) {
			return 0; }
		return skew/(count*variance*Math.sqrt(variance)); //since the Members are reNormed
		//return GET_SKEWNEWSS(skew, var, sum, count)/(count*variance*Math.sqrt(variance));
	}
	
	/** Calculates the SkewNess directly from the summed up Moments of the Stream	 */   
	final static public double GET_SKEWNEWSS(final double skew, final double var, final double sum, final long count) { 
		final double tmp = sum/count;
		final double d1 = 3*var - 2*sum*tmp;
		return skew -d1*tmp; 
	}
	
	/** Returns the dimensionless excess kurtosis of all elements seen so far.
	 * @return the Curtosis of all Elements so far
	 * It is made dimensionless, by scaling it with the Variance
	 * and Offset to the Curtosis of the Gaussian Distribution
	 * to be an Indicator for the Shape: 
	 * negative Values indicate a flat Distribution (e.g. uniform for -1.2) 
	 * positive Values indicate a sharp Peak 
	 * zero corresponds to the Gaussian Distribution
	 *   
	 * Curt(x)*(N-1) = 
	 * Sum(N, (x[n]-Avg(N, x[n]))^4) = Sum(N, (x[n]-x)^4)   (...with x = Avg(i, x[i]) )
	 * Sum(N,([x[n]-o]-[x-o])^4) =
	 * Sum(N,([x[n]-o]^4 - 4[x[n]-o]^3[x-o] + 6[x[n]-o]^2[x-o]^2 + 4[x[n]-o][x-o]^3 + [x-o]^4) = 
	 * Sum(N,([x[n]-o]^4 - 4*Sum(N,([x[n]-o]^3)*[x-o] + 6*Sum(N,([x[n]-o]^2)*[x-o]^2 - 4*Sum(N,([x[n]-o])*[x-o]^3 + [x-o]^4) = 
	 * curt - 4*skew*[x-o] + 6*var*[x-o]^2 - 4*sum*[x-o]^3 + N*[x-o]^4 = 
	 * curt - 4*skew*[sum/N] + 6*var*[sum/N]^2 - 4*sum*[sum/N]^3 + N*[sum/N]^4 = 
	 * curt - 4*skew*sum/N + 6*var*sum*sum/(N*N) - 4*sum^4/N^3 + N*sum^4/N^4 = 
	 * curt - 4*skew*sum/N + 6*var*sum*sum/(N*N) - 4*sum^4/N^3 + sum^4/N^3 = 
	 * curt - 4*skew*sum/N + 6*var*sum*sum/(N*N) - 3*sum^4/N^3 = 
	 * curt - (4*skew - (6*var - 3*sum^2/N)*sum/N)*sum/N = 
	 */	
	public double getCurtosis() { 
		final double variance = getVariance(); 
		if (variance == 0) {
			return 0; }
		//final double ret = GET_CURTOSIS(curt, skew, var, sum, count)/(count*variance*variance);
		final double ret = curt/(count*variance*variance);  //since the Members are reNormed
		return ret-3; }
	
	/** Computes the raw fourth-moment kurtosis term from the summed moments and re-normalizes it
	 * to the current offset.
	 * @param curt the sum of quartic, offset-corrected values
	 * @param skew the sum of cubed, offset-corrected values
	 * @param var the sum of squared, offset-corrected values
	 * @param sum the sum of offset-corrected values
	 * @param count the number of values summed
	 * @return the uncorrected (not divided by count) kurtosis term
	 */
	final static public double GET_CURTOSIS(final double curt, final double skew, final double var, final double sum, final long count) {
		final double tmp = sum/count;
		return curt - (4*skew -(6*var - 3*sum*tmp)*tmp)*tmp; }
	
	/////////////////////////////////////////////////////////////////////////////////////
	//	streaming Operation
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** adds a single Value to the Statistics, called by the stream Methods. 	 */
	public double addValue(final double value) {
		if (count >= reNorm) {
			reNorm += reNorm+1; //increase Step Size geometrically, even from 0 on, so that Chunks of equal Size are 
			if (count == 0) {
				++count;
				return offSet = value; } 
			reNorm();
		} 
		++count;
		final double diff = value-offSet;
		final double dSqr;
		abs += Math.abs(diff);   
		sum += diff; 
		var += (dSqr = diff*diff);
		skew += dSqr*diff;
		curt += dSqr*dSqr; 
		return value;
	}
	
	/** re-normalizes the current Estimates;
	 * after this Operations:
	 * - the new Sums have less Cancellation Errors 
	 * - the Member Variables directly contain Estimates for the Moments.  
	 */
	public void reNorm() {
		final double newOffset = getAverage(); //offSet+sum/count
		final double newSum = 0; //sum + count*(offSet - newOffset); //==sum + count*(offSet - (offSet+sum/count))
		//final double newAbs = abs; //not clear how to treat Corrections here! 
		final double newVar = GET_VARIANCE(var, sum, count); //-GET_VARIANCE(0, 0, count); // var - (sum*sum - newSum*newSum)/count; 
		final double newSkew = GET_SKEWNEWSS(skew, var, sum, count); //-GET_SKEWNEWSS(0, newVar, 0, count);
		final double newCurt = GET_CURTOSIS(curt, skew, var, sum, count); //-GET_CURTOSIS(0, newSkew, newVar, 0, count);
		this.curt = newCurt; 
		this.skew = newSkew; 
		this.var = newVar; 
		//this.abs = newAbs; 
		this.sum = newSum; 
		this.offSet = newOffset; 
	}

	/////////////////////////////////////////////////////////////////////////////////////
	// Testing and Main Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.	 */
	public static void testIt() {	//Testing single Step of Pegasus Step with Quality Control
		L.n("\nTesting ").l(FilterFloatStatistic.class);
		final int numItems = 1000000; 
		final double value = 5; 
		L.n(); 
		L.n("Checking for the Average and Variance of an affine rising Stream  ");
		final IStreamIn_Float steep = new StreamIn_Arithmetic(); 
		testStatistics(numItems+1, steep, new FilterFloatStatistic(), 567339, numItems/2, 288675, 0, 0, 500);
		L.n(); 
		L.n("Searching for the Average and Variance of a constant Stream with Value ").l(value);
		final IStreamIn_Float cnst = new ConstStreamIn_Float(value); 
		testStatistics(numItems, cnst, new FilterFloatStatistic(0.5), 9, value, 0, 0, 0, 0);
		L.n(); 
		L.n("Searching for the Average and Variance of random, equally distributed Numbers:");
		final RandomLong ran = new RandomLong(); //ran.randomize();
		testStatistics(numItems, ran, new FilterFloatStatistic(0.5), 0.25, 0.5, 0.2886, 0, -1.2, 5);
		L.n(); 
		L.n("Searching for the Average and Variance of random, normally distributed  Numbers:");
		final RandomGauss gauss = new RandomGauss((IStreamIn_Bound_Int) ran); // RandomFast()); 
		testStatistics(numItems, gauss, new FilterFloatStatistic(0), 0.7979, 0, 1, 0, 0, 5);
		L.n(); 
		L.n("Now the same Statistics for a Constant Distribution without a good Guess of the Mean:");
		testStatistics(numItems, cnst, new FilterFloatStatistic(), 0, value, 0, 0, 0, 0);
		L.n(); 
		L.n("Now the same Statistics for an equal Distribution without a good Guess of the Mean:");
		testStatistics(numItems, ran, new FilterFloatStatistic(), 0.25, 0.5, 0.2886, 0, -1.2, 5);
		L.n(); 
		L.n("Now the same Statistics for an normal Distribution without a good Guess of the Mean:");
		testStatistics(numItems, gauss, new FilterFloatStatistic(), 0.7979, 0, 1, 0, 0, 5);
	}

	static final void testStatistics( final int numItems, final IStreamIn_Float ran, final FilterFloatStatistic aveVar, 
	final double absDev, final double average, final double stdDev, final double skewness, final double curtosis, double accuracy) {
		STREAM(ran, aveVar, numItems);
		final long num = aveVar.getCount();
		final double adv = aveVar.getAbsDev();
		final double avg = aveVar.getAverage();
		final double var = Math.sqrt(aveVar.getVariance());
		final double skw = aveVar.getSkewness();
		final double crt = aveVar.getCurtosis();
		accuracy /= Math.sqrt(numItems); //Factor 2 works mostly, except for Gaussian
		L.n("Offset  =").l(aveVar.offSet); //
		L.n("Count   =").l(num); //
		L.n("AbsDev <=").l(adv); //
		L.n("Average =").l(avg); //
		L.n("StndDev =").l(var); //
		L.n("Skewness=").l(skw); //
		L.n("Curtosis=").l(crt); //
		L.n("accuracy=").l(accuracy); 
		
		Assert.EQUALS(numItems, num); //detected Outliers affect this Value!
		Assert.EQUALS(absDev  , adv, 2*accuracy); //may not be accurate without a good guess! 
		Assert.EQUALS(average , avg, accuracy, accuracy); 
		Assert.EQUALS(stdDev  , var, accuracy); //rather broad...
		Assert.EQUALS(skewness, skw, accuracy, 2*accuracy); //completely symmetric
		Assert.EQUALS(curtosis, crt, accuracy, 5*accuracy); //rather heavy Tails 
	}

	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}

}
