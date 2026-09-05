/*
 * File Name: HunterFloat.java
 * Created on: 20.01.2004
 *
 */
package math.vector;

import streamIO.Assert;
import streamIO.IOrdered;
import streamIO.Log;
import streamIO.integer.random.RandomFast;
import streamIO.object.IStreamIn;
import streamIO.real.ArrayStreamIn_Float;
import streamIO.real.IStreamIn_Float;
import streamIO.real.StreamIn_Float;
import function.byref.ByRefDouble;

/**
 * Stateful binary-search "hunter" over a sorted {@code float[]}, together with the static
 * QuickSort, permutation, ranking and order-statistic (median/percentile) algorithms shared
 * by the whole vector family for values requiring only an order relation.
 *
 * <p>Title: HunterFloat<p>
 * Description:
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
 * mtime: 2026-09-05T12:49:12Z
 * digest: 7d52769deb81f0aed858aaa8e3338947d4e5e53728dd49b3c712fb7fb99a566b
 * stale: false
 * -->
 */
public class HunterFloat {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(VectorDouble.class, 1);

	/**
	 * Don't use this in Vector Operations, because temporary Array is created. 
	 * @return this Vector with the Elements permuted according to the given Permutation     
	 */
	final static public float[] PERMUTE(final float[] a, final int[] index) {
		return PERMUTE(a, index, false, null); }

	/** Writes {@code a} reordered according to {@code index} into {@code ret}.
	 * @return this Vector with the Elements permuted according to the given Permutation     */
	final static public float[] PERMUTE(final float[] a, final int[] index, final float[] ret) {
		return PERMUTE(a, index, false, ret); }
	
	/**
	 * Don't use this in Vector Operations, because temporary Array is created. 
	 * @return this Vector with the Elements permuted according to the given Permutation     
	 */
	final static public float[] PERMUTE(final float[] a, final int[] index, final boolean reverse) {
		return PERMUTE(a, index, reverse, null); }

