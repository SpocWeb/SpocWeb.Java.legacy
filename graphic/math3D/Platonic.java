package graphic.math3D;

import math.matrix.MatrixDouble;
import math.vector.VectorDouble;
import streamIO.Assert;
import streamIO.Log;

/**Contains the Coordinates for the five Platonic Bodies
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:43:33Z
 * digest: 658452854a9bea9ff704f76e4f5357d4ebbdebcb30c762621df00ec1f79f04e5
 * stale: false
 * tags: [code/platonic_solids, code/3d_geometry]
 * concepts: [Platonic Solid Coordinate Generator]
 * facets: {layer: domain, status: legacy, complexity: medium}
 * -->
 */
public class Platonic {
	
	/** The square root of 3, reused by {@link #MatrixTetrahedron}. */
	final static public double Sqrt3 = Math.sqrt(3);
	
	/** Matrix to transform cartesian Coordinates 
	 * into 2D or 3D hexagonal Coordinates, 
	 * where each Node has d*(d+1) nearest Neighbors
	 * instead of 2^d nearest Neighbors(cartesian).
	 * d	cartesian	hexagonal
	 * 0	 1			 1
	 * 1	 2			 2
	 * 2	 4			 6
	 * 3	 8			12
	 * 4	16			20
	 * 5	32			30
	 * 
	 * The Base Cell of this Coordinate System consists of 
	 * 2 Tetrahedrons sitting on 
	 * 2 half Octahedrons 
	 * 
	 * It can be viewed as a flattened cube 
	 * with Plane and Volume Diagonals of the same Length as the Edges. 
	 * 
	 * @see #MatrixTetrahedron2 is isomorphic
	 */
	final static public double[][] MatrixTetrahedron = {
		{ 2,       0, 0},
		{ 1,   Sqrt3, 0}, 
		{ 1, 1/Sqrt3, Math.sqrt(8/3.0)}
	};
	
	/** The square root of 2, reused by {@link #MatrixTetrahedron2} and the Octahedron/Dodecahedron coordinates. */
	final static public double Sqrt2 = Math.sqrt(2);
	
	/** Alternative Matrix to transform cartesian Coordinates 
	 * into 2D or 3D hexagonal Coordinates, 
	 * where each Node has d*(d+1) nearest Neighbors
	 * instead of 2^d nearest Neighbors(cartesian). 
	 * 
	 * (rotated in Space) the Base Cell is half a square Pyramid (cartesian), 
	 * and the third Coordinate is aligned to the Pyramid Edge. 
	 * 
	 * @see #MatrixTetrahedron is isomorphic
	 */
	final static public double[][] MatrixTetrahedron2 = {
		{1  ,0  , 0},
		{0  ,1  , 0},
		{0.5,0.5, Sqrt2},
	};
	
	/**The Points of a Tetrahedron
	 * @see #Tetrahedron is it's own Fourier Transform; it has 
	 * 4 Points 
	 * 4 Planes 
	 * It is the minimum 3D Body, 
	 * but not sufficient to fill up 3D Space. 
	 * 
	 * 
	 */
	public static final
	int[][] PointsTetrahedron ={
		{ 1, 1, 1},
		{-1, 1,-1},
		{-1,-1, 1},
		{ 1,-1,-1}};

	/**The Planes of a Tetrahedron	 */
	public static final
	int[][] PlanesTetrahedron ={
		{1,2,3},
		{1,3,4},
		{1,4,2},
		{4,3,2}};

	/**The Points of a Cube	 
	 * @see #Octahedron is the Fourier Transform; it has 
	 * 8 Points 
	 * 6 Planes 
	 */
	public static final
	int[][] PointsCube	=  {
		{ 1, 1, 1},
		{ 1,-1, 1},
		{ 1,-1,-1},
		{ 1, 1,-1},
		{-1, 1, 1},
		{-1,-1, 1},
		{-1,-1,-1},
		{-1, 1,-1}};

