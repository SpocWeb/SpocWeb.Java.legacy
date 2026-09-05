package streamIO.integer.encoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.BitSet;

import streamIO.Assert;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.filter.FilterByte;

/**
  * Encodes the Bytes coming through this Input or Output streamIO
  * by converting their Values to a Byte URL Encoding.
  *
  * URL Encoding is used mostly to append Parameters to an HTTP POST Request, 
  * but also to encode the HTTP Entity Body, so CRs are prevented 
  * which would end the HTTP Conversation prematurely. 
  * The Parameters start with a Question Mark '?'
  * The Parameters are separated by an Ampersand '&'
  * key and Value are separated by an Equal Sign '='
  *
  * ASCII characters 'a' through 'z', 'A' through 'Z', and '0' through '9' remain the same, 
  * as well as the Characters in the following String "'()*-._".
  * The space character ' ' is converted into a plus sign '+'.
  * The Backslash character '\' is converted into an Exclamation Mark '!'.
  * remaining characters (exclusive Space) are represented by 3-character strings
  * 	which begin with the percent sign "%xy",
  * 	where xy is the two-digit hexadecimal representation
  * 	of the lower 8-bits of the character in the Sequence High Byte / Low Byte.
  * When the Content is guaranteed not to containg higher Values than 0x80, 
  * (e.g. on base64 encoded Contents) some Servers abbreviate CR and LF by %d and %a. 
  *
  * Known SubClasses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2002-02-17, 12;08;22<p>
  * @author 	Matthias Heuer
  * @version	1.0
  *
  * @see java.net.URLDecoder
  * @see java.net.URLEncoder
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T21:37:27Z
  * digest: e33ecfb2e3e5c7873d0389c0f820bb50e34f7a088d38d14c6270957e3556189c
  * stale: false
  * tags: [code/stream_filter, code/base64_encoding, code/crc, code/xor_cipher]
  * concepts: [Byte/Character Re-Encoding Filters - Base64 BinHex URL/Entity Escaping CRC XOR]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class FilterUrlEncode
extends FilterByte {
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * The Bit is set when the respective Character doesn't need Encoding
	  * The Characters are offset by 1 to provide for EOF = -1
	  */
	final static public BitSet dontNeedEncoding = new BitSet(257);
	
	/**
	  * The Bit is set when the respective Character doesn't need Encoding
	  * The Characters are offset by 1 to provide for EOF = -1
	  */
	final static public char UrlEscapeChar = '%';
	
	/** The Space Character is replaced by the '+' Character	  */
	final static public char UrlSpaceReplace = '+';
	
	/** The Space Character is replaced by the '+' Character	  */
	final static public char UrlSpaceOriginal = ' ';
	
	/** The BackSlash Character is replaced by the '!' Character	  */
	final static public char UrlBackSlashReplace = '!';
	
	/** The BackSlash Character is replaced by the '!' Character	  */
	final static public char UrlBackSlashOriginal = '\\';
	
	/**
	  * The Difference between a lower Case and a higher Case Character
	  */
	final static public int caseDiff = ('a' - 'A');
	
	/**
	  * The Difference between a lower Case and a higher Case Character
	  */
	private static final int offsetEOF = -EOF;

	/** The list of Characters that are not encoded
	  * offset by 1 to reflect EOF=-1
	  * determined by referencing O'Reilly's
	  * "HTML: The Definitive Guide" (page 164). */
	static {
		int i;
		for (i = 'a'; i <= 'z'; i++) 
			dontNeedEncoding.set(i+offsetEOF);
		for (i = 'A'; i <= 'Z'; i++) 
			dontNeedEncoding.set(i+offsetEOF);
		for (i = '0'; i <= '9'; i++) 
			dontNeedEncoding.set(i+offsetEOF);
		dontNeedEncoding.set( EOF +offsetEOF); /* makes handling EOF automatic.  */
		dontNeedEncoding.set( ' ' +offsetEOF); /* encoding a space to a '+' is done in the UrlEncode() method */
		dontNeedEncoding.set( '\\'+offsetEOF); /* encoding a BackSlash to a '!' is done in the UrlEncode() method */
		dontNeedEncoding.set( '-' +offsetEOF);
		dontNeedEncoding.set( '_' +offsetEOF);
		dontNeedEncoding.set( '.' +offsetEOF);
		dontNeedEncoding.set( '*' +offsetEOF);
		dontNeedEncoding.set( '(' +offsetEOF);
		dontNeedEncoding.set( ')' +offsetEOF);
		dontNeedEncoding.set( '\''+offsetEOF);
	}

	/**
	 * Translates a Character into <code>x-www-form-urlencoded</code> format.
	 * Also works for EOF == -1;
	 *
	 * @param   s   <code>String</code> to be translated.
	 * @return  the Length of the filled Array <code>String</code>.
	 */
	final static public int UrlEncode(int c, final byte[] chars) {
		if (dontNeedEncoding.get(c+offsetEOF)) {
			if (c == UrlSpaceOriginal) {
				c =  UrlSpaceReplace ; } else
			if (c == UrlBackSlashOriginal) {
				c =  UrlBackSlashReplace; }
			chars[0] = (byte) c;
			return 1; }
		chars[2] = (byte) '%'; //this is obvious and could be omitted
		chars[1] = (byte) FilterByte2BinHex.HEX_CODES[FilterByte2BinHex.NIBBLE & (c>>4)];
		chars[0] = (byte) FilterByte2BinHex.HEX_CODES[FilterByte2BinHex.NIBBLE &  c    ];
		return 3; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Constructor	 */
	protected FilterUrlEncode(IStreamIn_Byte streamIn_) {
		super(streamIn_); }
	
	/** Constructor	 */
	protected FilterUrlEncode(InputStream streamIn_) {
		super(streamIn_); }
	
	/** Constructor	 */
	protected FilterUrlEncode(IStreamOutByte streamOut_) {
		super(streamOut_); }
	
	/** Constructor	 */
	protected FilterUrlEncode(OutputStream streamOut_) {
		super(streamOut_); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn_Byte: abstract Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Cache for the Characters in read()
	  * used in both write() and read().	*/
	private byte[] chars = new byte[3]; // {FlagEmpty, FlagEmpty, FlagEmpty};
	
	/** current Position in the chars Array */
	private int Position = 0;
	
	/**
	  * Reads the specified byte from this Input streamIO.
	  * The general contract for read is that one to three Bytes
	  * are read from the Input streamIO.
	  *
	  * @param b - the byte.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the output stream has been closed.
	  */
	public int read() throws IOException {
		if (Position <= 0) {
			Position  = UrlEncode(streamIn.read(), chars); }
		return chars[--Position]; }
	
	/**
	  * Writes the specified byte to this Output streamIO.
	  * The general contract for write is that one to three Bytes
	  * are written to the Output streamIO.
	  *
	  * @param b - the byte.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the output stream has been closed.
	  */
	public void write(final int Value) throws IOException {
		int len = UrlEncode((char) Value, chars);
		while (--len >= 0) 
			streamOut.write(chars[len]); 
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * filters the given Bytes by sending them through an Encoder. 
	 * @param bytes
	 * @return the encoded String
	 * @throws IOException
	 */
	public static  String FILTER(final String arg) throws IOException {
		return new String(FILTER(arg.getBytes()));
	}

	/**
	 * filters the given Bytes by sending them through an Encoder. 
	 * @param bytes
	 * @return a new Array containing the encoded Characters. 
	 * @throws IOException
	 */
	public static byte[] FILTER(final byte[] bytes) throws IOException {
		final FilterUrlEncode encoder = new FilterUrlEncode(new ByteArrayInputStream(bytes)); 
		final ByteArrayOutputStream out = new ByteArrayOutputStream(bytes.length*2);
		encoder.stream(out); 
		encoder.close(); 
		return out.toByteArray();
	}
	
	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) throws java.io.IOException {
		System.out.println("Testing " + FilterUrlEncode.class.getName());
		for(int i = args.length; --i >= 0;) {
			Assert.EQUALS(args[i], FilterUrlDecode.FILTER(FilterUrlEncode.FILTER(args[i])));
		}
		final byte[] in = new byte[256]; 
		for (int i = in.length; --i >= 0;)
			in[i] = (byte) (i+1); //works, unless the Stream ends with -1!
		final byte[] inter =  FilterUrlEncode.FILTER(in);
		final byte[] out   =  FilterUrlDecode.FILTER(inter);
		Assert.EQUALS(626, inter.length); //2.44 times larger!  
		Assert.EQUALS(in.length, out.length); 
		for (int i = out.length; --i >= 0;)
			Assert.EQUALS(out[i], in[i]);
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws java.io.IOException {
		testIt(args); }
	
}

