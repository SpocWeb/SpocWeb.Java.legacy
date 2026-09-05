/*
 * File Name: FilterFloatOutliers.java
 * Created on: 15.03.2004
 *
 */
package streamIO.real;

import streamIO.Assert;
import streamIO.integer.random.IStreamIn_Bound_Int;
import streamIO.integer.random.RandomLong;
import streamIO.real.random.RandomGauss;
import function.IFloatFunction;

/**
 * Continuously filters out statistical outliers beyond a given standard-deviation limit,
 * counting how many were rejected on each side.
 *
 * <p>Continuously filters out Outliers
 * and counts them to be able to judge their Frequency. 
 * This is only possible by the Fact that the Standard Deviation 
 * is very large with  
 * Note that this is not a reliable Practice due to: 
 * -removing of linear Trends 
 *  (rather use FilterFloatAverage or FilterFloatWindow to dampen these)
 * -Outliers should not be filtered a priori, but only a posteriori! 
 * -1-Dim Analysis is not usable to prepare Data for a multidimensional Analysis!  
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * similar Classes: 
 * @see streamIO.real.FilterFloatAverage
 * @see streamIO.real.FilterFloatWindow
 * @see streamIO.real.FilterFloatExpWindow
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:13:33Z
 * digest: 5dd28cac687b750661025d54c4650a9877179fb797c16804bc9473eef0f215ee
 * stale: false
 * tags: [code/stream_filter, code/anomaly_detection]
 * concepts: [Outlier Filter]
 * facets: {layer: infrastructure, status: legacy, complexity: medium}
 * -->
 */
public class FilterFloatOutliers extends FilterFloatStatistic {

	/** the Number of Outliers above	 */
	int outliersAbove = 0; 
	
	/** the Number of Outliers below	 */
	int outliersBelow = 0; 
	
	/** the Limit in estimated Standard Deviations to define an Outlier
	 * The Distribution shape is multiplied with a Rectangle Function 
	 * that cuts off at +/-limit*StdDev, resulting in a different Distribution! 	
	 * A Limit of 3.0 filters out the top and bottom  1.5  Permille 
	 * A Limit of 2.5 filters out the top and bottom  0.98 Percent
	 * A Limit of 2.0 filters out the top and bottom  8.7  Percent
	 * A Limit of 1.9 filters out the top and bottom 16.5  Percent
	 * you shouldn't go below this Value, because 1/3 of all Input is thrown away already!
	 */
	final double limit; 
	
	/** adds a single Value to the Statistics, called by the stream Methods. 	 */
	public boolean isOutlier(final double value) {
		final double stdDev = Math.sqrt(GET_VARIANCE(var, sum, count)/(count-1));
		final double avg = getAverage();
		final double z = (value-avg)/stdDev; //Math.sqrt((var-sum*avg)/(count-1)); 
		if (z >  limit) {
			++outliersAbove; 
			return true;} 
		if (z < -limit) {
			++outliersBelow; 
			return true;} 
		return false;} 
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Constructors	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Creates an outlier filter reading from {@code inStream_}, with an explicit mean offset guess.
	 * @param inStream_ the source stream to filter
	 * @param offset_ the initial guess of the mean, seeding the running statistics
	 * @param mapper_ optional function mapping each value before the outlier test
	 * @param limit_ the standard-deviation threshold beyond which a value counts as an outlier
	 */
	public FilterFloatOutliers
	( final IStreamIn_Float inStream_, final double offset_, final IFloatFunction mapper_
	, final double limit_) {
		super(inStream_, offset_, mapper_); this.limit = limit_;
	}

	/** Creates an outlier filter writing to {@code outStream_}, with an explicit mean offset guess.
	 * @param outStream_ the destination stream for filtered output
	 * @param offset_ the initial guess of the mean, seeding the running statistics
	 * @param mapper_ optional function mapping each value before the outlier test
	 * @param limit_ the standard-deviation threshold beyond which a value counts as an outlier
	 */
	public FilterFloatOutliers
	( final IStreamOutFloat outStream_, final double offset_, final IFloatFunction mapper_
	, final double limit_) {
		super(outStream_, offset_, mapper_); this.limit = limit_;
	}

	/** Creates an outlier filter reading from {@code inStream_}, with no mapping function.
	 * @param inStream_ the source stream to filter
	 * @param offset_ the initial guess of the mean, seeding the running statistics
	 * @param limit_ the standard-deviation threshold beyond which a value counts as an outlier
	 */
	public FilterFloatOutliers(final IStreamIn_Float inStream_, final double offset_
	, final double limit_) {
		super(inStream_, offset_); this.limit = limit_;
	}

	/** Creates an outlier filter writing to {@code outStream_}, with no mapping function.
	 * @param outStream_ the destination stream for filtered output
	 * @param offset_ the initial guess of the mean, seeding the running statistics
	 * @param limit_ the standard-deviation threshold beyond which a value counts as an outlier
	 */
	public FilterFloatOutliers(final IStreamOutFloat outStream_, final double offset_
	, final double limit_) {
		super(outStream_, offset_); this.limit = limit_;
	}

	/** Creates an outlier filter reading from {@code inStream_}, with no mean offset guess.
	 * @param inStream_ the source stream to filter
	 * @param limit_ the standard-deviation threshold beyond which a value counts as an outlier
	 */
	public FilterFloatOutliers(final IStreamIn_Float inStream_, final double limit_) {
		super(inStream_); this.limit = limit_;
	}

