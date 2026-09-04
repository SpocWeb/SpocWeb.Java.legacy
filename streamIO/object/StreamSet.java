package streamIO.object; //.Enumerator;

import java.io.StreamTokenizer;
import java.util.Comparator;

import streamIO.AStreamOut;
import streamIO.Assert;
import streamIO.IMarkAble;
import streamIO.IReSetAble;
import streamIO.IIStreamIn;
import streamIO.Log;
import streamIO.StreamOutPrimitive;
import streamIO.copy.ICopyAble;
import streamIO.copy.boole.Lattice;
import streamIO.copy.boole.Boole;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ISemiGroup;
import streamIO.copy.groupM.ISemiGroupM;
import streamIO.integer.StreamIn_Arithmetic;
import streamIO.integer.filter.FilterIn_Int2Object;
import streamIO.object.filterInOut.FilterByFunction;
import function.AFunction;
import function.byref.ByRefLong;
import graphs.KeyValuePair;

/**
  * Title: StreamSet.java<p>
  * Description:
  * This is a Filter implementing the Interface 'Boole' for Streams of Objects.
  * This allows to represent (enumerable) infinite Sets
  * by having (enumerable) unlimited Input Streams.
  * 
  * This is the Set Equivalent to 'Algebra'
  * which allows symbolic and explicit Operations on Functions (mappings).
  * 
  * For symbolic Operations:
  * Codify Operations into Objects and apply them on Expressions,
  * until those Expressions have minimum Length
  * Use a Grammar Definition to generate the Object Structure from the Parse Tree. 
  * Use a Metric  Definition to define the "Result"
  * 
  * For Functions:
  *  f(~f) == ~f(f) == Id
  *  f(Const) = FConst
  *  Const(f) = Const
  * 
  * For Sets:
  *  ~(~A) == A //Negation
  *    A OR  B == B OR  A	//Commutative
  *    A AND B == B AND A	//Commutative
  *   (A OR  B) OR  C == A OR  (B OR  C)	//Associative
  *   (A AND B) AND C == A AND (B AND C)	//Associative
  *  ~(A AND B) == ~A OR  ~B 	//De Morgan
  *  ~(A OR  B) == ~A AND ~B 	//De Morgan
  * 
  * As Mathematics shows, the Convergence of Operations on infinite Sets
  * depends on the Sequence of Operations, that is why e.g. the OR Operation
  * interleaves Streams and the other Operations
  * require at least the Argument streamIO to be finite on Evaluation.
  * Symbolic Operations are supported via the simplify() Method
  *
  * Since Objects are from an (in Principle) unlimited Set,
  * Operations like NOT and Values like TRUE can not be defined explicitly,
  * both need a SuperSet to iterate from.
  * If no SuperSet is defined, a Set is assumed that contains any Element.
  *
  * SubSet and SuperSet can be checked in a more effective way than
  * defined in ALattice by creating
  * Known SubClasses:
  *
  * Design Decisions:
  * Inheriting from AStreamSet not from FilterIn, although this is a Filter,
  * because more Operations are already defined there than in FilterIn!
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-07, 01;06;24<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class StreamSet
extends AStreamSet { //FilterIn { //
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Log L = new Log(StreamSet.class, 0); 
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/**Boolean Constant for the Representation of 'false': 0
	 * i.e. NOT 'true'.
	 * For Sets: Returns the empty Set, which is identical for any Set	*/
	final static public StreamSet FALSE = new StreamSet(null, null); //FALSE); //null;
	
	/**Boolean Constant for the Representation of 'true': 1
	 * i.e. NOT 'false'.
	 * For Sets: The Set of ALL Elements
	 * (only posssible explicitly for known finite SuperSets) */
	final static public StreamSet TRUE = new StreamSet(null); //TRUE, TRUE); //FALSE.NOT(); // null; //or use 'null' to indicate both the empty and the full Set.
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Static Initializer avoiding Forward Reference 	 */
	//static { TRUE.Enum = TRUE; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Reference to the inner streamIO delegated to.
	 * It must be restartAble for many Operations.
	 * Otherwise I could also choose IStreamIn, which is a smaller Interface.
	 */
	protected IStreamIn enm;
	
	/**Boolean Constant for the Representation of 'true': 1
	 * i.e. NOT 'false'.
	 * For Sets: The Set of ALL Elements (only posssible for known finite SuperSets) */
	protected StreamSet True; //TODO: does this have to be a StreamSet?
	
	/** Current Item, cached for iterated Retrieval, cached here also for parsing
	  * Could be moved up into AStreamSet or be removed, because most Iterators have fast Access to the current Item	 */
	protected Object currItem; // = IStreamIn.SOI;
	
	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX/isXXX/makeXXX)
	////////////////////////////////////////////////////////////////////////////
	
	/** @return the Filter Object
	  * only Items that are equal to this Object are returned by nextItem()! */
	public Object getFilter() {
		return enm.getFilter(); }
	
	/** Sets the Filter Object
	  * only Items that are equal to this Object are returned by nextItem()!
	  * This allows for Optimizations on hashed and sorted Containers
	  * because the Result Set can be decreased dramatically. */
	public void setFilter(final Object Value) {
		enm.setFilter(Value); }
	
	/** @return the Order in which Elements are returned by the Iterators
	  * when they are added using addItem() and removed using nextItem().	 */
	public byte getOrder() {
	/*		if (Enum instanceof StreamIn) { //delegate
				return ((StreamIn) Enum).getOrder(); }
				return OrderUnDef; } //otherwise you don't know!
	*/			
		return enm.getOrder(); }
	
	/** @return The Comparator being used to compare Elements.
	  * If 'null', the Elements are assumed to implement
	  * @see IScalarMetric or
	  * @see Comparable  or
	  * @see IIOrderAble	 */
	public Comparator getComparator () { return null; }

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor taking the Input streamIO, that should be restartAble.	 */
	protected StreamSet(IStreamIn Enum) { this(Enum, TRUE); } //null); }
	
	/** Initializing Constructor taking the Input streamIO, that should be restartAble
	  * and the Reference to the Object representing the SuperSet
	  * that Operations like NOT are based on. 	 */
	protected StreamSet(IStreamIn Enum_, StreamSet True_) {
		//TODO: Check whether True is really a SuperSet of Enum
		this.enm = Enum_;
		this.True = True_; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface StreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns a new Input streamIO of the Objects in this Container.
	  * If this Container does not support multiple concurrent Iterators, returns 'null'
	  * @return  a new Input streamIO of the Objects in this Container.
	  * @see     Math.Iterator     */
	public IIStreamIn Iterator() {
		return new StreamSet((IStreamIn) enm.Iterator(), True); }

	/** Returns the current Object.
	  * Could be removed here, because most Iterators have fast Access to the current Item	 */
	public Object currItem() { return currItem; }

	//Marking and Resetting a Stream (for re-Processing, if supported)

	/** @see streamIO.object.AStreamSet#getPosition()	 */
	public long getPosition() { return enm.getPosition(); }

	/** @see streamIO.object.AStreamSet#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return enm.getMaxMarkSize(); }

	/** @see streamIO.object.AStreamSet#reSet(java.lang.String)	 */
	public IReSetAble reSet(final String failureExceptionMessage) { 
		return enm.reSet(failureExceptionMessage); }
	
	/**Resets the Iterator to the last marked Position,
	 * done automatically on Instantiation
	 * By Default the Start of the Iterator is marked on Instantiation	 */
	public IReSetAble reSet() { //throws NoSuchMethodException{ 
	    enm.reSet(0); return this; }

	/**Marks the current position in this Iterator.
	 * A subsequent call to the reset method repositions this Iterator
	 * at the last marked position.	 */
	public IMarkAble mark() { //throws NoSuchMethodException { 
	    enm.mark(); return this; }

	/**Returns the next Item without moving to it.	 */
	public Object peekItem() { //throws    NoSuchMethodException {
		return enm.peekItem(); }

	/**Resets the Iterator to the given Position
	 * counted from the last marked Position.	 */
	public long reSet(long Position) { //throws    NoSuchMethodException {
		return enm.reSet(Position); }

	/**Marks the current position in this Iterator.
	 * A subsequent call to the reset method repositions this Iterator
	 * at the last marked position.
	 * The readlimit arguments tells this input stream to allow that many Items
	 * to be read before the mark position gets invalidated.
	 * This is to limit the Blocking of System Ressources	 */
	public IMarkAble mark(final long ReadLimit) { //throws    NoSuchMethodException {
		enm.mark(ReadLimit); return this; }

	/** @return true, when the Items returned support the OrderAble Interface
	  * and they are returned in (strictly) ascending or descending Order.
	  * This is used as an additional criterion for Search Operations like findFirst()
	  * It is replaced by the @see Pipe.getOrder() Method: ordered, random, sorted
	  * Monotonous is implicitly sorted!	 */
	//public boolean isMonotonous() { return Enum.isMonotonous(); }

	////////////////////////////////////////////////////////////////////////////////
	//  Optimizations of StreamIn: Convenience Array Read Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////////

	/**Returns the (minimum) Number of Items left (in the Buffer).
	 * The actual Number may be higher, so available() should be called again
	 * at the End of this Number.
	 *
	 * Nearly equivalent is currItem != null
	 * (when the Container does not contain null Entries, like e.g. HashTables)
	 */
	public long availAble() { return enm.availAble(); }

	/**
	 * @see streamIO.IIStreamIn#isValid()
	 */
	public boolean isValid() { return enm.availAble() >= 0; }

	/**Returns the next (Parent) Object of this one.
	 * No Exception is thrown at the End, instead EOI is returned.
	 * When IO Processes are bound to this streamIO, IOException is wrapped into an IOError.
	 * This is less explicit, but much faster because Exception Handling can be extremely slow.
	 * Alternatively this Method can block until new Data is available,
	 * but this should always have a TimeOut to avoid DeadLocks.
	 */
	public Object nextItem() { return enm.nextItem(); }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface ILattice: Implementation
	////////////////////////////////////////////////////////////////////////////////

	/** Boolean Constant for the Representation of 'false' = 0
	  * i.e. not 'true'.
	  * For Conatainers this is equivalent to zeroAt() and clear()	 */
	public Boole FalseAt() { enm = null; return this; }

	/**Boolean Constant for the Representation of 'true': 1
	 * i.e. NOT 'false'.
	 * For Sets: The Set of ALL Elements (only posssible for known finite SuperSets) */
	public Boole TrueAt() { enm = True; return this; }

	/**Boolean Constant for the Representation of 'true': 1
	 * i.e. NOT 'false'.
	 * For Sets: The Set of ALL Elements (only posssible for known finite SuperSets) */
	public Boole True() { return True; }

	/** Boolean NOT Operation in Place: ~=, != for single Bit	*/
	public Boole NOTat	() { enm = new DIFF(True, enm); return this; }

	/** Boolean AND Operation in Place: &=, &&= for single Bit	*/
	public Lattice ANDat (Object arg) {
		//TODO: Check whether True is the same for both Arguments or choose the Super of both
		//This = ...;
		enm = new AND((IIStreamIn) this, (IStreamIn) arg);
		return this; }

	/** Boolean OR Operation in Place: |=, ||= for single Bit
	  * @return this |= arg
	  * |A OR B| == |A| + |B| - |A AND B|	 */
	public Lattice ORat  (Object arg) { //cannot be done with a Stream, only with a Container
		addAt(arg); return this; } //because it requires a finite Set and a definite Start!
		//otherwise use the Union Class which creates Duplicates...

	/**Boolean DIFF Operation in Place: -=
	 * a - b <=> (a AND NOT b) <=> NOT IMP
	 * For Sets:	Difference Set ; can also be defined without NOT!  */
	public Lattice DIFFat(Object arg) {
		enm = new DIFF(this, (IStreamIn) arg);
		//TODO: Check whether True is the same for both Arguments or choose the Super of both
		//This = ...;
		return this; }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface IGroup : Implementation
	////////////////////////////////////////////////////////////////////////////////

	/**Addition in Place: += 	 */
	public ISemiGroup addAt(Object arg) {
		IIStreamIn[] arg_ = new IIStreamIn[2];
		arg_[0] = this;
		arg_[1] = (IIStreamIn) arg;
		enm = new DeMultiplexerIn(arg_);
		//TODO: Check whether True is the same for both Arguments or choose the Super of both
		//This = ...;
		return this; }

	/**Subtraction in Place: -= 	 */
	public IGroup subAt (Object arg) { DIFFat(arg); return this; }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface IGroupM: Implementation
	////////////////////////////////////////////////////////////////////////////////

	/**Multiplication in Place: *=  	 */
	public ISemiGroupM mulAt(Object arg) {
		try {
			enm = new Product((IIStreamIn) this, (IStreamIn) arg);
		} catch (NoSuchMethodException x) { throw new NoSuchMethodError(x.toString()); }
		return this; }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface ICopy: Implementation
	////////////////////////////////////////////////////////////////////////////////

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Components.	 */
	public ICopyAble copyAt(Object arg, int Depth) {
	//	StreamIn arg_ = (StreamIn) arg_;
		switch(Depth) {
			case  0: break;
	//		case  1: Enum = ((StreamSet) arg).Enum.copy();
			default: enm = ((StreamSet) arg).enm;
		}
		return this; }

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() { return new StreamSet(null, True); }

	/**Fills this Instance with the Contents read from the String.	 */
	public ICopyAble fromStreamAt(StreamTokenizer arg) { //TODO: implement this.
		throw new AbstractMethodError(); }
		//TODO: not defined yet, possibly create an XML Object Stream from the Stream

	/**Method to simplify boolean Expressions and Variables.
	  *  ~(~A) == A //Negation
	  * (A-B)-B == A-B
	  *  A-(A-B) = B (if A >= B, i.e. is a SuperSet)
	  *  A-(B-A) = A
	  * (A AND B) AND B == A AND B //Transitivity
	  * (A OR  B) OR  B == A OR  B //Transitivity
	  *  A AND FALSE == FALSE //Neutral Element
	  *  A OR  TRUE  == TRUE  //Neutral Element
	  *  A AND TRUE  == A //Neutral Element
	  *  A OR  FALSE == A //Neutral Element
	  *
	  *    A OR  B == B OR  A	//Commutative
	  *    A AND B == B AND A	//Commutative
	  *   (A OR  B) OR  C == A OR  (B OR  C)	//Associative
	  *   (A AND B) AND C == A AND (B AND C)	//Associative
	  *  ~(A AND B) == ~A OR  ~B 	//De Morgan
	  *  ~(A OR  B) == ~A AND ~B 	//De Morgan
	  * Recognizes AND and OR Normal Form as well as DeMorgan
	  * and nested Negation and DIFF	*/
	public Boole simplify() {
		if (enm instanceof AND) {//   <=> ~(~A) == A //Negation
			AND Enum_ = (AND) enm; //~A = TRUE-A => ~~A = TRUE-(TRUE-A) = A
			if(Enum_.in  == ((StreamSet)Enum_.enum2).True()) return (Boole) Enum_.enum2; //TRUE AND A == A
			if(Enum_.enum2 == ((StreamSet)Enum_.in ).True()) return (Boole) Enum_.in ; //A AND TRUE == A
		}
		if (enm instanceof DIFF) {//   <=> ~(~A) == A //Negation
			DIFF Enum1 = (DIFF) enm; //~A = TRUE-A => ~~A = TRUE-(TRUE-A) = A
			if  (Enum1.enum2 instanceof DIFF) {//   <=> ~(~A) == A //Negation
				DIFF Enum2 = (DIFF) Enum1.enum2; //~A = TRUE-A => ~~A = TRUE-(TRUE-A) = A
				if (Enum1.in == Enum2.enum2) return (Boole) Enum2.in; //  A-(B-A) = A
				//  A-(A-B) = B (if A >= B, i.e. is a SuperSet)
				if (Enum1.in == ((StreamSet)Enum1.enum2).True()) //NOT Operation
				if (Enum2.in == ((StreamSet)Enum2.enum2).True()) //NOT Operation
					return (Boole) Enum1.enum2;
			}
		} return this; }

	//////////////////////////
	//	Join Operations		//
	//////////////////////////

	/** Joins a Table with Equality Equation between Column i of this Collection
	  * and Column j of arg. Therefore both this and arg have to be Tables
	  * (i.e. Collections of Collections)
	  * Since a new Container would be created, a streamIO is being used. */
	/*public Container joinByCols (int i, boolean[] Cols, Container arg, int j, boolean[] ColsArg) {
		Container res = (Container)  newInstance();
		Container o1; Iterator i1 =		Iterator();
		Container o2; Iterator i2 = arg.Iterator();
		res.ensureCapacity(getInt() + arg.getInt());	//the typical Join creates as many rows as the larger table has
		while ((o1 = (Container)i1.nextItem()) != Iterator.EOI) { // moreItems)) != null) {
			Object Test = o1.getAt(i);
			try { i2.reset(); } catch (OperationNotSupported e) { throw new AbstractMethodError(e.toString()); }
			while ((o2 = (Container) i2.nextItem()) != Iterator.EOI) // moreItems)) != null)
				if (o2.getAt(j).equals (Test))
					res.addAt (((Container) newInstance()).addColsAt(o1, Cols).addColsAt(o2, ColsArg));
		}
		return res; }

	/** Joins a Table with another and the Criterion given by 'Condition'.
	  * Therefore both this and arg have to be Tables (i.e. Collections of Collections)
	  * Since a new Container would be created, a Stream is being used. */
	/*public Container join (Container arg, ITester Condition) {
		Container res = (Container)  newInstance();
		Container o1; Iterator i1 =		Iterator();
		Container o2; Iterator i2 = arg.Iterator();
		res.ensureCapacity(getInt() + arg.getInt());	//the typical Join creates as many rows as the larger table has
		while ((o1 = (Container)i1.nextItem()) != Iterator.EOI) { // moreItems)) != null) {
			Container tmp;
			try { i2.reset(); } catch (OperationNotSupported e) { throw new AbstractMethodError(e.toString()); } //moreItems);
			while ((o2 = (Container) i2.nextItem()) != Iterator.EOI) // moreItems)) != null)
				if (Condition.Test (tmp = (Container)((Container)o1.copy()).addAt(o2)))
					res.addAt (tmp);
		}
		return res; }

	*/
	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt() throws Exception {
		try {
		testProd(); 
	//	AOrderAble.testIt(args); 
 		L.n("Testing " + StreamSet.class.getName()).l((IIStreamIn) null);
		IStreamIn CharStream = FilterIn_Int2Object.getCharacterStream();
		Assert.EQUALS(26, AStreamOut.STREAM(CharStream, new StreamOutPrimitive(), Integer.MAX_VALUE, false, false, ", ", Long.MAX_VALUE)); //short Method
		testStream(26, CharStream); 
	//	StreamSet Alph = new StreamSet(CharStream);
	//	StreamSet Vowel = new StreamSet(CharacterStream.VowelStream); 
	//	Object tmp = FilterIn_Char2Object.VowelStream.Iterator(); 
		final DIFF Consonants = new DIFF(CharStream, FilterIn_Int2Object.VowelStream); 
		testStream(21, Consonants); //CharStream.reSet(); FilterIn_Int2Object.VowelStream.reSet(); 
		final IStreamIn[] vowelsAndConsonants = {FilterIn_Int2Object.VowelStream, Consonants}; 
		DeMultiplexerIn AllChar2 = new DeMultiplexerIn(vowelsAndConsonants); 
		testStream(26, AllChar2); //CharStream.reSet(); FilterIn_Int2Object.VowelStream.reSet(); 
		Union AllChar1 = new Union(vowelsAndConsonants); 
		testStream(26, AllChar1); //CharStream.reSet(); FilterIn_Int2Object.VowelStream.reSet(); 
		testStream( 0, new AND(Consonants, FilterIn_Int2Object.VowelStream)); //CharStream.reSet(); //FilterIn_Int2Object.VowelStream.reSet(); 
		testStream( 5, new AND(FilterIn_Int2Object.VowelStream, FilterIn_Int2Object.VowelStream)); //CharStream.reSet(); FilterIn_Int2Object.VowelStream.reSet();
		//TODO: uses the same Iterator... 
		testStream(21, new AND(Consonants, Consonants)); //CharStream.reSet(); FilterIn_Int2Object.VowelStream.reSet(); 
		final IStreamIn numberStream = new FilterIn_Int2Object(new StreamIn_Arithmetic(0, 100));
		testStream(100, numberStream); //takes very long otherwise!
		numberStream.reSet(); 
		IStreamIn even = new FilterByFunction(numberStream, new DblAt());
	//	Log.Out.add(even).println(); //takes infinitely long otherwise!
		numberStream.reSet();
		IStreamIn odd = new DIFF(numberStream, even); //Log.Out.Separator = "\n";
		testStream(50, odd); //takes very long otherwise!
		numberStream.reSet();
		testProd();
		} catch (NoSuchMethodException x) { throw new java.io.IOException(x.toString()); }
	}
	
	/**
	 * @param CharStream
	 */
	private static void testStream(final int expectedCount, final IStreamIn CharStream) {
		L.n(); Assert.EQUALS(expectedCount, L.addItems(CharStream));
	}

	/**
	 * @throws NoSuchMethodException
	 */
	private static void testProd() throws NoSuchMethodException {
		char[] password = new char[] { 'a', 'b', 'c'}; 
		try {
			java.io.FileWriter writer = new java.io.FileWriter("C:/_/password.txt");
			writer.write("Password='" + new String(password) + "'");
			writer.close(); 
			System.out.println("Password='" + new String(password) + "'"); 
		} catch(Exception x) {}
		final IStreamIn CharStream = new CopyStreamIn(new FilterIn_Int2Object(new StreamIn_Arithmetic('A','E'+1)));
		final IStreamIn NumbStream = new CopyStreamIn(new FilterIn_Int2Object(new StreamIn_Arithmetic(0,7)));
		IStreamIn Prod; //Testing regular and special Cases:
		testStream(7*5, new Product(CharStream, NumbStream)); 
		testStream(7*5, new Product(FilterIn_Int2Object.VowelStream, NumbStream)); 
		testStream(7*0, new Product(CharStream, new FilterIn_Int2Object(new StreamIn_Arithmetic(0, 0)))); 
		testStream(7*0, new Product(CharStream, new FilterIn_Int2Object(new StreamIn_Arithmetic(1,-1)))); 
		testStream(7*5, new Cantor (CharStream, NumbStream)); 
		testStream(7*5, new Cantor (new CopyStreamIn(FilterIn_Int2Object.VowelStream), NumbStream)); 
		testStream(7*0, new Cantor (CharStream, new CopyStreamIn(new FilterIn_Int2Object(new StreamIn_Arithmetic(0,0))))); 
		testStream(7*0, new Cantor (CharStream, new CopyStreamIn(new FilterIn_Int2Object(new StreamIn_Arithmetic(1,0))))); 
		
		L.n("Create a higher Dimensional Product (Tripel):");
		IStreamIn CharStream2 = new FilterIn_Int2Object(new StreamIn_Arithmetic('x','z'+1));
		testStream(7*5*3, new Product(CharStream, new Product(NumbStream, CharStream2))); 
		
		L.n("Create a higher Dimensional Cantor Product (Tripel):");
 		testStream(7*5*3, new Cantor (CharStream, new Cantor (NumbStream, CharStream2))); 
		
		L.n("Enumerating all rational Numbers");
		L.n("When using Cantor, you have to create a CopyStream of the first Input Stream!");
		L.n("There is a much easier Way to derive the Cantor Product Stream:");
		L.n("map all Items to Integer Numbers and generate all Pairs (Tripels... etc.)");
		L.n("with the same integer Sum!");
		Prod = new Cantor (new CopyStreamIn(
			new FilterIn_Int2Object(new StreamIn_Arithmetic(0,9))),
			new FilterIn_Int2Object(new StreamIn_Arithmetic(0,9)));
		L.n("Number of full Cross Product: ").l(AStreamOut.GET_NUM_ITEMS(Prod)).println(); Prod.reSet();
	//	L.n(Prod); Prod.reset(); //
		IStreamIn rat = new NullFilterIn(new FilterByFunction(Prod, new NullWhenDivisible()));
		L.n("Number of reduced Cross Product: ").l(AStreamOut.GET_NUM_ITEMS(rat)).println();
		rat.reSet();
		L.n("Product: ").addItems(rat); //Prod.reset(); L.n().l(Prod); Log.Out.add(Prod);
	
		L.n("after Setup:");
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
		testIt(); 
	}

}

/** Stateless Function Class (Singleton) to double the Input Value of a ByRefLong.
  * Just for testing Purposes. */
class DblAt
extends AFunction {

	/** Optimization: reusing the same ByRefLong Object */
	protected ByRefLong myVal = new ByRefLong();

	/**Returns arg mapped by this Object: this.map(arg) == this°arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	 */
	public Object Map (final Object arg) {
		if (null == arg) 
			return  arg;
		myVal.Value = 2*((ByRefLong)arg).Value;
		return myVal; } //new ByRefLong(); }

}

/** Stateless Function Class (Singleton) to double the Input Value of a ByRefLong.
  * Just for testing Purposes. */
class NullWhenDivisible
extends AFunction {

	/**Returns arg mapped by this Object: this.map(arg) == this°arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	 */
	public Object Map (final Object arg) {
		if (null == arg) return arg;
		KeyValuePair arg_ = (KeyValuePair) arg;
		long Key = ((ByRefLong) arg_.key).Value;
		long Val = ((ByRefLong) arg_.val).Value;
		if (ByRefLong.GGT_CLASSIC(Key, Val) == 1) {
			return arg; }
		return null; }

}
