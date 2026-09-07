/*
 * Created on 02.04.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;

/**
 * Defines array-writing overloads — full array, or an inclusive/exclusive index range — for
 * shorts, ints, longs, floats, doubles, Strings, and Objects, returning an
 * {@link IStreamOutStruct} (or an item count for Objects) to allow chaining.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * <!-- docstate
 * tags: [code/stream_io, code/stream_input, code/stream_output, code/struct]
 * concepts: [Primitive and Structured Stream I/O Core Abstractions]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public interface IStreamOutStructArrays {
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Writes the whole short array in structured Manner to the Stream.
	 * @param values the Values to write
	 * @return this Stream to allow for Concatenation
	 */
	public abstract IStreamOutStruct addShorts(final short[] values);
	
	/** writes the given Array in structured Manner to the Stream 
	 * @param values the Values to write
	 * @param stop   the  last Value to write (exclusive)
	 * @param start  the first Value to write (inclusive)
	 * @return this Stream to allow for Concatenation 
	 */
	public abstract IStreamOutStruct addShorts(final short[] values,
			final int stop);
	
	/** writes the given Array in structured Manner to the Stream 
	 * @param values the Values to write
	 * @param stop   the  last Value to write (exclusive)
	 * @param start  the first Value to write (inclusive)
	 * @return this Stream to allow for Concatenation 
	 */
	public abstract IStreamOutStruct addShorts(final short[] values,
			final int stop, int start);
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Writes the whole int array in structured Manner to the Stream.
	 * @param values the Values to write
	 * @return this Stream to allow for Concatenation
	 */
	public abstract IStreamOutStruct addInts(final int[] values);
	
	/** writes the given Array in structured Manner to the Stream 
	 * @param values the Values to write
	 * @param stop   the  last Value to write (exclusive)
	 * @param start  the first Value to write (inclusive)
	 * @return this Stream to allow for Concatenation 
	 */
	public abstract IStreamOutStruct addInts(final int[] values, final int stop);
	
	/** writes the given Array in structured Manner to the Stream 
	 * @param values the Values to write
	 * @param stop   the  last Value to write (exclusive)
	 * @param start  the first Value to write (inclusive)
	 * @return this Stream to allow for Concatenation 
	 */
	public abstract IStreamOutStruct addInts(final int[] values,
			final int stop, int start);
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Writes the whole long array in structured Manner to the Stream.
	 * @param values the Values to write
	 * @return this Stream to allow for Concatenation
	 */
	public abstract IStreamOutStruct addLongs(final long[] values);
	
	/** writes the given Array in structured Manner to the Stream 
	 * @param values the Values to write
	 * @param stop   the  last Value to write (exclusive)
	 * @param start  the first Value to write (inclusive)
	 * @return this Stream to allow for Concatenation 
	 */
	public abstract IStreamOutStruct addLongs(final long[] values,
			final int stop);
	
	/** writes the given Array in structured Manner to the Stream 
	 * @param values the Values to write
	 * @param stop   the  last Value to write (exclusive)
	 * @param start  the first Value to write (inclusive)
	 * @return this Stream to allow for Concatenation 
	 */
	public abstract IStreamOutStruct addLongs(final long[] values,
			final int stop, int start);
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Writes the whole float array in structured Manner to the Stream.
	 * @param values the Values to write
	 * @return this Stream to allow for Concatenation
	 */
	public abstract IStreamOutStruct addFloats(final float[] values);
	
	/** writes the given Array in structured Manner to the Stream 
	 * @param values the Values to write
	 * @param stop   the  last Value to write (exclusive)
	 * @param start  the first Value to write (inclusive)
	 * @return this Stream to allow for Concatenation 
	 */
	public abstract IStreamOutStruct addFloats(final float[] values,
			final int stop);
	
	/** writes the given Array in structured Manner to the Stream 
	 * @param values the Values to write
	 * @param stop   the  last Value to write (exclusive)
	 * @param start  the first Value to write (inclusive)
	 * @return this Stream to allow for Concatenation 
	 */
	public abstract IStreamOutStruct addFloats(final float[] values,
			final int stop, int start);
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Writes the whole double array in structured Manner to the Stream.
	 * @param values the Values to write
	 * @return this Stream to allow for Concatenation
	 */
	public abstract IStreamOutStruct addDoubles(final double[] values);
	
	/** writes the given Array in structured Manner to the Stream 
	 * @param values the Values to write
	 * @param stop   the  last Value to write (exclusive)
	 * @param start  the first Value to write (inclusive)
	 * @return this Stream to allow for Concatenation 
	 */
	public abstract IStreamOutStruct addDoubles(final double[] values,
			final int stop);
	
	/** writes the given Array in structured Manner to the Stream 
	 * @param values the Values to write
	 * @param stop   the  last Value to write (exclusive)
	 * @param start  the first Value to write (inclusive)
	 * @return this Stream to allow for Concatenation 
	 */
	public abstract IStreamOutStruct addDoubles(final double[] values,
			final int stop, int start);
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Writes the whole String array in structured Manner to the Stream.
	 * @param values the Values to write
	 * @return this Stream to allow for Concatenation
	 */
	public abstract IStreamOutStruct addStrings(final String[] values);
	
	/** writes the given Array in structured Manner to the Stream 
	 * @param values the Values to write
	 * @param stop   the  last Value to write (exclusive)
	 * @param start  the first Value to write (inclusive)
	 * @return this Stream to allow for Concatenation 
	 */
	public abstract IStreamOutStruct addStrings(final String[] values,
			final int stop);
	
	/** writes the given Array in structured Manner to the Stream 
	 * @param values the Values to write
	 * @param stop   the  last Value to write (exclusive)
	 * @param start  the first Value to write (inclusive)
	 * @return this Stream to allow for Concatenation 
	 */
	public abstract IStreamOutStruct addStrings(final String[] values,
			final int stop, int start);
	
	////////////////////////////////////////////////////////////////////////////////
	
	/** Writes the whole Object array in structured Manner to the Stream.
	 * @param values the Values to write
	 * @return the Number of Items written
	 */
	public abstract long addItems(final Object[] values);
	
	/** writes the given Array in structured Manner to the Stream 
	 * @param values the Values to write
	 * @param stop   the  last Value to write (exclusive)
	 * @param start  the first Value to write (inclusive)
	 * @return this Stream to allow for Concatenation 
	 */
	public abstract IStreamOutStruct addItems(final Object[] values,
			final int stop);
	
	/** writes the given Array in structured Manner to the Stream 
	 * @param values the Values to write
	 * @param stop   the  last Value to write (exclusive)
	 * @param start  the first Value to write (inclusive)
	 * @return this Stream to allow for Concatenation 
	 */
	public abstract IStreamOutStruct addItems(final Object[] values,
			final int stop, int start);
	
}