package function.derive.ring;

import java.io.IOException;
import java.io.StreamTokenizer;

import streamIO.copy.ICopyAble;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ISemiGroup;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.metric.IMetricIRing;
import streamIO.copy.group.ring.metric.IWellOrder;
import streamIO.copy.groupM.IGroupM;
import streamIO.copy.groupM.ISemiGroupM;
import streamIO.copy.monoid.IMonoid;
import streamIO.copy.monoid.ISemiMonoid;
import function.ICountAble;
import function.IFunction;
import function.IInvertAble;
import function.IMeasurAble;
import function.IOrderAble;
import function.derive.CCountAble;
import function.derive.Const;
import function.derive.IDeriveAble;

/**Defines an Algebra or linear (Vector)Space (i.e. a Mapping)
  * over a Body like the Real Numbers R or the complex Numbers C
  * by extending AFunction with Ring Operations.
  * The Rules are the same as for a Vector Space respective Manifold.
  * The Implementation is similar to BodyDouble or RingLong,
  * because Function has no inner Components
  * that could be copied in copyAt().
  *
  * Design Decisiona:
  * The Change of Behavior between 'inner' being an IFunction which is done
  * in all Methods normally requires two Classes with all Methods polymorphic,
  * but that would 1) require duplicate Implementations
  * 2) make it slightly faster and 3) increase the Number of Classes.
  *
  * This is the Function (mapping) Equivalent to 'StreamBond'
  * which allows symbolic and explicit Operations on Sets.
  *
  * Codify Operations into Objects and apply them on Expressions,
  * until those Expressions have minimum Length
  * Use a Grammar Definition to generate it
  * Use a Metric  Definition to define the "Result"
  *
  * For Functions:
  *  f(~f) == ~f(f) == Id
  *  f(Const) = FConst
  *  Const(f) = Const
  *
  * For Sets:
  *  ~(~A) == A //Negation
  *    A OR  B == B OR  A	//Commutative
  *    A AND B == B AND A	//Commutative
  *   (A OR  B) OR  C == A OR  (B OR  C)	//Associative
  *   (A AND B) AND C == A AND (B AND C)	//Associative
  *  ~(A AND B) == ~A OR  ~B 	//De Morgan
  *  ~(A OR  B) == ~A AND ~B 	//De Morgan
  *
  * This is also an excellent Example of how to switch from an Operator based Syntax
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:39:12Z
  * digest: f3023da330410471b77c245de498a97b24c4b2cfaef87890931996afe1358bb2
  * stale: false
  * tags: [code/algebraic_function, code/function_wrapper]
  * concepts: [Ring Theory, Function Algebra]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  * with a 'simple' Type to the Method based Syntax of OO Programming.	 */
