package streamIO.copy;

//only for the parsing Routines!
//for Array.newInstance() and the generic copyAt()
import graphs.ACopy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringBufferInputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import streamIO.Assert;
import streamIO.IDeserializer;
import streamIO.IFormatOut;
import streamIO.IInstantiAble;
import streamIO.Log;
import streamIO.copy.group.ring.metric.body.ABodyDouble;
import streamIO.exception.BaseException;

/**Defines the Interface for a public 'copy' Method with variable Depth.
 * It replaces the 'clone' Method (for Object it is only protected).
 *
 * Since this is the Base Class for all following Objects,
 * self is not used for Delegation, so the Aggregation Stuff is not necessary.
 *to
 * Design Decisions:
 * (De-) Serialization can happen by using the toStream and fromStream Methods
 * in a generic way!
 * I have chosen to have the Output sent out to Streams, instead of Strings,
 * because that saves Memory and Streams can simply be written into StringsBuffer.
 *
 * In Java you can implement toStream(), fromStream, copyAt() and copy()
 * in a very generic, but slower stati c fashion using the Reflection API.
 * Thus you needn't reimplement fromStreamAt(), toStream(), copyAt()
 * and newInstance() (Object.clone() does a shallow Copy!).
 *
 * Due to the Reflection API of Java all the Methods can be implemented
 * generically so this class actually needn't be abstract anymore!
 * But I won't delete the existing Implementations until it is necessary,
 * because it would cause too much work and is less effective
 * than a concrete Implementation.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:48:42Z
 * digest: 57cfad59da206e214d89b2e91914168a7e0730ff2884efc952c5e138a5a7587f
 * stale: false
 * tags: [code/abstract_base, code/serialization, code/reflection]
 * concepts: [Copy Semantics, Serialization]
 * facets: {layer: utility, status: broken, complexity: medium}
 * -->
 */