	/** Creates an outlier filter writing to {@code outStream_}, with no mean offset guess.
	 * @param outStream_ the destination stream for filtered output
	 * @param limit_ the standard-deviation threshold beyond which a value counts as an outlier
	 */
	public FilterFloatOutliers(final IStreamOutFloat outStream_, final double limit_) {
		super(outStream_); this.limit = limit_;
	}

	/** Creates a standalone outlier filter with an explicit mean offset guess and no attached stream.
	 * @param offset_ the initial guess of the mean, seeding the running statistics
	 * @param limit_ the standard-deviation threshold beyond which a value counts as an outlier
	 */
	public FilterFloatOutliers(final double offset_, final double limit_) {
		super(offset_); this.limit = limit_;
	}

	/** Creates a standalone outlier filter with no mean offset guess and no attached stream.
	 * @param limit_ the standard-deviation threshold beyond which a value counts as an outlier
	 */
	public FilterFloatOutliers(final double limit_) { super(); this.limit = limit_; }
	
	/////////////////////////////////////////////////////////////////////////////////////
	//	streaming Operation
	/////////////////////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////////////////
	//	Interface IStreamOutFloat
	///////////////////////////////////////////////////////////////////////////////////////

	/** adds the next single Precision Number
	 * @see streamIO.real.IStreamOutFloat#addFloat(float)
	 * @return this */
	final public IStreamOutFloat addFloat(final float value_) { 
		final float ret = map(value_); 
		if (isOutlier(ret)) { //ignore the Result
			return this; }
		addValue((double)ret);
		if (outStream != null) {
			outStream.addFloat(ret); }
		return this; }

	/** adds the next double Precision Number
	 * @see streamIO.real.IStreamOutFloat#addDouble(double)
	 * @return this */
	final public IStreamOutFloat addDouble(final double value_) { //
		final double ret = map(value_);
		if (isOutlier(ret)) { //ignore the Result
			return this; }
		addValue(ret);
		if (outStream != null) {
			outStream.addDouble(ret); }
		return this; }

	///////////////////////////////////////////////////////////////////////////////////////
	//	Interface IStreamIn_Float
	///////////////////////////////////////////////////////////////////////////////////////

	/** Reads and re-reads from the source stream, discarding outliers, until a non-outlier value is found.
	 * @return the next single Precision Number	 */
	final public float nextFloat() {
		for(;;) { 
			final float ret = map(inStream.nextFloat());
			if (isOutlier(ret)) 
				continue; 
			addValue((double)ret);
			currItem.Value = ret;
			return ret; 
		}
	}

	/** @return the next double Precision Number	 */
	protected final double nextDoubleInternal() {
		for(;;) { 
			final double ret = map(inStream.nextDouble());
			if (isOutlier(ret)) 
				continue; 
			addValue(ret);
			return ret; 
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Testing and Main Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.	 */
	public static void testIt() {	//Testing single Step of Pegasus Step with Quality Control
		L.n("\nTesting ").l(FilterFloatStatistic.class);
		final int numItems = 1000000; 
		final double value = 5; 
		final double outlierLimit = 2.5; 
		L.n(); 
		L.n("Searching for the Average and Variance of a constant Stream with Value ").l(value);
		final IStreamIn_Float cnst = new ConstStreamIn_Float(value); 
		testStatistics(numItems, cnst, new FilterFloatOutliers(0.5, outlierLimit), 9, value, 0, 0, 0, 10);
		L.n(); 
		L.n("Searching for the Average and Variance of random, equally distributed Numbers:");
		final RandomLong ran = new RandomLong(); //ran.randomize();
		//testStatistics(numItems, ran, new FilterFloatOutliers(0.5, outlierLimit), 0.25, 0.5, 0.2886, 0, -1.2, 10);
		L.n(); 
		L.n("Searching for the Average and Variance of random, normally distributed  Numbers:");
		final RandomGauss gauss = new RandomGauss((IStreamIn_Bound_Int) ran); // RandomFast());
		FilterFloatOutliers filter = new FilterFloatOutliers(0, outlierLimit);
		STREAM(gauss, filter, numItems);
		final double ratioOutliersAbove = filter.outliersAbove/(double) filter.count; 
		final double ratioOutliersBelow = filter.outliersBelow/(double) filter.count;
		L.n("Fraction of Outliers above Threshold:").l(ratioOutliersAbove); 
		L.n("Fraction of Outliers below Threshold:").l(ratioOutliersBelow); 
		final double expectedRatio = 0.00981;//Gauss.pGaussCum(-outlierLimit*filter.getStdDev());
		final double accuracy = 1/Math.sqrt(filter.count);
		Assert.EQUALS(expectedRatio, ratioOutliersAbove, accuracy);
		Assert.EQUALS(expectedRatio, ratioOutliersBelow, accuracy);
		L.n(); 
		L.n("Now the same Statistics for a Constant Distribution without a good Guess of the Mean:");
		testStatistics(numItems, cnst, new FilterFloatOutliers(outlierLimit), 0, value, 0, 0, 0, 0);
		L.n(); 
		L.n("Now the same Statistics for an equal Distribution without a good Guess of the Mean:");
		//testStatistics(numItems, ran, new FilterFloatOutliers(outlierLimit), 0.25, 0.5, 0.2886, 0, -1.2, 5);
		L.n(); 
		L.n("Now the same Statistics for an normal Distribution without a good Guess of the Mean:");
		//testStatistics(numItems, gauss, new FilterFloatOutliers(outlierLimit), 0.7979, 0, 1, 0, 0, 30);
	}

	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}

}