	/** Writes {@code a} reordered according to {@code index}, optionally reversed, into {@code ret}.
	 * @return this Vector with the Elements permuted according to the given Permutation     */
	final static public float[] PERMUTE(final float[] a, final int[] index, final boolean reverse, float[] ret) {
		if (ret == null)
			ret =  new float[a.length];
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
	 * Don't use this in Vector Operations, because temporary Array is created.
	 * Permutation cannot be done in Place with O(n) Operations, 
	 * as you can see trying the Counter- Example [4,5,3,2,1,0]  
	 * @return this Vector with the Elements permuted according to the given Permutation     
	 */
	final static public float[] PERMUTE_AT(final float[] a, final int[] index) {
		/*
		final float[] tmp = new float[a.length]; 
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
			final float tmp = a[i]; 
			while(j != i) {
				a[k] = a[j]; k = j; j = index[j]; 
				index[k] = -j; 
			} a[k] = tmp; index[i] = -index[i];
		}
		return a;
	}
	
	/// The following Code does not work, because in Place is not possible! see [4,5,3,2,1,0] 
	/*		float tmp;	//Undo the Row Permutations!
			int j, k = a.length;
			while (--k > 0) { 	//first row is not modified, because L[1,1]=1
				if (perm[k] != k) {
					tmp = a[k]; a[k] = a[j = perm[k]]; a[j] = tmp; }
			}
			return a; }
	*/

	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Reports whether the given range of {@code items} is monotonically ordered.
	 * @return null  when the items are unordered or constant
	 * Boolean.TRUE  when the items are ordered ascending
	 * Boolean.False when the items are ordered descending
	 *
	 * @see streamIO.Float.IStreamIn_Float#getOrder()
	 */
	final static public Boolean IS_ASCENDING(final float[] items, final int start, final int stop) {
		final int order = GET_ORDER(items, start, stop); 
		switch (order) {
			case IStreamIn.ORDER_ASC_STRICT : return Boolean.TRUE; 
			case IStreamIn.ORDER_ASC        : return Boolean.TRUE; 
			case IStreamIn.ORDER_DESC       : return Boolean.FALSE; 
			//case IStreamIn.ORDER_NONE       : return null; 
			default: //throw new RuntimeException("Unexpected Return Value:"+order);
				return null; 
		}
	}
	
	/**
	 * Determines whether the whole array is ascending, descending or unordered.
	 * @return the Order of the Items in this Container
	 * @see streamIO.Float.IStreamIn_Float#getOrder()
	 */
	final static public int GET_ORDER(final float[] items) {
		return GET_ORDER(items, 0, items.length); }

	/**
	 * Determines the order of {@code items} between {@code start} and {@code stop}.
	 * @return the Order of the Items in this Container ORDER_ASC_STRICT, ORDER_ASC or IStreamIn.ORDER_DESC
	 * or the negated Index of the last offending Value
	 * @see streamIO.Float.IStreamIn_Float#getOrder()
	 */
	final static public int GET_ORDER(final float[] items, final int start, final int stop) {
		int i = stop;
		float last  = items[start];
		float first = items[  --i];
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
	 * or the negated Index-2 of the last offending Value
	 * @see streamIO.Float.IStreamIn_Float#getOrder()
	 */
	final static public int GET_ORDER_FULL(final float[] arr, final int start, final int stop) {
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
	final static public int[] RANK(final float[] arr) {
		return RANK(arr, new int[arr.length], new int[arr.length]); }

	/**
	  * Ranks {@code arr} by inverting its sort-index permutation.
	  * @param arr The Array to be ranked
	  * @param tmp a temporary Array passed for Effectiveness (Reuse)
	  * @param ret the Array to be returned passed for Effectiveness (Reuse)
	  * @return the Ranking of the given Array.
	  */
	final static public int[] RANK(final float[] arr, final int[] tmp, final int[] ret) {
		return VectorInt.INVERSE(INDEX(arr, tmp), ret); }

	////////////////////////////////////////////////////////////////////////////////
	//	QuickSort Algorithm for direct Array Ranking and Sorting, @see Maths.Vector
	////////////////////////////////////////////////////////////////////////////////

	/** Scrambles the Vector by swapping all of it's Elements	 */
	final static public float[] SCRAMBLE_AT(final float[] ret) {
		int j, i = ret.length;
		while (--i >= 0) { //Linear Distribution
			j = (int) (RandomFast.STREAM.nextLong() % ret.length);
			final float tmp = ret[i]; ret[i] = ret[j]; ret[j] = tmp;
		}
		return ret;
	}
	
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
	final static public void SORT_THREE(final float[] items, final int start, final int middle, final int stop) { 
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
				final float swap = items[stop]; items[stop] = items[middle]; items[middle] = items[start]; items[start] = swap;
				return; }
			final float swap = items[middle]; items[middle] = items[stop]; items[stop] = swap;
			return; 
		}
		//(items[start] > items[middle]) 
		if(items[start] > items[stop]) { //start is greater than both
			if(items[middle] > items[stop]) { //stop is smallest
				final float swap = items[stop]; items[stop] = items[start]; items[start] = swap; 
				return;	
			} //else middle is smallest
			final float swap = items[stop]; items[stop] = items[start]; items[start] = items[middle]; items[middle] = swap; 
			return;	
		} //else 
		//(items[stop  ] >= items[start] > items[middle]) 
		final float swap = items[middle]; items[middle] = items[start]; items[start] = swap;
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
	protected static final int PARTITION(final float[] items, final int stop, final int start) { //, boolean asc) {
		if (stop <= start + 1){
			if (items[stop] < items[start]) {
				final float swap = items[stop]; 
				items[stop ] = items[start]; 
				items[start] = swap;
			}
			return start; 
		}
		final int middle = (start + stop) >> 1;
		SORT_THREE(items, start, middle, stop); 		
		int i = start;  //start, middle and stop are now sorted...
		int j = stop ;
		final float item = items[middle]; //swap the Pivot out and partition the Rest around it
		items[middle] = items[--j]; items[j] = item; //necessary as Sentinel to terminate loop! 
		for(;;) { //swap all Items around the selected one
			while (item < (items[--j])); //search for a greater Item
			while (item > (items[++i])); //search for a smaller Item
			if (i >= j) //finished: all Elements left of j ...
				break; //...are smaller than those right of j
			final float swap = items[j]; items[j] = items[i]; items[i] = swap;
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
	 * Recursive QuickSort Algorithm:
	 * Divide and Conquer Method to sort the Array descending:
	 * The Array is divided into two, of which both are recursively sorted.	 
	 * 
	 * @param items the Array to be sorted 
	 * @param stop  the  last Index to sort
	 * @param start the first Index to sort
	 */
	private static final void SORT_RECURSIVE(final float[] items, final int stop, final int start) {
		if (start >= stop) 
			return; // Items; //not effective to return since recursive!
		final int middle = PARTITION(items, stop, start);
		SORT_RECURSIVE(items, middle-1, start);
		SORT_RECURSIVE(items, stop    , middle + 1);
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
	final static public void SORT(final float[] items) {
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
	final static public void SORT(final float[] items, int stop) {
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
	final static public void SORT(final float[] items, int stop, int start) {
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
	final static public void SORT(final float[] items, int stop, int start, int[] stack) {
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
	
	/** sort an array a[1..stop-1] by Shell's method (8.1) 
	 * a powerful Variant of Insertion Sort
	 * Faster than QuickSort for n smaller than 50! 
	 * TODO: also define an arbitrary lower Bound 'start' for sorting other than 1 
	 * @param a the array to sort
	 * @param n the Size of the Array
	 */
	final static public void SORT_SHELL(final float a[], final int stop) {
		int inc=1;
		do { //find Power of 3 
			inc *= 3;
			inc++;
		} while (inc < stop);
		do { //insert
			inc /= 3;
			for (int i=1+inc; i < stop; i++) {
				final float v=a[i];
				int j=i;
				while (a[j-inc] > v) {
					a[j]=a[j-inc];
					j -= inc;
					if (j <= inc) {
						break; } 
				}
				a[j]=v;
			}
		} while (inc > 1);
	}
	
	/**Side Effect: partially sorts the List around the Median 
	 * and both first and last Quartile. 
	 * @return the Tri-Median of this Array ('middle' Value), 
	 * which is a weighted Mean betwen the Median(*0,5) 
	 * and the first and last Quartile(*0.25) 	 */
	final static public float GET_TRI_MEDIAN(final float[] items) {
		final int half  = 1+(items.length >> 1);
		final int quart = 1+(items.length >> 2);
		final float median = items[GET_STATISTIC_POS(items, half, 0, items.length-1)];
		final float quart1 = items[GET_STATISTIC_POS(items, quart, 0, half-1)]; //since the Items are ordered around the Median, it lies in the Middle!
		final float quart2 = items[GET_STATISTIC_POS(items, quart, half, items.length-1)];
		return (quart1+median+median+quart2)*0.25f; 
	}
	
	/**Side Effect: partially sorts the List around the Median as the Pivot
	 * (which ends up in the Middle of the Array)
	 * @return the simple Median of this Array ('middle' Value)	 */
	final static public int GET_MEDIAN_POS(final float[] items) {
		return GET_STATISTIC_POS(items, 1+(items.length >> 1), 0, items.length-1); }
	
	/**Side Effect: partially sorts the List 
	 * @return the simple Median of this Array ('middle' Value)	 */
	final static public double GET_MEDIAN_FAST(final float[] items) {
		return items[GET_MEDIAN_POS(items)]; 
	}
	
	/** 
	 * Side-Effect: partially sorts the Array around the Median
	 * (which ends up in the Middle of the Array)
	 * @param arr the Array to search in 
	 * @return the exact Median (considering the tie Case) 
	 */
	final static public float GET_MEDIAN(final float[] arr) {
		return GET_MEDIAN(arr, 0, arr.length-1); }
	
	/** 
	 * Side-Effect: partially sorts the Array around the Median
	 * (which ends up in the Middle of the Array)
	 * @param arr the Array to search in 
	 * @param start the first Index (inclusive) 
	 * @param stop the last Index (inclusive) 
	 * @return the exact Median (considering the tie Case) 
	 */
	final static public float GET_MEDIAN(final float[] arr, final int start, final int stop) {
		final int numItems = stop-start+1; 
		if ((numItems & 1) != 0) { //odd Case, easy
			return arr[HunterFloat.GET_STATISTIC_POS(arr, 1+(numItems>>1), start, stop)]; //Median
		} //even Case
			final int j=numItems >> 1;
			final float m2 = arr[HunterFloat.GET_STATISTIC_POS(arr, j+1, start, stop)];
			final float m1 = arr[HunterFloat.GET_STATISTIC_POS(arr, j  , start, start+j)];
			return 0.5f*(m1+m2);
	}
	
	/**Creates the n-th Percentile Order Statistic for the whole Array
	 * using the QuickSort Algorithm.
	 * 100-th Percentile = Maximum
	 *  50-th Percentile = Median
	 *   0-th Percentile = Minimum and so on.
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
	public static int GET_PERCENTILE_POS(final float[] items, final int percentile) {
		return GET_PERCENTILE_POS(items, percentile, 0, items.length - 1);
	}
	
	/**Creates the n-th Percentile Order Statistic for the whole Array
	 * using the QuickSort Algorithm.
	 * 100-th Percentile = Maximum
	 *  50-th Percentile = Median
	 *   0-th Percentile = Minimum and so on.
	 * This Algorithm is ideal for Tasks like ranking up to a certain Point,
	 * because it needs only O(log N) Steps to find the Statistic
	 *
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
	public static int GET_PERCENTILE_POS(final float[] items, final int percentile, final int start, final int stop) {
		return GET_STATISTIC_POS(items, (50+percentile*(stop+1-start))/100, start, stop); //+50 to achieve Rounding!
	}
	
	/**Creates the i-th Order Statistic in the Range between p and r
	 * using the QuickSort Algorithm.
	 * n-th Order Statistic = Maximum
	 * n/2  Order Statistic = Median
	 * 0-th Order Statistic = Minimum and so on.
	 * This Algorithm is ideal for Tasks like ranking up to a certain Point,
	 * because it needs only O(log N) Steps to find the Statistic
	 * @return the Index of the Item with the given Rank
	 *  to retrieve the Value, simply use Items[Statistic(Items, p, r, i)]
	 *
	 * Side Effects: the Array itself is partially sorted afterwards in that
	 * all Items with an Index smaller than p are smaller and
	 * all Items with an Index  larger than p are larger than the Value in p!
	 * (Partition Tree structure). 
	 * So to find several ranked Items, start with the largest
	 * (or smallest, but then you have to subtract the previous Ranks)
	 * and search the Rest only within the first partial Array!
	 * Alternatively if you need the m largest Elements, 
	 * read the Data into a Heap in O(N log N) Steps 
	 * and then retrieve the first m Elements in O(m log N) Steps. 
	 * @param position the Position to find: 1..(stop-start), not starting from 0!!!  
	 * The Algorithm is robust against any Value for the Position, 
	 * if out of Bounds, the respective Bound Index (stop or start) will be returned!
	 * @return the Position of the desired Statistic
	 */
	final static public int GET_STATISTIC_POS(final float[] items, final int position) {
		return GET_STATISTIC_POS(items, position, 0, items.length - 1); }

	/**
	 * Returns the i-th Order Statistic in the Range between p and r
	 * using the QuickSort Algorithm.
	 * n-th Order Statistic = Maximum
	 * n/2  Order Statistic = Median
	 * 1-st Order Statistic = Minimum and so on.
	 * This Algorithm is ideal for Tasks like ranking up to a certain Point,
	 * because it needs only O(log N) Steps to find the Statistic.
	 * @return the Index of the Item with the given Rank
	 *  to retrieve the Value, simply use items[Statistic(items, p, r, i)]
	 *
	 * As a Side Effect the Array itself is partially sorted in that...
	 * all Items with an Index smaller than the returned Position are smaller and
	 * all Items with an Index  larger than the returned Position are larger than the Value in p!
	 * (Quasi Heap structure)
	 * So to find several ranked Items, start with the largest
	 * (or smallest, but then you have to subtract the previous Ranks)
	 * and search the Rest only within the first partial Array using this Method!
	 * @param items the Array to seach in 
	 * @param start
	 * @param stop
	 * @param position the Position to find: 1..(stop-start), not starting from 0!!!  
	 * The Algorithm is robust against any Value for the Position, 
	 * if out of Bounds, the respective Bound Index (stop or start) will be returned!
	 * @return the Position of the desired Statistic 
	 */
	final static public int GET_STATISTIC_POS(final float[] items, final int position, final int start, final int stop) {
		if (start >= stop)
			return start;
		final int q = PARTITION(items, stop, start); //after this, all Elements are heapified.
		final int k = q - start + 1;
		if (position <= k) {  //in the first partial Array...
			return GET_STATISTIC_POS(items, position, start, q); } //
		//else //...or in the second partial Array, but counting from k on!
			return GET_STATISTIC_POS(items, position - k, q + 1, stop);
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
	 * Precondition: index needs to be filled with a valid Permutation! 
	 * 
	 * Implementation: 
	 * Optimized Implementation for all 6 possible Cases!  
	 * @param items  the Array to sort
	 * @param start  the Index to contain the smallest Value  
	 * @param middle the Index to contain the middle   Value 
	 * @param stop   the Index to contain the largest  Value 
	 */
	final static public void INDEX_THREE(final float[] items, final int[] index, final int start, final int middle, final int stop) { 
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
	 * Precondition: index needs to be filled with a valid Permutation! 
	 * 
	 * @param items the Array to be searched for, partially sorted 
	 * @param stop  the  last Index to sort, typically items.length-1
	 * @param start the first Index to sort, typically 0
	 * @return the index of an inner Value 
	 * that partitions the Array into smaller Values below and larger Values above. 
	 */
	protected static final int PARTITION(final float[] values, final int[] index, final int stop, final int start) { //, boolean asc) {
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
	  * Computes the sort-index permutation of {@code arr}, allocating its own index array.
	  * @param arr The Array to be ranked
	  * @param tmp a temporary Array passed for Effectiveness (Reuse)
	  * @param ret the Array to be returned passed for Effectiveness (Reuse)
	  * @return the Ranking of the given Array.
	  */
	final static public int[] INDEX(final float[] arr) {
		return INDEX(arr, new int[arr.length]); }

	/** 
	 * QuickSort Algorithm:
	 * Divide and Conquer Method to sort the Array descending:
	 * The Array is divided into two, of which both are recursively sorted. 
	 * Precondition: index needs to be filled with a valid Permutation! 
	 * 
	 * Implementation: 
	 * The larger Recursion is replaced by a Stack (which limits it's Size to Lb(items.length))
	 * the smaller is done directly (End-Recursion) 
	 * 
	 * @param items	the Array to sort 
	 */
	final static public int[] INDEX(final float[] items, final int[] index) {
		return INDEX(items, index, items.length, 0, null); }
	
	/** 
	 * QuickSort Algorithm:
	 * Divide and Conquer Method to sort the Array descending:
	 * The Array is divided into two, of which both are recursively sorted. 
	 * Precondition: index needs to be filled with a valid Permutation! 
	 * 
	 * Implementation: 
	 * The larger Recursion is replaced by a Stack (which limits it's Size to Lb(items.length))
	 * the smaller is done directly (End-Recursion) 
	 * 
	 * @param items	the Array to sort 
	 * @param stop   last Index to sort+1 (typically items.length)
	 */
	final static public int[] INDEX(final float[] items, final int[] index, int stop) {
		return INDEX(items, index, stop, 0, null); }
	
	/** 
	 * QuickSort Algorithm:
	 * Divide and Conquer Method to sort the Array descending:
	 * The Array is divided into two, of which both are recursively sorted. 
	 * Precondition: index needs to be filled with a valid Permutation! 
	 * 
	 * Implementation: 
	 * The larger Recursion is replaced by a Stack (which limits it's Size to Lb(items.length))
	 * the smaller is done directly (End-Recursion) 
	 * 
	 * @param items	the Array to sort 
	 * @param stop   last Index to sort+1 (typically items.length)
	 * @param start first Index to sort   (typically 0)
	 */
	final static public int[] INDEX(final float[] items, final int[] index, int stop, int start) {
		return INDEX(items, index, stop, start, null); }
	
	/** 
	 * QuickSort Algorithm:
	 * Divide and Conquer Method to sort the Array descending:
	 * The Array is divided into two, of which both are recursively sorted. 
	 * Precondition: index needs to be filled with a valid Permutation! 
	 * 
	 * Implementation: 
	 * The larger Recursion is replaced by a Stack (which limits it's Size to Lb(items.length))
	 * the smaller is done directly (End-Recursion) 
	 * 
	 * @param items	the Array to sort 
	 * @param stop   last Index to sort+1 (typically items.length)
	 * @param start first Index to sort   (typically 0)
	 * @param stack optional Stack to use
	 */
	final static public int[] INDEX(final float[] values, int[] index, int stop, int start, int[] stack) {
		if ((stack == null) || 
			(stack.length <  64))
			 stack = new int[64]; //use maximum Length right away...
		if ((index == null) ||
			(index.length <  stop))
			 index = new int[stop]; 
		if (index[0] == index[1]) //simple Test for no Permutation
			VectorInt.IDENTITY(index); 
		--stop; //to account for the Length which is always 1 larger! 
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
	 * This Algorithm is ideal for Tasks like ranking up to a certain Point,
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
	 * 
	 * Precondition: index needs to be filled with the identical Permutation! 
	 */
	final static public int GET_STATISTIC_POS(final float[] items, final int[] index, final int position) {
		return GET_STATISTIC_POS(items, index, 0, items.length - 1, position); }

	/**Creates the i-th Order Statistic in the Range between p and r
	 * using the QuickSort Algorithm.
	 * n-th Order Statistic = Maximum
	 * n/2  Order Statistic = Median
	 * 0-th Order Statistic = Minimum and so on.
	 * This Algorithm is ideal for Tasks like ranking up to a certain Point,
	 * because it needs only O(log N) Steps to find the Statistic.
	 * @return the Index of the Item with the given Rank
	 *  to retrieve the Value, simply use Items[Statistic(Items, p, r, i)]
	 *
	 * Precondition: index needs to be filled with the identical Permutation! 
	 */
	final static public int GET_STATISTIC_POS(final float[] items, final int[] index, final int start, final int stop, final int position) {
		if (start >= stop) {
			return start; }
		// TODO: LOGIC: PARTITION is declared as PARTITION(values, index, stop, start) (see the
		// 4-arg overload above), but this call passes (start, stop) - the two bounds are swapped
		// positionally. Since start < stop is guaranteed here, PARTITION's internal "stop" ends up
		// smaller than its internal "start", which almost always trips its `stop <= start + 1`
		// short-circuit and returns the wrong split point, corrupting the indexed order-statistic
		// (median/percentile) results computed through this overload.
		final int q = PARTITION(items, index, start, stop); //after this all Elements
		final int k = q - start + 1;
		if (position <= k) {
			return GET_STATISTIC_POS(items, index, start, q, position); } //
			return GET_STATISTIC_POS(items, index, q + 1, stop, position - k);
	}
	
	/** find the Nth largest, without altering an array (8.5) using linear Sweeps throgh the Data Set. 
	 * Due to a local Array used for Binning, 
	 * this Method uses considerably fewer Sweeps than GET_MEDIAN below!!!
	 * Additionally it is capable of calculating any Statistic! 
	 * 
	 * @param k
	 * @param n
	 * @param arr the Stream to search
	 * @return the desired Statistic 
	 * -Infinity for Statistics less than 1
	 * +Infinity for Statistics more than N
	 */
	final static public float GET_STATISTIC(final StreamIn_Float x, final int k) {
		final int M = 64; //Array Size = 2*SqRt(lb(n)) which is so slowly varying that it is chosen constant. 
		final int[]  iSelected=new int  [1+(M+2)];
		final float[] selected=new float[1+(M+2)];
		int kk=k;
		float hiBound=Float.POSITIVE_INFINITY;;
		float loBound=Float.NEGATIVE_INFINITY;;
		for (;;) { //main Loop until desired Element is located (to the given Accuracy) 
			//L.n("Loop #").l(++count);
			int nLo = 0; 
			int mm = 0;
			int nxtmm=M+1;
			float sum=0;
			int i = 1; x.reSet();
			for (float xx; (xx = x.nextFloat()) != IStreamIn_Float.EOS; ++i) { //Pass through the whole Stream
				if ((xx < loBound) || (xx > hiBound)) {
					continue; } //only consider Elements in current Brackets 
				++mm;
				if (xx == loBound) { //Ties in the lower Bracket... 
					++nLo; } //
				//Select M Elements in Range with equal Probability for each...
				//...even if you don't know how many will follow! 
				if (mm <= M) {
					selected[mm]=xx; 
				} else if (mm == nxtmm) {
					nxtmm=mm+mm/M;
					selected[1 + ((i+mm+kk) % M)]=xx; //% generates a random Number
				}
				sum += xx;
			}
			if (kk <= nLo) { //Desired Element is tied for lower bound
				return loBound;
			} else if (mm <= M) { //all Elements in Range were kept...
				HunterFloat.SORT_SHELL(selected, mm+1); //so search the Element directly in Memory 
				//HunterFloat.SORT(selected, 1, mm); //shell sort
				hiBound = selected[kk];
				return hiBound;
			}
			selected[M+1]=sum/mm; //augment selected Set by mean value (fixes Degeneracies)...
			HunterFloat.SORT_SHELL(selected, M+2); //... and sort it
			//HunterFloat.SORT(selected, 1, M+1);
			selected[M+2]=hiBound;
			for (int j=1; j<=M+2; j++) { //zero out the Counter Array
				iSelected[j]=0; }
			x.reSet(); 
			for (float xx; (xx = x.nextFloat()) != IStreamIn_Float.EOS;) { //Pass through the whole Array / Stream
				if ((xx < loBound) || (xx > hiBound)) {
					continue; } //only consider Elements in current Brackets 
				int jLo=0; 
				int jHi=M+2; //for each Element find it's Position by BiSection
				while (jHi-jLo > 1) {
					final int jMid=(jHi+jLo)>>1;
					if (xx >= selected[jMid]) {
						jLo=jMid;
					} else {
						jHi=jMid;
					} 
				}
				iSelected[jHi]++; //and increment the respective Counter
			}
			int j=1; //Now narrow down the Bounds to a single Bin...
			while (kk > iSelected[j]) { //...a Step of Order O(m)
				loBound=selected[j];
				kk -= iSelected[j++];
			}
			hiBound=selected[j];
		}
	}
	
	/** 
	 * non-destructive Search of the k-th Statistic (e.g. Median) 
	 * using multiple sequential Access (about Log(N) = 12+/-2 for 10.000 Items). 
	 * Typically the Search can also be terminated after a maximum Number of Iterations 
	 * to get the Median with an Accuracy of 1%.
	 * Alternatively the End Game can be calculated in Memory 
	 * by copying the Stream Elements between aMin and aMax into an Array  
	 *  
	 * @param x the Array to search the Median, unchanged
	 * @param start the first Index (inclusive)
	 * @param stop the last Index (exclusive)
	 * @return the Median of the given Array
	 */
	final static public float GET_MEDIAN(final StreamIn_Float x) {
		final float afac = 1.5f;
		final float amp  = 1.5f;
		final float x0 = x.nextFloat(); 
		final float x1 = x.nextFloat(); 
		float middle = (x1 + x0)/2; //just any Starting Values for Middle
		float tol = Math.abs(x1 - x0); //and for the Variance
		float aMin = Float.POSITIVE_INFINITY;;
		float aMax = Float.NEGATIVE_INFINITY;
		for(;;) { //loops about log(N) times
			//L.n("Loop #").l(++count);
			int numAbove = 0;
			int numBelow = 0;
			float xWeighed = 0;
			float sumWeights = 0;
			float xMinAbove = Float.POSITIVE_INFINITY;
			float xMaxAbove = Float.NEGATIVE_INFINITY;
			int numItems = 0; x.reSet(); 
			for (float xx; (xx = x.nextFloat()) != IStreamIn_Float.EOS;) { //the Data is used only here in this sweep
				++numItems; 
				if (xx > middle) {
					++numAbove;
					if (xMinAbove > xx) {
						xMinAbove = xx; } 
				} else if (xx < middle) {
					++numBelow;
					if (xMaxAbove < xx) { 
						xMaxAbove = xx; } 
				} else { //most improbable Case last...
					continue;
				}
				final float weight = 1/(tol + Math.abs(xx - middle));
				xWeighed += xx * weight;
				sumWeights += weight;
			}
			final float sTemp = (xWeighed / sumWeights) - middle;
			if ((numAbove - numBelow) > 1) { //subdivide further
				aMax = middle;
				float aNew = xMinAbove;
				if (sTemp >= 0) {
					aNew += sTemp * amp; }
				if (aNew > aMin)
					aNew = (middle + aMin)/2;
				tol = afac * Math.abs(aNew - middle);
				middle = aNew;
			} else if ((numBelow - numAbove) > 1) { //subdivide further
				aMin = middle;
				float aNew = xMaxAbove;
				if (sTemp <= 0) {
					aNew += sTemp * amp; }
				if (aNew < aMax)
					aNew = (middle + aMax)/2;
				tol = afac * Math.abs(aNew - middle);
				middle = aNew;
			} else if ((numItems & 1) == 0) { //even, middle the two
				if (numAbove > numBelow) {
					return 0.5f * (xMinAbove + middle); } 
				if (numAbove < numBelow) {
					return 0.5f * (xMaxAbove + middle); }
				return 0.5f * (xMinAbove + xMaxAbove); 
			} else { //odd, return directly
				if (numAbove > numBelow)
					return xMinAbove;
				if (numAbove < numBelow)
					return xMaxAbove;
				return middle;
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// searching
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Searches an ordered Array by BiSection	 */
	final static public int POSITION_IN_SORTED_ARRAY(final float[] xx, final double x) {
		return POSITION_IN_SORTED_ARRAY(xx, x, 0, xx.length, true, 0, xx.length, xx[xx.length-1] > xx[0], false); }
	
	/**Searches an ordered Array by BiSection	 */
	final static public int POSITION_IN_SORTED_ARRAY(final float[] xx, final double x, final boolean ascending) {
		return POSITION_IN_SORTED_ARRAY(xx, x, 0, xx.length, true, 0, xx.length, ascending, false); }
	
	/**Searches an ordered Array by BiSection	 */
	final static public int POSITION_IN_SORTED_ARRAY(final float[] xx, final double x, final int start, final int stop) {
		return POSITION_IN_SORTED_ARRAY(xx, x, start, stop, true, 0, xx.length, true); }
	
	/**Searches an ordered Array by BiSection	 */
	final static public int POSITION_IN_SORTED_ARRAY(final float[] xx, final double x, final int start, final int stop, final boolean enlarge) {
		return POSITION_IN_SORTED_ARRAY(xx, x, start, stop, true, 0, xx.length, enlarge); }
	
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
	final static public int POSITION_IN_SORTED_ARRAY(final float[] items, final double item, int lower, int upper, boolean interPol, int minPos, int maxPos, final boolean enlarge){
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
		return POSITION_IN_SORTED_ARRAY(items, item, lower, upper, interPol, minPos, maxPos, ascending, enlarge);
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
	final static public int POSITION_IN_SORTED_ARRAY(final float[] items, final double item, int lower, int upper, boolean interPol, final int minPos, final int maxPos, final boolean ascending, final boolean enlarge){
		if (lower < minPos)  
			lower = minPos; 
		if (upper > maxPos) 
			upper = maxPos; 
		--upper; 
		if (enlarge) {
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
		}
		double dHL = interPol ? items[upper] - items[lower] : 0; 
		double dLo = interPol ? item         - items[lower] : 0; 
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

	/**	sorts TWO (!) arrays arr and brr by quicksort method (8.2) according to the Order in arr.
	 * 
	 * @param n Number of Elements to sort
	 * @param arr Array to sort by 
	 * @param brr Array to sort in Parallel (can also be null)
	 */ 
	final static public void SORT(int n, float arr[], float brr[]) {
		final int NSTACK = 50; 
		final int M = 7;
		int i,ir=n,j,k,l=1;
		int jstack=0;
		float a,b;
		//Stack for Pivot Positions
		final int[] istack=new int[1+NSTACK];
		for (;;) {
			if (ir-l < M) {
				for (j=l+1;j<=ir;j++) {
					a=arr[j];
					b=brr[j];
					for (i=j-1;i>=1;i--) {
						if (arr[i] <= a) break;
						arr[i+1]=arr[i];
						brr[i+1]=brr[i];
					}
					arr[i+1]=a;
					brr[i+1]=b;
				}
				if (jstack == 0) {
					return; }
				ir=istack[jstack];
				l=istack[jstack-1];
				jstack -= 2;
			} else {
				k=(l+ir) >> 1;
				VectorFloat.SWAP_AT(arr, k, l+1);
				VectorFloat.SWAP_AT(brr, k, l+1);
				if (arr[l+1] > arr[ir]) {
					VectorFloat.SWAP_AT(arr, l+1, ir);
					VectorFloat.SWAP_AT(brr, l+1, ir);
				}
				if (arr[l] > arr[ir]) {
					VectorFloat.SWAP_AT(arr, l, ir);
					VectorFloat.SWAP_AT(brr, l, ir);
				}
				if (arr[l+1] > arr[l]) {
					VectorFloat.SWAP_AT(arr, l+1, l);
					VectorFloat.SWAP_AT(brr, l+1, l);
				}
				i=l+1;
				j=ir;
				a=arr[l];
				b=brr[l];
				for (;;) {
					do i++; while (arr[i] < a);
					do j--; while (arr[j] > a);
					if (j < i) break;
					VectorFloat.SWAP_AT(arr, i, j);
					VectorFloat.SWAP_AT(brr, i, j);
				}
				arr[l]=arr[j];
				arr[j]=a;
				brr[l]=brr[j];
				brr[j]=b;
				jstack += 2;
				if (jstack > NSTACK) {
					throw new RuntimeException("NSTACK too small in sort2.");} 
				if (ir-i+1 >= j-l) {
					istack[jstack]=ir;
					istack[jstack-1]=i;
					ir=j-1;
				} else {
					istack[jstack]=j-1;
					istack[jstack-1]=l;
					l=i;
				}
			}
		}
	}

	/**	replaces sorted array elements by their rank (14.6)
	 * Also respects Ties by giving all the same (fractional) Rank
	 * @param w The sorted Array to replace by it's Ranks 
	 * @param n the Length to use 
	 * @return the Size of Ties in this Rank 
	 */
	final static public double RANK_AT(final float w[], final int n) {
		double s=0;
		int j=1,ji,jt;
		while (j < n) {
			if (w[j+1] != w[j]) {
				w[j]=j;
				++j;
			} else {
				for (jt=j+1; (jt<=n) && (w[jt]==w[j]); jt++);
				final float rank=(j+jt-1)>>1;
				for (ji=j;ji<=(jt-1);ji++) {
					w[ji]=rank; } 
				final float t=jt-j;
				s += t*t*t-t;
				j=jt;
			}
		}
		if (j == n) {
			w[n]=n; } 
		return s; 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Member Variables
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Index at which the previous Item was found	 */
	final public boolean ascending;
	
	/**Index at which the previous Item was found	 */
	final public int minPos;
	
	/**Index at which the previous Item was found	 */
	final public int maxPos;
	
	/**Local Reference to the sorted Array being searched.	 */
	private final float[] mArray;
	
	/**Index at which the previous Item was found	 */
	public int lastPosition;
	
	/** Flag whether to use Interpolation for Searching (but not Bracketing!)	 */
	public boolean interpolate; 
	
	/**Constructor to hand over the Array to be searched.
	 * Determines the Sort Order	*/
	public HunterFloat(final float[] array) {	//don't compare a[0] with a[1], they may be the same.
		this (array, 0, array.length-1);
	}
	
	/**Constructor to hand over the Array to be searched.
	 * Determines the Sort Order	*/
	public HunterFloat(final float[] array, int minPos_, int maxPos_) {	//don't compare a[0] with a[1], they may be the same.
		mArray = array;
		this.minPos = minPos_; 
		this.maxPos = maxPos_; 
		ascending = mArray[minPos] < mArray[maxPos-1]; 
	}

	/** resets the Hunter so it considers the whole Interval 	*/
	public void reset() { lastPosition = -1; }

	/** gives the Hunter the Hint to search at the given Position	*/
	public void hint(final int position) { lastPosition = position; }
	
	/**Hunts the next Item down based on the last find.	 */
	public int hunt(final double item) {
		return POSITION_IN_SORTED_ARRAY(mArray, item, lastPosition-1, lastPosition+1, interpolate, minPos, maxPos, ascending); 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// static Testing and main() Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** tests different ways to calculate the Median	 */
	private static final void testMedian() {
		L.enter();
		final double dbl = IStreamIn_Float.EOS;  
		final float flt = IStreamIn_Float.EOS; 
		Assert.EQUALS(IStreamIn_Float.EOS, dbl); 
		Assert.EQUALS(IStreamIn_Float.EOS, flt);
		final RandomFast ran = new RandomFast(); ran.randomize(); 
		final float[] data = new float[99]; //must be odd!
		for (int i = data.length; --i >= 0;) {
			data[i] = i*0.01f; }
		testMedian(data);
		for (int i = data.length; --i >= 0;) {
			data[i] = ran.nextFloat(); }
		testMedian(data);
	}

	/** tests different ways to calculate the Median on the given Array	 */
	private static void testMedian(final float[] data) {
		L.enter();
		final StreamIn_Float stream = new ArrayStreamIn_Float(data); 
		final float  streamMedian = GET_MEDIAN(stream); 
		final double fastMedian2  = data[GET_STATISTIC_POS(data, (data.length/2)+1, 0, data.length-1)]; 
		final double fastMedian3  = GET_MEDIAN_FAST(data); 
		final double exactMedian  = GET_MEDIAN(data); 
		final double triMedian    = GET_TRI_MEDIAN(data); 
		final double percentile50 = data[GET_PERCENTILE_POS(data, 50)]; 
		L.n("Stream Median=").l(streamMedian); 
		L.n("Fast  Median2=").l(fastMedian2); 
		L.n("Fast  Median3=").l(fastMedian3); 
		L.n("Exact Median =").l(exactMedian); 
		L.n("50.Percentile=").l(percentile50);
		L.n("Tri - Median =").l(triMedian);
		//Assert.EQUALS(median1, median2); //only with odd Array Sizes!
		Assert.EQUALS(streamMedian, exactMedian); 
		//Assert.EQUALS(median1, median3); //off by one rounding Error here!

		//Demonstrate Robustness of the Statistic Method against out of Range Arguments
		Assert.EQUALS(testStatistic(data, -10), testStatistic(data, 1));
		testStatistic(data, 2);
		testStatistic(data, data.length-1);
		Assert.EQUALS(testStatistic(data, data.length), testStatistic(data, data.length+10));
	}

	private static double testStatistic(final float[] data, final int stat) {
		final int minPos = GET_STATISTIC_POS(data, stat);
		L.n("Statistic ").l(stat).l("=a[").l(minPos).l("]=").l(data[minPos]);
		return minPos; 
	}
	
	/** tests the sweeping Statistic Method	 */
	private static final void testGetStatistic() {
		L.enter();
		final int NP = 1000; 
		final float[] a=new float[1+NP];//VectorFloat.randomized(1+NP);
		final float[] b=new float[1+NP];
		RandomFast.STREAM.fillArray(a); 
		
		L.n("original array:").l(a);
		Assert.EQUALS(IOrdered.ORDER_NONE, GET_ORDER(a, 1, a.length));
		final StreamIn_Float stream = new ArrayStreamIn_Float(a);
		/*
		stream.nextFloat(); //start at 1 
		try {
			stream.mark(Long.MAX_VALUE);
		} catch (Exception ignored) {
			L.n(ignored);
		}
		*/
		L.n("Median").l(GET_STATISTIC(stream, (NP >> 1)+1)); // 
		L.n("Median").l(GET_MEDIAN(stream)); // 
		for (int i=1; i<=NP; i++) {
			final float b1 = a[GET_STATISTIC_POS(a, i, 0, NP)];
			final float b2 =   GET_STATISTIC(stream, i); //  
			b[i]=b2; 
			Assert.EQUALS(b1, b2); 
		}
		L.n("sorted array by selecting each Statistic:").l(b);
		Assert.EQUALS(IOrdered.ORDER_ASC, 
			Math.min (IOrdered.ORDER_ASC, GET_ORDER(b, 1, b.length)));
	}
	
	protected static final float[][] Permutations3 = {
			{1,2,3}, {1,3,2}, {2,3,1}, {2,1,3}, {3,1,2}, {3,2,1} };
	
	private static final void testSortThree() {
		for(int i = Permutations3.length; --i >= 0;) {
			final float[] arr = Permutations3[i]; 
			SORT_THREE(arr,0,1,2); 
			Assert.EQUALS(IOrdered.ORDER_ASC_STRICT, GET_ORDER(arr)); 
		}
	}
	
	private static final void testPermuteAt() {
		L.enter();
		final float[] test   = new float[15];
		final float[] sorted = new float[test.length];
		int  [] index  = null; 
		for (int i = 9; --i >= 0;) {
			VectorFloat.RANDOMIZE_AT_1_1(test);
			index = INDEX(test, index); 
			PERMUTE(test, index, sorted); 
			Assert.EQUALS(IOrdered.ORDER_ASC, 
				Math.min (IOrdered.ORDER_ASC, GET_ORDER(sorted)));
			PERMUTE_AT(test, index);
			Assert.EQUALS(test, sorted); 
			VectorInt.NEG_AT(index);
		}
	}
	
	/** tests the sweeping Statistic Method	 */
	private static final void testSort(boolean useShellSort) {
		L.enter(); 
		final int NP = 10000000; //10.000.000 takes 4secs
		final float[] a=VectorFloat.RANDOMIZED(1+NP);
		L.n("original array:").l(a);
		Assert.EQUALS(IOrdered.ORDER_NONE, GET_ORDER(a, 1, a.length));
		for(int k = 4; --k >= 0; ) {
			VectorFloat.RANDOMIZE_AT(a); 
			for (int i = 10; --i > 0; ) {
				L.timer(null); 
				if(useShellSort)
					HunterFloat.SORT_SHELL(a, a.length-i);
				else
					HunterFloat.SORT(a, a.length-i, 0);
				L.timer("Duration of Sort#"+i, 1);
				L.n("partially sorted array by Shell:").l(a);
				Assert.EQUALS(IOrdered.ORDER_ASC, 
					Math.min (IOrdered.ORDER_ASC, 
							GET_ORDER(a, 1, a.length-i))); 
			}
		}
	}
	
	/** tests the sweeping Statistic Method	 */
	private static final void testIndex() {
		L.enter(); 
		final int NP = 1000000; //10.000.000 takes 4secs
		final float[] a=VectorFloat.RANDOMIZED(NP);
		int  [] ndx= null; //new int[a.length];
		L.n("original array:").l(a);
		Assert.EQUALS(IOrdered.ORDER_NONE, GET_ORDER(a, 1, a.length));
		for(int k = 4; --k >= 0; ) {
			VectorFloat.RANDOMIZE_AT(a); 
			for (int i = 10; --i > 0; ) {
				L.timer(null); 
				ndx = HunterFloat.INDEX(a, ndx, a.length-i, 0);
				L.timer("Duration of Sort#"+i, 1);
				L.n("partially sorted array by Shell:").l(a);
				for(int j = a.length-i; --j > 0;) 
					if(a[ndx[j]] < a[ndx[j-1]])
						throw new RuntimeException(); 
			}
		}
	}
	
	/** tests all Methods of this Class 	 */
	final static public void testIt() throws Exception {
		testIndex();
		testSortThree(); 
		testSort(false);
		testPermuteAt(); 
		testMedian();
		testGetStatistic();
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws Exception {
		testIt();
	}

}