public abstract class ACopyAble
extends ACopy
implements //Copy.ICopyAble, //not necessary to mention here!
ICopyAble, Serializable, Cloneable {//to be able to Stream out and clone the Object

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(10);

	////////////////////////////////////////////////////////////////////////////
	//  static Constants and Variables
	////////////////////////////////////////////////////////////////////////////

	/** Default Parser for System.in Input and for creating new Parsers	 */
	public static IDeserializer DefaultParser;

	/** Default Formatter for System.out Output and for creating new Formatters	 */
	public static IFormatOut DefaultFormatter = Log.L;

	///////////////////////////////////////////////////////////////////////////
	//  Static Methods (copying and creating primitive and Object Types)
	///////////////////////////////////////////////////////////////////////////

	/**Generic Implementation of the newInstance() Method, needn't be overwritten!
	 * Doesn't work for Arrays, but these can't be derived from ACopyAble anyway.
	 */
	public static Object newInstanceSafe(final Object arg) {
		try { return newInstance(arg); } //arg.getClass().newInstance(); }
		catch (IllegalAccessException e) { throw new IllegalStateException(e.toString()); }
		catch (InstantiationException e) { throw new IllegalStateException(e.toString()); }
	}

	/**Generic Implementation of the newInstance() Method,
	 * also works for (1 dimensional) Arrays!
	 */
	public static Object newInstance(final Object arg)
		throws IllegalAccessException, InstantiationException {
		Class myClass;
		if ((myClass = arg.getClass()).isArray())
			 return Array.newInstance(myClass.getComponentType(), Array.getLength(arg));
		else return myClass.newInstance();
	}

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Components.	 */
	public static Object COPY_AT(final Object ths, final Object arg) { 
		return copyAtSafe(ths, arg, Integer.MAX_VALUE); }

	/**Complement to copyAt()  */
	public static Object shallowCopyAt(final Object ths, final Object arg) {
		return copyAtSafe(ths, arg, 0); }

	/**Swap Algorithm: this <-> arg
	 * swaps the internal Components by using shallowCopyAt(), not copyAt()
	 * For Swapping Objects it is more effective to swap the Pointers directly!
	 * But that cannot be done calling a Procedure,
	 * because Java knows only ByVal Parameters.	 */
	public static ICopyAble SWAP (final Object arg, final Object ths) {
		Object tmp = shallowCopyAt(newInstanceSafe(arg), ths);	//self.copy();	//is equivalent
		shallowCopyAt(ths, arg);
		((ICopyAble)arg).shallowCopyAt(tmp);
		return (ICopyAble)ths; }

	/**Creates a Copy of the Original Object.	 */
	public static Object COPY(final Object Original, final int Depth) throws InstantiationException {
		if (Original instanceof ICopyAble) {
			return ((ICopyAble) Original).copy(Depth); } //usually faster
		return copyAtSafe( newInstanceSafe(Original), Original, Depth); }

	/**Creates a Copy of the Original Object.     */
	public static Object shallowCopy(final Object Original) throws InstantiationException {
		return copyAtSafe( newInstanceSafe(Original), Original, 0); }

	/**Creates a Copy of the Original Object.     */
	public static Object COPY(final Object Original) throws InstantiationException {
		return copyAtSafe( newInstanceSafe(Original), Original, Integer.MAX_VALUE); }

	/**Complement to copyAt() and shallopCopyAt().
	 * Does a 'deepCopy', to a certain Level
	 * i.e. also inner Components are copied up to the Depth.
	 * Returns the itself for further use. */
	public static Object copyAtSafe(final Object ths, final Object arg, final int Depth) {
		if (Depth < 0) 
			return ths;
		try { copyAt(ths, arg, Depth); return ths; }
		catch (InstantiationException e) { throw new IllegalStateException(e.toString()); }
		catch (ClassNotFoundException e) { throw new IllegalStateException(e.toString()); }
		catch (  NoSuchFieldException e) { throw new IllegalStateException(e.toString()); }
		catch (IllegalAccessException e) { throw new IllegalStateException(e.toString()); }
	}

	/**Copies the Array with the given Class (not Element Type!) from arg.
	 * Separated out, because Arrays are of variable Size!
	 */
	public static void ARRAY_COPY_AT(final Object inner, final Object arg, int Length, final Class myClass, final int Depth)
	throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException, InstantiationException {
		if (myClass.isPrimitive()) 
			System.arraycopy(arg, 0, inner, 0, Length); //
		else {
			final Object[] arrArg = (Object[]) arg;
			final Object[] arrObj = (Object[]) inner;
			while (--Length >= 0) {
				if  (arrArg[Length] == null)
					 arrObj[Length]  = null; else
				if  (arrObj[Length] == null)
					 arrObj[Length]  = COPY(	 //create new Elements on Demand
					 arrArg[Length], Depth);
				else                   copyAt(
					 arrObj[Length], //reuse existing Elements, if possible
					 arrArg[Length], Depth); } } }

	/**Complement to copyAt() = copyAt(infin) and shallopCopyAt() = copyAt(0).
	 * Does a 'deepCopy', to a certain Level
	 * i.e. also inner Components are copied up to the Depth.
	 * Returns the itself for further use.
	 * This should have been the Model to the XMLInputStream Routines,
	 * because those are only more complicated due to the Parsing  */
	public static void copyAt(Object ths, Object arg, int Depth)
	throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException, InstantiationException {
		Class myClass = ths.getClass();
		if (!myClass.isInstance(arg)) throw new NoSuchFieldException("incompatible Classes");
		COPY_AT(ths, arg, myClass, Depth); }

	/**Complement to copyAt() = copyAt(infin) and shallopCopyAt() = copyAt(0).
	 * Does a 'deepCopy', to a certain Level
	 * i.e. also inner Components are copied up to the Depth.
	 * Returns the itself for further use.
	 * This should have been the Model to the XMLInputStream Routines,
	 * because those are only more complicated due to the Parsing  */
	private static void CopyAt(Object ths, Object arg, Class myClass, int Depth)
	throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException, InstantiationException {
		do { //process all Super Classes
			Field[] Fields = myClass.getDeclaredFields();
			int i = Fields.length;
			while(--i >= 0) {
				Field currField = Fields[i];
				int mod = currField.getModifiers();
				if (Modifier.isFinal	(mod) ||
					Modifier.isTransient(mod) ||
					Modifier.isStatic	(mod) ||
					Modifier.isVolatile	(mod)) continue;
				currField.setAccessible(true);	//get Access to the Data, throws ClassNotFoundException
				Class fClass = currField.getType();
				if ((Depth == 0) || currField.getType().isPrimitive()) //load a primitive Type
					 currField.set(ths , currField.get(arg));
				else currField.set(ths , COPY_AT(
					 currField.get(ths),
					 currField.get(arg),
//					 myClass,
					 fClass,
					 Depth-1)); } //Recursion
		} while ((myClass = myClass.getSuperclass()) != Object.class);  //read the Super Type
	}

	/**Copies 'ths' from 'arg' and returns it.
	 * 'ths' can be null (this creates new Objects)
	 * or passed in for possibly reusing it (when of correct size).
	 */
	public static Object COPY_AT(Object ths, Object arg, Class fClass, int Depth)
		throws InstantiationException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
		if (fClass.isArray()) { //cannot reuse variable Size Objects == Arrays
			Class myClass = fClass.getComponentType();
			int Length = Array.getLength(arg);
			int Len2 = 0; if (ths != null) Len2 = Array.getLength(ths);
			if (Length != Len2) { //TODO: can this handle 'null' Array Objects?
				Object arr = Array.newInstance(myClass, Length);
				if (ths != null)  //TODO: try to reuse as many Elements as possible, also in Arrays
					if (! myClass.isPrimitive())
						System.arraycopy(ths, 0, arr, 0, Math.min(Length, Len2)); //
				ths = arr; }
			ARRAY_COPY_AT(ths, arg, Length, myClass, Depth);
		} else { //unwrapping non-primitive Type
			if (ths == null) {
//				if (arg instanceof Cloneable) return arg.clone();
				if (arg instanceof String) return arg;
				if (arg instanceof ICopyAble) ths = ((ICopyAble) arg).newInstance();
				else ths = arg.getClass().newInstance(); // fClass.newInstance();  //create a new Object, requires an empty Constructor!
			} CopyAt(ths, arg, fClass, Depth); } //...before the Recursion is entered, because the Elements may be used.
		return ths; }

	/**Does a Deep Copy of ANY Object (as opposed to the standard copy() Method)!
	 * Arrays of primitive Type or Object Type
	 * for simple Objects simply copy() is used.
	 * Similar to the copyAt() Routines in Polnom.
	 * Should be used in the copy() Routines of Array Types
	 * and Polygons.	 */
