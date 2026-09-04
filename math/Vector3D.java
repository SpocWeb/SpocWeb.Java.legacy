package math;

import function.byref.ByRefDouble;

/**The 3D Vector is represented by three Coordinate Systems:
 * Rectangular:	a[0] = x, a[1] = y  , a[2] = z
 * Cylindric:	a[0] = r, a[1] = phi, a[2] = z
 * Sphaeric:	a[0] = r, a[1] = phi, a[2] = theta
 *
 * It can be converted into each of those by partly computing the Angles.	 
 * @see math.VectorDouble which implements most of this Functionality
 */
final public class Vector3D {

	////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * This is equivalent to the Area of the Parallelepiped
	 * bounded by the three given Vectors!
	 * Direct Calculation is faster than using recursive Development
	 * Doesn't save the Operations, but 3 Calls of DET2x2
	 * @return the Determinant of the given 3D Vectors
	 */
	final static public double DET3x3(double[][] a) {
		return DET3x3(a[0], a[1], a[2]); }

	/**
	 * This is equivalent to the Area of the Parallelepiped
	 * bounded by the three given Vectors!
	 * Direct Calculation is faster than using recursive Development
	 * Doesn't save the Operations, but 3 Calls of DET2x2
	 * @return the Determinant of the given 3D Vectors
	 */
	final static public double DET3x3(double[] a0, double[] a1, double[] a2) {
		return DET3x3(a0[0], a0[1], a0[2], a1[0], a1[1], a1[2], a2[0], a2[1], a2[2]); }

	/**
	 * This is equivalent to the Area of the Parallelepiped
	 * bounded by the three given Vectors!
	 * Direct Calculation is faster than using recursive Development
	 * Doesn't save the Operations, but 3 Calls of DET2x2
	 * @return the Determinant of the given 3D Vectors
	 */
	final static public double DET3x3(
		double a00, double a01, double a02,
		double a10, double a11, double a12,
		double a20, double a21, double a22) {
		return a00*(a11*a22-a21*a12)+
			   a01*(a12*a20-a22*a10)+
			   a02*(a10*a21-a20*a11); }

	////////////////////////////////////////////////////////////////////////////
	//  Member Variables
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Coefficients of the Vector
	 */
	public double a[] = new double[3];

////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super() (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/**
	 * Empty Constructor
	 * Results in the Null Vector being created.
	 */
	public Vector3D(){}

	/**
	 * Constructor receiving the three Coordinates
	 */
	public Vector3D(final double x, final double y, final double z) {
		a[0]=x; a[1]=y; a[2]=z; }

	/**
	 * Constructor receiving the Coordinates in an Array
	 */
	public Vector3D(final double[] x) { a=x; }

	/**
	 * Constructor receiving the Coordinates in an Array. 
	 */
	public Vector3D(final float[] x) {
		System.arraycopy(x, 0, a, 0, 3); 
		//a[0]=x[0]; a[1]=x[1]; a[2]=x[2]; 
	}

	////////////////////////////////////////////////////////////////////////////
	//  Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * This is equivalent to the Area of the Parallelepiped
	 * bounded by the three given Vectors!
	 * Direct Calculation is faster than using recursive Development
	 * Doesn't save the Operations, but 3 Calls of DET2x2
	 * @return the Determinant of the given 3D Vectors
	 */
	public double det(final Vector3D v1, final Vector3D v2) {
		return DET3x3(a, v1.a, v2.a); }

	/** @return the Norm (Length) of this Vector */
	public double norm() {
		return Math.sqrt(sqrNorm()); }

	/** @return the Square of this Vector's Norm (Length)  */
	public double sqrNorm() {
		return 
		ByRefDouble.SQR(a[0])+
		ByRefDouble.SQR(a[1])+
		ByRefDouble.SQR(a[2]); }

	/** normalize the Vector in Place */
	public void normalize() {
		mulAt(1/norm()); }

