/*
 * Created on 12.11.2004
 *
 */
package streamIO.real.detector;

import streamIO.real.FilterFloatDiff;
import streamIO.real.FilterOutFloat;
import streamIO.real.IStreamOutFloat;

/**
 * Detects whenever incoming values cross a fixed threshold, changing from above to below it
 * or vice versa.
 *
 * <p>Can also be used to detect Roots, Minimum or Maximum Values (after Differentiation)
 * or Turning Points (after double Differentiation).
 * Since the Zero Line cannot (physically) be touched exactly, only approximately,
 * It makes Sense only for Initialization to store a Tri-State logical Value like int.
 *
 * @author heuerm
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:25:52Z
 * digest: e3c042bec832e913e2f064f11a8f7f3a45aa2da3d6b55e621d0815cf13963143
 * stale: false
 * tags: [code/anomaly_detection]
 * concepts: [Threshold Crossing Detector]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public class DetectorThreshold 
extends FilterOutFloat {

	/** the Value to compare with	 */
	protected double compareValue; //= 0;  
	
	/** counts the consecutive Events (here only one); the Sign indicates the Direction  */ 
	int countInDirection; //= 0; 

	/** Returns the sign of the last crossing direction: positive above, negative below, 0 before any value.
	 * @return the Direction this Threshold was crossed	 */
	public int getDirection() {
		return countInDirection;
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	/// Constructors 
	////////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor, defaults the Threshold to 0	 */
	public DetectorThreshold() { super((IStreamOutFloat) null); }
	
	/**
	 * initializing Constructor
	 * @param _compareValue the Threshold which triggers this Detector
	 */
	public DetectorThreshold(double _compareValue) {
		super((IStreamOutFloat) null); 
		this.compareValue = _compareValue;
	}
	
	/** Creates a threshold detector forwarding to the given delegate.
	 * @param outStream_ the destination stream to forward non-detected values to
	 */
	public DetectorThreshold(IStreamOutFloat outStream_) { super(outStream_); }
	
	/**
	 * @param inStream_
	 */
	//public DetectorThreshold(IStreamIn_Float inStream_) { super(inStream_); }
	/**
	 * @param inStream_
	 * @param mapper_
	 */
	//public DetectorThreshold(IStreamIn_Float inStream_, IFloatFunction mapper_) { super(inStream_, mapper_); }
	
	/**
	 * @param outStream_
	 * @param mapper_
	 */
	//public DetectorThreshold(IStreamOutFloat outStream_, IFloatFunction mapper_) {
	//	super(outStream_, mapper_); }
	
	///////////////////////////////////////////////////////////////////////////
	/// IStreamOutFloat
	///////////////////////////////////////////////////////////////////////////
	
	/** Forwards a float value to {@link #addDouble(double)}.
	 * @see streamIO.real.IStreamOutFloat#addFloat(float)	 */
	public IStreamOutFloat addFloat(final float value) {
		return addDouble(value); }

	/** Compares the value against the threshold and reports once the direction of crossing changes.
	 * @return this Object once a crossing was detected, otherwise the delegate's own result (often null)
	 * @see streamIO.real.IStreamOutFloat#addDouble(double)	 */
	public IStreamOutFloat addDouble(final double value) {
		final IStreamOutFloat ret = 
			(outStream != null) 
			?outStream.addDouble(value) : null; 
		final int prevCount = countInDirection; countInDirection = Double.compare(value, compareValue);
		if (prevCount == 0) 
			return ret; //no Event, especially after Construction
		if (prevCount == countInDirection) 
			return ret; 
		return this; //eats up Events from lower Levels! 
	}

	///////////////////////////////////////////////////////////////////////////
	/// static Testing & Main Methods. 
	///////////////////////////////////////////////////////////////////////////
	
	/** Demonstrates chained zero/min-max/turn-point detection over a sampled sine wave.
	 * @param args unused command-line arguments
	 */
	public static void main(final String[] args) throws Exception {
		DetectorThreshold detectorTurnPoint = new DetectorThreshold();
		FilterFloatDiff diff2 = new FilterFloatDiff((IStreamOutFloat) detectorTurnPoint); 
		DetectorThreshold detectorMinMax = new DetectorThreshold(diff2); 
		FilterFloatDiff diff1 = new FilterFloatDiff((IStreamOutFloat) detectorMinMax); 
		DetectorThreshold detectorZero = new DetectorThreshold(diff1); 
		for(double value = -1; (value += 0.01) < 7;) {
			IStreamOutFloat result = detectorZero.addDouble(Math.sin(value));
			if (result == detectorZero) 
				System.out.println("Zero detected at x="+value);
			else if (result == detectorMinMax)
				System.out.println("Min/Max detected at x="+value);
			else if (result == detectorTurnPoint)
				System.out.println("TurnPoint detected at x="+value);
		}
	}
	
}
