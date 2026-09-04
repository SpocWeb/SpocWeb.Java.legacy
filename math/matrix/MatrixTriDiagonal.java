/*
 * File Name: MatrixDiag.java
 * Created on: 01.11.2003
 *
 */
package math.matrix;

import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;

import math.vector.VectorDouble;
import streamIO.Assert;
import streamIO.Log;

/**
 * Title: MatrixTriDiagonal<p>
 * Description:
 * Purpose:
 * Groups Methods to solve (Tri-, Band-) Diagonal Matrices.
 * By letting the next Diagonal Vectors always start from 0, 
 * Transposition is made very easy: just swap the Vectors around the Diagonal. 
 * 
 * It also detects and solves cyclic tridiagonal Equations, 
 * (when the Length of the non-diagonal Vectors is the same as the Diagonal 
 * AND the Values of these Elements are nonzero)
 * by applying the Sherman-Morrison Formula for small Matrix Modifications. 
 *
 * @see math.MatrixBand can be used for unstable tridiagonal Matrices, 
 * because it implements basic Pivoting!
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
 */
public class MatrixTriDiagonal {

	private static final Log L = new Log(MatrixTriDiagonal.class);
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Member Variables 
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** SubDiagonal, filled from 0..n-1	 */
	private final double[] subDiag;
	
	/**	Diagonal, filled from 0..n */
	private final double[] diag; 
	
	/**	SuperDiagonal, filled from 0..n-1 */
	private final double[] superDiag; 
	
	/**	SuperDiagonal, filled from 0..n-1 */
	private boolean _isCyclic; 
	
	private MatrixTriDiagonal nonCyclic; 
	
	private double[] u; 
	
	/** @return true when this Equation is cyclic	 */
	public boolean getCyclic() { return _isCyclic; } //subDiag.length == diag.length; 
	
	/**used to override possible cyclicity 
	 * @param cyclic Flag when this Equation is to be considered as cyclic	 */
	public void setCyclic(boolean cyclic_) {
		if (cyclic_) {
			if ((subDiag.length < diag.length) || (superDiag.length < diag.length)) {
				throw new ArrayIndexOutOfBoundsException("Cannot make cyclic: Non-Diagonals not large enough: subDiagonal:"+subDiag.length+" superDiagonal:"+superDiag.length);} 
		}
		_isCyclic = cyclic_; // 
	}
	
	/** 
	 * @param cyclic Flag when this Equation is to be considered as cyclic	 */
	public boolean isCyclic() { return _isCyclic; }
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Constructors 
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * initializing Constructor
	 * @param subDiag_
	 * @param diag_
	 * @param superDiag_
	 * @param cyclic_
	 */
	public MatrixTriDiagonal(final double[] subDiag_, final double[] diag_, final double[] superDiag_) {
		this.superDiag = superDiag_;
		this.subDiag = subDiag_;
		this.diag = diag_; 
		if ((subDiag.length != superDiag.length)) { // || (subDiag.length != diag.length-1)) {
			throw new ArrayIndexOutOfBoundsException("Dimensions of subDiag["+subDiag.length
			+ "], diag["+diag.length+"] and superDiag["+superDiag.length+"] don't match:");
		}
		_isCyclic = (subDiag.length == diag.length) 
		&& (subDiag[subDiag.length-1] != 0) 
		&& (superDiag[superDiag.length-1] != 0);
	}
	
	/**
	 * initializing Constructor
	 * @param subDiag_
	 * @param diag_
	 * @param superDiag_
	 */
/*	public MatrixTriDiagonal(final double[] subDiag_, final double[] diag_, final double[] superDiag_, final boolean cyclic_) {
		this(subDiag_, diag_, superDiag_);
		cyclic = cyclic_; 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Methods 
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * for tridiagonal Matrices this is just lower and upper Subdiagonal switched. 
	 * @return a new Matrix which holds the Transpose of this Matrix, but sharing it's Elements! 
	 */
	public MatrixTriDiagonal TRP() {
		return new MatrixTriDiagonal(superDiag, diag, subDiag); //, cyclic);
	}

	/**
	 * for tridiagonal Matrices this is just lower and upper Subdiagonal switched.
	 * The Design for this Class does not allow this (final Fields) 
	 * @return the Transpose of this Matrix in Place
	 */
/*	public void TRP_AT() {//Vectors are final...
		double[] tmp = superDiag; superDiag = subDiag; subDiag = tmp;
	}
*/	