final public class Algebra
extends ACAlgebra //AIntRing
implements ICountAble {

	//Function Constants that don't need to be defined as Classes,
	//since they can easily be derived from the basic Functions.

    //Sequence is important!

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Constant containing the Function x^2-1	 */
	final static public CatDerive
		xx_1 = new CatDerive(Pred.PRED, Square.SQUARE);

	/**Constant containing the Function 1-x^2
	 * Negative of x^2-1 (see above)	 */
	final static public CatDerive
		One_xx = new CatDerive(Resid.RESID, Square.SQUARE);

	/**Constant containing the Function 1-x^2/2
	 * Integral of Negative(see above)	 */
	final static public CatDerive
		One_xx_2 = 	new CatDerive(Resid.RESID, Square.xx_2);

	/**Constant containing the Function x^2+1	 */
	final static public CatDerive
		xxp1 = new CatDerive(Square.SQUARE, Succ.SUCC);

	/**Constant containing the Function 1/(x^2-1)
	 * Negative of 1/(1-x^2) (see below)	 */
	final static public CatDerive
		Inv1_xx = new CatDerive(Inv.INV, One_xx);

	/**Constant containing the Function 1/(1-x^2)
	 * Negative of 1/(x^2-1) (see above)	 */
	final static public CatDerive
		Invxx_1 = new CatDerive(	Inv.INV,
									xx_1);
//									BodyFuncs.ArTanH.ArTanH);

	/**Constant containing the Function 1/(1+x^2)
	 * Also defines the Integral.	 */
/*	final static public CatDerive Invxxp1 = new CatDerive(Inv.Inv,
																xxp1,
																BodyFuncs.ArcTan.ArcTan);
*/
	/**Constant containing the Lorentz Function 1/(1+x^2)
	 * It can be normed by dividing it by Pi
	 * and thus be used as a Simulation of the Delta3 Function.
	 * Also defines the Integral,
	 * the Derivative and Inverse can be determined analytically.
	 * See also: Gauss for Delta2	 */
	final static public CatDerive
		Lorentz = new CatDerive(	Inv.INV,
									xxp1);
//									BodyFuncs.ArcTan.ArcTan);

	/**Returns a normed, scaled Lorentz Function usable as a Simulation of the Delta Function of width H.	 */
	public IDeriveAble Delta3(Object H) {
		return 	new CatDerive(			new MulAt(H),
				new CatDerive(Lorentz,  new MulAt(((IGroupM) H).div(IMeasurAble.pi)))); }

	/**Constant containing the Function 1/SqRt(x^2+1)	 */
	final static public CatDerive
		InvSqRtxxp1 = new CatDerive(	SqRt.SQRT,
										Lorentz);

	/**Constant containing the Function 1/SqRt(x^2-1)	 */
	final static public CatDerive
		InvSqRtxx_1 = new CatDerive(	SqRt.SQRT,
										Invxx_1);

	/**Constant containing the Function 1/SqRt(1-x^2)	 */
	final static public CatDerive
		InvSqRt1_xx = new CatDerive(	SqRt.SQRT,
										Inv1_xx);

	/**Constant containing the Function SqRt(x^2-1)
	 * Together with it's Integral: (x*SqRtxx_1 + arcSin)/2 +	 */
	final static public CatDerive
		SqRtxx_1 = new CatDerive(SqRt.SQRT, xx_1);

	/**Constant containing the Function SqRt(1-x^2)	 */
	final static public CatDerive
		SqRt1_xx = new CatDerive(SqRt.SQRT, One_xx);

	/**Constant containing the Function SqRt(x^2+1)	 */
	final static public CatDerive
		SqRtxxp1 = new CatDerive(SqRt.SQRT, xxp1);


	//////////////////////////////////
	//	Definition of Class Algebra	//
	//////////////////////////////////

	/**The Function is realized as an inner Component,
	 * because a copyAt() must be possible!
	 * The Type is 'Object' and Map() returns a Function only if 'inner' is of Type Function.
	 * Thus Algebra also implements the previous 'Const' Function. */
//	protected IFunction inner;
//	protected IDeriveAble inner;
	protected Object inner;

	/**Empty Constructor	 */
	protected Algebra(){}

	/**Initializing Constructor	 */
	public Algebra(Object Function) {
		if (Function instanceof Algebra)
			Function = ((Algebra) Function).inner;  //don't allow nested Algebras!
		if (!(inner instanceof IFunction)) Derivative = CCountAble.Zero;
		this.inner = Function; }

	/**Returns true, when this Class can operate on Arguments of this Type
	 * This Function makes sense at this Level,
	 * because here there is always the Alternative
	 * not to operate on the Constants,
	 * but to operate on the Functions and operate the Results on evaluation.	 */
	public boolean canProcess(Object arg) {
		if (! (inner instanceof IFunction)) return true;
		return ((IFunction) inner).canProcess (arg); } //Algebra can always postProcess any Result

	/**Returns an alternative Representation that is 'simplified'
	 * Eliminates Algebras from the Expression,
	 * because these are only Containers	 */
	public IFunction simplify() {
		if (! (inner instanceof IFunction)) return this;
		return (IFunction) (inner = ((IFunction) inner).simplify()); }

	/**Returns the Function Value of the inner Function Variable.
	 * The Implementation with this additional indirection
	 * is similar to BodyDouble or RingLong,
	 * and necessary because Function has no inner Components
	 * that could be copied in copyAt().
	 * It saves this indirection in the previous concatenation
	 * from which all Functions inherited and thus speeds up
	 * Evaluation of 'simple' Functions.	 */
	public Object Map(Object arg) {
		if (! (inner instanceof IFunction)) return inner;
		return ((IFunction) inner).Map(arg); }

	/**Helper Routine to convert to long from any other numeric Type:
	 * RingLong, Number or ICountAble. Even Strings are now supported! 	 */
	final public static IFunction convertArg (Object arg) {
//		if (arg instanceof Algebra)		return ((Algebra)arg).inner;
		if (arg instanceof IFunction) return (IFunction) arg;
		return new Algebra(arg); } // Const(arg); }

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
	final public boolean equals  (Object arg) {
		if (inner instanceof ICopyAble)
			return inner.equals(convertArg (arg));
			return inner ==		convertArg (arg) ; }	//Simple Functions are tested for Equality

	/**Addition in Place: +=
	 * It reduces Complexity by delegating to the inner Function if possible.	 */
	final public ISemiGroup addAt (Object arg) {
		if (arg == null) return this;
		if (canProcess(arg) && inner instanceof ISemiGroup)
			((ISemiGroup) inner).addAt(arg); //for Algebras and SemiGroup
		else if (inner instanceof Const)
			((ISemiGroup) ((IFunction) inner).Map(null)).addAt(arg);
		else if (inner instanceof IFunction)
			 inner = new Sum((IFunction)inner , convertArg(arg));
		else inner = new Sum(new Algebra (inner), convertArg(arg));
		return this; }

	/**Multiplication in Place: *=
	 * It reduces Complexity by delegating to the inner Function if possible.	 */
	final public ISemiGroupM mulAt(Object arg) {
		if (arg == null) return this;
		if (canProcess(arg) && inner instanceof ISemiGroupM)
			((ISemiGroupM) inner).mulAt(arg); //for Algebras and SemiGroup
		else if (inner instanceof Const)
			((ISemiGroupM) ((IFunction) inner).Map(null)).mulAt(arg);
		else if (inner instanceof IFunction)
			 inner = new Prod((IFunction)inner , convertArg(arg));
		else inner = new Prod(new Algebra (inner), convertArg(arg));
		return this; }

	/**Subtraction in Place: -=
	 * It reduces Complexity by delegating to the inner Function if possible.	 */
	final public IGroup subAt    (Object arg) {
		if (arg == null) return this;
		if (canProcess(arg) && inner instanceof IGroup)
			((IGroup) inner).subAt(arg); //for Algebras and SemiGroup
		else if (inner instanceof Const)
			((IGroup) ((IFunction)inner).Map(null)).subAt(arg);
		else if (inner instanceof IFunction)
			 inner = new Diff((IFunction)inner , convertArg(arg));
		else inner = new Diff(new Algebra (inner), convertArg(arg));
		return this; }

	/**Division in Place: /=
	 * It reduces Complexity by delegating to the inner IFunction if possible.	 */
	public IGroupM divAt(Object arg) {
		if (arg == null) return this;
		if (canProcess(arg) && inner instanceof IGroupM)
			((IGroupM) inner).divAt(arg); //for Algebras and SemiGroup
		else if (inner instanceof Const)
			((IGroupM) ((IFunction)inner).Map(null)).divAt(arg);
		else if (inner instanceof IFunction)
			 inner = new Quot((IFunction)inner , convertArg(arg));
		else inner = new Quot(new Algebra (inner), convertArg(arg));
		return this; }

	/**Concatenation / Mapping in Place: arg�=this  ==  arg(this(x))		*/
	public ISemiMonoid catAt(Object arg) {
			 if (!(  arg instanceof IFunction)) inner = arg;
		else if (    arg instanceof Const       ) inner = arg;
		else if (!(inner instanceof IFunction)) inner = ((IFunction) arg).Map(inner);
		else 									  inner = new CatDerive((IFunction) arg,
																		(IFunction) inner);
		return this; }

	/**Concatenation / Mapping in Place: this=�arg  ==  this(arg(x))
	 * Returns the (modified) Argument instead of the 'this'.	 */
	public ISemiMonoid MapAt(ISemiMonoid arg) {
		if (!(inner instanceof IFunction)) return this;
		if (  inner instanceof Const       ) return this;
		return ((Algebra) arg).catAt(this); }  //only Algebras can conCat in Place

	/**Returns the Inverse Function to this one.	 */
	public IMonoid revAt() {
		inner = ((IInvertAble) inner).getInverse(); return this; }

    /**Sets the Integral from outside
     * This can be done only once, after that an IllegalStateException is thrown.     */
	public void	setInverse(IInvertAble Inverse) { ((IInvertAble) inner).setInverse(Inverse); }

	/**Returns the Inverse Function to this one.	 */
	public IInvertAble getInverse() { return new Algebra(((IInvertAble) inner).getInverse()); }

	/**Setting to 0 in Place:
	 * It reduces Complexity by delegating to the inner Function if possible.	 */
	final public IGroup zeroAt()	{
		if (inner instanceof IGroup)
			((IGroup) inner).zeroAt();
		else inner = CCountAble.Zero;
		return this; }

	/**Setting to 1 in Place:
	 * It reduces Complexity by delegating to the inner Function if possible.	 */
	final public IGroupM oneAt()	{
		if (inner instanceof IGroupM)
			((IGroupM) inner).oneAt();
		else inner = CCountAble.One;
		return this; }

	/**Integer Part of this Number
	 * asymmetric to 0: i.e. Int(-x) = -Int(x)   */
	public IIntRing IntAt() {
//		if (inner instanceof IIntRing)
			((IIntRing) inner).oneAt(); return this; }

	/**Setting to Identity in Place:
	 * I use the static Identity Function here.	 */
	final public IMonoid IdentityAt() { inner = Identity(); return this; }

	/**Complement: ~=
	 * necessary for gAdic Calculation			*/
	public IIntRing CmplAt() {
//		if (inner instanceof IIntRing)
			((IIntRing)inner).CmplAt(); return this; }

	/**Returns the Value raised by one g-Adic Position in Place	 */
	public IIntRing toUpperAt() {
//		if (inner instanceof IIntRing)
			((IIntRing)inner).toUpperAt(); return this; }

	/**Carry the Overflow through the g-adic Representation.	 */
	public void addCarry() {
//		if (inner instanceof IIntRing)
			((IIntRing)inner).addCarry(); }

	/**Returns the conjugate Complex Number in Place:
	 * i.e. the imaginary Part flips it's sign.	 */
	public IIntRing cjgAt() {
//		if (inner instanceof IIntRing)
			((IIntRing)inner).cjgAt();
		return this; }

	/**Returns true, if the Number/Object has imaginary parts
	 * that require	special treatment (see ...)	 */
	public boolean isComplex() {
		if (inner instanceof IIntRing)
			return ((IIntRing) inner).isComplex();
			return false;}

	//////////////////////////
	//	Interface CopyAble	//
	//////////////////////////

	/**Complement to copyAt() and shallopCopyAt().
	 * Does a 'deepCopy', to a certain Level
	 * i.e. also inner Components are copied up to the Depth.
	 * Returns the itself for further use.
	 * Since Functions are (should be) immutable,
	 * they can be safely passed by Reference, because they cannot be changed! */
	public ICopyAble copyAt(Object arg, int Depth) {
		inner = convertArg(arg); return this; }

	/**Creates an uninitalized new Instance of it's class.
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() { return new Algebra(); }

	/**Delegates to the wrapped Function's own string representation.
	 * @return  The string representation of the Function.
	 * @since   JDK1.0	 */
	public String toString() { return inner.toString(); }

	/**Fills this Instance with the Contents read from the String.	 */
	public ICopyAble fromStreamAt(StreamTokenizer ST) throws IOException {
		throw new AbstractMethodError(); }
	//TODO: implement this. For this you have to parse the Function Expression!


	//////////////////////////////
	//	IInvertAble Interface	//
	//////////////////////////////

	/**Maps arg in Place through the wrapped Function, or Copies the constant inner Value into arg.	 */
	public Object   MapAt(Object arg) {
		if (inner instanceof IFunction)
			return ((IFunction) inner).MapAt(arg);
			return ((ICopyAble) arg).copyAt (inner); }

	/**Maps arg through this Function's Inverse. @return the un-mapped Value	 */
	public Object UnMap  (Object arg) { return getInverse().Map  (arg); }

	/**Maps arg in Place through this Function's Inverse. @return the un-mapped Value	 */
	public Object UnMapAt(Object arg) { return getInverse().MapAt(arg); }

	/**Maps arg in Place through this Function's Inverse, typed as an {@link IMonoid}. @return arg mapped	 */
	public IMonoid pamAt(Object arg) { return (IMonoid) ((IMonoid) getInverse()).mapAt(arg); }

	/**Maps arg in Place through this Function, typed as an {@link ISemiMonoid}. @return arg mapped	 */
	public ISemiMonoid mapAt(Object arg) { return (ISemiMonoid) MapAt(arg); }

	/**Maps arg through this Function, typed as an {@link ISemiMonoid}. @return the mapped Value	 */
	public ISemiMonoid map(Object arg) { return (ISemiMonoid) Map(arg); }

    /**Local Storage for the Integral to be set by setIntegral()     */
    protected IDeriveAble Derivative;

    /**Sets the Integral from outside
     * This can be done only once, after that an IllegalStateException is thrown.     */
	public void	setDerivative(IDeriveAble Derivative) {
        if ( this.Derivative != null) throw new IllegalStateException();
		if (      Derivative instanceof Algebra) {
        	 this.Derivative = Derivative;
			 ((IDeriveAble) inner).setDerivative((IDeriveAble)((Algebra) Derivative).inner); }
		else ((IDeriveAble) inner).setDerivative(                       Derivative); }

	/**Returns the Derivative of this Function
	 * No caching necessary here, since it is already cached in inner.	 */
/*	public IDeriveAble Derivative(int n) {
		if (inner instanceof IFunction)
			return ((IDeriveAble) inner).Derivative(n);
			return CCountAble.Zero; }
*/

	/**Returns the Derivative of this Function, wrapping the wrapped Function's own cached Derivative.
	 * No caching necessary here, since it is already cached in inner.	 */
	public IDeriveAble getDerivative() {
		if (Derivative != null) return Derivative;
		return Derivative =  (Algebra)
							((Algebra) newInstance()).copyAt(((IDeriveAble) inner).getDerivative()); } //((IDeriveAble) inner).getDerivative()); }

    /**Local Storage for the Integral to be set by setIntegral()     */
    protected IDeriveAble Integral;

    /**Sets the Integral from outside
     * This can be done only once, after that an IllegalStateException is thrown.     */
	public void	setIntegral(IDeriveAble Integral) {
        if (this.Integral != null) throw new IllegalStateException();
        	this.Integral = Integral; }

	/**Returns the Integral of this Function
	 * No caching necessary here, since it is already cached in inner.	 */
	public IDeriveAble getIntegral() {
		if (Integral != null) return Integral;
		if (inner instanceof IFunction)
		return Integral = (Algebra)
						 ((Algebra) newInstance()).copyAt(((IDeriveAble) inner).getIntegral()); //((IDeriveAble) inner).getIntegral());
		return Integral = new MulAt(inner); }


	//////////////////////
	//	Optimizations:	//
	//////////////////////

	/**Testing for 0:	 */
	public boolean isZero()	{
		if (inner instanceof IGroup)
			return ((IGroup) inner).isZero();
		return super.isZero(); }

	/**Testing for 1:	 */
	public boolean isOne() {
		if (inner instanceof IGroupM)
			return ((IGroupM) inner).isOne();
		return super.isOne(); }

	/**Negation in Place: -=
	 * It overwrites the 0 specific Implementation in Zero.
	 * It reduces the Copies by implementing: -1 = -1.	 */
	public IGroup negAt	() {
		if (inner instanceof IGroup)
			return ((IGroup) inner).negAt();
		return super.negAt(); }

	/**Inversion in Place: 1/=
	 * It reduces Complexity by implementing: 1/1 = 1.	 */
	public IGroupM invAt () {
		if (inner instanceof IGroupM)
			return ((IGroupM) inner).invAt();
		return super.invAt(); }

	/**Rounds the wrapped Value down to the nearest Integer In-Place. @return this Algebra	 */
	public IMetricIRing FloorAt() {
		((IMetricIRing) inner).FloorAt();
		return this; }

	/**Compares the wrapped Value to arg. @return this <  arg	 */
	public boolean isLessThan(Object arg) {
		return ((IOrderAble) inner).isLessThan(arg); }

	/**Sets the wrapped Value to its Type's maximum In-Place. @return this Algebra	 */
	public IWellOrder maxValueAt() {
		((IWellOrder) inner).maxValueAt(); return this; }

	/** Returns the Object Value represented by an 8 Bit Integer	  */
	public byte   getByte () { return ((ICountAble) inner).getByte(); }

	/** Returns the Object Value represented by a 16 Bit Integer	  */
	public short getShort () { return ((ICountAble) inner).getShort(); }

	/** Returns the Object Value represented by a 32 Bit Integer	  */
	public int     getInt () { return ((ICountAble) inner).getInt(); }

	/** Returns the Object Value represented by a 64 Bit Integer	  */
	public long   getLong () { return ((ICountAble) inner).getLong(); }

	/** Returns the Object Value represented by a scalar Variable of Type double.
	 * It consists of an IEEE Number with 64 Bit (8 Byte):
	 * 52 Bit Mantissa, 11 Bit Exponent, 1 Bit Sign	  */
	public double getDouble () { return ((ICountAble) inner).getDouble(); }

	/** Returns the Object Value represented by a scalar Variable of Type float.
	 * It consists of an IEEE Number with 32 Bit (4 Byte):
	 * 23 Bit Mantissa, 8 Bit Exponent, 1 Bit Sign	  */
	public float   getFloat () { return ((ICountAble) inner).getFloat(); }

}
