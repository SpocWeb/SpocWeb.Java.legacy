/*
 * Created on 02.04.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * adds some Methods to save double copying of Arrays. 
 * 
 * Design Decisions / Implementation Details:
 * @see streamIO.integer.StreamOutInstantiator These Methods don't make much sense here 
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
 * tags: [code/stream_io, code/stream_input, code/stream_output, code/struct]
 * concepts: [Primitive and Structured Stream I/O Core Abstractions]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public interface IStreamIn_StructX 
extends IStreamIn_Struct {

	/** return an Array of int Values read from the Stream
	 * @return an Array of int Values read from the Stream
	 */
	public int nextInts(final int[] ret); 
	
	/** return an Array of int Values read from the Stream
	 * @return an Array of int Values read from the Stream
	 */
	public int nextInts(final int[] ret, final int stop); 
	
	/** return an Array of int Values read from the Stream
	 * @return an Array of int Values read from the Stream
	 */
	public int nextInts(final int[] ret, final int stop, final int start); 
	
	///////////////////////////////////////////////////////////////////////////
	
	/** return an Array of short Values read from the Stream
	 * @return an Array of short Values read from the Stream
	 */
	public int nextShorts(final short[] ret); 
	
	/** return an Array of short Values read from the Stream
	 * @return an Array of short Values read from the Stream
	 */
	public int nextShorts(final short[] ret, final int stop); 
	
	/** return an Array of short Values read from the Stream
	 * @return an Array of short Values read from the Stream
	 */
	public int nextShorts(final short[] ret, final int stop, final int start); 
	
	///////////////////////////////////////////////////////////////////////////
	
	/** return an Array of long Values read from the Stream
	 * @return an Array of long Values read from the Stream
	 */
	public int nextLongs(final long[] ret); 
	
	/** return an Array of long Values read from the Stream
	 * @return an Array of long Values read from the Stream
	 */
	public int nextLongs(final long[] ret, final int stop); 
	
	/** return an Array of int Values read from the Stream
	 * @return an Array of int Values read from the Stream
	 */
	public int nextLongs(final long[] ret, final int stop, final int start); 
	
	///////////////////////////////////////////////////////////////////////////
	
	/** return an Array of float Values read from the Stream
	 * @return an Array of float Values read from the Stream
	 */
	public int nextFloats(final float[] ret); 
	
	/** return an Array of float Values read from the Stream
	 * @return an Array of float Values read from the Stream
	 */
	public int nextFloats(final float[] ret, final int stop); 
	
	/** return an Array of float Values read from the Stream
	 * @return an Array of float Values read from the Stream
	 */
	public int nextFloats(final float[] ret, final int stop, final int start); 
	
	///////////////////////////////////////////////////////////////////////////
	
	/** return an Array of double Values read from the Stream
	 * @return an Array of double Values read from the Stream
	 */
	public int nextDoubles(final double[] ret); 
	
	/** return an Array of double Values read from the Stream
	 * @return an Array of double Values read from the Stream
	 */
	public int nextDoubles(final double[] ret, final int stop); 
	
	/** return an Array of int Values read from the Stream
	 * @return an Array of int Values read from the Stream
	 */
	public int nextDoubles(final double[] ret, final int stop, final int start); 
	
	///////////////////////////////////////////////////////////////////////////
	
	/** return an Array of Strings read from the Stream
	 * @return an Array of Strings read from the Stream
	 */
	public int nextStrings(final String[] ret); 
	
	/** return an Array of Strings read from the Stream
	 * @return an Array of Strings read from the Stream
	 */
	public int nextStrings(final String[] ret, final int stop); 
	
	/** return an Array of Strings read from the Stream
	 * @return an Array of Strings read from the Stream
	 */
	public int nextStrings(final String[] ret, final int stop, final int start); 
	
	///////////////////////////////////////////////////////////////////////////
	
	/** return an Array of Objects read from the Stream
	 * @return an Array of Objects read from the Stream
	 */
	public int nextItems(final Object[] ret); 
	
	/** return an Array of Objects read from the Stream
	 * @return an Array of Objects read from the Stream
	 */
	public int nextItems(final Object[] ret, final int stop); 
	
	/** return an Array of Objects read from the Stream
	 * @return an Array of Objects read from the Stream
	 */
	public int nextItems(final Object[] ret, final int stop, final int start); 
	
	///////////////////////////////////////////////////////////////////////////
	
}
