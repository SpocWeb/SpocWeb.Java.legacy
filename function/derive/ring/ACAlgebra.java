package function.derive.ring;

import streamIO.copy.ICopyAble;
import streamIO.copy.group.ring.metric.body.AMetricBody;
import streamIO.copy.monoid.ISemiMonoid;
import function.IFunction;
import function.IInvertAble;
import function.derive.CCountAble;
import function.derive.IDeriveAble;

/** ACAlgebra.java
 *  Abstract Algebra for constant Functions
 *  These have a constant Derivative of 0 / Zero
 *
 * Subclasses:
 * @see AMetricBody
 *
 * Created on 1. Januar 2001, 20:02
 *
 * @author  Matthias Heuer
 * @version
 */
public abstract class ACAlgebra
extends AAlgebra
implements IDeriveAble {

	/** Returns the Derivative of this Constant Function	  */
	public IDeriveAble getDerivative() { return CCountAble.Zero; }

	/** Sets the Integral from outside
	 * This can be done only once, after that an IllegalStateException is thrown.      */
	public void setDerivative (IDeriveAble Derivative) {
		if (Derivative != CCountAble.Zero) throw new AbstractMethodError(); }

	/** Returns the Integral of this Constant Function	  */
	public IDeriveAble getIntegral () { return new MulAt(this);	}

	/** Sets the Integral from outside
	 * This can be done only once, after that an IllegalStateException is thrown.      */
	public void setIntegral (IDeriveAble Integral) {
		if (! Integral.equals(getIntegral())) throw new AbstractMethodError(); }

	/** The Inverse Function to this Constant does not exist!	  */
	public IInvertAble getInverse () { throw new AbstractMethodError(); }

	/** Sets the Integral from outside
	 * This can be done only once, after that an IllegalStateException is thrown.      */
	public void setInverse (IInvertAble Inverse) { throw new AbstractMethodError(); }

	/** Returns arg Mapped by the Inverse of this Object: !this°arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	  */
	public Object UnMap (Object arg) { throw new AbstractMethodError(); }

	/** Returns arg Mapped in Place by the Inverse of this Object: !this=°arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	  */
	public Object UnMapAt (Object arg) { throw new AbstractMethodError(); }

	/** Returns arg Mapped by this Object: this.Map(arg) == this°arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	  */
	public Object Map (Object arg) { return this; }

	/** Returns arg Mapped in Place by this Object: this.MapAt(arg) this=°arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	  */
	public Object MapAt (Object arg) { ((ICopyAble) arg).copyAt(this); return arg; }

	/** Returns arg Mapped in Place by this Object: this.MapAt(arg) this=°arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	  */
	public ISemiMonoid MapAt (ISemiMonoid arg) { arg.copyAt(this); return arg; }

	/** Returns true, when this Class can operate on Arguments of this Type
	 * This Function makes sense at this Level,
	 * because here there is always the Alternative for the Algebra
	 * to delegate the Operation to the inner Function,
	 * but to operate on the Functions and operate the Results on evaluation.	  */
	public boolean canProcess (Object arg) { return true; }

	/** Returns an alternative Representation that is 'simplified'	  */
	public IFunction simplify () { return this; }

}
