package graphic.example; //

import graphic.MemoryImage;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;

/**
  * Simulates "Langton's Ant", a 2D cellular automaton that flips pixel color
  * and turns based on the color of the cell it enters.
  * <p>
  * Title: AntHillInside<p>
  * Description:
  * This is an Example of a 2dim. cellular Automaton: "Langtons Ant" showing emergent Behavior,
  * which does not become obvious from the Rules directly, but only implicitly.
  *
  * The Ant starts on an empty Lattice, but modifies it in the most basic manner:
  * As soon as the Ant enters a Field, the Field changes Color.
  * The Ant changes its Direction based on the Field Color:
  * On a black Field it turns right,
  * on a white Field it turns left.
  *
  * It's Behavior can be represented in 3D by adding the Time Dimension.
  * It shows that after a regular Phase and a (seemingly) irregular Phase,
  * it starts building up a diagonal "Street" propagating to Infinity.
  *
  * Emergent Behavior, similar to John Conways "Life", also a 2D cellular Automaton,
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
  * mtime: 2026-09-05T11:49:07Z
  * digest: 190448e2d8ee6d9c29f63fb0b405b1606631fec72085f5b71028028e110a4665
  * stale: false
  * tags: [code/algorithm, code/simulation]
  * concepts: [Ant Colony Cellular Automaton]
  * facets: {layer: test, status: broken, complexity: medium}
  * -->
  */
public class AntHillInside 
extends Frame { //Applet {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Serialization version marker for {@link Frame} compatibility. */
	private static final long serialVersionUID = 1L;

	/** Height of the Graphic Area     */
	final static public int HEIGHT = 768;

	/** Width of the Graphic Area     */
	final static public int WIDTH = 1024;

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	/// Directions to move, ordered consecutively to be able to turn easily.
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Value for the upward Direction
	 */
	final static public byte DIR_UP = 0;

	/**
	 * Value for the upward Direction
	 */
	final static public byte DIR_RIGHT = 1;

	/**
	 * Value for the upward Direction
	 */
	final static public byte DIR_DOWN = 2;

	/**
	 * Value for the upward Direction
	 */
	final static public byte DIR_LEFT = 3;

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the Image's Graphics Object for the Double Buffer.	 */
	protected MemoryImage memImg;

	/** The x Position of the Ant	 */
	protected int x;

	/** The y Position of the Ant	 */
	protected int y;

	/** The Direction of the Ant (left, right, up, down)	 */
	protected byte dir;

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor */
	protected AntHillInside () {
		memImg = new MemoryImage(HEIGHT, WIDTH);
		memImg.orOperation(MemoryImage.OPAQUE); //make the Image opaque! Otherwise it is not visible!
		memImg.currentMode = MemoryImage.setMode;
		memImg. setColor ( Color.black); //does not do anything, because Color is determined by the Algorithm
		memImg.BackColor = Color.white ; //does not change anything, because Image is not Cleared!
		memImg.clear();
		x = WIDTH /2;
		y = HEIGHT/2;
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

	/**
	 * Runs the ant simulation forever, moving one cell per iteration and
	 * flipping each visited cell's color according to Langton's Ant rules.
	 *
	 * @throws RuntimeException if {@link #dir} holds a value outside the
	 *         {@code DIR_*} constants
	 */
	protected void moveAnt() {
		while (true) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException x) {}
			switch(dir) {
				case DIR_UP   : --y; break;
				case DIR_DOWN : ++y; break;
				case DIR_LEFT : --x; break;
				case DIR_RIGHT: ++x; break;
				default: throw new RuntimeException("Invalid Direction:" + dir); //break;
			}
			//wrap around the buffer edges, the Ant runs forever
			if (x <  0     ) { x = WIDTH  - 1; } else
			if (x >= WIDTH ) { x = 0; }
			if (y <  0     ) { y = HEIGHT - 1; } else
			if (y >= HEIGHT) { y = 0; }
			int col = memImg.getPixel(x, y) & MemoryImage.RGB_VALUES;
			if (col == 0) { //pixel is black...
				memImg.setPixel(x, y, Color.white); //color it white
				if(++dir > DIR_LEFT) { //move right
					 dir = DIR_UP; }
			} else {
				memImg.setPixel(x, y, Color.black); //color it black
				if(--dir < DIR_UP  ) { //move left
					 dir = DIR_LEFT; }
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + AntHillInside.class.getName());
		AntHillInside f = new AntHillInside(); //Frame();
		f.setSize(WIDTH, HEIGHT);
		f.show();
		f.moveAnt();
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}

