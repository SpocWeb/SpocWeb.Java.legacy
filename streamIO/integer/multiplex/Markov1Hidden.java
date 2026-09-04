/*
 * Created on 12.04.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer.multiplex;

import math.vector.VectorInt;
import streamIO.Assert;
import streamIO.Log;
import streamIO.integer.AStreamWriteAble;
import streamIO.integer.IStreamOutStruct;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Defines the Hidden Markov Model (HMM) of Order 1. 
 * Hidden, because the Sequence of Outcomes cannot be determined directly. 
 * 
 * Implements the Viterbi Algorithm 
 * to determine the most probable hidden Sequence
 * and the Probability of this Sequence in the Hidden Markov Model (HMM, "Sequence"), 
 * based on the Sequence of a given Observation Variable, 
 * that depends statistically on the hidden Variable. 
 * 
 * This can be used as an error-correction scheme for noisy digital communication links, 
 * finding universal application in decoding the convolutional codes 
 * used in both CDMA and GSM digital cellular, dial-up modems, satellite, 
 * deep-space communications, and 802.11 wireless LANs. 
 * 
 * A brute alternative Approach for robust Transmission without ReSend is 
 * to send the Data three (or an uneven) Number of times 
 * and to choose those Values that appear in the Majority of Cases.
 * Of course the Question here is how large the repeated Group should be! 
 * If it is minimal = 1, any Noise longer than a single Character 
 * will still destroy the Message, since the at least the second Character is also disturbed. 
 * A long Window requires longer Caches on the Sender and Receiver Side 
 * and additionally the Streaming becomes chunky, reducing Efficiency.  
 * 
 * Viterbi is now also commonly used in speech recognition, keyword spotting, 
 * computational linguistics, and bioinformatics. 
 * For example, in speech-to-text speech recognition, 
 * the acoustic signal is treated as the observed sequence of events, 
 * and a string of text is considered to be the "hidden cause" of the acoustic signal. 
 * The Viterbi algorithm finds the most likely string of text given the acoustic signal.
 * 
 * The algorithm is not general; it makes a number of assumptions. 
 * First, both the observed events and hidden events must be in 1:1 Sequences. 
 * This sequence often corresponds to time. 
 * Second, these two sequences need to be aligned, 
 * and an observed event needs to correspond to exactly one hidden event. 
 * Third, computing the most likely hidden sequence up to a certain point t 
 * must depend only on the observed event at point t, 
 * and the most likely sequence at point t + 1. 
 * These assumptions are all satisfied in a first-order hidden Markov model.
 * 
 * It discovers the single most likely explanation for an observation. 
 * For example, in stochastic parsing a dynamic programming algorithm can be used 
 * to discover the single most likely context-free derivation (parse) of a string, 
 * which is sometimes called the "Viterbi parse".
 * 
 * Design Decisions / Implementation Details:
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
 */
