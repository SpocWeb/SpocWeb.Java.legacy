package synch.aspect;

/**
  * Title: AddressAspect<p>
  * Description:
  * Example {@link ContainerAspect} bundling a postal address (street/number, zip,
  * city) as three child {@link StringAspect} fields, each keyed under this Aspect's
  * Name using the {@link Aspect#SEP} separator.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-21-2001, 11:58 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:42:30Z
  * digest: 51cf0ab196e32ef1e7a9a78b2443b26b1a50c2304d4ac31af8b59cf8444635d6
  * stale: false
  * tags: [code/attached_property, code/observer_pattern]
  * concepts: [Composite Value Object]
  * facets: {layer: domain, status: legacy, complexity: medium}
  * -->
  */
public class AddressAspect
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

	/** Constant used as a local key for the Street and Number Property */
	final static public String STREET_NR = "StreetNr";

	/** Holds the Street and Number, initialized in the Constructor!	 */
	 final public StringAspect StreetNr = new StringAspect(Name + SEP + STREET_NR, this);

	/** Constant used as a local key for the Zip Code Property */
	final static public String ZIP = "Zip";

	/** Holds the Zip Code, initialized in the Constructor!	 */
	 final public StringAspect Zip = new StringAspect(Name + SEP + ZIP, this);

	/** Constant used as a local key for the City Property */
	final static public String CITY = "City";

	/** Holds the Name of the City, initialized in the Constructor!	 */
	 final public StringAspect City = new StringAspect(Name + SEP + CITY, this);

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
	protected AddressAspect(String Name, Aspect Parent) { super (Name, Parent); }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Parent Aspect: abstract Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Parent Aspect: Implementation / Overrides
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface : abstract Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface : Implementation
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
		System.out.println("Testing " + AddressAspect.class.getName());
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

