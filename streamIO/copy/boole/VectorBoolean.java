package streamIO.copy.boole;

import streamIO.Assert;
import streamIO.Log;
import streamIO.copy.ICopyAble;

/**
  * Title: VectorBoolean<p>
  * Description:
  * 'null' is defined as being 'false' in all Dimensions.
  *
  * Design Decisions:
  * Since Java is a 32 Bit Language and int is the Default Type,
  * the Elements of the Array are chosen to be of Type int.
  *
  * Known SubClasses: <none>
  * @see java.util.BitVector for a Class that doesn't implement Boole
  * but has the same features as this Class.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-13-2002, 05:20 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class VectorBoolean
extends ABoole {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Log L = new Log(VectorBoolean.class); 
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** The Flags for the Vector Operations.	 */
	protected static final int OP_OR   = 0;
	protected static final int OP_XOR  = 1;
	protected static final int OP_AND  = 2;
	protected static final int OP_DIFF = 3;

	/** The Shift necessary to navigate to the correct int: 32 = 1 << 5.	 */
	protected static final int SHIFT = 5;

	/** The Modulus / Mask necessary to calculate the Bit in the int.	 */
	protected static final int MODULUS = (1 << SHIFT)-1; //31

	/** Default Value for the Number of Bits in the empty Constructor */
	public static int NumBitsDefault = MODULUS;
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return the Byte for the given Index 	*/
	protected static int byt(int i) { return i >> SHIFT; }

	/** @return the Bit  for the given Index 	*/
	protected static int bit(int i) { return 1 << (i & MODULUS); }

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Vector containing the Bits.	 */
	protected int[] values;
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return true, if the Bit at the given Index is set. 	 */
	public boolean isSet(final int i) {
		final int byt = i >> SHIFT;
		if (byt >= values.length) 
			return false; 
		final int bit = 1 << (i & MODULUS);
		return (values[byt] & bit) != 0; } //query the Bit

	/** Sets the given Bit to the given Value 	 */
	public void set(final int i) { set(i, true); }

	/** Sets the given Bit to the given Value 	 */
	public void set(final int i, final boolean value) {
		int byt = i >> SHIFT;
		int bit = 1 << (i & MODULUS);
		if (byt >= values.length) {
			if (!value) 
				return; 
			final int[] tmp = new int[byt+1];
			System.arraycopy(values, 0, tmp, 0, values.length);
			values = tmp; }
		if (value) 
			values[byt] |=  bit; //  set the Bit
		 else 
			values[byt] &= ~bit; //unset the Bit
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor
	  * Defaulting the Number of Bits.	 */
	public VectorBoolean() { this (NumBitsDefault); }

	/** Constructor
	  * @param NumBits Number of Bits to be represented at least to save Resizing	 */
	public VectorBoolean (int NumBits) {
		this.values = new int[1 + (NumBits >> SHIFT)]; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return a String Representation of this Object    */
	public String toString() {
		final StringBuffer buf = new StringBuffer(values.length << (SHIFT+3));
		buf.append("(");
		int Mask = 0;
//		int bit  = 1 << SHIFT;
		int i = values.length;
		int val = 0;
		int v = values.length << SHIFT;
		while (--v >= 0) {
			if((Mask >>>= 1) == 0) {
				Mask = 1 << MODULUS;
				while (0 == (val = values[--i])) {
					if (0 > (v -= (1 << SHIFT))) {
						break; } } }
			if ((Mask & val) != 0) {
				buf.append(v).append(", "); }
		}
		int len;
		if (1 < (len = buf.length())) {
	 		buf.setLength(len-2); }
		buf.append(")");
		return buf.toString(); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface Boole: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return a new Instance of this Class	*/
	public ICopyAble newInstance() {
		return new VectorBoolean(values.length); }

	/** @return a new Instance of this Class	*/
	public ICopyAble copyAt(Object arg) {
		VectorBoolean arg_ = (VectorBoolean) arg;
		if (arg_.values.length > values.length) {
			values = new int[arg_.values.length]; //make it at least as large...
		} else if (arg_.values.length < values.length) {
			java.util.Arrays.fill(values, arg_.values.length, values.length, 0); //if larger, clear the Rest
		}
		System.arraycopy(arg_.values, 0, values, 0, arg_.values.length);
		return new VectorBoolean(values.length); }

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Boole: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Increases the capacity of this Array, if necessary, to ensure
	  * that it can hold at least the number of components specified by
	  * the minimum capacity argument.
	  *
	  * @param   minCapacity   the desired minimum Capacity.
	  * @return  the actual Capacity allocated for this Container */
	int setCapacity(int minCapacity) {
		int cap = 1 + (minCapacity >> SHIFT);
		if (cap > values.length) {
			int[] tmp = new int[cap];
			System.arraycopy(values, 0, tmp, 0, values.length);
			values = tmp; }
		return values.length << SHIFT; }

	/** Returns the current minimum capacity of this Array.
	  *
	  * @return  the current capacity of this Array.	 */
	int getCapacity() {
		return values.length << SHIFT; }

	/** @return the maximum Bit set 	*/
	public int size() {
		int i = values.length;
		while (--i >= 0) {
			if (values[i] == 0) {
				continue; }
			int bit  = 1 << SHIFT;
			int Mask = 1 << (--bit);
			int val = values[i];
			do {
				if ((Mask & val) != 0) {
					return bit + (i << SHIFT) ; }
				Mask >>>= 1;
			} while (--bit >= 0);
		}
		return -1; }

	/** Boolean Constant for the Representation of 'false': =0
	  * @return false
	  * Sets this Object to False, i.e. not 'true';
	  * with Vectors it sets all Elements to their respective Value of False*/
	public Boole FalseAt() {
		java.util.Arrays.fill(values, 0);
/*		int i = Values.length;
		while (--i >= 0) {
			Values[i] = 0; }
*/		return this; }

	/** Boolean Constant for the Representation of 'true': =1
	  * @return true
	  * Sets this Object to True, i.e. not 'false';
	  * with Vectors it sets all Elements to their respective Value of True*/
	public Boole TrueAt() {
		java.util.Arrays.fill(values, MODULUS-1);
/*		int i = Values.length;
		while (--i >= 0) {
			Values[i] = 0; }
*/		return this; }

	/** Boolean NOT Operation in Place: ~=, != for single Bit
	  * @return !a
	  * NOT a = true <=> (a = false)
	  * This Operation cannot be implemented by infinite Sets,
	  * Therefore you need other means to define some Operations.	 */
	public Boole NOTat	() {
		int i = values.length;
		while (--i >= 0) {
			values[i] = ~values[i]; }
		return this; }

	/** AND Operation in Place: &=, &&= for single Bit
	  * @return a & b
	  * a AND b = true <=> (a = true) AND (b = true) 	 */
	protected void OPat	(Object arg, int op) {
		VectorBoolean arg_ = (VectorBoolean) arg;
		int i = values.length;
		if ((arg_.values.length > values.length) && (op != OP_AND)) {
			int[] tmp = new int[arg_.values.length];
			System.arraycopy(values, 0, tmp, 0, values.length);
			values = tmp; }
		int min =      values.length;
		int max = arg_.values.length;
		if (min > arg_.values.length) {
			min = arg_.values.length;
			max =      values.length; }
		while (--max >= i) { //when this was shorter
			switch(op) {
				case OP_OR  :
				case OP_XOR : values[i] =  arg_.values[i]; continue;
				case OP_DIFF: values[i] = ~arg_.values[i]; continue;
				case OP_AND : continue;
				default: break;
			}
		}
		while (--i >= min) { //when this was longer
			switch(op) {
				case OP_XOR :
				case OP_OR  : continue;
				case OP_AND : values[i]  = 0; break;
				case OP_DIFF: continue;
				default: break;
			}
		}
		++i;
		while (--i >= 0) { //combine both Vectors
			switch(op) {
				case OP_OR  : values[i] |=  arg_.values[i]; continue;
				case OP_XOR : values[i] ^=  arg_.values[i]; continue;
				case OP_AND : values[i] &=  arg_.values[i]; continue;
				case OP_DIFF: values[i] &= ~arg_.values[i]; continue;
				default: break;
			}
		}
	}

	/** AND Operation in Place: &=, &&= for single Bit
	  * @return a & b
	  * a AND b = true <=> (a = true) AND (b = true) 	 */
	public Lattice ANDat	(Object arg) {
		OPat(arg, OP_AND);
		return this; }

	/** OR Operation in Place: |=, ||= for single Bit
	  * @return a | b
	  * a OR b = true <=> (a = true) OR (b = true) 	 */
	public Lattice ORat	(Object arg) {
		OPat(arg, OP_OR);
		return this; }

	/** OR Operation in Place: |=, ||= for single Bit
	  * @return a | b
	  * a OR b = true <=> (a = true) OR (b = true) 	 */
	public Lattice XORat	(Object arg) {
		OPat(arg, OP_XOR);
		return this; }

	/** Boolean DIFF Operation in Place: -=
	  * @return a - b
	  * a - b <=> (a AND NOT b) <=> NOT IMP
	  * For Sets:	Difference Set ; can also be defined without NOT!  */
	public Lattice DIFFat (Object arg) {
		OPat(arg, OP_DIFF);
		return this; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		L.n("Testing " + VectorBoolean.class.getName());
		final VectorBoolean v1 = new VectorBoolean();
		final VectorBoolean v2 = new VectorBoolean();
		final int b1 = 47;
		final int b2 = 88;
		v1.set(b1);
		v2.set(b2);
		v2.set(188, false);
		for(int i = 10; --i >= 0;)
			Assert.IS_TRUE(!v1.isSet(i)); 
		for(int i = 1000; --i >= 990;)
			Assert.IS_TRUE(!v1.isSet(i)); 
		Assert.IS_TRUE(v1.isSet(b1)); L.l(v1.size());
		Assert.IS_TRUE(v2.isSet(b2)); L.l(v2.size());
		L.l(v1.getCapacity());
		L.l(v2.getCapacity());
		Assert.EQUALS(b1, v1.size());
		Assert.EQUALS(b2, v2.size());
		v1. ORat(v2); 
		Assert.IS_TRUE(v1.isSet(b1)); L.l(v1.size());
		Assert.IS_TRUE(v1.isSet(b2)); L.l(v2.size());
		v1.ANDat(v2); 
		Assert.IS_TRUE(!v1.isSet(b1)); L.l(v1.size());
		Assert.IS_TRUE(v1.isSet(b2)); L.l(v2.size());
		v1.XORat(v2); 
		Assert.IS_TRUE(!v1.isSet(b1)); L.l(v1.size());
		Assert.IS_TRUE(!v1.isSet(b2)); L.l(v2.size());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

