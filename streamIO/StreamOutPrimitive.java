package streamIO;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import streamIO.integer.AStreamOutByte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.IStreamOutChar;
import streamIO.integer.IStreamOutInt;
import streamIO.integer.IStreamOutPrimitive;
import streamIO.real.IStreamOutFloat;

/**
  * Title: PrintStreamOut.java<p>
  * Description:
  * Adds the Interfaces IStreamOut, IStreamOutChar and IFormatterOut 
  * to the PrintStream Class. 
  * 
  * Similar Classes: 
  * @see streamIO.object.parser.InputStream2StreamIn which does the Reverse: 
  * parsing an Input Stream of Bytes into a Stream of Strings. 
  * 
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-09, 09;54;18<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class StreamOutPrimitive
extends PrintStream //, AStreamOut {
implements IFormatOut, IStreamOutByte, IStreamOut, IStreamOutFloat, IStreamOutInt, IStreamOutPrimitive {
	
	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the Output streamIO, obsolete, using the out of the parent Class PrintStream. 	 */
	//protected PrintStream out;
	
	/** Separator used to separate the Entries */
	final static public String DEFAULT_SEPARATOR = "|";
	
	/** Separator used to separate the Entries
	 * defined as String to avoid having to call toString() repeatedly!  
	 * TODO: To avoid using the Separator accidently in Outputs, 
	 * you should define the first Character of the Separator as Escape Character 
	 * and prepend it to any internal Occurrences and strip it when writing the Separator. 
	 */
	public String separator;
	
	/** To allow for obtaining the Result from the inner Stream	 */
	public String toString() { return out.toString(); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor defaulting Output to System.out	 */
	public StreamOutPrimitive() { this(System.out, DEFAULT_SEPARATOR); }
	
	/** Empty Constructor defaulting Output to System.out	 */
	public StreamOutPrimitive(final String _separator) { this(System.out, _separator); }
	
	/** Initializing Constructor	 */
	public StreamOutPrimitive(final StringBuffer buffer, final String _separator) {
		this(new StringBufferOutputStream(buffer), _separator); 
	}
	
	/** Initializing Constructor	 */
	public StreamOutPrimitive(final StringBuffer buffer) { this(buffer, DEFAULT_SEPARATOR); }
	
	/** Initializing Constructor	 */
	public StreamOutPrimitive(final PrintStream out_, final String _separator) {
		super(out_);
		this.out = out_;
		this.separator = _separator; }
	
	/** Initializing Constructor	 */
	public StreamOutPrimitive(final OutputStream out_, final String _separator) {
		super(out_);
		//this.out = new PrintStream(out_);
		this.separator = _separator; }
	
	/** Initializing Constructor	 */
	public StreamOutPrimitive(final OutputStream out) { this(out, DEFAULT_SEPARATOR); }
	
	/** Initializing Constructor	 */
	public StreamOutPrimitive(final PrintStream out) { this(out, DEFAULT_SEPARATOR); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamOutChar: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.integer.IStreamOutChar#getStreamOutByte()	 */
	public IStreamOutByte getStreamOutByte() { return this;	} //.out; }

	/** @see streamIO.integer.IStreamOutPrimitive#addChar(char)	 */
	public IStreamOutChar addChar(final char chr) {
		super.print(chr); 
		return this; }

	/** adds this Item to the Store/Log in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	   
	  * @see IStreamOutByte#addBuffer(StringBuffer)
	  */
	public IStreamOutChar addBuffer(final StringBuffer arg) {
		return addBuffer(arg, arg.length()); }
	
	/** adds this Item to the Store/Log in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is. 
	  * @see IStreamOutByte#addBuffer(StringBuffer, int, int)
	  */
	public IStreamOutChar addBuffer(final StringBuffer arg, final int stop) {
		return addBuffer(arg, stop, 0); }
	
	/** adds this Item to the Store/Log in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	   */
	public IStreamOutChar addBuffer(final StringBuffer arg, final int stop, final int start) {
		//print(separator); 
		for(int i = start-1; ++i < stop;)
			print(arg.charAt(i)); 
		return this; 
	}
	
	/** adds this Item to the Store/Log in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	   
	  * @see IStreamOutByte#addBuffer(StringBuffer)
	  */
	public IStreamOutChar addString(final String arg) {
		return addString(arg, arg.length()); }
	
	/** adds this Item to the Store/Log in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is. 
	  * @see IStreamOutByte#addBuffer(StringBuffer, int, int)
	  */
	public IStreamOutChar addString(final String arg, final int stop) {
		return addString(arg, stop, 0); }
	
	/** adds this Item to the Store/Log in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	   */
	public IStreamOutChar addString(final String arg, final int stop, final int start) {
		//print(separator); 
		for(int i = start-1; ++i < stop;)
			print(arg.charAt(i)); 
		return this; 
	}
	
	/** @see streamIO.integer.IStreamOutByte#escapeString(java.lang.String, java.lang.String)	 */
	public IStreamOutChar escapeString(final String b, final String separators) {
		AStreamOutByte.ESCAPE_SAFE(this, b, separators); 
		return this; }

	/** @see streamIO.integer.IStreamOutByte#escapeString(java.lang.StringBuffer, java.lang.String)	 */
	public IStreamOutChar escapeString(final StringBuffer b, final String separators) {
		AStreamOutByte.ESCAPE_SAFE(this, b, separators); 
		return this; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// Writer Interface
	////////////////////////////////////////////////////////////////////////////////
	
	/** adds this Item to the Store/Log in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	   */
	public StreamOutPrimitive println(final StringBuffer arg) {
		return println(arg, 0, arg.length()); }
	
	/** adds this Item to the Store/Log in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	   */
	public StreamOutPrimitive println(final StringBuffer arg, final int start) {
		return println(arg, start, arg.length()); }
	
	/** adds this Item to the Store/Log in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	   */
	public StreamOutPrimitive println(final StringBuffer arg, final int start, final int stop) {
		addBuffer(arg, stop, start); 
		println(); 
		return this; 
	}
	
	/** adds this Item to the Store/Log in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	   */
	public IIStreamOut addItem (final Object _arg) {
		if ((_arg == null) || !_arg.getClass().isArray()) {
			final String arg = String.valueOf(_arg);
			if (arg == _arg) {
				addString(arg); 
			} else {
				print(separator); 
				super.print(arg); 
			}
		} else //and allows for flatDepth = 0
			addItems(_arg);
		return this; }
	
	/** adds these Items to the Store in Place: +=
	  * The Type of Item is analyzed, i.e. Containers Contents is added,
	  * but not recursively, but only flattened by one Level (flatDepth == 1).	  */
	public long addItems(final Object arg) { return AStreamOut.ADD_ITEMS(this, arg, 3); }
	
	/** adds these Items to the Store in Place: +=
	  * The Type of Item is analyzed, i.e. Containers Contents is added,
	  * but only recursively, when flattened is true.	  */
	public long addItems(final Object arg, final int flatDepth) { return AStreamOut.ADD_ITEMS(this, arg, flatDepth); }
	
	/** adds these Items to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	  */
	public long addItems(final Object[] arg) { return AStreamOut.ADD_ITEMS(this, arg); }
	
	/** adds all Items from the Enumerator to the Store in Place: +=
	 * The Type of Item is not analyzed, i.e. Containers are added as is.	   */
	public long addItems(final IIStreamIn Iter) { return AStreamOut.STREAM(Iter, this); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface FormatterOut: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns a new Instance of this Formatter Class using the given OutPut streamIO	 */
	public IFormatOut newInstance(final OutputStream OS) { return new StreamOutPrimitive(OS); }
	
	/** Returns a new Instance of this Formatter Class using the given Print streamIO	 */
	public IFormatOut newInstance(final PrintStream PS) { return new StreamOutPrimitive(PS); }
	
	/** @see streamIO.integer.IStreamOutInt#addInt(int)	 */
	public IStreamOutInt addInt(final int b) {
		super.print(b); 
		return this; }
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	public IStreamOutInt addLong(final long b) {
		super.print(b); 
		return this; }
	
	/** @see streamIO.real.IStreamOutFloat#addFloat(float)	 */
	public IStreamOutFloat addFloat(final float value) {
		super.print(value); 
		return this; }
	
	/** @see streamIO.real.IStreamOutFloat#addDouble(double)	 */
	public IStreamOutFloat addDouble(double value) {
		super.print(value); 
		return this; }
	
	/** @see streamIO.integer.IStreamOutPrimitive#addBool(boolean)	 */
	public IStreamOutPrimitive addBool(final boolean value) {
		super.print(value); 
		return this; }
	
	/** @see streamIO.integer.IStreamOutByte#write(char[])	 */
	public void write(char[] b) { super.print(b); }

	/** @see streamIO.integer.IStreamOutByte#write(char[], int, int)	 */
	public void write(char[] b, int off, int len) throws IOException {
		for (int i = -1; ++i < len;) 
			super.print(b[off+i]); 
	}
	
	/** @see streamIO.integer.IStreamOutByte#write(java.lang.String)	 */
	public void write(final String b) { this.print(b); }

	/** @see streamIO.integer.IStreamOutByte#write(java.lang.String, int, int)	 */
	public void write(final String b, final int off, int len) {
		len += off;
		for(int i = off-1; ++i < len;)
			super.print(b.charAt(i)); 
	}

}
