package streamIO.copy.boole;

//import Stream.Copy.*;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.byref.ByRefFloat;

/** Defines a Boolean Set on the continuous Range of [0, 1] using 'float'
  * The Operations AND and OR refer to the analogous Operations
  * on the Result Sets from a Result Space X.
  * Thus a Homeophism is defined between the Result Sets and their Probabilities.
  *
  * The Problem is that you cannot consider the Probabilities
  * without Reference to the actual Result Sets,
  * because their Calculation depends on Knowing the Dependencies of Results:
  * p(A & B) = p(A) * p(B|A)
  * p(A | B) = p(A) + p(B) - p(A & B)
  *
  *
  */
public class Probability
extends ABoole
implements Boole, IMeasurAble {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**Reference to the Value of this Instance	 */
	protected float Value;

	//////////////////////////
	//  Interface IBoole
	//////////////////////////

	/**Boolean AND Operation in Place: &=, &&= for single Bit
	 * a AND b = true <=> (a = true) AND (b = true)
	 * Here you have to consider the possible Dependency of the corresponding Events,
	 * because p(A | A) = p(A & A) = p(A) != p(A)*p(A)
	 * When considered here, you needn't do it again in ORat()!!!	 */
	public Lattice ANDat	(Object arg){Value *= ByRefFloat.getFloat(arg); return this;}

	/**Boolean OR Operation in Place: |=, ||= for single Bit
	 * a OR b = true <=> (a = true) OR (b = true)
	 * By taking care of dependent Events in the AND Method,
	 * this is not necessary here!	 */
	public Lattice ORat	(Object arg){
		Value += ByRefFloat.getFloat(arg)
			  -  ByRefFloat.getFloat(self.AND(arg)); return this;}

	/**Boolean Constant for the Representation of 'false': =0
	 * Sets this Object to False, i.e. not 'true';
	 * with Vectors it sets all Elements to their respective Value of False*/
	public Boole FalseAt() { Value = 0; return this; }

	/**Boolean Constant for the Representation of 'false': =0
	 * Sets this Object to False, i.e. not 'true';
	 * with Vectors it sets all Elements to their respective Value of False*/
	public Boole TrueAt() { Value = 1; return this; }

	/**Boolean NOT Operation in Place: ~=, != for single Bit
	 * NOT a = true <=> (a = false)
	 * This Operation cannot be implemented by infinite Sets or limited Sets,
	 * Therefore you need other means to define some Operations.	 */
	public Boole NOTat	() { Value = 1.0f-Value; return this; }

	///////////////////////////////////////////////////////////////////////////////
	//  Interface IMeasurAble
	///////////////////////////////////////////////////////////////////////////////

	/**Returns the Object Value represented by a scalar Variable of Type double.
	 * It consists of an IEEE Number with 64 Bit (8 Byte):
	 * 52 Bit Mantissa, 11 Bit Exponent, 1 Bit Sign	 */
	public double getDouble() { return Value; }

	/**Returns the Object Value represented by a scalar Variable of Type float.
	 * It consists of an IEEE Number with 32 Bit (4 Byte):
	 * 23 Bit Mantissa, 8 Bit Exponent, 1 Bit Sign	 */
	public float   getFloat() { return Value; }

	///////////////////////////////////////////////////////////////////////////////
	//  Interface OrderAble: optimized Implementations
	///////////////////////////////////////////////////////////////////////////////

	/** Sloppy (on Equality) but fast 'between' Implementation
	  * @param arg1 : first  Border to compare to <CODE>this</CODE>
	  * @param arg2 : second Border to compare to <CODE>this</CODE>
	  * @return True, when 'Self' is between arg1 and arg2
	  */
	public boolean isBetween (Object arg1, Object arg2) {
		return isLessThan(arg1) ^ isLessThan (arg2);}

	/** less Relation: '<'
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return True, when 'Self' < arg
	 */
	public boolean isLessThan (Object arg) {
		if ((arg == null) ||
			(arg == this)) return false;
		return Value < ByRefDouble.GET_DOUBLE(arg); }

	/** greater Relation: '>'
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return True, when 'Self' > arg
	 */
	public boolean isMoreThan (Object arg) {
		if ((arg == null) ||
			(arg == this)) return false;
		return Value > ByRefDouble.GET_DOUBLE(arg); }

	/** greater or equal: '>='
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return True, when 'Self' >= arg
	 */
	public boolean notLessThan (Object arg) {
		if (arg == null) return false;
		if (arg == this) return  true;
		return Value >= ByRefDouble.GET_DOUBLE(arg); }

	/** less or equal: '<='
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return True, when 'Self' <= arg
	 */
	public boolean notMoreThan (Object arg) {
		if (arg == null) return false;
		if (arg == this) return  true;
		return Value <= ByRefDouble.GET_DOUBLE(arg); }

	/** Returns the Position of this Object relative to arg:
	  * This Operation is leaner than compareTo.
	  * @param arg  : Object to compare to <CODE>this</CODE>
	  * @return -1 for this < arg
	  *         +1 otherwise
	  */
	public int Position(Object arg) {
		if ((arg == null) ||
			(arg == this)) return 1;
		return Value < ByRefDouble.GET_DOUBLE(arg) ? -1 : 1; }

	/** Returns the exact Position of this Object relative to arg:
	 * The Java 1.2 Interface 'Comparable' calls this 'compareTo'
	 * The Java 1.2 Interface 'Comparable' defines an Operator with 'compare'
	 * -1 for smaller, 0 for equal, otherwise +1
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return -1 for this <  arg
	 *          0 for this == arg
	 *         +1 otherwise
	 */
	public int compareTo(Object arg) {
		if (arg == null) return 1;
		if (arg == this) return 0;
		double Val;
		if     (Value < (Val = ByRefDouble.GET_DOUBLE(arg))) return -1;
		return (Value >  Val) ? +1 : 0; }

	//////////////
	//  testing
	//////////////

	/**This Method tests all the Methods of this Class.	 */
	public static void testIt (String[] args) throws Exception {
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
		testIt(args);
	}

}
