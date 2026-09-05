package streamIO.copy.groupM;

import streamIO.Assert;
import streamIO.Log;
import streamIO.copy.ICopyAble;



/**Default Implementation of a multiplicative Group (G,*,/,1).
 * This Implementation must be kept completely synchronous to absGroup
 * The Interface is separated out,
 * because it is used to simulate multiple Inheritance by Delegation.
 *
 * Design Decisions:
 * Not all Methods are implemented as Defaults although that would be possible:
 * /= is not missing, but raises an Error when being called.
 * This is for one thing to keep this Class abstract
 * and for another to indicate the Methods that have in any case to be redefined.
 *
 * This SemiGroup must be concrete, because it is used for delegation,
 * instead of being used for inheritance!
 *
 * Abstract Methods:
 * mulAt (*=)
 * divAt (/=)
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:25Z
 * digest: 6a488b8d1bea511a961fb9e4561e3ba567f6b7d00b49ade3727e2305e33cf821
 * stale: false
 * tags: [code/abstract_base, code/delegation, code/multiplicative_group]
 * concepts: [Algebraic Group, Delegation Pattern]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class AGroupM
extends ASemiGroupM
implements IGroupM {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(10);

	/////////////////////////////////////////////////////////////////////////////////////

	//Interfaces:

	//Implementation of Interface "GroupM"

	/**Local Reference to the Self, initialized by concrete classes.
	 * Used for the Simulation of (multiple) Inheritance with Delegation.
	 * Must be a virtual Interface Type to be able to take any Implementation.	 */
//	protected GroupM self;	//not necessary, because defined by ASemiGroupM

	/**Sets the 'self' Reference for Delegation.
	 * This Constructor is only used in 'Initialize' and 'Terminate' of abstract Classes
	 * and should normally be marked as 'protected' or 'friend',
	 * but all these Routines are not within one Package.
	 * It is needed for the Child Classes to call
	 * and replace Self by the Child Object with it's overloaded Methods.	 */
	public AGroupM (IGroupM self_) { super(self_); }	//call Constructor of SuperClass 'ASemiGroupM'

	/**Setting to 1 in Place:
	 * Can be implemented by dividing any number by itself. (except for 0!)
	 * A Standard Implementation. Should be overwritten by faster Implementations.	 */
	public IGroupM oneAt () { return ((IIGroupM)self).divAt(self); }

	/**Setting to 1:
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IGroupM one() { return ((IGroupM) self.newInstance()).oneAt(); }
	//uses newInstance() instead of copy() to save copying

	/**Testing for 1:
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public boolean isOne() { return self.equals(one()); }

	//Interface "IGroupM"

	/**Virtual Method!
	 * Division in Place: /=
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IGroupM divAt(Object arg) { return (IGroupM) self.mulAt(((IGroupM) arg).inv()); }

	//Implementations:

	//Internal Delegation

	/**Inversion in Place: 1/x
	 * A Standard Implementation. Should be overwritten by faster Implementations.	 */
	public IGroupM invAt() {
		return (IGroupM) self.shallowCopyAt(((IIGroupM) ((IGroupM)self).one()).divAt(self)); }

	/**Inversion: 1/x
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IGroupM inv() { return ((IGroupM)self).one().divAt(self); }
//	{return (GroupM) ((IGroupM) self.copy()).invAt();}
//	{throw new AbstractMethodError();}

	/**Division: /
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IGroupM div(Object arg) { return ((IGroupM) self.copy()).divAt(arg); }

	/**Integer Power: x^=n
	 * You can implement a similar Algorithm for Multiplication.
	 * This Algorithm uses the binary Representation of the integer n.
	 * You could also write this with Prime Factors, but the Algorithm would be less
	 * elegant and also slower, because this one uses only 2*lb n Multiplications.	 */
	public ISemiGroupM PowAt(int n) {
		if (n == 0) return ((IGroupM)self).oneAt();
		if (n <  0) {((IGroupM)self).invAt(); n=-n;}
		return super.PowAt(n); }

	////////////////////////////////////////////////////////////////////////////////////
	//	Testing	
	////////////////////////////////////////////////////////////////////////////////////

	/** Tests the Neutrality of the Zero Element 
	 * @param a arbitrary Member of the Group
	 */
	private static final void testInverse(final IGroupM a) {
		final IGroupM inv = a.inv(); 
		Assert.IS_TRUE(a.div(a).isOne());
		inv.mulAt(a);
		Assert.IS_TRUE(inv.isOne());
	}

	/** Tests the Neutrality of the Zero Element 
	 * @param a arbitrary Member of the Group
	 */
	private static final void testOne(final IGroupM a) {
		final IGroupM one = a.one(); 
		Assert.IS_TRUE(one.isOne());
		Assert.EQUALS(a, a.mul(one)); 
		Assert.EQUALS(a, one.mul(a)); 
	}

	/**Method to test all Implementations in this class.
	 * Must call testIt of the super Class.	 */
	public static void testIt(ICopyAble testInstance) throws Exception {
		for (double i=-20;++i < 20;) {
			L.n(i+"and 7="+Math.abs(i-7)/Math.abs(i+7));
		}
		IGroupM test = (IGroupM) testInstance.random();
		IGroupM test1 =(IGroupM) testInstance.random();
		testOne(test); 
		testInverse(test); 

		L.n(test + ".isOne()=" + test.isOne());
		L.n(test + ".equals(	" + test1 + ")=" + test.equals(test1));
		L.n(test + ".inv()=	" + test.inv());
//		L.n(test + ".invAt()=" + test.invAt());
		L.n(test + ".div(	" + test + ")=" + test.div	(test1));
//		L.n(test + ".divAt(	" + test + ")=" + test.divAt(test1));
		L.n(test + ".one()=	" + test.one());
//		L.n(test + ".oneAt()=" + test.oneAt());
		int i = -5; final ISemiGroupM product = test.Pow(i);
		while (++i < 5) {
			final ISemiGroupM result = test.Pow(i);
			L.n(test+".pow("+i+") = "+result);
			product.mulAt(test);
			Assert.EQUALS(product, result);
//			L.n(test+"PowAt("+i+") = "+test.PowAt(i));
		}
		testIt(ASemiGroupM.class, testInstance);	//call the super class
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
		testIt(AGroupM.class, args); 
	}

}
