package streamIO.copy.group.ring.metric.body.complex;

import streamIO.Log;
import streamIO.copy.ICopyAble;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ISemiGroup;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.integer;
import streamIO.copy.group.ring.metric.IMetricIRing;
import streamIO.copy.group.ring.metric.IScalarMetric;
import streamIO.copy.group.ring.metric.IWellOrder;
import streamIO.copy.group.ring.metric.body.AMetricBody;
import streamIO.copy.group.ring.metric.body.Body;
import streamIO.copy.group.ring.metric.body.BodyDouble;
import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.copy.groupM.IGroupM;
import streamIO.copy.groupM.ISemiGroupM;
import function.ICountAble;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.derive.CCountAble;
import function.derive.ring.body.ArSinH;
import function.derive.ring.body.ArTanH;
import function.derive.ring.body.ArcSin;
import function.derive.ring.body.CosH;
import function.derive.ring.body.SinH;
import function.derive.ring.body.TanH;

//TODO: Optimization: Delegate ALL Operations to Value, not only the Basic ones!
//TODO: Optimization: Cache the Results of Tests like isZero() isOne() etc.

/**Concrete final Class to define Complex Numbers backed by primitive {@code double} parts.
 * Complex Numbers from a Metric Body form a non-metric algebraic complete Body.
 *
 * Design Decisions:
 * Unlike {@link Complex}, which stores its real and imaginary parts as {@link IMetricIRing}
 * constituents of arbitrary type, this class fixes both parts to {@code double} for speed.
 *
 * The Behavior of Complex Numbers can be controlled by boolean static Variables:
 * -A Complex will always be checked for zero imaginary Part after any Operation.
 *
 * The Methods cjg(), cjgAt() and isComplex() are all implemented in Integer.
 * The Methods mulAtCjg, mulCjg, divCjg, divAtCjg
 *		should maybe also be implemented there, so they can be called
 *		e.g. in Matrix Multiplication
 *		where they would save some negAt() Operations.
 *
 * This Class is practically completely copied from Interval!
 * Any change here should also be done in Interval!
 *
 * Making real and imag protected fields
 * prevents direct Modification from the Outside.
 * Indirect Modification is not possible, if using Constants as Elements.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:17:34Z
 * digest: 55b2ece8dd7d36b0abe6fef22ad5add3d66b8de1b801a3a5fd0fcff924db63a4
 * stale: false
 * tags: [code/complex_numbers, code/fourier_transform]
 * concepts: [Complex Number Arithmetic and Fourier Transform]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 * This makes it possible to create real Complex Constants!	 */
