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
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:23:07Z
  * digest: 695fe82375939a56c70f06f41f95b9006d9f6b0a8052e78216ac6b4eede202bc
  * stale: false
  * tags: [code/domain_model]
  * concepts: [Domain Model]
  * facets: {layer: domain, status: legacy, complexity: low}
  * -->
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

	/** Property Name of {@link #firstName}, for use with {@link ReflectAble#get(String)}/{@code set} */
	final static public String FIRST_NAME = "firstName";

	/** Given name */
	public String firstName;

	/** Property Name of {@link #lastName}, for use with {@link ReflectAble#get(String)}/{@code set} */
	final static public String LAST_NAME = "lastName";

	/** Family name */
	public String  lastName;

	/** Property Name reserved for a future {@code birthDate} Field (currently commented out) */
	final static public String BIRTH_DATE = "birthDate";

//	public Date   birthDate;

	/** Property Name of {@link #home}, for use with {@link ReflectAble#get(String)}/{@code set} */
	final static public String HOME = "home";

	/** Home Address; eagerly instantiated so nested Property access never hits a null Reference */
	final public Address home = new Address(); //have to instantiate it to prevent NullPointers!

	/** Property Name of {@link #addresses}, for use with {@link ReflectAble#get(String)}/{@code set} */
	final static public String ADRESSES = "addresses";

	/** Additional Addresses beyond {@link #home}; eagerly instantiated so nested Property access never hits a null Reference */
	final public Collection addresses = new ArrayList(); //have to instantiate it to prevent NullPointers!

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor
	 *
	 * <!-- docstate
	 * tags: [code/domain_model]
	 * concepts: [Domain Model]
	 * facets: {layer: domain, status: stable, complexity: low}
	 * -->
	 */
	public Person() { }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/// Test Data
	/** Test Value for {@link #firstName} on the Spouse. */
	protected static final String STR_NICOLE    = "Nicole";
	/** Test Value for {@link #lastName} before Marriage. */
	protected static final String STR_WARMBOLD  = "Warmbold";
	/** Test Value for {@link #firstName}. */
	protected static final String STR_MATTHIAS  = "Matthias";
	/** Test Value for {@link #lastName}. */
	protected static final String STR_HEUER     = "Heuer";
	/** Test Value for {@link Address#City}, initial. */
	protected static final String STR_FRANKFURT = "Frankfurt";
	/** Test Value for {@link Address#City}, unused so far. */
	protected static final String STR_HANNOVER  = "Hannover";
	/** Test Value for {@link Address#StreetNr}. */
	protected static final String STR_MERCATOR  = "Mercatorstr. 5";

	/** Tests all Methods of this Class
	 *
	 * <!-- docstate
	 * tags: [code/test_harness]
	 * concepts: [Testing]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
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
	 * <!-- docstate
	 * tags: [code/test_harness]
	 * concepts: [Testing]
	 * facets: {layer: test, status: stable, complexity: low}
	 * -->
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

