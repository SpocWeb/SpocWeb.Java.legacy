package streamIO.object.parser;

import java.io.IOException;

import streamIO.object.AFilterIn;
import streamIO.object.IStreamIn;
import function.byref.ByRefInt;

/** MaskedStreamIn
  * This is a possible Element of a Parser Chain:
  * It assembles Strings from a StreamIn to Strings,
  * hands over the Strings to the Parser
  * and masks out Segments, e.g. using quotes or double Quotes.
  * You can choose between nested Qoting or non nested quoting.
  *
  * Masking could be done most performant at the InputStream Level,
  * but that would be less modular.
  * Thus the Separator Characters are handed down the Parser Chain
  * to combine Performance and Modularity.
  *
  * Design Decisions:
  *
  * @see nextItem() returns the next Character
  * @see currItem() returns and clears the current String
  *
  * @see StreamInParser for the Post Procesing of this streamIO.
  * @see java.io.InputStream into the
  * @see streamIO.IIStreamIn Interface
  *
  * <!-- docstate
  * tags: [code/stream_parsing, code/parser]
  * concepts: [Separator-Driven Token Parsing and Stream Adapters]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
final public class MaskedStreamIn
extends AFilterIn
implements IParserIn {
	
	/** Indicator for not using Escape Characters. */
	final static public char NO_ESCAPE = (char) -2; //no Escaping by Default...
	
	///////////////////////////////////////////////////////////////////////////////
	//  Member Variables
	///////////////////////////////////////////////////////////////////////////////
	
	/** StringBuffer containing the current String to be returned by currItem(). 	 */
	protected StringBuffer Buffer = new StringBuffer();
	
	/** Determines whether Masks are nested or not.	 */
	public boolean nest;
	
	/** Mask Start Character.
	  * Escapes the following Characters to enable parsing.
	  * The MaskStart Character itself is filtered out, except if escaped!  */
	public char MaskStart;
	
	/** Mask Stop Character.
	  * Escapes the following Characters to enable parsing.
	  * The MaskStop Character itself is filtered out, except if escaped!  */
	public char MaskStop;
	
	/** Replacement for the Mask Start Character.  */
	public String MaskStartReplace = "";
	
	/** Replacement for the Mask Stop Character.  */
	public String MaskStopReplace = "";
	
	////////////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor
	  * @param Scanner that returns Tokens (nextItem) and Strings (currItem)
	  * @param Separators is max. 127 Characters long
	  * which allows using the smaller 'byte' Type
	  */
	public MaskedStreamIn(char MaskStart_, char MaskStop_, boolean nest_, IParserIn Scanner) {
		super(Scanner);
		this.nest = nest_;
		this.MaskStart = MaskStart_;
		this.MaskStop  = MaskStop_; }
	
	/** Initializing Constructor
	  * @param Scanner that returns Tokens (nextItem) and Strings (currItem)
	  * @param Separators is max. 127 Characters long
	  * which allows using the smaller 'byte' Type
	  */
	public MaskedStreamIn(char MaskStart_, char MaskStop_, boolean nest_, IParserIn Scanner, String Separators) {
		super(Scanner);
		this.nest = nest_;
		this.MaskStart = MaskStart_;
		this.MaskStop  = MaskStop_;
		setSeparators(Separators); }
	
	/** Initializing Constructor
	  * @param Scanner that returns Tokens (nextItem) and Strings (currItem)
	  * @param Separators is max. 127 Characters long
	  * which allows using the smaller 'byte' Type
	  */
	public MaskedStreamIn(char MaskStart_, char MaskStop_, boolean nest_, String MaskStartReplace_, String MaskStopReplace_, IParserIn Scanner) {
		super(Scanner);
		this.nest = nest_;
		this.MaskStartReplace = MaskStartReplace_;
		this.MaskStopReplace  = MaskStopReplace_;
		this.MaskStart = MaskStart_;
		this.MaskStop  = MaskStop_; }
	
	/** Initializing Constructor
	  * @param Scanner that returns Tokens (nextItem) and Strings (currItem)
	  * @param Separators is max. 127 Characters long
	  * which allows using the smaller 'byte' Type
	  */
	public MaskedStreamIn(char MaskStart_, char MaskStop_, boolean nest_, String MaskStartReplace_, String MaskStopReplace_, IParserIn Scanner, String Separators) {
		super(Scanner);
		this.nest = nest_;
		this.MaskStartReplace = MaskStartReplace_;
		this.MaskStopReplace  = MaskStopReplace_;
		this.MaskStart = MaskStart_;
		this.MaskStop  = MaskStop_;
		setSeparators(Separators); }
	
	/** Sets the Separator of this Parser to this Value.
	  * This is necessary, because the outer Parsers determine the inner Parsers'
	  * Separators, but must know the inner Parsers to call their Methods.
	  * @param the Separators String	*/
	public void setSeparators(final String Separators) {
		StringBuffer SepNeu = new StringBuffer();
		SepNeu.append(MaskStart);
		SepNeu.append(MaskStop);
		SepNeu.append(Separators);
		((IParserIn) in).setSeparators(SepNeu.toString()); }
	
	////////////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////////////
	
	/**
	  * @return the Position of the next found Separator in the Separator String.
	  * This Routine is sped up by indexing the Separator String (for long Separators)
	  * and could be even faster by not calling the nextItem() Routine.	 */
	protected Object nextItemInternal() {
		ByRefInt currToken;
		int maskLevel = 0;  //assembling is done automatically as long as no currItem is being called!
		while (true) { //read the next Character... assembling is assumed to be done by EscapeStreamIn
			currToken = (ByRefInt) in.nextItem();
			if (currToken.Value <= InputStream2StreamIn.SCN_TAG_EOF) {
						return currToken; }
			if (MaskStart == MaskStop) { //could also be split up into two Classes.
				if (currToken.Value < 2) { //Mask, independent of Token chosen.
					maskLevel = 1-maskLevel; //and take the first Character literally
				} else {
					currToken.Value -= 2;
					if (maskLevel == 0)
						return currToken; //
				}
			} else { //different Characters for starting and stopping
						if (0 == currToken.Value) { //Mask
					if (++maskLevel > 1) if (!nest) //increase nesting
						  maskLevel = 1;
				} else  if (1 == currToken.Value) { //Mask
					if (--maskLevel < 0)
						  maskLevel = 0; //ignore closing Characters without openings!
				} else {
					currToken.Value -= 2;
					if (maskLevel == 0)
						return currToken; //
				}
			}
		}
	}
	
	/** Delegates to the wrapped Scanner's currItem(), forwarding the assembled Data rather than a Token.
	  * @return the current Object without moving.
	  * This is just a caching Functionality and should be done
	  * at the Client Process, for faster Access.
	  * Here the Semantics are changed: it is used to return the actual Data instead of the Tokens. */
	public Object currItem() { return ((IStreamIn) in).currItem(); }
	
	//////////////////
	//	Testing		//
	//////////////////
	
	/**tests all Methods of this Class	 */
	public static void testIt() throws IOException {
	//	testPrimitives();
	//	testReadBag();
	//	testScanner();
	//	testReadRelation();
	}
	
}
