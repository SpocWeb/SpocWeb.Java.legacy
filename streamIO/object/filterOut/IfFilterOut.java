package streamIO.object.filterOut;

import streamIO.FilterOut;
import streamIO.IIStreamOut;
import streamIO.IStreamOut;
import tester.ITester;

/**
  * Title: IfFilterOut<p>
  * Description:
  * Branch in the Chain of Messages dependent on the Message itself.
  * Based on the given ITester either 'Store' or 'Else' are chosen. 
  *
  * Known SubClasses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-11-26, 01;13;44<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class IfFilterOut
	extends FilterOut
{
	/** Reference to the Else Branch */
	protected IStreamOut Else;

	/** Reference to the ITester Method */
	protected ITester test;

	/** Initializing Constructor */
	public IfFilterOut(IStreamOut store, IStreamOut Else_, ITester test_) {
		super(store);
		this.Else = Else_;
		this.test = test_; }

	/** Accepts the Argument, logs it and sends it further down the Chain */
	public IIStreamOut addItem(Object arg) {
		if (test.test(arg)) {
			out.addItem(arg);
		} else {
			Else.addItem(arg);
		}
		return this; }

}
