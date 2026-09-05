/*
 * File Name: HunterDouble.java
 * Created on: 20.01.2004
 *
 */
package math.vector;

import streamIO.Assert;
import streamIO.IOrdered;
import streamIO.Log;
import streamIO.copy.monoid.integer.Permutation;
import streamIO.integer.random.RandomFast;
import streamIO.object.IStreamIn;
import function.byref.ByRefDouble;

/**
 * Stateful binary-search "hunter" over a sorted {@code double[]}, together with the static
 * QuickSort, permutation, ranking and order-statistic (median/percentile) algorithms shared
 * by the whole vector family for values requiring only an order relation.
 *
 * <p>Title: HunterDouble<p>
 * Description:
 * Purpose:
 * Implements a (stateful) Hunter to search through sorted Arrays.
 * Additionally collects all static Methods
 * related to Sorting, Searching, Permuting, Indexing and Ranking
 * (non-arithmetic, requires only an Order Relation)
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
 * mtime: 2026-09-05T12:50:31Z
 * digest: c65db4c674eeb0aad5b3a94f9799b939ffb48b69e43471fe1cf26ce7a65e433f
 * stale: false
 * -->
 */
public class HunterDouble {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(HunterDouble.class, 0);

	////////////////////////////////////////////////////////////////////////////////
	//	Permutation Methods for Array Sorting, Swapping and Scrambling, @see Maths.Vector
	////////////////////////////////////////////////////////////////////////////////

	/**Swaps the Columns of this Tensor in Place	
	 * 
	 * @param ret the Vector to swap the Data in. 
	 * @param dim1
	 * @param dim2
	 * @return the given Vector with Elements ret[dim1] and ret[dim2] swapped 	 
	 */
	final static public double[] SWAP_AT(final double[] ret, final int dim1, final int dim2) {
		if (dim1 == dim2) 
			return ret; 
		final double tmp = ret[dim1]; ret[dim1] = ret[dim2]; ret[dim2] = tmp;
		return ret;
	}

	/** Scrambles the Vector by randomly swapping all of it's Elements	 */
	final static public double[] SCRAMBLE_AT(final double[] ret) {
		double tmp;
		int j, i = ret.length;
		while (--i >= 0) { //Linear Distribution
			j = (int) (RandomFast.STREAM.nextLong() % ret.length);
			tmp = ret[i];
			ret[i] = ret[j];
			ret[j] = tmp;
		}
		return ret;
	}

	/**
	 * Don't use this in Vector Operations, because temporary Array is created. 
	 * @return this Vector with the Elements permuted according to the given Permutation     
	 */
	final static public double[] PERMUTE(final double[] a, final int[] index) {
		return PERMUTE(a, index, false, null); }

	/** Writes {@code a} reordered according to {@code index} into {@code ret}.
	 * @return this Vector with the Elements permuted according to the given Permutation     */
	final static public double[] PERMUTE(final double[] a, final int[] index, final double[] ret) {
		return PERMUTE(a, index, false, ret); }
	
	/**
	 * Don't use this in Vector Operations, because temporary Array is created. 
	 * @return this Vector with the Elements permuted according to the given Permutation     
	 */
	final static public double[] PERMUTE(final double[] a, final int[] index, final boolean reverse) {
		return PERMUTE(a, index, reverse, null);
	}

	/**
	 * Writes {@code a} reordered according to {@code index}, optionally reversed, into {@code ret}.
	 * @param a the Vector to permute
	 * @param index the Permutation to apply to Vector a
	 * @param reverse Flag whether to revert the Result (i.e. a[0]<->a[n], a[1] <-> a[n-1] etc.
	 * This should NOT be confused with the Inverse!
	 * @param ret optional (null allowed) Workspace; returned if large enough
	 * @return ret (or a new Vector) with the Elements of a permuted according to the given Permutation
	 */
	final static public double[] PERMUTE(final double[] a, final int[] index, final boolean reverse, double[] ret) {
		if (ret == null)
			ret =  new double[a.length];
		for (int i = index.length; --i >= 0;) {
			if (reverse)
				ret[index.length-1 - i] = a[index[i]];
			else
				ret[i] = a[index[i]];
		}
		System.arraycopy(a, index.length, ret, index.length, a.length-index.length); 
		return ret;
	}
	
	/** 
	 * Don't use this Method in Vector Operations, because temporary Array is created, 
	 * since performing a Permutation cannot be done in Place. 
	 * @return this Vector with the Elements permuted according to the given Permutation     
	 */
	final static public double[] PERMUTE_AT(final double[] a, final int[] index) {
		/*
		final double[] tmp = new double[a.length];
		PERMUTE(a, index, reverse, tmp);
		System.arraycopy(tmp, 0, a, 0, a.length);
		*/
		for(int i = index.length; --i >= 0;) { //need to go to the last...  
			int j = index[i];
			if (j <= 0) //...since testing for <= instead of < 0, due to -0 = 0
				continue; //so a Pair Swap of 0 with any Number is only fixed when encountering 0! 
			if (j == i) { index[i] = -j;	
				continue; } 
			int k = i; //index[k] = -j;
			final double tmp = a[i]; 
			while(j != i) {
				a[k] = a[j]; k = j; j = index[j]; 
				index[k] = -j; 
			} a[k] = tmp; index[i] = -index[i];
		}
		return a;
	}
	
	/// The following Code does not work, because in Place is not possible!
	/*		float tmp;	//Undo the Row Permutations!
			int j, k = a.length;
			while (--k > 0) { 	//first row is not modified, because L[1,1]=1
				if (perm[k] != k) {
					tmp = a[k]; a[k] = a[j = perm[k]]; a[j] = tmp; }
			}
			return a; }
	*/

	/////////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	//  QuickSort Algorithm for Array Ranking and Sorting, @see Maths.Vector
	//  Sorting makes sense only when Read Access happens at least once on every Item,
	//  otherwise use the O(log N) Statistic() Method.
	////////////////////////////////////////////////////////////////////////////////

	/**
	  * Ranks {@code arr}, allocating its own temporary and result arrays.
	  * @param arr The Array to be ranked
	  * @param tmp a temporary Array passed for Effectiveness (Reuse)
	  * @param ret the Array to be returned passed for Effectiveness (Reuse)
	  * @return the Ranking of the given Array.
	  */
	final static public int[] RANK(final double[] arr) {
		return HunterDouble.RANK(arr, new int[arr.length], new int[arr.length]); }

