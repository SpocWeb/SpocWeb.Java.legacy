package streamIO;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/** Logging Class that...
  * -has very brief Method Names (l,n and L,N) 
  *  to encourage using Logging instead of Comments! 
  * -Using these Method Names, also ToDos can be logged and found! 
  * -enforce the Format of 'Fix', 'Todo' or 'Deprecated' Tags 
  *  (User, Severity, due/done Date, external Reference) 
  * -writes the Output in a separated File Structure so it can be queried! 
  * -Object Creation and Destruction is logged
  * -logs Calls and Returns of Methods with Parameters and Return Values 
  * -uses the printStackTrace Method to log the current Call Stack.
  * -Reformats the printStackTrace to allow One-Line Log Entries.
  * -presents a Template of how to log CVS Version Info on Class Load
  * 
  * Apart from the Possibility to Log the Comments 
  * and thus receive a better readable Log File, 
  * you have the IDE Tools for looking up deprecated Methods or TODOs! 
  * On the Downside you can introduce (new, but easy to fix) Bugs. 
  * 
  * Important Items to log: 
  * Comments, Method Calls and Returns with Params and Return Values
  * Exceptions, ToDos, Deprecation Flags, Version Info
  *
  * Static Member L can be substituted and acts as the Default Logger for t() and T() Statements.
  * Overloads the n() Method to flush and close the previous Log Line.
  * Overloads the l() Method to allow a Shortcut for the same Types as print().
  * The Reason for not using n() solely is the Option to add arbitrary Info to THE SAME Log Line
  * before starting a new one and to determine the Start of the Log Line
  * to allow for a (Semi-) structured Log ResultSet structure.
  * Optionally adds TimeStamp and full StackTrace to the Output of every Line.
  * Thus the structure is like this:
  * LogLevel;[TimeStamp];[ClassName];[MethodName];[Context];[StackTrace];Message;Variable(s)
  *
  * The Class and Method Name of the calling Class are extracted from the Stack Trace.
  * This prevents the Mistake of Log4J that a different Class is used in the Constructor of the logger Instance.
  * Also a local static Logger Instance Member is not necessary!
  * The only Advantage of a local Logger is the Possibility to debug only a certain Class,
  * but without Context Information this is usually not helpful!
  *
  * A typical Log Statements looks like this:
  * Log.L(....).l(....).l(....).n(....);
  *
  * To save Log Processing when Logging is switched off, use:
  * if (Log.L != null) { Log.L(....).l(....).l(....).n(....); }
  *
  * The idea is to keep the Client Log Code really brief,
  * so that instead of writing Comments, People start writing Log Statements.
  * Though Log Statements do repeat and thus shouldn't be as long
  * as a Comment like this one (and specifically should not contain Line Breaks),
  * often Comments indicate a certain Milestone in Processing.
  *
  * Another Goal is to log Entering and Exiting Routines
  * together with all their Parameters and Return Values, preferably with their meaningful Variable Names. 
  * If that hits Performance too much, at least log Parameters of Exceptions
  * on the Way back to the handling Routine by placing a try {} catch {} Block 
  * into each and every Routine, log Parameters in the catch Block and rethrow the Exception.
  *
  * When you go with Fowler's Book on Refactoring,
  * you shouldn't need Comments (at least not this kind).
  * Instead you should create a Method for each Milestone and name it accordingly.
  * Logging is also used for Profiling though
  * and one of the first Infos desired is the Times and Number of
  * entering and exiting a Routine.
  *
  * This Information could also be retrieved from the JVM,
  * because it is a Value-added Processor with Debugging Support,
  * Adding Logging, Profiling and full Exception Info including local Variables
  * should not be a major Problem 
  * and should especially NOT clutter your Code!
  *
  *
  * an Exception is logged as an Error: Default Levels are
  * Levels below 0 indicate Trace Stack Levels
  *-1 TraceStack
  * 0 Log / Comment / Information
  * 1 Warning
  * 2 caught   Exception:
  * 3 Runtime  Exception:
  * 4 severe   Error: Abort / Retry / Ignore
  * 5 critical Error: Abort / Retry
  * 6 fatal	Error: Exit
  *
  * TODO:
  * write the Log in the ResultSetSep Format to be able to later merge (union) and Query it.
  *
  * TODO:
  * Bind an Instance of this Log Class to an IP Port or make it Remoteable
  * to support distributed Logging, which is useful for distributed Applications.
  * An Alternative to distributed Logging is to merge (by Time) and split (by Client)
  * the distributed Logs after Testing,
  * but that requires either Time Synchonization or (better) unique Call / TX IDs (ascending)
  *
  * Usage:
  * Log.L.L("Starting").l(arg1).l(arg2).l(arg3); //is equivalent to
  * Log.L("Starting" + arg1 + arg2 + arg3); //this
  *
  * Interfaces:
  * PrintStreamOut, FormatterOut, PrintStream, StreamOut
  *
  * Design Decisions:
  * Made final to speed up Operation.
  * Does not implement the Interface
  * @see IFormatOut
  * because it does not enforce a certain Formatting.
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:54:24Z
  * digest: d45324ee8c057fe0ebfc7029448c0b07b13fa27a19711ff699051f8cb49b57af
  * stale: false
  * tags: [code/logging, code/date_formatting]
  * concepts: [Logging Framework]
  * facets: {layer: infrastructure, status: broken, complexity: medium}
  * -->
  */
