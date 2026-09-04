/*
 * File Name: PrintOp.java
 * Created on: 19.06.2003
 *
 */
package function;

import java.io.OutputStream;
import java.io.PrintStream;

import tester.ITester;
import function.byref.ByRefInt;

/**
 * Title: PrintOp<p>
 * Description:<p>
 * Helper Class defining the Print Operation
 * to stream out Results of the Testing Methods
 *
 * Design Decisions / Implementation Details:<p>
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class PrintOp 
implements IProcessor, ITester {

	/**streamIO to which the Data is written	 */
	final public PrintStream PS;

	/**Initializing Constructor	 */
	public PrintOp(final OutputStream OS) {
		this.PS = new PrintStream(OS);
	}

	/**Initializing Constructor	 */
	public PrintOp(final PrintStream OS) {
		this.PS = OS;
	}

	/**Operation, implemented as writing the Argument to the Output streamIO	 */
	public Object MapAt(final Object arg) {
		PS.print((char) ('A' + ((ByRefInt) arg).Value));
		return arg;
	}

	/**Operation, implemented as writing the Argument to the Output streamIO	 */
	public boolean test(final Object arg) {
		MapAt(arg);
		return false;
	}

}
