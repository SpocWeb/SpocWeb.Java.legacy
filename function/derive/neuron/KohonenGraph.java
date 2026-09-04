package function.derive.neuron;

import function.vector.OdeLorentz;
import graphic.JavaGraphic;
import graphic.MemoryImage;
import graphic.Wire2D;
import graphic.math2D.Raster;
import graphic.math3D.Coordinates3D;
import graphic.math3D.Wire3D;
import graphs.MatrixGraph;
import graphs.SparseGraph;

import java.awt.Color;
import java.awt.Event;
import java.awt.Frame;

import math.matrix.MatrixFloat;
import math.vector.VectorDouble;
import math.vector.VectorFloat;
import streamIO.IIStreamIn;
import streamIO.copy.group.ring.StepRK;
import streamIO.integer.random.RandomFast;
import streamIO.real.IStreamIn_Float;
import tester.ITester;

/**
  * Class for Testing the intermediate Progress of the Kohonen Approximation.
  * Handed over is the actual Kohonen Object with the full Information about the Approximation.
  * Although this is a very broad Interface!
  *
  * Used internally in testIt() to print the Progress to the Screen.
  *
  * Graphics to illustrate the Workings of a Kohonen Map with a 2Dim Topology.
  * If applied to (pseudo) random Numbers, the Network spreads out equally
  * over the Range. The Density and Distribution of Points
  * reveals functional Dependencies and indicates the Probabilities.
  */
