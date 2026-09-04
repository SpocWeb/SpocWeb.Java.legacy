package streamIO.copy.monoid;

import streamIO.copy.ICopyAble;
import function.IFunction;
import function.IInvertAble;

/**Monoid working on Strings.
 * Concatenation is is implemented as String Concatenation.
 * A Reverse can be defined, but it would be the same for each String
 * and consist of as many Delete Characters
 * as Characters in the original String.
 * But any Information on the original String is lost.
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
 *
 * The String Methods endsWith(), startsWith() and contains() define a HomoMorphism
 * that is explained in StrSearcher (also for isPrefixOf() and isSuffixOf()).
 */
public class StringMonoid
extends AMonoid //
{ //, orderAble { //can define less, grtr using alphabetical Order.
	//order Relation and Invertability are homomoph as described in StrSearcher.

	//////////////////////
	//	static Methods	//
	//////////////////////

	//these Methods are especially useful in comparing two Strings
	//without caring for Case and opening, trailing or in between Spaces.

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Trims the Characters from the left Side $[Chars]*  */
	public static String lTrim(String in, String Chars) {
		int i = -1, len = in.length();
		while (++i < len)
			if ((Chars.indexOf (in.charAt (i))) < 0)
				return in.substring (i);
		return ""; } //all Characters were found!

	/** Trims the Characters from the right Side [Chars]*Z */
	public static String rTrim(String in, String Chars) {
		int i = in.length();
		while (--i >= 0)
			if ((Chars.indexOf (in.charAt (i))) < 0)
				return in.substring(0, ++i);
		return ""; } //all Characters were found!

	/** Trims the Characters in the Middle and replaces [Chars]* by 'Subst'
	  * Useful for ignoring arbitrary Sets of Tabs and Spaces */
	public static String mTrim(String in, String Chars, String Subst) {
		return in; }

	/** Trims the Characters anywhere and replaces [Chars]* by 'Subst' */
	public static String trim(String in, String Chars, String Subst) {
		return in; }

	/** Converts the Characters to Upper Case */
	public static String upper(String in) {
		return in.toUpperCase(); }

	/** Converts the Characters to Lower Case */
	public static String lower(String in) {
		return in.toLowerCase(); }

	//these Routines handle regular Expressions
	//it would be a great optimization if you could write an Input- and Output-
	//Stream that takes RegExp and processes it, just like sed and perl do it.
	//But this Class could be much more powerful,
	// because it can operate on Objects instead of dumb Characters.
	//

	/** Constructor for Container Type 'Array'
	  * For Iterators and general Containers use ConstructorIterator	 */
	public static StringMonoid[] Constructor(String[] Strg) {
		int i = Strg.length;
		StringMonoid[] ret = new StringMonoid[i];
		while (--i >= 0) ret[i] = new StringMonoid(Strg[i]);
		return ret; }

	//////////////////////////
	//	Member Variables	//
	//////////////////////////

	/**Local Reference to the String	 */
	protected String inner = "";

	/**Local Reference to the String	 */
//	protected boolean invers = false;

	/**Local Reference to the Inverse	 */
	public IInvertAble Inverse;

	/**Empty Constructor used for inheriting Classes only.
	 * Self must be set explicitly.	 */
	public StringMonoid(){ super(); self = this; }

	/** Initializing Constructor.	 */
	public StringMonoid(String arg){
		this(); inner = arg; this.Inverse = new StringMonoid(arg, this); }

	/** Initializing Constructor for setting the Inverse and thus preventing Recursion.	 */
	public StringMonoid(String arg, IInvertAble Inverse_){
		this(); inner = arg; this.Inverse = Inverse_; } //this.invers = true; }

	/**Creates an uninitalized new Instance of it's class.	 */
	public ICopyAble newInstance() { return new StringMonoid(); }

	/**Complement to copyAt() and shallopCopyAt().
	 * Does a 'deepCopy', to a certain Level
	 * i.e. also inner Components are copied up to the Depth.
	 * Returns the itself for further use. */
	public ICopyAble copyAt(Object arg, int Depth) {
		if (arg instanceof String)       inner =  (String      ) arg;
		if (arg instanceof StringMonoid) inner = ((StringMonoid) arg).inner;
		return this; }

	/** Converts this Object to a String	 */
	public String toString() { return inner; }

	/**Fills this Instance with the Contents read from the String.	 */
	public ICopyAble fromStreamAt(java.io.StreamTokenizer arg) { //throws java.io.IOException{
		try { inner = arg.sval; arg.nextToken(); return this; }
		catch (java.io.IOException x) { return null; }}

	/**Concatenation in Place: this°=arg
	 * This virtual Operation has to be implemented by each subclass.	 */
	public ISemiMonoid catAt(Object arg)	{
		inner = inner.concat(arg.toString()); // Stream.XMLOutputStream.toString(arg));
		return this; }

	/**Right-Concatenation with the Inverse in Place: this°=!arg  this\=arg
	 * This is the Inverse Operation to catAt(), not to map()!
	 * This virtual Operation has to be implemented by each concrete Subclass.		 */
	public IMonoid tacAt (Object arg) {//throws InvalidAlgorithmParameterException {
		String arg_ = arg.toString();
		if (! inner.endsWith(arg_)) throw new IllegalArgumentException(); //Runtime Exception, needn't be declared!
		inner = inner.substring (0, inner.length() - arg_.length()); // Stream.XMLOutputStream.toString(arg));
		return this; }

	/**Mapping from Left in Place:  this=°arg
	 * This Operation doesn't return 'this', but 'arg'!
	 * so to concatenate Mappings use B.mapAt(A.mapAt(a))
	 * which is more efficient than B.map(A.map(a)) or B.map(A).map(a)
	 * or A.cat(B).map(a)	       */
	//public ISemiMonoid mapAt(ISemiMonoid arg) {
	//	return arg.catAt (this); }

	/**Mapping / Left-Concat with !arg in Place: !this=°arg */
	public ISemiMonoid unMapAt(ISemiMonoid arg) {
		return ((StringMonoid) arg).tacAt (this); }

	/**Setting to  Id in Place:	 */	public IMonoid	 IdentityAt	() {
		inner = ""; return this; } //

	//Any Object with Operations can act as Operator
	//The only Problem is that the Operation is usually not unique
	//With Group and GroupM extra Classes AddAt and MulAt were created.

	/** Returns the Inverse Function to this one: !this
	 * i.e. the Function that returns the identical Mapping,
	 * if mapped / concatenated with this Function (at least locally)	  */
	public IInvertAble Inverse () { return Inverse; }

	/** Sets the Inverse from outside.
	 * This can be done only once, after that an IllegalStateException is thrown.	  */
	public void setInverse (IInvertAble Inverse_) {
		if (this.Inverse != null) throw new IllegalStateException();
		this.Inverse = Inverse_; }

	/** Returns arg mapped by the Inverse of this Object: !this°arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	  */
	public IMonoid pam (Object arg) { return pamAt(((ISemiMonoid) arg).copy()); }

	/** Returns arg mapped in Place by the Inverse of this Object: !this=°arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	  */
	public IMonoid pamAt (Object arg) { return pamAt(arg); }

	/** Returns arg mapped by this Object: this.map(arg) == this°arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	  */
	public ISemiMonoid map (Object arg) { return mapAt(((ISemiMonoid) arg).copy()); }

	/** Returns true, when this Class can operate on Arguments of this Type
	 * This Function makes sense at this Level,
	 * because here there is always the Alternative for the Algebra
	 * to delegate the Operation to the inner Function,
	 * but to operate on the Functions and operate the Results on evaluation.	  */
	public boolean canProcess (Object arg) { return arg instanceof StringMonoid; }

	/** Returns an alternative Representation that is 'simplified'	  */
	public IFunction simplify () { return this; }

	/** Tests all Methods of this Class	  */
	public static void testIt() {
		String[] test1 = {"The ", "quick ", "brown ", "Fox ", "jumps ", "over ", "the ", "lazy ", "black ", "Dog"};
		System.out.println(test1 instanceof Object[]);
		IMonoid[] test = StringMonoid.Constructor(test1);
		IMonoid res;
		res=(IMonoid) test[1].map(test[0]); System.out.println(res);
		//res=(IMonoid) test[0].cat(test[1]); System.out.println(res);
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

}