/*	final static public Object copy (Object arg, int Depth) {
		//First test for known structured Types
		if (--Depth == 0) return arg;
		Class C = arg.getClass();
		if (! C.isArray()) return ((CopyAble) arg).copy(Depth);	//simple Object
		Class Typ = C.getComponentType();	//determine the Type of Array Elements.
		int Length = java.lang.reflect.Array.getLength(arg);
		if (Typ.isPrimitive()) { 	//Array of primitive Types
			Object Buffer = java.lang.reflect.Array.newInstance(Typ, Length);	//initialize for the Compiler
			System.arraycopy(arg, 0, Buffer, 0, Length);
			return Buffer;
		} else { 	//Array of Objects, start Recursion
			Object[] Arg = (Object[]) arg;
			Object[] tmp = (Object[]) java.lang.reflect.Array.newInstance(Typ, Length);
			int i = Length; while (--i >= 0)
				tmp[i] = copy(Arg[i], Depth);
			return tmp;
		}
	}
*/
	///////////////////////////////////////////////////////////////////////////////
	//  Interface Copy: abstract Methods
	///////////////////////////////////////////////////////////////////////////////

	/**Integrates deepCopyAt() and shallopCopyAt().
	 * Does a Copy to a certain Level
	 * i.e. also inner Components are copied up to the Depth.
	 * Returns the itself for further use.
	 * Depth is only valid >= 0, for 0 only copy() is valid and returns itself.
	 * Implemented directly using the Reflection API (see below)	 */
//	public CopyAble copyAt(Object arg, int Depth);

	/**Fills this Instance with the Contents read from the streamIO.
	 * Implemented directly using the Reflection API
	 * and the ParserIn Interface (see below)	 */
