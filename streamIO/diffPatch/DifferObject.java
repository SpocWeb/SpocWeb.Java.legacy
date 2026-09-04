/*
 * Created on 02.02.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.diffPatch;

import java.util.Arrays;

import streamIO.Assert;
import streamIO.Log;
import streamIO.copy.boole.VectorBoolean;
import synch.ValidationRule;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Creates the Difference of two Streams. 
 * It does this by identifying one of the Longest Common Sequences (LCSs). 
 * This Algorithm can actually find ALL LCSs; all LCSs have the same Length. 
 * One Stream needs to be IResetAble or is cached in this Instance. 
 * 
 * 'Similarity' can be defined in several different ways: 
 * -The discrete Topology only checks for Equality in O(min(N,M)) 
 * -SubSet doesn't care for the Elements' Sequence and uses O(N+M)
 * -strict SubSequence checks whether one Sequence is a SubSequence 
 * and determines it's Position in the other Stream. 
 * An Automaton can be constructed for efficiently searching the SubSequence. 
 * -loose SubSequence checks whether the Items of one Sequence 
 * appear in ascending order in the other Sequence. 
 * -Longest common SubSequences take this even further 
 * by searching for common SubSequences of both Sequences. 
 * 
 * History: 
 * -diff is a Unix Shell Command which produces a Delta Output
 * -patch is the Inverse Operation which reconstructs the new File from the old and the diff. 
 * -RCS (Revision Control System) keeps track of the Sequence of diffs applied to the Original, 
 * 	but allows only exclusive Locks on each File and has no Directory Concept.   
 * CVS (Concurrent Versions System, 1989) allows concurrent Work on the same File
 * 	and received a Network Interface to remotely update and commit Code. 
 * 
 * Design Decisions / Implementation Details:
 * The List of Differences is only half-ordered: 
 * Deletions and Insertions are each ordered individually but not interleaved.   
 * The Operations are sorted in the way they are applied to the String 
 * when constructed from 0. 
 * Alternative working Implementations: 
 * -storing Insertions and Deletions in individual Arrays 
 * -storing Insertions and Deletions together and the Values in another Array
 * -this Implementation stores all three in one Array of DiffInt[] 
 *  so the Record Integrity and Identity of each Operation is fulfilled. 
 *  
 * @see streamIO.diffPatch.DifferInt which diffs Streams of Integer Values. 
 * 
 * A Difference in the Set-based Aspect of Streams is implemented in 
 * @see streamIO.object.DIFF which returns only the Difference Set, 
 * ignoring the Sequence of Items 
 * (an essential Stream Property which could as well be accidental).    
 * @see stringOp.EditMetric wich uses a most similar Algorithm 
 * to calculcate the (closest) Distance between two Streams. 
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
public class DifferObject {
	
	/** Logger for this Class	 */
	private static final Log L = new Log(DifferObject.class);
	
	/** 
	 * Static Convenience Method for a single One-Shot Difference. 
	 * Unfortunately the Sequence of Operands is reversed in this Class: 
	 * The Subtrahent is given to the Constructor and the Minuent is passed to the Method. 
	 * @param diff the newer Version 
	 * @param curr the older Version (current) 
	 * @return a DiffSetObject describing all the Changes
	 */
	final static public DiffSetObject DIFF(final Object[] diff, final Object[] curr) {
		return new DifferObject(curr).getDiffs(diff); } 
	
	/** 
	 * Static Convenience Method for a single One-Shot Difference. 
	 * Unfortunately the Sequence of Operands is reversed in this Class: 
	 * The Subtrahent is given to the Constructor and the Minuent is passed to the Method. 
	 * @param diff the newer Version 
	 * @param curr the older Version (current) 
	 * @return a DiffSetObject describing all the Changes
	 */
	final static public DiffSetObject DIFF(final Object[] diff, final Object[] curr, 
			final DiffSet parent) throws VersionException {
		return new DifferObject(curr).getDiffs(diff, parent); } 
	
	/** 
	 * Static Convenience Method for a single One-Shot Difference. 
	 * Unfortunately the Sequence of Operands is reversed in this Class: 
	 * The Subtrahent is given to the Constructor and the Minuent is passed to the Method. 
	 * @param diff the newer Version 
	 * @param curr the older Version (current) 
	 * @return a DiffSetObject describing all the Changes
	 */
	final static public DiffSetObject DIFF(final Object[] diff, final Object[] curr, 
			final DiffSet parent, final String branch) throws VersionException {
		return new DifferObject(curr).getDiffs(diff, parent, branch); } 
	
	/** 
	 * Static Convenience Method for a single One-Shot Difference. 
	 * Unfortunately the Sequence of Operands is reversed in this Class: 
	 * The Subtrahent is given to the Constructor and the Minuent is passed to the Method. 
	 * @param diff the newer Version 
	 * @param curr the older Version (current) 
	 * @return a DiffSetObject describing all the Changes
	 */
	final static public DiffSetObject DIFF(final Object[] diff, final Object[] curr, 
			final DiffSet parent, final String branch, final boolean samePosX
			) throws VersionException {
		return new DifferObject(curr).getDiffs(diff, parent, branch, samePosX); } 
	
	/** 
	 * Static Convenience Method for a single One-Shot Difference. 
	 * Unfortunately the Sequence of Operands is reversed in this Class: 
	 * The Subtrahent is given to the Constructor and the Minuent is passed to the Method. 
	 * @param diff the newer Version 
	 * @param curr the older Version (current) 
	 * @return a DiffSetObject describing all the Changes
	 */
	final static public DiffSetObject DIFF(final Object[] diff, final Object[] curr, 
			final DiffSet parent, final String branch, final boolean samePosX, final boolean samePosY
			) throws VersionException {
		return new DifferObject(curr).getDiffs(diff, parent, branch, samePosX, samePosY); } 
	
	////////////////////////////////////////////////////////////////////////////
	/// Member Variables & Constructor
	////////////////////////////////////////////////////////////////////////////
	
	/** List of Values to compare with 	*/
	protected final Object[] vals; 
	
	/** Count of Values already compared with 	*/
	protected int itemCount; 
	
	/** Array of Counts to reconstruct the LCS, dynamically enlarged. 
	 * A Matrix of Type 'short' allows to merge Lists with a Size of 32767 Items
	 * using up to 1 GB of RAM. 
	 * Unfortunately this Table is not sparse, 
	 * so it cannot be easily stored sparsely. 
	 * But actually you need only the last Row 
	 * to determine the largest Matching 
	 * and only 2 (or rather 1.58) Bits instead of 16 per Field 
	 * to determine the Direction: up, left or both. 
	 * This would allow to match 128k Items with 2 GB of RAM. 
	 */
	protected final int[] c;
	
	/** Initialized late, only on calling compare()
	 * @see #compare(Object[]) initializes this Object. 
	 */
	protected VectorBoolean[] xUp;  
	
	/** Initialized late, only on calling compare()
	 * @see #compare(Object[]) initializes this Object. 
	 */
	protected VectorBoolean[] yUp;  
	
	/**
	 * 
	 * @param list
	 */
	public DifferObject(final Object[] _list) {
		L.debug(_list); 
		vals = _list; 
		c = new int[vals.length+1]; 
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// Resetting / Initialization 
	////////////////////////////////////////////////////////////////////////////
	
	/** resets the current State, so that a new Comparison 
	 * with the same Vector can take place.  
	 */
	public void reSet() { 
		itemCount = 0; 
		Arrays.fill(c, 0); 
		if (xUp != null)
			for (int i = xUp.length; --i >= 0;) {
				xUp[i].FalseAt(); 
				yUp[i].FalseAt(); 
			}
	}
	
	/** initializes for tracking the actual Diff Positions.	 */
	public void initTracking() {
		if (xUp != null) 
			return; 
		xUp = new VectorBoolean[vals.length+1]; 
		yUp = new VectorBoolean[vals.length+1]; 
		for (int i = xUp.length; --i >= 0;) {
			xUp[i] = new VectorBoolean(xUp.length*3/2); //typically the Strings to compare have about the same Length! 
			yUp[i] = new VectorBoolean(xUp.length*3/2); //typically the Strings to compare have about the same Length! 
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Comparison
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * This Algorithm is most similar to the Calculation of the fuzzy Word Distance 
	 * the so-called @see stringOp.EditMetric 
	 * @param y the List of Objects to compare
	 * @param startStop
	 * @return the Length of the LCS
	 */
	final public int compare(final Object[] y) {
		reSet(); 
		initTracking(); 
		int ret = 0; 
		for(int i = -1; ++i < y.length;) 
			ret = addVal(y[i]);
		return ret; 
	}
	
	/**
	 * @param yi  y[i] the next Value to compare
	 * @return the (current) Length of the LCS
	 */
	public int addVal(final Object yi) {
		final int[] ci1, ci = ci1 = c; 
		int ci1j1 = 0; 
		for(int j = 0; j < vals.length;) {
			final Object xj = vals[j++]; 
			final int cij = ci[j]; 
			//Assert.EQUALS(ci1[j-1], ci1j1); 
			if (ValidationRule.EQUALS(yi, xj))  {
				ci[j] = ci1j1 + 1; 
			} else {
				if (ci1[j]>= ci [j-1]) { //cij >= ci[j-1]) { //(
					ci [j] = ci1[j]; //cij; // 
					if (yUp != null)
						yUp[j].set(itemCount); 
				} else {
					ci [j] = ci [j-1];  
					if (xUp != null)
						xUp[j].set(itemCount); 
				}
			}
			ci1j1 = cij; 
		}
		++itemCount; 
		return ci[vals.length]; 
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// Diffs Calculation
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * generates the Lists of Differences and common Values 
	 * between the current and the given Stream.   
	 * @param delArg
	 * @param common
	 * @param delThis
	 * @return the Number of Deletions in the Differences, to be able to determine the Result Lengh
	 */
	final public DiffSetObject getDiffs(final Object[] values) {
		try { return getDiffs(values, null); 
		} catch (final VersionException x) {
			throw new RuntimeException("Should never happen!", x); 
		}
	}
	
	/**
	 * generates the Lists of Differences and common Values 
	 * between the current and the given Stream.   
	 * @param delArg
	 * @param common
	 * @param delThis
	 * @return the Number of Deletions in the Differences, to be able to determine the Result Lengh
	 * @throws VersionException when you add a Child in the middle of a Branch
	 */
	final public DiffSetObject getDiffs(final Object[] values, final DiffSet parent
			) throws VersionException {
		return getDiffs(values, parent, null); }
	
	/**
	 * generates the Lists of Differences and common Values 
	 * between the current and the given Stream.   
	 * @param delArg
	 * @param common
	 * @param delThis
	 * @return the Number of Deletions in the Differences, to be able to determine the Result Lengh
	 * @throws VersionException when you add a Child in the middle of a Branch
	 */
	final public DiffSetObject getDiffs(final Object[] values, final DiffSet parent, final String branch
			) throws VersionException {
		return getDiffs(values, parent, branch, false); }
	
	/**
	 * generates the Lists of Differences and common Values 
	 * between the current and the given Stream.   
	 * @param delArg
	 * @param common
	 * @param delThis
	 * @return the Number of Deletions in the Differences, to be able to determine the Result Lengh
	 * @throws VersionException when you add a Child in the middle of a Branch
	 */
	final public DiffSetObject getDiffs(final Object[] values, final DiffSet parent, final String branch
			, final boolean samePosX) throws VersionException {
		return getDiffs(values, parent, branch, samePosX, false); }
	
	/**
	 * generates the Lists of Differences and common Values 
	 * between the current and the given Stream.   
	 * 
	 * @param values the List of Objects to compare 
	 * @param parent the optional Parent for the new DiffSet 
	 * @param branch the optional Branch Name for the new DiffSet
	 * @param samePosX Flag whether to collect the Indices of the same Items
	 * @param samePosY Flag whether to collect the values' Indices of the same Items
	 * @return the Number of Deletions in the Differences, to be able to determine the Result Lengh
	 * @throws VersionException when you create a direct Child in the middle of a Branch
	 */
	final public DiffSetObject getDiffs(final Object[] values, final DiffSet parent, final String branch
			, final boolean samePosX, final boolean samePosY) throws VersionException {
		compare(values); 
		final int numSame = c[vals.length]; //largest Value is always last! 
		final DiffObject[] diffVals = new DiffObject[(vals.length-numSame)+(values.length-numSame)];
		final int[][] same = new int[2][]; 
		if (samePosX)
			same[0] = new int[numSame]; 
		if (samePosY)
			same[1] = new int[numSame]; 
		final int numDeletions = getDiffs(diffVals, same, values, new int[2], values.length, vals.length);
		final DiffSetObject ret = new DiffSetObject(diffVals, same, numDeletions, parent, branch, values); 
		return ret; 
	}
	
	/** recursive inner Method to generate the Lists of Differences and common Values 
	 * Problem here: the Sequence of Deletions and Insertions is not ordered. 
	 * It is possible to have only Deletions and then Insertions or vice versa. 
	 * Only Deletions and Insertions individually are ordered. 
	 * @param diffVals Array to be filled with the Differences 
	 * @param same optional (nulls allowed) Array of Indices to be filled
	 * @param values the Values to fill the DiffObject Values 
	 * @param n ByRef Integer Indices for filling the 'diffVals' and 'same' Arrays.  
	 * @param i
	 * @param j
	 * @return the Number of Deletions in the Differences, to be able to determine the Result Lengh
	 */
	protected final int getDiffs(final DiffObject[] diffVals, final int[][] same, final Object[] values
			, final int[] n, final int i, final int j) {
		if (i <= 0) {
			for(int k= -1; ++k < j; ) //
				diffVals[n[0]++] = new DiffObject(vals[k], ~k); 
		    return j; 
		}
		if (j <= 0) {
			for(int k= -1; ++k < i; ) //
				diffVals[n[0]++] = new DiffObject(values[k], k); 
		    return 0; 
		}
		final int j_1 = j-1; 
		final int i_1 = i-1; 
		if (yUp[j].isSet(i_1)) { //ci[j] == c[i_1][j]) {  //
			final int ret = getDiffs(diffVals,same, values, n, i_1, j); 
			diffVals[n[0]++] = new DiffObject(values[i_1], i_1); //Addition 
			return ret;
		}
		if (xUp[j].isSet(i_1)) { //ci[j] == ci[j_1]) { //
			final int ret = getDiffs(diffVals, same, values, n, i, j_1); 
			diffVals[n[0]++] = new DiffObject(vals[j_1], ~j_1); //Deletion
			return ret+1;
		}
		//Assert.EQUALS(ci[j], c[i_1][j_1] + 1);  
		final int ret = getDiffs(diffVals, same, values, n, i_1, j_1); 
		if((same.length > 1) && (same[1] != null)) 
			same[1][n[1]] = j_1;
		if((same.length > 0) && (same[0] != null)) 
			same[0][n[1]] = i_1; 
		n[1]++;
		return ret; 
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// static Testing and Main Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** tests all Methods of this Class	 */
	final static public void testIt() throws VersionException {
		test1(); 
		testIt2();
	}
	
	/** tests all Methods of this Class	 */
	final static public void testIt2() {
		final Object[] x  = {"A", "B", "C", "D", "E"}; 
		final Object[] x1 = {"A", "B", "X", "D", "E"};
		final Object[] x2 = {"A", "B", "C", "Y", "E"}; //Mergeable with x1 
		final Object[] x3 = {"A", "B", "C", "Z", "E"}; //Conflict with x2
		final DifferObject  xDiffer = new DifferObject(x); 
		final DiffSetObject x1_x = xDiffer.getDiffs(x1); 
		//No Conflict
		//final DifferObject  xDiffer2 = new DifferObject(x); 
		final DiffSetObject x2_x = xDiffer.getDiffs(x2); 
		final DiffSet Conflicts1 = x1_x.applyDiffs(x2_x); //x1_x is modified internally here! 
		Assert.EQUALS(0, Conflicts1.getInt()); 
		final Object[] x4 = x1_x.merge(x2); 
		Assert.EQUALS(new Object[] {"A", "B", "X", "Y", "E"} , x4);
		//Conflict
		//final DifferObject  xDiffer3 = new DifferObject(x); 
		final DiffSetObject x3_x = xDiffer.getDiffs(x3); 
		final DiffSetObject Conflicts2 = (DiffSetObject) x3_x.applyDiffs(x2_x); 
		Assert.EQUALS(2, Conflicts2.getInt()); 
		final String xml = Conflicts2.toString(); 
		final Object[] x5 = x3_x.merge(x2); 
		Assert.EQUALS(new Object[] {"A", "B", "C", "Z", "E"} , x5);
		final Object[] x6 = Conflicts2.merge(x5); 
		Assert.EQUALS(new Object[] {"A", "B", "C", "Y", "E"} , x6);
	} 
	
	/** tests all Methods of this Class	 */
	final static public void test1() throws VersionException {
		final Object[] Y = {"A", "B", "C", "B", "D", "A", "B"}; 
		final Object[] X = {"B", "D", "C", "A", "B", "A"};
		testIt(Y, X); 
		testIt(X, Y);
		/*
		final DifferObject yDiff = new DifferObject(Y); 
		yDiff.compare(X); 
		final int[][] yDiffs = yDiff.getDiffs();
		final int[] delValX = VectorInt.GET_AT(X, yDiffs[3]); 
		
		final DifferObject xDiff = new DifferObject(X); 
		xDiff.compare(Y); 
		final int[][] xDiffs = xDiff.getDiffs();
		final int[] delValY = VectorInt.GET_AT(Y, xDiffs[3]); 
		
		final int[][] merged = mergeDiffs(yDiffs[0], yDiffs[3], delValX, xDiffs[0], xDiffs[3], delValY);
		final int[] result = mergeDiff(Y, merged[0], merged[1], merged[2]);
		Assert.EQUALS(Y, result);
		*/ 
	}
	
	/** tests all Methods of this Class	 */
	private static final void testIt(final Object[] x, final Object[] y) throws VersionException {
		final DifferObject xDiffer = new DifferObject(x); 
		Assert.EQUALS(x.length, xDiffer.compare(x)); 
		final DiffSetObject diff = xDiffer.getDiffs(y, null, null, true, true);
		Assert.EQUALS(4, diff.same[0].length); 
		Assert.EQUALS(4, diff.same[1].length); 
		final Object[] merged = diff.merge(x); 
		Assert.EQUALS(y, merged); 
		//VectorInt.CPL_AT(sameDiffVals[0]); 
		final Object[] mergedBack = diff.merge(y, true); //mergeDiff(y, sameDiffVals[0], sameDiffVals[1], sameDiffVals[0].length-numDel); 
		Assert.EQUALS(x, mergedBack); 
	}
	
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}
	
}
