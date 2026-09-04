/*
 * Created on 03.12.2004
 *
 * This DiGraphCounter counts the Occurrences of Byte Triples in a Stream. 
 */
package streamIO.integer.filter.stats;

import java.io.IOException;
import java.io.OutputStream;

import streamIO.integer.IStreamOutByte;
import streamIO.integer.filter.FilterOutByte;

/**
 * This DiGraphCounter counts the Occurrences of Byte Triples in a Stream. 
 * It does this by incrementing the Counts in a 3D Byte Sized Array for Simplicity.
 * Additionally several Methods are given to aggregate Counts e.g. on WhiteSpace
 * or on Indifference to Character Case.
 * @see streamIO.integer.filter.stats.FilterByteBag 
 * @see streamIO.integer.filter.stats.FilterTriGraphCounter
 * @see streamIO.object.enumer.container.Bag
 * @author heuerm
 *
 */
public class FilterTriGraphCounter 
extends FilterOutByte {

	///////////////////////////////////////////////////////////////////////////////////////
	/// Instance Members 
	///////////////////////////////////////////////////////////////////////////////////////

	/** enlarged dynamically */
	int[][][] counters;

	/** cache for the last Character, initialize with White Space */
	int Char1;

	/** cache for the second last Character, initialize with White Space  */
	int Char2;

	/**
	 *  
	 */
	public FilterTriGraphCounter() {
		this(FilterByteBag.MAX_CHAR_DEFAULT);
	}

	/**
	 * @param streamOut
	 */
	public FilterTriGraphCounter(IStreamOutByte streamOut) {
		this(streamOut, FilterByteBag.MAX_CHAR_DEFAULT);
	}

	/**
	 * @param streamOut
	 */
	public FilterTriGraphCounter(OutputStream streamOut) {
		this(streamOut, FilterByteBag.MAX_CHAR_DEFAULT);
	}

	/**
	 *  
	 */
	public FilterTriGraphCounter(int _initialSize) {
		super();
		this.counters = new int[_initialSize][_initialSize][_initialSize];
	}

	/**
	 * @param streamOut
	 */
	public FilterTriGraphCounter(IStreamOutByte streamOut, int _initialSize) {
		super(streamOut);
		this.counters = new int[_initialSize][_initialSize][_initialSize];
	}

	/**
	 * @param streamOut
	 */
	public FilterTriGraphCounter(OutputStream streamOut, int _initialSize) {
		super(streamOut);
		this.counters = new int[_initialSize][_initialSize][_initialSize];
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
			int[][][] tmp = new int[b + 1][b + 1][b + 1];
			for (int i = counters.length; --i >= 0;)
				for (int j = counters.length; --j >= 0;)
					System.arraycopy(counters[i][j], 0, tmp[i][j], 0, counters.length);
			counters = tmp;
		}
		++counters[Char2][Char1][b];
		Char2 = Char1; 
		super.write(Char1 = b);
	}

}