final public class Markov1Hidden 
extends Markov1 {
	
	public static final int[] EMPTY_LIST = new int[0]; 
	
	/** initializing Constructor for the given Markov- Model  
	 * All Matrices and Vectors are expected to be column-Sum-Normed (transposed) to 1. 
	 * This is explicitly tested in the Constructor.  
	 * @param _initialProbs  the Vector of initial Probabilities. 
	 * @param _transitProbs  the Matrix of State Transition Probabilities. 
	 * @param _observedProbs the Matrix to probabilistically map hidden States to observable Values. 
	 */
	public Markov1Hidden(final double[] _initialProbs
			, final double[][] _transitProbs
			, final double[][] _observedProbs) {
		super(_initialProbs, _transitProbs); 
		observationProbs=_observedProbs; 
		//Assert.EQUALS(observationProbs.length, transitionProbs.length); 
		for(int i = observationProbs.length; --i >= 0;) 
			Assert.EQUALS(transitionProbs.length, observationProbs[i].length);
		for(int i =  transitionProbs.length; --i >= 0;) {
			double sum = 0; 
			for(int j = observationProbs.length; --j >= 0;) 
				sum  += observationProbs[j][i]; 
			Assert.EQUALS(1, sum); 
		}
		t0 = new Viterbi[initialProbs.length]; 
		t1 = new Viterbi[initialProbs.length]; 
		for(int state = initialProbs.length; --state >= 0;) { 
			t0[state] = new Viterbi(); 
			t1[state] = new Viterbi();
		}
	}
	
	/** Observation Probabilities to map the hidden States to observable Values	 */
	protected final double[][] observationProbs; 
	
	/** Vector to hold the Probabilities for t+0	 */
	protected transient Viterbi[] t0; 
	
	/** Vector to hold the Probabilities for t+1	 */
	protected transient Viterbi[] t1; 
	
	/** initialize the Space for Calculation 
	 * @param observationLength the maximum Index of Observations
	 */
	private void initCalc(final int observationLength) {
		for(int state = initialProbs.length; --state >= 0;) { 
			t0[state].ensureCapacity(observationLength); 
			t1[state].ensureCapacity(observationLength);
			t0[state].fill(initialProbs[state], EMPTY_LIST, 0, state, initialProbs[state]);
		}		
	}
	
	/** return the Viterbi-Path (the one with maximum Probability)
	 * @return the Viterbi-Path (the one with maximum Probability) 
	 */
	private Viterbi maxProbPath() { 
		//Collect the final sum/ find max. Prob.:
		Viterbi maxPath = null; 
		double  maxProb = 0; 
		double  sumProb = 0; 
		for(int state = initialProbs.length; --state >= 0;) {
			final Viterbi currT = t0[state]; 
			sumProb += currT.probAll; 
			if (maxProb < currT.probPath) { 
				maxProb = currT.probPath; 
				maxPath = currT; 
			}
		}
		maxPath.probAll = sumProb; 
		maxPath.probPath= maxProb; 
		return maxPath; 
	}
	
	/**
	 * @param observations the Sequence of Observations to compare 
	 * @return a Tripel containing the 
	 * Probability of the Model 
	 * Probability of the Sequence given this Model 
	 * Sequence of hidden Events plus the most probable next Event.  
	 */
	final public Viterbi calcPathProbs(final int[] observations) { 
		initCalc(observations.length);
		
		for(int j = 0; j < observations.length;) { //consider the observations from y IN SEQUENCE!!!
			final double[] observation = observationProbs[observations[j++]]; 
			for(int next_state = initialProbs.length; --next_state >= 0;)
				addState(j, observation, next_state);
			final Viterbi[] tmp = t0; t0 = t1; t1 = tmp; 
		}
		
		return maxProbPath(); 
	}
	
	/**
	 * @param position the Position to add the new most probable State at. 
	 * @param observation Only transposing the Matrices allowed to hand over the Vector only. 
	 * @param next_state the Sequence to add to   
	 */
	private void addState(final int position, final double[] observation, final int next_state) {
		final double[] transition = transitionProbs[next_state]; //Only transposing the Matrices allowed to hand over the Vector only. 
		int[]  maxPath = null; 
		double maxProb = 0; 
		double sumProb = 0; 
		for(int state = initialProbs.length; --state >= 0;) {
			final Viterbi currT = t0[state]; 
			double prob = currT.probAll; 
			double v_prob = currT.probPath; 
			final double p = observation[state] * transition[state]; 
			prob *= p; 
			sumProb += prob; 
			v_prob *= p; 
			if (maxProb < v_prob) {
				maxProb = v_prob; 
				maxPath = currT.path; // + [next_state]; 
			}
		}
		t1[next_state].fill(maxProb, maxPath, position, next_state, sumProb); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Static main & Testing Methods, Test Data 
	///////////////////////////////////////////////////////////////////////////
	
	/** Observation Probabilities for the hidden State	 */
	static final double[][] OBSERVED_PROBS = { 
			{.1, .6}, 
			{.4, .3}, 
			{.5, .1}
	}; 
	
	/** the actual Observations of which the Estimation of the hidden Events is derived 	 */
	static final int[]   OBSERVATIONS = {0, 1, 2}; 
	
	/** the Probability of the given Sequence in the given Model	 */
	static final double PROB_MODEL = 0.033612;  
	
	/** the Probability of the given Sequence in the given Model	 */
	static final double PROB_PATH = 0.009408;
	
	/** The most probable Path 	 */ 
	static final int[] PATH = {1,0,0,0};
	
	
	/** Observation Probabilities for the hidden State	 */
	static final double[][] OBSERVED_PROBS_SEAWEED = { 
			{0.60, 0.25, 0.05}, 
			{0.20, 0.25, 0.10}, 
			{0.15, 0.25, 0.35}, 
			{0.05, 0.25, 0.50}
	};
	
	/** the actual Observations of which the Estimation of the hidden Events is derived 	 */
	static final int[]   OBSERVATIONS_SEAWEED = {0, 2, 3}; 
	
	/** the Probability of the given Sequence in the given Model	 */
	static final double PROB_MODEL_SEAWEED = 0.02690140625; //0.0249109375;
	
	/** the Probability of the given Sequence in the given Model	 */
	static final double PROB_PATH_SEAWEED = 0.0041528320312499995; //0.0029900390625; 
	
	/** The most probable Path 	 */ 
	static final int[] PATH_SEAWEED = {0,1,2,2};
	
	/** The Logger for this Class	 */
	private static final Log L = new Log(Viterbi.class); 
	
	public static void testIt() {
		Viterbi totalArgmaxValmax;
		Markov1Hidden markov;
		///
		markov = new Markov1Hidden(initialProbsSeaweed, transitProbsSeaweed, OBSERVED_PROBS_SEAWEED); 
		totalArgmaxValmax = markov.calcPathProbs(OBSERVATIONS_SEAWEED); 
		L.n(totalArgmaxValmax); 
		Assert.EQUALS(PROB_PATH_SEAWEED, totalArgmaxValmax.probPath); 
		Assert.EQUALS(PROB_MODEL_SEAWEED, totalArgmaxValmax.probAll); 
		Assert.EQUALS(PATH_SEAWEED, totalArgmaxValmax.path); 
		///
		markov = new Markov1Hidden(INITIAL_PROBS, TRANSIT_PROBS, OBSERVED_PROBS); 
		totalArgmaxValmax = markov.calcPathProbs(OBSERVATIONS); 
		L.n(totalArgmaxValmax); 
		Assert.EQUALS(PROB_MODEL, totalArgmaxValmax.probAll); 
		Assert.EQUALS(PROB_PATH, totalArgmaxValmax.probPath); 
		Assert.EQUALS(PATH, totalArgmaxValmax.path); 
	}
	
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}
	
}

