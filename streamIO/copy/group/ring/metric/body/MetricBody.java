package streamIO.copy.group.ring.metric.body;

import streamIO.copy.ICopyAble;
import streamIO.copy.group.ring.metric.IMetricIRing;

/**
 * Defines Methods and Constants for analytical Operations
 * which are possibly overwritten by fast native Implementations like sin() etc.
 * <!-- docstate
 * tags: [code/rational_numbers, code/interval_arithmetic]
 * concepts: [Rational Numbers and Interval Arithmetic]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public interface MetricBody
extends IMetricIRing, Body {

	//////////////////////////////
	//	Trigonometric Constants	//
	//////////////////////////////

	/**Returns the Constant 2*Pi = 6,283185307179586476925286766559...
	 * This is the Quotient of Circumference and Radius of any circle.	 */
	public MetricBody twoPi();

	/**Returns the Constant 2*Pi = 6,283185307179586476925286766559... in Place
	 * This is the Quotient of Circumference and Radius of any circle.	 */
	public MetricBody twoPiAt();

	/**Returns the Constant Pi = 3,1415926535897932384626433832795...
	 * This is half the Quotient of Circumference and Radius of any circle.	 */
	public MetricBody pi();

	/**Returns the Constant Pi = 3,1415926535897932384626433832795... in Place
	 * This is half the Quotient of Circumference and Radius of any circle.	 */
	public MetricBody piAt();

	/**Returns the Constant Pi/2 = 1,5707963267948966192313216916398...	 */
	public MetricBody piHalf();

	/**Returns the Constant Pi/2 = 1,5707963267948966192313216916398... in Place	 */
	public MetricBody piHalfAt();

	/**Returns the Constant Pi/3 = 1,0471975511965977461542144610932...	 */
	public MetricBody piThird();

	/**Returns the Constant Pi/3 = 1,0471975511965977461542144610932... in Place	 */
	public MetricBody piThirdAt();

	/**Returns the Constant Pi/4 = 0,78539816339744830961566084581988...	 */
	public MetricBody piQuarter();

	/**Returns the Constant Pi/4 = 0,78539816339744830961566084581988... in Place	 */
	public MetricBody piQuarterAt();

	/**Returns the Constant Pi/6 = 0,52359877559829887307710723054658...	 */
	public MetricBody piSixth();

	/**Returns the Constant Pi/6 = 0,52359877559829887307710723054658... in Place	 */
	public MetricBody piSixthAt();

	//////////////////////////////
	//	Logarithmic Constants	//
	//////////////////////////////

	/**Returns the Constant e = exp(1) = 2.718281828459... 	 */
	public MetricBody e();

	/**Returns the Constant e = exp(1) = 2.718281828459... in Place	 */
	public MetricBody eAt();

	/**Returns the Constant lb(10) =  1/lg(2) = 3.321928094887...	 */
	public MetricBody lb10();

	/**Returns the Constant lb(10) = 1/lg(2) = 3.321928094887... in Place	 */
	public MetricBody lb10At();

	/**Returns the Constant ln(10) = 1/lg(e) = 2.302585092994...	 */
	public MetricBody ln10();

	/**Returns the Constant ln(10) = 1/lg(e) = 2.302585092994... in Place	 */
	public MetricBody ln10At();

	/**Returns the Constant lb(e) = 1/ln(2) 1.442695040889... 	 */
	public MetricBody lbe();

	/**Returns the Constant lb(e) = 1/ln(2) = 1.442695040889... in Place	 */
	public MetricBody lbeAt();

	/**Returns the Constant lg(2) = 1/lb(10) = 0,301029995664...	 */
	public MetricBody lg2();

	/**Returns the Constant lg(2) = 1/lb(10) = 0,301029995664... in Place	 */
	public MetricBody lg2At();

	/**Returns the Constant ln(2) = 1/lb(e) = 0,6931471805599... 	 */
	public MetricBody ln2();

	/**Returns the Constant ln(2) = 1/lb(e) = 0,6931471805599... in Place	 */
	public MetricBody ln2At();

	//////////////////////////////
	//	Trigonometric Functions	//
	//////////////////////////////

	/**Returns the Sinus of the angle x: sin(x)	 */
	public MetricBody sin();

	/**Returns the Sinus of the angle x in Place: sin(x)	 */
	public MetricBody sinAt();

	/**Returns the Cosinus of the angle x: cos(x)	 */
	public MetricBody cos();

	/**Returns the Cosinus of the angle x in Place: cos(x)	 */
	public MetricBody cosAt();

	/**Returns Cos(x)-1
	 * Gives better accuracy.	 */
	public MetricBody cosM1();

	/**Returns Cos(x)-1 in Place
	 * Gives better accuracy.	 */
	public MetricBody cosM1At();

	/**Returns the Tangens of the angle x: tan == sin / cos == sin/(1-sin^2)^1/2	*/
	public MetricBody tan();

	/**Returns the Tangens of the angle x in Place: tan == sin / cos == sin/(1-sin^2)^1/2	*/
	public MetricBody tanAt();

	/**Returns the CoTangens of the angle x: cot == cos / sin == (1-sin^2)^1/2/sin	 */
	public MetricBody cotAt();

	/**Returns the CoTangens of the angle x: cot == cos / sin == (1-sin^2)^1/2/sin	 */
	public MetricBody cot();

	/**Returns both the Sinus and Cosinus Hyperbolicus.
	 * This is more efficient, because cos^2+sin^2=1	 */
	public MetricBody Cos_Sin(ICopyAble Sin);

	//////////////////////
	//	Arcus Functions	//
	//////////////////////

	/**Returns the Arcus Sinus of the Angle x: ArcSin(x)	 */
	public MetricBody ArcSin();

	/**Returns the Arcus Sinus of the Angle x in Place: ArcSin(x)	 */
	public MetricBody ArcSinAt();

	/**Returns the Arcus Cosinus of the Angle x: ArcCos(x)	 */
	public MetricBody ArcCos();

	/**Returns the Arcus Cosinus of the Angle x in Place: ArcCos(x)	 */
	public MetricBody ArcCosAt();

	/**Returns the Arcus Tangens of the Angle x: ArcTan(x)	 */
	public MetricBody ArcTan();

	/**Returns the Arcus Tangens of the Angle x in Place: ArcTan(x)	 */
	public MetricBody ArcTanAt();

	/**Returns the angle in the full Range of -pi to pi
	 * that is given by the two coordinated x and y.
	 * The Condition x^2+y^2 = 1 needn't be fulfilled.	 */
	public MetricBody ArcTg (Object x);

	/**Returns the angle in the full Range of -pi to pi
	 * that is given by the two coordinated x and y in Place.
	 * The Condition x^2+y^2 = 1 needn't be fulfilled.	 */
	public MetricBody ArcTgAt (Object x);


	//////////////////////////////
	//	Exponential Functions	//
	//////////////////////////////

	/**Returns the exponential Function: e^x
	 * This is the Inverse to the natural Logarithm ln().
	 * For small Arguments |x| use expM1(x) to gain Accuracy.	 */
	public MetricBody exp();

	/**Returns the exponential Function in Place: e^x
	 * This is the Inverse to the natural Logarithm ln().
	 * For small Arguments |x| use expM1At(x) to gain Accuracy.	 */
	public MetricBody expAt();

	/**Returns the exponential Function: e^x - 1
	 * This Function is more accurate than exp for small Arguments |x|.
	 * Converges slowly for |x| > 1.	 */
	public MetricBody expM1();

	/**Returns the exponential Function in Place: e^x - 1
	 * This Function is more accurate than exp() for small Arguments |x|.
	 * Converges slowly for |x| > 1.	 */
	public MetricBody expM1At();

	/**Returns the binary exponential Function: 2^x
	 * This is the Inverse to the binary natural Logarithm lb().
	 * For small Arguments |x| use bxpM1(x) to gain Accuracy.	 */
	public MetricBody bxp();

	/**Returns the binary exponential Function in Place: e^x
	 * This is the Inverse to the binary natural Logarithm lb().
	 * For small Arguments |x| use bxpM1At(x) to gain Accuracy.	 */
	public MetricBody bxpAt();

	/**Returns the binary exponential Function: 2^x - 1
	 * This Function is more accurate than bxp() for small Arguments |x|.
	 * Converges slowly for |x| > 1.	 */
	public MetricBody bxpM1();

	/**Returns the binary exponential Function in Place: 2^x - 1
	 * This Function is more accurate than bxp() for small Arguments |x|.
	 * Converges slowly for |x| > 1.	 */
	public MetricBody bxpM1At();

	/**Returns the binary integer exponential Function: 2^n
	 * This is the Inverse to the binary integer natural Logarithm lb().	 */