final public class Log 
extends StreamOutPrimitive {

	/** Reference to the Default Log streamIO.
	  * If set to null, no Logging takes place!
	  * Declaring it final enables the Compiler
	  * to remove and thus optimize any Logging Calls
	  * when testing for null first:
	  * if (Log.L != null) Log.L.l(....);
	  * you can also set L to a valid Value
	  * and switch off Logging by setting Log.L.log = false;
	  * 
	  * Connected to System.err, 
	  * so that the Logging Output can easily be separated from the regular Output! 
	  */
	final static public Log L =
		new Log(System.err, true); //System.getProperty("debug") == null);
	
	///////////////////////////////////////////////////////////////////////
	/// Example of how to log a CVS Version Tag and make it available at Runtime
	/// put after the Declaration of L, because it needs an initialized L Variable
	///////////////////////////////////////////////////////////////////////
		
	/** 
	 * private to enforce overwriting in Child Classes	 
	 * public to allow Modules to query the Version
	 */
	final static public String _VERSION ="$Id: Log.java,v 1.4 2002/11/08 19:17:11 mheuer Exp $";

	/** Default Log Message for the Time, if none is given	 */
	public static String DEFAULT_TIMER_MSG = "Duration of Time Measurement:"; 
	
	/** logs the Class Loading Process, in the Log Class this possibly creates a Bootstrap Problem	 */
	static { N(_VERSION).n(); }
		
	////////////////////////////////////////////////////////////////////////////////
	//  #region : static Methods to help handling User Input
	////////////////////////////////////////////////////////////////////////////

	/** Number of Filter Characters to expect on a single Stack Trace: 2 for CR and LF */
	private static final int NEST_INCREMENT = 2;

	/////////////////////////////////////////////////////////////////////////////////////
	/// Compatibility to Log4J
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Log Level for a fatal Error that aborts the Application, matching Log4J's FATAL. */
	final static public int LEVEL_FATAL = 50000;

	/** Log Level for a caught or runtime Error that does not necessarily abort the Application, matching Log4J's ERROR. */
	final static public int LEVEL_ERROR = 40000;

	/** Log Level for a Warning, matching Log4J's WARN. */
	final static public int LEVEL_WARN = 30000;

	/** Log Level for informational Messages, matching Log4J's INFO. */
	final static public int LEVEL_INFO = 20000;

	/** Log Level for Debugging Output, matching Log4J's DEBUG. */
	final static public int LEVEL_DEBUG = 0;

	/** Log Level below {@link #LEVEL_DEBUG} for Trace Stack Output. */
	final static public int LEVEL_TRACE = -1;

	/** Log Level below {@link #LEVEL_TRACE}, reserved for Test-only Output. */
	final static public int LEVEL_TEST = -2;
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Helper Method for manual key Press
	 * Since a streamIO synchronously waits for input,
	 * this Method blocks until all Data is entered.
	 *
	 * It is designed to be used with the System.in Console Input.
	 */
	final static public String READ_STRING() { //throws java.io.IOException {
		return READ_STRING(System.in);
	}
	
	/**
	 * Helper Method for manual key Press
	 * Since a streamIO synchronously waits for input,
	 * this Method blocks until all Data in a Line is entered.
	 *
	 * It is designed to be used with the System.in Console Input on all Platforms.
	 * It also catches any Exceptions and returns null instead.
	 *
	 * @see BufferedReader.readLine() which reads up to the next CR, LF or CR/LF!
	 */
	public static String READ_STRING(
		java.io.InputStream in) { //throws java.io.IOException {
		final StringBuffer SB = new StringBuffer();
		try {
			do {
				final char b = (char) in.read();
				SB.append(b);
			} while (in.available() > 0);
			return SB.toString();
		} catch (final IOException ignored) {
		}
		return null;
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Default Value for the Default Log Level of newly created Log Objects */
	public static int DEFAULT_LEVEL = 0;
	
	/** Default Value for the Threshold of newly created Log Objects */
	public static int DEFAULT_THRESHOLD = 0;
//		Integer.getInteger("ThresholdLog", 0).intValue();
	
	/** Default Value for the streamIO of newly created Log Objects */
	public static OutputStream DEFAULT_STREAM = System.out;
	
	/** Reference to the System Output streamIO	 */
	final static public Log OUT = new Log(System.out);
	
	/** Reference to the System Error streamIO	 */
	final static public Log ERR = new Log(System.err);
	//, System.getProperty("debug")==null);
	
	//Reading Properties is not allowed for an Applet! => Exception!
	
	/** To switch logging off completely, analog to L == null.
	  * Using a brief boolean Variable to make the Code even shorter */
	//	public static boolean l;
	
	/** Counter for the Object IDs 	 */
	protected static int ID;
	
	/** Date-Time Pattern (SimpleDateFormat syntax) used to format Dates in {@link #GET_XML_DATE()} and {@link #GET_XML_DATE(Date)}. */
	public static String XML_DATE_FORMAT = "yyyy.MM.dd'T'HH:mm:ss.SSS";
	//also get the MilliSeconds!

	/** Shared {@link DateFormat} instance, initialized from {@link #XML_DATE_FORMAT}, used to render Dates for logging. */
	// TODO: LOGIC: SimpleDateFormat is not thread-safe, but this single static instance is shared and called from GET_XML_DATE()/GET_XML_DATE(Date) across all Loggers and Threads; concurrent calls can corrupt the formatted output or throw.
	public static DateFormat XML_DATE_FORMATTER =
		new SimpleDateFormat(XML_DATE_FORMAT);

	/** Formats the given Date using {@link #XML_DATE_FORMAT}.
	 * @return a String with XML formatted Date and Time */
	final static public String GET_XML_DATE(Date dat) {
		return XML_DATE_FORMATTER.format(dat); }

	/** Formats the current Date and Time using {@link #XML_DATE_FORMAT}.
	 * @return a String with XML formatted current Date and Time */
	final static public String GET_XML_DATE() {
		return XML_DATE_FORMATTER.format(new Date()); }
	
	/* @return a String with formatted Date and Time */
	/*	final static public String FORMAT_DATE(Date dat) {
			return   (1900 + dat.getYear())
				+ "-" + (1 + dat.getMonth())
				+ "-" + dat.getDate()
				+ "T" + dat.getHours()
				+ ":" + dat.getMinutes()
				+ ":" + dat.getSeconds()
				+ "." + dat.getTime() % 1000;
		}
	*/
	
	////////////////////////////////////////////////////////////////////////////////
	//  public Member Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Switches logging on or off. 
	 * This affects only the brief Methods, 
	 * not the Methods inherited from PrintStream! 
	 */
	public boolean doLog = true;
	
	/** Switches logging the Call Stack. 	*/
	public boolean doLogStack = false;
	
	/** Switches logging the Time Stamps. 	*/
	public boolean doLogTime = false;
	
	/**
	 * Switches flushing the Log Entries after each call, even the l()
	 * Flushing can be implicitly switched on in the PrintStream via a Constructor Parameter.
	 * Here it can be explicitly and dynamically switched on and off
	 * e.g. to increase Performance in non-critical Sections.
	 */
	public boolean flushLog = false; //k�nnte auch durch den OutputStream bestimmt werden!
	
	/** Prefix used to prepend the Entries */
	public Object prefixLine = "";
		
	/** String for separating the logging Entries. 	*/
	public String SeparatorLine = "\n"; //\r";
	
	//TODO: instead of getting each Property individually,
	//only set the Path to a Property File using a System.getProperty!
	
	/** Threshold for logging the Values.
	 * Values are only logged if their Log Level 
	 * (or the DefaultLevel) 
	 * is larger than or equal to this Value. 
	 */
	public int thresholdLog; // = Integer.getInteger("ThresholdLog",0).intValue(); // = 0;
	
	/**Default Level for logging without giving an explicit Level, 
	 * should be equivalent to 'debug'. 
	 * Initialized by System Properties
	 * @see #DEFAULT_LEVEL is the Default
	 */
	public int defaultLevel = DEFAULT_LEVEL;
	//Integer.getInteger("DefaultLevel", 0).intValue(); // = 0;
	
	/** Default Threshold for notifying the User 	*/
	public int DefaultThresholdNotify = 0; //Integer.getInteger("DefaultThresholdNotify", 0).intValue(); // = 0;
	
	/** Threshold for interactively notifying the User 	*/
	//	public int ThresholdNotify;
	
	/** Returns the underlying Output Stream this Logger writes to.
	 * @return The Output streamIO, could be used to test for null	*/
	public OutputStream getOut() { return out; }
	
	/** Sets a new Output streamIO, the old one is flushed.
	  * This Indirection is sufficient to chamge Destinations at Runtime
	  * @param Out The new Output streamIO	*/
	public void setOut(final OutputStream _out) throws IOException {
		this.flush(); this.out = _out; }
	
	/////////////////////////////////////////////////////////////////////////////////////
	//  private Member Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Thowable Instance for acquiring the Stack Information 	*/
	protected Throwable stacker =
		new Throwable() {//excellent use for a local Class
			/**
			 * 
			 */
			private static final long serialVersionUID = 1347750380801196825L;

			public String toString() { return ""; } };
		
	/** FilterOutputStream for counting and filtering the CR from the Stack Information 	*/
	final protected StringBufferOutputStream bufferOut = new StringBufferOutputStream();
	
	/** PrintStream for counting and filtering the CR from the Stack Information 	*/
	final protected PrintStream streamFilter = new PrintStream(bufferOut);
	
	/** FilterOutputStream for counting and filtering the CR from the Stack Information 	*/
	final protected StringBuffer buffer = bufferOut.getBuffer();
	
	/** Counter Value to track Counts for the whole Class analyzed; 
	 * could also be a static Member of the Class logged, 
	 * but that would blur it's Design. 
	 * 
	 * Since there typically is one Instance of Log per Class analyzed, 
	 * it is sufficient to have a single Counter, instead of a HashMap of Counters! 
	 * Rather use different Loggers for this. 
	 * 
	 * This could e.g. count the Number of Instances, 
	 * or the Number of Method Calls of a specific Method. 
	 * Since a Class should only have a single Purpose, 
	 * the Usage of this Counter should be well-defined! 
	 */
	public int counter; 
	
	/** Timer Value to track Durations.
	 * Positive Values indicate a Time Measurement underway. 
	 * 
	 * Since there typically is one Instance of Log per Class analyzed, 
	 * it is sufficient to have a single Timer, instead of a HashMap of Timers! 
	 * Rather use different Loggers for this. 
	 * 
	 * Since a Class should only have a single Purpose, 
	 * the Usage of this Counter should be well-defined! 
	 */
	protected long timer; 
	
	/** Mapping of Strings to integer Timers.
	 * int is sufficient for 23 Days Duration before rollover happens. 
	 * Since the cast Difference is the same as the Difference of the cast Values, 
	 * it is sufficient to take the Difference between two Measurements downcast to int!  
	 * TODO: take the Definition of PatriciaNode to the Level of Log, 
	 * so it can be used here, without creating circular References.  
	 */
	//protected PatriciaNode timers = new PatriciaNode(); 
	
	/** Mapping of Strings to integer Counters 	 */
	//protected PatriciaNode counters = new PatriciaNode(); 
	
	/** resets the Timer and returns the Time elapsed in milliSeconds since the last Call of timer()
	 * @return the Time elapsed in milliSeconds since the last Call of timer()
	 */
	public long timer() { return timer(DEFAULT_TIMER_MSG); }
	
	/** resets the Timer and returns the Time elapsed in milliSeconds since the last Call of timer()
	 * @param level the Level at which the given Message should be logged. 
	 * @return the Time elapsed in milliSeconds since the last Call of timer()
	 */
	public long timer(final int level) { return timer(DEFAULT_TIMER_MSG, level); }
	
	/** resets the Timer and returns the Time elapsed in milliSeconds since the last Call of timer()
	 * @param logMessage optional Message to log. If null, nothing is logged! 
	 * @return the Time elapsed in milliSeconds since the last Call of timer()
	 */
	public long timer(final String logMessage) { return timer(logMessage, defaultLevel); }
	
	/** resets the Timer and returns the Time elapsed in milliSeconds since the last Call of timer()
	 * @param logMessage optional Message to log. If null, nothing is logged!
	 * @param level the Level at which the given Message should be logged. 
	 * @return the Time elapsed in milliSeconds since the last Call of timer()
	 */
	public long timer(final String logMessage, final int level) {
		final long ret = System.currentTimeMillis() - timer;
		if (logMessage != null) 
			this.n(ret*0.001, level).l("s ", level).l(logMessage, level); 
		timer = System.currentTimeMillis();
		return ret; 
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor defaulting Output to System.out
	  * Filtering out all CR and LFs to fit the Output into one Line */
	public Log() { this(DEFAULT_STREAM, true, DEFAULT_THRESHOLD); }

	/** Empty Constructor defaulting Output to System.out
	  * Filtering out all CR and LFs to fit the Output into one Line */
	public Log(final Object prefix) { this(DEFAULT_STREAM, prefix, true, DEFAULT_THRESHOLD); }
	
	/** Empty Constructor defaulting Output to System.out
	  * Filtering out all CR and LFs to fit the Output into one Line */
	public Log(final Object prefix, final boolean enabled) {
		this(DEFAULT_STREAM, prefix, enabled, DEFAULT_THRESHOLD); }
	
	/** Empty Constructor defaulting Output to System.out
	  * Filtering out all CR and LFs to fit the Output into one Line */
	public Log(final boolean enabled) { this(DEFAULT_STREAM, enabled, DEFAULT_THRESHOLD); }
	
	/** Empty Constructor defaulting Output to System.out
	  * Filtering out all CR and LFs to fit the Output into one Line */
	public Log(final int threshold) { this(DEFAULT_STREAM, true, threshold); }
	
	/** Empty Constructor defaulting Output to System.out
	  * Filtering out all CR and LFs to fit the Output into one Line */
	public Log(final Object prefix, final int threshold) {
		this(DEFAULT_STREAM, prefix, true, threshold); }
	
	/** Initializing Constructor
	  * Filtering out all CR and LFs to fit the Output into one Line */
	public Log(final OutputStream Out) { this(Out, true, DEFAULT_THRESHOLD); }
	
	/** Initializing Constructor
	  * Filtering out all CR and LFs to fit the Output into one Line */
	public Log(final OutputStream Out, final Object prefix) {
		this(Out, prefix, true, DEFAULT_THRESHOLD); }
	
	/** Initializing Constructor
	  * Filtering out all CR and LFs to fit the Output into one Line */
	public Log(final OutputStream Out, final int threshold) {
		this(Out, true, threshold); }
	
	/** Initializing Constructor
	  * Filtering out all CR and LFs to fit the Output into one Line */
	public Log(final OutputStream Out, final Object prefix, final int threshold) {
		this(Out, prefix, true, threshold); }
	
	/** Initializing Constructor
	  * Filtering out all CR and LFs to fit the Output into one Line */
	public Log(final OutputStream Out, final boolean enabled) {
		this(Out, enabled, DEFAULT_THRESHOLD); }
	
	/** Initializing Constructor
	  * Filtering out all CR and LFs to fit the Output into one Line */
	public Log(final OutputStream Out, final Object prefix, final boolean enabled) {
		this(Out, prefix, enabled, DEFAULT_THRESHOLD); }
	
	/** Initializing Constructor
	  * Filtering out all CR and LFs to fit the Output into one Line */
	public Log(
		final OutputStream out_,
		final boolean enabled,
		final int threshold) { //this(Out, enabled); }
		this(out_, "", enabled, threshold);
	} //

	/** Initializing Constructor
	  * Filtering out all CR and LFs to fit the Output into one Line */
	public Log(
		final OutputStream out_,
		final Object prefix,
		final boolean enabled,
		final int threshold) { //this(Out, enabled); }
		super(out_);
		prefixLine = prefix;
		doLog = enabled;
		this.thresholdLog = threshold;
		bufferOut.filter = "\n\r";
	} //SBOS.Replace = Separator; }
	
	/** Initializing Constructor
	  * Filtering out all CR and LFs to fit the Output into one Line */
	//	public Log(final PrintStream Out) { this(Out, true); }

	/** Initializing Constructor
	  * Filtering out all CR and LFs to fit the Output into one Line */
	//	public Log(final PrintStream Out, final boolean enabled) {
	//		super(Out); SBOS.Filter = "\n\r"; }//SBOS.Replace = Separator; }

	////////////////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Method to log on the Debugging Level
	 * 
	 * @param arg the Object to log
	 * @return always true to allow for using it with the 'assert' Statement!
	 */
	public boolean trace(final Object arg){ this.n(arg, LEVEL_TRACE); return true; }
	
	/**Method to log on the Debugging Level
	 * 
	 * @param arg the Object to log
	 * @return always true to allow for using it with the 'assert' Statement!
	 */
	// TODO: LOGIC: debug() logs at LEVEL_ERROR instead of LEVEL_DEBUG (copy-paste from error()); messages logged via debug() are misclassified as errors.
	public boolean debug(final Object arg){ this.n(arg, LEVEL_ERROR); return true; }
	
	/**Method to log on the Info Level
	 * 
	 * @param arg the Object to log
	 * @return always true to allow for using it with the 'assert' Statement!
	 */
	public boolean info(final Object arg){ this.n(arg, LEVEL_INFO); return true; }
	
	/**Method to log on the Debugging Level
	 * 
	 * @param arg the Object to log
	 * @return always true to allow for using it with the 'assert' Statement!
	 */
	public void warn(final Object arg){ this.n(arg, LEVEL_WARN); } //return true; }
	
	/**Method to log on the Debugging Level
	 * 
	 * @param arg the Object to log
	 * @return always true to allow for using it with the 'assert' Statement!
	 */
	public void error(final Object arg){ this.n(arg, LEVEL_ERROR); } //return true; }
	
	/**Method to log on the Debugging Level
	 * 
	 * @param arg the Object to log
	 * @return always true to allow for using it with the 'assert' Statement!
	 */
	public void fatal(final Object arg){ this.n(arg, LEVEL_FATAL); } //return true; }
	
	/**
	 * Helper Method for manual key Press
	 * Since a streamIO synchronously waits for input,
	 * this Method blocks until all Data is entered.
	 *
	 * It is designed to be used with the System.in Console Input.
	 */
	final public String readString() { //throws java.io.IOException {
		return readString(System.in);
	}
			
	/**
	 * Helper Method for manual key Press
	 * Since a streamIO synchronously waits for input,
	 * this Method blocks until all Data is entered.
	 *
	 * It is designed to be used with the System.in Console Input.
	 */
	final public String readString(java.io.InputStream in) { //throws java.io.IOException {
		return readString(in, "Enter a String:");
	}
		
	/**
	 * Helper Method for manual key Press
	 * Since a streamIO synchronously waits for input,
	 * this Method blocks until all Data is entered.
	 *
	 * It is designed to be used with the System.in Console Input.
	 */
	final public String readString(final String prompt) { //throws java.io.IOException {
		return readString(System.in, prompt);
	}
	
	/**
	 * Helper Method for manual key Press
	 * Since a streamIO synchronously waits for input,
	 * this Method blocks until all Data is entered.
	 *
	 * It is designed to be used with the System.in Console Input.
	 * Prompt only when Log Level is negative, 
	 * so the Flow is not interrupted!
	 */
	final public String readString(final java.io.InputStream in, final String prompt) { //throws java.io.IOException {
		if (doLog && this.thresholdLog < 0) {
			println(prompt);
			return READ_STRING(in); 
		}
		return "";
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log l(final boolean value) {
		if (!doLog) 
			return this;
		if (defaultLevel < thresholdLog) //early Return
			return this;
		print(separator);
		print(value);
		if (flushLog)
			flush();
		return this;
	}

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log l(final boolean value, final int level) {
		if (!doLog) 
			return this;
		if (level < thresholdLog) //early Return
			return this;
		print(separator);
		print(value);
		if (flushLog)
			flush();
		return this;
	}
	
	/** Appends the Separator String to the current Log Entry Line. 
	 * This is necessary due to the Fact that Strings and StringBuffers 
	 * are not separated automatically to allow for easy and performant concatenation. 
	 * The Method Name is kept short on purpose to enable fast Logging.
	 * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log s() { 
		if (!doLog) 
			return this;
		if (defaultLevel < thresholdLog) //early Return
			return this;
		print(separator); 
		if (flushLog)
			flush();
		return this; }
	
	/** Appends the Separator String to the current Log Entry Line. 
	 * This is necessary due to the Fact that Strings and StringBuffers 
	 * are not separated automatically to allow for easy and performant concatenation. 
	 * The Method Name is kept short on purpose to enable fast Logging.
	 * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log s(final int level) { 
		if (!doLog) 
			return this;
		if (level < thresholdLog) //early Return
			return this;
		print(separator); 
		if (flushLog)
			flush();
		return this; }
	
	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log l(final char value) {
		if (!doLog) 
			return this;
		if (defaultLevel < thresholdLog) //early Return
			return this;
		print(separator);
		print(value);
		if (flushLog)
			flush();
		return this;
	}

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log l(final char value, final int Level) {
		if (!doLog) 
			return this;
		if (Level < thresholdLog) //early Return
			return this;
		print(separator);
		print(value);
		if (flushLog)
			flush();
		return this;
	}

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log l(final char[] value) {
		if (!doLog) 
			return this;
		if (defaultLevel < thresholdLog) //early Return
			return this;
		//print(separator); //left out on purpose
		write(value);
		if (flushLog)
			flush();
		return this;
	}

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log l(final char[] value, final int Level) {
		if (!doLog) 
			return this;
		if (Level < thresholdLog) //early Return
			return this;
		super.write(value);
		//super.print(separator); //left out on purpose
		if (flushLog)
			this.flush();
		return this;
	}

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log l(final double value) {
		if (!doLog) 
			return this;
		if (defaultLevel < thresholdLog) //early Return
			return this;
		print(separator); //
		print(value);
		if (flushLog)
			flush();
		return this;
	}

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log l(final float value) {
		if (!doLog) 
			return this;
		if (defaultLevel < thresholdLog) //early Return
			return this;
		print(separator); //
		print(value);
		if (flushLog)
			flush();
		return this;
	}
	
	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log l(final double value, final int level) {
		if (!doLog) 
			return this;
		if (level < thresholdLog) //early Return
			return this;
		print(separator); //
		print(value);
		if (flushLog)
			flush();
		return this;
	}

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log l(final float value, final int level) {
		if (!doLog) 
			return this;
		if (level < thresholdLog) //early Return
			return this;
		print(separator); //
		print(value);
		if (flushLog)
			flush();
		return this;
	}
	
	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log l(final long value) {
		if (!doLog) 
			return this;
		if (defaultLevel < thresholdLog) //early Return
			return this;
		print(separator); //
		print(value);
		if (flushLog)
			flush();
		return this;
	}

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log l(final long value, final int level) {
		if (!doLog) 
			return this;
		if (level < thresholdLog) //early Return
			return this;
		print(separator); //
		print(value);
		if (flushLog)
			this.flush();
		return this;
	}

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log l(final Object value) {
		if (!doLog) 
			return this;
		if (defaultLevel < thresholdLog) //early Return
			return this;
		print(separator); //
		addItems(value, Integer.MAX_VALUE);
		if (flushLog)
			flush();
		return this;
	}

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log l(final IIStreamIn stream) {
		if (!doLog) 
			return this;
		if (defaultLevel < thresholdLog) //early Return
			return this;
		print(separator); //
		if(stream == null)
			addItems(stream, Integer.MAX_VALUE);
		else 
			addItems(stream); 
		if (flushLog)
			flush();
		return this;
	}

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log l(final Object value, final int level) {
		if (!doLog) 
			return this;
		if (level < thresholdLog) //early Return
			return this;
		print(separator); //
		addItems(value);
		if (flushLog)
			flush();
		return this;
	}

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log l(final String value) {
		if (!doLog) 
			return this;
		if (defaultLevel < thresholdLog) //early Return
			return this;
		//print(separator); //left out on purpose
		print(value);
		if (flushLog) 
			flush();
		return this;
	}

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log l(final String Comment, final int Level) {
		if (!doLog) 
			return this;
		if (Level < thresholdLog) //early Return
			return this;
		//print(separator); //left out on purpose
		print(Comment);
		if (flushLog)
			flush();
		return this;
	}

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log l(final Throwable x) {
		if (!doLog) 
			return this;
		if (defaultLevel < thresholdLog) //early Return
			return this;
		print(separator); //
		x.printStackTrace(this);
		if (flushLog)
			flush();
		return this;
	}

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log l(final Throwable x, final int level) {
		if (!doLog) 
			return this;
		if (level < thresholdLog) //early Return
			return this;
		x.printStackTrace(this); //could also use 'filter' to avoid CRLFs
		super.print(separator); //this contains both the Exception String and the Stack Trace!
		if (flushLog)
			this.flush();
		return this;
	}
	
	/** Short command for flushing the Log (to indicate the end of a Log Entry)
	  * If not used at the End of all Parameters,
	  * they will possibly be missing on a Crash.
	  * Called on every L Entry, so this is not mandatory */
	public void f() { //if (flushLog)
		flush(); }
	
	/**
	 * Tests whether this Logger would currently log at its {@link #defaultLevel}.
	 * @return true when this Logger would currently log at it's Default Level!
	 */
	final public boolean logs() { return logs(defaultLevel); }

	/**
	 * Tests whether this Logger would currently log at the given Level.
	 * @param level the Level to test, compared against {@link #thresholdLog}
	 * @return true when this Logger would currently log at this Level!
	 */
	final public boolean logs(final int level) {
		if (!doLog) 
			return false;
		return (level >= thresholdLog);
	}
	
	/** Adds a NEW Log Entry Line with the given Object
	  * A Log Level is checked to be above the Threshold
	  * The Time and Stack are logged optionally
	  * Stack Traces are only logged up to the negative Threshold!
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log n(final double val) { 
		if (!doLog) 
			return this;
		if (defaultLevel < thresholdLog) //early Return
			return this;
		return n(0, Double.toString(val), defaultLevel); }
	
	/** Adds a NEW Log Entry Line with the given Object
	  * A Log Level is checked to be above the Threshold
	  * The Time and Stack are logged optionally
	  * Stack Traces are only logged up to the negative Threshold!
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log n(final double val, final int Level) {
		if (!doLog) 
			return this;
		if (Level < thresholdLog) //early Return
			return this;
		return n(0, Double.toString(val), Level); }
	
	/** Adds a NEW Log Entry Line with the given Object
	  * A Log Level is checked to be above the Threshold
	  * The Time and Stack are logged optionally
	  * Stack Traces are only logged up to the negative Threshold!
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log n(final Object obj, final int Level) {
		if (!doLog) 
			return this;
		if (Level < thresholdLog) //early Return
			return this;
		return n(0, obj, Level);
	}

	/** Adds a NEW Log Entry Line with the given Object
	  * A Log Level is checked to be above the Threshold
	  * The Time and Stack are logged optionally
	  * Stack Traces are only logged up to the negative Threshold!
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	private Log n(final int nesting, final Object obj, final int level) {
		if (!doLog) 
			return this;
		if (level < thresholdLog) //early Return
			return this;
		writeLineStarter(nesting + NEST_INCREMENT);
		//print(separator); //not necessary; addItems already adds a Separator
		if (obj instanceof Throwable) {
			((Throwable) obj).printStackTrace(this);
		} else if (obj instanceof String){
			print(this.separator); print(obj.toString()); 
		} else if (obj != null){
			this.addItems(obj, Integer.MAX_VALUE);
		} else 
			print(this.separator); 
		if (flushLog)
			this.flush();
		return this;
	}

	private void writeLineStarter(final int nesting) {
		super.print(SeparatorLine);
		//Start a new Line and flush() only on the next Log Statement!
		if (!flushLog) //if not flushed before (flushing regularly),
			 flush(); //flush now!
		/*		if (level < ThresholdLog) //early Return
					return this;
				}
		*/ 
		if (doLogTime) { //both CR and LF are skipped!
			print(separator);
			print(GET_XML_DATE());
		}
		if (prefixLine != null) {
			print(separator);
			print(prefixLine);
		}
		if (doLogStack) {
			traceStack(nesting+NEST_INCREMENT, false);
		} //print the Output only at the very End! //especially Stack Traces are only logged up to the negative Threshold!
	}

	/** unconditionally prints a one-Line Stack Trace into the current Line of the Log */
	public Log traceStack(int level) {
		if (!doLog) 
			return this;
		if (level < thresholdLog) //early Return
			return this;
		return traceStack(0, false);
	}
		
	/** unconditionally prints a one-Line Stack Trace into the current Line of the Log */
	public Log traceStack() {
		if (!doLog) 
			return this;
		if (defaultLevel < thresholdLog) //early Return
			return this;
		return traceStack(0, false);
	}
		
	/** unconditionally prints a Stack Trace into a single Line of the Log */
	private Log traceStack(int nesting, boolean dummy) {
		super.print(separator);
		super.print(getStack(nesting+1).toString());
		//Support logging only up to a given Level (from the current or the absolute Depth)
		return this;
	}

	/**Starts a NEW Log Entry Line with the given Object or String. 
	 * Only using a String or Object to enforce starting Lines with meaningful Comments! 
	 * 
	 * A Log Level is checked to be above the Threshold
	 * The Time and Stack are logged optionally
	 * Stack Traces are only logged up to the negative Threshold!
	 * The Method Name is kept short on purpose to enable fast Logging.
	 * The Log Object is returned to allow for concatenating e.g. Parameters. 
	 */
	public Log n(final String comment, final int level) {
		if (!doLog) 
			return this;
		if (level < thresholdLog) //early Return
			return this;
		return n(0, comment, level);
	}

	/**Starts a NEW Log Entry Line with the given Object or String. 
	 * Only using a String or Object to enforce starting Lines with meaningful Comments! 
	 * 
	 * A Log Level is checked to be above the Threshold
	 * The Time and Stack are logged optionally
	 * Stack Traces are only logged up to the negative Threshold!
	 * The Method Name is kept short on purpose to enable fast Logging.
	 * The Log Object is returned to allow for concatenating e.g. Parameters. 
	 */
	private Log n(final int nesting, final String comment, final int level) {
		if (!doLog) 
			return this;
		if (level < thresholdLog) //early Return
			return this;
		writeLineStarter(nesting + NEST_INCREMENT);
		super.print(separator);
		super.print(comment);
		if (flushLog)
			this.flush();
		return this;
	}

	/** Adds a new Log Entry Line with the given String Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log n(final Object obj) {
		if (!doLog) 
			return this;
		if (defaultLevel < thresholdLog) //early Return
			return this;
		return n(0, obj, defaultLevel);
	}

	/** Adds a new Log Entry Line with the given String Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	private Log n(final int nesting, final Object obj) {
		if (!doLog) 
			return this;
		if (defaultLevel < thresholdLog) //early Return
			return this;
		return n(nesting + NEST_INCREMENT, obj, defaultLevel);
	}

	/** Adds a new Log Entry Line with the given String Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log n(final String Comment) {
		if (defaultLevel < thresholdLog) //early Return
			return this;
		if (!doLog) 
			return this;
		return n(0, Comment, defaultLevel);
	}

	/** Adds a new Log Entry Line with the given String Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log n() {
		if (!doLog) 
			return this;
		if (defaultLevel < thresholdLog) 
			return this;
		return n(0);
	}

	/** Adds a new Log Entry Line with the given String Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	private Log n(int nesting) {
		if (!doLog) 
			return this;
		if (defaultLevel < thresholdLog) 
			return this;
		return n(nesting + NEST_INCREMENT, null);
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Adds a new Log Entry Line for logging Parameters and local Variables 
	 * in case of an Exception. 
	 * The Exception is rethrown as a LoggedException, which saves Declaration 
	 * and allows to ignore it at higher Levels, if Parameters are not needed. 
	 */
	public void fail(final Throwable exception) { //throws LoggedException
		//if (exception instanceof LoggedException) 
		//	throw exception; 
		throw new RuntimeException(exception); 
		//return enterMethod(true, 0); 
	}
	
	///////////////////////////////////////////////////////////////////////////

	/** Adds a new Log Entry Line for entering or exiting a Method
	  * The Log Object is returned to allow for concatenating e.g. Parameters. 
	  */
	public Log enter() { 
		if (!doLog) 
			return this; 
		if (0 < thresholdLog) //early Return
			return this;
		return enterMethod(true, 0, 0); }
	
	/** Adds a new Log Entry Line for entering a Method
	  * The Log Object is returned to allow for concatenating e.g. Parameters. 
	  */
	public Log enter(final int level) { 		
		if (!doLog) 
			return this; 
		if (level < thresholdLog) //early Return
			return this;
		return enterMethod(true, 0, level); }
	
	/** Adds a new Log Entry Line for exiting a Method
	  * The Log Object is returned to allow for concatenating e.g. Parameters. 
	  */
	public Log exit() { 
		if (!doLog) 
			return this; 
		if (0 < thresholdLog) //early Return
			return this;
		return enterMethod(true, 0, 0); }
	
	/** Adds a new Log Entry Line for exiting a Method
	  * The Log Object is returned to allow for concatenating e.g. Parameters. 
	  */
	public Log exit(final int level) { 
		if (!doLog) 
			return this; 
		if (level < thresholdLog) //early Return
			return this;
		return enterMethod(false, 0, level); }
	
	/** Adds a new Log Entry Line for entering or exiting a Method
	  * The Log Object is returned to allow for concatenating e.g. Parameters. 
	  */
	public Log enter(final boolean enter) { 
		if (!doLog) 
			return this; 
		if (0 < thresholdLog) //early Return
			return this;
		return enterMethod(enter, 0, 0); }
	
	/** Adds a new Log Entry Line for entering or exiting a Method
	  * The Log Object is returned to allow for concatenating e.g. Parameters. 
	  */
	public Log enter(final int level, final boolean enter) { 
		if (!doLog) 
			return this; 
		if (level < thresholdLog) //early Return
			return this;
		return enterMethod(enter, 0, level); }
	
	///////////////////////////////////////////////////////////////////////////

	/**Helper Method to save coding the Method Name as a String Constant, quite expensive... 
	 * @return the Stack Trace separated by Tabs instead of CRLFs, starting nestLevel above this Method	 */
	public StringBuffer getStack(final int nestLevel) {
		bufferOut.counter = -nestLevel-6; //skip the first 4 Stack Trace Elements
		buffer.setLength(0); //don't assemble the Call to (the final) L Method,
		//this is easier with the Java 1.4 Throwable.getStackTrace() Method
		stacker.fillInStackTrace();
		//filter CR , Problem: you know only afterwards, whether you should have logged or not!
		//would have been easier if all Stack Entries starting with '	at streamIO.Log.' would be skipped. 
		stacker.printStackTrace(streamFilter); //this); //}
		return buffer; 
	}
	
	/** Adds a new Log Entry Line for entering or exiting a Method
	  * The Log Object is returned to allow for concatenating e.g. Parameters. 
	  */
	private Log enterMethod(final boolean enter, final int nesting) {
		if (!doLog) 
			return this; 
		if (0 < thresholdLog) //early Return
			return this;
		return enterMethod(enter, nesting, 0); }
	
	/** Adds a new Log Entry Line for entering or exiting a Method
	  * The Log Object is returned to allow for concatenating e.g. Parameters. 
	  */
	private Log enterMethod(final boolean enter, final int nesting, final int level) {
		if (!doLog) 
			return this; 
		if (level < thresholdLog) //early Return
			return this;
		final StringBuffer buffer = getStack(nesting+1);
		buffer.setLength(buffer.indexOf("\t", 1)); //strip all but the current Stack Position
		return enter(enter, buffer.toString(), level); }
	
	/** Adds a new Log Entry Line for entering or exiting a Method
	  * The Log Object is returned to allow for concatenating e.g. Parameters. 
	  */
	public Log enter(final boolean enter, final String methodName) {
		if (!doLog) 
			return this; 
		if (0 < thresholdLog) //early Return
			return this;
		return enter(enter, methodName, 0); 
	}
	
	/** Adds a new Log Entry Line for entering or exiting a Method
	  * The Log Object is returned to allow for concatenating e.g. Parameters. 
	  */
	// TODO: LOGIC: ignores the 'level' parameter and always compares 0 < thresholdLog instead of 'level < thresholdLog', unlike the sibling overloads; a caller passing a level below thresholdLog to suppress logging will still have it logged (and vice versa for a level above 0).
	public Log enter(final boolean enter, final String methodName, final int level) {
		if (!doLog)
			return this;
		if (0 < thresholdLog) //early Return
			return this;
		return method(enter ? "Entering" : "Exiting", methodName, enter ? "Parameters" : "returns"); 
	}
	
	/** Adds a new Log Entry Line for entering or exiting a Method
	  * The Log Object is returned to allow for concatenating e.g. Parameters. 
	  */
	private Log method(String state, String methodName, String addtlInfo) {
		n(0, state); 
		this.write(separator);
		this.write(methodName);
		this.write(separator);
		this.write(separator);
		this.write(addtlInfo);
		return this; 
	}

	/** Adds a new Log Entry Line for logging Parameters and local Variables 
	 * in case of an Exception. 
	 * The Exception should be rethrown using the fail() Method. 
	 */
	public Log caught(final Throwable exception) { //throws LoggedException
		int nesting = 1; 
		final StringBuffer buffer = getStack(nesting+1);
		buffer.setLength(buffer.indexOf("\t", 1)); //strip all but the current Stack Position
		String MethodName = buffer.toString(); 
		//TODO: log Method Name indicating the Exception! 
		return this; 
	}
	
	/** Adds a new Log Entry Line for entering or exiting a Transaction
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log traceTrans(final String txName, final boolean enter) {
		return traceTrans(0, txName, enter);
	}

	/** Adds a new Log Entry Line for entering or exiting a Transaction
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	private Log traceTrans(
		final int nesting,
		final String txName,
		final boolean enter) {
		if (!doLog) 
			return this;
		return n(
			nesting + NEST_INCREMENT,
			(enter ? "Starting" : "Committing") + separator + txName,
			thresholdLog);
	}

	/** Adds a new Log Entry Line for creating or destroying an Object
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log traceObject(final Object obj, final boolean create) {
		return traceObject(0, obj, create);
	}

	/** Adds a new Log Entry Line for creating or destroying an Object
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	private Log traceObject(
		final int nesting,
		final Object obj,
		final boolean create) {
		if (!doLog) 
			return this;
		return n(
			nesting + NEST_INCREMENT,
			(create ? "Creating" : "Destroying") 
			+separator+" instanceOf("+obj.getClass().getName()+")=" 
			+separator+obj.toString(),
			thresholdLog);
	}

	/** Returns the next Object ID available
	  * Used to identify Objects and also their Creation Sequence.
	  * Since Destruction is done automatically and asynchronously,
	  * this is not so important.   */
	public int nextID() {
		return ++ID;
	}

	/** Adds a Log Entry and pushes the current Call Stack with the MethodName.
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	//public Log push(String MethodName, Object self) { }

	/** Pops the current Call Stack and uses the MethodName
	  * to verify the correct Log Usage	 */
	//public Log pop(String MethodName) { }

	/** Pops the current Call Stack 	 */
	//public Log pop() { }

	////////////////////////////////////////////////////////////////////////////////
	/// Overrides of inherited Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for static Member L: all Methods are named 't()' or 'T()'
	////////////////////////////////////////////////////////////////////////////////
	
	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log L(final boolean bool) { return L.l(bool); }
	
	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log L(final boolean bool, final int Level) { return L.l(bool, Level); }
	
	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log L(final char chr) { return L.l(chr); }

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log L(final char Comment, final int Level) { return L.l(Comment, Level); }

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log L(final char[] chrs) { return L.l(chrs); }

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log L(final char[] chrs, final int Level) { return L.l(chrs, Level); }

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log L(final double dbl) { return L.l(dbl); }

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log L(final double dbl, final int Level) { return L.l(dbl, Level); }

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log L(final float flt) { return L.l(flt); }
	
	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log L(final float flt, final int Level) { return L.l(flt, Level); }
	
	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log L(final int integer) { return L.l(integer); }

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log L(final int integer, final int Level) { return L.l(integer, Level); }
	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log S() { return L.s(); }

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log S(final int _level) { return L.s(_level); }
	
	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log L(final long lng) { return L.l(lng); }

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log L(final long lng, final int Level) { return L.l(lng, Level); }

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log L(final Object obj) { return L.l(obj); }

	/** Appends to the current Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log L(final Object obj, final int Level) { return L.l(obj, Level); }

	/** Appends to the current Log Entry Line the given Comment
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log L(final String str) { return L.l(str); }

	/** Appends to the current Log Entry Line the given Comment
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log L(final String str, final int Level) { return L.l(str, Level); }

	/** Appends to the current Log Entry Line the given Exception
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log L(final Exception x) { return L.l(x); }

	/** Appends to the current Log Entry Line the given Exception
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log L(final Exception x, final int Level) { return L.l(x, Level); }

	/** Short command for flushing the Log (to indicate the end of a Log Entry)
	  * If not used at the End of all Parameters,
	  * they will possibly be missing on a Crash.
	  * But at least the L Entry will be written, so this is not mandatory */
	public static void F() { L.f(); }

	/** Adds a new Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log N(final String str, final int Level) { return L.n(0, str, Level); }

	/** Adds a new Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log N(final Object obj, final int Level) { return L.n(0, obj, Level); }

	/** Adds a new Log Entry Line with the given String Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log N(final Object obj) { return L.n(0, obj); }

	/** Adds a new Log Entry Line with the given String Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log N(final String str) { return L.n(0, str); }

	/** Adds a new Log Entry Line with the given String Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log N() { return L.n(DEFAULT_LEVEL); }
	
	/** Adds a new Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log TODO(final String str, final int Level) { return L.todo(0, str, Level); }
	
	/** Adds a new Log Entry Line with the given String Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log TODO(final String str) { return L.todo(0, str, DEFAULT_LEVEL); }
	
	/** Adds a new Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	private Log todo(final int nesting, final String str, final int Level) {
		return n(nesting+NEST_INCREMENT, "TODO"+separator+str, Level);
	}
			
	/** Adds a new Log Entry Line with the given Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log todo(final String str, final int level) {
		if (!doLog) 
			return this;
		if (level < thresholdLog) //early Return
			return this;
		return todo(0, str, level);
	}
			
	/** Adds a new Log Entry Line with the given String Object
	  * The Method Name is kept short on purpose to enable fast Logging.
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log todo(final String str) {
		if (!doLog) 
			return this;
		if (defaultLevel < thresholdLog) //early Return
			return this;
		return todo(0, str, defaultLevel);
	}
	
	/** Adds a new Log Entry Line with the given Object
	 * The Method Name is kept short on purpose to enable fast Logging.
	 * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log DEPRECATE(final String str, final int Level) {
		return L.deprecate(0, str, Level);
	}
		
	/** Adds a new Log Entry Line with the given String Object
	 * The Method Name is kept short on purpose to enable fast Logging.
	 * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log DEPRECATE(final String str) { return L.deprecate(0, str, DEFAULT_LEVEL); }
		
	/** Adds a new Log Entry Line with the given Object
	 * The Method Name is kept short on purpose to enable fast Logging.
	 * The Log Object is returned to allow for concatenating e.g. Parameters. 
	 */
	private Log deprecate(final int nesting, final String str, final int Level) {
		//throw new RuntimeException("calling Deprecated Method:"+str); 
		return n(nesting+NEST_INCREMENT, "DEPRECATED", Level).s(Level).l(str, Level);
	}
				
	/** Adds a new Log Entry Line with the given Object
	 * The Method Name is kept short on purpose to enable fast Logging.
	 * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log deprecate(final String str, final int level) {
		if (!doLog) 
			return this;
		if (level < thresholdLog) //early Return
			return this;
		return deprecate(0, str, level);
	}
				
	/** Adds a new Log Entry Line with the given String Object
	 * The Method Name is kept short on purpose to enable fast Logging.
	 * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public Log deprecate(final String str) {
		if (!doLog) 
			return this;
		if (defaultLevel < thresholdLog) //early Return
			return this;
		return deprecate(0, str, defaultLevel);
	}
		
	/*
	 * an Exception is logged as an Error: Default Levels are
	 * Levels below 0 indicate Trace Stack Levels
	 *-1 TraceStack
	 * 0 Log / Comment / Information
	 * 1 Warning
	 * 2 caught   Exception:
	 * 3 Runtime  Exception:
	 * 4 severe   Error: Abort / Retry / Ignore
	 * 5 critical Error: Abort / Retry
	 * 6 fatal	Error: Exit
	 */

	/** Adds a new Log Entry Line with the given Object
	  * Stack Traces are only logged up to the negative Threshold!
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log ENTER_METHOD(final boolean _Enter) { return L.enter(_Enter); }

	/** unconditionally prints a Stack Trace into a single Line of the Log */
	public static Log TRACE_STACK() { return L.traceStack(); }

	/** Adds a new Log Entry Line with the given Object
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log TRACE_TRANS(final String TxName, final boolean Enter) {
		return L.traceTrans(0, TxName, Enter);
	}

	/** Adds a new Log Entry Line with the given Object
	  * The Log Object is returned to allow for concatenating e.g. Parameters. */
	public static Log TRACE_OBJECT(final Object obj, final boolean Create) {
		return L.traceObject(0, obj, Create);
	}

	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) throws Exception {
		testDefaultLogging();
		//The Test for a static final Variable can be optimized
		//and prevents unnecessary Calls and Argument Preparations!
		//	Log.L.logStack = true;
		Log.L.flushLog = true;
		if (Log.L != null)
			Log.ENTER_METHOD(true).l(args);
		//Trace entering and append all Parameters
		if (Log.L != null)
			Log.L.n("Testing " + Log.class.getName());

		//	Log.L.printStackTrace();

		//	if(Log.l) Log.L.L("Testing " + Log.class.getName()); //not using a boolean Flag
		if (Log.L != null)
			Log.ENTER_METHOD(false);
		//Trace exiting and append the Return Value
	}
	
	/** Prints out the System Properties into the Log */
	public static void PRINT_SYSTEM_PROPERTIES() { //int i, int j, int k) { //just testing local Variables.
		System.getProperties().list(Log.L);
	}

	/** Tests the Standard Java Logging Feature @since JDK 1.4 */ 
	private static final void testDefaultLogging() {
		Logger l = Logger.getLogger(Log.class.getName());
		//l = Logger.global; 
		l.finest("finest");    //These are the Log4J Levels
		l.finer("finer");      //DEBUG
		l.fine("fine");        //INFO
		l.info("info");        //WARN
		l.warning("warning");  //ERROR
		l.severe("severe");    //FATAL
	}

	/**
	  * The main entry point for the application.
	  *
	  * @param args Array of parameters passed to the application
	  * via the command line.	 */
	public static void main(final String[] args) throws Exception {
		assert null != L.n("Testing ").l(Log.class.getName());
		Runtime.getRuntime().traceMethodCalls(true); //has no Effect!
		PRINT_SYSTEM_PROPERTIES();
		//4,5,6); //just testing logging local Variables
		testIt(args);
		/*	Stream.Object.Merger.testIt(args);
			Stream.Object.Enumerator.Container.Relation.testIt(args);
			Stream.Object.Enumerator.Container.RecordSet.testIt(args);
		
		/*	Stream.Object.Parser.XMLScannerStreamIn.testIt();
			Stream.Object.Parser.ScannerStreamIn.testIt();
			Stream.Object.Parser.XMLWriter.testIt(args);
		//	Stream.Object.BackTracker.BackTracker.testIt(); //takes quite long...
			Stream.Copy.Monoid.Association.testIt();
			Stream.Object.Enumerator.Container.HashContainer.testIt(args);
		//	Stream.Object.StreamSet.testIt(args);
			Log.L.ThresholdLog = -500;
			Log.L.logStack = true;
			Log.L.logTime = true;
			args = new String[3];
			args[0] = "Hi";
			args[1] = "There";
			args[2] = "how";
			testIt(args);
		*/
	}

}
