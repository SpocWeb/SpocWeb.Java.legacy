package tester.stateful;

import tester.ITester;

/**
  * Title: Flipper<p>
  * Description:
  * Purpose:
  * Flips between two States: true and false.
  * Stateful ITester that doesn't actually consider the actual Object being passed.
  * This can be used e.g. in flow.pushflow.Alternator to split up a streamIO of Objects
  * into two Streams.
  *
  * Purpose / Responsibilities of this Class
  *
  * Design Decisions / Implementation Details:
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-14-2002, 08:58 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class Flipper
implements ITester {
    
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Flag to switch between the States	 */
	protected boolean value;
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return true iff TODO:   */
	public boolean isTrue() { return value; }
	
	/** sets the TODO: 	 */
	void setTrue(final boolean value_) { this.value = value_; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor	 */
	protected Flipper() { }
	
	/** Initializing Constructor	 */
	protected Flipper(final boolean value_) { this.value = value_; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface ITester: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/**This is the Test working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.
	 * @param  arg	The Object being 'tested'
	 * @return 	'true' or 'false' depending on the ITester and the Parameter 'arg'	 */
	public boolean test(final Object arg) { return value = !value; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Flipper.class.getName());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) { //throws java.io.IOException {
		testIt(args); }
	
}