	/**The Planes of a Cube	 */
	public static final
	int[][] PlanesCube  =  {
		{1,2,3,4},
		{1,4,8,5},
		{1,5,6,2},
		{3,7,8,4},
		{2,6,7,3},
		{5,8,7,6}};
	
	/**The Points of an Octahedron	
	 * @see #Cube is the Fourier Transform; it has 
	 * 6 Points 
	 * 8 Planes 
	 */
	public static final
	double[][] PointsOctahedron={
		{ 1, 1, 0},
		{-1, 1, 0},
		{-1,-1, 0},
		{ 1,-1, 0},
		{ 0, 0, Sqrt2},
		{ 1, 1,-Sqrt2}};

	/**The Planes of an Octahedron	 */
	public static final
	int[][] PlanesOctahedron ={
		{1,2,5},
		{1,6,2},
		{2,3,5},
		{2,6,3},
		{3,4,5},
		{3,6,4},
		{4,1,5},
		{4,6,1}};

	/** The golden ratio, used to build the Dodecahedron and Icosahedron coordinates. */
	final static public double golden  = function.IMeasurAble.GOLDEN;
	/** The golden ratio minus one (its reciprocal), used alongside {@link #golden}. */
	final static public double golden1 = function.IMeasurAble.ONEGOLDEN;

	/**The Points of an Dodecahedron	
	 * @see #Ikosahedron is the Fourier Transform; it has 
	 * 20 Points 
	 * 12 Planes 
	 */
	public static final
	double[][] PointsDodecahedron ={{		1,		  1, -		1},
									{  golden,		  0, -golden1},
									{- golden,		  0, -golden1},
									{-		1,		  1, -		1},
									{		0,  golden1, -golden },
									{ golden1,  golden ,		0},
									{		1, -	  1, -		1},
									{-		1, -	  1, -		1},
									{-golden1,  golden ,		0},
									{		0,  golden1,  golden },
									{ golden1, -golden ,		0},
									{		0, -golden1, -golden },
									{-golden1, -golden ,		0},
									{-		1,		  1,		1},
									{		1,		  1,		1},
									{		1, -	  1,		1},
									{		0, -golden1,  golden },
									{-		1, -	  1,		1},
									{- golden,		  0,  golden1},
									{  golden,		  0,  golden1}};

	/**The Planes of a Dodecahedron	 */
	public static final
	int[][] PlanesDodecahedron ={{ 3,  8, 13,  9,  4},
								 {19, 14,  9, 13, 18},
								 { 2,  1,  6, 11,  7},
								 {20, 16, 11,  6, 15},
								 {19, 20, 15, 10, 14},
								 {20, 19, 18, 17, 16},
								 { 3,  2,  7, 12,  8},
								 { 2,  3,  4,  5,  1},
								 {13,  8, 12, 17, 18},
								 {11, 16, 17, 12,  7},
								 { 6,  1,  5, 10, 15},
								 { 9, 14, 10,  5,  4}};

	/**The Points of an Ikosahedron 
	 * @see #Dodecahedron is the Fourier Transform; it has 
	 * 12 Points 
	 * 20 Planes 
	 */
	public static final
	double[][] PointsIcosahedron ={{		0, +	  1, -golden1},
								   { +golden1, +	  0, -		1},
								   { +		1, +golden1, -		0},
								   { +		0, -	  1, -golden1},
								   { +golden1, -	  0, +		1},
								   { -		1, +golden1, +		0},
								   { -		0, +	  1, +golden1},
								   { -golden1, +	  0, -		1},
								   { +		1, -golden1, +		0},
								   { +		0, -	  1, +golden1},
								   { -golden1, +	  0, +		1},
								   { -		1, -golden1, -		0}};

	/**The Planes of an Icosahedron	 */
	public static final
	int[][] PlanesIcosahedron ={{ 1, 3, 2},
								{ 1, 2, 4},
								{ 1, 4, 8},
								{ 1, 8, 6},
								{ 1, 6, 3},
								{ 2, 3, 5},
								{ 2, 9, 4},
								{ 4,12, 8},
								{ 8,11, 6},
								{ 3, 6, 7},
								{ 2, 5, 9},
								{ 4, 9,12},
								{ 8,12,11},
								{ 6,11, 7},
								{ 3, 7, 5},
								{ 5,10, 9},
								{ 9,10,12},
								{12,10,11},
								{11,10, 7},
								{ 7,10, 5}};

