package streamIO.copy.monoid;

//import Stream.XMLOutputStream;
import java.io.StreamTokenizer;
import java.util.Vector;

import streamIO.copy.ICopyAble;
import function.IFunction;
import function.IInvertAble;

/**
 * Monoid whose elements are {@code Object[]} arrays, concatenated the same way
 * {@link StringMonoid} concatenates {@code char[]}s.
 *
 * ArrayMonoid.java
 *
 * Created on 6. Mai 2001, 11:16
 *
 * @author  Matthias Heuer
 * @version
 *
 * Monoid working on Object[]s analog to StringMonoid working on char[].
 * Concatenation is is implemented as Object[] Concatenation.
 * A Reverse can be defined, but it would be the same for each Object[]
 * and consist of as many Delete Characters
 * as Characters in the original Object[].
 * But any Information on the original Object[] is lost.
 *
 * Any Vector is a Monoid in two ways:
 * It is always possible to define adding and removing Elements at the End
 * as a Mapping, because it is not commutable, but associative.
 * When it's Elements can be considered as Numbers / Indices
 * additionally the Permutation Interpretation is possible.
 *
 * With Respect to this the whole Container / Iterator / streamIO Family can be
 * integrated by a Bridge Class or by implementing this Interface directly.
 * Even simple Stacks can be used to implement this Interface.
 * Reversibility is especially important for Editors and Version Management.
 * Another Serialization Schema could create normal diff Files.
 *
 * Replacing and Undo can either be applied to single Characters
 * as well as to Lines, Objects in a Container etc.
 * It is not possible to use Inverses with Streams,
 * because the Backstep doesn't work properly.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:41:38Z
 * digest: 9b321a96e2aed71fc4863e756efd19dcb7a89acb76d78a63215c0eb739c43262
 * stale: false
 * tags: [code/concatenation, code/array_manipulation]
 * concepts: [Monoid, String/Array Concatenation]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class ArrayMonoid
extends AMonoid {

	//////////////////////
	//	static Methods	//
	//////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Conversion Routine 	  */
	public static char[][] Strings2chars(String[] arg) {
		int i = arg.length;
		char[][] ret = new char[i][];
		while (--i >= 0)
			ret[i] = arg[i].toCharArray();
		return ret; }

	/** Conversion Routine 	  */
	public static Character[] char2Character(char[] arg) {
		int i = arg.length;
		Character[] ret = new Character[i];
		while (--i >= 0)
			ret[i] = new Character(arg[i]);
		return ret; }

	/** Conversion Routine 	  */
	public static Character[][] chars2Characters(char[][] arg) {
		int i = arg.length;
		Character[][] ret = new Character[i][];
		while (--i >= 0)
			ret[i] = char2Character(arg[i]);
		return ret; }

	/**Converts each character of the given string into a boxed {@link Character}.	 */
	public static Character[] String2Character(String arg) {
		return char2Character(arg.toCharArray()); }

	/** Conversion Routine 	  */
	public static Character[][] Strings2Characters(String[] arg) {
		int i = arg.length;
		Character[][] ret = new Character[i][];
		while (--i >= 0)
			ret[i] = char2Character(arg[i].toCharArray());
		return ret; }

	//these Methods are especially useful in comparing two Object[]s
	//without caring for Case and opening, trailing or in between Spaces.
	/** Converts the Argument into an Object Array.	 */
	public static boolean endsWith(Object[] arg, Object[] Suf) {
		int i = arg.length;
		int j = Suf.length;
		while (--j >= 0) { --i;
			if ((Suf[j]   !=   arg[i]) &&
				!Suf[j].equals(arg[i]))
			return false; }
		return true; }

	/** Converts the Argument into an Object Array.	 */
	public static Object[] convertArg(Object arg) {
		if (arg instanceof Object[]) return (Object[]) arg; //cares for all Arrays, since all can be converted to Object[]!
		if (arg instanceof ArrayMonoid) return ((ArrayMonoid) arg).inner;
//		if (arg instanceof Container) return ((ArrayMonoid) arg).inner; //TODO: integrate Containers.
		Object[] ret = new Object[1];
		ret[1] = arg;
		return ret; }

	/** Returns a new Array that is a concatenation of Arr and Sub.	 */
	public static Object[] conCat(Object[] Arr, Object[] Suf) {
		Object[] ret = new Object[Arr.length + Suf.length];
		System.arraycopy(Arr, 0, ret, 0, Arr.length);
		System.arraycopy(Suf, 0, ret,    Arr.length, Suf.length);
		return ret; }

	/** Returns a new string that is a substring of this string.
	  * The substring begins at the specified Start and ends before the Stop.
	  * Uses both = and equals()! */
	public static Object[] subString(Object[] Arr, int Start, int Stop) {
//		if (Stop > Arr.length)
//			Stop = Arr.length; //Tolerant cut off at the maximum Length prevents Error Detection!!!
		int Length = Stop - Start;
//		if (Length <= 0) return new Object[0]; //tolerant negative Cut Off
		Object[] ret = new Object[Length];
		System.arraycopy(Arr, Start, ret, 0, Length);
		return ret; }

	/** Returns the first Position of Char in Arr (from the left Side)
	  * Uses both = and equals()! */
	public static Object[] subString(Object[] Arr, int Start) {
		return subString(Arr, Start, Arr.length-Start); }

	/** Returns the first Position of Char in Arr (from the left Side)
	  * Uses both = and equals()! */
	public static int indexOf(Object[] Arr, Object Char) {
		int i = -1;
		while (++i < Arr.length)
			if ((Char   ==   Arr[i]) ||
				 Char.equals(Arr[i]))
				return i;
		return -1; } //Object not found!

	/** Returns the first Position of Char in Arr (from the left Side)  */
	public static int lastIndexOf(Object[] Arr, Object Char) {
		int i = Arr.length;
		while (--i >= 0)
			if ((Char   ==   Arr[i]) ||
				 Char.equals(Arr[i]))
				return i;
		return -1; } //Object not found!

	/** Trims the Characters from the left Side $[Chars]*  */
	public static Object[] lTrim(Object[]  in, Object[] Chars) {
		int i = -1;
		while (++i < in.length)
			if ((indexOf(Chars, in[i])) < 0)
				return subString (in, i);
		return new Object[0]; } //all Objects were found!

	/** Trims the Characters from the right Side [Chars]*Z */
	public static Object[] rTrim(Object[] in, Object[] Chars) {
		int i = in.length;
		while (--i >= 0)
			if ((indexOf (Chars, in[i])) < 0)
				return subString(in, 0, ++i);
		return new Object[0]; } //all Objects were found!

	/** Trims the Characters in the Middle and replaces [Chars]* by 'Subst' */
	public static Object[] mTrim(Object[] in, Object[] Chars, Object[] Subst) {
		return in; }

	/** Trims the Characters anywhere and replaces [Chars]* by 'Subst' */
	public static Object[] trim(Object[] in, Object[] Chars, Object[] Subst) {
		return in; }

	//these Routines handle regular Expressions
	//it would be a great optimization if you could write an Input- and Output-
	//Stream that takes RegExp and processes it, just like sed and perl do it.
	//But this Class could be much more powerful,
	// because it can operate on Objects instead of dumb Characters.
	//

	/** Constructor for Container Type 'Array'
	  * For Iterators and general Containers use ConstructorIterator	 */
	public static ArrayMonoid[] Constructor(Object[][] Strg) {
		int i = Strg.length;
		ArrayMonoid[] ret = new ArrayMonoid[i];
		while (--i >= 0) ret[i] = new ArrayMonoid(Strg[i]);
		return ret; }

	//////////////////////////
	//	Member Variables	//
	//////////////////////////

	/** Local Reference to the Object[]
	  * I could have used a Vector that resizes automatically,
	  * but since the Size of each Array is well known beforehand,
	  * the Target Size can be calculated which is more performant and predictable. */
	protected Object[] inner = new Object[0];

	/**Local Reference to the Object[]	 */
