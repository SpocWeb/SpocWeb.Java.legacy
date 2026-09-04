package graphic.math3D;

import graphic.Wire2D;
import graphs.MatrixGraph;
import graphs.SparseGraph;
import graphs.SparseMatrix;

/**
  * Title: Wire3D<p>
  * Description:<p>
  * Adds a Description of the Wireframe Lines to the Points in Polygon3D
  * This is identical to the Model in @see graphic.math2D.Frame2DMap
  *
  * Design Decisions / Implementation Details:<p>
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	08-07-2002, 12:41 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class Wire3D extends Polygon3D {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////

	/** SparseMatrix Representation of the WireFrame Lines	 */
	protected SparseMatrix LineList;

	/** MatrixGraph Representation of the WireFrame Lines	 */
	protected MatrixGraph LineMatrix;

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor, that takes the Points either as Rows or as Columns of a float[][].
	 * Creates a full (deep) Copy of the Points.
	 * Transposes the Points, when they are given as a Set of Coordinate Lists.
	 */
	public Wire3D(float[][] Points_, boolean ListOfPoints_, SparseMatrix LineList_) {
		super(Points_, ListOfPoints_);
		this.LineList = LineList_;
	}

	/**
	 * Constructor, that takes the Points either as Rows or as Columns of a float[][].
	 * Creates a full (deep) Copy of the Points.
	 * Transposes the Points, when they are given as a Set of Coordinate Lists.
	 */
	public Wire3D(float[][] Points_, boolean ListOfPoints_, MatrixGraph LineMatrix_) {
		super(Points_, ListOfPoints_);
		this.LineMatrix = LineMatrix_;
	}

	/**
	 * Constructor, that takes the Points either as Rows or as Columns of a float[][].
	 * Creates a full (deep) Copy of the Points.
	 * Transposes the Points, when they are given as a Set of Coordinate Lists.
	 */
	public Wire3D(float[][] Points_, boolean ListOfPoints_, float[][] Weights_) {
		super(Points_, ListOfPoints_);
		this.LineMatrix = new MatrixGraph(Weights_, false);
	}

	/**
	 * Constructor, that generates the Points randomly from the Distances in the Adjacency Matrix.
	 */
	public Wire3D(float[][] Distances, int dim) {
		this(new MatrixGraph(Distances, false), dim);
	}

	/**
	 * Constructor, that generates the Points randomly from the Distances in the Adjacency Matrix.
	 */
	public Wire3D(MatrixGraph LineMatrix_, int dim) {
		super(LineMatrix_.generateGraphics(dim), true);
		this.LineMatrix = LineMatrix_;
	}

	/**
	 * Constructor, that generates the Points randomly from the Distances in the Adjacency Matrix.
	 */
	public Wire3D(SparseGraph LineList_, int dim) {
		super(LineList_.generateGraph(dim), true);
		this.LineList = LineList_;
	}

	/**
	 * Constructor, that generates the Points randomly from the Distances in the Adjacency Matrix.
	 */
	public Wire3D(float[][] StartPoints, MatrixGraph LineMatrix_) {
		super(LineMatrix_.generateGraphics(StartPoints), true);
		this.LineMatrix = LineMatrix_;
	}

	/**
	 * Constructor, that generates the Points randomly from the Distances in the Adjacency Matrix.
	 */
	public Wire3D(float[][] StartPoints, SparseGraph LineList_) {
		super(LineList_.generateGraph(StartPoints), true);
		this.LineList = LineList_;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////

	/** draws itself into the given Drawing Context */
	public Wire2D getWire2D(ICoordMapper CD) {
		if (LineList != null) {
			return new Wire2D(CD.mapPt(points), getExtent2D(CD), LineList);
		}
		return new Wire2D(CD.mapPt(points), getExtent2D(CD), LineMatrix);
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + Wire3D.class.getName());
//		testGenerateGraph();
		System.in.read();
		System.in.read();
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws java.io.IOException {
		testIt(args);
	}

}
