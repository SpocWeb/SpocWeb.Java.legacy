package streamIO;

import java.io.OutputStream;
import java.io.PrintStream;

/**
  * Title: RunnablePrinter<p>
  * Description:
  * Purpose:
  * Prints a String to the given streamIO each time run
  * Purpose / Responsibilities of this Class
  *
  * Design Decisions / Implementation Details:
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
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
  */
public class RunnablePrinter
implements Runnable {

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