//	public MetricBody nBxp(int n);

	/**Returns the exponential Function: 10^x
	 * This is the Inverse to the decadic Logarithm lg().
	 * For small Arguments |x| use dxpM1(x) to gain Accuracy.	 */
	public MetricBody dxp();

	/**Returns the decadic exponential Function in Place: 10^x
	 * This is the Inverse to the decadic Logarithm lg().
	 * For small Arguments |x| use dxpM1(x) to gain Accuracy.	 */
	public MetricBody dxpAt();

	/**Returns the decadic exponential Function: 10^x - 1
	 * This Function is more accurate than dxp() for small Arguments |x|.
	 * Converges slowly for |x| > 1.	 */
	public MetricBody dxpM1();

	/**Returns the decadic exponential Function in Place: 10^x - 1
	 * This Function is more accurate than dxp() for small Arguments |x|.
	 * Converges slowly for |x| > 1.	 */
	public MetricBody dxpM1At();

	/**Returns this number raised to the Power of arg: this^arg
	 * For small Arguments |x| use PowM1(arg) to gain Accuracy.	 */
	public MetricBody Pow(Object arg);

	/**Returns this number raised to the Power of arg in Place: this^arg
	 * For small Arguments |x| use PowM1(arg) to gain Accuracy.	 */
	public MetricBody PowAt(Object arg);

	/**Returns this number raised to the Power of arg: this^arg - 1
	 * This Function is more accurate than Pow() for small Arguments |arg|.
	 * Converges slowly for |arg| > 1.	 */
	public MetricBody PowM1(Object arg);

	/**Returns this number raised to the Power of arg in Place: this^arg - 1
	 * This Function is more accurate than Pow() for small Arguments |arg|.
	 * Converges slowly for |arg| > 1.	 */
	public MetricBody PowM1At(Object arg);

	//////////////////////////////
	//	Hyperbolic Functions	//
	//////////////////////////////

	/**Returns the Cosinus Hyperbolicus of this Number	*/
	public MetricBody CosH();

	/**Returns the Cosinus Hyperbolicus of this Number in Place	*/
	public MetricBody CosHAt();

	/**Returns CosH(x)-1
	 * Gives better accuracy.	 */
	public MetricBody cosHm1();

	/**Returns CosH(x)-1 in Place
	 * Gives better accuracy.	 */
	public MetricBody cosHm1At();

	/**Returns the Sinus Hyperbolicus of this Number	*/
	public MetricBody SinH();

	/**Returns the Sinus Hyperbolicus of this Number in Place	*/
	public MetricBody SinHAt();

	/**Returns the Tangens Hyperbolicus of this Number	*/
	public MetricBody TanH();

	/**Returns the Tangens Hyperbolicus of this Number in Place	*/
	public MetricBody TanHAt();

	/**Returns the CoTangens Hyperbolicus of this Number	*/
	public MetricBody CotH();

	/**Returns the CoTangens Hyperbolicus of this Number in Place	*/
	public MetricBody CotHAt();

	/**Returns both the Sinus and Cosinus Hyperbolicus.
	 * This is more efficient, because cosH^2-sinH^2=1	 */
	public MetricBody CosH_SinH(ICopyAble SinH);


	//////////////////////
	//	Area Functions	//
	//////////////////////

	/**Returns the Area Cosinus Hyperbolicus of this Number
	 * This is the Inverse to CosH	*/
	public MetricBody ArCosH();

	/**Returns the Area Cosinus Hyperbolicus of this Number in Place
	 * This is the Inverse to CosH	 */
	public MetricBody ArCosHAt();

	/**Returns the Area Sinus Hyperbolicus of this Number
	 * This is the Inverse to SinH	 */
	public MetricBody ArSinH();

	/**Returns the Area Sinus Hyperbolicus of this Number in Place
	 * This is the Inverse to SinH	 */
	public MetricBody ArSinHAt();

	/**Returns the Area Tangens Hyperbolicus of this Number
	 * This is the Inverse to TanH	 */
	public MetricBody ArTanH();

	/**Returns the Area Tangens Hyperbolicus of this Number in Place
	 * This is the Inverse to TanH	 */
	public MetricBody ArTanHAt();


	//////////////////////////////
	//	Logarithmic Functions	//
	//////////////////////////////

	/**Returns the natural Logarithm of x: ln(x)
	 * This is the Inverse to the exponential Function exp(x).
	 * For Arguments x near 1 use lnXP1(x) to gain Accuracy.	 */
	public MetricBody ln();

	/**Returns the natural Logarithm of x in Place: ln(x)
	 * This is the Inverse to the exponential Function exp(x).
	 * For Arguments x near 1 use lnXP1(x) to gain Accuracy.	 */
	public MetricBody lnAt();

	/**Returns the natural binary Logarithm of x+1: ln(x+1)
	 * This is the Inverse to the exponential Function expM1(x).
	 * This Function is more accurate for Arguments near 1 than ln().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody lnXP1();

	/**Returns the natural binary Logarithm of x+1 in Place: ln(x+1)
	 * This is the Inverse to the exponential Function expM1(x).
	 * This Function is more accurate for Arguments near 1 than ln().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody lnXP1At();

	/**Returns the natural binary Logarithm of x: lb(x)
	 * This is the Inverse to the binary exponential Function bxp(x).
	 * For Arguments x near 1 use lbXP1(x) to gain Accuracy.	 */
	public MetricBody lb();

	/**Returns the natural binary Logarithm of x in Place: lb(x)
	 * This is the Inverse to the binary exponential Function bxp(x).
	 * For Arguments x near 1 use lbXP1(x) to gain Accuracy.	 */
	public MetricBody lbAt();

	/**Returns the natural binary Logarithm of x+1: lb(x+1)
	 * This is the Inverse to the exponential Function bxpM1(x).
	 * This Function is more accurate for Arguments near 1 than lb().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody lbXP1();

	/**Returns the natural binary Logarithm of x+1 in Place: lb(x+1)
	 * This is the Inverse to the exponential Function bxpM1(x).
	 * This Function is more accurate for Arguments near 1 than lb().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody lbXP1At();

	/**Returns the natural decadic Logarithm of x: lg(x)
	 * This is the Inverse to the decadic exponential Function dxp(x).
	 * For Arguments x near 1 use lbXP1(x) to gain Accuracy.	 */
	public MetricBody lg();

	/**Returns the natural decadic Logarithm of x in Place: lg(x)
	 * This is the Inverse to the decadic exponential Function dxp(x).
	 * For Arguments x near 1 use lbXP1(x) to gain Accuracy.	 */
	public MetricBody lgAt();

	/**Returns the natural decadic Logarithm of x+1: lg(x+1)
	 * This is the Inverse to the decadic exponential Function dxpM1(x).
	 * This Function is more accurate for Arguments near 1 than lg().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody lgXP1();

	/**Returns the natural decadic Logarithm of x+1: lg(x+1)
	 * This is the Inverse to the decadic exponential Function dxpM1(x).
	 * This Function is more accurate for Arguments near 1 than lg().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody lgXP1At();

	/**Returns the Logarithm of this number to the Basis of arg: Log(arg)this
	 * This is the Inverse to PowAt (arg).
	 * For small Arguments |x| use LogXP1(arg) to gain Accuracy.	 */
	public MetricBody Log(Object arg);

	/**Returns the Logarithm of this number to the Basis of arg in Place: Log(arg)this
	 * This is the Inverse to Pow (arg).
	 * For small Arguments |x| use LogXP1(arg) to gain Accuracy.	 */
	public MetricBody LogAt(Object arg);

	/**Returns the Logarithm of (x+1) to the Basis of arg: Log(arg)(x+1)
	 * This is the Inverse to PowM1 (arg).
	 * This Function is more accurate for Arguments near 1 than ln().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody LogXP1(Object arg);

	/**Returns the Logarithm of (x+1) to the Basis of arg in Place: Log(arg)(x+1)
	 * This is the Inverse to PowM1At (arg).
	 * This Function is more accurate for Arguments near 1 than ln().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody LogXP1At(Object arg);

	/**Langevin Function:	L (x) = CosH (x) - 1/x	 */
