package graphic;

import graphs.Edge;
import graphs.MatrixGraph;
import graphs.SparseMatrix;
import streamIO.IIStreamIn;

/**
  * A 2D wireframe model needing only vertex coordinates and undirected edges,
  * stored as either a {@link SparseMatrix} or a {@link MatrixGraph}.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	08-08-2001, 07:05 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * @see Polygon2D base class holding the vertex points
  * @see SparseMatrix one representation of the edge list
  * @see MatrixGraph the other representation of the edge list
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:54:53Z
  * digest: 1d456d041e156550ca8898df3d481b9000384e4deb665d9d1f91b77203ca8fc6
  * stale: false
  * tags: [code/2d_geometry, code/graph_rendering]
  * concepts: [Wireframe Model]
  * facets: {layer: domain, status: legacy, complexity: medium}
  * -->
  */
public class Wire2D
extends Polygon2D {

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
	
	/** Initializing Constructor	 */
	public Wire2D(Point2D[] Points_, Line2D Extent_, SparseMatrix LineList_) {
		super(Points_, Extent_);
		this.LineList = LineList_; }

	/** Initializing Constructor	 */
	public Wire2D(Point2D[] Points_, Line2D Extent_, MatrixGraph LineMatrix_) {
		super(Points_, Extent_);
		this.LineMatrix = LineMatrix_; }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** draws itself into the given Drawing Context.
	 * @param g2D The 2D Graphics Context to draw into
	 * @param LimitFactor The Factor to multiply the Weights with before comparing them to 1
	 *        Only Products larger than 1 are being drawn!
	 */
	public void draw(AGraph2D g2D, double LimitFactor) {
		IIStreamIn iter;
		if (LineList != null) {
			iter = LineList  .Iterator();
		} else {
			iter = LineMatrix.Iterator();
		}
		Edge edge;
		while (null != (edge = (Edge) iter.nextItem())) {
			if (LimitFactor*edge.weight < 1) {
				continue; }
			g2D.drawLine(
				Points[edge.key  ], //slightly ineffective, because Key is less likely to change!
				Points[edge.val]);
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Parent Polygon2D: abstract Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Parent Polygon2D: Implementation / Overrides
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface : abstract Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface : Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Wire2D.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

