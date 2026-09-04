/*
 * Created on 13.11.2004
 *
 * Differentiator, continuously differentiates the Stream Elements. 
 */
package streamIO.real;

import streamIO.Assert;
import function.IFloatFunction;

/**
 * Filter for Float Numbers. 
 * Differentiator, continuously differentiates the Stream Elements. 
 * @see streamIO.real.FilterFloatSumm which is the Inverse Operator. 
 * @author heuerm
 */
public class FilterFloatDiff 
extends FilterFloatCache {

	/**
	 * @param inStream_
	 * @param mapper_
	 */
	public FilterFloatDiff(IStreamIn_Float inStream_, IFloatFunction mapper_) {
		super(inStream_, mapper_); }

	/**
	 * @param outStream_
	 * @param mapper_
	 */
	public FilterFloatDiff(IStreamOutFloat outStream_, IFloatFunction mapper_) {
		super(outStream_, mapper_); }

	/**
	 * @param inStream_
	 * @param startValue_
	 */
	public FilterFloatDiff(IStreamIn_Float inStream_, double startValue_) {
		super(inStream_, startValue_); }

	/**
	 * @param outStream_
	 * @param startValue_
	 */
	public FilterFloatDiff(IStreamOutFloat outStream_, double startValue_) {
		super(outStream_, startValue_); }

	/** @param inStream_	 */
	public FilterFloatDiff(IStreamIn_Float inStream_) { super(inStream_); }

	/** @param outStream_	 */
	public FilterFloatDiff(IStreamOutFloat outStream_) { super(outStream_); }

	/** adds a single Value to the Statistics, called by the stream Methods. 	 */
	public double addValue(final double value) {
		double ret = value - lastValue; lastValue = value; return ret; }
	
	///////////////////////////////////////////////////////////////////////////
	/// static Testing & Main Methods. 
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * tests whether the Concatenation of diff and summ results in the identical Mapping. 
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		FilterFloatCache cache1 = new FilterFloatCache(); 
		IStreamOutFloat  diff1 = new FilterFloatDiff((IStreamOutFloat) cache1); 
		IStreamOutFloat  summ1 = new FilterFloatSumm(diff1); 
		FilterFloatCache cache2 = new FilterFloatCache(); 
		IStreamOutFloat  summ2 = new FilterFloatSumm((IStreamOutFloat) cache2); 
		IStreamOutFloat  diff2 = new FilterFloatDiff(summ2); 
		for(int i = 99; --i >= 0;) {
			double value = Math.random(); 
			summ1.addDouble(value); Assert.EQUALS(value, cache1.getLastValue()); 
			diff2.addDouble(value); Assert.EQUALS(value, cache2.getLastValue()); 
		}
	}
	
}
