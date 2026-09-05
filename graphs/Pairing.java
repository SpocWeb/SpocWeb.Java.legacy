/*
 * File Name: Pairing.java
 * Created on: 22.06.2003
 *
 */
package graphs;

import java.util.Arrays;

import math.matrix.MatrixInt;
import math.vector.VectorInt;
import streamIO.Assert;
import streamIO.Log;
import streamIO.copy.monoid.integer.Permutation;

/**
 * Title: Pairing<p>
 * Description:
 * Contains Methods to generate a maximum ranked Pairing for (bipartite) Graphs. 
 * These are directed Graphs where the Origin is only from one Subset 
 * and the Destination from another Subset. 
 * Other Types of Pairing include 
 * ranked Pairing which can be calculated using maximum Flows in MatrixGraph and 
 * weighed Pairing which ist hard to calculate.
 * Ranked pairing is less complex than weighed; 
 * the latter is uncertain any way, since People often use different Scales.  
 * @see graphs.MatrixGraph To generate the maximum unranked Pairing (or n-tupel-Set)    
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
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 477d33ec8a4e4b2fc90cda4766e0af11ad3a1fa7b430eb9df146ff4eddb2be3a
 * stale: false
 * tags: [code/graph_matching]
 * concepts: [Graph Pairing/Matching Quality]
 * facets: {layer: domain, status: legacy, complexity: medium}
 * -->
 */
public class Pairing {
	
	/** Logger for this Class */
	private static Log L = new Log(1);
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** originPrefs Preference List of the Origins 	 */
	final int[][] originPrefs;
	
	/** targetRanks Ranking List of the Targets, Inverse Permutations of @see #targetPrefs 	 */
	int[][] originRanks;
	
