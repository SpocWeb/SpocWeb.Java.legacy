/*
 * File Name: Wavelet.java
 * Created on: 30.10.2003
 *
 */
package math.wavelet;

import math.vector.VectorDouble;
import streamIO.Assert;

/**
 * Collects static Methods to transform real Vectors into Wavelet Space and back, for both
 * one-dimensional and multidimensional Data, given a pluggable {@link IWaveletStep}.
 *
 * Known SubClasses: <none>
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
 * mtime: 2026-09-05T11:53:03Z
 * digest: 3cd6b021660f26dcab84e3789ae5ffd81dbe99f36aba8b7c18b6bd0fe738f558
 * stale: false
 * tags: [code/wavelet_transform]
 * concepts: [Wavelet Transform Driver]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class Wavelet {
	
	/**	WT1     one-dimensional discrete wavelet transform (13.10)
	 * 
	 * @param a Vector to transform
	 * @param n Portion of the Vector to transform
	 * @param inverse Flag to perform the inverse Transformation 
	 * @param wtstep Stepper Routine 
	 * <!-- docstate
	 * tags: [code/wavelet_transform]
	 * concepts: [Single-Level Wavelet Transform]
	 * facets: {layer: utility, status: legacy, complexity: low}
	 * -->
	 */
	final static public void transformWavelet
	( final double[] a, final int n, final boolean inverse, final IWaveletStep wtstep) {
		if (n < 4) return;
		if (inverse) {
			for (int nn=4; nn<=n; nn<<=1) {
				wtstep.wtstep(a, nn, inverse); } 
		} else {
			for (int nn=n; nn>=4; nn>>=1) {
				wtstep.wtstep(a, nn, inverse); } 
		}
	}
	
	/**	multidimensional discrete wavelet transform (13.10)
	 * 
	 * @param a Tensor of Dimensions nn[0]*nn[1]*...*nn[m] to transform
	 * @param nn Array of Dimensions
	 * @param inverse Flag to perform the inverse Transformation 
	 * @param wtstep Stepper Routine to perform the individual Step
	 * <!-- docstate
	 * tags: [code/wavelet_transform]
	 * concepts: [Multi-Level Wavelet Transform]
	 * facets: {layer: utility, status: legacy, complexity: low}
	 * -->
	 */
	final static public void transformWavelet
	( final double[] a, final int[] nn, final boolean inverse, final IWaveletStep wtstep) {
		int nTot=1;
		for (int idim=nn.length; --idim >= 0; ) {
			nTot *= nn[idim]; } 
		Assert.EQUALS(nTot, a.length); //
		final double[] wksp=new double[nTot]; //requires Workspace of same Size...
		for (int nPrev=1, iDim=0; iDim < nn.length; iDim++) {
			final int n=nn[iDim];
			final int nNew=n*nPrev;
			if (n > 4) {
				transformWavelet(a, inverse, wtstep, nTot, wksp, nPrev, n, nNew); }
			nPrev = nNew;
		}
	}

	/** @see #transformWavelet(double[], int[], boolean, IWaveletStep) uses this Method exclusively for Recursion 	 */
	private static void transformWavelet(
		final double[] a,
		final boolean inverse,
		final IWaveletStep wtstep,
		final int nTot,
		final double[] wksp,
		final int nPrev,
		final int n,
		final int nNew) {
		for (int i2=0; i2<nTot; i2+=nNew) {
			for (int i1=0; i1<nPrev; i1++) {
				for (int i3=i1+i2, k=0; k<n; k++, i3+=nPrev) { //fill the Workspace...
					wksp[k]=a[i3]; } 
				transformWavelet(wksp, n, inverse, wtstep); 
				for (int i3=i1+i2, k=0; k<n; k++, i3+=nPrev) { //copy back the Workspace...
					a[i3]=wksp[k]; } 
			}
		}
	}

	/////////////////////////////////////////////////////////////////////////////////
	/// testing the Wavelet Methods in this Class
	/////////////////////////////////////////////////////////////////////////////////

	private static final int NX = 1 << 3;
	private static final int NY = 1 << 4;
	private static final int[] ndim={NX,NY};

	/** Runs the 1D and multidimensional Wavelet round-trip Tests for every registered Filter.
	 *
	 * <!-- docstate
	 * tags: [code/wavelet_transform]
	 * concepts: [Self-Test Method]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	final static public void testIt() {
		testWavelet1Dim(Daubechies4.SINGLETON);
		testWavelet1Dim(WaveletStep.GET_PARTIAL_TRAFO( 4));
		testWavelet1Dim(WaveletStep.GET_PARTIAL_TRAFO( 6));
		testWavelet1Dim(WaveletStep.GET_PARTIAL_TRAFO(12));
		testWavelet1Dim(WaveletStep.GET_PARTIAL_TRAFO(20));
		testWaveletNDim(Daubechies4.SINGLETON, 1e-15);
		testWaveletNDim(WaveletStep.GET_PARTIAL_TRAFO( 4), 1e-15);
		testWaveletNDim(WaveletStep.GET_PARTIAL_TRAFO( 6), 1e-14);
		testWaveletNDim(WaveletStep.GET_PARTIAL_TRAFO(12), 1e-11);
		testWaveletNDim(WaveletStep.GET_PARTIAL_TRAFO(20), 1e-10);		
	}

	private static final void testWaveletNDim(final IWaveletStep trafo, final double maxDiff) {
		final int nTot=NX*NY;
		final double[] aOrig = new double[nTot]; // vector(1,ntot);
		final double[] a =new double[nTot]; //vector(1,ntot);
		for (int i=0; i<NX; i++) {
			for (int j=0; j<NY; j++) {
				final int l=i+j*NX;
				aOrig[l]=a[l]=((i == j) ? -1 : 1/Math.sqrt(Math.abs((i-j))));
			}
		}
		transformWavelet(a,ndim, false, trafo); //; //
		/* here, one might set the smallest components to zero, encode and transmit
		the remaining components as a compressed form of the "image" */
		transformWavelet(a,ndim, true , trafo); //Daubechies4.SINGLETON); //WaveletStep.GET_PARTIAL_TRAFO(4)); //
		System.out.println("\nUsing Wavelet Trafo: "+trafo);
		System.out.println("\n\tntot="+nTot);
		Assert.EQUALS(aOrig, a); //, maxDiff);
	}

	/////////////////////////////////////////////////////////////////////////////////

	private static final int NUM_VALUES = 1 << 9;
	private static final int CENTER_POSITION = 332; //333;
	private static final int WIDTH =  33;

	private static final void testWavelet1Dim(final IWaveletStep trafo) {
		double frac = 2;
		System.out.print("\tNMAX="+NUM_VALUES);
		System.out.println("\tDaubechies Trafo: "+trafo);
		for (int i = 0;++i < 9;) {
			testWaveletReduced(frac, trafo);
			frac /= 2;
		}
	}

	static void testWaveletReduced(final double frac, final IWaveletStep trafo) {
		final double[] values  =new double[NUM_VALUES]; //
		final double[] original=new double[NUM_VALUES]; //
		for (int i=0; i<NUM_VALUES; i++) {
			original[i]=values[i]=((i > CENTER_POSITION-WIDTH) && (i < CENTER_POSITION+WIDTH) ?
				((double)(i-CENTER_POSITION+WIDTH)*(double)(CENTER_POSITION+WIDTH-i))/(WIDTH*WIDTH) : 0.0);
		}
		transformWavelet(values, NUM_VALUES, false, trafo); //WaveletStep.GET_PARTIAL_TRAFO(4)); //Daubechies4.SINGLETON); //
		//find out Threshold below which to zero out Coefficients...
		final double[] absValues=new double[NUM_VALUES]; //vector(1,NMAX);
		for (int i=0; i<NUM_VALUES;i++) { 
			absValues[i]=Math.abs(values[i]); } 
		//double maxDiff=frac; //select((int)((1.0-frac)*NMAX),NMAX,u); //select the n-th largest Coefficient.
		int nused=0;
		for (int i=0; i<NUM_VALUES; i++) {
			if (Math.abs(values[i]) <= frac)
				values[i]=0;
			else
				nused++;
		}
		transformWavelet(values,NUM_VALUES, true, trafo); //WaveletStep.GET_PARTIAL_TRAFO(4)); //Daubechies4.SINGLETON); //
		double maxDiff=0;
		for (int i=0; i<NUM_VALUES; i++) {
			final double tmp=Math.abs(values[i]-original[i]);
			if (maxDiff < tmp) {
				maxDiff = tmp; } 
		}
		System.out.print("\tThreshold ="+frac);
		System.out.print("\tnused="+nused); Assert.IS_TRUE(nused < 45);
		System.out.print("\tMaximum discrepancy= "+maxDiff); Assert.IS_TRUE(maxDiff < frac+frac);
		System.out.print("\tMaximum Function Value= "+VectorDouble.MAX_VAL(absValues));
		System.out.println();
	}

	/** Runs {@link #testIt()} as the externally visible Main Method.
	 *
	 * <!-- docstate
	 * tags: [code/wavelet_transform]
	 * concepts: [Demo Entry Point]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	final static public void main(String[] args) {
		testIt();
	}

}
