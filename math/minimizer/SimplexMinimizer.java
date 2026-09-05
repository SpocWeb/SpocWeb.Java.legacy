/*
 * File Name: SimplexMinimizer.java
 * Created on: 05.02.2004
 *
 */
package math.minimizer;

import math.matrix.MatrixDouble;
import streamIO.Assert;
import streamIO.Log;

/**
 * Solves linear programs by the simplex method (10.8): maximizes a linear objective
 * function over a bounded region defined by linear inequality and equality constraints.
 *
 * <p>Linear Optimization:
 * Optimizes a linear Objective Function
 * z = a[0]*x = a[0](1,2,...,N)*(x[1],x[2],...,x[N])
 * within a bounded HyperSimplex consisting of a convex Area,
 * formed by HyperPlanes described by the following Equations:
 * x[i] >=0; b[i] >=0;   (multiply Equation by -1, if b[i] < 0)
 *   
 * a[i]*x >= b[i] for i=1      ,...,m1
 * a[i]*x <= b[i] for i=1+m1   ,...,m1+m2
 * a[i]*x == b[i] for i=1+m1+m2,...,m1+m2+m3 = M
 * 
 * The total Number of Constraints M can be less than, equal to 
 * or even more than the Number of Variables N. 
 * Additionally the non-negativity Constraints form another N Inequations. 
 * 
 * Inequations carve away a Half-Space each, 
 * Equations restrict the Result to a HyperPlane. 
 * The Combination of these end up in a convex Area 
 * with at most the Dimensionality of the original Space. 
 * If the Equations contradict, the Area is empty and no Solution is possible.
 * 
 * Since the Objective Function is linear it always has the same 
 * nonzero Gradient and thus the Maximum cannot lie within the Area, 
 * but at it's Borders, typically even at it's Nodes/Corners. 
 * This allows to convert the Inequations into Equations 
 * resulting in an overdetermined System of N+M Equations for N Variables.
 *  
 * Thus the Solution is rather a combinatorical: 
 * Choose which N (out of the N+M) Equations to solve for  
 * to bound the optimal feasible Basic Vector.  
 * Rather than trying out every Combination here is a greedy Algorithm, 
 * that ends up in the maximum Solution Vector:  
 * 
 * Choosing any Point within the Simplex or at it's Boundary, 
 * you can always go "up" in the steepest Direction until you hit a border 
 * and you will invariably end up in the absolute Maximum, 
 * again because the Area is convex 
 * and because the Border always has 1 topological Dimension less than the Area, 
 * so you will end up in a single Point after no more than max(N, M) Steps.  
 * 
 * Example: 
 * Objective Function: z = 1*x1 + 1*x2 + 3*x3 - x4/2 
 * Constraints: N*Non-Negativity: x[i] >= 0 
 * 1*x1        + 2*x3        <= 740
 *        2*x2 - 7*x4        <=   0
 *        1*x2 - 1*x3 + 2*x4 >=   0.5
 * 1*x1 + 1*x2 + 1*x3 + 1*x4 ==   9
 * 
 * Conversion into Restricted Normal Form: 
 * The Problem can be canonicalized into "Restricted Normal Form", 
 * having only Equations and non-negativity Constraints x[i] >= 0 
 * AND additionally each Equation has (at least) one Variable 
 * that has a positive Coefficient (so-called Left Hand Variables) 
 * AND appears only in this Equation! 
 * 
 * The Inequations can be eliminated by introducing so-called "Slack Variables", 
 * y[i] which substitute the Inequations with their non-negativity. 
 * These Variables will be treated just like the others 
 * but ultimately they will be ignored from the Solution.
 * 
 * Normalized Example: y[i] >= 0
 * 1*x1        + 2*x3        + y1           == 740
 *        2*x2 - 7*x4             + y2      ==   0
 *        1*x2 - 1*x3 + 2*x4           - y3 ==   0.5
 * 1*x1 + 1*x2 + 1*x3 + 1*x4                ==   9
 * 
 * If you have only 
 * Now to come up with an initial Set of M Left-Hand Variables, 
 * instead of searching them, just introduce them as z[i] >= 0 
 * which have a positive Coefficient (of 1) 
 * and appear only once in each Equation.  
 * Now construct an initial feasible Vector (and then improve it)
 * for the following Substitute Objective Function
 * z' = -z1-z2-...-z[M]
 * which is obviously maximized for z[i] == 0
 * and consists only of right Hand Variables. 
 * 
 * The optimal Solution for the Substitute Objective Function 
 * is also a feasible Solution for the Original Objective Function, 
 * if z[i] == 0 for each i, since you can just throw out all z[i] Terms. 
 * 
 * If the optimal Solution does not give z[i] == 0 for each i,
 * this indicates that the Constraints are contradictory and cannot be solved! 
 * 
 * Restricted Normalized Example: 
 * y[i] >= 0 
 * z[i] >= 0
 * z1 == 740   - (1*x1        + 2*x3        + y1          ) 
 * z2 ==   0   - (       2*x2 - 7*x4             + y2     )
 * z3 ==   0.5 - (       1*x2 - 1*x3 + 2*x4           - y3) 
 * z4 ==   9   - (1*x1 + 1*x2 + 1*x3 + 1*x4               )
 * 
 * Solving z (or it's Substitute z') in Restricted Normal Form: 
 * 1) Construct an initial feasible Solution: 
 * Each Equation in Restricted Normal Form can easily be solved for a feasible Solution 
 * by just setting the right Hand Variables to 0 
 * and calculating the left Hand Variables from the b[i].
 * 
 * Example initial feasible Solution: 
 * x[i] = 0
 * y[i] = 0
 * z[i] = b[i] >= 0 (since b[i] >= 0) 
 * 
 * 2) iteratively improve the initial feasible Solution: 
 * This initial Solution can now be improved by selecting a right Hand Variable x[j] 
 * with a positive z-Coefficient z[0] > 0 to increase the Value of z 
 * until the constrained left Hand Variable will become negative. 
 * If all z-Coefficients are negative, the optimal Solution is found! 
 * For this, look at the negative Coefficients int the Column for the chosen right Hand Variable. 
 * If this Column does not have negative Coefficients, 
 * the Target Function is unlimited and can be increased by just increasing this Variable! 
 * The Increment Limit for x[j] in each Row r is given by dx[j]=-b[r]/a[r][j]
 * The Pivot Element now is the one with the maximum dx[j}. 
 * 
 * Now solve the Equation in Row r for x[j] and place it into this Row 
 * with x[j] as the new Left Hand Variable. 
 * Also substitute the Formula into all other Rows, including the Objective Function, 
 * resulting in x[r] being a Right Hand Variable, 
 * effectively exchanging both Variables.
 * 
 * Iterate this until either all z-Coefficients are negative (optimal Solution) 
 * or the Column has no negative Coefficients (unlimited Solutions). 
 * 
 * Example final Substitute Solution: 
 * 
 * 
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:48:12Z
 * digest: 5ecc21792202700fab2774b67d0ad00bccc11dab61cd9ea752a7fc8ba4fd83ab
 * stale: false
 * tags: [code/simplex_method, code/optimization]
 * concepts: [Linear Programming Simplex Tableau Solver]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public class SimplexMinimizer {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(SimplexMinimizer.class, 0);

	/**	linear programming, (10.8)
	 * determines the Maximum of those Row Elements whose Index is contained in the List l1
	 * either with or without taking the absolute Value
	 * @param aRow the Row to search in 
	 * @param index the Index to use 
	 * @param numIndex Items left in the Index
	 * @param useAbsV Flag to use absolute Values
	 * @param maxPos return Index of maximum Value
	 * @param maxVal return maximum Value
	 * @return Index of maximum Value
	 * <!-- docstate
	 * tags: [code/simplex_method]
	 * concepts: [Simplex Tableau Pivot Column Selection]
	 * facets: {layer: utility, status: legacy, complexity: low}
	 * -->
	 */
	private static final int maxPos(final double[] aRow, final int[] index, final int indexLen, final boolean useAbsV) {
		int maxPos=index[1];
		double maxVal = aRow[maxPos+1];
		if (useAbsV) {
			maxVal = Math.abs(maxVal); }
		for (int k=2; k<=indexLen; k++) {
			double test = aRow[index[k]+1];
			if (useAbsV) {
				test=Math.abs(test); }
			if (maxVal < test) {
				maxVal=test;
				maxPos=index[k];
			}
		}
		return maxPos;
	}
	
	static final double EPS = 1.0e-6;

	/**	linear programming, (10.8)
	 * locates a Pivot Element, taking Degeneracy into Account
	 * @param a
	 * @param n
	 * @param l2
	 * @param nl2
	 * @param maxPos
	 * @param ip return Value
	 * <!-- docstate
	 * tags: [code/simplex_method]
	 * concepts: [Simplex Tableau Pivot Row Selection]
	 * facets: {layer: utility, status: legacy, complexity: low}
	 * -->
	 */
	private static final int getPivot(final double[][] a, final int numCols
	, final int[] index2, final int index2Length, final int maxPos) {
		for (int j=1; j<=index2Length; j++) {
			if (a[index2[j]+1][maxPos+1] >= -EPS) {
				continue; } //no possible Pivot 
			return getPivot(a, numCols, index2, index2Length, maxPos, -a[index2[j]+1][1]/a[index2[j]+1][maxPos+1], j);
		}
		return 0;
	}

	/** @see #simp2(double[][], int, int[], int, int[], int, double[]) uses this Method exclusively 	 */
	private static int getPivot(final double[][] a, final int numCols
	, final int[] index2, final int index2Length, final int maxPos, double q1, final int j) {
		int iPivot = index2[j];
		for (int i= j+1;i<=index2Length;i++) {
			final int ii=index2[i];
			if (a[ii+1][maxPos+1] >= -EPS) {
				continue; }
			final double q = -a[ii+1][1]/a[ii+1][maxPos+1];
			if (q < q1) {
				iPivot=ii;
				q1=q;
			} else if (q == q1) { //Degeneracy encountered
				double qp = 0; 
				double q0 = 0;
				for (int k=1; k<=numCols; k++) {
					qp = -a[iPivot+1][k+1]/a[iPivot+1][maxPos+1];
					q0 = -a[ii+1][k+1]/a[ii+1][maxPos+1];
					if (q0 != qp) {
						break; } 
				}
				if (q0 < qp) {
					iPivot=ii; } 
			}
		}
		return iPivot; 
	}

	/** linear programming, (10.8)
	 * Matrix Operations to exchange a left Hand and a right Hand Variable
	 * @param a
	 * @param index1 
	 * @param k1
	 * @param iPivot
	 * @param maxPos
	 * <!-- docstate
	 * tags: [code/simplex_method]
	 * concepts: [Simplex Tableau Pivot Exchange]
	 * facets: {layer: utility, status: legacy, complexity: medium}
	 * -->
	 */
	private static final void exchangeVariable(final double[][] a, final int index1, final int k1, final int iPivot, final int maxPos) {
		final double pivot = 1/a[iPivot+1][maxPos+1];
		for (int i=1; i<=index1+1; i++) {
			if (i-1 == iPivot) {
				continue; }
			a[i][maxPos+1] *= pivot;
			for (int k=1; k<=k1+1; k++) {
				if (k-1 == maxPos) {
					continue; }
				a[i][k] -= a[iPivot+1][k]*a[i][maxPos+1]; 
			}
		}
		for (int k=1; k<=k1+1; k++) {
			if (k-1 == maxPos) {
				continue; } 
			a[iPivot+1][k] *= -pivot; 
		}
		a[iPivot+1][maxPos+1]=pivot;
	}

	/**	linear programming maximization of a linear function (10.8)
	 * 
	 * @param a in/out Matrix containing in Row 1 the Objective Function and in the following 
	 * numLess  Rows the '<=' Inequations 
	 * numMore  Rows the '>=' Inequations 
	 * numEqual Rows the '=='   Equations 
	 * @param numRows Number of Rows in a, must match numLess + numMore + numEqual 
	 * @param numCols Number of Columns in a
	 * @param indexLeftHandRows return the Indices i = indexLeftHandRows[j] of the Left  Hand Variables x[i], 
	 * now represented in Row j+1 with i > N indicating an y[i-N]
	 * @param indexRightHandCols return the Indices i = indexRightHandCols[j] of the Right Hand Variables x[i], 
	 * which are all Zero in the optimal Solution, 
	 * now represented in Column j+1 with i > N indicating an y[i-N]
	 * @return a Flag: 
	 * 	 0 if there is one or more optimal Solutions
	 *  -1 if there is no Solution due to conflicting Constraints
	 *  +1 if the Solutions are unbound
	 * <!-- docstate
	 * tags: [code/simplex_method]
	 * concepts: [Simplex Tableau Minimization Loop]
	 * facets: {layer: utility, status: legacy, complexity: high}
	 * -->
	 */
	final static public int minimizeSimplex(final double[][] a, final int numRows, final int numCols
	, final int numLess, final int numMore, final int numEqual, 
	final int[] indexRightHandCols, final int[] indexLeftHandRows) {
		//Initialization 
		if (numRows != (numLess+numMore+numEqual)) {
			throw new RuntimeException("Numbers of Input Constraints 't add up: m="+numRows+" != (numLess+numMore+numEqual)="+(numLess+numMore+numEqual)); } 
		int index1Length=numCols; //Initialize Number of Left-Hand Variables... 
		final int[] index1=new int[1+numCols+1];
		final int[] index2=new int[1+numRows];
		final int[] index3=new int[1+numRows];
		for (int k=1; k<=numCols; k++) { //and Index List of Columns admissible for Exchange 
			index1[k]=indexRightHandCols[k]=k; } 
		final int index2Length=numRows;
		for (int i=1;i<=numRows;i++) {
			if (a[i+1][1] < 0) { 
				throw new RuntimeException("Input tableau in minimizeSimplex is not in Normalized Form: a["+(i+1)+"][1]==b["+(i+1)+"]="+a[i+1][1]+" should not be negative!!!");} 
			index2[i]=i; //initial Left Hand Variables. numLess Type Constraints have their slack Variable left Hand with no artificial Variable. 
			indexLeftHandRows[i]=numCols+i; //m2 Type Constraints 
		} //m2 Type Constraints  have their artificial Variable left hand initially. 
		for (int i=1;i<=numMore;i++) {
			index3[i]=1; } 
			
		if (numMore+numEqual != 0) { //Origin is not a feasible Starting Point... Phase 1
			index1Length = findStartPoint(a, numRows, numCols, numLess, numMore, indexRightHandCols, indexLeftHandRows, index1, index2, index3, index1Length, index2Length); 
			if (index1Length < 0) {
				return -1; }
		}
		//Phase II, optimizing Solution
		for (;;) {
			final int maxPos = maxPos(a[1],index1,index1Length,false); //Test z-Row for Doneness
			final double maxVal=a[1][maxPos+1];
			if (maxVal <= 0) { //Done
				return 0; 
			}
			final int iPivot = getPivot(a,numCols,index2,index2Length,maxPos); //Locate Pivot Element
			if (iPivot == 0) { //Objective Function is unbounded
				return 1;
			}
			exchangeVariable(a,numRows,numCols,iPivot,maxPos); //Exchange left and right Hand Variable
			final int swap=indexRightHandCols[maxPos]; indexRightHandCols[maxPos]=indexLeftHandRows[iPivot]; indexLeftHandRows[iPivot]=swap;
		}
	}

	/** finds a Starting Point for the Simplex Algorithm if the Origin is not possible
	 * @see #simplx(double[][], int, int, int, int, int, int[], int[]) uses this Method exclusively 
	 * @return the Length of index1 or -1 if there is no Solution due to conflicting Constraints
	 * <!-- docstate
	 * tags: [code/simplex_method]
	 * concepts: [Simplex Feasible Start Point Search]
	 * facets: {layer: utility, status: legacy, complexity: high}
	 * -->
	 */
	private static final int findStartPoint(
		final double[][] a,
		final int numRows,
		final int numCols,
		final int numLess,
		final int numMore,
		final int[] indexRightHandCols,
		final int[] indexLeftHandRows,
		final int[] index1,
		final int[] index2,
		final int[] index3,
		int index1Length,
		final int index2Length) {
		//Initialize a List of m2 Constraints whose Slack Variables have never been exchanged
		for (int k=1; k<=(numCols+1); k++) {
			double sum=0; //Compute the auxiliary Objective Function
			for (int i=numLess+1;i<=numRows;i++) {
				sum += a[i+1][k];} 
			a[numRows+2][k] = -sum;
		}
		int iPivot = 0; 
		outer: for(;;) {  //still in Phase 1
			int maxPos; 
			inner: {
				maxPos = maxPos(a[numRows+2],index1,index1Length,false); //find max. Coeff. of aux. Objective Fn.
				double maxVal=a[numRows+2][maxPos+1];
				if ((maxVal <= EPS) && (a[numRows+2][1] < -EPS)) { //aux. Function still negative, cannot be improved
					return -1; //no feasible Solution; return immediately 
				} //
				if ((maxVal <= EPS) && (a[numRows+2][1] <= EPS)) { 
					maxPos = maxPos(a, numRows, numCols, numLess, numMore, indexLeftHandRows, index1, index3, index1Length, maxPos);
					if (maxPos < 0) {
						maxPos = -maxPos;
						break inner; 
					}
					break outer; //Jump out of the Phase 1 Loop and goto Phase 2
				}
				iPivot = getPivot(a,numCols,index2,index2Length,maxPos); //Locate a pivot Element
				if (iPivot == 0) { //unbounded Objective Function 
					return -1; //no feasible Solution, return immediately 
				}
			}
			exchangeVariable(a,numRows+1,numCols,iPivot,maxPos); 
			//exchange a left and a right Variable, then update Lists
			if (indexLeftHandRows[iPivot] >= (numCols+numLess+numMore+1)) { 
				int k; //Exchanging an artifical Variable ...
				for (k=1; k<=index1Length; k++) { //...for a '==' Constraint...
					if (index1[k] == maxPos) { 
						break; }
				} 
				--index1Length; //... and remove it from the l1 List. 
				for (int is=k; is<=index1Length; is++) {
					index1[is]=index1[is+1]; } 
				++a[numRows+2][maxPos+1]; 
				for (int i=1; i<=numRows+2; i++) {
					a[i][maxPos+1] = -a[i][maxPos+1]; } 
			} else { //exchange an m2 Type Constraint.
				if (indexLeftHandRows[iPivot] >= (numCols+numLess+1)) {
					final int kh=indexLeftHandRows[iPivot]-numLess-numCols;
					if (index3[kh] != 0) {
						index3[kh]  = 0;
						++a[numRows+2][maxPos+1]; //correct for implicit Variable
						MatrixDouble.NEG_COL_AT(a, maxPos+1, 1, numRows+3);
					}
				}
			}
			final int swap=indexRightHandCols[maxPos]; indexRightHandCols[maxPos]=indexLeftHandRows[iPivot]; indexLeftHandRows[iPivot]=swap;
		}
		return index1Length;
	}

	/** @see #findStartPoint(double[][], int, int, int, int, int[], int[], int[], int[], int[], int, int)
	 * uses this Method exclusively  
	 */
	private static int maxPos(
		final double[][] a,
		final int numRows,
		final int numCols,
		final int numLess,
		final int numMore,
		final int[] indexLeftHandRows,
		final int[] index1,
		final int[] index3,
		final int index1Length,
		int maxPos) {
		double maxVal;
		int m12=numLess+numMore+1; //aux. Fn. is Zero and cannot be improved
		if (m12 <= numRows) {
			for (int i=m12; i<=numRows; i++) {
				if (indexLeftHandRows[i] != (i+numCols)) { //found artificial Variable
					continue; } 
				maxPos = maxPos(a[i+1],index1,index1Length,true); //for an equality Constraint
				maxVal=a[i+1][maxPos+1];
				if (maxVal > 0) { //Exchange with Column of max. Pivot Element in row
					return -maxPos; } //Jump out of Search Loop
			}
		}
		--m12; //Change Sign of Row for any m2 Constraints still present. 
		if (numLess+1 <= m12) {
			for (int i=numLess+1;i<=m12;i++) {
				if (index3[i-numLess] != 1) {
					continue; }
				for (int k=1; k<=numCols+1; k++) {
					a[i+1][k] = -a[i+1][k]; } 
			}
		}
		return maxPos;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	/// Testing and Main Method
	/////////////////////////////////////////////////////////////////////////////////////

	/** Runs {@link #minimizeSimplex} against a fixed example tableau and asserts the known result.
	 *
	 * <!-- docstate
	 * tags: [code/simplex_method]
	 * concepts: [Self-Test Method]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	final static public void testIt() {
		final int N = 4;
		final int M1 = 2;        /* M1+M2+M3 = M */
		final int M2 = 1;
		final int M3 = 1;
		final int M = M1+M2+M3; 
		final int NM1M2 = N+M1+M2;

		final double c[][]= { {
			},{0,   0  , 1, 1, 3,-0.5,
			},{0, 740  ,-1, 0,-2, 0,
			},{0,   0,0,-2, 0, 7,
			},{0,   0.5, 0,-1, 1,-2,
			},{0,   9  ,-1,-1,-1,-1,
			},{0,   0  , 0, 0, 0, 0
			} };
			
		final double result[][]= { { 
			},{0,  17.025, -0.95, -0.05,  1.95, -1.05
			},{0, 730.55 ,  0.1 , -0.1 , -1.1 ,  0.9
			},{0,   3.325, -0.35, -0.15,  0.35,  0.35
			},{0,   0.95 , -0.1 ,  0.1 ,  0.1 ,  0.1
			},{0,   4.725, -0.55,  0.05,  0.55, -0.45
			},{0,   0    ,  0   ,  0   , -0   ,  0.0
			  }};
			
		final int[] indexRightHandCols=new int[1+N];
		final int[] indexLeftHandRows=new int[1+M];
		final double[][] a=MatrixDouble.COPY(c);
		final int icase = minimizeSimplex(a,M,N,M1,M2,M3,indexRightHandCols,indexLeftHandRows);
		if (icase == 1) {
			Assert.FAIL("unbounded objective function");
		} else if (icase == -1) {
			Assert.FAIL("no solutions satisfy constraints given");
		} else {
			printSimplexMaximum(N, M, NM1M2, indexRightHandCols, indexLeftHandRows, a);
			Assert.EQUALS(result, a);
		}
	}

	private static void printSimplexMaximum(
		final int N,
		final int M,
		final int NM1M2,
		final int[] indexRightHandCols,
		final int[] indexLeftHandRows,
		final double[][] a) {
		final String[] txt= {" ","x1","x2","x3","x4","y1","y2","y3"};
		L.n("\t");
		for (int i=1;i<=N;i++) {
			if (indexRightHandCols[i] <= NM1M2) {
				L.l(txt[indexRightHandCols[i]]); } 
		}
		L.n();
		MatrixDouble.STREAM(a);
		for (int i=1;i<=M+1;i++) {
			if (i == 1 || indexLeftHandRows[i-1] <= NM1M2) {
				if (i > 1)
					L.l(txt[indexLeftHandRows[i-1]]);
				L.l('\t');
				L.l(a[i][1]);
				for (int j=2;j<=N+1;j++)
					if (indexRightHandCols[j-1] <= NM1M2) {
						L.l(a[i][j]); } 
				L.n();
			}
		}
	}

	/** Main method to be called from the command line, running {@link #testIt()}.
	 *
	 * <!-- docstate
	 * tags: [code/simplex_method]
	 * concepts: [Demo Entry Point]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	final static public void main(final String[] args) {
		testIt();
	}

}
