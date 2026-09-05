package function;

import math.vector.VectorDouble;

/**Provides {@code double}/{@code float} conversion and a library of floating-point math
 * constants shared by every measurable, countable and orderable numeric type in this package.
 *
 * <p>All IMeasurAble Classes can be converted to these Float Types,
 * and also (with Rounding Errors) to the Integer Types in {@link ICountAble}.
 *
 * Contains only Constants in Double Precision:
 * Double   2.2*10^- 308...1.8*10^ 308 (.5*2^-1023...2^1023)  8-Byte  15-16
 *
 * Double-Properties:
 * Bits:     64 = 8*8Byte
 * Mantissa: 52 = 8*6Byte + 4 Bit  => 53 Bits ^ 16 Digits Accuracy
 * Exponent: 11 = 8*1Byte + 3 Bit  => 11 Bits ^ +/- 307 Exponent
 * Sign:      1 =           1 Bit
 * abs.Range: 4.9e-324 to 1.7976931348623157e+308
 *
 * Float-Properties:
 * Bits:     32 = 8*4Byte
 * Mantissa: 23 ~ 8*3Byte  => 23 Bits ^ 7 Digits Accuracy
 * Exponent:  8 = 8*1Byte  =>  8 Bits ^ +/- 38 Exponent
 * Sign:      1 =   1 Bit
 * abs.Range: 1.5e-45 to 3.4028235e+38
 *
 * This class does not extend Number, because not every Group maps to numeric Values.
 * Instead it presents the conversion Routine to convert from Number Types.
 * This is unfortunately necessary, because Number does not implement an Interface.
 *
 * The 'extended' Type (from Delphi) is not supported.
 * It allows saving intermediate Result with full accuracy.
 * It consists of an IEEE Number with 64 Bit (10 Byte):
 * 64 Bit Mantissa, 15 Bit Exponent, 1 Bit Sign
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:30:14Z
 * digest: 1ed79c91c57804bffa8a25f6b669f6b6f2d76471f5cbe761cc8d4c818e4c43b9
 * stale: false
 * tags: [code/function_contract, code/function_composition]
 * concepts: [Function/Relation Contract]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 * The Problem is that static Methods cannot be declared in Interfaces */
