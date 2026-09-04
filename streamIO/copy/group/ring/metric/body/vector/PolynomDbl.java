/*
 * File Name: PolynomDbl.java
 * Created on: 10.01.2004
 *
 */
package streamIO.copy.group.ring.metric.body.vector;

import math.vector.VectorDouble;
import streamIO.Log;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.groupM.IGroupM;
import function.ICountAble;
import function.IFloatFunction;
import function.IFunction;
import function.IInvertAble;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.derive.IDeriveAble;

/**
 * Title: PolynomDbl<p>
 * Description:
 * Extends the VectorDbl Class to redefine the Methods that work differently 
 * as Samples (i.e. ordered Sets) over the same Dimension (addAt, diff etc.) or  
 * as Polynomes (addAt adds only to the 1st Element, diff does Polynom division)
 *
 * TODO: Sampling a Function builds the interpolating Polynom right away...
 *
 * Polynoms can be defined on any Ring, especially Integrity Rings
 * and they themselves form a Ring again.
 *
 * Vectors can be defined on any Ring, especially Integrity Rings
 * and they form a Vector Space with the Ring as external Operand.
 * I.e. the Vector Space forms a commutative Body (V,+)
 * and an external Multiplication *: V*R -> V with an Integrity Ring (R,+,*,1)
 * is defined so that for any a,b from V and u, v from R the following is true:
 * 1)(a+b)*u  = a*u + b*u	(distributive)
 * 2) a*(u+v) = a*u + a*v	(distributive)
 * 3) a*(u*v) =(a*u)*v		(homogenic)
 * 4) a*1     = a
 *
 * Additionally a Scalar Product <,>: V*V -> R can be defined
 * that maps back to the Ring with:
 * 1) <x+y,z> = <x,z> + <y,z>	(distributive)
 * 2) <k*x,y> = k * <x,y>		(homogenic)
 * 3) <x,y> > 0					(only for metric R)
 * 4) <x,y> = ~<y,x>			(only for R as Complex)
 *
 * It can be proven that...
 * <x,y+z> = <x,y> + <x,z>
 * <x,k*y> = ~k*<x,y>
 * <x,0> = <0,x> = 0
 * (<x,z> = <y,z> für alle z) => x == y
 *
 * The Problem here is the redefinition of several Methods.
 * The Multiplication with a Scalar means the Streching of the Vector/Polynom
 * The Multiplication with a Vector/Polynom can mean two things:
 * Scalar Product or Polynom Multiplication.
 * Both are homogenic and fulfil the commutative and distributive Laws,
 * but only the latter is an inner Operation.
 * So the Scalar Product receives a new Name: "scalar",
 * but the Multiplication is redefined in the SubClass "Vektor" by this Scalar Product.
 *
 * Design Decisions:
 * The Array is not implemented as a java.util.Vector
 * and also this class is not derived from a java.util.Vector,
 * to avoid the casting (Vector contains only Objects),
 * because the Access notation is more transparent,
 * and because the number of Elements can be computed before each Operation.
 *
 * Polynomial Inter/Extrapolation: 
 * The Polynom is evaluated on a normed Input Range of [0,1] 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class PolynomDbl 
extends VectorDbl 
implements IDeriveAble 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Logger for Testing, modify Threshold for switching Logging */
	private static Log L = new Log(PolynomDbl.class, 0);

	/////////////////////////////////////////////////////////////////////////////////////

	/** 
	 * @param zeroPos
	 * @return the LinearFactor (1*x-zeroPos) as a Polynom 
	 */
	final static public PolynomDbl GET_LF(final double zeroPos) {
		return new PolynomDbl(VectorDouble.GET_LF(zeroPos), false); }

	/** 
	 * @param real
	 * @param imag
	 * @return the LinearFactor (x-real-i*imag)*(x-real+i*imag)=x²-2*real*x+real²+imag² as a Polynom
	 */
	final static public PolynomDbl GET_LF(final double real, final double imag) {
		return new PolynomDbl(VectorDouble.GET_LF(real, imag), false); }

	/////////////////////////////////////////////////////////////////////////////////////

	/** empty Constructor, defaults to 0, equivalent to null	 */
	public PolynomDbl() { super(); }

	/** Copy Constructor, also allowing Vectors
	 * @param arg
	 */
	public PolynomDbl(VectorDbl arg) { super(arg); }
	//public PolynomDbl(PolynomDbl arg) { super(arg); }

	/**Copy Constructor
	 * @param arg
	 */
	public PolynomDbl(VectorDouble arg) { super(arg); }

	/**Copy Constructor
	 * @param arg
	 * @param copy
	 */
	public PolynomDbl(VectorDouble arg, boolean copy) {
		super(arg, copy);
		// TODO Auto-generated constructor stub
	}

	/**Copy Constructor
	 * @param arg
	 * @param copy
	 */
	public PolynomDbl(double[] arg, boolean copy) { super(arg, copy); }


	/**
	 * Sampling a Function builds the interpolating Polynom right away...
	 * @param f
	 * @param x
	 */
	public PolynomDbl(IFloatFunction f, VectorDbl x) {
		super(f, x);
	}

	/**
	 * Sampling a Function builds the interpolating Polynom right away...
	 * @param f
	 * @param x0
	 * @param dx
	 * @param Grad
	 */
	public PolynomDbl(IFloatFunction f, double x0, double dx, int Grad) {
		super(f, x0, dx, Grad);
	}

	/**
	 * Sampling the Dimension linearly between x0 and x1 
	 * @param x0
	 * @param dx
	 * @param Grad
	 */
	public PolynomDbl(double x0, double dx, int Grad) {
		super(x0, dx, Grad);
		// TODO Auto-generated constructor stub
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/* Addition and Subtraction stay the same
	 * rational Functions are a simple Extension to the multiplicative SemiGroup 
	 * similar to Fractions or to CreditContainers. 
	 */
	
	/** multiplies the Polynom; not possible in Place, 
	 * since it is effectively a Convolution. 
	 * Could be even faster (for higher Dimensions) 
	 * when performed in Fourier Space, where it reduces to a simple, 
	 * element-wise Multiplication. 
	 */ 
	public PolynomDbl mul(final PolynomDbl arg) {
		return new PolynomDbl(a.mulPolynom(arg.a)); }

	/**
	 * Do a Kind of Polynom Division, which is slow, 
	 * but at least applies both to Integers and to Polynoms!  
	 * @see streamIO.copy.groupM.IIGroupM#divAt(java.lang.Object)	 
	 */
	public IGroupM divAt(final Object arg) {
		return div((PolynomDbl) arg); }

	/** divides the Polynom  	 */ 
	public PolynomDbl div(final PolynomDbl arg) {
		final PolynomDbl ret = new PolynomDbl();
		((PolynomDbl) copy()).ModAtDivAt(arg, ret);
		return ret; }
	
	/** @see streamIO.copy.group.ring.IIntRing#ModAtDivAt(java.lang.Object, streamIO.copy.group.ring.IIntRing)	 */
	public IIntRing ModAtDivAt(Object arg, IIntRing quotient) {
		return ModAtDivAt((PolynomDbl) arg, (PolynomDbl) quotient); }

	/**
	 * divides the Polynom in Place with Remainder;  
	 * 
	 * Despite it's similar Structure, 
	 * Polynom Division is very different from g-adic Division, 
	 * which is very simple for g=2, but quite complicated for larger gs!!!
	 * With Rollover the Algorithm needs a lot more than the simple Polynom Division.
	 * You ALWAYS (except for g=2) have an Uncertainty in the last Digits
	 * that could affect the first Digits by a Ripple Carry!
	 * 
	 * Could be even faster (for higher Dimensions) 
	 * when performed in Fourier Space, where it reduces to a simple, 
	 * element-wise Division. 
	 * 
	 * A more efficient Algorithm is described in the numerical Recipes, 
	 * where the Newton Algorithm is used to solve the Equation this = quot*arg+mod for quot and mod
	 * @see  streamIO.copy.group.ring.IIntRing#ModAtDivAt(java.lang.Object, streamIO.copy.group.ring.IIntRing)
	 */ 
	public PolynomDbl ModAtDivAt(final PolynomDbl arg, final PolynomDbl quotient) {
		if ((arg == null) || (arg.isZero())) {
			throw new ArithmeticException("Division of "+this+" by Zero:"+arg); }
		a.modAtDivAt(arg.a, quotient.a); 
		return this; }
	
	/////////////////////////////////////////////////////////////////////////////////////
	//redefine Differentiation and Integration
	/////////////////////////////////////////////////////////////////////////////////////
		
	/** @see streamIO.copy.group.ring.metric.body.vector.IManifold#diffAt()	 */
	public IManifold diffAt() { a.diffPolynomAt(); return this; }

	/**Returns the Integrated Vector of this Manifold in Place: int(i)= a(i) + a(i+1)
	 * This is the reverse Operation to diffAt().
	 * The Integral Polynom has one Item more than the original Polynom.
	 * This is either restored from the highest Element
	 * or assumed to Zero. 	 
	 * @see streamIO.copy.group.ring.metric.body.vector.IManifold#summAt()	 
	 */
	public IManifold summAt() {
		a.summPolynomAt();
		return this; }

	/////////////////////////////////////////////////////////////////////////////////////
	//	Implementation of IFunction	
	/////////////////////////////////////////////////////////////////////////////////////

	/**Returns the Polynom as the Function Value.
	 * The Polynom is it's own Function Class! 
	 * TODO: Origin and Step Size are not defined. 
	 */
	public Object Map(Object arg) { return new Double(a.Horner(ByRefDouble.GET_DOUBLE(arg), 0, 1)); }

	/**Returns the Polynom as the Function Value.
	 * The Polynom is it's own Function Class!	 */
	public Object MapAt(Object arg) { return map(arg); }

	/**Returns true, when this Class can operate on Arguments of this Type
	 * This Function makes sense at this Level,
	 * because here there is always the Alternative
	 * not to operate on the Constants,
	 * but to operate on the Functions and operate the Results on evaluation.	 */
	public boolean canProcess(Object arg) {
		return (arg instanceof PolynomDbl)
			|| (arg instanceof ICountAble)
			|| (arg instanceof IMeasurAble)
//			|| (arg instanceof IConst)	//taken out only to deCouple RingFuncs in canProcess
	;}

	/**Returns the simplified Polynom, doesn't change	 */
	public IFunction simplify() { return this; }

	/**Returns the differentiated Polynom (Derivative)	 */
	public IDeriveAble getDerivative() { return new PolynomDbl(a.copyOrig().diffPolynomAt(), false); }

	/**Sets the differentiated Polynom (Derivative)	 */
	public void setDerivative(IDeriveAble Derivative) { throw new AbstractMethodError(); }

	/**Returns the integrated Polynom (Integral)	 */
	public IDeriveAble getIntegral() { return new PolynomDbl(a.copyOrig().summPolynomAt(), false); }

	/**Sets the integrated Polynom (Integral)	 */
	public void setIntegral(IDeriveAble Integral) { throw new AbstractMethodError(); }

	/**Returns the inverted Polynom (Inverse)	 */
	public IInvertAble getInverse() { throw new AbstractMethodError(); }

	/**Sets the inverted Polynom (Inverse)	 */
	public void setInverse(IInvertAble Inverse) { throw new AbstractMethodError(); }

	/**Returns the Solution of the Polynom by the Function Value.
	 * Could use Newton Iteration or any other Algorithm, but a Starting Value is necessary.
	 * This could as well have several Solutions!  */
	public Object UnMap(Object arg) { throw new AbstractMethodError(); }

	/**Returns the Solution of the Polynom by the Function Value.
	 * Could use Newton Iteration or any other Algorithm, but a Starting Value is necessary.
	 * This could as well have several Solutions!  */
	public Object UnMapAt(Object arg) { throw new AbstractMethodError(); }

	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * multiplies the LinearFactor (x-zeroPos) into this Polynom 
	 * creating a Radix at zeroPos. 
	 * @param zeroPos the Position of the Radix
	 * @return this Polynom in Place
	 */
	public PolynomDbl mulLfAt(final double zeroPos) {
		a.mulLfAt(zeroPos); 
		return this; }

	/**
	 * multiplies the LinearFactor (x-real-i*imag)*(x-real+i*imag) into this Polynom 
	 * creating two Radices at the complex Positions. 
	 * @param real
	 * @param imag
	 * @return
	 */
	public PolynomDbl mulLf(final double real, final double imag) {
		return mul(GET_LF(real, imag)); }

	/**
	 * divides the LinearFactor (1*x-zeroPos) from this Polynom 
	 * eliminating a Radix at zeroPos. 
	 * (a detailed Implementation would only save the Division required to calculate the Quotient) 
	 * @param zeroPos
	 * @return the Remainder of this Division (zero if the Radix was exact) 
	 */
	public double modLfAt(final double zeroPos, final PolynomDbl quotient) {
		final PolynomDbl div = GET_LF(zeroPos);
		ModAtDivAt(div, quotient);
		return a.getDoubleAt(0); }

	/**
	 * divides the LinearFactor (x-real-i*imag)*(x-real+i*imag)=x²-2*real*x+real²+imag² from this Polynom 
	 * eliminating two Radices at the complex Positions. 
	 * This saves half of the complex Operations. 
	 * @param real
	 * @param imag
	 * @return
	 */
	public PolynomDbl modLfAt(final double real, final double imag, final PolynomDbl quotient) {
		final PolynomDbl div = GET_LF(real, imag);
		ModAtDivAt(div, quotient);
		return this; }

	/*

	FUNCTION Make_Raster (VAR V : Vektorx0,dx : Real) : Word
	{
	 VAR G : Word
	 G = SizeOf (Real)*V.SpaltenGetMem (V.Vektor,G);
	 V.Vektor^[1]:=x0; Skal_Add (@V.Vektor^[1],@V.Vektor^[2],SizeOf (Real),SizeOf (Real),Pred (V.Spalten),dx);
	 Make_Raster = G
	}

	FUNCTION ShortLorentz (VAR L : IntegerS,G : Byte) : ShortInt
	{
	 INC (           DoShort (L).HiShort,Random (Succ (S SHL 1))-S);
	 DEC (L,Integer (DoShort (L).HiShort) SHL G);
	 ShortLorentz  :=DoShort (L).HiShort
	};

	PROCEDURE transformieren (N : cNullStellet : Real);
	{  {Variablenr?cktransformation aus der reduzierten Form}
	 RZ1 = P_Real (N.a);
	 FOR Z1 = 1 TO N.Grad DO { INC (P_Complex (RZ1));RZ1^:=RZ1^-t }
	};

	*/

	/////////////////////////////////////////////////////////////////////////////////////
	//	Testing	
	/////////////////////////////////////////////////////////////////////////////////////

	/**Testing this class	 */
	public static void testIt() throws Exception {
		L.n("Testing "+PolynomDbl.class+":");
		
		//demonstrate the complete Reconstruction (Integration) of a derived Polynom
		final double[] p1 = {1, 1, 1, 1};
		PolynomDbl P1 = new PolynomDbl(p1, true);
					 L.n("P1    = " + P1);
		P1.diffAt(); L.n("P1'   = " + P1);
		P1.diffAt(); L.n("P1''  = " + P1);
		P1.diffAt(); L.n("P1''' = " + P1);
		P1.diffAt(); L.n("P1''''= " + P1);
		P1.summAt(); L.n("P1''' = " + P1);
		P1.summAt(); L.n("P1''  = " + P1);
		P1.summAt(); L.n("P1'   = " + P1);
		P1.summAt(); L.n("P1    = " + P1);
		P1.summAt(); L.n("I(P1) = " + P1);
		P1.summAt(); L.n("II(P1)= " + P1);
		L.readString(); L.readString();
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws Exception {
		testIt();
	}
	
}
