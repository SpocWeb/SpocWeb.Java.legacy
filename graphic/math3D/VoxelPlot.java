package graphic.math3D;

import graphic.IGraphShape;
import graphic.Point2D;

import java.awt.Color;
/*
import Graph2D.*;
import Ring.*;
import Metric.*;
import Vector.*;
import ByRef.*;
import Body.*;
*/
/**
 * Helper Class that encapsulates Drawing a Sequence of HyperCubes (Voxels) in 3D.
 * It is supposed to being called by the recursive Rastering Routine.
 * In this Example the Value is calculated.
 */
public class VoxelPlot
	extends ASpatial {

	/**Scaling for the Color the Arrows are drawn, using ColorMode = true. */
	public float ColorFactor = 255;

	/**Radius of the Points drawn, when using fixed Size,
	 * resp. Factor for Scaling	the Size when SizeMode = true.
	 * If the Radius is negative, only a Pixel is set.
	 * That makes drawing faster! */
	public float Radius = 5;

	/**palette for Scalar Voxel Plots. If the palette is empty (like at the Start),
	 * the Colors are generated from the Integer Ordinates directly.
	 * Determines the coloring of the Square's sides.
	 * Must contain three arrays of Colors for the three visible Sides.	 */
	public Color[][] Palette;

	/**Offset for the Colors of this palette.
	 * This Offset is being added to each Ordinate giving the Index of the Color.	 */
	public int ColorOffset;

	/**Graphics Context to point to	 */
	private IGraphShape g;

	/**Reference to the Coordinate System for Conversion	 */
	private ICoordMapper CD;

	/**Constructor, taking the Maximum Indices to size the buffers. 	 */
	public VoxelPlot(IGraphShape g2D, ICoordMapper CD_, int[] MaxIndex_) {
		CD = CD_; g = g2D;
		PNew = new Point2D[MaxIndex_[1]+2][MaxIndex_[0]+2];
		POld = new Point2D[MaxIndex_[1]+2][MaxIndex_[0]+2];
		MaxIndex = new int[MaxIndex_.length];
		System.arraycopy(MaxIndex_, 0, MaxIndex, 0, MaxIndex.length);
	}

	/**Maximum Index for each dimension	 */
	private int[] MaxIndex;

	/**Cache for the old Points	 */
	private Point2D[][] POld;

	/**Cache for the new Points	 */
	private Point2D[][] PNew;

	/**Cache for the old Points	 */
	private Point2D[][] tmp;

	/**Four Point Polygon for painting the Planes directly	 */
	private Point2D[] Plane = new Point2D[4];

	/**Increment for painting	 */
	int[] Incr = {1,1,1};

	/**Draws the given Value S (may even be a float[]) at the given Vector V.  */
	/**Resets the given Coordinate of the Algorithm	 */
	public void reSet(int dim) {
		if (dim >= 0) Incr[dim] = +1; else Incr[-dim-1]=-1;
		if (dim == 1)
		{tmp = POld; POld = PNew; PNew = tmp;} //You could save this operation
	}

	/**This Function determines the color in which the Square has to be painted
	 * Here the Value of S is completely ignored, but the Index is used
	 * to create a Menger Schwamm.
	 * For transparent Voxel Pictures just use the Value of S! */
	private int Plot (Object S, int[] Index) {
		boolean start;
		int[] i = new int[Index.length];
		int m = -1;	while (++m < Index.length) i[m] = Index[m]-((Incr[m]+1)>>1);	//only if you paint from lower to higher
		do { //Test whether this spot is empty
			start = false;
			int j, k = -1, l = 0;
			while (++k < 3)
				if (i[k] > 0) {
					start = true;
					j = i[k]; i[k] /= 3;
					if ((j-i[k]*3) == 1) l++;
				}
			if (l > 1) return -1;
		} while (start);
		return 0; }

	/**This drawing Routine fills the whole R^3 and can also be used
	 * to paint transparent Pictures in a Volume.
	 */
	public void moveTo (float[]  V, Object S, int[] Index) {
		//3-dim: Needs caching of 1 Plane, 1 Row and 1 Point, compare to 2-dim: 1 Row and 1 Point
		if (Index[1] == 0);
		Plane [0] = PNew[Index[1]][Index[0]] = CD.mapPt(V);
//		if (! connect) {connect = true; return;}
		int k = -1, c;
		while (++k < Index.length)
		{int l = Index[k]-Incr[k]; if ((l < 0) || (l > MaxIndex[k])) return;}
		if ((c = Plot(S, Index)) >=  0)	// || (Index[2] != 0)) //not necessary
		{	//Using Incr automatically paints the correct planes in correct order!!!
			Plane [1] = PNew[Index[1]		 ][Index[0]-Incr[0]	];
			Plane [2] = PNew[Index[1]-Incr[1]][Index[0]-Incr[0]	];
			Plane [3] = PNew[Index[1]-Incr[1]][Index[0]			];
			g.setColor(Palette[0][c]); g.fillPolygon(Plane);
			Plane [2] = POld[Index[1]		 ][Index[0]-Incr[0]	];
			Plane [3] = POld[Index[1]		 ][Index[0]			];
			g.setColor(Palette[1][c]); g.fillPolygon(Plane);
			Plane [1] = PNew[Index[1]-Incr[1]][Index[0]];
			Plane [2] = POld[Index[1]-Incr[1]][Index[0]];
			Plane [3] = POld[Index[1]		 ][Index[0]];
			g.setColor(Palette[2][c]); g.fillPolygon(Plane);
		}
		//Setting only Points is only for extremely fine Rasters ..
		//...and leads to moirees and patterns!
/*		Point2D P1 = CD.map(V);
		g.setColor(Color.blue);
		g.setPixel(P1);
		g.setColor(Color.green);
		P1.x++ ; g.setPixel(P1);
//		P1.x-=2; g.setPixel(P1);
//		P1.x++;
		P1.x--;
		g.setColor(Color.red);
		P1.y-- ; g.setPixel(P1);
//		P1.y+=2; g.setPixel(P1);
*/
	}

	/**draws the Scalar Value S at the Position V with the Parameters given in the Constructor	 */
	public void moveTo(float[] V, Object S) {
		throw new AbstractMethodError(); }

}