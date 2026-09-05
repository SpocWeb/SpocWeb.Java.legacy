package function.derive;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import streamIO.IDeserializer;
import streamIO.IFormatOut;
import streamIO.IInstantiAble;
import streamIO.copy.CCopyAble;
import streamIO.copy.ICopyAble;
import streamIO.copy.boole.Boole;
import streamIO.copy.boole.Lattice;
import streamIO.exception.ReadOnlyException;
import graphs.ICopy;

/**
  * Title: Ternary<p>
  * Description:
  * Purpose:
  * Enumeration Example for Ternary boolean Values
  * Purpose / Responsibilities of this Class
  *
  * Implementation Details:
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * For Boolean (2) Functions with 2 Input Values there are
  * 2^(2^2) = 16 possible Functions
  * For Ternary (3) Functions with 2 Input Values there are
  * 3^(3^2) = 7625597484987 possible Functions!
  * Truth Table: for EQV       and XOR
  *  A  B !A !B (!A|B)&(!B|A) (!A&B)|(!B&A)
  * -1 -1  1  1   1   & 1 = 1   -1  | -1 = -1
  * -1  0  1  0   1   & 0 = 0    0  | -1 =  0
  * -1  1  1 -1   1   &-1 =-1    1  | -1 =  1
  *  0 -1  0  1   0   & 1 = 0   -1  |  0 =  0
  *  0  0  0  0   0   & 0 = 0    0  |  0 =  0
  *  0  1  0 -1   1   & 0 = 0    0  | -1 =  0
  *  1 -1 -1  1  -1   & 1 =-1   -1  |  1 =  1
  *  1  0 -1  0   0   & 1 = 0   -1  |  0 =  0
  *  1  1 -1 -1   1   & 1 = 1   -1  | -1 = -1
  * So it is still EQV == !XOR
  * and EQV is intuitively correct in that
  * as soon as one Operand is 0/null the Result is null
  * and otherwise it is the boolean EQV Function!
  *
  * De Morgans Laws still apply:
  * !(A&B) == !A | !B
  * !(A|B) == !A & !B
  *
  * Known SubClasses:
  *
  * Known Uses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-29-2002, 07:01 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:29:30Z
  * digest: 7285306fbed827a680140b2a5cc2715961b2463f7d8502da6652c4fc0574a75a
  * stale: false
  * tags: [code/enum_modeling, code/boolean_algebra]
  * concepts: [Three-Valued Logic, Lattice Theory]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
final public class Ternary
extends Enum //ACLattice
implements Boole {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Constant denoting FALSE	 */
	final static public String STR_FALSE = "False";

	/** Constant denoting NULL or Unknown	 */
	final static public String STR_NULL  =  "Null";

	/** Constant denoting TRUE	 */
	final static public String STR_TRUE  = "True";

	/** Constant denoting FALSE	 */
	final static public byte FALSE = -1;

	/** Constant denoting NULL or Unknown	 */
	final static public byte NULL  =  0;

	/** Constant denoting TRUE	 */
	final static public byte TRUE  =  1;

	/** The Offset of the Month Values and their Position in the Enum */
	final static public byte OFFSET = -1;

	/** List of Names for the Enums */
	protected static final String[] NAMES = { STR_FALSE, STR_NULL, STR_TRUE };

	/** List of Names for the Enums */
	protected static final byte[] VALUES = { FALSE, NULL, TRUE };

	/** Constant denoting TRUE	 */
	protected static final Enum[] LIST = CREATE_LIST(NAMES, OFFSET, new Ternary());

	/** Constant denoting FALSE	 */
	final static public Ternary False = (Ternary) LIST[FALSE-OFFSET];

	/** Constant denoting NULL or Unknown	 */
	final static public Ternary Null  = (Ternary) LIST[NULL -OFFSET];

	/** Constant denoting TRUE	 */
	final static public Ternary True  = (Ternary) LIST[TRUE -OFFSET];
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Interface Enum: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/** Used by the createList Method to create Instances for the List */
	protected Enum newEnum(long val_, long Offset_, Enum[] list_, String[] names_, Hashtable EnumsByName_) {
		return new Ternary(val_, Offset_, list_, names_, EnumsByName_); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor */
	private Ternary() {}
	
	/**
	 * Initializing Constructor
	 * @param val  the Value for this Enum
	 * @param list the Enumeration this Enum belongs to
	 */
	private Ternary(final long val_, final long Offset_, final Enum[] list_, final String[] names_, final Hashtable EnumsByName_) {
		super(val_, Offset_, list_, names_, EnumsByName_); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface Lattice: abstract Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests whether this Value is {@link #False}. @return true, when this Object is false 	*/	public boolean isFalse () { return this == False; }
	/** Tests whether this Value is {@link #True}. @return true, when this Object is true  	*/	public boolean isTrue  () { return this == True ; }
	/** Compares this Ternary's ordinal Value to arg's. @return this <= arg 	*/	public boolean SubEq  (Object arg) { return Value <= ((Ternary) arg).Value; }
	/** Compares this Ternary's ordinal Value to arg's. @return this >= arg 	*/	public boolean SuperEq(Object arg) { return Value >= ((Ternary) arg).Value; }
	/** Compares this Ternary's ordinal Value to arg's. @return this <  arg 	*/	public boolean Sub    (Object arg) { return Value <  ((Ternary) arg).Value; }
	/** Compares this Ternary's ordinal Value to arg's. @return this >  arg 	*/	public boolean Super  (Object arg) { return Value >  ((Ternary) arg).Value; }

	/** DIFF : A-B == A&!B 	 */
	public Lattice DIFF(Object arg) {
		return AND(((Boole) arg).NOT()); }

	/** XOR  : A^B == (A&!B) | (!A&B) = (A-B) + (B-A)	 */
	public Lattice XOR (Object arg) {
		Lattice arg_ = (Lattice) arg;
		return this.DIFF(arg_).OR(arg_.DIFF(this)); }

	/** AND Operation in Place: &=
	  * This corresponds to the MinAt Operation.
	  * @return a & b
	  * a AND b = true <=> (a = true) AND (b = true) 	 */
	public Lattice AND (Object arg) {
		Ternary arg_ = (Ternary) arg;
		if (Value > arg_.Value) {
			return arg_; }
		return this; }

	/** OR Operation in Place: |=
	  * This corresponds to the MaxAt Operation.
	  * @return a | b
	  * a OR b = true <=> (a = true) OR (b = true) 	 */
	public Lattice OR  (Object arg) {
		Ternary arg_ = (Ternary) arg;
		if (Value < arg_.Value) {
			return arg_; }
		return this; }

	/** Returns the constant {@link #False} Value. @return false  */
	public Boole False() { return False; }

	/** Returns the constant {@link #True} Value. @return true  */
	public Boole True() { return True; }

	/** Boolean NOT Operation in Place: ~=, != for single Bit
	  * @return !a
	  * NOT a = true <=> (a = false)
	  * This Operation cannot be implemented by infinite Sets,
	  * Therefore you need other means to define some Operations.	 */
	public Boole NOT () {
		return (Ternary) LIST[(int)(-Value-OFFSET)]; }

	/** Boolean Implication: !this OR arg. @return IMP, this implies arg */
	public Boole IMP (Object arg){ return (Boole) (NOT().OR(arg)); }
	/**Computes the ternary Equivalence: {@link #Null} if either Operand is Null, else True/False by Identity.
	 * @return EQV, the Equivalence is defined in the Header */
	public Boole EQV (Object arg){
		if ((this == Null) ||
			(arg  == Null)) {
			return   Null ; }
		if  (this == arg) {
			return   True ; }
			return   False; }

	/** Returns this immutable Value unchanged, since Ternary constants are shared Singletons. */
	public ICopy    Copy() { return this; }
	/** Returns this immutable Value unchanged, since Ternary constants are shared Singletons. */
	public ICopyAble copy() { return this; }
	/** Returns this immutable Value unchanged, since Ternary constants are shared Singletons. */
	public ICopyAble copy(int Depth) { return this; }
	/** Returns this immutable Value unchanged, since Ternary constants are shared Singletons. */
	public ICopyAble shallowCopy() { return this; }
	/** Writes this constant to the given output Format. */
	public void     toStream  (IFormatOut arg) { arg.addItem(this); }
	/** Returns this constant unchanged; genuine deserialization is not implemented. */
	public ICopyAble fromStream(IDeserializer ST) { return this; }
	/** Returns this constant unchanged; genuine deserialization is not implemented. */
	public ICopyAble fromStream(InputStream ST) throws IOException { return this; } //fromStreamAt(DefaultParser.newInstance(ST)); }
	/** Returns this constant unchanged; genuine parsing is not implemented. */
	// TODO: LOGIC: fromString() ignores its 'ST' argument and always returns 'this' instead of
	// parsing "-1"/"0"/"1" or "true"/"false"/"null" into the matching Ternary constant, as the
	// TODO comment below already notes; any caller relying on round-tripping a serialized Ternary
	// silently gets back the wrong constant.
	public ICopyAble fromString(String ST) { return this; }
	///TODO: implement reading -1,0 or 1
	///as well as 'true', 'false' and 'null'

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Lattice: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Always throws, since Ternary constants are read-only Singletons that cannot be created In-Place. */
	public IInstantiAble NewInstance() { throw new ReadOnlyException(CCopyAble.strConst); }
	/** Always throws, since Ternary constants are read-only Singletons that cannot be created In-Place. */
	public ICopyAble     newInstance () { throw new ReadOnlyException(CCopyAble.strConst); }
	/** Always throws, since Ternary constants are read-only Singletons that cannot be copied In-Place. */
	public ICopyAble     copyAt      (Object arg, int Depth) { throw new ReadOnlyException(CCopyAble.strConst); }
	/** Always throws, since Ternary constants are read-only Singletons that cannot be copied In-Place. */
	public ICopyAble     copyAt      (Object arg) { throw new ReadOnlyException(CCopyAble.strConst); }
	/** Always throws, since Ternary constants are read-only Singletons that cannot be deserialized In-Place. */
	public ICopyAble     fromStreamAt(streamIO.IDeserializer arg) { throw new ReadOnlyException(CCopyAble.strConst); }
	/** Always throws, since Ternary constants are read-only Singletons that cannot be deserialized In-Place. */
	public ICopyAble     fromStreamAt(InputStream arg) { throw new ReadOnlyException(CCopyAble.strConst); }
	/** Always throws, since Ternary constants are read-only Singletons that cannot be copied In-Place. */
	public ICopyAble    shallowCopyAt(Object arg) { throw new ReadOnlyException(CCopyAble.strConst); }
	/** Always throws, since Ternary constants are read-only Singletons that cannot be swapped In-Place. */
	public ICopyAble     swap        (Object arg) { throw new ReadOnlyException(CCopyAble.strConst); }
	/** Always throws, since Ternary constants are read-only Singletons that cannot be parsed In-Place. */
	public ICopyAble     fromStringAt(String arg) { throw new ReadOnlyException(CCopyAble.strConst); }

	/** AND  in Place: &=	 */	public Lattice ANDat	(Object arg) { throw new ReadOnlyException(CCopyAble.strConst); }
	/** OR   in Place: |=	 */	public Lattice ORat	(Object arg) { throw new ReadOnlyException(CCopyAble.strConst); }
	/** DIFF in Place: -=	 */	public Lattice DIFFat	(Object arg) { throw new ReadOnlyException(CCopyAble.strConst); }
	/** XOR  in Place: ^=	 */	public Lattice XORat	(Object arg) { throw new ReadOnlyException(CCopyAble.strConst); }

	/** Always throws, since Ternary constants cannot be set to false In-Place. @return false	*/	public Boole FalseAt() { throw new ReadOnlyException(CCopyAble.strConst); }
	/** Always throws, since Ternary constants cannot be set to true In-Place. @return true	*/	public Boole TrueAt () { throw new ReadOnlyException(CCopyAble.strConst); }
	/** Always throws, since Ternary constants cannot be negated In-Place. @return NOT: !	*/	public Boole NOTat  () { throw new ReadOnlyException(CCopyAble.strConst); }
	/** Always throws, since Ternary constants cannot be implicated In-Place. @return IMP: =>	*/	public Boole IMPat  (Object arg){ throw new ReadOnlyException(CCopyAble.strConst); }
	/** Always throws, since Ternary constants cannot be compared for equivalence In-Place. @return EQV: =>	*/	public Boole EQVat  (Object arg){ throw new ReadOnlyException(CCopyAble.strConst); }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Ternary.class.getName());
		testIt(False);
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); 
	}

	/**Returns a uniformly random Ternary constant, delegating to {@link #random()}.
	 * @see streamIO.copy.IICopyAble#randomizeAt()	 */
	public ICopyAble randomizeAt() {
		return random();
	}

	/**Returns a uniformly random Ternary constant: {@link #False}, {@link #Null} or {@link #True}.
	 * @see streamIO.copy.IICopyAble#random()	 */
	public ICopyAble random() {
		int ran = (int)(Math.random()*3);
		switch (ran) {
			case 0 : return False;
			case 1 : return Null;
			case 2 : return True;
			default : throw new IndexOutOfBoundsException("Unexpected switch Value:"+ran);
		}
	}

}

