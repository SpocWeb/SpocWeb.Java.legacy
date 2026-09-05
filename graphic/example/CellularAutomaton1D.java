package graphic.example; //

import graphic.MemoryImage;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;

/**
  * Simulates a 1D elementary cellular automaton, evolving a row of binary cells
  * over time according to a numbered rule.
  * <p>
  * Title: CellularAutomaton1D<p>
  * Description:
  * This is an Example of a 1dim. cellular Automaton showing emergent Behavior,
  * which does not become obvious from the Rules directly, but only implicitly.
  *
  * The 'Ant' starts on an empty 1D Lattice, but modifies it in the most basic manner:
  * Every Cell in the Lattice is modified according to a Set of Rules
  * that take the two nearest Neighbors into Account.
  *
  * Thus the Number of (binary) Input Parameters is 3 resulting in 2^3 = 8 possible Inputs.
  * The Output is also binary resulting in 2^(2^3) = 2^8 = 256 possible Rule Sets.
  *
  * These Rule Sets are applied to every Cell from the previous Time Step.
  *
  * The Figures that appear are all triangular in Shape,
  * due to the Propagation Scheme of 1 Neighbor limiting any Effects.
  * The Types of Figures appearing are:
  * Class 1 empty or filled or patterned Planes, einfache Fl�chen 0, 255
  * Class 2 fractal Patterns, Sierpinski Triangles in various Orientations and Completeness
  * Class 3 Pseudo randomi Patterns  30 =   11110 zufalls�hnlich
  * Class 4 110 = 1101110 lokalisiert mit Ww.
  *
  * single Lines
  *
  *
  *
  *
  * It's Behavior can be represented in 2D by adding the Time Dimension as y Direction.
  *
  * Emergent Behavior, similar to John Conways "Life", a 2D cellular Automaton,
  * cannot (easily) be derived from the initial Rules
  * and can lead to surprisingly and sometimes chaotic Behavior.
  * It can only be researched using Computers.
  * There are no analytic Methods possible, because the Domain has a discrete Topology.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-07-2002, 11:36 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:49:54Z
  * digest: 0485dcc8aec69e18709ab6f228b542669bf38114a6ea6316237bcb6d561f1eff
  * stale: false
  * tags: [code/algorithm, code/simulation]
  * concepts: [1D Cellular Automaton]
  * facets: {layer: test, status: legacy, complexity: medium}
  * -->
  */
public class CellularAutomaton1D 
extends Frame { //Applet {

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

	/** Serialization version marker for {@link Frame} compatibility. */
	private static final long serialVersionUID = 1L;

	/** Size of the Graphic Area     */
	final static public int WIDTH = 1024;

	/** Size of the Graphic Area     */
	final static public int HEIGHT = WIDTH >> 1; //768;

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
/// Directions to move, ordered consecutively to be able to turn easily.
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////////

	/**
     * Evaluates the boolean function determined by the given rule number at
     * the given multi-index.
     * @param NumRule RuleNumber from 0 to 2^(2^index.length)
     * @param index Multi index for the Function.
     * @return the Function Value of the boolean Function
     *  determined by the given RuleNumber at the given Index
     */
	final static public boolean RuleValue(int NumRule, int[] index) {
		int i = index.length;
		int n = index[--i];
		while (--i >= 0) {
			n = n + n + index[i]; }
		return 0 != (NumRule & (1 << n)); }

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables for the Graphics
////////////////////////////////////////////////////////////////////////////////

	/** Reference to the Image's Graphics Object for the Double Buffer.	 */
	protected MemoryImage memImg;

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables for the Algorithm
////////////////////////////////////////////////////////////////////////////////

	/** holds the Index used in timeStep()     */
    protected transient int[] index;

	/** holds the Number of the Rule used in timeStep()     */
    protected int numRule;

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor
	 * @param RuleNum  Number of the Rule from 0 to 2^(2^RuleSize)
	 * @param RuleSize Size = #of Neighboring Cells being evaluated in the Rule
	 */
	protected CellularAutomaton1D (int RuleNum_, int RuleSize) {
		this.numRule = RuleNum_;
		this.index = new int[RuleSize + RuleSize + 1];
		memImg = new MemoryImage(HEIGHT, WIDTH);
		memImg.orOperation(MemoryImage.OPAQUE); //make the Image opaque! Otherwise it is not visible!
		memImg.currentMode = MemoryImage.setMode;
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Component: Implementation
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Fundamental Painting Method for Frames and Applets
	 * Just performs the Double Buffering here!
	 */
	public void paint(Graphics g) {
//		System.out.println("Before painting");
		memImg.paint(this, true);
//		System.out.println("After  painting");
	}

	/** Evolves over Time using timeStep().	 */
	protected void evolve() {
		/** The Time elapsed in the System	 */
		int t = 24; // 0; //Offset for the Window Border
		int x0 = WIDTH >> 1; //Start Position
		memImg.setPixel(x0, t, Color.white); //single Start Value
		lastMin = t;
		lastMax = WIDTH-t;
//		System.out.println(getPixel(x0,t));
		while (++t < HEIGHT) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException x) {}
			timeStep(t);
		}
	}

	/** Index of last Rows Minimum set Value */
	protected int lastMin;

	/** Index of last Rows Maximum set Value */
	protected int lastMax;

	/**
	 * Performing the next Time Step.
	 * Exploits the Fact that a MemoryImage can be 'read' using getPixel(x,y).
	 */
	protected void timeStep(int t) { //optimization for single Start Point!
		boolean value;
		int offset = index.length >> 1;
		int start = lastMax + offset + offset; lastMax = 0;//WIDTH-1; //don't count Border Conditions...
		int stop  = lastMin - offset - offset; lastMin = WIDTH; //1;
		int tOld = t-1;
		int i = start; //
		while (--i >= stop) { //loop over the whole, except for the Border
			int j = index.length;
			while (--j >= 0) {
				index[j] = getPixel(i+j-offset,tOld); }
			if (value = RuleValue(numRule, index)) { //assume the whole Tableau is empty
				if (lastMax < i) {
					lastMax = i; }
				if (lastMin > i) {
					lastMin = i; }
//				setPixel(i, t, value);
			}
			setPixel(i, t, value);
		}
	}

	/**
	 * Reads back the cell state at the given position from {@link #memImg}.
	 * @return 0 if the pixel is black (cell off), 1 otherwise (cell on)
	 */
	protected int getPixel(int x, int y) { //black == 0 == false
		if ((memImg.getPixel(x, y) & MemoryImage.RGB_VALUES) == 0) {
			return 0; }
			return 1; }

	/** Sets the given cell's color in {@link #memImg}: white if on, black if off. */
	protected void setPixel(int x, int y, boolean value) {
		if (value) { //
			memImg.setPixel(x, y, Color.white); //color it white
		} else {
			memImg.setPixel(x, y, Color.black); //color it black
		}
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + CellularAutomaton1D.class.getName());
		CellularAutomaton1D f = new CellularAutomaton1D(2, 1); //Frame();
		/** interesting Rule Sets:
		* 0, 255 Klasse 1 einfache Fl�chen
		*  Klasse 2 fraktale Muster
		*  30 =   11110 Klasse 3 zufalls�hnlich
		* 110 = 1101110 Klasse 4 lokalisiert mit Ww.
		*
		*/
		f.setSize(WIDTH, HEIGHT);
		f.show();
		do {
			System.out.println("Number of the current Rule: " + f.numRule);
			f.memImg.clear();
			f.evolve();
		} while ((f.numRule+=2) < 256); //only the even ones don't flip with each row!
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}

