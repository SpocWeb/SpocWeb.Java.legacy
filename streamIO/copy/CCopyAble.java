package streamIO.copy;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StreamTokenizer;

import streamIO.exception.ReadOnlyException;

/**Implements Constants for all Types of CopyAble Classes.
 * This Class inhibits the Use of ...At() Routines by throwing Exceptions
 * but still supports all other Methods of the CopyAble Class.
 * This is an Application of the Decorator Pattern
 * which is a Generalization of the Pipes and Filters Pattern.
 * It has been made obsolete by the 'Const' Class.
 *
 * Design Decisions:
 * All Constant Classes are derived from this one.
 * They cannot inherit the Implementations from the abs... Classes,
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 63c4af46ef332e49269761392a59ef34cca9e8b0754daf2b766d632a695f5f14
 * stale: false
 * tags: [code/immutable_wrapper, code/delegation]
 * concepts: [Copy Semantics, Constant/Immutable Wrapper]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * but it is more performant to delegate everything right away!	 */
public class CCopyAble
extends ACopyAble
implements ICopyAble {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**Local Store of the actual inner, on Operations always copied!	 */
	protected ICopyAble inner;

	//////////////////////
	//	Constructor		//
	//////////////////////

	/** Initializing Constructor,
     * Creates a DeepCopy of cnst, to be really sure that no Object has a Reference
     * @param cnst Object to be represented and copied by Value.
     */
	public CCopyAble(ICopyAble cnst)	{ inner = cnst.copy(); }


	//////////////////////////////
	//	Delegation to 'inner'	//
	//////////////////////////////


	/** Creates an uninitalized new Instance of it's class.
     * When overriding, use newInstance on all Components.
     * @return an uninitalized new Instance of it's class.
     */
	public ICopyAble newInstance()	{ return inner.newInstance(); }

	/** Complement to copy() and shallopCopy().
     * Does a 'deepCopy', to a certain Level
     * i.e. also inner Components are copied up to the Depth.
     * Returns the Copy for further use.
     * @param Depth Depth up to which copying by Value happens.
     * @return a Copy of this Object.
     */
	public ICopyAble copy(int Depth)	{ return inner.copy(Depth); }

	/** Creates a new object of the same class as this object. It then
     * initializes each of the new object's fields by assigning it the
     * same value as the corresponding field in this object. No
     * constructor is called.
     * <p>
     * The <code>copy</code> method of class <code>Object</code> will
     * only copy an object whose class indicates that it is willing for
     * its instances to be cloned. A class indicates that its instances
     * can be cloned by declaring that it implements the
     * <code>Cloneable</code> interface.
     * @return a deep copy of this instance.
     * @see java.lang.Cloneable#
     */
	public ICopyAble copy() { return inner.copy(); }

	//////////////////////////
	//	basic operations	//
	//////////////////////////

	/** Compares two Objects for equality.
     * <p>
     * The <code>equals</code> method implements an equivalence relation:
     * <ul>
     * <li>It is <i>reflexive</i>: for any reference inner <code>x</code>,
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
     * <li>For any reference inner <code>x</code>, <code>x.equals(null)</code>
     * should return <code>false</code>.
     * </ul>
     * <p>
     * The equals method for class <code>Object</code> implements the most
     * discriminating possible equivalence relation on objects; that is,
     * for any reference values <code>x</code> and <code>y</code>, this
     * method returns <code>true</code> if and only if <code>x</code> and
     * <code>y</code> refer to the same object (<code>x==y</code> has the
     * inner <code>true</code>).
     * @return <code>true</code> if this object is the same as the obj
     * argument; <code>false</code> otherwise.
     * @see java.lang.Boolean#hashCode()
     * @see java.util.Hashtable#
     * @param arg Object to be compared with 'this'.
     */
	final public boolean equals  (Object arg) { return inner.equals(arg); }


	//These are the virtual Methods of Object:

	/**Returns a hash code inner for the object. This method is
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
	 * @return  a hash code inner for this object.
	 * @see     java.lang.Object#equals(java.lang.Object)
	 * @see     java.util.Hashtable
	 * @since   JDK1.0 */
	public int hashCode(){ return inner.hashCode(); }

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
	public String toString()	{ return inner.toString(); }


	//////////////////////////
	//	Throwing 'Errors'	//
	//////////////////////////

	/**Error Message for Constants on the left side of Assignments	 */
	final static public String strConst = "Cannot use ...At() Methods on Constants!";

	/** Complement to copyAt() and shallopCopyAt().
     * Does a 'deepCopy', to a certain Level
     * i.e. also inner Components are copied up to the Depth.
     * Returns itself for further use.
     * @param arg Object to be copied from
     * @param Depth Depth up to which copying by Value happens.
     * @return 'this' after copying from 'arg'.
     */
	public ICopyAble copyAt(Object arg, int Depth) { throw new ReadOnlyException(strConst);}

	/** Fills this Instance with the Contents read from the streamIO.
     * @param ST Input streamIO from which the Contents is being read.
     * @throws IOException raised by the streamIO 'ST'
     *@return 'this' after reading from the streamIO
     */
	public ICopyAble fromStreamAt(StreamTokenizer ST) throws IOException {
		throw new ReadOnlyException(strConst); }

	/** Does a shallow Copy of the Argument.
     * I.e. both Instances will share their inner Components.
     * @param arg Object to be copied from.
     * @return 'this' after copying only 1 Level deep.
     */
	public ICopyAble shallowCopyAt(Object arg) {
		throw new ReadOnlyException(strConst); }

	/** Complement to Copy.
     * Does a 'deepCopy', i.e. also inner Components are copied.
     * Copies the Value of arg into it's own Value
     * and returns itself for further use.
     * When overriding, use copyAt on all Components.
     * @param arg Object to be deep copied from.
     * @return 'this' after deep copying from 'arg'
     */
	public ICopyAble copyAt(Object arg) { throw new ReadOnlyException(strConst); }

	/** Swap Algorithm: this <-> arg
     * swaps the internal Components by using shallowCopyAt(), not copyAt()
     * For Swapping Objects it is more effective to swap the Pointers directly!
     * @param arg Object to be shallow swapped with.
     * @return 'this' after shallow swapping with 'arg'.
     */
	public ICopyAble swap (Object arg) { throw new ReadOnlyException(strConst); }

	/** Creates an uninitalized new Instance of it's class
     * and fills it with the Contents read from the String.
     * @param IS Input streamIO to be read from.
     * @throws IOException raised by the streamIO
     * @return new Instance initialized from the streamIO.
     */
	public ICopyAble fromStreamAt(InputStream IS) throws IOException {
		throw new ReadOnlyException(strConst); }

	/** Creates an uninitalized new Instance of it's class
     * and fills it with the Contents read from the String.
     * @param IS Input streamIO to be read from.
     * @throws IOException raised by the streamIO 'IS'
     * @return an uninitalized new Instance of this Object.
     */
	public ICopyAble fromStreamAt(Reader IS) throws IOException {
		throw new ReadOnlyException(strConst);}


	//////////////
	//	Testing	//
	//////////////

	/**Instance of a concrete Class derived from ACopyAble
	 * to be able to perform the Tests.
	 * This backward Reference is necessary,
	 * since only abstract Classes are defined in the first Packages.	 */
//	public static CopyAble testInstance;

	/** Method to test all Implementations in this class.
     * Must call testIt of the super Class.
     * @throws IOException raised by the Input streamIO.
     */
	public static void testIt() throws java.io.IOException {
		System.out.println("Testing ConstCopyAble:");
		System.out.println("Original:" + testInstance);
/*		System.out.println(testInstance + ".copy() =" + testInstance.copy( ));	//deepCopy
		System.out.println(testInstance + ".copy(0)=" + testInstance.copy(0)); //shallowCopy
		System.out.println(testInstance + ".shallowCopy() :" + testInstance.shallowCopy()); //shallowCopy
		System.out.println(testInstance + ".newInstance() :" + testInstance.newInstance()); //
		CopyAble newInstance = testInstance.newInstance();
		System.out.println(testInstance + ".swap(" + newInstance + ") :" + testInstance.swap(newInstance)); //
		System.out.println(newInstance  + ".copyAt("+ testInstance + ") :" + newInstance.copyAt(testInstance)); //
		String newValue = newInstance.toText();
		System.out.println(newInstance  + ".toText()=" + newValue); //
		try{
		System.out.println("fromText(" + newValue + ")=" + fromText(newInstance.toText())); //
		}catch (Exception e){System.out.println(e);}
//		System.out.println("() :" + newInstance); //
*/	}

}