//	public CopyAble fromStreamAt(ParserIn ST) throws IOException;

	/**Writes the Contents of this Object into the streamIO.
	 * Implemented directly using the Reflection API
	 * and the FormatterOut Interface (see below)	 */
//	public void toStream(FormatterOut ST) throws IOException;

	///////////////////////////////////////////////////////////////////////////////
	//  Interface Copy: Implementation
	///////////////////////////////////////////////////////////////////////////////

	/**Creates a new object of the same class as this object. It then
	 * initializes each of the new object's fields by assigning it the
	 * same value as the corresponding field in this object. No
	 * constructor is called.
	 * <p>
	 * The <code>copy</code> method of class <code>Object</code> will
	 * only copy an object whose class indicates that it is willing for
	 * its instances to be cloned. A class indicates that its instances
	 * can be cloned by declaring that it implements the
	 * <code>Cloneable</code> interface.
	 *
	 * @return     a copy of this instance.
	 * @exception  CloneNotSupportedException  if the object's class does not
	 * support the <code>Cloneable</code> interface. Subclasses
	 * that override the <code>copy</code> method can also
	 * throw this exception to indicate that an instance cannot
	 * be cloned.
	 * @exception  OutOfMemoryError            if there is not enough memory.
	 * @see        java.lang.Cloneable
	 * @since      JDK1.0	 */
	public ICopyAble copy() { //does a Deep Copy by Default
		return newInstance().copyAt(this, Integer.MAX_VALUE); }

	/** Complement to copy() = copy(infin) and shallopCopy() = copy(0).
	  * Does a 'deepCopy', to a certain Level
	  * i.e. also inner Components are copied up to the Depth.
	  * Copies the Value of arg into it's own Value
	  * and returns itself for further use. */
	public ICopyAble copy(final int Depth) {
		if (Depth <= 0) return this;
		if (Depth >  1) return newInstance().copyAt(this, Depth);
		try { return (ICopyAble) clone(); //clone is protected, but faster than anything else!
		} catch (CloneNotSupportedException x) { throw new BaseException("Should never happen!", x); }
	}

	/**Creates a new shallow Copy of this Instance.
	 * I.e. both Instances will share their inner Components.
	 * shallowCopy also clones the Types, but does not initialize them!
	 * rarely used.
	 * This is the Default Implementation and should always work,
	 * although a direct Implementation could be faster.	 */
	public ICopyAble shallowCopy() {
//		return newInstance().copyAt(this, 0); }
//		return newInstance().shallowCopyAt(this); }
		try { return (ICopyAble) clone(); }
 		catch(CloneNotSupportedException e) { //should never happen
			throw new IllegalStateException(e.toString()); } }

	/**Does a shallow Copy of the Argument.
	 * I.e. both Instances will share their inner Components.	 */
	public ICopyAble shallowCopyAt(Object arg) { return copyAt(arg, 0); }

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Components.	 */
	public ICopyAble copyAt(Object arg) { return copyAt(arg, Integer.MAX_VALUE); }

	/**Complement to copyAt() and shallopCopyAt().
	 * Does a 'deepCopy', to a certain Level
	 * i.e. also inner Components are copied up to the Depth.
	 * Returns the itself for further use. */
	public ICopyAble copyAt(Object arg, int Depth) {
		return (ICopyAble) copyAtSafe(this, arg, Depth); }

	/**Swap Algorithm: this <-> arg
	 * swaps the internal Components by using shallowCopyAt(), not copyAt()
	 * For Swapping Objects it is more effective to swap the Pointers directly!
	 * But that cannot be done calling a Procedure,
	 * because Java knows only ByVal Parameters.	 */
	public ICopyAble swap (Object arg) { return SWAP(arg, this); }

	/** Generic Implementation of the newInstance() Method, needn't be overwritten!
	  * Doesn't work for Arrays, but these can't be derived from ACopyAble anyway.
	  */
	public ICopyAble newInstance() { try {
		return (ICopyAble) getClass().newInstance(); } //newInstanceSafe(this); }
		catch (IllegalAccessException e) {throw new IllegalAccessError(e.toString()); }
		catch (InstantiationException e) {throw new InstantiationError(e.toString()); }
	}

	/**Default no-op that leaves this instance unchanged; override to actually randomize state.
	 * @see streamIO.copy.IICopyAble#randomizeAt()	 */
	public ICopyAble randomizeAt() { return this; }

	/**Returns a new, randomized instance, built via {@link #newInstance()} then {@link #randomizeAt()}.
	 * @see streamIO.copy.IICopyAble#random()	 */
	public ICopyAble random() { return newInstance().randomizeAt(); }
		
	/** Generic Implementation of the newInstance() Method, needn't be overwritten!
	  * Doesn't work for Arrays, but these can't be derived from ACopyAble anyway.
	  */
	public IInstantiAble NewInstance() { try {
		return (ICopyAble) getClass().newInstance(); } //newInstanceSafe(this); }
		catch (IllegalAccessException e) {throw new IllegalAccessError(e.toString()); }
		catch (InstantiationException e) {throw new InstantiationError(e.toString()); }
	}

	//generic (De-)Serialization, needn't be overwritten!:

	/**Writes the Contents of this Object into the streamIO.
	 * Default Implementation that can be overwritten by more effective ones.
	 * TODO: declare this Methods as abstract!
	 */
	/** Object currently being written by {@link #toStream(IFormatOut)} on this Thread,
	  * used to break the Recursion when a Formatter falls back to toString() again.	 */
	private static final ThreadLocal IN_TO_STREAM = new ThreadLocal();

	public void toStream(final IFormatOut ST)
	throws IOException {
		final Object prev = IN_TO_STREAM.get();
		if (prev == this) { //re-entered for the same Object: write a plain Description instead of recursing
			ST.addItem(getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)));
			return; }
		IN_TO_STREAM.set(this);
		try { ST.addItem(this); }
		finally { IN_TO_STREAM.set(prev); } }

	/**Writes the Contents of this Object into the streamIO.
	 * Default Implementation that can be overwritten by more effective ones.
	 */
	public String toString() {
		final ByteArrayOutputStream OS = new ByteArrayOutputStream();  //StringBuffer SB = new StringBuffer();
		try {
			toStream(DefaultFormatter.newInstance(OS)); //possible Recursion! 
		} catch (final IOException x) {
			throw new RuntimeException(x.toString());
		}
		return OS.toString(); }

	/**Fills this Instance with the Contents read from the streamIO.	 */
	public ICopyAble fromStreamAt(IDeserializer ST) throws IOException {
		return (ICopyAble) ST.loadItem(this); } //map all Errors to IOExceptions, because they should not happen!
