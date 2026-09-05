/*
 * Created on 13.04.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer.multiplex;

import math.matrix.MatrixDouble;
import streamIO.Assert;
import streamIO.Log;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Validating the Transition Model of a Markov Series Order 1. 
 * 
 * The Values of Markov Time Series with Order N are random 
 * with a Probability that depends only on the last N Values.
 *  
 * Order 0: these are true random Values with fixed Probability Distributions. 
 * 		This is a stateless, randomized State Machine. 
 * Order 1: the next Value depends solely on the previous one, no other 'State' exists. 
 * 		This is a stateful, randomized State Machine. 
 * 
 * Keys to the Markov Theory are the following Assumptions: 
 * -The System is random, but the Probabilities don't change (e.g. with Time) 
 * -The System is closed, i.e. the next State depends on the N previous States only. 
 * 		This can, of course, always be achieved by extending the System 
 * 		and making the States nearly continuously distributed. 
 * 
 * For Chains of Order 1 this Forward Calculation of the Probability is O(#Observations), 
 * since it exploits the Fact, that the Probabilities of ALL previous Path Segments 
 * can be summed up in the last State of this Segment.   
 * The Full evaluation of all Probabilities along all Paths would be O(exp(#Observations)). 
 * For Chains of Order N, the Forward Calculation is O(#Observations*exp(N)) 
 * 
 * 
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * <!-- docstate
 * tags: [code/multiplexer, code/multiplexing, code/raid_encoding]
 * concepts: [RAID-Style Stream Multiplexing plus Markov/Viterbi Math]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class Markov1 {

	/** Computes the stationary (equilibrium) Probability Vector by raising the Transition Matrix to a high Power.
	 * @param matrix the Transition Matrix.
	 * @return the stationary Vector for the given Transition Matrix.
	 */
	public static final double[] STATIONARY(final double[][] matrix) {
		final double[][] base = MatrixDouble.BXP(matrix, 10);
		final double[] col = base[0]; 
		for(int i = base.length; --i > 0;) 
			col[i] = base[i][0]; 
		return col; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Constructors & Members 
	///////////////////////////////////////////////////////////////////////////
	
	/** this reflects the uncertain Knowledge about the initial State. 
	 * Since there may be additional Knowledge, 
	 * this is not necessarily the Equilibrium Probability Distribution 
	 * resulting from 
	 * @see #hiddenTransitProbs 
	 */
	protected final double[]	initialProbs;
	
	/** Transition Probabilities for the hidden State	 */
	protected final double[][]	transitionProbs;
	
	protected final String[] states; 
	
	/** Constructs an unnamed Markov Chain of Order 1 from initial and transition Probabilities.
	 * @param _initialProbs initial Probabilities
	 * @param _transitProbs transition Probabilities
	 */
	public Markov1(final double[] _initialProbs, final double[][] _transitProbs) {
		this(_initialProbs, _transitProbs, null);
	}
	
	/** Constructs a named Markov Chain of Order 1, validating that Probabilities sum to 1.
	 * @param _initialProbs initial    Probabilities
	 * @param _transitProbs transition Probabilities
	 * @param _stateNames Strings describing the Meaning of the States
	 */
	public Markov1(final double[] _initialProbs
			, final double[][] _transitProbs
			, final String[] _stateNames) {
		this.initialProbs = _initialProbs; 
		this.transitionProbs = _transitProbs; 
		this.states = _stateNames; 
		Assert.EQUALS( initialProbs.length, transitionProbs.length); 
		for(int i =  transitionProbs.length; --i >= 0;) {
			Assert.EQUALS(transitionProbs.length,  transitionProbs[i].length);
			double sum = 0; 
			for(int j = transitionProbs.length; --j >= 0;) 
				sum  += transitionProbs[j][i]; 
			Assert.EQUALS(1, sum); 
		}
		double sum = 0; 
		for(int j = initialProbs.length; --j >= 0;) 
			sum  += initialProbs[j]; 
		Assert.EQUALS(1, sum); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Methods 
	///////////////////////////////////////////////////////////////////////////
	
	/** Returns this Chain's stationary Probability Vector.
	 * @return the stationary Probability Vector,
	 * unless the Matrix is reduceable.
	 */
	public double[] stationary() { return STATIONARY(this.transitionProbs); }
	
	///////////////////////////////////////////////////////////////////////////
	/// Static main & Testing Methods, Test Data 
	///////////////////////////////////////////////////////////////////////////
	
	/** this Vector reflects the uncertain Knowledge about the initial (hidden) State. 
	 * Since there may be additional Knowledge, 
	 * this is not necessarily the Equilibrium Probability Distribution 
	 * resulting from 
	 * @see #hiddenTransitProbs 
	 */
	static final double[]   INITIAL_PROBS = {.6, .4}; 
	
	/** Transition Probabilities for the hidden State	 */
	static final double[][] TRANSIT_PROBS = { 
			{.7, .4}, 
			{.3, .6}
	}; 
	
	/** the Stationary Probability Distribution 	 */
	static final double[] STATIONARY = { 0.5714285714285708, 0.42857142857142816 };
	
	/** this Vector reflects the uncertain Knowledge about the initial (hidden) State. 
	 * Since there may be additional Knowledge, 
	 * this is not necessarily the Equilibrium Probability Distribution 
	 * resulting from 
	 * @see #hiddenTransitProbs 
	 */
	static final double[]   initialProbsSeaweed = {.63, .17, .2}; 
	
	/** Transition Probabilities for the hidden State	 */
	static final double[][] transitProbsSeaweed = { 
			{0.500, 0.250, 0.250}, 
			{0.375, 0.125, 0.375}, 
			{0.125, 0.625, 0.375} 
	}; 
	
	/** the Stationary Probability Distribution 	 */
	static final double[] stationarySeaweed = { 1./3, .3, 1 - 1./3 - .3 };
	
	/** The Logger for this Class	 */
	private static final Log L = new Log(Markov1.class); 
	
	/** Runs {@link #testIt()}. */
	public static void main(final String[] args) throws Exception {
		testIt();
	}

	/** Smoke-tests that {@link MatrixDouble#BXP} matches repeated squaring via {@link MatrixDouble#POW}. */
	public static void testPower() throws Exception {
		for(int i = -1; ++i < 6;)
			Assert.EQUALS(
					MatrixDouble.BXP(transitProbsSeaweed, i),
					MatrixDouble.POW(transitProbsSeaweed, 1 << i));
	}

	/** Runs {@link #testPower()} and validates the stationary Vector for both Test Matrices. */
	public static void testIt() throws Exception {
		testPower();
		testEigen(transitProbsSeaweed, stationarySeaweed);
		testEigen(TRANSIT_PROBS, STATIONARY);
	}

	/** Validates that {@link #STATIONARY} for the given Matrix matches the expected eigenvector.
	 * @param matrix the Transition Matrix to test
	 * @param eigenvector the expected stationary Probability Vector
	 */
	public static void testEigen(final double[][] matrix,
			final double[] eigenvector) throws Exception {
		final double[] eigen = STATIONARY(matrix);
		L.n(eigen); 
		Assert.EQUALS(eigenvector, eigen); 
	}
	
}
