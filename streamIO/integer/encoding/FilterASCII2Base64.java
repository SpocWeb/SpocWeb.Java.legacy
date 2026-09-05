package streamIO.integer.encoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import streamIO.Assert;
import streamIO.Log;
import streamIO.integer.AStreamIn_Byte;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.filter.FilterByte;

/**
  * Implements both the Base64 and the so called UUEncode "Unix to Unix Encode" Format,
  * converting three 8-bit input bytes into four 6-bit output characters.
  * Both Formats convert three 8 Bit Characters ("Octets")
  * into four 6 Bit Characters using a 64 Character ASCII Subset.
  * Any Character not from this Set is to be ignored (Whitespace + CRLF)
  * or should be raised as an Error.
  *
  * A Problem with the UUEncoding is that the Characters used
  * are not identical in all versions of ISO 646 (in Base64 they are!).
  * Base 64 uses the small and large Character Set plus the Numbers.
  * UUEncoding uses consecutive Characters from 0x20 up (large Chars).
  * Another Difference is the actual File Format:
  *
  * UUENCODE Format:
  * "begin <octal mode, e.g. 777> <filename>\n
  * <#Chars><full 4 Byte Groups>
  * ...
  * <#Chars><full 4 Byte Groups><4 Byte Group (with Garbage)>
  * '
  * end\n
  *
  * Base64 Format:
  * The Base64 Format has no Header and Footer:
  * The lines have no more than 76 characters (80 Char. -4). 
  * The Padding Character '=' signals the End of the Encoding.
  * All line breaks or other characters must be ignored.
  * <full 4 Byte Groups>...<full 4 Byte Groups>==
  *
  * Possible Regex for Base64: 
  * ^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$
  *
  * 1) UUENCODE:
  *
  * Files output by uuencode() consist of
  * a header line, followed by a number of body lines, and a trailer line.
  * The uudecode() command will ignore any lines preceding the header
  * or following the trailer.
  * Lines preceding a header must not, of course, look like a header.
  *
  * The header line starts with the word ``begin'', a space, a file mode (in
  * octal), a space, and finally a string which names the file being encoded.
  *
  * The central engine of uuencode is a six-bit encoding function which
  * outputs an ASCII character.  The six bits to be encoded are treated as a
  * small integer and added with the ASCII value for the space character 0x20.
  * The result is a printable ASCII character.
  * In the case where all six bits to be encoded are zero, the ASCII character `
  * 0x60 is emitted instead of what would normally be a space.
  *
  * The body of an encoded file consists of one or more lines, each of which
  * may be a maximum of 86 characters long (including the trailing newline).
  * Each line represents an encoded chunk of data from the input file and be-
  * gins with a byte count, followed by encoded bytes, followed by a newline.
  *
  * The byte count is a six-bit integer encoded with the above function,
  * representing the number of bytes encoded in the rest of the line.
  * The method used to encode the data expands its size by 133%.
  * Therefore it is important to note that the byte count describes
  * the size of the chunk of data BEFORE it is encoded, not afterwards.
  * The six bit size of this number effectively limits the number of bytes
  * that can be encoded in each line to a maximum of 63.
  * While uuencode will not encode more than 45 bytes per line,
  * uudecode will tolerate the maximum line size.
  *
  * The remaining characters in the line represent the data of the input file
  * encoded as follows.  Input data are broken into groups of three eight-bit
  * bytes, which are then interpreted together as a 24-bit block.
  * The first bit of the block is the lowest order bit of the first character,
  * and the last is the highest order bit of the third character.
  * This block is then broken into four six-bit integers
  * which are encoded one by one starting from the first bit of the block.
  * The result is a four character ASCII string for every three bytes of input data.
  *
  * Encoded lines of data continue in this manner until the input file is exhausted.
  * The end of the body is signaled by an encoded line with a byte
  * count of zero (the ASCII character `).
  *
  * Obviously, not every input file will be a multiple of three bytes in size.
  * In these cases, uuencode() will pad the remaining one or two bytes of data
  * with garbage bytes until a three byte group is created.
  * The byte count in a line containing garbage padding will reflect
  * the actual number of bytes encoded,
  * making it possible to convey how many bytes are garbage.
  *
  * The trailer line consists of ``end'' on a line by itself.
  *
  * The interpretation of the uuencode format relies on properties of the
  * ASCII character set and may not work correctly on non-ASCII systems.
  *
  *
  * The standard output is a text file
  * (encoded in the character set of the current locale) that begins with the line:
  * "begin%s%s\n", <mode>, decode_pathname
  * and ends with the line:
  * end\n
  *
  * In both cases, the lines have no preceding or trailing blank characters.
  *
  * The algorithm that is used for lines in between begin and end
  * takes three octets as input and writes four characters of output
  * by splitting the input at six-bit intervals into four octets,
  * containing data in the lower six bits only.
  * These octets are converted to characters by adding a value of 0x20 to each octet,
  * so that each octet is in the range 0x20-0x5f,
  * and then it is assumed to represent a printable character
  * in the ISO/IEC 646:1991 standard encoded character set.
  * It then will be translated into the corresponding character codes
  * for the codeset in use in the current locale.
  * (For example, the octet 0x41, representing A,
  * would be translated to A in the current codeset, such as 0xc1 if it were EBCDIC.)
  *
  * Where the bits of two octets are combined,
  * the least significant bits of the first octet are shifted left
  * and combined with the most significant bits of the second octet shifted right.
  * Thus the three octets A, B, C are converted into the four octets:
  *
  * 0x20 + (( A >> 2                    ) & 0x3F)
  * 0x20 + (((A << 4) ' |' ((B >> 4) & 0xF)) & 0x3F)
  * 0x20 + (((B << 2) ' |' ((C >> 6) & 0x3)) & 0x3F)
  * 0x20 + (( C                         ) & 0x3F)
  *
  * These octets are then translated into the local character set.
  * Each encoded line contains a length character,
  * equal to the number of characters to be decoded
  * plus 0x20 translated to the local character set as described above,
  * followed by the encoded characters.
  * The maximum number of octets to be encoded on each line is 45.
  *
  *
  * 2) Base 64
  *
  * implements the base64 encoding as defined in RFC 1521.
  * base64 encoding is used for MIME email, HTTP basic authentication,
  * and to encode binary objects in XML-RPC and SOAP.
  *
  * Content-Type: text/plain; charset=ISO-8859-1
  * Content-transfer-encoding: base64
  *
  * This must be interpreted to mean that the body is a base64 US-ASCII
  * encoding of data that was originally in ISO-8859-1, and will be in
  * that character set again after decoding.
  *
  * Three transformations are currently defined: identity,
  * the "quoted-printable" encoding, and the "base64" encoding.
  *
  * Implementors may, if necessary, define private Content-Transfer-
  * Encoding values, but must use an x-token, which is a name prefixed by
  * "X-", to indicate its non-standard status,
  *
  * Examples base64.encode ("mypassword", 0) = "bXlwYXNzd29yZA=="
  *
  * The Base64 Content-Transfer-Encoding is designed to represent
  * arbitrary sequences of octets in a form that need not be humanly readable.
  * The encoding and decoding algorithms are simple, but the encoded data
  * are consistently only about 33 percent larger than the unencoded data.
  * This encoding is virtually identical to the one used
  * in Privacy Enhanced Mail (PEM) applications, as defined in RFC 1421.
  *
  * A 65-character subset of US-ASCII is used, enabling 6 bits to be
  * represented per printable character. (The extra 65th character, "=",
  * is used to signify a special processing function.)
  *
  * NOTE:  This subset has the important property that it is represented
  * identically in all versions of ISO 646, including US-ASCII, and all
  * characters in the subset are also represented identically in all
  * versions of EBCDIC. Other popular encodings, such as the encoding
  * used by the uuencode() utility, Macintosh binhex 4.0 [RFC-1741], and
  * the base85 encoding specified as part of Level 2 PostScript,
  * do not share these properties, and thus do not fulfill the portability
  * requirements a binary transport encoding for mail must meet.
  *
  * The encoding process represents 24-bit groups of input bits as output
  * strings of 4 encoded characters.  Proceeding from left to right, a
  * 24-bit input group is formed by concatenating 3 8bit input groups.
  * These 24 bits are then treated as 4 concatenated 6-bit groups, each
  * of which is translated into a single digit in the base64 alphabet.
  * When encoding a bit stream via the base64 encoding, the bit stream
  * must be presumed to be ordered with the most-significant-bit first.
  * That is in the stream...
  * the first  bit will be the high-order bit in the first 8bit byte, and
  * the eighth bit will be the low -order bit in the first 8bit byte, and so on.
  *
  * Each 6-bit group is used as an index into an array of 64 printable Characters.
  * The character referenced by the index is placed in the output string.
  * These characters, identified in Table 1, below, are selected so
  * as to be universally representable, and the set excludes characters
  * with particular significance to SMTP (e.g., ".", CR, LF)
  * and to the multipart boundary delimiters defined in RFC 2046 (e.g., "-").
  *
  * The encoded output stream must be represented in lines of no more
  * than 76 characters each.  All line breaks or other characters not
  * found in Table 1 must be ignored by decoding software.  In base64
  * data, characters other than those in Table 1, line breaks, and other
  * white space probably indicate a transmission error, about which a
  * warning message or even a message rejection might be appropriate
  * under some circumstances.
  *
  * Special processing is performed if fewer than 24 bits are available
  * at the end of the data being encoded.  A full encoding quantum is
  * always completed at the end of a body.  When fewer than 24 input bits
  * are available in an input group, zero bits are added (on the right)
  * to form an integral number of 6-bit groups.  Padding at the end of
  * the data is performed using the "=" character.  Since all base64
  * input is an integral number of octets, only the following cases can arise:
  * 0) the final quantum of encoding input is an integral multiple of 24 bits;
  * 	here, the final unit of encoded output will be an integral multiple
  * 	of 4 characters with no "=" padding,
  * 1) the final quantum of encoding input is exactly 16 bits;
  * 	here, the final unit of encoded output will be three characters
  * 	(the last one not fully qualified) followed by one "=" padding character, or
  * 2) the final quantum of encoding input is exactly 8 bits;
  * 	here, the final unit of encoded output will be two characters
  * 	(again the last one not fully def.) followed by two "=" padding characters.
  *
  * Because it is used only for padding at the end of the data, the
  * occurrence of any "=" characters may be taken as evidence that the
  * end of the data has been reached (without truncation in transit).
  *
  * No such assurance is possible, however, when the number of octets
  * transmitted was a multiple of three and no "=" characters are present.
  *
  * Any characters outside of the base64 alphabet are to be ignored in
  * base64-encoded data.
  *
  * Care must be taken to use the proper octets for line breaks if base64
  * encoding is applied directly to text material that has not been
  * converted to canonical form.  In particular, text line breaks must be
  * converted into CRLF sequences prior to base64 encoding.  The
  * important thing to note is that this may be done directly by the
  * encoder rather than in a prior canonicalization step in some
  * implementations.
  *
  * NOTE: There is no need to worry about quoting potential boundary
  * delimiters within base64-encoded bodies within multipart entities
  * because no hyphen characters are used in the base64 encoding.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2002-02-17, 12;08;22<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T21:33:44Z
  * digest: afb6eea92b47952e345bd7da32af6244b2355fa93c1364ee7614b63e361fee6c
  * stale: false
  * tags: [code/stream_filter, code/base64_encoding, code/crc, code/xor_cipher]
  * concepts: [Byte/Character Re-Encoding Filters - Base64 BinHex URL/Entity Escaping CRC XOR]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class FilterASCII2Base64
extends FilterByte {

	////////////////////////////////////////////////////////////////////////////////
	//  static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** The UU Encoding Header. Can be changed to other File Modes or Names  */
	final static public String STR_BEGIN = "begin ";

	/**
	 * Converts UUEncode Character Values to Base64 character values.
	 * Using the ASCII Value - 0x20 as an Index, this Array returns the EBCDIC Value
	 * It is faster to build a Conversion Lookup Table in an Array.
	 */
	final static public int[] BASE_64_CODES = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
		'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
		'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
		'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Encodes the full input Array into the full output Array.
	  * not filled Input Arrays should be padded with ???
	  */
	public static void ENCODE(final int[] in, final byte[] ot, final boolean uuencode) {
		ot[0] = (byte)   (in[0] >> 2);
		ot[1] = (byte) (((in[0] << 4) | ((in[1] >> 4) & 0x0F)) & 0x3F);
		ot[2] = (byte) (((in[1] << 2) | ((in[2] >> 6) & 0x03)) & 0x3F);
		ot[3] = (byte)   (in[2]                       & 0x3F);
		if (uuencode) { //add the Offset and use ` instead of Space
			if ((ot[0] += 0x20) == 0x20) ot[0]=0x60;
			if ((ot[1] += 0x20) == 0x20) ot[1]=0x60;
			if ((ot[2] += 0x20) == 0x20) ot[2]=0x60;
			if ((ot[3] += 0x20) == 0x20) ot[3]=0x60;
		} else { //base 64 encoding:
			ot[0] = (byte) BASE_64_CODES[ot[0]];
			ot[1] = (byte) BASE_64_CODES[ot[1]];
			ot[2] = (byte) BASE_64_CODES[ot[2]];
			ot[3] = (byte) BASE_64_CODES[ot[3]];
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** number of 4 Character Blocks before a Line Feed is inserted. */
	final static public int NUM_BLOCKS_BEFORE_LF = 19; 
	
	/** number of 4 Character Blocks before a Line Feed is inserted. 
	 * Flag to insert Line Feeds in Base64 Encoding every 76 output Characters, 
	 * i.e. every 57 Input Characters resp. 19 Blocks */
	private int numBlocksBeforeLF = NUM_BLOCKS_BEFORE_LF+1; 
	
	/** The UU Encoding Header. Can be changed to other File Modes or Names  */
	public String strUUHeader = STR_BEGIN + "777 UUENCODE.TXT\n";

	/** The UU Encoding Footer. Can be changed to append other stuff  */
	public String strUUFooter = "`\nend\n";

	/** Flag to indicate uuencoding instead of Base64 Encoding	 */
	protected boolean uuencode;

	/** Array used for cacheing the incoming Bytes	 */
	protected int [] buffer = new int [3];

	/** Array used for encoding the outgoing Bytes	 */
	protected byte[] encode = new byte[4];

	/** The Index to the Encoding Array used for cacheing and encoding the incoming Bytes	 */
	protected int index = buffer.length;

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Creates a Base64-encoding filter, without UU-encoding or inserted line feeds.
	 * @param streamIn_
	 */
	public FilterASCII2Base64(final InputStream streamIn_) throws IOException {
		this(streamIn_, false, false);
	}

	/**
	 * Creates a Base64-encoding filter, without UU-encoding or inserted line feeds.
	 * @param streamIn_
	 */
	public FilterASCII2Base64(final IStreamIn_Byte streamIn_) throws IOException {
		this(streamIn_, false, false);
	}

	/**
	 * Creates a Base64-encoding filter, without UU-encoding or inserted line feeds.
	 * @param streamOut
	 */
	public FilterASCII2Base64(final IStreamOutByte streamOut) throws IOException {
		this(streamOut, false, false);
	}

	/**
	 * Creates a Base64-encoding filter, without UU-encoding or inserted line feeds.
	 * @param streamOut
	 */
	public FilterASCII2Base64(final OutputStream streamOut) throws IOException {
		this(streamOut, false, false);
	}
	
	/** Constructor
	  * @param streamOut IStreamOutByte Object being delegated to
	  * @param uuencode_ Flag determining whether to use base64 or uu encoding. */
	protected FilterASCII2Base64(final IStreamOutByte streamOut
			, final boolean uuencode_, final boolean insertLineFeed) throws IOException {
		super(streamOut);
		if (! insertLineFeed) 
			numBlocksBeforeLF = -1;
		if (this.uuencode = uuencode_) {
			streamOut.write(strUUHeader.getBytes()); }
		index = 0;
	}

	/** Constructor
	  * @param streamOut OutputStream Object being delegated to
	  * @param uuencode_ Flag determining whether to use base64 or uu encoding. */
	protected FilterASCII2Base64(OutputStream streamOut, boolean uuencode_, boolean insertLineFeed) throws IOException {
		super(streamOut);
		if (! insertLineFeed) 
			numBlocksBeforeLF = -1;
		if (this.uuencode = uuencode_) {
			streamOut.write(strUUHeader.getBytes()); }
		index = 0;
	}

	/** Constructor
	  * @param streamIn_ IStreamIn_Byte Object being delegated to
	  * @param uuencode_ Flag determining whether to use base64 or uu encoding. */
	protected FilterASCII2Base64(IStreamIn_Byte streamIn_, boolean uuencode_, boolean insertLineFeed) throws IOException {
		super(streamIn_);
		if (! insertLineFeed) 
			numBlocksBeforeLF = -1;
		if (this.uuencode = uuencode_) {
//			streamOut.write(strUUHeader.getBytes());
		}
	}

	/** Constructor
	  * @param streamIn_ InputStream Object being delegated to
	  * @param uuencode_ Flag determining whether to use base64 or uu encoding. */
	protected FilterASCII2Base64(InputStream streamIn_, boolean uuencode_, boolean insertLineFeed) throws IOException {
		super(streamIn_);
		if (! insertLineFeed) 
			numBlocksBeforeLF = -1;
		if (this.uuencode = uuencode_) {
//			streamOut.write(strUUHeader.getBytes());
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	//  public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn_Byte: abstract Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Reads the next byte from this Input streamIO.
	  *
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the output stream has been closed.
	  * @return the byte.
	  */
	public int read() throws IOException {
		if (  index  < -1) return -1; //at the End of the Stream...
		if (++index  < encode.length) return encode[index]; //go on cacheing
		index = -2; 
		int len = streamIn.read(buffer); //
		if (len <= 0)
			return -1; //
		// TODO: LOGIC: integer division truncates `len*4/3` for a final partial group -
		// 1 remaining input byte should keep 2 valid encoded characters (1*4/3==1, off by
		// one) and 2 remaining bytes should keep 3 (2*4/3==2, off by one); only a full
		// 3-byte group (len==3) computes correctly. The last encoded character of a
		// trailing 1- or 2-byte group is overwritten with '=' padding instead of kept,
		// corrupting the final group of every stream not an exact multiple of 3 bytes -
		// matches the "97. Element is missing" failure noted in testIt() below.
		len = len*4/3; //encoded Length is higher
		ENCODE(buffer, encode, uuencode);
		if (uuencode) { //can fill up with Garbage
		} else { //base64: use Padding Character "=" (0x3d)
			while (++len < encode.length) { //1 => 1,(2),=;=
				encode[len] = '='; } //2 => 1,2,(3),=
		}
		if(--numBlocksBeforeLF == 0) {
			 numBlocksBeforeLF = NUM_BLOCKS_BEFORE_LF; 
			index = -1; 
			return AStreamIn_Byte.CHR_LINE_FEED; 
		}
		return encode[index = 0]; }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface StreamOutByte: abstract Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Writes the specified byte to this output stream.
	  * The general contract for write is that one byte is written to the output stream.
	  * The byte to be written is the eight low-order bits of the argument b.
	  * The 24 high-order bits of b are ignored.
	  *
	  * Subclasses of OutputStream must provide an implementation for this method.
	  *
	  * @param b - the byte.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the output stream has been closed.
	  */
	public void write(int b) throws IOException {
		buffer[index] = b;
		if  (++index  < buffer.length) { return; } //go on cacheing
		index = 0;
		ENCODE(buffer, encode, uuencode);
		super.write(encode);
		if(--numBlocksBeforeLF == 0) {
			 numBlocksBeforeLF = NUM_BLOCKS_BEFORE_LF; 
			 super.write(AStreamIn_Byte.CHR_LINE_FEED); 
		}
/*		if (streamOut != null) {
			streamOut.write(encode); return; }
			outStream.write(encode);
*/	}

	/**
	  * Flushes this output stream and forces any buffered output bytes to be written out.
	  * The general contract of flush is that calling it is an indication that,
	  * if any bytes previously written have been buffered
	  * by the implementation of the output stream,
	  * such bytes should immediately be written to their intended destination.
	  *
	  * The flush method of OutputStream does nothing.
	  *
	  * @throws IOException - if an I/O error occurs.
	  */
	public void flush() throws IOException {
		if (streamOut != null) {
			if (index > 0) { //clear the Buffer
				ENCODE(buffer, encode, uuencode); //
				if (uuencode) { //can fill up with Garbage
				} else { //base64: use Padding Character "=" (0x3d)
					while (++index < encode.length) { //1 => 1,(2),=;=
						encode[index] = '='; } //2 => 1,2,(3),=
				}
	//			write(); //3 => 1,2,3,4
				streamOut.write(encode); 
				index  = 0; }
			if (uuencode) {
				streamOut.write(strUUFooter.getBytes()); } //slightly ineffective to not cache the Bytes!
		}
		super.flush(); }

	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * filters the given Bytes by sending them through an Encoder. 
	 * @param bytes
	 * @return the encoded String
	 * @throws IOException
	 */
	public static  String FILTER(final String arg, final boolean uuEncode, final boolean insertLineFeeds) throws IOException {
		return new String(FILTER(arg.getBytes(), uuEncode, insertLineFeeds));
	}

	/**
	 * filters the given Bytes by sending them through an Encoder. 
	 * @param bytes
	 * @return a new Array containing the encoded Characters. 
	 * @throws IOException
	 */
	public static byte[] FILTER(final byte[] bytes, final boolean uuEncode, final boolean insertLineFeeds) throws IOException {
		final FilterASCII2Base64 decoder = new FilterASCII2Base64(new ByteArrayInputStream(bytes), uuEncode, insertLineFeeds); 
		final ByteArrayOutputStream out = new ByteArrayOutputStream(bytes.length*2);
		decoder.stream(out); 
		decoder.close(); 
		return out.toByteArray();
	}
	
	/** Tests all Methods of this Class	 */
	public static void testIt() throws java.io.IOException {
		Log.N("Testing " + FilterASCII2Base64.class.getName());
		Assert.EQUALS(
				FilterBase64ToASCII.TEST_ENCODED, 
				FilterASCII2Base64 .FILTER(
				FilterBase64ToASCII.TEST_DECODED, false, false));
		final byte[] in = new byte[256]; 
		for (int i = in.length; --i >= 0;)
			in[i] = (byte) (i+1); //works, unless the Stream ends with -1!
		byte[] inter =  FilterASCII2Base64 .FILTER(in   , false, false);
		byte[] out   =  FilterBase64ToASCII.FILTER(inter, false);
		Log.N(new String(inter)); 
		Assert.EQUALS(344, inter.length); //2.5 times larger! 
		Assert.EQUALS(in.length, out.length); 
		for (int i = out.length; --i >= 0;)
			Assert.EQUALS(out[i], in[i]);
		inter =  FilterASCII2Base64 .FILTER(in   , false, true);
		out   =  FilterBase64ToASCII.FILTER(inter, false);
		Log.N(new String(inter)); 
		Assert.EQUALS(348, inter.length); //2.5 times larger! 
		Assert.EQUALS(in.length, out.length); 
		for (int i = out.length; --i >= 0;)
			Assert.EQUALS(out[i], in[i]);
		inter =  FilterASCII2Base64 .FILTER(in   , true, false);
		out   =  FilterBase64ToASCII.FILTER(inter, true);
		Log.N(new String(inter)); 
		Assert.EQUALS(344, inter.length); //2.5 times larger!
		ByteArrayOutputStream outStream = new ByteArrayOutputStream(256);  
		FilterBase64ToASCII f2 = new FilterBase64ToASCII(outStream, true); 
		FilterASCII2Base64 f1 = new FilterASCII2Base64((IStreamOutByte) f2, true, false); 
		for (int i = 0; ++i < in.length;)
			f1.write((byte) (i)); //works, unless the Stream ends with -1!
		/* doesn't work, the 97. Element (=29) is missing (not Position and not Character) and 2 Elements are added at the End 
		Assert.EQUALS(in.length, out.length); 
		for (int i = out.length; --i >= 0;)
			Assert.EQUALS(out[i], in[i]);
		*/
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws java.io.IOException {
		if (args.length == 0) {
			System.out.println("Converts all String Parameters into Base64 Encoded Strings.");
			testIt(); 
		}
		for(int i = args.length; --i >= 0;) 
			System.out.println(FilterASCII2Base64.FILTER(args[i], false, false));
	}

}