	/**	solution of cyclic tridiagonal systems (2.7)
	 * The Corner Elements a[0][n] and a[n][0] 
	 * are the last Elements of the Next-Diagonal Elements. 
	 * This Method is not thread-safe due to shared and modified 'nonCyclic' Element.
	 * 
	 * @param alpha
	 * @param beta
	 * @param r
	 * @param x
	 */
	private synchronized void solveCyclicAt(final double[] solution, final double[] tmp)	{
		final int n = diag.length;
		
		final double alpha = superDiag[n-1];
		final double beta  = subDiag[n-1];
			
		if (nonCyclic == null) { //set up an auxiliary, non-cyclic tridiagonal System
			final double[] newDiag=VectorDouble.COPY(diag); //new double[n];
			u=new double[n];
			nonCyclic = new MatrixTriDiagonal(subDiag, newDiag, superDiag);
			//nonCyclic.setCyclic(false); 
		} else {
			VectorDouble.FILL_AT(u, 0, 1, n-1);
		}
	
		final double gamma = -diag[0]; //can be chosen arbitrarily...
		nonCyclic.diag[0]=diag[0]-gamma; //...so that Extinction happens neither here...
		nonCyclic.diag[n-1]=diag[n-1]-alpha*beta/gamma; //...nor here!
		
		nonCyclic.solveNonCyclicAt(solution, tmp); //A*x=r
		u[0]=gamma;
		u[n-1]=alpha;
		nonCyclic.solveNonCyclicAt(u,tmp); //A*z=u
		
		final double fact=(solution[0]+beta*solution[n-1]/gamma)/
			(1+u[0]+beta*u[n-1]/gamma); //v*x/(1+v*z)
		for (int i=0; i<n; i++) { //correction of x
			solution[i] -= fact*u[i];
		}
	}
	
	/** temporary Working Space for solving an Equation */
	//final double[] tmp; 
	
		
	/**	Solution of tridiagonal systems (2.4)
	 * 
	 * @param rightSide the right Vector to solve for...
	 * @param solution the Solution to the Equation 
	 */
	public double[] solve(final double[] rightSide) {
		final double[] solution = new double[diag.length];
		solve(rightSide, solution);
		return solution; 
	}

	/**	Solution of tridiagonal systems (2.4)
	 * 
	 * @param rightSide the right Vector to solve for...
	 * @param solution the Solution to the Equation 
	 */
	public void solve(final double[] rightSide, final double[] solution) {
		final double[] tmp = new double[diag.length]; 
		solve(rightSide, solution, tmp);
	}
	
	/**	Solution of tridiagonal systems (2.4)
	 * 
	 * @param rightSide the right Vector to solve for...
	 * @param solution the Solution to the Equation 
	 */
	public void solveAt(final double[] solution, final double[] tmp) {
		if (isCyclic()) { //make sure to use the faster way...
			solveCyclicAt(solution, tmp);
		} else {
			solveNonCyclicAt(solution, tmp);
		}
	}
	
	/**	Solution of tridiagonal systems (2.4)
	 * 
	 * @param rightSide the right Vector to solve for...
	 * @param solution the Solution to the Equation 
	 */
	private void solve(final double[] rightSide, final double[] solution, final double[] tmp) {
		VectorDouble.COPY(rightSide, solution); 
		solveAt(solution, tmp);
	}
	
	/**	Solution of tridiagonal systems (2.4)
	 * 
	 * @param rightSide the right Vector to solve for...
	 * @param solution the Solution to the Equation, since it cannot be resolved in place 
	 */
	private double[] solveNonCyclicAt(double[] solution, final double[] rightSide) {
		if (solution == null) 
			solution =  new double[rightSide.length];
		final int n = diag.length-1;
		double bet = diag[0];
		if (bet == 0) 
			throw new RuntimeException("1st Diagonal Element is Zero, rewrite as N-1 Problem");
		solution[0]/=bet;
		for (int j=1; j<=n; j++) {
			bet=diag[j]-subDiag[j-1]*(rightSide[j]=superDiag[j-1]/bet);
			if (bet == 0)
				throw new RuntimeException("Use MatrixBand to solve this Problem with Pivoting.");
			solution[j]=(solution[j]-subDiag[j-1]*solution[j-1])/bet;
		}
		for (int j=n-1; j>=0; j--) { // Backsubstition
			solution[j] -= rightSide[j+1]*solution[j+1];
		}
		return solution;
	}

