package reflect;

import java.util.ArrayList;
import java.util.Collection;

import streamIO.Assert;

/**
  * Title: Person<p>
  * Description:
  * Purpose:
  * Test Implementation for ReflectAble
  * Demonstrates that Reflection can be used
  * to implement Attributes and Relations.
  * The Relation to the Parent Class
  * is not modeled implicitly but explicitly,
  * which breaks the Analogy.
  *
  *
  * Design Decisions / Implementation Details:
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-24-2002, 11:51 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class Person
extends ReflectAble {

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	final static public String FIRST_NAME = "firstName";

	public String firstName;

	final static public String LAST_NAME = "lastName";

	public String  lastName;

	final static public String BIRTH_DATE = "birthDate";

//	public Date   birthDate;

	final static public String HOME = "home";

	final public Address home = new Address(); //have to instantiate it to prevent NullPointers!

	final static public String ADRESSES = "addresses";

	final public Collection addresses = new ArrayList(); //have to instantiate it to prevent NullPointers!

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	public Person() { }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/// Test Data
	protected static final String STR_NICOLE    = "Nicole";
	protected static final String STR_WARMBOLD  = "Warmbold";
	protected static final String STR_MATTHIAS  = "Matthias";
	protected static final String STR_HEUER     = "Heuer";
	protected static final String STR_FRANKFURT = "Frankfurt";
	protected static final String STR_HANNOVER  = "Hannover";
	protected static final String STR_MERCATOR  = "Mercatorstr. 5";

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Person.class.getName());
		Person mHeuer = new Person();
		Assert.IS_TRUE(!mHeuer.dirty); //check the Dirty Flag
		mHeuer.firstName  = STR_MATTHIAS; Assert.EQUALS(mHeuer.get(FIRST_NAME), STR_MATTHIAS);
		Assert.IS_TRUE(!mHeuer.dirty); //check the Dirty Flag being set only after 'set'
		mHeuer.set(LAST_NAME, STR_HEUER); Assert.EQUALS(mHeuer.lastName, STR_HEUER);
		Assert.IS_TRUE( mHeuer.dirty); //check the Dirty Flag being set only after 'set'
		Assert.IS_TRUE(!mHeuer.home.dirty); //check the Dirty Flag
		mHeuer.home.City = STR_FRANKFURT;
		Assert.EQUALS (mHeuer.get(HOME + SEP + Address.CITY), STR_FRANKFURT);
		Assert.IS_TRUE(!mHeuer.home.dirty); //check the Dirty Flag being set only after 'set'
		mHeuer.set(HOME + SEP + Address.STREET_NR, STR_MERCATOR);
		Assert.EQUALS (mHeuer.home.StreetNr, STR_MERCATOR);
		Assert.IS_TRUE( mHeuer.home.dirty); //check the Dirty Flag being set only after 'set'
		Person mHeuer2 = (Person) mHeuer.Copy();
		Assert.EQUALS (mHeuer2.home.StreetNr, STR_MERCATOR);
		//  update() doesn't really work properly...
		//validate() is not implemented yet...
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