	/**
	  * Ranks {@code arr} by inverting its sort-index permutation.
	  * @param arr The Array to be ranked
	  * @param tmp a temporary Array passed for Effectiveness (Reuse)
	  * @param ret the Array to be returned passed for Effectiveness (Reuse)
	  * @return the Ranking of the given Array.
	  */
	final static public int[] RANK(final double[] arr, final int[] tmp, final int[] ret) {
		return VectorInt.INVERSE(INDEX(arr, tmp), ret); }

	/**
	  * Computes the sort-index permutation of {@code arr}, allocating its own index array.
	  * @param arr The Array to be ranked
	  * @param tmp a temporary Array passed for Effectiveness (Reuse)
	  * @param ret the Array to be returned passed for Effectiveness (Reuse)
	  * @return the Ranking of the given Array.
	  */
	final static public int[] INDEX(final double[] arr) {
		return HunterDouble.INDEX(arr, new int[arr.length]); }
	
	/**
	 * sorts three Elements of the given Array in Place.  
	 * Reverse Sort Order can be implemented by just swapping start and stop Index!
	 * 
	 * Implementation: 
	 * Optimized Implementation for all 6 possible Cases!  
	 * @param items  the Array to sort
	 * @param start  the Index to contain the smallest Value  
	 * @param middle the Index to contain the middle   Value 
	 * @param stop   the Index to contain the largest  Value 
	 */
	final static public void SORT_THREE(final double[] items, final int start, final int middle, final int stop) { 
		//float swap; 
		//primitive Implementation, requires up to 9 Assignments and 3 Comparisons. 
		/*
		if (items[start] > items[middle]) {
			swap = items[start]; items[start] = items[middle]; items[middle] = swap; }
		if (items[start] > items[stop]) {
			swap = items[start]; items[start] = items[stop]; items[stop] = swap; }
		if (items[middle] > items[stop]) {
			swap = items[middle]; items[middle] = items[stop]; items[stop] = swap; }
		*/
		//requires only 3.5 Assignments and 2.6 Comparisons
		if(items[start] <= items[middle]) {
			if(items[middle] <= items[stop])
				return; 
			//(items[middle] > items[stop])
			if(items[start ] > items[stop]) { //start is greater than both
				final double swap = items[stop]; items[stop] = items[middle]; items[middle] = items[start]; items[start] = swap;
				return; }
			final double swap = items[middle]; items[middle] = items[stop]; items[stop] = swap;
			return; 
		}
		//(items[start] > items[middle]) 
		if(items[start] > items[stop]) { //start is greater than both
			if(items[middle] > items[stop]) { //stop is smallest
				final double swap = items[stop]; items[stop] = items[start]; items[start] = swap; 
				return;	
			} //else middle is smallest
			final double swap = items[stop]; items[stop] = items[start]; items[start] = items[middle]; items[middle] = swap; 
			return;	
		} //else 
		//(items[stop  ] >= items[start] > items[middle]) 
		final double swap = items[middle]; items[middle] = items[start]; items[start] = swap;
	}
	
	/**
	 * Divide and Conquer Method for sorting an Array or finding its (n-th) Statistic:
	 * A Separator Element is determined and all other Elements ordered around it.
	 * a[p..r] -> a[p..q] <= a[q+1..r]
	 * The Elements of the Items Array are expected to be of Type OrderAble	 
	 * 
	 * @param items the Array to be searched for, partially sorted 
	 * @param stop  the  last Index to sort, typically items.length-1
	 * @param start the first Index to sort, typically 0
	 * @return the index of an inner Value 
	 * that partitions the Array into smaller Values below and larger Values above. 
	 */
	protected static final int PARTITION(final double[] items, final int stop//length
			, final int start) { //, boolean asc) {
		//final int stop = length-1; 
		if (stop <= start + 1){
			if (items[stop] < items[start]) {
				final double swap = items[stop]; items[stop ] = items[start]; items[start] = swap; }
			return start; 
		}
		final int middle = (start + stop) >> 1;
		SORT_THREE(items, start, middle, stop); 		
		int i = start;  //start, middle and stop are now sorted...
		int j = stop ;
		final double item = items[middle]; //swap the Pivot out and partition the Rest around it
		items[middle] = items[--j]; items[j] = item; //necessary as Sentinel to terminate loop! 
		for(;;) { //swap all Items around the selected one
			while (item < (items[--j])); //search for a greater Item
			while (item > (items[++i])); //search for a smaller Item
			if (i >= j) //finished: all Elements left of j ...
				break; //...are smaller than those right of j
			final double swap = items[j]; items[j] = items[i]; items[i] = swap;
		} //swap Item above Pivot with Item below Pivot.
		items[stop-1] = items[i]; items[i] = item; //swap Pivot Value with item[i]
		/*/testing the Partitioning/Heap Property: middle Item is
		for(int k = i;++k <= stop;) //...larger than all left and... 
			Assert.IS_TRUE(items[k] >= item);
		for(int k = i;--k >= start;) //...smaller than all right
			Assert.IS_TRUE(items[k] <= item);
		*/
		return i; 
	}
	
	/** 
	 * QuickSort Algorithm:
	 * Divide and Conquer Method to sort the Array descending:
	 * The Array is divided into two, of which both are recursively sorted. 
	 * Implementation: 
	 * The larger Recursion is replaced by a Stack (which limits it's Size to Lb(items.length))
	 * the smaller is done directly (End-Recursion) 
	 * 
	 * @param items	the Array to sort 
	 */
	final static public void SORT(final double[] items) {
		SORT(items, items.length, 0, null); }
	
	/** 
	 * QuickSort Algorithm:
	 * Divide and Conquer Method to sort the Array descending:
	 * The Array is divided into two, of which both are recursively sorted. 
	 * Implementation: 
	 * The larger Recursion is replaced by a Stack (which limits it's Size to Lb(items.length))
	 * the smaller is done directly (End-Recursion) 
	 * 
	 * @param items	the Array to sort 
	 * @param stop   last Index to sort+1 (typically items.length)
	 */
	final static public void SORT(final double[] items, int stop) {
		SORT(items, stop, 0, null); }
	