	/**The Points of a Rauthedron	 */
	public static final
		int[][] PointsRauthedron ={{+0, +0, +2},
								   {+1, +1, +1},
								   {-1, +1, +1},
								   {-1, -1, +1},
								   {+1, -1, +1},
								   {+2, +0, +0},
								   {+0, +2, +0},
								   {-2, -0, +0},
								   {-0, -2, +0},
								   {+1, +1, -1},
								   {-1, +1, -1},
								   {-1, -1, -1},
								   {+1, -1, -1},
								   {-0, +0, -2}};

	/**The Planes of a Rauthedron	 */
	public static final
	int[][] PlanesRauthedron ={{ 1,  2,  7,  3},
							   { 1,  3,  8,  4},
							   { 1,  4,  9,  5},
							   { 1,  5,  6,  2},
							   { 6, 10,  7,  2},
							   { 7, 11,  8,  3},
							   { 8, 12,  9,  4},
							   { 9, 13,  6,  5},
							   {14, 11,  7, 10},
							   {14, 12,  8, 11},
							   {14, 13,  9, 12},
							   {14, 10,  6, 13}};

/*	final static public //doesn't work in the Initializer!!!
	float[] PointsRauthedron = new  float[](PointsRauthedron);

/*	public static final
	Body3D Rauthedron = new Body3D((float[][]) PointsRauthedron.a, PlanesRauthedron);
*/
	/**
     * Number of the Inverse (Fourier Transformed) Platonic Body
     */
	final static public int[] InverseBody = {0, 2, 1, 4, 3};

	/**
     * Names of the Platonic Body
     */
	final static public String[] NamesPlatonic = {
		"Tetrahedron", "Cube", "Octahedron", "Dodecahedron", "Icosahedron"};

	/**
     * Planes of the Platonic Bodies
     */
	final static public Object[][] PlanesPlatonic = {
		PlanesTetrahedron, PlanesCube, PlanesOctahedron, PlanesDodecahedron, PlanesIcosahedron};

	/**
     * Points of the Platonic Bodies
     */
	final static public Object[][] PointsPlatonic = {
		PointsTetrahedron, PointsCube, PointsOctahedron, PointsDodecahedron, PointsIcosahedron};
	
	private static final Log L = new Log(Platonic.class); 
	
	/**
	 * demonstrates how each Point the hexagonal System 
	 * has d*(d+1) nearest Neighbors 
	 * <!-- docstate
	 * tags: [code/platonic_solids, code/testing]
	 * concepts: [Tetrahedron Matrix Self-Test]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	final static public void testMatrixTetrahedron() {
		int counterDist1 = 0; 
		int counterTotal = 0; 
		final double[] v = new double[MatrixTetrahedron.length];  
		final double[] w = new double[MatrixTetrahedron.length];  
		for(v[2] = 2; --v[2] > -2;) {
			for(v[1] = 2; --v[1] > -2;) {
				for(v[0] = 2; --v[0] > -2;) {
					++counterTotal; 
					MatrixDouble.MAP(v, MatrixTetrahedron, w);
					final double normSqr = VectorDouble.NORM_SQR(w); 
					L.n(v).l(normSqr).l(w);
					if (Math.abs(normSqr - 4) < 1e-6) {
						L.n(v); 
						++counterDist1; 
					}
				}
			}
		}
		L.n().l(counterTotal); 
		Assert.EQUALS(12, counterDist1); 
	}
	
	/**The main entry point for the application; runs {@link #testMatrixTetrahedron()}.
	 * <!-- docstate
	 * tags: [code/platonic_solids, code/testing]
	 * concepts: [Demo Entry Point]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 * @param args Array of parameters passed to the application via the command line. */
	final static public void main(final String[] args) {
		testMatrixTetrahedron();
	}
}
