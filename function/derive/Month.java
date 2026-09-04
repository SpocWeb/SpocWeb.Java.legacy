package function.derive;

import java.util.Hashtable;

/**
  * Title: Month<p>
  * Description:
  * Purpose:
  * Enumeration for the Months of a Year
  * Purpose / Responsibilities of this Class
  *
  * Implementation Details:
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Known SubClasses:
  *
  * Known Uses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-29-2002, 08:14 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
final public class Month
extends Enum {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** The Offset of the Month Values and their Position in the Enum */
	final static public byte OFFSET   = 1;

	/** Constant denoting the Month January	 */
	final static public byte JANUARY  = 1;

	/** Constant denoting the Name of the Month January	 */
	final static public String STR_JANUARY = "January";

	/** Constant denoting the Month February	 */
	final static public byte FEBRUARY = 2;

	/** Constant denoting the Name of the Month February	 */
	final static public String STR_FEBRUARY = "February";

	/** Constant denoting the Month March	 */
	final static public byte MARCH    = 3;

	/** Constant denoting the Name of the Month March	 */
	final static public String STR_MARCH = "March";

	/** Constant denoting the Month April	 */
	final static public byte APRIL    = 4;

	/** Constant denoting the Name of the Month April	 */
	final static public String STR_APRIL = "April";

	/** Constant denoting the Month May	 */
	final static public byte MAY      = 5;

	/** Constant denoting the Name of the Month May	 */
	final static public String STR_MAY = "May";

	/** Constant denoting the Month June	 */
	final static public byte JUNE     = 6;

	/** Constant denoting the Name of the Month June	 */
	final static public String STR_JUNE = "June";

	/** Constant denoting the Month July	 */
	final static public byte JULY     = 7;

	/** Constant denoting the Name of the Month July	 */
	final static public String STR_JULY = "July";

	/** Constant denoting the Month August	 */
	final static public byte AUGUST   = 8;

	/** Constant denoting the Name of the Month August	 */
	final static public String STR_AUGUST = "August";

	/** Constant denoting the Month September	 */
	final static public byte SEPTEMBER= 9;

	/** Constant denoting the Name of the Month September	 */
	final static public String STR_SEPTEMBER = "September";

	/** Constant denoting the Month October	 */
	final static public byte OCTOBER  = 10;

	/** Constant denoting the Name of the Month October	 */
	final static public String STR_OCTOBER = "October";

	/** Constant denoting the Month November	 */
	final static public byte NOVEMBER = 11;

	/** Constant denoting the Name of the Month November	 */
	final static public String STR_NOVEMBER = "November";

	/** Constant denoting the Month December	 */
	final static public byte DECEMBER = 12;

	/** Constant denoting the Name of the Month December	 */
	final static public String STR_DECEMBER = "December";

	/**Names of the Months	 */
	protected static final String[] NAMES = {
		STR_JANUARY, STR_FEBRUARY, STR_MARCH, STR_APRIL, STR_MAY, STR_JUNE,
		STR_JULY, STR_AUGUST, STR_SEPTEMBER, STR_OCTOBER, STR_NOVEMBER, STR_DECEMBER};

	/** List of Names for the Enums */
	protected static final byte[] VALUES = {
		JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE,
		JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER };

	/** Constant denoting TRUE	 */
	protected static final Enum[] LIST = CREATE_LIST(NAMES, OFFSET, new Month());

	/** Constant denoting the Name of the Month January	 */
	final static public Month January   = (Month) LIST[JANUARY  -OFFSET];

	/** Constant denoting the Name of the Month February	 */
	final static public Month February  = (Month) LIST[FEBRUARY -OFFSET];

	/** Constant denoting the Name of the Month March	 */
	final static public Month March     = (Month) LIST[MARCH    -OFFSET];

	/** Constant denoting the Name of the Month April	 */
	final static public Month April     = (Month) LIST[APRIL    -OFFSET];

	/** Constant denoting the Name of the Month May	 */
	final static public Month May       = (Month) LIST[MAY      -OFFSET];

	/** Constant denoting the Name of the Month June	 */
	final static public Month June      = (Month) LIST[JUNE     -OFFSET];

	/** Constant denoting the Name of the Month July	 */
	final static public Month July      = (Month) LIST[JULY     -OFFSET];

	/** Constant denoting the Name of the Month August	 */
	final static public Month August    = (Month) LIST[AUGUST   -OFFSET];

	/** Constant denoting the Name of the Month September	 */
	final static public Month September = (Month) LIST[SEPTEMBER-OFFSET];

	/** Constant denoting the Name of the Month October	 */
	final static public Month October   = (Month) LIST[OCTOBER  -OFFSET];

	/** Constant denoting the Name of the Month November	 */
	final static public Month November  = (Month) LIST[NOVEMBER -OFFSET];

	/** Constant denoting the Name of the Month December	 */
	final static public Month December  = (Month) LIST[DECEMBER -OFFSET];

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
		return new Month(val_, Offset_, list_, names_, EnumsByName_); }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	private Month() { }

	/**
	 * Initializing Constructor
	 * @param val  the Value for this Enum
	 * @param list the Enumeration this Enum belongs to
	 */
	private Month(long val_, long Offset_, Enum[] list_, String[] names_, Hashtable EnumsByName_) {
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
		System.out.println("Testing " + Month.class.getName());
		testIt(January);
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

