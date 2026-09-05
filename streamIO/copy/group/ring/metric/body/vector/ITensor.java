package streamIO.copy.group.ring.metric.body.vector;

import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.metric.IMetricIRing;
import streamIO.object.enumer.IndexEnumerator;

/** Integrates the Interfaces of Metric IntegrityRing and
  * IndexEnumerator
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:39:56Z
  * digest: dba6f78e09f333faee839af511e0a86e21085d747f697e56b2581eded70e7f69
  * stale: false
  * tags: [code/tensor, code/manifold_generation, code/interpolation]
  * concepts: [Vector/Matrix/Tensor and Manifold Interpolation]
  * facets: {layer: domain, status: legacy, complexity: high}
  * -->
  */
public interface ITensor
extends IMetricIRing, IndexEnumerator {

	/** Returns the Grad of the Polynom == Dimension-1 of the Vector,
	  * or the Number of Items for the shifting Operations.
	  * This is also Period for large Shifting, Rotation and Reversion
	  * (for performance Reasons)	 */
	int getDim();

	/**Sets the Grad of the Polynom == Dimension-1 of the Vector.
	 * When Preserve = true, the Contents of the Polynom is preserved.
	 * Initializes the Elements above mDim to 0, when zeroUpper = true.
	 * The Grad is the Period for the large Rotation Operations.
	 *
	 * Possibilities: new Array is...
	 * 1) uninitialized and potentially (half) empty (preserved = false)
	 * 2) initialized with only new Elements (not implemented)
	 *		use a brand new gAdic.
	 * 3) initialized with preserved old and new Elements
	 *		(Preserve = true)
	 *
	 * The Carry Element is used to generate new Elements. */
	public int setDim(int Grad, boolean preserve, boolean zeroUpper);


	/** @return the Item at the given Multi Index Position
	  * This is used to define the generic Tensor Product!
	  * @return this for index.Grad == -1  !
	  *
	  * Instead of defining this recursively, it is calculated faster iteratively!
	  */
//	public IIntRing getAt(final Permutation MIndex);

	/** @return the Item at the given Multi Index Position
	  * This is used to define the generic Tensor Product!
	  * @return this for index.Grad == -1  !
	  *
	  * Instead of defining this recursively, it is calculated faster iteratively!
	  */
//	public IIntRing getAt(final Permutation MIndex, final int MaxGrad);

	/** @return the Item at the given Multi Index Position
	  * This is used to define the generic Tensor Product!
	  * @return this for index.Grad == -1  !
	  *
	  * Instead of defining this recursively, it is calculated faster iteratively!
	  */
//	public void setAt(Permutation MIndex, IIntRing Value);

	/** @return the Item at the given Multi Index Position
	  * This is used to define the generic Tensor Product!
	  * @return this for index.Grad == -1  !
	  *
	  * Instead of defining this recursively, it is calculated faster iteratively!
	  */
//	public void setAt(Permutation MIndex, IIntRing Value, int MaxGrad);

	///////////////////////////////////////////////////////////////////////////
	//  Tensor specific Methods
	///////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	//	Normalization, Orthogonalization
	////////////////////////////////////////////////////////////////////////////////

	/**Subtracts the Part which lies parallel to the Vector arg.
	 * Used primarily in orthogonalization.
	 * @param argSqrNorm If == null, it is assumed to be 1 (orthoNormal)
	 * this -= arg*((arg*this)/(arg*arg))	 */
	public ITensor subtPartAt(ITensor arg, IIntRing argSqrNorm);

	/**Subtracts the Part which lies parallel to the Vector arg.
	 * Used in orthogonalization.
	 * @param argSqrNorm If == null, it is assumed to be 1 (orthoNormal)
	 * this -= arg*((arg*this)/(arg*arg))	 */
	public ITensor subtPart(ITensor arg, IIntRing argSqrNorm);

	/** Normalizes this Vector in Place to (euklidean) Length 1
	  * Makes only Sense for Vectors */
	public ITensor normalizeAt();

	/** Normalizes this Vector to (euklidean) Length 1
	  * Makes only Sense for Vectors */
	public ITensor normalize();

	///////////////////////////////////////////////////////////////////////////
	//  Methods
	///////////////////////////////////////////////////////////////////////////

	//////////////////////////
	//	Vector Operations:	//
	//////////////////////////

	/**Removes leading 0s by decreasing the Grad	 */
	public ITensor canonicalizeAt();

	//////////////////////
	//	Optimizations:	//
	//////////////////////

	/**Flags for the Array Operations 	 */

	/** Op-code for element-wise Addition: only for Tensor + Tensor	 */
	final static public int opFlagAdd		=  0; 	//only for Tensor + Tensor
	/** Op-code for element-wise Subtraction: only for Tensor - Tensor	 */
	final static public int opFlagSubt		=  1; 	//only for Tensor - Tensor
	/** Op-code for Matrix Multiplication: only for Manifold * Manifold	 */
	final static public int opFlagMMul		=  2; 	//only for Manifold * Manifold
	/** Op-code for Matrix Division: only for Manifold / Manifold	 */
	final static public int opFlagMDiv		=  3; 	//only for Manifold / Manifold
	/** Op-code for a linear (scale-and-shift) Operation	 */
	final static public int opFlagLin		=  4;
	/** Op-code for an add-Product Operation: this += a*b	 */
	final static public int opFlag_AddProd =  5;
	/** Op-code for a subtract-Product Operation: this -= a*b	 */
	final static public int opFlagSubtProd =  6;
	/** Op-code for a bilinear Operation combining two Factors and two Terms	 */
	final static public int opFlagBiLin	=  7;
	/** Op-code for an element-wise Maximum Operation	 */
	final static public int opFlagMax		=  8;
	/** Op-code for an element-wise Minimum Operation	 */
	final static public int opFlagMin		=  9;
	/** Op-code selecting the first  Operand/Term of a multi-argument Operation	 */
	final static public int opFlagONE		= 10;
	/** Op-code selecting the second Operand/Term of a multi-argument Operation	 */
	final static public int opFlagTWO		= 11;
	/** Op-code selecting the third  Operand/Term of a multi-argument Operation	 */
	final static public int opFlagTHREE	= 12;
	/** Op-code selecting the fourth Operand/Term of a multi-argument Operation	 */
	final static public int opFlagFOUR 	= 13;

	/** Function-code testing whether a Value lies between two bounds	 */
	final static public int funcFlagBetween= 0;
	/** Function-code testing "greater than"	 */
	final static public int funcFlagGrtr	= 1;
	/** Function-code testing "greater than or equal"	 */
	final static public int funcFlagGrtrEq	= 2;
	/** Function-code testing "less than"	 */
	final static public int funcFlagLess	= 3;
	/** Function-code testing "less than or equal"	 */
	final static public int funcFlagLessEq	= 4;


	//////////////////////////////
	//	Generic Scalar Products
	//////////////////////////////

	/**Returns the Sum of all Elements in this Tensor.
	  * @return the Sum of all Elements in this Tensor Sum(i, x[i])	  */
	public IIntRing Sum();

	/**Returns a copy of this Tensor with the given Degree summed away.
	  * @return this Tensor shortened at the given Degree.
	  * I.e. all Elements at Level Degree are replaced by the Sum of all Elements below it
	  */
	public IIntRing Sum(int Degree);

	/**Sums away the given Degree in Place, decreasing this Tensor's Degree by 1.
	  * @return this Tensor shortened in Place at the given Degree.
	  * I.e. all Elements at Level Degree are replaced
	  * by the Sum of the Elements right below them.
	  * The Degree of the Tensor decreases by 1
	  * a[i,j,k] => a[i,k] = Sum(j, a[i,j,k])
	  */
	public IIntRing SumAt(int Degree);

	//The Tensor Products can be different:
	// mul  () c[i  ] = a[i]*b[i]
	// dyad () c[i,j] = a[i]*b[j]
	// short() c      = Sum(i, a[i]*b[i] = Sum(i, a.mul(b)) , the Scalar Product
	// map  () uses the Scalar Product

	/**Returns the dyadic Product of this Tensor and arg, a pre-step to the generic Scalar Product.
	  * @return the dyadic Product of this Tensor and arg.
	  * This is a pre Step to calculating the generic Scalar Product.
	  * The Degree of the Tensor is the Sum of the Degrees of the Factors.
	  *
	  * The inner structure of arg is retained on creating the Product.
	  */
	public ITensor dyad(ITensor arg);

	/**Returns the dyadic Product of this Tensor and arg at the given Degree.
	  * @return the dyadic Product of this Tensor and arg.
	  * This is a pre Step to calculating the generic Scalar Product.
	  * The Degree of the Tensor is the Sum of the Degrees of the Factors.
	  *
	  * The inner structure of arg is retained on creating the Product.
	  */
	public ITensor dyad(ITensor arg, int Degree);

	/**Computes the dyadic Product of this Tensor and arg in Place.
	  * @return the dyadic Product of this Tensor and arg.
	  * This is a pre Step to calculating the generic Scalar Product.
	  * The Degree of the Tensor is the Sum of the Degrees of the Factors.
	  *
	  * The inner structure of arg is retained on creating the Product.
	  *
	  * a[i,j,k]�b[l,m,n] = c[i,j,k,l,m,n] is the scalar Element calculated by
	  * a[i,j,k] := a[i,j,k]�b[]
	  */
	public ITensor dyadAt(ITensor arg);

	/**Multiplies this Tensor by arg in Place at the given Degree.
	  * @return this Tensor multiplied in Place at the given Degree.
	  * This is a pre Step to calculating the Scalar Product.
	  *
	  * The outer structure remains the same and is used to hold the Product.
	  * The inner structure of arg is retained on creating the Product.
	  */
	public ITensor dyadAt(ITensor arg, int Degree);

	/**Re-Composition of LU decomposition in Place.
	 * Undoes the Permutation of Rows also.
	 * This Operation can be done in Place,
	 * if you start from Bottom Left, because this Element == a[i,j]
	 * is only used within this same line.		*/
	public ITensor LU_ComposeAt();

	/** "Multiplication": �
	  * This is in fact a non-commutative linear Mapping:
	  * M�(a+b) == M�a + M�b
	  * (x+y)�M == x�M + y�M
	  *
	  * The Matrix itself is the Derivative Jacobian Matrix of the Mapping:
	  * (A*x)' = A
	  *
	  * Here the Tensor Multiplication is defined recursively
	  * between two Tensors of these Dimensions: (n,m)*(m,k) => (n,k)
	  * The innermost (last ) Dimension of the left  Argument has to match
	  * the outermost (first) Dimension of the right Argument.
	  * The Transposition is done implicitly, only the Complement
	  * has to be done in place manually, if needed for complex coefficients.
	  *
	  * This Multiplication can also multiply the Transpose Tensors,
	  * by just swapping the Operands!
	  * (and transposing the Result, which is not necessary for Vectors) */
	public IIntRing catAt(ITensor arg);

	/** Scalar Product Multiplication: �
	  * This is in fact a non-commutative linear Mapping:
	  * M�(a+b) == M�a + M�b
	  * (x+y)�M == x�M + y�M
	  *
	  * The Matrix itself is the Derivative Jacobian Matrix of the Mapping:
	  * (A*x)' = A
	  *
	  * Here the Tensor Multiplication is defined recursively
	  * between two Tensors of these Dimensions: (n,m)*(m,k) => (n,k)
	  * The innermost (last ) Dimension of the left  Argument has to match
	  * the outermost (first) Dimension of the right Argument.
	  * The Transposition is done implicitly, only the Complement
	  * has to be done in place manually, if needed for complex coefficients.
	  *
	  * Since higher Coefficients are assumed to be null resp. Zero,
	  * it is sufficient to multiply only to the lesser Degree of both Vectors.
	  *
	  * This Multiplication can also multiply the Transpose Tensors,
	  * by just swapping the Operands!
	  * (and transposing the Result, which is not necessary for Vectors) */
	public IIntRing cat(ITensor arg);

	/** @return the generic Tensor Product
	  * calculated by Summing up the Products along the given Degrees
	  * and leaving the other Degrees in Order, i.e.
	  *
	  * T[i,j,k]*A[l,j,m] = R[i,l,m,k]
	  * To move the Elements at the Proper Position,
	  * Transpose the Tensor in it's Elements.
	  */
//	public IIntRing catAt(byte Degree1,
//		ITensor arg, byte Degree2);

	/** @return the generic Tensor Product
	  * calculated by Summing up the Products along the given Degrees
	  * and leaving the other Degrees in Order, i.e.
	  *
	  * T[i,j,k]*U[l,j,m] = R[i,k,l,m]
	  * To move the Elements at the Proper Position,
	  * Transpose the Tensor in it's Elements.
	  */
//	public IIntRing cat(byte Degree1,
//		ITensor arg, byte Degree2);

	/**Computes the Tensor Product of this Tensor with a Matrix along the given Degree, in Place.
	  * @return the Tensor Product with a Matrix
	  *
	  * T[i,j,k]*A[m,j] = R[i,m,k]
	  * Transpose the Matrix if necessary.
	  *
	  * R[i,m,k] = T[i,k,j]*A[m,j] =T[i,k][j]*A[m][j]
	  *
	  * T[i,k,j]*A[j,m] =T[i,k][j]*A[j][m]
	  */
	public ITensor catAt(byte Degree1, Matrix arg);

	/** @return the Element of the generic Tensor Product
	  * calculated from the Coefficients at the given Multi Index Positions
	  * of the Factors.
	  * @return Sum(MIndex1[Degree1] = MIndex2[Degree2], Tensor1[MIndex1] * Tensor2[MIndex2])
	  *
	  * This is the Basis for a generic Tensor Concatenation
	  * in arbitrary Indices for Tensors of arbitrary Degree.
	  */
//	public static IIntRing cat(
//		Permutation MIndex1, byte Degree1, ITensor Tensor1,
//		Permutation MIndex2, byte Degree2, ITensor Tensor2);

	/** Creates the Transpose of this Tensor: M^T
	  * in the given (Default: first two) Dimensions: a[i][j] = b[j][i]
	  * The Elements are copied, not reused. */
	public ITensor trp();

	/** Creates the Transpose of this Tensor: M^T
	  * in the given (Default: first two) Dimensions: a[i][j] = b[j][i]
	  * The Elements are copied, not reused. */
	public ITensor trp(int Degree);

	/** Creates the Transpose of this Tensor in Place: MT
	  * in the given (Default: first two) Dimensions: a[i][j] = b[j][i]
	  * i.e. in the Dimensions Degree and Degree+1.	 */
	public ITensor trpAt();

	/** Creates the Transpose of this Tensor in Place: MT
	  * in the given (Default: first two) Dimensions: a[i][j] = b[j][i]
	  * i.e. in the Dimensions Degree and Degree+1.	 */
	public ITensor trpAt(int Degree);

}
