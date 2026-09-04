package streamIO.copy.group.ring.metric.body.vector;

import java.io.IOException;

import math.vector.VectorDouble;
import streamIO.IDeserializer;
import streamIO.Log;
import streamIO.copy.ICopyAble;
import streamIO.copy.group.IDblGroup;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ILngGroup;
import streamIO.copy.group.ISemiGroup;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.Interpolator;
import streamIO.copy.group.ring.metric.IMetricIRing;
import streamIO.copy.group.ring.metric.IScalarMetric;
import streamIO.copy.group.ring.metric.IWellOrder;
import streamIO.copy.group.ring.metric.body.BodyDouble;
import streamIO.copy.groupM.IDblGroupM;
import streamIO.copy.groupM.IGroupM;
import streamIO.copy.groupM.ILngGroupM;
import streamIO.copy.groupM.ISemiGroupM;
import streamIO.copy.order.IDblOrder;
import streamIO.copy.order.ILngOrder;
import streamIO.copy.order.IOrder;
import streamIO.object.enumer.IndexEnumerator;
import function.ICountAble;
import function.IFloatFunction;
import function.byref.ByRefDouble;
import function.byref.ByRefInt;
import function.byref.ByRefLong;
import function.byref.ByRefObject;
import function.derive.IFloatDeriveAble;
import function.index.IDirectAccess;

/**
  * Title: VectorDbl<p>
  * Description:
  * VectorDbl with double Numbers, which is much easier to debug and faster
  * than using Tensor, which is a Vector of IIntRing Elements. 
  * Is both a Container for Numbers and a MetricIntegrityRing
  * on the Set of it's Elements. 
  * 
  * TODO: resolve the Conflict between treating these Objects 
  * as Samples (i.e. ordered Sets) over the same Dimension (addAt, diff etc.) or  
  * as Polynomes (addAt adds only to the 1st Element, diff does Polynom division)
  * 
  * The Inheritance Hierarchies of both Strains merge at ARing.
  * 
  * AContainer -> AStreamSet -> ABoolRing -> ARing
  * VectorDbl  -> ATensor -> AMetricIRing -> AIntRing -> ARing
  *
  * AContainer treats Items as integer individual Objects.
  * Addition is done by adding Items of one Container to the other
  * Multiplication is done by creating Pairs of both Container's Elements.
  * AContainer handles only (finite) integer Numbers of individual Objects!
  * Thus it supports many Boolean and Set Operations
  *
  * Manifold treats Items as Numbers of themselves.
  * It just groups them and allows Bulk Operations.
  *
  * It is a great Effort to merge both Interfaces:
  * @see <{IndexEnumerator}>
  * @see IMetricIRing
  * and the Multitude of Methods makes it hard to handle the Object
  * Thus it only allows to create Iterators and Enumerators
  * to be able to stream their Contents.
  *
  * Could also define Specific Methods for 2D and 3D Figures and Operations.
  *
  * Most Operations could have been delegated to Graph.VectorDouble!!!
  *
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-11-26, 01;13;44<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class VectorDbl
extends AManifold // ATensor // AMetricIRing
implements IDblGroup, IDblGroupM, IDblOrder, IDirectAccess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(VectorDbl.class, 0);

	/////////////////////////////////////////////////////////////////////////////////////

	/**
	  * Local Array storing the Values
	  * made publicly accessible to speed up Access!
	  */
	final public VectorDouble a;

	//////////////////////
	//	Accessor Methods:
	//////////////////////


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
		//TODO: implement this!!!
		return null; }//this; }

	/** @return this Tensor multiplied in Place at the given Degree.
	  * This is a pre Step to calculating the Scalar Product.
	  *
	  * The outer structure remains the same and is used to hold the Product.
	  * The inner structure of arg is retained on creating the Product.
	  */
	public ITensor dyadAt(ITensor arg, int Degree) {
/*		if (--Degree < 0) {
			dyadAt(arg); return this; } //TODO: what in the generic Case where the Elements are Tensors again, multiplied by Tensors?
		int i = mDim+1;
		while (--i >= 0) {
			a[i] = ((Tensor) a[i]).dyadAt(arg, Degree);
		} return this; }
*/		//TODO: implement this!!!
		return null; }//this; }

	/**Re-Composition of LU decomposition in Place.
	 * Undoes the Permutation of Rows also.
	 * This Operation can be done in Place,
	 * if you start from Bottom Left, because this Element == a[i,j]
	 * is only used within this same line.		*/
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
/*		if (a[0] instanceof Tensor) { //There are deeper Levels... break them up recursively...
			//TODO: The Multiplication can also be done directly with the LU Form, leading to a non-LU Form.
			LU_ComposeAt();	//UnDo any Decomposition done before! only applies to Matrices!
			int i = mDim+1;
			while (--i >= 0)	//do the Multiplication recursively and elementwise !
				a[i] = ((Tensor) a[i]).catAt(arg); //Assignment only necessary for the lowest Tensor Level! May be even primitive Values here!
			return this;
		} else {	//Vector from the Left: Vector*Vector or Vector*Matrix results in Scalar or Vector
			return cat(arg); } //results not in a Mapping in Place!
*/		return this; }

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
/*		Tensor arg  =  (Tensor)arg_;
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
*/		return this; }

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
/*		if (--Degree1 < 0) { //do the actual Multiplication
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
*/		return this; }


	/** Creates the Transpose of this Tensor: M^T
	  * in the given (Default: first two) Dimensions: a[i][j] = b[j][i]
	  * The Elements are copied, not reused. */
	public ITensor trp(int Degree) { //cannot operate in the Decomposed Form!
/*		if (Degree > 0) { //do the Transpose
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
*/		return null; }

	/** Creates the Transpose of this Tensor in Place: MT
	  * in the given (Default: first two) Dimensions: a[i][j] = b[j][i]
	  * i.e. in the Dimensions Degree and Degree+1.	 */
	public ITensor trpAt(int Degree)	{
/*		if (--Degree < 0) { //do the Transpose
			shallowCopyAt(trp());
		} else {
			int i = mDim+1;
			while (--i >= 0) {
				((Tensor) a[i]).trpAt(Degree); }
		}
*/		return this; }

	/** Object being used to return the Value of the Interface StreamIn
	  * Chose BodyDouble instead of ByRefDouble to supply as many Operations as possible. */
	protected  BodyDouble value = new  BodyDouble();
	//protected ByRefDouble Value = new ByRefDouble();
	
	//////////////////////
	//	Constructors	//
	//////////////////////

	/** Empty Constructor	 */
	VectorDbl (){ a = new VectorDouble(); }

	/** Copy-Constructor
	 * don't share the inner Object!
	 */
	VectorDbl (final VectorDbl arg) { a = arg.a.copyOrig(); }
	
	/** Copy-Constructor	 */
	VectorDbl (final VectorDouble arg) { a = arg; }
		
	/** Copy-Constructor	 */
	VectorDbl (final VectorDouble arg, final boolean copy) { 
		if (copy) {
			a = arg;
		} else {
			a = arg.copyOrig();
		}
	}
		
	//VectorDbl (int	arg[], int length){ VectorDbl(Arr(arg, length)); }
	//VectorDbl (long	arg[], int length){ VectorDbl(Arr(arg, length)); }
	//VectorDbl (float	arg[], int length){ VectorDbl(Arr(arg, length)); }

