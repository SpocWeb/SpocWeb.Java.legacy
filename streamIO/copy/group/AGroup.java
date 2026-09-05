package streamIO.copy.group;

import streamIO.Assert;
import streamIO.Log;
import streamIO.copy.ICopyAble;

/**Default Implementation of an additive Group (G,+,-,0).
 * The Interface is separated out, because of Symmetry
 * to the multiplicative GroupM,
 * which  is used to simulate multiple Inheritance by Delegation.
 *
 * Design Decisions:
 * Not all Methods are implemented as Defaults although that would be possible:
 * -= is missing.
 * This is for one thing to keep this Class abstract
 * and for another to indicate the Methods that have in any case to be redefined.
 *
 * Abstract Methods:
 * addAt (+=)
 * subAt(-=)
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:16:44Z
 * digest: 9feda1a5b100dd0d323db8d2add7a65d93675a05f0fa0011ce6bfda352dd51ed
 * stale: false
 * tags: [code/group_algebra, code/date_time]
 * concepts: [Group/SemiGroup Algebra]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public abstract class AGroup
extends ASemiGroup
implements IGroup {

	/** Logger for Testing, modify Threshold for switching Logging */
	private static final Log L = new Log(AGroup.class, 1);

	//Interfaces:

	//Implementation of Interface "Group":

	/**Sets this to 0 in Place, by subtracting itself from itself.
	 * @return this, set to 0 in Place:
	  * Can be implemented by subtracting any number from itself.
	  * A Standard Implementation. Should be overwritten by faster Implementations.	 */
	public IGroup zeroAt() { return subAt(this); }

	/**Setting to 0:
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IGroup zero() { return ((IGroup) newInstance()).zeroAt(); }	//Not using copy here, but newInstance!
	//{return subt(this);}

	/**Testing for 0:
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public boolean isZero() { return equals(zero()); }

	/**Negation in Place: -=
	 * Redefined to enable Definition by either INVat or DIVat!
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IGroup negAt() { shallowCopyAt(zero().subAt(this)); return this; }

	/**Negation: -
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IGroup neg() { return ((IGroup) copy()).negAt(); }

	/**Subtraction: -
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IGroup sub(Object arg) { return  ((IIGroup) copy()).subAt(arg); }

	/**Subtraction in Place: -=
	 * Redefined to enable Definition by either INVat or DIVat!
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IGroup subAt(Object arg) { addAt(((IGroup) arg).neg()); return this; }

	/**Compares two Objects for equality.
	 * <p>
	 * The <code>equals</code> method implements an equivalence relation:
	 * <ul>
	 * <li>It is <i>reflexive</i>: for any reference value <code>x</code>,
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
	 * <li>For any reference value <code>x</code>, <code>x.equals(null)</code>
	 * should return <code>false</code>.
	 * </ul>
	 * <p>
	 * The equals method for class <code>Object</code> implements the most
	 * discriminating possible equivalence relation on objects; that is,
	 * for any reference values <code>x</code> and <code>y</code>, this
	 * method returns <code>true</code> if and only if <code>x</code> and
	 * <code>y</code> refer to the same object (<code>x==y</code> has the
	 * value <code>true</code>).
	 *
	 * @param   obj   the reference object with which to compare.
	 * @return  <code>true</code> if this object is the same as the obj
	 * argument; <code>false</code> otherwise.
	 * @see     java.lang.Boolean#hashCode()
	 * @see     java.util.Hashtable
	 * @since   JDK1.0
	 */
	public boolean equals  (Object arg){ return sub(arg).isZero(); }
	//This implementation leads to a recursion, because isZero uses equals
	//So one of them has to be redefined.

	/**Integer Multiplication: x*=n	 */
	public ISemiGroup mulAt(int n) {
		if (n == 0) return zeroAt();
		if (n <  0) {negAt(); n = -n;}
		return super.mulAt(n);
	}

	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** Tests the Neutrality of the Zero Element 
	 * @param a arbitrary Member of the Group
	 */
	private static final void testNegative(final IGroup a) {
		final IGroup neg = a.neg(); 
		Assert.IS_TRUE(a.sub(a).isZero());
		neg.addAt(a);
		Assert.IS_TRUE(neg.isZero());
	}

	/** Tests the Neutrality of the Zero Element 
	 * @param a arbitrary Member of the Group
	 */
	private static final void testZero(final IGroup a) {
		final IGroup zero = a.zero(); 
		Assert.IS_TRUE(zero.isZero());
		Assert.EQUALS(a, a.add(zero)); 
		Assert.EQUALS(a, zero.add(a)); 
	}

	/** Tests all Methods of this Class	 */
	public static void testIt(final ICopyAble testInstance) throws Exception {
//		super.testIt();	//class methods are inherited, but cannot be called using 'super'!
		IGroup test = (IGroup) testInstance.random();
		IGroup test1 =(IGroup) testInstance.random();

		testZero(test); 
		testNegative(test); 
		L.n(test+".isZero()=" + test.isZero());
		L.n(test+".equals(	" + test1 + ")=" + test.equals(test1));
		L.n(test+".neg()=	" + test.neg());
//		L.n(test+".negAt()=" + test.negAt());
		L.n(test+".subt(	" + test1 + ")=" + test.sub	(test1));
//		L.n(test+".subAt(	" + test1 + ")=" + test.subAt	(test1));
		L.n(test+".zero()=	" + test.zero());
//		L.n(test+".zeroAt()=" + test.zeroAt());
		int i = -5; final ISemiGroup product = test.mul(i);
		for (; ++i < 5; ) {
			final ISemiGroup result = test.mul(i);
			L.n(test+".mul("+i+") = "+result);
			product.addAt(test); //exact cancellation is rare!
			if (i != 0) {
				Assert.EQUALS(product, result);
			}
//			L.n(test+".mulAt("+i+") = "+test.mulAt(i));
		}
		testIt(ASemiGroup.class, testInstance);	//first test the Superclass Methods
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
		testIt(AGroup.class, args); 
	}

}