public class KohonenGraph 
extends Frame //Applet
implements ITester, IIStreamIn {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Height of the Graphic Area     */
	final static public int HEIGHT = 768;

	/** Width of the Graphic Area     */
	final static public int WIDTH = 1024;

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////

	//Parameters for the Graphics:

	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////

	/** Reference to the Image's Graphics Object for the Double Buffer.	 */
	protected MemoryImage memImg;

	/** Reference to the Kohonen Object to keep it alive */
	//	protected Kohonen Kohonen;

	//Parameters for the Kohonen Map:

	/**Number of Points in a Row	 */
	protected static int RowSize = 10;

	/**Starting Neighborhood Size, reduced during Operation	 */
	protected static int StartNeighbor = RowSize;

	/**Outer Loop Size	 */
	protected static int maxCount1 = StartNeighbor * 2;

	/**Inner Loop Size	 */
	protected static int maxCount2 = maxCount1 * 5;

	/**Reduction Factor for Learning on each pass of the outer Loop. 	 */
	protected static float Reduction = 0.9f;

	/**Starting Value for the Learning Factor	 */
	protected static float betaStart = 0.33f;

	/**Stop Value for the Learning Factor	 */
	protected static float betaStop = 0.1f;

	public KohonenGraph() {
		memImg = new MemoryImage(HEIGHT, WIDTH);
		memImg.orOperation(MemoryImage.OPAQUE); //make the Image opaque! Otherwise it is not visible!
		memImg.currentMode = MemoryImage.setMode;
		memImg.setColor(Color.white); //getForeground()); //important to keep both in synch!
		memImg.BackColor = Color.black; //getBackground();
		memImg.clear(); 
		this.setBackground(memImg.getColor());
		this.setForeground(memImg.BackColor);
	}

	/** Cache for the Graphics Context */
	protected java.awt.Graphics g;

	/**
	 * Fundamental Painting Method for Frames and Applets
	 * Just performs the Double Buffering here!
	 */
	public void paint(java.awt.Graphics g) {
		this.g = g;
		if (drawState == drawStateMixedDim) {
			g.clearRect(0, 0, 1024, 768);
			float[] SP = { 2.0f, 0.5f, 0.0f };
			float[] Direction = VectorFloat.SUB(ViewPoint, SP); //look at the Origin.
			Coordinates3D C3D = new Coordinates3D(ViewPoint, Direction, g.getClipBounds());
			Wire2D W2D = W3D.getWire2D(C3D);
			try {
				W2D.draw(new JavaGraphic(g), 1e18); //memImg, 1e18);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		} else {
			memImg.paint(this, true);
		}
	}

	/**The entry point for the applet. 	 */
	public void init() {
		initForm();
	}

	/**Intializes values for the applet and its components	 */
	void initForm() {
		//		this.setBackground(Color.lightGray);
		//		this.setForeground(Color.black);
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Interface ITester: Implementation
	////////////////////////////////////////////////////////////////////////////

	/** Counter Interval for reduced Painting */
	protected int paintInterval = 80; //1; //80;

	/** Initial Counter */
	protected int paintCounter = paintInterval;

	protected long StartTime = System.currentTimeMillis();

	protected float[] ViewPoint = { 2.0f, 3.0f, 4.0f };

	protected Wire3D W3D;

	///////////////////////////////////////////////////////////////////////////////////
	/// State Model for coordinating Drawing and Modelling
	///////////////////////////////////////////////////////////////////////////////////

	protected static final int drawStateLorentz = -3;
	protected static final int drawStateGraph = -2;
	protected static final int drawStateLineIn2D = -1;
	protected static final int drawStatePlaneIn2D = 0;
	protected static final int drawStateMixedDim = 1;
	protected static final int drawStateSphereIn2D = 2;
	protected static final int drawStateTorusIn2D = 3;

	/** State to distinguish different Test Graphs */
	protected int drawState;

	/**This is the Test working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.
	 * @param  arg	The Object being 'tested'
	 * @return 	'true' or 'false' depending on the ITester and the Parameter 'arg'	 */
	public boolean test(Object arg) {
		Kohonen kohonen = (Kohonen) arg;
		System.out.print((System.currentTimeMillis() - StartTime) / 1000 + " Secs "); //new Date());
		System.out.print("Loops:" + kohonen.numBackProps);
		System.out.print("\tProgress: " + kohonen.progress);
		//		System.out.print("\tLearnFactor: " + kohonen.beta);
		//		System.out.print("\tmaxDelta: " + Max1/numInner); //even with amortized Maxima
		//		System.out.print("\tmaxDelta: " + Max2/numInner); //the Value of currMax determines the
		//		Kohonen.printWeights(Kohonen);
		System.out.println();

		//painting Code.
		//		Rectangle Clip = g.getClipBounds();
		//		memImg.clearRect(Clip.x, Clip.y, Clip.width, Clip.height);
		memImg.clear(); //also clears the Graphics Context!
		float[] Weight;
		int x, y, i;
		switch (drawState) {
			case drawStateMixedDim :
				if ((W3D != null) && (--paintCounter > 0)) {
					return false;
				} //return immediately without drawing!
				paintCounter = paintInterval;
				W3D = new Wire3D(kohonen.weights, true, kohonen.connections);
				this.repaint();
				break;
			case drawStateLineIn2D :
				x = y = 0;
				i = kohonen.getNumWeights(); //RowSize*RowSize
				while (--i >= 0) { //break Connections between opposite Row Ends!
					Weight = kohonen.getWeight(i);
					//				System.out.println(Weight[0] + " , " + Weight[1]);
					int xOld = x;
					x = ((int) (Weight[0] * WIDTH)); // + Clip.x;
					int yOld = y;
					y = ((int) (Weight[1] * HEIGHT)); // + Clip.y;
					if ((xOld == 0) && (yOld == 0)) {
						continue;
					}
					//				System.out.println("(" + xOld + " , " + yOld + " , " + x + " , " + y + ")");
					memImg.drawLine(xOld, yOld, x, y);
					//				memImg.fillRect(x, y, x+5, y+5); //draw a small Square...
				}
				break;
			case drawStatePlaneIn2D :
				paint2D(kohonen.weights, RowSize, 0);
				break;
		}
		if (g != null) { //paint synchronously!
			paint(g);
			System.out.print((System.currentTimeMillis() - StartTime) / 1000 + " Secs "); //new Date());
			System.out.println("Painting finished!");
		}
		//		this.repaint(1); //kick off an asynchronous Repaint
		//Random Deviations from the average maximum Distance
		//are assumed to be regularly distributed so they reduce like SqRt(numInner)
		return false;
	} //not breaking Process!

	/** Paints the 2D Array */
	protected void paint2D(float[][] Points, int RowSize, int shift) {
		int n = -1;
		int x, y, i = RowSize;
		float[] Weight;
		while (--i >= 0) {
			x = y = 0;
			int j = RowSize;
			while (--j >= 0) {
				Weight = Points[++n];
				//				System.out.print  ("Weight[" + n + "] = (");
				//				System.out.println( Weight[0] + " , " + Weight[1] + ")");
				int xOld = x;
				x = ((int) (Weight[0] * (WIDTH >> shift))); // + Clip.x;
				int yOld = y;
				y = ((int) (Weight[1] * (HEIGHT >> shift))); // + Clip.y;
				//				memImg.fillRect(x, y, x+5, y+5); //draw a small Square...
				if ((xOld != 0) || (yOld != 0)) {
					memImg.drawLine(xOld, yOld, x, y);
				}
			}
		}
		i = RowSize;
		while (--i >= 0) {
			x = y = 0;
			n = i - RowSize;
			int j = RowSize;
			while (--j >= 0) {
				n += RowSize;
				Weight = Points[n];
				//				System.out.print  ("Weight[" + n + "] = (");
				//				System.out.println( Weight[0] + " , " + Weight[1] + ")");
				int xOld = x;
				x = ((int) (Weight[0] * (WIDTH >> shift))); // + Clip.x;
				int yOld = y;
				y = ((int) (Weight[1] * (HEIGHT >> shift))); // + Clip.y;
				memImg.fillRect(x, y, x + 5, y + 5); //draw a small Square...
				if ((xOld != 0) || (yOld != 0)) {
					memImg.drawLine(xOld, yOld, x, y);
				}
			}
		}
		System.out.println("Finished painting Grid...");
	}

	/** Overriding the Default Update Method to save clearing the Background */
	public void update(java.awt.Graphics g) {
		paint(g);
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Modelling a mixed Dimension Topology:
	///////////////////////////////////////////////////////////////////////////////////

	/** streamIO of random Numbers */
	protected IStreamIn_Float str = new RandomFast();

	/** Array to be returned by nextItem() */
	float[] arr = new float[3];

	/** Dummy Implementation for IStreamIn!  */
	public long availAble() {
		return 0;
	}

	/**
	 * @see streamIO.IIStreamIn#isValid()
	 */
	public boolean isValid() {
		return true;
	}

	protected int DataItem = 0;

	/** streamIO of float[] containing Input Vectors */
	public Object nextItem() {
		switch (DataItem) {
			case (0) :
				return nextSeparateItems();
			case (1) :
				return nextLorentzItem();
			case (2) :
				return nextTorusItem();
			case (3) :
				return nextMixedTopologyItem();
			case (4) :
				return nextDiagonalItem();
		}
		return arr;
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Calculation of Lorentz Curve
	///////////////////////////////////////////////////////////////////////////////////

	double[] start = { 0.1, 0.1, 0.1 };
	double[] y = VectorDouble.COPY(start);
	double Step = 0.01;
	double x = 0;
	StepRK Stepper = new StepRK(Step, x, y, new OdeLorentz());

	/** streamIO of float[] containing separate Volumes in 3D 	 */
	public float[] nextLorentzItem() {
		Stepper.stepFloat();
		VectorFloat.COPY(Stepper.yv, arr); //convert Double to float
		return arr;
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Calculation of individuall discrete Ball Volumes
	///////////////////////////////////////////////////////////////////////////////////

	/** Positions and Radius for the nextSeparateItems() Method
	  * To separate 4 Points you need exactly 8 Points
	  * which you can achieve by using at least 2 Points per Dimension: 2*2*2 = 8
	  * The Areas are separated as soon as there is more than one Point per Area.
	  * But the Separation also prevents Migration to these Areas!
	  */
	float[][] Items = { { 0, 0, 0, 0.1f }, {
			0, 0, 1, 0.1f }, {
			0, 1, 0, 0.1f }, {
			1, 0, 0, 0.1f }
	};

	/** streamIO of float[] containing separate Volumes in 3D 	 */
	public float[] nextSeparateItems() {
		int i = (int) (Items.length * str.nextFloat());
		float[] Item = Items[i]; //Position and Radius
		System.arraycopy(Item, 0, arr, 0, arr.length);
		arr[0] += Item[3] * str.nextFloat(); //add some Noise of given Radius to the Data
		arr[1] += Item[3] * str.nextFloat();
		arr[2] += Item[3] * str.nextFloat();
		return arr;
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Calculation of Torus or Sphere Surface
	///////////////////////////////////////////////////////////////////////////////////

	/** Larger Radius of the Torus */
	protected static float TorusR1 = 2;

	/** Smaller Radius of the Torus */
	protected static float TorusR2 = 0.3f;

	/** streamIO of float[] containing random 3D Numbers on a 2D Torus Manifold
	  * Since the Mapping onto a Torus is quite uniform,
	  * the streamIO of random Numbers needn't be stretched!
	  */
	public float[] nextTorusItem() {
		double phi = 2 * Math.PI * str.nextFloat();
		double rho = 2 * Math.PI * str.nextFloat();
		arr[2] = (float) Math.sin(rho) * TorusR2; //z
		arr[1] = arr[0] = (float) Math.cos(rho) * TorusR2 + TorusR1;
		arr[1] *= Math.sin(phi); //y
		arr[0] *= Math.cos(phi); //x
		return arr;
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Calculation of a small Disk
	///////////////////////////////////////////////////////////////////////////////////

	/** streamIO of float[] containing random 2D Numbers along a nearly 1D Manifold (Diagonal)	 */
	public float[] nextDiagonalItem() {
		int j = 2;
		float val = str.nextFloat();
		while (--j >= 0) {
			arr[j] = val + str.nextFloat() / 10;
		}
		return arr;
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Calculation of a Sample from the Mixed Topology
	///////////////////////////////////////////////////////////////////////////////////

	/** streamIO of float[] containing random Numbers
	 * These Numbers come from a mixed Topology Manifold consisting of...
	 * a Cube   between (0, 0  ,-0.5) and (1,1  ,+0.5)
	 * a Plane  between (1, 0  , 0  ) and (2,1  , 0  )
	 * a Line   between (2, 0.5, 0  ) and (3,0.5, 0  )
	 * a Circle between (3,-0.5, 0  ) and (5,1.5, 0  )
	 */
	public float[] nextMixedTopologyItem() {
		//categorize them into 4 weighted Regions
		int Case = (int) (40 * str.nextFloat()); //(Nodes are distributed according to their Frequency)
		if (Case < 30) { //a half Cube  (0.7)
			arr[2] = str.nextFloat() - 0.5f; //z
			arr[1] = str.nextFloat(); //y
			arr[0] = str.nextFloat(); //x
		} else if (Case < 37) { //a half Plane (0.2)
			arr[2] = 0; //z
			arr[1] = str.nextFloat(); //y
			arr[0] = 1 + str.nextFloat(); //x
		} else if (Case < 38) { //a Line and   (0.05)
			arr[2] = 0; //z
			arr[1] = 0.5f; //y
			arr[0] = 2 + str.nextFloat(); //x
		} else if (Case < 40) { //a Circle	 (0.05)
			double phi = 2 * Math.PI * str.nextFloat();
			arr[2] = 0; //z
			arr[1] = 0.5f + (float) Math.sin(phi); //y
			arr[0] = 4 + (float) Math.cos(phi); //x
		}
		return arr;
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Moving the 3D Graphics Methods
	////////////////////////////////////////////////////////////////////////////

	private double Angle = 0.01;

	private double turnAngle;

	int xOld;

	int yOld;

	/** Store the Start Position	*/
	public boolean mouseDown(Event evt, int x, int y) {
		xOld = x;
		yOld = y;
		return true;
	}

	/** Use the Difference to the Start Position to rotate the ViewPoint around 0	*/
	public boolean mouseDrag(Event evt, int x, int y) {
		if (drawState != 1) {
			return true;
		}
		if (x != xOld) {
			turnAngle = -Angle * (x - xOld);
			//			P.rotateView(turnAngle);	//rather than recalculating the whole Matrix!
			MatrixFloat.ROTATE_AT(ViewPoint, turnAngle, 0, 1);
			xOld = x;
		}
		if (y != yOld) {
			turnAngle = -Angle * (y - yOld);
			//			P.rotateView(turnAngle);	//rather than recalculating the whole Matrix!
			MatrixFloat.ROTATE_AT(ViewPoint, turnAngle, 1, 2);
			yOld = y;
		}
		this.repaint();
		return true;
	}

	/**
	 * Moves the ViewPoint or the State with key Presses.
	 */
	public boolean keyDown(Event evt, int key) {
		switch (key) { //move the ViewPoint without changing the Rotation Vector!
			//			case 10: state++; Body3DG = null; break;	//Return
			case 1002 :
				ViewPoint[2] += 0.2;
				break; //PgUp
			case 1003 :
				ViewPoint[2] -= 0.2;
				break; //PgDn
			case 1004 :
				ViewPoint[1] += 0.2;
				break; //up
			case 1005 :
				ViewPoint[1] -= 0.2;
				break; //down
			case 1006 :
				ViewPoint[0] -= 0.2;
				break; //left
			case 1007 :
				ViewPoint[0] += 0.2;
				break; //right
			default :
				return true;
		}
		this.repaint();
		return true;
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**
	 */

	/** Tests all Methods of this Class	 */
	public static void testIt() throws java.io.IOException {
		System.out.println("Testing " + KohonenGraph.class.getName());
		KohonenGraph f = new KohonenGraph(); //Frame();
		f.init();
		f.show();
		f.setState(Frame.NORMAL);
		f.setSize(WIDTH, HEIGHT);
		f.drawState = drawStateGraph;
		float[][] gr = Raster.testGenerateGraph();
		int i = gr.length;
		while (--i >= 0) {
			int x = ((int) ((2 + gr[i][0]) * WIDTH / 4)); // + Clip.x;
			int y = ((int) ((2 + gr[i][1]) * HEIGHT / 4)); // + Clip.y;
			System.out.println("(" + x + " , " + y + ")");
			f.memImg.fillRect(x, y, x + 5, y + 5); //draw a small Square...
		}
		f.repaint();
		System.in.read();
		System.in.read();
		///////////////////////////////////////////////////////////////////////////////////
		///
		///////////////////////////////////////////////////////////////////////////////////
		f.drawState = drawStateLorentz;

		///////////////////////////////////////////////////////////////////////////////////
		///
		///////////////////////////////////////////////////////////////////////////////////
		f.drawState = drawStateLineIn2D;
		//		int OutputDim;
		//		int InputDim = 2;
		//		FilterIn_Float2Array FloatStream = new FilterIn_Float2Array(new RandomFast(), InputDim, false);
		Kohonen.LEARN_INITIAL = 0.3f;
		Kohonen k = null; //Alpha should  stay on a high Level until all Crossings are removed!!!
		//Crossings in the Target Domain should never be allowed,
		//except for when the Data Domain has less Dimensions than the Target Domain!
		//instead of distributing the Data randomly,
		//it should better be lined up along the Data HypeCube Diagonal (Plane)

		//		Kohonen.  modelData(OutputDim = 1, InputDim, 3*RowSize, 2*RowSize ,FloatStream, f); //too many Points => no Convergence
		System.in.read();
		System.in.read();
		/*		Kohonen.reModelData(1, k, 3*RowSize, 2*RowSize ,FloatStream, f); //
				System.in.read();
				System.in.read();
				Kohonen.reModelData(1, k, 3*RowSize, 4*RowSize ,FloatStream, f); //
				System.in.read();
				System.in.read();
		
		*/
		f.drawState = drawStatePlaneIn2D; //0
		Kohonen.LEARN_INITIAL = 0.3f;
		//		k = Kohonen.modelData(OutputDim = 2, InputDim, RowSize, RowSize, FloatStream, f);
		System.in.read();
		System.in.read();

		///		Kohonen.LearnFactorInitial = 0.05f; //choose a smaller Factor for Reiterating!
		///		Kohonen.reModelData(2, k, RowSize, 10*RowSize ,FloatStream, f); //
		//		System.in.read();
		//		System.in.read();

		f.drawState = drawStateMixedDim; //1
		//draw the Net in 3 Dimensions
		//add the Data to the Kohonen Net and let it converge...
		Kohonen.LEARN_INITIAL = 0.3f;
		f.DataItem = 0;
		System.out.println("Modelling a Set of 4 discrete Volumes with only 8 Points");
		f.test(Kohonen.MODEL_DATA(0, 3, RowSize = 2, f, f));
		System.in.read();
		System.in.read();

		SparseGraph lst, lst2;
		MatrixGraph mat;
		float[][] Pts;

		int r = 2;
		while (--r >= 0) {
			f.DataItem = 2;
			System.out.println("Modelling a Torus");
			k = Kohonen.MODEL_DATA(0, 3, RowSize = 7, f, f);
			f.test(k);
			f.repaint();
			System.in.read();
			System.in.read();

			lst = new SparseGraph(MatrixFloat.NEG_AT(MatrixFloat.LOG_AT(k.connections)), false, 0, 1000);
			lst2 = new SparseGraph(k.weights, 2);
			//Neighborhood Size: too small => convoluted via Diffusion; too large => bad Convergence, large Errors and Convolution via Hyperdim-Neighborhood, but better Shape Preservation
			mat = new MatrixGraph(k.weights, true); //calculate all Distances...
			Pts = lst2.generateGraph(MatrixFloat.RESIZE(k.weights, k.weights.length, 2));
			//mat); //project into 2D
			MatrixFloat.SUB_AT(Pts, VectorFloat.COPY(Pts[Pts.length / 2])); //move it into the Origin
			f.W3D = new Wire3D(Pts, true, lst); //but the (emptier) TRN Matrix to display...
			f.repaint();
			System.out.println("Torus topologically mapped into 2D");
			System.out.println("This demonstrates the Topology Match, except in the Overlap Area!");
			System.in.read();
			System.in.read();
			System.out.println("making a Sphere out of the Torus!");
			TorusR1 = 0;
			TorusR2 = 2;
		}

		f.DataItem = 3;
		System.out.println("Modelling a mixed Topology Manifold");
		k = Kohonen.MODEL_DATA(0, 3, RowSize = 6, f, f);
		f.test(k);
		System.in.read();
		System.in.read();

		//don't derive the Distances from the Connections Matrix, it is not full enough!
		//just use it to draw the Wire Frame
		lst = new SparseGraph(MatrixFloat.NEG_AT(MatrixFloat.LOG_AT(k.connections)), false, 0, 1000);
		//but from the actual nDim Distances!
		//Uses the (fuller) Distance Matrix to reconstruct
		lst2 = new SparseGraph(k.weights, 1);
		//Neighborhood Size: too small => convoluted via Diffusion; too large => bad Convergence, large Errors and Convolution via Hyperdim-Neighborhood
		mat = new MatrixGraph(k.weights, true); //calculate all Distances...
		Pts = mat.generateGraphics(MatrixFloat.RESIZE(k.weights, k.weights.length, 2));
		//lst); //lst2); //mat); //project into 2D
		MatrixFloat.SUB_AT(Pts, VectorFloat.COPY(Pts[Pts.length / 2])); //move it into the Origin
		f.W3D = new Wire3D(Pts, true, lst); //but the (emptier) TRN Matrix to display...
		f.repaint();
		System.out.println("Mixed Topology mapped into 2D");
		System.out.println("This demonstrates the Topology Mismatch!");
		System.in.read();
		System.in.read();

		System.out.println("Re-modelling a 2D Grid from the Distances");
		float[][] aPts;
		aPts = new float[10 * 10][2];
		int n = aPts.length;
		float y = 1;
		i = 10;
		while (--i >= 0) {
			float x = 1;
			int j = 10;
			while (--j >= 0) {
				aPts[--n][0] = (x -= 0.1);
				aPts[n][1] = y;
			}
			y -= 0.1;
		}
		f.paint2D(aPts, 10, 0);
		System.in.read();
		System.in.read();

		mat = new MatrixGraph(aPts, true); //calculate all Distances...
		//add a Clipping to the Distances, so only local Environments are considered!
		//		float[][] nPts = Wire3D.generateGraph(MatrixFloat.copy(aPts), mat); //project into 2D
		float[][] nPts = mat.generateGraphics(MatrixFloat.RANDOMIZE_AT_1_1(new float[aPts.length][2]));
		//project into 2D
		MatrixFloat.SUB_AT(nPts, VectorFloat.ADD(nPts[nPts.length / 2], -0.5f));
		f.paint2D(nPts, 10, 0);
		System.in.read();
		System.in.read();

		f.DataItem = 1;
		System.out.println("Modelling a Lorentz Attractor with only 7^3 Points");
		k = Kohonen.MODEL_DATA(0, 3, RowSize = 7, f, f);
		//very quickly fills the Points, needs at least 7 Points per Dimension, otherwise it doesn't converge
		f.test(k);
		System.in.read();
		System.in.read(); //use a List to select the Connections to use!

		//don't derive the Distances from the Connections Matrix,
		lst = new SparseGraph(MatrixFloat.NEG_AT(MatrixFloat.LOG_AT(k.connections)), false, 0, 1000);
		//but from the actual nDim Distances!
		lst2 = new SparseGraph(k.weights, 35);
		//Neighborhood Size: too small => convoluted via Diffusion; too large => bad Convergence, large Errors and Convolution via Hyperdim-Neighborhood
		mat = new MatrixGraph(k.weights, true); //calculate all Distances...
		Pts = lst2.generateGraph(MatrixFloat.RESIZE(k.weights, k.weights.length, 2)); //mat); //project into 2D
		MatrixFloat.SUB_AT(Pts, VectorFloat.COPY(Pts[Pts.length / 2])); //move it into the Origin
		MatrixFloat.MUL_AT(Pts, 0.1); //Uses the (fuller) Distance Matrix to reconstruct
		f.W3D = new Wire3D(Pts, true, lst); //but the (emptier) TRN Matrix to display...
		f.repaint();
		System.out.println("Lorentz Attractor topologically mapped into 2D");
		System.out.println("This demonstrates the Topology Match, except in the Overlap Area!");
		System.in.read();
		System.in.read();

		System.out.println("Done...");
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws java.io.IOException {
		testIt();
	}

}
