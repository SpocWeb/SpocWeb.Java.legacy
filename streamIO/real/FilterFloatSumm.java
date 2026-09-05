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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:15:20Z
 * digest: e7d3b8c812cc6ea33362f37fd1ace11113f27b089b7feae29322f56ae439383a
 * stale: false
 * tags: [code/stream_filter, code/running_statistics]
 * concepts: [Running Sum Filter]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public class FilterFloatSumm 
extends FilterFloatCache {

	/** Creates an integrator reading from {@code inStream_} through the given mapping function.
	 * @param inStream_ the source stream to integrate
	 * @param mapper_ optional function mapping each value before it is summed
	 */
	public FilterFloatSumm(final IStreamIn_Float inStream_, final IFloatFunction mapper_) {
		super(inStream_, mapper_); }

	/** Creates an integrator writing to {@code outStream_} through the given mapping function.
	 * @param outStream_ the destination stream for integrated output
	 * @param mapper_ optional function mapping each value before it is summed
	 */
	public FilterFloatSumm(final IStreamOutFloat outStream_, final IFloatFunction mapper_) {
		super(outStream_, mapper_); }

	/** Creates an integrator reading from {@code inStream_}, with the running sum pre-set.
	 * @param inStream_ the source stream to integrate
	 * @param startValue_ the initial value of the running sum
	 */
	public FilterFloatSumm(final IStreamIn_Float inStream_, final double startValue_) {
		super(inStream_, startValue_);
	}

	/** Creates an integrator writing to {@code outStream_}, with the running sum pre-set.
	 * @param outStream_ the destination stream for integrated output
	 * @param startValue_ the initial value of the running sum
	 */
	public FilterFloatSumm(final IStreamOutFloat outStream_, final double startValue_) {
		super(outStream_, startValue_);
	}

	/** Creates an integrator reading from {@code inStream_}, with no mapping function.
	 * @param inStream_ the source stream to integrate
	 */
	public FilterFloatSumm(final IStreamIn_Float inStream_) {
		super(inStream_); }

	/** Creates an integrator writing to {@code outStream_}, with no mapping function.
	 * @param outStream_ the destination stream for integrated output
	 */
	public FilterFloatSumm(final IStreamOutFloat outStream_) {
		super(outStream_); }

	/** adds a single Value to the Statistics, called by the stream Methods. 	 */
	public double addValue(final double value) {
		return lastValue += value; }
	
}
