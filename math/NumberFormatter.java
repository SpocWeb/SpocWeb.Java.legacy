package math;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Formats numbers into fixed-width, fixed-point decimal Strings with a configurable Digit
 * count before and after the decimal separator, and streams them directly to a Writer or
 * OutputStream without allocating intermediate Strings.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:48:41Z
 * digest: dec88050ddd604b0075dbc511e7ca5ef89859ae6bbbd1df2eb0e2ef678b330ac
 * stale: false
 * tags: [code/formatting]
 * concepts: [Number Formatter]
 * facets: {layer: utility, status: broken, complexity: low}
 * -->
 */
public class NumberFormatter
implements IFormatter {

	/** Function to check whether the String contains a Number */
	final static public boolean isNumber(String val) {
		if (val == null) { return false; }
		final String trimmed = val.trim();
		if (trimmed.isEmpty()) { return false; }
		try {
			Double.parseDouble(trimmed);
			return true;
		} catch (final NumberFormatException e) {
			return false;
		} }

	/** Number of Digits before the Comma */
	final static public byte DIGITS_BEFORE_DEFAULT = 3;

	/** Number of Digits before the Comma */
	final static public byte DIGITS_AFTER_DEFAULT  = 4;

	/** Number of Digits before the Comma */
	private final byte digitsBefore;

	/** Number of Digits before the Comma */
	private final byte digitsAfter;

	/** Number of Digits before the Comma */
	private final byte digitsAll;

	/** Number of Digits before the Comma */
	private final long factorAfter;

	/** Decimal separator inserted between the Integer and Fraction Part when streaming. */
	final public String separator=".";

	/** StringBuffer to save String Operations	 */
	private StringBuffer sb = new StringBuffer(20);

	/**
	 * streams out the given Number to the given Stream 
	 * without creating intermediary Strings. 
	 * @param stream
	 * @param d
	 */
	public void stream(final OutputStream stream, final double d) throws IOException {
		final Writer pw = new OutputStreamWriter(stream);
		stream(pw, d); 
		pw.flush();
	}

	/**
	 * streams out the given Number to the given Stream 
	 * without creating intermediary Strings. 
	 * @param stream
	 * @param d
	 */
	public void stream(final Writer stream, final double d) throws IOException {
		final int len = prepareStream(stream, 5+(long)(d*factorAfter))-1; //+5 and -1 for addtl. rounding Digit
		//print the Prefix
		for (int i = digitsAll - len; --i >= 0;) {
			stream.write('0');
		}
		int pos = -1;
		//print the Integer Part
		for (int i = len - digitsAfter; --i >= 0;) {
			stream.write(sb.charAt(++pos));
		}
		stream.write(separator);
		//print the Fraction Part
		for (; ++pos < len;) {
			stream.write(sb.charAt(pos));
		}
		sb.setLength(0);
	}

	/**
	 * Method shared by all Stream Methods: 
	 * makes the Number positive and adds it to the StringBuffer. 
	 * @param stream the Stream to write to
	 * @param allDigits the Number to stream out 
	 * @return the Length of the Number in the StringBuffer
	 */
	private int prepareStream(final Writer stream, long allDigits) throws IOException{
		if (allDigits < 0) {
			allDigits = -allDigits;
			stream.write('-');
		} else {
			stream.write('+');
		}
		sb.append(allDigits);
		return sb.length(); 
	}

	/**
	 * streams out the given Number to the given Stream 
	 * without creating intermediary Strings. 
	 * @param stream
	 * @param d
	 */
	public void stream(final Writer stream, long allDigits) throws IOException {
		final int len = prepareStream(stream, allDigits); //because of addtl. rounding Digit
		//print the Prefix
		for (int i = digitsAll - len; --i >= 0;) {
			stream.write('0');
		}
		//print the Number
		for (int pos = -1; ++pos < len;) {
			stream.write(sb.charAt(pos));
		}
	}

	/** Constructs a formatter using {@link #DIGITS_BEFORE_DEFAULT} and {@link #DIGITS_AFTER_DEFAULT}. */
	public NumberFormatter() {
		this(DIGITS_BEFORE_DEFAULT, DIGITS_AFTER_DEFAULT);
	}

	/** Constructs a formatter using the given Digit counts before and after the separator. */
	public NumberFormatter(final int digitsBefore_, final int digitsAfter_) {
		this.digitsBefore = (byte) digitsBefore_;
		this.digitsAfter = (byte) digitsAfter_;
		digitsAll = (byte) (digitsBefore+digitsAfter); 
		factorAfter = (long) Math.pow(10, digitsAfter+1); //for Rounding
	}

	/**
	 * Formats the given Object using its own {@code toString()}, ignoring this Formatter's
	 * configured Digit counts.
	 *
	 * @return the Object formatted by the Default Format of this Formatter
	 */
	public String format(Object arg) {
		return arg.toString(); }

	/**
	 * Parses the given Object as a decimal number scaled down by 1000 and left-pads it to
	 * {@link #digitsBefore} width with a leading sign; falls back to the plain
	 * {@code toString()} when the Object is not a parseable number.
	 *
	 * @return the Object formatted by the given Format
	 */
	public String format(final Object arg, final String Format) {
		String str = arg.toString();
		try {
			double val = Double.parseDouble(str);
			str = Long.toString(Math.round(val/1000));
			if (val > 0) {
				str = "+" + str; }
			str = "          ".substring(0, digitsBefore-str.length()) + str; 
			return str;
		} catch (NumberFormatException x) { //too expensive for regular use!
		}
		return str; }

	/** Demonstrates streaming the value of Pi three times to standard output. */
	final static public void main(final String[] args) throws IOException {
		NumberFormatter formatter = new NumberFormatter();
		PrintWriter pw = new PrintWriter(System.out); 
		formatter.stream(pw, Math.PI); pw.println();
		formatter.stream(pw, Math.PI); pw.println();
		formatter.stream(pw, Math.PI); pw.println();
		pw.close();
	}

}
