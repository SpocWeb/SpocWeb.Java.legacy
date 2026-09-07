package streamIO.object.parser;

import streamIO.object.IStreamIn;

/**
  * Interface defining a Method to set the Separators for an Input streamIO Parser.
  * To combine Performance and Modularity
  * it is necessary to propagate Separator Changes down a Parser Chain
  * to the @see InputStream2StreamIn Class,
  * so Assembly happens at the lowest Level possible.
  *
  * This is used e.g. in @see MaskedStreamIn
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-07T00:00:00Z
  * digest: 404354b990a8ecf141b6d3a554a2a527c80b077550909bb88d1cd36062aceec7
  * stale: true
  * tags: [code/stream_parsing, code/parser]
  * concepts: [Separator-Driven Token Parsing and Stream Adapters]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public interface IParserIn
	extends IStreamIn {

	/** Sets the Separator of this Parser to this Value.
	  * This is necessary, because the outer Parsers determine the inner Parsers'
	  * Separators, but must know the inner Parsers to call their Methods.
	  * @param the Separators String	*/
	public void setSeparators(final String Separators);
	
	/** @return the next String or Character and add it to the StringBuffer.
	  * Wraps IO Exceptions into BaseExceptions
	  */
//	public String nextString();

	/** @return the next String or Character and add it to the StringBuffer.
	  * Wraps IO Exceptions into BaseExceptions
	  */
//	public String nextString(final String Separators);

}
