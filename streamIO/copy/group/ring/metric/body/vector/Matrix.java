package streamIO.copy.group.ring.metric.body.vector;

//import Functions.IFunction;
//import Stream.Copy.Monoid.Monoid;
//import Stream.Copy.GroupM.SemiGroupM;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.metric.AMetricIRing;
import streamIO.copy.group.ring.metric.IMetricIRing;
import streamIO.copy.group.ring.metric.body.BodyDouble;
import streamIO.copy.groupM.IGroupM;

/** This class defines all Methods specific to Matrices.
  * This helps to logically structure the Functionality compared to Tensor and Manifold
  * A Matrix is a Tensor of 2nd Degree
  * It contains Rows and Columns like a Relation
  * it is used for two Purposes:
  * -linear Mapping of Tensors
  * -bilinear Form for Mapping Vectors to Scalars
  */
public class Matrix
extends Tensor // AMatrix {
implements IMatrix {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Reference to the inverse Matrix */
	protected Matrix inverse;

	/**
	 * @see math.AMatrix which defines the same Variables...
	 */

	/** Contains the Permutation due to Pivoting.	 */
	protected int[] rows;

	/** Contains the Sign of the Permutation due to Pivoting.	 */
	protected boolean sign;

	/** Returns the Permutation due to Pivoting.	 */
	public int[] getRows() {
		if (rows == null) return null;
		int[] tmp = new int[rows.length];
		System.arraycopy(rows, 0, tmp, 0, rows.length);
		return tmp;	}

	/** Returns the Sign of the Permutation due to Pivoting.	 */
	public boolean getSign() { return sign; }

	/** Contains the Sign of the Permutation due to Pivoting.	 */
	//protected boolean decomposedLU;

	/** Contains the LU Decomposition.	 */
	public boolean isDecomposedLU() { return rows != null; } //decomposedLU; }

	//////////////////////////
	//	Replication GroupM:	//
	//////////////////////////

	/**Setting to 1 (Unity Matrix):	 */
	public IGroupM oneAt() {
		if (a[0] instanceof ITensor)
			 return diagAt(null);
		else return super.oneAt(); }


	/**Setting to 1 (Unity Matrix):	 */
/*	public GroupM one() {
		if (a[0] instanceof ITensor)
			 return new Tensor(mDim, ((Tensor)a[0]).a[0]).diagAt(null);
		else return new Tensor(a[0], mDim).mOneAt(); }
*/
	/** Empty Constructor	 */
	public Matrix(IIntRing element, int Grad){
		super(element, Grad);
	}

	/**Setting to a diagonal Matrix in Place:
	 * makes only sense for Matrices, not Vectors!
	 * If diag is null, the Unity Matrix is returned.	 */
	public Matrix diag(ITensor diag_) {
		Tensor diag = (Tensor) diag_;
		return ((Matrix) new Matrix(diag.a[0], diag.mDim)).diagAt(diag);}

	/**Determines the maximum Degree of the given Dimension
	 * As a preparation for Transposition. 	 */
	public int MaxGrad(int Dim) {
		if (--Dim == 0) return mDim;
		int maxGrad = ((Matrix)a[0]).MaxGrad(Dim);
		for (int  i = -1; ++i <= mDim;)
			maxGrad = Math.max(maxGrad, ((Matrix)a[i]).MaxGrad(Dim));
		return maxGrad;
	}

	/**Setting to a diagonal Matrix in Place:
	 * makes only sense for Matrices, not Vectors!
	 * If diag is null, the Unity Matrix is returned.	 */
	public Matrix diagAt(ITensor diag_) {
		Tensor diag = (Tensor) diag_;
		if (diag != null) {
			if (Carry == null) Carry = diag.a[0]; //.newInstance();
			setDim(diag.mDim, true, false); }
		if (! (a[0] instanceof ITensor)) { //if this is only a Vector
			if (diag == null) {	super.oneAt(); }
			else { copyAt(diag); }
			return this; }
		for (int i = -1; ++i <= mDim;) {
			Tensor Row = (Tensor) a[i];
			Row.mDim = -1;
			Row.setDim(i, true, true);
			if (diag == null)Row.a[i].oneAt();
			else			 Row.a[i].copyAt(diag.a[i]); }
		return this; }

	/**Makes these Row- Vectors othogonal
	 * and can bring their (euklidean) Length to 1.
	 * Makes only Sense for Matrices */
	public boolean isOne() { 	//Assume a square Matrix
		if (a[0] instanceof Tensor) { 	//real Matrix
			for (int i = -1; ++i <= mDim;) {
				Tensor iRow = (Tensor)a[i];
				for (int j = -1; ++j <= iRow.mDim;)
					if (j == i)	if (! iRow.a[j].isOne()) return false; else;	//Use an Epsilon here
					else		if (! iRow.a[j].isZero())return false;	//Use an Epsilon here
			}
			return true;
		}
		else return a[0].isOne();	//Scalar or Vector of Length 0
	}

	/** Re-Composition of LU decomposition	in Place.	*/
	public ITensor LU_Compose(){ return ((ITensor)copy()).LU_ComposeAt(); }

	/** Re-Composition of LU decomposition in Place.
	  * Undoes the Permutation of Rows also.
	  * This Operation can be done in Place,
	  * if you start from Bottom Left, because this Element == a[i,j]
	  * is only used within this same line.		*/
	public ITensor LU_ComposeAt() {
		if (! isDecomposedLU()) {
			return this; } 
		for (int i = mDim +1; --i > 0;) { 	//first row is not modified, because L[1,1]=1
			Matrix iRow = (Matrix) a[i];
			for (int j = mDim +1; --j >= 0;) {
				int k = i;
				IIntRing Element = iRow.a[j];	// == a[i,j]
				if (j < i) {	//because lower left Elements of U == 0
					Element.mulAt(((Tensor)a[j]).a[j]); k = j;}	//because Diagonal   Elements of U == 1
				while (--k >= 0) {
					Element.addProdAt(iRow.a[k], ((Tensor)a[k]).a[j]);
				}
			}
		}
		IIntRing tmp;	//Undo the Row Permutations!
		for (int i=mDim; --i >= 0;)
			if (rows[i] != i) {tmp = a[i]; a[i] = a [rows[i]]; a [rows[i]] = tmp;}
		rows = null; //decomposedLU = false;
		return this; }

	/**Adds Columns (not Rows) to a Tensor to make it square.
	 * This eliminates possible Optimizations due to sparse Matrices,
	 * but is necessary for Operations like LU_DecomposeAt()	 */
	public Matrix makeSquareAt() {
		int i=-1; while (++i <= mDim) //Store the Inverse of the Row-Max Norm for Pivoting
				  ((Tensor) a[i]).setDim(mDim, true, true);
		return this; }

	////////////////////////////////////////////////////////////////////////////////
	//	Normalization, Orthogonalization
	////////////////////////////////////////////////////////////////////////////////

	/**Makes these Row- Vectors othogonal
	 * and can bring their (euklidean) Length to 1 in Place.
	 * Makes only Sense for Matrices */
	public Matrix OrthoAt(boolean normal) {
		IMetricIRing Sqr;
		IMetricIRing[] SqrNorm = null;	//For saving the SqrNorm of the Vectors,
//		if (! normal)	//if not normalized, save this
			SqrNorm = new IMetricIRing[mDim+1];	//now it is actually used by subtPart!
		for (int i = -1; ++i <= mDim;) {
			Tensor tmp = (Tensor) a[i];
			for (int j = -1; ++j < i;)	//Subtract all upper Row Vectors
				tmp.subtPart((Tensor) a[j], SqrNorm[j]);	//a[i] -= <a[i],a[j]> a[j] / <a[j],a[j]>
			Sqr = (IMetricIRing) tmp.SqrNorm();
			if (normal) tmp.mulAt(Sqr.SqRt().inv());
			else SqrNorm[i] = Sqr; }
		return this; }

	/**Normalizes these Row- Vectors to (euklidean) Length 1
	 * Makes only Sense for Matrices */
	public Matrix Ortho(boolean normal)	{return ((Matrix)copy()).OrthoAt(normal);}

	/**linear equation solution by LU decomposition	in Place.	*/
	public Matrix decomposeLU(){return ((Matrix)copy()).decomposeLuAt();}

	/**linear equation solution by LU decomposition	in Place.
	 * An LU Decomposed Matrix is represented by a lower triangle Matrix
	 * and an upper triangle Matrix with only 1s in the Diagonal.
	 * Is an n3/3 Algorithm, i.e. 3 times faster than calculating the Inverse
	 * Useful for calculating the Determinant also!
	 * The Matrix is replaced in Place by it's decomposed Matrix,
	 * The Index Vector 'Rows' keeps track of the Row Permutations.
	 * 'Sign' keeps track of the Sign of the Permutation*/
	public Matrix decomposeLuAt() { 	//N3/3 Algorithm
		if (isDecomposedLU()) { 
			return this; } 
		rows =  new int[mDim+1]; //decomposedLU = true;
		sign = false;

		makeSquareAt();	//to create Space for the higher Elements.

		int i, j, k, imax = 0;
		AMetricIRing big, sum;
		AMetricIRing[] vv = new AMetricIRing[mDim+1];	//Contains the Max-Norm of each row
		AMetricIRing dum;
		Tensor iRow;

		AMetricIRing Zero = (AMetricIRing)((Tensor) a[0]).a[0].zero();
		for (i=-1; ++i <= mDim;) //Store the Inverse of the Row-Max Norm for Pivoting
			vv[i] = (AMetricIRing)((AMetricIRing)a[i]).Max_Norm().invAt();
		for (j=-1; ++j <= mDim;) {
			for (i=-1; ++i < j;) { 	//Process the lower Rows
				iRow = (Tensor)a[i];
				sum = (AMetricIRing)iRow.a[j];
				for (k =-1; ++k < i;)
					sum.subtProdAt(iRow.a[k], ((Tensor)a[k]).a[j]); }
			big = Zero;
			for (i = j; i <= mDim; i++) {	//Process the upper Rows and search for the relative Pivot, normalized by the Max-Norm.
				iRow = (Tensor)a[i];
				sum = (AMetricIRing)iRow.a[j];
				for (k = -1; ++k < j;)	sum.subtProdAt(iRow.a[k], ((Tensor)a[k]).a[j]);
				if  (big.isLessThan (dum = (AMetricIRing)((IGroupM)sum.AbsV()).mulAt(vv[i])))
					{big = dum; imax = i;}
			}
			if (j != imax) { 	//Swap the rows
				dum = (AMetricIRing) a[imax]; a[imax] = a[j]; a[j] = dum;
				sign = !sign; vv[imax] = vv[j]; }
			rows[j] = imax;	//Don't care for Overflows anymore, using Infinity!
//			if (((Tensor)a[j]).a[j].isZero()) ((Tensor)a[j]).a[j].copyAt(MaxAccuracy);	//not necessary, work with Infinity
			if (j != mDim) {
				dum = (AMetricIRing)((Tensor)a[j]).a[j].inv();
				for (i = j; ++i <= mDim;)
					((Tensor)a[i]).a[j].mulAt(dum); }
		}
		return this; }

	/**linear equation solution by Backsubstitution
	 * after Decomposition
	 * b is replaced by the Solution in Place.*/
	public Matrix solveLU(Matrix b) {
		int i, j, ii = -1;
		IIntRing Sum;
		Tensor iRow;
		decomposeLuAt();	//Does not decompose, if already done!
		b.LU_ComposeAt();
		b.setDim(mDim, true, true);
		for (i=-1; ++i <= mDim;) {	//Process the upper Triangle
			int ip = rows[i];
			Sum = b.a[ip]; b.a[ip] = b.a[i]; b.a[i] = Sum;	//Redo the Permutation
			iRow = (Tensor) a[i];
			if (ii >= 0)	//Optimization: only from the first nonzero Element on!
				for (j = ii; j <= i-1; j++)
					Sum.subtProdAt(iRow.a[j], b.a[j]);
			else if (! Sum.isZero()) ii = i; }
		for (i = mDim+1; --i >= 0;) {	//Process the lower Triangle
			Sum = b.a[i];
			iRow = (Tensor) a[i];
			for (j=i; ++j <= mDim;)
				Sum.subtProdAt(iRow.a[j], b.a[j]);
			Sum.divAt(iRow.a[i]); }
		return b; }

	/** Multiplication in Place: °=
	  * This is a linear Mapping in Fact.
	  * It returns the Argument, modified in Place.
	  *
	  * The Tensor Multiplication is defined in mul() and delegated to,
	  * because it needs a Copy of the original Tensor.	 */
/*	public Object MapAt(Object arg) {
		if (LU_Decomposed) { 	//Multiply only with the upper Matrix U
			for (int i = -1; ++i <= mDim;) {
				Tensor iRow = (Tensor) a[i];
				for (int j = i; j <= mDim; j++)
					iRow.a[j].mulAt(arg); }
			return this; }
		else return super.mulAt(arg);	//Use same Scalar Multiplication as with Polynoms and Manifolds
	}

	//////////////////////////
	//	Matrix Inversion	//
	//////////////////////////

	/** Inversion in Place: 1/x	 */	public IGroupM invAt(){return (IGroupM)shallowCopyAt(inv());}
	/** Inversion: 1/x	 */			public IGroupM inv()	 {return invTrp().trpAt();}
	/** Inversion and Transposition: 1/xT	 */
	public Matrix invTrp() { return solveLU((Matrix) one());}

	/**Division and Transposition in Place: / arg^T
	 * Requires arg to be LU Decomposed.	 */
	public Matrix divTrpAt(Matrix arg) {
		return arg.solveLU(this);	//possible both to solve the whole System with one Call or with several Calls
//		for (int i=-1; ++i <= mDim;) arg.LU_Solve((Tensor)a[i]);	//does Decomposition on it's own!
//		return this;
	}

	/**Division and Transposition: /= arg^T	 */
	public Matrix divTrp(Matrix arg){
		return ((Matrix) copy()).divTrpAt(arg);}

	/**Division in Place: /= arg	 */
	public IGroupM divAt(Object arg)	{
		if (arg instanceof Tensor) { 	//(Vector / Vector) or (Tensor / Vector) or (Tensor / Tensor)
			Tensor arg_ = (Tensor) arg;
			if (arg_.a[0] instanceof Matrix) 	//Division by Matrix
				return divTrpAt((Matrix) arg).trpAt();	//The Argument must not be decomposed!!!
			else
				return divAt(arg);	//(Tensor / Vector) or (Matrix / Vector):  ManiFold- Like Division of the Argument by each Item
		}	//else (Matrix / Scalar) or (Vector / Scalar)
		else return (IGroupM)mulAt(((IGroupM) arg).inv());//Use same Scalar Multiplication as with Polynoms and Manifolds
	}

	/**Division: /	 */
	public IGroupM div(Object arg)	{return ((Tensor) copy()).divAt(arg);}

	//Calculation of Determinant:
	//Build all Permutations of the Indices is of Order n!
	//Multiply the Coefficients according to the Indices and build the Sum is of order (n+1)!
	//This is numerically not stable, since the Products tend to cancel each other out!

	/**Negation in Place: -=
	 * Extended to also allow it for LU Decomposed Matrices: -(L*U) == L*-U	*/
	public IGroup negAt () {	//Negation 
		if (!isDecomposedLU()) {	//Negate only the upper Matrix U
			super.negAt(); 
		} else {
			for (int i = -1; ++i <= mDim;)	{
				Tensor iRow = (Tensor) a[i];
				for (int j = i; j <= mDim; j++) {
					iRow.a[j].negAt(); }
			}
		}
		return this; 
	}

	/**Returns the Determinant of the (square) Matrix:
	 * The Determinant of a Matrix is the Volume of the Figure
	 * built from it's Row- or Column- Vectors.
	 * It stays constant with orthogonal Transformations.	 */
	public IIntRing Det()	{return ((Matrix)copy()).DetAt();}

	/**Returns the Determinant of the (square) Matrix in Place:
	 * The Determinant of a Matrix is the Volume of the Figure
	 * built from it's Row- or Column- Vectors.
	 * It stays constant with orthogonal Transformations.	 */
	public IIntRing DetAt()		//Assume that this is a square Matrix.
	{	//The Determinant is the Product of the Diagonal Elements of the Decomposed Matrix
		decomposeLuAt();	//Using Decomposition is very stable and fast! N3/3 instead of n!
		IIntRing Prod = ((Tensor)a[0]).a[0];
		for (int i = 0; ++i <= mDim;) Prod.mulAt(((Tensor)a[i]).a[i]);
		if (sign) Prod.negAt();
		((Tensor)a[0]).mDim = 0;
		mDim = 0;
		return Prod; }

	/** true, when the Matrix is orthogonal, i.e. M*Mt = Mt*M = diag(a, b, c, ...).
	  * If a Matrix contains complex coefficients, it should be checked to be unitarian.	 */
	public boolean isOrthogonal() {	//The Optimization here is that you have to test only one Triangle
		//because the Product is symmetric.
		for (int i = -1; ++i <= mDim;)
			for (int j = -1; ++j < i;)
				if (!(((IGroup)a[i].mul(a[j])).isZero()))	//Use an Epsilon here that corresponds to any Matrix Norm
					return false;
		return true; }

	/**true, when the Matrix is unitarian resp. orthonormal, i.e. M*Mt = Mt*M = 1.
	 * unitarian is the complex equivalent to orthonormal 	 */
	public boolean isUnitarian()
	{	//The Optimization here is that you have to test only one Triangle
		//because the Product is symmetric.
		if (! isOrthogonal()) return false;
		for (int i = -1; ++i <= mDim;)
			if (!(((IGroupM)a[i].sqr()).isOne()))
				return false;
		return true; }

//	public boolean  orthoNorm();  ð unitaer fuer reelle Matrizen;

	/**true, when the Matrix is hermitean resp. symmetric, i.e. M = Mt.	 */
	public boolean  isHermitean  () {	//The Optimization here is that you have to test only one Triangle
		for (int i = -1; ++i <= mDim;)
			for (int j = -1; ++j < i;)
				if (!(((Tensor)a[i]).a[j].equals(((Tensor)a[j]).a[i])))	//Could also test for the Difference to be Zero
					return false;
		return true; }

//	public boolean  symmetr  ();  ð hermite fuer reelle Matrizen;

	/**true, when the Matrix is anti-hermitean resp. anti-symmetric, i.e. M = -Mt.	 */
	public boolean  isAntiHermitean () {	//The Optimization here is that you have to test only one Triangle
		for (int i = -1; ++i <= mDim;)
			for (int j = -1; ++j <= i;)
				if (!(((Tensor)a[i]).a[j].equals(((Tensor)a[j]).a[i].neg())))	//Could also test for the Sum to be Zero
					return false;
		return true; }

//	public boolean  antiSym  (); ð antiHerm fuer reelle Matrizen;

	/**true, when the Matrix is normal, i.e. M*M^T = M^T*M.
	 * i.e. M*M^T is symmetric	 */
	public boolean  isNormal() {	//The Optimization here is that you have to test only one Triangle
		//because the Product is symmetric.
		Tensor Trp = (Tensor) trp();
		for (int i = -1; ++i <= mDim;)
			for (int j = -1; ++j <= i;)
				if (!(a[i].mul(a[j]).equals(Trp.a[i].mul(Trp.a[j]))))
					return false;
		return true; }

	/**The Trace of a Matrix is the Sum along it's Diagonal.
	 * It stays constant with orthogonal Transformations.	 */
	public IIntRing Trace() {	//Assume that this is a square Matrix.
		IIntRing Trace = (IIntRing)((Tensor)a[0]).a[0].copy();
		for (int i = 0; ++i <= mDim;)
			if (((Tensor)a[i]).mDim >= i)
				Trace.addAt(((Tensor)a[i]).a[i]);
		return Trace; }

	/**Swaps the Columns of this Tensor in Place	 */
	public Matrix SwapColumns (int Dim1, int Dim2) {
		return ((Matrix) copy()).SwapColumnsAt (Dim1, Dim2);}

	/**Swaps the Columns of this Tensor in Place	 */
	public Matrix SwapColumnsAt (int Dim1, int Dim2) {
		IIntRing r;
		for (int i = -1; ++i <= mDim;) {
			Matrix Row = (Matrix) a[i];
			r = Row.a[Dim1]; Row.a[Dim1] = Row.a[Dim2]; Row.a[Dim2] = r; }
		return this; }

	/**Swaps the Rows of this Tensor in Place	 */
	public Matrix SwapRows (int Dim1, int Dim2) {
		return ((Matrix) copy()).SwapRowsAt (Dim1, Dim2);}

	/**Swaps the Rows of this Tensor in Place	 */
	public Matrix SwapRowsAt (int Dim1, int Dim2) {
		IIntRing c = a[Dim1]; a[Dim1] = a[Dim2]; a[Dim2] = c; return this;}

	public static void main(String[] args) {
		Matrix matrix = new Matrix(new BodyDouble(), 2); 
		matrix.map(matrix); 
	}
}