	/**
	 * calculates the Target Ranks, if not given. 
	 * @return the Target Ranks
	 */
	public int[][] getOriginRanks() {
		if (originRanks == null) //calculate the inverse Permutations
			originRanks = MatrixInt.INVERSE(originPrefs);
		return originRanks;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** targetPrefs Preference List of the Targets, Inverse Permutations of @see #targetRanks 	 */
	final int[][] targetPrefs;
	
	/** targetRanks Ranking List of the Targets, Inverse Permutations of @see #targetPrefs 	 */
	int[][] targetRanks;
	
	/**
	 * calculates the Target Ranks, if not given. 
	 * @return the Target Ranks
	 */
	public int[][] getTargetRanks() {
		if (targetRanks == null) //calculate the inverse Permutations
			targetRanks = MatrixInt.INVERSE(targetPrefs);
		return targetRanks;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** 
	 * Initializes the List of Preferences
	 * @param originPrefs_
	 * @param targetPrefs_
	 */
	public Pairing(final int[][] originPrefs_, final int[][] targetPrefs_) {
		//		this.originPrefs = originPrefs_;
		//		this.targetPrefs = targetPrefs_;
		/*		//add the Sentinel Elements in the first Row and the last Column
				this.originPrefs = new int[originPrefs_.length+1][targetPrefs_.length+1];
				this.targetPrefs = new int[targetPrefs_.length+1][originPrefs_.length+1];
				for (int i = originPrefs_.length; --i >= 0;) {
					VectorInt.add(originPrefs[i+1], originPrefs_[i], 1);
				}
				for (int i = targetPrefs_.length; --i >= 0;) {
					VectorInt.add(targetPrefs[i+1], targetPrefs_[i], 1);
				}
		*/ //add the Sentinel Elements in the last Row and the last Column
		this.originPrefs =
			new int[originPrefs_.length + 1][targetPrefs_.length + 1];
		this.targetPrefs =
			new int[targetPrefs_.length + 1][originPrefs_.length + 1];
		for (int i = originPrefs_.length; --i >= 0;) {
			VectorInt.COPY_AT(originPrefs[i], originPrefs_[i]);
			originPrefs[i][targetPrefs_.length] = targetPrefs_.length;
		}
		for (int i = targetPrefs_.length; --i >= 0;) {
			VectorInt.COPY_AT(targetPrefs[i], targetPrefs_[i]);
			targetPrefs[i][originPrefs_.length] = originPrefs_.length;
		}
	}

		/**
	 * Returns the overall Quality of the given Pairing, combining both the origins' and the targets' Rank preferences.
	 * @param pairing The List of Origins as chosen by the Targets.
	 * @return the Quality of the given Pairing
	 * mesured in Rank, meaning
	 * best  Quality = 0       <p/>
	 * worst Quality = n*n(-1) <p/>
	 */
	public int quality(final int[] pairing) {
		return
		originQuality(pairing) +
		targetQuality(pairing);
	}

	/**
	 * Returns the Quality of the given Pairing as seen from the origins' Rank preferences (how well each Target was ranked by its assigned Origin).
	 * @param pairing The List of Origins as chosen by the Targets.
	 * @return the Quality of the given Pairing
	 * mesured in Rank, meaning
	 * best  Quality = 0       <p/>
	 * worst Quality = n*n(-1) <p/>
	 */
	public int originQuality(final int[] pairing) {
		getOriginRanks();
		int ret = 0;
		for (int target = pairing.length; --target >= 0;)
			ret += originRanks[pairing[target]][target];
		return ret;
	}

	/**
	 * Returns the Quality of the given Pairing as seen from the targets' Rank preferences (how well each Target ranked its assigned Origin).
	 * @param pairing The List of Origins as chosen by the Targets.
	 * @return the Quality of the given Pairing
	 * mesured in Rank, meaning
	 * best  Quality = 0       <p/>
	 * worst Quality = n*n(-1) <p/>
	 */
	public int targetQuality(final int[] pairing) {
		getTargetRanks();
		int ret = 0;
		for (int target = pairing.length; --target >= 0;)
			ret += targetRanks[target][pairing[target]];
		return ret;
	}

	/**
	 * calculates one 'stable' Solution, 
	 * (but not necessarily the one with the best quality!, )
	 * where there are no two Pairs 
	 * in which two would commit 'adultery' 
	 * just because each of these would prefer 
	 * the other Gender of the other Pair. 
	 * 
	 * Mind: there usually are many stable Solutions!!!
	 * Try swapping Origin and Target, 
	 * because the Origin usually gets easier what it wants! 
	 * To find the optimum Pairing, 
	 * try all Permutations / Variations 
	 * and select the one with the best Quality. 
	 * @return the Origin for each Target; i.e. origin = calcPairing()[target] 
	 */
	public int[] calcPairing() {
		/** List of the current Position for the Origins 	 */
		int[] next = new int[originPrefs.length];
		getTargetRanks();
		int[] fiancee = new int[targetRanks.length];
		final int sentinel = originPrefs.length - 1;
		Arrays.fill(fiancee, sentinel); //set the Sentinels
		for (int currOrigin = originPrefs.length - 1; --currOrigin >= 0;) {
			int target; //getestete Dame
			int origin = currOrigin; //anfragender Herr
			do {
				target = originPrefs[origin][next[origin]];
				int[] rank = targetRanks[target];
				if (rank[origin] < rank[fiancee[target]]) {
					int swap = fiancee[target];
					fiancee[target] = origin;
					origin = swap;
				}
			} while (
				(origin != sentinel) && (++next[origin] < targetPrefs.length));
		}
		return fiancee;
	}

	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**Tests all Methods of this Class	 */
	public static void testIt() {
		L.n(" Testing ", 1).l(Pairing.class.getName(), 1);
		testPairingAymmetric();
		testPairingSymmetric();
		testPairingIdeal();
	}

	/** ideal Preference List for both Partners 	 */
	private static int[][] idealPrefs 
	= {  {  0, 1, 2, 3, 4, 5 }, {
			1, 2, 3, 4, 5, 0 }, {
			2, 3, 4, 5, 0, 1 }, {
			3, 4, 5, 0, 1, 2 }, {
			4, 5, 0, 1, 2, 3 }, {
			5, 0, 1, 2, 3, 4 }
	};

	/**Tests all Methods of this Class	 */
	public static void testPairingIdeal() {
		L.n(" Testing ideal Pairing", 1);
		Permutation[] perms = Permutation.Permutations(5);
		Pairing idealWahl = new Pairing(idealPrefs, idealPrefs);
		for (int i = perms.length; --i >= 0;) {
			Permutation perm = perms[i];
			L.n(perm).l(" Quality=").l(idealWahl.quality(perm.a));
		}
		Assert.EQUALS(0, idealWahl.quality(new int[] { 0, 1, 2, 3, 4, 5 }));
	};

	/** Preference List of the Gentlemen without Sentinel	 */
	private static int[][] herrenPrefs1 
	= {  {  1, 4, 0, 2, 3 }, {
			0, 1, 2, 3, 4 }, {
			1, 2, 4, 3, 0 }, {
			0, 2, 1, 3, 4
			//}, { 4, 2, 1, 0, 3 
		}
	};

	/** Preference List of the Ladies without Sentinel	 */
	private static int[][] damenPrefs1 
	= {  {  0, 3, 1, 2 }, {
			3, 1, 0, 2 }, {
			0, 3, 1, 2 }, {
			2, 1, 3, 0 }, {
			3, 1, 2, 0 }
	};

	/**Tests all Methods of this Class	 */
	public static void testPairingAymmetric() {
		L.n(" Testing asymmetric Pairing (one male less than females)", 1);
		Pairing herrenWahl1 = new Pairing(herrenPrefs1, damenPrefs1);
		int[] herrenResult1 = herrenWahl1.calcPairing();
		L.n("Herrenwahl: ").l(herrenResult1).l("Quality:").l(
			herrenWahl1.quality(herrenResult1));
		Assert.EQUALS(new int[] {3,1,2,4,0,4}, herrenResult1);
		Pairing damenWahl1 = new Pairing(damenPrefs, herrenPrefs);
		int[] damenResult1 = damenWahl1.calcPairing();
		L.n("Damenwahl: ").l(damenResult1).l("Quality:").l(
			damenWahl1.quality(damenResult1));
		Assert.EQUALS(new int[] {2,4,3,1,0,5}, damenResult1);
	}
	
	/** Preference List of the Gentlemen without Sentinel	 */
	private static int[][] herrenPrefs 
	= {  {  1, 4, 0, 2, 3 }, {
			0, 1, 2, 3, 4 }, {
			1, 2, 4, 3, 0 }, {
			0, 2, 1, 3, 4 }, {
			4, 2, 1, 0, 3 }
	};

	/** Preference List of the Ladies without Sentinel	 */
	private static int[][] damenPrefs 
	= {  {  4, 0, 3, 1, 2 }, {
			3, 4, 1, 0, 2 }, {
			0, 3, 1, 2, 4 }, {
			2, 1, 3, 0, 4 }, {
			3, 1, 2, 4, 0 }
	};

	/**Tests all Methods of this Class	 */
	public static void testPairingSymmetric() {
		L.n(" Testing symmetric Pairing (equal # of males and females)", 1);
		Pairing herrenWahl = new Pairing(herrenPrefs, damenPrefs);
		int[] herrenResult = herrenWahl.calcPairing();
		L.n("Herrenwahl: ").l(herrenResult).l("Quality:").l(
			herrenWahl.quality(herrenResult));
		Assert.EQUALS(new int[] {0,4,3,1,2,5}, herrenResult);
		Pairing damenWahl = new Pairing(damenPrefs, herrenPrefs);
		int[] damenResult = damenWahl.calcPairing();
		L.n("Damenwahl: ").l(damenResult).l("Quality:").l(
			damenWahl.quality(damenResult));
		Assert.EQUALS(new int[] {2,4,3,1,0,5}, damenResult);
		Permutation[] perms = Permutation.Permutations(5);
		for (int i = perms.length; --i >= 0;) {
			Permutation perm = perms[i];
			L.n(perm).l(" Quality=").l(herrenWahl.quality(perm.a));
		}
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) {
		testIt();
	}

}