	/** Multiplication with a Scalar */
	public Vector3D mulAt(final double v) {
		a[0]*=v; 
		a[1]*=v; 
		a[2]*=v; 
		return this; }

	public Vector3D mul(final double v) { 
		return new Vector3D(a[0]*=v, a[1]*=v, a[2]*=v); }

	public Vector3D addAt(final Vector3D v) {
		a[0]+=v.a[0]; a[1]+=v.a[1]; a[2]+=v.a[2]; 
		return this; }

	public Vector3D add(final Vector3D v) {
		return new Vector3D(a[0]+v.a[0], a[1]+v.a[1], a[2]+v.a[2]); }

	public Vector3D sub(final Vector3D v) {
		return new Vector3D(a[0]-v.a[0], a[1]-v.a[1], a[2]-v.a[2]); }

	/** Scalar "Product" 
	 * linear Mapping of Vector v to rational Numbers. 
	 * @param v
	 * @return
	 */
	public double map(final Vector3D v) {
		return a[0]*v.a[0] + a[1]*v.a[1] + a[2]*v.a[2]; }

	/**Calculates the Volume of the 'Spat' = 3* Volume (Pyramid)
	 * with the four given Corners. */
	public double PyramidVolume(final Vector3D v1, final Vector3D v2, final Vector3D v3) {
		return sub(v1).det (sub (v2), sub (v3)); }
	
	/** 
	 * 
	 * @param rayOrigin The Origin of the Ray (1 Degree of Freedom) 
	 * @param rayDirection The Direction of the Ray 
	 * @param v0 Three Points...
	 * @param v1 ...which define ... 
	 * @param v2 ...the Plane and the Triangle. 
	 * @return the Point in which the Ray intersects the Triangle, 
	 * 			null if the Ray is parallel to the Triangle 
	 * 			or hits the Plane outside the Triangle. 
	 */
	final static public Vector3D rayIntersectsTriangle(final Vector3D rayOrigin, final Vector3D rayDirection, 
	final Vector3D v0, final Vector3D v1, final Vector3D v2) {
		// Calculate the edges of our triangle in the correct order
		// for our winding (i.e. the Inner always lies 'right' to the Edge in the Plane).
		final Vector3D edge1 = v1.sub(v0); 
		final Vector3D edge2 = v2.sub(v1); 
		final Vector3D edge3 = v0.sub(v2); 
		
		// Compute normalized normal of triangle by crossing two edges
		final Vector3D planeNormal = edge1.CrossProduct(edge2); //these three Values...  
		planeNormal.normalize(); //...could be pre-calculated for a Triangle!
		
		final Vector3D Q = rayPlaneIntersection(rayOrigin, rayDirection, v0, planeNormal);
		if (Q == null) {
			return Q; }
		// Now let's test to see if it's on the inside edge of each side.
		// if Q is outside of any of the edge planes, we are done.
		
		// Determinant = N°(Q-Vertex of Edge)
		// Determinant > 0 : Q is on the outside of the current edge.
		// Determinant = 0 : Q is on the current edge.
		// Determinant < 0 : Q is on the inside of the current edge.
		if (Q.sub(v0).det(edge1, planeNormal) > 0) { 
			return null; }
		if (Q.sub(v1).det(edge2, planeNormal) > 0) { 
			return null; }
		if (Q.sub(v2).det(edge3, planeNormal) > 0) { 
			return null; }
		// Q is on inside of all three edges of triangle.
		return Q;
	}

