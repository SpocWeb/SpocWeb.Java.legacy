/**
 * File  Name: RunningMean.java
 * Created on: 10.04.2003
 */
package function.real;
import java.util.Arrays;

/**
  * Title: FilterGlideMean<p>
  * Description:
  * Offsets the Elements of the streamIO, but hands them on unchanged,
  * so also other Operations can take place on them.
  * 
  * Especially useful to offset the streamIO for Sum 
  * and SumSquares for greater numerical Stability. 
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
  * mtime: 2026-09-05T16:44:49Z
  * digest: f2083985eabb479c85dba28dab3b576f2a74eaccd146c195a39c4add6a1e7e25
  * stale: false
  * tags: [code/running_aggregates, code/mathematical_function]
  * concepts: [Streaming Numeric Aggregator]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class RunningMean
extends StatefulFloatFunction {
	
	/** List of the previous Values to subtract them from the Mean */
	protected double[] _Bins; 
	
	/** Initializing Constructor
	  * @param numBins	 the Number of Bins to average across */
	public RunningMean(int numBins_) { //, double startValue) {
		super(0); //startValue*numBins_); 
		_Bins = new double[numBins_];
		_Count = -1; 
	}

	/** Returns the current gliding mean without advancing the filter.
	 * @return the current Gliding Mean of the Filter without advancing the Filter. 	 */
	public double getMean() { return _Value/_Bins.length; }

	/** Widens {@code value_} to {@code double} and delegates to {@link #Map(double)}.
	 * @see function.IFloatFunction#Map(float)	 */
	public float Map(float value_) {
		return (float) Map((double) value_); }

	/** Folds {@code value} into the running window, evicting the oldest bin.
	 * @see function.IFloatFunction#Map(double)	 */
	public double Map(double value) {
		if (--_Count < 0) { //Rollover
			if (_Count < -1) { //Initialization with Constant Value
				InitializeToConstant(value);
				return value; 
			}
			_Count = _Bins.length - 1;
		}
		_Value -= _Bins[_Count]; //remove the last Bin...
		return _Value += (_Bins[_Count] = value); //...and fill it with the incoming Value
	}

	protected void InitializeToConstant(double value) {
		_Value = value * _Bins.length;
		Arrays.fill(_Bins, value);
		_Count = _Bins.length - 1;
	}

}