//	public

	/**Constructor for an Array of Type IIntRing.
	 * The Degree is automatically adjusted to the Array Length
	 * and the Vector is filled with the Elements from the Array.	 */
	VectorDbl (final double[] arg, final boolean copy) {
		a = new VectorDouble(arg, copy); 
	}

	//////////////////////////////
	//	Sampling of a Function	//
	//////////////////////////////

	/**Generates a Manifold by sampling f across x	 */
	public VectorDbl(final IFloatFunction f,  final VectorDbl x) {	//preserve Internals of x0
		a = new VectorDouble(f, x.a); }

	/** Samples the Function f on the Interval [x0, x0+Grad*dx]	 */
	public VectorDbl (final IFloatFunction f, double x0, final double dx, final int length) {	//
		a = new VectorDouble(f, x0, dx, length); }

	/** Creates an equidistant Raster on the Interval [x0, x0+Grad*dx]	 */
	public VectorDbl (double x0, final double dx, final int length) {
		a = new VectorDouble(x0, dx, length); }

	/** Sets the Grad of the Polynom == Dimension-1 of the Vector.
	  * When Preserve = true, the Contents of the Polynom is preserved.
	  * Initializes the Elements above mDim to 0, when zeroUpper = true.
	  * The Grad is the Period for the large Rotation Operations.
	  *
	  * Possibilities: new Array is...
	  * 1) uninitialized and potentially (half) empty (preserved = false)
	  * 2) initialized with only new Elements (not implemented)
	  *		use a brand new VectorDbl.
	  * 3) initialized with preserved old and new Elements
	  *		(Preserve = true)
	  *
	  */
	public int setDim(int length, boolean preserve, boolean zeroUpper){
		a.setSize(length); 
		return mDim = length; }

	///////////////////////////////////////////////////////////////////////////
	//	IndexEnumerator: abstract Methods
	///////////////////////////////////////////////////////////////////////////

	/** @return the Item at the given absolute Position
	  * While this is possible in principle for all Enumerators,
	  * it is too ineffective to loop through the whole Enumerator
	  */
	public Object getAt(final int index) {
	//	if (index >= a.length) return EOI; 
		if (index >= a.getInt())  // mDim)  
			return EOI;  
		if (index < 0) 
			return SOI;  
		value.value = a.getDoubleAt(index);
		return value; }

	/**
	 * adds (the Value of) the given Object into this ordered List.
	 * The other Elements are shifted appropriately.   
	 * @return this Container
	  */
	public IndexEnumerator addAt(final int pos, final Object arg) {
		++minorVersion;
		++majorVersion;
		a.insertItemAt(pos, ByRefDouble.GET_DOUBLE(arg)); ++mDim;
		return this; }

	/** Removes the Object at the given Index in the Container with this Enumerator knowing it.
	  * One Problem is other Enumerators that concurrently work through this Container.
	  * Another Problem is that removing the Item may not be possible at all.
	  * That is why this Method should throw an exception if removing is not allowed.   */
	public Object removeAt(final int pos) {
		++minorVersion;
		++majorVersion;
		value.value = a.removeItemAt(pos); 
		return value; }

	/** Replaces the Object at the given Index in the Container with this Enumerator knowing it.
	  * One Problem is other Enumerators that concurrently work through this Container.
	  * Another Problem is that removing the Item may not be possible at all.
	  * That is why this Method should throw an exception if removing is not allowed.   */
	public Object setAt(final int index, final Object item) {
		++minorVersion;
		Object ret = null; 
		if (index < a.getInt()) 
			ret = getAt(index); 
		a.setAt(index, ByRefDouble.GET_DOUBLE(item)); 
		return ret; 
	}

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
	public boolean equals(final Object arg_) {	//Compare the two Polynoms elementwise
		if (! (arg_ instanceof VectorDbl)) {
			final double dbl = ByRefDouble.GET_DOUBLE(arg_); 
			if (Double.isNaN(dbl)) {
				return arg_.equals(this); }
			return equals(dbl);
		}
		final VectorDbl arg = (VectorDbl) arg_;
		return a.equals(arg.a); } 

	/**Large Rotation right by one Item in Place	 */
	/*VectorDbl rorLargeAt() {
		final double tmp = a[0];
		System.arraycopy(a, 1, a, 0, mDim);	//very fast
		a[mDim] = tmp;	//preserve the first Item
		return this; }
	*/
	/**Large Rotation left by one Item in Place	 */
	/*VectorDbl rolLargeAt() {
		final double tmp = a[mDim];
		System.arraycopy(a, 0, a, 1, mDim);	//very fast
		a[0] = tmp;	//preserve the first Item
		return this; }
	*/
	/** Copies only the Values of the Items including Start- and excluding EndIndex. 	 */
	void CopyAt(final double[] a2, int Grad, int StartIndex, int EndIndex, int Depth) {
		a.copyAt(a2, StartIndex, EndIndex); }

	/**Copies only the Values of the Items including Start- and EndIndex. 	 */
	void CopyAt(final Object[] a2, int Grad, int StartIndex, int EndIndex, int Depth) {
		for (int i = EndIndex; --i >= StartIndex;) {
			a.setAt(i, ByRefDouble.GET_DOUBLE(a2[i])); } 
		return; }

	/**Negation in Place: -		*/
	public IGroup negAt() { a.negAt(); return this; }

	/**Inversion in Place: 1/x		*/
	public IGroupM invAt() { a.invAt(); return this; }

	/** @see IIntRing#IntAt()
	 * TODO: find out how IntAt should be implemented!
	 */
	public IIntRing IntAt() { a.FloorAt(); return this; }

	/** @see IIntRing#FloorAt()
	 * TODO: find out how FloorAt should be implemented!
	 */
	public IMetricIRing FloorAt() { a.FloorAt(); return this; }

	/////////////////////////////////////////////////////////////////////////////////////

	/**Addition in Place: +=	*/
	public ISemiGroup addAt(final Object arg)	{
		if (arg == null) { //null == 0 
			return this; } 
		if (arg instanceof VectorDbl) {
			a.addAt(((VectorDbl) arg).a);
			return this; }
		final double dbl = ByRefDouble.GET_DOUBLE(arg); 
		if (Double.isNaN(dbl)) { //a+b = b+a
			if (arg instanceof ISemiGroup) { //Addition is always commutative!
				return ((ISemiGroup) arg).add(this); }
		}
		return addAt(dbl); } //simple scalar Value

	/**Subtraction in Place: -=	*/
	public IGroup subAt(Object arg) {
		if (arg == null) { //null == 0 
			return this; } 
		if (arg instanceof VectorDbl) {
			a.subAt(((VectorDbl) arg).a);
			return this; }
		final double dbl = ByRefDouble.GET_DOUBLE(arg); 
		if (Double.isNaN(dbl)) {
			if (arg instanceof IGroup) { //a-b == -(b-a)
				return ((IGroup) arg).sub(this).negAt(); //Target Type knows best
			} //adding with a Scalar is always commutative
		}
		return subAt(dbl); } //simple scalar Value

	/**Multiplication: *
	 * This Polynom Multiplication is in Fact a Convolution!
	 * With a 1-Dim Vector it comes back to a normal Vector Multiplication.
	 * You cannot multiply two Manifolds in Place,
	 * because the Coefficients in different levels are coupled.	 */
	public ISemiGroupM mulAt (Object arg) {
		if (arg == null) { //null == 0 
			zeroAt(); return this; } 
		if (arg instanceof VectorDbl) {
			a.mulAt(((VectorDbl) arg).a);
			return this; }
		final double dbl = ByRefDouble.GET_DOUBLE(arg); 
		if (Double.isNaN(dbl)) { //a*b = b*a
			if (arg instanceof ISemiGroup) { //here Multiplication is commutative!
				return ((ISemiGroupM) arg).mul(this); }
		}
		return mulAt(dbl); } //simple scalar Value

	/**Multiplication: *
	 * This Polynom Multiplication is in Fact a Convolution!
	 * With a 1-Dim Vector it comes back to a normal Vector Multiplication.
	 * You cannot multiply two Manifolds in Place,
	 * because the Coefficients in different levels are coupled.	 */
	public IGroupM divAt (Object arg) {
		if (arg == null) { //null == 0 
			InfinityAt(); return this; } 
		if (arg instanceof VectorDbl) {
			a.divAt(((VectorDbl) arg).a);
			return this; }
		final double dbl = ByRefDouble.GET_DOUBLE(arg); 
		if (Double.isNaN(dbl)) {
			if (arg instanceof IGroupM) { //a-b == -(b-a)
				return ((IGroupM) arg).div(this).invAt(); //Target Type knows best
			} //adding with a Scalar is always commutative
		}
		return divAt(dbl); } //simple scalar Value

	//////////////////////////
	//	Vector Operations:	//
	//////////////////////////

	/**Removes leading 0s by decreasing the Grad	 */
	public ITensor canonicalizeAt() {
		a.canonicalizeAt(); 
		return this; }

	//////////////////////////////
	//	Interface shiftAble:	//
	//////////////////////////////

	//////////////////
	//	Constants	//
	//////////////////

	//These Routines rely on the Modulus being greater than three!

	/**Returns 2 in Place: 
	 * TODO: check wheter to treat a Polynoms or as Vectors 	*/
	public IIntRing   twoAt() { a.copyAt(ICountAble.TWO); return this; }

	/**Returns 3 in Place:
	 * TODO: check wheter to treat a Polynoms or as Vectors 	*/
	public IIntRing threeAt() { a.copyAt(ICountAble.THREE); return this; } 

	//////////////////////////
	//	Replication Group:	//
	//////////////////////////

	/**Setting to 0 in Place:
	 * It is faster, but less correct to set the Grad to -1,
	 * because on Multiplication it is no longer Grad(P1*P2) <= Grad(P1) + Grad(P2)		*/
	public IGroup zeroAt() { a.setSize(0); mDim = -1; return this; }

	/**Testing for 0:			*/
	public boolean isZero()	{ return a.isZero(); }
	
	//////////////////////////
	//	Replication GroupM:	//
	//////////////////////////
	
	/**Testing for 1:	 */
	public boolean isOne() { return a.isValue(ICountAble.ONE); }

	/**Setting to 1 in Place:	 */
	public IGroupM oneAt() { a.copyAt(ICountAble.ONE); return this; }

	//////////////////////////////
	//	Replication SemiGroupM:	//
	//////////////////////////////

	/**Optimizations are very likely here,
	 * but the Algorithm would be too complicated to be implemented here!	 */

	/** @return the Square in Place: x*=x	*/
	public ISemiGroupM sqrAt() { a.sqrAt(); return this; }

	/** @return the Cubic in Place: x*=x^2	*/
	public ISemiGroupM cbcAt() { a.cbcAt(); return this; }


	//////////////////////////////
	//	Basic Object Operations	//
	//////////////////////////////

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * NewInstance also clones the Types, but does not initialize them!
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() { return new VectorDbl(); }

	/** @see streamIO.copy.IICopyAble#randomizeAt()	 */
	public ICopyAble randomizeAt() { 
		this.a.randomizeAt(); return this;  
	} 
		
	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Constituents.
	 * It keeps the current reference, so any Argument has to be converted to Complex!
	 *
	 * This is the first Mathematical Container and it defines
	 * Depth = 0	=> shallowCopyAt()
	 *
	 * It creates Copies of a[0] and keeps it's Elements,
	 * thus saving the Destruction and new Allocation.
	 * This is o.k. for simple Elements in gAdic Numbers and Polynoms,
	 * but not for recursive Structures like in Tensor, where this is no longer possible.	 */
	public VectorDbl deepCopyAt(Object arg, int Depth){	//don't rely on the Argument being a Polynom!
		Class C = arg.getClass();
		if (! C.isArray()) {	//simple Object, no Array, use copyAt on the 0th Element.
			if (arg instanceof VectorDbl) {	//deep Copy of the Array a
				VectorDbl arg_	= (VectorDbl)arg;
				setDim(arg_.mDim, false, false);
				System.arraycopy(arg_.a, 0, a, 0, arg_.mDim +1);
			} else {
				mDim = 0; a.setAt(0, ByRefDouble.GET_DOUBLE(arg)); }
		} else {	//Array Type, determine the Type of Array Elements.
			Class Typ = C.getComponentType();
			if (Typ.isPrimitive())	//Always copy from primitive Types (float, double, byte etc.)
			{	//Array of primitive Types e.g. double, int etc.
				int Length = java.lang.reflect.Array.getLength(arg);
				setDim(Length -1, false, false);
				for (int i = -1; ++i <= mDim;)
					a.setAt(i, java.lang.reflect.Array.getDouble(arg, i));
			} else {
				Object[] tmp = (Object[]) arg;
				if (Typ.isArray() || tmp[0] instanceof VectorDbl) // gAdic)	//build recursive Structure
				{	//for Arrays of Arrays or Arrays of gAdics
					throw new AbstractMethodError("Cannot create a recursive Structure with " + getClass().getName());
	//				setDim(tmp.length -1, false, false);	//Don't create Elements or initialize them
	//				for (int i = -1; ++i <= mDim;)
	//					a[i] = (VectorDbl) newInstance().copyAt(tmp[i], Depth);
				}	//Object Array Items (no gAdics!)
				else CopyAt(tmp, tmp.length -1, 0, tmp.length -1, Depth);	//Array of Objects
			}
		}
		return this; }

	/**Does a shallow Copy
	 * For this Class a deep Copy is not possible anyway,
	 * since it contains Double Numbers directly...	 */
	public ICopyAble shallowCopyAt(Object arg){	//don't rely on the Argument being a Polynom!
		if (arg instanceof VectorDbl) {
			VectorDbl arg_ = (VectorDbl) arg;	//don't rely on the Argument being a Polynom!
			//a		= arg_.a; //TODO this final Element cannot be changed or shared!
			a.copyAt(arg_.a); 
			mDim	= arg_.mDim;
			return this; }
		if (arg instanceof double[]) {	//simple Object, no Array, use copyAt on the 0th Element.
			double[] tmp = (double[]) arg; 	//Array is converted into a Polynom!
			System.arraycopy(tmp, 0, a, 0, tmp.length); }
	//	else throw new AbstractMethodError();
		{
			mDim = 0; a.setAt(0, ByRefDouble.GET_DOUBLE(arg)); }
		return this; }

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Constituents.
	 * It keeps the current reference, so any Argument has to be converted to Complex!
	 *
	 * Since this is a recursive structure, it is no longer possible
	 * to keep the existing Elements like in VectorDbl and Polynom!
	 * So the old Elements are discarded and new ones are created,
	 * of course with a larger Overhead!
	 *
	 * Also look at the toString(Object arg) Method in absCopyAble.	 */
	public ICopyAble copyAt(Object arg, int Depth) {
		if (arg == null) return this;
		if (--Depth < 0)
			return shallowCopyAt(arg);
			return    deepCopyAt(arg, Depth); }

	/**@return  a hash code value for this object.
	 * @see     java.lang.Object#equals(java.lang.Object)
	 * @see     java.lang.Object#hashCode()
	 */
	public int hashCode(){ return a.hashCode(); }

	/**Parses the Stream to a VectorDbl Number. 	 */
	public ICopyAble fromStreamAt(IDeserializer arg) throws IOException {
		a.fromStreamAt(arg); 
		return this; }

	//////////////////////////
	//	Metric Interfaces:	//
	//////////////////////////


	//////////////////////////////////
	//	Additional Polynom Stuff	//
	//////////////////////////////////


	/**absolute Value in Place: |x|
	 * Returns the fastest Norm, which is the AbsV_Norm
	 * Leaves Vectors with only 1 Dim behind, which contain the Abs-Norm of this Row.	 */
	public IScalarMetric AbsVAt() { a.AbsVAt(); return this; }

	/**p-Norm: Defined as Sum(|x|^p)^1/p
	 * Generic Norm: the other Norms are Special Cases:
	 * In 1-dimensional Spaces all Norms fall together.	 */
	
	/**	public MetricIRing p_Norm (double p) {
	 * 		Double p_ = new Double(p);
	 * 		MetricIRing Sum = (MetricIRing)((MetricBody)((MetricIRing)a[0]).AbsV()).PowAt(new Double(p));
	 * 		for (long j = 0; ++j <= mDim;)
	 * 			Sum.addAt(((MetricBody)((MetricIRing)a[j]).AbsV()).PowAt(p_));
	 * 		return (MetricIRing)((MetricBody)Sum).PowAt(new Double(TDouble.ONE/p)); }
	 */
	
	/**Maximums-Norm
	 * Special Case of the p-Norm for p -> Infinity	 
	 */
	public IMetricIRing Max_Norm () { return new BodyDouble(a.MaxNorm()); }

	/**(Euklidische Norm)^2
	 * Special Case of the p-Norm for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public IMetricIRing SqrNorm() { return new BodyDouble(a.SqrNorm()); }

	//Accumulative and Statistical Operations:

	/**Sum: Returns the Sum of all Elements in the Tensor	 */
	public IIntRing Sum() { return new BodyDouble(a.Sum()); }

	/**Prod: Returns the Product of all Elements in the Tensor	 */
	public IMetricIRing Prod() { return new BodyDouble(a.Prod()); }

	//TODO: Add the statistical Methods like Median, etc. to the Class
	//either as Class Methods or as Object Methods.

	//	Multiplication with a Permutation

	/**Multiply the Vector by a Permutation in Place.
	 * This corresponds to swapping the Coefficients.	 */
	//public VectorDbl mulAt(Permutation P){return (VectorDbl) shallowCopyAt(mul(P));}

	/**Multiply the Vector by a Permutation in Place.
	 * This corresponds to swapping the Coefficients.	 */
	 /*
	public VectorDbl mul(Permutation P) {
		IIntRing[] arr = new IIntRing[mDim +1];
		for (long i = -1; ++i <= mDim;) { 
			arr[i] = (IIntRing) a[P.a[i]].copy(); } 
		return new VectorDbl(arr); }

	//Container Methods:

	/**Adds an Item to the Array (at the Top)	 */
	public VectorDbl addItem(final double arg) { a.addItem(arg); return this; }

	//////////////////////////////
	//	Interface intComplex	//
	//////////////////////////////

	/**Returns the conjugate Complex Number in Place:
	 * i.e. the imaginary Part flips it's sign.	 */
	public IIntRing cjgAt()	{ return this; } 	//conjugate all it's Elements!

	/**Returns the conjugate Complex Number in Place:	 */
	public IIntRing CmplAt(){throw new AbstractMethodError(); } //return this; } 	//complement all it's Elements!

	/**Returns this Number converted to Upper in Place:	 */
	public IIntRing toUpperAt(){throw new AbstractMethodError(); } //return this; } 	//complement all it's Elements!

	/**Returns the conjugate Complex Number in Place:
	 * i.e. the imaginary Part flips it's sign.	 */
	public void addCarry(){throw new AbstractMethodError(); } //return; } 	//complement all it's Elements!


	/**Testing Method, should be static or directly tested on the Types.
	 * Normally there are only these two Representations: Complex and Polar	 */
	public boolean isComplex() { return false; }

	//////////////////////////
	//	Interface swapAble	//
	//////////////////////////

	/**Swaps the Elements i and j of the Array in Place.	 */
	public VectorDbl swapAt(final int i, final int j) { a.swapAt(i, j); return this; }

	/**Swaps the Elements i and j of the Array.	 */
	/*
	public VectorDbl swap(int i, int j)
	{return ((swapAble)copy()).swapAt(i,j);}

	/**Returns true, when the Items in the Container are ordered ascending
	 * from the i-th Item on (monotonous Sequence)	 */
	public boolean isOrdered(){ return a.isOrdered(); }

	/**Returns an Iterator of the components in this Container.
	 *
	 * @return  an Iterator of the components in this Container.
	 * @see     Math.Iterator     */
	//public Iterator Iterator() {return new ManifoldIterator(this);}

	/**Arithmetic Shift right by one position in Place: x>>=1	*/
	public IIntRing shrAt(final int shift) { a.shrAt(shift); return this; }

	/**Arithmetic Shift left  by one position in Place: x<<=1	*/
	public IIntRing shlAt(final int shift) { a.shlAt(shift); return this; }

	/** less: '<'
	  * @return  True, when 'Self' < arg	*/
	public boolean isLessThan(Object arg) { 
		if (!(arg instanceof VectorDbl))
			return less(ByRefDouble.GET_DOUBLE(arg)); 	//normal Number
		return a.less(((VectorDbl) arg).a); }

	/**grtr: '<' Returns True, when 'Self' > arg	*/
	public boolean isMoreThan(Object arg) {
		return a.grtr(((VectorDbl) arg).a); }

	public void addCarry(long Carry) {
		addAt(new ByRefLong(Carry)); return; }

	public long  getUpper(long Mask, long ModByPeriod) {
		return 0;}	//also clears the Upper Part (Mask) and prepares Addition to the Lower (ModByPeriod)
	public long  getLower(long Mask, long ModByPeriod) {
		return 0;}	//Lower Part is cleared on Shifting automatically.

	//////////////////////
	//	Optimizations	//
	//////////////////////
	
	/** Returns /=2 in Place	 */
	public IIntRing halfAt() { a.halfAt(); return this; }
	
	/** Returns /=3 in Place	 */
	public IIntRing	thirdAt() { a.thirdAt(); return this; }
	
	/** Returns /=4 in Place	 */
	public IIntRing	quarterAt() { a.quarterAt(); return this; }
	
	/** Returns *=2 in Place	 */
	public ISemiGroup	dblAt() { a.dblAt(); return this; }
	
	/** Returns *=3 in Place	 */
	public ISemiGroup	trplAt() { a.trplAt(); return this; }

	/** Returns *=4 in Place	 */
	public ISemiGroup	quadAt() { a.quadAt(); return this; }

	/** Returns *=n in Place	 */
	public ISemiGroup	mulAt(final int n) { a.mulAt(n); return this; }

	/** Returns *=2^n in Place	 */
	public ISemiGroup  mul2PowAt(final int n) { a.mul2PowAt(n); return this; }

	/**Returns the Minimum of this and the Operand in Place	 */
	public IOrder MaxAt (Object arg) {
		if (arg == null) {
			return this; } 
		if (arg instanceof VectorDbl) {
			a.MaxAt(((VectorDbl) arg).a);
		} else {
			MaxAt(ByRefDouble.GET_DOUBLE(arg)); 	//normal Number
		}
		return this; }

	/**Returns the Minimum of this and the Operand in Place	 */
	public IOrder MinAt(Object arg) {
		if (arg == null) {
			return this; } 
		if (arg instanceof VectorDbl) {
			a.MinAt(((VectorDbl) arg).a);
		} else {
			MinAt(ByRefDouble.GET_DOUBLE(arg)); 	//normal Number
		}
		return this; }

	/**Sets and returns the maximum Value for this Class in Place.	 */
	public IWellOrder maxValueAt() { a.maxValueAt(); return this; }

	/**Sets and returns the minimum Value for this Class in Place.
	 * Usually for symmetric Types this is about the negative maxValue.	 */
	public IWellOrder minValueAt() { a.minValueAt(); return this; }

	/**Returns the Representation of -Infinity for this Class in Place.	 */
	public IWellOrder NegInfinityAt() { a.NegInfinityAt(); return this; }

	/**Returns true when the Value of this Object is Infinity.	 */
	public boolean  isInfinite() { return a.isInfinite(); } 

	/**Returns the minimum absolute Value (greater than Zero) for this Class in Place.	 */
	public IWellOrder minAbsValueAt() { a.minAbsValueAt(); return this; }

	/**Returns the Representation of Infinity for this Class in Place.
	 * The resulting Complex Infinity is projective (not affine),
	 * it has indefinite Length (1/0) and no phase! (0/0)	 */
	public IWellOrder InfinityAt() { a.InfinityAt(); return this; }

	/**Returns the Representation of an invalid Number for this Class in Place.	 */
	public IWellOrder NaNAt() { a.NaNAt(); return this; }

	//////////////////////////
	//	Double Operations	//
	//////////////////////////

	/**Since this Class operates with "double" Numbers,
	 * you can define special Operations that take double as Arguments
	 * unlike the other Operations that rely on compatibility of Arguments.
	 * This could be defined as different Types of Operations on the same Iterator
	 * but that would save no LOC and be much more inefficient.
	 *
	 * All n^m Operations can be sped up by a Factor of 10 using double Numbers
	 * in the basic Operation.
	 * All other efforts like counting Pointers or keeping References
	 * don't really pay off that much.
	 * Another cost saver is not creating too many Objects (Flyweight).
	 */

	/** Addition in Place: +=	*/
	public ILngGroup addAt( long    arg)  {
		a.addAt(arg); 
		return this; }

	/** Addition: +	*/
	public ILngGroup add(final long arg)  {
		return new VectorDbl(a.add(arg)); }

	/** Addition: +	*/
	public IDblGroup add(final double arg)	{
		return new VectorDbl(a.add(arg)); }

	/** Addition in Place: +=
	 * TODO: this treats this Array as a Sample over the same Dimension
	 * just like diff and the other analytical Operations!
	 */
	public IDblGroup addAt(final double arg)	{
		a.addAt(arg); 
		return this; }

	/** Subtraction: -	*/
	public ILngGroup subt(final long arg)  {
		return new VectorDbl(a.subt(arg)); }

	/** Subtraction in Place: -=	*/
	public ILngGroup subAt(final long arg)  {
		a.subAt(arg); 
		return this; }

	/** Subtraction: -	*/
	public IDblGroup sub(final double arg)	{
		return new VectorDbl(a.subt(arg)); }

	/** Subtraction in Place: -=	*/
	public IDblGroup subAt(final double arg)	{
		a.subAt(arg); 
		return this; }

	/** Multiplication: *	*/
	public ILngGroupM mul(final long arg)  {
		return new VectorDbl(a.mul(arg)); }

	/** Multiplication in Place: *=	*/
	public ILngGroupM mulAt(final long arg)  {
		a.mulAt(arg); 
		return this; }

	/** Multiplication: *	*/
	public IDblGroupM mul(double arg)	{
		return new VectorDbl(a.mul(arg)); }

	/** Multiplication in Place: *=	*/
	public IDblGroupM mulAt(double arg)	{
		a.mulAt(arg); 
		return this; }

	/** Division: /	*/
	public ILngGroupM div(long arg)  {
		return new VectorDbl(a.div(arg)); }

	/** Division in Place: /=	*/
	public ILngGroupM divAt(long arg)  {
		a.divAt(arg); 
		return this; }

	/** Division: /	*/
	public IDblGroupM div(double arg) {
		return new VectorDbl(a.div(arg)); }

	/** Division in Place: /=	*/
	public IDblGroupM divAt(double arg) {
		a.divAt(arg); 
		return this; }

	/** Copy in Place: =
	  * No distinction between deep or shallow Copy, because this is a shallow structure.	*/
	public VectorDbl copyAt(double arg)	{ a.copyAt(arg); return this; }

	/** Check for equality: ==	*/
	public boolean equals(double arg) { return a.equals(arg); }

	/** less: '<'
	  * @return  True, when 'Self' < arg	*/
	public boolean less(long arg) { return less((double) arg); }

	/** less: '<'
	  * @return  True, when 'Self' < arg	*/
	public boolean less(double arg)	{ return a.less(arg); }

	/** less or equal: '<'
	  * @return  True, when 'Self' <= arg	*/
	public boolean lessEq(long arg)	{ return lessEq((double) arg); }

	/** less or equal: '<'
	  * @return  True, when 'Self' <= arg	*/
	public boolean lessEq(double arg) { return a.lessEq(arg); }

	/** greater: '>'
	  * @return  True, when 'Self' > arg	*/
	public boolean grtr(long arg) { return grtr((double) arg); }

	/** greater: '>'
	  * @return  True, when 'Self' > arg	*/
	public boolean grtr(double arg)	{ return a.grtr(arg); }

	/** greater or equal: '>='
	  * @return  True, when 'Self' >= arg	*/
	public boolean grtrEq(long arg)	{ return grtrEq((double) arg); }

	/** greater or equal: '>='
	  * @return  True, when 'Self' >= arg	*/
	public boolean grtrEq(double arg) { return a.grtrEq(arg); }

	/** @return the Maximum: 	*/
	public ILngOrder Max (long arg)  { return Max((double) arg); }

	/** @return the Maximum in Place: 	*/
	public IDblOrder Max (double arg) {
		return new VectorDbl(a.Max(arg), false); }


	/** @return the Maximum in Place: 	*/
	public ILngOrder MaxAt (long arg) { return MaxAt((double) arg); }

	/** @return the Maximum in Place: 	*/
	public IDblOrder MaxAt (double arg) { a.MaxAt(arg); return this; }

	/** @return the Minimum: 	*/
	public ILngOrder Min (long arg) { return Min((double) arg); }

	/** @return the Minimum: 	*/
	public IDblOrder Min (double arg) { 
		return new VectorDbl(a.Min(arg), false); }

	/** @return the Minimum in Place: 	*/
	public ILngOrder MinAt (long    arg)  {
		     return MinAt((double) arg); }

	/** @return the Minimum in Place: 	*/
	public IDblOrder MinAt (double arg) { a.MinAt(arg); return this; }


	/////////////////////////////////////////////////////////////////////////////////////
	//  Interface IManifold
	/////////////////////////////////////////////////////////////////////////////////////

	/** Constructor building the Interpolation Polynom
	  * from the Samples given in this Manifold and y.	 */
	public Interpolator Interpolator(IManifold y_) {
		return new Interpolator (
			BodyDouble.BODY_DOUBLE(a.getItems()),
			BodyDouble.BODY_DOUBLE(((VectorDbl) y_).a.getItems()), mDim); }
	
	/** @return the Difference Vector of this Manifold in Place: diff(i)= a(i) - a(i+1)
	  * The Difference Vector has one Item less than the original Vector.
	  * For complete Reversibility the last Item is preserved.	 */
	public IManifold diffAt() { a.diffAt(); return this; }

	/** @return  the Integrated Vector of this Manifold in Place: int(i)= a(i) + a(i+1)
	  * This is the reverse Operation to diffAt().
	  *
	  * It is used e.g.
	  * to calculate the accumulated Probability of a discrete Distribution
	  *
	  * The Integral has one Item more than this Vector.
	  * This last Item is new and initialized to zero, if it was not preserved
	  * from a previous diff Operation or initialized before.
	  * If you want to start Integration from a certain Value,
	  * it is faster to modify this start Value by modifying the last Item.	 */
	public IManifold summAt() { a.summAt(); return this; }

	/** @return the full Difference Vector of this Manifold in Place
	  * The full Difference Vector consists of all Derivatives.
	  * It can be used to calculate inter- and extrapolations with Horner(). 	 */
	public IManifold fullDiffAt() { a.fullDiffAt(); return this; }

	/** Adds a Point (y0) to the Manifold.
	  * If the Manifold has been differentiated,
	  * all Points are differentiated 	 */
	public IManifold addPointAt(IIntRing y0) { 
		a.addPointAt(ByRefDouble.GET_DOUBLE(y0)); 
		return this; }

	/** Adds a Point (y0, x0) to the Difference Vector.
	  * The x Coordinate is given implicitly by the inverse Coordinate Differences
	  * in invDiffX. 	 */
	public IManifold addPointAt(IIntRing y0, IIntRing x0, IManifold x) {
		a.addPointAt(ByRefDouble.GET_DOUBLE(y0), ByRefDouble.GET_DOUBLE(x0), ((VectorDbl) x).a);
		return this; }

	/** Calculates the Value of this Manifold at the Point x,
	  * using the already calculated Differences at equidistant Points.
	  * Gives best results, if the Manifold has been differenced all through,
	  * because only the higher Coefficients are used.
	  * This is well suited for the repetitive Calculation of interpolating Values,
	  * but for a single interpolated Value, it is better to use Inter/Extrapolation
	  * with either Polynomial or Rational Functions.
	  * The Division by the factorials is done once, when this function is differenced! 	 */
	public IIntRing Horner(IIntRing x, IIntRing x0, IIntRing h) {
		return new BodyDouble(a.Horner(
			ByRefDouble.GET_DOUBLE(x ),
			ByRefDouble.GET_DOUBLE(x0),
			ByRefDouble.GET_DOUBLE(h))); }

	/** @return the dyadic Product of this Tensor and arg.
	  * This is a pre Step to calculating the generic Scalar Product.
	  * The Degree of the Tensor is the Sum of the Degrees of the Factors.
	  *
	  * The inner structure of arg is retained on creating the Product.
	  */
	public ITensor dyad(ITensor arg) {
	/*	IIntRing   a_i;
		Tensor     T_i;
		int i =     mDim+1;
		IIntRing[] ret;
		while (--i >= 0) {
			Value.Value = a[i];
			ret[i] = (IIntRing) arg.mul(Value);  //Product with a Scalar
		} return new Tensor(ret);
	*/	return null;
	}
	
	//////////////////////
	//	Optimizations:	//
	//////////////////////

	//LinAt, addProdAt etc. are not really getting better
	//by saving a Loop or an intermediate Result.

	//////////////
	//	Testing	//
	//////////////

	/** Testing the Differential Operations of this class	 */
	public static void testDiff() {
		double[] x1 = {0.0, 1.0, 3.0};
		double[] y1 = {1.0, 3.0, 2.0};

		VectorDbl x1_ = new VectorDbl(x1, false);
		VectorDbl y1_ = new VectorDbl(y1, false);
		VectorDbl dx_ = (VectorDbl) x1_.diff().invAt(); //creates the Factors for
//		y.fullDiffAt(x); //TODO: Differencing on non equidistant Raster!
//		y.Horner(new BodyDouble(2.0), x);  //TODO: test the Horner Scheme on a non equidistant Raster!
		System.out.println(y1_); 
		System.out.println(dx_); 

		IFloatDeriveAble fktn = function.derive.ring.body.Cosinus.Cosinus;	//RingFuncs.IdentityCopy();	//RingFuncs.fSquare(); //IdentityCopy(); //Cosinus(); //CosHMinus1(); //CosinusMinus1(); //Cosinus(); //Sinus();
		BodyDouble x0  = new BodyDouble(0  );//-Math.PI / 3); 
		BodyDouble ddx = new BodyDouble(0.2);//Math.PI / 12); 
		VectorDbl x = new VectorDbl(x0.value, ddx.value, 5); 
		VectorDbl Sample = new VectorDbl(fktn, x); 

		System.out.println(" Interpolation with equidistant Sample Points");
		System.out.println(" x = " + x);
		VectorDbl dx = (VectorDbl) x.diff();
		System.out.println(" dx = " + dx);
		System.out.println(" y = f (x) = " + Sample);
		Sample.fullDiffAt();
		System.out.println(" Values at the Sample Points: approx == exakt ");
		int i = -1;
		while (++i <= x.getDim()) {
			BodyDouble z = new BodyDouble(x.getAt(i));//new BodyDouble(Math.random());
			System.out.println(" z = " + z + "; f (z) = " + Sample.Horner(z, x0, ddx) + " == " + fktn.Map(z.value)); }
		System.out.println(" Values at random Points: approx == exakt ");
		i = -1;
		while (++i <= x.getDim()) {
			BodyDouble z = new BodyDouble(Math.random());
			System.out.println(" z = " + z + "; f (z) = " + Sample.Horner(z, x0, ddx) + " == " + fktn.Map(z.value)); }
	}

	public static void testVector(VectorDbl P2) {
		ByRefInt    Pos  = new ByRefInt();
		ByRefObject Max  = new ByRefObject();
		BodyDouble x0    = new BodyDouble(0.5);
		BodyDouble sqr   = new BodyDouble();
		BodyDouble skew  = new BodyDouble();
		BodyDouble kurt  = new BodyDouble();
		BodyDouble AVG   = new BodyDouble();

		Log.OUT.l(P2).println();
		System.out.println("Maximum:   :" + AManifold.MaxCopy(P2));
		System.out.println("Minimum:   :" + AManifold.MinCopy(P2));
		System.out.println("Minimum:   :" + AManifold.MinMaxCopy(P2, Max));
		System.out.println("Maximum:   :" + Max); AVG = (BodyDouble) AManifold.Sum(P2, Pos);
		System.out.println("Sum @ 0:   :" + AManifold.Sum   (P2, Pos) + " Number: " + Pos.Value);
		System.out.println("Average:   :" +(AVG.value /= Pos.Value));
		System.out.println("Prod:      :" + AManifold.Prod  (P2, Pos) + " Number: " + Pos.Value);
		System.out.println("SqrSum@0   :" + AManifold.SqrSum(P2, Pos) + " Number: " + Pos.Value);
		System.out.println("Sum@.5     :" +(AManifold.SumSqrSum(P2, Pos, x0, sqr)));// +(Pos.Value * x0.Value)) + " Number: " + Pos.Value);
		System.out.println("SqrSum@AVG :" +(sqr.value - ByRefDouble.SQR(AVG.value-x0.value)*Pos.Value));
		System.out.println("Sum@.5     :" +(AManifold.Moments(P2, Pos, x0, sqr, skew, kurt)));// +(Pos.Value * x0.Value)));
		System.out.println("SqrSum@AVG :" +(sqr.value - ByRefDouble.SQR(AVG.value-x0.value)*Pos.Value));
		System.out.println("Skewness@.5:" + skew);
		System.out.println("Kurtosis@.5:" + kurt);
		System.out.println("search     :" + AManifold.search(P2, x0, true) + " with Value: " + x0); x0.value = 0.5;

	}

	/**Testing this class	 */
	public static void testIt() throws Exception {
		System.out.println("Testing " + VectorDbl.class.getName());
		testDiff();

		int i = 22,j = 15;

		i = (char)(i*j);
		j *= j;
		j /= i;
	//	long a[] = {7};
		int ArrLength = 10;

		double[] x = new double[ArrLength];	//{7};
		double[] y = {0.9895367129870271,0.8945692396440466,0.957776777001035,0.17857843614759128,0.11463640253953944,0.8240336934847097,0.6284423676353167,0.07429121036123232,0.757808517776348};
		double[] a = new double[ArrLength];	//{7};
		double[] b = new double[ArrLength];	//{7};
		double[] c = new double[ArrLength];	//{7};
		i = ArrLength;
		while (--i >= 0) {
			x[i] = 0.5; //all Data identical => 0 Variance etc.
			a[i] = Math.random();
			b[i] = Math.random()*10;
			c[i] = Math.random()*100;
		}
		VectorDbl P0 = new VectorDbl (x, false);
		VectorDbl P4 = new VectorDbl (y, false);
		VectorDbl P1 = new VectorDbl (a, false); b[0] = 3;
		VectorDbl P2 = new VectorDbl (b, false);
		VectorDbl P3 = new VectorDbl (c, false);

		testVector(P0);
		testVector(P2);
		testVector(P3);
		testVector(P4);
		testVector(P1);

//		System.in.read();
		/**Results in 9 mio Additions on a 250 MHz machine,
		 * i.e. only 27,5 Cycles per double precision Addition.
		 * This is only 30% slower than the equivalent C++ Implementation.	 */
//		i =  200; while (--i >= 0) P1.addAt(P2);
	/*
													L.n(P1 + " + " + P2 + " = ");
		P1.addAt (P2); L.n(P1); L.n(P1 + " + " + P2 + " = ");
		P1.addAt (P2); L.n(P1); L.n(P1 + " + " + P2 + " = ");
		P1.addAt (P2); L.n(P1); L.n(P1 + " + " + P2 + " = ");
		P1.addAt (P2); L.n(P1); L.n(P1 + " + " + P2 + " = ");
		P1.addAt (P2); L.n(P1); L.n(P1 + " + " + P2 + " = ");
		P1.addAt (P2); L.n(P1); L.n(P1 + " - " + P2 + " = ");
		P1.subAt(P2); L.n(P1); L.n(P1 + " - " + P2 + " = ");
		P1.subAt(P2); L.n(P1); L.n(P1 + " - " + P2 + " = ");
		P1.subAt(P2); L.n(P1); L.n(P1 + " - " + P2 + " = ");
		P1.subAt(P2); L.n(P1); L.n(P1 + " - " + P2 + " = ");
		P1.subAt(P2); L.n(P1); L.n(P1 + " - " + P2 + " = ");
		P1.subAt(P2); L.n(P1); L.n(P1 + " + " + P2 + " = ");
		P1.addAt (P2); L.n(P1); L.n(P1 + " * " + P2 + " = ");
								P1.mulAt (P2);	L.n(P1);
		L.n(P1+"*"+P2+" = ");	P1.mulAt (P2);	L.n(P1);
		L.n(P2+" ^2 = ");		P2.mulAt (P2);	L.n(P2);
	//	L.n("-"+P1+" = ");		P1.negAt ();	L.n(P1);	//
		L.n(P2+" ^2 = ");		P2.sqrAt ();	L.n(P2);
		L.n("-"+P2+" = ");
								P2.negAt ();
																	L.n(P2 + "\n");
		L.n(P2+" ^2 = ");		P2.mulAt (P2);	L.n(P2);
		L.n("-"+P2+" = ");		P2.negAt ();	L.n(P2); //
		L.n("-" + P2 + " = ");	P2.negAt ();	L.n(P2); //
		L.n(P1+" * "+P2+" = ");	P1.mulAt (P2);	L.n(P1);
		L.n(P1+" / "+P2+" = ");	P1.ModAtDivAt(P2, P3); L.n(P3+" Rest: "+P1);
		L.n(P1+" / "+P2+" = ");
								P1.divAt (P2); L.n(P1 + "\n");
	*/
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws Exception {
		testIt();
	}

}