	/** 
	 * 
	 * @param rayOrigin The Origin of the Ray (1 Degree of Freedom) 
	 * @param rayDirection The Direction of the Ray 
	 * @param planePoint any Point in the Plane (2 Degrees of Freedom) 
	 * @param planeNormal the Normal defining the Plane 
	 * @return the Point in which the Ray intersects the Plane, 
	 * 			null if the Ray is parallel to the Plane 
	 * 			or lies within the Plane. 
	 */
	private static Vector3D rayPlaneIntersection(
		final Vector3D rayOrigin,
		final Vector3D rayDirection,
		final Vector3D planePoint,
		final Vector3D planeNormal) {
		final double t = rayPlaneParameter(rayOrigin, rayDirection, planePoint, planeNormal);
		if (Double.isInfinite(t) || //parallel outside the Plane, no Solution 
			Double.isNaN(t)) { //parallel, inside the Plane, infinite Solutions
			return null; }
		//single Solution: 
		//Start at P and move along t units in the direction of Dir 
		//to find the intersection point Q.
		//
		// Q = P + t * Dir
		return rayOrigin.add(rayDirection.mul(t)); //addProd would be faster
	}
	
	/** 
	 * 
	 * @param rayOrigin The Origin of the Ray (1 Degree of Freedom) 
	 * @param rayDirection The Direction of the Ray 
	 * @param planePoint any Point in the Plane (2 Degrees of Freedom) 
	 * @param planeNormal the Normal defining the Plane 
	 * @return the Line Parameter for which the Line intersects the Plane 
	 * 		Infinity if the Line lies parallel to the Plane 
	 * 		NaN if the Line lies in the Plane 
	 */
	private static double rayPlaneParameter(
		final Vector3D rayOrigin,
		final Vector3D rayDirection,
		final Vector3D planePoint,
		final Vector3D planeNormal) {
		// Ray equation
		// Q = P + t*Dir
		//
		// Plane equation
		// Ax + By + Cz + D = 0
		//
		// normal°Q + D = 0
		// normal°(P + t*Dir) + D = 0
		// normal°P + t*normal°Dir + D = 0
		//
		// t = -(D + normal°P)
		// ———————-
		// normal°Dir
		//
		// For any plane, D is the distance from the origin to the plane.
		// By definition, D can be computed by calculating the negative
		// of the normal dotted with any vertex on the plane.
		// D = -normal°vertex0
		final double D = -planeNormal.map(planePoint);
		
		// denominator = Normal.Dir
		final double denominator = planeNormal.map(rayDirection);
		
		// Check if ray is parallel with the plane of the triangle
		//if (Math.abs(denominator) < 0.0001f) {
		//	return null; }
			
		// numerator = -(Normal.P + D)
		final double numerator = - (planeNormal.map(rayOrigin) + D);
		
		//t tells us how far from P along Dir we intersect the plane of the triangle. 
		//Note that t can be negative if the intersection point is behind P from the perspective of Dir.
		//If ray is parallel with the plane of the triangle then t is infinite or NAN! 
		final double t = numerator / denominator;
		return t;
	}

	/**This contains the Euler turning angles (alpha, beta, gamma)
	 * in it's coordinates.
	 * m1, m2, m3 form the Matrix.
	 * Directly filling the Turning Matrix is very fast.	 */
	public void DrehMatrix (Vector3D[] Turn) {
		double c2 = Math.cos (a[2]);
		double s2 = Math.sin (a[2]);
		double c1 = Math.cos (a[1]);
		double s1 = Math.sin (a[1]);
		double c0 = Math.cos (a[0]);
		double s0 = Math.sin (a[0]);
		Turn [2].a[0]= s1;
		Turn [2].a[1]=-c1*s2;
		Turn [2].a[2]= c1*c2;
		Turn [0].a[0]= c0*c1;
		Turn [1].a[0]=-s0*c1;
		c1=c2*c0;
		c2=c2*s0;
		c0=s2*c0;
		s0=s2*s0;
		Turn [0].a[1]=c2+c0*s1;
		Turn [0].a[2]=s0-c1*s1;
		Turn [1].a[1]=c1-s0*s1;
		Turn [1].a[2]=c0+c2*s1;
	}

