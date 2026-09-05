package streamIO.copy.boole.fuzzy;

import streamIO.Assert;
import streamIO.Log;
import streamIO.object.IStreamIn;
import function.IFloatFunction;
import function.byref.ByRefFloat;

/**
 * Class representing a fuzzy 1-dim. (scalar) Number or Interval 
 * by defining it's Membership / Degree (not Probability) with Range [0,1] 
 * by a Triangle(!) on a compact 1D Carrier of scalar Numbers. 
 * This is similar to the Class IntervalA which defines a Range on which 
 * the boolean Value for the Number reaches 'true' for Error Calculation etc.
 * Instead of [0,1] any 1-dim metric Space could have been used 
 * to operate with fuzzy Rules. 
 * Higher-dimensional Spaces can be constructed as Vectors of lesser Spaces. 
 * @see streamIO.copy.boole.fuzzy.IFuzzifier for a generic fuzzy Predicate Function. 
 * @see tester.ITester for a generic crisp Predicate Function. 
 * 
 * The typical Crisp Processing System works like this: 
 * a crisp boolean Vector Input (on/off) derived from Measurements 
 * a Set of crisp Rules IF ... THEN 
 * a crisp boolean Vector Output (on/off) 
 * unfortunately this can lead to unwanted Oscillations 
 * when used as a Controller for a feedback System. 
 * 
 * The typical Fuzzy Processing System works like this: 
 * a fuzzy Vector Input derived from Measurements (Fuzzification, regular ) 
 * a Set of fuzzy Rules IF ... THEN 
 * a fuzzy boolean Vector Output to control a System in a smooth Manner. 
 * this Vector can be calculated in different ways (Defuzzification) 
 * At last there can also be selected a 'crisp' Value 
 * based on the fuzzy Result, nearly as if you had only used crisp Rules, 
 * but possibly handling of Range Input Values or forgotten Combinations better. 
 * 
 * Fuzzy Rules allow to apply 'Rules of Operation by Thumb' 
 * in an automated Manner! 
 * 
 * The alternative to a Fuzzy System would be to solve the Differential Equation
 * of the System and construct an appropriate Controller, 
 * but apart from the Fact that sometimes the System is not well known, 
 * the Solution might work only for a certain Range of Operation, 
 * so a smooth Change between Solutions is necessary anyway! 
 * 
 * Sources of Uncertainty can be:
 * probabilistic: i.e. random, not cohesive.
 * lexicalic: i.e. Definition of Terms (cognitive Problem), this corresponds to 'fuzzy'.
 * Dependence on the Context: this must be avoided by clearing the Context!
 *
 * One Difference to Probability Theory is the Fact that 
 * Probabilities of disjoint Events must add up to 1 (Normalization), 
 * whereas although Fuzzy Values are also limited between 0 to 1, 
 * since only min and max are used, no Normalization is necessary. 
 * Another Difference is that an Individual is considered. 
 * A very good Example is the Creation of Terms and Sets from sensed Contexts.
 * The Problem of creating Information from (subjective or objective) Sensus 
 * is a fundamental philosophical Problem
 * but due to it's Universality it is usually not considered.
 *
 * Consider the Term of the 'Species' of an Animal:
 * Species and Races are Names for special biologically motivated Sets.
 * Species are defined such that Individuals of the same Species can have Offspring together.
 * Races are defined by their physiological Specialties.
 * Evolution shows that different Species result from the same Ancestors.
 * The Question now is:
 * when does a new Species develop and
 * when does it make sense do define a new Species and
 * how do you assign a Species to an Individual?
 *
 * Fuzzy Sets can help here, because they allow the Definition of smoothly bordered Sets,
 * thus modelling the Definitions of everyday Sets.
 *
 * Computer Models are usually numeric or algebraic Models with exact Values.
 * One Application of Fuzzy Logic is the Transformation of Sensus into Data within these Models.
 * Fuzzifier are used for that.
 *
 * Following the 80/20 Rule (Pareto Principle) it is mostly not necessary
 * to know the Set or the Element Relation completely.
 * The Pareto Principle is an Example of applied Fuzzy Logic: TODO:
 *
 * 'Knowledge' is to a great Extent defined by the Number of Connotations
 * a Mind can make to concrete Examples and References.
 *
 * Here the Distribution of Probabilities is approximated by a Triangle,
 * which requires the Use of Probabilities different from 0 and 1.
 * In IntervalA a Rectangle is used,
 * so the Calculations don't require a Fuzzyfier.
 *
 * To ensure correct Treatment of the Values returned from this Class,
 * Values of Type Fuzzy should be used (and returned by this Class).
 *
 * Applications for fuzzy Sets:
 * *Using fuzzy Definitions e.g. in social Sciences
 * *Artificial Intelligence and Expert Systems have to be fed with Knowledge
 *   this Knowledge is usually fuzzy, so the Terms used have to be fuzzy.
 * *Fuzzy Control: for this the exact numeric Input has to be fuzzified,
 *   fed into the Knowledge Base, which infers the Rules and a Result
 *   which has to be defuzzified again into Commands.
 * *Data Analysis:
 *
 * Design Decisions:
 * Not necessary to apply full Accuracy here, so I use float.
 * Could also use Integer Numbers, since I'm dealing with a fixed Range.
 * This Class could be extended by
 * 
 * Der Unterschied zwischen Wahrscheinlichkeitsrechnung, Messtechnik und Fuzzy-Logik
 * l��t sich laut dem Begr�nder der Fuzzy Logik, Herrn Prof. Dr. Lotfi Zadeh
 * anhand der verschiedenen Interpretationen der Antwort <<0,5>>
 * auf die Frage: "Ist ein Salami Sandwich im K�hlschrank?"
 * auf gut verst�ndliche Weise erl�utern.
 * Voraussetzung ist nat�rlich, dass man den K�hlschrank noch nicht �ffnen durfte.
 *
 * Nach der Wahrscheinlichkeitstheorie ist entweder ein Salami Sandwich im K�hlschrank
 * oder es ist kein Salami Sandwich im K�hlschrank -
 * die Wahrscheinlichkeit betr�gt jeweils 0,5.
 *
 * Der Sichtweise der Messtechnik folgend
 * befindet sich genau ein halbes Salami Sandwich im K�hlschrank.
 *
 * Entsprechend dem Fuzzy-Ansatz ist da was im K�hlschrank,
 * das man als eine Art Salami Sandwich bezeichnen k�nnte.
 *
 * Der Nachteil eines neuronalen Netzes im Unterschied zu einem Fuzzy Regler ist,
 * da� aus der Konfiguration eines Netzes keine Regeln oder Verst�ndnis abgeleitet werden k�nnen.
 *
 *
 * Regelstrategie des Fuzzy Kranreglers:
 * In einer einfachen Tabelle kann die gesamte Regelstrategie des Kranreglers dargestellt werden:
 * * Solange der Krankopf noch weit vom Ziel entfernt ist, wird einerlei wie die Last auch pendelt
 *   der Motor auf volle Leistung gestellt. Dies entspricht der Strategie:
 *   Wenn ich noch weit weg vom Ziel bin, fahre ich erst mal schnell los.
 * * Kommt der Krankopf jedoch n�her, so ver�ndert sich die Strategie.
 *   Bei kleinen Pendelungen wird die Leistung reduziert:
 *   Komme ich dem Ziel n�her, re-duziere ich die Motorleistung;
 *   eine kleine Pendelung gleiche ich besser sp�ter aus,
 *   sonst verliere ich hier zuviel Zeit.
 * Allerdings gibt es zwei Sonderf�lle.
 * * Eilt die Last stark dem Krankopf voraus,
 *   so bleibt die Motorleistung auf ihrem Maximalwert, denn:
 *   Wenn die Last mir weit voraus ist, reduziere ich die Pendelung am besten,
 *   indem ich den Motor auf voller Leistung lasse; zudem bin ich dann auch noch schneller am Ziel.
 *
 * * Der andere Sonderfall ist das starke Hinterhereilen der Last hinter dem Krankopf.
 *   Hier mu� das System sogar mit starkem Abbremsen reagieren,
 *   da sich dieses Pendeln sonst am Ziel nur sehr schwer in den Griff bekommen l��t.
 *
 * * Wenn das Ziel erreicht ist, wird nur noch versucht, die Pendelung auszugleichen.
 *   Eilt die Last im Zielpunkt hinterher, mu� sich der Krankopf zun�chst nach vorn bewegen.
 *   Auf starkes Ausschwingen der Last nach vorn sollte der Regler urspr�nglich mit starkem Abbremsen reagieren.
 *   Es zeigte sich jedoch, da� dann jedesmal ein �berschwingen in die Gegenrichtung entstand.
 *   Dies ist vor allem auf die starke Reibung des Antriebs und die Nichtlinearit�t des Motors zur�ckzuf�hren.
 *   Durch Umformulieren der Regelstrategie 'statt starkem Abbremsen nur noch mittleres f�r diesen Fall
 *   konnten wir dann sofort das gew�nschte Fahrverhalten erreichen.
 *
 * Gegen�ber einer konventionellen L�sung haben wir durch den Einsatz der Fuzzy Logic folgende Vorteile erzielt:
 * * Die gew�nschte Regelstrategie konnte ohne aufwendige mathematische Modellbildung
 *   auf der Basis vorhandenen technischen Wissens erzielt werden.
 * * Auch Nichtspezialisten der Regelungstechnik k�nnen ein solches System aufbauen und optimieren.
 * * Da jedem Systemzustand leicht verst�ndliche Regeln zugeordnet sind,
 *   beschleunigen sich die Inbetriebnahme und sp�tere Modifikationen.
 *
 * Im Gegensatz zu einer konventionellen L�sung,
 * deren komplexe mathematische Berechnungen einen leistungsf�higen Rechner ben�tigen,
 * l��t sich Fuzzy Logic sehr effizient auch auf einfachen Rechnern einsetzen.
 * 
 * 
 * Arithmetic Operations like Addition, Subtraction etc. is not well-defined, 
 * similar to Interval Arithmetics: 
 * For Addition and Subtraction: 
 * the Mids add/subtract up 
 * the left and right widths always add up 
 * For Multiplication and Division: 
 * the Mids multiply/divide up 
 * the left and right widths have to be processed individually. 
 * 
 * Multidimensional fuzzy Numbers can be created 
 * using fuzzy Numbers in each Dimension 
 * or using Intervals of real Numbers, 
 * but that is less efficient, 
 * since you have to build a MetricIRing anyway, 
 * so why not use real Numbers instead of Objects!  
 * 
 * @see streamIO.copy.boole.Fuzzy for a constant Predicate without any Range 
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:44:37Z
 * digest: 93575cdbf2061fcbe3cf543b28349a0e6213caa9c275e5ad865913b885386e70
 * stale: false
 * tags: [code/fuzzy_logic, code/interval_arithmetic]
 * concepts: [Fuzzy Logic]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class FuzzyNumber 
//extends ABoole //to allow to treat it like a Set (Vector of [0..1] with Min, Max), 
//similar to a Vector or Function.
implements IFloatFunction, IFuzzifier 
{	//since there is no unified Representation of an ORed Fuzzy (only for AND)
	//the Expression (a OR b) has to be evaluated every time, just like for Intervals.

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(FuzzyNumber.class, 1);
	
	////////////////////////////////////////////////////////////////////////////
	//  Member Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Name of this Range in this Manifold	 */
	final public String name;
	
	/** Left  Border of the Range where the Match is > 0	 */
	final public float left;

	/**Returns the Left  Border	 */	public float getLeft () { return left; }

	/** Position     in the Range where the Match is maximum (height or 1)	 */
	final public float mid;

	/**Returns the Mid Point	 */	public float getMid	 () { return mid; }

	/** Right Border of the Range where the Match is > 0	 */
	final public float right;

	/**Returns the Right Border	 */	public float getRight() { return right; }

	/** Height of the Triangle	 */
	final public float height;
	
	/** Total Mass of this Number, used to speed up Calculation	 */
	final public float weight;
	
	/**Returns the precomputed total mass (area) of this number's triangle.
	 * @return the total Weight of this Number, growing with the Width	 */
	public float getWeight () { return weight; }
	
	/** Center of Mass, used to speed up Calculation 
	 * must not necessarily fall together with 'mid' (only for symmetric Triangles) 
	 */
	final public float center;
	
	/**Returns the precomputed center of mass of this number's triangle.
	 * @return the Center of Mass of this Number	 */
	public float getCenter() { return center; }
	
	/** Width of the left  Range, used to speed up Calculation	 */
	private final float lWidth;
	
	/** Width of the right Range, used to speed up Calculation	 */
	private final float rWidth;
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Initializing Constructor	 */
	public FuzzyNumber(final float mid_, final float width_, final String name) {
		this(mid_-width_, mid_, mid_+width_, 1, name); }
	
	/**Initializing Constructor	 */
	public FuzzyNumber(final float left_, final float mid_, final float right_, final String name) {
		this(left_, mid_, right_, 1, name); }
	
	/**Initializing Constructor	 */
	public FuzzyNumber(final double left_, final double mid_, final double right_, final String name) {
		this(left_, mid_, right_, 1, name); }
	
	/**Initializing Constructor	 */
	public FuzzyNumber(final double left_, final double mid_, final double right_, final double height_, final String name) {
		this((float) left_, (float) mid_, (float) right_, (float) height_, name); 
	}
	
	/**Initializing Constructor	 */
	public FuzzyNumber(float left_, float mid_, float right_, final float height_, final String name_) {
		this.name = name_; 
		//generic Algorithm to bring three OrderAbles into Order
		if (left_ > mid_  ) { final float tmp = left_ ; left_  = mid_; mid_ = tmp; }
		if (mid_  > right_) { final float tmp = right_; right_ = mid_; mid_ = tmp; }
		if (left_ > mid_  ) { final float tmp = left_ ; left_  = mid_; mid_ = tmp; }
		//Set the local Variables
		this.left	= left_	;
		this.mid		= mid_	;
		this.right	= right_;
		//Calculate optimizing Values
		this.rWidth = right - mid;
		this.lWidth = mid  - left; 
		this.height = height_;  
		//The Weight and CoM of a Triangle can be pre-calculated: 
		//Int(x=0..w, x*h/w) = h*w�/2w = h*w/2
		//Int(x=0..w, x*x*h/w) = h*w�/3w = h*w�/3
		this.weight = height*(rWidth+lWidth); //lWeight = lWidth/2 etc.
		final float lCenter = left +(lWidth+lWidth)/3;
		final float rCenter = right-(rWidth+rWidth)/3;
		this.center = (lCenter*lWidth+rCenter*rWidth)*height/weight; //CoM = Sum(i, CoM[i]*weight[i])/Sum(i, weight[i]) 
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** de-Fuzzifies for the given Category and adds the Weights to the Sum	 
	 * 
	 * @param sums Accumulators for the Weights and weighted Positions 
	 * @param category specific FuzzyNumber to evaluate using the given Limit
	 * @param limit the Limit to consider when evaluating the given Category. 
	 * @return the Center and Weight (both weighted with the limit) of this Number. 
	 * The Quotient is the Center of Mass 
	 */
	public float[] deFuzzify(float[] sums, double limit) {
		if (limit == 0) {
			return sums; }
		if (sums == null) {
			sums = new float[2]; }
		limit*=this.weight; //TODO: the rules could be float[][]... 
		sums[1] += limit * this.center; 
		sums[0] += limit; 
		return sums;  //...with a fractional Rule Weight to indicate insecure Rules
	}
	
    /**Returns {@link IStreamIn#ORDER_NONE}, since a fuzzy number imposes no ordering.
     * @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_NONE; }
    
	/**
	 * Returns the Degree of Membership (not the Probability!)
	 * with which this Position represents this Number	 
	 * @see function.IFloatFunction#Map(float)
	 */
	public float Map(final float position) {
		if ((position <= left ) ||
			(position >= right))  
			return 0; 
		if  (position > mid) {
			return (right-position)/rWidth; } 
			return (position-left )/lWidth; }
	
	/**Returns the degree of membership for the given position, delegating to {@link #Map(float)}.
	 * @see function.IFloatFunction#Map(double)	 */
	public double Map(final double arg) { return Map((float) arg); }

	/**Returns the degree of membership of arg, converting it to a float position first.
	 * @see streamIO.copy.boole.fuzzy.IFuzzifier#getMembership(java.lang.Object)	 */
	public float getMembership(Object arg) {
		return Map(ByRefFloat.getFloat(arg));
	}

	////////////////////////////////////////////////////////////////////////////
	//	Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////

	/**Method to test all Implementations in this class.
	 * Must call testIt of the super Class.	 */
	public static void testIt() throws Exception {	//testInstance inherited from ACopyAble;
		L.n("Testing " + FuzzyNumber.class.toString());
		FuzzyNumber symmetric1 = new FuzzyNumber(10, 12, 14, "symm1");  
		FuzzyNumber symmetric2 = new FuzzyNumber(10, 12, 14, "symm2");
		Assert.EQUALS(symmetric1.center, symmetric2.center); 
		Assert.EQUALS(symmetric1.weight, symmetric2.weight); 
		Assert.EQUALS(symmetric1.center, symmetric1.mid); 
		for (int i = 100; --i >= 0; ) {
			testRandom(); 
		}
	}

	/** tests some Invariants with random Values 	 */
	private static void testRandom() {
		FuzzyNumber random = new FuzzyNumber(Math.random(), Math.random(), Math.random(), Math.random(), "random");  
		Assert.IS_TRUE(random.left   < random.mid   ); 
		Assert.IS_TRUE(random.mid    < random.right ); 
		Assert.IS_TRUE(random.left   < random.center); 
		Assert.IS_TRUE(random.center < random.right ); 
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
		testIt(); 
	}

}
