/*
 * File Name: FilterFloatAverage.java
 * Created on: 20.02.2004
 *
 */
package streamIO.real;

import math.matrix.MatrixFloat;
import streamIO.Assert;
import streamIO.Log;
import function.IFloatFunction;

/**
 * Filters the incoming data linearly using a fixed array of convolution coefficients.
 *
 * <p>Filters the incoming Data linearly, using an Array of Coefficients.
 * Performs the Convolution in Real Time, not in Fourier Space. 
 * Filtering N Samples with a Filter of Size M is an O(N*M) Operation! 
 * 
 * @see streamIO.real.FilterFloatWindow which averages all Fields 
 * with an identical Weight, resulting in an O(N*1) Operation 
 * independent of the Window Size. 
 *
 * Purpose / Responsibilities of this Class
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Similar Classes: 
 * @see streamIO.real.FilterFloatWindow is much faster, 
 * averaging the Elements with identical Weights 
 * resulting in a low Pass Filter.  
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
 * mtime: 2026-09-05T11:10:54Z
 * digest: e76c3c570d594721ac2cae008612dd0fd4aadf7e2097db0e8517be2bcc7acb40
 * stale: false
 * tags: [code/stream_filter, code/running_statistics]
 * concepts: [Running Average Filter]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public class FilterFloatAverage 
extends FilterFloatDelay {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(FilterFloatWindow.class);
	
	/////////////////////////////////////////////////////////////////////////////////////
	//	static Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Calculates the Savitzky-Golay smoothing coefficients (14.8)
	 * 
	 * @param numPast   number of Elements 'left ' of the Center
	 * @param numFuture number of Elements 'right' to the Center
	 * @param numDerivative Order of the Derivative to calculate, typically 0 for smoothing, 
	 * but for e.g. 1 the Order m should be >= 4 
	 * @param polynomOrder Order of the Smoothing Polynomial, 
	 * also the highest conserved Moment. 
	 * @return the Coefficients in Time Order starting with the earliest 
	 */
	final static public float[] SAVITZKY_GOLAY_COEFFICIENTS(final int numPast, final int numFuture
	, final int numDerivative, final int polynomOrder) {
		final float[] c = new float[numPast+numFuture+1];
		if ((numPast < 0) || (numFuture < 0)) {
			throw new RuntimeException("No negative Counts allowed for the Window Sizes: nl="+numPast+" nr="+numFuture); } 
		if (numDerivative > polynomOrder) {
			throw new RuntimeException("Order of the Polynomial "+polynomOrder+" is not sufficient for calculating the "+numDerivative+"th Derivative!"); } 
		if (numPast+numFuture < polynomOrder) {
			throw new RuntimeException("Number of Sample Points ("+numPast+"+1+"+numFuture+") is not sufficient for calculating a Polynomial of Order: "+polynomOrder); } 
		final float[][] a1=new float[polynomOrder+1][polynomOrder+1];
		for (int ipj=0;ipj<=(polynomOrder << 1);ipj++) { //Set up the Normal Equations...
			float sum=((ipj != 0) ? 0 : 1); //...of the desired Least Quares Fit
			for (int k=1; k<=numFuture; k++) {
				sum += Math.pow(k,ipj); } 
			for (int k=1; k<=numPast; k++) {
				sum += Math.pow(-k,ipj); } 
			final int mm=Math.min(ipj,2*polynomOrder-ipj);
			for (int imj = -mm; imj<=mm; imj+=2) {
				a1[(ipj+imj)/2][(ipj-imj)/2]=sum; } 
		}
		final int[] indx1=new int[polynomOrder+1];
		MatrixFloat.SPLIT_LU_AT(a1, indx1);
		
		final float[] b1=new float[polynomOrder+1];
		//for (int j=0; j<m+1; j++) { b1[j]=0; } //not necessary
		//Right-Hand Side is a Unit Vector; which depends on the wanted Derivative 
		b1[numDerivative]=1; //
		MatrixFloat.SOLVE_LU_AT(a1, indx1, b1);
		
		//VectorFloat.fillAt(c, 0); //Zero the Output Array, not necessary
		for (int k = -numPast; k<=numFuture; k++) {
			float sum=b1[0]; //Each Coefficient is the Scalar Product...
			float fac=1; //...of Powers of an Integer...
			for (int mm=1; mm<=polynomOrder; mm++) { //with the Inverse Matrix Row
				sum += b1[mm]*(fac *= k); } 
			final int kk=k+numPast; // 
			c[kk]=sum;
		}
		return c; 
	}
	
	/**Store in negative wrap-around Order 
	 * (first Elements are the negative Time Coefficients) 
	 * so that causal Filters (containing positive Time Coefficients) 
	 * start with nonzero Coefficients. 
	 */ 
	final static public float[] reOrder(final float[] coeff, final int numPast) {
		final float[] ret = new float[coeff.length];
		for (int k = coeff.length; --k>=0; ) {
			final int kk=(ret.length-(k-numPast)) % ret.length; //could be optimized by calculating ret.length+numPast
			ret[kk] = coeff[k];
		}
		return ret; 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	//	Member Variables
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** stores the Coefficients, used as a Queue.
	 * To preserve the Amplitude (0th Moment), all Coefficients should add up to 1	*/
	private final double[] coeff;
	
	/////////////////////////////////////////////////////////////////////////////////////
	//	Constructors
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Creates a filter reading from {@code inStream_} that averages using {@code coeff_}.
	 * @param inStream_ the source stream to filter
	 * @param coeff_ the convolution coefficients
	 * @param mapper_ optional function mapping the averaged value before delivery
	 * @param initValue the value the delay cache is pre-filled with
	 */
	public FilterFloatAverage( final IStreamIn_Float inStream_, final double[] coeff_, final IFloatFunction mapper_, final double initValue) {
		super(inStream_, coeff_.length, mapper_, initValue);
		this.coeff = coeff_;
	}

	/** Creates a filter writing to {@code outStream_} that averages using {@code coeff_}.
	 * @param outStream_ the destination stream for filtered output
	 * @param coeff_ the convolution coefficients
	 * @param mapper_ optional function mapping the averaged value before delivery
	 * @param initValue the value the delay cache is pre-filled with
	 */
	public FilterFloatAverage( final IStreamOutFloat outStream_, final double[] coeff_,
	final IFloatFunction mapper_, final double initValue) {
		super(outStream_, coeff_.length, mapper_, initValue);
		this.coeff = coeff_;
	}

	/** Creates a filter reading from {@code inStream_}, with the delay cache pre-filled with NaN.
	 * @param inStream_ the source stream to filter
	 * @param coeff_ the convolution coefficients
	 * @param mapper_ optional function mapping the averaged value before delivery
	 */
	public FilterFloatAverage(final IStreamIn_Float inStream_, final double[] coeff_, final IFloatFunction mapper_) {
		this(inStream_, coeff_, mapper_, Double.NaN); }

	/** Creates a filter writing to {@code outStream_}, with the delay cache pre-filled with NaN.
	 * @param outStream_ the destination stream for filtered output
	 * @param coeff_ the convolution coefficients
	 * @param mapper_ optional function mapping the averaged value before delivery
	 */
	public FilterFloatAverage(final IStreamOutFloat outStream_, final double[] coeff_, final IFloatFunction mapper_) {
		this(outStream_, coeff_, mapper_, Double.NaN); }

	/** Creates a filter reading from {@code inStream_}, with no output-mapping function.
	 * @param inStream_ the source stream to filter
	 * @param coeff_ the convolution coefficients
	 * @param initValue the value the delay cache is pre-filled with
	 */
	public FilterFloatAverage(final IStreamIn_Float inStream_, final double[] coeff_, final double initValue) {
		this(inStream_, coeff_, null, initValue); }

	/** Creates a filter writing to {@code outStream_}, with no output-mapping function.
	 * @param outStream_ the destination stream for filtered output
	 * @param coeff_ the convolution coefficients
	 * @param initValue the value the delay cache is pre-filled with
	 */
	public FilterFloatAverage(final IStreamOutFloat outStream_, final double[] coeff_, final double initValue) {
		this(outStream_, coeff_, null, initValue); }

	/** Creates a filter reading from {@code inStream_}, with the delay cache pre-filled with NaN.
	 * @param inStream_ the source stream to filter
	 * @param coeff_ the convolution coefficients
	 */
	public FilterFloatAverage(final IStreamIn_Float inStream_, final double[] coeff_) {
		this(inStream_, coeff_, null, Double.NaN); }

	/** Creates a filter writing to {@code outStream_}, with the delay cache pre-filled with NaN.
	 * @param outStream_ the destination stream for filtered output
	 * @param coeff_ the convolution coefficients
	 */
	public FilterFloatAverage(final IStreamOutFloat outStream_, final double[] coeff_) {
		this(outStream_, coeff_, null, Double.NaN); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	//	streaming Operation
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** The Cache is filled in wrap-around Order.
	 * The Average is the weighted Mean (Scalar Product) 
	 * of the Cached Data and the Coefficients.
	 */
	public double addValue(final double value) {
		super.addValue(value); //ignore the returned last Value
		double sum = 0;
		for(int i = coeff.length; --i >= 0; ) {
			int k = (i-coeff.length)+cachePtr;  
			if (k < 0) { //the Cache Size must be at least as large as the Coefficients Size!
				k += cache.length; } //actually the Cache could be larger!
			sum += coeff[i]*cache[k];
		}
		return sum;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** tests calculating the Coefficients of Savitzky-Golay Filters	 */
	private static final void testSGFilter() {
		//Parameters to create S-G-Filters
		final int[][] orderNumLeftNumRight 
		= { {2,2,2}, 
			{2,3,1},
			{2,4,0},
			{2,5,5}, 
			{4,4,4},
			{4,5,5},
			{0,0,0} 
		};
		//Expected Coefficient Arrays
		final float[][] expected1
		= { {-0.085714310f,  0.342857120f,  0.485714300f, 0.34285712f, -0.08571431f //symmetric!
		}, {-0.142857200f,  0.171428590f,  0.342857150f, 0.37142858f,  0.25714287f
		}, { 0.085714340f, -0.142857070f, -0.085714340f, 0.25714278f,  0.88571423f   //causal Filter
		}, {-0.083916080f,  0.020979017f,  0.102564100f, 0.16083916f,  0.19580418f, 0.20745920f, 0.19580418f,  0.16083916f,  0.102564100f,  0.020979017f, -0.083916080f //symmetric
		}, { 0.034965040f, -0.128205210f,  0.069930020f, 0.31468537f,  0.41724950f, 0.31468537f, 0.06993002f, -0.12820521f,  0.034965040f  //symmetric
		}, { 0.041958094f, -0.104895175f, -0.023310095f, 0.13986012f,  0.27972030f, 0.33333337f, 0.27972030f,  0.13986012f, -0.023310095f, -0.104895175f,  0.041958094f  //symmetric
		}, { 1 
		} };
		
		L.n("Sample Savitzky-Golay Coefficients");
		for (int i=orderNumLeftNumRight.length; --i>=0; ) {
			final int[] orderNumLeftNumRight_i = orderNumLeftNumRight[i];
			final int m =orderNumLeftNumRight_i[0]; //
			final int nl=orderNumLeftNumRight_i[1]; //
			final int nr=orderNumLeftNumRight_i[2]; //
			final int np=nl+nr+1;
			final float[] c= SAVITZKY_GOLAY_COEFFICIENTS(nl,nr,0,m);
			float sum=0; 
			for (int j=0; j<np; j++) { 
				sum += c[j]; } 
			L.n("").l(m).l(nl).l(nr).l("Sum of all Coefficients= ").l(sum);
			L.n().l(c); 
			Assert.EQUALS(1, sum);
			Assert.EQUALS(expected1[i], c);
		}
	}
	
	/** tests averaging 1 Element	 */
	private static final void testAverage() throws Exception {
		final StreamIn_Arithmetic s1 = new StreamIn_Arithmetic(); 
		final StreamIn_Arithmetic s2 = new StreamIn_Arithmetic(); 
		final FilterFloatAverage avg1 = new FilterFloatAverage(s2, new double[]{1});
		for (int i = 10; --i >= 0; ) {
			Assert.EQUALS(s1.nextDouble(), avg1.nextDouble()); 
		}
	}

	/** tests averaging 0 Elements	 */
	private static void testAvg0() {
		final StreamIn_Arithmetic s1 = new StreamIn_Arithmetic(); 
		//no Average first
		final FilterFloatAverage avg0 = new FilterFloatAverage(s1, new double[0], Math.PI);
		for (int i = 10; --i >= 0; ) {
			Assert.EQUALS(0, avg0.nextDouble());
		}
	}
	
	/** tests all Methods of this Class 	 */
	public static void testIt() throws Exception {
		testSGFilter();
		testAvg0();
		testAverage();
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws Exception {
		testIt();
	}
	
}
