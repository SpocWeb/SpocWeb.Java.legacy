package streamIO.integer.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import streamIO.integer.AStreamOutByte;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.filter.FilterByte;
import function.string.Char2String;
import function.string.StringFunction;

/**
  * Title: FilterChar2String<p>
  * Description:
  * Recodes the Characters coming through this Input streamIO
  * by looking up their Values in a String[] Array.
  * This is NOT a Size-preserving Transformation!
  *
  * Example Byte Arrays are given for XML Entities.
  * Only '<' and '&' (plus " and ' in Attributes) Markup needs to be escaped in XML!
  * Three other Markup Characters are defined that can, but must not be escaped:
  * '>', "'" and '"' need only be escaped, when in Danger of being recognized as Markup.
  *
  * Other XML Markup can encode ALL printable Latin-1 (ISO-8859-1) Characters up to 255,
  * so they can be written in UTF-8 with single Byte Unicode Characters!
  * Other Alternatives for german Umlaut Characters is their standard Replacement:
  * ss for ß
  * ae for ä
  * oe for ö
  * ue for ü
  *
  * @see FilterChar2String uses readable Entity Names, that require DTD Declaration
  * @see FilterChar2Entity uses hex or decimal Entities, that don't need Declaration
  *
  * Usually you would chain FilterChar2String and FilterChar2Entity so
  * that first FilterChar2String is applied, eliminating all the simple Characters.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2002-02-17, 12;08;22<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class FilterChar2String
extends FilterByte {
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** If the start or stop Character are this, they are not printed
	 * This makes later Decoding ambiguous, but enables direct String Replacement
	 */
	final static public char NON_PRINT = 0;
	
	////////////////////////////////////////////////////////////////////////////////
	/// Factory Methods for basic XML Output Encoding (not usable for byte Streams)
	////////////////////////////////////////////////////////////////////////////////
	
	/** XML String Encoding Filter Factory  */
	final static public IStreamOutByte GET_XML_ENCODER(final OutputStream out) {
		return new FilterChar2String(out, StringFunction.XML_ENTITY_ENCODER, StringFunction.XML_ENTITY_START, StringFunction.XML_ENTITY_STOP); }
	
	/** XML String Encoding Filter Factory  */
	final static public IStreamOutByte GET_XML_ENCODER(final InputStream in) {
		return new FilterChar2String(in, StringFunction.XML_ENTITY_ENCODER, StringFunction.XML_ENTITY_START, StringFunction.XML_ENTITY_STOP); }
	
	/** XML String Encoding Filter Factory  */
	final static public IStreamOutByte GET_XML_ENCODER(final IStreamIn_Byte in) {
		return new FilterChar2String(in , StringFunction.XML_ENTITY_ENCODER, StringFunction.XML_ENTITY_START, StringFunction.XML_ENTITY_STOP); }
	
	/** XML String Encoding Filter Factory  */
	final static public IStreamOutByte GET_XML_ENCODER(final IStreamOutByte out) {
		return new FilterChar2String(out, StringFunction.XML_ENTITY_ENCODER, StringFunction.XML_ENTITY_START, StringFunction.XML_ENTITY_STOP); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// Factory Methods for complete XML Output Encoding of the Latin-1 Character Set
	////////////////////////////////////////////////////////////////////////////////
	
	/** XML String Encoding Filter Factory  */
	final static public IStreamOutByte GET_LATIN1_ENCODER(final OutputStream out) {
		return new FilterChar2String(out, StringFunction.LATIN1_ENTITY_ENCODER, StringFunction.XML_ENTITY_START, StringFunction.XML_ENTITY_STOP); }
	
	/** XML String Encoding Filter Factory  */
	final static public IStreamOutByte GET_LATIN1_ENCODER(final InputStream in) {
		return new FilterChar2String(in, StringFunction.LATIN1_ENTITY_ENCODER, StringFunction.XML_ENTITY_START, StringFunction.XML_ENTITY_STOP); }
	
	/** XML String Encoding Filter Factory  */
	final static public IStreamOutByte GET_LATIN1_ENCODER(final IStreamIn_Byte in) {
		return new FilterChar2String(in , StringFunction.LATIN1_ENTITY_ENCODER, StringFunction.XML_ENTITY_START, StringFunction.XML_ENTITY_STOP); }
	
	/** XML String Encoding Filter Factory  */
	final static public IStreamOutByte GET_LATIN1_ENCODER(final IStreamOutByte out) {
		return new FilterChar2String(out, StringFunction.LATIN1_ENTITY_ENCODER, StringFunction.XML_ENTITY_START, StringFunction.XML_ENTITY_STOP); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// Factory Methods for Output German Umlaut Encoding
	////////////////////////////////////////////////////////////////////////////////
	
	/** German Umlaut String Encoding Filter Factory  */
	final static public IStreamOutByte GET_UMLAUT_ENCODER(final OutputStream out) {
		return new FilterChar2String(out, StringFunction.GERMAN_CHAR_ENCODER, NON_PRINT, NON_PRINT); }
	
	/** German Umlaut String Encoding Filter Factory  */
	final static public IStreamOutByte GET_UMLAUT_ENCODER(final InputStream in) {
		return new FilterChar2String(in , StringFunction.GERMAN_CHAR_ENCODER, NON_PRINT, NON_PRINT); }
	
	/** German Umlaut String Encoding Filter Factory  */
	final static public IStreamOutByte GET_UMLAUT_ENCODER(final IStreamIn_Byte in) {
		return new FilterChar2String(in , StringFunction.GERMAN_CHAR_ENCODER, NON_PRINT, NON_PRINT); }
	
	/** German Umlaut String Encoding Filter Factory  */
	final static public IStreamOutByte GET_UMLAUT_ENCODER(final IStreamOutByte out) {
		return new FilterChar2String(out, StringFunction.GERMAN_CHAR_ENCODER, NON_PRINT, NON_PRINT); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// Factory Methods for complete and readable Input Encoding to Characters less than 0x80
	////////////////////////////////////////////////////////////////////////////////
	
	/** Full XML String Encoding Filter Factory  */
	final static public IStreamOutByte GET_ENCODER(final OutputStream out) {
		return FilterChar2Entity.GET_XML_ENCODER(
		GET_LATIN1_ENCODER(
		GET_UMLAUT_ENCODER(out)));
	}
	
	/** Full XML String Encoding Filter Factory  */
	final static public IStreamOutByte GET_ENCODER(final InputStream in) {
		return FilterChar2Entity.GET_XML_ENCODER(
		GET_LATIN1_ENCODER(
		GET_UMLAUT_ENCODER(in)));
	}
	
	/** Full XML String Encoding Filter Factory  */
	final static public IStreamOutByte GET_ENCODER(final IStreamIn_Byte in) {
		return FilterChar2Entity.GET_XML_ENCODER(
		GET_LATIN1_ENCODER(
		GET_UMLAUT_ENCODER(in)));
	}
	
	/** Full XML String Encoding Filter Factory  */
	final static public IStreamOutByte GET_ENCODER(final IStreamOutByte out) {
		return FilterChar2Entity.GET_XML_ENCODER(
		GET_LATIN1_ENCODER(
		GET_UMLAUT_ENCODER(out)));
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
		
	/** The current String on acting as an Input streamIO	 */
	protected Char2String encoding;
	
	/** The current Position on acting as an Input streamIO	 */
	protected int currPos = -1;
	
	/** The current String on acting as an Input streamIO	 */
	protected String currString;
	
	/** Start Character for a String encoded Section */
	protected char start;
	
	/** Stop Character for a String encoded Section */
	protected char stop;
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Constructor */
	public FilterChar2String(IStreamIn_Byte streamIn_, Char2String encoding_, char start_, char stop_) {
		super(streamIn_);
		encoding = encoding_;
		start = start_;
		stop = stop_;
	}

	/** Constructor */
	public FilterChar2String(InputStream streamIn_, Char2String encoding_, char start_, char stop_) {
		super(streamIn_);
		encoding = encoding_;
		start = start_;
		stop = stop_;
	}

	/** Constructor */
	public FilterChar2String(IStreamOutByte streamOut, Char2String encoding_, char start_, char stop_) {
		super(streamOut);
		encoding = encoding_;
		start = start_;
		stop = stop_;
	}

	/** Constructor */
	public FilterChar2String(OutputStream streamOut, Char2String encoding_, char start_, char stop_) {
		super(streamOut);
		encoding = encoding_;
		start = start_;
		stop = stop_;
	}

	/** Constructor
	 * @param chars_ The Characters to be encoded
	 * @param strings_ The Strings to encode the Characters
	 */
	public FilterChar2String(IStreamIn_Byte streamIn_, char[] chars_, String[] strings_, char start_, char stop_) {
		this(streamIn_, new Char2String(chars_, strings_), start_, stop_); }

	/** Constructor	 */
	public FilterChar2String(InputStream streamIn_, char[] chars_, String[] strings_, char start_, char stop_) {
		this(streamIn_, new Char2String(chars_, strings_), start_, stop_); }

	/** Constructor
	 */
	public FilterChar2String(IStreamOutByte streamOut_, char[] chars_, String[] strings_, char start_, char stop_) {
		this(streamOut_, new Char2String(chars_, strings_), start_, stop_); }

	/** Constructor	 */
	public FilterChar2String(OutputStream streamOut_, char[] chars_, String[] strings_, char start_, char stop_) {
		this(streamOut_, new Char2String(chars_, strings_), start_, stop_); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn_Byte: abstract Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Writes the specified byte to this output stream.
	  * The general contract for write is that one byte is written to the output stream.
	  * The byte to be written is the eight low-order bits of the argument b.
	  * The 24 high-order bits of b are ignored.
	  *
		  * Subclasses of InputStream must provide an implementation for this method.
	  *
	  * @param b - the byte.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the output stream has been closed.
	  */
	public int read() throws IOException {
		if (currString != null) {
			if (++currPos < currString.length()) {
				return currString.charAt(currPos); }
			currString = null;
			currPos = -1;
			if (stop != NON_PRINT) {
				return(stop); } //simply comment this out when String is included completely!
		}
		int chr = streamIn.read();
		if (null == (currString = encoding.Map(chr))) {
			return chr; }
		if (start != NON_PRINT) {
			return start; }
		return read(); //simply comment this out when String is included completely!
	}

	/**
	  * Writes the specified byte to this output stream.
	  * The general contract for write is that one byte is written to the output stream.
	  *
	  * @param b - the byte.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the output stream has been closed.
	  */
	public void write(final int chr) throws IOException {
		String str;
		if (null == (str = encoding.Map(chr))) {
			streamOut.write(chr); return; }
		if (start != NON_PRINT) 
			streamOut.write(start); //simply comment this out when String is included completely!
		AStreamOutByte.WRITE(streamOut, str);
		if (stop != NON_PRINT) 
			streamOut.write(stop); //simply comment this out when String is included completely!
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt() throws Exception {
		System.out.println("Testing " + FilterChar2String.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		testIt(args); }

}