	/** 
	 * QuickSort Algorithm:
	 * Divide and Conquer Method to sort the Array descending:
	 * The Array is divided into two, of which both are recursively sorted. 
	 * Implementation: 
	 * The larger Recursion is replaced by a Stack (which limits it's Size to Lb(items.length))
	 * the smaller is done directly (End-Recursion) 
	 * 
	 * @param items	the Array to sort 
	 * @param stop   last Index to sort+1 (typically items.length)
	 * @param start first Index to sort   (typically 0)
	 */
	final static public void SORT(final double[] items, int stop, int start) {
		SORT(items, stop, start, null); }
	
	/** 
	 * QuickSort Algorithm:
	 * Divide and Conquer Method to sort the Array descending:
	 * The Array is divided into two, of which both are recursively sorted. 
	 * Implementation: 
	 * The larger Recursion is replaced by a Stack (which limits it's Size to Lb(items.length))
	 * the smaller is done directly (End-Recursion) 
	 * 
	 * @param items	the Array to sort 
	 * @param stop   last Index to sort+1 (typically items.length)
	 * @param start first Index to sort   (typically 0)
	 * @param stack optional Stack to use
	 */
	final static public void SORT(final double[] items, int stop, int start, int[] stack) {
		--stop; //to account for the Length which is always 1 langer! 
		if ((stack == null) || 
			(stack.length <  64))
			 stack = new int[64]; //use maximum Length right away...
		stack[0] = stop ; 
		stack[1] = start; 
		int SP = 1; 
		do {
			if (start >= stop) {
				start = stack[SP--]; 
				stop  = stack[SP--]; 
			} else {
				final int middle = PARTITION(items, stop, start);
				if ((stop-middle) < (middle - start)) { //stack the smaller Interval
					stack[++SP] = middle-1; stack[++SP] = start   ; start = middle+1; 
				} else {
					stack[++SP] = stop    ; stack[++SP] = middle+1; stop  = middle-1; 
				}
			}
		} while(SP > 0);
	}
	
	/**QuickSort Algorithm:
	 * Divide and Conquer Method:
	 * The Array is divided into two, of which both are again sorted.	 */
	private static final void SORT_RECURSIVE(final double[] items, final int start, final int stop) {
		if (start >= stop)
			return; // Items; //not effective to return since recursive!
		final int middle = PARTITION(items, stop, start);
		SORT_RECURSIVE(items, start     , middle-1);
		SORT_RECURSIVE(items, middle + 1, stop    );
	}

	/**Side Effect: partially sorts the List around the Median 
	 * and both first and last Quartile. 
	 * @return the Tri-Median of this Array ('middle' Value), 
	 * which is a weighted Mean betwen the Median(*0,5) 
	 * and the first and last Quartile(*0.25) 	 */
	final static public double GET_TRI_MEDIAN(final double[] items) {
		final int half  = 1+(items.length >> 1);
		final int quart = 1+(items.length >> 2);
		final double median = items[GET_STATISTIC_POS(items, half, 0, items.length-1)];
		final double quart1 = items[GET_STATISTIC_POS(items, quart, 0, half-1)]; //since the Items are ordered around the Median, it lies in the Middle!
		final double quart2 = items[GET_STATISTIC_POS(items, quart, half, items.length-1)];
		return (quart1+median+median+quart2)*0.25; 
	}
	
	/**Side Effect: partially sorts the List 
	 * @return the approximate Position of the Median of this Array ('middle' Value)	 */
	final static public int GET_MEDIAN_POS(final double[] items) {
		return GET_STATISTIC_POS(items, items.length >> 1, 0, items.length-1); }
	
	/**Side Effect: partially sorts the List 
	 * @return the approximate Median of this Array ('middle' Value)	 */
	final static public double GET_MEDIAN_FAST(final double[] items) {
		return items[GET_MEDIAN_POS(items)]; }
	
	/** 
	 * Side-Effect: partially sorts the Array around the Median
	 * (which ends up in the Middle of the Array)
	 * @param arr the Array to search in 
	 * @return the exact Median (considering the tie Case) 
	 */
	final static public double GET_MEDIAN(final double[] arr) {
		return GET_MEDIAN(arr, 0, arr.length-1); }
	
	/** 
	 * Side-Effect: partially sorts the Array around the Median
	 * (which ends up in the Middle of the Array)
	 * @param arr the Array to search in 
	 * @param start the first Index (inclusive) 
	 * @param stop the last Index (inclusive) 
	 * @return the exact Median (considering the tie Case) 
	 */
	final static public double GET_MEDIAN(final double[] arr, final int start, final int stop) {
		final int numItems = stop-start+1; 
		if ((numItems & 1) != 0) { //odd Case
			return arr[GET_STATISTIC_POS(arr, stop, (numItems+1)>>1, start)]; //Median
		} //even Case
			final int j=numItems >> 1;
			final double m2 = arr[GET_STATISTIC_POS(arr, stop, j+1, start)]; //TODO: can possibly be optimized... 
			final double m1 = arr[GET_STATISTIC_POS(arr, stop  , j, start)];
			return 0.5f*(m1+m2); //...due to the first partial Sorting
	}
	
	/**Creates the n-th Percentile Order Statistic for the whole Array
	 * using the QuickSort Algorithm.
	 * 100-th Order Statistic = Maximum
	 *  50-th Order Statistic = Median
	 *   0-th Order Statistic = Minimum and so on.
	 * This Algorithm is ideal for Tasks like ranking up to a certain Point,
	 * because it needs only O(log N) Steps to find the Statistic
	 *
	 * As a Side Effect the Array itself is partially sorted in that
	 * all Items with an Index smaller than p are smaller and
	 * all Items with an Index  larger than p are larger than the Value in p!
	 * So to find several ranked Items, start with the largest
	 * (or smallest, but then you have to subtract the previous Ranks)
	 * and search the Rest only within the first partial Array!
	 * Alternatively if you need the m largest Elements, 
	 * read the Data into a Heap in O(N log N) Steps 
	 * and then retrieve the first m Elements in O(m log N) Steps. 
	 * @return the Index of the Item with the given Rank
	 *  to retrieve the Value, simply use Items[Statistic(Items, p, r, i)]
	 */
	public static int GET_PERCENTILE_POS(final double[] items, final int percentile) {
		return GET_PERCENTILE_POS(items, percentile, 0, items.length - 1);
	}
	
