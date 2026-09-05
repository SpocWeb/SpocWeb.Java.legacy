package structure.blackBoard.triangle;

import graphs.ICopy;

import java.util.Arrays;

import streamIO.Assert;
import structure.blackBoard.IKnowledge;

/**
  * Blackboard Representation of a Triangle, holding up to six Side/Angle Values and
  * dispatching to {@link IKnowledge} Solvers ({@link SSS}, {@link SSW}, {@link SWS},
  * {@link WSW}, {@link WWW}) to derive the unknown ones.
  *
  * Purpose:
  * BlackBoard Representation of a Triangle.
  *
  * Design Decisions / Implementation Details:
  * The unknown Values are represented by null and not by NaN
  * because NaN appears also in regular Calculation with invalid Triangles
  * and thus cannot be used to detect already completed Calculations.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	10-25-2002, 09:05 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:23:47Z
  * digest: dd07e8419a13399553c3ee551dc3743a443d6ecffdc44194af88a9ab21f5c8dd
  * stale: false
  * tags: [code/2d_geometry]
  * concepts: [Triangle Value Object]
  * facets: {layer: domain, status: legacy, complexity: low}
  * -->
  */
final public class Triangle implements ICopy {

	/** Square Function */
	final static public double SQR(double val) {
		return val * val;
	}

	/** Returns the third Index (0, 1 or 2) not equal to either given Index.
	  * @return the complementing Index */
	final static public int COMPLEMENT(int i, int j) {
		return 0 + 1 + 2 - i - j;
	}

	/** Returns a fresh 3-Element Array of Indexes starting at {@code i}, cyclically ordered.
	  * @return the Array filled with the complementing Indexes in cyclic Order */
	final static public int[] COMPLEMENT(int i) {
		return COMPLEMENT(new int[3], i);
	}

	/** Fills the given Array with the three Indexes starting at {@code i}, cyclically ordered.
	  * @return the Array filled with the complementing Indexes in cyclic Order */
	final static public int[] COMPLEMENT(int[] ret, int i) {
		ret[0] = i;
		if (++i >= 3) {
			i -= 3;
		}
		ret[1] = i;
		if (++i >= 3) {
			i -= 3;
		}
		ret[2] = i;
		return ret;
	}