	/**Calculates the 3 Euler Turning Angles of the Matrix 'Turn',
	 * which describes a turning Operation in 3 Dimensions.	 */
	public Vector3D angles (Vector3D[] Turn) {
		a[1]=Math.atan2 (Math.sqrt (1.0-ByRefDouble.SQR(Turn [2].a[0])),Turn [2].a[0]); // = ArcSin (Turn [2,0]);
		a[0]=Math.atan2 (Turn [0].a[0],-Turn [1].a[0]);
		a[3]=Math.atan2 (Turn [2].a[2],-Turn [2].a[1]);
		return this; }

	/**Calculates the Cross Product of two Vectors in 3 Dimensions.
	 * Actually it is a coincidence that the Cross Product,
	 * which is in Fact a Differential Form,
	 * contains exactly 3 Coordinates in 3 Dimensions.
	 * This makes it possible to represent is as a Vector. 	 */
	public Vector3D CrossProduct (final Vector3D v) {
		Vector3D res = new Vector3D();
		for (int i=-1; ++i <= 2;) {
			final int j = (i == 2) ? 0 : i+1;
			final int k = 3-j-i;
			res.a[i] = a[j]*v.a[k] - a[k]*v.a[j];
		}
		return res; }

	/**
	 * Converts Sphaeric Coordinates (r,phi,theta) 
	 * into Rectangular Coordinates (x,y,z)
	 */
	public Vector3D Sphaeric2Rect() {
		Vector2D V2 = new Vector2D(a)			.Polar2Rect();
		Vector2D V3 = new Vector2D(V2.a[0],a[3]).Polar2Rect();
		return new Vector3D(V3.a[0],V2.a[1],V3.a[1]);
		//as fast as the calculations below.
//		Vector3D tmp = new Vector3D();
//		tmp.a [2]=a[0]*Math.sin(a [2]);
//		tmp.a [1]=a[0]*Math.cos(a [2]);	//saves 1 Multiplication
//		tmp.a [0]=tmp.a [1]*Math.cos(a [1]);
//		tmp.a [1]*=         Math.sin(a [1]);
//		return tmp;
	}

	/**
	 * Converts Rectangular Coordinates (x,y,z)
	 * into Sphaeric Coordinates (r,phi,theta)
	 */
	public Vector3D Rect2Sphaeric() {
		Vector2D V2 = new Vector2D(a)			.Rect2Polar();
		Vector2D V3 = new Vector2D(V2.a[0],a[3]).Rect2Polar();
//		double z;
//		if (V2.a[0] > (z = Math.abs(a[3])))	V3.a[0]= V2.a[0]*Math.sqrt(1.0+Sqr(z/V2.a[0]));
//		else								V3.a[0]= z		*Math.sqrt(1.0+Sqr(V2.a[0]/z)); //besser als r=SqRt (f+Sqr (z));
//		V3.a[1] = Math.atan(z/V2.a[0]);	//theta
		return new Vector3D(V3.a[0],V2.a[1],V3.a[1]);
		//There is a slight Optimization missing, because V2.a[0] >= 0
		//So the test for the second Dimension is easier
		//And you can use ArcTan instead of ArcTg;
	}

	/**
	 * Converts Cylindric Coordinates into Rectangular Coordinates
	 */
	public Vector3D Cylindric2Rect() {
		Vector2D V2 = new Vector2D(a).Polar2Rect();
		return new Vector3D(V2.a[0],V2.a[1],a[2]); }

	/**
	 * Converts Rectangular Coordinates into Cylindric Coordinates
	 */
	public Vector3D Rect2Cylindric() {
		Vector2D V2 = new Vector2D(a).Rect2Polar();
		return new Vector3D(V2.a[0],V2.a[1],a[2]); }

