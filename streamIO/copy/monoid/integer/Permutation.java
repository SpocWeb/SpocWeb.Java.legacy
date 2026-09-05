package streamIO.copy.monoid.integer;

import math.vector.VectorInt;
import streamIO.AStreamOut;
import streamIO.Assert;
import streamIO.IIStreamIn;
import streamIO.IReSetAble;
import streamIO.Log;
import streamIO.copy.ICopyAble;
import streamIO.copy.boole.Boole;
import streamIO.copy.boole.Lattice;
import streamIO.copy.monoid.AMonoid;
import streamIO.copy.monoid.Association;
import streamIO.copy.monoid.IMonoid;
import streamIO.copy.monoid.ISemiMonoid;
import streamIO.copy.shift.SwapAble;
import streamIO.object.AStreamIn;
import function.IFunction;
import function.IInvertAble;
import function.byref.ByRefInt;
import function.byref.ByRefLong;

/**
  * Instances of this Class can be used for 3 different Purposes at the same Time: <BR/>
  * - Permutation <BR/>
  * - Multi Index <BR/>
  * - Integer Set Operations <BR/>
  *
  * TODO: most Functionality is already in VectorInt!
  * Permutations for a fixed length Vector of Integers form a Monoid, no Group,
  * since they don't commute, as can be seen easily using (1,3,2)*(2,1,3).
  * Permutations can be applied to each other or other Vectors / Manifolds
  * of the same length.
  * Permutations are Functions and are thus derived from Relations,
  * which cannot be represented by Integer Arrays,
  * but by Forest.SparseMatrix or Forest.MatrixGraph.
  *
  * 'map' and 'solve' Operations swap the Order of the Arguments,
  * while 'cat' and 'unCat' keep the Order.
  * Thus:
  * A.catAt(B).unCatAt(B) == A == (A�=B)\=B (very fast, with A,B Permutations)
  * C.map  (B).  solve  (B) == A == !B�(B�C) (with Permutation B and C: I->O)
  * B.map  (B.   solve  (i))== i == (i�B)�!B (with B working from the right)
  *
  * Permutations of different Length can be concatenated (multiplied),
  * because the upper Elements are assumed as being identical.
  * The One Element is the identical Permutation: a[i] = i
  * The Inverse and the Quotient are the Inverse Mapping.
  * Each Permutation is either odd or even.
  * The even Permutations form a SubGroup.
  *
  * A Multi Index is used to define a generic Index in Tensors of arbitrary Degree
  * It is also used to define
  * @see Multi_ABS
  * @see Multi_Pow
  * @see Multi_Fact
  *
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:34:07Z
  * digest: 60d5e807916ae1e87b561028a803cc692be4c2b4ce7797e3b46d25b405d88eef
  * stale: false
  * tags: [code/permutation, code/multiplicative_semigroup, code/bit_manipulation]
  * concepts: [Permutation, Multi-Index]
  * facets: {layer: utility, status: broken, complexity: medium}
  * -->
  * The Methods are analogous to the ones in gAdic. */
