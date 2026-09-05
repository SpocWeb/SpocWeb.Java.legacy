package streamIO.copy.group.ring.metric.body;

import streamIO.copy.ICopyAble;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.metric.CMetricIRing;
import streamIO.exception.ReadOnlyException;
import function.ICountAble;
import function.IMeasurAble;

/** Creates constant Representatives of the Constructed Classes
  * by overwriting their addAt() etc. Methods to throw Exceptions.
  * The Instances can be used in add(), subt() etc. Methods,
  * but not in any ...At() Method!
  *
  * Design Decisions:
  * Cannot derive this Class from two Base Classes,
  * so either AMetricIRing or ConstRing would be the Parent
  * and I rather inherit the Implementations of AMetricIRing for now...
  * As an Optimization later all the Methods will be delegated to inner
  * <!-- docstate
  * tags: [code/rational_numbers, code/interval_arithmetic]
  * concepts: [Rational Numbers and Interval Arithmetic]
  * facets: {layer: domain, status: legacy, complexity: high}
  * -->
  * and can then be derived from ConstRing.	 */
public class CBody
extends CMetricIRing
implements MetricBody, ICountAble
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Initializing Constructor		 */	public CBody(MetricBody cnst){super(cnst); }

	//////////////////////////////
	//	interface IMeasurAble	//
	//////////////////////////////

	/**Returns the Object Value represented by a scalar Variable of Type double.
	 * It consists of an IEEE Number with 64 Bit (8 Byte):
	 * 52 Bit Mantissa, 11 Bit Exponent, 1 Bit Sign	 */
	public double getDouble(){return ((IMeasurAble) inner).getDouble(); }

	/**Returns the Object Value represented by a scalar Variable of Type float.
	 * It consists of an IEEE Number with 32 Bit (4 Byte):
	 * 23 Bit Mantissa, 8 Bit Exponent, 1 Bit Sign	 */
	public float  getFloat(){return ((IMeasurAble) inner).getFloat(); }


	//////////////////////////////
	//	interface ICountAble	//
	//////////////////////////////

	/** Returns the Object Value represented by an 8 Bit Integer	 */
	public byte	 getByte(){return ((ICountAble) inner).getByte(); }

	/**Returns the Object Value represented by a 16 Bit Integer	 */
	public short getShort(){return ((ICountAble) inner).getShort(); }

	/**Returns the Object Value represented by a 32 Bit Integer	 */
	public int	 getInt(){return ((ICountAble) inner).getInt(); }

	/**Returns the Object Value represented by a 64 Bit Integer	 */
	public long  getLong(){return ((ICountAble) inner).getLong(); }

	//////////////////////////////////////////////////////
	//	Operations dealing with Separation to Integers	//
	//////////////////////////////////////////////////////

	/**Returns the Fractional Part of this Number in Place,
	 * and the integer Part in Object.	 */
	public MetricBody FracAtIntAt(ICopyAble Int) {throw new ReadOnlyException(strConst); }

	/**Returns the Fractional Part of this Number,
	 * and the integer Part in Object.	 */
	public IIntRing FracIntAt(IIntRing Int){ return ((MetricBody) inner).FracIntAt(Int); }

	/**Returns the Fractional Part of a float Number in Place.	 */
	public IIntRing FracAt () { throw new ReadOnlyException(strConst); }

	/**Returns the Fractional Part of a float Number.	 */
	public IIntRing Frac (){return ((MetricBody) inner).Frac(); }


	//////////////////////////////
	//	Trigonometric Constants	//
	//////////////////////////////

	/**Returns the Constant 2*Pi = 6,283185307179586476925286766559...
	 * This is the Quotient of Circumference and Radius of any circle.	 */
	public MetricBody twoPi()	{return ((MetricBody) inner).twoPi (); }

	/**Returns the Constant 2*Pi = 6,283185307179586476925286766559... in Place
	 * This is the Quotient of Circumference and Radius of any circle.	 */
	public MetricBody twoPiAt() {throw new ReadOnlyException(strConst); }

	/**Returns the Constant Pi = 3,1415926535897932384626433832795...
	 * This is half the Quotient of Circumference and Radius of any circle.	 */
	public MetricBody pi()	{return ((MetricBody) inner).pi (); }

	/**Returns the Constant Pi = 3,1415926535897932384626433832795... in Place
	 * This is half the Quotient of Circumference and Radius of any circle.	 */
	public MetricBody piAt() {throw new ReadOnlyException(strConst); }

	/**Returns the Constant Pi/2 = 1,5707963267948966192313216916398...	 */
	public MetricBody piHalf()	{return ((MetricBody) inner).piHalf (); }

	/**Returns the Constant Pi/2 = 1,5707963267948966192313216916398... in Place	 */
	public MetricBody piHalfAt() {throw new ReadOnlyException(strConst); }

	/**Returns the Constant Pi/3 = 1,0471975511965977461542144610932...	 */
	public MetricBody piThird()	{return ((MetricBody) inner).piThird (); }

	/**Returns the Constant Pi/3 = 1,0471975511965977461542144610932... in Place	 */
	public MetricBody piThirdAt() {throw new ReadOnlyException(strConst); }

	/**Returns the Constant Pi/4 = 0,78539816339744830961566084581988...	 */
	public MetricBody piQuarter()	{return ((MetricBody) inner).piQuarter(); }

	/**Returns the Constant Pi/4 = 0,78539816339744830961566084581988... in Place	 */
	public MetricBody piQuarterAt() {throw new ReadOnlyException(strConst); }

	/**Returns the Constant Pi/6 = 0,52359877559829887307710723054658...	 */
	public MetricBody piSixth()	{return ((MetricBody) inner).piSixth(); }

	/**Returns the Constant Pi/6 = 0,52359877559829887307710723054658... in Place	 */
	public MetricBody piSixthAt() {throw new ReadOnlyException(strConst); }

	//////////////////////////////
	//	Logarithmic Constants	//
	//////////////////////////////

	/**Returns the Constant e = exp(1) = 2.718281828459... 	 */
	public MetricBody e()	{return ((MetricBody) inner).e (); }

	/**Returns the Constant e = exp(1) = 2.718281828459... in Place	 */
	public MetricBody eAt() {throw new ReadOnlyException(strConst); }

	/**Returns the Constant lb(10) =  1/lg(2) = 3.321928094887...	 */
	public MetricBody lb10()	{return ((MetricBody) inner).lb10 (); }

	/**Returns the Constant lb(10) = 1/lg(2) = 3.321928094887... in Place	 */
	public MetricBody lb10At() {throw new ReadOnlyException(strConst); }

	/**Returns the Constant ln(10) = 1/lg(e) = 2.302585092994...	 */
	public MetricBody ln10()	{return ((MetricBody) inner).ln10 (); }

	/**Returns the Constant ln(10) = 1/lg(e) = 2.302585092994... in Place	 */
	public MetricBody ln10At() {throw new ReadOnlyException(strConst); }

	/**Returns the Constant lb(e) = 1/ln(2) 1.442695040889... 	 */
	public MetricBody lbe()	{return ((MetricBody) inner).lbe (); }

	/**Returns the Constant lb(e) = 1/ln(2) = 1.442695040889... in Place	 */
	public MetricBody lbeAt() {throw new ReadOnlyException(strConst); }

	/**Returns the Constant lg(2) = 1/lb(10) = 0,301029995664...	 */
	public MetricBody lg2()	{return ((MetricBody) inner).lg2 (); }

	/**Returns the Constant lg(2) = 1/lb(10) = 0,301029995664... in Place	 */
	public MetricBody lg2At() {throw new ReadOnlyException(strConst); }

	/**Returns the Constant ln(2) = 1/lb(e) = 0,6931471805599... 	 */
	public MetricBody ln2()	{return ((MetricBody) inner).ln2 (); }

	/**Returns the Constant ln(2) = 1/lb(e) = 0,6931471805599... in Place	 */
	public MetricBody ln2At() {throw new ReadOnlyException(strConst); }

	//////////////////////////////
	//	Trigonometric Functions	//
	//////////////////////////////

	/**Returns the Sinus of the angle x: sin(x)	 */
	public MetricBody sin()	{return ((MetricBody) inner).sin (); }

	/**Returns the Sinus of the angle x in Place: sin(x)	 */
	public MetricBody sinAt() {throw new ReadOnlyException(strConst); }

	/**Returns the Cosinus of the angle x: cos(x)	 */
	public MetricBody cos()	{return ((MetricBody) inner).cos (); }

	/**Returns the Cosinus of the angle x in Place: cos(x)	 */
	public MetricBody cosAt() {throw new ReadOnlyException(strConst); }

	/**Returns Cos(x)-1
	 * Gives better accuracy.	 */
	public MetricBody cosM1()	{return ((MetricBody) inner).cosM1 (); }

	/**Returns Cos(x)-1 in Place
	 * Gives better accuracy.	 */
	public MetricBody cosM1At() {throw new ReadOnlyException(strConst); }

	/**Returns the Tangens of the angle x: tan == sin / cos == sin/(1-sin^2)^1/2	*/
	public MetricBody tan()	{return ((MetricBody) inner).tan (); }

	/**Returns the Tangens of the angle x in Place: tan == sin / cos == sin/(1-sin^2)^1/2	*/
	public MetricBody tanAt() {throw new ReadOnlyException(strConst); }

	/**Returns the CoTangens of the angle x: cot == cos / sin == (1-sin^2)^1/2/sin	 */
	public MetricBody cotAt() {throw new ReadOnlyException(strConst); }

	/**Returns the CoTangens of the angle x: cot == cos / sin == (1-sin^2)^1/2/sin	 */
	public MetricBody cot()	{return ((MetricBody) inner).cot (); }

	/**Returns both the Sinus and Cosinus Hyperbolicus.
	 * This is more efficient, because cosH^2+sinH^2=1	 */
	public MetricBody Cos_Sin(ICopyAble Sin)	{return ((MetricBody) inner).Cos_Sin(Sin); }

	//////////////////////
	//	Arcus Functions	//
	//////////////////////

	/**Returns the Arcus Sinus of the Angle x: ArcSin(x)	 */
	public MetricBody ArcSin()	{return ((MetricBody) inner).ArcSin (); }

	/**Returns the Arcus Sinus of the Angle x in Place: ArcSin(x)	 */
	public MetricBody ArcSinAt() {throw new ReadOnlyException(strConst); }

	/**Returns the Arcus Cosinus of the Angle x: ArcCos(x)	 */
	public MetricBody ArcCos()	{return ((MetricBody) inner).ArcCos(); }

	/**Returns the Arcus Cosinus of the Angle x in Place: ArcCos(x)	 */
	public MetricBody ArcCosAt() {throw new ReadOnlyException(strConst); }

	/**Returns the Arcus Tangens of the Angle x: ArcTan(x)	 */
	public MetricBody ArcTan()	{return ((MetricBody) inner).ArcTan(); }

	/**Returns the Arcus Tangens of the Angle x in Place: ArcTan(x)	 */
	public MetricBody ArcTanAt() {throw new ReadOnlyException(strConst); }

	/**Returns the angle in the full Range of -pi to pi
	 * that is given by the two coordinated x and y.
	 * The Condition x^2+y^2 = 1 needn't be fulfilled.	 */
	public MetricBody ArcTg (Object x)	{return ((MetricBody) inner).ArcTg(x); }

	/**Returns the angle in the full Range of -pi to pi
	 * that is given by the two coordinated x and y in Place.
	 * The Condition x^2+y^2 = 1 needn't be fulfilled.	 */
	public MetricBody ArcTgAt (Object x) {throw new ReadOnlyException(strConst); }


	//////////////////////////////
	//	Exponential Functions	//
	//////////////////////////////

	/**Returns the exponential Function: e^x
	 * This is the Inverse to the natural Logarithm ln().
	 * For small Arguments |x| use expM1(x) to gain Accuracy.	 */
	public MetricBody exp()	{return ((MetricBody) inner).exp(); }

	/**Returns the exponential Function in Place: e^x
	 * This is the Inverse to the natural Logarithm ln().
	 * For small Arguments |x| use expM1At(x) to gain Accuracy.	 */
	public MetricBody expAt() {throw new ReadOnlyException(strConst); }

	/**Returns the exponential Function: e^x - 1
	 * This Function is more accurate than exp for small Arguments |x|.
	 * Converges slowly for |x| > 1.	 */
	public MetricBody expM1()	{return ((MetricBody) inner).expM1(); }

	/**Returns the exponential Function in Place: e^x - 1
	 * This Function is more accurate than exp() for small Arguments |x|.
	 * Converges slowly for |x| > 1.	 */
	public MetricBody expM1At() {throw new ReadOnlyException(strConst); }

	/**Returns the binary exponential Function: 2^x
	 * This is the Inverse to the binary natural Logarithm lb().
	 * For small Arguments |x| use bxpM1(x) to gain Accuracy.	 */
	public MetricBody bxp()	{return ((MetricBody) inner).bxp(); }

	/**Returns the binary exponential Function in Place: e^x
	 * This is the Inverse to the binary natural Logarithm lb().
	 * For small Arguments |x| use bxpM1At(x) to gain Accuracy.	 */
	public MetricBody bxpAt() {throw new ReadOnlyException(strConst); }

	/**Returns the binary exponential Function: 2^x - 1
	 * This Function is more accurate than bxp() for small Arguments |x|.
	 * Converges slowly for |x| > 1.	 */
	public MetricBody bxpM1()	{return ((MetricBody) inner).bxpM1(); }

	/**Returns the binary exponential Function in Place: 2^x - 1
	 * This Function is more accurate than bxp() for small Arguments |x|.
	 * Converges slowly for |x| > 1.	 */
	public MetricBody bxpM1At() {throw new ReadOnlyException(strConst); }

	/**Returns the binary integer exponential Function: 2^n
	 * This is the Inverse to the binary integer natural Logarithm lb().	 */
