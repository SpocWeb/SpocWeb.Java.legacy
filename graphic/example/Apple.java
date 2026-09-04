package graphic.example;

import graphic.IPoint2DFunction;
import graphic.Point2D;

/**
  * Title: Apple<p>
  * Description:
  * Purpose:
  * Class encapsulating the Algorithm to generate a AppleMan Fractal
  * Purpose / Responsibilities of this Class
  *
  * Design Decisions / Implementation Details:
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Known SubClasses:
  *
  * Known Uses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-07-2002, 09:50 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class Apple
implements IPoint2DFunction {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return the Square of the argument */
	final static public double sqr(double arg) { return arg*arg; }
	
	/** @return the Square of the argument */
	final static public float sqr(float arg) { return arg*arg; }
	
	/** @return true, if this Vector definitely lies inside the Apple Man */
	public static boolean inAppleMan(double[] V) {
		//the first Test solely relies on the real Component!
		if ((V[0] < -1.25) ||
			(V[0] >  0.50)) {
			return false; }
		boolean Cycl = (V[0] > -0.75); //test for a cycloid
		//the second Test reuses the real test!
		double sqrIm = sqr(V[1]);
		if (Cycl) {
			double shiftRe = V[0]-0.25; sqrIm += sqr(shiftRe);
			return sqr(sqrIm + sqrIm + shiftRe) < sqrIm; }
			return sqr(1 + V[0]) + sqrIm < 0.0625; } //1/16
	
	/** @return the abs Norm of the given complex Value */
	public static double AbsV(double[] V) {
		if (V[0] >= 0) {
			if (V[1] >= 0) { //even faster not to call abs()? Should be left to the Compiler!
				return  V[0] + V[1]; }
				return  V[0] - V[1]; }
			if (V[1] >= 0) { //
				return  V[1] - V[0]; }
				return -V[1] - V[0]; }
	
	/** @return the Apple Function x*x+c in Place */
	public static double[] AppleFuncAt (double[] x, double[] c) {
	//	x *= x;
	//	x += c; //x*x+c komplex !
		double x0x1 = x[0]*x[1];
		x[0] = c[0] + x[0]*x[0] - x[1]*x[1];
		x[1] = c[1] + x0x1      + x0x1; //replace Multiplication by Addition
		return x; }
	
	/** the Limit used to test the Value resulting from the Apple Recursion */
	final static public double Limit = 4;
	
	/**
	 * The Difference between Julia Set and Apple Set is that
	 * for Apple Sets both c and z are initialized to (x,y)
	 * for Julia Sets only z       is  initialized to (x,y)
	 * while c is a constant external Parameter
	 *
	 * Thus the Apple Set is the Set of all Parameters
	 * for which Julia Sets don't trivially explode.
	 * @return the Number of Iterations necessary for the Value to exceed the Limit
	 */
	public static int AppleDepth (double[] z, double[] c, int MaxDepth, boolean retSize) {
	//	System.arraycopy(c, 0, z, 0, 2) //z = c;
		int ret = 0;
		while (++ret < MaxDepth) { //Recourse the Function iteratively
			if (AbsV(AppleFuncAt(z, c)) > Limit) { //until it exceeds the Limit!
				return ret; }
		}
		if (retSize) { //evaluate the final Size...
			ret = (int)((-MaxDepth >> 1) * Math.log(sqr(z[0])+sqr(z[1])));
	//		System.out.println(ret);
			} //Ln (1+ MaxTiefe - Tiefe)
			return ret; }
	
	/*
	 IF KStart [2] THEN
	  BEGIN
	   IF KStart [1] THEN BEGIN ASM FINIT;FLD Schranke END; END;{Coprozessor setzen,geht leider nicht eher}
	   {Einmal fuer die gesamte Spalte setzen}
	   IF NOT drin AND NOT InOrJu THEN drin := c.re > mFuenfViertel;
	   IF     drin AND NOT Invert THEN drin := c.re < Halb;
	   IF     drin                THEN Cycl := c.re < mDreiViertel;
	  END;
	   IF drin
	    THEN
	     BEGIN
	      sqrIm:=Sqr (c.im);
	      IF     Cycl           THEN
	       Iter:=Sqr (c.re+Eins) + sqrIm > Sechzehntel;
	      IF NOT Cycl OR Invert THEN {Test auf Epizykloide}
	       BEGIN
	        shiftRe:=c.re-Viertel;sqrIm:=Sqr (shiftRe) + sqrIm;
	        Iter:=Sqr (sqrIm+sqrIm+shiftRe) > sqrIm
	       END
	     END
	    ELSE GOTO Start;
	   IF iter
	    THEN
	     BEGIN
	Start:IF Julia
	       THEN
	        ASM
	         FLD Julia_Par.im
	         FLD Julia_Par.re
	         FLD c.im
	         FLD c.re
	        END
	       ELSE
	        ASM
	         FLD c.im
	         FLD c.re
	         FLD ST (1) {z:=c}
	         FLD ST (1)
	        END;
	      ASM
	       MOV CX,MaxTiefe
	*/
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Maximum Depth for the Color in the AppleMan Diagram */
	protected int MaxDepth;

	/** Flag whether to return the Logarithm of the final Size when no Convergence */
	protected boolean retSize;

	/** Flag whether to calculate the Julia Set */
	protected boolean Julia;

	/** Current Complex Number */
	protected double[] c = new double[2];

	/** Complex Julia Parameter  */
	protected double[] z = new double[2];

	/** Complex Start Value  */
	protected double[] Start = new double[2];

	/** Complex Width Value  */
	protected double[] Width = new double[2];

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Initializing Constructor
	 * @param Start_ the Start Value of the Parameter Range
	 * @param StepWidth_ the Step Size of the Parameter Range
	 * @param JuliaParam_ the Julia Parameter. If null the AppleMan Set is calculated.
	 * @param MaxDepth_ the Maximum Depth up to which the Iteration is performed.
	 * @param boolean retSize_ switches returning the final Value when no Convergence.
	 */
	protected Apple(double[] StartValue_, double[] StepWidth_, double[] JuliaParam_, int MaxDepth_, boolean retSize_) {
		this.MaxDepth = MaxDepth_;
		this. retSize =  retSize_;
		if (this.Julia = (JuliaParam_  != null)) { //copy all to ensure Encapsulation
		System.arraycopy (JuliaParam_, 0, this.c    , 0, 2); }
		System.arraycopy (StartValue_, 0, this.Start, 0, 2);
		System.arraycopy ( StepWidth_, 0, this.Width, 0, 2);
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface : Implementation
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Useful for Generation of Pseudo random Numbers in multiple Dimensions.
	 * Generates a 2-dimensional integer Array with pseudorandom integer Numbers (Fractal).
	 * Maintains the range of Values (Minimum in x, Maximum in y)
	 * This Algorithm can easily be extended to work in any number of Dimensions and with float Point Values.
	 * @return the Color for the given AppleMan Point
	 */
	final public int getValue(Point2D SF) {
//		System.arraycopy (Start, 0, c, 0, 2); //copy the Values
		z[0] = Start[0] + SF.getX() * Width[0]; //use z as a Start Value
		z[1] = Start[1] + SF.getY() * Width[1];
		if (!Julia) { //use c as a Parameter (and the Start Value)
			if ((!retSize) && inAppleMan(z)) { //definitely inside
//				System.out.println("inAppleMan!");
				return 0; } //save Calculation of Depth!
			System.arraycopy (z, 0, c, 0, 2); } //i.e. use (0,0) as Start Value implicitly
		return AppleDepth (z, c, MaxDepth, retSize); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Apple.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

