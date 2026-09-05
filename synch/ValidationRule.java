package synch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.xml.bind.ValidationException;

import math.vector.VectorString;
import streamIO.copy.group.DateTime;
import tester.IEquivalence;
import function.byref.ByRefDouble;

/**
  * Title: ValidationRule<p>
  * Description:
  * Purpose:
  * Describes a parameterized Rule and allows to invoke it.
  * Parameters are:
  * Method Class and Name
  * a fixed Object as Parameter
  *
  * Also contains many Standard Rules as static Methods: 
  * IS_MIN_LENGTH
  *
  *
  * Design Decisions / Implementation Details:
  * As an Indicator of the Statelessnes of Validation Methods
  * only static Methods can act as Validation Methods!
  *
  * On the other Hand evaluating the Parameter may well be a costly Operation
  * e.g. for regular Expressions.
  * Thus it would be of advantage to prepare the Parameter
  * after reading it from the File or DB and to combine it to an Object
  * together with the Method called.
  *
  * A major Design Decision exists between
  * calling a Method only based on its Name and Number of Parameters
  * or also considering the Types of its Parameters.
  * Since Java supports a direct getMethod() Reflection Method
  * the latter Approach is chosen.
  * This requires the Method to implement the Type Check and Cast,
  * a minor Inconvenience, that could be handled by the Reflection Engine automatically.
  * But that would require a more complex Approach to identifying the Method
  * by looping through all Methods and introduce Ambiguity when Methods are overloaded!
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	10-14-2002, 11:00 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:32Z
  * digest: 69ab8550f0470ca094094a078d916abd3940345b4ad56ea8451b1e0e6d3468c8
  * stale: false
  * tags: [code/validation_rule]
  * concepts: [Validation Rule Chain]
  * facets: {layer: domain, status: legacy, complexity: high}
  * -->
  */
