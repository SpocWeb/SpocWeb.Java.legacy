package streamIO.integer.encoding;

import java.io.IOException;
import java.io.InputStream;

import streamIO.integer.filter.FilterIn_Byte;

/**
  * Implements a Filter that either inserts or filters out Escape Characters.
  * It is quite ineffective to chain Filters at the Character Level.
  *
  * Design Decisions:
  * There are two ways to filter out unwanted Characters:
  * *One Way is to precede them with the Escape Character.
  *  The Problem here is that each processing routine (also Filters)
  *  have to overlook the following Character.
  * *The other way is to replace it with a different Character (and an Escape Character).
  *  This lets other Processors work on the streamIO in an undisturbed way.
  *
  * Both Usages are possible depending on whether the Strings
  * forbidden and replace are equal or not.
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T21:32:34Z
  * digest: 449f4ba1061fe1494fb646109bcb58d8cfa51ec016596e30d64e73fa050f9f3a
  * stale: false
  * tags: [code/stream_filter, code/base64_encoding, code/crc, code/xor_cipher]
  * concepts: [Byte/Character Re-Encoding Filters - Base64 BinHex URL/Entity Escaping CRC XOR]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class EscapeInputFilter
extends FilterIn_Byte {

	//Class Variables Start

	/** Flag for Encoding or Decoding Operation	 */
	protected boolean EnCode;

	/** Indicator for Replacement, used for Speed Reasons	 */
	protected boolean doReplace;

	/** String with forbidden Characters	 */
	protected String forbidden;

	/** String with replacement Characters	 */
	protected String replace;

	/** The Escape Character	 */
	protected char Escape;

	//Class Variables Stop
	//Constructors Start

	/** Initializing Constructor	 */
	public EscapeInputFilter(InputStream InStream,
							 String forbidden,
							 String replace,
							 char Escape,
							 boolean EnCode) {
		super (InStream);
		this.Escape = Escape;
		this.EnCode = EnCode;
		this.forbidden = forbidden + Escape;
		this.replace = replace;
		// TODO: LOGIC: unlike the sibling EscapeOutputFilter constructor, this omits the
		// `replace.length() < forbidden.length()` guard (see the comment below) - when a
		// caller passes a `replace` string as long as or longer than `forbidden`,
		// `forbidden.substring(replace.length())` throws StringIndexOutOfBoundsException
		// instead of leaving `replace` unpadded.
		if (doReplace = (replace != null)) { // && (replace.length() < forbidden.length()))
			//fill up the Replace String with the rest of the forbidden String.
			replace += forbidden.substring(replace.length());
		}	//that replaces only the beginning Characters and makes it more consistent.
	}

	//Constructors Stop
	//Variables only for the next Method

	/** Flag that the next read Byte has been escaped	 */
	protected boolean escaped;

	/** Buffer for the last read Byte	 */
	protected int Byte;

	/** Returns the next Character from the streamIO. 	 */
	public int read() throws IOException {
		int Index;
		if (EnCode)	{ //adds Escape Characters
			//give out the original Character only when escaped from last time or not a forbidden Character.
			if((escaped) || ((Index = forbidden.indexOf(Byte = super.read())) < 0)) {
				escaped = false; return Byte; }
			if (escaped = !escaped) {
				Byte = replace.charAt(Index); }	//replace Byte for next Time
			return Escape; } //or leave it alone, and return the Escape Character
//		} else { 	//DeCode, removes Escape Characters
			if((Byte = super.read()) == Escape) {	//just skip the Escape Character,
				Byte = super.read(); //and read the next Character
				if ((doReplace) && ((Index = replace.indexOf(Byte)) >= 0)) { //replace the Character
					return forbidden.charAt(Index); } //first find it and then replace it //this is faster, when an Array is used!
			} return Byte; }
			
}
