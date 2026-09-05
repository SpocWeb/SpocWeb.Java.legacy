package synch.aspect;

//import Aspects.ReflectAble; 
//import java.lang.reflect.Field;
//import Synch.InvalidException;

/**
  * Title: PersonAspect<p>
  * Description:
  * Example {@link ContainerAspect} describing a natural person: first name, last
  * name and a nested {@link AddressAspect}, each a child field keyed under this
  * Aspect's Name.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-20-2002, 12:20 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:42:34Z
  * digest: 7748fa43ead03f212becdeed8d0ad0135fbf93b9b06f0813a627abec8aa5cb6b
  * stale: false
  * tags: [code/attached_property, code/observer_pattern]
  * concepts: [Composite Value Object]
  * facets: {layer: domain, status: legacy, complexity: medium}
  * -->
  */
public class PersonAspect
extends ContainerAspect //Aspect
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
	final static public String FIRST_NAME = "firstName";

	/** Holds the First Name, initialized in the Constructor!	 */
	final public StringAspect firstName = new StringAspect(Name + SEP + FIRST_NAME, this);

	/** Constant used as a local key for the Last Name Property */
	final static public String LAST_NAME = "lastName";

	/** Holds the Last Name, initialized in the Constructor!	 */
	final public StringAspect lastName = new StringAspect(Name + SEP + LAST_NAME, this);

	/** Constant used as a local key for the Last Name Property */
	final static public String ADDRESS = "address";

	/** Holds the Last Name, initialized in the Constructor!	 */
	final public AddressAspect address = new AddressAspect(Name + SEP + ADDRESS, this);

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor
	 *
	 * <!-- docstate
	 * tags: [code/attached_property]
	 * concepts: [Composite Value Object]
	 * facets: {layer: domain, status: legacy, complexity: low}
	 * -->
	 */
	public PersonAspect(String Name, Aspect Parent) { super(Name, Parent); }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////
/// #region : Interface Aspect: abstract Methods
////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IValidator: Implementation
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class
	 *
	 * <!-- docstate
	 * tags: [code/attached_property]
	 * concepts: [Scratch Test Method]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + PersonAspect.class.getName());
		try {
			PersonAspect asp1 = new PersonAspect("MHeuer", null);
			PersonAspect asp2 = (PersonAspect) asp1.clone(); //new PersonAspect("NWarmbold");
			asp2.firstName.setVal("Nicole");
			asp1.firstName.setVal("Matthias");
//			asp1.set(FIRST_NAME, "Matthias"); //doesn't need to call the set() Routine!
			asp1.addSubscriber(asp2);
//			asp2.addSubscriber(asp1); //leads to circular Updates!
			asp2.Parent = asp1;
//			System.out.println(asp2.get(FIRST_NAME));
			System.out.println(asp1.firstName);
//			asp2.set(LAST_NAME, "Warmbold"); //
//			System.out.println(asp2.get(LAST_NAME));
//			System.out.println(asp1.get(LAST_NAME));
//			asp1.set(LAST_NAME, "Heuer"); //doesn't need to call the set() Routine!
			asp1.address.City.setVal("Frankfurt");
//			System.out.println(asp2.get(LAST_NAME));
//			System.out.println(asp2.get(ADDRESS + SEP + AddressAspect.CITY));
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
	 * tags: [code/attached_property]
	 * concepts: [Scratch Test Entry Point]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

