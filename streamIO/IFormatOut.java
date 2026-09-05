package streamIO; //TODO: always define a Package

import java.io.OutputStream;
import java.io.PrintStream;

/**
  * Title: FormatterOut<p>
  * Description:
  * Defines the Interface for Formatting an Objects into an OutPut streamIO.
  * This requires a new Method, which is able
  * to return a new Instance of the Formatter using a given Output streamIO
  * to simulate Nesting Objects. 
  *
  * This is the Partner for 
  * @see IDeserializer, which parses Objects from Output Streams.
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
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:24Z
  * digest: 1661791ba8dd4400f8919f51be8d3009f64156443bf72a7f5fc04bd4bdebfc85
  * stale: false
  * tags: [code/output_stream]
  * concepts: [Format Output Interface]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public interface IFormatOut
extends IStreamOut {
	
	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	//  public Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** Returns a new Instance of this Formatter Class using the given OutPut streamIO	 */
	public IFormatOut newInstance(final OutputStream OS);
	
	/** Returns a new Instance of this Formatter Class using the given Print streamIO	 */
	public IFormatOut newInstance(final PrintStream PS);

}
