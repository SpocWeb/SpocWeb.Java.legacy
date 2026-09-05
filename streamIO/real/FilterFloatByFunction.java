package streamIO.real;

import function.IFloatFunction;

/**
  * Processes the elements of either an input or an output float stream, counting them and
  * optionally mapping them through an {@link IFloatFunction}.
  *
  * <p>Class to process the Elements of either an Input or an Output streamIO.
  * Counts the Items passed through it, 
  * and optionally applies an IFloatFunction to them.  
  * Evaluates a single double Value and the Number of Elements
  * which both can be queried anytime!
  * 
  * This Class is an identical Filter, 
  * i.e. it hands the Values through unchanged. 
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-11-26, 01;13;44<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:11:12Z
  * digest: d4c95c1c41b029e92661fda6ebfbf29c6283d0024a841cfea7918e07bbb002c7
  * stale: false
  * tags: [code/stream_filter]
  * concepts: [Function-Based Float Filter]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public class FilterFloatByFunction
extends    FilterIn_FloatByFunction //CMeasurAble //ByRefDouble
implements IStreamOutFloat {
	
	/** Local Reference to the Generator	 */
	protected final IStreamOutFloat outStream;
	
	/** Initializing Constructor
	  * @param Generator the actual IStreamInNumber
	  * @param Scale     Maximum Value returned by this Filter */
	public FilterFloatByFunction(final IStreamIn_Float inStream_, final IFloatFunction mapper_) {
		super(inStream_, mapper_); 
		outStream = null; }
	
	/** Initializing Constructor
	  * @param Generator the actual IStreamInNumber
	  * @param Scale     Maximum Value returned by this Filter */
	public FilterFloatByFunction(final IStreamOutFloat outStream_, final IFloatFunction mapper_) {
		super(null, mapper_); 
		this.outStream = outStream_; }
	
	/** Initializing Constructor
	  * @param Generator the actual IStreamInNumber
	  * @param Scale     Maximum Value returned by this Filter */
	public FilterFloatByFunction(final IStreamIn_Float inStream_) {
		this(inStream_, null); }
	
	/** Initializing Constructor
	  * @param Generator the actual IStreamInNumber
	  * @param Scale     Maximum Value returned by this Filter */
	public FilterFloatByFunction(final IStreamOutFloat outStream_) {
		this(outStream_, null); }
	
	///////////////////////////////////////////////////////////////////////////////////////
	//	Interface IStreamIn_Float
	///////////////////////////////////////////////////////////////////////////////////////
	
	/** Reads, counts and maps the next value from the wrapped input stream.
	 * @return the next single Precision Number	 */
	public float nextFloat() { final float ret;
		currItem.Value = ret = addValue(map(inStream.nextFloat())); 
		return ret; }
	
	/** @return the next double Precision Number	 */
	protected double nextDoubleInternal() { 
		return addValue(map(inStream.nextDouble())); }
	
	///////////////////////////////////////////////////////////////////////////////////////
	//	Interface IStreamOutFloat
	///////////////////////////////////////////////////////////////////////////////////////
	
	/** adds the next single Precision Number
	 * @see streamIO.real.IStreamOutFloat#addFloat(float)
	 * @return this */
	public IStreamOutFloat addFloat(final float value_) { 
		final float ret = addValue(map(value_));
		if (outStream != null) 
			return outStream.addFloat(ret); 
		return this; }
	
	/** adds the next double Precision Number
	 * @see streamIO.real.IStreamOutFloat#addDouble(double)
	 * @return this */
	public IStreamOutFloat addDouble(final double value_) { //
		final double ret = addValue(map(value_));
		if (outStream != null) 
			return outStream.addDouble(ret); 
		return this; }
	
	///////////////////////////////////////////////////////////////////////////////////////
	//	These two Methods need to be overwritten to implement other Behavior
	///////////////////////////////////////////////////////////////////////////////////////
	
	/** adds a single Value to the Statistics, called by the stream Methods. 	 */
	final public float addValue(final float value) {
		return  (float)addValue(    (double)value);}
	
	/** adds a single Value to the Statistics, called by the stream Methods. 	 */
	public double addValue(final double value) { return value; }
	
}
