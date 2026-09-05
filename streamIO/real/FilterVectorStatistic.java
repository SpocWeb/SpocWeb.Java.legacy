/*
 * File Name: FilterVectorStatistic.java
 * Created on: 08.03.2004
 *
 */
package streamIO.real;

import math.matrix.MatrixDouble;
import math.vector.VectorDouble;
import math.vector.VectorFloat;
import streamIO.AStreamOut;
import streamIO.Assert;
import streamIO.IIStreamIn;
import streamIO.IIStreamOut;
import streamIO.Log;
import streamIO.integer.random.RandomLong;
import streamIO.object.AFilter;
import streamIO.real.random.RandomGaussVector;
import streamIO.real.random.RandomUniformVector;

/**
 * Bidirectional filter that accumulates the running mean vector and covariance matrix of
 * vectors passing through it.
 *
 * <p>Bidirectional Filter,
 * collecting statistical Information (Average and Variance) on the Way, 
 * i.e. whenever a Vector is read from this Filter (working as IStreamIn_Float) 
 * or when a Number is written into this Filter (working as IStreamOutFloat).
 * 
 * The Mean indicates the Center of Mass for this Distribution, 
 * but it is only a significant Measure, 
 * if the Distribution is 'localized'/gaussian, 
 * i.e. it has a single Maximum with rapidly falling Tails. 
 * 
 * The CoVariance can be used to: 
 * - calculate Correlation Coefficients between the Coordinates 
 *   as the normed CoVariance var[i][j]/SqRt(var[i][i]*var[j][j])
 *   which determines the Strength of the Correlation 
 *   (but not it's Significance, for this consider also the # of Items)
 * - determine and sort the Eigenvectors of this Distribution, 
 *   which allow to determine the Shape 
 *   and possibly introduce fewer, compound Coordinates, 
 *   that make the Analysis of the Data considerably easier! 
 * 
 * Note that the CoVariance also assumes a linear Model, 
 * so if the Dependency between Variables has a different Shape, 
 * this Analysis returns misleading Results! 
 * Look at the Distribution Plot and determine a Transformation 
 * into a linear Shape before analyzing the Strength or Significance! 
 * 
 * This Implementation is not thread-safe. 
 * A synchronizing Filter Object should be provided
 * to coordinate between adding Numbers and reading the Statistics!  
 * 
 * Higher Level Moments like 
 * Skewness Sum(N, (x[n]-x)^3), which indicates asymmetric Tails and 
 * Curtosis Sum(N, (x[n]-x)^4), which indicates heavier Tails than Base 
 * are numerically not stable and considerably more Complex, because Tensors.  
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
 * mtime: 2026-09-05T11:20:24Z
 * digest: 0a3abd32d9550340a8acd0bdd945f3050a47ea1a05e6d24ed1ac46597222a86d
 * stale: false
 * tags: [code/statistics, code/vector_math]
 * concepts: [Vector Statistics Filter]
 * facets: {layer: infrastructure, status: legacy, complexity: medium}
 * -->
 */
