package function;

import graphs.ACopy;

import java.io.IOException;

/**Default Implementation of an order Relation.
 * Every Function is derived from the 'less' Function,
 * which itself is kept abstract.
 *
 * Contains many static Methods which could not be defined in IMeasurAble
 * except for Sin_Cos, Sin_CosSafe and SinH_CosH (in ByRefDouble)
 * and extra Classes AMeasurAble or ACountAble didn't make sense.
 *
 * ternary Result:
 * When two Objects are compared you can have three (four) Results:
 * A < B, A > B, A == B (or an Equivalence Relation can be defined)
 * and A, B are incomparAble.
 *
 * Abstract Methods:
 * less	 */
public class AOrderAble
extends ACopy
implements IOrderAble, Comparable {

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	//	Members
	////////////////////////////////////////////////////////////////////////////

	/**Local Reference to the Self, initialized by concrete classes.
	 * Used for the Simulation of (multiple) Inheritance with Delegation.
	 * Must be a virtual Interface Type to be able to take any Implementation.
	 * Using the Basic Interface, because all is needed is less(), except for lessEq() 	 */
	protected IIOrderAble self;

	////////////////////////////////////////////////////////////////////////////
	//	Constructors
	////////////////////////////////////////////////////////////////////////////

	/** This Constructor is used for the Delegator Classes to call
	 * and replace Self by the Delegator Object with it's overloaded Methods.
	 * @param self_ : inner Object for Delegation.
	 */
	public AOrderAble(IIOrderAble self_) { self = self_; }

	/**This Constructor is only used for direct Child Classes to call.
	 * They replace Self by the Child Object with it's overloaded Methods.
	 * Thus you cannot forget to call the correct Constructor
	 * and don't need to initialize   */
	protected AOrderAble(){ self = this; }//

	////////////////////////////////////////////////////////////////////////////
	//  Interface OrderAble: abstract Methods
	////////////////////////////////////////////////////////////////////////////

	/** Virtual Method!
	 * less: '<' Returns True, when 'Self' < arg
	 * Implemented only to make this class concrete for delegation.
	 * Should be overwritten!
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return
	 */
	public boolean isLessThan (Object arg) { return self.isLessThan(arg); } // ! self.grtrEq(arg); }

	////////////////////////////////////////////////////////////////////////////
	//  Interface OrderAble: Implementation
	////////////////////////////////////////////////////////////////////////////

	/** Returns the Position of this Number relative to arg:
	 * -1 for smaller, otherwise +1
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return -1 when this.less(arg)
	 *         +1 else
	 */
	public int Position(Object arg)	{
		if (self.isLessThan(arg))	return -1;
		else				return +1; }

	/** Returns the exact Position of this Number relative to arg:
	  * -1 for smaller, 0 for equal, otherwise +1
	  * @param arg  : Object to compare to <CODE>this</CODE>
	  * @return -1 when this.less  (arg)
	  *          0 when this.equals(arg)
	  *         +1 else
	  */
	public int compareTo(Object arg) {
		if		(self.isLessThan	(arg))	return -1;
		else if (self.equals(arg))	return  0;
		else						return +1; }

	/** between: returns True, when 'Self' is between arg1 and arg2
	  * @param arg1 : first Border to compare to <CODE>this</CODE>
	  * @param arg2 : second Border to compare to <CODE>this</CODE>
	  * @return
	  */
	public boolean isBetween (Object arg1, Object arg2) {
		return self.isLessThan(arg1) ^ self.isLessThan (arg2);}

	/** greater: '>' Returns True, when 'Self' > arg
		 * @param arg  : Object to compare to <CODE>this</CODE>
		 * @return
		 */
	public boolean isMoreThan (Object arg) { //do the most probable Test first
		return !(self.isLessThan(arg) || self.equals(arg)); }
//		return !self.lessEq(arg); }

	/** greater or equal: '>=' Returns True, when 'Self' >= arg
	  * @param arg  : Object to compare to <CODE>this</CODE>
	  * @return
	  */
	public boolean notLessThan (Object arg) { return !(self.isLessThan(arg)); }

	/** less or equal: '<=' Returns True, when 'Self' <= arg
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return
	 */
	public boolean notMoreThan (Object arg) { return (self.isLessThan(arg) || self.equals(arg)); }

	////////////////////////////////////////////////////////////////////////////
	//  static Methods for Interface OrderAble
	////////////////////////////////////////////////////////////////////////////

	/** compares A[] and B[] up to the given Length
	  * @return true when A[] < B[]
	  *			starting comparison with the Most Significant A[0]
	  */
	public static boolean LESS(IOrderAble[] A, IOrderAble[] B, int length) {
		while (--length >= 0)
			if (!A[length].isLessThan(B[length])) return false;
		return true; }

	/** compares A[] and B[] up to the given Length
	  * @return true when A[] > B[]
	  *			starting comparison with the Most Significant A[0]
	  */
	final static public boolean GRTRfinal (IOrderAble[] A, final IOrderAble[] B, int length) {
		while (--length >= 0)
			if (!A[length].isMoreThan(B[length])) return false;
		return true; }

	/** compares A[] and B[] up to the given Length
	  * @return true when A[] <= B[]
	  *			starting comparison with the Most Significant A[0]
	  */
	final static public boolean LESS_EQ(final IOrderAble[] A, final IOrderAble[] B, int length) {
		while (--length >= 0)
			if (B[length].isMoreThan(A[length])) return false;
		return true; }

	/** compares A[] and B[] up to the given Length
	  * @return true when A[] < B[]
	  *			starting comparison with the Most Significant A[0]
	  */
	final static public boolean GRTR_EQ(IOrderAble[] A, IOrderAble[] B, int length) {
		while (--length >= 0)
			if (B[length].isLessThan(A[length])) return false;
		return true; }

	/** compares A[] and B[] up to the given Length
	  * The Problem is that it is possible that not all Elements
	  * have the same Position. In this Case I return 0
	  * @return -1 when A[].less  (B[])
	  *          0 when A[].equals(B[])
	  *         +1 else
	  */
/*	final static public int compareTo(OrderAble A[], OrderAble B[], int length) {
		int p, i = -1;
		while (--length >= 0) {
			if (0 != (p = A[i].compareTo (B[i]))) return p;
		return 0; }
	*/

	///////////////////////////////////////////////////////////////////////////
	//	static Methods for converting IMeasurAble and ICountAble
	///////////////////////////////////////////////////////////////////////////

	/** Compares this object to the specified byte Value.	 */
	final static public boolean EQUALS (final IMeasurAble arg, final double Value) {
		return arg.getDouble() == Value; }

	/** Compares this object to the specified byte Value.
	 * @see function.byref.ByRefDouble#GET_DOUBLE(Object) which performs the same Functions	 */
	final static public boolean EQUALS(final Object arg, final double Value) 	{
		if (arg instanceof IMeasurAble) return ((IMeasurAble)arg).  getDouble() == Value; else
		if (arg instanceof Number     ) return ((Number     )arg).doubleValue() == Value; else
		return Double.parseDouble(arg.toString()) == Value; }

	/**Compares this object to the specified byte Value.	 */
	final static public boolean EQUALS(Object arg, long Value) {
		if (arg instanceof ICountAble) {
			return ((ICountAble)arg).getLong() == Value; }
		if (arg instanceof Number   ) {
			return ((Number   )arg).longValue() == Value; }
		return false; }

	////////////////////////////////////////////////////////////////////////////////
	//	double trigonometric Functions, moved to ByRefDouble
	////////////////////////////////////////////////////////////////////////////////

	/**Returns the Sinus and Cosinus of x in Place	 */
	//final static public double Sin_Cos(double x, ByRefDouble Cos);

	/**Returns the Sinus and Cosinus of x in Place
	 * x is expected to be in the Range of -PI..Pi	 */
	//final static public double Sin_CosSafe(double x, ByRefDouble Cos);

	/**Returns both the Sinus and Cosinus Hyperbolicus.
	 * This is more efficient, because CosH^2-SinH^2=1	 */
	//final static public double SinH_CosH(double x, ByRefDouble CosH);

	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
		testIt(args); }

	/** Tests all Methods of this Class
	 * @throws IOException raised by reading Keystrokes
	 */
	public static void testIt(String[] args) throws Exception {
		System.out.println("Testing " + AOrderAble.class.getName());
		IOrderAble a = null; //(OrderAble) testInstance;
		IOrderAble b = null; //(OrderAble) testInstance.copy();
		System.out.println("Testing absOrderAble:");

		System.out.println (a + "< " + b  + " = " + a.isLessThan(b));
		System.out.println (a + "<=" + b  + " = " + a.notMoreThan(b));
		System.out.println (b + "<=" + a  + " = " + b.notMoreThan(a));
		System.out.println (a + "<=" + a  + " = " + a.notMoreThan(a));
		System.out.println (a + "> " + b  + " = " + a.isMoreThan(b));
		System.out.println (a + ">=" + a  + " = " + a.notLessThan(a));
		System.out.println (a + ">=" + b  + " = " + a.notLessThan(b));
		System.out.println (b + ">=" + a  + " = " + b.notLessThan(a));
		System.in.read(); System.in.read();
	}

}
