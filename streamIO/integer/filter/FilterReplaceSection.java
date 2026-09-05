package streamIO.integer.filter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import math.vector.VectorString;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.adapter.InputStreamToStreamIn_Byte;
import streamIO.integer.adapter.OutputStreamToStreamOutByte;
import streamIO.integer.file.FileStreamOutByte;
import streamIO.integer.pipe.ByteStreamerThread;

/**
  * Filters a streamIO and cuts out a Section between the beginning and Ending String.
  *
  * Design Decisions / Implementation Details:
  * Searching is done primitive, using an O(N*M) Algorithm.
  * Can be improved later to use one of the more sophisticated Algorithms like Boyer-Moore. 
  *
  *
  * Known SubClasses: <none>
  *
  * otherwise related Classes: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	02-12-2003, 11:29 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T21:46:15Z
  * digest: 9e1036ce698dcd4172d097862e34517c85f2a76907fe9f7d7d167a126f1f7280
  * stale: false
  * tags: [code/stream_filter]
  * concepts: [Pluggable Byte-Stream Filter Infrastructure and java.io Adapters]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class FilterReplaceSection 
extends FilterByte 
implements IPlugAbleFilterByte {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////

	/** Separator to start the Section */
	private final String starter;

	/** Separator to end the Section */
	private final String stopper;

	/** Replacement instead of the Section */
	private final String replace;

	/** current Position in the Separator */
	private int posInSeparator;

	/** Number of Findings before Replacement kicks in  */
	//private int numFinds;

	/** The String to search for currently 
	 * is either @see #starter or @see #stopper 
	 */
	private String stringToSearch;

	/** 
	 * seta the Input streamIO to filter
	 * @param stream the new Input Sream
	 */
	public void setStreamIn_(IStreamIn_Byte stream) {
		this.streamIn = stream;
	}

	/** Sets the Input stream to filter, wrapping the given InputStream.
	 * @see Stream.Byte.IConfigFilterOut#setStreamOut(java.io.OutputStream)
	 */
	public void setStreamIn_(InputStream stream) {
		this.streamIn = new InputStreamToStreamIn_Byte(stream);
	}

	/**
	 * seta the Output streamIO to filter
	 * @param stream the new Output Sream
	 */
	public void setStreamOut(IStreamOutByte stream) {
		this.streamOut = stream;
	}

	/** Sets the Output stream to filter, wrapping the given OutputStream.
	 * @see Stream.Byte.IConfigFilterOut#setStreamOut(java.io.OutputStream)
	 */
	public void setStreamOut(OutputStream stream) {
		this.streamOut = new OutputStreamToStreamOutByte(stream);
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor for FilterReplaceSection.
	 * @param streamOut
	 */
	public FilterReplaceSection(
		final IStreamOutByte streamOut,
		final String starter_,
		final String stopper_,
		final String replace_) {
		super(streamOut);
		this.starter = stringToSearch = starter_;
		this.stopper = stopper_;
		this.replace = replace_;
	}

	/**
	 * Constructor for FilterReplaceSection.
	 * @param streamOut
	 */
	public FilterReplaceSection(
		final OutputStream streamOut,
		final String starter_,
		final String stopper_,
		final String replace_) {
		super(streamOut);
		this.starter = stringToSearch = starter_;
		this.stopper = stopper_;
		this.replace = replace_;
	}

	/**
	 * Constructor for FilterReplaceSection.
	 * @param streamIn_
	 */
	public FilterReplaceSection(
		final IStreamIn_Byte streamIn_,
		final String starter_,
		final String stopper_,
		final String replace_)
		throws IOException {
		super(streamIn_);
		this.starter = stringToSearch = starter_;
		this.stopper = stopper_;
		this.replace = replace_;
	}

	/**
	 * Constructor for FilterReplaceSection.
	 * @param streamIn_
	 */
	public FilterReplaceSection(
		final InputStream streamIn_,
		final String starter_,
		final String stopper_,
		final String replace_)
		throws IOException {
		super(streamIn_);
		this.starter = stringToSearch = starter_;
		this.stopper = stopper_;
		this.replace = replace_;
	}

	/** 
	 * primitive N*M String Searcher, 
	 * not able to resolve Repetitions of the Starting Sequence. 
	 * @return the Number of Characters to go for the full Match
	 * the negative Number matched on a failed Match (to roll them back)
	 */
	private int diffToString(int value) {
		if (stringToSearch.charAt(posInSeparator) == value) {
			//matching Character
			int ret = stringToSearch.length() - ++posInSeparator;
			if (ret == 0) { //matching String
				posInSeparator = 0;
			}
			return ret;
		}
		int ret = -posInSeparator - 1;
		posInSeparator = 0;
		return ret;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Parent AFilter: Implementation / Overrides
	////////////////////////////////////////////////////////////////////////////////

	/** Writes the byte through, replacing any matched section between starter and stopper.
	 * @see streamIO.Byte.IStreamOutByte#addString(int)
	 */
	public void write(int val) throws IOException {
		int diff = diffToString(val);
		if (diff == 0) { //String Match
			if (stringToSearch == starter) { //Section Start...
				stringToSearch = stopper; //search for the Section End
			} else {
				stringToSearch = starter;
				super.write(this.replace);
			}
		} else if (diff < 0) { //failed match, write the Cache
			if (stringToSearch == starter) {
				//				if (diff < -1) {
				super.write(starter.substring(0, -diff - 1));
				//				}
				super.write(val);
			} else { //in the Section, ... skip it
			}
		} else { //Match in Progress, hold it...
		}
	}

	/**
	 * Doesn't work currently.
	 * @see streamIO.Byte.IStreamIn_Byte#read()
	 */
	/*	public int read() throws IOException {
			int val = streamIn.read(); 
			int diff = diffToString(val); 
			if (diff == 0) {//String Match
				if (stringToSearch == starter) {
					stringToSearch = stopper;
				} else {
					stringToSearch = starter;
				}
			} else if (diff < 0) {//failed match, write the Cache
			} else { //Match in Progress, hold it...
			} //
			return cache(val); }
	*/
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + FilterReplaceSection.class.getName());
	}

	/**
	 * Replaces the given Section in the given File
	 * @param in_FileName
	 * @param starter
	 * @param stopper
	 * @param replace
	 * @param outFileName
	 * @throws IOException
	 */
	public static void REPLACE_IN_FILE(
		final String in_FileName,
		final String starter,
		final String stopper,
		final String replace,
		final String outFileName)
		throws IOException {
		final IPlugAbleFilterOutByte filter =
			new FilterReplaceSection(
				(OutputStream) null,
				starter,
				stopper,
				replace);
		FILTER_FILE(in_FileName, filter, outFileName);
	}

	/**
	 * Replaces the given Section in the given File
	 * @param in_FileName
	 * @param starter
	 * @param stopper
	 * @param replace
	 * @param outFileName
	 * @throws IOException
	 */
	public static void REPLACE_IN_DIR(
		final String in_DirName,
		final String starter,
		final String stopper,
		final String replace,
		final String outDirName,
		final String[] Suffixes)
		throws IOException {
		File in_Dir = new File(in_DirName);
		File outDir = new File(outDirName);
		final IPlugAbleFilterOutByte filter =
			new FilterReplaceSection(
				(OutputStream) null,
				starter,
				stopper,
				replace);
		FILTER_DIR(in_Dir, filter, outDir, Suffixes);
	}

	/**
	 * Replaces the given Section in the given File
	 * @param in_FileName
	 * @param starter
	 * @param stopper
	 * @param replace
	 * @param outFileName
	 * @throws IOException
	 */
	public static void FILTER_DIR(
		final File in_Dir,
		final IPlugAbleFilterOutByte filter,
		final File outDir,
		final String[] Suffixes)
		throws IOException {
		if (!outDir.exists()) {
			outDir.mkdirs(); //outDir.createNewFile();
		}
		File[] list = in_Dir.listFiles();
		for (int i = list.length; --i >= 0;) {
			File in_File = list[i];
			File outFile = new File(outDir, in_File.getName());
			if (in_File.isDirectory()) {
				FILTER_DIR(in_File, filter, outFile, Suffixes);
			} else {
				if (VectorString.ENDS_WITH(in_File.getName(), Suffixes) >= 0) {
					REPLACE_IN_FILE(in_File, filter, outFile);
				}
			}
		}
	}

	/**
	 * Replaces the given Section in the given File
	 * 
	 * @param in_FileName the File to read from 
	 * @param filter a Filter with exchangeable Streams. 
	 * @param outFileName
	 * @throws IOException
	 */
	public static void FILTER_FILE(
		final String in_FileName,
		final IPlugAbleFilterOutByte filter,
		final String outFileName)
		throws IOException {
		final InputStream streamIn_ =
			new BufferedInputStream(new FileInputStream(in_FileName));
		final OutputStream streamOut =
			new BufferedOutputStream(new FileStreamOutByte(outFileName));
		REPLACE_IN_STREAM(streamIn_, filter, streamOut);
		streamOut.close(); 
	}

	/**
	 * Replaces the given Section in the given File
	 * @param in_FileName
	 * @param starter
	 * @param stopper
	 * @param replace
	 * @param outFileName
	 * @throws IOException
	 */
	public static void REPLACE_IN_FILE(
		final File in_File,
		final IPlugAbleFilterOutByte filter,
		final File outFile)
		throws IOException {
		final InputStream streamIn_ =
			new BufferedInputStream(new FileInputStream(in_File));
		final OutputStream streamOut =
			new BufferedOutputStream(new FileStreamOutByte(outFile));
		REPLACE_IN_STREAM(streamIn_, filter, streamOut);
		streamOut.close();
	}

	/**
	 * Replaces the given Section in the given File
	 * @param streamIn_
	 * @param starter
	 * @param stopper
	 * @param replace
	 * @param streamOut
	 * @throws IOException
	 */
	public static long REPLACE_IN_STREAM(
		final IStreamIn_Byte streamIn_,
		final IPlugAbleFilterOutByte filter,
		final IStreamOutByte streamOut)
		throws IOException {
		filter.setStreamOut(streamOut);
		return ByteStreamerThread.STREAM(streamIn_, filter);
	}

	/**
	 * Replaces the given Section in the given File
	 * @param streamIn_
	 * @param starter
	 * @param stopper
	 * @param replace
	 * @param streamOut
	 * @throws IOException
	 */
	public static long REPLACE_IN_STREAM(
		final InputStream streamIn_,
		final IPlugAbleFilterOutByte filter,
		final OutputStream streamOut)
		throws IOException {
		filter.setStreamOut(streamOut);
		return ByteStreamerThread.STREAM(streamIn_, filter);
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 
	 */
	// TODO: LOGIC: when args.length < 5, the Syntax message is printed but main() falls
	// through instead of returning, so args[0..4] below throw
	// ArrayIndexOutOfBoundsException instead of exiting cleanly (same pattern as
	// EchoFile.main() and FixRecordScrambler.main() elsewhere in this codebase).
	public static void main(String[] args) throws java.io.IOException {
		if (args.length < 5) {
			System.out.println(
				"FileInPath Starter Stopper Replacement FileOutPath ");
			System.out.println(
				"DirInPath Starter Stopper Replacement DirOutPath ListOfSuffixes");
		}
		final String in_FileName = args[0];
		final String starter = args[1];
		final String stopper = args[2];
		final String replace = args[3];
		final String outFileName = args[4];
		System.out.println(
			"Syntax: java Stream.Byte.FilterFind in_FileName="
				+ in_FileName
				+ ", starter="
				+ starter
				+ ", stopper ="
				+ stopper
				+ ", replace ="
				+ replace
				+ " , outFileName ="
				+ outFileName);
		if (args.length == 5) {
			REPLACE_IN_FILE(in_FileName, starter, stopper, replace, outFileName);
		} else {
			REPLACE_IN_DIR(in_FileName, starter, stopper, replace, outFileName, args);
		}
		testIt(args);
	}

}
