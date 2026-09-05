/*
 * File Name: IWaveletStep.java
 * Created on: 30.10.2003
 *
 */
package math.wavelet;

/**
 * Defines the single Stepper Method that applies one Wavelet Filter Sweep to a Data Array,
 * implemented by each concrete Wavelet Filter (e.g. {@link Daubechies4}, {@link WaveletStep}).
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:52:19Z
 * digest: 1947d12136ced4ddb3292f59a4115a1dc3458857288467a5732f9bbb3da98c67
 * stale: false
 * tags: [code/wavelet_transform]
 * concepts: [Wavelet Step Interface]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface IWaveletStep {
	
	/**
	 * performs a single Sweep of Wavelet Transformation
	 * @param a Array to transform, switched with wksp afterwards. 
	 * @param n Size of the Array to transform
	 * @param inverse Flag for the inverse Transformation
	 * @param wksp optional Workspace to save Allocation
	 */
	//public void wtstep(double [] a, int n, boolean inverse, double [] wksp); 

	/**
	 * performs a single Sweep of Wavelet Transformation
	 * @param a Array to transform, switched with wksp afterwards. 
	 * @param n Size of the Array to transform
	 * @param inverse Flag for the inverse Transformation
	 * @param wksp optional Workspace to save Allocation
	 */
	public void wtstep(double [] a, int n, boolean inverse); 

}
