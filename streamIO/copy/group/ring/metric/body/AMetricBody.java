package streamIO.copy.group.ring.metric.body;

import streamIO.copy.ICopyAble;
import streamIO.copy.group.ring.metric.AMetricIRing;
import streamIO.copy.group.ring.metric.IMetricIRing;
import streamIO.copy.group.ring.metric.IWellOrder;
import function.ICountAble;
import function.IMeasurAble;
import function.byref.ByRefLong;
import function.derive.ring.ACAlgebra;

/**This is the abstract Implementation of a Metric Body.
 * It extends the Algebra, since any Number can be interpreted
 * as the constant Function / Algebra / Vector over it's own Set.
 * Abstract Methods:
 * addAt, subAt, mulAt, divAt, less
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:57:34Z
 * digest: 056dd460a0a6c6fc4e22b93fa0d9035ffdb2e7684b4f1df9b471031c486bf19d
 * stale: false
 * tags: [code/rational_numbers, code/interval_arithmetic]
 * concepts: [Rational Numbers and Interval Arithmetic]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 * FloorAt */
public abstract class AMetricBody
extends ACAlgebra // AMetricIRing
implements MetricBody {

	//////////////////
	//	Constants	//
	//////////////////

	/**Returns 1/0: Infinity	 */
	public IWellOrder Infinity() { return ((MetricBody) newInstance()).InfinityAt(); }

	/**Returns 1/0 in Place: Infinity	 */
/*	public WellOrder InfinityAt() { 		//left abstract
		oneAt(); divAt(zero()); return this; }

	/**Returns 0/0: NaN (Not a Number) { 	 */		//left abstract
/*	public WellOrder NaN() { return ((WellOrder) newInstance()).NaNAt(); }

	/**Returns 0/0 in Place: NaN (Not a Number)	 */
/*	public WellOrder NaNAt() {		//left abstract
		zeroAt(); divAt(zero()); return this; }
*/

	//////////////////////////////
	//	Trigonometric Constants	//
	//////////////////////////////

	/**Returns the Constant 2*Pi = 6,283185307179586476925286766559...
	 * This is the Quotient of Circumference and Radius of any circle.	 */
	public MetricBody twoPi() { return ((MetricBody)newInstance()).twoPiAt(); }

	/**Returns the Constant 2*Pi = 6,283185307179586476925286766559... in Place
	 * This is the Quotient of Circumference and Radius of any circle.	 */
	public MetricBody twoPiAt() { piAt().dblAt(); return this; }

	/**Returns the Constant Pi = 3,1415926535897932384626433832795...
	 * This is half the Quotient of Circumference and Radius of any circle.	 */
	public MetricBody pi() { return ((MetricBody)newInstance()).piAt(); }

	/**Returns the Constant Pi = 3,1415926535897932384626433832795... in Place
	 * This is half the Quotient of Circumference and Radius of any circle.
	 * There is no Sum that converges to Pi rapidly. Examples are:
	 * (-1)^n/n => pi/4
	 * 1/(4n-1)(4n+1) => 1/2 - pi/4	This one converges relatively fast.
	 * 1/n^2 => pi^2/6
	 * 1/(2n+1)^4 => pi^4/96	Where taking the Square Root
	 *							reduces the accuracy again	 */
//	public MetricBody piAt();	//left abstract

	/**Returns the Constant Pi/2 = 1,5707963267948966192313216916398...	 */
	public MetricBody piHalf() { return ((MetricBody)newInstance()).piHalfAt(); }

	/**Returns the Constant Pi/2 = 1,5707963267948966192313216916398... in Place	 */
	public MetricBody piHalfAt() { piAt().halfAt(); return this; }

	/**Returns the Constant Pi/3 = 1,0471975511965977461542144610932...	 */
	public MetricBody piThird() { return ((MetricBody)newInstance()).piThirdAt(); }

	/**Returns the Constant Pi/3 = 1,0471975511965977461542144610932... in Place	 */
	public MetricBody piThirdAt() { piAt().thirdAt(); return this; }

	/**Returns the Constant Pi/4 = 0,78539816339744830961566084581988...	 */
	public MetricBody piQuarter() { return ((MetricBody)newInstance()).piQuarterAt(); }

	/**Returns the Constant Pi/4 = 0,78539816339744830961566084581988... in Place	 */
	public MetricBody piQuarterAt() { piHalfAt().quarterAt(); return this; }

	/**Returns the Constant Pi/6 = 0,52359877559829887307710723054658...	 */
	public MetricBody piSixth() { return ((MetricBody)newInstance()).piSixthAt(); }

	/**Returns the Constant Pi/6 = 0,52359877559829887307710723054658... in Place	 */
	public MetricBody piSixthAt() { piHalfAt().thirdAt(); return this; }


	//////////////////////////////
	//	Logarithmic Constants	//
	//////////////////////////////

	/**Returns the Constant e = exp(1) = 2.718281828459... 	 */
	public MetricBody e() { return ((MetricBody)newInstance()).eAt(); } //e =e^1

	/**Returns the Constant e = exp(1) = 2.718281828459... in Place	 */
	public MetricBody eAt() { oneAt(); return expAt(); }

	/**Returns the Constant lb(10) =  1/lg(2) = 3,3219280948873623478703194294894...	 */
	public MetricBody lb10() { return ((MetricBody)newInstance()).lb10At(); }

	/**Returns the Constant lb(10) = 1/lg(2) = 3,3219280948873623478703194294894... in Place	 */
	public MetricBody lb10At() { copyAt(ICountAble.Ten); return lbAt(); }

	/**Returns the Constant ln(2) = 1/lb(e) = 0,69314718055994530941723212145818... 	 */
	public MetricBody ln2() { return ((MetricBody)newInstance()).ln2At(); }

	/**Returns the Constant ln(2) = 1/lb(e) = 0,69314718055994530941723212145818... in Place	 */
	public MetricBody ln2At() { twoAt(); return lnAt(); }

	/**Returns the Constant ln(10) = 1/lg(e) = 2,3025850929940456840179914546844...	 */
	public MetricBody ln10() { return ((MetricBody)newInstance()).ln10At(); }

	/**Returns the Constant ln(10) = 1/lg(e) = 2,3025850929940456840179914546844... in Place	 */
	public MetricBody ln10At() { copyAt(ICountAble.Ten); return lnAt(); }

	/**Returns the Constant lb(e) = 1/ln(2) 1,4426950408889634073599246810019... 	 */
	public MetricBody lbe() { return ((MetricBody)newInstance()).lbeAt(); }

	/**Returns the Constant lb(e) = 1/ln(2) = 1,4426950408889634073599246810019... in Place	 */
	public MetricBody lbeAt() { ln2At().invAt(); return this; }

	/**Returns the Constant lg(2) = 1/lb(10) = 0,30102999566398119521373889472449...	 */
	public MetricBody lg2() { return ((MetricBody)newInstance()).lg2At(); }

	/**Returns the Constant lg(2) = 1/lb(10) = 0,30102999566398119521373889472449... in Place	 */
	public MetricBody lg2At() { lb10At().invAt(); return this; }


	//////////////////////////////
	//	Trigonometric Functions	//
	//////////////////////////////

	/**Brings the Argument in the Range [-Pi, +Pi]	 */
//	protected MetricBody PeriodicSin() {
//		MetricBody x = (MetricBody)Rem(IMeasurAble.TwoPi);// twoPi()); }

	/**Expects this to be in the Range [-Pi/4, +Pi/4]	 */
	protected IMetricIRing SinPi_4() {	//sin(x) = -sin(-x)
		if (!negative()) return	PRsin();	//positive Values speed up convergence testing
		negAt();
		IMetricIRing tmp = PRsin(); tmp.negAt();
		return tmp; }	//caching is cheaper than casting!

	/**Power Series for the Sine.
	 * Used only in the Range [0, Pi/4]	 */
	protected IMetricIRing PRsin() {
		boolean add = true;
		IMetricIRing Accuracy	= (IMetricIRing)mul(BaseAccuracy);	//speeds up testing
	    IMetricIRing Quadrat	= (IMetricIRing)sqr();	//).neg();	//Factor changes Sign every times.
	    IMetricIRing Summe	= (IMetricIRing)copy();
	    IMetricIRing Faktor	= this;
		int Z1 = 1;	ByRefLong Divisor = new ByRefLong();
		do {	//faster Convergence Checking when Factor stays positive!
			Divisor.Value = ++Z1;
			Divisor.Value*= ++Z1; 	//potentially weakly defined!!!
			Faktor.divAt(Divisor).mulAt(Quadrat);
			if (add = !add)	Summe.addAt (Faktor);
			else			Summe.subAt(Faktor);
	    } while (Faktor.isMoreThan(Accuracy));
		return Summe; }

	/**Returns the Sinus of this angle, reduced modulo 2*Pi into the Range (-pi,+pi) first.	 */
	public MetricBody sin() {	//return ((MetricBody)copy()).sinAt(); }
		return (MetricBody) SinPi(Rem(twoPi())); }	//sin(x) = sin (x+2Pi) Range (-pi,+pi)

	/**Returns the Sinus of the angle x, but modifies x: sin(x)
	 *	x must be in the Range of [-Pi, +Pi]
	 * Rather use sin() as the base for trigonometric calculations
	 * than tan(), because the coefficients are easier to calculate.	 */
	protected static IMetricIRing SinPi(IMetricIRing x) {	//return ((MetricIRing)copy()).sinAt(); }
		//sin(x) = sin (x+2Pi) Range (-pi,+pi)
		boolean negativ; if (negativ = x.negative()) x.negAt();		//Range: [0, Pi]
			 if (x.isMoreThan(IMeasurAble.ThreePiQuarter)) //sin(x) = -sin(x-Pi) = sin(Pi-x)
			 x = ((AMetricBody) x.subAt(IMeasurAble.pi).negAt()).PRsin();	//Range: [0, Pi/4]
		else if (x.isMoreThan(IMeasurAble.PiQuarter))		//sin(x) = cos(x+Pi/2) = -cos(x-Pi/2)
			 x = ((   MetricBody) x.subAt(IMeasurAble.PiHalf)).cos();	//Range: [-Pi/4, +Pi/4]
		else x = ((AMetricBody) x).PRsin();
		if (negativ)return (IMetricIRing) x.negAt();
		else		return x; }

	/**Returns the Sinus of the angle x in Place: sin(x)	 */
	public MetricBody sinAt() { return (MetricBody) shallowCopyAt(sin()); }

	/**Returns the Cosinus of the angle x: cos(x)	 */
	public MetricBody cos() {
		MetricBody tmp = cosM1(); tmp.inc(); return tmp; }
//		return ((MetricBody)copy()).cosAt(); }

	/**Returns the Cosinus of the angle x in Place: cos(x)	 */
	public MetricBody cosAt() { return  (MetricBody)shallowCopyAt(cos()); }
//	{return ((MetricBody)addAt(piHalf())).sinAt(); }

	/**Returns Cos(x)-1 in Place
	 * Gives better accuracy.	 */
	public MetricBody cosM1At() { return (MetricBody)shallowCopyAt(cosM1()); }

	/**Returns Cos(x)-1
	 * Gives better accuracy.	 */
	public MetricBody cosM1() { //return ((MetricBody)copy()).cosM1At(); }
		//cos(x) = cos (x+2Pi) Range (-pi,+pi)
		MetricBody ret; //already copied
		AMetricBody x = (AMetricBody) Rem(twoPi()).AbsVAt();	//cos (x) = cos(-x) Range: [0, Pi]
		if		(x.isMoreThan(IMeasurAble.ThreePiQuarter)) {	//cos(x) = -cos(x-Pi) = cos(Pi-x)
			x.subAt(IMeasurAble.pi); ret = x.PRcosM1(); ret.addAt(ICountAble.Two); ret.negAt(); return ret; }
		else if (x.isMoreThan(IMeasurAble.	 PiQuarter)) {	//cos(x) = sin(x+Pi/2) = -sin(x-Pi/2)
			x.addAt(IMeasurAble.PiHalf); ret = (MetricBody) x.SinPi_4(); ret.dec(); return ret; }
		return x.PRcosM1(); }

	/**Returns Cos(x)-1
	 * Gives better accuracy.
	 * Requires the Argument to be in the Range [-Pi/4, +Pi/4] at max [-1,+1]
	 * For complex or Matrix Arguments the AbsV() Function has to be used in the Check
	 * Additionally you cannot exploit the Periodicity.  */
	protected MetricBody PRcosM1() { //return ((MetricBody)copy()).cosM1At(); }
		boolean add = false; //already copied!
	    MetricBody Quadrat  = (MetricBody) sqrAt();
		MetricBody Accuracy = (MetricBody) Quadrat.mul(BaseAccuracy);	//speeds up testing
	    MetricBody Faktor   = (MetricBody) Quadrat.copy(); Faktor.halfAt();
	    MetricBody Summe    = (MetricBody) Faktor.neg();
		int Z1 = 3;	ByRefLong Divisor = new ByRefLong();
		do {
			Divisor.Value = (Z1++)*(Z1++);	//potentially weakly defined!!!
			Faktor.divAt(Divisor).mulAt(Quadrat);
			if (add = !add)	Summe.addAt (Faktor);
			else			Summe.subAt(Faktor);
	    } while (Faktor.isMoreThan(Accuracy));
		return Summe; }

	/**Returns the Tangens of the angle x: tan == sin / cos == sin/(1-sin^2)^1/2
	 * Rather use sin() as the base for trigonometric calculations
	 * than tan(), because the coefficients are easier to calculate.	 */
	public MetricBody tanAt()	{ return (MetricBody)shallowCopyAt(tan()); }

	/**Returns the Tangens of the angle x: tan == sin / cos == sin/(1-sin^2)^1/2
	 * Rather use sin() as the base for trigonometric calculations
	 * than tan(), because the coefficients are easier to calculate.	 */
	public MetricBody tan() { //return ((MetricBody)copy()).tanAt(); }
		MetricBody Sin = (MetricBody) newInstance();
		MetricBody Cos = Cos_Sin(Sin);
		return (MetricBody)Sin.divAt(Cos); }

	/**Returns the CoTangens of the angle x: cot == cos / sin == (1-sin^2)^1/2/sin
	 * Rather use sin() as the base for trigonometric calculations
	 * than tan(), because the coefficients are easier to calculate.	 */
	public MetricBody cotAt()	{ return (MetricBody)shallowCopyAt(cot()); }

	/**Returns the CoTangens of the angle x: cot == cos / sin == (1-sin^2)^1/2/sin
	 * Rather use sin() as the base for trigonometric calculations
	 * than tan(), because the coefficients are easier to calculate.	 */
	public MetricBody cot() { //return ((MetricBody)copy()).cotAt(); }
		MetricBody Sin = (MetricBody) newInstance();
		MetricBody Cos = Cos_Sin(Sin);
		return (MetricBody)Cos.divAt(Sin); }

	/**Returns both the Sinus and Cosinus.
	 * This is more efficient, because cos^2 + sin^2 == 1	 */
	public MetricBody Cos_Sin(final ICopyAble Sin)	{
		MetricBody x = (MetricBody) Rem(twoPi());
		IMetricIRing Sin_ = SinPi(x);
		Sin.shallowCopyAt(Sin_);	//transfer the Result to the ByRef Parameter
		MetricBody tmp = (MetricBody) Sin_.sqr();	//c^2 = 1-s^2
		tmp.ResidAt();
		tmp.SqRtAt();
		if (x.AbsVAt().isMoreThan(IMeasurAble.PiHalf)) tmp.negAt();	//negate Cosine for large Arguments
		return tmp; }


	//////////////////////
	//	Arcus Functions	//
	//////////////////////

	/**Returns the Arcus Sinus of the Angle x: ArcSin(x)	 */
	public MetricBody ArcSin() {	//return ((MetricBody)copy()).ArcSinAt(); }
		MetricBody ret = (MetricBody) sqr();
		ret.ResidAt(); ret.SqRtAt();
		return (MetricBody) div(ret.ArcTanAt()); }	//Werte-Bereich nur [-1,+1]

	/**Returns the Arcus Sinus of the Angle x in Place: ArcSin(x)	 */
	public MetricBody ArcSinAt() { return (MetricBody)shallowCopyAt(ArcSin()); }

	/**Returns the Arcus Cosinus of the Angle x: ArcCos(x)	 */
	public MetricBody ArcCos() { //return ((MetricBody)copy()).ArcCosAt(); }
		return (MetricBody)piHalf().subAt(ArcSin()); } //Werte-Bereich nur [-1,+1]

	/**Returns the Arcus Cosinus of the Angle x in Place: ArcCos(x)	 */
	public MetricBody ArcCosAt() { return (MetricBody)shallowCopyAt(ArcCos()); }

	/**Returns the Arcus Tangens of the Angle x: ArcTan(x)
	 * This Algorithm works only for real Arguments. */
	public MetricBody ArcTan()//{return ((MetricBody)copy()).ArcTanAt(); }
	{	// bring the argument into the Range (0,+1/2):
		boolean nega;
		boolean oneG;
		boolean half;
		MetricBody x = (MetricBody) copy();
		if (nega = x.negative()  ) x.negAt();	//arcTan( -x) = - arcTan(x); positive Values speed up convergence testing
		if (oneG = x.isMoreThan(one ())) x.invAt();	//arcTan(1/x) = Pi/2 - arcTan(x)
		if (half = x.isMoreThan(OneHalf())) //use Addition Theorem to bring the Factor to Values < 1/3
		{	//arcTan((v-u)/1+vu) = arcTan(v) - arcTan(u), mit v = 1 ist u = (1-x)/(1+x)
			MetricBody y = (MetricBody) x.Resid(); y.divAt(x.inc()); x = y;
		}
		boolean add = true;
		MetricBody Summe	= (MetricBody) x.copy();
		MetricBody Accuracy = (MetricBody) x.mul(BaseAccuracy);	//speeds up testing
		MetricBody Quadrat	= (MetricBody) x.sqr();	//Factor changes Sign every times.
		MetricBody DIV		= (MetricBody) x.newInstance();	//Factor changes Sign every times.
		ByRefLong Z1 = new ByRefLong(1);
		do {
			Z1.Value += 2;
			x.mulAt(Quadrat);
			DIV.copyAt(x); DIV.divAt(Z1);
			if (add = !add)	Summe.addAt (DIV);
			else			Summe.subAt(DIV);
		} while (DIV.isMoreThan(Accuracy));
		if (half) Summe.subAt(piQuarter()).negAt();
		if (oneG) Summe.subAt(piHalf   ()).negAt();	//arcTan(1/x) = Pi/2 - arcTan(x)
		if (nega) Summe.negAt();
		return Summe; }

	/**Returns the Arcus Tangens of the Angle x in Place: ArcTan(x)	 */
	public MetricBody ArcTanAt() { return (MetricBody)shallowCopyAt(ArcTan()); }

	/**Returns the angle in the full Range of -pi to pi
	 * that is given by the two coordinated x and y.
	 * The Condition x^2+y^2 = 1 needn't be fulfilled.	 */
	public MetricBody ArcTgAt (Object x) { return (MetricBody) shallowCopyAt(ArcTg(x)); }

	/**Returns the angle in the full Range of -pi to pi
	 * that is given by the two coordinated x and y.
	 * The Condition x^2+y^2 = 1 needn't be fulfilled.	 */
	public MetricBody ArcTg (Object x) {
		MetricBody x_ = (MetricBody) x;	//instead of casting, a conversion is mostly necessary
		MetricBody tmp;
		if (! x_.isZero())
			tmp = ((MetricBody)div(x)).ArcTan();
		else {
			if (positive()) tmp =				piHalf();
			else			tmp = (MetricBody)	piHalf().negAt();
		}
		if (! x_.negative())	return				tmp;
		else	//				return (MetricBody)	tmp.addAt (pi());	//this is also correct, but
			 if (positive())	return (MetricBody)	tmp.addAt (pi());	//keep the Value in the Range (-Pi,+Pi)
		else					return (MetricBody)	tmp.subAt(pi()); }


	//////////////////////////////
	//	Exponential Functions	//
	//////////////////////////////

	/**Returns the exponential Function: e^x
	 * This is the Inverse to the natural Logarithm ln().
	 * For small Arguments |x| use expM1(x) to gain Accuracy.	 */
	public MetricBody exp()	{ return ((MetricBody) copy()).expAt(); }
							//return (MetricBody)expM1().inc(); }
	//if (x < Unterlauf ) Exp = Null	//Verhinderung eines Fehlers


	/**Returns the exponential Function in Place: e^x
	 * This is the Inverse to the natural Logarithm ln().
	 * For small Arguments |x| use expM1At(x) to gain Accuracy.	 */
	public MetricBody expAt() { //return (MetricBody)shallowCopyAt(exp()); }
		boolean bolNegative = negative();
		if (bolNegative) {
			negAt();	//Accuracy of expM1At() is very bad for negative x!
			expM1At(); inc(); invAt();
			return this; }
		expM1At(); inc();
		return this; }

	private static byte Recursion;

	/**Returns the exponential Function: e^x - 1
	 * This Function is more accurate than exp for small Arguments |x|.
	 * Converges slowly for |x| > 1.	 */
	public MetricBody expM1() { //return ((MetricBody)copy()).expM1At(); }
		Recursion++;
		MetricBody tmp;
		IMetricIRing Accuracy = mulAbsAccuracy();	//Use the sign to save the abs() on testing
		if ((Recursion == 1) && Accuracy.isMoreThan(MaxAccuracy))	//Value large enough to use conventional Calculation with sufficient Accuracy.
			{tmp = exp(); tmp.dec(); }	//this could lead to a recursion!
		else if (Recursion == 2)	//Value large enough to use conventional Calculation with sufficient Accuracy.
			tmp = ((MetricBody) copy()).expM1At();	//this could lead to a recursion!
		else {	//Konvergenz schlecht f�r x < 0, wegen Ausl�schung!
			MetricBody Summe   = (MetricBody)copy();
			boolean bolNegative = negative(); 	//=> negativen Wert nehmen
			if (bolNegative) Summe.negAt(); 	//und anschlie�end Kehrwert bilden
			MetricBody	Faktor	= (MetricBody) Summe.copy();
			MetricBody  Quadrat = (MetricBody) sqr(); Quadrat.halfAt();
			ByRefLong Z1 = new ByRefLong(2);
			int CountDown = MaxIteration;
			while ((--CountDown > 0) && (Accuracy.isLessThan (Quadrat.AbsV()))) {
			    Summe .addAt(Quadrat); Z1.Value++;
			    Quadrat.divAt(Z1).mulAt(Faktor);
			}
			if (bolNegative) tmp = (MetricBody) Summe.divAt(Summe.succ());	//very bad accuracy!
			else			 tmp = (MetricBody) Summe.addAt(Quadrat);    //zusaetzliche Genauigkeit
		}
		Recursion--;
		return tmp; }

	/**Returns the exponential Function in Place: e^x - 1
	 * This Function is more accurate than exp() for small Arguments |x|.
	 * Converges slowly for |x| > 1.
	 */
	public MetricBody expM1At() { return (MetricBody)shallowCopyAt(expM1()); }

	/**Returns the binary exponential Function: 2^x
	 * This is the Inverse to the binary natural Logarithm lb().
	 * For small Arguments |x| use bxpM1(x) to gain Accuracy.	 */
	public MetricBody bxp() {//	return ((MetricBody)copy()).bxpAt(); }
								return ((MetricBody)mul(IMeasurAble.Ln2)).expAt(); }

	/**Returns the binary exponential Function in Place: e^x
	 * This is the Inverse to the binary natural Logarithm lb().
	 * For small Arguments |x| use bxpM1At(x) to gain Accuracy.	 */
	public MetricBody bxpAt() { return (MetricBody)shallowCopyAt(bxp()); }

	/**Returns the binary exponential Function: 2^x - 1
	 * This Function is more accurate than bxp() for small Arguments |x|.
	 * Converges slowly for |x| > 1.	 */
	public MetricBody bxpM1() {//return ((MetricBody)copy()).bxpM1At(); }
								 return ((MetricBody)mul(IMeasurAble.Ln2)).expM1At(); }

	/**Returns the binary exponential Function in Place: 2^x - 1
	 * This Function is more accurate than bxp() for small Arguments |x|.
	 * Converges slowly for |x| > 1.	 */
	public MetricBody bxpM1At() { return (MetricBody)shallowCopyAt(bxpM1()); }

	/**Returns the binary integer exponential Function: 2^n
	 * This is the Inverse to the binary integer natural Logarithm lb().	 */
/*	public MetricBody nBxp() {
		return (MetricBody)mul2Pow(((ICountAble)Floor()).getInt()); }
*/

	/**Returns the exponential Function: 10^x
	 * This is the Inverse to the decadic Logarithm lg().
	 * For small Arguments |x| use dxpM1(x) to gain Accuracy.	 */
	public MetricBody dxp() { //return ((MetricBody)copy()).dxpAt(); }
								return ((MetricBody)mul(IMeasurAble.Ln10)).expAt(); }

	/**Returns the decadic exponential Function in Place: 10^x
	 * This is the Inverse to the decadic Logarithm lg().
	 * For small Arguments |x| use dxpM1(x) to gain Accuracy.	 */
	public MetricBody dxpAt() { return (MetricBody)shallowCopyAt(dxp()); }

	/**Returns the decadic exponential Function: 10^x - 1
	 * This Function is more accurate than dxp() for small Arguments |x|.
	 * Converges slowly for |x| > 1.	 */
	public MetricBody dxpM1() {
//		return ((MetricBody)copy()).dxpM1At(); }
		return ((MetricBody)mul(IMeasurAble.Ln10)).expM1At(); }

	/**Returns the decadic exponential Function in Place: 10^x - 1
	 * This Function is more accurate than dxp() for small Arguments |x|.
	 * Converges slowly for |x| > 1.	 */
	public MetricBody dxpM1At() { return (MetricBody)shallowCopyAt(dxpM1()); }

	/**Returns this number raised to the Power of arg: this^arg
	 * For small Arguments |x| use PowM1(arg) to gain Accuracy.	 */
	public MetricBody Pow(Object arg) {
		MetricBody ret = ln(); ret.mulAt(arg);
		return ret.expAt();	}

	/**Returns this number raised to the Power of arg in Place: this^arg
	 * For small Arguments |x| use PowM1(arg) to gain Accuracy.	 */
	public MetricBody PowAt(Object arg) {
		return (MetricBody) shallowCopyAt(Pow(arg)); }

	/**Returns this number raised to the Power of arg: this^arg - 1
	 * This Function is more accurate than Pow() for small Arguments |arg|.
	 * Converges slowly for |arg| > 1.	 */
	public MetricBody PowM1(Object arg) {
		MetricBody ret = ln(); ret.mulAt(arg);
		return ret.expM1At(); }

	/**Returns this number raised to the Power of arg in Place: this^arg - 1
	 * This Function is more accurate than Pow() for small Arguments |arg|.
	 * Converges slowly for |arg| > 1.	 */
	public MetricBody PowM1At(Object arg) {
		return (MetricBody) shallowCopyAt(PowM1(arg)); }


	//////////////////////////////
	//	Hyperbolic Functions	//
	//////////////////////////////

	/**Returns the Cosinus Hyperbolicus of this Number
	 * No Problems with Accuracy here, so use the dumb Addition.	*/
	public MetricBody CosH() {	//return ((MetricBody)copy()).CosHAt(); }
		MetricBody Exp = exp(); Exp.addAt(Exp.inv()); Exp.halfAt();
		return Exp; }

	/**Returns the Cosinus Hyperbolicus of this Number in Place	*/
	public MetricBody CosHAt() { return (MetricBody) shallowCopyAt(CosH()); }


	/**Returns CosH(x)-1 in Place.
	 * Gives better accuracy.	 */
	public MetricBody cosHm1At() { //return ((MetricBody)copy()).cosM1At(); }
		return (MetricBody) shallowCopyAt(cosHm1()); }

	/**Returns CosH(x)-1
	 * Gives better accuracy.	 */
	public MetricBody cosHm1() { //return ((MetricBody)copy()).cosM1At(); }
		//already copied!!!
		// bring the argument into the Range (-pi/2,+pi/2):
	    MetricBody Quadrat  = (MetricBody) sqr();
		MetricBody Accuracy = (MetricBody) Quadrat.mul(BaseAccuracy);	//speeds up testing
	    MetricBody Summe    = (MetricBody) Quadrat.copy(); Summe.halfAt();
	    MetricBody Faktor   = (MetricBody) Summe  .copy();
		int Z1 = 2;	ByRefLong Divisor = new ByRefLong();
		do {
			Divisor.Value = (++Z1)*(++Z1);
			Faktor.divAt(Divisor).mulAt(Quadrat);
			Summe.addAt(Faktor);
	    } while (Faktor.isMoreThan(Accuracy));
		return Summe; }

	/**Returns the Sinus Hyperbolicus of this Number
	 * Rather use SinH() as the base for hyperbolic calculations
	 * than TanH(), because the coefficients are easier to calculate.	 */
	public MetricBody SinH() { //return ((MetricBody)copy()).SinHAt(); }
//		MetricBody tmp;
		MetricBody Summe = (MetricBody) AbsV();	//only for real Values!
		MetricBody Accuracy = (MetricBody) Summe.mul(BaseAccuracy);	//speeds up testing
		if ((Recursion == 0) && Accuracy.isMoreThan(MaxAccuracy)) //larger Value, so use dumb Subtraction.
		{	//prevent a Recusion by setting a Flag
			Recursion++;
//			MetricBody ExpM1 = expM1();	//Converges slower, because all even Coefficients cancel each other out!
//			return (MetricBody)((MetricBody)ExpM1.addAt(ExpM1.div(ExpM1.succ()))).halfAt();
			MetricBody Exp = exp();	//this could lead to a recursion!
			Recursion--;
			Exp.subAt(Exp.inv()); Exp.halfAt();
			return Exp;
		} else {	//use the Power Series directly for small Values to prevent Extinction.
			boolean negativ = negative();		//positive Values speed up convergence testing
			MetricBody Quadrat = (MetricBody) sqr();
			MetricBody Faktor = (MetricBody) Summe.copy();
			int Z1 = 1;	ByRefLong Divisor = new ByRefLong();
			do {
				Divisor.Value = (++Z1)*(++Z1);
				Faktor.divAt(Divisor).mulAt(Quadrat);
				Summe.addAt(Faktor);
			} while (Faktor.isMoreThan(Accuracy));	//only for Real Values!
			if (negativ) return (MetricBody)Summe.negAt();
			else		 return				Summe;
		}
	}

	/**Returns the Sinus Hyperbolicus of this Number in Place	*/
	public MetricBody SinHAt() { return (MetricBody) shallowCopyAt(SinH()); }

	/**Returns the Tangens Hyperbolicus of this Number: TanH()
	 * Rather use SinH() as the base for hyperbolic calculations
	 * than TanH(), because the coefficients are easier to calculate.	 */
	public MetricBody TanH()//	{return ((MetricBody)copy()).TanHAt(); }
	{
		MetricBody tmp = ((MetricBody) dbl()).expM1At();
//		return tmp.div(tmp.inc().inc());
		return (MetricBody) tmp.divAt(tmp.add(two()));
	}

	/**Returns the Tangens Hyperbolicus of this Number in Place: TanH()
	 * Rather use SinH() as the base for hyperbolic calculations
	 * than TanH(), because the coefficients are easier to calculate.	 */
	public MetricBody TanHAt()	{return (MetricBody)shallowCopyAt(TanH()); }

	/**Returns the CoTangens Hyperbolicus of this Number	*/
	public MetricBody CotH() {
		MetricBody tmp = ((MetricBody) dbl()).expM1At();
		MetricBody ret =  (MetricBody) tmp.add(ICountAble.Two); ret.divAt(tmp);
		return ret; }

	/**Returns the CoTangens Hyperbolicus of this Number in Place	*/
	public MetricBody CotHAt()	{return (MetricBody)shallowCopyAt(CotH()); }

	/**Returns both the Sinus and Cosinus Hyperbolicus.
	 * This is more efficient, because CosH^2-SinH^2=1	 */
	public MetricBody CosH_SinH(ICopyAble SinH_) {
		MetricBody SinH  = (MetricBody) SinH_;
		MetricBody ExpM1 = expM1();
		MetricBody Exp   = (MetricBody) ExpM1.succ();
		SinH.copyAt(ExpM1.addAt(ExpM1.div(Exp))); SinH.halfAt();	//((MetricBody)Exp.inv()).dblAt()));
		Exp.addAt(Exp.inv()); Exp.halfAt();
		return Exp; }



	//////////////////////
	//	Area Functions	//
	//////////////////////

	//Def. s. Bronstein S. 518 Chapter 3.4.4.2

	/**Returns the Area Cosinus Hyperbolicus of this Number
	 * This is the Inverse to CosH.
	 * This Implementation is only for real Values:
	 * ArCosH(y) = Ln(y + SqRt(y^2-1)) */
	public MetricBody ArCosH() {	//{return ((MetricBody)copy()).ArCosHAt(); }
		MetricBody ret = (MetricBody) sqr(); ret.dec(); ret.SqRtAt();
		ret.addAt(this);
		return ret.lnAt(); }

//	{return ((MetricBody)AbsV().add(((MetricBody)((MetricBody)sqr()).dec()).SqRtAt())).lnAt(); }

	/**Returns the Area Cosinus Hyperbolicus of this Number in Place
	 * This is the Inverse to CosH*/
	public MetricBody ArCosHAt() { return (MetricBody)shallowCopyAt(ArCosH()); }

	/**Returns the Area Sinus Hyperbolicus of this Number
	 * This is the Inverse to SinH
	 * This Implementation is only for real Values:
	 * ArSinH(y) = Ln(y + SqRt(y^2+1)) */
	public MetricBody ArSinH() {	//{return ((MetricBody)copy()).ArSinHAt(); }
		MetricBody ret = (MetricBody) sqr(); ret.inc(); ret.SqRtAt();
		ret.addAt(this);
		return ret.lnAt(); }

	/**Returns the Area Sinus Hyperbolicus of this Number in Place
	 * This is the Inverse to SinH*/
	public MetricBody ArSinHAt() { return (MetricBody)shallowCopyAt(ArSinH()); }

	/**Returns the Area Tangens Hyperbolicus of this Number
	 * This is the Inverse to TanH*/
	public MetricBody ArTanH() {
		MetricBody ret = (MetricBody) succ(); ret.divAt(Resid());
		ret.lnAt(); ret.halfAt();
		return ret; }

	/**Returns the Area Tangens Hyperbolicus of this Number in Place
	 * This is the Inverse to TanH*/
	public MetricBody ArTanHAt() { return (MetricBody)shallowCopyAt(ArCosH()); }


	//////////////////////////////
	//	Logarithmic Functions	//
	//////////////////////////////

	/**Returns the Logarithm of this number to the Basis of arg: Log(arg)this
	 * This is the Inverse to PowAt (arg).
	 * For small Arguments |x| use LogXP1(arg) to gain Accuracy.	 */
	public MetricBody Log(Object arg) { return ((MetricBody) copy()).LogAt(arg); }

	/**Returns the Logarithm of this number to the Basis of arg in Place: Log(arg)this
	 * This is the Inverse to Pow (arg).
	 * For small Arguments |x| use LogXP1(arg) to gain Accuracy.	 */
	public MetricBody LogAt(Object arg) {
		lnAt().divAt(((MetricBody)arg).ln()); return this; }

	/**Returns the Logarithm of (x+1) to the Basis of arg: Log(arg)(x+1)
	 * This is the Inverse to PowM1 (arg).
	 * This Function is more accurate for Arguments near 1 than ln().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody LogXP1(Object arg) { return ((MetricBody) copy()).LogXP1At(arg); }

	/**Returns the Logarithm of (x+1) to the Basis of arg in Place: Log(arg)(x+1)
	 * This is the Inverse to PowM1At (arg).
	 * This Function is more accurate for Arguments near 1 than ln().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody LogXP1At(Object arg) {
		lnXP1At().divAt(((MetricBody)arg).ln()); return this; }

	/**Returns the natural Logarithm of x: ln(x)
	 * This is the Inverse to the exponential Function exp(x).
	 * For Arguments x near 1 use lnXP1(x) to gain Accuracy.	 */
	public MetricBody ln() { return ((MetricBody)copy()).lnAt(); }
							//{return (MetricBody) ((MetricBody) pred()).lnXP1At(); }


	/**Returns the natural Logarithm of x in Place: ln(x)
	 * This is the Inverse to the exponential Function exp(x).
	 * For Arguments x near 1 use lnXP1(x) to gain Accuracy.	 */
	public MetricBody lnAt() { //return (MetricBody)shallowCopyAt(ln()); }
		dec(); lnXP1At(); return this; }

	/**Returns the natural binary Logarithm of x+1: ln(x+1)
	 * This is the Inverse to the exponential Function expM1(x).
	 * This Function is more accurate for Arguments near 1 than ln().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody lnXP1() {
		Recursion++;
		MetricBody tmp;
		MetricBody Accuracy = (MetricBody) mulAbsAccuracy();	//Use the sign to save the abs() on testing
		if ((Recursion == 1) && Accuracy.isMoreThan(MaxAccuracy))		//conventional Calculation using ln()
			tmp = ((MetricBody)succ()).lnAt();	//this could lead to a recursion!
		else if (Recursion == 2)	//try it with ln() instead
			tmp = ((MetricBody)succ()).ln();	//this could lead to a recursion!
		else	//Use the Power Series, converges best for x around 0
		{	//This converges for ALL real x > -1 ! //see Bronstein
			MetricBody Summe	= (MetricBody)copy(); Summe.divAt(succ().inc());
//			if (equals(Summe)) return (MetricBody) minValue();	//minValue is not 0, but -Infinity, additionally it returns without decrementing Recursion
			MetricBody	Faktor	= (MetricBody) Summe.sqr();
			MetricBody Quadrat	= (MetricBody) Summe.copy();
			ByRefLong Z1 = new ByRefLong(1);
			while (Accuracy.isLessThan (Quadrat.AbsV())) {	// (((MetricBody)Summe.mul(BaseAccuracy)).less (Quadrat.AbsV()))
				Quadrat.mulAt(Faktor);
				if ((Z1.Value += 2) > AMetricIRing.MaxIteration)
				{Summe = (MetricBody) lnXP1().halfAt(); break; }
				Summe .addAt(Quadrat.div(Z1));
			}
			tmp = (MetricBody) Summe.dblAt();
/*			 //This Converges only for |x| < 1
			MetricBody Summe	= (MetricBody)copy();
			MetricBody	Faktor	= (MetricBody) neg();
			MetricBody Quadrat	= (MetricBody) ((MetricBody)sqr()).negAt();
			RingLong Z1 = new RingLong(1);
			while (Accuracy.less (Quadrat.AbsV())) {	// (((MetricBody)Summe.mul(BaseAccuracy)).less (Quadrat.AbsV()))
			    Summe .addAt(Quadrat.div(Z1.inc()));
			    Quadrat.mulAt(Faktor);
			}
			return (MetricBody) Summe.addAt(Quadrat.div(Z1));	//Add some Accuracy!
*/		}
		Recursion--;
		return tmp; }

	/**Returns the natural binary Logarithm of x+1 in Place: ln(x+1)
	 * This is the Inverse to the exponential Function expM1(x).
	 * This Function is more accurate for Arguments near 1 than ln().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody lnXP1At() { return (MetricBody)shallowCopyAt(lnXP1()); }

	/**Returns the natural binary Logarithm of x: lb(x)
	 * This is the Inverse to the binary exponential Function bxp(x).
	 * For Arguments x near 1 use lbXP1(x) to gain Accuracy.	 */
	public MetricBody lb() { return (MetricBody)ln().mulAt(lbe()); }

	/**Returns the natural binary Logarithm of x in Place: lb(x)
	 * This is the Inverse to the binary exponential Function bxp(x).
	 * For Arguments x near 1 use lbXP1(x) to gain Accuracy.	 */
	public MetricBody lbAt() { return (MetricBody)shallowCopyAt(lb()); }

	/**Returns the natural binary Logarithm of x+1: lb(x+1)
	 * This is the Inverse to the exponential Function bxpM1(x).
	 * This Function is more accurate for Arguments near 1 than lb().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody lbXP1() { return (MetricBody)lnXP1().mulAt(lbe()); }

	/**Returns the natural binary Logarithm of x+1 in Place: lb(x+1)
	 * This is the Inverse to the exponential Function bxpM1(x).
	 * This Function is more accurate for Arguments near 1 than lb().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody lbXP1At() { return (MetricBody)shallowCopyAt(lbXP1()); }

	/**Returns the natural decadic Logarithm of x: lg(x)
	 * This is the Inverse to the decadic exponential Function dxp(x).
	 * For Arguments x near 1 use lbXP1(x) to gain Accuracy.	 */
	public MetricBody lg() { return (MetricBody)ln().divAt(ln10()); }

	/**Returns the natural decadic Logarithm of x in Place: lg(x)
	 * This is the Inverse to the decadic exponential Function dxp(x).
	 * For Arguments x near 1 use lbXP1(x) to gain Accuracy.	 */
	public MetricBody lgAt() { return (MetricBody)shallowCopyAt(lg()); }

	/**Returns the natural decadic Logarithm of x+1: lg(x+1)
	 * This is the Inverse to the decadic exponential Function dxpM1(x).
	 * This Function is more accurate for Arguments near 1 than lg().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody lgXP1() { return (MetricBody)lnXP1().divAt(ln10()); }

	/**Returns the natural decadic Logarithm of x+1: lg(x+1)
	 * This is the Inverse to the decadic exponential Function dxpM1(x).
	 * This Function is more accurate for Arguments near 1 than lg().
	 * Converges slowly for |x| > 0.5!	 */
	public MetricBody lgXP1At() { return (MetricBody)shallowCopyAt(lgXP1()); }


	//////////////////////////////////////
	//	Routines for new Result Types	//
	//////////////////////////////////////

	/** 'Super-Gauss': Exp(-x^(2m))	 */
	public MetricBody SGauss (byte m, MetricBody x) {
		return (MetricBody) x.Pow(m << 1); }

	/**asymmetrischer Cosh :       a=0 => CosH
	 * Exp (x/(1+a))+Exp (x/(1-a)) a=1 => Exp (-t)	 */
	public MetricBody ACosh (MetricBody a, MetricBody x) {
		MetricBody m1 = ((MetricBody) x.div(a.succ ())).exp();
		MetricBody m2 = ((MetricBody) x.div(a.Resid())).exp(); m1.addAt(m2);
		return m1; }

	/**Returns ((1+y)^x)-1
	 * Instead of using lnXP1 and expM1
	 * we rather use the Power Series directly, because that converges faster.	 */
	public MetricBody P1PowM1(MetricBody x) {
		MetricBody Summe = (MetricBody) mul(x.succ());	//Base*(1+x)
		MetricBody Accuracy = (MetricBody) mulAbsAccuracy();	//Use the sign to save the abs() on testing
		if (Accuracy.isMoreThan(MaxAccuracy))	{	//conventional Calculation using ln()
			MetricBody tmp = lnXP1(); tmp.mulAt(x);
			return tmp.expM1At(); }
		MetricBody Faktor = (MetricBody) Summe.halfAt().mul(this).mulAt(x);
		ByRefLong Z = new ByRefLong(1);
		while (Faktor.AbsV().isLessThan(Accuracy)) {
			Summe.addAt(Faktor);
			Faktor.mulAt(this).mulAt(x.sub(Z));
			Z.Value+=2; Faktor.divAt(Z);
			Z.Value--;
		}
		return (MetricBody) Summe.addAt(Faktor); }

	/**Returns the Logarithm of x to this Base	 */
	public MetricBody log(MetricBody x) { return (MetricBody) x.ln().divAt(ln()); }

	/**Returns the Logarithm of x to this Base.
	 * This Routine is for cases in which both x
	 * and this Number are close to 1, so rounding Errors occur.	 */
	public MetricBody logP1(MetricBody x) { return (MetricBody) x.lnXP1().divAt(lnXP1()); }

	/**Not normalized Bell Curve: e^-(x^2)
	 * Fastest Implementation with Gauss Characteristics, not normed 	 */
	public MetricBody  Gauss()	{ return ((MetricBody) copy()).GaussAt(); }

	/**Not normalized Bell Curve: e^-(x^2)
	 * Fastest Implementation with Gauss Characteristics, not normed.
	 * To Norm it, use Delta2.	 */
	public MetricBody  GaussAt() { sqrAt(); negAt(); return expAt(); } //SqrNorm()).negAt()).expAt();

	/**Not normed Lorentz Curve: 1/(1+x^2)
	 * Fastest Implementation with Lorentz Characteristics, not normed.
	 * To norm it, use Delta3. 	 */
	public MetricBody  LorentzAt() { sqrAt(); inc(); invAt(); return this; } //SqrNorm()).inc()).invAt();

	/**Not normed Lorentz Curve: 1/(1+x^2)
	 * Fastest Implementation with Lorentz Characteristics, not normed 	 */
	public MetricBody  Lorentz() { return ((MetricBody) copy()).LorentzAt(); }

	/**Normed Sigmoid Curve: 1/(1+e^-x)
	 * Fast, smooth normed Implementation of an integrated Delta Function	 */
	public MetricBody  SigmoidAt() { negAt(); expAt(); inc(); invAt(); return this; }

	/**Normed Sigmoid Curve: 1/(1+e^-x)
	 * Fast, smooth normed Implementation of an integrated Delta Function	 */
	public MetricBody  Sigmoid() { return ((MetricBody) copy()).SigmoidAt(); }