	/**Creates the i-th Order Statistic in the Range between start and stop (inclusive)
	 * using the QuickSort Algorithm.
	 * n-th Order Statistic = Maximum
	 * n/2  Order Statistic = Median
	 * 0-th Order Statistic = Minimum and so on.
	 * This Algorithm is ideal for Tasks like ranking up to a certain Point,
	 * because it needs only O(log N) Steps to find the Statistic.
	 * @return the Index of the Item with the given Rank
	 *  to retrieve the Value, simply use Items[Statistic(Items, p, r, i)]
	 *
	 * As a Side Effect the Array itself is partially sorted in that
	 * all Items with an Index smaller than p are smaller and
	 * all Items with an Index  larger than p are larger than the Value in p!
	 * So to find several ranked Items, start with the largest
	 * (or smallest, but then you have to subtract the previous Ranks)
	 * and search the Rest only within the first partial Array using this Method!
	 */
	public static int GET_PERCENTILE_POS(final double[] items, final int percentile, final int start, final int stop) {
		return GET_STATISTIC_POS(items, (150+percentile*(stop+1-start))/100, start, stop); //+50 to achieve Rounding!
	}
	
	/**Creates the i-th Order Statistic for the whole Array
	 * using the QuickSort Algorithm.
	 * n-th Order Statistic = Maximum
	 * n/2  Order Statistic = Median
	 * 0-th Order Statistic = Minimum and so on.
	 * This Algorithm is ideal for Tasks like ranking up to a certain Point,
	 * because it needs only O(log N) Steps to find the Statistic
	 *
	 * As a Side Effect the Array itself is partially sorted in that
	 * all Items with an Index smaller than p are smaller and
	 * all Items with an Index  larger than p are larger than the Value in p!
	 * So to find several ranked Items, start with the largest
	 * (or smallest, but then you have to subtract the previous Ranks)
	 * and search the Rest only within the first partial Array!
	 * Alternatively if you need the m largest Elements, 
	 * read the Data into a Heap in O(N log N) Steps 
	 * and then retrieve the first m Elements in O(m log N) Steps. 
	 * @return the Index of the Item with the given Rank
	 *  to retrieve the Value, simply use Items[Statistic(Items, p, r, i)]
	 */
	public static int GET_STATISTIC_POS(final double[] items, final int i) {
		return GET_STATISTIC_POS(items, i, 0, items.length - 1);
	}
	
