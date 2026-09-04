package flow;

import function.byref.IFloat;
import graphs.KeyValuePair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import tester.IDoubleMetric;

//for testing...
//import Functions.ByRef.ByRefDouble;

/**
  * Title: ScalarFlowConnection<p>
  * Description:
  * Purpose:
  * Represents a Flow between two scalar Nodes.
  * The Flow depends only on the current Value of its Elements,
  * not on the current Rate of Change,
  * so the ODE is only of 1st Order.
  * A simple Integration would be sufficient to solve it,
  * but setting up the ODE is harder and more Error Prone
  * than building the Network,
  * and complex or piecewise defined Functions
  * may not be integrated analytically.
  *
  * A Problem that is not resolved here is the Fact,
  * that the Functions depend not only on the current Value,
  * in Fact they rarely do, but on several other Parameters!
  *
  * To avoid modelling and thus hardcoding the exact Relations,
  * a Broker Pattern can be used.
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
  * Created on	10-26-2002, 11:34 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class ScalarFlowConnection
extends KeyValuePair
implements Runnable {

////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////

/** Collection containing all Connections to be able to update them in one Sweep  */
private static final Collection COLL = new ArrayList();

////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////

/** updates all Nodes by executing their Connections  */
final static public void update() {
	Iterator iter = COLL.iterator();
	while (iter.hasNext()) {
		((ScalarFlowConnection) iter.next()).run(); }
}

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** The Function that determines the Flow between the two connected Nodes:	 */
	final public IDoubleMetric flow;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** @return The Function that determines the Flow between the connected Nodes:	 */
	public IDoubleMetric getFlow() {
		return flow; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor	 */
	public ScalarFlowConnection(IFloat source_, IFloat target_, IDoubleMetric flow_) {
		super(source_, target_);
		this.flow = flow_;
		COLL.add(this);
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////
/// #region : Interface Runnable: Implementation
////////////////////////////////////////////////////////////////////////////

	/** performs the Flow with a discrete Transport */
	public void run() {
		double srcVal = ((IFloat) key).getDouble();
		double tgtVal = ((IFloat) val).getDouble();
		double flwVal = flow.dist(srcVal, tgtVal);
		((IFloat) key).setDouble(srcVal-flwVal);
		((IFloat) val).setDouble(tgtVal+flwVal);
	}

////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

/** Tests all Methods of this Class	 */
public static void testIt(String[] args) { //throws java.io.IOException {
	System.out.println("Testing " + ScalarFlowConnection.class.getName());
//	ByRefDouble Reservoir = new ByRefDouble();
//	ByRefDouble Reservoir = new ByRefDouble();
//	ByRefDouble Reservoir = new ByRefDouble();
}

/**The main entry point for the application.
 *
 * @param args Array of parameters passed to the application
 * via the command line.	 */
public static void main (String[] args) { //throws java.io.IOException {
	testIt(args); }

}

