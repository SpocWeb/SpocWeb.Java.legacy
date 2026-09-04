package graphic.math3D;

import math.matrix.MatrixFloat;
import math.vector.VectorFloat;

/**
 * Stores the Parameters of a projective or planar Mapping of n to n-1 Dimensions.
 * The Result is centered around (0), because the final translation
 * cannot be incorporated in the Translation of the ViewPoint,
 * due to the rescaling of Coordinates on Projection by the last Coordinate.
 *
 * This makes modifying the Rotation Matrix independent
 * from the Translation Vector, which is the Start Point a[0].
 *
 * Rotations can be done around the ViewPoint or the StandPoint.
 * Both are not easily done because of the Translations involved!
 * The Mapping is done by: V' = (V - StandPoint)*rot
 *
 * When CoordZ = true or project = true,
 * then the mapped float[] retains the z Coordinate in it's last Component!
 *
 * Da hier Zeilenvektoren von links multipliziert
 * (und damit die Zeilen von Matrizen addiert) werden,
 * müssen die Spalten mit ratio skaliert werden und nicht die Zeilen!!!
 *
 *
 */
public class Projection
extends Line {

	/**Signals to the Line Object, that Items dependent on the Width have changed
	 * Overwritten, because validRot is new!	 */
	public void inValidate() {
		super.inValidate(); rot = null; }

	//////////////////////
	//	Rotation Matrix	//
	//////////////////////

	/** Rotaton Matrix, does Mapping of Dimensions, calculated from Rotation Vector:	 */
	protected float[][] rot;

	/** @returns the Rotation Matrix of the Projection.	 */
	protected float[][] getRotAt() {
		if (rot == null) {
			float[] Width = getWidthAt();	//returns the Width Vector
			int len = Width.length-1;
			rot = MatrixFloat.ALIGN_MATRIX_AT(VectorFloat.COPY(Width), len);	//Use the Diagonal Vector to define the Direction
			if (ratio != 1) {	//Scale the first two Dimensions, but only the Columns!!!
				MatrixFloat.MUL_AT(rot, ratio, 0, len); }
			rot = MatrixFloat.TRP_AT(rot); //by transposing only afterwards we can use the shorter mulAt of Row Vectors.
//			if (ratio != 1) {	//Scale the first two Dimensions, but only the Columns!!!
//				MatrixFloat.mulAt(rot, ratio, 0, rot.length, 0, len); }
			//except for the last Dimension! So the projection still does scale (in projective View)
			letProjectZ(); //initialize the Rest
		}
		return rot; }

	/**Returns the Rotation Matrix of the Projection.	 */
	public float[][] getRot() {
		return MatrixFloat.COPY(getRotAt());}

	//////////////////////////
	//	Projection Settings	//
	//////////////////////////

	/**Determines, if a projective Geometry is used.
	 * This takes 50% more Conversion Effort, because the Distance has to be calculated.	 */
	private boolean project;

	/**Retrieve the Projection Settings (planar or projective Geometry)	 */
	public boolean getProject() { return project; }

	/**Set the Projection Settings (planar or projective Geometry)
	 * This determines if, on mapping, the first Coordinates are scaled
	 * according to their z-Distance (the last Coordinate).	 */
	public void letProject(boolean Project) {
		if (this.project != Project) { //if something changes, only a small Optimization!
			this.project  = Project;
			letProjectZ();
		}
	}

	/**Determines, if the last z Coordinate is calculated or not.
	 * This takes 50% more Conversion Effort, because the Distance has to be calculated.	 */
	private boolean calcZ;

	/**Retrieve the Projection Settings (planar or projective Geometry)	 */
	public boolean getCalcZ(){return calcZ;}

	/**Set the Projection Settings (planar or projective Geometry)
	 * This determines if, on mapping, the first Coordinates are scaled
	 * according to their z-Distance (the last Coordinate).	 */
	public void setCalcZ(final boolean calcZ) {
		if (this.calcZ != calcZ) { //if something changes, only a small Optimization!
			this.calcZ  = calcZ;
			letProjectZ();
		}
	}

	/**Adjusts the Dimensions of the Matrix,
	 * the Vector is subtracted first with all Dimensions!
	 * This saves a 3rd of the Calculations.
	 */
	private void letProjectZ() {
		rotLength = rot.length;
		if (project || calcZ) return;	//for planar Mapping and when no z Coordinate is needed.
		--rotLength; //MatrixFloat.setDimAt(rot, rot.length-1);	//
	}

	/**Scaling Ratio, used to scale the first two Coordinates.
	 * Stored, because it is necessary to undo it on rotating the Matrix.
	 * A Ratio of 1 corresponds to the Distance being the Diagonal
	 * and makes undoing the Scaling unnecessary in the Rot Matrix.
	 * Initialized in the Constructor...
	 */
	private float ratio;

	/** Dimension of the Rotation Matrix to be used in calculating the Result
	 * may be shorter than Matrix Size due to planar Projection */
	private int rotLength;

	/**Rotates the current view around the View Vector, usually not applied,
	 * because the user would get confused with the view (see 'Descent' game)
	 */
	public void rotateView (float phi) {
		MatrixFloat.ROTATE_AT(rot, phi, 1, 2); }	//not affected whether it is 2 or 3-dim.
	//Problem, because the Target Translation is embedded in the Origin Translation.
	//You have to rescale this Translation, if you rotate this View! but this is easy when you conserve Target!

	/** Rotates the current view around the StandPoint in the given Dimension.  */
	public void rotateView (float phi, int dim) { //scale the last Dimension to 'ratio' too!
		if ((dim != 2) && (ratio != 1)) { MatrixFloat.MUL_AT(rot,   ratio, 0, rot.length, 2, 2); } //VectorFloat.mulAt(rot[2],   ratio); }
		MatrixFloat.ROTATE_AT(rot, phi, (dim + 1)%3 , (dim + 2)%3);
		if ((dim != 2) && (ratio != 1)) { MatrixFloat.MUL_AT(rot, 1/ratio, 0, rot.length, 2, 2); } //VectorFloat.mulAt(rot[2], 1/ratio); }
		//Also the End Vector has to be rotated!
		//Otherwise it will be reset on the next recalc() Action.
	}	//not affected whether it is 2 or 3-dim.
	//Problem, because the Target Translation is embedded in the Origin Translation.
	//You have to rescale this Translation, if you rotate this View! but this is easy when you conserve Target!

	//////////////////////
	//	Constructors	//
	//////////////////////

	/**With a projective mapping it is not necessary to calculate the last Coordinate.
	 * But the Mapping has to be scaled to the original length of all items displayed.
	 * If this is null, projective Mapping is assumed,
	 * and the Distance of the Viewer to the ViewPort is assumed to be about the same
	 * as the Width of the ViewPort. That Way the Scaling Factor becomes 1.0!
	 *
	 * Ratio of Lengths only suitable for parallel Mapping.
	 * With Integer TargetLength the Inverse would go wrong, so use float!
	 * This is able to directly deliver Screen Coordinates. */


	/**Constructor, which takes Start- and Endpoint of the Projection Line,
	 * defaults to projective Geometry, because no typical Length is given.
	 * @param Start the Beginning of the Projection Line, the Point Of View POV
	 * @param Stop the Endpoint of the Projection Line
	 */
	public Projection (float[] Start, float[] Stop, float TargetLength) {
		this (Start, Stop, false, TargetLength, 0); }

	/**Constructor, which takes Startpoint and Direction or EndPoint
	 * of the Projection Line, as well as the original and targeted Length.
	 * If the OriginLength is null, projective Geometry is used.
	 * Since the View has to stay natural, the ratio is given by scalar Arguments.
	 * @param Start the Beginning of the Projection Line, the Point Of View POV
	 * @param EndDir the Direction or Endpoint of the Projection
	 * @param direction Flag whether to interpret EndDir as a Point or a Direction.
	 * @param OriginLength the typical Length of a Line, if 0, projective Geometry is used.
	 */
	public Projection (float[] Start, float[] EndDir, boolean direction, float TargetLength, float OriginLength) {
		super (Start, EndDir, direction);
		ratio = TargetLength;
		if(!(project = (OriginLength == 0))) {
			ratio /= OriginLength; } //
	}

	/**Constructor, which takes Start- and Direction or Endpoint of the Projection Line,
	 * defaults to Projective Geometry, because no original Length is given.	 */
	public Projection (float[] Start, float[] Stop, boolean direction, float[] Target) {
		super (Start, Stop, false); project = false;
	}

	/**Empty Constructor; for newInstance	 */
//	protected Projection () { super ();} //recalc(null, null); }


	//////////////////
	//	Operations	//
	//////////////////

	/**Transforms the float[] arg in place by multiplying it with a Rotation Matrix,
	 * adding a Vector and projecting it along the last Dimension.
	 *
	 * With a planar mapping it is not necessary to calculate the z Coordinate. 	 */
	public float[] mapAt(float[] arg) {
		System.arraycopy( map(arg), 0, arg, 0, arg.length);
		return arg; }

	/**Contains the z Coordinate of the last Mapping, if project = true.	 */
	public float zCoordInv;

	/** Flag for optimizing 3D Rotation: don't calculate Points in the Viewer's Back 	 */
	public boolean skipNegativePoints = false; 

	/**Transforms the float[] arg by multiplying it with a Rotation Matrix,
	 * adding a Vector and projecting it along the last Dimension.
	 *
	 * With a planar mapping it is not necessary to calculate the z Coordinate.
	 * Except if you want it, e.g. for the Distance! 	 */
	public float[] map(final float[] arg) {
		final float[][] rotMatrix = getRotAt(); //also to init rotLength! 
		final float[] ret = new float[rotLength]; 
		final float[] diff = VectorFloat.SUB(arg, this.a[0]);	//Translate to the StandPoint (with ALL Coordinates)
		if (rotLength == 3) { //TODO: calc 3rd Dim first and stop if negative
			MatrixFloat.MAP(diff, getRotAt(), 3, 2, ret); 
			if (skipNegativePoints && (ret[2] <= 0)) {
				return null; }
		}
		MatrixFloat.MAP(diff, rotMatrix, 2, 0, ret); 	//Rotate to bring the ViewPoint in Sight
		if (! this.project) { //TODO: if calculating the last Dim anyway, use it to rule out negative z Coordinates and thus save calculating the others. 
			return ret; }	//Don't need to calculate the last Dimension on planar Mapping.
		//Rescale the Widths according to their Distance
		final int len = ret.length-1;
		return VectorFloat.MUL_AT(ret, zCoordInv = 1/Math.abs(ret[len]), 0, len);	//This Component can be used
	}	//zCoordInv used later to calculate the Scaling of local Components.

	/**Transforms the float[] arg by multiplying it with a Rotation Matrix,
	 * adding a Vector and projecting it along the last Dimension.
	 *
	 * With a planar mapping it is not necessary to calculate the z Coordinate.
	 * Except if you want it, e.g. for the Distance! 	 */
	public float[] map(double[] arg) {
		float[] V = VectorFloat.SUB(this.a[0], arg);	//Translate to the StandPoint (with ALL Coordinates)
		V = MatrixFloat.MAP(V, getRot(), rotLength, 0); 	//Rotate to bring the ViewPoint in Sight
		if (! this.project) {
			return V; }	//Don't need to calculate the last Dimension on planar Mapping.
		//Rescale the Widths according to their Distance
		int len = V.length-1;
		return VectorFloat.MUL_AT(V, zCoordInv = 1/V[len], 0, len);	//This Component can be used
	}	//Value also used later to calculate the Scaling of local Components.

	//////////////////////
	//	Object Methods	//
	//////////////////////

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * NewInstance also clones the Types, but does not initialize them!
	 * When overriding, use newInstance on all Components.	 */
//	public CopyAble newInstance() { return new Projection(Carry); }

}