//	public MetricBody Langevin  ();

	/**Brillouin Function:	lim (j -> infin) B (j,x) = L (x)	 */
//	public MetricBody Brillouin (MetricBody j);

	/**Sinc-Function :      Sin  (x)/x	 */
//	public MetricBody Sinc();

	/**Airy-Function with Finesse F = 1/(1+Sin^2)	 */
//	public MetricBody Airy();

	/**Airy-Function with Finesse F: 1/(1+F*Sin^2 (x/2))
	 * when the Finesse is null, it is assumed to 1.	 */
//	public MetricBody Airy(Object F);

	/**Not normalized Bell Curve: e^-(x^2)
	 * Fastest Implementation with Gauss Characteristics, not normed 	 */
	public MetricBody  GaussAt();

	/**Not normalized Bell Curve: e^-(x^2)
	 * Fastest Implementation with Gauss Characteristics, not normed 	 */
	public MetricBody  Gauss();

	/**Not normed Lorentz Curve: 1/(1+x^2)
	 * Fastest Implementation with Lorentz Characteristics, not normed 	 */
	public MetricBody  LorentzAt();

	/**Not normed Lorentz Curve: 1/(1+x^2)
	 * Fastest Implementation with Lorentz Characteristics, not normed 	 */
	public MetricBody  Lorentz();

	/**Normed Sigmoid Curve: 1/(1+e^-x)
	 * Fast, smooth normed Implementation of an integrated Delta Function	 */
	public MetricBody  SigmoidAt();

	/**Normed Sigmoid Curve: 1/(1+e^-x)
	 * Fast, smooth normed Implementation of an integrated Delta Function	 */
	public MetricBody  Sigmoid();

	/**Fast, but unsmooth Representation of Delta as a Rectangle Function */
