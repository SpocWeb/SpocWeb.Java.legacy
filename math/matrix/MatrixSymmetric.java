/*
 * File Name: MatrixSymmetric.java
 * Created on: 28.10.2003
 *
 */
package math.matrix;

import java.io.IOException;

import math.NumberFormatter;
import math.vector.VectorDouble;
import streamIO.Assert;
import streamIO.Log;
import function.IMeasurAble;
import function.byref.ByRefDouble;

/**
 * Groups static methods to solve linear equations and to calculate eigenvalues and
 * eigenvectors of symmetric matrices via Cholesky decomposition and Householder
 * tridiagonalization.
 * <p>
 * Symmetric matrices pose opportunity for fast algorithms and are extraordinarily stable,
 * even without or only minimal pivoting, at least when being positive definite. A matrix
 * with an offset (e.g. 1) poses no problem for eigenvalues and vectors, because the 0s in
 * the 0th row and column are resolved to an eigenvalue of 0 automatically.
 * Known SubClasses: <none> Known Uses:
 * <none> Copyright: Copyright (c) Matthias Heuer
 * <p>
 * Company: personal
 * <p>
 * Created on 10-26-2002, 12:47 PM
 * <p>
 * @author mheuer
 * @version 1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:46:40Z
 * digest: ec24a2c0026dce2eeb778a1b3eb68550eb616044c801c5cbd72bbb35fd70e262
 * stale: false
 * tags: [code/eigenvalue_decomposition, code/tridiagonal_matrix_solving]
 * concepts: [Symmetric Matrix Eigen-Decomposition (Cholesky/Householder)]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public class MatrixSymmetric {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(MatrixSymmetric.class, 1);

	// ///////////////////////////////////////////////////////////////////////////////////

	/**
	 * Cholesky decomposition: A=L*L^t O(N^3/6), so about twice as fast as LU
	 * Decomposition.
	 * @param a symmetric Matrix, only the upper right Part is not being modified, the
	 *            lower right Part is replaced by most of the Decomposition L, except for
	 *            the Diagonal.
	 * @param diag stores the Diagonal of the Cholesky Decomposition.
	 * @return true if the Matrix is positive definite and thus could be decomposed.
	 * <!-- docstate
	 * tags: [code/lu_decomposition]
	 * concepts: [Cholesky Decomposition]
	 * facets: {layer: utility, status: legacy, complexity: medium}
	 * -->
	 */
	final static public boolean DECOMPOSE(final double[][] a, final double[] diag) {
		for (int i = 0; i < a.length; i++) {
			for (int j = i; j < a.length; j++) {
				double sum = a[i][j];
				for (int k = i; --k >= 0;) {
					sum -= a[i][k] * a[j][k];
				}
				if (i == j) {
					if (sum <= 0) { return false; }
					// throw new RuntimeException("not positive definite!");
					diag[i] = Math.sqrt(sum);
				} else a[j][i] = sum / diag[i];
			}
		}
		return true;
	}

	/**
	 * Cholesky decomposition: A=L*L^t
	 * @param decomposed, symmetric Matrix, the lower right Part is replaced by most of
	 *            the Decomposition L
	 * @param diag stores the Diagonal of the Cholesky Decomposition.
	 * @return the Inverse of the decomposed Matrix: L^-1
	 * <!-- docstate
	 * tags: [code/matrix_algebra]
	 * concepts: [Symmetric Matrix Inversion via Cholesky]
	 * facets: {layer: utility, status: legacy, complexity: medium}
	 * -->
	 */
	final static public boolean INVERSE(final double[][] a, final double[] diag) {
		for (int i = 0; i < a.length; i++) {
			a[i][i] = 1 / diag[i];
			for (int j = i; ++j < a.length;) {
				double sum = 0;
				for (int k = i; k < j; k++) {
					sum -= a[j][k] * a[k][i];
				}
				a[j][i] = sum / diag[j];
			}
		}
		return true;
	}

	/**
	 * Cholesky backsubstitution to solve A*x=A*L*L^t*x=b
	 * @param a decomposed symmetric Matrix
	 * @param diag stores the Diagonal of the Cholesky Decomposition.
	 * @param b right Side of the Equation
	 * @param x Solution of the Equation
	 * <!-- docstate
	 * tags: [code/matrix_algebra]
	 * concepts: [Cholesky-Based Linear Solve]
	 * facets: {layer: utility, status: legacy, complexity: medium}
	 * -->
	 */
	final static public void SOLVE(final double[][] a, final double[] diag,
			final double[] b, final double[] x) {
		VectorDouble.COPY(b, x);
		SOLVE_AT(a, diag, x);
	}

	/**
	 * Cholesky backsubstitution in Place to solve A*x=A*L*L^t*x=b
	 * @param a decomposed symmetric Matrix
	 * @param diag stores the Diagonal of the Cholesky Decomposition.
	 * @param b right Side of the Equation, replaced by the Solution of the Equation
	 * <!-- docstate
	 * tags: [code/matrix_algebra]
	 * concepts: [Cholesky-Based Linear Solve (In-Place)]
	 * facets: {layer: utility, status: legacy, complexity: medium}
	 * -->
	 */
	final static public void SOLVE_AT(final double[][] a, final double[] diag,
			final double[] b) {
		for (int i = 0; i < a.length; i++) {
			double sum = b[i];
			for (int k = i; --k >= 0;) {
				sum -= a[i][k] * b[k];
			}
			b[i] = sum / diag[i];
		}
		for (int i = a.length; --i >= 0;) {
			double sum = b[i];
			for (int k = i; ++k < a.length;) {
				sum -= a[k][i] * b[k];
			}
			b[i] = sum / diag[i];
		}
	}

	/**
	 * Eigensolution of a symmetric tridiagonal math.matrix (11.3) QL Algorithm with
	 * implicit Shifts to determine the Eigenvalues and Eigenvectors of a real, symmetric,
	 * tridiagonal Matrix. The Matrix A is stepwise decomposed into an orthogonal Matrix Q
	 * and a lower triangular Matrix L: A[m]=Q[m]*L[m] A[m+1]=L[m]*Q[m]=(Q[m]^t*A[m])*Q[m]
	 * A-k*1 has Eigenvalues e[i]-k which can be used to speed up Convergence.
	 * A[m+1]-k*1=L[m]*Q[m] <=> A[m+1]=L[m]*Q[m]+k*1=(Q[m]^t*A[m])*Q[m] For Decomposition
	 * can be used either a Householder or a Rotation Matrix. The latter is more efficient
	 * for tridiagonal Matrices.
	 * @param diag Diagonal of tridiagonal Matrix, replaced by Eigenvalues
	 * @param subDiag Subdiagonal of tridiagonal Matrix, destroyed
	 * @param trafo Transformation Matrix of TRI_DIALONALIZE or Identity Matrix for a
	 *            simple tridiagonal Matrix, null if Eigenvectors are not wanted. The
	 *            Columns will contain the Eigenvectors corresponding to d.
	 * <!-- docstate
	 * tags: [code/eigenvalue_decomposition]
	 * concepts: [Tridiagonal QL Eigenvalue Extraction]
	 * facets: {layer: utility, status: legacy, complexity: high}
	 * -->
	 */
	final static public void EIGENVALUES(final double[] diag, final double[] subDiag,
			final double[][] trafo) {
		final int n = diag.length - 1;

		// renumber Subdiagonal for Convenience
		VectorDouble.SHL_AT(subDiag, subDiag.length, 1);

		for (int l = 0; l <= n; l++) { // for each Eigenvalue...
			int m;
			int iter = 0;
			for (;;) { // while (m != l);
				if (++iter >= 30) { throw new RuntimeException(
						"Too many iterations on calculating the EigenValues:" + iter); }

				for (m = l; m <= n - 1; m++) { // Look for a small Subdiagonal
					// Elements...
					double dd = Math.abs(diag[m]) + Math.abs(diag[m + 1]);
					if ((Math.abs(subDiag[m]) + dd) == dd) break; // smaller than
					// Machine Accuracy
				} // ...to split the Matrix by

				if (m == l) {
					break;
				}
				double g = (diag[l + 1] - diag[l]) / (2 * subDiag[l]); // Form Shift
				double r = ByRefDouble.NORM(g, 1);
				g = diag[m] - diag[l] + subDiag[l] / (g + ByRefDouble.ASSIGN_SIGN(r, g)); // d[m]-ks
				double sin = 1;
				double cos = 1;
				double p = 0;
				int i;
				for (i = m - 1; i >= l; i--) { // A plane QL Roation...
					final double f = sin * subDiag[i];
					final double b = cos * subDiag[i];
					subDiag[i + 1] = (r = ByRefDouble.NORM(f, g));
					if (r == 0) { // Recover from Underflow
						diag[i + 1] -= p;
						subDiag[m] = 0;
						break;
					}
					sin = f / r;
					cos = g / r;
					g = diag[i + 1] - p;
					r = (diag[i] - g) * sin + 2 * cos * b;
					diag[i + 1] = g + (p = sin * r);
					g = cos * r - b;
					if (trafo != null) {
						rotateTrafo(trafo, n, sin, cos, i);
					}
				}
				if (r == 0 && i >= l) continue;
				diag[l] -= p; // set the eigenvalue
				subDiag[l] = g;
				subDiag[m] = 0;
			}
		}
	}

	private static void rotateTrafo(final double[][] trafo, final int n, double sin,
			double cos, int i) {
		for (int k = 0; k <= n; k++) { // ... followed by Rotations...
			final double[] tk = trafo[k];
			final double tki0 = tk[i]; // ...to restore tridiagonal Form.
			final double tki1 = tk[i + 1]; // ...to restore tridiagonal Form.
			tk[i + 1] = sin * tki0 + cos * tki1;
			tk[i] = cos * tki0 - sin * tki1;
		}
	}

	/**
	 * Householder reduction of a real, symmetric math.matrix to calculate Eigenvectors.
	 * @param n
	 * @param diag The Diagonal of the tridiagonal symmetric Matrix
	 * @param subDiag The off-Diagonal Elements of the tridiagonal symmetric Matrix
	 * @param a the Matrix to compute Eigenvectors from; modified in Place to contain the
	 *            Transformation Matrix Q, so that the EigenVectors can be computed in the
	 *            original Coordinate System.
	 * <!-- docstate
	 * tags: [code/tridiagonal_matrix_solving]
	 * concepts: [Householder Tridiagonalization]
	 * facets: {layer: utility, status: legacy, complexity: high}
	 * -->
	 */
	final static public void TRI_DIAGONALIZE(final double[][] a, final double[] diag,
			final double[] subDiag, final boolean calcTrafo) {
		final int n = a.length - 1;
		// double scale,hh,h,g,f;

		for (int i = n; i >= 1; i--) { // for each Dimension...
			final int i_1 = i - 1;
			final double[] ai = a[i];
			double h = 0;
			if (i == 1) {
				subDiag[i] = ai[i_1];
			} else {
				double scale = 0;
				for (int k = 0; k < i; k++)
					scale += Math.abs(ai[k]);
				if (scale == 0) { // skip Transformation
					subDiag[i] = ai[i_1];
				} else {
					for (int k = 0; k < i; k++) { // use scaled a[i][k] for
						// Transformation
						h += ByRefDouble.SQR(ai[k] /= scale); // Form sigma in h
					}
					final double aii_1 = ai[i_1];
					final double g = (aii_1 >= 0 ? -Math.sqrt(h) : Math.sqrt(h));
					subDiag[i] = scale * g;
					h -= aii_1 * g; // see Equation 11.2.4
					ai[i_1] = aii_1 - g; // store u in a[i] Row
					reduceHouseholder(a, subDiag, calcTrafo, i, ai, h);
				}
			}
			diag[i] = h;
		}
		diag[0] = 0;
		subDiag[0] = 0;

		for (int i = 0; i <= n; i++) {
			final double[] ai = a[i];
			diag[i] = ai[i];
			if (calcTrafo) { // store u/H in i-th Column of a
				if (diag[i] != 0) {
					orthogonalize(a, i, ai);
				} else {
					L.n();
				}
				ai[i] = 1;
				for (int j = 0; j < i; j++) {
					a[j][i] = ai[j] = 0;
				}
			}
		}
	}

	/**
	 * @see #TRI_DIAGONALIZE(double[][], double[], double[], boolean) uses this Method
	 *      exclusively
	 */
	private static final void reduceHouseholder(final double[][] a,
			final double[] subDiag, final boolean calcTrafo, final int i,
			final double[] ai, final double h) {
		double accum = 0;
		for (int j = 0; j < i; j++) {
			final double[] aj = a[j];
			if (calcTrafo) { // store u/H in i-th Column of a
				aj[i] = ai[j] / h;
			}
			double sum = 0; // Form an Element of A*u in g
			for (int k = 0; k <= j; k++) {
				sum += aj[k] * ai[k];
			} // leave off the Diagonal
			for (int k = j + 1; k < i; k++) {
				sum += a[k][j] * ai[k];
			}
			subDiag[j] = sum / h; // Form Element of p in temp. unused Element of e
			accum += subDiag[j] * ai[j];
		}
		final double hh = accum / (h + h); // Form K, see 11.2.11
		for (int j = 0; j < i; j++) { // Form q and store in e overwriting temp. p
			final double[] aj = a[j];
			final double aij = ai[j];
			final double tmp = subDiag[j] = subDiag[j] - hh * aij;
			for (int k = 0; k <= j; k++)
				aj[k] -= (aij * subDiag[k] + tmp * ai[k]);
		}
	}

	/** Subtracts from every column of {@code a} its projection onto {@code v}, in place.
	 * @see #TRI_DIAGONALIZE(double[][], double[], double[], boolean) uses this Method
	 *      exclusively
	 * <!-- docstate
	 * tags: [code/matrix_algebra]
	 * concepts: [Gram-Schmidt Orthogonalization]
	 * facets: {layer: utility, status: legacy, complexity: medium}
	 * -->
	 */
	final static public void orthogonalize(final double[][] a, final int length,
			final double[] v) {
		for (int j = 0; j < length; j++) {
			double sum = 0;
			for (int k = 0; k < length; k++)
				sum += v[k] * a[k][j];
			for (int k = 0; k < length; k++)
				a[k][j] -= sum * a[k][length];
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////

	/** Sample symmetric Matrix to test Eigenvalues */
	private static final double[] TEST_EIGENVALUES = {0.5917220656841293,
			0.5149329749426654, 0.6533636591582951, 0.35457361935985965,
			0.9647851456069693, 1.069803292496755, 2.5442767774998156,
			2.4073129777019315, 20.429245488025927, 20.56998399952364};

	/** Sample symmetric Matrix to test Eigenvalues */
	private static final double[][] TEST_MATRIX = {
			{5.0, 4.3, 3.0, 2.0, 1.0, 0.0, -1.0, -2.0, -3.0, -4.0,},
			{4.3, 5.1, 4.0, 3.0, 2.0, 1.0, 0.0, -1.0, -2.0, -3.0,},
			{3.0, 4.0, 5.0, 4.0, 3.0, 2.0, 1.0, 0.0, -1.0, -2.0,},
			{2.0, 3.0, 4.0, 5.0, 4.0, 3.0, 2.0, 1.0, 0.0, -1.0,},
			{1.0, 2.0, 3.0, 4.0, 5.0, 4.0, 3.0, 2.0, 1.0, 0.0,},
			{0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 4.0, 3.0, 2.0, 1.0,},
			{-1.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 4.0, 3.0, 2.0,},
			{-2.0, -1.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 4.0, 3.0,},
			{-3.0, -2.0, -1.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 4.0,},
			{-4.0, -3.0, -2.0, -1.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0}};

	/** Threshold below which a coordinate is treated as zero to avoid division by it in tests. */
	final static public double TINY = 1e-6;

	/** Formatter used by the self-tests to print matrices and vectors with 2 integer and 2 fraction digits. */
	final static public NumberFormatter FORMATTER = new NumberFormatter(2, 2);

	/**
	 * This Method calculates all EigenVectors and EigenValues and compares the Coordinate
	 * Ratios of Eigenvector and Mapping with the EigenValue. It also demonstrates the
	 * Ability to handle Index Offsets (here by 1) resulting in Rows and Columns
	 * consisting of 0s resulting in an EigenValue of 0.
	 * <!-- docstate
	 * tags: [code/testing, code/eigenvalue_decomposition]
	 * concepts: [Eigenvector Self-Test]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	private static final void testEigenVectors() throws IOException {
		final int NP=TEST_MATRIX.length;

		final double[] d=new double[NP];
		final double[] e=new double[NP];
		final double[][] a= MatrixDouble.COPY(TEST_MATRIX); 
		TRI_DIAGONALIZE(a, d, e, true);
		EIGENVALUES(d, e, a);
		L.n("\nEigenvectors for a real symmetric math.matrix");
		// regular Matrix Product...
		final double[][] result = MatrixDouble.CAT(TEST_MATRIX, a);
		for (int i=0;i<NP;i++) { 
			L.n("\neigenvalue["+i+"]="+d[i]);
			L.n("\nvector mtrx*vect. ratio");
			for (int j=0;j<NP;j++) {
				compareCoords(result[j][i], a[j][i], d[i], 0.06);
			}
		}
	}

	/**
	 * Compares the Ratio c1/c2 with the given ratio
	 * @param c1
	 * @param c2
	 * @param ratio expected Ratio
	 * <!-- docstate
	 * tags: [code/testing]
	 * concepts: [Coordinate Comparison Test Helper]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	final static public void compareCoords(final double c1, final double c2,
			final double ratio, final double acc) throws IOException {
		Assert.EQUALS(ratio * c2, c1, acc);
		L.n();
		FORMATTER.stream(System.out, c1);
		FORMATTER.stream(System.out, c2);
		if (Math.abs(c1) < TINY)
			L.n("\tdiv. by 0");
		else FORMATTER.stream(System.out, c1 / c2);
	}

	/**
	 * Offsets the given Matrix by the given Number of Rows and Columns
	 * @param source Matrix to shift right and down
	 * @param minRowIndex # of Rows to shift down
	 * @param minColIndex # of Columns to shift right
	 * @return a new Matrix filled with the Contents of source
	 * <!-- docstate
	 * tags: [code/matrix_algebra]
	 * concepts: [Diagonal Offset Utility]
	 * facets: {layer: utility, status: legacy, complexity: low}
	 * -->
	 */
	final static public double[][] OFFSET_MATRIX(final double[][] source,
			final int minRowIndex, final int minColIndex) {
		final int NP = source.length;
		final double[][] ret = new double[minRowIndex + NP][minColIndex + NP];
		for (int i = 0; i < NP; i++) {
			final double[] ri = ret[i + minRowIndex];
			final double[] si = source[i];
			for (int j = 0; j < NP; j++)
				ri[j + minColIndex] = si[j];
		}
		return ret;
	}

	/**
	 * This Method calculates all EigenVectors and EigenValues and compares the Coordinate
	 * Ratios of Eigenvector and Mapping with the EigenValue. It also demonstrates the
	 * Ability to handle Index Offsets (here by 1) resulting in Rows and Columns
	 * consisting of 0s resulting in an EigenValue of 0.
	 * @throws IOException
	 * <!-- docstate
	 * tags: [code/testing, code/eigenvalue_decomposition]
	 * concepts: [Offset Eigenvector Self-Test]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	private static final void testEigenVectorsOffset() throws IOException {
		final int NP = TEST_MATRIX.length;

		final double[] d = new double[NP + 1];
		final double[] e = new double[NP + 1];
		final double[] f = new double[NP + 1];
		final double[][] a = OFFSET_MATRIX(TEST_MATRIX, 1, 1);
		TRI_DIAGONALIZE(a, d, e, true);
		EIGENVALUES(d, e, a);
		L.n("\nEigenvectors for a real symmetric math.matrix");
		for (int i = 0; i <= NP; i++) {
			for (int j = 1; j <= NP; j++) {
				f[j] = 0.0;
				for (int k = 1; k <= NP; k++)
					f[j] += (TEST_MATRIX[j - 1][k - 1] * a[k][i]);
			}
			L.n("eigenvalue[" + i + "]=" + d[i]);
			L.n("vector mtrx*vect. ratio");
			for (int j = 0; j <= NP; j++) {
				compareCoords(f[j], a[j][i], d[i], IMeasurAble.DOUBLE_ACCURACY);
			}
		}
	}

	private static final void testEigenValues() throws IOException {
		final int NP = TEST_MATRIX.length;
		double[][] a = MatrixDouble.COPY(TEST_MATRIX);
		final double[] d = new double[NP];
		final double[] e = new double[NP];
		TRI_DIAGONALIZE(a, d, e, false);
		EIGENVALUES(d, e, null);
		L.n("\nEigenvalues for a real symmetric math.matrix");
		VectorDouble.STREAM(d, System.out, FORMATTER, ", ");
		Assert.EQUALS(TEST_EIGENVALUES, d);
	}

	/**
	 * tests Transformation of a symmetric into a tri-diagonal Matrix for faster
	 * Calculation of EigenValues
	 * <!-- docstate
	 * tags: [code/testing, code/tridiagonal_matrix_solving]
	 * concepts: [Tridiagonalization Self-Test]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	private static final void testTriDiagonalize() throws IOException {
		final int NP = TEST_MATRIX.length;
		final double[] d = new double[NP];
		final double[] e = new double[NP];
		final double[][] a = MatrixDouble.COPY(TEST_MATRIX);
		TRI_DIAGONALIZE(a, d, e, true);
		L.n("\ndiagonal elements");
		VectorDouble.STREAM(d, System.out, FORMATTER, ", ");
		L.n("\noff-diagonal elements");
		VectorDouble.STREAM(e, System.out, FORMATTER, ", ");
		L.n("\nTrafo Matrix");
		MatrixDouble.STREAM(a, System.out, FORMATTER, ", ");
		/*
		 * Check transformation math.matrix by multiplying it with the original Matrix
		 */
		// A^t*M*A Instead of 2*n^3 Mul/Add you need n^4 here, which is bad!
		final double[][] f = new double[NP][NP];
		for (int j = 0; j < NP; j++) { // n
			for (int k = 0; k < NP; k++) { // n
				double sum = 0;
				for (int l = 0; l < NP; l++) { // n
					final double[] tl = TEST_MATRIX[l];
					double sum1 = 0;
					for (int m = 0; m < NP; m++)
						// n
						sum1 += tl[m] * a[m][k];
					sum += sum1 * a[l][j];
				}
				f[j][k] = sum;
				if (Math.abs(j - k) > 1) { // check if non-diagonal Elements are zero
					Assert.IS_TRUE(Math.abs(sum) < 1e-14);
				}
			}
		}
		/* How does it look? */
		L.n("\ntridiagonal math.matrix");
		MatrixDouble.STREAM(f, System.out, FORMATTER, ", ");
	}

	/** tests both Cholesky Decomposition and Backsubstition
	 *
	 * <!-- docstate
	 * tags: [code/testing, code/lu_decomposition]
	 * concepts: [Cholesky Solve Self-Test]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	private static final void testSolveCholesky() {
		final int N = 3;
		double sum;
		double[][] a, atest, chol;
		double[] p, x;
		final double aorig[][] = {{100.0, 15.0, 0.01}, {15.0, 2.3, 0.01}, {0.01, 0.01, 1}};
		final double[] b = {0.4, 0.02, 99.0};

		a = new double[N][N];
		atest = new double[N][N];
		chol = new double[N][N];
		p = new double[N];
		x = new double[N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				a[i][j] = aorig[i][j];
			}
		}
		DECOMPOSE(a, p);
		L.n("Original math.matrix:\n");
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				chol[i][j] = ((i > j) ? a[i][j] : (i == j ? p[i] : 0.0));
				if (i > j)
					chol[i][j] = a[i][j];
				else chol[i][j] = (i == j ? p[i] : 0.0);
				L.l(aorig[i][j]);
			}
			L.n();
		}
		L.n("\n");
		L.n("Product of Cholesky factors:\n");
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				sum = 0;
				for (int k = 0; k < N; k++) {
					sum += chol[i][k] * chol[j][k];
				}
				atest[i][j] = sum;
				L.l(atest[i][j]);
			}
			L.n();
		}
		Assert.EQUALS(aorig, atest);
		L.n("Check solution vector:\n");
		SOLVE(a, p, b, x);
		for (int i = 0; i < N; i++) {
			sum = 0;
			for (int j = 0; j < N; j++) {
				sum += aorig[i][j] * x[j];
			}
			p[i] = sum;
			L.n().l(p[i]).l(b[i]);
		}
		Assert.EQUALS(b, p);
	}

	/**
	 * tests all Methods of this Class
	 * @param args
	 * <!-- docstate
	 * tags: [code/testing]
	 * concepts: [Demo Entry Point]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	final static public void main(final String[] args) throws IOException {
		testTriDiagonalize();
		testEigenValues();
		testEigenVectorsOffset();
		testEigenVectors();
		testSolveCholesky();
	}

}
