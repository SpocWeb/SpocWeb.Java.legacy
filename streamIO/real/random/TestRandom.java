package streamIO.real.random;

import java.util.Arrays;

import math.vector.VectorFloat;
import streamIO.Assert;
import streamIO.Log;
import streamIO.integer.IStreamIn_Int;
import streamIO.integer.random.IStreamIn_Bound_Int;
import streamIO.integer.random.RandomAffine;
import streamIO.integer.random.RandomBinomial;
import streamIO.integer.random.RandomBit;
import streamIO.integer.random.RandomBit2;
import streamIO.integer.random.RandomBySubt;
import streamIO.integer.random.RandomDiscrete;
import streamIO.integer.random.RandomFast;
import streamIO.integer.random.RandomJava;
import streamIO.integer.random.RandomLinear;
import streamIO.integer.random.RandomLong;
import streamIO.integer.random.RandomMix;
import streamIO.integer.random.RandomQuick;
import streamIO.integer.random.RandomShuffle;
import streamIO.real.FilterInMul;
import streamIO.real.FilterIn_FloatByFunction;
import streamIO.real.IStreamIn_Float;
import streamIO.real.StreamOutPlotter;
import streamIO.vector.random.RandomVectorQuasi;
import function.derive.ring.body.Gauss;

/**Tests all Methods of this Package
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:31:02Z
 * digest: f8a40a52b7cf70bd21ab8eb4df60c73cd6cb6a15ca4fd6fa51173a996b751d2c
 * stale: false
 * tags: [code/random_number_generator]
 * concepts: [Random Generator Test Harness]
 * facets: {layer: test, status: legacy, complexity: low}
 * -->
 */
public class TestRandom {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(TestRandom.class, 1);

	////////////////////////////////////////////////////////////////////////////