/*		try { ST.loadItem(this); } //map all Errors to IOExceptions, because they should not happen!
		catch(ClassNotFoundException e) { throw new IOException(e.toString()); } //At expects this or a SuperClass, so it is known!
		catch(IllegalAccessException e) { throw new IOException(e.toString()); } //should be able to access all it's fields
		catch(InstantiationException e) { throw new IOException(e.toString()); } //no abstract Classes or Interfaces involved
		catch(  NoSuchFieldException e) { throw new IOException(e.toString()); } //only known Fields should occur!
		return this; }
*/
	/**Creates an uninitalized new Instance of it's class
	 * and fills it with the Contents read from the Stream.
	 * Default Implementation that can be overwritten by more effective ones.
	 */
	public ICopyAble fromStream(IDeserializer ST) throws IOException { return newInstance().fromStreamAt(ST); }
/*	throws ClassNotFoundException, IllegalAccessException, IOException {
		try{ return (CopyAble) ST.fromXML(); }
		catch(InstantiationException e) { throw new ClassNotFoundException(e.toString()); } //no abstract Classes or Interfaces involved
		catch(  NoSuchFieldException e) { throw new ClassNotFoundException(e.toString()); } //only known Fields should occur!
	}
*/
	/**fills this Instance with the Contents read from the String.	 */
	public ICopyAble fromStreamAt(InputStream ST) throws IOException {
		return fromStreamAt(DefaultParser.newInstance(ST)); }

	/**Creates an uninitalized new Instance of it's class
	 * and fills it with the Contents read from the String.	 */
	public ICopyAble fromStream(InputStream ST) throws IOException {
		return fromStream(DefaultParser.newInstance(ST)); }

	/**Creates an uninitalized new Instance of it's class
	 * and fills it with the Contents read from the String.	 */
	public ICopyAble fromString(String ST) {
		try { return fromStream(new StringBufferInputStream(ST)); }
		catch(IOException e) { throw new IllegalStateException(e.toString()); } }

	/**fills this Instance with the Contents read from the String.	 */
	public ICopyAble fromStringAt(String ST) {
		try { return fromStreamAt(new StringBufferInputStream(ST)); }
		catch(IOException e) { throw new IllegalStateException(e.toString()); } }

	//////////////
	//	Testing	//
	//////////////

	/** tests copying */
	private static final void testCopy(final ICopyAble testInstance) {
		final ICopyAble copy = testInstance.copy();
		Assert.IS_TRUE(copy != testInstance); 
		Assert.EQUALS(copy, testInstance); 
	}
	
	/** tests copying */
	private static final void testCopyAt(final ICopyAble testInstance) {
		ICopyAble copy; 
		int count = 0; 
		do { copy = testInstance.random();
			if (++count > 10) return; 
		} while (copy.equals(testInstance));
		Assert.IS_TRUE( copy != testInstance); 
		Assert.IS_TRUE(!copy.equals(testInstance)); 
		copy.copyAt(testInstance);
		Assert.EQUALS(copy, testInstance); 
	}
	
	/**Instance of a concrete Class derived from ACopyAble
	 * to be able to perform the Tests.
	 * This backward Reference is necessary,
	 * since only abstract Classes are defined in the first Packages.	 */
	public static ICopyAble testInstance;
	
	/**Method to test all Implementations in this class.
	 * Must call testIt of the super Class.	 */
	public static void testIt(final ICopyAble testInstance) throws Exception {
		//ACopy.testIt(testInstance);
		testCopy(testInstance);
		testCopyAt(testInstance);
		L.n("Original:"+testInstance);
		L.n(testInstance+".copy() =" + testInstance.copy( ));	//deepCopy
		L.n(testInstance+".copy(0)=" + testInstance.copy(0)); //shallowCopy
		L.n(testInstance+".shallowCopy() :" + testInstance.shallowCopy()); //shallowCopy
		L.n(testInstance+".newInstance() :" + testInstance.newInstance()); //
		final ICopyAble new_Instance = testInstance.newInstance();
		L.n(testInstance+".swap  (" + new_Instance + ") :" + testInstance.swap (new_Instance)); //
		L.n(new_Instance+".copyAt(" + testInstance + ") :" + new_Instance.copyAt(testInstance)); //
		final String newValue = new_Instance.toString();
		L.n(new_Instance+".toText()=" + newValue); //
		try {L.n("fromString(" + newValue + ")=" + new_Instance.fromString(new_Instance.toString())); //
		}catch (Exception e){L.n(e);}
//		L.n("() :" + new_Instance); //
	}

	/////////////////////////////////////////////////////////////////////////////////////
	/// Testing Framework
	/////////////////////////////////////////////////////////////////////////////////////
	
	private static final Class[] testParamTypes = { ICopyAble.class }; //Object.class};
	
	private static final Object[] testParams = new Object[1];
	
	/** tests with an Instance of the given Class Name */
	final static public void testIt(final Class tester, final String[] args) throws Exception {
		final String className = args.length > 0 ? args[0] : ABodyDouble.class.getName();
		testIt(tester, className);
	}
	
	/** tests with an Instance of the given Class Name */
	final static public void testIt(final Class tester, final String className) throws Exception {
		Object obj = Class.forName(className).newInstance();
		testIt(tester, obj);
	}
	
	/** tests with an Instance of the given Class Name */
	final static public void testIt(final Class tester, final Object obj) throws Exception {
		try {
			final Method method = tester.getMethod("testIt", testParamTypes);
			L.n("Testing "+tester+" with Instances of "+obj.getClass()+"\n", 10);
			testParams[0] = obj;
			method.invoke(null, testParams); //static Method doesn't require first arg!
		} catch (NoSuchMethodException x) {
			L.n("Found no Testing Method for Class "+tester+"!\n", 20);
		}
	}
	
}
