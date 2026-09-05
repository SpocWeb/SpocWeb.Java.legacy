
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import streamIO.AStreamOut;
import streamIO.integer.filter.LimitedSizeOutputStream;
import streamIO.object.backTrack.BackTracker;
import streamIO.object.enumer.container.AContainer;
import tester.process.StreamProcessor;


/**
  * Title: FilterFind<p>
  *
  * Purpose:
  * Filters a streamIO and ends it 
  * as soon as a certain String is found 
  * more often than the specified Number. 
  * @see streamIO.integer.filter.FilterSplitAtFind which uses custom Interfaces.
  * 
  * Design Decisions / Implementation Details:
  * Counter is integrated into this Class to save coupling Streams at different Levels.
  * Since Files are not only separated AFTER a certain String, 
  * but also possibly BEFORE or WITHIN the streamIO, thus it must read ahead. 
  * Searching is done primitive, using an O(N*M) Algorithm.
  * Can be improved later to use one of the more sophisticated Algorithms like Boyer-Moore. 
  *
  *
  * Known SubClasses: <none>
  *
  * otherwise related Classes: 
  * @see FixRecordScrambler for splitting structured Text Files 
  *
  * Known Uses: 
  * for splitting structured Text Files.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	02-12-2003, 11:29 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T09:55:11Z
  * digest: 8e6e31be2b12c9ab92963dd8fe058847ea7ab71290debe5a5d357ffd1e361c73
  * stale: false
  * tags: [code/text_parsing, code/cli_tool]
  * concepts: [Text Parsing]
  * facets: {layer: utility, status: broken, complexity: medium}
  * -->
  */
public class FilterFind
extends FilterInputStream
{

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Streams the WHOLE InputStream byte-wise into the OutputStream
	  * @see BackTracker.operate
	  * @see ProcessorRunner.run
	  * @see StreamProcessor.run
	  * @see LimitedSizeOutputStream.stream for Bytes
	  * @see AContainer.copyAt for creating Copies of the Items
	  * @see AStreamOut.stream for fast streaming
	  * @see AStreamOut.add for flattening 	 */
	final static public long STREAM(InputStream in, OutputStream out) throws java.io.IOException {
		long ret = 0;
		for (int val; (val = in.read()) != -1; ++ret ) {
			out.write (val); }
		out.flush();
		return ret; }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Separator to sep the Streams */
	private final String separator; 

	/** Position to separate the Streams */
	private final int breakPosition; 

	/** Position to separate the Streams */
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
	 * Constructor for FilterFind.
	 * @param streamIn_
	 */
	public FilterFind(final InputStream streamIn_, final String separator_, final int breakPosition_, final int maxFinds_
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
	
	/** Reads and caches one Byte, cutting off the Stream {@link #breakPosition} Bytes after the
	 * {@link #maxFinds}-th Occurrence of {@link #separator} is found.
	 * @see streamIO.Byte.IStreamIn_Byte#read()
	 */
	public int read() throws IOException {
		// TODO: LOGIC: breakCountDown defaults to 0 and is never set in the Constructor, so on
		// the very first call --breakCountDown becomes -1 and this returns EOF immediately,
		// before ever reading from the wrapped Stream or checking for the Separator. main()'s
		// `while (streamIn_.available() > 0)` loop then never terminates, since the underlying
		// Stream is never actually consumed through this filter.
		if (--breakCountDown == -1) {
			return -1; }
		int val = super.read();
		if (matchesFullString(val)) {
			breakCountDown = breakPosition; } //
		return cache(val);
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + FilterFind.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 
	 */
	public static void main (String[] args) throws java.io.IOException {
		System.out.println("Syntax: java Stream.Byte.FilterFind2 FileInPath FileOutPath Separator breakPosition numSeparators ");
		final String separator = args[2];
		final int breakPosition = (args.length > 3) ? Integer.parseInt(args[3]) : 0;
		final int maxFinds      = (args.length > 4) ? Integer.parseInt(args[4]) : 1; 
		final InputStream streamIn_ = new BufferedInputStream(new FileInputStream(args[0]));////somehow doesn't work!
		final InputStream filter = new FilterFind(streamIn_, separator, breakPosition, maxFinds); 
		int i = 0; 
		do {
			final String counter = "00"+Integer.toString(i++).trim();
			final String trimmedCounter = counter.substring(counter.length()-3);
			final OutputStream streamOut = new BufferedOutputStream(new FileOutputStream(args[1]+trimmedCounter+".chunk")); //
			System.out.println(STREAM(filter, streamOut)); 
			streamOut.close(); 
		} while (streamIn_.available() > 0); //filter.available() > 0);
		testIt(args); }

}