final public class ComplexDbl
extends AMetricBody 
//implements IMeasurAble 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(0);

	/**The real Part of the ComplexDbl Number.	 */
	protected double real; //Re

	/**The imaginary Part of the ComplexDbl Number.	 */
	protected double imag; //Im

	/**Local Object for consistent Argument Treatment in convertArg()	 */
	protected ComplexDbl arg_; // = new ComplexDbl(); //Recursion!!!

	/**Local Object for faster Calculations in sin() etc. 
	 * unfortunately not thread-safe	 */
	protected ByRefDouble BR = new ByRefDouble();

	/**Returns the conjugate ComplexDbl Number in Place:
	 * i.e. the imaginary Part flips it's sign.	 */
	public IIntRing cjgAt() {
		imag = -imag;
		return this;
	}

	/**Overrides the false Value from the Implementation in AComplex	 */
	public boolean isComplex() {
		return true;
	}

	/**		Constants
	 * The Problem is that they could be modified by an ...At() Operation!
	 * They must never be used like this!	 */

	//Using ComplexDbl here, instead of CComplex,
	//to be able to use these like ComplexDbl Numbers.
	//ComplexDbl cannot be modified directly since Real and Imag are protected.
	//Their indirect Modification is covered by using Constants.
	//The Alternative would be to introduce an Interface ComplexDbl
	//and rewrite the ComplexDbl Class to only use that one
	//with a Performance Penalty!

	/**This Constant indicates the Factor i	 */
	final static public ComplexDbl I = new ComplexDbl(BodyDouble.Zero, BodyDouble.One);
	//	final static public CComplex  I = new CComplex(new ComplexDbl(	BodyDouble.Zero,
	//																		BodyDouble. One));

	/**This Constant indicates the Factor -i	 */
	final static public ComplexDbl _I = new ComplexDbl(BodyDouble.Zero, BodyDouble._One);

	/**This Constant represents  0	 */
	final static public ComplexDbl Zero = new ComplexDbl(BodyDouble.Zero, BodyDouble.Zero);

	/**This Constant represents  1	 */
	final static public ComplexDbl One = new ComplexDbl(BodyDouble.One, BodyDouble.Zero);

	/**This Constant represents -1	 */
	final static public ComplexDbl _One = new ComplexDbl(BodyDouble._One, BodyDouble.Zero);

	/**Switches Checking for real Results on or off.
	 * Since you can not expect to be a result real, it is typically switched on. 	 */
	//	public static boolean bolLazySimplify = true; //false;

	//////////////////////
	//	Constructors	//
	//////////////////////

	/**Constructor that takes an Object of the same Class as Input(Copy Constructor).
	 * Uses the Copy Constructors of the Constituents.	 */
	public ComplexDbl(ComplexDbl arg) { //copyAt(arg);	//...the same, only faster:
		real = arg.real;
		imag = arg.imag;
	}

	/**Constructor that takes Constants as Input for real and imaginary Part.
	 * Those Objects should be of the same Type to speed up calculation
	 * or even enabling it (when e.g. one is not countable).
	 * This prevents the indirect Change of the Real and Imaginary Part,
	 * but that is due to Performance Reasons.
	 * Don't make Copies of these Elements,
	 * since these Copies are no Constants anymore!	 */
	public ComplexDbl(double Real_, double Imag_) {
		real = Real_;
		imag = Imag_;
	}

	/**Constructor that takes any Object as Input for real and imaginary Part.
	 * Those Objects should be of the same Type to speed up calculation
	 * or even enabling it (when e.g. one is not countable).	 */
	public ComplexDbl(Object Real_, Object Imag_) {
		boolean ReC;
		boolean ImC;
		Object buf = null;
		if (ReC = Real_ instanceof Complex) {
			buf = Imag_;
			Imag_ = ((Complex) Real_).Imag;
			Real_ = ((Complex) Real_).Real;
		}
		if (ImC = Imag_ instanceof Complex) {
			buf = Real_;
			Real_ = ((Complex) Imag_).Real;
			Imag_ = ((Complex) Imag_).Imag;
		}
		real = ByRefDouble.GET_DOUBLE(Real_);
		imag = ByRefDouble.GET_DOUBLE(Imag_);
		if (ReC & ImC)
			throw new AbstractMethodError();
		if (ReC)
			imag += ByRefDouble.GET_DOUBLE(buf);
		if (ImC)
			real += ByRefDouble.GET_DOUBLE(buf);
	}

	/**Converts to rectangular Coordinates,
	 * in which Addition and Subtraction are easier to calculate.	 */
	public ComplexDbl(PolarDbl arg) {
		real = Math.cos(arg.ang) * arg.r; //TODO: Optimize this
		imag = Math.sin(arg.ang) * arg.r;
	}

	/**Constructor that takes any Object as Input for the real Part.
	 * The imaginary Part is set to 0.	 */
	public ComplexDbl(Object arg) {
		if (arg instanceof ComplexDbl) { //this((ComplexDbl) arg);
			ComplexDbl arg_ = (ComplexDbl) arg;
			real = arg_.real;
			imag = arg_.imag;
		} else {
			real = ByRefDouble.GET_DOUBLE(arg);
			imag = ICountAble.ZERO;
		} //Choose the same type -> faster
	}

	/**Constructor that takes a double as Input for the real Part.
	 * The imaginary Part is set to 0.
	 * The Types are defaulted to BodyDouble.	 */
	public ComplexDbl(double Real_) {
		real = Real_;
		imag = ICountAble.ZERO;
	} //Choose the same type -> faster

	/**Constructor that takes an int as Input for the real Part.
	 * The imaginary Part is set to 0.	 */
	/*	public ComplexDbl(int Real_) {
		Real = new RingLong(Real_);
		Imag = (MetricIRing) Real.zero(); } 	//Choose the same type -> faster
	
	*/
	/**TODO: incorporate this,
	 * although it is very unlikely to calculate with complex Integers.	 */

	/**Empty Constructor (for newInstance Method).
	 * Does not create Dummy Objects for it's Constituents.
	 * So those Objects are not well-defined, but contain Null Pointers.
	 * This Constructor must not be public,
	 * because the Type of it's Parts must be defined!	 */
	protected ComplexDbl() {
	}

	/**Setting to 0 in Place:	 */
	public IGroup zeroAt() {
		real = ICountAble.ZERO;
		imag = ICountAble.ZERO;
		return this;
	}

	/**Setting to 1 in Place:	 */
	public IGroupM oneAt() {
		real = ICountAble.ONE;
		imag = ICountAble.ZERO;
		return this;
	}

	/**Returns the Constant Pi = 3.14159265359... in Place
	 * This is half the Quotient of Circumference and Radius of any circle.	 */
	public MetricBody piAt() {
		real = IMeasurAble.PI;
		imag = ICountAble.ZERO;
		return this;
	}

	/**Testing for 0:	 */
	public boolean isZero() {
		return real == ICountAble.ZERO && imag == ICountAble.ZERO;
	}

	/**Setting to i:	 */
	public ComplexDbl i() {
		return ((ComplexDbl) newInstance()).iAt();
	}

	/**Setting to i in Place:	 */
	public ComplexDbl iAt() {
		real = ICountAble.ZERO;
		imag = ICountAble.ONE;
		return this;
	}

	//////////////////////////////////////
	//	Multiplication / Division by i	//
	//////////////////////////////////////

	/**Multiplies the ComplexDbl Number by i or divides it by -i:
	 * i.e. Im <= Re and Re <= -Im, which is a Rotation by +90�	 */
	public IIntRing MulI() {
		return ((ComplexDbl) copy()).mulIAt();
	}

	/**Divides the ComplexDbl Number by i or multiplies it by -i:
	 * i.e. Im <= -Re and Re <= Im, which is a Rotation by -90�	 */
	public IIntRing DivI() {
		return ((ComplexDbl) copy()).divIAt();
	}

	/**Divides the ComplexDbl Number by i or multiplies it by -i in Place:
	 * i.e. Im <= -Re and Re <= Im, which is a Rotation by -90�	 */
	public IIntRing divIAt() {
		double tmp = real;
		real = imag;
		imag = -tmp;
		return this;
	}

	/**Multiplies the ComplexDbl Number by i or divides it by -i in Place:
	 * i.e. Im <= Re and Re <= -Im, which is a Rotation by +90�	 */
	public IIntRing mulIAt() {
		double tmp = imag;
		imag = real;
		real = -tmp;
		return this;
	}

	//

	/**Returns the 'arg',
	 * i.e. the angle of the corresponding Polar Representation	 */
	public IMetricIRing arg() {
		return this;
	} //uses ArcTg

	/**Converts to polar Coordinates,
	 * in which exponential Functions and Potency are easier to calculate.	 */
	//	public PolarDbl PolarDbl(){return new PolarDbl(this);}

	/**Returns an Integer, not a ComplexDbl, this also saves time in further Calculations!	 */
	public IMetricIRing FloorAt() {
		real = Math.floor(real);
		imag = Math.floor(imag);
		return this;
	}

	/**less: '<' Returns True, when 'Self' < arg
	 * By now: (a.re < b.re) && (a.im < b.im)
	 * Better: MaxMetric: max(a.re,a.im) < max(b.re,b.im),
	 * because small Phase Shifts could make this
	 * The Best would be to check if the Difference is significantly
	 * in Favor of the wanted Direction. 	 */
	public boolean isLessThan(Object arg) {
		convertArg(arg);
		return (real < arg_.real) && (imag < arg_.imag);
	}

	/**Returns +Infinity = 1/0	 */
	public IWellOrder maxValueAt() {
		real = Double.MAX_VALUE;
		imag = Double.MAX_VALUE;
		return this;
	}

	//////////////////////////////
	//	Arithmetic Operations	//
	//////////////////////////////

	/**Helper Routine to convert to ComplexDbl from any other numeric Type:
	 * RingLong, Number or countable.
	 * Uses ASemiGroup.getLong to do that.
	 * Using this Helper Routine generates Overhead,
	 * because the special optimizations for integer Values are not considered.	 */
	private final void convertArg(Object arg) { //	private final ComplexDbl convertArg (Object arg)
		if (arg_ == null) arg_ = new ComplexDbl();
		if (arg instanceof ComplexDbl) {
			arg_.real = ((ComplexDbl) arg).real;
			arg_.imag = ((ComplexDbl) arg).imag;
		} else {
			arg_.real = ByRefDouble.GET_DOUBLE(arg);
			arg_.imag = ICountAble.ZERO;
		}
	}

	/**Counter for the Optimizations	 */
	public static int AddOptimizations = 0;

	/**Counter for the normal Operations	 */
	public static int AddOperations = 0;

	/**Counter for the Optimizations	 */
	public static int MulOptimizations = 0;

	/**Counter for the normal Operations	 */
	public static int MulOperations = 0;

	/**Addition in Place: +=
	 * assumes null to be 0	 */
	public ISemiGroup addAt(Object arg) {
		//++AddOperations;
		//++AddOptimizations;
		if ((arg == ICountAble.Zero)
			|| (arg == CCountAble.Zero)
			|| (arg == BodyDouble.Zero)
			|| (arg == ComplexDbl.Zero)
			|| (arg == null))
			return this;
		if (arg instanceof ComplexDbl) {
			real += ((ComplexDbl) arg).real;
			imag += ((ComplexDbl) arg).imag;
		} else if (arg instanceof PolarDbl) {
			addAt(new ComplexDbl(((PolarDbl) arg)));
		} else { //check, if it is a real Result
			final double argVal = ByRefDouble.GET_DOUBLE(arg); 
			if (Double.isNaN(argVal)) { //multiplication with a Scalar  
				if (arg instanceof ISemiGroup) { //is always commutative
					return ((ISemiGroup) arg).add(this);  //Target Type knows best
				} //just return the commuted Product!
			}
			real += argVal;
		} //real Argument
		//--AddOptimizations;
		return this;
	}

	/**Subtraction in Place: -=
	 * assumes null to be 0	 */
	public IGroup subAt(Object arg) {
		//++AddOperations;
		//++AddOptimizations;
		if ((arg == ICountAble.Zero)
			|| (arg == CCountAble.Zero)
			|| (arg == BodyDouble.Zero)
			|| (arg == ComplexDbl.Zero)
			|| (arg == null))
			return this;
		if (arg instanceof ComplexDbl) {
			ComplexDbl arg_ = (ComplexDbl) arg;
			real -= arg_.real;
			imag -= arg_.imag;
		} else {//real Argument
			final double argVal = ByRefDouble.GET_DOUBLE(arg);
			if (Double.isNaN(argVal)) { //multiplication with a Scalar  
				if (arg instanceof ISemiGroup) synchronized (this){ //is always commutative
					negAt(); //Target Type knows best
					final ISemiGroup ret = ((ISemiGroup) arg).add(this); //.neg());  
					negAt(); //Undo Negation
					return (IGroup) ret;
				} //just return the commuted Product!
			}
			real -= argVal;
			//TODO: check, if it is a real Result
		}
		//--AddOptimizations;
		return this;
	}

	/**Multiplication in Place: *=
	 * assumes null to be 0	 */
	public ISemiGroupM mulAt(Object arg) { //Check if the Argument is a scalar, before converting it to a ComplexDbl
		//++MulOperations;
		//++MulOptimizations;
		if ((arg == ICountAble.One)
			|| (arg == CCountAble.One)
			|| (arg == BodyDouble.One)
			|| (arg == ComplexDbl.One))
			return this;
		if ((arg == ICountAble.Zero)
			|| (arg == CCountAble.Zero)
			|| (arg == BodyDouble.Zero)
			|| (arg == ComplexDbl.Zero)
			|| (arg == null)) {
			zeroAt();
			return this;
		}
		if ((arg == ICountAble._One)
			|| (arg == CCountAble._One)
			|| (arg == BodyDouble._One)
			|| (arg == ComplexDbl._One)) {
			negAt();
			return this;
		}
		if (arg == I)
			return (ISemiGroupM) mulIAt();
		if (arg == _I)
			return (ISemiGroupM) divIAt();
		if (arg instanceof ComplexDbl) {
			ComplexDbl arg_ = (ComplexDbl) arg;
			double tmp = real;
			real = real * arg_.real - arg_.imag * imag;
			imag = imag * arg_.real + arg_.imag * tmp;
			// check, if it is a real Result
		} else { //Real Argument
			final double argVal = ByRefDouble.GET_DOUBLE(arg); 
			if (Double.isNaN(argVal)) { //multiplication with a Scalar  
				if (arg instanceof ISemiGroupM) { //is always commutative
					return ((ISemiGroupM) arg).mul(this);  //Target Type knows best
				} //just return the commuted Product!
			}
			real *= argVal;
			imag *= argVal;
		}
		
		//		--MulOptimizations;
		return this;
	}

	/**Division in Place: /=
	 * assumes null to be 1
	 * obige Implementation vermeidet Genauigkeitsverlust und einen �berlauf durch die Quadrierung
	 * und spart au�erdem effektiv 2 Sqr und wendet nur 1 Vergleich mehr an als andere.	 */
	public IGroupM divAt(Object arg) {
		//		++MulOperations;
		//		++MulOptimizations;
		if ((arg == ICountAble.One)
			|| (arg == CCountAble.One)
			|| (arg == BodyDouble.One)
			|| (arg == ComplexDbl.One))
			return this;
		if ((arg == ICountAble.Zero)
			|| (arg == CCountAble.Zero)
			|| (arg == BodyDouble.Zero)
			|| (arg == ComplexDbl.Zero)
			|| (arg == null)) {
			InfinityAt();
			return this;
		}
		if ((arg == ICountAble._One)
			|| (arg == CCountAble._One)
			|| (arg == BodyDouble._One)
			|| (arg == ComplexDbl._One)) {
			negAt();
			return this;
		}
		if (arg == I)
			return divIAt();
		if (arg == _I)
			return mulIAt();
		if (arg instanceof ComplexDbl) {
			ComplexDbl arg_ = (ComplexDbl) arg;
			if (!(bolLazySimplify && arg_.imag == ICountAble.ZERO)) {
				double ratio = arg_.real / arg_.imag; //|Skalar| < 1 ?
				if (Math.abs(ratio) > 1) {
					double den = arg_.imag + arg_.real * ratio;
					double tmp = this.real;
					this.real = (this.real * ratio + this.imag) / den; 
					this.imag = (this.imag * ratio - tmp) / den;
				} else {
					double den = arg_.real + arg_.imag / ratio;
					double tmp = this.real;
					this.real = (this.real + this.imag / ratio) / den;
					this.imag = (this.imag - tmp / ratio) / den;
				}
				//			if (!bolLazySimplify && (Imag == ICountAble.ZERO)) return Real;  // check, if it is a real Result:
			} else { //Real Argument, no real Result
				real /= arg_.real;
				imag /= arg_.real;
			}
		} else { //Real Argument, no real Result
			final double argVal = ByRefDouble.GET_DOUBLE(arg); 
			if (Double.isNaN(argVal)) { //multiplication with a Scalar  
				if (arg instanceof ISemiGroupM) synchronized (this){ //is always commutative
					invAt(); //Target Type knows best
					final ISemiGroupM ret = ((ISemiGroupM) arg).mul(this); //.inv());  
					invAt(); //Undo Negation
					return (IGroupM) ret;
				} //just return the commuted Product!
			}
			real /= argVal;
			imag /= argVal;
		}
		//		--MulOptimizations;
		return this;
	}

	//These two implementations avoid the explicit Conjugation of the second argument
	//and thus save an additional call, negation and saving Operation.
	//They could be used in Matrix Operations
	//instead of the explicit Conjugation of the Tensor's Elements.
	//although it would be generally easier if the Conjugation took place once
	//and consciously chosen by the Programmer instead of implicitly.

	/**Addition of the conjugate complex argument in Place: +=	 */
	public IIntRing addAtCjg(Object arg) {
		//Check if the Argument is a scalar, before converting it to a ComplexDbl
		if (arg instanceof ComplexDbl) {
			ComplexDbl tmp = (ComplexDbl) arg;
			real += tmp.real;
			imag -= tmp.imag;
		} // check, if it is a real Result
		else
			real += ByRefDouble.GET_DOUBLE(arg); //Real Argument
		return this;
	}

	/**Subtraction of the conjugate complex argument in Place: -=	 */
	public IIntRing subAtCjg(Object arg) { //Check if the Argument is a scalar, before converting it to a ComplexDbl
		if (arg instanceof ComplexDbl) {
			ComplexDbl tmp = (ComplexDbl) arg;
			real -= tmp.real;
			imag += tmp.imag;
		} // check, if it is a real Result
		else
			real -= ByRefDouble.GET_DOUBLE(arg); //Real Argument
		return this;
	}

	/**Multiplication by the conjugate complex argument in Place: *=	 */
	public IIntRing mulAtCjg(Object arg) { //Check if the Argument is a scalar, before converting it to a ComplexDbl
		if (arg instanceof ComplexDbl) {
			ComplexDbl arg_ = (ComplexDbl) arg;
			double tmp = real;
			real = (tmp = real) * arg_.real + (arg_.imag * imag);
			imag = imag * arg_.real - (arg_.imag * tmp);
			// check, if it is a real Result
		} else { //Real Argument
			double tmp;
			real *= (tmp = ByRefDouble.GET_DOUBLE(arg));
			imag *= tmp;
		}
		return this;
	}

	/**Division by the conjugate complex argument in Place: /=
	 * obige Implementation vermeidet Genauigkeitsverlust und einen �berlauf durch die Quadrierung
	 * und spart au�erdem effektiv 2 Sqr und wendet nur 1 Vergleich mehr an als andere.	 */
	public IIntRing divAtCjg(Object arg) {
		if (arg instanceof ComplexDbl) {
			ComplexDbl arg_ = (ComplexDbl) arg;
			if (!(bolLazySimplify && arg_.imag == ICountAble.ZERO)) {
				double Skalar = arg_.real / arg_.imag; //|Skalar| < 1 ?
				double Faktor = arg_.imag + Skalar * arg_.real;
				double Helfer = imag;
				imag = (imag * Skalar + real) / Faktor;
				real = (real * Skalar - Helfer) / Faktor;
				//				if (!bolLazySimplify && Imag.isZero()) return (GroupM) Real;  // check, if it is a real Result:
			} else { //Real Argument, no real Result
				real /= arg_.real;
				imag /= arg_.real;
			}
		} else { //Real Argument, no real Result
			double tmp;
			real /= (tmp = ByRefDouble.GET_DOUBLE(arg));
			imag /= tmp;
		}
		return this;
	}

	/**Inversion in Place: 1/x
	 * obige Implementation ist genauer und verhindert einen �berlauf, ersetzt aber effektiv
	 * 1 Division durch eine Inversion und 1 Vergleich	 */
	public IGroupM invAt() { //loses the Reference to 'Real', because it doesn't use copyAt
		if (!(bolLazySimplify && (imag == ICountAble.ZERO))) {
			double Skalar = real / imag;
			imag = ICountAble._ONE / (imag + real * Skalar);
			real = - (Skalar * imag);
		} else
			real = ICountAble.ONE / real;
		return this;
	} // no check, if it is a real Result! not possible.

	//virtual Methods of Object

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * NewInstance also clones the Types, but does not initialize them!
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() {
		return new ComplexDbl();
	}

	/**Returns a new instance with both real and imaginary parts set to random values in [-1, 1].
	 * @see streamIO.copy.IICopyAble#randomizeAt()	 */
	public ICopyAble randomizeAt() { return new ComplexDbl(ByRefDouble.RANDOM_1_1(), ByRefDouble.RANDOM_1_1()); }
	
	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Constituents.
	 * It keeps the current reference, so any Argument has to be converted to ComplexDbl!	 */
	public ICopyAble copyAt(
		Object arg,
		int Depth) { //DeepCopy, ripples through, by using copyAt() on all Elements.
		//		super = arg);	//not necessary, since all these Fields apply only to Integers.
		convertArg(arg);
		imag = arg_.imag; //Imag = (ComplexDbl) arg_.Imag.copy();	//Doesn't matter,
		real = arg_.real; //Real = (ComplexDbl) arg_.Real.copy();	//only for Performance!
		return this;
	}

	/**Does a shallow Copy of the Argument.
	 * I.e. both Instances will share their inner Components.	 */
	public ICopyAble shallowCopyAt(Object arg) { //don't rely on the Argument being a ComplexDbl
		//		super.shallowCopyAt(arg);	//not necessary, since all these Fields apply only to Integers.
		convertArg(arg);
		real = arg_.real;
		imag = arg_.imag;
		return this;
	}

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
		return ((int) Double.doubleToLongBits(imag)) ^ ((int) Double.doubleToLongBits(real));
	}

	/**Separator String, see also in ACopyAble.Separator	 */
	public static String Separator = ";";

	/**Returns a string representation of the object. In general, the
	 * <code>toString</code> method returns a string that
	 * "textually represents" this object. The result should
	 * be a concise but informative representation that is easy for a
	 * person to read.
	 * It is recommendedthat all subclasses override this method.
	 * <p>
	 * The <code>toString</code> method for class <code>Object</code>
	 * returns a string consisting of the name of the class of which the
	 * object is an instance, the at-sign character `<code>@</code>', and
	 * the unsigned hexadecimal representation of the hash code of the
	 * object.
	 *
	 * @return  a string representation of the object.
	 * @since   JDK1.0	 */
	public String toString() {
		return "(" + real + ", " + imag + ")";
	}
	//	return Starter + Real + Separator + Imag + Stopper;}

	/**Parses the String with the full Description to a ComplexDbl Number.
	 * of two Object as Input for Denominator and Numerator.
	 * Those Objects should be of the same Type to speed up calculation. */
	public ICopyAble fromStreamAt(java.io.StreamTokenizer arg) throws java.io.IOException {
		//TODO
		//	Real = Parsing.nextNumber(arg, false);
		//	Imag = Parsing.nextNumber(arg, false);
		return this;
	}

	//////////////////////
	//	Optimizations:	//
	//////////////////////

	//////////////////////
	//	Optimizations	//
	//////////////////////

	/**Returns -1:	*/
	public IIntRing _one() {
		return _One;
	};
	/**Returns  0:	*/
	public IGroup zero() {
		return Zero;
	};
	/**Returns  1:	*/
	public IGroupM one() {
		return One;
	};
	//	/**Returns  2:	*/	public IIntRing  two() {return  Two;};

	/**Negation in Place: -	 */
	public IGroup negAt() {
		real = -real;
		imag = -imag;
		return this;
	}

	/**Double in Place: x+=x == x*=2	*/
	public ISemiGroup dblAt() {
		real *= ICountAble.TWO;
		imag *= ICountAble.TWO;
		return this;
	}

	/**Triple in Place: x+=x+x == x*=3	*/
	public ISemiGroup trplAt() {
		real *= ICountAble.THREE;
		imag *= ICountAble.THREE;
		return this;
	}

	/**Triple in Place: x*=4	*/
	public ISemiGroup quadAt() {
		real *= ICountAble.FOUR;
		imag *= ICountAble.FOUR;
		return this;
	}

	/**Decrements the Value by 1	 */
	public integer dec() {
		real -= ICountAble.ONE;
		return this;
	}

	/**Inrements the Value by 1	 */
	public integer inc() {
		real += ICountAble.ONE;
		return this;
	}

	/**Residual in Place: 1-x	 */
	public integer ResidAt() {
		real = ICountAble.ONE - real;
		imag = -imag;
		return this;
	}

	//Only the real part counts, this is at least valid for the Calculation of ArcTan

	/**Returns whether the real part is negative; the imaginary part is not considered.	 */
	public boolean negative() {
		return (real < ICountAble.ZERO);
	} // && Imag.negative());}
	/**Returns whether the real part is positive; the imaginary part is not considered.	 */
	public boolean positive() {
		return (real > ICountAble.ZERO);
	} // && Imag.positive());}

	/**Sets this to the real value 2, in Place, zeroing the imaginary part.	 */
	public IIntRing twoAt() {
		real = ICountAble.TWO;
		imag = ICountAble.ZERO;
		return this;
	}
	/**Sets this to the real value 3, in Place, zeroing the imaginary part.	 */
	public IIntRing threeAt() {
		real = ICountAble.THREE;
		imag = ICountAble.ZERO;
		return this;
	}

	/**Half in Place: x/=2	*/
	public IIntRing halfAt() {
		real *= IMeasurAble.HALF;
		imag *= IMeasurAble.HALF;
		return this;
	}

	/**Third in Place: x/=3	*/
	public IIntRing thirdAt() {
		real *= IMeasurAble.THIRD;
		imag *= IMeasurAble.THIRD;
		return this;
	}

	/**Applies the Mandelbrot/Julia iteration step z = z*z + c to this value, in Place.
	 * @return the Apple Function x*x+c in Place */
	public ComplexDbl AppleFuncAt(ComplexDbl c) {
		double reIm = real * imag;
		real = c.real + real * real - imag * imag;
		imag = c.imag + reIm + reIm; //replace Multiplication by Addition
		return this;
	}

	/**Square in Place: x^2 == x*=x
	 * (a+ib)^2 == a^2 + 2i ab - b^2 == (a+b)(a-b) + 2i(ab)	 */
	public ISemiGroupM sqrAt() {
		double Hilf = real * ICountAble.TWO; //no copy, because the value stays the same
		real = (real - imag) * (real + imag); //instead of r*r-i*i
		imag *= Hilf;
		return this;
	} //No ggT for Real or Imag => only rounding.

	/**Cubic in Place: x*=x^3
	 * (a+ib)^3 == a(a^2-3b^2)+ib(3a^2-b^2)	 */
	public ISemiGroupM cbcAt() {
		double reSqr = real * real;
		double imSqr = imag * imag;
		real *= reSqr - imSqr * ICountAble.THREE;
		imag *= reSqr * ICountAble.THREE - imSqr;
		return this;
	}

	/**Standard Implementation: a+ib-c+id == 0
	 * Faster: a == c && b == d
	 * but less correct when it comes to rounding!	 */
	public boolean equals(Object arg) {
		if (arg instanceof ComplexDbl) {
			ComplexDbl arg_ = (ComplexDbl) arg;
			return ((real == arg_.real) && (imag == arg_.imag));
		}
		if (imag != ICountAble.ZERO)
			return false;
		return real == ByRefDouble.GET_DOUBLE(arg);
	}

	/**Checks if the ComplexDbl Number is even.
	 * i.e. both real and imaginary part are even	 */
	public boolean isEven() {
		return (((real % 2) == ICountAble.ZERO) && ((imag % 2) == ICountAble.ZERO));
	}

	/**Checks if the ComplexDbl Number is odd.
	 * i.e. both real and imaginary part are odd	 */
	public boolean isOdd() {
		return (((real % 2) != ICountAble.ZERO) && ((imag % 2) != ICountAble.ZERO));
	}

	//left out: ModAtDivAt, ModlAt, kgV, ggT, IntAt == FloorAt

	/**Returns the Square Root of this: x^=.5	*/
	public IMetricIRing SqRt() {
		return ((IMetricIRing) copy()).SqRtAt();
	}

	/**Returns the Square Root of this in Place: x^=.5	*/
	public IMetricIRing SqRtAt() {
		if (imag == ICountAble.ZERO) { //this case has to be considered separately!
			if (real >= ICountAble.ZERO)
				real = Math.sqrt(real); //positive => real Root
			else {
				imag = Math.sqrt(-real);
				real = ICountAble.ZERO;
			} //negative => imag Root
			return this;
		}
		boolean rNeg = (real < ICountAble.ZERO);
		double f = rNeg ? -real * IMeasurAble.HALF : real * IMeasurAble.HALF;
		boolean iNeg = (imag < ICountAble.ZERO);
		double g = iNeg ? -imag * IMeasurAble.HALF : imag * IMeasurAble.HALF;
		if (f < g) { //f and g are the absolute half Values
			f /= g;
			f = Math.sqrt(g * (f + Math.sqrt(ICountAble.ONE + f * f)));
		} else {
			g /= f;
			f = Math.sqrt(f * (ICountAble.ONE + Math.sqrt(ICountAble.ONE + g * g)));
		}
		if (rNeg) //w�hlt immer die L�sung mit der kleinsten Phase aus !
			if (iNeg) {
				real = -imag / (f + f);
				imag = -f;
			} else {
				real = imag /= (f + f);
				imag = f;
			}
		else {
			imag /= (f + f);
			real = f;
		}
		return this;
	}

	/*	Public Function cCbcRtAt() As clsComplex
	'uses the complex Exponential and Logarithmic Functions
	'    Self = Pol(Self)
	'    Self.re = Exp(Ln(Self.re) / Drei)
	'    Self.im = Self.im / Drei
	'    cCbcRt = Rec(Self)
	End Function
	*/

	/**p-Norm: Defined as Sum(|x|^p)^1/p
	 * Generic Norm: the other Norms are Special Cases:
	 * In 1-dimensional Spaces all Norms fall together.	 */
	public IMetricIRing p_Norm(double p) {
		return new BodyDouble(
			Math.pow(
				Math.pow(Math.abs(real), p) + Math.pow(Math.abs(imag), p),
				ICountAble.ONE / p));
	}

	/**Maximums-Norm
	 * Special Case of the p-Norm for p -> Infinity	 */
	public IMetricIRing Max_Norm() {
		double r, i;
		return new BodyDouble(((r = Math.abs(real)) < (i = Math.abs(imag)) ? i : r));
	}

	/**(Euklidische Norm)^2
	 * Special Case of the p-Norm for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public IMetricIRing SqrNorm() {
		return new BodyDouble(SqrNormDbl());
	}

	/**(Euklidische Norm)^2
	 * Special Case of the p-Norm for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public double SqrNormDbl() {
		return real * real + imag * imag;
	}

	/**absolute Value in Place:				 |x|
	 * Returns the fastest Norm, which is the AbsV_Norm	 */
	public IScalarMetric AbsVAt() { //((CopyAble)Real).ShallowCopyAt(AbsV());
		real = Math.abs(real) + Math.abs(imag);
		imag = ICountAble.ZERO; //still keep this a sensible Representation!
		return new BodyDouble(real);
		//		return this;
	}

	/**absolute Value: |x|
	 * Returns the fastest Norm, which is the AbsV_Norm	 */
	public IScalarMetric AbsV() {
		//	return (Group) ((MetricIRing)SqrAbsV()).SqRt();}
		return new BodyDouble(AbsVDbl());
	}

	/**absolute Value: |x|
	 * Returns the fastest Norm, which is the AbsV_Norm	 */
	public double AbsVDbl() {
		//	return (Group) ((MetricIRing)SqrAbsV()).SqRt();}
		//	return Math.abs(Real) + Math.abs(Imag); }
		/*	double ret = (Real >= 0) ? Real : -Real;
			if (Imag >= 0) { //even faster not to call abs()? Should be left to the Compiler!
				return ret + Imag; }
				return ret - Imag; }
		*/
		if (real >= 0) {
			if (imag >= 0) { //even faster not to call abs()? Should be left to the Compiler!
				return real + imag;
			}
			return real - imag;
		}
		if (imag >= 0) { //
			return imag - real;
		}
		return -imag - real;
	}

	/**Carry the Overflow through the g-adic Representation.	 */
	public void addCarry() {
	}

	//Complement, necessary for gAdic Calculation
	/**Complement in Place: ~=	*/
	public IIntRing CmplAt() {
		throw new AbstractMethodError();
	}

	/**Returns the Value raised by one g-Adic Position	 */
	public IIntRing toUpperAt() {
		throw new AbstractMethodError();
	}

	/**Chordaler Betrag: 0 < x < 1 streng monoton steigend.
	 * Der Chordale Betrag gibt die H�he des Punktes
	 * auf der Riemannschen Zahlenkugel an.	 */
	public IIntRing chordal() {
		IIntRing tmp = (IIntRing) Norm();
		tmp.divAt(tmp.succ());
		return tmp;
	}

	//////////////////////////////////
	//	Analytical Optimizations	//
	//////////////////////////////////

	/**Returns the exponential Function: e^x
	 * This is the Inverse to the natural Logarithm ln().	 */
	public MetricBody exp() {
		PolarDbl p = new PolarDbl(Math.exp(real), imag);
		return new ComplexDbl(p);
	}

	/**Returns the natural Logarithm of x: ln(x)
	 * This is the Inverse to the exponential Function exp(x).
	 * For Arguments x near 1 use lnXP1(x) to gain Accuracy.	 */
	public MetricBody ln() {
		PolarDbl p = new PolarDbl(this);
		return new ComplexDbl(Math.log(p.r), p.ang);
	}

	/**Returns the Sinus of the angle x: sin(x)	 */
	public MetricBody sin() {
		if (imag == ICountAble.ZERO)
			return new BodyDouble(Math.sin(real));
		if (real == ICountAble.ZERO)
			return new ComplexDbl(ICountAble.ZERO, SinH.SinH.Map(imag));
		ComplexDbl Sin = new ComplexDbl();
		double S = ByRefDouble.SIN_COS(real, BR); // Math.sin(Real);
		Sin.imag = BR.Value; // Math.cos(Real);
		double SH = ByRefDouble.SINH_COSH(imag, BR);
		Sin.real = BR.Value * S; //re = sin (re)*cosh (im)
		Sin.imag *= SH; //im = cos (re)*sinh (im)
		return Sin;
	}

	/**Returns the Cosinus of the angle x: cos(x)	 */
	public MetricBody cos() {
		if (imag == ICountAble.ZERO)
			return new BodyDouble(Math.cos(real));
		if (real == ICountAble.ZERO)
			return new ComplexDbl(ICountAble.ZERO, CosH.COS_H(imag));
		ComplexDbl Cos = new ComplexDbl();
		double SH = ByRefDouble.SINH_COSH(imag, BR);
		double CH = BR.Value;
		Cos.imag = ByRefDouble.SIN_COS(real, BR);
		Cos.real = BR.Value;
		Cos.imag *= -SH; //im =-sin (re)*sinh (im)
		Cos.real *= CH; //re = cos (re)*cosh (im)
		return Cos;
	}

	/**Returns the Sinus Hyperbolicus of this number: SinH(x)	 */
	public MetricBody SinH() {
		if (imag == ICountAble.ZERO)
			return new BodyDouble(SinH.SinH.Map(real));
		if (real == ICountAble.ZERO)
			return new ComplexDbl(ICountAble.ZERO, Math.sin(imag));
		ComplexDbl SinH = new ComplexDbl();
		SinH.real = ByRefDouble.SINH_COSH(real, BR);
		double CH = BR.Value;
		SinH.imag = ByRefDouble.SIN_COS(imag, BR) * CH; //im = CosH (re)*sin (im)
		SinH.real *= BR.Value; //re = SinH (re)*cos (im)
		return SinH;
	}

	/**Returns the Cosinus Hyperbolicus of this number: CosH(x)	 */
	public MetricBody CosH() {
		if (imag == ICountAble.ZERO)
			return new BodyDouble(CosH.COS_H(real));
		if (real == ICountAble.ZERO)
			return new ComplexDbl(ICountAble.ZERO, Math.cos(imag));
		ComplexDbl CosH = new ComplexDbl();
		CosH.imag = ByRefDouble.SIN_COS(imag, BR);
		double C = BR.Value;
		CosH.imag *= ByRefDouble.SINH_COSH(real, BR); //im = SinH (re)*sin (im)
		CosH.real = BR.Value * C; //re = CosH (re)*cos (im)
		return CosH;
	}

	/**Returns the Tangens of the angle x: tan == sin / cos == sin/(1-sin^2)^1/2	*/
	public MetricBody tan() {
		if (imag == ICountAble.ZERO)
			return new BodyDouble(Math.tan(real));
		if (real == ICountAble.ZERO)
			return new ComplexDbl(ICountAble.ZERO, TanH.TanH.Map(imag));
		ComplexDbl Tan = new ComplexDbl();
		double CH;
		Tan.imag = ByRefDouble.SINH_COSH(imag + imag, BR);
		CH = BR.Value; //im = SinH(2im)/(cos (2re)+cosH (2im))
		Tan.real = ByRefDouble.SIN_COS(real + real, BR);
		CH += BR.Value; //re = sin (2re)/(cos (2re)+cosH (2im))
		Tan.imag /= CH;
		Tan.real /= CH;
		return Tan;
	}

	/**Returns the Tangens Hyperbolicus of this Number: TanH	*/
	public MetricBody TanH() {
		if (imag == ICountAble.ZERO)
			return new BodyDouble(TanH.TanH.Map(real));
		if (real == ICountAble.ZERO)
			return new ComplexDbl(ICountAble.ZERO, Math.tan(imag));
		ComplexDbl TanH = new ComplexDbl();
		double C;
		TanH.real = ByRefDouble.SINH_COSH(real + real, BR);
		C = BR.Value; //re = SinH(2re)/(CosH(2re)+cos(2im))
		TanH.imag = ByRefDouble.SIN_COS(imag + imag, BR);
		C += BR.Value; //im = sin (2im)/(CosH(2re)+cos(2im))
		TanH.imag /= C;
		TanH.real /= C;
		return TanH;
	}

	/**Returns the Arcus Tangens of the Angle x: ArcTan(x)
	 * = -i/2*Ln ((1+i*c1)/(1-i*c1))*/
	public MetricBody ArcTan() {
		if (imag == ICountAble.ZERO)
			return new BodyDouble(Math.atan(real));
		if ((real == ICountAble.ZERO) && (AbsVDbl() <= ICountAble.ONE))
			return new ComplexDbl(ICountAble.ZERO, ArTanH.ArTanH.Map(imag)); //only for |x| <= 1
		ComplexDbl xm1 = new ComplexDbl(ICountAble.ONE + imag, -real);
		ComplexDbl xp1 = new ComplexDbl(ICountAble.ONE - imag, real);
		xp1.divAt(xm1);
		xp1.lnAt().halfAt();
		xm1.real = xp1.imag;
		xm1.imag = -xp1.real;
		return xm1;
	}

	/**Returns the Arcus Sinus of the Angle x: ArcSin(x)	 */
	public MetricBody ArcSin() {
		if (real == ICountAble.ZERO)
			return new ComplexDbl(ICountAble.ZERO, ArSinH.ArSinH.Map(imag));
		if ((imag == ICountAble.ZERO) && (AbsVDbl() <= ICountAble.ONE))
			return new BodyDouble(ArcSin.ARC_SIN.Map(real));
		return super.ArcSin(); //The Power Series always converges...
	}

	/**Returns the Arcus Cosinus of the Angle x: ArcCos(x)	 */
	/*	public MetricBody ArcCos()//{return ((MetricBody)copy()).ArcCosAt();}
	{//no sense to define the Simplifications here, since the Definition and Value Range of Sin and Cos are restricted with real Numbers
		if (Imag.isZero()) return ((MetricBody) Real).ArcCos();
		if (Real.isZero()) return ((MetricBody) Imag).ArCosH();
		return super.ArcCos(); }
	*/

	/**Returns both the Sinus and Cosinus: Cos, Sin
	 * This is more efficient, because cos^2+sin^2 = 1	 */
	public MetricBody Cos_Sin(MetricBody Sin_) {
		ComplexDbl Cos = new ComplexDbl();
		ComplexDbl Sin = (ComplexDbl) Sin_;
		Sin.real = ByRefDouble.SIN_COS(real, BR);
		Cos.real = BR.Value; //Sin.re = sin (re)*cosh (im)
		Sin.imag = ByRefDouble.SINH_COSH(imag, BR); //Sin.im = cos (re)*sinh (im)
		Cos.imag = -Sin.real * Sin.imag; //Cos.im =-sin (re)*sinh (im)
		Sin.imag *= Cos.real;
		Sin.real *= BR.Value;
		Cos.real *= BR.Value; //Cos.re = cos (re)*cosh (im)
		return Cos;
	}

	/**Returns both the Sinus and Cosinus Hyperbolicus: CosH, SinH
	 * This is more efficient, because cosH^2-sinH^2=1	 */
	public MetricBody CosH_SinH(Object SinH_) {
		ComplexDbl CosH = new ComplexDbl();
		ComplexDbl SinH = (ComplexDbl) SinH_;
		SinH.real = ByRefDouble.SINH_COSH(real, BR);
		CosH.real = BR.Value; //SH.re = sinh (re)*cos (im)
		SinH.imag = ByRefDouble.SIN_COS(imag, BR); //SH.im = cosh (re)*sin (im)
		CosH.imag = SinH.real * SinH.imag; //CH.im = sinh (re)*sin (im)
		SinH.imag *= CosH.real;
		SinH.real *= BR.Value;
		CosH.real *= BR.Value; //CH.re = cosh (re)*cos (im)
		return CosH;
	}

	//Taken out, because implemented by Norm now.

	/**Square of the absolute Value in Place: |x|^2		 */
	//	public SemiGroupM SqrAbsVAt(){return (SemiGroupM)ShallowCopyAt(SqrAbsV());}

	/**Square of the absolute Value: |x|^2		 */
	//	public SemiGroupM SqrAbsV()
	//	{return (SemiGroupM) ((MetricIRing) Real.SqrAbsV()) += Imag.SqrAbsV());}

	//////////////////////////////
	//	Interface countable		//
	//////////////////////////////

	//Taken out, because Information gets lost!

	/** Returns the Object Value represented by an 8 Bit Integer	 */
	//	public byte	  getByte(){return Real.getByte();}

	/** Returns the Object Value represented by an 16 Bit Integer	 */
	//	public short getShort(){return Real.getShort();}

	/** Returns the Object Value represented by an 32 Bit Integer	 */
	//	public int	   getInt(){return Real.getInt();}

	/** Returns the Object Value represented by an 64 Bit Integer	 */
	//	public long   getLong(){return Real.getLong();}

	//////////////////////////////
	//	Interface measurable	//
	//////////////////////////////

	/**Returns the Object Value represented by a scalar Variable of Type double.
	 * It consists of an IEEE Number with 64 Bit (8 Byte):
	 * 52 Bit Mantissa, 11 Bit Exponent, 1 Bit Sign	 */