	/** Fills the given Array with {@code NaN}, marking every Value as unknown. */
	final static public void fillUnknowns(double[] arr) {
		Arrays.fill(arr, Double.NaN);
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////

	/** The Angles of the Triangle:	 */
	//	private final Double[] angles = new Double[3];

	/** The Sides of the Triangle: primitive Values cannot distinguis NaN!	 */
	//	private final double[] sides = new double[3];

	/** The Sides and Angles of the Triangle:	 */
	private final Double[] sidesAngles = new Double[3 + 3];

	/** WSW Solver */
	private SSS sss = new SSS(this);

	/** WSW Solver */
	private SSW ssw = new SSW(this);

	/** WSW Solver */
	private SWS sws = new SWS(this);

	/** WSW Solver */
	private WSW wsw = new WSW(this);

	/** WSW Solver */
	private WWW www = new WWW(this);

	/** List of Knowledge Sources in descending Order of Precedence 
	 * Works like a Chain of Responsibility
	 */
	private IKnowledge[] sideSolver = { www, wsw, sws, ssw };

	/** List of Knowledge Sources in descending Order of Precedence 
	 * Works like a Chain of Responsibility
	 */
	private IKnowledge[] angleSolver = { sss, sws };

	/** This is a possible alternative Solution of the Triangle.
	  * The Alternative of the Alternative is 'this'.
	  */
	private Triangle alternative;

	////////////////////////////////////////////////////////////////////////////////
	/// #region : calculated Values...
	////////////////////////////////////////////////////////////////////////////////

	/** Returns the sum of all three Sides.
	  * @return the Radius of the outer Circle */
	public double getCircumference() {
		return getSide(0) + getSide(1) + getSide(2);
	}

	/** Returns the Radius of the Circle circumscribing this Triangle.
	  * @return the Radius of the outer Circle */
	public double getCircleOuter() {
		return getCircumference() / (Math.cos(getAngle(0) / 2) + Math.cos(getAngle(1) / 2) + Math.cos(getAngle(2) / 2));
	}

	/** Returns the Radius of the Circle inscribed in this Triangle.
	  * @return the Radius of the inner Circle */
	public double getCircleInner() {
		double circ = getCircumference() / 2;
		return Math.sqrt((circ - getSide(0)) * (circ - getSide(1)) * (circ - getSide(2)) / circ);
	}

	/** Returns the Height dropped onto Side {@code i} from the opposite Vertex.
	  * @return the Height based on the Line of the Triangle */
	public double getHeight(int i) {
		int[] ndx = COMPLEMENT(i);
		return getSide(ndx[1]) * Math.sin(getAngle(ndx[2]));
	}

	/** Returns the Length of the Median from Vertex {@code i} to the midpoint of the
	  * opposite Side.
	  * @return the Length of the Line in the middle of the opposite Line */
	public double getSideHalf(int i) {
		int[] ndx = COMPLEMENT(i);
		return Math.sqrt(
			SQR(getSide(ndx[1]))
				+ SQR(getSide(ndx[2]))
				+ 2 * getSide(ndx[1]) * getSide(ndx[2]) * Math.sin(getAngle(i)))
			/ 2;
	}

	/** Returns the Length of the Angle Bisector from Vertex {@code i}.
	  * @return the Length of the Line in the middle of the Angle */
	public double getAngleHalf(int i) {
		int[] ndx = COMPLEMENT(i);
		return 2 * getSide(ndx[1]) * Math.cos(getAngle(ndx[2]) / 2) * getSide(i) / (getSide(ndx[1]) + getSide(i));
	}

	/** Returns the Area of this Triangle via Heron's Formula.
	  * @return the Area of the Triangle */
	public double getArea() {
		double circ = getCircumference() / 2;
		return Math.sqrt(circ * (circ - getSide(0)) * (circ - getSide(1)) * (circ - getSide(2)));
		//Heronsche Fl�chenformel
		//		return getSide(1)*getSide(2)*Math.sin(0); //Sinussatz
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////

	/** Returns the ambiguous second Solution paired with this Triangle, if one was recorded.
	  * @return an alternative Solution if it exists:   */
	public Triangle getAlternative() {
		return alternative;
	}

	/**
	 * Returns a Copy of this Triangle, satisfying the {@link ICopy} Contract.
	 * @see graphs.ICopy#Copy()
	 */
	public ICopy Copy() {
		return copy();
	}

	/**
	 * Creates a Copy of this Triangle (usually as a Preparation for the Alternative)
	 * @see graphs.ICopy#Copy()
	 */
	public Triangle copy() {
		Triangle ret = null;
		ret = new Triangle(); //copy the unknown Values too! (null is the Indicator for this!) 
		System.arraycopy(sidesAngles, 0, ret.sidesAngles, 0, sidesAngles.length);
		//		System.arraycopy(angles, 0, ret.angles, 0, angles.length);
		//		System.arraycopy( sides, 0, ret. sides, 0,  sides.length);
		ret.numAngles = numAngles;
		ret.numSides = numSides;
		/*		try { ret =(Triangle) this.clone(); //new Triangle(); //
				} catch(CloneNotSupportedException x) {
					x.printStackTrace(); }
				ret.angles = VectorDouble.copy(angles);
				ret. sides = VectorDouble.copy( sides);
		*/
		return ret;
	}

	/**
	 * Sets the alternative.
	 * @param alternative The alternative to set
	 */
	public void setAlternative(Triangle alternative_) {
		this.alternative = alternative_;
		alternative_.alternative = this;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// Handling the null Values explicitly
	////////////////////////////////////////////////////////////////////////////////

	/** Returns whether the Side or Angle at Index {@code i} (0-2 Sides, 3-5 Angles) is known.
	  * @return true if the specified Value is set:   */
	public boolean isValueSet(int i) {
		return (sidesAngles[i] != null);
	}

	/** Returns the Value at Index {@code i}, solving for Sides or Angles first if it is not
	  * yet known.
	  * @return the specified Angle:   */
	public double getValue(int i) {
		if (!isValueSet(i)) {
			if (i < 3) {
				solveForSides();
			} else {
				solveForAngles();
			}
		}
		return sidesAngles[i].doubleValue();
	}

	/** Sets the Value at Index {@code i}, rejecting an Angle already exceeding Pi.
	  * @throws IllegalArgumentException when the Index is already set, or an Angle Value
	  * exceeds Pi
	  * @return the specified Angle:   */
	public void setValue(int i, double value) {
		if (isValueSet(i)) {
			throw new IllegalArgumentException("Value is already set!");
		}
		if (i < 3) {
			++numSides;
		} else {
			if (value > Math.PI) {
				throw new IllegalArgumentException("Value is too large for an Angle!");
			}
			++numAngles;
		}
		sidesAngles[i + 3] = new Double(value);
	}

	/** Returns whether Side {@code i} is known.
	  * @return true if the specified Side is set:   */
	public boolean isSideSet(int i) {
		return isValueSet(i);
	}
	/** Returns Side {@code i}, solving for it first if not yet known.
	  * @return the specified Angle:   */
	public double getSide(int i) {
		return getValue(i);
	}
	/** Sets Side {@code i}. */
	public void setSide(int i, final double value) {
		setValue(i, value);
	}

	/** Returns whether Angle {@code i} is known.
	  * @return true if the specified Angle is set:   */
	public boolean isAngleSet(int i) {
		return isValueSet(i + 3);
	}

	/** Returns Angle {@code i}, solving for it first if not yet known.
	  * @return the specified Angle:   */
	public double getAngle(int i) {
		return getValue(i + 3);
	}

	/** Sets Angle {@code i}. */
	public void setAngle(int i, final double value) {
		setValue(i + 3, value);
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	public Triangle() {
		//		Arrays.fill(angles, Double.NaN);
		//		Arrays.fill( sides, Double.NaN);
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Number of Elements.
	////////////////////////////////////////////////////////////////////////////////

	/** The Number of Sides is needed so often that it is cached */
	private int numSides;

	/** The Number of Angles is needed so often that it is cached */
	private int numAngles;

	/** Returns how many of the three Sides are currently known.
	  * @return the Number of Sides in this Triangle */
	public int numSides() {
		return numSides;
	}

	/** Returns how many of the three Angles are currently known.
	  * @return the Number of Angles in this Triangle */
	public int numAngles() {
		return numAngles;
	}

	/** Returns the total count of known Sides and Angles.
	  * @return the Number of Values in this Triangle */
	public int numValues() {
		return numAngles + numSides;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Solving Methods
	////////////////////////////////////////////////////////////////////////////////

	/** tries to calculate all Sides
	 *  this is not always possible!
	 *  At least one Side has to be set. (Scale)
	 *  Sometimes there are 0, 1 or 2 Solutions.
	 */
	public boolean solveForSides() {
		if (numSides() < 1) {
			return false;
		}
		if (numValues() < 3) {
			return false;
		}
		while (numSides < 3) {
			for (int i = sideSolver.length; --i >= 0;) {
				if (sideSolver[i].check()) {
					sideSolver[i].update();
				}
			}
		}
		return true;
	}

	/** tries to calculate all Angles
	 *  this is always possible!
	 */
	public boolean solveForAngles() {
		if (numValues() < 3) {
			return false;
		}
		if (www.check()) {
			www.update();
			return true;
		}
		if (!solveForSides()) {
			return false;
		}
		while (numAngles < 3) {
			for (int i = angleSolver.length; --i >= 0;) {
				if (angleSolver[i].check()) {
					angleSolver[i].update();
				}
			}
		}
		return true;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////

	/** tests whether SSS Calculation works */
	public static void testSSSValid() {
		Triangle t = new Triangle();
		t.setSide(0, 1);
		t.setSide(1, 2);
		t.setSide(2, 1.5);
		//t.setSide(2, 4); //results
		Assert.EQUALS(0.5053605102841573, t.getAngle(0));
		Assert.EQUALS(1.8234765819369754, t.getAngle(1));
		Assert.EQUALS(0.8127555613686607, t.getAngle(2));
	}

	/** tests whether SSS Calculation works */
	public static void testSSSLimit() {
		Triangle t = new Triangle();
		t.setSide(0, 1);
		t.setSide(1, 2);
		t.setSide(2, 3);
		Assert.EQUALS(0, t.getAngle(0));
		Assert.EQUALS(0, t.getAngle(1));
		Assert.EQUALS(Math.PI, t.getAngle(2));
	}

	/** tests whether SSS Calculation works */
	public static void testSSSInvalid() {
		Triangle t = new Triangle();
		t.setSide(0, 1);
		t.setSide(1, 2);
		t.setSide(2, 4);
		Assert.NOT_A_NUMBER(t.getAngle(0));
		Assert.NOT_A_NUMBER(t.getAngle(1));
		Assert.NOT_A_NUMBER(t.getAngle(2));
	}

	/** tests whether SSS Calculation works */
	public static void compareTriangles(Triangle t1, Triangle t2) {
		for (int i = t1.sidesAngles.length; --i >= 0;) {
			try {
				Assert.EQUALS(t1.getValue(i), t2.getValue(i));
			} catch (streamIO.exception.FailureException x) {
				t2 = t2.getAlternative();
				if (t2 == null) {
					throw x;
				}
				Assert.EQUALS(t1.getValue(i), t2.getValue(i));
			}
		}
	}

	/** test all Combinations of 3 out of 6 Values set */
	public static Triangle copyTriangle(Triangle t, int v1, int v2, int v3) {
		Triangle ret = new Triangle();
		ret.setValue(v1, t.getValue(v1));
		ret.setValue(v2, t.getValue(v2));
		ret.setValue(v3, t.getValue(v3));
		return ret;
	}

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Triangle.class.getName());
		Triangle t = new Triangle();
		t.setSide(0, 1);
		t.setSide(1, 2);
		t.setSide(2, 1.5);
		Assert.EQUALS(0.5053605102841573, t.getAngle(0));
		Assert.EQUALS(1.8234765819369754, t.getAngle(1));
		Assert.EQUALS(0.8127555613686607, t.getAngle(2));
		compareTriangles(t, copyTriangle(t, 0, 1, 2));
		compareTriangles(t, copyTriangle(t, 0, 1, 3));
		compareTriangles(t, copyTriangle(t, 0, 1, 4));
		compareTriangles(t, copyTriangle(t, 0, 1, 5));
		compareTriangles(t, copyTriangle(t, 0, 2, 3));
		compareTriangles(t, copyTriangle(t, 0, 2, 4));
		compareTriangles(t, copyTriangle(t, 0, 2, 5));
		compareTriangles(t, copyTriangle(t, 0, 3, 4));
		compareTriangles(t, copyTriangle(t, 0, 3, 5));
		testSSSInvalid();
		testSSSLimit();
		testSSSValid();
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) { //throws java.io.IOException {
		testIt(args);
	}

}