public class FilterVectorStatistic 
extends AFilter {
	
	/** Logger for Testing, modify Threshold for switching Logging */
	protected static Log L = new Log(FilterVectorStatistic.class);
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Member Variables	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Index at which the Offset should be re-initialized	 */
	long reinitialize = Long.MAX_VALUE; 
	
	/** counts the Vectors passed through this Filter	 */
	long count; 
	
	/** Difference of the current Values and the Offset, Working Space	 */	
	double[] tmpDiff;
	
	/** Sum of the Values	 */	
	double[] sum;
	
	/**Should be chosen close to the Average.  
	 * The Offset allows to sum up many Values 
	 * with minimum Extinction or accumulating Errors. 
	 * Especially the Variance is renormed successfully! 
	 */
	double[] offSet;
	
	/** (squared) Variance of the Values	 */	
	double[][] var;
	
	/** Absolute Variance of the Values	 */
	//double[][] abs;
	
	////////////////////////////////////////////////////////////////////////////
	// Constructors
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Creates a vector-statistics filter reading from {@code inStream_}, with an explicit offset guess.
	 * @param inStream_ the source stream to collect statistics from
	 * @param offset_ the initial guess of the mean vector, seeding the running sums
	 */
	public FilterVectorStatistic(final IIStreamIn inStream_, final double[] offset_) {
		super(inStream_);
		init(offset_);
	}

	/** Creates a vector-statistics filter writing to {@code outStream_}, with an explicit offset guess.
	 * @param outStream_ the destination stream for filtered output
	 * @param offset_ the initial guess of the mean vector, seeding the running sums
	 */
	public FilterVectorStatistic(final IIStreamOut outStream_, final double[] offset_) {
		super(outStream_);
		init(offset_);
	}

	/** Creates a vector-statistics filter reading from {@code inStream_}, with an explicit offset guess.
	 * @param inStream_ the source stream to collect statistics from
	 * @param offset_ the initial guess of the mean vector, seeding the running sums
	 */
	public FilterVectorStatistic(final IIStreamIn inStream_, final float[] offset_) {
		super(inStream_);
		init(VectorDouble.COPY(offset_));
	}

	/** Creates a vector-statistics filter writing to {@code outStream_}, with an explicit offset guess.
	 * @param outStream_ the destination stream for filtered output
	 * @param offset_ the initial guess of the mean vector, seeding the running sums
	 */
	public FilterVectorStatistic(final IIStreamOut outStream_, final float[] offset_) {
		super(outStream_);
		init(VectorDouble.COPY(offset_));
	}
	
	/** initializes the internal Arrays	 */
	private void init(final double[] offset_) {
		this.offSet = offset_; 
		this.tmpDiff = new double[offSet.length];
		this.sum = new double[offSet.length];
		this.var = new double[offSet.length][offSet.length]; //could also be lower Triangular
	}
	
	/** Creates a vector-statistics filter reading from {@code inStream_}, offsetting to the first item read.
	 * @param inStream_ the source stream to collect statistics from
	 */
	public FilterVectorStatistic(final IIStreamIn inStream_) {
		super(inStream_); reinitialize = 0; }

	/** Creates a vector-statistics filter writing to {@code outStream_}, offsetting to the first item written.
	 * @param outStream_ the destination stream for filtered output
	 */
	public FilterVectorStatistic(final IIStreamOut outStream_) {
		super(outStream_); reinitialize = 0;  }
	
	/** Constructor, receiving the Offset 	 */
	public FilterVectorStatistic(final double[] offset_) {
		this((IIStreamOut) null, offset_); }
	
	/** Constructor, receiving the Offset 	 */
	public FilterVectorStatistic(final float[] offset_) {
		this((IIStreamOut) null, offset_); }
	
	/** Empty Constructor, defaulting the Offset to the first Item in the Stream 	 */
	public FilterVectorStatistic() {
		super((IIStreamOut) null); reinitialize = 0;  }
	
	/////////////////////////////////////////////////////////////////////////////////////
	// statistic Methods (need to be synchronized!)	
	/////////////////////////////////////////////////////////////////////////////////////
	
	//To calculate the Median, you have to keep track of all the "middle" Elements. 
	//This is too much Overhead. 
	//public double getMedian() { return sum+count*offSet; }

	/** Returns the number of vectors accumulated so far.
	 * @return the Number of all Elements considered so far	 */
	public long getCount() { return count; }

	/** Returns the sum of all vectors seen so far, corrected for the running offset.
	 * @return the Sum of all Elements so far
	 * Sum(N, x[n]-o) = Sum(N, x[n]) - o*N
	 */	//sum+count*offSet
	public double[] getSum() { return VectorDouble.ADD_PROD(sum, offSet, count); }

	/** Computes the mean vector of all elements seen so far into the given result array.
	 * @return the Average of all Elements so far
	 * x - o = Avg(N, x[n]-o) = Sum(N, x[n]-o)/N
	 */	//sum/count+offSet
	public double[] getAverage(final double[] ret) {
		return VectorDouble.ADD_PROD(ret, offSet, sum, 1./count); }

	/** Returns the mean vector of all elements seen so far, allocating a new result array.
	 * @return the Average of all Elements so far
	 * x - o = Avg(N, x[n]-o) = Sum(N, x[n]-o)/N
	 */
	public double[] getAverage() { return getAverage(null); }
	
	/** @return the absolute Deviation so far
	 * This cannot be calculated very well 
	 * without taking the whole Dataset into Account, 
	 * because the absolute Value is not analytical: 
	 * A very crude upper Bound for the absolute Deviation is:
	 * since |x[n]-o| + |o-x| >= |(x[n]-o) + (o-x)| == |x[n]-x| 
	 * Sum(N, x[n]-o)/N + |x-o| >= AbsDev(X)/N
	 */	
	//public double[] getAbsDev() { return (abs+Math.abs(sum))/count; }
	
	/** re-normalizes the current Estimates;
	 * after this Operations:
	 * - the new Sums have less Cancellation Errors 
	 * - the Member Variables directly contain Estimates for the Moments.  
	 */
	public void reNorm() { //ADD_PROD(ret, offSet, sum, 1./count); }
		this.offSet = getAverage(this.offSet); //
		this.var =  GET_VARIANCE(this.var, sum, count); // 
		VectorDouble.ZERO_AT(this.sum);// = 0;
	}
	
	/** Returns the sample covariance matrix of all elements seen so far, re-normalizing the running sums first.
	 * @return the Variance of all Elements so far
	 * Only the lower Triangle of the Matrix is filled.
	 * Var(x)*(N-1) = 
	 * Sum(N, (x[n]-Avg(N, x[n]))�) = Sum(N, (x[n]-x)�)   (...with x = Avg(i, x[i]) )
	 * Sum(N,([x[n]-o]+[o-x])�) =
	 * Sum(N,[x[n]-o]� + 2*[x[n]-o][o-x] + [o-x]�) =
	 * Sum(N,[x[n]-o]�) + 2*Sum(N,[x[n]-o])[o-x] + N*[o-x]� =
	 * Sum(N,[x[n]-o]�) - 2*Sum(N,[x[n]-o])[x-o] + N*[x-o]� =  (...with Sum(N, x[n]-o)/N = [x-o])
	 * Sum(N,[x[n]-o]�) - 2*N*[x-o][x-o] + N*[x-o]� = Sum(N,[x[n]-o]�) - N*[x-o]� =  
	 * Sum(N,[x[n]-o]�) - Sum(N, x[n]-o)�/N 
	 */
	public double[][] getVariance() {
		reNorm(); 
		MatrixDouble.COPY_LOWER_TO_UPPER(var); 
		return MatrixDouble.MUL(var, 1./(count-1)); } 
	
	/** @return the Co-Variance of all Elements so far 
	 * Sum(N, (xn-x)*(yn-y)) = Sum(N, xn*yn - x*yn - xn*y + x*y)
	 * = Sum(N, xn*yn) - x*Sum(N, yn) - Sum(N, xn)*y + N*x*y 
	 * = Sum(N, xn*yn) - x*N*y - N*x*y + N*x*y  
	 * = Sum(N, xn*yn) - x*N*y  but this accumulates too much Errors
	 *  
	 * = Sum(N,([xn-x0]+[x0-x ]) *([yn-y0]+[y0-y])) 
	 * = Sum(N, [xn-x0]*[yn-y0]  + [x0-x]*[yn-y0] + [xn-x0]*[y0-y] + [x0-x]*[y0-y])
	 * = Sum(N, [xn-x0]*[yn-y0]) + [x0-x]*Sum(N, [yn-y0]) + Sum(N, [xn-x0])*[y0-y] + N*[x0-x]*[y0-y]
	 * = Sum(N, [xn-x0]*[yn-y0]) + [x0-x]*[Sum(N, yn)-N*y0] + [Sum(N, xn)-N*x0]*[y0-y] + N*[x0-x]*[y0-y]
	 * = Sum(N, [xn-x0]*[yn-y0]) + [x0-x]*[N*y-N*y0] + [N*x-N*x0]*[y0-y] + N*[x0-x]*[y0-y]
	 * = Sum(N, [xn-x0]*[yn-y0]) - [x-x0]*N*[y-y0] - N*[x0-x]*[y0-y] + N*[x0-x]*[y0-y]
	 * = Sum(N, [xn-x0]*[yn-y0]) - [x-x0]*N*[y-y0] 
	 * this is numerically as efficient AND it does not accumulate Errors, 
	 * if x0 is close to x. 
	 * 
	 * @param var
	 * @param sum
	 * @param count
	 * @return
	 */
	private static final double[][] GET_VARIANCE(final double[][] var, final double[] sum, final long count) {
		for(int i = var.length; --i>= 0; ) {
			final double[] var_i = var[i];
			final double   sum_i = sum[i];
			var_i[i]-=sum_i*sum_i/count; 
			for(int j = i; --j>= 0;) {
				var_i[j]-=sum_i*sum[j]/count; }
		}
		return var; }
	
	/**
	 * It is possible to calculate the Square Root of this positive definite Matrix 
	 * @return the Standard Deviation of all Elements so far = SqRt(Var(x)) 
	 */	
	//public double getStdDev() { return Math.sqrt(getVariance()); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	//	streaming Operation
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** TODO: move these Methods up to Filter
	 * @see streamIO.IIStreamOut#addItem(Object)
	 */
	public IIStreamOut addItem(final Object arg) {
		final Object nextItem = addVector(arg);
		if (out != null) {
			out.addItem(nextItem); }
		return this; }
	
	/** TODO: move these Methods up to Filter
	 * @see streamIO.IFactory#nextItem()
	 */
	protected Object nextItemInternal() {
		currItem = in.nextItem(); 
		return addVector(currItem); 
	}
	
	/** adds a single Value to the Statistics, called by the stream Methods. 	 */
	public Object addVector(final Object value) {
		if (value instanceof  double[]) 
			return addVector((double[]) value); 
		if (value instanceof  float []) 
			return addVector((float []) value); 
		return value;
	}
	
	/** adds a single Value to the Statistics, called by the stream Methods. 	 */
	public double[] addVector(final double[] value) {
		tmpDiff = VectorDouble.SUB(tmpDiff, value, offSet); 
		addVector();
		return value; 
	}
	
	/** adds a single Value to the Statistics, called by the stream Methods. 	 */
	public float[] addVector(final float[] value) {
		tmpDiff = VectorDouble.SUB(tmpDiff, value, offSet); 
		addVector();
		return value; 
	}
	
	/** adds a single Value to the Statistics, called by the stream Methods. 	 */
	private void addVector() {
		if (count >= reinitialize) {
			reinitialize += reinitialize+1; //increase Step Size geometrically, even from 0 on
			if (count == 0) {
				++count; 
				init(tmpDiff);
				return; } 
			reNorm();
		} 
		++count;
		for (int i = 0; i < offSet.length; ++i) {
			final double diff = tmpDiff[i]; // value[i]-offSet[i];
			final double[] var_i = var[i];  
			//abs[i] += Math.abs(diff);   
			sum[i] += diff; 
			var_i[i] += diff*diff;
			for (int j = i; --j >= 0; ) { //only the lower Triangle is filled! 
				var_i[j] += diff*tmpDiff[j]; }
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Testing and main Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.	 */
	public static void testIt() {	//
		L.n("Testing ").l(FilterVectorStatistic.class);
		final long numItems = 1000*1000; 
		testStatistic(); 
		
		final float[] width = { 0, 0, 0 };
		final float[] mean  = { 0.5f, 0.5f, 0.5f };
		final RandomLong ran = new RandomLong(); //ran.randomize();
		L.n();
		L.n("Searching for the Average and Variance of a constant Stream with correct Mean: ").l(mean);
		final IIStreamIn cnst = new RandomUniformVector(ran, width, mean); 
		testStatistics(numItems, cnst, new FilterVectorStatistic(mean), mean, 0);
		L.n();
		L.n("Searching for the Average and Variance of random uniformly distributed Numbers:");
		final IIStreamIn rand = new RandomUniformVector(ran, mean.length); 
		testStatistics(numItems, rand, new FilterVectorStatistic(mean), mean, 0.0833);
		L.n();
		L.n("Searching for the Average and Variance of random gaussian distributed Numbers:");
		final RandomGaussVector gauss = new RandomGaussVector(3); //  
		testStatistics(numItems, gauss, new FilterVectorStatistic(width), width, 1);
		L.n();
		L.n("Now the same Statistics of a Constant Distribution without a good Guess of the Mean:");
		testStatistics(numItems, cnst, new FilterVectorStatistic(), mean, 0);
		L.n();
		L.n("Now the same Statistics of a uniform Distribution without a good Guess of the Mean:");
		testStatistics(numItems, rand, new FilterVectorStatistic(), mean, 0.0833);
		L.n();
		L.n("Now the same Statistics of a gaussian Distribution without a good Guess of the Mean:");
		testStatistics(numItems, gauss, new FilterVectorStatistic(), width, 1);
	}
	
	/** tests CoVariance for Vectors not aligned with Coordinate Axes	 */
	private static final void testStatistic() {
		final int numItems = 1000*1000; 
		final FilterVectorStatistic stat = new FilterVectorStatistic();
		final float[] value = new float[3]; 
		for (int i = numItems; --i >= 0;) {
			VectorFloat.FILL_AT(value, RandomLong.NEXT_FLOAT()); //should result in a uniformly filled CoVariance Matrix
			stat.addVector(value); 
		}
		testStatistics(numItems, stat, new float[]{0.5f, 0.5f, 0.5f}, Double.NaN);
	}
	
	private static final void testStatistics( final long numItems, final IIStreamIn ran, final FilterVectorStatistic aveVar, 
	final float[] average, final double varDiag) {
		AStreamOut.STREAM(ran, aveVar, numItems);
		testStatistics(numItems, aveVar, average, varDiag);
	}
	
	private static final void testStatistics( final long numItems, final FilterVectorStatistic aveVar, 
	final float[] average, final double varDiag) {
		final long num = aveVar.getCount();
		//double adv = aveVar.getAbsDev();
		final double[] avg = aveVar.getAverage();
		final double[][]var = //Math.sqrt
		( aveVar.getVariance());
		L.n("Offset  =").l(aveVar.offSet); //
		L.n("Count   =").l(num); //
		//L.n("AbsDev <=").l(adv); //
		L.n("Average =").l(avg); //
		L.n("CoVarnc =").l(var); //
		final double accuracy = 2/Math.sqrt(numItems);
		L.n("accuracy=").l(accuracy);
		Assert.EQUALS(numItems, num); 
		//Assert.EQUALS(absDev  , adv, 2*accuracy); 
		Assert.EQUALS(avg, average, accuracy, accuracy);
		if (Double.isNaN(varDiag)) {
			return; }
		//Test for a scaled Unit Matrix 
		for (int i = var.length; --i >= 0;) {
			final double[] var_i = var[i]; 
			for (int j = var.length; --j >= 0;) {
				final double comp = (i == j) ? varDiag : 0; 
				Assert.EQUALS(comp, var_i[j], accuracy, accuracy); 
			}
		}
		//Assert.EQUALS(stdDev  , var, accuracy); //rather broad...
	}
	
	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}
	
}