	/**Creates the i-th Order Statistic in the Range between p and stop (inclusive)
	 * using the QuickSort Algorithm.
	 * n-th Order Statistic = Maximum
	 * n/2  Order Statistic = Median
	 * 0-th Order Statistic = Minimum and so on.
	 * This Algorithm is ideal for Tasks like ranking up to a certain Point,
	 * because it needs only O(log N) Steps to find the Statistic.
	 * @return the Index of the Item with the given Rank
	 *  to retrieve the Value, simply use Items[Statistic(Items, p, r, i)]
	 *
	 * As a Side Effect the Array itself is partially sorted in that
	 * all Items with an Index smaller than p are smaller and
	 * all Items with an Index  larger than p are larger than the Value in p!
	 * So to find several ranked Items, start with the largest
	 * (or smallest, but then you have to subtract the previous Ranks)
	 * and search the Rest only within the first partial Array using this Method!
	 */
	public static int GET_STATISTIC_POS(final double[] items, final int stat, final int start, final int stop) {
		if (start >= stop)
			return start;
		final int pivotPos = PARTITION(items, stop, start); //after this, all Elements are heapified.
		final int k = pivotPos - start + 1;
		if (stat <= k) //in the first partial Array...
			return GET_STATISTIC_POS(items, stat    , start     , pivotPos); //
		//else //...or in the second partial Array, but counting from k on
			return GET_STATISTIC_POS(items, stat - k, pivotPos+1, stop    );
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//	QuickSort Algorithm working indirectly on the Array using an Index, @see Maths.Vector
	//  using an Index makes sense only when
	//  1) several different Sortings have to be present on the same Array OR
	//     Sorting is expensive, e.g. because of File Access OR
	//     because the Data is too large to be copied and must not be modified AND
	//  2) Read Access happens at least once on every Item.
	//
	//  alternatively make a Copy of the Data and sort that directly.
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * sorts three Elements of the given Array in Place.  
	 * Reverse Sort Order can be implemented by just swapping start and stop Index!
	 * 
	 * Implementation: 
	 * Optimized Implementation for all 6 possible Cases!  
	 * @param items  the Array to sort
	 * @param start  the Index to contain the smallest Value  
	 * @param middle the Index to contain the middle   Value 
	 * @param stop   the Index to contain the largest  Value 
	 */
	final static public void INDEX_THREE(final double[] items, final int[] index, final int start, final int middle, final int stop) { 
		//int swap; 
		//primitive Implementation, requires up to 9 Assignments and 3 Comparisons. 
		/*
		if (items [index[start]] > items[index[middle]]) {
			swap = index[start]; index[start] = index[middle]; index[middle] = swap; }
		if (items [index[start]] > items[index[stop]]) {
			swap = index[start]; index[start] = index[stop]; index[stop] = swap; }
		if (items [index[middle]] > items[index[stop]]) {
			swap = index[middle]; index[middle] = index[stop]; index[stop] = swap; }
		*/
		//requires only 3.5 Assignments and 2.6 Comparisons
		//double startVal, middleVal, stop_Val; //extra Assignments cost more than they save!   
		if(items[index[start]] <= items[index[middle]]) {
			if(items[index[middle]] <= items[index[stop]])
				return; 
			//(items[index[middle]] > items[index[stop]])
			if(items[index[start ]] > items[index[stop]]) { //start is greater than both
				final int swap = index[stop]; index[stop] = index[middle]; index[middle] = index[start]; index[start] = swap;
				return; }
			final int swap = index[middle]; index[middle] = index[stop]; index[stop] = swap;
			return; 
		}
		//(items[index[start]] > items[index[middle]]) 
		if(items[index[start]] > items[index[stop  ]]) { //start is greater than both
			if(items[index[middle]] > items[index[stop]]) { //stop is smallest
				final int swap = index[stop]; index[stop] = index[start]; index[start] = swap; 
				return;	
			} //else middle is smallest
			final int swap = index[stop]; index[stop] = index[start]; index[start] = index[middle]; index[middle] = swap; 
			return;	
		} //else 
		//(items[index[stop]] >= items[index[start]] > items[index[middle]]) 
		final int swap = index[middle]; index[middle] = index[start]; index[start] = swap;
	}
	
	/**
	 * Divide and Conquer Method for sorting an Array or finding its (n-th) Statistic:
	 * A Separator Element is determined and all other Elements ordered around it.
	 * a[p..r] -> a[p..q] <= a[q+1..r]
	 * The Elements of the Items Array are expected to be of Type OrderAble	 
	 * 
	 * @param items the Array to be searched for, partially sorted 
	 * @param stop  the  last Index to sort, typically items.length-1
	 * @param start the first Index to sort, typically 0
	 * @return the index of an inner Value 
	 * that partitions the Array into smaller Values below and larger Values above. 
	 */
	protected static final int PARTITION(final double[] values, final int[] index, final int stop, final int start) { //, boolean asc) {
		//final int stop = length-1; 
		if (stop <= start + 1){
			if (values[index[stop]] < values[index[start]]) {
				final int swap = index[stop]; index[stop] = index[start]; index[start] = swap; }
			return start; 
		}
		final int middle = (start + stop) >> 1;
		INDEX_THREE(values, index, start, middle, stop); 		
		int i = start;  //start, middle and stop are now sorted...
		int j = stop ;
		final int item = index[middle]; index[middle] = index[--j]; index[j] = item; //necessary as Sentinel to terminate loop! 
		final double val = values[item]; //swap the Pivot out and partition the Rest around it
		for(;;) { //swap all Items around the selected one
			while (val < (values[index[--j]])); //search for a greater Item
			while (val > (values[index[++i]])); //search for a smaller Item
			if (i >= j) //finished: all Elements left of j ...
				break; //...are smaller than those right of j
			final int swap = index[j]; index[j] = index[i]; index[i] = swap;
		} //swap Item above Pivot with Item below Pivot.
		index[stop-1] = index[i]; index[i] = item; //swap Pivot Value with item[i]
		/*/testing the Partitioning/Heap Property: middle Item is
		for(int k = i;++k <= stop;) //...larger than all left and... 
			Assert.IS_TRUE(values[index[k]] >= val);
		for(int k = i;--k >= start;) //...smaller than all right
			Assert.IS_TRUE(values[index[k]] <= val);
		*/
		return i; 
	}
	
	/** 
	 * QuickSort Algorithm:
	 * Divide and Conquer Method to sort the Array descending:
	 * The Array is divided into two, of which both are recursively sorted. 
	 * Implementation: 
	 * The larger Recursion is replaced by a Stack (which limits it's Size to Lb(items.length))
	 * the smaller is done directly (End-Recursion) 
	 * 
	 * @param items	the Array to sort 
	 */
	final static public int[] INDEX(final double[] items, final int[] index) {
		return INDEX(items, index, items.length, 0, null); }
	
	/** 
	 * QuickSort Algorithm:
	 * Divide and Conquer Method to sort the Array descending:
	 * The Array is divided into two, of which both are recursively sorted. 
	 * Implementation: 
	 * The larger Recursion is replaced by a Stack (which limits it's Size to Lb(items.length))
	 * the smaller is done directly (End-Recursion) 
	 * 
	 * @param items	the Array to sort 
	 * @param stop   last Index to sort+1 (typically items.length)
	 */
	final static public int[] INDEX(final double[] items, final int[] index, int stop) {
		return INDEX(items, index, stop, 0, null); }
	
	/** 
	 * QuickSort Algorithm:
	 * Divide and Conquer Method to sort the Array descending:
	 * The Array is divided into two, of which both are recursively sorted. 
	 * Implementation: 
	 * The larger Recursion is replaced by a Stack (which limits it's Size to Lb(items.length))
	 * the smaller is done directly (End-Recursion) 
	 * 
	 * @param items	the Array to sort 
	 * @param stop   last Index to sort+1 (typically items.length)
	 * @param start first Index to sort   (typically 0)
	 */
	final static public int[] INDEX(final double[] items, final int[] index, int stop, int start) {
		return INDEX(items, index, stop, start, null); }
	
	/** 
	 * QuickSort Algorithm:
	 * Divide and Conquer Method to sort the Array descending:
	 * The Array is divided into two, of which both are recursively sorted. 
	 * Implementation: 
	 * The larger Recursion is replaced by a Stack (which limits it's Size to Lb(items.length))
	 * the smaller is done directly (End-Recursion) 
	 * 
	 * @param items	the Array to sort 
	 * @param stop   last Index to sort+1 (typically items.length)
	 * @param start first Index to sort   (typically 0)
	 * @param stack optional Stack to use
	 */
	final static public int[] INDEX(final double[] values, int[] index, int stop, int start, int[] stack) {
		if ((index == null) ||
			(index.length <  stop))
			 index = new int[stop]; 
		if (index[0] == index[1]) //simple Test for no Permutation
			VectorInt.IDENTITY(index); 
		if ((stack == null) || 
			(stack.length <  64))
			 stack = new int[64]; //use maximum Length right away...
		--stop; //to account for the Length which is always 1 langer! 
		stack[0] = stop ; 
		stack[1] = start; 
		int SP = 1; 
		do {
			if (start >= stop) {
				start = stack[SP--]; 
				stop  = stack[SP--]; 
			} else {
				final int middle = PARTITION(values, index, stop, start);
				if ((stop-middle) < (middle - start)) { //stack the smaller Interval
					stack[++SP] = middle-1; stack[++SP] = start   ; start = middle+1; 
				} else {
					stack[++SP] = stop    ; stack[++SP] = middle+1; stop  = middle-1; 
				}
			}
		} while(SP > 0);
		return index; 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Calculating Statistics using Partitioning
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Creates the i-th Order Statistic in the Range between p and r
	 * using the QuickSort Algorithm.
	 * n-th Order Statistic = Maximum
	 * n/2  Order Statistic = Median
	 * 0-th Order Statistic = Minimum and so on.
	 * This Algorithm is ideal for Tasks like ranking up to a certain Point.
	 * because it needs only O(log N) Steps to find the Statistic
	 * @return the Index of the Item with the given Rank
	 *  to retrieve the Value, simply use Items[Statistic(Items, p, r, i)]
	 *
	 * As a Side Effect the Array itself is partially sorted in that
	 * all Items with an Index smaller than p are smaller and
	 * all Items with an Index  larger than p are larger than the Value in p!
	 * (Quasi Heap structure)
	 * So to find several ranked Items, start with the largest
	 * (or smallest, but then you have to subtract the previous Ranks)
	 * and search the Rest only within the first partial Array!
	 */
	public static int GET_STATISTIC(double[] Items, int[] index, int i) {
		return GET_STATISTIC(Items, index, 0, Items.length - 1, i); }
	
	/**Creates the i-th Order Statistic in the Range between p and r
	 * using the QuickSort Algorithm.
	 * n-th Order Statistic = Maximum
	 * n/2  Order Statistic = Median
	 * 0-th Order Statistic = Minimum and so on.
	 * This Algorithm is ideal for Tasks like ranking up to a certain Point.
	 * because it needs only O(log N) Steps to find the Statistic.
	 * @return the Index of the Item with the given Rank
	 *  to retrieve the Value, simply use Items[Statistic(Items, p, r, i)]
	 *
	 * As a Side Effect the Array itself is partially sorted in that
	 * all Items with an Index smaller than p are smaller and
	 * all Items with an Index  larger than p are larger than the Value in p!
	 * (Quasi Heap structure)
	 * So to find several ranked Items, start with the largest
	 * (or smallest, but then you have to subtract the previous Ranks)
	 * and search the Rest only within the first partial Array using this Method!
	 */
	public static int GET_STATISTIC(double[] Items, int[] index, int p, int r, int i) {
		if (p >= r)
			return p;
		// TODO: LOGIC: PARTITION is declared as PARTITION(values, index, stop, start) (see the
		// 4-arg overload above), but this call passes (p, r) - the two bounds are swapped
		// positionally. Since p < r is guaranteed here, PARTITION's internal "stop" ends up
		// smaller than its internal "start", which almost always trips its `stop <= start + 1`
		// short-circuit and returns the wrong split point, corrupting the indexed order-statistic
		// results computed through this method.
		int q = PARTITION(Items, index, p, r); //after this all Elements
		int k = q - p + 1;
		if (i <= k)
			return GET_STATISTIC(Items, index, p, q, i); //
		else
			return GET_STATISTIC(Items, index, q + 1, r, i - k);
	}
	
	/**
	 * Determines whether the whole array is ascending, descending or unordered.
	 * @return the Order of the Items in this Container
	 * @see streamIO.Float.IStreamIn_Float#getOrder()
	 */
	final static public int GET_ORDER(final double[] arr) {
		return GET_ORDER(arr, 0, arr.length); }

	/**
	 * Determines the order of {@code items} between {@code start} and {@code stop}.
	 * @return the Order of the Items in this Container (ORDER_ASC_STRICT, ORDER_ASC or IStreamIn.ORDER_DESC)
	 * or the negated Index of the last offending Value
	 * @see streamIO.Float.IStreamIn_Float#getOrder()
	 */
	final static public int GET_ORDER(final double[] items, final int start, final int stop) {
		int i = stop;
		double last  = items[start];
		double first = items[  --i];
		boolean strict = true; 
		final boolean asc = (last < first);
		for (; --i >= start;) {
			last = first; first = items[i];
			if (asc != (last > first)) {
				strict = false; 
				if (last != first) //
					return -i-1; //IStreamIn.ORDER_NONE; 
			}
		}
		return asc 
			? strict 
					? IStreamIn.ORDER_ASC_STRICT 
					: IStreamIn.ORDER_ASC
			: IStreamIn.ORDER_DESC;
	}
	
	/**
	 * Determines the order of {@code arr} between {@code start} and {@code stop}, also
	 * distinguishing the constant case from strict monotonicity.
	 * @return the Order of the Items in this Container
	 * or the negated Index-1 of the last offending Value
	 * @see streamIO.Float.IStreamIn_Float#getOrder()
	 */
	final static public int GET_ORDER_FULL(final double[] arr, final int start, final int stop) {
		int i = stop;
		double last  = arr[start];
		double first = arr[  --i];
		boolean strict = true; 
		byte asc = ByRefDouble.POSITION(first, last); //0+-1
		for (; --i >= start;) {
			last = first; first = arr[i]; 
			byte pos = ByRefDouble.POSITION(first, last); 
			if (pos != asc) { 
				strict = false; 
				if (pos != IOrdered.ORDER_CONST)
					return -i-2; //IOrdered.ORDER_NONE;
			}
		}
		if (asc == IStreamIn.ORDER_CONST)
			return IStreamIn.ORDER_CONST; 
		return (asc > 0)
			? strict 
				? IStreamIn.ORDER_ASC_STRICT 
				: IStreamIn.ORDER_ASC
			: strict
				? IStreamIn.ORDER_DESC_STRICT
				: IStreamIn.ORDER_DESC;
	}
	
	/**Returns true, when the Items in the Container are ordered (ascending or descending)
	 * from the i-th Item on (monotonous Sequence)	 */
	final static public boolean IS_ORDERED(final double[] a, final int start, final int stop){
		int j = stop;
		double pred = a[--j];
		double succ = a[start]; 
		boolean asc = (pred > succ); 
		while (--j >= start) {
			succ = pred; pred = a[j];
			if ((pred > succ) == asc) {
				return false;} 
		}
		return true; }
		
	/////////////////////////////////////////////////////////////////////////////////////
	// searching
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Searches an ordered Array by BiSection	 */
	final static public int POSITION_IN_SORTED_ARRAY(final double[] xx, final double x) {
		return POSITION_IN_SORTED_ARRAY(xx, x, 0, xx.length); }
	
	/**Searches an ordered Array by BiSection	 */
	final static public int POSITION_IN_SORTED_ARRAY(final double[] xx, final double x, final int start, final int stop) {
		return POSITION_IN_SORTED_ARRAY(xx, x, start, stop, true, 0, xx.length); }
	
	/**Searches an ordered Array by BiSection
	 * TODO: use Interpolation to speed up BiSection like with 
	 * @see streamIO.object.enumer.container.ArraySorted 
	 * @param xx the sorted Array to search 
	 * @param x the Value to search for 
	 * @param start the start Index (inclusive) 
	 * @param stop the end Index (exclusive, to stay consistent with other Array Ops)
	 * @param ascending flag whether the Array is sorted ascending or descending 
	 * @return the Index of the first Value larger then the given, 
	 * start-1 if it is smaller than the first and 
	 * stop if is larger than the last 
	 * If you want to check for exact Matches, you have to test further for x == xx[POSITION_IN_SORTED_ARRAY]. 
	 *//*
	final static public int POSITION_IN_SORTED_ARRAY(final double[] xx, final double x, int start, int stop, final boolean ascending) {
		--stop; //avoid access over the last Item
		while (stop > start+1) { //otherwise BiSection doesn't make sense...
			final int jm = (stop+start) >> 1;
			if ((x >= xx[jm]) == ascending)
				 start= jm;
			else stop = jm;	}
		//End Game
		if (x < xx[start]) 
			return start-1;  
		if (x > xx[stop]) 
			return stop; 
		return start; 
	}*/
	
	/** Searches for the specified object / Value, and returns an index to it.
	  * It is here where the Hunter Algorithm is implemented (binary Extension & Search)!
	  *
	  * @param   elem	the desired component.
	  * @param   lower   the proposed lower bound to search from (inclusive).
	  * @param   upper   the proposed upper bound to search from (exclusive).
	  * @return  the index of an occurrence of the specified object in this
	  *		  Array at position less than <code>upper</code>
	  *			and greater than <code>lower</code> in the sorted Array;
	  *			If the object is not found, the next Index above the targeted one
	  *			is returned, also <code>upper</code> for items larger than the largest
	  *			and <code>lower-1</code> for items smaller than the smallest.
	  *
	  * Design Decisions:
	  * This could be programmed recursively, but that would be less effective!
	  * This returns ANY Item that equals the searched one, NOT the first one!	
	  */
	final static public int POSITION_IN_SORTED_ARRAY(final double[] items, final double item, int lower, int upper, boolean interPol, int minPos, int maxPos){
		//Check and correct Parameters
		if (minPos >= maxPos) {
			final int swap = minPos; minPos = maxPos; maxPos = swap; 
		}
		if (lower >= upper) {
			final int swap = upper; upper = lower; lower = swap; 
		}
		if (minPos < 0)
			minPos = 0;
		if (maxPos > items.length)
			maxPos = items.length;
		final boolean ascending = items[upper-1] > items[lower];
		return POSITION_IN_SORTED_ARRAY(items, item, lower, upper, interPol, minPos, maxPos, ascending);
	}		
	
	/** Searches for the specified object / Value, and returns an index to it.
	  * It is here where the BiSection Algorithm is implemented (binary Search)!
	  *
	  * @param   elem	the desired component.
	  * @param   lower   the lower bound to search from (inclusive).
	  * @param   upper   the upper bound to search from (exclusive).
	  * @return  the index of an occurrence of the specified object in this
	  *		  Array at position less than <code>upper</code>
	  *			and greater than <code>lower</code> in the sorted Array;
	  *			If the object is not found, the next Index above the targeted one
	  *			is returned, also <code>upper</code> for items larger than the largest
	  *			and <code>lower-1</code> for items smaller than the smallest.
	  *
	  * Design Decisions:
	  * This could be programmed recursively, but that would be less effective!
	  * This returns ANY Item that equals the searched one, NOT the first one!	
	  */
	final static public int POSITION_IN_SORTED_ARRAY(final double[] items, final double item, int lower, int upper, boolean interPol, final int minPos, final int maxPos, final boolean ascending){
		if (lower < minPos)  
			lower = minPos; 
		if (upper > maxPos) 
			upper = maxPos; 
		--upper; 
		//Extrapolation is even more dangerous than Interpolation, so don't do it here!!! 
		while((item < items[lower]) == ascending) { //enlarge the Area
			if (lower <= minPos)
				return minPos-1; 
			lower -= upper - lower; 
			if (lower <= minPos)
				lower  = minPos; 
		}
		while((item > items[upper]) == ascending) { //enlarge the Area
			if (upper >= maxPos-1)
				return maxPos; 
			upper += upper - lower; 
			if (upper >= maxPos)
				upper  = maxPos-1; 
		}
		double dHL = items[upper] - items[lower]; 
		double dLo = item - items[lower]; 
		while (lower+1 < upper) { //iterative instead of recursive!
			final int middle; 
			if (interPol) { //Use linear Interpolation to find the Item faster!
				final int span = upper-lower; 
				final int delta = (int) (span*dLo/dHL); 
				if ((delta <= 0) || //Extrapolation is even more dangerous than Interpolation, so don't do it!!! 
					(delta >= span)) { //value not bracketed or Array not sorted!
					interPol = false; //once failed, switch to BiSection!
					middle = (lower + upper) >> 1;	// (lower + upper)/ 2
				} else 
					middle = lower + delta; 
			} else //Use BiSection
				middle = (lower + upper) >> 1;	// (lower + upper)/ 2
			final double middleItem = items[middle]; // ItemAt(middle);	//equivalent!
			if ((item > middleItem) == ascending) {
				  lower = middle; if (interPol) dLo = item - middleItem;
			}else{upper = middle; if (interPol) dHL = dLo - item + middleItem; }	//greater or equal
		}	
		//End Game
		if ((item < items[lower]) == ascending)
			return lower-1;  
		if ((item > items[upper]) == ascending)  
			return upper; 
		return lower; 
	}	//is always greater or equal to the Item

	/////////////////////////////////////////////////////////////////////////////////////
	// Member Variables
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Flag whether the Array is sorted ascending	 */
	final public boolean ascending;
	
	/** Flag whether to use Interpolation for Searching.	 */
	public boolean interpolate;
	
	/** minimum Index to use, defaults to 0	 */
	final public int minPos;
	
	/** maximum Index to use, defaults to the Array Length	 */
	final public int maxPos;
	
	/**Local Reference to the sorted Array being searched.	 */
	private final double[] mArray;
	
	/**Index at which the previous Item was found	 */
	public int lastPosition;
	
	/**Constructor to hand over the Array to be searched.
	 * Determines the Sort Order	*/
	public HunterDouble(final double[] array) {	//don't compare a[0] with a[1], they may be the same.
		this (array, 0, array.length);
	}
	
	/**Constructor to hand over the Array to be searched.
	 * Determines the Sort Order	*/
	public HunterDouble(final double[] array, final int minPos_, final int maxPos_) {	//don't compare a[0] with a[1], they may be the same.
		mArray = array;
		this.minPos = minPos_; 
		this.maxPos = maxPos_; 
		ascending = mArray[minPos] < mArray[maxPos-1]; 
	}
	
	/** resets the Hunter so it considers the whole Interval 	*/
	public void reset() { lastPosition = -1; }
	
	/** gives the Hunter the Hint to search at the given Position	*/
	public void hint(final int position) { lastPosition = position; }
	
	/** Hunts the next Item down, starting from the last find
	 * by first enlarging the Search Area 
	 * and then narrowing it down again.	 */
	public int hunt(final double item) {
		return POSITION_IN_SORTED_ARRAY(mArray, item, lastPosition-1, lastPosition+1, interpolate , minPos, maxPos, ascending); 
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////

	/** Tests the static Ranking Method of this Class	 */
	private static void testRank() throws java.io.IOException {
		L.n("Testing Scrambling and Ranking:");
		double[] arr = new double[10];
		double length = arr.length; 
		for (int i = arr.length; --i >= 0; ) { //Linear Distribution
			arr[i] = i / length; } //worst Case for QuickSort! 
		L.n("Original Vector: ");
		streamIO.AStreamOut.ARRAY_TO_STREAM(System.out, arr, ", ");
		SCRAMBLE_AT(arr); //Now: scramble the Set to avoid sorted Effects.
		L.n("\nVector after Scrambling: ");
		streamIO.AStreamOut.ARRAY_TO_STREAM(System.out, arr, ", ");
		
		//Testing Min, Max, which don't modify the Array...
		final int[] maxPair = new int[2];
		VectorDouble.MAX2POS(arr, maxPair);
		L.n("\nThe Indices of the two maximum Values: ").l(maxPair[0]).l(maxPair[1]);
		Assert.IS_TRUE(arr[maxPair[0]] >= arr[maxPair[1]]);
		
		VectorDouble.MIN2POS(arr, maxPair);
		L.n("\nThe Indices of the two minimum Values: ").l(maxPair[0]).l(maxPair[1]);
		Assert.IS_TRUE(arr[maxPair[0]] <= arr[maxPair[1]]);
		
		VectorDouble.MIN2MAX2POS(arr, maxPair);
		L.n("\nThe Indices of the Minimum and Maximum Values: ").l(maxPair[0]).l(maxPair[1]);
		Assert.IS_TRUE(arr[maxPair[0]] <= arr[maxPair[1]]);
		
		final int[] maxQuad = new int[4];
		VectorDouble.MIN2MAX2POS(arr, maxQuad);
		L.n("\nThe Indices of the two Minimum and two Maximum Values: "
		).l(maxQuad[0]).l(maxQuad[1]).l(maxQuad[2]).l(maxQuad[3]);
		Assert.IS_TRUE(arr[maxQuad[0]] <= arr[maxQuad[1]]);
		Assert.IS_TRUE(arr[maxQuad[1]] <= arr[maxQuad[2]]);
		Assert.IS_TRUE(arr[maxQuad[2]] <= arr[maxQuad[3]]);

		//Testing Statistic, which modifies the Array...
		int i = 5;
		int pos = HunterDouble.GET_STATISTIC_POS(arr, i);
		L.n("\n" + i + "th Element is:" + pos + " with Value " + arr[pos]);
		L.n("\nThe Array should have been partially sorted around the returned Index:");
		streamIO.AStreamOut.ARRAY_TO_STREAM(System.out, arr, ", ");
		i = 2;
		pos = HunterDouble.GET_STATISTIC_POS(arr, i, 0, 5);
		L.n(i + "th Element is:" + pos + " with Value " + arr[pos]);
		L.n("\nVector's i-th Statistic: ");
		i = arr.length + 1;
		while (--i > 0) { //Linear Distribution
			pos = HunterDouble.GET_STATISTIC_POS(arr, i, 0, i - 1);
			L.n(i + "th Element is:" + pos + " with Value " + arr[pos]);
		}
		streamIO.AStreamOut.ARRAY_TO_STREAM(System.out, arr, ", ");
	}

	/** tests Searching by BiSection 	*/
	private static final void testPositionInSortedArray() {
		final int N = 100; 
		double x;
		double[] xx = new double[1+N];
		/* create array to be searched */
		for (int i=0;i<=N; i++) {
			xx[i]=Math.exp(i/20.0)-74.0; } 
		L.n("\nresult of:  j=-1 indicates x too small\n");
		L.n("j="+N+" indicates x too large");
		L.n("locate ").l("\tj").l("\txx(j)").l("\txx(j+1)");
		final int iLo = 0; 
		final int iHi = xx.length-1; 
		
		final HunterDouble hunter = new HunterDouble(xx, iLo, iHi+1);
		/* perform test */
		for (int i=0; i<=20; i++) {
			switch (i) {
			case  0: x = xx[iLo]-1; break;
			case  1: x = (xx[iLo]+xx[iLo+1])/2; break;
			case 19: x = (xx[iHi]+xx[iHi-1])/2; break;
			case 20: x = xx[iHi]+1; break;
			default: x = -100.0+10.0*i; break;
			}
			final int j = POSITION_IN_SORTED_ARRAY(xx,x); //,iLo,iHi);
			
			verifyPosition(xx, j, x);
			
			hunter.hint(5*i);
			final int h = hunter.hunt(x);
			Assert.EQUALS(j, h); 
			//verifyPosition(xx, iLo, iHi, j, x);
		}
	}

	/** @see #testPositionInSortedArray() uses this Method exclusively 	 */
	private static final void verifyPosition(final double[] xx, int j, final double x) {
		double less = Double.NEGATIVE_INFINITY; 
		double more = Double.POSITIVE_INFINITY; 
		if (j < xx.length-1) 
			Assert.IS_TRUE(x <= (more = xx[j+1]));
		else if (j >= xx.length)
			j = xx.length-1; 
		if (j >= 0) 
			Assert.IS_TRUE(x >= (less = xx[j]));
		L.n().l(x).l(j).l(less).l(more);
	}

	/** Tests Sorting Methods	 */
	public static void testSort() {
		final double[] items = VectorDouble.RANDOMIZED(20000);
		SORT(items); 
		for (int i = items.length; --i >= 1;) {
			Assert.IS_TRUE(items[i]>=items[i-1]);
		}
	}
	
	private static final void testPermuteAt() {
		L.enter();
		final double[] test   = new double[15];
		final double[] sorted = new double[test.length];
		final int  [] index  = Permutation.IDENTITY(test.length); 
		for (int i = 9; --i >= 0;) {
			VectorDouble.RANDOMIZE_AT_1_1(test);
			INDEX(test, index); 
			PERMUTE(test, index, sorted); 
			Assert.EQUALS(2, GET_ORDER(sorted));
			PERMUTE_AT(test, index);
			Assert.EQUALS(test, sorted); 
			VectorInt.NEG_AT(index);
		}
	}
	
	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) throws Exception {
		L.n("Testing " + HunterDouble.class.getName());
		testPermuteAt(); 
		testSort(); 
		testPositionInSortedArray(); 
		testRank();
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws Exception {
		testIt(args);
	}

}
