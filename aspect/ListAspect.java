package aspect;

import java.util.ArrayList;

import synch.InvalidException;

/**
  * Title: ListAspect<p>
  * Description:
  * Purpose:
  * Extends and implements the Aspect Class for Objects (Tables)
  * Purpose / Responsibilities of this Class
  *
  * Design Decisions:
  * Instead of using a ListAspect containing ListAspects (generic),
  * analogous to a streamIO containing Streams,
  * or a 2D Array, which opens up the inner structure,
  * better use a ListAspect containing Arrays (= DataTable, nested 2D Array) or Records!
  * Since Aspect Record Fields can be accessed via their Column Names
  * they can be treated like Arrays, not considering their hierarchical structure!
  * And additionally they can be used externally
  * as real Objects without having to copy them!
  *
  * Implementation Details:
  * The Aspect defines a Reference to the List,
  * which again holds References to the individual Objects,
  * the Value and the Name of the identifying Column
  * or alternatively a Reference to the Object selected.
  *
  * Known SubClasses:
  *
  * Known Uses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-01-2002, 05:26 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class ListAspect
extends AHierarchyAspect { //Aspect {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** The Value of this Aspect */
	protected IAspect Value; //
	
	/** The Value of this Aspect */
	protected ArrayList list; // = new ArrayList(); //better redefine the Size in the Constructor!
	
	/** The ID "Column" of this Aspect List */
	protected String IdCol; //
	
	/** The BaseClass for all Elements of this List Aspect
	  * To be able to treat all Objects in the List uniformly,
	  * they have to implement the same Interface.
	  * If the List would also implement this Interface,
	  * the Composite Pattern would be fulfilled.
	  */
	protected Class BaseClass; //
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Variable 'MinLength' with Accessor Methods
	/// This is independent of 'Required'.
	/// A non required Field can still have a MinLength > 0
	/// indicating to enter either Nothing or at minimum the given Number of Characters.
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor for Standalone String Aspects	 */
	public ListAspect(final String name) { super(name, null); }

	/** Initializing Constructor	 */
	public ListAspect(final String name, final IHierarchyAspect Parent) { 
		super(name, Parent); }

	/** Initializing Constructor for Standalone String Aspects	 */
	public ListAspect(final String name, final String IdCol) { 
		super(name, null);
		this.list = new ArrayList();
		this.IdCol = IdCol; }

	/** Initializing Constructor	 */
	public ListAspect(final String name, final String IdCol, final IHierarchyAspect Parent) {
		super(name, Parent);
		this.list = new ArrayList();
		this.IdCol = IdCol; }

	/** Initializing Constructor for Standalone String Aspects	 */
	public ListAspect(final String name, final String IdCol, final int InitialSize) { 
		super(name, null);
		this.list = new ArrayList(InitialSize);
		this.IdCol = IdCol; }

	/** Initializing Constructor	 */
	public ListAspect(final String name, final String IdCol, 
			final int InitialSize, final IHierarchyAspect Parent) {
		super(name, Parent);
		this.list = new ArrayList(InitialSize);
		this.IdCol = IdCol; }

	/// How do you name an Element of a List?
	/// by Index or not at all (just listing it up)?
	/// Sometimes the Order is important,
	/// but usually the Role should be determined
	/// either by specific Member Variables
	/// or by inherent Data in the Row.
	///
	/// A Possibility to smoothen the Border between named
	/// and unnamed (open ended) Elements would be nice!
	///
	/// Thus the primary Address would not have to appear in the Addresses List again,
	/// but can transparently be found when looping over the Addresses in this Object.
	///
	///
	///

	/** @return a new Instance of this Object */
	//	public IAspect newInstance(String Name) {
	//		return new ListAspect(Name, null); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface Aspect: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return a certain (indexed) Aspect Value as a (boxed) Object  */
	public IAspect getAt(final int i) {
		return (IAspect) list.get(i); }
	
	/** @return a certain (indexed) Aspect Value as a (boxed) Object
	  * where the Property Prop has the given Value.
	  * only the first Match is returned (Prop should be a unique key)
	  */
	public IAspect getVal(final String Prop, final Object val) {
		IAspect curr, currProp;
		int i = list.size();
		while (--i >= 0) {
			curr = (IAspect) list.get(i);
			currProp = curr.getField(Prop);
			if  ((val  == currProp) ||
				((null != currProp) && (currProp.equals(val)))) {
				return curr; }
		} return null; }
	
	/** @return a certain (indexed) Aspect Value as a (boxed) Object
	  * where the Property Prop has the given Value.
	  * only the first Match is returned (Prop should be a unique key)
	  */
	public ArrayList getVals(String Prop, Object val) {
		ArrayList ret = new ArrayList(list.size());
		IAspect curr, currProp;
		int i = list.size();
		while (--i >= 0) {
			curr = (IAspect) list.get(i);
			currProp = curr.getField(Prop);
			if  ((val  == currProp) ||
				((null != currProp) && (currProp.equals(val)))) {
				ret.add(curr); }
		} return ret; }
	
	/** @return The current Aspect Value as a (boxed) Object  */
	public Object getVal() {
		return Value; }
	
	/**
	 * @return The Aspect Value as a String Representation
	 * This is always possible for any Type
	 */
	public String toString() {
		if (Value == null) {
			return   null; }
		return Value.toString(); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : unsafe Accessor Methods (getXXX/isXXX/setXXX)
	/// The Question is whether to throw Exceptions when the Type was not suitable
	/// or to return special Values indicating the Missing of the Value
	////////////////////////////////////////////////////////////////////////////////
	
	/** adds the given Value to the List at the given Index.
	  * All Objects at higher Index Values are shifted right by one Position
	  * @param value the Value to add
	  * @param Index the Position to add
	  * @throw IllegalArgumentException when the Type of the Value does not match the List Type.
	  */
	public void addVal(IAspect value, int Index)
		throws IllegalArgumentException {
		if ((BaseClass != null) && (!BaseClass.isInstance(value))) {
			throw new IllegalArgumentException("Expected Type:'" + BaseClass + "' actual Type: '" + value.getClass() + "'"); }
	//	if (list.size() > 0) { //test for the Type of the existing Items
	//		Class cls = list.get(0).getClass(); }
		list.add(value);
	}
	
	/** adds the given Value to the End of the List.
	  * @param value the Value to add
	  * @throw IllegalArgumentException when the Type of the Value does not match the List Type.
	  */
	public void addVal(IAspect value) throws IllegalArgumentException {
		addVal(value, list.size());
	}
	
	public IAspect removeVal(IAspect value) {
		int ndx = list.indexOf(value);
		if (ndx < 0) { //not in the List
			return null; }
		return removeVal(ndx); }
	
	/** removes the Value at the given Index.
	  * @return the Item just removed
	  */
	public IAspect removeVal(int Index) {
		if ((Index < 0) || (Index > list.size())) { //better to let the Exception to be thrown now...
			return null; } //...than later when this Context does no longer exist
		return (IAspect) list.remove(Index); }
	
	/** removes the last Value where the given Prop has the given Value
	  * @return the Item just removed
	  */
	public IAspect removeVal(String Prop, Object val) {
		IAspect curr, currProp;
		int i = list.size();
		while (--i >= 0) {
			curr = (IAspect) list.get(i);
			currProp = curr.getField(Prop);
			if  ((val  == currProp) ||
				((null != currProp) && (currProp.equals(val)))) {
				list.remove(i);
				return curr; }
		} return null; }
	
	/** @return a certain (indexed) Aspect Value as a (boxed) Object
	  * where the Property Prop has the given Value.
	  * only the first Match is returned (Prop should be a unique key)
	  */
	public ArrayList removeVals(String Prop, Object val) {
		ArrayList ret = new ArrayList(list.size());
		IAspect curr, currProp;
		int i = list.size();
		while (--i >= 0) {
			curr = (IAspect) list.get(i);
			currProp = curr.getField(Prop);
			if  ((val  == currProp) ||
				((null != currProp) && (currProp.equals(val)))) {
				ret.add(curr);
				list.remove(i); }
		} return ret; }
	
	/** Local Validation Routine to validate multifield Checks
	  * Called both from validate() Child and validateParent() Validation!
	  */
	/*protected void validatePrimVal(Object Source, Object Value, Object oldVal) throws InvalidException {
		validatePrimVal(Value.toString()); }
	
	/** Local Validation Routine to validate multifield Checks
	  * Called both from validate() Child and validateParent() Validation!
	  */
	protected void validatePrimVal(Object value) throws InvalidException {
		if ((value == null) || list.contains(value)) {
			return; }
		throw new InvalidException(this, value, "Value " + value + " is not contained in the List ");
	}
	
	/** sets the Aspect Value as a (boxed) Object  */
	protected void setPrimVal(Object value) {
		Value = (IAspect) value; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //java.io.IOException {
		System.out.println("Testing " + ListAspect.class.getName());
		try { testList();
		} catch (InvalidException x) {
			x.printStackTrace(); } //Log.l(x)
	}

	/** Tests all Methods of this Class	 */
	protected static void testList() throws InvalidException {
		ListAspect sa = new ListAspect("TestName", "LastName");
		PersonAspect asp1 = new PersonAspect("MHeuer"); //, null);
		PersonAspect asp2 = (PersonAspect) asp1.clone();
		asp2.setDirty(false); //new PersonAspect("NWarmbold");
		asp2.FirstName.setVal(PersonAspect.STR_NICOLE  );
		asp1.set(PersonAspect.FIRST_NAME,  PersonAspect.STR_MATTHIAS);
		asp2.set(PersonAspect.LAST_NAME, PersonAspect.STR_WARMBOLD);
		asp1.set(PersonAspect.LAST_NAME, PersonAspect.STR_HEUER   );
		asp1.Address.City.setVal(PersonAspect.STR_FRANKFURT);
		asp2.set(PersonAspect.HOME + SEP + AddressAspect.CITY, PersonAspect.STR_HANNOVER);
		sa.addVal(asp1);
		sa.addVal(asp2); //would I allow Duplicates? yes, otherwise inserting would be too expensive!
		//actually I could use a HashMap, when the Index Column is given
		//by evaluating the HashCode() and using it!
		//When an Object has an ID Column, its HashCode() and equals() Methods
		//should use the same Logic.
		sa.setVal(asp1); //new PersonAspect("Test")); //must be in the List!
		System.out.println("old Instance: '" +  sa + "'");
		IAspect asp = sa.newInstance("test");
		System.out.println("new Instance: '" + asp + "'");
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

