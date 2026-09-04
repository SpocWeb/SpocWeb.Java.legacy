package streamIO; //TODO: always define a Package

import java.io.OutputStream;
import java.io.PrintStream;

/**
  * Title: FormatterOut.java<p>
  * Description:
  * Defines the Interface for Formatting an Objects into an OutPut streamIO
  * This is the Partner for ParserIn, which parses Objects from Output Streams.
  *
  * Known SubInterfaces:
  *
  * Known Implementors: AFormatterOut, XMLFormatterOut
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on 06-03-2001, 03:57 PM<p>
  * @author 	Matthias Heuer
  * @version 1.0
  */
public abstract class AFormatterOut
extends AStreamOut
implements IFormatOut {

	////////////////////////////////////////////////////////////////////////////
	//  static Constants and Variables
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////

	/** Reference to the Input streamIO used for reading.	 */
	protected PrintStream PS;

	////////////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected AFormatterOut() { }

	/** Initializing Constructor	 */
	public AFormatterOut(OutputStream OS) { PS = new PrintStream(OS); }

	/** Initializing Constructor	 */
	public AFormatterOut(PrintStream PS) { this.PS =  PS; }

	////////////////////////////////////////////////////////////////////////////
	//  public Methods
	////////////////////////////////////////////////////////////////////////////

	/** Returns a new Instance of this Formatter Class using the given OutPut streamIO	 */
	public abstract IFormatOut newInstance(OutputStream Out);

	/** Returns a new Instance of this Formatter Class using the given Print streamIO	 */
	public abstract IFormatOut newInstance(PrintStream PS);

}