	/** maps the given Vector with this tridiagonal Matrix
	 * 
	 * @param arg Vector to multiply with the tridiagonal Matrix
	 * @return a Vector containing the Result.
	 */
	public double[] map(final double[] arg) {
		final double[] ret = new double[diag.length];
		map(arg, ret);
		return ret; 
	}

	/** maps the given Vector with this tridiagonal Matrix.
	 * Also supports cyclic tridiagonal Matrices 
	 * with the Corner-Elements as the very last non-Diagonal Elements. 
	 * 
	 * @param arg Vector to multiply with the tridiagonal Matrix
	 * @param prod Array to receive the Result.
	 * @return prod
	 */
	public void map(final double[] arg, final double[] prod) {
		prod[0]=diag[0]*arg[0]+superDiag[0]*arg[1];
		final int last = diag.length-1;
		final boolean cyclic = isCyclic(); 
		if (cyclic) {// subDiag.length == diag.length){ //special Case for Corner Elements
			prod[0]+=subDiag[last]*arg[last];
		}
		for (int k=1; k<last; k++) {
			prod[k]=subDiag[k-1]*arg[k-1]+diag[k]*arg[k]+superDiag[k]*arg[k+1];
		}
		prod[last]=subDiag[last-1]*arg[last-1]+diag[last]*arg[last];
		if (cyclic){ //special Case for Corner Elements
			prod[last]+=superDiag[last]*arg[0];
		}
		//return prod;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////

	/**
	 * tests all Methods of this Class
	 * @param args Command Line Parameters 
	 * @throws IOException
	 */
	final static public void main(final String[] args) throws IOException {
		if (args.length == 0) 
			testIt(); 
		else {
			
		}
	}
	
	/**
	 * tests all Methods of this Class
	 * @param args Command Line Parameters 
	 * @throws IOException
	 */
	final static public void testIt() throws IOException {
		FileReader fr = new FileReader("./math/TriDiag.tab");
		StreamTokenizer st = new StreamTokenizer(fr); //Alternative to ResultSet
		st.eolIsSignificant(true); 
		for(; StreamTokenizer.TT_EOF != st.nextToken();) {
			final int n = (int) st.nval; VectorDouble.skipLine(st); 
			final double[] diag   = VectorDouble.readVector(st, n);
			final double[] superd = VectorDouble.readVector(st, n);
			final double[] subd   = VectorDouble.readVector(st, n);
			final double[] rhs    = VectorDouble.readVector(st, n);
			MatrixTriDiagonal matrix = new MatrixTriDiagonal(subd, diag, superd);
			matrix.setCyclic(false); 
			testTridiagonal(matrix, rhs);
			// use random Variables...
			VectorDouble.RANDOMIZE_AT(diag);
			VectorDouble.RANDOMIZE_AT(superd);
			VectorDouble.RANDOMIZE_AT(subd);
			testTridiagonal(matrix, rhs);
			matrix.setCyclic(true); 
			testTridiagonal(matrix, rhs);
		}
	}

	/**
	 * tests the Solution of the given tridiagonal System
	 * 
	 * @param matrix
	 * @param rhs Right Hand Side 
	 */
	private static void testTridiagonal(
		final MatrixTriDiagonal matrix,
		final double[] rhs) {
		//math.matrix.setCyclic(cyclic); 
		final double[] soln = matrix.solve(rhs);
		L.n("The solution vector is:\n");
		VectorDouble.STREAM(soln, System.out); 
		L.n();
		/* test solution */
		L.n("(math.matrix)*(sol'n vector) should be:\n");
		VectorDouble.STREAM(rhs, System.out); 
		L.n();
		L.n("actual result is:\n");
		final double[] prod = matrix.map(soln);
		VectorDouble.STREAM(prod, System.out); 
		Assert.EQUALS(prod, rhs);
		L.n();
		L.n("***********************************");
		L.n("press return for next problem:");
	}
	
}
