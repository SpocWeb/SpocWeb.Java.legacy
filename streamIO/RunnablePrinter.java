package streamIO;

import java.io.OutputStream;
import java.io.PrintStream;

/**
  * Title: RunnablePrinter<p>
  * Description:
  * A Runnable that prints a fixed String to a given PrintStream each time it is run(),
  * inserting a line break once the running Position exceeds the configured Line Size.
  * Useful e.g. as a HeartBeat Callback (see Assert.DEFAULT_HEARTBEAT) to print a Progress Marker
  * on every call without wrapping Output into unreadably long single Lines.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	10-20-2002, 09:49 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:54:29Z
  * digest: 0dc8ee42d0bf8909339ddd526737baa45d908f7401f688917151e5920635c8d9
  * stale: false
  * tags: [code/output_stream]
  * concepts: [Runnable Line Printer]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public class RunnablePrinter
implements Runnable {

	/** Default maximum Line Length before {@link #run()} inserts a Line Break. */
	public static int LINE_SIZE_DEFAULT = 80;

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** String to write to the streamIO 	 */
	protected final String str;

	/** streamIO to write the String to	 */
	protected final PrintStream stream;

	/** Line Size	 */
	protected final int lineSize;

	/** current Position on the Line 	 */
	protected int position;

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Initializing Constructor
	 * @param str_ String to write to the streamIO
	 * @param stream_ streamIO to write the String to
	 */
	public RunnablePrinter(final PrintStream stream_, final String str_, final int lineSize_) {
		this.lineSize = lineSize_;
		this.stream = stream_;
		this.str = str_; 
	}

	/**
	 * Initializing Constructor
	 * @param str_ String to write to the streamIO
	 * @param stream_ streamIO to write the String to
	 */
	public RunnablePrinter(final OutputStream stream_, final String str_, final int lineSize_) {
		this.lineSize = lineSize_;
		this.stream = new PrintStream(stream_);
		this.str = str_; 
	}

	/**
	 * Initializing Constructor
	 * @param str_ String to write to the streamIO
	 * @param stream_ streamIO to write the String to
	 */
	public RunnablePrinter(final PrintStream stream_, final String str_) {
		this(stream_, str_, LINE_SIZE_DEFAULT); }

	/**
	 * Initializing Constructor
	 * @param str_ String to write to the streamIO
	 * @param stream_ streamIO to write the String to
	 */
	public RunnablePrinter(final OutputStream stream_, final String str_) {
		this(stream_, str_, LINE_SIZE_DEFAULT); }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface Runnable: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Implementation of the Runnable Interface: prints the String to the streamIO */
	public void run() { 
		if (++position > lineSize) {
			position = 0;
			stream.println(); 
		}
		stream.print(str); }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + RunnablePrinter.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

