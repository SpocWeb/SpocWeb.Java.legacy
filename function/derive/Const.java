package function.derive;

//Identity, Cat, Const and CCountAble/CMeasurAble could be isolated into their own Package!

import function.ICountAble;
import function.IFunction;
import function.IMeasurAble;

/**This Class encapsulates the Constant (Object) Function.
 * It returns the Constant Value to any Argument.
 * If Value is modifyable, it cannot be protected against Changes.
 * This Function doesn't implement the AIntegrityRing Interface
 * so it cannot be added / subtracted ... directly to other Const Objects.
 * This is done indirectly using the Algebra Class.
 *
 * Known SubClasses: none
 *
 * Created on 26. Dezember 2000, 18:25
 *
 * @author  Matthias Heuer
 * @version
 */
public class Const
//extends ByRefObject  //in Java cannot (like in C++) make protected Variable 'Value' public! Only add a setValue() Method.
extends AConst //ADeriveAble
//extends CCopyAble //like CCopyAble it doesn't allow copyAt(), but copy()
//implements ICountAble { //, CopyAble
{

	///////////////////////////////////////////////////////////////////////////////
	//  Variables
	///////////////////////////////////////////////////////////////////////////////

	/**The Value is 'protected' against accidental Changes
	 * It is still possible though, when Value is 'CopyAble'   */
	protected Object Value;

	///////////////////////////////////////////////////////////////////////////////
	//  Constructors
	///////////////////////////////////////////////////////////////////////////////

	/** Creates new Const, returns 'this' by Default,
	  * which leads to Recursion in most Functions.  */
	protected Const () { this.Value = this; } //setIntegral(new MulAt(H)); }

	/** Creates new Const returning the given Result.  */
	public Const (Object Result) throws IllegalArgumentException{
		if (Result instanceof Const) //don't allow nested Constant Functions.
			Result = ((Const) Result).Map(this);
		if (Result instanceof IFunction)
			throw new IllegalArgumentException("No Functions allowed as Constant Result!");
		this.Value = Result; }

	//////////////////////////////
	//	Interface IDeriveAble	//
	//////////////////////////////

	/** Returns arg mapped by this Object: this.Map(arg) == this°arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	  */
	public Object Map (Object arg) { return Value; }

	//////////////////////////////
	//  Interface ICountAble
	//////////////////////////////

	/** Returns the Object Value represented by an 8 Bit Integer	 */
	public byte   getByte() { return ((ICountAble)Value). getByte(); }

	/**Returns the Object Value represented by a 16 Bit Integer	 */
	public short getShort() { return ((ICountAble)Value).getShort(); }

	/**Returns the Object Value represented by a 32 Bit Integer	 */
	public int     getInt() { return ((ICountAble)Value).  getInt(); }

	/**Returns the Object Value represented by a 64 Bit Integer	 */
	public long   getLong() { return ((ICountAble)Value). getLong(); }


	//////////////////////////////
	//  Interface IMeasurAble
	//////////////////////////////

	/**Returns the Object Value represented by a scalar Variable of Type double.
	 * It consists of an IEEE Number with 64 Bit (8 Byte):
	 * 52 Bit Mantissa, 11 Bit Exponent, 1 Bit Sign	 */
	public double getDouble() { return ((IMeasurAble)Value).getDouble(); }

	/**Returns the Object Value represented by a scalar Variable of Type float.
	 * It consists of an IEEE Number with 32 Bit (4 Byte):
	 * 23 Bit Mantissa, 8 Bit Exponent, 1 Bit Sign	 */
	public float   getFloat() { return ((IMeasurAble)Value). getFloat(); }

	//////////////////////////
	//  Interface CopyAble
	//////////////////////////

	//These are the virtual Methods of Object: they cannot be abstracted int AConst!

	/**@return  The string representation of the Function.
	 * @since   JDK1.0	 */
	public String toString() { return "Const(" + Value.toString() + ")"; }

	/**Returns a hash code inner for the object. This method is
	 * supported for the benefit of hashtables such as those provided by
	 * <code>java.util.Hashtable</code>.
	 * <p>
	 * The general contract of <code>hashCode</code> is:
	 * <ul>
	 * <li>Whenever it is invoked on the same object more than once during
	 * an execution of a Java application, the <code>hashCode</code> method
	 * must consistently return the same integer. This integer need not
	 * remain consistent from one execution of an application to another
	 * execution of the same application.
	 * <li>If two objects are equal according to the <code>equals</code>
	 * method, then calling the <code>hashCode</code> method on each of the
	 * two objects must produce the same integer result.
	 * </ul>
	 *
	 * @return  a hash code inner for this object.
	 * @see     java.lang.Object#equals(java.lang.Object)
	 * @see     java.util.Hashtable
	 * @since   JDK1.0 */
	public int hashCode() { return Map(this).hashCode(); }

	/**Compares two Objects for equality.
	 * <p>
	 * The <code>equals</code> method implements an equivalence relation:
	 * <ul>
	 * <li>It is <i>reflexive</i>: for any reference Value <code>x</code>,
	 * <code>x.equals(x)</code> should return <code>true</code>.
	 * <li>It is <i>symmetric</i>: for any reference values <code>x</code> and
	 * <code>y</code>, <code>x.equals(y)</code> should return
	 * <code>true</code> if and only if <code>y.equals(x)</code> returns
	 * <code>true</code>.
	 * <li>It is <i>transitive</i>: for any reference values <code>x</code>,
	 * <code>y</code>, and <code>z</code>, if <code>x.equals(y)</code>
	 * returns  <code>true</code> and <code>y.equals(z)</code> returns
	 * <code>true</code>, then <code>x.equals(z)</code> should return
	 * <code>true</code>.
	 * <li>It is <i>consistent</i>: for any reference values <code>x</code>
	 * and <code>y</code>, multiple invocations of <code>x.equals(y)</code>
	 * consistently return <code>true</code> or consistently return
	 * <code>false</code>.
	 * <li>For any reference Value <code>x</code>, <code>x.equals(null)</code>
	 * should return <code>false</code>.
	 * </ul>
	 * <p>
	 * The equals method for class <code>Object</code> implements the most
	 * discriminating possible equivalence relation on objects; that is,
	 * for any reference values <code>x</code> and <code>y</code>, this
	 * method returns <code>true</code> if and only if <code>x</code> and
	 * <code>y</code> refer to the same object (<code>x==y</code> has the
	 * Value <code>true</code>).
	 *
	 * @param   obj   the reference object with which to compare.
	 * @return  <code>true</code> if this object is the same as the obj
	 * argument; <code>false</code> otherwise.
	 * @see     java.lang.Boolean#hashCode()
	 * @see     java.util.Hashtable
	 * @since   JDK1.0 	 */
	public boolean equals  (Object arg) {
		if (  arg instanceof IFunction) {
		if (!(arg instanceof AConst)) throw new AbstractMethodError();
		arg = ((IFunction) arg).Map(this); }
		return Map(this).equals(arg); }

}
