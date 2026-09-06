package streamIO.object.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import streamIO.IIStreamIn;
import streamIO.exception.BaseException;
import streamIO.integer.AStreamIn_Byte;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.pipe.ByteStreamerThread;
import streamIO.object.IStreamIn;

/**
  * Merges the Contents of a streamIO of File Names (and Flags for Directories)
  * into a single streamIO of Bytes.
  * The Separation between the Files is indicated by special Bytes
  * that should not appear within the streamIO.
  * Those are encoded using Escaping!
  * 
  * The Sequence is:
  * Dir_Name,DirStartChar
  * FileName,FileStartChar,FileContents,FileEnd,
  * FileName,FileStartChar,....FileEnd,
  * Dir_Name,Dir_StartChar,
  * FileName,FileStartChar,....FileEnd,
  * Dir_EndChar
  *
  * Using different Start and End Characters allows to detect Errors
  * during the Parsing Process and is necessary for a Nesting!
  *
  *
  * Design Decisions / Implementation Details:
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * similar Classes:
  * @see RecCmd
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	12-25-2002, 06:32 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * tags: [code/stream_parsing, code/parser]
  * concepts: [Separator-Driven Token Parsing and Stream Adapters]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public class FileStream2Stream
extends AStreamIn_Byte {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Character used to indicate a File End:	 */
	final static public char CHAR_ESCAPE     = IStreamIn_Byte.CHR_ESCAPE;
	
	/** Character used to indicate a Directory Start:	 */
	final static public char CHAR_DIR_START  = IStreamIn_Byte.CHR_UNIT_SEPARATOR;
	
	/** Character used to indicate a Directory End:	 */
	final static public char CHAR_DIR_END    = IStreamIn_Byte.CHR_GROUP_SEPARATOR;
	
	/** Character used to indicate a File Start:	 */
	final static public char CHAR_FILE_START = IStreamIn_Byte.CHR_FILE_SEPARATOR;
	
	/** Character used to indicate a File End:	 */
	final static public char CHAR_FILE_END   = IStreamIn_Byte.CHR_RECORD_SEPARATOR;
	
	/** Character used to escape any of the special Characters:	 */
	//final static public char DIR_END_CHAR = IStreamIn_Byte.;
	
	/** String to use for Parsing this streamIO */
	final static public String DIR_FILE_SEPARATORS = new StringBuffer().
	append(CHAR_DIR_START ).
	append(CHAR_DIR_END   ).
	append(CHAR_FILE_START).
	append(CHAR_FILE_END  ).toString();
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Convenience Overload defaulting the Name Prefix to the Directory's own canonical Path.
	  * @return a streamIO of Bytes in the Format described from all Files in the given Directory	 */
	final static public FileStream2Stream FILE_SYSTEM_STREAM(File dir)
		throws IOException {
		return FILE_SYSTEM_STREAM(dir, "", dir.getCanonicalPath()); }

	/** Convenience Overload defaulting the Name Prefix to the Directory's own canonical Path.
	  * @return a streamIO of Bytes in the Format described from all Files in the given Directory	 */
	final static public FileStream2Stream FILE_SYSTEM_STREAM(File dir, String suffix_)
		throws IOException {
		return FILE_SYSTEM_STREAM(dir, suffix_, dir.getCanonicalPath(), ""); }

	/** Convenience Overload defaulting the Replacement to an empty String, i.e. the Prefix is simply stripped.
	  * @return a streamIO of Bytes in the Format described from all Files in the given Directory	 */
	final static public FileStream2Stream FILE_SYSTEM_STREAM(File dir, String suffix_, String prefix_)
		throws IOException {
		return FILE_SYSTEM_STREAM(dir, suffix_, prefix_, ""); }

	/** Builds the Byte Stream by chaining a {@link FileSystem2Stream} of matching Files under this Instance.
	  * @return a streamIO of Bytes in the Format described from all Files in the given Directory	 */
	final static public FileStream2Stream FILE_SYSTEM_STREAM(File dir, String suffix_, String prefix_, String replacement_)
		throws IOException {
		return
			new FileStream2Stream(
			new FileSystem2Stream(dir, suffix_), prefix_, replacement_); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	// Converting a recursive Design with a recursive Algorithm
	// into an iterative Algorithm requires a Stack with all local Variables
	// but in this Case no structured Variable is necessary!
	
	/** Prefix subtracted from each Name */
	protected String prefix;
	
	/** Replacement to the Prefix; prepended to each Name */
	protected String replacement;
	
	/** Prefix Subtracted from each Name */
	//protected String startDir;
	
	/** current File Object */
	protected File currFile;
	
	/** current File Name */
	protected String currFileName;
	
	/** current Position in the current Name */
	protected int currFileNamePos;
	
	/** streamIO to read from */
	protected FileInputStream stream;
	
	/** streamIO to read the File Objects from */
	protected IIStreamIn fileStream;
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** last Value returned */
	protected int curChar;
	
	/** Value escaped on last Operation */
	protected int escapedChar = EOF;
	
	/** Returns the next Byte of the merged Stream, escaping any special Separator Byte encountered
	  * and moving to the next File/Directory Name once the current Source is exhausted.
	 * @see streamIO.Byte.IStreamIn_Byte#read()
	 */
	public int read() throws IOException {
		if (escapedChar != EOF) {	  curChar = escapedChar;
			escapedChar  = EOF; return curChar; }
		if (stream != null) { //most common Case
			switch(curChar = stream.read()) {
				case CHAR_DIR_START :
				case CHAR_DIR_END   :
				case CHAR_FILE_START:
				case CHAR_FILE_END  :
				case CHAR_ESCAPE    : escapedChar = curChar; curChar = CHAR_ESCAPE  ; break;
				case EOF            :         stream = null; curChar = CHAR_FILE_END; break;
				default: break;
			} //switch();
			return curChar;
		}
		if (currFileName != null) { //next less common Case
			if (++currFileNamePos < currFileName.length()) {
				return currFileName.charAt(currFileNamePos); }
			currFileName  = null;
			stream = new FileInputStream(currFile);
			return CHAR_FILE_START;
		}
		currFile = (File) fileStream.nextItem();
		if  (currFile == FileSystem2Stream.DIR_END) {
			return CHAR_DIR_END; }
		if  (currFile == FileSystem2Stream.DIR_START) {
			return CHAR_DIR_START; }
		if  (currFile == IIStreamIn.EOI) { //least frequent Case
			return EOF; }
		currFileName = currFile.getCanonicalPath();
		if (currFileName.startsWith(prefix)) {
			currFileName = replacement + currFileName.substring(prefix.length()); } //.substring(startDir.length());
		currFileNamePos = -1;
		return read(); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Constructor	 */
	public FileStream2Stream(IIStreamIn fileStream_, String prefix_, String replacement_)
	throws IOException {
		prefix  = prefix_;
		replacement = replacement_;
		this.fileStream = fileStream_;
		//startDir = dir_.getCanonicalPath();
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IStreamIn_Byte: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IStreamIn_Byte: abstract Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** This Stream imposes no particular Order on its Items.
	  * @see streamIO.Float.IStreamIn_Int#getOrder()	 */
	public byte getOrder() { return IStreamIn.ORDER_NONE; }

	/** Not tracked: always reports EOF regardless of how much data actually remains.
	  * @see streamIO.Byte.IStreamIn_Byte#available()	 */
	public int available() { return EOF; }

	/** No-op: nothing is held open across Files that needs explicit closing here.
	  * @see streamIO.Byte.IStreamIn_Byte#close()	 */
	public void close() {}

	/** No-op: Marking is not actually implemented, despite {@link #getMaxMarkSize()} reporting a real Channel Size.
	  * @see streamIO.Byte.IStreamIn_Byte#mark(int)	 */
	public void mark(final int readLimit) {}

	/** Reports the current File's Channel Size, delegated from the underlying FileInputStream.
	  * Returns -1 while no File is open (before the first one and after EOF).
	  * @see streamIO.Byte.IStreamIn_Byte#getMaxMarkSize()	 */
	public long getMaxMarkSize() {
		if (stream == null) return -1; //no File open
		try { return stream.getChannel().size();
		} catch (final IOException x) {
			throw new BaseException(x);
		}
	}

	/** Not supported: Reset is a no-op that always reports failure.
	  * @see streamIO.Byte.IStreamIn_Byte#reSet(long)	 */
	public long reSet(long Position) { return -1; }

	/** Reports the current File's Channel Position, delegated from the underlying FileInputStream.
	  * Returns -1 while no File is open (before the first one and after EOF).
	  * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() {
		if (stream == null) return -1; //no File open
		try { return stream.getChannel().position();
		} catch(final IOException x) {
			throw new BaseException(x);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + FileStream2Stream.class.getName());
		System.out.println("Syntax: FileStream2Stream DirPath Suffix Prefix");
		ByteStreamerThread.STREAM(
			FILE_SYSTEM_STREAM(
				new File(args[0]),
				args.length > 1 ? args[1] : ""),
			System.out);
//		System.out.println(DIR_FILE_SEPARATORS.length());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