	/**Tests the given Random Number Generator
	 * using it to calculate the Volume of the Unit n-Spheres.
	 * Float Point Operations are sufficient,
	 * since the Result is evaluated statistically anyway!
	 * <!-- docstate
	 * tags: [code/random_number_generator]
	 * concepts: [Generator Self-Test]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	private static final void testGenerator(final IStreamIn_Float ran, final int maxPower) {
		final double[] actual = { Math.PI, 4*Math.PI/3, Math.PI*Math.PI/2}; 

		final int[] iy = new int[3];
		/* Calculates pi statistically, using the volume of unit n-sphere */
		//for (int i = iy.length; --i >= 0; ) { iy[i]=0; } 
		L.n("Volume of unit n-Sphere, n = 2, 3, 4");
		L.n("# points      pi        (4/3)*pi    (1/2)*pi^2\n");
		for (int l = 1, j = 0; ++j <= maxPower;) {
			for (int k = (l += l); --k >= 0;) {
				float sum, x;
				x = ran.nextFloat(); sum  = x*x;
				x = ran.nextFloat(); if ((sum += x*x) < 1) ++iy[0];  else continue;
				x = ran.nextFloat(); if ((sum += x*x) < 1) ++iy[1];  else continue;
				x = ran.nextFloat(); if ((sum += x*x) < 1) ++iy[2];//else continue;
			}
			L.n(); 
			for (int i = iy.length; --i >= 0; ) {
				final float factor = 1.0f/(l >> (i+1)); 
				final float x = iy[i]*factor; 
				L.l(x); 
				if (!Float.isInfinite(factor)) {
					Assert.EQUALS(actual[i], x, 1.1*Math.sqrt(factor));} 
			}
		}
		L.n("actual:"
		).l(actual[0]
		).l(actual[1]
		).l(actual[2]);
	}

	/**Tests and plots the Random Number Generator 
	 * by comparing it with a given Distribution 
	 * <!-- docstate
	 * tags: [code/random_number_generator]
	 * concepts: [Distribution Test Helper]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 * by sampling it between -1 and +1	 */
	final static public void TEST_RANDOM(final IStreamIn_Float ran,
	final float[] compare,
	final double scale,
	final int numBins,
	final double shift,
	final int NPTS,
	final String DevName) {
		ran.reSet(); 
		int[] dist = new int[numBins];
		//for(int j = N; --j >= 0;) { dist[j] = 0; } 
		for(int i = NPTS; --i >= 0;) {
			final int j = (int)Math.floor(ran.nextFloat()/scale + shift);
			//final int j = Math.round(ran.nextFloat()/scale + shift);
			if ((j >= 0) && 
				(j <  numBins)) { 
				++dist[j];
			}
		}
		final float uncertainty = 1f/NPTS; 
		final float[] actual = VectorFloat.MUL_AT(VectorFloat.COPY(dist), uncertainty); 
		L.n("\n"+DevName + " distributed deviate of " + NPTS + " points");
		L.n("x \t actual \t p(x) :");
		L.n(StreamOutPlotter.PLOT(actual, compare, 1, 4));//, 0, 0.1f));
		if (compare != null) { 
			Assert.EQUALS(compare, actual, 0, 2*Math.sqrt(uncertainty)); }  
	}

	/**Tests the Gaussian Random Number Generator
			Assert.EQUALS(compare, actual, 0, 2* <!-- docstate
			Assert.EQUALS(compare, actual, 0, 2* tags: [code/random_number_generator]
			Assert.EQUALS(compare, actual, 0, 2* concepts: [Distribution Test Helper]
			Assert.EQUALS(compare, actual, 0, 2* facets: {layer: test, status: legacy, complexity: low}
			Assert.EQUALS(compare, actual, 0, 2* -->
	 * by sampling it between -1 and +1	 */
	final static public void TEST_RANDOM(final IStreamIn_Int ran, final float[] compare,
	final int numBins, final int numPoints, final String distrName) {
//		if (N != compare.length) throw new AbstractMethodError();
		final int[] dist = new int[numBins];
		//for (int j = N; --j >= 0;) { dist[j] = 0; } 
		for (int i = numPoints; --i >= 0; ) {
			int j = ran.nextInt();
			if ((j >= 0) &&
				(j <  numBins)) ++dist[j];
		}
		final float uncertainty = 1f/numPoints; 
		final float[] actual = VectorFloat.MUL_AT(VectorFloat.COPY(dist), uncertainty);
		L.n(distrName + " distributed deviate of " + numPoints + " points");
		L.n("x \t p(x) \t graph:");
		L.n(StreamOutPlotter.PLOT(actual, compare, 1, 4));//, 0, 0.1f));
		if (compare != null) {
			Assert.EQUALS(compare, actual, 0, Math.sqrt(uncertainty)); }
	}

	/** tests an irregular Sequence / Stream of Bits 
	 * @param ran the stream to test...
	 * <!-- docstate
	 * tags: [code/random_number_generator]
	 * concepts: [Bit-Level Randomness Test]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */ 
	private static final void testRandomBit(final IStreamIn_Int ran, final double error) {
		final int numBins = 15;
		final long numPoints = 1000000;
		long ipts=0;
		final double[] delay = new double[numBins+1];

		/* Calculate distribution of runs of zeros */
		//for(int i = 1; i <= numBins; i++) { delay[i] = 0.0; } 
		L.n("\nDistribution of runs of n zeros");
		L.n("n \t probability \t expected");
		for(int i=1; i <= numPoints; i++) {
			if (ran.nextInt() == 1) {
				++ipts;
				boolean flag = false;
				for (int j=1; j <= numBins; j++) {
					if (((ran.nextInt() & 1) == 1) && (!flag)) {
						flag=true;
						++delay[j];
					}
				}
			}
		}
		double twoinv = 0.5;
		for (int i=1; i <= numBins; i++) {
			final double actual = delay[i]/ipts; 
			L.n((i-1) + "\t" + actual + "\t" + twoinv);
			Assert.EQUALS(twoinv, actual, 0, error/Math.sqrt(numPoints));
			twoinv /= 2;
		}
	}

	/**Tests the Gauss Random Number Generator
	 *
	 * <!-- docstate
	 * tags: [code/random_number_generator]
	 * concepts: [Gaussian Test Helper]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	private static final void testGauss(final IStreamIn_Float ran
	, final double scale, final double shift, final int numPoints, final int numBins) {
		final float[] compare = new float[numBins];
		for (int i = numBins; --i >= 0; ) { 
			compare[i] = (float) (Gauss.pGauss((i- shift)*scale)*scale); } 
		TEST_RANDOM(ran, compare, scale, numBins, shift+.5, numPoints, "Gauss");
	} //+.5 because of Truncation!

	/** Runs {@link #testIt(String[])}.
	 * @param args unused command-line arguments
	 * <!-- docstate
	 * tags: [code/random_number_generator]
	 * concepts: [Demo Entry Point]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	public static void main(final String[] args) throws Exception {
		testIt(args); }

	/** Exercises every random-number generator in this package against its expected distribution.
	 * @param args unused command-line arguments
	 * <!-- docstate
	 * tags: [code/random_number_generator]
	 * concepts: [Self-Test Method]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	public static void testIt(final String[] args) throws Exception {
		L.enter().println();
		testGenerators();
		RandomVectorQuasi.testIt(args);
		final byte numBins = 20;
		final int numPoints = 100000;
		final IStreamIn_Bound_Int ran = new RandomFast(); //RandomShuffle();
		final float binScale = 1f/numBins;
		final float[] compare = new float[numBins]; Arrays.fill(compare, binScale); 
		
		RandomChiSqr.testIt	(ran, 13, 0.5, 0, numPoints, numBins);
		RandomChiSqr.testIt	(ran,  3, 0.3, -0.6, numPoints, numBins); //Chi� has a very steep Start at 0!
		RandomChiSqr.testIt	(ran,  7, 0.5, -0.5, numPoints, numBins);
		
		RandomGamma.testIt	(ran, 7, numPoints, numBins); //and one above the Threshold
		testGauss	(new RandomGauss (ran), 0.2f, 10, numPoints, numBins);
		testRandomBit(new RandomBit2(), 5);
		testRandomBit(new RandomBit (), 1);
		testGauss	(new RandomGauss2(ran), 0.2f, 10, numPoints, numBins);
		RandomLorentz.testIt	(ran, 0.2f, 10, numPoints, numBins);
		RandomDiscrete.testIt();
		//does not converge well! Shape is different!
		//RandomKvSv.testIt	(ran,     1f/(numBins >> 1),  0, numPoints >> 3, numBins);
		RandomStudent.testIt	(ran, 12, 1f/(numBins >> 2), 10, numPoints, numBins);
		RandomFisherZ.testIt	(ran,  7, 13, 1f/(numBins >> 1), numBins >> 1, numPoints, numBins);
		RandomFisherF.testIt	(ran,  7, 13, 1f/(numBins >> 2), 0           , numPoints, numBins);
		RandomBeta.testIt	(ran, 7, 13, 1.0/numBins, 0, numPoints, numBins);
		FilterIn_FloatByFunction.testExponential(ran, -0.05f, 0, numPoints, numBins);
		RandomBinomial.testIt(ran, 100, 0.005f, numPoints, numBins);
		RandomBinomial.testIt(ran, 100, 0.100f, numPoints, numBins);
		RandomPoisson.testIt	(ran,  8, numPoints, numBins);
		RandomPoisson.testIt	(ran, 16, numPoints, numBins);
		TEST_RANDOM  (new FilterInMul(ran, 1.0), compare, binScale, numBins, 0, numPoints, "Uniform (Shuffle)");
		//testGamma	(ran, 5, NPTS, numBins); //one below the Threshold
	}

	private static void testGenerators() {
		testGenerator(new RandomFast	(), 19); //not quite as good as the others!!!
		testGenerator(new RandomBySubt	(), 19);
		testGenerator(new RandomAffine		(), 19);
		testGenerator(new RandomJava	(), 19);
		testGenerator(new RandomLinear	(), 19);
		testGenerator(new RandomLong	(), 19);
		testGenerator(new RandomMix		(), 19);
		testGenerator(new RandomQuick	(), 19);
		testGenerator(new RandomShuffle	(), 19);
	}

}