	/**
	 * Liefert den Mittelpunkt M,
	 * die Haupt-Minoranten Det,
	 * die Laenge der Hauptachsen HA,
	 * den Winkel einer Hauptachse mit der x-Richtung
	 *
	 * true  : sowie den Parameter des Kegelschnittes
	 * false : keine reelle Loesung,bzw. nur ein Punkt   | QK[1] QK[2] LK[1]|
	 *                                               X = | QK[2] QK[3] LK[2]|
	 * Dies entspricht einer Quadrik mit dieser Matrix   | LK[1] LK[2] LK[3]|
	 * in homogenen Koordinaten V = (x,y,1) mit der Gleichung:
	 * 0 = V*X*V = QK [1]*x^2+QK [2]*2xy+QK [3]*y^2+LK [1]*2x+LK [2]*2y+LK [3]
	 *
	 *                                     Det[1]| Det[2] <> 0   Det[2] = 0
	 *									   ------+----------------------------
	 * Es liegt in Abhaengigkeit von Det    > 0  |	Ellipse(*)		Punkt
	 * folgende Situation vor :             = 0  |	Parabel		Geradenpaar(+)
	 *                                      < 0  |	Hyperbel		Kreuzung
	 * Bem:(*) nur wenn		   QK[1]*Det[2] > 0,	(sonst nur IMAGINÄRE Loesung)
	 *     (+) parallel ,wenn |QK[2] LK[2]| < 0		(sonst nur IMAGINÄRE Loesung)
	 *         identisch,wenn |LK[2] LK[3]| = 0	 */
	public boolean Quadrik   (
		Vector3D QK,
		Vector3D LK,
		Vector2D _M,
		Vector2D _Det,
		Vector3D _HA,
		Vector2D PhiPar)
	{	//Calculate the EigenValue of QK
		boolean tmp = false;		//{positive Determinante !}
		Vector2D V1 = new Vector2D(QK.a[0], QK.a[1]);
		Vector2D V2 = new Vector2D(QK.a[1], QK.a[2]);
		Vector2D V3 = new Vector2D(LK.a[0], LK.a[1]);
//		ByRef.ByRefDouble Det = new ByRef.ByRefDouble(); //jetzt in _HA(3)
		if (Vector2D.EW_2x2(V1.a, V2.a, _HA.a))	//{f und g enthaelt die Eigenwerte !}
			tmp = ((QK.a[1] > 0) ^ (_Det.a[2] > 0)); //{Paraboloid/Ellipsoid liegt unterhalb der x-y-Ebene bis auf evtl. 1 Punkt !}
		else {   //{negative Determinante => komplexe E.W.}
			_HA.a[1] = Math.abs(_HA.a[1]);
			_HA.a[2] =			_HA.a[1] ; }
		_Det.a[1] = _HA.a[2]; //Det.Value;
		double fa = V2.DET2x2(V3);
		double sx = V3.DET2x2(V1);
		_Det.a[2] = _Det.a[1]*LK.a[3]+ //{Entwicklungs-Satz nutzt nicht die Symmetrie aus,ist aber ca. genauso schnell}
					fa		 *LK.a[1]+
					sx		 *LK.a[2]; //{Pot2Mul (QK [2]*LK [1]*LK [2],1)-Sqr (LK [1])*QK [3]-Sqr (LK [2])*QK [1];}
		double Hilf = _Det.a[2]/_Det.a[1];
		_HA.a[1] = Math.sqrt(Math.abs(Hilf/_HA.a[1]));
		_HA.a[2] = Math.sqrt(Math.abs(Hilf/_HA.a[2]));
		_M .a[1] = fa/_Det.a[2];
		_M .a[2] = sx/_Det.a[2];
		double Skalar = QK.a[0] + QK.a[2];	//already calculated in EW_2x2
		PhiPar.a[0] = Math.atan2 (2*QK.a[2],QK.a[1]-QK.a[3])/2;
		PhiPar.a[1] = Math.sqrt(Math.abs(_Det.a[2]/(Skalar*Skalar*Skalar))); //{Skalar schon in EW_2x2 gesetzt}
		return tmp; }

}