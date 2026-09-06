package function.derive;

import java.util.Hashtable;

import streamIO.IIStreamIn;
import streamIO.IIterAble;
import streamIO.object.ArrayStreamIn;

/**
  * Title: Enum<p>
  * Description:
  * Purpose:
  * Abstract Base Class for any Enumeration.
  *
  * To actually define an Enum, create a Subclass
  * and use the static createList() Method.
  * Subclasses can creates an internal List of Values
  * that can be formatted into and parsed from Strings
  * and also contiguous (Distance 1) integer Values.
  *
  * Some Effort has been put into creating Enums easily
  * by just giving them a String[].
  *
  * Enumerations are a common Means to structure and describe limited Size Sets.
  * They are also used as States in a State Machine
  * and to define Parameters for limited State Methods.
  * A third Application is the Flyweight / Singleton Pattern,
  * where a limited Size Set can considerably conserve Memory!
  *
  * The actual Value and Order Relation of an Enum is usually not important,
  * only the Fact that it can be used in a switch () Statement.
  * For Enums denoting discrete Sections of a Dimension like Months, Hours etc.
  * the Order Relation actually is important, but only within a Period!
  *
  * Sets of Enums can very well be maintained by a BitVector
  * or easier, a long Variable containing Space for at most 64 Items.
  * Parsing a String could return such a BitVector or alternatively
  * a streamIO of Enums.
  *
  * Design Decisions / Implementation Details:
  * Using short on purpose to force Users to cast or use predefined Constants!
  * Making the Value writeAble by adding a setValue() Method results in:
  * * allowing to hand back a Result ByRef, although also an Array could do that!
  * * non constant-ness which opens up Complexities in Algorithms and Concurrency!
  * * requires a Runtime Check by Value in setValue()
  * * replaces fast Identity Check with slower equals Method.
  * * Exactly for Enums with its fixed Set of Members (unlike Strings)
  *   Constant Members are ideal! They implement the Flyweight Pattern!
  * * The Flyweight Pattern also saves Memory
  *   as well as expensive Creation and Destruction of Objects.
  *
  * If you want ByRef Handover, you can use Arrays for that!
  *
  * Known SubClasses:
  *
  * Known Uses:
  *
  * Similar Classes:
  * @see structure.Enum which is only a small Test Implementation.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-29-2002, 06:22 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:17:11Z
  * digest: 25af641be81fcea98462fa887a5a0a666ed9c9cda219eb12a09a026f612fdc5d
  * stale: false
  * tags: [code/enum_modeling, code/flyweight_pattern]
  * concepts: [Flyweight Pattern]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public abstract class Enum
extends CCountAble
implements IIterAble {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Use this Method in a static Initializer of any Subclass!
	 *
	 * Creates and fills a List of Enums from the given List of Values
	 * The Values don't need to be contiguous but should be ordered ascending!
	 * They are ordered beforehand to speed up Search and Access.
	 * @param names the parseable Names of the Items
	 * @param Offset the Value of the 0th Name
	 * @any An Instance of the Type to create.
	 */
	protected static Enum[] CREATE_LIST(final String[] names, final int Offset, final Enum any) {
		int len = names.length;
		Enum curr;
		Enum[] ret = new Enum[len];
		final Hashtable hash = new Hashtable(len);
		while (--len >= 0) {
/*			if (len == any.Value) {
				ret[len] = any; //reuse the Instance
				continue; }
*/			ret[len] = curr = any.newEnum(len+Offset, Offset, ret, names, hash);
			hash.put(names[len], curr);
/*			curr.EnumsByName = hash;
			curr.Offset= Offset;
			curr.Value = len+Offset;
			curr.names = names;
			curr.list  = ret;
*/		}
		return ret; }
	
	/**
	 * Creates and fills a List of Enums from the given List of Values
	 * The Values don't need to be contiguous but should be ordered ascending!
	 * They are ordered beforehand to speed up Search and Access.
	 */
	/*protected static Enum[] createList (long[] list, Enum any) {
		int len;
		long curr, last = Long.MIN_VALUE;
		Enum[] ret = new Enum[len = list.length];
		while (--len >= 0) {
			if (last <= (curr = list[len])) {
				throw new IllegalArgumentException("The List has to be strictly monotonic ascending! "
				+ "Last Value:" + last + " at Position " +(len+1)
				+ "Curr Value:" + curr + " at Position " + len ); }
			if (curr == any.Value) {
				ret[len] = any; //reuse the Instance
				continue; }
			(ret[len] = any.newEnum()).Value = curr;
		}
		return ret; }
		*/
	/**
	 * Using short on purpose to force casting or using predefined Constants
	 * Either tries to access the Elements directly or uses Bisection.
	 * @return the Enum for the given Value
	 */
	/*protected static Enum getEnum(Enum[] list, short val) {
		int TestPos = (int)  ( ( val  -list[0].Value)*list.length/
			(list[list.length-1].Value-list[0].Value)); //using proportional Search
		if (TestPos < 0) {
			return null; }
		if (TestPos >= list.length) {
			TestPos  = list.length-1; }
		while (list[TestPos].Value > val) { //linear Search from here!
			if (--TestPos >= 0) {
				continue; }
			return null; }
		while (list[TestPos].Value < val) {
			if (++TestPos < list.length) {
				continue; }
			return null; }
		return list[TestPos]; }
		*/
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
		
	/**
	 * the Offset between the Position i and the Value := i+Offset.
	 * Since most Enums are consecutive, it pays off to bring these into Tune.
	 * Thus an Enum will allow a bijective Mapping between integers and Objects.
	 * Enum[i].Value = i+Offset
	 * Position = Enum.Value - Offset
	 */
	protected final long Offset;
	
	/** Reference to the List of Enum Objecs (Flyweights)	 */
	protected final Enum[] list;
	
	/** Reference to the List of Enum Names	 */
	protected final String[] names;
	
	/** Reference to the List of Enums indexed by Name	 */
	protected final Hashtable EnumsByName;
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Using short on purpose to force casting or using predefined Constants.
	 * Inverse Method to getValue()
	 * @return the Enum for the given Value
	 */
	public Enum getEnum(final long val) { return list[(int) (val-Offset)]; }
	
	/**
	 * Inverse Method to getName()
	 * Have to do either a linear Search or use a HashTable!
	 * @return the Enum for the given Value
	 */
	public Enum getEnum(final String name) { return (Enum) EnumsByName.get(name); }
	
	/**
	 * Returning short on purpose
	 * @return the Enum for the given Value
	 */
	public short getValue() { return (short) Value; }
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Interface Object: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Use the Definition in the Parent Class CCountAble!
	 * @param arg the Object to compare to
	 * @return true if arg == this or if the Value matches.
	 */
	//public boolean equals(Object arg) { return (arg == this); } //use Identity, which is the Default!
	
	/**
	 * Returning short on purpose
	 * @return the String for this Enum
	 */
	public String toString() { return names[(int)(Value-Offset)]; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor, creates an empty Enum */
	protected Enum() { this(0, 0, null, null, null); }

	/**
	 * Initializing Constructor (only for Subclasses)
	 *
	 * To actually define the Subclass, create a Subclass
	 * and use the static createList() Method.
	 *
	 * @param val  the Value for this Enum
	 * @param list the Enumeration this Enum belongs to
	 * @param Offset the Offset between the Position i and the Value = i+Offset.
	 * @param list[] the List of Instances to create
	 * @param names  the List of Names to refer to
	 * @param EnumsByNames the HashMap of Strings to actual Enums.
	 */
	protected Enum(long val_, long Offset_, Enum[] list_, String[] names_, Hashtable EnumsByName_) {
		super(val_);
		this.Offset = Offset_;
		this.list   = list_  ;
		this.names  = names_ ;
		this.EnumsByName = EnumsByName_;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Interface Enum: abstract Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** protected Factory Method, used by the createList Method to create Instances for the List
	 *  Takes the same Parameters as the Constructor.
	 */
	protected abstract Enum newEnum(long val_, long Offset_, Enum[] list_, String[] names_, Hashtable EnumsByName_);
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface Integer: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/**Returns the next Enum in list order, or {@code null} when this is already the last one.
	 * @return the Successor of the current Enum */
	public Enum succ() {
		final int pos = (int) (Value - Offset);
		if (pos >= list.length-1) {
			return null; }
			return list[pos+1]; }

	/**Returns the previous Enum in list order, or {@code null} when this is already the first one.
	 * @return the Predecessor of the current Enum */
	public Enum pred() {
		final int pos = (int) (Value - Offset);
		if (pos <= 0) {
			return null; }
			return list[pos-1]; }

	/**Returns the first Enum in the underlying list, by list position rather than Value order.
	 * @return the Minimum Value in the List of Enums */
	public Enum getMinValue() { return list[0]; }

	/**Returns the last Enum in the underlying list, by list position rather than Value order.
	 * @return the Maximum Value in the List of Enums */
	public Enum getMaxValue() { return list[list.length-1]; }
	
	/** Returns a new Input streamIO of the Objects in this Container
	  * in exactly the same State as this one.
	  * If this Container does not support multiple concurrent Iterators, returns 'null'
	  * @return  a new Input streamIO of the Objects in this Container.
	  * @see     Math.Iterator     */
	public IIStreamIn Iterator() {
		return new ArrayStreamIn(list); }
	
	/**
	 * Using short on purpose to force casting or using predefined Constants
	 * Either tries to access the Elements directly or uses Bisection.
	 * To avoid storing the List in each Element, you can hand over a static List.
	 * should not return the List itself, but only a copy!
	 * @return a Copy of the List of Enums
	 */
	public Enum[] getEnum() {
		Enum[] ret = new Enum[list.length];
		System.arraycopy(list, 0, ret, 0, list.length);
		return ret; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(Enum arg) { //throws java.io.IOException {
		System.out.println("Testing " + Enum.class.getName());
		IIStreamIn stream = arg.Iterator();
		Enum curr;
		while (null != (curr = (Enum) stream.nextItem())) {
			System.out.println(
				curr + " " +
				curr.getValue() + " " +
				curr.getEnum(curr.getValue()) + " " +
				curr.getEnum(curr.toString()));
		}
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
//	public static void main (String[] args) { testIt(args); }

}

