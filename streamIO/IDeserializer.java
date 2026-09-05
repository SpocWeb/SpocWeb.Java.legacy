package streamIO;

import java.io.IOException;
import java.io.InputStream;

/**
  * Title: IDeserializer.java<p>
  * Description:
  * Defines the Interface for Parsing an InPut streamIO into Objects
  * This is the Partner for FormatterOut, which formats Objects into Output Streams.
  * The Methods are used in ACopyAble exclusively for:
  * 
  *
  * Known SubInterfaces:
  *
  * Known Implementors: AParserIn, XMLParserOut
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-03-2001, 03:46 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * @stereotype enumeration
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:24Z
  * digest: 12f8ba0cc1a64e9f2316d8d87bb74d0afdef528294179b2503eb1b59d66577a9
  * stale: false
  * tags: [code/factory_pattern]
  * concepts: [Deserializer Interface]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public interface IDeserializer
extends IIStreamIn {
	
	////////////////////////////////////////////////////////////////////////////
	//  static Constants and Members
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	//  public Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** Returns a new Instance of this Parser Class using the given InPut streamIO
	  * This is necessary for the fromStream() Methods to be able
	  * to switch to other Streams.  */
	public IDeserializer newInstance(final InputStream In) throws IOException;
	
	/** Tries to load the given Object from this Input streamIO and return it.
	  * The Way of Parsing is left to the ParserIn itself
	  * and corresponds to a certain FormatterOut Class.  */
	public Object loadItem(final Object Item);
	
}
