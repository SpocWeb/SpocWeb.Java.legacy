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
 */
public class FilterFloatCache 
extends FilterFloatByFunction {

	/** stores the last Value to be able to return it, to differentiate or to integrate	 */
	public double lastValue; // = 0;
	
	/** @return the last Value added to/read from this Stream	 */
	public double getLastValue() { return lastValue; }
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Constructors
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param inStream_
	 * @param mapper_
	 */
	public FilterFloatCache(final IStreamIn_Float inStream_, final IFloatFunction mapper_) {
		super(inStream_, mapper_);
	}
	
	/**
	 * @param outStream_
	 * @param mapper_
	 */
	public FilterFloatCache(final IStreamOutFloat outStream_, final IFloatFunction mapper_) {
		super(outStream_, mapper_);
	}

	/**
	 * 
	 * @param inStream_
	 * @param startValue_
	 */
	public FilterFloatCache(final IStreamIn_Float inStream_, final double startValue_) {
		super(inStream_); 
		this.lastValue = startValue_; 
	}

	/**
	 * 
	 * @param outStream_
	 * @param startValue_
	 */
	public FilterFloatCache(final IStreamOutFloat outStream_, final double startValue_) {
		super(outStream_); 
		this.lastValue = startValue_; 
	}

	/**
	 * @param inStream_
	 */
	public FilterFloatCache(final IStreamIn_Float inStream_) { super(inStream_); }

	/**
	 * @param outStream_
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