public class ValidationRule 
implements  IValidationRule, IValidator {

	///////////////////////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants...
	///////////////////////////////////////////////////////////////////////////////////////////////////

	/** Default Value for the relative Accuracy
	 * Accessor Methods to test whether this is non-negative */
	final static public double RELATIVE_ACCURACY_MAXIMUM = 1;

	/** Default Value for the relative Accuracy
	 * Accessor Methods to test whether this is non-negative */
	public static double RELATIVE_ACCURACY_DEFAULT = 1e-6;

	/** Accessor Method to set the relative Accuracy  */
	final static public void SET_RELATIVE_ACCURACY_DEFAULT(double value) {
		if (value < 0) {
			throw new IllegalArgumentException(
				"Value must not be negative, but actually is:" + value);
		}
		if (value > RELATIVE_ACCURACY_MAXIMUM) {
			throw new IllegalArgumentException(
				"Value must not be larger than "
					+ RELATIVE_ACCURACY_MAXIMUM
					+ ", but actually is:"
					+ value);
		}
		RELATIVE_ACCURACY_DEFAULT = value;
	}

	/** Accessor Method to set the relative Accuracy  */
	final static public double GET_RELATIVE_ACCURACY_DEFAULT() {
		return RELATIVE_ACCURACY_DEFAULT;
	}

	/** Default Value for the absolute Accuracy
	 * @todo: use Accessor Methods to test whether this is non-negative */
	public static double ABSOLUTE_ACCURACY_DEFAULT = 1e-6;

	/** Accessor Method to set the absolute Accuracy  */
	final static public void SET_ABSOLUTE_ACCURACY_DEFAULT(double value) {
		if (value < 0) {
			throw new IllegalArgumentException(
				"Value must not be negative, but actually is:" + value);
		}
		ABSOLUTE_ACCURACY_DEFAULT = value;
	}

	/** Accessor Method to set the absolute Accuracy  */
	final static public double GET_ABSOLUTE_ACCURACY_DEFAULT() {
		return ABSOLUTE_ACCURACY_DEFAULT;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////
	/// #region : static Test Methods, all 1 Parameter- 1 Field Plausis ...
	///////////////////////////////////////////////////////////////////////////////////////////////////

	/** Date Formatter for a Day only */
	final static public String XML_DATE_FORMAT = "yyyy-MM-dd";

	/** Date Formatter for a Day only */
	final static public SimpleDateFormat XML_DATE_FORMATTER =
		new SimpleDateFormat(XML_DATE_FORMAT);

	/** Date Formatter for a Day only */
	final static public String XML_TIME_FORMAT = "HH:mm:ss";

	/** Time Formatter for Time only */
	final static public SimpleDateFormat XML_TIME_FORMATTER =
		new SimpleDateFormat(XML_TIME_FORMAT);

	/** Date Formatter for Date and Time only */
	final static public String XML_TIMESTAMP_FORMAT =
		XML_DATE_FORMAT + "'T'" + XML_TIME_FORMAT;

	/** Date Formatter for Date and Time only */
	final static public SimpleDateFormat XML_TIMESTAMP_FORMATTER =
		new SimpleDateFormat(XML_TIMESTAMP_FORMAT);

	/** Default Formatter used by {@link #PARSE_DATE(String)}, currently the Date-and-Time Formatter */
	final static public DateFormat DEFAULT_DATE_FORMAT =
		XML_TIMESTAMP_FORMATTER;

	/**
	 * parses a Date Parameter
	 * @param date Parameter f�r das Datum.
	 *        kann entweder absolut sein in der Notation YYYY-MM-DD
	 *        oder relativ in der Form +/-nnn[y|M|d|H|m|s]
	 */
	final static public Date PARSE_DATE(final String date, final String format)
		throws java.text.ParseException {
		return PARSE_DATE(date, new SimpleDateFormat(format));
	}

	/**
	 * parses a Date or Time or both Parameter
	 * @param date Parameter f�r das Datum.
	 *        kann entweder absolut sein in der Notation YYYY-MM-DD
	 *        oder relativ in der Form +/-nnn[y|M|d|H|m|s]
	 */
	final static public Date PARSE_DATE_OR_TIME(final String date)
		throws java.text.ParseException {
		boolean hasDate = date.indexOf('-') >= 0;
		boolean hasTime = date.indexOf(':') >= 0;
		Date ret;
		if ((ret = PARSE_REL_DATE_OR_TIME(date)) != null) {
			return ret;
		}
		if (hasDate) {
			if (hasTime) { //both given...
				return XML_TIMESTAMP_FORMATTER.parse(date);
			} else {
				return XML_DATE_FORMATTER.parse(date);
			}
		} else {
			if (hasTime) {
				return XML_TIME_FORMATTER.parse(date);
			} else { //unknown Format...
				return null;
				//				throw new java.text.ParseException(); 
			}
		}
	}

	/**
	 * parses a Date Parameter
	 * @param date Parameter f�r das Datum.
	 *        kann entweder absolut sein in der Notation YYYY-MM-DD
	 *        oder relativ in der Form +/-nnn[y|M|d|H|m|s]
	 */
	final static public Date PARSE_DATE(final String date)
		throws java.text.ParseException {
		return PARSE_DATE(date, DEFAULT_DATE_FORMAT);
	}

	/**
	 * parses a Date Parameter
	 * @param date Parameter f�r das Datum.
	 *        kann entweder absolut sein in der Notation YYYY-MM-DD
	 *        oder relativ in der Form +/-nnn[y|M|d|H|m|s]
	 */
	final static public Date PARSE_DATE(
		final String date,
		final DateFormat format)
		throws java.text.ParseException {
		Date ret;
		if ((ret = PARSE_REL_DATE_OR_TIME(date)) != null) {
			return ret;
		}
		return format.parse(date);
		// XML_DATE_FORMATTER.parse(date); //.getTime());
	}

	/**
	 * parses a Date Parameter
	 * @param date Parameter f�r das Datum.
	 *        kann entweder absolut sein in der Notation YYYY-MM-DD
	 *        oder relativ in der Form +/-nnn[y|M|d|H|m|s]
	 * @return the Date relative to the current Date and Time
	 * or null if the Date is not relative. 
	 */
	final static public Date PARSE_REL_DATE_OR_TIME(final String date)
		throws java.text.ParseException {
		long diff = 0;
		switch (date.charAt(0)) {
			case '+' :
				diff = +1;
				break;
			case '-' :
				diff = -1;
				break;
			default :
				return null; // 
		}
		int len_1 = date.length() - 1;
		switch (date.charAt(len_1)) {
			case 'y' :
				diff *= DateTime.MILLIS_PER_YEAR;
				break;
			case 'M' :
				diff *= DateTime.MILLIS_PER_MONTH;
				break;
			case 'w' :
				diff *= DateTime.MILLIS_PER_WEEK;
				break;
			case 'd' :
				diff *= DateTime.MILLIS_PER_DAY;
				break;
			case 'H' :
				diff *= DateTime.MILLIS_PER_HOUR;
				break;
			case 'm' :
				diff *= DateTime.MILLIS_PER_MIN;
				break;
			case 's' :
				diff *= DateTime.MILLIS_PER_SEC;
				break;
			default :
				throw new java.text.ParseException(
					"Unexpected Character: '" + date.charAt(len_1) + "'",
					len_1);
		}
		return new Date(diff + System.currentTimeMillis());
	}

	/** Diese Methode validiert ob der �bergebene Wert nicht �ber einem maximalen Datum liegt.
	 * @param maxDate Parameter f�r das maximale Datum.
	 *        kann entweder absolut sein in der Notation YYYY-MM-DD
	 *        oder relativ in der Form +/-nnn[Y,M,D]
	 * @param wert zu validierender Wert
	 * @throws ValidationException if the Value 'wert' was invalid
	 */
	public static void VALIDATE_MAX_DATE(
		final Date maxDate,
		final Object wert,
		final int severity_)
		throws InvalidException {
		if (wert == null) {
			return;
		}
		final String strWert = wert.toString();
		if (strWert.length() == 0) {
			return;
		}
		try {
			Date date = PARSE_DATE(strWert);
			//			if (logger.isDebugEnabled()) logger.debug("TIMESTAMP PARSED:"+timeStamp);
			if (date.getTime() > maxDate.getTime()) {
				throw new InvalidException(
					maxDate,
					wert,
					"Date '"
						+ strWert
						+ "' should be less than '"
						+ maxDate
						+ "'!",
					severity_);
			}
		} catch (java.text.ParseException x) {
			throw new InvalidException(
				x,
				wert,
				strWert + " led to the following Exception:" + x.getMessage(),
				severity_);
		}
	}

	/** Diese Methode validiert ob der �bergebene Wert nicht �ber einem maximalen Datum liegt.
	 * @param maxDate Parameter f�r das maximale Datum.
	 *        kann entweder absolut sein in der Notation YYYY-MM-DD
	 *        oder relativ in der Form +/-nnn[Y,M,D]
	 * @param wert zu validierender Wert
	 * @throws ValidationException if the Value 'wert' was invalid
	 */
	public static void VALIDATE_MIN_DATE(
		final Date minDate,
		final Object wert,
		final int severity_)
		throws InvalidException {
		if (wert == null) {
			return;
		}
		final String strWert = wert.toString();
		if (strWert.length() == 0) {
			return;
		}
		try {
			Date date = PARSE_DATE(strWert);
			//			if (logger.isDebugEnabled()) logger.debug("TIMESTAMP PARSED:"+timeStamp);
			if (date.getTime() < minDate.getTime()) {
				throw new InvalidException(
					minDate,
					wert,
					"Date '"
						+ strWert
						+ "' should be greater than '"
						+ minDate
						+ "'!",
					severity_);
			}
		} catch (java.text.ParseException x) {
			throw new InvalidException(
				x,
				wert,
				strWert + " led to the following Exception:" + x.getMessage(),
				severity_);
		}
	}

	/** validates whether the given Value can be divided by the given Module	 */
	final static public boolean IS_MODULE(
		final Object module,
		final Object value) {
		return IS_MODULE(
			ByRefDouble.GET_DOUBLE(module),
			ByRefDouble.GET_DOUBLE(value));
	}

	/** validates whether the given Value can be divided by the given Module	 */
	final static public boolean IS_MODULE(
		final double module,
		final double dblVal) {
		if ((0 < module) && (Math.abs(dblVal % module) > (0.001 * module))) {
			return false;
		}
		return true;
	}

	/** validates whether the given Value can be divided by the given Module	 */
	final static public boolean IS_BOOLEAN(final Object value) {
		if (value == null) {
			return false; }
		if (value.getClass() == Boolean.class) {
			return true; }
		final double dbl = ByRefDouble.GET_DOUBLE(value);
		if ((dbl == 0) || (dbl == 1)) {
			return true; }
		return VectorString.STRING2BOOLEAN(value.toString()) != 0;
	}

	/** validates whether the given Value exceeds the given minimum Value	 */
	final static public boolean EQUALS(
		final double expected,
		final double actual) {
		return ByRefDouble.EQUALS(
			expected,
			actual,
			RELATIVE_ACCURACY_DEFAULT,
			ABSOLUTE_ACCURACY_DEFAULT);
	}

	/** validates whether the given Value exceeds the given minimum Value	 */
	final static public boolean EQUALS(
		final double expected,
		final double actual,
		final double rel) {
		return ByRefDouble.EQUALS(expected, actual, rel, ABSOLUTE_ACCURACY_DEFAULT);
	}

	/** validates whether the given Value is infinite in Size	 */
	final static public boolean IS_INFINITE(final Object value) {
		return Double.isInfinite(ByRefDouble.GET_DOUBLE(value));
	}

	/** validates whether the given Value is infinite in Size	 */
	final static public boolean IS_INFINITE(final double value) {
		return Double.isInfinite(value);
	}

	/** validates whether the given Value is Not a Number	 */
	final static public boolean IS_NAN(final Object value) {
		return Double.isNaN(ByRefDouble.GET_DOUBLE(value));
	}

	/** validates whether the given Value is Not a Number	 */
	final static public boolean IS_NAN(final double value) {
		return Double.isNaN(value);
	}

	/** validates whether the given Value exceeds the given minimum Value	 */
	final static public boolean IS_MIN_VALUE(
		final Object param,
		final Object value) {
		return IS_MIN_VALUE(
			ByRefDouble.GET_DOUBLE(param),
			ByRefDouble.GET_DOUBLE(value));
	}

	/** validates whether the given Value exceeds the given minimum Value	 */
	final static public boolean IS_MIN_VALUE(
		final double MinVal,
		final double dblVal) {
		if (dblVal >= MinVal) {
			return true;
		}
		return false;
	}

	/** validates whether the given Value does not exceed the given maximum Value	 */
	final static public boolean IS_MAX_VALUE(
		final Object param,
		final Object value) {
		return IS_MAX_VALUE(
			ByRefDouble.GET_DOUBLE(value),
			ByRefDouble.GET_DOUBLE(param));
	}

	/** validates whether the given Value does not exceed the given maximum Value	 */
	final static public boolean IS_MAX_VALUE(
		final double maxVal,
		final double dblVal) {
		if (dblVal <= maxVal) {
			return true;
		}
		return false;
	}

	/** validates whether the Length of the given Value does not exceed the given maximum Value	 */
	final static public boolean IS_MAX_LENGTH(
		final Object param,
		final Object value) {
		return IS_MAX_LENGTH(
			Integer.parseInt(param.toString()),
			value.toString());
	}

	/** validates whether the Length of the given Value does not exceed the given maximum Value	 */
	final static public boolean IS_MAX_LENGTH(
		final int MaxLength,
		final String strVal) {
		int Length = strVal.length();
		if (Length <= MaxLength) {
			return true;
		}
		return false;
	}

	/** validates whether the Length of the given Value exceeds the given minimum Value	 */
	final static public boolean IS_MIN_LENGTH(
		final Object param,
		final Object value) {
		return IS_MIN_LENGTH(
			Integer.parseInt(param.toString()),
			value.toString());
	}

	/** validates whether the Length of the given Value exceeds the given minimum Value	 */
	final static public boolean IS_MIN_LENGTH(
		final int MinLength,
		final String strVal) {
		int Length = strVal.length();
		if (Length >= MinLength) {
			return true;
		}
		return false;
	}

	/** validates whether the given Value String contains the given Parameter String
	 * For the Reverse Relation, just revert the two Parameters
	 */
	final static public boolean CONTAINS(
		final Object param,
		final Object container) {
		return CONTAINS(param.toString(), container.toString());
	}

	/** validates whether the given Value String contains the given Parameter String
	 * For the Reverse Relation, just revert the two Parameters
	 */
	final static public boolean CONTAINS(
		final String contained,
		final String container) {
		if (container.indexOf(contained) >= 0) {
			return true;
		}
		return false;
	}

	/** validates whether the given Value String contains the given Parameter String
	 * For the Reverse Relation, just revert the two Parameters
	 */
	final static public boolean IS_CONTAINED(
		final Object param,
		final Object container) {
		return CONTAINS((Object[]) param, container.toString());
	}

	/** validates whether the given Value String contains the given Parameter String
	 * For the Reverse Relation, just revert the two Parameters
	 */
	final static public boolean IS_CONTAINED(
		final Object[] list,
		final String value) {
		for (int i = list.length; --i >= 0;) {
			String item = list[i].toString();
			if ((item == value) || (item.equals(value))) {
				return true;
			}
		}
		return false;
	}

	/** validates whether the given Value String contains the given Parameter String
	 * For the Reverse Relation, just revert the two Parameters
	 */
	final static public boolean IS_CONTAINED(
		final Collection list,
		final String value) {
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			String item = iter.next().toString();
			if ((item == value) || (item.equals(value))) {
				return true;
			}
		}
		return false;
	}

	/** Checks whether the given Value conforms to the given Regular Expression Parameter.
	 * @return true when the given Value conforms to the given Regular Expression Parameter 	 */
	final static public boolean CONFORMS_TO_REG_EXP(
		final Object regExp,
		final Object value) {
		return CONFORMS_TO_REG_EXP(regExp.toString(), value.toString());
	}

	/** Checks whether the given Value conforms to the given Regular Expression Parameter.
	 * @return true when the given Value conforms to the given Regular Expression Parameter 	 */
	final static public boolean CONFORMS_TO_REG_EXP(
		final String regExp,
		final String strVal) {
		if (java.util.regex.Pattern.matches(regExp, strVal)) {
			return true;
		}
		return false;
	}

	/** Checks whether the given Value is null.
	 * @return true when the given Value is null 	 */
	final static public boolean IS_NULL(final Object value) {
		if (value == null) {
			return true;
		}
		return false;
	}

	/** Tests all Methods of this Class	 */
	final static public boolean IS_EMPTY(final Object value) {
		if ((value == null) || (value.toString().trim().length() <= 0)) 
			return true;
		return false;
	}

	/** Tests all Methods of this Class	 */
	final static public boolean EQUALS(
		final Object param,
		final Object value, 
		final IEquivalence eq) {
		if (eq == null)
			return EQUALS(param, value); 
		return eq.equals(param, value); 
	}

	/** Tests all Methods of this Class	 */
	final static public boolean EQUALS(
		final Object param,
		final Object value) {
		//L.n("'"+param+"'?='"+value+"'"); 
		if (param == value) 
			return true;
		if (param == null) 
			return false; //value == null; //since null == null is always true!
		return param.equals(value);
	}

	/** Tests all Methods of this Class	 */
	final static public boolean IS_SAME(
		final Object param,
		final Object value) {
		if (param == value) 
			return true;
		return false;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////
	/// #region : static Validation Rules and their Names...
	///////////////////////////////////////////////////////////////////////////////////////////////////

	/** Name of the static Validation Method {@link #validateMinDate} */
	final static public String VALIDATE_MIN_DATE = "validateMinDate";

	/** Diese Methode validiert ob der �bergebene Wert nicht �ber einem maximalen Datum liegt.
	 * @param minDate Parameter f�r das minimale Datum.
	 *        kann entweder absolut sein in der Notation YYYY-MM-DD
	 *        oder relativ in der Form +/-nnn[Y,M,D]
	 * @param wert zu validierender Wert
	 * @throws ValidationException if the Value 'wert' was invalid
	 */
	public static void validateMinDate(
		final String minDate_,
		final Object wert,
		final int severity)
		throws InvalidException {
		String strWert = minDate_;
		try {
			Date minDate = PARSE_DATE_OR_TIME(minDate_);
			// check das kein Null kommt
			if (wert == null) {
				return;
			}
			strWert = wert.toString();
			if (strWert.length() == 0) {
				return;
			}
			Date timeStamp = PARSE_DATE_OR_TIME(strWert);
			//			if (logger.isDebugEnabled())
			//				logger.debug("TIMESTAMP PARSED:" + timeStamp);
			if (timeStamp.getTime() < minDate.getTime()) {
				throw new InvalidException(
					minDate_,
					wert,
					"Date '"
						+ timeStamp
						+ "' must be larger than "
						+ minDate
						+ " but is actually "
						+ strWert,
					severity);
			}
		} catch (java.text.ParseException x) {
			throw new InvalidException(
				minDate_,
				wert,
				strWert + " led to the following Exception:" + x.getMessage(),
				severity);
		}
	}

	/** Name of the static Validation Method {@link #validateMaxDate} */
	final static public String VALIDATE_MAX_DATE = "validateMaxDate";

	/** Diese Methode validiert ob der �bergebene Wert nicht �ber einem maximalen Datum liegt.
	 * @param maxDate Parameter f�r das maximale Datum.
	 *        kann entweder absolut sein in der Notation YYYY-MM-DD
	 *        oder relativ in der Form +/-nnn[Y,M,D]
	 * @param wert zu validierender Wert
	 * @throws ValidationException if the Value 'wert' was invalid
	 */
	public static void validateMaxDate(
		final String maxDate_,
		final Object wert,
		final int severity)
		throws InvalidException {
		String strWert = maxDate_;
		try {
			Date maxDate = PARSE_DATE_OR_TIME(maxDate_);
			// check das kein Null kommt
			if (wert == null) {
				return;
			}
			strWert = wert.toString();
			if (strWert.length() == 0) {
				return;
			}
			Date timeStamp = PARSE_DATE_OR_TIME(strWert);
			//			if (logger.isDebugEnabled())
			//				logger.debug("TIMESTAMP PARSED:" + timeStamp);
			if (timeStamp.getTime() < maxDate.getTime()) {
				throw new InvalidException(
					maxDate_,
					wert,
					"Date '"
						+ timeStamp
						+ "' must be smaller than "
						+ maxDate
						+ " but is actually "
						+ wert,
					severity);
			}
		} catch (java.text.ParseException x) {
			throw new InvalidException(
				maxDate_,
				wert,
				strWert + " led to the following Exception:" + x.getMessage(),
				severity);
		}
	}

	/** Name of the corresponding Validation Method */
	final static public String VALIDATE_BOOLEAN = "validateBoolean";

	/** Diese Methode validiert ein Feld, das nur 0 oder 1 beinhalten darf
	 * @param Param Beinhaltet 2 oder null, 2 bedeutet das 2 als tri-boolische
	 * Wert erlaubt ist
	 * @param wert zu validierender Wert
	 * @throws ValidationException if the Value 'wert' was invalid
	 */
	public static void validateBoolean(
		final Object ignored,
		final Object wert,
		final int severity)
		throws InvalidException {
		// check das kein Null kommt
		if (wert == null) {
			return;
		}
		String strWert = wert.toString();
		if (strWert.length() == 0) {
			return;
		}
		// if the value is != 0, 1 or '' then error
		if (((!strWert.equals("0")) && (!strWert.equals("1")))
			&& (!strWert.equals(""))) {
			throw new InvalidException(
				ignored,
				wert,
				"Value " + wert + " is not a boolean or empty Field (0,1,)  ",
				severity);
		}
	}

	/** Name of the static Validation Method to test whether the given Value can be divided by the given Module	 */
	final static public String VALIDATE_MODULE = "validateModule";

	/** validates whether the given Value can be divided by the given Module	 */
	final static public void validateModule(
		final Object param,
		final Object value,
		final int severity)
		throws InvalidException {
		if (!IS_MODULE(param, value)) {
			throw new InvalidException(
				param,
				value,
				"Value " + value + " is not an integer Multiple of  " + param,
				severity);
		}
	}

	/** Name of the static Validation Method to test whether the given Value exceeds the given minimum Value	 */
	final static public String VALIDATE_MIN_VALUE = "validateMinValue";

	/** validates whether the given Value exceeds the given minimum Value	 */
	final static public void validateMinValue(
		final Object param,
		final Object value,
		final int severity)
		throws InvalidException {
		if (!IS_MIN_VALUE(param, value)) {
			throw new InvalidException(
				param,
				value,
				"Minimum Value should be: '"
					+ param
					+ "' but actually was '"
					+ value
					+ "'!",
				severity);
		}
	}

	/** Name of the static Validation Method to test whether the given Value exceeds the given maximum Value	 */
	final static public String VALIDATE_MAX_VALUE = "validateMaxValue";

	/** validates whether the given Value does not exceed the given maximum Value	 */
	final static public void validateMaxValue(
		final Object param,
		final Object value,
		final int severity)
		throws InvalidException {
		if (!IS_MAX_VALUE(param, value)) {
			throw new InvalidException(
				param,
				value,
				"Maximum Value should be: '"
					+ param
					+ "' but actually was '"
					+ value
					+ "'!",
				severity);
		}
	}

	/** Name of the static Validation Method to test whether the Length of the given Value does not exceed the given maximum Value	 */
	final static public String VALIDATE_MAX_LENGTH = "validateMaxLength";

	/** validates whether the Length of the given Value does not exceed the given maximum Value	 */
	final static public void validateMaxLength(
		final Object param,
		final Object value,
		final int severity)
		throws InvalidException {
		if (!IS_MAX_LENGTH(param, value)) {
			throw new InvalidException(
				param,
				value,
				"Maximum Length: '"
					+ param
					+ "' but actually was '"
					+ value
					+ "' with a Length of '"
					+ value.toString().length()
					+ "'!",
				severity);
		}
	}

	/** Name of the static Validation Method to test whether the Length of the given Value exceeds the given minimum Value	 */
	final static public String VALIDATE_MIN_LENGTH = "validateMinLength";

	/** validates whether the Length of the given Value exceeds the given minimum Value	 */
	final static public void validateMinLength(
		final Object param,
		final Object value,
		final int severity)
		throws InvalidException {
		if (!IS_MAX_LENGTH(param, value)) {
			throw new InvalidException(
				param,
				value,
				"Minimum Length: '"
					+ param
					+ "' but actually was '"
					+ value
					+ "' with a Length of '"
					+ value.toString().length()
					+ "'!",
				severity);
		}
	}

	/** Name of the static Validation Method to test whether the given Value contains the given Value	 */
	final static public String VALIDATE_CONTAINS = "validateContains";

	/** validates whether the given Value contains the given Parameter Value	 */
	final static public void validateContains(
		final Object param,
		final Object value,
		final int severity)
		throws InvalidException {
		if (!CONTAINS(param, value)) {
			throw new InvalidException(
				param,
				value,
				"'" + value + "' was supposed to contain '" + param + "'!",
				severity);
		}
	}

	/** Name of the static Validation Method {@link #validateIsContained} */
	final static public String VALIDATE_IS_CONTAINED = "validateIsContained";

	/** validates whether the given Value is contained in the given Parameter Value	 */
	final static public void validateIsContained(
		final Object param,
		final Object value,
		final int severity)
		throws InvalidException {
		if (!CONTAINS(param, value)) {
			throw new InvalidException(
				param,
				value,
				"'"
					+ value
					+ "' was supposed to be contained in '"
					+ param
					+ "'!",
				severity);
		}
	}

	/** Name of the static Validation Method {@link #validateRegExp} */
	final static public String VALIDATE_REG_EXP = "validateRegExp";

	/** Validates that the given Value conforms to the given Regular Expression Parameter.
	 * @throws InvalidException when the Value doesn't conform to the given Regular Expression Parameter	 */
	final static public void validateRegExp(
		final Object param,
		final Object value,
		final int severity)
		throws InvalidException {
		if (!CONFORMS_TO_REG_EXP(param, value)) {
			throw new InvalidException(
				param,
				value,
				"'"
					+ value
					+ "' was supposed to match the Regular Expression '"
					+ param
					+ "'!",
				severity);
		}
	}

	/** Name of the static Validation Method {@link #validateNotNull} */
	final static public String VALIDATE_NOT_NULL = "validateNotNull";

	/** The inverse Test whether the Value is 'null' is only rarely used!
	 * @throws InvalidException when the Value is 'null'
	 */
	final static public void validateNotNull(
		final Object param,
		final Object value,
		final int severity)
		throws InvalidException {
		if (IS_NULL(value)) {
			throw new InvalidException(
				param,
				value,
				"Expected not to be null, but actually was '" + value + "'!",
				severity);
		}
	}

	/** Name of the static Validation Method {@link #validateNotEmpty} */
	final static public String VALIDATE_NOT_EMPTY = "validateNotEmpty";

	/** The inverse Test whether the Value is empty is only rarely used!
	 * @throws InvalidException when the Value is 'null' or ""
	 */
	final static public void validateNotEmpty(
		final Object param,
		final Object value,
		final int severity)
		throws InvalidException {
		if (IS_EMPTY(value)) {
			throw new InvalidException(
				param,
				value,
				"Expected not to be empty, but actually was '" + value + "'!",
				severity);
		}
	}

	/** Name of the static Validation Method {@link #validateIfEquals} */
	final static public String VALIDATE_IF_EQUALS = "validateIfEquals";

	/** Validates the Value against the Parameter using the Parameter's own .equals() Method,
	  * making this the most versatile of the Validation Methods.
	 * @throws InvalidException when the Value does not match the Parameter
	 */
	final static public void validateIfEquals(
		final Object param,
		final Object value,
		final int severity)
		throws InvalidException {
		if (!EQUALS(param, value)) {
			throw new InvalidException(
				param,
				value,
				"Expected: '" + param + "' but actually was '" + value + "'!",
				severity);
		}
	}

	/** Name of the static Validation Method {@link #validateIfSame} */
	final static public String VALIDATE_IF_SAME = "validateIfSame";

	/** Validates that the Value is identical (==) to the Parameter, rather than merely equal.
	 * @throws InvalidException when the Value is not identical to the Parameter	 */
	final static public void validateIfSame(
		final Object param,
		final Object value,
		final int severity)
		throws InvalidException {
		if (!IS_SAME(param, value)) {
			throw new InvalidException(
				param,
				value,
				"Expected: '"
					+ param
					+ "' but actually was '"
					+ value
					+ "' and should be identical!",
				severity);
		}
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////

	/** Constant Set of Classes used for finding the Methods  */
	private static final Class[] ParamTypes = { Object.class, Object.class };

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////

	/** Reference to the Class with the Validation Method,
	 *  implicit in the Method
	 */
	//	protected Class type;

	/** Reference to the Class with the Validation Method
	 *  not necessary with static Methods
	 */
	//	protected Object instance;

	/** Reference to the Validation Method:	 */
	protected final Method method;

	/** Reference to the Object tested with the Validation Method:	 */
	protected final Object value;

	/** holds The Value of the Severity that was invalidated   */
	protected final Integer severity;

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor well usable for reading Parameters from a File
	  * By directly creating the Instance and directly validating the Method
	  * some Checks can be performed already early on creating the Rule.
	  * @param class_  the Class containing the Validation Method
	  * @param value_  the second Validation Method Parameter
	  * The Class must implement the IValidationRule Interface.
	  */
	public ValidationRule(
		final ResultSet rs,
		final int classCol,
		final int methodCol,
		final int paramCol)
		throws ClassNotFoundException, NoSuchMethodException, SQLException {
		this(
			rs.getString(classCol),
			rs.getString(methodCol),
			rs.getString(paramCol));
	}

	/** Initializing Constructor well usable for reading Parameters from a File
	  * By creating the Instance and validating the Method
	  * some Checks can be performed already early on creating the Rule.
	  * @param class_  the Class containing the Validation Method
	  * @param method_ the actual Validation Method
	  * @param value_  the second Validation Method Parameter
	  */
	public ValidationRule(
		final String class_,
		final String method_,
		final Object value_)
		throws ClassNotFoundException, NoSuchMethodException {
		this(class_, method_, value_, 0);
	}

	/** Initializing Constructor well usable for reading Parameters from a File
	  * By creating the Instance and validating the Method
	  * some Checks can be performed already early on creating the Rule.
	  * @param class_  the Class containing the Validation Method
	  * @param method_ the actual Validation Method
	  * @param value_  the second Validation Method Parameter
	  */
	public ValidationRule(
		final String class_,
		final String method_,
		final Object value_,
		final int severity_)
		throws ClassNotFoundException, NoSuchMethodException {
		Class cls = Class.forName(class_);
		this.value = value_;
		this.severity = new Integer(severity_);
		//		this.instance = cls.newInstance(); //needs an empty Constructor, but only for non-static Methods!
		this.method = cls.getMethod(method_, ParamTypes);
		if (!Modifier.isStatic(method.getModifiers())) { //only static Methods!
			throw new NoSuchMethodException("Method is not static!");
		}
		Class[] exceptions = method.getExceptionTypes();
		if ((exceptions.length != 1)
			|| (exceptions[0] != InvalidException.class)) {
			throw new NoSuchMethodException("Method does not solely throw InvalidException");
		}
		//call the Method for Testing Purposes
		try {
			validate(null);
		} catch (InvalidException x) {
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IValidator: Implementation
	////////////////////////////////////////////////////////////////////////////////

	/** Validates only the new Value, ignoring the Source and old Value Parameters.
	 * @see synch.IValidator#validate(Object, Object, Object)	 */
	public void validate(Object Source, Object Value, Object oldVal)
		throws InvalidException {
		validate(Value);
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IValidationRule : Implementation
	////////////////////////////////////////////////////////////////////////////////

	/** Validates the given Object */
	public void validate(final Object arg) throws InvalidException {
		try {
			method.invoke(null, new Object[] { value, arg, severity });
			//static Methods don't require Instances!
		} catch (IllegalAccessException x) { //was already checked in the Constructor by only finding public Methods!
			throw new streamIO.exception.BaseException(x);
		} catch (InvocationTargetException x) {
			Throwable t = x.getTargetException();
			if (t instanceof InvalidException) {
				throw (InvalidException) t;
			} //rethrowing retains the Stack
			throw new InvalidError(t);
			//to retain the Stack it is wrapped in a BaseException.
			//			throw (RuntimeException) t; //any other Exception is a Runtime Exception or an Error
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////

	/** any Dummy Parameter for internal Test */
	private static final String TEST_PARAM = "Hallo";

	/** Tests all Methods of this Class	 */
	final static public ValidationRule getRule() {
		try {
			return new ValidationRule(
				ValidationRule.class.getName(),
				VALIDATE_IF_EQUALS,
				TEST_PARAM);
		} catch (Exception x) {
			x.printStackTrace(System.err);
		}
		return null;
	}

	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) throws InvalidException {
		System.out.println("Testing " + ValidationRule.class.getName());
		ValidationRule rule = getRule();
		rule.validate(TEST_PARAM);
		try {
			rule.validate("ErrorValue");
			streamIO.Assert.FAIL("InvalidException expected!");
		} catch (InvalidException x) {
		}
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) throws InvalidException {
		testIt(args);
	}

}