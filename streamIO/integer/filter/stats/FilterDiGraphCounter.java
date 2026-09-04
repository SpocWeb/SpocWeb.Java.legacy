/*
 * Created on 03.12.2004
 * This DiGraphCounter counts the Occurrences of Byte Pairs in a Stream. 
 *
 */
package streamIO.integer.filter.stats;

import java.io.IOException;
import java.io.OutputStream;

import math.vector.VectorInt;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.filter.FilterOutByte;

/**
 * This DiGraphCounter counts the Occurrences of Byte Pairs in a Stream. 
 * It does this by incrementing the Counts in a 2D Byte Sized Array for Simplicity.
 * Additionally several Methods are given to aggregate Counts e.g. on WhiteSpace
 * or on Indifference to Character Case.
 * @see streamIO.integer.filter.stats.FilterByteBag 
 * @see streamIO.integer.filter.stats.FilterTriGraphCounter
 * @see streamIO.object.enumer.container.Bag
 * @author heuerm
 */
public class FilterDiGraphCounter 
extends FilterOutByte {

	/**
	 * relative Frequencies of German Character Pairs normed to 999
	 */
	private static final int[][] DI_GRAPH_RELATIVE_GERMAN = {
			{  12, 48,  42,  17,  99, 23, 46,  31,   8, 2, 11,  92,  43, 158,   0,  6, 0,  79,  82,  71, 116,  3,  5, 0, 2,  3 },
			{  84,  5,   0,   5, 533,  0, 16,   5,  63, 0,  5,  47,   0,   5,  42,  0, 0,  47,  32,	 21,  74,  0,  5, 0, 5,  5 },
			{   8,  0,   0,   8,   4,  0,  0, 907,   4, 0, 52,   4,   0,   0,   8,  0, 0,   0,   4,   0,   0,  0,  0, 0, 0,  0 },
			{ 112,  6,   2,  27, 470,  6,  8,   4, 192, 2,  6,  10,   8,  12,  19,  6, 0,  21,  23,  12,  33,  6,  8, 0, 0,  6 },
			{  15, 26,  14,  29,  13, 15, 29,  33, 110, 2, 11,  36,  32, 228,   3,  7, 1, 233,  80,  32,  21,  8, 13, 1, 1,  6 },
			{ 116, 12,   0,  55, 152, 73, 18,   6,  43, 0,  6,  31,   6,  12,  55,  6, 0, 110,  12, 122, 146,  6,  6, 0, 0,  6 },
			{  65, 10,   0,  39, 475,  6, 10,  10,  62, 3, 10,  29,  10,  16,  20,  3, 0,  46,  58,  58,  36, 13, 10, 0, 0, 10 },
			{ 165, 10,   2,  33, 240,  5, 10,   7,  54, 2,  7,  59,  26,  45,  43,  2, 0,  88,  26, 111,  26, 10, 21, 0, 0,  7 },
			{   9,  9,  98,  26, 211,  6, 49,  16,   1, 1, 16,  32,  35, 218,  26,  3, 0,  22, 102, 101,   4,  7,  1, 0, 0,  6 },
			{ 360,  0,   0,   0, 359,  0,  0,   0,   0, 0,  0,   0,   0,   0,  80,  0, 0,   0,   0,   0, 200,  0,  0, 0, 0,  0 },
			{ 176,  7,   0,  14, 176,  7,  7,   7,  48, 0,  7,  68,   7,   7, 162,  7, 0,  88,  34,  95,  61,  7,  7, 0, 0,  7 },
			{ 128, 20,   6,  40, 184, 14, 17,   6, 173, 3, 20, 119,   8,  11,  40,  6, 0,   6,  62,  77,  37,  8,  6, 0, 0,  8 },
			{ 154, 23,   4,  31, 191, 15, 15,  12, 168, 8, 12,  15,  88,  12,  58, 27, 0,   8,  38,  31,  54, 15, 12, 0, 0,  8 },
			{  69, 23,   5, 191, 124, 19, 96,  17,  66, 5, 26,  10,  23,  44,  18, 10, 0,  10,  75,  60,  34, 18, 30, 0, 0, 26 },
			{  10, 27,  50,  23,  83, 20, 17,  30,   3, 3, 10, 103,  57, 214,   3, 20, 0, 167,  63,  30,  10, 10, 23, 0, 3, 20 },
			{ 173,  0,   0,  33, 109, 65,  0,  22,  44, 0,  0,  44,   0,   0, 119, 54, 0, 248,  11,  33,  44,  0,  0, 0, 0,  0 },
			{   0,  0,   0,   0,   0,  0,  0,   0,   0, 0,  0,   0,   0,   0,   0,  0, 0,   0,   0,   0, 999,  0,  0, 0, 0,  0 },
			{ 105, 33,  12,  89, 148, 24, 36,  25,  69, 5, 31,  24,  27,  41,  40, 12, 0,  20,  72,  65,  63, 16, 23, 0, 0, 19 },
			{  53, 15, 130,  29, 145, 10, 19,  13,  95, 3, 16,  13,  18,  10,  41, 32, 0,  12, 111, 171,  22, 13, 15, 0, 3, 10 },
			{  93, 13,   2,  42, 303,  8, 17,  23,  97, 3,  7,  18,  15,  15,  30,  5, 0,  51,  83,  38,  43, 13, 35, 0, 2, 43 },
			{   7, 19,  38,  12, 187, 65, 19,  10,   5, 0,  7,  17,  50, 286,   0, 12, 0,  79, 115,  55,   2,  7,  5, 0, 0,  2 },
			{  33,  0,   0,   0, 402,  0,  0,   0,  98, 0,  0,   0,   0,   0, 466,  0, 0,   0,   0,   0,   0,  0,  0, 0, 0,  0 },
			{ 231,  0,   0,   0, 325,  0,  0,   0, 245, 7,  0,   0,   0,   7, 116,  0, 0,   0,   7,   0,  61,  0,  0, 0, 0,  0 },
			{   0,  0,   0,   0,   0,  0,  0,   0, 333, 0,  0,   0,   0,   0, 333,  0, 0,   0,   0, 333,   0,  0,  0, 0, 0,  0 },
			{   0,  0,   0,   0, 249,  0,  0,   0,   0, 0,  0, 250, 250,   0,   0,  0, 0,   0, 250,   0,   0,  0,  0, 0, 0,  0 },
			{  35,  9,   0,   9, 245,  0,  9,   0,  96, 0,  9,  18,   9,   0,  18,  0, 0,   0,   9,  61, 375,  9, 79, 0, 0,  9 } };

	/**
	 * relative Frequencies of English Character Pairs normed to 999
	 */
	private static final int[][] DI_GRAPH_RELATIVE_ENGLISH = {
			{    1, 40,  48,  19,   0,  12,  22,   0,  20,  0, 12,  96, 22, 214,   2,  38,  1, 126,  83, 154,  15, 30,  9,  0,  34,   1},   
			{   49,  0,   0,   0, 358,   0,   0,   0,  37, 12,  0, 130,  6,   0,  68,   0,  0,  37,  31,   0, 154,  0,  0,  0, 117,   0},   
			{  138,  0,  38,   0, 171,   3,   0, 144,  47,  0, 25,  50,  0,   0, 183,   3,  0,  22,   3, 119,  50,  0,  3,  0,   0,   0},   
			{  123, 49,  11,  27, 107,  33,   6,   8, 155,  3,  0,  19, 25,  14, 101,  19,  3,  27,  88, 107,  22, 11, 25,  0,  16,   0},   
			{  106,  9,  52,  87,  32,  19,  16,  12,  32,  1,  2,  37, 35,  98,  38,  26, 11, 124, 117,  65,   6, 13, 33, 14,  14,   0},   
			{   92,  9,  40,   4, 110,  61,   4,  26,  92,  4,  0,  44, 13,   9, 167,  13,  0,  18,  35, 184,  48,  4, 18,  0,   4,   0},   
			{   69, 12,   6,   6, 201,  19,   6, 101,  62,  0,  0,  25,  6,  19, 145,   6,  0, 132,  44,  82,  40,  0, 12,  0,   6,   0},
			{  162,  2,   4,   2, 486,   4,   0,  10, 140,  0,  0,   6,  2,   4,  90,   2,  0,  16,   6,  43,   4,  0, 14,  0,   2,   0},   
			{   25, 10,  76,  22,  52,  38,  14,   0,   0,  0, 11,  54, 44, 235,  88,   4,  0,  29, 147, 122,   0, 20,  1,  1,   0,   6},   
			{    0,  0,   0,   0, 200,   0,   0,   0,   0,  0,  0,   0,  0,   0, 400,   0,  0,   0,   0,   0, 399,  0,  0,  0,   0,   0},   
			{    0,  0,   0,   0, 547,   0,   0,   0, 157,  0,  0,   0,  0,  59,  59,   0,  0,   0,  39,  20,   0,  0, 59,  0,  59,   0},   
			{   84, 17,  20,  70, 180,  12,   2,   0, 141,  2,  7, 136, 10,   2,  70,   5,  5,   5,  30,  47,  20,  5, 12,  0, 117,   0},   
			{  249, 40,   4,   9, 213,   0,   0,   4, 116,  0,  0,   0, 22,  13, 124,  71,  0,   0,  27,  27,  58,  0,  9,  0,  13,   0},   
			{   75, 10,  43, 163,  89,  11, 104,  12,  52,  4,  4,  14, 10,  12,  90,  10,  0,   7,  71, 153,  17,  6, 21,  1,  20,   0},   
			{   11, 23,  23,  20,   4, 119,   4,   4,  16,  0,  6,  21, 55, 183,  29,  36,  0, 142,  47,  67, 121, 16, 45,  0,   5,   2},   
			{   92,  4,   0,   0, 174,   0,   0,  31,  35,  0,  0, 127,  0,   0, 122, 114,  0, 182,  13,  61,  31,  0,  4,  0,   9,   0},   
			{    0,  0,   0,   0,   0,   0,   0,   0,   0,  0,  0,   0,  0,   0,   0,   0,  0,   0,   0,   0, 999,  0,  0,  0,   0,   0},   
			{   95,  7,  23,  27, 244,  10,  10,   5, 127,  2, 18,  20, 25,  20,  90,  13,  0,  30,  65, 105,  10,  8, 17,  0,  28,   0},   
			{  113, 20,  32,   9, 127,  20,   9,  46,  64,  0,  3,   9, 21,  29, 108,  36,  3,   9,  62, 183,  46,  3, 41,  0,   6,   0},   
			{   58, 15,   6,   9,  98,   5,   1, 328, 134,  0,  0,  12, 15,   8, 116,   8,  0,  31,  34,  55,  23,  4, 17,  0,  22,   0},   
			{   58, 16,  55,  36,  36,   3,  39,   6,  16,  0,  0,  91, 29, 107,   6,  55,  0, 158, 136, 146,   0,  0,  0,  0,   3,   3},   
			{  160,  0,   0,   0, 562,   0,   0,   0, 202,  0,  0,   0,  0,   0,  64,   0,  0,   0,   0,   0,   0,  0,  0, 11,   0,   0},   
			{  157,  0,  15,  20, 148,   5,   0, 235, 181,  0,  0,  20,  5,  49,  84,  10,  0,   5,  15,  30,   5,  5, 10,  0,   0,   0},   
			{  150,  0, 249,   0,  50,   0,   0,   0, 200,  0,  0,   0,  0,   0,  50, 200,  0,   0,   0,  50,  50,  0,  0,  0,   0,   0},   
			{   58, 58,  53,  21,  64,  16,  27,  27,  96,  0,  0,  32, 21,  16, 149,  37,  0,  27,  90, 112,   5, 16, 74,  0,   0,   0},
			{    0,  0,   0,   0, 555,   0,   0,   0, 222,  0,  0, 111,  0,   0,   0,   0,  0,   0,   0,   0,   0,  0,  0,  0,   0, 111},};
	
	///////////////////////////////////////////////////////////////////////////////////////
	/// Instance Members 
	///////////////////////////////////////////////////////////////////////////////////////
	
	/** enlarged dynamically */
	protected int[][] counters;
	
	/** cache for the last Character */
	protected int lastChar;
	
	/**
	 *  
	 */
	public FilterDiGraphCounter() {
		this(FilterByteBag.MAX_CHAR_DEFAULT);
	}

	/**
	 * @param streamOut
	 */
	public FilterDiGraphCounter(final IStreamOutByte streamOut) {
		this(streamOut, FilterByteBag.MAX_CHAR_DEFAULT);
	}

	/**
	 * @param streamOut
	 */
	public FilterDiGraphCounter(final OutputStream streamOut) {
		this(streamOut, FilterByteBag.MAX_CHAR_DEFAULT);
	}

	/**
	 *  
	 */
	public FilterDiGraphCounter(final int _initialSize) {
		super();
		this.counters = new int[_initialSize][_initialSize];
	}

	/**
	 * @param streamOut
	 */
	public FilterDiGraphCounter(final IStreamOutByte streamOut, final int _initialSize) {
		super(streamOut);
		this.counters = new int[_initialSize][_initialSize];
	}

	/**
	 * @param streamOut
	 */
	public FilterDiGraphCounter(final OutputStream streamOut, final int _initialSize) {
		super(streamOut);
		this.counters = new int[_initialSize][_initialSize];
	}

	////////////////////////////////////////////////////////////////////////////////
	//  Interface StreamOutByte: abstract Methods
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Writes the specified byte to this output stream. The general contract for
	 * write is that one byte is written to the output stream. The byte to be
	 * written is the eight low-order bits of the argument b. The 24 high-order
	 * bits of b are ignored.
	 * 
	 * Subclasses of OutputStream must provide an implementation for this
	 * method.
	 * 
	 * @param b -
	 *            the byte.
	 * @throws IOException -
	 *             if an I/O error occurs. In particular, an IOException may be
	 *             thrown if the output stream has been closed.
	 */
	public void write(int b) throws IOException {
		//if (b < 0)
		//	return; Exception is thrown below...
		if (b >= counters.length) {
			int[][] tmp = new int[b + 1][b + 1];
			for (int i = counters.length; --i >= 0;)
				System.arraycopy(counters[i], 0, tmp[i], 0, counters.length);
			counters = tmp;
		}
		++counters[lastChar][b];
		super.write(lastChar = b);
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// static testing & main Methods
	///////////////////////////////////////////////////////////////////////////
	
	public static void main(final String[] args) {
		testIt();
	};

	/** checks whether the Counts in the Tables add up to the given Sums	 */
	public static void testIt() {
		testTable(DI_GRAPH_RELATIVE_ENGLISH, 999);
		testTable(DI_GRAPH_RELATIVE_GERMAN, 999);
	};
	
	/**
	 * tests whether the Counts in the Table add up to the given Sum
	 * @param table
	 * @param sum
	 */
	private static void testTable(final int[][] table, final int sum) {
		for (int i = table.length; --i >= 0;) {
			if (table[i].length != table.length)
				//throw new
				// RuntimeException("Row["+i+"].length="+table[i].length);
				System.out.println("Row[" + i + "].length=" + table[i].length);
			final long rowSum = VectorInt.SUM(table[i]);
			if (sum != rowSum)
				//throw new RuntimeException("Row["+i+"]="+rowSum);
				System.out.println("Row[" + i + "]=" + rowSum);
			/*
			 * final long colSum = MatrixInt.COL_SUM(table, i); if (sum !=
			 * colSum) //throw new RuntimeException("Col["+i+"]="+colSum);
			 * System.out.println("Col["+i+"]="+colSum);
			 */
		}
	}
	
}