package streamIO.copy.boole;

/**
  * Abstract base class for constant (read-only) Boolean-algebra objects, throwing on
  * every in-place `...At()` operation.
  * Title: ACBoole<p>
  * Description:
  * Purpose:
  *
  * Purpose / Responsibilities of this Class
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
  * Created on	07-16-2002, 03:59 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:45:04Z
  * digest: 2447fe19369e9d0d8f9ea54e6a08fb30045516a3c2ed2d8dadb36ef8932d8b06
  * stale: false
  * tags: [code/boolean_algebra, code/abstract_base]
  * concepts: [Boolean Algebra]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public abstract class ACBoole
extends ACLattice
implements Boole {

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected ACBoole() { }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Parent ACLattice: abstract Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Parent ACLattice: Implementation / Overrides
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Boole: abstract Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Boole: Implementation
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + ACBoole.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

