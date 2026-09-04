package streamIO.integer.encoding;

import java.io.IOException;
import java.io.OutputStream;

import streamIO.integer.filter.FilterOutByte;

/** Implements a Filter that either inserts or filters out Escape Characters.
  * It is quite ineffective to chain Filters at the Character Level.
  *
  * Design Decisions:
  * There are two ways to filter out unwanted Characters:
  * *One Way is to precede them with the Escape Character.
  *  The Problem here is that each processing routine (also Filters and Parsers)
  *  have to overlook the following Character.
  * *The other way is to replace it with the Escape Character and a different Character.
  *  This lets other Processors work on the streamIO in an undisturbed way.
  *
  * Both Usages are possible depending on whether the Strings
  * forbidden and replace are equal or not.
  */
public class EscapeOutputFilter
extends FilterOutByte {

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

	/** Initializing Constructor	 */
	public EscapeOutputFilter(OutputStream OutStream,
							  String forbidden,
							  String replace,
							  char Escape,
							  boolean EnCode) {
		super (OutStream);
		this.Escape = Escape;
		this.EnCode = EnCode;
		this.forbidden = forbidden + Escape;
		this.replace = replace;
		if ((doReplace = (replace != null)) && (replace.length() < forbidden.length()))
		{	//fill up the Replace String with the rest of the forbidden String.
			replace += forbidden.substring(replace.length());
		}	//that replaces only the beginning Characters and makes it more consistent.
	}

	/** Flag that the last read Byte has been escaped	 */
	protected boolean escaped;

	/** Writes the Byte out to the streamIO, optionally encodes/decodes it.	 */
	public void write(int Byte)	throws IOException {
		int Index;
		if (EnCode) {	//adds Escape Characters for forbidden Characters
			if ((Index = forbidden.indexOf(Byte)) >= 0) {	//replace the Character
				super.write (Escape);	//write the Escape Character
				if (doReplace) Byte = replace.charAt(Index);	//first find it and then replace it //this is faster, when an Array is used!
			} super.write(Byte);
		} else { 	//DeCode, remove Escape Characters coming in by write()
			if (escaped) {	//last time an Escape Character came...
				escaped = false;
				if (doReplace && (Index = replace.indexOf(Byte)) >= 0) {	//found in the replacement list
					super.write (forbidden.charAt(Index)); return; }
				super.write (Byte); return; }	//not found, keep it
			if (escaped = (Byte == Escape)) {
				return;	} //just skip the Escape Character, and read the next Character
			super.write (Byte);	//write unchanged Character
		}
	}
	
}
