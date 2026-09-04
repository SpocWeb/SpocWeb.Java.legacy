package streamIO.copy.group.ring.metric.body.vector;

import streamIO.copy.ICopyAble;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ISemiGroup;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.IRing;
import streamIO.copy.group.ring.metric.IMetricIRing;
import streamIO.copy.group.ring.metric.IScalarMetric;
import streamIO.copy.group.ring.metric.IWellOrder;
import streamIO.copy.groupM.IGroupM;
import streamIO.copy.groupM.ISemiGroupM;
import streamIO.copy.monoid.integer.Permutation;
import streamIO.copy.order.IOrder;
import streamIO.object.enumer.IndexEnumerator;
import function.IFunction;
import function.IOrderAble;

/**
  * Title: Tensor<p>
  * Description:
  * Implementation of a Tensor as a Vector of IIntRing Elements 
  * (which can be Tensors of their own)
  * These Tensors can be randomly accessed using the getAt() and setAt() Methods.
  * 
  * Tensors are Vectors of higher Degree. 
  * When not multiplying with the first Dimension, 
  * Multiplication has to be deferred down the recursive Call 
  * through the Tensor Hierarchy. 
  * 
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-11-26, 01;13;44<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
/** Most generic Implementation of a Tensor
  * The Elements can be Scalars or Tensors, which creates a Tensor of higher Degree
  */
