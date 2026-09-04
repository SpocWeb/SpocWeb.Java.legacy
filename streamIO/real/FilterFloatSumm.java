/*
 * Created on 13.11.2004
 *
 * Integrator, continuously integrates (sums up) the Stream Elements.
 */
package streamIO.real;

import function.IFloatFunction;

/**
 * Filter for Float Numbers. 
 * Integrator, continuously integrates (sums up) the Stream Elements.
 * @see streamIO.real.FilterFloatDiff is the inverse Operator 
 * @author heuerm
 *
 */
public class FilterFloatSumm 
extends FilterFloatCache {

	/**
	 * @param inStream_
	 * @param mapper_
	 */
	public FilterFloatSumm(final IStreamIn_Float inStream_, final IFloatFunction mapper_) {
		super(inStream_, mapper_); }

	/**
	 * @param outStream_
	 * @param mapper_
	 */
	public FilterFloatSumm(final IStreamOutFloat outStream_, final IFloatFunction mapper_) {
		super(outStream_, mapper_); }

	/**
	 * 
	 * @param inStream_
	 * @param startValue_
	 */
	public FilterFloatSumm(final IStreamIn_Float inStream_, final double startValue_) {
		super(inStream_, startValue_); 
	}

	/**
	 * 
	 * @param outStream_
	 * @param startValue_
	 */
	public FilterFloatSumm(final IStreamOutFloat outStream_, final double startValue_) {
		super(outStream_, startValue_); 
	}

	/**
	 * @param inStream_
	 */
	public FilterFloatSumm(final IStreamIn_Float inStream_) {
		super(inStream_); }

	/**
	 * @param outStream_
	 */
	public FilterFloatSumm(final IStreamOutFloat outStream_) {
		super(outStream_); }

	/** adds a single Value to the Statistics, called by the stream Methods. 	 */
	public double addValue(final double value) {
		return lastValue += value; }
	
}
