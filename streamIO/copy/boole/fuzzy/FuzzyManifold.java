/*
 * File Name: FuzzyManifold.java
 * Created on: 31.12.2003
 *
 */
package streamIO.copy.boole.fuzzy;

import streamIO.Assert;
import streamIO.Log;

/**
 * Title: FuzzyManifold<p>
 * Description:
 * Models a 1-dim. fuzzy Manifold 
 * by grouping an (ordered) Set of Fuzzy Numbers 
 * 
 * supported Operations are: 
 * Fuzzification (Categorization) 
 * De-Fuzzification (resulting in a float) 
 * Categorization (resulting in a discrete) by selecting the most applicable Category
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
 * mtime: 2026-09-05T20:45:07Z
 * digest: 9d50ed2ee16fff46e8e224d3f84d3efe0e374c6ddc8b57ab1acb68010e2a9779
 * stale: false
 * tags: [code/fuzzy_logic]
 * concepts: [Fuzzy Logic]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class FuzzyManifold {

	/** Logger to be used for Output 	 */
	final static public Log L = new Log(FuzzyManifold.class, 0); 

	/////////////////////////////////////////////////////////////////////////////////////
	/// Fuzzy Manifold Examples
	/////////////////////////////////////////////////////////////////////////////////////

	/** Data for 1Dim Classification of Traffic by Speed */ 
	private static final Object[][] TRAFFIC_VELOCITY_KM_H_DATA = {
		{"jam"  , new float[]{Float.NEGATIVE_INFINITY, 20, 50}}, 
		{"stop" , new float[]{20, 50, 80}}, 
		{"float", new float[]{50, 80, Float.POSITIVE_INFINITY}}
	};
	
	/** Manifold for 1Dim Classification of Traffic by Speed
	 * Actually you also need Car Density and Flow
	 */ 
	final static public FuzzyManifold TRAFFIC_VELOCITY_KM_H = new FuzzyManifold("TrafficStateByKm_h", TRAFFIC_VELOCITY_KM_H_DATA);
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Factory Methods
	/////////////////////////////////////////////////////////////////////////////////////

	/**Builds one {@link FuzzyNumber} per name/values pair.
	 * @param values an Array of Values to initialize this FuzzyNumber
	 * @return a FuzzyNumber initialized by this Array
	 */
	final static public FuzzyNumber[] FuzzyNumber(final String[] names, final float[][] values) {
		FuzzyNumber[] ret = new FuzzyNumber[values.length];
		for (int i = ret.length; --i >= 0; ) {
			ret[i] = FuzzyNumber(names[i], values[i]);
		}
		return ret; 
	}
	
	/**Builds one {@link FuzzyNumber} per (name, values) pair packed as an {@code Object[2]} row.
	 * @param valuesNames an Array of Values and Names to initialize this FuzzyNumber
	 * @return a FuzzyNumber initialized by this Array
	 */
	final static public FuzzyNumber[] FuzzyNumber(final Object[][] namesValues) {
		FuzzyNumber[] ret = new FuzzyNumber[namesValues.length];
		for (int i = ret.length; --i >= 0; ) {
			ret[i] = FuzzyNumber(
				(String )namesValues[i][0], 
				(float[])namesValues[i][1]);
		}
		return ret; 
	}
	
	/**Builds a {@link FuzzyNumber} from a 2-, 3- or 4-element values array (left/mid/right/height).
	 * @param values an Array of Values to initialize this FuzzyNumber
	 * @return a FuzzyNumber initialized by this Array
	 */
	final static public FuzzyNumber FuzzyNumber(final String name, final float[] values) {
		switch (values.length) {
			case 2 : return new FuzzyNumber(values[0], values[1], name);
			case 3 : return new FuzzyNumber(values[0], values[1], values[2], name);
			case 4 : return new FuzzyNumber(values[0], values[1], values[2], values[3], name);
			default : throw new RuntimeException("Invalid number of Parameters:"+values.length); 
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// static Methods
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * prints out all Rules with 1 Dependant semantically attributed 
	 * with the Categories from the given Dimensions. 
	 * @param prefix String to prepend 
	 * @param dim1 Input Categorization 
	 * @param rules The rules to describe 
	 * @param dim2 Output Categorization 
	 */	
	protected static final void printRules(final String prefix, final FuzzyManifold dim1, final byte[] rules, final FuzzyManifold dim2) {
		for (int j = rules.length; --j >= 0; ) {
			System.out.print(prefix);
			System.out.print(dim1.getDescription(j)+" THEN ");
			System.out.print(dim2.getDescription(rules[j]));
			System.out.println();
		}
	}

	/** 
	 * prints out all Rules with 1 Dependant semantically attributed 
	 * with the Categories from the given Dimensions. 
	 * @param dim1 Input Categorization 
	 * @param rules The rules to describe 
	 * @param dim2 Output Categorization 
	 */
	final static public void printRules(final FuzzyManifold dim1, final byte[] rules, final FuzzyManifold dim2) {
		printRules("IF ", dim1, rules, dim2); }

	/**
	 * prints out all Rules with 2 Dependants semantically attributed 
	 * with the Categories from the given Dimensions. 
	 * @param prefix
	 * @param dim1 Input Categorization 
	 * @param dim2 Input Categorization 
	 * @param rules The rules to describe 
	 * @param dim3 Output Categorization 
	 */	
	protected static final void printRules(final String prefix, final FuzzyManifold dim1, final FuzzyManifold dim2, final byte[][] rules, final FuzzyManifold dim3) {
		for (int i = rules.length; --i >= 0; ) {
			printRules(prefix+dim1.getDescription(i)+" AND ", dim2, rules[i], dim3); 		}
	}
	
	/** 
	 * prints out all Rules with 2 Dependants semantically attributed 
	 * with the Categories from the given Dimensions. 
	 * @param dim1 Input Categorization 
	 * @param dim2 Input Categorization 
	 * @param rules The rules to describe 
	 * @param dim3 Output Categorization 
	 */
	final static public void printRules(final FuzzyManifold dim1, final FuzzyManifold dim2, final byte[][] rules, final FuzzyManifold dim3) {
		printRules("IF ", dim1, dim2, rules, dim3); }

	/** 
	 * prints out all Rules with 3 Dependants semantically attributed 
	 * with the Categories from the given Dimensions. 
	 * @param prefix
	 * @param dim1 Input Categorization 
	 * @param dim2 Input Categorization 
	 * @param dim3 Input Categorization 
	 * @param rules The rules to describe 
	 * @param dim4 Output Categorization 
	 */
	protected static final void printRules(final String prefix, final FuzzyManifold dim1, final FuzzyManifold dim2, final FuzzyManifold dim3, final byte[][][] rules, final FuzzyManifold dim4) {
		for (int i = rules.length; --i >= 0; ) {
			printRules(prefix+dim1.getDescription(i)+" AND ", dim2, dim3, rules[i], dim4); 		}
	}

	/** 
	 * prints out all Rules with 3 Dependants semantically attributed 
	 * with the Categories from the given Dimensions. 
	 * @param dim1 Input Categorization 
	 * @param dim2 Input Categorization 
	 * @param dim3 Input Categorization 
	 * @param rules The rules to describe 
	 * @param dim4 Output Categorization 
	 */
	final static public void printRules(final FuzzyManifold dim1, final FuzzyManifold dim2, final FuzzyManifold dim3, final byte[][][] rules, final FuzzyManifold dim4) {
		printRules("IF ", dim1, dim2, dim3, rules, dim4); }

	/////////////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	/////////////////////////////////////////////////////////////////////////////////////

	/** Name of this Manifold / Dimension	 */
	final public String name;
	
	/** a complete Set of Categories 	 */
	private final FuzzyNumber[] categories;

	/**Returns the fuzzy number for the given category index.
	 * @return the indicated Category 	 */
	public FuzzyNumber getCategory(final int category) {
		return categories[category]; }

	/** initializing Constructor
	 * creates an ordered Set of 
	 * @param leftCenter
	 * @param rightCenter
	 * @param overlap
	 * @param numItems
	 */
	public FuzzyManifold(final String name_, final float[][] values, final String[] names) {
		this.name = name_;
		categories = FuzzyNumber(names, values);
	}

	/** initializing Constructor
	 * creates an ordered Set of 
	 * @param leftCenter
	 * @param rightCenter
	 * @param overlap
	 * @param numItems
	 */
	public FuzzyManifold(final String name_, final Object[][] namesValues) {
		this.name = name_;
		categories = FuzzyNumber(namesValues);
	}
 
	/** initializing Constructor
	 * creates an ordered Set of 
	 * @param leftCenter
	 * @param rightCenter
	 * @param overlap
	 * @param numItems
	 */
	public FuzzyManifold(final String name_, final float leftCenter, final float rightCenter, final float overlap, final String[] names) {
		this.name = name_;
		this.categories = new FuzzyNumber[names.length];
		float dist = (rightCenter-leftCenter)/names.length; 
		float width = dist*(1+overlap)*.5f; //
		float currPos = rightCenter;
		for (int i = names.length; --i >= 0;) {
			categories[i] = new FuzzyNumber(currPos-width, currPos, currPos+width, names[i]);
			currPos-=dist;
		}
	}

	/** initializing Constructor
	 * 
	 * @param leftCenter
	 * @param rightCenter
	 * @param overlap
	 * @param numItems
	 */
	public FuzzyManifold(final String name_, final FuzzyNumber[] categories_) { 
		this.name = name_;
		this.categories = categories_; 
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/**Computes the membership of value in every category, allocating a fresh result array.
	 * @return an Array with the Memberships for the given Value	*/
	public float[] fuzzify(final float value) {
		return fuzzify(null, value); }

	/**Computes the membership of value in every category, reusing ret when non-null.
	 * @return an Array with the Memberships for the given Value	*/
	public float[] fuzzify(float[] ret, final float value) {
		if (ret == null) {
			ret = new float[categories.length]; }
		for (int i = ret.length; --i >= 0; ) {
			ret[i] = categories[i].Map(value); }
		return ret; }

	/**Returns the given category's precomputed weight (mass).
	 * @return the absolute Weight of this Category to find a weighted Center	*/
	public float getWeight(final int category) {
		return categories[category].getWeight(); }

	/**Returns the given category's precomputed center of mass.
	 * @return the Center 'of Mass' for this Category	*/
	public float getCenter(final int category) {
		return categories[category].getCenter(); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Complete Processing and DeFuzzification of the given Rules for 3 Variables	 
	 * 
	 * @param l Membership Values for the 1st Variable
	 * @param r Membership Values for the 2nd Variable
	 * @param m Membership Values for the 3rd Variable
	 * @param rules (complete) Set of fuzzy Rules for the three Variables 
	 * @return the defuzzified (weighted) Value of this Manifold  
	 */
	public float deFuzzify(final float[] l, final float[] r, final float[] m, final byte[][][] rules) {
		final float[] ret = deFuzzify(null, FuzzyBoole.TRUE, l, r, m, rules); 
		return ret[1]/ret[0]; }
	
	/** Complete Processing and DeFuzzification of the given Rules for 3 Variables	 
	 * 
	 * @param sums Accumulators for the Weights and weighted Positions 
	 * @param limit Limiting Membership Value for this Branch of Evaluation, 
	 * 			usually from a 4th Variable  
	 * @param l Membership Values for the 1st Variable
	 * @param r Membership Values for the 2nd Variable
	 * @param m Membership Values for the 3rd Variable
	 * @param rules (complete) Set of fuzzy Rules for the three Variables 
	 * @return the Center and Weight (both weighted with the limit) of this Manifold. 
	 * The Quotient is the Center of Mass 
	 */
	protected float[] deFuzzify(float[] sums, final double limit, final float[] l, final float[] r, final float[] m, final byte[][][] rules) {
		if (limit == 0) { 
			return sums; }
		if (sums == null) { 
			sums = new float[2]; }
		for (int i = l.length; --i >= 0; ) {
			if (l[i] != 0) {
				deFuzzify(sums, Math.min(l[i], limit), r, m, rules[i]); }
		}
		return sums; }
	
	/** Complete Processing and DeFuzzification of the given Rules for 2 Variables	
	 * 
	 * @param l Membership Values for the 1st Variable
	 * @param r Membership Values for the 2nd Variable
	 * @param rules (complete) Set of fuzzy Rules for the three Variables 
	 * @return the defuzzified (weighted) Value of this Manifold  
	 */
	public float deFuzzify(final float[] l, final float[] r, final byte[][] rules) {
		final float[] ret = deFuzzify(null, FuzzyBoole.TRUE, l, r, rules); 
		return ret[1]/ret[0]; }
	
	/** Complete Processing and DeFuzzification of the given Rules for 2 Variables	 
	 * 
	 * @param sums Accumulators for the Weights and weighted Positions 
	 * @param limit Limiting Membership Value for this Branch of Evaluation, 
	 * 			usually from a 4th Variable  
	 * @param l Membership Values for the 1st Variable
	 * @param r Membership Values for the 2nd Variable
	 * @param rules (complete) Set of fuzzy Rules for the three Variables 
	 * @return the Center and Weight (both weighted with the limit) of this Manifold. 
	 * The Quotient is the Center of Mass 
	 */
	protected float[] deFuzzify(float[] sums, final double limit, final float[] l, final float[] r, final byte[][] rules) {
		if (limit == 0) { 
			return sums; }
		if (sums == null) { 
			sums = new float[2]; }
		for (int i = l.length; --i >= 0; ) {
			if (l[i] != 0) {
				deFuzzify(sums, Math.min(l[i], limit), r, rules[i]);
			} 
		}
		return sums;
	}

	/** Complete Processing and DeFuzzification of the given Rules for 1 Variable	
	 * 
	 * @param r Membership Values for the 2nd Variable
	 * @param rules (complete) Set of fuzzy Rules for the three Variables 
	 * @return the defuzzified (weighted) Value of this Manifold  
	 */
	public float deFuzzify(final float[] r, final byte[] rules) {
		final float[] ret = deFuzzify(null, FuzzyBoole.TRUE, r, rules); 
		return ret[1]/ret[0]; }

	/** Complete Processing and DeFuzzification of the given Rules for 1 Variable	
	 * 
	 * @param sums Accumulators for the Weights and weighted Positions 
	 * @param limit Limiting Membership Value for this Branch of Evaluation, 
	 * 			usually from a 4th Variable  
	 * @param r Membership Values for the 2nd Variable
	 * @param rules (complete) Set of fuzzy Rules for the three Variables 
	 * @return the Center and Weight (both weighted with the limit) of this Manifold. 
	 * The Quotient is the Center of Mass 
	 */
	protected float[] deFuzzify(float[] sums,
	final double limit,
	final float[] r,
	final byte[] rules) {
		if (limit == 0) {
			return sums; }
		if (sums == null) {
			sums = new float[2]; }
		for (int j = r.length; --j >= 0; ) { //TODO: optimization possible by ordering
			final int category = (rules == null ? j : rules[j]); 
			if (r[j] != 0) {
				categories[category].deFuzzify(sums, Math.min(r[j], limit)); } 
		} //
		return sums; 
	}

	/** de-Fuzzifies for the given Category and adds the Weights to the Sum	
	 * 
	 * @param category specific FuzzyNumber to evaluate using the given Limit
	 * @param limit the Limit to consider when evaluating the given Category. 
	 * @return the defuzzified (weighted) Value of this Category in this Manifold  
	 */ 
	public float deFuzzify(final double limit, final int category) {
		final float[] ret = categories[category].deFuzzify(null, limit); 
		return ret[1]/ret[0]; }

	/**Returns a "dimensionName=categoryName" description of the given category.
	 * @param category the Category to describe
	 * @return a visual Description of this Category inclusive Dimension
	 */
	public String getDescription(final int category) {
		return this.name+"="+this.categories[category].name;
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** Data for the Test Manifold */ 
	private static final Object[][] TEST_DISTANCE = {
		{"near" , new float[]{150, 150}}, 
		{"close", new float[]{500, 250}}, 
		{"far"  , new float[]{750, 250}}
	};
	
	/** Data for the Test Manifold */ 
	private static final Object[][] TEST_POWER = {
		{"weak"     , new float[]{1.5f, 1.5f}}, 
		{"normal"   , new float[]{5, 3}}, 
		{"energized", new float[]{8, 2}}
	};
	
	/** Data for the Test Manifold */ 
	private static final Object[][] TEST_OUTPUT = {
		{"off" , new float[]{1, 1}}, 
		{"half", new float[]{4.5f, 3.5f}}, 
		{"full", new float[]{8, 2}}
	};
	
	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) throws Exception {
		L.n("Testing " + FuzzyManifold.class.getName());
		final FuzzyManifold distances = new FuzzyManifold("Distance", TEST_DISTANCE); 
		final FuzzyManifold powers = new FuzzyManifold("Power", TEST_POWER); 
		final FuzzyManifold outputs = new FuzzyManifold("Output", TEST_OUTPUT); 
		
		L.n().l(distances.deFuzzify(1, 0)); //calculating the Input from the Characterization is a possibly overdetermined System, since the Values are (nearly) unique! 
		L.n().l(distances.deFuzzify(1, 1)); //calculating the Input from the Characterization is a possibly overdetermined System, since the Values are (nearly) unique! 
		L.n().l(distances.deFuzzify(1, 2)); //calculating the Input from the Characterization is a possibly overdetermined System, since the Values are (nearly) unique! 

		final byte[][] rules 
		= { {1, 1, 2}, //
			{0, 1, 1}, 
			{0, 2, 2} 
		}; 
		System.out.println();
		printRules(distances, powers, rules, outputs);
		
		testAllRules(distances, powers, outputs, rules);
		
		//now we can actually use it...
		L.n("Output generated: "+outputs.deFuzzify(
			distances.fuzzify(275), 
			powers.fuzzify(6.5f), rules));
		
		//Input thats beyound the outer Categories still triggers full Category Response! 
		L.n("Output generated: "+outputs.deFuzzify(
			distances.fuzzify(5), 
			powers.fuzzify(0.5f), rules));

	}

	/** exhaustively tests that the Rules work accurately 
	 * (at least for the specified Center Values) 
	 * 
	 * @param distances
	 * @param powers
	 * @param outputs
	 * @param rules
	 */
	public static void testAllRules (
		final FuzzyManifold m1,
		final FuzzyManifold m2,
		final FuzzyManifold outputs,
		final byte[][] rules) {
		for (int i = rules.length; --i >= 0; ) {
			final byte[] rule = rules[i];
			final float val1 = m1.getCenter(i);
			final float[] vals1 = m1.fuzzify(val1); 
			testAllRules(m2, outputs, rules, rule, vals1);
			L.n();
		}
	}
	
	/** exhaustively tests that the Rules work accurately 
	 * (at least for the specified Center Values) 
	 * 
	 * @param powers
	 * @param output
	 * @param rules
	 * @param rule
	 * @param vals1
	 */
	private static void testAllRules(
		final FuzzyManifold m2,
		final FuzzyManifold output,
		final byte[][] rules,
		final byte[] rule,
		final float[] vals1) {
		for (int j = rule.length; --j >= 0; ) {
			final byte result = rule[j]; 
			final float val2 = m2.getCenter(j);
			final float[] vals2 = m2.fuzzify(val2); 
			final float val3 = output.deFuzzify(vals1, vals2, rules); 
			Assert.EQUALS(output.getCenter(result), val3);
			L.n().l(output.getCenter(result)).l(val3); 
		}
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
		testIt(args); }
	
}