public class Tensor
extends ATensor { //AManifold

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**Coefficients of the Tensor or g-adic Number
	 * Array ranges from 0 to mDim
	 * The Declaration is public instead of protected,
	 * although the Elements should never be modified directly.
	 * The reason is that the Package 'BodyTensor' accesses a directly.	 */
	public IIntRing a [];	//AIntRing a []; //TODO: protect it!
//	protected IIntRing a [];	//AIntRing a [];

	/** Empty Constructor	 */
	public Tensor (){}

	/** Copy-Constructor	 */
	Tensor (VectorDbl arg) { copyAt(arg.a); }

	//Tensor (int	arg[], int length){ VectorDbl(Arr(arg, length)); }
	//Tensor (long	arg[], int length){ VectorDbl(Arr(arg, length)); }
	//Tensor (float	arg[], int length){ VectorDbl(Arr(arg, length)); }

	/**Constructor for an Array of Type IIntRing.
	 * The Degree is automatically adjusted to the Array Length
	 * and the Vector is filled with the Elements from the Array.	 */
	Tensor (double[] arg) { copyAt(arg); }

	/**Constructor for an Array of Type IIntRing.
	 * The Degree is automatically adjusted to the Array Length
	 * and the Vector is filled with the Elements from the Array.	 */
	Tensor (IIntRing[] arg) { copyAt(arg); }

	/**Constructor for an Array of Type IIntRing.
	 * The Degree is automatically adjusted to the Array Length
	 * and the Vector is filled with the Elements from the Array.	 */
	Tensor (IIntRing elements, int Length) {
		Carry = elements;
		setDim(Length, true, true); }

	//////////////////////////////
	//	Sampling of a Function	//
	//////////////////////////////

	/**Generates a Manifold by sampling f across x	 */
	public Tensor (IFunction f,  Tensor x) {	//preserve Internals of x0
		setDim(x.mDim , false, false);
		int i = -1;
		while (++i <= mDim) {
			a[i] = (IIntRing) f.Map(x.a[i]); }
	}

	/**Generates a Manifold by sampling f across X	 */
/*	public Tensor (Tensor X, IRealFunction f) {
		super(X);
		letGrad(X.getDim(), false, false);
		int i = -1;
		while (++i <= mDim) {
			a[i] = (IIntRing) f.map(X); }
	}
*/

	/** Samples the Function f on the Interval [x0, x0+Grad*dx]	 */
	public Tensor (IFunction f, ISemiGroup x0, Object dx, int Grad) {	//preserve Internals of x0
		setDim(Grad , false, false);
		int i = 0; a[0] = (IIntRing) f.Map(x0);
		while (++i <= mDim) {
			a[i] = (IIntRing) f.Map(x0.addAt(dx)); }
	}

	/** Creates an equidistant Raster on the Interval [x0, x0+Grad*dx]	 */
	public Tensor (ISemiGroup x0, Object dx, int Grad) {
		setDim(Grad, false, false);
		int i = 0; a[0] = (IIntRing) x0;
		while (++i <= mDim) {
			a[i] = (IIntRing) (x0 = x0.add(dx)); }
	}

	/**Sets the Grad of the Polynom == Dimension-1 of the Vector.
	 * When Preserve = true, the Contents of the Polynom is preserved.
	 * Initializes the Elements above mDim to 0, when zeroUpper = true.
	 * The Grad is the Period for the large Rotation Operations.
	 *
	 * Possibilities: new Array is...
	 * 1) uninitialized and potentially (half) empty (preserved = false)
	 * 2) initialized with only new Elements (not implemented)
	 *		use a brand new gAdic.
	 * 3) initialized with preserved old and new Elements
	 *		(Preserve = true)
	 *
	 * The Carry Element is used to generate new Elements. */
	public int setDim(int Grad, boolean preserve, boolean zeroUpper) {
		if (Grad > mDim) //ReDim and extend the Array
		{	//Instantiate new upper Elements
			int oldLength  = 0; if ((a != null) && (a[0] != null))oldLength= a.length;
			if (oldLength <= Grad)	//Redimension and Copy the Array only when necessary!
			{	//when this is a gAdic Number, initialize the Behavior of the new Items.
				IIntRing tmpArr[] = new IIntRing [Grad +1];
				if (preserve) {
//					IIntRing tmp;	//Copy the old Elements...
					if (oldLength > 0) System.arraycopy (a, 0, tmpArr, 0, oldLength);	//
					IIntRing Item = Carry;	//ensure that all Items are of the same Type...
					if (a != null) if (a[0] != null) Item = a[0];	//...by copying from a[0]
					if (Carry == null) Carry = (IIntRing) Item.newInstance();
					for (int i = oldLength-1; ++i <= Grad ;) {	//initialize the new Elements
						tmpArr[i] = (IIntRing) Item.newInstance();
//						tmp = tmpArr[i];
///						tmp.Signed = false;	//default
//						tmp.Modul = Modul;	//too expensive to make the next Command conditional!
//						tmp.Carry = Carry;
					}
				}	//instantiate the new upper Coefficients and set their behavior
				a = tmpArr;	//now set the coefficients
			}	//now we have the same Fields, also initialized in a larger Array
			if (zeroUpper) //initialisieren der Werte auf Zero nur, wenn es gewollt ist
			{	//initialize only the new, upper Coefficients to Zero
				for (int i = mDim; ++i <= Grad;)
					a[i].zeroAt();	//not necessary normally, because Elements are initialized to Zero
			}
		}
//		else {}   //Don't make it shorter!
/*		if (preserve && (Modul > 0)) {
			if (mDim >= 0) a[mDim].Signed = false;
			if ( Grad >= 0) a[ Grad].Signed = true;
		}
*/		mDim = -1;	if (Grad >= -1) mDim = Grad;  //Don't initialize the Parameters
		return mDim;
	}

	///////////////////////////////////////////////////////////////////////////
	//	IndexEnumerator: abstract Methods
	///////////////////////////////////////////////////////////////////////////

	/** @return the Item at the given Multi Index Position
	  * This is used to define the generic Tensor Product!
	  * @return this for index.Grad == -1  !
	  *
	  * Instead of defining this recursively, it is calculated faster iteratively!
	  */
	public IIntRing getAt(Permutation MIndex) {
		return getAt(MIndex, MIndex.getDim()); }

	/** @return the Item at the given Multi Index Position
	  * This is used to define the generic Tensor Product!
	  * @return this for index.Grad == -1  !
	  *
	  * Instead of defining this recursively, it is calculated faster iteratively!
	  */
	public IIntRing getAt(Permutation MIndex, int MaxGrad) {
		IIntRing ret = this;
		int Degree  = -1;
		while (++Degree <= MaxGrad) {
			ret = ((Tensor) ret).a[MIndex.a[Degree]]; }
		return ret; }

	/** @return the Item at the given Multi Index Position
	  * This is used to define the generic Tensor Product!
	  * @return this for index.Grad == -1  !
	  *
	  * Instead of defining this recursively, it is calculated faster iteratively!
	  */
	public void setAt(Permutation MIndex, IIntRing Value) {
		setAt(MIndex, Value, MIndex.getDim()); }

	/** @return the Item at the given Multi Index Position
	  * This is used to define the generic Tensor Product!
	  * @return this for index.Grad == -1  !
	  *
	  * Instead of defining this recursively, it is calculated faster iteratively!
	  */
	public void setAt(Permutation MIndex, IIntRing Value, int MaxGrad) {
		IIntRing ret = this;
		int Degree = -1;
		while (++Degree < MaxGrad) {
			ret = ((Tensor) ret).a[MIndex.a[Degree]]; }
				  ((Tensor) ret).a[MIndex.a[Degree]] = Value;
//		ret.copyAt(Value);
//		ret   =    Value ;
	}

	/** @return the Item at the given absolute Position
	  * While this is possible in principle for all Enumerators,
	  * it is too ineffective to loop through the whole Enumerator
	  */
	public Object getAt(int index) {
//		if (index >= a.length) return EOI;
		if (index >  mDim   ) return EOI;
		if (index < 0) return SOI;
		return a[index]; }

	/** @return the Item at the given absolute Position
	  * While this is possible in principle for all Enumerators,
	  * it is too ineffective to loop through the whole Enumerator
	  */
	public IndexEnumerator addAt(int Pos, Object arg) {
		++minorVersion;
		++majorVersion;
		System.arraycopy(	a, Pos - 1,
							a, Pos, mDim - Pos + 2);
		a[Pos] = (IIntRing)arg; ++mDim;
		return this; }

	/** Removes the Object at the given Index in the Container with this Enumerator knowing it.
	  * One Problem is other Enumerators that concurrently work through this Container.
	  * Another Problem is that removing the Item may not be possible at all.
	  * That is why this Method should throw an exception if removing is not allowed.   */
	public Object removeAt(int Pos) {
		++minorVersion;
		++majorVersion;
		Object Value = a[Pos];
		System.arraycopy(	a, Pos + 1,
							a, Pos, mDim - Pos + 1);
		return Value; }

	/** Replaces the Object at the given Index in the Container with this Enumerator knowing it.
	  * One Problem is other Enumerators that concurrently work through this Container.
	  * Another Problem is that removing the Item may not be possible at all.
	  * That is why this Method should throw an exception if removing is not allowed.   */
	public Object setAt(int index, Object item) {
		++minorVersion;
		Object ret = null;
		if (mDim < index) {
			setDim(index, true, true); 
			ret = a[index];
		}
		a[index] = (IIntRing) item;
		return ret; }

	///////////////////////////////////////////////////////////////////////////
	//  Tensor specific Methods
	///////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	//	Normalization, Orthogonalization
	////////////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////
	//  Methods
	///////////////////////////////////////////////////////////////////////////

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
	 * @see     <{Hashtable}>
	 * @since   JDK1.0 	 */
	public boolean equals(Object arg) {	//Compare the two Tensors elementwise
//		if (! (arg instanceof Tensor))
//			return equals(AOrderAble.getDouble(arg));
		Tensor arg_ = (Tensor) arg;
		if (mDim != arg_.mDim) return false;
		int i = mDim+1;
		while (--i >= 0) {
			if (! a[i].equals(arg_.a[i])) {
				return false; }
		} return true; }

	/** Large Rotation right by one Item in Place	 */
	Tensor rorLargeAt() {
		IIntRing tmp = a[0];
		System.arraycopy(a, 1, a, 0, mDim);	//very fast
		a[mDim] = tmp;	//preserve the first Item
		return this; }

	/** Large Rotation left by one Item in Place	 */
	Tensor rolLargeAt() {
		IIntRing tmp = a[mDim];
		System.arraycopy(a, 0, a, 1, mDim);	//very fast
		a[0] = tmp;	//preserve the first Item
		return this; }

	/** Copies only the Values of the Items including Start- and EndIndex. 	 */
	void CopyAt(IIntRing[] a2, int Grad, int StartIndex, int EndIndex, int Depth) {
		setDim(Grad, true, false);	//resize, don't set the higher coefficients to anything!
		int i = StartIndex-1;
		while (++i <= EndIndex)
			a[i] = a2[i];
		return; }

	/** Copies only the Values of the Items including Start- and EndIndex. 	 */
	void CopyAt(Object[] a2, int Grad, int StartIndex, int EndIndex, int Depth) {
		setDim(Grad, true, false);	//resize, don't set the higher coefficients to anything!
		int i = StartIndex-1;
		while (++i <= EndIndex)
			a[i] = (IIntRing) a2[i];
		return; }

	/** @return Negation in Place: -		*/
	public IGroup negAt() {
		int i = mDim+1;
		while (--i >= 0)
			a[i].negAt();
		return this; }

	/** @return Inversion in Place: 1/x		*/
	public IGroupM invAt() {
		int i = mDim+1;
		while (--i >= 0)
			a[i].invAt();
		return this; }

	/** @return the maximum Value  (less than Infinity) for this Class in Place.	 */
	public IWellOrder maxValueAt() {
		int i = mDim+1;
		while (--i >= 0)
			((IWellOrder) a[i]).maxValueAt();
		return this; }

	/** @return the maximum Value  (less than Infinity) for this Class in Place.	 */
//	public WellOrder maxValueAt() { Value = Double.MAX_VALUE; return this; }

	/** @return A Vector with integer Numbers in Place: FloorAt	*/
	public IIntRing IntAt() {
		int i = mDim+1;
		while (--i >= 0) //TODO: find out how IntAt should be implemented!
			a[i].IntAt();
		return this; }

	/** @return A Vector with integer Numbers in Place: FloorAt	*/
	public IMetricIRing FloorAt() {
		int i = mDim+1;
		while (--i >= 0)
			((IMetricIRing) a[i]).FloorAt();
		return this; }

	//////////////////////////
	//	Vector Operations:	//
	//////////////////////////

	/**Removes leading 0s by decreasing the Grad	 */
	public ITensor canonicalizeAt() {
		while ((mDim > 0) && (a[mDim].isZero()))
			mDim--; 	//Remove leading 0s
		return this; }

	//////////////////////////////
	//	Interface shiftAble:	//
	//////////////////////////////

	//////////////////
	//	Constants	//
	//////////////////

	//These Routines rely on the Modulus being greater than three!

	/**Returns 2 in Place:	*/
	public IIntRing   twoAt() { //mGrad = 0; a[0] = ICountAble.TWO  ;
		int i = mDim+1;
		while (--i >= 0) {
			a[i].twoAt();
		} return this; }

	/**Returns 3 in Place:	*/
	public IIntRing threeAt() { //mGrad = 0; a[0] = ICountAble.THREE;
		int i = mDim+1;
		while (--i >= 0) {
			a[i].threeAt();
		} return this; }

	//////////////////////////
	//	Replication Group:	//
	//////////////////////////

	/**Setting to 0 in Place:
	 * It is faster, but less correct to set the Grad to -1,
	 * because on Multiplication it is no longer Grad(P1*P2) <= Grad(P1) + Grad(P2)		*/
	public IGroup zeroAt() { //mGrad = 0; a[0] = ICountAble.ZERO;
		mDim = -1;
		return this; }

	/**Testing for 0:			*/
	public boolean isZero()	{ //return (mDim == 0) && (a[0] == ICountAble.ZERO); }
		int i = mDim+1;
		while (--i >= 0) {
			if (! a[i].isZero()) {
				return false; }
		} return true; }

	//////////////////////////
	//	Replication GroupM:	//
	//////////////////////////

	/**Testing for 1:	 */
	public boolean isOne() { //return (mDim == 0) && (a[0] == ICountAble.ONE); }
		int i = mDim+1;
		while (--i >= 0) {
			if (! a[i].isOne()) {
				return false; }
		} return true; }

	/**Setting to 1 in Place:	 */
	public IGroupM oneAt() { //mGrad = 0; a[0] = ICountAble.ONE; return this; }
		int i = mDim+1;
		while (--i >= 0) {
			a[i].oneAt(); }
		return this; }

	//////////////////////////////
	//	Replication SemiGroupM:	//
	//////////////////////////////

	/**Optimizations are very likely here,
	 * but the Algorithm would be too complicated to be implemented here!	 */

	/** @return the Square in Place: x*=x	*/
	public ISemiGroupM sqrAt () {
		int i = mDim+1;
		while (--i >= 0) {
			a[i].sqrAt();
		} return this; }

	/** @return the Cubic in Place: x*=x^2	*/
	public ISemiGroupM cbcAt () {
		int i = mDim+1;
		while (--i >= 0) {
			a[i].cbcAt();
		} return this; }

	/** Conjugation in Place: ~=	 */
	public IIntRing cjgAt() {
		int i = mDim+1;
		while (--i >= 0) {
			a[i].cjgAt();
		} return this; }

	/** @return the Complement in Place: 1-x	 */
	public IIntRing CmplAt() {
		int i = mDim+1;
		while (--i >= 0) {
			a[i].CmplAt();
		} return this; }

	/** @return true when this is Object has an imaginary Component	 */
	public boolean isComplex() {
		int i = mDim+1;
		while (--i >= 0) {
			if (a[i].isComplex()) {
				return true; }
		} return false; }

	/** Carry the Overflow through the g-adic Representation.	 */
	public void addCarry() {}

	/** Carry the Overflow through the g-adic Representation.	 */
	public IIntRing toUpperAt() { return this; }

	/** @return the Sum of all Elements	 */
	public IIntRing Sum() {
		int i = mDim;
		IIntRing ret = (IIntRing) a[i].copy();
		while (--i >= 0) {
			ret.addAt(a[i]); }
		return ret; }

	/** @return the Sum of all Elements	 */
	public IIntRing Prod() {
		int i = mDim;
		IIntRing ret = (IIntRing) a[i].copy();
		while (--i >= 0) {
			ret.mulAt(a[i]); }
		return ret; }

	//////////////////////
	//	Optimizations:	//
	//////////////////////

	/** Adds or subtracts a Polynom / Vector from another in Place
	  * optionally multiplies the first and/or second Vector with a Scalar.
	  * The Sequence of Processing is swapped, so the Carry can add
	  * through the (newer) higher Coefficients (no longer necessary, only for gAdic).	 */
	public Tensor opAt(Object s1, Object arg, IIntRing s2, int Operation) {
		int i = mDim+1;
		while (--i >= 0) {
			switch (Operation) {
			case opFlagAdd		: a[i].     addAt(    arg    ); break; 	//only for Tensor + Tensor
			case opFlagSubt		: a[i].    subAt(    arg    ); break; 	//only for Tensor - Tensor
			case opFlagMMul		: a[i].     mulAt(    arg    ); break; 	//only for Manifold * Manifold
			case opFlagMDiv		: a[i].     divAt(    arg    ); break; 	//only for Manifold / Manifold
			case opFlagLin		: a[i].     LinAt(s1, arg    ); break;
			case opFlag_AddProd : a[i]. addProdAt(s1, arg    ); break;
			case opFlagSubtProd : a[i].subtProdAt(s1, arg    ); break;
			case opFlagBiLin	: a[i].   BiLinAt(s1, arg, s2); break;
			case opFlagMax		:((IOrder) a[i]).MaxAt(arg    ); break;
			case opFlagMin		:((IOrder) a[i]).MinAt(arg    ); break;
			case opFlagONE		: a[i].     oneAt(           ); break;
			case opFlagTWO		: a[i].     twoAt(           ); break;
			case opFlagTHREE	: a[i].   threeAt(           ); break;
			case opFlagFOUR 	: a[i].    fourAt(           ); break;
			}
		} return this; }

	/**Addition in Place: +=	 */
	public ISemiGroup addAt	(Object arg) {
		if (arg == null) return this;
		if (arg instanceof Tensor)
			return opAt(null, (Tensor) arg, null, opFlagAdd);
			return opAt(null,          arg, null, opFlagAdd); }

	/**Subtraction in Place: -=	 */
	public IGroup subAt		(Object arg) {
		if (arg == null) return this;
		if (arg instanceof Tensor)
			return opAt(null, (Tensor) arg, null, opFlagSubt);
			return opAt(null,          arg, null, opFlagSubt); }

	/**Manifold Operation: Multiplication in Place: x*=a
	 * Redefined, because already needed in Metric.	*/
	public ISemiGroupM mulAt	(Object arg) {
		if (arg == null) return this;
		if (arg instanceof Tensor)
			return opAt(null, (Tensor)arg, null, opFlagMMul);
			return opAt(null,         arg, null, opFlagMMul); }

	/**Manifold Operation: Division in Place: x/=a	*/
	public IGroupM divAt	(Object arg) {
		if (arg instanceof Tensor)
			return opAt(null, (Tensor)arg, null, opFlagMDiv);
			return opAt(null,         arg, null, opFlagMDiv); }

	/**  Linear Mapping in Place: x*=a + y	*/
	public IRing LinAt  (Object a1, Object y) {
		if ((!(a1 instanceof Tensor)) && (y instanceof Tensor))
			 return opAt(a1, (Tensor)y, null, opFlagLin);
			 return opAt(a1,         y, null, opFlagLin); }
//		else return super.LinAt(a1, y);	} //no Optimization!
/*		if (((a1 instanceof Tensor) && (((Tensor)a1).mDim != mDim)) ||
			(( y instanceof Tensor) && (((Tensor) y).mDim != mDim)))
			return (Tensor) super.LinAt (a1, y);	//Optimizations skipped, because of too many Cases
		if (a1 instanceof Tensor)
			if (y instanceof Tensor) {
				 int i = mDim+1; while (--i >= 0) a[i].LinAt(((Tensor) a1).a[i], ((Tensor) y).a[i]); }
			else{int i = mDim+1; while (--i >= 0) a[i].LinAt(((Tensor) a1).a[i], y); }
		else
			if (y instanceof Tensor) {
				 int i = mDim+1; while (--i >= 0) a[i].LinAt(a1, ((Tensor) y).a[i]); }
			else{int i = mDim+1; while (--i >= 0) a[i].LinAt(a1, y); }
		return this; }

	/**  Linear Mapping in Place: x+=a * y	*/
	public IRing addProdAt (Object a1, Object y)	{
		if ((!(a1 instanceof Tensor)) && (y instanceof Tensor))
			 return opAt(a1, (Tensor)y, null, opFlag_AddProd);
			 return opAt(a1,         y, null, opFlag_AddProd); }
//		else return super.addProdAt(a1, y); }	//no Optimization!
/*		if (((a1 instanceof Tensor) && (((Tensor)a1).mDim != mDim)) ||
			(( y instanceof Tensor) && (((Tensor) y).mDim != mDim)))
			return (Tensor) super.addProdAt (a1, y);	//no optimization here,
		if (a1 instanceof Tensor)		//otherwise the 4 different Cases below
			if (y instanceof Tensor) {	//become even more complicated!!!
				 int i = mDim+1; while (--i >= 0) a[i].addProdAt(((Tensor) a1).a[i], ((Tensor) y).a[i]); }
			else{int i = mDim+1; while (--i >= 0) a[i].addProdAt(((Tensor) a1).a[i], y); }
		else
			if (y instanceof Tensor) {
				 int i = mDim+1; while (--i >= 0) a[i].addProdAt(a1, ((Tensor) y).a[i]); }
			else{SemiGroupM tmp = ((SemiGroupM)a1).mul(y);
				 int i = mDim+1; while (--i >= 0) a[i].addAt(tmp); }
		return this; }

	/**  Linear Mapping in Place: x-=a * y	*/
	public IRing subtProdAt (Object a1, Object y) {
		if ((!(a1 instanceof Tensor)) && (y instanceof Tensor))
			 return opAt(a1, (Tensor)y, null, opFlagSubtProd);
			 return opAt(a1,         y, null, opFlagSubtProd); }
//		else return super.subtProdAt(a1, y); }	//no Optimization!
/*		if (((a1 instanceof Tensor) && (((Tensor)a1).mDim != mDim)) ||
			(( y instanceof Tensor) && (((Tensor) y).mDim != mDim)))
			return (Tensor) super.subtProdAt (a1, y);	//no optimization here,
		if (a1 instanceof Tensor)		//otherwise the 4 different Cases below
			if (y instanceof Tensor) {	//become even more complicated!!!
				 int i = mDim+1; while (--i >= 0) a[i].subtProdAt(((Tensor) a1).a[i], ((Tensor) y).a[i]); }
			else{int i = mDim+1; while (--i >= 0) a[i].subtProdAt(((Tensor) a1).a[i], y); }
		else
			if (y instanceof Tensor) {
				 int i = mDim+1; while (--i >= 0) a[i].subtProdAt(a1, ((Tensor) y).a[i]); }
			else{SemiGroupM tmp = ((SemiGroupM)a1).mul(y);
				 int i = mDim+1; while (--i >= 0) a[i].subAt(tmp); }
		return this; }

	/**BiLinear Mapping in Place: x*=a + y*b	*/
	public IRing BiLinAt(Object a1, Object y, Object b)	{
		if ((!(a1 instanceof Tensor)) && (y instanceof Tensor) && (!(b instanceof Tensor)))
			 return opAt(a1, (Tensor)y, (IIntRing)b, opFlagBiLin);
			 return opAt(a1,         y, (IIntRing)b, opFlagBiLin); }
//		else return super.BiLinAt(a1, y, b); }	//no Optimization!
/*		if (((a1 instanceof Tensor) && (((Tensor)a1).mDim != mDim)) ||
			(( y instanceof Tensor) && (((Tensor) y).mDim != mDim)) ||
			(( b instanceof Tensor) && (((Tensor) b).mDim != mDim)))
			return (Tensor) super.BiLinAt (a1, y, b);	//no optimization here,
		if (a1 instanceof Tensor)		//otherwise the 8 different Cases below
			if (y instanceof Tensor)	//become even more complicated!!!
				if (b instanceof Tensor) {
					 int i = mDim+1; while (--i >= 0) a[i].BiLinAt(((Tensor) a1).a[i], ((Tensor) y).a[i], ((Tensor) b).a[i]); }
				else{int i = mDim+1; while (--i >= 0) a[i].BiLinAt(((Tensor) a1).a[i], ((Tensor) y).a[i], b); }
			else
				if (b instanceof Tensor) {
					 int i = mDim+1; while (--i >= 0) a[i].BiLinAt(((Tensor) a1).a[i], y, ((Tensor) b).a[i]); }
				else{SemiGroupM tmp = ((SemiGroupM)a1).mul(y);
					 int i = mDim+1; while (--i >= 0) a[i].LinAt(((Tensor) a1).a[i], tmp); }
		else
			if (y instanceof Tensor)
				if (b instanceof Tensor) {
					 int i = mDim+1; while (--i >= 0) a[i].BiLinAt(a1, ((Tensor) y).a[i], ((Tensor) b).a[i]); }
				else{int i = mDim+1; while (--i >= 0) a[i].BiLinAt(a1, ((Tensor) y).a[i], b); }
			else
				if (b instanceof Tensor) {
					 int i = mDim+1; while (--i >= 0) a[i].BiLinAt(a1, y, ((Tensor) b).a[i]); }
				else{SemiGroupM tmp = ((SemiGroupM)a1).mul(y);
					 int i = mDim+1; while (--i >= 0) a[i].LinAt(a1, tmp); }
		return this; }

	/**Adds or subtracts a Polynom / Vector from another in Place
	 * optionally multiplies the first and/or second Vector with a Scalar.
	 * The Sequence of Processing is swapped, so the Carry can add
	 * through the (newer) higher Coefficients (no longer necessary, only for gAdic).	 */
	public Tensor op(Object s1, Tensor arg, IIntRing s2, int Operation) {
		return ((Tensor) copy()).opAt(s1, arg, s2, Operation); }

	/** Adds or subtracts a Polynom / Vector from another in Place
	  * optionally multiplies the first and/or second Vector with a Scalar.
	  * The Sequence of Processing is swapped, so the Carry can add
	  * through the (newer) higher Coefficients (no longer necessary, only for gAdic).	 */
	public Tensor opAt(Object s1, Tensor arg, IIntRing s2, int Operation) {
	    int largeGrad = mDim;						//= Math.max(mDim, arg.getDim());
	    int smallGrad = arg.mDim;	//getDim();	//= Math.min(mDim, arg.getDim());

	    if (smallGrad > mDim) {
			smallGrad = mDim;
		 largeGrad = arg.mDim; //getDim();
		 setDim(largeGrad, true, false);	//resize, keep existing Values,
		}	//don't set the higher coefficients to zero! => very fast.

		int i = -1;
		while (++i <= smallGrad)
			switch (Operation) {	//More performant, if you don't single out the Loop!
				case opFlagAdd		: a[i].     addAt(    arg.a[i]); break;	//Addition		 x+arg
				case opFlagSubt		: a[i].    subAt(    arg.a[i]); break;	//Subtraktion	 x-arg
				case opFlagMMul		: a[i].     mulAt(    arg.a[i]); break;	//Multiplikation x*arg
				case opFlagMDiv		: a[i].     divAt(    arg.a[i]); break;	//Division		 x\arg
				case opFlagLin		: a[i].     LinAt(s1, arg.a[i]); break;	//LinAt			 x*s1+arg
				case opFlag_AddProd : a[i]. addProdAt(s1, arg.a[i]); break;	//addProdAt		 x+s1*arg
				case opFlagSubtProd : a[i].subtProdAt(s1, arg.a[i]); break;	//subtProdAt	 x-s1*arg
				case opFlagBiLin	: a[i].   BiLinAt(s1, arg.a[i], s2); break;	//BiLinAt		 x*s1+arg*s2
				case opFlagMax		:((IOrder)a[i]).MaxAt( arg.a[i]); break;	//Maximum		x >= arg
				case opFlagMin		:((IOrder)a[i]).MinAt( arg.a[i]); break;	//Minimum		x <= arg
			}
		int tmp = --i;
	    if (arg.getDim() > smallGrad) {	//higher Coefficients of 'this' are zero
			if ((Operation != opFlagMMul) &&
				(Operation != opFlagMDiv)) //ignore Multiplication or Division by Zero!
				while (++i <= mDim)a[i].copyAt (arg.a[i]);	//Used Deep Copy here!
			i = tmp;
			switch (Operation) {	//More performant, if you don't single out the Loop!
			case opFlagAdd		:										break;	//Addition	+
			case opFlagSubt		: while (++i <= mDim)a[i].negAt ();	break;	//Subtraktion	-
			case opFlagMMul		: mDim = smallGrad;					break;	//Multiplication	*
			case opFlagMDiv		: mDim = smallGrad;					break;	//Division	/, ignore case 0/0
			case opFlagLin		:										break;	//LinAt
			case opFlag_AddProd : while (++i <= mDim)a[i].mulAt (s1);	break;	// addProdAt
			case opFlagSubtProd : ((IGroup) s1).negAt();
								  while (++i <= mDim)a[i].mulAt (s1);
								  ((IGroup) s1).negAt();					break;	//subtProdAt
			case opFlagBiLin	: while (++i <= mDim)a[i].mulAt (s2);	break;	//BiLinAt
			case opFlagMax		: while (++i <= mDim)
									  if (((IScalarMetric) a[i]).negative()) a[i].zeroAt();	break;	// addProdAt
			case opFlagMin		: while (++i <= mDim)
									  if (((IScalarMetric) a[i]).positive()) a[i].zeroAt();	break;	// addProdAt
			}
		} else if (arg.getDim() < largeGrad) {	//higher Coefficients of 'arg' are zero
			switch (Operation) {	//More performant, if you don't single out the Loop!
			case opFlagAdd		:										break;	//Addition
			case opFlagSubt		:										break;	//Subtraktion
			case opFlagMMul		: mDim = smallGrad;					break;	//Multiplication
			case opFlagMDiv		: while (++i <= mDim)
								  ((IMetricIRing) a[i]).InfinityAt();	break;	//Division, ignore case 0/0
			case opFlagLin		: while (++i <= mDim)a[i].mulAt (s1);	break;	//LinAt
			case opFlag_AddProd :										break;	// addProdAt
			case opFlagSubtProd :										break;	//subtProdAt
			case opFlagBiLin	: while (++i <= mDim)a[i].mulAt (s1);	break;	//BiLinAt
			case opFlagMax		: while (++i <= mDim)
									  if (((IScalarMetric) a[i]).negative()) a[i].zeroAt();	break;	// addProdAt
			case opFlagMin		: while (++i <= mDim)
									  if (((IScalarMetric) a[i]).positive()) a[i].zeroAt();	break;	// addProdAt
			}
		}
		else	//Shorten the Polynom, because the upper Coefficients
			if (! bolLazySimplify)	//may have cancelled each other.
				while (a[mDim].isZero()) mDim--;	//If isZero is aware of the Coefficient Size, it can round!
		return this; }

	/** Adds or subtracts a Polynom / Vector from another in Place
	  * optionally multiplies the first and/or second Vector with a Scalar.
	  * The Sequence of Processing is swapped, so the Carry can add
	  * through the (newer) higher Coefficients (no longer necessary, only for gAdic).	 */
	public Tensor opAt(Object s1, ITensor arg, IIntRing s2, int Operation) {
	    int largeGrad = mDim;			//= Math.max(mDim, arg.getDim());
	    int smallGrad = arg.getDim();	//= Math.min(mDim, arg.getDim());

	    if (smallGrad > mDim) {
			smallGrad = mDim;
		 largeGrad = arg.getDim();
		 setDim(largeGrad, true, false);	//resize, keep existing Values,
		}	//don't set the higher coefficients to zero! => very fast.

		int i = -1;
		while (++i <= smallGrad)
			switch (Operation) {	//More performant, if you don't single out the Loop!
				case opFlagAdd		: a[i].     addAt(    arg.getAt(i)); break;	//Addition		 x+arg
				case opFlagSubt		: a[i].     subAt(    arg.getAt(i)); break;	//Subtraktion	 x-arg
				case opFlagMMul		: a[i].     mulAt(    arg.getAt(i)); break;	//Multiplikation x*arg
				case opFlagMDiv		: a[i].     divAt(    arg.getAt(i)); break;	//Division		 x\arg
				case opFlagLin		: a[i].     LinAt(s1, arg.getAt(i)); break;	//LinAt			 x*s1+arg
				case opFlag_AddProd : a[i]. addProdAt(s1, arg.getAt(i)); break;	//addProdAt		 x+s1*arg
				case opFlagSubtProd : a[i].subtProdAt(s1, arg.getAt(i)); break;	//subtProdAt	 x-s1*arg
				case opFlagBiLin	: a[i].   BiLinAt(s1, arg.getAt(i), s2); break;	//BiLinAt		 x*s1+arg*s2
				case opFlagMax		:((IOrder)a[i]).MaxAt( arg.getAt(i)); break;	//Maximum		x >= arg
				case opFlagMin		:((IOrder)a[i]).MinAt( arg.getAt(i)); break;	//Minimum		x <= arg
			}
		int tmp = --i;
	    if (arg.getDim() > smallGrad) {	//higher Coefficients of 'this' are zero
			if ((Operation != opFlagMMul) &&
				(Operation != opFlagMDiv)) //ignore Multiplication or Division by Zero!
				while (++i <= mDim)a[i].copyAt (arg.getAt(i));	//Used Deep Copy here!
			i = tmp;
			switch (Operation) {	//More performant, if you don't single out the Loop!
			case opFlagAdd		:										break;	//Addition	+
			case opFlagSubt		: while (++i <= mDim)a[i].negAt ();	break;	//Subtraktion	-
			case opFlagMMul		: mDim = smallGrad;					break;	//Multiplication	*
			case opFlagMDiv		: mDim = smallGrad;					break;	//Division	/, ignore case 0/0
			case opFlagLin		:										break;	//LinAt
			case opFlag_AddProd : while (++i <= mDim)a[i].mulAt (s1);	break;	// addProdAt
			case opFlagSubtProd : ((IGroup) s1).negAt();
								  while (++i <= mDim)a[i].mulAt (s1);
								  ((IGroup) s1).negAt();					break;	//subtProdAt
			case opFlagBiLin	: while (++i <= mDim)a[i].mulAt (s2);	break;	//BiLinAt
			case opFlagMax		: while (++i <= mDim)
									  if (((IScalarMetric) a[i]).negative()) a[i].zeroAt();	break;	// addProdAt
			case opFlagMin		: while (++i <= mDim)
									  if (((IScalarMetric) a[i]).positive()) a[i].zeroAt();	break;	// addProdAt
			}
		} else if (arg.getDim() < largeGrad) {	//higher Coefficients of 'arg' are zero
			switch (Operation) {	//More performant, if you don't single out the Loop!
			case opFlagAdd		:										break;	//Addition
			case opFlagSubt		:										break;	//Subtraktion
			case opFlagMMul		: mDim = smallGrad;					break;	//Multiplication
			case opFlagMDiv		: while (++i <= mDim)
								  ((IMetricIRing) a[i]).InfinityAt();	break;	//Division, ignore case 0/0
			case opFlagLin		: while (++i <= mDim)a[i].mulAt (s1);	break;	//LinAt
			case opFlag_AddProd :										break;	// addProdAt
			case opFlagSubtProd :										break;	//subtProdAt
			case opFlagBiLin	: while (++i <= mDim)a[i].mulAt (s1);	break;	//BiLinAt
			case opFlagMax		: while (++i <= mDim)
									  if (((IScalarMetric) a[i]).negative()) a[i].zeroAt();	break;	// addProdAt
			case opFlagMin		: while (++i <= mDim)
									  if (((IScalarMetric) a[i]).positive()) a[i].zeroAt();	break;	// addProdAt
			}
		}
		else	//Shorten the Polynom, because the upper Coefficients
			if (! bolLazySimplify)	//may have cancelled each other.
				while (a[mDim].isZero()) mDim--;	//If isZero is aware of the Coefficient Size, it can round!
		return this; }

	//////////////////////////////
	//	Generic Scalar Products
	//////////////////////////////

	/** @return this Tensor multiplied in Place at the given Degree.
	  * This is a pre Step to calculating the Scalar Product.
	  *
	  * The outer structure remains the same and is used to hold the Product.
	  * The inner structure of arg is retained on creating the Product.
	  */
	public ITensor dyadAt(ITensor arg, int Degree) {
		if (--Degree < 0) {
			dyadAt(arg); return this; } //TODO: what in the generic Case where the Elements are Tensors again, multiplied by Tensors?
		int i = mDim+1;
		while (--i >= 0) {
			a[i] = ((Tensor) a[i]).dyadAt(arg, Degree);
		} return this; }

	/** @return the dyadic Product of this Tensor and arg.
	  * This is a pre Step to calculating the generic Scalar Product.
	  * The Degree of the Tensor is the Sum of the Degrees of the Factors.
	  *
	  * The inner structure of arg is retained on creating the Product.
	  *
	  * a[i,j,k]°b[l,m,n] = c[i,j,k,l,m,n] is the scalar Element calculated by
	  * a[i,j,k] := a[i,j,k]°b[]
	  */
	public ITensor dyadAt(ITensor arg_) {
		Tensor arg = (Tensor) arg_;
		IIntRing[] arr;
		IIntRing   a_i;
		Tensor     T_i;
		int j, i =     mDim+1;
		int jMax = arg.mDim+1;
		while (--i >= 0) {
			if ((a_i = a[i]) instanceof Tensor) {
				arr = new IIntRing[j = jMax];
				if (arg.a[0] instanceof Tensor) {
					T_i = (Tensor) a_i;
					while (--j >= 0) { //inner dyadic Product
						arr[j] = T_i.dyadAt((Tensor) arg.a[j]); } //arg.a are Tensors
				}else{ //arg.a[j] are Scalars
					while (--j >= 0) { //inner dyadic Product
						arr[j] = (IIntRing) a_i.mul(arg.a[j]); } //arg.a are Scalars
				}
				a[i] = new Tensor(arr);
			} else {
				a[i] = (IIntRing) arg.mul(a_i);  //Product with a Scalar
			}
		} return this; }

	/**Re-Composition of LU decomposition in Place.
	 * Undoes the Permutation of Rows also.
	 * This Operation can be done in Place,
	 * if you start from Bottom Left, because this Element == a[i,j]
	 * is only used within this same line.
	 */
	public ITensor LU_ComposeAt() {
		//TODO: Implement this
		return this; }

	/** "Multiplication": °
	  * This is in fact a non-commutative linear Mapping:
	  * M°(a+b) == M°a + M°b
	  * (x+y)°M == x°M + y°M
	  *
	  * The Matrix itself is the Derivative Jacobian Matrix of the Mapping:
	  * (A*x)' = A
	  *
	  * Here the Tensor Multiplication is defined recursively
	  * between two Tensors of these Dimensions: (n,m)*(m,k) => (n,k)
	  * The innermost (last ) Dimension of the left  Argument has to match
	  * the outermost (first) Dimension of the right Argument.
	  * The Transposition is done implicitly, only the Complement
	  * has to be done in place manually, if needed for complex coefficients.
	  *
	  * This Multiplication can also multiply the Transpose Tensors,
	  * by just swapping the Operands!
	  * (and transposing the Result, which is not necessary for Vectors) */
	public IIntRing catAt(ITensor arg) { //previously named mul()
		if (a[0] instanceof Tensor) { //There are deeper Levels... break them up recursively...
			//TODO: The Multiplication can also be done directly with the LU Form, leading to a non-LU Form.
			LU_ComposeAt();	//UnDo any Decomposition done before! only applies to Matrices!
			int i = mDim+1;
			while (--i >= 0)	//do the Multiplication recursively and elementwise !
				a[i] = ((Tensor) a[i]).catAt(arg); //Assignment only necessary for the lowest Tensor Level! May be even primitive Values here!
			return this;
		} else {	//Vector from the Left: Vector*Vector or Vector*Matrix results in Scalar or Vector
			return cat(arg); } //results not in a Mapping in Place!
	}

	/** Scalar Product Multiplication: °
	  * This is in fact a non-commutative linear Mapping:
	  * M°(a+b) == M°a + M°b
	  * (x+y)°M == x°M + y°M
	  *
	  * The Matrix itself is the Derivative Jacobian Matrix of the Mapping:
	  * (A*x)' = A
	  *
	  * Here the Tensor Multiplication is defined recursively
	  * between two Tensors of these Dimensions: (n,m)*(m,k) => (n,k)
	  * The innermost (last ) Dimension of the left  Argument has to match
	  * the outermost (first) Dimension of the right Argument.
	  * The Transposition is done implicitly, only the Complement
	  * has to be done in place manually, if needed for complex coefficients.
	  *
	  * Since higher Coefficients are assumed to be null resp. Zero,
	  * it is sufficient to multiply only to the lesser Degree of both Vectors.
	  *
	  * This Multiplication can also multiply the Transpose Tensors,
	  * by just swapping the Operands!
	  * (and transposing the Result, which is not necessary for Vectors) */
	public IIntRing cat(ITensor arg_) { //previously named mul()
		Tensor arg  =  (Tensor)arg_;
		if (a[0] instanceof Tensor) { //There are deeper Levels... break them up recursively...
			return ((Tensor) copy()).catAt(arg);
		} else { //Row Vector from the Left, directly calculated!
			int i =     mDim;
			if (i > arg.mDim) {
				i = arg.mDim; }
			IIntRing tmp = (IIntRing) a[i].mul(arg.a[i]);	//Don't initialize to 0, Single out the first Operation:
			while (--i >= 0)	//saves Initialization and one Addition!
				tmp.addProdAt(a[i],    arg.a[i]);	//* Skalar!
			return tmp; }
	}

	/** @return the generic Tensor Product
	  * calculated by Summing up the Products along the given Degrees
	  * and leaving the other Degrees in Order, i.e.
	  *
	  * T[i,j,k]*A[l,j,m] = R[i,l,m,k]
	  * To move the Elements at the Proper Position,
	  * Transpose the Tensor in it's Elements.
	  */
//	public IIntRing catAt(byte Degree1,
//		Tensor arg, byte Degree2) {

	/** @return the generic Tensor Product
	  * calculated by Summing up the Products along the given Degrees
	  * and leaving the other Degrees in Order, i.e.
	  *
	  * T[i,j,k]*U[l,j,m] = R[i,k,l,m]
	  * To move the Elements at the Proper Position,
	  * Transpose the Tensor in it's Elements.
	  */
//	public IIntRing cat(byte Degree1,
//		Tensor arg, byte Degree2) {
//		return ((Tensor) copy()).catAt(Degree1, arg, Degree2); }

	/** @return the Tensor Product with a Matrix
	  *
	  * T[i,j,k]*A[m,j] = R[i,m,k]
	  * Transpose the Matrix if necessary.
	  *
	  * R[i,m,k] = T[i,k,j]*A[m,j] =T[i,k][j]*A[m][j]
	  *
	  * T[i,k,j]*A[j,m] =T[i,k][j]*A[j][m]
	  */
	public ITensor catAt(byte Degree1, Matrix arg) {
		if (--Degree1 < 0) { //do the actual Multiplication
			if (a[0] instanceof Tensor) { //multiplying from the left!
				return (Tensor) arg.cat(this); //not returned in Place!
			} else { //multiplying from the right!
				return (Tensor) catAt(arg.trp());
			}
		} else { //delegate Multiplication to the Elements
			int i = mDim+1;
			while (--i >= 0) {
				((Tensor) a[i]).catAt(Degree1, arg); }
			return this; }
	}

	/** @return the Element of the generic Tensor Product
	  * calculated from the Coefficients at the given Multi Index Positions
	  * of the Factors.
	  * @return Sum(MIndex1[Degree1] = MIndex2[Degree2], Tensor1[MIndex1] * Tensor2[MIndex2])
	  *
	  * This is the Basis for a generic Tensor Concatenation
	  * in arbitrary Indices for Tensors of arbitrary Degree.
	  */
	public static IIntRing cat(
		Permutation MIndex1, byte Degree1, Tensor Tensor1,
		Permutation MIndex2, byte Degree2, Tensor Tensor2) {
		int MaxDim  = ((Tensor) Tensor1.getAt(MIndex1, Degree1)).getDim();
		if (MaxDim != ((Tensor) Tensor2.getAt(MIndex2, Degree2)).getDim())
			throw new java.security.InvalidParameterException("Dimensions don't match! " + MaxDim);
		int Grad1 = MIndex1.getDim(); MIndex1.a[Degree1] = MaxDim; //prepare
		int Grad2 = MIndex2.getDim(); MIndex2.a[Degree2] = MaxDim; //the loop
		IIntRing ret = (IIntRing) //calculate first Element
			Tensor1.getAt(MIndex1, Grad1).mul(
			Tensor2.getAt(MIndex2, Grad2));
		while (--MaxDim >= 0) { //loop over the rest...
			ret.addAt(
				Tensor1.getAt(MIndex1, Grad1).mul(
				Tensor2.getAt(MIndex2, Grad2))); }
		return ret; }

	/** Creates the Transpose of this Tensor: M^T
	  * in the given (Default: first two) Dimensions: a[i][j] = b[j][i]
	  * The Elements are copied, not reused. */
	public ITensor trp(int Degree) { //cannot operate in the Decomposed Form!
		if (Degree > 0) { //do the Transpose
			return ((Tensor) copy()).trpAt(Degree);
		} else {
			if (!(a[0] instanceof Tensor))
				return (Tensor) copy();	//Don't transpose Vectors.
			if (this instanceof Matrix)
				((Matrix) this).LU_ComposeAt();	//(L*U)T = UT * LT , The Result is still an U' and L' Matrix, unfortunately the 1-Diagonal is now in the right Matrix!
			//Get the maximum Dimension in the Second Degree, saves a step
			int maxGrad = -1;//((Tensor)a[0]).getDim();
			for (int  i = -1; ++i <= mDim;) maxGrad = Math.max(maxGrad, ((Tensor)a[i]).getDim());
			IIntRing [] row = new IIntRing [mDim+1];	//Number of Coefficients
			Tensor tmp = new Tensor(this.Carry, -1);
			tmp.setDim(maxGrad, false, false); //needn't create a square Tensor!
			for (int i = -1; ++i <= maxGrad;) {
				for (int j = -1; ++j <= mDim;) {
					if (((Tensor)a[j]).getDim() >= i)
						 row[j] = ((Tensor)a[j]).a[i];
					else row[j] = (IIntRing)((Tensor)a[0]).a[0].zero();	//This would not create a deeper Structure!
				}
				tmp.a[i] = new Tensor(row);// = new Tensor(row, true);	//Carry not set on these Items!!!
			}	//could also use addItem here to build up the Tensor from 0.
			return tmp; }
	}

	/** Creates the Transpose of this Tensor in Place: MT
	  * in the given (Default: first two) Dimensions: a[i][j] = b[j][i]
	  * i.e. in the Dimensions Degree and Degree+1.	 */
	public ITensor trpAt(int Degree)	{
		if (--Degree < 0) { //do the Transpose
			shallowCopyAt(trp());
		} else {
			int i = mDim+1;
			while (--i >= 0) {
				((Tensor) a[i]).trpAt(Degree); }
		} return this; }

	//////////////////////////////
	//	Optimizations			//
	//////////////////////////////

	/** Compares a Manifold with a Scalar in different Ways	 */
	public boolean funcAt(Object arg, Object arg2, int selFunction) {
		int i = mDim+1;
		while (--i >= 0) {
			switch (selFunction) {	//More performant, if you don't single out the Loop!
				case funcFlagBetween: if (! ((IOrderAble)a[i]).isBetween(arg, arg2)) return false; 	//Between
				case funcFlagGrtr	: if (! ((IOrderAble)a[i]).isMoreThan	 (arg)) return false; 	//Greater
				case funcFlagGrtrEq	: if (! ((IOrderAble)a[i]).notLessThan (arg)) return false; 	//Greater or Equal
				case funcFlagLess	: if (! ((IOrderAble)a[i]).isLessThan	 (arg)) return false; 	//Less
				case funcFlagLessEq	: if (! ((IOrderAble)a[i]).notMoreThan (arg)) return false; 	//Less or Equal
			}
		} return true; }

	/** Compares a Manifold with another in different Ways	 */
	public boolean funcAt(Tensor arg, Tensor arg2, int selFunction) {
	    int largeGrad = mDim;						//= Math.max(mDim, arg.getDim());
	    int smallGrad = arg.mDim;	//getDim();	//= Math.min(mDim, arg.getDim());
	    if (arg.getDim() > mDim) {
			smallGrad = mDim;
	        largeGrad = arg.getDim();
		}

		if ((selFunction == funcFlagBetween) && (arg2.mDim != arg.mDim)) throw new AbstractMethodError();

		int i = -1;
		while (++i <= smallGrad)
			switch (selFunction) {	//More performant, if you don't single out the Loop!
				case funcFlagBetween: if (! ((IOrderAble)a[i]).isBetween(arg.a[i], arg2.a[i])) return false; break;	//Between
				case funcFlagGrtr	: if (! ((IOrderAble)a[i]).isMoreThan	 (arg.a[i])) return false; break;	//Greater
				case funcFlagGrtrEq	: if (! ((IOrderAble)a[i]).notLessThan (arg.a[i])) return false; break;	//Greater or Equal
				case funcFlagLess	: if (! ((IOrderAble)a[i]).isLessThan	 (arg.a[i])) return false; break;	//Less
				case funcFlagLessEq	: if (! ((IOrderAble)a[i]).notMoreThan (arg.a[i])) return false; break;	//Less or Equal
			}

	    if (arg.getDim() > smallGrad) {	//higher Coefficients of 'this' are zero
			while (++i <= mDim)
			switch (selFunction)
			{	//More performant, if you don't single out the Loop!
				case funcFlagBetween: if (!(((IScalarMetric)arg .a[i]).positive() ^
											((IScalarMetric)arg2.a[i]).positive()))return false; break;	//Between
				case funcFlagGrtr	: if (! ((IScalarMetric)arg .a[i]).positive()) return false; break;	//Greater
				case funcFlagGrtrEq	: if (  ((IScalarMetric)arg .a[i]).negative()) return false; break;	//Greater or Equal
				case funcFlagLess	: if (! ((IScalarMetric)arg .a[i]).negative()) return false; break;	//Less
				case funcFlagLessEq	: if (  ((IScalarMetric)arg .a[i]).positive()) return false; break;	//Less or Equal
			}
		} else if (arg.getDim() < largeGrad) {	//higher Coefficients of 'arg' are zero
			while (++i <= mDim)
			switch (selFunction) {	//More performant, if you don't single out the Loop!
			case funcFlagBetween: if (!				   a[i] .isZero  ()) return false; break;	//Between
			case funcFlagGrtr	: if (! ((IScalarMetric)a[i]).positive()) return false; break;	//Greater
			case funcFlagGrtrEq	: if (  ((IScalarMetric)a[i]).negative()) return false; break;	//Greater or Equal
			case funcFlagLess	: if (! ((IScalarMetric)a[i]).negative()) return false; break;	//Less
			case funcFlagLessEq	: if (  ((IScalarMetric)a[i]).positive()) return false; break;	//Less or Equal
			}
		}

		return true; }

	//	Comparisons

	/**between: returns True, when 'Self' is between arg1 and arg2	 */
	public boolean isBetween (Object arg, Object arg2) {
		if (arg instanceof Tensor)
			return funcAt((Tensor)arg, (Tensor)arg2, funcFlagBetween);
			return funcAt(        arg,         arg2, funcFlagBetween); }

	/**greater: '>' Returns True, when 'Self' > arg	 */
	public boolean isMoreThan (Object arg) {
		if (arg instanceof Tensor)
			return funcAt((Tensor)arg, null, funcFlagGrtr);
			return funcAt(        arg, null, funcFlagGrtr); }

	/**greater or equal: '>=' Returns True, when 'Self' >= arg	 */
	public boolean notLessThan (Object arg) {
		if (arg instanceof Tensor)
			return funcAt((Tensor)arg, null, funcFlagGrtrEq);
			return funcAt(        arg, null, funcFlagGrtrEq); }

	/**less or equal: '<' Returns True, when 'Self' < arg	 */
	public boolean isLessThan (Object arg) {
		if (arg instanceof Tensor)
			return funcAt((Tensor)arg, null, funcFlagLess);
			return funcAt(        arg, null, funcFlagLess); }

	/**less or equal: '<=' Returns True, when 'Self' <= arg	 */
	public boolean notMoreThan (Object arg) {
		if (arg instanceof Tensor)
			return funcAt((Tensor)arg, null, funcFlagLessEq);
			return funcAt(        arg, null, funcFlagLessEq); }

	//////////////////////////////
	//	Basic Object Operations	//
	//////////////////////////////

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * NewInstance also clones the Types, but does not initialize them!
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() { return new Tensor(); }

	/**Returns a hash code value for the object. This method is
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
	 * @return  a hash code value for this object.
	 * @see     java.lang.Object#equals(java.lang.Object)
	 * @see     java.util.Hashtable
	 * @since   JDK1.0	 */
	public int hashCode() {
		int Sum = 0;
		for (int i = -1; ++i <= mDim;)
			Sum += (a[i].hashCode() >> 1);
		return Sum; }

}
