package streamIO.integer.filter;

import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;

import streamIO.fileSystem.FileIterator;
import streamIO.integer.pipe.ByteStreamerThread;
import streamIO.object.StreamIn2Enumeration;

/**
 * LimitedSizeInputStream
 * limits the Number of Bytes to be read from a streamIO.
 * not used yet...
 * Exploits the Fact that FilterInputStream delegates to the inner streamIO.
 *
 * Created on 31. M�rz 2001, 20:51
 *
 * @author  Matthias Heuer
 * @version
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:49:17Z
 * digest: 540f94e3bd7e95508214e33e0e519f8e8ef53052a93575969a76bc891450bff7
 * stale: false
 * tags: [code/stream_filter]
 * concepts: [Pluggable Byte-Stream Filter Infrastructure and java.io Adapters]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class LimitedSizeInputStream
extends FilterInputStream {

	////////////////////////////////////////////////////////////////////////////////
	//	Static Members 	//
	////////////////////////////////////////////////////////////////////////////////

	/** Describes the Syntax for the splice Method  */
	final static public String strSpliceSyntax = "splice [Prefix, Suffix,] Original[, ChunkSize]";

	////////////////////////////////////////////////////////////////////////////////
	//  Static Methods from LimitedSizeOutputStream moved here!
	//  Splices a Stream of Files into a single very long Stream.
	////////////////////////////////////////////////////////////////////////////////

	/** Splices the split up Files byte-wise into one File.  */
	public static void splice(String Prefix, String Suffix, String Original) throws IOException {
		ByteStreamerThread.STREAM(
			new SequenceInputStream(
			new StreamIn2Enumeration(
			new FileIterator(Prefix, Suffix, true, false))),
			new FileOutputStream(Original)); }

	/** Splices the split up Files byte-wise into one File,
	 *  using Blocks of the given Chunk Size.  */
	public static void splice(String Prefix, String Suffix, String Original, int ChunkSize) throws IOException {
		ByteStreamerThread.STREAM(
			new SequenceInputStream(
			new StreamIn2Enumeration(
			new FileIterator(Prefix, Suffix, true, false))),
			new FileOutputStream(Original), ChunkSize); }

	/**
	 * The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 *		String Merged Path and FileName
	 *		[String Prefix String Suffix]
	 *		[int ChunkSize]
	 *
	 * TODO: A faster Alternative is to just append to and rename the first File!
	 * When a command line is used, the User can be queried synchronously
	 * for missing Files.
	 *
	 * Alternatively use the Command Line for appending:
	 * type "File2" >> "File1" */
	public static void splice(String[] args)	throws IOException {
/*		args = new String[] {"C:/Gladiator", ".avi", "D:/Gladiator.avi"};
		args = new String[] {"D:/Gladiator", ".avi", "\\\\Cenb0026\\D\\Gladiator.avi"};
		args = new String[] {"C:/Code", ".doc", "C:/Code.doc"};
		args = new String[] {"C:/Marillion", ".mp3", "C:/Marillion.mp3", "100000"};
		args = new String[] {"C:/Marillion.mp3", "100000"};
		*/
		if (args.length == 0) { System.out.println (LimitedSizeOutputStream.strSyntax + strSpliceSyntax); return; }
		if (args.length <= 2) { //less than 3 Args: parse the File Name
			int Pos = args[0].lastIndexOf (".");
			if (args.length == 2) //ChunkSize
				 args = new String[] { args[0].substring (0, Pos), args[0].substring (Pos), args[0], args[1] };
			else args = new String[] { args[0].substring (0, Pos), args[0].substring (Pos), args[0] }; }
		if (args.length == 3)
			 splice(args[0], args[1], args[2]);
		else splice(args[0], args[1], args[2], Integer.parseInt(args[3]));
//		return 0;
	}

	/**
	 * The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args)	throws IOException {
		splice(args); }

	////////////////////////////////////////////////////////////////////////////////
	//	Member Variables
	////////////////////////////////////////////////////////////////////////////////

	/**Counter for the Bytes written  */
	protected long Counter;

	/**Maximum Size of this streamIO */
	protected long MaxSize;

    /** Creates new LimitedSizeStream */
    public LimitedSizeInputStream (InputStream IS, long MaxSize) {
		super(IS); this.MaxSize = MaxSize; reStart(); }

	/**ReStarts the streamIO by resetting the internal Counter */
	public void reStart() {
		Counter = 0; if (in.markSupported ()) in.mark(Integer.MAX_VALUE); }

    /**
	 * Returns the number of bytes that can be read (or skipped over) from this input stream
	 * without blocking by the next caller of a method for this input stream.
	 */
	public int available() throws IOException {
		return (int) Math.min(in.available(), MaxSize-Counter); }

    // TODO: LOGIC: calls `skip(...)` on itself (same overload, same class) instead of
    // delegating to the wrapped stream's `in.skip(...)`. Since Counter keeps increasing
    // toward MaxSize with every recursive call, this either recurses until
    // StackOverflowError or (once Counter reaches MaxSize) recurses with an
    // ever-non-advancing argument - either way the underlying stream position is never
    // actually advanced. Should call `in.skip(...)`.
    /**
	 * Skips over and discards n bytes of data from this input stream.
	 */
	public long skip(long n) throws IOException {
		return skip(Counter += Math.min(n, MaxSize-Counter)); }

    // TODO: LOGIC: off-by-one - `++Counter < MaxSize` stops reading once Counter reaches
    // MaxSize-1, so only MaxSize-1 bytes are ever read before EOF is returned instead of
    // the documented MaxSize bytes. Should be `++Counter <= MaxSize`.
    /**
	 * Reads the next byte of data from the input stream.
	 */
	public int read() throws IOException {
		if (++Counter < MaxSize) {
			return in.read(); }
		return LimitedSizeOutputStream.EOF; }


}