//	public MetricBody nBxp(int n)	{return ((MetricBody) inner). (); }

	/**Returns the exponential Function: 10^x
	 * This is the Inverse to the decadic Logarithm lg().
	 * For small Arguments |x| use dxpM1(x) to gain Accuracy.	 */
	public MetricBody dxp()	{return ((MetricBody) inner).dxp(); }

	/**Returns the decadic exponential Function in Place: 10^x
	 * This is the Inverse to the decadic Logarithm lg().
	 * For small Arguments |x| use dxpM1(x) to gain Accuracy.	 */
	public MetricBody dxpAt() {throw new ReadOnlyException(strConst); }

	/**Returns the decadic exponential Function: 10^x - 1
	 * This Function is more accurate than dxp() for small Arguments |x|.
	 * Converges slowly for |x| > 1.	 */
	public MetricBody dxpM1()	{return ((MetricBody) inner).dxpM1(); }

	/**Returns the decadic exponential Function in Place: 10^x - 1
	 * This Function is more accurate than dxp() for small Arguments |x|.
	 * Converges slowly for |x| > 1.	 */
	public MetricBody dxpM1At() {throw new ReadOnlyException(strConst); }

	/**Returns this number raised to the Power of arg: this^arg
	 * For small Arguments |x| use PowM1(arg) to gain Accuracy.	 */
	public MetricBody Pow(Object arg)	{return ((MetricBody) inner).Pow(arg); }

	/**Returns this number raised to the Power of arg in Place: this^arg
	 * For small Arguments |x| use PowM1(arg) to gain Accuracy.	 */
	public MetricBody PowAt(Object arg) {throw new ReadOnlyException(strConst); }

	/**Returns this number raised to the Power of arg: this^arg - 1
	 * This Function is more accurate than Pow() for small Arguments |arg|.
	 * Converges slowly for |arg| > 1.	 */
	public MetricBody PowM1(Object arg)	{return ((MetricBody) inner).PowM1(arg); }

	/**Returns this number raised to the Power of arg in Place: this^arg - 1
	 * This Function is more accurate than Pow() for small Arguments |arg|.
	 * Converges slowly for |arg| > 1.	 */
	public MetricBody PowM1At(Object arg) {throw new ReadOnlyException(strConst); }

	//////////////////////////////
	//	Hyperbolic Functions	//
	//////////////////////////////

	/**Returns the Cosinus Hyperbolicus of this Number	*/
	public MetricBody CosH()	{return ((MetricBody) inner).CosH(); }

	/**Returns the Cosinus Hyperbolicus of this Number in Place	*/
	public MetricBody CosHAt() {throw new ReadOnlyException(strConst); }

	/**Returns CosH(x)-1
	 * Gives better accuracy.	 */
	public MetricBody cosHm1()	{return ((MetricBody) inner).cosHm1(); }

	/**Returns CosH(x)-1 in Place
	 * Gives better accuracy.	 */
	public MetricBody cosHm1At() {throw new ReadOnlyException(strConst); }

	/**Returns the Sinus Hyperbolicus of this Number	*/
	public MetricBody SinH()	{return ((MetricBody) inner).SinH(); }

	/**Returns the Sinus Hyperbolicus of this Number in Place	*/
	public MetricBody SinHAt() {throw new ReadOnlyException(strConst); }

	/**Returns the Tangens Hyperbolicus of this Number	*/
	public MetricBody TanH()	{return ((MetricBody) inner).TanH(); }

	/**Returns the Tangens Hyperbolicus of this Number in Place	*/
	public MetricBody TanHAt() {throw new ReadOnlyException(strConst); }

	/**Returns the CoTangens Hyperbolicus of this Number	*/
	public MetricBody CotH()	{return ((MetricBody) inner).CotH(); }

	/**Returns the CoTangens Hyperbolicus of this Number in Place	*/
	public MetricBody CotHAt() {throw new ReadOnlyException(strConst); }

	/**Returns both the Sinus and Cosinus Hyperbolicus.
	 * This is more efficient, because cosH^2-sinH^2=1	 */
	public MetricBody CosH_SinH(ICopyAble SinH)	{return ((MetricBody) inner).CosH_SinH(SinH); }


	//////////////////////
	//	Area Functions	//
	//////////////////////

	/**Returns the Area Cosinus Hyperbolicus of this Number
	 * This is the Inverse to CosH	*/
	public MetricBody ArCosH()	{return ((MetricBody) inner).ArCosH(); }

	/**Returns the Area Cosinus Hyperbolicus of this Number in Place
	 * This is the Inverse to CosH	 */
	public MetricBody ArCosHAt() {throw new ReadOnlyException(strConst); }

	/**Returns the Area Sinus Hyperbolicus of this Number
	 * This is the Inverse to SinH	 */
	public MetricBody ArSinH()	{return ((MetricBody) inner).ArSinH(); }

	/**Returns the Area Sinus Hyperbolicus of this Number in Place
	 * This is the Inverse to SinH	 */
	public MetricBody ArSinHAt() {throw new ReadOnlyException(strConst); }

	/**Returns the Area Tangens Hyperbolicus of this Number
	 * This is the Inverse to TanH	 */
	public MetricBody ArTanH()	{return ((MetricBody) inner).ArTanH(); }

	/**Returns the Area Tangens Hyperbolicus of this Number in Place
	 * This is the Inverse to TanH	 */
	public MetricBody ArTanHAt() {throw new ReadOnlyException(strConst); }


	//////////////////////////////
	//	Logarithmic Functions	//
	//////////////////////////////

	/**Returns the natural Logarithm of x: ln(x)
	 * This is the Inverse to the exponential Function exp(x).
	 * For Arguments x near 1 use lnXP1(x) to gain Accuracy.	 */
	public MetricBody ln()	{return ((MetricBody) inner).ln(); }

	/**Returns the natural Logarithm of x in Place: ln(x)
	 * This is the Inverse to the exponential Function exp(x).
	 * For Arguments x near 1 use lnXP1(x) to gain Accuracy.	 */
	public MetricBody lnAt() {throw new ReadOnlyException(strConst); }

	/**Returns the natural binary Logarithm of x+1: ln(x+1)
	 * This is the Inverse to the exponential Function expM1(x).
	 * This Function is more accurate for Arguments near 1 than ln().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody lnXP1()	{return ((MetricBody) inner).lnXP1(); }

	/**Returns the natural binary Logarithm of x+1 in Place: ln(x+1)
	 * This is the Inverse to the exponential Function expM1(x).
	 * This Function is more accurate for Arguments near 1 than ln().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody lnXP1At() {throw new ReadOnlyException(strConst); }

	/**Returns the natural binary Logarithm of x: lb(x)
	 * This is the Inverse to the binary exponential Function bxp(x).
	 * For Arguments x near 1 use lbXP1(x) to gain Accuracy.	 */
	public MetricBody lb()	{return ((MetricBody) inner).lb(); }

	/**Returns the natural binary Logarithm of x in Place: lb(x)
	 * This is the Inverse to the binary exponential Function bxp(x).
	 * For Arguments x near 1 use lbXP1(x) to gain Accuracy.	 */
	public MetricBody lbAt() {throw new ReadOnlyException(strConst); }

	/**Returns the natural binary Logarithm of x+1: lb(x+1)
	 * This is the Inverse to the exponential Function bxpM1(x).
	 * This Function is more accurate for Arguments near 1 than lb().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody lbXP1()	{return ((MetricBody) inner).lbXP1(); }

	/**Returns the natural binary Logarithm of x+1 in Place: lb(x+1)
	 * This is the Inverse to the exponential Function bxpM1(x).
	 * This Function is more accurate for Arguments near 1 than lb().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody lbXP1At() {throw new ReadOnlyException(strConst); }

	/**Returns the natural decadic Logarithm of x: lg(x)
	 * This is the Inverse to the decadic exponential Function dxp(x).
	 * For Arguments x near 1 use lbXP1(x) to gain Accuracy.	 */
	public MetricBody lg()	{return ((MetricBody) inner).lg(); }

	/**Returns the natural decadic Logarithm of x in Place: lg(x)
	 * This is the Inverse to the decadic exponential Function dxp(x).
	 * For Arguments x near 1 use lbXP1(x) to gain Accuracy.	 */
	public MetricBody lgAt() {throw new ReadOnlyException(strConst); }

	/**Returns the natural decadic Logarithm of x+1: lg(x+1)
	 * This is the Inverse to the decadic exponential Function dxpM1(x).
	 * This Function is more accurate for Arguments near 1 than lg().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody lgXP1()	{return ((MetricBody) inner).lgXP1(); }

	/**Returns the natural decadic Logarithm of x+1: lg(x+1)
	 * This is the Inverse to the decadic exponential Function dxpM1(x).
	 * This Function is more accurate for Arguments near 1 than lg().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody lgXP1At() {throw new ReadOnlyException(strConst); }

	/**Returns the Logarithm of this number to the Basis of arg: Log(arg)this
	 * This is the Inverse to PowAt (arg).
	 * For small Arguments |x| use LogXP1(arg) to gain Accuracy.	 */
	public MetricBody Log(Object arg)	{return ((MetricBody) inner).Log(arg); }

	/**Returns the Logarithm of this number to the Basis of arg in Place: Log(arg)this
	 * This is the Inverse to Pow (arg).
	 * For small Arguments |x| use LogXP1(arg) to gain Accuracy.	 */
	public MetricBody LogAt(Object arg) {throw new ReadOnlyException(strConst); }

	/**Returns the Logarithm of (x+1) to the Basis of arg: Log(arg)(x+1)
	 * This is the Inverse to PowM1 (arg).
	 * This Function is more accurate for Arguments near 1 than ln().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody LogXP1(Object arg)	{return ((MetricBody) inner).LogXP1(arg); }

	/**Returns the Logarithm of (x+1) to the Basis of arg in Place: Log(arg)(x+1)
	 * This is the Inverse to PowM1At (arg).
	 * This Function is more accurate for Arguments near 1 than ln().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody LogXP1At(Object arg) {throw new ReadOnlyException(strConst); }

	/**Langevin Function:	L (x) = CosH (x) - 1/x	 */
