package streamIO.object.parser;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import streamIO.AStreamOut;
import streamIO.StreamOutPrimitive;
import streamIO.object.AStreamIn;
import streamIO.object.filterInOut.FilterByFunction;
import streamIO.object.filterInOut.FilterReflectionFunction;
import function.FunctionByHash;

/**
  * Returns all Files and Directories in a FileSystem as a streamIO of FileNames.
  * The Separation between Directories is indicated by special Strings:
  * "."  indicates the Start of a SubDirectory
  * ".." indicates the End   of a SubDirectory
  * 
  * The recursive Depth First InOrder Walk is used. 
  *
  * Design Decisions / Implementation Details:
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
public class FileSystem2Stream 
extends AStreamIn {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** String used to indicate a Sub-Directory Start.
	  * (In this Case also the Directory itself could be returned!)
	  */
	final static public String STR_DIR_START  = ".";

	/** String used to indicate a Directory End:	 */
	final static public String STR_DIR_END = "..";

	/** String used to indicate a Sub-Directory Start.
	  * (In this Case also the Directory itself could be returned!)
	  */
	final static public File DIR_START  = new File(STR_DIR_START);

	/** String used to indicate a Directory End:	 */
	final static public File DIR_END = new File(STR_DIR_END);

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	// Converting a recursive Design with a recursive Algorithm
	// into an iterative Algorithm requires a Stack with all local Variables
	// but in this Case no structured Variable is necessary!
	
	/** either need a Queue or List of Files currently processed:
	 * A Queue is necessary to keep the Sequence and thus detect the Directory Change
	 * A Stack is necessary to keep the nested structure for accumulating Statistics!
	 */
	protected Stack stack = new Stack();
	
	/** The File Name Suffix, used to select Files */
	protected String suffix;
	
	/** Files in the current Directory */
	protected String[] fileNames;
	
	/** current File# in the current Directory */
	protected int currFile;
	
	/** current Directory */
	protected File currDir;
	
	/** Object returned last, typed as String */
	protected File currItem;
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns the File or Directory Marker last produced by {@link #nextItem()}.
	 * @see streamIO.Object.IStreamIn#currItem()
	 */
	public Object currItem() {
		return currItem; }

	/** Flag to switch on transferring the Directory Object    directly AFTER  the Start Object DIR_START
	 * alternatively the Directory Object could be transferred directly BEFORE the Start Object DIR_START
	 */
	public boolean dirAfterStart; 
	
	/** Flag to switch on transferring the Directory Object    
	 * directly BEFORE the Start Object DIR_START instead of 
	 * directly AFTER  the Start Object DIR_START
	 */
	public boolean dirBeforeStart; 
	
	/** Advances the recursive depth-first Directory Walk by one Step, returning the next File,
	  * Directory or a Directory-Start/-End Marker as appropriate.
	 * @see streamIO.Byte.IStreamIn_Byte#read()
	 */
	public Object nextItem() {
		if (currDir != null) {
			if (fileNames == null) {
				fileNames  = currDir.list();
				return currItem = DIR_START; 
			}
			if (currFile < 0) {
				currFile = fileNames.length; 
				if (dirAfterStart) {
					return currItem = currDir; }
			}
		}
		while (--currFile >= 0) { //regular Case: File or Directory in a Directory List
			String fileName = fileNames[currFile]; //returns only local Names! 
			File file = new File(currDir, fileName);
			if (!file.exists()) {
				System.out.println("Error in Algorithm"); }
			if (file.isDirectory()) {
				stack.push(file); } //.list()); //so the file can be used later and the Memory Consumption is lower!
			if (fileName.endsWith(suffix)) {
				return currItem = file; }
		} //while
		fileNames = null;
		if (stack.size() <= 0) { // isEmpty()) {
			return currItem = (File) EOI; }
		currDir = (File)stack.pop();
		if (currDir == null) {
			return currItem = (File) EOI; }
		if (currDir == DIR_END) {
			currDir = null;
			return DIR_END; }
		stack.push(DIR_END); //so it is retrieved!
//		currDir = new File(currDirName); //duplicate, but necessary to be able to use ".."!
		if (!currDir.isDirectory()) { //should only happen if a File was handed over in the Constructor, instead of a Directory
			if (!currDir.exists()) {
				System.out.println("Error in Algorithm"); }
			return currItem = currDir; } 
		if (dirBeforeStart) {
			return currItem = currDir; }
		return nextItem(); } //
		
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Constructor	 */
	public FileSystem2Stream(File dir_, String suffix_)
	throws IOException {
		suffix = suffix_;
		String startDir = dir_.getCanonicalPath();
		if (!dir_.isDirectory()) {
			throw new IOException("must be a Directory: '" + startDir + "'"); }
		stack.push(dir_);
//		System.out.println("cmd='" + cmd + "'");
//		System.out.println("dir='" + dir + "'");
//		System.out.println("pattern='" + pattern + "'");
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
	public byte getOrder() { return 0; }

	/** Rough Estimate of remaining Work, derived from the pending Directory Stack and current File Index.
	  * @see streamIO.Byte.IStreamIn_Byte#available()	 */
	public long availAble() { return stack.size() + currFile; }

	/** No-op: nothing is held open across Directories that needs explicit closing here.
	  * @see streamIO.Byte.IStreamIn_Byte#close()	 */
	public void close() throws IOException {}

	/** No-op: Marking is not implemented for this Tree Walk.
	  * @see streamIO.Byte.IStreamIn_Byte#mark(int)	 */
	public void mark(int readLimit) {}

	/** Not supported: always reports -1, i.e. no Mark/Reset support.
	  * @see streamIO.Byte.IStreamIn_Byte#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return -1; }
	
	/** A Scalar cannot well encode the current State in a TreeWalk!  
	 * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return 0; }
	
	/** Not supported: this Tree Walk cannot rewind to an arbitrary Position; always returns 0.
	  * @see streamIO.Byte.IStreamIn_Byte#reSet(long)	 */
	public long reset(final long Position) throws IOException { return 0; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	protected static final Object[][] MAP_DIRS = {
		{STR_DIR_START, STR_DIR_START}, 
		{STR_DIR_END  , null}
	};

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + FileSystem2Stream.class.getName());
		System.out.println("Syntax: FileSystem2Stream DirPath Suffix Prefix");
		Array2Stream arr = new Array2Stream(); 
		File dir = new File(args[0]); 
		FileSystem2Stream files = new FileSystem2Stream(dir, args.length > 1 ? args[1] : ""); files.dirAfterStart = true; files.dirBeforeStart = true; 
		FunctionByHash  mapping = new FunctionByHash(); mapping.identity = true; 
		FilterByFunction filter = new FilterByFunction(FilterReflectionFunction.FILTER_GET_NAME(files), mapping);
		MAP_DIRS[0][1] = filter; filter.inValidOnNull = true;
		mapping.setAt(MAP_DIRS); 
		AStreamOut.STREAM(filter, arr); 
		AStreamOut.STREAM(
			FilterReflectionFunction.FILTER_GET_NAME(
//			new FilterFileToName(
			new FileSystem2Stream(dir, args.length > 1 ? args[1] : "")), 
			new StreamOutPrimitive(""), 3, false, false, "\r\n", Long.MAX_VALUE);
//		System.out.println(DIR_FILE_SEPARATORS.length());
		System.out.println(arr); 
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}