/*	public double getDouble() {
		if (Imag != ICountAble.ZERO)
			throw new AbstractMethodError();
		return Real;
	}

	/**Returns the Object Value represented by a scalar Variable of Type float.
	 * It consists of an IEEE Number with 32 Bit (4 Byte):
	 * 23 Bit Mantissa, 8 Bit Exponent, 1 Bit Sign	 */
/*	public float getFloat() {
		if (Imag != ICountAble.ZERO)
			throw new AbstractMethodError();
		return (float) Real;
	}

	/**Tests the Methods of this Class	 */
	/**Runs this class's self-tests and prints expected-versus-actual results to standard out.	 */
	public static void testIt() throws java.io.IOException {
		ComplexDbl test = new ComplexDbl(new BodyDouble(), new BodyDouble());
		testInstance = test; //defined in ACopyAble to test the abstract Methods
		testComplex();
		testRcFunc();
		testExp();
		testLn();
		testSinCos();
	}

	//////////////
	//	Testing	//
	//////////////

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
		testComplex(); 
	}
		
	/**Tests the basic complex Methods	 */
	private static void testComplex() throws java.io.IOException {
		//	double r = java.lang.Math.atan2(1,0);
		BodyDouble b1 = new BodyDouble(5); 
		ComplexDbl c1 = new ComplexDbl(0); //No empty Constructor allowed!
		ComplexDbl c2 = new ComplexDbl(0); //Test empty Constructor
		MetricBody c3; //Test empty Constructor
		c1.real = +1;
		c1.imag = -1;
		c3 = (MetricBody) c1.copy();
		Body result = (Body) b1.add(c1);
		L.n("Test der Addition von reeller & komplexer Zahl: " + result);
		L.n("Test von Copy   : Soll : (1,-1)  Ist : " + c3);
		c2 = c1;
		L.n("Test von CopyAt : Soll : (1,-1)  Ist : " + c2);
		c3 = new ComplexDbl(new PolarDbl(c1));
		L.n("Test von REC/POL  : Soll : (1,-1)  Ist : " + c3);
		c1.real = 1;
		c1.imag = 2;
		c2.real = 3;
		c2.imag = 4;
		L.n("Betrag : Soll : 7  Ist :" + c2.AbsV());
		double tmp;
		tmp = c2.real;
		c2.real = c2.imag;
		c2.imag = tmp;
		L.n("Betrag : Soll : 7  Ist :" + c2.AbsV());
		L.n("Realteil : Soll : 4  Ist :" + c2.real);
		L.n("Imagteil : Soll : 3  Ist :" + c2.imag);
		L.n("Chordaler Betrag : Soll : " + 5.0 / 6 + "  Ist : " + c2.chordal());
		tmp = c2.real;
		c2.real = c2.imag;
		c2.imag = tmp;
		//		AMetricBody x1; x1.add(c3);
		c3 = (MetricBody) c1.add(c2.sub(c1));
		L.n("Test von ADD/SUB  : Soll : (3,4)  Ist : (" + c3);
		c3 = (MetricBody) c2.div(c1).mul(c1);
		L.n("Test von MUL/DVN  : Soll : (3,4)  Ist : (" + c3);
		c3 = new ComplexDbl(0, -1);
		c3 = (MetricBody) c1.div(c3);
		L.n("Test von DVN      : Soll : (-2,1) Ist : (" + c3);
		c3 = (MetricBody) c1.div(c1.one());
		L.n("Test von DVN      : Soll : (1,2)  Ist : (" + c3);
		c3 = (MetricBody) ((IGroupM) c1.add(c2)).div(c1.add(c2));
		L.n("Test von allem    : Soll : (1,0)  Ist : (" + c3);
		c3 = (MetricBody) c1.div(c2).div(c1.div(c2));
		L.n("Test von allem    : Soll : (1,0)  Ist : (" + c3);
		c3 = (MetricBody) c2.inv().inv();
		L.n("Test von INV      : Soll : (3,4)  Ist : (" + c3);
		c3 = (MetricBody) c1.mul(c2).mul(c2.inv());
		L.n("Test von MUL/INV  : Soll : (1,2)  Ist : (" + c3);
		System.in.read();
		c3 = new ComplexDbl(0, -1);
		c3 = (MetricBody) c3.inv();
		L.n("Test von INV      : Soll : (0,1)  Ist : (" + c3);
		c3 = (MetricBody) c3.one().inv(); //Converts to a Scalar, so the Conversion is
		L.n("Test von INV      : Soll : (1,0)  Ist : " + c3);
		c3 = (MetricBody) c2.i().inv();
		L.n("Test von INV      : Soll : (0,-1) Ist : (" + c3);
		c3 = (MetricBody) c2.SqRt().sqr();
		L.n("Test von Sqr/SqRt : Soll : (3,4)  Ist : (" + c3);
		c3 = (MetricBody) ((ComplexDbl) c2.sqr()).SqRt();
		L.n("Test von Sqr/SqRt : Soll : (3,4)  Ist : (" + c3);
		c3 = (MetricBody) ((ComplexDbl) c2.cbc()).CbcRt();
		L.n(
			"Test von Cbc/CbcRt: Soll : (3,4)  Ist : (" + c3 + " oder um 120� verschoben");
		c3 = (MetricBody) c2.CbcRt().cbc();
		L.n("Test von Cbc/CbcRt: Soll : (3,4)  Ist : (" + c3);
	};

	/**Tests the complex Exponential Function	 */
	private static void testExp() throws java.io.IOException {
		ComplexDbl c1 = new ComplexDbl(0.0); //Test empty Constructor
		ComplexDbl c2 = new ComplexDbl(0.0); //Test empty Constructor
		//	ComplexDbl c3;	//Test empty Constructor
		L.n("Test von ComplexDbl Exp :");
		c1.zeroAt();
		c2 = (ComplexDbl) c1.exp();
		L.n("Soll : (1,0)  Ist : (" + c2.real + ";" + c2.imag + ")");
		c1.oneAt();
		c2 = (ComplexDbl) c1.exp();
		L.n(
			"Soll : (" + java.lang.Math.E + ";0)  Ist : (" + c2.real + ";" + c2.imag + ")");
		c1.iAt();
		c2 = (ComplexDbl) c1.exp();
		L.n(
			"Soll : ("
				+ java.lang.Math.cos(1)
				+ ";"
				+ java.lang.Math.sin(1)
				+ ")  Ist : ("
				+ c2.real
				+ ";"
				+ c2.imag
				+ ")");
	};

	/**Tests the complex Logarithm Function	 */
	private static void testLn() throws java.io.IOException {
		ComplexDbl c1 = new ComplexDbl(0.0); //Test empty Constructor
		ComplexDbl c2 = new ComplexDbl(0.0); //Test empty Constructor
		//	ComplexDbl c3;	//Test empty Constructor
		L.n("Test von Ln :");
		c1.zeroAt();
		c2 = (ComplexDbl) c1.ln();
		L.n(
			"Soll : ("
				+ java.lang.Double.NEGATIVE_INFINITY
				+ ";0)  Ist : ("
				+ c2.real
				+ ";"
				+ c2.imag
				+ ")");
		c1.oneAt();
		c2 = (ComplexDbl) c1.ln();
		L.n("Soll : (0,0)  Ist : (" + c2.real + ";" + c2.imag + ")");
		c1.iAt();
		c2 = (ComplexDbl) c1.ln();
		L.n(
			"Soll : (0;" + java.lang.Math.PI / 2 + ")  Ist : (" + c2.real + ";" + c2.imag + ")");
		c1.real = 3;
		c1.imag = 4;
		c2 = (ComplexDbl) c1.ln();
		L.n(
			"Soll : ("
				+ Math.log(5)
				+ ","
				+ 0.927295218001612
				+ ")  Ist : ("
				+ c2.real
				+ ";"
				+ c2.imag
				+ ")");
	};

	/**Tests the complex Sine and Cosine Function	 */
	private static void testSinCos() throws java.io.IOException {
		ComplexDbl c1 = new ComplexDbl(0.0); //Test empty Constructor
		MetricBody c2 = new ComplexDbl(0.0); //Test empty Constructor
		//	MetricBody c3;	//Test empty Constructor
		L.n(
			"Test von SinH,CosH,TanH,CotH,ArSinH,ArCosH,ArTanH,ArCotH,Sin,Cos,Tan,Cot,ArSin,ArCos,ArTan,ArCot :");
		for (int Z1 = -2; ++Z1 <= +1;) {
			for (int Z2 = -2; ++Z2 <= +1;) {
				c1.real = Z1;
				c1.imag = Z2;
				c1.quarterAt();
				L.n("Soll : " + c1);
				L.n(
					"SinH = "
						+ (c2 = c1.SinH())
						+ "; ArSinH = "
						+ (c2 = c2.ArSinH())
						+ "; SinH = "
						+ c2.SinH());
				L.n(
					"CosH = "
						+ (c2 = c1.CosH())
						+ "; ArCosH = "
						+ (c2 = new ComplexDbl(c2).ArCosH())
						+ "; CosH = "
						+ c2.CosH());
				L.n(
					"TanH = "
						+ (c2 = c1.TanH())
						+ "; ArTanH = "
						+ (c2 = c2.ArTanH())
						+ "; TanH = "
						+ c2.TanH());
				//				L.n ("CotH = " + (c2 = c1.CotH()) + "; ArCotH = " + (c2 = c2.ArCotH()) + "; CotH = " + c2.CotH());
				L.n(
					"ArSinH = "
						+ (c2 = c1.ArSinH())
						+ "; SinH = "
						+ (c2 = c2.SinH())
						+ "; ArSinH = "
						+ c2.ArSinH());
				L.n(
					"ArCosH = "
						+ (c2 = c1.ArCosH())
						+ "; CosH = "
						+ (c2 = c2.CosH())
						+ "; ArCosH = "
						+ new ComplexDbl(c2).ArCosH());
				L.n(
					"ArTanH = "
						+ (c2 = c1.ArTanH())
						+ "; TanH = "
						+ (c2 = c2.TanH())
						+ "; ArTanH = "
						+ c2.ArTanH());
				//				L.n ("ArCotH = " + (c2 = c1.ArCotH()) + "; CotH = " + (c2 = c2.CotH()) + "; ArCotH = " + c2.ArCotH());
				L.n(
					"Sin = "
						+ (c2 = c1.sin())
						+ "; ArSin = "
						+ (c2 = c2.ArcSin())
						+ "; Sin = "
						+ c2.sin());
				L.n(
					"Cos = "
						+ (c2 = c1.cos())
						+ "; ArCos = "
						+ (c2 = new ComplexDbl(c2).ArcCos())
						+ "; Cos = "
						+ c2.cos());
				L.n(
					"Tan = "
						+ (c2 = c1.tan())
						+ "; ArTan = "
						+ (c2 = c2.ArcTan())
						+ "; Tan = "
						+ c2.tan());
				//				L.n ("Cot = " + (c2 = c1.cot()) + "; ArCot = " + (c2 = c2.ArcCot()) + "; Cot = " + c2.cot());
				L.n(
					"ArcSin = "
						+ (c2 = c1.ArcSin())
						+ "; Sin = "
						+ (c2 = c2.sin())
						+ "; ArcSin = "
						+ c2.ArcSin());
				L.n(
					"ArcCos = "
						+ (c2 = c1.ArcCos())
						+ "; Cos = "
						+ (c2 = c2.cos())
						+ "; ArcCos = "
						+ new ComplexDbl(c2).ArcCos());
				L.n(
					"ArcTan = "
						+ (c2 = c1.ArcTan())
						+ "; Tan = "
						+ (c2 = c2.tan())
						+ "; ArcTan = "
						+ c2.ArcTan());
				//				L.n ("ArcCot = " + (c2 = c1.ArcCot()) + "; Cot = " + (c2 = c2.cot()) + "; ArcCot = " + c2.ArcCot());
				System.in.read();
				System.in.read();
			}
		}
	};

	/**Tests the ComplexDbl Methods with real Arguments	 */
	private static void testRcFunc() {
		ComplexDbl c1 = new ComplexDbl(0.0); //Test empty Constructor
		ComplexDbl c2 = new ComplexDbl(0.0); //Test empty Constructor
		ComplexDbl c3; //Test empty Constructor
		L.n("Test von add, mul, div:");
		c1.real = 3;
		c1.imag = 2;
		c2 = (ComplexDbl) c1.add(new Double(7));
		L.n("Soll : (10,2)  Ist : ("+c2.real+";"+c2.imag+")");
		c2 = (ComplexDbl) c2.sub(new Double(7));
		L.n("Soll : (3,2)  Ist : ("+c2.real+";"+c2.imag+")");
		c1.real = 4;
		c1.imag = 3;
		c2 = (ComplexDbl) c1.mul(new Double(5));
		L.n("Soll : (20,15)  Ist : ("+c2.real+";"+c2.imag+")");
		c2 = (ComplexDbl) c2.div(new Double(5));
		L.n("Soll : (4,3)  Ist : ("+c2.real+";"+c2.imag+")");
		c3 = (ComplexDbl) c1.one().div(new Double(5));
		L.n("Soll : ( 0.2, 0)  Ist : ("+c3.real+ ";"+c3.imag+")");
		c3 = (ComplexDbl) c1.i().div(new Double(5));
		L.n("Soll : ( 0, 0.2)  Ist : ("+c3.real+ ";"+c3.imag+")");
		c1.real = 0;
		c1.imag = 1;
		c2 = (ComplexDbl) c1.mul(new Double(5));
		L.n("Soll : (0,5)  Ist : ("+c2.real+";"+c2.imag+")");
		c2 = (ComplexDbl) c2.div(new Double(-5));
		L.n("Soll : (0,-1)  Ist : ("+c2.real+";"+c2.imag+")");
	};
}
