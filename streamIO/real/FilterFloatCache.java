/*
 * Created on 14.11.2004
 *
 * Cache for a single Value. 
 * Continuously offsets the Stream Elements by one Element. 
 */
package streamIO.real;

import function.IFloatFunction;

/**
 * Filter for Float Numbers. 
 * Cache for a single Value. 
 * Continuously offsets the Stream Elements by one Element. 
 * @author heuerm
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:11:34Z
 * digest: f27b4dee74e43c0822ffb5bb862b62f4268164c243071ea2dc808c0078604411
 * stale: false
 * tags: [code/stream_filter]
 * concepts: [Float Value Cache Filter]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public class FilterFloatCache 
extends FilterFloatByFunction {

	/** stores the last Value to be able to return it, to differentiate or to integrate	 */
	public double lastValue; // = 0;
	
	/** Returns the last value added to or read from this stream.
	 * @return the last Value added to/read from this Stream	 */
	public double getLastValue() { return lastValue; }

	/////////////////////////////////////////////////////////////////////////////////////
	/// Constructors
	/////////////////////////////////////////////////////////////////////////////////////

	/** Creates a cache reading from {@code inStream_} through the given mapping function.
	 * @param inStream_ the source stream to cache
	 * @param mapper_ optional function mapping each value before it is cached
	 */
	public FilterFloatCache(final IStreamIn_Float inStream_, final IFloatFunction mapper_) {
		super(inStream_, mapper_);
	}

	/** Creates a cache writing to {@code outStream_} through the given mapping function.
	 * @param outStream_ the destination stream for cached output
	 * @param mapper_ optional function mapping each value before it is cached
	 */
	public FilterFloatCache(final IStreamOutFloat outStream_, final IFloatFunction mapper_) {
		super(outStream_, mapper_);
	}

	/** Creates a cache reading from {@code inStream_}, with {@link #lastValue} pre-set.
	 * @param inStream_ the source stream to cache
	 * @param startValue_ the initial value of {@link #lastValue}
	 */
	public FilterFloatCache(final IStreamIn_Float inStream_, final double startValue_) {
		super(inStream_);
		this.lastValue = startValue_;
	}

	/** Creates a cache writing to {@code outStream_}, with {@link #lastValue} pre-set.
	 * @param outStream_ the destination stream for cached output
	 * @param startValue_ the initial value of {@link #lastValue}
	 */
	public FilterFloatCache(final IStreamOutFloat outStream_, final double startValue_) {
		super(outStream_);
		this.lastValue = startValue_;
	}

	/** Creates a cache reading from {@code inStream_}, with no mapping function.
	 * @param inStream_ the source stream to cache
	 */
	public FilterFloatCache(final IStreamIn_Float inStream_) { super(inStream_); }

	/** Creates a cache writing to {@code outStream_}, with no mapping function.
	 * @param outStream_ the destination stream for cached output
	 */
	public FilterFloatCache(final IStreamOutFloat outStream_) { super(outStream_); }

	/** Empty Constructor, only for caching
	 */
	public FilterFloatCache() { super((IStreamIn_Float) null); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	///
	/////////////////////////////////////////////////////////////////////////////////////

	/** adds a single Value to the Statistics, called by the stream Methods. 	 */
	public double addValue(final double value) {
		return lastValue = value; }
	
}
