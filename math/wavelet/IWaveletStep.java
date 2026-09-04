/*
 * File Name: IWaveletStep.java
 * Created on: 30.10.2003
 *
 */
package math.wavelet;

/**
 * Title: IWaveletStep<p>
 * Description:
 * Purpose:
 * Interface defining the Stepper Method for Wavelet Transformation.  
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
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