//	protected boolean invers = false;

	/**Local Reference to the String Separator for Output	 */
	public String Separator = ", ";

	/**Local Reference to the Inverse	 */
	protected IInvertAble Inverse;

	////////////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////////////

	/**Empty Constructor used for inheriting Classes only.
	 * Self must be set explicitly!	 */
	public ArrayMonoid(){ super(); self = this; }

	/**Empty Constructor used for inheriting Classes only.
	 * Self must be set explicitly.	 */
	public ArrayMonoid(Object[] arg){
		this(); inner = arg; this.Inverse = new ArrayMonoid(arg, this); }

	/**Empty Constructor used for inheriting Classes only.
	 * Self must be set explicitly.	 */
	public ArrayMonoid(Object[] arg, IInvertAble inverse){
		this(); inner = arg; this.Inverse = inverse; } //this.invers = true; }

	////////////////////////////////////////////////////////////////////////////
	//	Interface CopyAble
	////////////////////////////////////////////////////////////////////////////

	/** Creates an uninitalized new Instance of it's class.
	  * Preserve the Separator here! */
	public ICopyAble newInstance() {
		ArrayMonoid ret = new ArrayMonoid();
		ret.Separator = this.Separator;
		return ret; }

	/**Complement to copyAt() and shallopCopyAt().
	 * Does a 'deepCopy', to a certain Level
	 * i.e. also inner Components are copied up to the Depth.
	 * Returns the itself for further use. */
	public ICopyAble copyAt(Object arg, int Depth) {
		if (arg instanceof Object[])    inner =  (Object[]      ) arg;
		if (arg instanceof ArrayMonoid) inner = ((ArrayMonoid) arg).inner;
		return this; }

	/** Converts this Object to a String	 */
	public String toString() {
		return streamIO.AStreamOut.ARRAY_TO_STRING(inner, Separator); }

	/**Fills this Instance with the Contents read from the String.	 */
	public ICopyAble fromStreamAt(StreamTokenizer arg) { //throws java.io.IOException{
		try {
			Vector vec = new Vector();
			while (arg.nextToken () != StreamTokenizer.TT_EOF)
				vec.add (arg.sval);
			inner = vec.toArray();
			return this; }
		catch (java.io.IOException x) { return null; }}

	/**Concatenation in Place: this�=arg
	 * This virtual Operation has to be implemented by each subclass.	 */
	public ISemiMonoid catAt(Object arg)	{
		inner = conCat(inner, convertArg(arg)); //
		return this; }

	/**Right-Concatenation with the Inverse in Place: this�=!arg  this\=arg
	 * This is the Inverse Operation to catAt(), not to map()!
	 * This virtual Operation has to be implemented by each concrete Subclass.		 */
	public IMonoid tacAt (Object arg) {//throws InvalidAlgorithmParameterException {
		Object[] arg_ = convertArg(arg);
		if (! endsWith(inner, arg_)) throw new IllegalArgumentException(); //Runtime Exception, needn't be declared!
		inner = subString (inner, 0, inner.length - arg_.length); // Stream.XMLOutputStream.toString(arg));
		return this; }

	/**Mapping from Left in Place:  this=�arg
	 * This Operation doesn't return 'this', but 'arg'!
	 * so to concatenate Mappings use B.mapAt(A.mapAt(a))
	 * which is more efficient than B.map(A.map(a)) or B.map(A).map(a)
	 * or A.cat(B).map(a)           */
	//public ISemiMonoid mapAt(ISemiMonoid arg) {
	//	return arg.catAt (this); }

	/**Mapping / Left-Concat with !arg in Place: !this=�arg */
	public ISemiMonoid unMapAt(ISemiMonoid arg) {
		return ((ArrayMonoid) arg).tacAt (this); }

	/**Setting to  Id in Place:	 */
	public IMonoid	 IdentityAt	() {
		inner = new Object[0]; return this; } //

	//Any Object with Operations can act as Operator
	//The only Problem is that the Operation is usually not unique
	//With Group and GroupM extra Classes AddAt and MulAt were created.

	/** Returns the Inverse Function to this one: !this
	 * i.e. the Function that returns the identical Mapping,
	 * if mapped / concatenated with this Function (at least locally)	  */
	public IInvertAble Inverse () { return Inverse; }

	/** Sets the Inverse from outside.
	 * This can be done only once, after that an IllegalStateException is thrown.	  */
	public void setInverse (IInvertAble inverse) {
		if (this.Inverse != null) throw new IllegalStateException();
		this.Inverse = inverse; }

	/** Returns arg mapped by the Inverse of this Object: !this�arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	  */
	public IMonoid pam (Object arg) { return pamAt(((ISemiMonoid) arg).copy()); }

	/** Returns arg mapped in Place by the Inverse of this Object: !this=�arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	  */
	public IMonoid pamAt (Object arg) { return pamAt(arg); }

	/** Returns arg mapped by this Object: this.map(arg) == this�arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	  */
	public ISemiMonoid map (Object arg) { return mapAt(((ISemiMonoid) arg).copy()); }

	/** Returns true, when this Class can operate on Arguments of this Type
	 * This Function makes sense at this Level,
	 * because here there is always the Alternative for the Algebra
	 * to delegate the Operation to the inner Function,
	 * but to operate on the Functions and operate the Results on evaluation.	  */
	public boolean canProcess (Object arg) { return arg instanceof ArrayMonoid; }

	/** Returns an alternative Representation that is 'simplified'	  */
	public IFunction simplify () { return this; }

	////////////////////////////////////////////////////////////////////////////
	//  Test Methods
	////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	  */
	public static void testIt() {
		String[] arg = {"The ", "quick ", "brown ", "Fox ", "jumps ", "over ", "the ", "lazy ", "black ", "Dog"};
		Character[][] test1 = Strings2Characters(arg);
		ArrayMonoid[] test2 = ArrayMonoid.Constructor(test1);
		IMonoid[] test = test2;
		//it would be really nice if Container Support was integrated into the Language like in C#,
		//because that could make the Syntax more concise and reduce the Probability of Errors.
		int i = test2.length; while (--i >= 0) test2[i].Separator = null;
		IMonoid res;
		res=(IMonoid) test[1].map(test[0]); System.out.println(res); //The quick
		//res=(IMonoid) test[0].cat(test[1]); System.out.println(res); //The quick
		//res.  catAt (test[2]); System.out.println(res); //so much for Assembling, now to Parsing:
//		res.unCatAt (test[1]); System.out.println(res); //creates a Runtime Exception!
		//res.tacAt (test[2]); System.out.println(res); //now to Parsing by unknown Items:
		//Operation to copy into an Array, from an Iterator
		//Array Operations.
		//Regular Expressions with Characters, Strings and Objects
	}

	/** Main Method, so far used for testing this Class	  */
	public static void main(String[] args) {
		testIt();
	}

	/** Returns arg mapped in Place by this Object: this.mapAt(arg) this=�arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	  */
/*	public Object mapAt (Object arg) {
		return null; //added by the Compiler, also missing with StringMonoid?
	}
	*/
}
