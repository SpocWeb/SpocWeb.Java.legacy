package reflect;

/**
  * Title: Address<p>
  * Description:
  * Sample {@link ReflectAble} entity holding a street/zip/city Address,
  * used as a nested Property of {@link Person} to demonstrate path-based
  * get/set navigation (e.g. "home_StreetNr") through the reflect Package.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-25-2002, 12:13 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:22:23Z
  * digest: 691dddd61ce042528ae219fade243e9f0fac67586f6d485551ecb0d99b7b214a
  * stale: false
  * tags: [code/domain_model]
  * concepts: [Domain Model]
  * facets: {layer: domain, status: stable, complexity: low}
  * -->
  */
public class Address
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

	/** Property Name of {@link #StreetNr}, for use with {@link ReflectAble#get(String)}/{@code set} */
	final static public String STREET_NR = "StreetNr";
	/** Street name and house number */
	public String StreetNr;

	/** Property Name of {@link #Zip}, for use with {@link ReflectAble#get(String)}/{@code set} */
	final static public String ZIP = "Zip";
	/** Postal / ZIP code */
	public String Zip;

	/** Property Name of {@link #City}, for use with {@link ReflectAble#get(String)}/{@code set} */
	final static public String CITY = "City";
	/** City name */
	public String City;

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
	public Address() { }

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
		System.out.println("Testing " + Address.class.getName());
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

