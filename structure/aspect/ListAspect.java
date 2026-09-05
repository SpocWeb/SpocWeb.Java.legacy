package structure.aspect;

/**
  * Extends {@link Aspect} to represent a selectable List of other Aspects, e.g. for a Combo
  * Box or DataGrid Binding.
  *
  * Extends and implements the Aspect Class for Lists
  * Returns a single Selected Item or a List of selected Items
  * The Items can be any Object, but especially Aspects are interesting!
  * Lists of Strings can be used in Combo Boxes
  * Lists of Lists can be used in Combo Boxes with Decode and in DataGrids.
  * Instead of a Boolean Type also the List or the Number Aspect can be used.
  *
  *
  * Implementation Details:
  *
  * Known SubClasses:
  *
  * Known Uses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-01-2002, 08:15 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:21:22Z
  * digest: 8bb23d7809b3d70773b8c770833a64d71667efabc2865b4b0b73f1410970ecf1
  * stale: false
  * tags: [code/property_binding]
  * concepts: [List-Valued Aspect]
  * facets: {layer: domain, status: broken, complexity: low}
  * -->
  */
public abstract class ListAspect
extends Aspect {

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Reference to the List of Aspects	 */
	protected Aspect[] list;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Constructor taking the underlying Name and List of Aspects.
	 *
	 * <!-- docstate
	 * tags: [code/property_binding]
	 * concepts: [List Aspect Constructor]
	 * facets: {layer: domain, status: broken, complexity: low}
	 * -->
	 */
	// TODO: LOGIC: `list_` is never assigned to the `list` field, so `list` stays permanently
	// null regardless of what is passed in here - every Subclass method reading it would see
	// an empty List instead of the one the caller supplied.
	protected ListAspect(String name, Aspect[] list_) { super(name); }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface TODO: abstract Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface TODO: Implementation
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class
	 *
	 * <!-- docstate
	 * tags: [code/property_binding]
	 * concepts: [Self-Test Method]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + ListAspect.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * <!-- docstate
	 * tags: [code/property_binding]
	 * concepts: [Demo Entry Point]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

