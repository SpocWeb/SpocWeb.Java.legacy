package function;

/**
  * Abstract base class for {@link ICountAble} implementations, providing static helpers that
  * narrow a {@code double} into an exact {@code byte}/{@code short}/{@code int}/{@code long}.
  *
  * Title: ACountAble<p>
  * Description:
  * Purpose:
  * Abstract Base Class for ICountAble Implementations
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
  * Created on	06-29-2002, 06:26 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:31:14Z
  * digest: 685f865e2eaee425976713b2f24d5b5f278fdac69aa8e6bea55e495d377da455
  * stale: false
  * tags: [code/function_contract, code/function_composition]
  * concepts: [Function/Relation Contract]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public abstract class ACountAble
extends AOrderAble
implements ICountAble {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns the Object Value represented by an 8 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range
	  * @throw IllegalArgumentException when the Value is not an Integer!
	  */
	final static public byte getByte(final double Value)
	throws IllegalArgumentException {
		final byte Val  = (byte) Value;
		if  (Val ==        Value) {
			return Val; }
		throw new IllegalArgumentException("Value '" + Value + "' does not fit the Range 'byte'"); }

	/** Returns the Object Value represented by a 16 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	final static public short getShort(final double Value)
	throws IllegalArgumentException {
		final short ret = (short) Value;
//		if  (Math.abs(Val - Value) > Value*DoubleAccuracy) throw new IllegalArgumentException();
		if  (ret ==         Value) {
			return ret; }
		throw new IllegalArgumentException("Value '" + Value + "' does not fit the Range 'short'"); }

	/** Returns the Object Value represented by a 32 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	final static public int	 getInt(final double Value)
	throws IllegalArgumentException {
		final int Val  = (int) Value;
		if (Val ==       Value) {
			return Val; }
		throw new IllegalArgumentException("Value '" + Value + "' does not fit the Range 'int'"); }

	/** Returns the Object Value represented by a 64 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	final static public long  getLong(final double Value)
	throws IllegalArgumentException {
		final long ret  = (long) Value;
		if  (ret ==        Value) {
			return ret; }
		throw new IllegalArgumentException("Value '" + Value + "' does not fit the Range 'long'"); }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor	 */
	protected ACountAble() { }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + ACountAble.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		testIt(args); }

}