//	{MetricBody tmp = this.exp(); return tmp.divAt(tmp.succ()); }

	/**Smooth, sharp, but expensive Representation of the normed Delta
	 * as a Bell Curve.
	 * If H is null (not given), it is assumed to 1.
	 * The Width is proportional to 1/H, the Height to H.
	 * To get the original Gauss Function, use H = 1/SqRt(2)	 */
	public MetricBody  Delta2(Object H) { return ((MetricBody) copy()).Delta2At(H); }

	/**Smooth, sharp, but expensive Representation of the normed Delta
	 * as a Bell Curve.
	 * If H is null (not given), it is assumed to 1.
	 * The Width is proportional to 1/H, the Height to H.
	 * To get the original Gauss Function, use H = 1/SqRt(2)	 */
	public MetricBody  Delta2At(Object H) {
		if (H != null) mulAt(H);	//assume it to 1
		GaussAt().divAt(IMeasurAble.SqRtPi);
		if (H != null) mulAt(H);	//assume it to 1
		return this; }

	/**Smooth, fuzzy, but inexpensive Representation of the normed Delta
	 * as a Lorentz Curve.
	 * The Width is proportional to 1/H, the Height to H.	 */
	public MetricBody  Delta3(Object H) { return ((MetricBody) copy()).Delta3At(H); }

	/**Smooth, fuzzy, but inexpensive Representation of the normed Delta
	 * as a Lorentz Curve.
	 * The Width is proportional to 1/H, the Height to H.	 */
	public MetricBody  Delta3At(Object H) {
		if (H != null) mulAt(H);	//assume it to 1
		LorentzAt().divAt(IMeasurAble.pi);
		if (H != null) mulAt(H);	//assume it to 1
		return this; }

	/**Smooth, sharp, a bit expensive Representation of Delta as a Sigmoid Curve
	 * The Width is proportional to 1/H, the Height to H.	 */
	public MetricBody  Delta4(Object H) { return ((MetricBody) copy()).Delta4At(H); }

	/**Smooth, sharp, a bit expensive Representation of Delta as a Sigmoid Curve
	 * The Width is proportional to 1/H, the Height to H.	 */
	public MetricBody  Delta4At(Object H) {
		if (H != null) mulAt(H); SigmoidAt();	//assume it to 1
		if (H != null) mulAt(H); return this;	//assume it to 1
	}

	/**Continuous, sharp, but at the Corners not differentiable and a bit expensive
	 * Representation of Delta as a Cosinus Curve.
	 * The Width is proportional to 1/H, the Height to H.	 */
	public MetricBody  Delta5(Object H) { return ((MetricBody) copy()).Delta5At(H); }

	/**Continuous, sharp, but at the Corners not differentiable and a bit expensive
	 * Representation of Delta as a Cosinus Curve.
	 * The Width is proportional to 1/H, the Height to H.	 */
	public MetricBody  Delta5At(Object H) {
		if (H != null) mulAt(H); if (AbsVAt().isLessThan(IMeasurAble.PiHalf)) //assume it to 1
									return (MetricBody)zeroAt();
		if (H != null) mulAt(H);	return				cosAt(); 	//assume it to 1
	}


	//////////////
	//	Testing	//
	//////////////

	/**Method to test all Implementations in this class.
	 * Must call testIt of the super Class.	 */
	public static void testIt() throws java.io.IOException {
		System.out.println("Testing AMetricBody:");
		testRational();
		System.out.println("Testing logarithmic Functions:");
		System.in.read(); System.in.read();
		System.out.println("Testing trigonometric Functions:");
		tConst();
		System.in.read(); System.in.read();
		//Testers are missing for the special Functions.

//		System.out.println(" = " + test.());
	}

	/**Method to test all Implementations in this class.	 */
	public static void testRational() throws java.io.IOException {
		MetricBody test=(MetricBody) testInstance.newInstance();
		MetricBody Z2 = (MetricBody) testInstance.newInstance();
		MetricBody Z3 = (MetricBody) testInstance.newInstance();
		MetricBody Z4 = (MetricBody) testInstance.newInstance();
//		Vector.RingLong Z2 = new Vector.RingLong(0);
//		Vector.RingLong Z3 = new Vector.RingLong(0);
//		Vector.RingLong Z4 = new Vector.RingLong(0);
		System.out.println ("Teste rational");
		test.copyAt(IMeasurAble.pi);
		test.rational (Z2,Z3,Z4);
		System.out.println ("Soll : " + test + "  Ist : " + Z2.addAt(Z3.divAt(Z4)));
		System.out.println ("Differenz : Soll : kleiner als" + AMetricIRing.BaseAccuracy + "  Ist : " + test.sub(Z2));
		test.copyAt(IMeasurAble.Golden);
		test.rational (Z2,Z3,Z4);
		System.out.println ("Soll : " + test + "  Ist : " + Z2.addAt(Z3.divAt(Z4)));
		System.out.println ("Differenz : Soll : kleiner als" + AMetricIRing.BaseAccuracy + "  Ist : " + test.sub(Z2));
		test.copyAt(IMeasurAble.pi); test.negAt();
		test.rational (Z2,Z3,Z4);
		System.out.println ("Soll : " + test + "  Ist : " + Z2.addAt(Z3.divAt(Z4)));
		System.out.println ("Differenz : Soll : kleiner als" + AMetricIRing.BaseAccuracy + "  Ist : " + test.sub(Z2));
		test.copyAt(IMeasurAble.Golden); test.negAt();
		test.rational (Z2,Z3,Z4);
		System.out.println ("Soll : " + test + "  Ist : " + Z2.addAt(Z3.divAt(Z4)));
		System.out.println ("Differenz : Soll : kleiner als" + AMetricIRing.BaseAccuracy + "  Ist : " + test.sub(Z2));
		System.in.read(); System.in.read();
	}

	/**Tests the Functionality of the Constants	 */
	private static void tConst() throws java.io.IOException {
		MetricBody test = (MetricBody) testInstance;	//defined in ACopyAble to test the abstract Methods
		System.out.println("Testing simple Constants:");
		System.out.println("OneHalf = " + test.OneHalf());
		System.out.println("OneThird = " + test.OneThird());
		System.out.println("OneQuarter = " + test.OneQuarter());
		System.out.println("Infinity = " + test.Infinity());
		System.out.println("NaN = " + test.NaN());	//NaN prints as 0.0!!
		IWellOrder nan;
		nan = test.NaN();
		System.out.println("NaN = " + nan);
		System.in.read(); System.in.read();

		System.out.println("Testing trigonometric Constants:");
		System.out.println("Pi = " + test.pi());
		System.out.println("twoPi = " + test.twoPi());
		System.out.println("piHalf = " + test.piHalf());
		System.out.println("piQuarter = " + test.piQuarter());
		System.in.read(); System.in.read();

		System.out.println("Testing logarithmic Constants:");
		System.out.println("e = " + test.e());
		System.out.println("lb10 = " + test.lb10());
		System.out.println("ln2 = " + test.ln2());
		System.out.println("ln10 = " + test.ln10());
		System.out.println("lbe = " + test.lbe());
		System.out.println("lg2 = " + test.lg2());
		System.in.read(); System.in.read();
	}

	/**Tests 'Super-Gauss': Exp(-x^(2m))	 */
	public static void tSGauss () throws java.io.IOException { }

}