public interface IMeasurAble
extends IOrderAble //maybe it is a bad idea to enforce Implementation of these Methods! 
{

	////////////////////////////////////////////////////////////////////////////
	//  Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**Returns the Object Value represented by a scalar Variable of Type double.
	 * It consists of an IEEE Number with 64 Bit (8 Byte):
	 * 52 Bit Mantissa, 11 Bit Exponent, 1 Bit Sign	 */
	double getDouble();

	/**Returns the Object Value represented by a scalar Variable of Type float.
	 * It consists of an IEEE Number with 32 Bit (4 Byte):
	 * 23 Bit Mantissa, 8 Bit Exponent, 1 Bit Sign	 */
	float  getFloat();

	////////////////////////////////////////////////////////////////////////////
	//  static Rational Constants:
	////////////////////////////////////////////////////////////////////////////

	/** The fraction one half (0.5). */
	final static public float  HALF   = 1.0f/2; //ICountAble.ONE/ICountAble.TWO;
	/** The fraction one third. */
	final static public double THIRD  = 1.0 /3; //ICountAble.ONE/ICountAble.THREE;
	/** The fraction one quarter (0.25). */
	final static public float  QUARTER= 1.0f/4; //ICountAble.ONE/ICountAble.FOUR;

	/** One percent (0.01), for converting percentage values to a fraction. */
	final static public double PERCENT  = 1.0/100 ; //ICountAble.ONE/ICountAble.HUNDRED;	//For Perccent Values
	/** One permille (0.001), for converting permille values to a fraction. */
	final static public double PERMILLE = 1.0/1000; //ICountAble.ONE/ICountAble.THOUSAND;	//For Permille Values

	/** Over-relaxation factor (1.1) used by iterative solvers. */
	final static public float EINSK1	= 1.1f; 	//for Over -Relaxation
	/** Under-relaxation factor (0.9) used by iterative solvers. */
	final static public float NULLK9	= 0.9f; 	//for Under-Relaxation
	/** Small relaxation step factor (0.1). */
	final static public float NULLK1	= 0.1f;

	/** Positive infinity, computed as 1.0f/0 rather than {@link Float#POSITIVE_INFINITY}. */
	final static public float  INFINITY	=  1.0f/0; //(ICountAble. ONE/ICountAble.ZERO);
	/** Negative infinity, computed as -1.0f/0 rather than {@link Float#NEGATIVE_INFINITY}. */
	final static public float _INFINITY	= -1.0f/0; //(float) (ICountAble._ONE/ICountAble.ZERO);
	/** Not-a-number, computed as 0.0f/0 rather than {@link Float#NaN}. */
	final static public float  NAN		=  0.0f/0; //(float) (ICountAble.ZERO/ICountAble.ZERO);
	
	////////////////////////////////////////////////////////////////////////////
	//  static Rational Constant Objects:
	////////////////////////////////////////////////////////////////////////////
	
	/** Boxed form of {@link #HALF}. */
	final static public Double Half		= new Double(HALF);
	/** Boxed form of {@link #THIRD}. */
	final static public Double Third	= new Double(THIRD);
	/** Boxed form of {@link #QUARTER}. */
	final static public Double Quarter	= new Double(QUARTER);

	/** Boxed form of {@link #PERCENT}. */
	final static public Double Percent	= new Double(PERCENT);				//For Prozentangaben
	/** Boxed form of {@link #PERMILLE}. */
	final static public Double Permille	= new Double(PERMILLE);			   //For Promilleangaben

	/** Boxed form of {@link #EINSK1}. */
	final static public Double EinsK1	= new Double(EINSK1);				  //for Over -Relaxation
	/** Boxed form of {@link #NULLK9}. */
	final static public Double NullK9	= new Double(NULLK9);				  //for Under-Relaxation
	/** Boxed form of {@link #NULLK1}. */
	final static public Double NullK1	= new Double(NULLK1);

	/** Boxed form of {@link #INFINITY}. */
	final static public Double  Infinity	= new Double( INFINITY);
	/** Boxed form of {@link #_INFINITY}. */
	final static public Double _Infinity	= new Double(_INFINITY);

	////////////////////////////////////////////////////////////////////////////
	//  static Transcendental Constants:
	////////////////////////////////////////////////////////////////////////////

	/** Natural logarithm of 2 (approx. 0.693147). */
	final static public double LN2	= Math.log(ICountAble.TWO);	//0,69314718055994530941723212145818;      nat?rlicher Logarithmus von 2=1/Lbe
	/** Binary logarithm of e, i.e. 1/{@link #LN2} (approx. 1.442695). */
	final static public double LBE	= ICountAble.ONE/LN2;	//1,4426950408889634073599246810019;      Binaerer Logarithmus von e (s.u.)
	/** Natural logarithm of 10 (approx. 2.302585). */
	final static public double LN10	= Math.log(10);	//2,3025850929940456840179914546844
//	final static public double LN10 = Lb10/Lbe;	//define it like this for fast binary Logarithms
	/** Binary logarithm of 10 (approx. 3.321928). */
	final static public double LB10	= LN10/LN2;	//3,3219280948873623478703194294894;      Binaerer Logarithmus von 10

	/**Decadic Logarithm of two LG2	= 0,30102999566398119521373889472449	 */
	final static public double LG2	= ICountAble.ONE/LB10;	//0,30102999566398119521373889472449;      dekadischer Logarithmus von 2=

	/**Kreiszahl Pi = 3.1415926535897932384626433832795*/
	final static public double PI		= 3.1415926535897932384626433832795; //Math.PI;

	/**Eulersche Zahl e = 2.7182818284590452353602874713527	 */
	final static public double E		= 2.7182818284590452353602874713527; //Math.E;

	/**Eulersche Zahl e^2 = 7.389056098930650227230427460575	 */
	final static public double E2		= E*E;

	/**Eulersche Zahl e^2*Pi = 23.213404357363387236150345896007	 */
	final static public double E2PI		= E2*PI;

	/**Eulersche-Mascheroni-Konstante	 */
	final static public double EULER_C	= 0.57721566490152870;

	/**Feigenbaum-Delta :
	 * Grenzwert der Verhaeltnisse der Abstaende
	 * von quadratischen Bifurkationen*.	 */
	final static public double FEIGEN = 4.669201660910299097;
	/** Negative Pi. */
	final static public double _PI = -PI;
	/** Pi divided by four (45 degrees in radians). */
	final static public double PI_QUARTER = PI*QUARTER;
	/** Pi divided by two (90 degrees in radians). */
	final static public double PI_HALF = PI*HALF;
	/** Three quarters of Pi (135 degrees in radians). */
	final static public double THREE_PI_QUARTER = 3*PI_QUARTER;
	/** Two times Pi, a full circle in radians. */
	final static public double TWO_PI  = PI*ICountAble.TWO;
	/** A full circle expressed in gon (400). */
	final static public double FULL_GON= 400;
	/** A full circle expressed in degrees (360). */
	final static public double FULL_DEG= 360;
	/** Radians per degree, for converting degrees to radians. */
	final static public double GRAD   = TWO_PI/FULL_DEG;	//Zur Umrechnung in andere Winkelsysteme
	/** Radians per gon, for converting gon to radians. */
	final static public double GON    = TWO_PI/FULL_GON;
	/** Cube root of 2 (approx. 1.259921). */
	final static public double CBCRT2 =  1.2599210498948731647672106072782; //Bxp  (Drittel);
//	final static public double CBCRT2 = (ONE/Sqr (CbcRt2)+CbcRt2)*TWO/THREE; //NachIteration fuer letzte Bits}
//	final static public double SQRT2  = (TWO/SqRt2+SqRt2)*HALF;  //Nachiteration fuer letzte Bits
	/** Square root of 2. */
	final static public double SQRT2  = Math.sqrt(2.0); //ICountAble.TWO);
	/** Square root of 3. */
	final static public double SQRT3  = Math.sqrt(3.0); //ICountAble.THREE);
	/** Square root of 5. */
	final static public double SQRT5  = Math.sqrt(5.0); //ICountAble.FIVE);
	/** Square root of Pi. */
	final static public double SQRTPI = Math.sqrt(PI);
	/** Square root of 2*Pi. */
	final static public double SQRT2PI= SQRTPI * SQRT2;
	/** Square of {@link #CBCRT2}. */
	final static public double SQRCBCRT2 = CBCRT2*CBCRT2;
	/** The golden ratio's reciprocal, (sqrt(5)-1)/2, approx. 0.618. */
	final static public double      GOLDEN = (SQRT5-1.0)*0.5;//(SqRt (5)-1)/2=0.618 Verhaeltnis des goldenen Schnittes
	/** The complement of {@link #GOLDEN}, approx. 0.382. */
	final static public double     CGOLDEN = 1-GOLDEN;//= 0.3819 ,dessen Komplement und der
	/** One plus {@link #GOLDEN}, the golden ratio, approx. 1.618. */
	final static public double   ONEGOLDEN = 1+GOLDEN;//= 1.681 Faktor zur Vergroesserung e. Intervalles
	/** Natural logarithm of {@link #ONEGOLDEN}. */
	final static public double LNONEGOLDEN = Math.log(ONEGOLDEN);
	/** Small value used as a floating-point comparison tolerance (2e-16). */
	final static public double EPSILON    = 2e-16; //2 ^ Genauigkeit;      //'Kleine' Groesse im Vergleich zu 1

	////////////////////////////////////////////////////////////////////////////
	//  static Transcendental Constant Objects:
	////////////////////////////////////////////////////////////////////////////

	/** Boxed form of {@link #LN2}. */
	final static public Double Ln2	= new Double(LN2);	//.69314718055994530941;	  nat?rlicher Logarithmus von 2=1/Lbe
	/** Boxed form of {@link #LBE}. */
	final static public Double Lbe	= new Double(LBE);	//1.4426950408889634073;	  Bin�rer Logarithmus von e (s.u.)
	/** Boxed form of {@link #LN10}. */
	final static public Double Ln10	= new Double(LN10);	//2.302585093	//Lb10/Lbe;
	/** Boxed form of {@link #LB10}. */
	final static public Double Lb10	= new Double(LB10);	//Ln10/Ln2;	//3.3219280948873623479;	  Bin�rer Logarithmus von 10

	/**Decadic Logarithm of two Lg2	= 0,30102999566398119521373889472449	 */
	final static public Double Lg2	= new Double(LG2);	//.30102999566398119519;	  dekadischer Logarithmus von 2=
	/**Kreiszahl Pi = 3.1415926535897932386	 */
	final static public Double pi	= new Double(PI);
	/**Eulersche Zahl e = 2.71828182845904523536	 */
	final static public Double e	= new Double(E);
	/**Eulersche Zahl e^2 = 7.389056098930650227230427460575	 */
	final static public Double e2	= new Double(E2);
	/**Eulersche Zahl e^2*Pi = 23.213404357363387236150345896007	 */
	final static public Double e2Pi	= new Double(E2PI);
	/**Eulersche-Mascheroni-Konstante	 */
	final static public Double EulerC= new Double(EULER_C);	//0.57721566490152870;

	/**Feigenbaum-Delta :
	 * Grenzwert der Verhaeltnisse der Abstaende
	 * von quadratischen Bifurkationen.	 */
	final static public Double Feigen = new Double(FEIGEN);
	/** Boxed form of {@link #_PI}. */
	final static public Double _Pi    = new Double(-PI);
	/** Boxed form of {@link #PI_HALF}. */
	final static public Double PiHalf = new Double(PI_HALF);
	/** Boxed form of {@link #PI_QUARTER}. */
	final static public Double PiQuarter = new Double(PI_QUARTER);
	/** Boxed form of {@link #THREE_PI_QUARTER}. */
	final static public Double ThreePiQuarter = new Double(THREE_PI_QUARTER);
	/** Boxed form of {@link #TWO_PI}. */
	final static public Double TwoPi  = new Double(TWO_PI);
	/** Boxed form of {@link #FULL_GON}. */
	final static public Double FullGon= new Double(FULL_GON);
	/** Boxed form of {@link #FULL_DEG}. */
	final static public Double FullDeg= new Double(FULL_DEG);
	/** Boxed form of {@link #GRAD}. */
	final static public Double Grad   = new Double(GRAD);	//Zur Umrechnung in andere Winkelsysteme
	/** Boxed form of {@link #GON}. */
	final static public Double Gon    = new Double(GON);
	/** Boxed form of {@link #SQRT2}. */
	final static public Double SqRt2  = new Double(SQRT2);
	/** Boxed form of {@link #CBCRT2}. */
	final static public Double CbcRt2 = new Double(CBCRT2); //Bxp  (Drittel);
	/** Boxed form of {@link #SQRT3}. */
	final static public Double SqRt3  = new Double(SQRT3);
	/** Boxed form of {@link #SQRTPI}. */
	final static public Double SqRtPi = new Double(SQRTPI);
	/** Boxed form of {@link #SQRT2PI}. */
	final static public Double SqRt2Pi= new Double(SQRT2PI);
	/** Boxed form of {@link #SQRT5}. */
	final static public Double SqRt5  = new Double(SQRT5);
	/** Boxed form of {@link #GOLDEN}. */
	final static public Double	Golden= new Double(GOLDEN);//(SqRt (5)-1)/2=0.681 Verhaeltnis des goldenen Schnittes
	/** Boxed form of {@link #CGOLDEN}. */
	final static public Double   cGolden	= new Double(CGOLDEN);//1 - Golden = 0.319 ,dessen Komplement und der
	/** Boxed form of {@link #ONEGOLDEN}. */
	final static public Double OneGolden	= new Double(ONEGOLDEN);//1 + Golden = 1.681 Faktor zur Vergroesserung e. Intervalles
	/** Boxed form of {@link #LNONEGOLDEN}. */
	final static public Double LnOneGolden	= new Double(LNONEGOLDEN);
	/** Boxed form of {@link #SQRCBCRT2}. */
	final static public Double  SqrCbcRt2	= new Double(SQRCBCRT2);
	/** Boxed form of {@link #EPSILON}. */
	final static public Double Epsilon		= new Double(EPSILON); //2 ^ Genauigkeit;	  //'Kleine' Groesse im Vergleich zu 1

	////////////////////////////////////////////////////////////////////////////
	//  Precision Constants for Float Pount Types
	////////////////////////////////////////////////////////////////////////////

	/**Default Maximum Number of Iterations of Algorithms
	 * If an Algorithm is expected to converge slow or fast,
	 * it should use a derived Number of Iterations.	 */
	final static public int MAX_ITER = 100;
	
	/**Number of Bits in the Mantissa of a double Precision Number	 */
	final static public byte DOUBLE_MANTISSA_BITS = 52;	//6*8 + 4 Bit	=> 53 Bits ^ 16 Digits Accuracy

	/**Number of Bits in the Mantissa of a double Precision Number	 */
	final static public byte DOUBLE_MANTISSA_DIGITS = (byte) (DOUBLE_MANTISSA_BITS*LG2);	//16 Digits Accuracy

	/**Number of Bits in the Exponent of a double Precision Number	 */
	final static public byte DOUBLE_EXPONENT_BITS = 11;	//1*8 + 3 Bit	=> 11 Bits ^ +/- 307 Exponent

	/**Number of Bits in the Mantissa of a float Number	 */
	final static public byte FLOAT_MANTISSA_BITS = 23;	//2*8 + 7 Bit	=> 23 Bits ^ 8 Digits Accuracy

	/**Number of Bits in the Exponent of a float Number	 */
	final static public byte FLOAT_EXPONENT_BITS = 8;	//1*8 Bit	=> 8 Bits ^ +/- 38 Exponent
	
	/**Accuracy used for Calculations with float Numbers,
	 * the full Accuracy, which is 1e-9	 */
	final static public float FLOAT_FULL_ACCURACY = (float) Math.exp(-(FLOAT_MANTISSA_BITS >> 1) * LN2);

	/**Accuracy used for Calculations with float Numbers,
	 * not the full Accuracy, which is 1e-9, but only about its Square Root: 3e-5	 */
	final static public float FLOAT_ACCURACY = (float) Math.sqrt(FLOAT_FULL_ACCURACY); //Math.exp(-(FLOAT_MANTISSA_BITS >> 1) * LN2);

	/**Accuracy used for Calculations with double Numbers,
	 * the full Accuracy, which is 1e-16	 */
	final static public double DOUBLE_FULL_ACCURACY = Math.exp(-DOUBLE_MANTISSA_BITS * LN2);

	/**Accuracy used for Calculations with double Numbers,
	 * not the full Accuracy, which is 1e-16, but only about the Square Root: 1e-8 	 */
	final static public double DOUBLE_ACCURACY  = Math.sqrt(DOUBLE_FULL_ACCURACY); //Math.exp(-(DOUBLE_MANTISSA_BITS >> 1) * LN2);

	/**Half the Maximum Value of the Type 'double' = DOUBLE_MAX_VALUE	 */
	final static public double DOUBLE_OVERFLOW = Math.exp((1 << (DOUBLE_EXPONENT_BITS - 1)) * LN2);

	/**Half the Maximum Value of the Type 'float' 3e38 = FLOAT_MAX_VALUE	 */
	final static public double FLOAT_OVERFLOW = Math.exp((1 << (FLOAT_EXPONENT_BITS - 1)) * LN2);

	/**	Maximum absolute Value for double Numbers	*/
	final static public double	DOUBLE_MAX_VALUE = 1.7976931348623157e+308;

	/**	Minimum absolute Value for double Numbers, about 1e-323	*/
	final static public double	DOUBLE_MIN_VALUE = 4.9e-324 ;

	/**	Maximum absolute Value for float Numbers	*/
	final static public float	FLOAT_MAX_VALUE = 3.4028235e+38f;

	/**	Minimum absolute Value for float Numbers, about 1e-44	*/
	final static public float	FLOAT_MIN_VALUE = 1.5e-45f;

	/** Coefficients for the fast Gamma Function Approximation	 */
	final static public double[]
	COEFF_GAMMA= {  1.000000000190015,
					 76.18009172947146,
					-86.50532032941677,
					 24.01409824083091,
					 -1.231739572450155,
					  0.1208650973866179e-2,
					 -0.5395239384953e-5};

	/**Values of the Zeta-Function at Positions 2 to 32;
	 * for calculating the Gamma-Funktion, see dtv-Atlas Mathematik:Bd.2 S 310/311
	 */
	final static public double[]
	ZetaValues= {	0.0, 0.0, //starts with 2
					1.64493406684822643657,	1.2020569031595892854,	1.0823232337111381916,
					1.0369277551433699263,	1.0173430619844491398,	1.0083492773819228268,
					1.004077356197944340,	1.002008392826082210,	1.000994575127818090,
					1.000494188604119460,	1.000246086553308050,	1.000122713347578490,
					1.0000612481350587,		1.00003058823630702,	1.00001528225940865,
					1.0000076371976379,		1.000003817293265,		1.00000190821271655,
					1.00000095396203387,	1.00000047693298679,	1.00000023845050273,
					1.00000011921992597,	1.00000005960818905,	1.00000002980350351,
					1.00000001490155483,	1.00000000745071179,	1.00000000372533402,
					1.00000000186265972,	1.00000000093132743,	1.00000000046566291,
					1.00000000023283118};

	/**Coefficients of the Zeta Function Approximation   */
	final static public double[]
	CoeffZeta={0.0, -7.2815845E-2, -9.6903E-3,
				 2.054E-3, 2.34E-3, 1.2E-3, 1E-2, 3E-1, 7};

	/**Coefficients for the fast Gamma Function Approximation	 */
	final static public Double[] CoeffGamma = VectorDouble.const2Const(COEFF_GAMMA);

}