//	public MetricBody Langevin  () { return ((MetricBody) inner).Langevin(); }

	/**Brillouin Function:	lim (j -> infin) B (j,x) = L (x)	 */
//	public MetricBody Brillouin (MetricBody j) { return ((MetricBody) inner).Brillouin (j); }

	/**Not normalized Bell Curve: e^-(x^2)
	 * Fastest Implementation with Gauss Characteristics, not normed 	 */
	public MetricBody  GaussAt() { throw new ReadOnlyException(strConst); }

	/**Not normalized Bell Curve: e^-(x^2)
	 * Fastest Implementation with Gauss Characteristics, not normed 	 */
	public MetricBody  Gauss  () { return ((MetricBody) inner).Gauss(); }

	/**Not normed Lorentz Curve: 1/(1+x^2)
	 * Fastest Implementation with Lorentz Characteristics, not normed 	 */
	public MetricBody  LorentzAt() {throw new ReadOnlyException(strConst); }

	/**Not normed Lorentz Curve: 1/(1+x^2)
	 * Fastest Implementation with Lorentz Characteristics, not normed 	 */
	public MetricBody  Lorentz()	{return ((MetricBody) inner).Lorentz(); }

	/**Fast, but unsmooth Representation of Delta as a Rectangle Function */
//	public MetricBody Delta1(Object H)	{return ((MetricBody) inner). (); }

	/**Fast, but unsmooth Representation of Delta as a Rectangle Function */
