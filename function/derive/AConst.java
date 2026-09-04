package function.derive;

import streamIO.copy.ICopyAble;
import function.AFunction;
import function.IInvertAble;

/**AConst.java
 * Base Class for all constant Functions.
 * Although this is IDeriveAble, it is not IInvertAble and not derived from these
 * abstract Classes, because Inverse and Derivative are very simple.
 *
 * It is not abstract though, but returns 'this' by Default.
 * That makes it usable to define specific Constants without any Properties.
 * To encapsulate an existing Object into a Function use 'Const'
 *
 * Known SubClasses: Const, CCountAble, CMeasurAble (not ByRefObject or CCopyAble)
 *
 * Created on 29. Dezember 2000, 11:08
 *
 * @author  Matthias Heuer
 * @version
 */
public class AConst
extends AFunction
implements IDeriveAble {

	//////////////////////////////
	//	static Constants
	//////////////////////////////

	//////////////////////////////
	//	Methods
	//////////////////////////////

	/** Returns the Derivative of this Function	  */
	public IDeriveAble getDerivative() { return CCountAble.Zero; }

	/** Sets the Integral from outside
	 * This can be done only once, after that an IllegalStateException is thrown.	  */
	public void setDerivative (IDeriveAble derivative) { throw new AbstractMethodError(); }

	/**Local Cache for the Integral Function   */
	protected IDeriveAble Integral; // = new MulAt(H);

	/** Returns the Integral of this Function	  */
	public IDeriveAble getIntegral() { return Integral; }

	/** Sets the Integral from outside
	 * This can be done only once, after that an IllegalStateException is thrown.	  */
	public void setIntegral (IDeriveAble integral) {
		if  (this.Integral   ==   Integral) return;
		if  (this.Integral   !=   null)
		if  (this.Integral.equals(integral)) return; //throw new AbstractMethodError();
			 this.Integral    =   integral; //don't throw Errors, because Integrals can differ
		integral.setDerivative(this); }

	/** Returns the Inverse Function to this one: !this
	 * i.e. the Function that returns the identical Mapping,
	 * if mapped / concatenated with this Function (at least locally)
	 * Not possible with constant Functions, because the Inverse is no Function 	*/
	public IInvertAble getInverse () { throw new AbstractMethodError(); }

	/** Sets the Integral from outside
	 * This can be done only once, after that an IllegalStateException is thrown.	  */
	public void setInverse (IInvertAble inverse) { throw new AbstractMethodError(); }

	/** Returns arg mapped by the Inverse of this Object: !this°arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.
	 * Not possible with constant function, because the Inverse is no Function 	*/
	public Object UnMap  (Object arg) { throw new AbstractMethodError(); }

	/** Returns arg mapped in Place by the Inverse of this Object: !this=°arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.
	 * Not possible with constant Functions, because the Inverse is no Function 	*/
	public Object UnMapAt (Object arg) { throw new AbstractMethodError(); }

	/** Returns arg mapped by this Object: this.Map(arg) == this°arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	  */
	public Object Map (Object arg) { return this; }

	/** Returns arg mapped in Place by this Object: this.MapAt(arg) this=°arg
	  * This is the Function working on 'arg' defined by the implementing Class.
	  * The Class implementing this Method is the means of exchanging this Operation.	  */
	public Object MapAt (Object arg) {
//		throw new AbstractMethodError(); }
		return ((ICopyAble) arg).copyAt(Map(arg)); }	//would require the Definition of CopyAble and create circular Dependencies!

	/** Returns an alternative Representation that is 'simplified'	  */
//	public IFunction simplify () { }

	/**Returns true, when this Class can operate on Arguments of this Type
	 * This Function makes sense at this Level,
	 * because here there is always the Alternative
	 * not to operate on the Constants,
	 * but to operate on the Functions and operate the Results on evaluation.	 */
	public boolean canProcess(Object arg) { return true; }

}
