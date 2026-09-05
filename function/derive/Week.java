package function.derive;

import java.util.Hashtable;

/**Enumeration of the seven Weekdays, following the {@link Enum} singleton-list pattern.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-29-2002, 08:54 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:16:29Z
  * digest: 4b04997eed9628d7ecd4c29825e4f2b5ae23a8ec28668604820e66731bb44424
  * stale: false
  * tags: [code/enum_modeling]
  * concepts: [Calendar]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
final public class Week
extends Enum {

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

	/** The Offset of the Month Values and their Position in the Enum */
	final static public byte OFFSET   = 0;

	/** Constant denoting the Weekday Monday	 */
	final static public byte MONDAY = 0;

	/** Constant denoting the Name of the Weekday Monday	 */
	final static public String STR_MONDAY = "Monday";

	/** Constant denoting the Weekday Tuesday	 */
	final static public byte TUESDAY = 1;

	/** Constant denoting the Name of the Weekday Tuesday	 */
	final static public String STR_TUESDAY = "Tuesday";

	/** Constant denoting the Weekday Wednesday	 */
	final static public byte WEDNESDAY = 2;

	/** Constant denoting the Name of the Weekday Wednesday	 */
	final static public String STR_WEDNESDAY = "Wednesday";

	/** Constant denoting the Weekday Thursday	 */
	final static public byte THURSDAY = 3;

	/** Constant denoting the Name of the Weekday Thursday	 */
	final static public String STR_THURSDAY = "Thursday";

	/** Constant denoting the Weekday Friday	 */
	final static public byte FRIDAY = 4;

	/** Constant denoting the Name of the Weekday Friday	 */
	final static public String STR_FRIDAY = "Friday";

	/** Constant denoting the Weekday Saturday	 */
	final static public byte SATURDAY = 5;

	/** Constant denoting the Name of the Weekday Saturday	 */
	final static public String STR_SATURDAY = "Saturday";

	/** Constant denoting the Weekday Sunday	 */
	final static public byte SUNDAY = 6;

	/** Constant denoting the Name of the Weekday Sunday	 */
	final static public String STR_SUNDAY = "Sunday";

	/**Names of the Months	 */
	protected static final String[] NAMES = {
		STR_MONDAY, STR_TUESDAY, STR_WEDNESDAY, STR_THURSDAY, STR_FRIDAY, STR_SATURDAY, STR_SUNDAY };

	/** List of Names for the Enums */
	protected static final byte[] VALUES = { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY };

	/** Constant denoting TRUE	 */
	protected static final Enum[] LIST = CREATE_LIST(NAMES, OFFSET, new Week());

	/** Constant denoting the Name of the Weekday 	 */
	final static public Week Monday   = (Week) LIST[MONDAY   -OFFSET];

	/** Constant denoting the Name of the Weekday 	 */
	final static public Week Tuesday  = (Week) LIST[TUESDAY  -OFFSET];

	/** Constant denoting the Name of the Weekday 	 */
	final static public Week Wednesday= (Week) LIST[WEDNESDAY-OFFSET];

	/** Constant denoting the Name of the Weekday 	 */
	final static public Week Thursday = (Week) LIST[THURSDAY -OFFSET];

	/** Constant denoting the Name of the Weekday 	 */
	final static public Week Friday   = (Week) LIST[FRIDAY   -OFFSET];

	/** Constant denoting the Name of the Weekday 	 */
	final static public Week Saturday = (Week) LIST[SATURDAY -OFFSET];

	/** Constant denoting the Name of the Weekday 	 */
	final static public Week Sunday   = (Week) LIST[SUNDAY   -OFFSET];

////////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////
/// #region : Interface Enum: Implementation
////////////////////////////////////////////////////////////////////////////

	/** Used by the createList Method to create Instances for the List */
	protected Enum newEnum(long val_, long Offset_, Enum[] list_, String[] names_, Hashtable EnumsByName_) {
		return new Week(val_, Offset_, list_, names_, EnumsByName_); }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	private Week() { }

	/**
	 * Initializing Constructor
	 * @param val  the Value for this Enum
	 * @param list the Enumeration this Enum belongs to
	 */
	private Week(long val_, long Offset_, Enum[] list_, String[] names_, Hashtable EnumsByName_) {
		super(val_, Offset_, list_, names_, EnumsByName_); }

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

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Week.class.getName());
		testIt(Monday);
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

