package aspect;

//import java.lang.reflect.Field;
//import Synch.InvalidException;
import streamIO.Assert;

/**
  * Title: PersonAspect<p>
  * Description:
  * Composite Aspect describing a natural person: first name, last name and
  * a nested AddressAspect. Example of a Composite Aspect built purely from
  * public Aspect fields; also carries this class's own unit-test data/logic
  * in testIt().
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-20-2002, 12:20 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:24:53Z
  * digest: 6cf1f556eb3bc6ecf4dd03a247f597d59051c0ea3a1d93a3f7301304977ec23e
  * stale: false
  * tags: [code/composite_pattern, code/domain_model]
  * concepts: [Composite Aspect, Attribute Modelling]
  * facets: {layer: domain, status: stable, complexity: low}
  * -->
  */
public class PersonAspect
extends SimpleAspect //AHierarchyAspect //Aspect
{

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Constant used as a local key for the First Name Property */
	final static public String FIRST_NAME = "FirstName";

	/** Holds the First Name, initialized in the Constructor!	 */
	 final public StringAspect FirstName = new StringAspect(Name + SEP + FIRST_NAME, this);

	/** Constant used as a local key for the Last Name Property */
	final static public String LAST_NAME = "LastName";

	/** Holds the Last Name, initialized in the Constructor!	 */
	 final public StringAspect LastName = new StringAspect(Name + SEP + LAST_NAME, this);

	/** Constant used as a local key for the Last Name Property */
	final static public String ADDRESS = "Address";

	/** Holds the Last Name, initialized in the Constructor!	 */
	 final public AddressAspect Address = new AddressAspect(Name + SEP + ADDRESS, this);

	/** Constant used as a local key prefix when addressing the home Address's sub-Properties, e.g. HOME + SEP + AddressAspect.CITY */
	final static public String HOME = "home";

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor
	 *
	 * <!-- docstate
	 * tags: [code/domain_model]
	 * concepts: [Attribute Modelling]
	 * facets: {layer: domain, status: stable, complexity: low}
	 * -->
	 */
	public PersonAspect(String name, IHierarchyAspect Parent) { super(name, Parent); }

	/** Initializing Constructor
	 *
	 * <!-- docstate
	 * tags: [code/domain_model]
	 * concepts: [Attribute Modelling]
	 * facets: {layer: domain, status: stable, complexity: low}
	 * -->
	 */
	public PersonAspect(String Name) { super(Name, null); }

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

	/** Tests all Methods of this Class
	 *
	 * <!-- docstate
	 * tags: [code/test_harness]
	 * concepts: [Testing]
	 * facets: {layer: test, status: broken, complexity: low}
	 * -->
	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + PersonAspect.class.getName());
		try {
			PersonAspect asp1 = new PersonAspect("MHeuer"); //, null);
			PersonAspect asp2 = (PersonAspect) asp1.clone();
			asp2.setDirty(false); //new PersonAspect("NWarmbold");
			Assert.IS_TRUE(!asp2.dirty); asp2.FirstName.setVal(STR_NICOLE  ); Assert.EQUALS(STR_NICOLE  , asp2.get(FIRST_NAME));
			Assert.IS_TRUE( asp2.dirty); asp1.set(FIRST_NAME,  STR_MATTHIAS); Assert.EQUALS(STR_MATTHIAS, asp1.FirstName.getVal());
//			asp1.addSubscriber(asp2);
///			asp2.addSubscriber(asp1); //leads to circular Updates!
//			asp2.Parent = asp1;
			asp2.set(LAST_NAME, STR_WARMBOLD); Assert.EQUALS(STR_WARMBOLD, asp2.get(LAST_NAME));
			asp1.set(LAST_NAME, STR_HEUER   ); Assert.EQUALS(STR_HEUER   , asp1.get(LAST_NAME));
//			Assert.Equals(STR_HEUER, asp2.get(LAST_NAME)); //due to Subscription!
			asp1.Address.City.setVal(STR_FRANKFURT); Assert.EQUALS(STR_FRANKFURT, asp1.Address.City.getVal());
			asp2.set(ADDRESS + SEP + AddressAspect.CITY, STR_HANNOVER);
			Assert.EQUALS(STR_HANNOVER, asp2.get(ADDRESS + SEP + AddressAspect.CITY));
//			System.out.println(asp2);
			asp2.setVal(null); //check if Clearing works...
//			System.out.println(asp2); //should be all empty afterwards!
			Assert.EQUALS("", asp2.LastName.getVal());
			Assert.EQUALS("", asp2.Address.City.getVal());
		} catch (Exception x) {
			System.out.println("Exception " + x);
			x.printStackTrace();
		}
		//
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

