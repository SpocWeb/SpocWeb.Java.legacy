package streamIO.object.parser;

import java.io.IOException;

import math.IFormatter;
import math.NumberFormatter;
import streamIO.IIStreamIn;
import streamIO.IIStreamOut;
import streamIO.exception.BaseException;
import streamIO.integer.AStreamOutByte;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.file.FileStreamIn_Byte;
import streamIO.integer.file.FileStreamOutByte;
import function.byref.ByRefInt;

/**
  * Title: ByteStreamFormatter<p>
  * Description:
  * Purpose:
  * Streams out the Contents of the Input streamIO to the Output streamIO.
  * The streamIO is parsed by the given Parser Characters
  * and the Formatter formats the Strings read from the Input streamIO
  * before writing them to the Output streamIO.
  *
  *
  * Design Decisions / Implementation Details:
  * @see streamIO.LimitedSizeOutputStream.stream() for a Runnable that streams Bytes
  * @see streamIO.StreamInRunner for a Runnable that streams Objects
  *
  * Implements Interface Runnable
  *     because the stream() Method takes no additional Parameters
  *
  * Uses Class InputStream2StreamIn for parsing the Input streamIO
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	08-06-2001, 04:05 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class ByteStreamFormatter
implements Runnable {

	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Separators applied on the Input streamIO.
	  * Necessary to restore the Characters from the Tokens
	  */
	protected String Separators;

	/** Input streamIO wrapped with a Parser     */
	protected InputStream2StreamIn streamIn;

	/** Output streamIO     */
	protected IStreamOutByte streamOut;

	/** Formatter formatting the Strings read from the Input streamIO before writing them to the Output streamIO    */
	protected IFormatter formatter;

	/** Format for the Formatter    */
	public String Format = null;

	/** Initializing Constructor     */
	public ByteStreamFormatter(final String _Separators,
			final IStreamIn_Byte IS_, final IFormatter formatter_, final IStreamOutByte OS_) 
	throws IOException {
		this.Separators = _Separators;
		this.formatter  = formatter_;
		this.streamOut  = OS_;
		this.streamIn   = new InputStream2StreamIn(IS_, Separators);
		streamIn.clearOnNext = true;
		streamIn.removeLast = true;
	}

	/** streams all Values from the Input streamIO to the Output streamIO. */
	public void stream() throws IOException {
		ByRefInt item;
		StringBuffer strB = (StringBuffer) streamIn.currItem();
		while (IIStreamIn.EOI != (item = streamIn.nextToken())) { //read next Token...
			int len = strB.length();
			if (len > 1) { //remove the Separator from the Token, 
				strB.setLength(len-1); //normally done in currItem()!
				AStreamOutByte.WRITE(streamOut, formatter.format(strB, Format)); }
			if (item.Value < 0) {
				break; }
			streamOut.write(Separators.charAt(item.Value));
		}
	}

	/** Exception Handler e.g. to log Exception thrown during run() */
	final static public IIStreamOut ExceptionHandler = streamIO.Log.L;

	/** streams all Values from the Input streamIO to the Output streamIO. */
	public void run() {
		try { stream();
		} catch (IOException x) {
//			ExceptionHandler.addItem(x); //or use a StreamOut to handle Exceptions
			throw new BaseException("", x); //rethrow a Runtime Exception
		}
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + ByteStreamFormatter.class.getName());
	//	try {
			IStreamIn_Byte in_ = new FileStreamIn_Byte("E:/Personal/Databases/POLYEDER/TRICERAT.PNT");
			IStreamOutByte out = new FileStreamOutByte("E:/Personal/Databases/POLYEDER/TRICERAT1.PNT");
			IFormatter f = new NumberFormatter(); //"\t\n\f\r "
			ByteStreamFormatter BSF = new ByteStreamFormatter("\t\f\r\n ", in_, f, out); //in_.WHITESPACE, in_, f, out);
			BSF.stream();
			in_.close();
			out.close();
	//	} catch (Exception x) {
	//		x.printStackTrace();
	//	}
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }
	
}