//	public MetricBody Delta1(Object H);

	/**Fast, but unsmooth Representation of Delta as a Rectangle Function */
//	public MetricBody Delta1At(Object H);

	/**Smooth, sharp, but expensive Representation of Delta as a Gauss Function,
	 * If H is null (not given), it is assumed to 1.
	 * The Width is proportional to 1/H, the Height to H.
	 * To get the original Gauss Function, use H = 1/SqRt(2*Pi)	 */
	public MetricBody  Delta2(Object H);

	/**Smooth, sharp, but expensive Representation of Delta as a Gauss Function,
	 * If H is null (not given), it is assumed to 1.
	 * The Width is proportional to 1/H, the Height to H.
	 * To get the original Gauss Function, use H = 1/SqRt(2*Pi)	 */
	public MetricBody  Delta2At(Object H);

	/**Smooth, fuzzy, but inexpensive Representation of Delta as a Lorentz Curve
	 * The Width is proportional to 1/H, the Height to H.	 */
	public MetricBody  Delta3(Object H);

	/**Smooth, fuzzy, but inexpensive Representation of Delta as a Lorentz Curve
	 * The Width is proportional to 1/H, the Height to H.	 */
	public MetricBody  Delta3At(Object H);

	/**Smooth, sharp, a bit expensive Representation of Delta as a Sigmoid Curve
	 * The Width is proportional to 1/H, the Height to H.	 */
	public MetricBody  Delta4(Object H);

	/**Smooth, sharp, a bit expensive Representation of Delta as a Sigmoid Curve
	 * The Width is proportional to 1/H, the Height to H.	 */
	public MetricBody  Delta4At(Object H);

	/**Continuous, sharp, but at the Corners not differentiable and a bit expensive
	 * Representation of Delta as a Cosinus Curve.
	 * The Width is proportional to 1/H, the Height to H.	 */
	public MetricBody  Delta5(Object H);

	/**Continuous, sharp, but at the Corners not differentiable and a bit expensive
	 * Representation of Delta as a Cosinus Curve.
	 * The Width is proportional to 1/H, the Height to H.	 */
	public MetricBody  Delta5At(Object H);

}
