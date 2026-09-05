package aspect;

/**
  * Title: AddressAspect<p>
  * Description:
  * Composite Aspect modelling a postal address, composed of three
  * StringAspect sub-properties (street/number, zip code, city).
  * Serves as the (nested) Address sub-aspect of PersonAspect and as an
  * example of a Composite Sub-Aspect built purely from public Aspect fields.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-21-2001, 11:58 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:23:48Z
  * digest: 08377d527689d15cc06f217d2798c615e2e5524671ad6d75a2d42204d8e08865
  * stale: false
  * tags: [code/composite_pattern, code/domain_model]
  * concepts: [Composite Aspect, Attribute Modelling]
  * facets: {layer: domain, status: stable, complexity: low}
  * -->
  */
public class AddressAspect
extends SimpleAspect //AHierarchyAspect //Aspect
{

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
	 * tags: [code/domain_model]
	 * concepts: [Attribute Modelling]
	 * facets: {layer: domain, status: stable, complexity: low}
	 * -->
	 */
	public AddressAspect(String name, IHierarchyAspect Parent) { super(name, Parent); }

	/** Initializing Constructor
	 *
	 * <!-- docstate
	 * tags: [code/domain_model]
	 * concepts: [Attribute Modelling]
	 * facets: {layer: domain, status: stable, complexity: low}
	 * -->
	 */
	public AddressAspect(String Name) { super (Name, null); }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class
	 *
	 * <!-- docstate
	 * tags: [code/test_harness]
	 * concepts: [Testing]
	 * facets: {layer: test, status: stable, complexity: low}
	 * -->
	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + AddressAspect.class.getName());
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