//	public MetricBody Delta1At(Object H)	{return ((MetricBody) inner). (); }

	/**Normed Sigmoid Curve: 1/(1+e^-x)
	 * Fast, smooth normed Implementation of an integrated Delta Function	 */
	public MetricBody  SigmoidAt() {throw new ReadOnlyException(strConst); }

	/**Normed Sigmoid Curve: 1/(1+e^-x)
	 * Fast, smooth normed Implementation of an integrated Delta Function	 */
	public MetricBody  Sigmoid()	{return ((MetricBody) inner).Lorentz(); }

	/**Smooth, sharp, but expensive Representation of Delta as a Bell Curve,
	 * If H is null (not given), it is assumed to 1.
	 * The Width is proportional to 1/H, the Height to H.
	 * To get the original Gauss Function, use H = 1/SqRt(2*Pi)	 */
	public MetricBody  Delta2(Object H)	{return ((MetricBody) inner).Delta2(H); }

	/**Smooth, sharp, but expensive Representation of Delta as a Bell Curve,
	 * If H is null (not given), it is assumed to 1.
	 * The Width is proportional to 1/H, the Height to H.
	 * To get the original Gauss Function, use H = 1/SqRt(2*Pi)	 */
	public MetricBody  Delta2At(Object H) {throw new ReadOnlyException(strConst); }

	/**Smooth, fuzzy, but inexpensive Representation of Delta as a Lorentz Curve
	 * The Width is proportional to 1/H, the Height to H.	 */
	public MetricBody  Delta3(Object H)	{return ((MetricBody) inner).Delta3(H); }

	/**Smooth, fuzzy, but inexpensive Representation of Delta as a Lorentz Curve
	 * The Width is proportional to 1/H, the Height to H.	 */
	public MetricBody  Delta3At(Object H) {throw new ReadOnlyException(strConst); }

	/**Smooth, sharp, a bit expensive Representation of Delta as a Sigmoid Curve
	 * The Width is proportional to 1/H, the Height to H.	 */
	public MetricBody  Delta4(Object H)	{return ((MetricBody) inner).Delta4(H); }

	/**Smooth, sharp, a bit expensive Representation of Delta as a Sigmoid Curve
	 * The Width is proportional to 1/H, the Height to H.	 */
	public MetricBody  Delta4At(Object H) {throw new ReadOnlyException(strConst); }

	/**Continuous, sharp, but at the Corners not differentiable and a bit expensive
	 * Representation of Delta as a Cosinus Curve.
	 * The Width is proportional to 1/H, the Height to H.	 */
	public MetricBody  Delta5(Object H)	{return ((MetricBody) inner).Delta5(H); }

	/**Continuous, sharp, but at the Corners not differentiable and a bit expensive
	 * Representation of Delta as a Cosinus Curve.
	 * The Width is proportional to 1/H, the Height to H.	 */
	public MetricBody  Delta5At(Object H) {throw new ReadOnlyException(strConst); }

}
