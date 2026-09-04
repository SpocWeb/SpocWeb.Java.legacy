package streamIO.object;

import java.io.IOException;
import java.io.InputStream;

import streamIO.IDeserializer;

/**
  * Title: AParserIn.java<p>
  * Description:
  * Partially implements the Interface for Parsing an InPut streamIO into Objects
  * This is the Partner for FormatterOut, which formats Objects into Output Streams.
  * 
  * Known SubClasses: XMLParserOut
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-03-2001, 03:46 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public abstract class AParserIn
extends AStreamIn {

	////////////////////////////////////////////////////////////////////////////
	//  static Constants and Variables
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////

	/** Reference to the Input streamIO used for reading.	 */
	protected InputStream In;

	////////////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected AParserIn() { }

	/** Initializing Constructor	 */
	public AParserIn(final InputStream In) { this.In =  In; }

	////////////////////////////////////////////////////////////////////////////
	//  public Methods
	////////////////////////////////////////////////////////////////////////////

	/** Returns a new Instance of this Parser Class using the given InPut streamIO
	  * TODO: This is necessary for... ??? */
//	protected ParserIn newInstance() { }

	/** Returns a new Instance of this Parser Class using the given InPut streamIO	 */
	public abstract IDeserializer newInstance(final InputStream In) throws IOException; // { }

	/** Tries to load the given Object from this Input streamIO	 */
	public abstract Object loadItem(final Object Item);

}
