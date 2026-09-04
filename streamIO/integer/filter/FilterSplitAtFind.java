package streamIO.integer.filter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.file.FileStreamOutByte;
import streamIO.integer.pipe.ByteStreamerThread;


/**
  * Title: FilterSplitAtFind<p>
  *
  * Purpose:
  * Filters a streamIO and ends it (-1) 
  * as soon as a certain String is found more often than the specified Number
  *
  * Design Decisions / Implementation Details:
  * The Counter is integrated into this Class to save coupling Streams at different Levels.
  * Since Files are not only separated AFTER a certain String, 
  * but also possibly BEFORE or WITHIN the streamIO, thus it must read ahead. 
  * Searching is done primitive, using an O(N*M) Algorithm.
  * Can be improved later to use one of the more sophisticated Algorithms like Boyer-Moore. 
  *
  * Known SubClasses: <none>
  *
  * otherwise related Classes: 
  * @see streamIO.integer.filter.LimitedSizeInputStream
  * @see streamIO.integer.filter.LimitedSizeOutputStream
  * @see streamIO.detector.LimitedSizeStreamOut
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	02-12-2003, 11:29 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class FilterSplitAtFind
extends FilterByte
{

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Separator to sep the Streams */
	private final String separator; 

	/** relative Position to the separator to separate the Streams */
	private final int breakPosition; 

	/** Number of Occurrences to separate the streamIO at */
	private final int maxFinds; 

	/** Buffer / Cache to read ahead the Streams, should be encapsulated in a byte Queue */
	private final int[] buffer; 

	/** Position to separate the Streams */
	private int bufferPosition; 

	/** Counter for placing the break in the streamIO 
	 * 'long' to prevent underrun 
	 */
	private long breakCountDown; 

	/** current Position in the Separator */
	private int posInSeparator; 

	/** current Position in the Separator */
	private int numFinds; 

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor for FilterSplitAtFind.
	 * @param streamOut
	 */
	public FilterSplitAtFind(final IStreamOutByte streamOut, final String separator_, final int breakPosition_, final int maxFinds_) { 
		super(streamOut); 
		this.maxFinds = maxFinds_; 
		this.separator = separator_; 
		this.breakPosition = breakPosition_; 
		this.buffer = new int[separator.length()];
	}

	/**
	 * Constructor for FilterSplitAtFind.
	 * @param streamOut
	 */
	public FilterSplitAtFind(final OutputStream streamOut, final String separator_, final int breakPosition_, final int maxFinds_) { 
		super(streamOut); 
		this.maxFinds = maxFinds_; 
		this.separator = separator_; 
		this.breakPosition = breakPosition_; 
		this.buffer = new int[separator.length()];
	}

	/**
	 * Constructor for FilterSplitAtFind.
	 * @param streamIn_
	 */
	public FilterSplitAtFind(final IStreamIn_Byte streamIn_, final String separator_, final int breakPosition_, final int maxFinds_
	) throws IOException { 
		super(streamIn_); 
		this.maxFinds = maxFinds_; 
		this.separator = separator_; 
		this.breakPosition = breakPosition_; 
		this.buffer = new int[separator.length()];
		for (int i = buffer.length; --i >= 0;) { //Assumption: File is at least as long as the Buffer!
			int val = streamIn_.read();
			matchesFullString(val); //although it is improbable that it already starts here!
			cache(val); //
		}
	}

	/**
	 * Constructor for FilterSplitAtFind.
	 * @param streamIn_
	 */
	public FilterSplitAtFind(final InputStream streamIn_, final String separator_, final int breakPosition_, final int maxFinds_
	) throws IOException { 
		super(streamIn_); 
		this.maxFinds = maxFinds_; 
		this.separator = separator_; 
		this.breakPosition = breakPosition_; 
		this.buffer = new int[separator.length()];
		for (int i = buffer.length; --i >= 0;) { //Assumption: File is at least as long as the Buffer!
			int val = streamIn_.read();
			matchesFullString(val); //although it is improbable that it already starts here!
			cache(val); //
		}
	}

	/** 
	 * primitive N*M String Searcher, 
	 * not able to resolve Repetitions of the Starting Sequence. 
	 * @return true when the full String is matched
	 */
	private boolean matchesFullString(int value) {
		if (separator.charAt(posInSeparator) == value) { //matching Character
			if (++posInSeparator >= separator.length()) { //matching String
				posInSeparator = 0;
				if (++numFinds >= maxFinds) {
					numFinds = 0;   //
					return true; 
				} 
			}
		}
		return false; }		

	/** caches a single Byte and returns the cached Value to output 
	 * called from either read() or write()
	 */
	private int cache(int value) {
		int outVal = buffer[bufferPosition]; 
		buffer[bufferPosition] = value; 
		if ( ++bufferPosition >= buffer.length) { //use it ascending
			bufferPosition = 0; } //makes the Contents more readable!
		return outVal;
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Parent AFilter: Implementation / Overrides
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Defines a new Protocol Interpretation: -1 closes the File / streamIO 
	 * and triggers the Creation of a new one! 
	 * @see streamIO.Byte.IStreamOutByte#addString(int)
	 */
	public void write(int val) throws IOException {
		if (--breakCountDown == -1) {
			super.write(EOF); } //
		if (matchesFullString(val)) {
			breakCountDown = breakPosition; } //
		super.write(cache(val)); 
	}

	/**
	 * @see streamIO.Byte.IStreamIn_Byte#read()
	 */
	public int read() throws IOException {
		if (--breakCountDown == -1) {
			return -1; }
		int val = streamIn.read(); 
		if (matchesFullString(val)) {
			breakCountDown = breakPosition; } //
		return cache(val); }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + FilterSplitAtFind.class.getName());
	}

	public static void SPLIT_FILE
	( final String in_FileName
	, final String separator
	, final String outFileName
	, final int breakPosition 
	, final int maxFinds 
	) throws IOException {
		final InputStream streamIn_ = new BufferedInputStream(new FileInputStream(in_FileName));////somehow doesn't work!
		final IStreamIn_Byte filter = new FilterSplitAtFind(streamIn_, separator, breakPosition, maxFinds); 
		int i = 0; 
		do {
			final OutputStream streamOut = new BufferedOutputStream(new FileStreamOutByte(outFileName+(i++)+".chunk")); //
			System.out.println(ByteStreamerThread.STREAM(filter, streamOut)); 
			streamOut.close(); 
		} while (streamIn_.available() > 0); //filter.available() > 0);
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 
	 */
	public static void main (String[] args) throws java.io.IOException {
		if (args.length < 2) {
			 System.out.println("FileInPath FileOutPath Separator breakPosition numSeparators "); }
		final String in_FileName =  args[0]; 
		final String separator   =  args[1];
		final String outFileName = (args.length > 2) ? args[2] : in_FileName; 
		final int breakPosition  = (args.length > 3) ? Integer.parseInt(args[3]) : 0;
		final int maxFinds       = (args.length > 4) ? Integer.parseInt(args[4]) : 1; 
		System.out.println("Syntax: java Stream.Byte.FilterFind in_FileName="+in_FileName+", separator="+separator+", outFileName="+outFileName+", breakPosition="+breakPosition+" , maxFinds="+maxFinds);
		SPLIT_FILE(in_FileName, separator, outFileName, breakPosition , maxFinds);
		testIt(args); }

}
