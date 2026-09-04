package streamIO.integer.filter;

import java.io.FileInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import streamIO.IIStreamIn;
import streamIO.fileSystem.FileIterator;
import streamIO.integer.pipe.ByteStreamerThread;

/**
 * LimitedSizeOutputStream
 * Maps a simple (unlimited) Output streamIO
 * to an Enumeration Output Streams with limited Size.
 * 
 * I.e. this Class just creates limited Size Chunk Streams
 * from a very large streamIO
 *
 * Typically used Classes:
 * @see FileIterator / FileBackupIterator for generating File Names
 * @see FileInputStream for reading a File (although this works for any Input and Output streamIO)
 * @see streamIO.JSONTest.LimitedSizeStreamOut which collects only a limited Size of Objects.
 *
 * Typically used Methods:
 * 		stream() for streaming Data from an Input- to an Output- streamIO (byte- or chunk-wise)
 * 		split() / main() for splitting up a File (by Name) into it's parts.
 *
 * Created on 31. März 2001, 22:23
 *
 * @author  Matthias Heuer
 * @version
 */
public class LimitedSizeOutputStream
extends FilterOutputStream {
	
	////////////////////////////////////////////////////////////////////////////
	//	static Members
	////////////////////////////////////////////////////////////////////////////
	
	/**Indicator for the End of this streamIO
	 * Returned by read() when the End of the streamIO is reached,
	 * rather than throwing an EOFException.
	 */
	final static public int EOF = -1;
	
	/** String "Syntax"  */
	final static public String strSyntax = "Syntax: ";
	
	/** Describes the Syntax for the split Method  */
	final static public String strSplitSyntax = "split OriginalPrefix.suf, MaxSize [, ChunkSize] [, Prefix = OriginalPrefix, Suffix = .suf]";
	
	////////////////////////////////////////////////////////////////////////////
	//	static Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** Splits up the original File byte-wise into several Limited Size Files.  */
	public static void split(String Original, int MaxSize, String Prefix, String Suffix) throws IOException {
		ByteStreamerThread.STREAM( new FileInputStream(Original),
				new LimitedSizeOutputStream(
				new FileIterator(Prefix, Suffix, false, false), MaxSize)); }
	
	/** Splits up the original File into several Limited Size Files
	 *  using Blocks of the given Chunk Size.  */
	public static void split(String Original, int MaxSize, String Prefix, String Suffix, int ChunkSize) throws IOException {
		ByteStreamerThread.STREAM( new FileInputStream(Original),
				new LimitedSizeOutputStream(
//				new FileBackupEnumeration("C:/Orig.txt", "C:/Backup.old"), MaxSize)); }
				new FileIterator(Prefix, Suffix, false, false), MaxSize), ChunkSize); }

	/**
	 * Splits up the given File into several Parts with maximum Size.
	 *
	 * @param args  Array of parameters passed to the application via the command line:
	 * At least 2 Parameters:
	 *		String OriginalName
	 *		int MaxSize
	 *		[String Prefix String Suffix]
	 *		[int ChunkSize]
	 * TODO: A faster Alternative is to just truncate and rename the first File	(After Operation!) */
	public static void split (String[] args)	throws IOException {
/*		args = new String[] {"D:/Gladiator.avi", "100000000", "C:/Gladiator", ".avi", "100000"};
		args = new String[] {"D:/Gladiator.avi", "100000000", "C:/Gladiator", ".avi", "100000"};
		args = new String[] {"\\\\Cenb0026\\D\\Gladiator - DivX ;-).avi", "680000000", "D:/Gladiator", ".avi", "100000"};
		args = new String[] {"C:/Code.doc", "10000", "C:/Code", ".doc", "1000"};
		args = new String[] {"C:/Marillion.mp3", "1000000", "C:/Marillion", ".mp3", "100000"};
		args = new String[] {"C:/Marillion.mp3", "1000000", "100000"};
*/	
		if (args.length < 2) { System.out.println (strSyntax + strSplitSyntax); return; }
		if (args.length <= 3) { //parse the File Name by the last Dot
			int Pos = args[0].lastIndexOf (".");
			if (args.length == 3) //ChunkSize
				 args = new String[] { args[0], args[1], args[0].substring (0, Pos), args[0].substring (Pos), args[2] };
			else args = new String[] { args[0], args[1], args[0].substring (0, Pos), args[0].substring (Pos) }; }
		if (args.length == 4)
			 split(args[0], Integer.parseInt(args[1]), args[2], args[3]);
		else split(args[0], Integer.parseInt(args[1]), args[2], args[3], Integer.parseInt(args[4]));
//		return 0;
	}
	
	/**
	 * The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args)	throws IOException {
		split(args); }
	
	//////////////////////////
	//	Member Variables	//
	//////////////////////////
	
	/**Counter for the Bytes written  */
	protected long Counter;
	
	/** Maximum Size of this streamIO.
	  * Since it is final and set in the Constructor, it can be made public. */
	final public long MaxSize;
	
	/** Reference to the Enumeration that returns new Output Streams	 */
	protected IIStreamIn Outs;
	
	/** Creates new OutputStreamSequence */
	public LimitedSizeOutputStream(IIStreamIn Outs, int MaxLength) {
		super(null); this.Outs = Outs; this.MaxSize = MaxLength;
		try { reStart(); } catch ( IOException e) { } } //cannot happen!
	
	/** Closes the current streamIO and opens up a new one at any time. */
	public void reStart() throws IOException {
		if (out != null) {
			out.close(); }
		out = (OutputStream) Outs.nextItem();
		Counter = 0; }
	
	/** Writes a single Byte into the streamIO */
	public void write(int val) throws IOException {
		if (++Counter > MaxSize) {
			reStart(); ++Counter; }
		out.write(val); }
	
	/** Writes a whole Chunk into the streamIO
	 *  This is an Optimization if not every Byte has to be Checked.
	 *  If the ChunkSize is an Integer Multiple of MaxSize, the Files are filled up completely.
	 */
	public void write(byte[] val, int Offset, int Length) throws IOException {
		if ((Counter += Length) > MaxSize) {
			reStart(); }
		out.write(val, Offset, Length); }

}