/** 
 * 
 * Title: <p>
 * Description:
 * Purpose:
 * Helper Value Class to hold intermediaries & 
 * return the Result from the Viterbi Calculation 
 *
 * Design Decisions / Implementation Details:
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
 */
final class Viterbi 
extends AStreamWriteAble {
	
	/** the most probable Viterbi path up to the current state	 */
	protected int[] path;  
	
	/** @return Returns the path.	 */
	public int[] getPath() { return path; }
	
	/** the probability of the most probable Viterbi path up to the current state.	 */
	protected double probPath; 
	
	/** @return Returns the probPath.	 */
	public double getProbPath() { return probPath; }
	
	/** the Sum of all Probabilities of all paths from the start to the current state, 
	 * i.e. the Probability of the Sequence given this Model of transition and observation.  	 */
	protected double probAll; 
	
	/** @return Returns the probAll.	 */
	public double getProbAll() { return probAll; }
	
	/** Deriving Classes must add their own Attributes and Elements (possibly via Reflection)  
	 * @see streamIO.integer.IStreamWriteAble#writeTo(streamIO.integer.IStreamOutStruct)	 */
	public void writeTo(final IStreamOutStruct stream) {
		stream.writeName  ("probAll" ).addDouble(probAll );
		stream.writeName  ("probPath").addDouble(probPath);
		stream.open_Struct("path"    ).addInts  (path).closeStruct();
	}
	
	/** Constructor to reserve Space for the given Input Sequence	 */
	public Viterbi() {
		probPath = probAll = 0; 
		path = Markov1Hidden.EMPTY_LIST; //new int[pathLength]; 
	}
	
	/** makes sure the given Index fits into the Array	 */
	public void ensureCapacity(final int len_1) {
		if (path.length <= len_1) {
			final int[] tmp = new int[len_1+1]; 
			System.arraycopy(path, 0, tmp, 0, path.length); 
			path = tmp; 
		}
	}
	
	/** fills the Values into this Tupel 
	 * 
	 * @param _probPath the prob. for this Path 
	 * @param _prevStates the Beginning of the Path 
	 * @param position the position to add the new State at 
	 * @param _state the new State Value to append 
	 * @param _probAll the overall Probability for this Model. 
	 */
	public void fill(final double _probPath, final int[] _prevStates, final int position,  
			final int _state, final double _probAll) {
		VectorInt.COPY_AT(path, _prevStates); 
		path[position]=_state; 
		probPath = _probPath; 
		probAll = _probAll; 
	}
	
}