final public class Permutation
extends AMonoid
implements SetInteger, SwapAble, IInvertAble {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Logger for Testing, modify Threshold for switching Logging */
	private static final Log L = new Log(Permutation.class, -0);
	
	/////////////////////////////////////////////////////////////////////////////////////
	//static Members Start
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Determines, if the Degree of Permutations is shortened after Operations	 */
	public static boolean bolLazySimplify;
	
	/////////////////////////////////////////////////////////////////////////////////////
	//static Methods Start
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Calculates the binary Code from the Gray Code. 	 */
	final static public long GRAY2BIN(long gray) {
		long iDiv;
		long shift = 1;
		do {
			gray ^= (iDiv = gray >> shift);
			if (iDiv <= 1)
				return gray;
		} while ((shift <<= 1) != 0);
		return gray;
	}
	
	/**Calculates the Gray Code from the binary Code.
	 * G(i) = i ^(i/2)	=> G(i) ^ G(i+1) = rightmost Zero Bit of i
	 * this is how these two Gray Codes differ.	 */
	final static public long BIN2GRAY(final long bin) {
		return bin ^ (bin >> 1);
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// Permutation Methods
	////////////////////////////////////////////////////////////////////////////////

	/**Testing for 1:	 */
	public static boolean isIdentity(final int[] a, final int length) {
		for(int i = length; --i >= 0;) {
			if (a[i] != i) 
				return false;  
		}
		return true;
	}
	//	  return shortenAt(a, Length) > 0; }

	/**Testing for 1:	 */
	public boolean isIdentity() {
		return CANONICAL_LENGTH(a, mDim + 1) > 0;
	}

	/**Shortens the Permutation by reducing the Degree while a[i]=i
	 * This is necessary on all Operations:
	 * Addition, Subtraction, Multiplication and Division.
	 * Notation specific, whether the identical Permutation is () or (0)
	 */
	public static int CANONICAL_LENGTH(final int[] a, int length) {
		//if (! bolLazySimplify)
		while (--length >= 0) {
			if (a[length] != length) 
				break; 
		}
		return length + 1;
	}

	/**Shortens the Permutation by reducing the Degree while a[i]=i
	 * This is necessary on all Operations:
	 * Addition, Subtraction, Multiplication and Division.
	 * Notation specific, whether the identical Permutation is () or (0)
	 */
	public static int SHORTEN_AT(final Object[] a, int length) {
		//if (! bolLazySimplify)
		while (--length >= 0) {
			if (a[length] != null)  
				break; 
		} 
		return length + 1;
	}

	/**Shortens the Permutation by reducing the Degree while a[i]=i
	 * This is necessary on all Operations:
	 * Addition, Subtraction, Multiplication and Division.
	 */
	public static void ELONGATE_AT(final int[] a, final int length, int newLength) {
		//if (newLength > a.length) { throw new AbstractMethodError("cannot be enlarged in Place!"); } //
		while (--newLength >=    length) 
			   a[newLength] = newLength; 
	} //Exception thrown on exceeding Space

	/**Setting to 1 in Place, usually done by decreasing Length:	 */
	public static int[] IDENTITY(final int[] a) { 
		return IDENTITY(a.length, a); } //
	
	/**Setting to 1 in Place, usually done by decreasing Length:	 */
	public static int[] IDENTITY(final int length) { 
		return IDENTITY(length, null); } //
	
	/**Setting to 1 in Place, usually done by decreasing Length:	 */
	public static int[] IDENTITY(int length, int[] a) { //elongateAt(a, 0, Length);
		if (a == null)
			a = new int[length]; 
		while (--length >= 0) 
			a[length] = length;  
		return a;
	}

	/**Setting to 1 in Place, usually done by decreasing Length:	 */
	public IMonoid IdentityAt() {
		mDim = -1;
		return this;
	}

	/**Shortens the Permutation by reducing the Degree while a[i]=i
	 * This is necessary on all Operations:
	 * Addition, Subtraction, Multiplication and Division.
	 */
	public static void elongateAt(Object[] a, int Length, int newLength) {
		//		if (newLength > a.length) throw new AbstractMethodError(); //cannot be enlarged in Place!
		while (--newLength >= Length)
			a[Length] = null;
	}

	/**Checking whether an Array int[] is a Permutation is an O(N) Operation	 */
	public static boolean isPermutation(final int[] perm) {
		final int[] inv = VectorInt.INVERSE(perm, perm.length);
		for (int i = perm.length; --i >= 0; ) {
			if (inv[i] == 0) {
				if (perm[0] != i) 
					return false; 
			}
		}
		return true;
	}

	/**Mapping from the Left in Place:  this=�arg
	 * This Operation doesn't return 'this', but 'arg'!
	 * so to concatenate Mappings use B.mapAt(A.mapAt(a))
	 * which is more efficient than B.map(A.map(a)) or B.map(A).map(a)
	 * or A.cat(B).map(a)  */
	public static Object mapAt(int[] ths, int thsLength, Permutation arg_) {
		int argDim = arg_.mDim;
		if (argDim < thsLength - 1) {
			arg_.letGrad(thsLength - 1, true, false);
			arg_.mDim = argDim;
		} //don't enlarge the Array, not necessary!
		mapAt(ths, thsLength, arg_.a, arg_.mDim + 1);
		return arg_;
	}

	/**Mapping from the Left in Place:  this=�arg
	 * This Operation doesn't return 'this', but 'arg'!
	 * so to concatenate Mappings use B.mapAt(A.mapAt(a))
	 * which is more efficient than B.map(A.map(a)) or B.map(A).map(a)
	 * or A.cat(B).map(a)  */
	public static Object mapAt(final int[] ths, final int thsLength, final Object arg) {
		//	  return ((copyAble) arg).copyAt(map(arg)); } //default Implementation
		//	  if (arg instanceof Object[])	return ((Object[]   ) arg).catAt(self); //not defined for Objects, only for Integer Types, cannot be done in Place!
		//	  if (arg instanceof Integer)	 return new Integer(a[((Integer) arg).getInt()]); //cannot be done in Place!
		if (arg instanceof int[]) {
			int[] arg_ = (int[]) arg;
			return mapAt(ths, thsLength, arg_, arg_.length);
		} //cannot enlarge the Array
		if (arg instanceof Permutation) {
			return mapAt(ths, thsLength, (Permutation) arg); }
		if (arg instanceof ByRefInt) {
			ByRefInt arg_ = (ByRefInt) arg;
			if (arg_.Value < thsLength)
				arg_.Value = ths[arg_.Value];
			return arg;
		}
		if (arg instanceof Association) {
			Association arg_ = (Association) arg;
			Object tmp = mapAt(ths, thsLength, arg_.val);
			if (tmp != null)
				return arg;
			arg_.val = map(ths, thsLength, arg_.val);
			return arg;
		}
		return null;
	}

	/**Mapping from the Left :  this�arg	*/
	public static Object map(int[] ths, int thsLength, Object arg) {
		//	  if (arg instanceof Object[])	{ Object   [] arg_ = (Object   []) arg; return map(arg_, arg_.length, ths, thsLength); }
		if (arg instanceof int[]) {
			int[] arg_ = (int[]) arg;
			return map(ths, thsLength, arg_, arg_.length);
		}
		if (arg instanceof Permutation) {
			Permutation arg_ = (Permutation) arg;
			return new Permutation(map(ths, thsLength, arg_.a, arg_.mDim + 1));
		}
		if (arg instanceof ByRefInt) {
			int v;
			if ((v = ((ByRefInt) arg).Value) < thsLength)
				return new ByRefInt(ths[v]);
			return new ByRefInt(v);
		}
		if (arg instanceof Integer) {
			int v;
			if ((v = ((Integer) arg).intValue()) < thsLength)
				return new Integer(ths[v]);
			return new Integer(v);
		}
		if (arg instanceof Association) {
			Association arg_ = (Association) arg;
			return new Association(arg_.key, map(ths, thsLength, arg_.val));
		}
		return null;
	} //could not map arg...

	//defining unMap directly on Integers is very ineffective,
	//because you have to loop through O(N) Elements

	/**Mapping from the Left :  this�arg  <=>  return Value(arg)	*/
	public static int map(int[] ths, int thsLength, int arg) {
		if (arg >= thsLength)
			return arg;
		return ths[arg];
	}

	/**Mapping of arg by this: this�arg  == this(arg)
	 * <=> arg.Value(Value) = key()  <=> ret[i]:=!this[arg[i]] <=> ret[this[i]]
	 * It cannot be calculated in Place!	 */
	public static Object[] map(Object[] ths, int thsLength, Permutation arg) {
		return map(ths, thsLength, arg.a, arg.mDim + 1);
	}

	/**Mapping of arg by this: this�arg  == this(arg)
	 * <=> arg.Value(Value) = key()  <=> ret[i]:=!this[arg[i]] <=> ret[this[i]]
	 * It cannot be calculated in Place!	 */
	public static Object[] map(Object[] ths, int thsLength, int[] arg) {
		return map(ths, thsLength, arg, arg.length);
	}

	/**Mapping of arg by this: this�arg  == this(arg)
	 * <=> arg.Value(Value) = key()  <=> ret[i]:=!this[arg[i]] <=> ret[this[i]]
	 * It cannot be calculated in Place!	 */
	public static Object[] map(Object[] ths, int[] arg) {
		return map(ths, ths.length, arg, arg.length);
	}

	/**Mapping of arg by this: this�arg  == this(arg)
	 * <=> arg.Value(Value) = key()  <=> ret[i]:=!this[arg[i]] <=> ret[this[i]]
	 * It cannot be calculated in Place!	 */
	public static Object[] map(
		Object[] ths,
		int thsLength,
		int[] arg,
		int argLength) {
		Object[] ret;
		int i;
		if (argLength > thsLength) //ths[i] = null for i > thsLength,
			{
			ret = new Object[argLength];
			i = thsLength;
			System.arraycopy(
				arg,
				thsLength,
				ret,
				thsLength,
				argLength - thsLength);
		} //
		else {
			ret = new Object[thsLength];
			i = argLength;
			if (thsLength > argLength)
				System.arraycopy(
					ths,
					argLength,
					ret,
					argLength,
					thsLength - argLength);
		} //
		while (--i >= 0)
			//			if ((j = arg[i]) < thsLength)  //tolerance for argLength > thsLength
			ret[i] = ths[arg[i]]; //j]; //
		return ret;
	}

	/**Mapping of arg by this: this�arg  == this(arg)
	 * <=> arg.Value(Value) = key()  <=> ret[i]:=!this[arg[i]] <=> ret[this[i]]
	 * It cannot be calculated in Place!	 */
	public static int[] map(
		int[] ths,
		int thsLength,
		int[] arg,
		int argLength) {
		int[] ret;
		int i;
		if (argLength > thsLength) //ths[i] = i for i > thsLength,
			{
			ret = new int[argLength];
			i = thsLength;
			System.arraycopy(
				arg,
				thsLength,
				ret,
				thsLength,
				argLength - thsLength);
		} //
		else {
			ret = new int[thsLength];
			i = argLength;
			if (thsLength > argLength)
				System.arraycopy(
					ths,
					argLength,
					ret,
					argLength,
					thsLength - argLength);
		} //
		while (--i >= 0)
			//		  if ((j = arg[i]) < thsLength)  //tolerance for argLength > thsLength
			ret[i] = ths[arg[i]]; //j]; //
		return ret;
	}

	/** Mapping / Concatenation from the right in Place:  this=�arg <=> arg[i]:=this[arg[i]]
	  * <=> arg.Value(arg.key) := Value(arg.key)
	  * Relies on the Fact that arg is in fact a Permutation!
	  * Cannot handle when 'arg' is longer than 'this' => ArrayOutOfBoundsException
	  */
	final static public int[] mapAt
	( final int[] ths
	, final int thsLength
	, final int[] arg
	, final int argLength) {
		if (thsLength > argLength)
			System.arraycopy(
				ths,
				argLength,
				arg,
				argLength,
				thsLength - argLength);
		//			i = argLength; } //arg[i] == i for i > argLength; prepare the loop
		
		for(int i = argLength; --i >= 0; ) {
			final int j = arg[i];
			if (j < thsLength) { //tolerance for argLength > thsLength, could also be achieved by elongating ths
				arg[i] = ths[j]; }//can be done in Place!!!
		}
		return arg;
	}

	//the Definitions of unMapAt and unMap in absMonoid are the only possible!

	/**Concatenation with the Inverse in Place: this�!arg
	 * This is the Inverse Operation to map(), not to cat()!
	 * It cannot be done in Place!	 */
	public static int[] solve(
		int[] ths,
		int thsLength,
		int[] arg,
		int argLength) {
		int[] ret;
		int i;
		if (argLength > thsLength) {
			ret = new int[argLength];
			i = argLength;
			ELONGATE_AT(ths, thsLength, argLength);
		} //initialize the upper part for swapping below.
		else {
			ret = new int[thsLength];
			i = argLength;
			if (thsLength > argLength)
				System.arraycopy(
					ths,
					argLength,
					ret,
					argLength,
					thsLength - argLength);
		}
		while (--i >= 0)
			ret[arg[i]] = ths[i]; //
		return ret;
	}

	//unMap with Object[] needs a HashTable or Order.indexed[],
	//which carry the Information of how to map between 'Order.indexed' and 'int'

	/**Concatenation with the Inverse in Place: this�!arg
	 * This is the Inverse Operation to map(), not to cat()!
	 * It cannot be done in Place!	 */
	public static Object[] solve(
		Object[] ths,
		int thsLength,
		int[] arg,
		int argLength) {
		Object[] ret;
		int i;
		if (argLength > thsLength) {
			ret = new Object[argLength];
			i = thsLength;
			ELONGATE_AT(arg, thsLength, argLength);
		} //initialize the upper part for swapping below.
		else {
			ret = new Object[thsLength];
			i = argLength;
			System.arraycopy(
				ths,
				argLength,
				ret,
				argLength,
				thsLength - argLength);
		}
		while (--i >= 0)
			ret[arg[i]] = ths[i]; //
		return ret;
	}

	/**Concatenation with the Inverse: �=!arg  <=>  \=arg  <=>
	 * Can be done in Place only if arg.invert() is known.	 */
	/*	public static int[] unCat(int[] ths, int thsLength, int[] arg, int argLength) {
			int[] ret; int i;
			if (argLength > thsLength) {
				 ret = new int[argLength]; i = thsLength; elongateAt(arg, thsLength, argLength); } //initialize the upper part for swapping below.
			else{ret = new int[thsLength]; i = argLength; System.arraycopy(ths, argLength, ret, argLength, thsLength-argLength); }
	//	  int i = argLength > thsLength ? argLength : thsLength;
	//		int[] ret = new int[i];
	//	  while (  i > argLength){ ret[	i ] = ths[i]; --i; } //arg[i] == i for i > argLength
	//	  while (  i > thsLength){ ret[arg[i]] =	 i ; --i; } //ths[i] == i for i > thsLength
			while (--i >= 0)		 ret[arg[i]] = ths[i]; //
			return ret;	}
	
		/**Calculates ths�arg  <=>  arg[ths[i]]
		 * Maps the Array to the Permutation in arg
		 * Relies on the Fact that arg is in fact a Permutation!
		 * Not possible to do this in Place, because of Order AND Types!
		 */
	/*  public static Object[] cat(int[] ths, int thsLength, Object[] arg, int argLength) {
			Object[] ret;
			int  minLength;
			if  (argLength > thsLength) {
				 minLength = thsLength; ret = new Object[argLength]; System.arraycopy(arg, thsLength, ret, thsLength, argLength-thsLength); }
			else{minLength = argLength; ret = new Object[thsLength]; } //nulls stay!
			int j, i = minLength; while (--i >= 0)
				if ((j = ths[i]) < argLength)  //tolerance for thsLength > argLength
					ret[i] = arg[j]; //cannot be done in Place!!!
			return ret;	}
	
		/**Concatenation / Mapping: ths�arg  <=>  arg[ths[i]]
		 * Maps the Array to the Permutation in arg
		 * Relies on the Fact that arg is in fact a Permutation!
		 */
	/*  public static int[] cat(int[] ths, int thsLength, int[] arg, int argLength) {
			int[] ret;
			int  minLength; //arg[j] == j for j > argLength
			if  (argLength >= thsLength) { //ths[i] == i for i > thsLength
				 minLength  = thsLength; ret = new int[argLength]; System.arraycopy(arg, thsLength, ret, thsLength, argLength-thsLength); }
			else{minLength  = argLength; ret = new int[thsLength]; System.arraycopy(ths, argLength, ret, argLength, thsLength-argLength); }
			int j, i = minLength; while (--i >= 0)
				if ((j = ths[i]) < argLength)  //tolerance for thsLength > argLength
					ret[i] = arg[j]; //can be done in Place!!!
			return ret;	}
	
		/**Maps the Array ths from Right in Place: ths�=arg  <=> arg[ths[i]]
		 * Relies on the Fact that arg is in fact a Permutation!
		 * Cannot handle when 'arg' is longer than 'this' => ArrayOutOfBoundsException
		 */
	/*  public static int[] catAt(int[] ths, int thsLength, int[] arg, int argLength) {
			if (argLength > thsLength) { System.arraycopy(arg, thsLength, ths, thsLength, argLength-thsLength);
				argLength = thsLength; } //ths[i] == i for i > thsLength; prepare the loop
			int j, i = argLength; while (--i >= 0)
				if ((j = ths[i]) < argLength)  //tolerance for thsLength > argLength
					ths[i] = arg[j]; //can be done in Place!!!
			return ths;	}
	
	//static Methods Stop

	/////////////////////////////////////////////////////////////////////////////////////
	//member Variables Start
	/////////////////////////////////////////////////////////////////////////////////////

	/**Counting is done like in Polynoms
	 * mDim = -1 is considered as the identical Permutation!	 */
	protected int mDim = -1;
	
	/**Array holding the Values, public for faster Access.	 */
	public int[] a; // = null;
	
	/**Integer Set to delegate to	 */
	private ASetInteger sSet;
	
	//member Variables Stop
	
	//Constructors Start
	
	/**Empty Constructor, only used for newInstance().
	 * mDim = -1 is considered as the identical Permutation!	 */
	public Permutation() { sSet = new ASetInteger(this); }
	
	//Reason: 'this' cannot be used in Constructor Call
	
	/**Constructor, creates the identical Permutation of the given Degree	 */
	public Permutation(final int grad) { this();
		letGrad(grad, false, true);
	}
	
	/**Returns a new array holding a copy of this permutation's coefficients.	 */
	public int[] toArray() {
		final int[] ret = new int[mDim+1]; 
		System.arraycopy(a, 0, ret, 0, ret.length); 
		return ret; 
	}
	
	/**Copies the Coefficients into this Permutation	 */
	private void CopyArr(final int grad, final int[] coeff) {
		letGrad(grad, false, false);
		if (grad >= 0)
			System.arraycopy(coeff, 0, a, 0, grad + 1);
		//No Container Class, so you can use arraycopy
		//{for (int i = -1; ++i <= Grad;) a[i] = Coeff[i];}
	}

	/**Constructor that uses the given Coefficients.	 */
	public Permutation(final int[] coeff) {
		this();
		CopyArr(coeff.length - 1, coeff);
		if (!bolLazySimplify)
			simplify();
	}
	
	/**Copy Constructor */
	public Permutation(final Permutation p) {
		this();
		CopyArr(p.getDim(), p.a);
	}
	
	/**Constructor taking a textual Description*/
	/*	public Permutation(StreamTokenizer inStream)
		{this(absCopyAble.parseList2int(inStream, 0, true));}	//parse by the Separator
	*/
	/**Constructor that creates a Permutation of Grad,
	 * initialized with the Permutation of PermID.
	 * The Grad has to be so large that [Grad+1]! > PermID.
	 * The Schema corresponds to Permutations() Permutation (PermID) and PermID()	 */
	public Permutation(final byte grad, long permID) {
		this(grad); //Create an ID Permutation
		long b = (permID >> 1) & 1;
		permID ^= b; //Flip the Bits for the Sign
		long F = ByRefLong.fact(grad);
		int i = grad;
		while (permID > 0) {
			int j = (int) (permID / F);
			if (j > 0) {
				permID -= j * F;
				//Schema resulting from Permutations() and PermID()
				int k, tmp = a[k = (i - j)];
				System.arraycopy(a, k + 1, a, k, j);
				a[i] = tmp;
			}
			F /= i--;
		}
	}

	//Constructors End

	/**Reduces the Degree of the Permutation by one
	 * Used in Tensor to quickly dynamically create Tensors of higher Degrees */
	public void changeGrad(final int diff) {
		letGrad(mDim + diff, true, false);
	}

	/**Returns the Degree of the Permutation, i.e. the highest Coefficient	 */
	public int getDim() {
		return mDim;
	} //a.length-1;} //

	/**Elongates the Array if necessary	 */
	protected int letGrad(final int grad, final boolean preserve, final boolean initialize) {
		int len = 0;
		if (a != null)
			len = a.length;
		if (grad >= len) {
			int[] tmp = new int[grad + 1];
			if (preserve && (len > 0))
				System.arraycopy(a, 0, tmp, 0, len);
			if (initialize)
				ELONGATE_AT(tmp, mDim + 1, grad + 1);
			a = tmp;
		}
		mDim = grad;
		return mDim;
	}

	//////////////////////////////
	//	Replication intMonoid:	//
	//////////////////////////////

	/**Mapping from the Left in Place:  this=�arg
	 * This Operation doesn't return 'this', but 'arg'!
	 * so to concatenate Mappings use B.mapAt(A.mapAt(a))
	 * which is more efficient than B.map(A.map(a)) or B.map(A).map(a)
	 * or A.cat(B).map(a)		   */
	public Object MapAt(final Object arg) {
		return mapAt(a, mDim + 1, arg);
	}

	/**Mapping from the Left in Place:  this=�arg
	 * This Operation doesn't return 'this', but 'arg'!
	 * so to concatenate Mappings use B.mapAt(A.mapAt(a))
	 * which is more efficient than B.map(A.map(a)) or B.map(A).map(a)
	 * or A.cat(B).map(a)		   */
	public ISemiMonoid mapAt(final ISemiMonoid arg) {
		mapAt(a, mDim + 1, arg);
		return arg;
	}

	/**Mapping from Left in Place:  this=�arg
	 * This Operation doesn't return 'this', but 'arg'!
	 * so to concatenate Mappings use B.mapAt(A.mapAt(a))
	 * which is more efficient than B.map(A.map(a)) or B.map(A).map(a)
	 * or A.cat(B).map(a)		   */
	//	public SemiMonoid mapAt(SemiMonoid arg) { return (SemiMonoid) mapAt(a, mDim+1, arg); }

	/**Mapping from the Left in Place:  this=�arg
	 * This Operation doesn't return 'this', but 'arg'!
	 * so to concatenate Mappings use B.mapAt(A.mapAt(a))
	 * which is more efficient than B.map(A.map(a)) or B.map(A).map(a)
	 * or A.cat(B).map(a)		   */
	public int[] mapAt(final int[] arg, final int argLength) {
		return mapAt(a, mDim + 1, arg, argLength);
	}

	/**Mapping from the Left in Place:  this=�arg
	 * This Operation doesn't return 'this', but 'arg'!
	 * so to concatenate Mappings use B.mapAt(A.mapAt(a))
	 * which is more efficient than B.map(A.map(a)) or B.map(A).map(a)
	 * or A.cat(B).map(a)		   */
	public Permutation mapAt(final Permutation arg) {
		mapAt(a, mDim + 1, arg.a, arg.mDim + 1);
		return arg;
	}

	/**Mapping from the Left :  arg�	*/
	public Permutation map(final Permutation arg) {
		return new Permutation(map(a, mDim + 1, arg.a, arg.mDim + 1));
	}

	/**Mapping from the Left :  arg�	*/
	public int[] map(final int[] arg, final int argLength) {
		return map(a, mDim + 1, arg, argLength);
	}

	/**Mapping from the Left :  arg�	*/
	public Object Map(final Object arg) {
		return map(a, mDim + 1, arg);
	}

	/**Concatenation with the Inverse in Place: �=!this
	 * This is the Inverse Operation to mapAt(), not to catAt()!
	 * This Operation doesn't return 'this', but 'arg'!
	 * so to concatenate Mappings use B.solveAt(A.solveAt(a))
	 * which is more efficient than B.solve(A.solve(a)) or B.map(A).solve(a)
	 * or A.cat(B).solve(a)		   */
	public Object UnMapAt(final Object arg) {
		//	   return ((Monoid) arg).unCatAt(self);
		if (arg instanceof ISemiMonoid)
			return super.pamAt((ISemiMonoid) arg);
		//	  if (arg instanceof Permutation) return ((Monoid) this).unMapAt((Permutation) arg);
		if (arg instanceof ByRefInt) {
			ByRefInt arg_ = (ByRefInt) arg;
			arg_.Value = ((Permutation) rev()).a[arg_.Value];
			return arg;
		}
		//	  if (arg instanceof Integer)	 return new Integer(((Permutation) invert()).a[((Integer) arg).getInt()]); //cannot be done in Place!
		return ((Permutation) rev()).mapAt(arg);
	}

	/**Concatenation with the Inverse in Place: �=!this
	 * This is the Inverse Operation to mapAt(), not to catAt()!
	 * This Operation doesn't return 'this', but 'arg'!
	 * so to concatenate Mappings use B.solveAt(A.solveAt(a))
	 * which is more efficient than B.solve(A.solve(a)) or B.map(A).solve(a)
	 * or A.cat(B).solve(a)		   */
	public Object UnMap(final Object arg) {
		//	   return ((Monoid) arg).unCatAt(self);
		if (arg instanceof ISemiMonoid)
			return super.pam((ISemiMonoid) arg);
		//	  if (arg instanceof Permutation ) return ((Monoid) this).unMap((Permutation) arg);
		if (arg instanceof ByRefInt)
			return new ByRefInt(
				((Permutation) rev()).a[((ByRefInt) arg).Value]);
		if (arg instanceof Integer)
			return new Integer(
				((Permutation) rev()).a[((Integer) arg).intValue()]);
		//cannot be done in Place!
		return ((Permutation) rev()).map(arg);
	}

	//////////////////////////////
	//	Interface SemiMonoid:	//
	//////////////////////////////

	/**Mapping from the Left :  arg�	*/
	public int map(final int arg) {
		if (arg > mDim)
			return arg;
		return a[arg];
	}

	/**Concatenation with the Inverse in Place: �!this	*/
	public int solve(final int arg) {
		if (arg > mDim)
			return arg;
		return ((Permutation) rev()).a[arg];
	}

	/**Concatenation with the Inverse in Place: this�!arg
	 * This is the Inverse Operation to map(), not to cat()!
	 * It cannot be done in Place!	 */
	public int[] solve(final int[] arg, final int argLength) {
		return solve(a, mDim + 1, arg, argLength); }

	/**Concatenation with the Inverse in Place: this�!arg
	 * This is the Inverse Operation to map(), not to cat()!
	 * It cannot be done in Place!	 */
	public Permutation solve(final Permutation arg) {
		return new Permutation(solve(a, mDim + 1, arg.a, arg.mDim + 1)); }

	/** Returns the inverse Permutation in Place
	 * @return the inverse Permutation in Place == this
	 */
	public IMonoid revAt() {
		//		Monoid tmpInverse = (Monoid) shallowCopy();
		shallowCopyAt(rev());
		//	  Inverse = tmpInverse;
		return this; }

	/** Returns a new Instance with the inverse Permutation:   !this
	 * @return the inverse Permutation
	 */
	public IMonoid rev() {
		Permutation ret = new Permutation(VectorInt.INVERSE(a, mDim + 1));
		//	  ret.Inverse = (Monoid) self;
		return ret;
	}

	////////////////////////////
	//  Interface invertAble  //
	////////////////////////////
	
	/** Returns a new Instance with the inverse Permutation:  !this
	 * @return the inverse Permutation
	 */
	public IInvertAble getInverse() {
		return new Permutation(VectorInt.INVERSE(a, mDim + 1));
	}
	
	/**Sets the Inverse Permutation, not used here,
	 * since the Inverse is easy and fast to calculate	 */
	public void setInverse(final IInvertAble inv) {} //
	
	/**Returns true, when this Class can operate on Arguments of this Type
	 * This Function makes sense at this Level,
	 * because here there is always the Alternative for the Algebra
	 * to delegate the Operation to the inner Function,
	 * but to operate on the Functions and operate the Results on evaluation.	 */
	public boolean canProcess(final Object arg) {
		if (arg instanceof Permutation)
			return true;
		if (arg instanceof ByRefInt)
			return true;
		if (arg instanceof Integer)
			return true;
		return false;
	}
	
	/**Returns an alternative Representation that is 'simplified'
	 * Here it is shortened, the only Way of simplifying a Permutation.
	 */
	public IFunction simplify() {
		mDim = CANONICAL_LENGTH(a, mDim + 1) - 1;
		return this;
	}
	
	/**Shortens the Permutation by reducing the Degree while a[i]=i
	 * This is necessary on all Operations:
	 * Addition, Subtraction, Multiplication and Division.
	 * Notation specific, whether the identical Permutation is () or (0)
	 */
	//	public Permutation shortenAt() { mDim = shortenAt(a, mDim+1)-1; return this; }

	/**Shortens the Permutation by reducing the Degree while a[i]=i
	 * This is necessary on all Operations:
	 * Addition, Subtraction, Multiplication and Division.
	 * Notation specific, whether the identical Permutation is () or (0)
	 * Design Decisions:
	 * since shortening is an equivalence Operation, copying is done AFTER it.
	 * Rarely used!
	 */
	//	public Permutation shorten() { return (Permutation) shortenAt().copy(); }

	//////////////////////////
	//  Interface swapAble  //
	//////////////////////////

	/**Permutation: *		*/
	public SwapAble permute(final SwapAble arg) {
		return permuteAt((SwapAble) arg.copy()); }

	/**Permutation in Place: *=
	 * Quite ineffective,
	 * because Move Operations are implemented as concatenated Swaps
	 */
	public SwapAble permuteAt(final SwapAble arg) { //The Permutation Operation is not commutable
		Permutation tmp = (Permutation) copy();
		int i = -1;
		while (++i <= mDim)
			if (tmp.a[i] < i) { //The Permutation has to be swapped also!,
				arg.swapAt(i, a[i]); //but all this can be done in Place!
				tmp.swapAt(i, a[i]);
			} //But of course this is only true, if the Permutation is a valid one!
		return arg;
	}

	//////////////////////////
	//	Replication Object	
	//////////////////////////

	/**Compares two Objects for equality.
	 * <p>
	 * The <code>equals</code> method implements an equivalence relation:
	 * <ul>
	 * <li>It is <i>reflexive</i>: for any reference Value <code>x</code>,
	 * <code>x.equals(x)</code> should return <code>true</code>.
	 * <li>It is <i>symmetric</i>: for any reference values <code>x</code> and
	 * <code>y</code>, <code>x.equals(y)</code> should return
	 * <code>true</code> if and only if <code>y.equals(x)</code> returns
	 * <code>true</code>.
	 * <li>It is <i>transitive</i>: for any reference values <code>x</code>,
	 * <code>y</code>, and <code>z</code>, if <code>x.equals(y)</code>
	 * returns  <code>true</code> and <code>y.equals(z)</code> returns
	 * <code>true</code>, then <code>x.equals(z)</code> should return
	 * <code>true</code>.
	 * <li>It is <i>consistent</i>: for any reference values <code>x</code>
	 * and <code>y</code>, multiple invocations of <code>x.equals(y)</code>
	 * consistently return <code>true</code> or consistently return
	 * <code>false</code>.
	 * <li>For any reference Value <code>x</code>, <code>x.equals(null)</code>
	 * should return <code>false</code>.
	 * </ul>
	 * <p>
	 * The equals method for class <code>Object</code> implements the most
	 * discriminating possible equivalence relation on objects; that is,
	 * for any reference values <code>x</code> and <code>y</code>, this
	 * method returns <code>true</code> if and only if <code>x</code> and
	 * <code>y</code> refer to the same object (<code>x==y</code> has the
	 * Value <code>true</code>).
	 *
	 * @param   obj   the reference object with which to compare.
	 * @return  <code>true</code> if this object is the same as the obj
	 * argument; <code>false</code> otherwise.
	 * @see	 java.lang.Boolean#hashCode()
	 * @see	 java.util.Hashtable
	 * @since   JDK1.0	 */
	public boolean equals(final Object arg) {
		return equals((Permutation) arg); 
	}
	/**Compares two Objects for equality.
	 * <p>
	 * The <code>equals</code> method implements an equivalence relation:
	 * <ul>
	 * <li>It is <i>reflexive</i>: for any reference Value <code>x</code>,
	 * <code>x.equals(x)</code> should return <code>true</code>.
	 * <li>It is <i>symmetric</i>: for any reference values <code>x</code> and
	 * <code>y</code>, <code>x.equals(y)</code> should return
	 * <code>true</code> if and only if <code>y.equals(x)</code> returns
	 * <code>true</code>.
	 * <li>It is <i>transitive</i>: for any reference values <code>x</code>,
	 * <code>y</code>, and <code>z</code>, if <code>x.equals(y)</code>
	 * returns  <code>true</code> and <code>y.equals(z)</code> returns
	 * <code>true</code>, then <code>x.equals(z)</code> should return
	 * <code>true</code>.
	 * <li>It is <i>consistent</i>: for any reference values <code>x</code>
	 * and <code>y</code>, multiple invocations of <code>x.equals(y)</code>
	 * consistently return <code>true</code> or consistently return
	 * <code>false</code>.
	 * <li>For any reference Value <code>x</code>, <code>x.equals(null)</code>
	 * should return <code>false</code>.
	 * </ul>
	 * <p>
	 * The equals method for class <code>Object</code> implements the most
	 * discriminating possible equivalence relation on objects; that is,
	 * for any reference values <code>x</code> and <code>y</code>, this
	 * method returns <code>true</code> if and only if <code>x</code> and
	 * <code>y</code> refer to the same object (<code>x==y</code> has the
	 * Value <code>true</code>).
	 *
	 * @param   obj   the reference object with which to compare.
	 * @return  <code>true</code> if this object is the same as the obj
	 * argument; <code>false</code> otherwise.
	 * @see	 java.lang.Boolean#hashCode()
	 * @see	 java.util.Hashtable
	 * @since   JDK1.0	 */
	public boolean equals(final Permutation _arg) {
		//if both Permutations are shortened,
		//their Grad must match!
		//If not, the larger one must be identical Mapping in the upper components.
		if (bolLazySimplify)
			_arg.simplify();
		simplify();
		if (mDim != _arg.mDim) {
			return false; } 
		for(int i = mDim+1; --i >= 0; ) {
			if (a[i] != _arg.a[i]) {
				return false; } 
		}
		return true;
	}

	/**Returns a hash code Value for the object. This method is
	 * supported for the benefit of hashtables such as those provided by
	 * <code>java.util.Hashtable</code>.
	 * <p>
	 * The general contract of <code>hashCode</code> is:
	 * <ul>
	 * <li>Whenever it is invoked on the same object more than once during
	 * an execution of a Java application, the <code>hashCode</code> method
	 * must consistently return the same integer. This integer need not
	 * remain consistent from one execution of an application to another
	 * execution of the same application.
	 * <li>If two objects are equal according to the <code>equals</code>
	 * method, then calling the <code>hashCode</code> method on each of the
	 * two objects must produce the same integer result.
	 * </ul>
	 *
	 * @return  a hash code Value for this object.
	 * @see	 java.lang.Object#equals(java.lang.Object)
	 * @see	 java.util.Hashtable
	 * @since   JDK1.0	 */
	public int hashCode() {
		return (int) PermID();
		/*		int Value = 0;
				for (int i = -1; ++i <= mDim;)
					Value += a[i] >> 1;
				return Value;
		*/
	}

	/**Returns a string representation of the object. In general, the
	 * <code>toString</code> method returns a string that
	 * "textually represents" this object. The result should
	 * be a concise but informative representation that is easy for a
	 * person to read.
	 * It is recommendedthat all subclasses override this method.
	 * <p>
	 * The <code>toString</code> method for class <code>Object</code>
	 * returns a string consisting of the name of the class of which the
	 * object is an instance, the at-sign character `<code>@</code>', and
	 * the unsigned hexadecimal representation of the hash code of the
	 * object.
	 *
	 * @return  a string representation of the object.
	 * @since   JDK1.0	 */
	public String toString() { return AStreamOut.ARRAY_TO_STRING(this.a, ","); }

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Components.	 */
	public ICopyAble copyAt(final Object arg, int depth) {
		Permutation P = (Permutation) arg;
		//		letGrad (P.getDim(), true, false);
		if (--depth < 0)
			a = P.a; //former shallowCopyAt
		else
			CopyArr(P.getDim(), P.a);
		return this;
	}

	/**Does a shallow Copy of the Argument.
	 * I.e. both Instances will share their inner Components.	 */
	public ICopyAble shallowCopyAt(Object arg) {
		Permutation P = (Permutation) arg;
		a = P.a;
		//		bolLazySimplify = P.bolLazySimplify; //static Variable
		mDim = P.mDim;
		CopyArr(P.getDim(), P.a);
		return this;
	}

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() { return new Permutation(this.mDim); }

	/**Returns a unique ID for this Permutation in the Range [1..n!]-1
	 * With 32/64 Bits possible for Degrees up to 12/20.
	 * This ID also reflects the Sign of the Permutation: Sign() = (-1)^PermID()
	 * So an even/odd ID denotes an even/odd Permutation.
	 * This ID is also used for the HashCode.
	 * The Schema is similar to the Evaluation of a Power Series,
	 * only that instead of F=x^n here we use F=n!
	 * and the Coefficients are given by the Size of the Swap
	 * The Schema corresponds to Permutations() Permutation (PermID) and PermID() 
	 */
	public long PermID() {
		long ret = 0;
		long factor = 1; //Factor, 'Value' of Swaps, multiplies with the Counter
		/*		for (int Z1 = 0; ++Z1 <= mDim;)
				{	//This Operation is not destructive, the Result is unique, but not intellegible!
					int Z2 = -1, Z3 = 0;
					F *= Z1; //Fakult�ten! Wertigkeit der Vertauschungen
					while (a[++Z2] != Z1);	//Search for Z1 in the Array
					while (++Z2 <= mDim) if (a[Z2] < Z1) Z3++;	//Count the number of Inversions
					Zaehler += F*Z3;	//multiply it by the Value of the Inversion
				}
		*/ //This Algorithm undoes the Swaps performed during the Creation of this Permutation.
		//A Criterium for determining what to undo is that at each level of the Tree
		//the Items to be permuted are in strict monotonous order at the very left Item in this Branch
		//So first the Permutations that have led from there have to be undone
		//Then you can walk up one Level.
		final Permutation P = (Permutation) copy();
		//This Operation is destructive, so make a Copy!
		for (int Z1 = 0; ++Z1 <= mDim;) {
			factor *= Z1; //Factor grows on each iteration,
			int Z2 = Z1;
			while (--Z2 >= 0)
				if (P.a[Z2] <= P.a[Z1])
					break; //search an Item less than a[Z1] in the Array
			final int swapSize = Z1 - (++Z2); 
			if (swapSize > 0) { //undo the Swap
				ret += factor * swapSize; //The Size of the Swap determines the Coefficient
				final int tmp = P.a[Z1];
				System.arraycopy(P.a, Z2, P.a, Z2 + 1, swapSize);
				P.a[Z2] = tmp;
			} //Inverse swap compared to Generation (see Construuctor() or Permutations()!
			//			while (++Z2 < Z1) { //do pair-wise Swapping, very slow!
			//				int tmp = P.a[Z2]; P.a[Z2] = P.a[Z1]; P.a[Z1] = tmp;}	//undo the Swap partially (not necessary to do the full swap)
		}
		//This Algorithm is for a modified Generation Scheme, where a Swap is undone each time.
		/*Permutation P = (Permutation)copy();	//This Operation is destructive
		int Z1 = mDim;		//it starts from the End, because this has the most Effect on the Result
		Zaehler = 0;	//(Z1 - P.a[Z1]);	//saves 1 Multiplication with 1
		while (Z1 > 0) {
			if (Z1 != P.a[Z1]) {
				Zaehler += (Z1 - P.a[Z1]);
				int Z2 = Z1; while (P.a[--Z2] != Z1);	//search for Z1 in the Array
				P.a[Z2] = P.a[Z1];	//undo the Swap partially	(not necessary to do the full swap)
			} Zaehler *= Z1--;
		} */
		final long i = (ret >> 1) & 1; //invert the 0th Bit with the 1st
		return ret ^ i;
	}

	/**Liefert das Vorzeichern der Permutation,d.h. +1 bei gerader,
	 * -1 bei ungerader und 0 bei der identischen Permutation.
	 * Dieses Vorzeichen wird durch Z�hlen der notwendigen
	 * Paarvertauschungen ermittelt.
	 * Dies ist schneller als die Ermittlung von PermID.	
	 * Das Vorzeichen multipliziert sich bei Verkettungen von Permutationen, 
	 * da sich die Anzahl der Vertauschungen addiert.  
	 */
	public int getSign() { //
		int sign = 1;
		final Permutation P = (Permutation) copy(); //Destructive Operation
		for (int i = -1; ++i <= mDim;) {
			if (P.a[i] == i) {
				continue; }
			//search for the item with Value Z1, swap it with this item and flip the Sign.
			for (int j = i; ++j <= mDim; ) {//not necessary to do the full swap,
				if (P.a[j] == i) {
					P.a[j] = P.a[i];
					sign = -sign;
					break;
				}
			}
		}
		return sign;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	//  Solution for creating the Arrays of all Permutations, Combinations or Variations
	//  To calculate Combinations with repeated Elements, see RandomPseudoVector. 
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * The Permutations are returned in the Array A whose actual P 
	 */
	/**
	 * Recursive Method to fill the Array A with Permutations of Pe
	 * where only the first m Elements permute.
	 * You can choose to either receive the Permutations or Combinations
	 * 
	 * Design Decisions:
	 * Instead of using a private static Array of Permutations and a Counter
	 * to build the Array, I rather hand over the Array directly.
	 * This allows for multithreaded Generation of Permutations.
	 * 
	 * 
	 * @param out Store to collect the Returned Permutations 
	 * @param itemCount the starting Position in the Output Permutation
	 * @param in the initial Permutation, to be permuted further
	 * @param m the Number of Positions left to be permuted.
	 * @param k the Number of Items to be selected = Recursion depth
	 * @param unordered determines, whether only ordered Elements are searched
	 * @return the Number of generated Permutations 
	 */
	private static final int permute(
		final SwapAble[] out,
		int itemCount,
		final SwapAble in,
		final int m,
		final int k,
		final boolean unordered) { //Create new Permutations
		int l = k - 1;
		int n = m - 1;
		SwapAble copy = (SwapAble) in.copy();
		//Create Copies, so the Original is not affected.
		if ((k == 0) && (itemCount == 0)) {
			out[0] = copy;
			return itemCount;
		}
		final boolean combi = (m != k);
		//int tmp = 0;	//P.a[m];
		for (int j = m; --j >= -1;) {
			//P.a[m] = P.a[Z]; P.a[Z] = tmp;	//Do the partial Swap. This Scheme is easier to decode in the PermID, ...
			if (l == 0) { //Save one recursive Call
				//copy the Permutation into it's designated Location
				if (combi) {
					if (unordered || (copy.ordered(m))) {
						out[itemCount++] = copy;
						//inOrder Sequence, give up alternating Sign
						//n = P.getDim();	//cannot decrease the Degree, but that can be done later!
						//for (int i = m, j = 0; i <= n; ++i, ++j) P.swapAt(i,j);
					} //no swapping necessary!
				} else { //in favor of consistent Size Ordering...
					final int i = (itemCount >> 1) & 1;
					//flip the last two bits to ensure flipping of the Sign!
					out[itemCount++ ^ i] = copy;
					//changing Order to account for flipping Sign.
				}
			} else
				itemCount = permute(out, itemCount, copy, n, l, unordered);
			//Do the other Permutations
			if (j >= 0) { //Swap places m and j and create new permutations
				if (combi)
					copy = (SwapAble) in.swap(j, m);
				//use the original Permutation
				else
					copy = (SwapAble) copy.swap(j, m);
				//this makes it more complicated, ...
			} //... but this Scheme creates Signs in Pairs
		}
		return itemCount;
	}
	
	/**Generates all Permutations of n Elements and saves into an Array:
	 * a[	0] is the identical Permutation
	 * a[n!-1] is the reverse	Permutation
	 * with growing Index the Permutation involves more and more
	 * Elements with higher Indexes.
	 * The Index of the Permutation is PermID();
	 * The Schema corresponds to Permutations() Permutation (PermID) and PermID() 
	 * The Index indicates also the Sign of the Permutation!
	 * 
	 * Odd Elements of the Result have negative Sign: a[i].Sign() = (-1)^i.
	 * Normally the Items would be returned "inOrder" from the recursive Tree,
	 * but since the Sign doesn't flip then, the 0Bit is XORed with the 1Bit.
	 */
	public static Permutation[] Permutations(final SwapAble p) {
		final byte grad = (byte) p.getDim();
		final int faculty = (int) ByRefLong.fact((byte)(1+grad));
		//n = 8 => 64 kByte Segment- Grenze
		final Permutation[] ret = new Permutation[faculty];
		permute(ret, 0, p, grad, grad, true);
		return ret;
	}
	
	/**Generates all Combinations 
	 * (without Sequence) of k Elements out of n
	 * (without recurrence) and saves them into Arrays indexed by k.
	 * 
	 * If the Sequence of the Permutations does not matter, 
	 * it is much easier to use the Bits set in an Integer Number 
	 * to switch individual Values on or off.  
	 * @see #Combinations(int, int) for Details
	 */
	final static public Permutation[][] Combinations(final int n) {
		final Permutation[][] ret = new Permutation[n+1][]; 
		for(int i = (ret.length+1) / 2; --i >= 0;) //save half of the Combinations
			ret[i] = ret[n-i] = Combinations(n, i);
		return ret;
	}
	
	/**Generates all Combinations 
	 * (without Sequence) of k Elements out of n
	 * (without recurrence) and saves them into an Array:
	 * 
	 * All Array Elements are still valid Permutations. 
	 * The Selection takes place in the last k Elements for the first Half
	 * and in the first n-k Elements for the last Half. 
	 * 
	 * The first part of the Permutations form the Remainder, 
	 * which also is a Selection without Sequence and Recurrence, 
	 * so it can be used as well. In fact, when k > n/2, 
	 * the actual selection is in the first part (which saves time). 
	 * 
	 */
	public static Permutation[] Combinations(final int n, final int k) {
		return Combinations(new Permutation(n-1), k); 
	}
	
	/**Generates all Combinations 
	 * (without Sequence) of k Elements out of n
	 * (without recurrence) and saves them into an Array:
	 * 
	 * All Array Elements are still valid Permutations. 
	 * The Selection takes place in the last k Elements. 
	 * 
	 * The first part of the Permutations form the Remainder, 
	 * which also is a Selection without Sequence and Recurrence, 
	 * so it can be used as well. In fact, when k > n/2, 
	 * the actual selection is in the first part (which saves time). 
	 * 
	 */
	public static Permutation[] Combinations(final SwapAble p, int k) {
		final int n = p.getDim()+1; 
		final int k2 = n - k; 
		if (k > k2) 
			k = k2; 
		final int numCombi = (int) ByRefLong.Combination(n, k);
		//n = 8 => 64 kByte Segment- Grenze
		final Permutation[] ret = new Permutation[numCombi];
		permute(ret, 0, p, n-1, k, false);
		return ret;
	}
	
	/**Generates ALL Permutations of n Elements 
	 * and saves them into an Array:
	 *
	 * This is equivalent to generating the Variations of ALL Elements
	 * All Array Elements are still valid Permutations.	 */
	final static public Permutation[] Permutations(final int grad) {
		return Variations(new Permutation(grad), grad, grad); }
	
	/**Generates all Variations (i.e. Selections 
	 * WITH considering Sequence but 
	 * WITHOUT Duplicates) 
	 * of k Elements out of n
	 * and saves the Permutations into an Array:
	 *
	 * The Selection takes place in the last k Elements.
	 * All Array Elements are still valid Permutations.	 */
	final static public Permutation[] Variations(final int grad, final int k) {
		return Variations(new Permutation(grad), grad, k); }

	/**Generates all Variations (i.e. Selections without Duplicates) 
	 * of k Elements out of n
	 * and saves the Permutations into an Array:
	 *
	 * The Selection takes place in the last k Elements.
	 * All Array Elements are still valid Permutations.	 */
	final static public Permutation[] Variations(final SwapAble P, int k) {
		return Variations(P, P.getDim(), k); 
	}
	
	/**Generates all Variations (i.e. Selections without Duplicates) 
	 * of k Elements out of n
	 * and saves the Permutations into an Array:
	 *
	 * The Selection takes place in the last k Elements.
	 * All Array Elements are still valid Permutations.	 */
	private static final Permutation[] Variations(SwapAble p, final int grad, int k) {
		if (k > grad)
			k = grad;
		//selecting n+1 of n+1 creates the same List as selecting n of n+1. Selecting more, makes no sense
		final int numVariations = (int) ByRefLong.Variation(grad + 1, k);
		//n = 8 => 64 kByte Segment- Grenze
		final Permutation[] ret = new Permutation[numVariations];
		if (p == null) 
			p = new Permutation(grad); 
		permute(ret, 0, p, grad, k, true);
		return ret;
	}
	
	//////////////////////
	//	Interface Set	//
	//////////////////////

	//This Implementation is also useful for the Implementation
	//of Set Operations on Collection Classes.

	//The Set Operations are defined elementwise,
	//originating from the basic Set operations get(), set() and clear()

	//keep the set ordered makes retrieving faster, but inserting slower
	//since you typically retrieve more than insert this is a good strategy.

	/**Clears the Entry n in a Set of integer Numbers	 */
	public void clear(final int n) {
		//This Type of Implementation could even return whether the Item was found or not!
		for (int i = -1; ++i <= mDim;)
			if (a[i] == n) {
				System.arraycopy(a, i + 1, a, i, mDim - i);
				mDim--;
				return;
			}
	}

	/**Sets the Entry n	in a Set of integer Numbers	 */
	public void set(final int n) { //If not in the set yet...
		if (!get(n)) { //...add it to the Set
			letGrad(mDim + 1, true, false); //makes inserting faster,
			a[mDim] = n; //but retrieving slower
		}
	}

	/**Gets the Entry n	from a Set of integer Numbers */
	public boolean get(final int n) {
		for (int i = -1; ++i <= mDim;)
			if (a[i] == n)
				return true;
		return false;
	}

	//////////////////////
	//	Interface Boole	//
	//////////////////////

	/**Boolean AND Operation in Place: &=, &&= for single Bit
	 * a AND b = true <=> (a = true) AND (b = true) 	 */
	public Lattice ANDat(Object arg) {
		Permutation arg_ = (Permutation) arg;
		for (int i = -1; ++i <= mDim;)
			if (!arg_.get(a[i]))
				clear(a[i]);
		return this;
	}

	/**Boolean OR Operation in Place: |=, ||= for single Bit
	 * a OR b = true <=> (a = true) OR (b = true) 	 */
	public Lattice ORat(Object arg) {
		Integer Item;
		//		ByRefLong available = new ByRefLong();
		SetInteger arg_ = (SetInteger) arg;
		IIStreamIn iter = arg_.Iterator();
		while ((Item = (Integer) iter.nextItem()) != IIStreamIn.EOI)
			//Add the nonexistent items to the current Set
			//			if (! get(Item.getInt()))	//Don't need to test, because a Set has no duplicates.
			set(Item.intValue());
		return this;
	}

	/**Boolean Constant for the Representation of 'false': =0
	 * Sets this Object to False, i.e. not 'true';
	 * with Vectors it sets all Elements to their respective Value of False*/
	public Boole FalseAt() {
		mDim = -1;
		return this;
	} //Empty Set

	/**Boolean NOT Operation in Place: ~=, != for single Bit
	 * NOT a = true <=> (a = false)
	 * This Operation cannot be implemented by infinite Sets,
	 * Therefore you need other means to define some Operations.	 */
	public Boole NOTat() {
		throw new AbstractMethodError();
	}

	//Delegation

	/**Boolean XOR Operation in Place: ^=
	 * a XOR b = true <=> ((a = true) AND (b = false)) OR ((a = false) AND (b = true))*/
	public Lattice XORat(Object arg) {
		return sSet.XORat(arg);
	}

	/**Boolean DIFF Operation in Place: -=
	 * a - b <=> (a AND NOT b) <=> NOT IMP
	 * For Sets:	Difference Set*/
	public Lattice DIFFat(Object arg) {
		return sSet.DIFFat(arg);
	}

	/**Boolean IMP Operation in Place: =>
	 * a IMP b = true <=> (a = true) => (b = true)*/
	public Boole IMPat(Object arg) {
		return sSet.IMPat(arg);
	}

	/**Boolean EQV Operation in Place: <=>
	 * a EQV b = true <=> (a = b)*/
	public Boole EQVat(Object arg) {
		return sSet.EQVat(arg);
	}

	/**Boolean Constant for the Representation of 'true': 1	*/
	public Boole TrueAt() {
		return sSet.TrueAt();
	}

	/**Boolean AND Operation: &, && for single Bit	*/
	public Lattice AND(Object arg) {
		return sSet.AND(arg);
	}

	/**Boolean OR Operation: |, || for single Bit	*/
	public Lattice OR(Object arg) {
		return sSet.OR(arg);
	}

	/**Boolean NOT Operation: ~, ! for single Bit	*/
	public Boole NOT() {
		return sSet.NOT();
	}

	/**Boolean XOR Operation: ^		*/
	public Lattice XOR(Object arg) {
		return sSet.XOR(arg);
	}

	/**Boolean DIFF Operation: -
	 * For Sets:	Difference Set*/
	public Lattice DIFF(Object arg) {
		return sSet.DIFF(arg);
	}

	/**Boolean IMP Operation: =>	*/
	public Boole IMP(Object arg) {
		return sSet.IMP(arg);
	}

	/**Boolean EQV Operation: <=>	*/
	public Boole EQV(Object arg) {
		return sSet.EQV(arg);
	}

	/**Boolean Constant for the Representation of 'true': 1		*/
	public Boole True() {
		return sSet.True();
	}

	/**Boolean Constant for the Representation of 'false': 0	*/
	public Boole False() {
		return sSet.False();
	}

	/**Returns true, when 'this' is False, or an empty Set	 */
	public boolean isFalse() {
		return mDim == -1;
	}

	/**Returns true, when 'this' is True, or a full Set	 */
	public boolean isTrue() {
		return sSet.isTrue();
	}

	/**Determines, whether 'this' is less than or a SubSet of arg	*/
	public boolean SubEq(Object arg) {
		return sSet.SubEq(arg);
	}

	/**Determines, whether 'this' is more than or a SuperSet of arg	*/
	public boolean SuperEq(Object arg) {
		return sSet.SuperEq(arg);
	}

	/**Determines, whether 'this' is less than or a real SubSet of arg	*/
	public boolean Sub(Object arg) {
		return sSet.Sub(arg);
	}

	/**Determines, whether 'this' is more than or a real SuperSet of arg	*/
	public boolean Super(Object arg) {
		return sSet.Super(arg);
	}

	//////////////////////////
	//	Interface swapAble	//
	//////////////////////////

	/**Swaps the Elements i and j of the Permutation in Place.	 */
	public SwapAble swapAt(int i, int j) {
		int tmp = a[i];
		a[i] = a[j];
		a[j] = tmp;
		return this;
	}

	/**Swaps the Elements i and j of the Permutation.	 */
	public SwapAble swap(int i, int j) {
		return ((SwapAble) copy()).swapAt(i, j);
	}

	/**Returns true, when the Items in the Container are ordered
	 * from the i-th Item on	 */
	public boolean ordered(int i) {
		while (++i <= mDim)
			if (a[i - 1] > a[i])
				return false;
		return true;
	}

	/**Returns an Iterator of the components in this Container.
	 *
	 * @return  an Iterator of the components in this Container.
	 * @see	 Math.Iterator
	 */
	public IIStreamIn Iterator() {
		return new PermutationIterator(this);
	}

	//////////////////////////////
	//	Multi-Index-Funktionen	//
	//////////////////////////////

	//Multi_Pow is defined in Vector.Tensor

	/**Sums the absolute values of this multi-index's own coefficients.
	 * @return the Sum of all absolute Values of the Indices = Sum (|n [i]|)	 */
	public long Multi_ABS() {
		return Multi_ABS(a, mDim + 1);
	}

	/**Sums the absolute values of the coefficients in the given array.
	 * @return the Sum of all absolute Values of the Indices = Sum (|n [i]|)	 */
	public static long Multi_ABS(int[] a, int length) {
		long sum = 0;
		while (--length >= 0) {
			sum += Math.abs(a[length]); } 
		return sum;
	}

	/**Computes the product of the faculties of this multi-index's own coefficients.
	 * @return the Product of all Faculties of the Indices = Prod( n [i]!)
	  * The Carry Element is used for the Base. 	 */
	// TODO: LOGIC: parameter 'p' is never read - the method always computes the product from this instance's own array 'a', ignoring the "Carry Element used for the Base" documented in the @return comment, so a caller passing a different Carry/base permutation gets a result computed from the wrong operand.
	public long Multi_Fact(final Permutation p) {
		long ret = 1;
		for (int i = mDim+1; --i >= 0; ) {
			ret *= ByRefLong.fact((byte)a[i]); }
		return ret;
	}

	/*
	FUNCTION iCRC (CRC : Word;P : P_Byte_Feld;len : Word;jinit,jrev : Integer) :Word;
		 berechnet einen Cyclic Redundancy Check fuer P.
	
	FUNCTION iCRC1 (CRC : Word;OneCh : Byte) : Word;
		 {entspricht CRC (,,1,-1,1), ist aber langsamer und wird nur zur
		  Initialisierung verwendet}
	VAR i : Integer;
	 ans : Word;
	
	BEGIN
	ans:=CRC XOR (OneCh SHL 8);
	FOR i:=0 TO 7 DO
	IF (ans AND $8000) <> 0
	THEN
	BEGIN
	 ans:=(ans SHL 1) XOR 4192; {4192 hat die Bits des erzeugenden Polynoms}
	END
	ELSE ans:=ans SHL 1;
	iCRC1:=ans;
	END;
	
	FUNCTION iCRC (CRC : Word;P : P_Byte_Feld;len : Word;jinit,jrev : Integer) :Word;
	
	VAR  iCrcTb : ARRAY [0..MaxByte] OF Word;
	  rChr : ARRAY [0..MaxByte] OF Byte;
	  j,cWord : Word;
	  B1 : Byte;
	
	CONST init : Word = 0;
	
	CONST it : ARRAY [0..15] OF Byte = (0,8,4,12,2,10,6,14,1,9,5,13,3,11,7,15);
			{Tabelle der Bit-Reversen von 0..15 (4 Bit)}
	BEGIN
	cWord:=CRC;
	IF init = 0 THEN
	BEGIN
	init:=1;
	FOR j:=0 TO MaxByte DO {2 Tabellen aufbauen : CRC's aller Zeichen und Bit-Reverse aller Zeichen}
	BEGIN
	 iCrcTb [j]:=icrc1 (j SHL 8,0);
	 rChr[j]:=(it[j AND $F] SHL 4) OR (it[j SHR 4]); {8Bit-Reverse auf vertauschte 4Bit-Reverse zurueckfuehren}
	END;
	END;
	IF (jInit >= 0)
	THEN cWord:=Lo (jInit) OR (jInit SHL 8) {Reste-Register initialisieren}
	ELSE IF (jrev < 0) THEN cWord:=rChr [Hi (cWord)] OR (rChr [Lo (cWord)] SHL 8);
	{falls keine Initialisierung, Register umkehren ?}
	FOR j:=1 TO len DO
	BEGIN
	IF jRev < 0
	THEN B1:=rChr [P^[j]]
	ELSE B1:=	  P^[j];
	cWord:=iCrcTb [B1 XOR Hi (cWord)] XOR (Lo (cWord) SHL 8)
	END;
	IF jRev >= 0
	THEN iCRC:=cWord
	ELSE iCRC:=rChr[Hi (cWord)] OR (rChr[Lo (cWord)] SHL 8) {Umkehrung}
	END;
	
	PROCEDURE Tempern;
	
	LABEL 1,2,9;
	
	VAR P2 : Permutation;
	dE : Real;
	nOver,nLimit,nSucc,j,k,Gr : Word;
	
	{Entscheidet,ob eine Umordnung mit Energieaufwand dE gemacht wird.
	Ist dE < 0,dann wird immer zugestimmt,sonst nur mit einer Wahrscheinlichkeit
	Exp (-dE/t) mit der 'Temperatur' t.}
	
	BEGIN  {Eigentliches 'Ausglueh'-Programm}
	Gr:=P1.Grad SHL 1;
	P2.Grad:=P1.Grad;GetMem (P2.a,Gr);
	nOver  :=P1.Grad SHL 7; {Maximale Anzahl von ausprobierten Pfaden bei T}
	nLimit :=P1.Grad SHL 3; {Maximale Anzahl von erfolgreichen Pfaden bei T}
	{IF Ze_Zeiger (@Annehmen)^ <> NIL THEN Annehmen (P1,P2); {evtl. Gr��e initialisieren}
	IF Negativ (T) THEN {Bereich der Energie abschaetzen}
	BEGIN
	dE:=Null;
	FOR j:=1 TO nLimit DO
	BEGIN
	 Kopiere (P1.a,P2.a,Gr);
	 FOR k:=1 TO P1.Grad DO
	  Tausche (@P2.a^[k],@P2.a^[Succ (Random (P1.Grad))],SizeOf (Word));
	 dE:=dE+ABS (Kosten (P2))
	END;
	T:=dE/(nLimit SHR 1)	{2*typische Kosten fuer 8*n zufaellige Umordnungen}
	END;					 {2* => 7-mal mehr iterieren}
	FOR j:=1 TO CountDown DO {Bis zu CountDown Temperatur-Schritte unternehmen}
	BEGIN
	nSucc:=0;
	FOR k:=1 TO nOver DO {so oft bei konstanter Temperatur versuchen}
	BEGIN
	 Aendern (P1,P2);
	 dE:=Kosten (P2); {Kosten berechnen}
	 IF dE < T THEN   {Schwellwert-Tempern}
	{	IF Negativ (dE) OR (Random < Exp (-dE/T)) THEN   {falls bewilligt}
	  BEGIN
	   INC (nSucc);
	   IF P_Zeiger (@Annehmen)^ = NIL
	THEN Tausche (@P1.a,@P2.a,SizeOf (Pointer))
	ELSE Annehmen (P2,P1)
	  END; {umbauen}
	 IF nSucc >= nLimit THEN GOTO 2 {vorzeitiges Absenken weil genug ?nderungen}
	END;
	2: t:=t*tFaktor; {Abkuehlen,evtl ebenfalls durch eine Funktion ersetzen}
	IF nSucc = 0 THEN GOTO 9 {fertig,wenn keine Erfolge mehr}
	END;
	Fehler:=TRUE; {es waren noch mehr Minimierungen m�glich!}
	9:FreeMem (P2.a,Gr)
	END;
	
	 */

	//////////////
	//	Testing	//
	//////////////

	/**Testing this class	 */
	public static void testPermutation() {
		//	public static void testIt() throws java.io.IOException {
		final Permutation[] tupel = Permutation.Permutations(new Permutation(3));
		L.n("All Permutations of Elements 0..3:");
		for (int i = tupel.length; --i >= 0;) {
			L.n(" Index: ").l(i
			).l(" Wert: ").l(tupel[i]
			).l(" ID: ").l(((Permutation) tupel[i]).PermID()
			).l(" Sign: ").l(((Permutation) tupel[i]).getSign()
			).l(" Perm(i): ").l(new Permutation((byte)5, i).simplify());
		}
		int kl = 2;
		int kn = 4;
		L.n("All Variations of 2 Elements out of 0..4:");
		Permutation[] Vari = Permutation.Variations(new Permutation(kn), kl);
		for (int i = -1; ++i < Vari.length;)
			L.n(" Index: ").l(i
			).l(" Wert: ").l(Vari[i]
			).l(" Sign: ").l(((Permutation) Vari[i]).getSign());
		L.n("All Combinations of 2 Elements out of 0..4:");
		Permutation[] Combi = Permutation.Combinations(new Permutation(kn), kl);
		for (int i = -1; ++i < Combi.length;)
			L.n(" Index: ").l(i
			).l(" Wert: ").l(Combi[i]
			).l(" Sign: ").l(((Permutation) Combi[i]).getSign());
		L.n("Test the Multiplication of Permutations:");
		for (int i = 1; ++i < 10;) { //Test the Multiplication of Permutations
			int j = (int) (Math.random() * tupel.length);
			//choose a Permutation
			int k = (int) (Math.random() * tupel.length);
			Permutation tmp = (Permutation) ((Permutation) tupel[j]).map(tupel[k]);
			final int sign_j = ((Permutation) tupel[j]).getSign();
			final int sign_k = ((Permutation) tupel[k]).getSign();
			final int signtmp= tmp.getSign();
			L.n().l(j).l(tupel[j]).l(" * ").l(k).l(tupel[k]).l(" = ").l(tmp.PermID()).l(tmp
			).l(" Signs: ").l(sign_j).l(" * ").l(sign_k).l(" = ").l(signtmp);
			Assert.EQUALS(signtmp, sign_j*sign_k);
		}
		L.n("Test the Inversion of Permutations:");
		for (int i = 1; ++i < 10;) { //Test the Inversion of Permutations
			int j = (int) (Math.random() * tupel.length);
			//choose a Permutation
			L.n().l(j).l(tupel[j]).l(" = ").l(((Permutation) tupel[j]).rev());
			L.n(" P�!P = ").l(tupel[j].map((Permutation) tupel[j].rev()));
			L.n().l(j).l(tupel[j]).l("; !P�P = ").l(((IMonoid) tupel[j]).pam(tupel[j]));
			L.n().l(j).l(tupel[j]).l("; P�!P = ").l(((IMonoid) tupel[j]).solve(tupel[j]));
		}
		L.n("Test the Division of Permutations:");
		for (int i = 1; ++i < 10;) { //Test the Division of Permutations
			int j = (int) (Math.random() * tupel.length);
			//choose a Permutation
			int k = (int) (Math.random() * tupel.length);
			//			j =8; k=1;
			L.n().l(j).l(": J=").l(tupel[j]);
			L.n().l(k).l(": K=").l(tupel[k]);
			L.n(" (!K�K)�J = J = ").l((((IMonoid) tupel[k]).pam(tupel[k])).map(tupel[j]));
			L.n(" !K�(K�J) = J = ").l( ((IMonoid) tupel[k]).pam(tupel[k]  .map(tupel[j])));
			L.n(" (J�K)�!K = J = ").l((tupel[j].map(tupel[k])).solve(tupel[k]));
			L.n(" J�(K�!K) = J = ").l( tupel[j].map(tupel[k]  .solve(tupel[k])));
		}
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** test Calculation of the Gray Code from the binary Code.	 */
	private static final void testGray() {
		L.n("Testing Gray Coding");
		testGray(0);
		testGray(1);
		testGray(2);
		testGray(3);
		testGray(Integer.MAX_VALUE);
		//testGray(Integer.MIN_VALUE); //no negative Values
		//testGray(Long.MAX_VALUE);
		//testGray(Long.MIN_VALUE); //too large!
		final int nmin = 0;
		final int nmax = 312;
		int jp=(nmax-nmin)/11;
		if (jp < 1) {
			jp=1; } 
		L.n("Gray[n]").l("	Gray(Gray[n])").l("	Gray[n] ^ Gray[n+1]\n");
		for (int n=nmin; n<=nmax; n++) {
			final long ng=BIN2GRAY(n);
			final long nni=GRAY2BIN(ng);
			Assert.EQUALS(nni, n, "WRONG ! AT n="+n+"	ng="+ng+"	nni="+nni); 
			if (0 == ((n-nmin) % jp)) {
				final long nxor=ng ^ BIN2GRAY(n+1);
				L.n().l(n).l(ng).l(nni).l(nxor);
			}
		}
	}
	
	/** test Calculation of the Gray Code from the binary Code.	 */
	private static final void testGray(final long bin) {
		final long gray = GRAY2BIN(bin);
		L.n("Gray(").l(bin).l(")=").l(gray);
		Assert.EQUALS(bin, BIN2GRAY(gray)); 
	}
	
	/**Method to test all Implementations in this class.
	 * Relies on testStepper set to a concrete Implementation.
	 * Tests the Integration of ODEs
	 * @param testStepper Instance of the Stepper to be tested
	 */
	public static void testIt() {	//
		testGray(); 
		testPermutation();
	}
	
	/**
	 *The main entry point for the application.
	 * @param args Array of parameters passed to the application via the command line.
	 */
	public static void main(final String[] args) { //throws java.io.IOException {
		testIt();
	}
    
}

/**
 * Iterates through the indices of a {@link Permutation}, delegating position tracking
 * to the wrapped instance.
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:34:07Z
 * digest: a161b1166546bcdfeae795bcf43db60707c808385944a84360d40d6109417151
 * stale: false
 * tags: [code/iterator]
 * concepts: [Permutation]
 * facets: {layer: utility, status: broken, complexity: medium}
 * -->
 */
class PermutationIterator 
extends AStreamIn { //IndexIterator {
	
	//////////////////////////
	//	Interface iterAble	//
	//////////////////////////
	
	/**Current Position in the Container	 */
	int current;
	
	/**Reference to the Container	 */
	Permutation self;
	
	/**Constructor that takes the Container to iterate over	 */
	public PermutationIterator(final Permutation self_) { self = self_; }
	
	/**Restart the Iterator	 */
	public IReSetAble reSet() { current = 0; return this; }
	
	/** There is no Order in the Values of a Permutation.	  */
	public byte getOrder() { return ORDER_NONE; }

	/**Returns the maximum mark size, equal to the degree of the underlying permutation.
	 * @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return self.mDim; }

	/**Returns the current iteration position.
	 * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return current; }
	
	/**Returns the next Object, postIncremental.	 */
	public Object nextItem() { //ByRefLong available) {
		Object Item = currItem(); //available);
		++current;
		return Item;
	}
	//{++current; return currentItem();}	//preIncremental
	
	/**Returns true, while nextItem gives new Values.
	 * Watch out for the postIncrement: current has already the next Item!	 */
	public long availAble() { return self.mDim - current; }
	
	/**Returns the current Object.	 */
	public Object currItem() { //
		if (//(available.Value =
		 ((self.mDim - current)) > 0)
			return new Integer(self.a[current]);
		return IIStreamIn.EOI;
	} // null; }
	
	/**Removes the current Object from the Container with this Iterator knowing it.
	 * The remaining Problem is other Iterators that concurrently work through this. */
	public Object removeCurr() { //
		Object ret = new Integer(self.a[current]);
		self.a[current--] = 0;
		return ret;
	}
	
}
