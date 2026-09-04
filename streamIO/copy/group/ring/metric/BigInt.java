/*
 * File Name: BigInt.java
 * Created on: 27.11.2003
 *
 */
package streamIO.copy.group.ring.metric;

import java.awt.Color;
import java.awt.SystemColor;
import java.io.Console;

import math.vector.VectorInt;
import streamIO.Assert;
import streamIO.Log;
import streamIO.copy.ICopyAble;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ISemiGroup;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.groupM.IGroupM;
import streamIO.copy.groupM.ISemiGroupM;
import function.ICountAble;

/**
 * Title: BigInt<p>
 * Description:
 * Purpose:
 * Concrete Implementation of a dynamic Size arbitrary Precision Number. 
 * Can also be used to convert any Number from one Representation into any other Representation. 
 * 
 * Implementation Details: 
 * It shows that the Separation of Sign and absolute Value make Calculations considerably easier! 
 * Rollover and 1-Complement are useful for fixed Size Numbers, as long as no Rollover happens! 
 * But for dynamic Size Numbers any negative Number tends to roll up to the highest Index.
 * And the Complications of using Complements as Dividend or Divisor are immense!   
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Similar Classes: 
 * @see java.math.BigInteger which also uses O(N^2) Algorithms for Multiplication and Division, 
 * but on a fixed Radix System. 
 * @see Numerical Recipes demonstrate a O(N*logN) Algorithms for these 
 * using Fourier Transformation.  
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class BigInt extends AMetricIRing 
implements ICountAble{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(1);

	/////////////////////////////////////////////////////////////////////////////////////

	/** @see streamIO.copy.group.ring.metric.IIWellOrder#maxValueAt()	 */
	public IWellOrder maxValueAt() {
		throw new RuntimeException("No maximum Value defined for "+BigInt.class);
	}

	/** @see streamIO.copy.group.ring.metric.IMetricIRing#FloorAt()	 */
	public IMetricIRing FloorAt() { return this; }

	/** @see streamIO.copy.group.ring.IIntRing#CmplAt()	 */
	public IIntRing CmplAt() { return this; }

	/** @see streamIO.copy.group.ring.IComplex#cjgAt()	 */
	public IIntRing cjgAt() { return this; }

	/** @see streamIO.copy.group.ring.IComplex#isComplex()	 */
	public boolean isComplex() { return false; }

	/** @see streamIO.copy.group.ring.IIntRing#addCarry()	 */
	public void addCarry() {}

	/** @see streamIO.copy.group.ring.IIntRing#toUpperAt()	 */
	public IIntRing toUpperAt() {
		return this;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////

	/** Elements of this Number, all positive! */
	protected int[] items = new int[1];
	
	/** Number of valid Items in this Number */
	protected int numItems; //= 0; 

	/** module to trim all Items in this Number */
	protected int module; 

	/** Sign of this Number */
	protected boolean negative; 

	/////////////////////////////////////////////////////////////////////////////////////
	/// Constructors
	/////////////////////////////////////////////////////////////////////////////////////

	/** @param module_ the Module to use	 */
	public BigInt(final int module_) {
		this.module = module_;
	}

	/** @param module_ the Module to use	 */
	public BigInt(final int module_, int value) {
		if (value < 0) {
			this.negative = true; 
			value = -value; }
		this.module = module_;
		if (value == 0) {
			return; }
		this.items[0] = value;
		int i = numItems = 1; 
		while(0 != (value /= module)) { 
			++i; }
		setNumItems(i);
		VectorInt.TRIM_AT(items, module, numItems);
	}

	/** @param module_ the Module to use	 */
	public BigInt(final int module_, final BigInt value) {
		this.module = module_; 
		if (module == value.module) {
			copyAt(value);
		} else { //Radix Conversion using the Horner Schema
			ensureCapacity(1+(value.module*value.numItems)/module);
			for (int i = value.numItems; --i >= 0;){
				mulAt(value.module);
				addAt(value.items[i]); 
			}
		}
	}

	/** @see streamIO.copy.group.IISemiGroup#addAt(java.lang.Object)	 */
	public ISemiGroup addAt(final Object arg) {
		return addAt((BigInt) arg);
	}

	/**
	 * ensures that the Array has at least this Capacity!
	 * @param maxItems
	 */
	private void setNumItems(final int numItems_) {
		ensureCapacity(numItems_); 
		this.numItems = numItems_;
	}

	/**
	 * ensures that the Array has at least this Capacity!
	 * @param maxItems
	 */
	public void ensureCapacity(final int maxItems) {
		if (maxItems > items.length) {
			final int[] tmp = new int[maxItems];
			System.arraycopy(items, 0, tmp, 0, numItems);
			items = tmp; 
		}
	}

	/** @see streamIO.copy.group.IISemiGroup#addAt(java.lang.Object)	 */
	public IGroup subAt(final Object arg) {
		return subAt((BigInt) arg);
	}

	/** @see streamIO.copy.group.IISemiGroup#addAt(java.lang.Object)	 */
	public BigInt subAt(final BigInt arg) {
		return addAt(arg, !arg.negative);
	}

	/** @see streamIO.copy.group.IISemiGroup#addAt(java.lang.Object)	 */
	public ISemiGroup addAt(final BigInt arg) {
		return addAt(arg, arg.negative);
	}
	
	/** @see streamIO.copy.group.IISemiGroup#addAt(java.lang.Object)	 */
	public BigInt addAt(final int value) {
		items[0]+=value; 
		canonicalizeAt();
		return this; 
	}
	
	/** normalizes the gAdic Number */
	private void canonicalizeAt() {
		int carry = VectorInt.TRIM_AT(items, module, numItems);
		if (carry != 0) {
			items[numItems] = carry;
			++numItems; 
		} else {
			trimLeadingZeros();
		}
	}

	/** trims the leading Zeros and adjusts the valid Size */
	private void trimLeadingZeros() {
		while(--numItems >= 0) {
			if (items[numItems] != 0) {
				break; }
		} ++numItems;
	}

	/** @see streamIO.copy.group.IISemiGroup#addAt(java.lang.Object)	 */
	private BigInt addAt(final BigInt arg, final boolean arg_negative) {
		if (module != arg.module) {
			throw new RuntimeException("The Modules dont match!"); }
		final int min;
		final int max;
		if (numItems < arg.numItems) {
			min = numItems; 
			max = arg.numItems; 
		} else {
			max = numItems; 
			min = arg.numItems; 
		}
		ensureCapacity(max+1);
		int carry; 
		boolean renormalize = false; 
		if (negative == arg_negative) { //same sign, add
			//normally a CarryOver happens only once in the highest Element
			VectorInt.ADD_AT(items, arg.items, 0, min); //add
			if (max > numItems) { //copy the rest
				System.arraycopy(arg.items, min, items, min, max-min); }
			numItems = max; 
			canonicalizeAt();
			return this;
		} else { //opposite Sign, subtract
			VectorInt.SUB_AT(items, arg.items, 0, min); //subtract, not considering Signs
			if (max > numItems) { //negate the rest, definitely negative Result
				System.arraycopy(arg.items, min, items, min, max-min); 
				VectorInt.NEG_AT(items, 0, min); 
				negative = !negative;
			} else if (numItems > min) { //definitely positive Result 
			} else { //unsure whether positive or negative
			}
			numItems = max; 
			carry = VectorInt.TRIM_AT(items, module, max);
			if (carry < 0) { //flip Sign
				negative = !negative;
				VectorInt.NEG_AT(items, 0, max);
				carry = -carry;
				renormalize = true;  
			}
		}
		if (carry > 0) {
			items[numItems] = carry;
			++numItems; 
		}
		if (renormalize) {
			carry = VectorInt.TRIM_AT(items, module, numItems); 
			if (carry != 0) {
				throw new RuntimeException("Should never happen!"); }
		}
		trimLeadingZeros();
		return this;
	}

	/** @see function.IIOrderAble#isLessThan(java.lang.Object)	 */
	public boolean isLessThan(Object arg) {
		return less((BigInt) arg);
	}

	/** @see function.IIOrderAble#isLessThan(java.lang.Object)	 */
	public boolean less(final BigInt arg) {
		return Position(arg) < 0;
	}

	/** @see function.IIOrderAble#isLessThan(java.lang.Object)	 */
	public int Position(final BigInt arg) {
		if (negative == arg.negative) { //same sign
			int sign = VectorInt.COMPARE_TO(this.items, this.numItems, arg.items, arg.numItems);
			if (negative) {
				return -sign; 
			} else {
				return sign; 
			}
		}
		//opposite Sign: 
		if (this.numItems + arg.numItems == 0) {//check if negative Zero
			return 0; }
		return negative?-1:1;
	}

	/** @see streamIO.copy.groupM.IISemiGroupM#mulAt(java.lang.Object)	 */
	public ISemiGroupM mulAt(final Object arg) {
		return mul((BigInt) arg);
	}
		
	/** @see streamIO.copy.groupM.IISemiGroupM#mulAt(java.lang.Object)	 */
	public ISemiGroupM mul(final Object arg) {
		return mul((BigInt) arg);
	}
		
	/**
	 * although the explicit convolution is an O(n²) Operation, 
	 * it is more explicit and easy to realize using addProdAt 
	 * 
	 * A more efficient Algorithm is described in the numerical Recipes, 
	 * where Fourier Transformation is used to perform the Convolution in O(N*logN) instead of O(N^2) Operations.  
	 * @see streamIO.copy.groupM.IISemiGroupM#mulAt(java.lang.Object)	 
	 */
	public BigInt mul(final BigInt arg) {
		BigInt ret = new BigInt(module); 
		final int sum = arg.numItems+this.numItems;
		ret.negative = (arg.negative != this.negative);
		ret.setNumItems(sum);
		for(int i = arg.numItems; --i >= 0;) {
			VectorInt.ADD_PROD_AT(ret.items, items, arg.items[i], 0, numItems, i); }
		ret.canonicalizeAt();
		return ret;
	}
	
	/**
	 * Do a Kind of Polynom Division, which is slow, 
	 * but at least applies both to Integers and to Polynoms!  
	 * @see streamIO.copy.groupM.IIGroupM#divAt(java.lang.Object)	 
	 */
	public IGroupM divAt(final Object arg) {
		return div((BigInt) arg);
	}

	/**
	 * Do a Kind of Polynom Division, which is slow, 
	 * but at least applies both to Integers and to Polynoms!  
	 * @see streamIO.copy.groupM.IIGroupM#divAt(java.lang.Object)	 
	 */
	public BigInt div(final BigInt arg) {
		return ((BigInt) copy()).ModAtDivAt(arg, new BigInt(module));
	}
	
	/** @see streamIO.copy.group.ring.IIntRing#ModAtDivAt(java.lang.Object, streamIO.copy.group.ring.IIntRing)	 */
	public IIntRing ModAtDivAt(Object arg, IIntRing quotient) {
		return ModAtDivAt((BigInt) arg, (BigInt) quotient);
	}

	/**Despite it's similar Structure, 
	 * Polynom Division is very different from g-adic Division, 
	 * which is very simple for g=2, but quite complicated for larger gs!!!
	 * With Rollover the Algorithm needs a lot more than the simple Polynom Division.
	 * You ALWAYS (except for g=2) have an Uncertainty in the last Digits
	 * that could affect the first Digits by a Ripple Carry!
	 * 
	 * A more efficient Algorithm is described in the numerical Recipes, 
	 * where the Newton Algorithm is used to solve the Equation this = quot*arg+mod for quot and mod
	 * @see  streamIO.copy.group.ring.IIntRing#ModAtDivAt(java.lang.Object, streamIO.copy.group.ring.IIntRing)
	 */
	public BigInt ModAtDivAt(final BigInt arg, final BigInt quotient) {
		if ((arg == null) || (arg.isZero())) {
			throw new ArithmeticException("Division of "+this+" by Zero:"+arg); }
		quotient.setNumItems(numItems - arg.numItems + 1);	//Make Space for the maximum Degree of the Quotient
		if (quotient.numItems <= 0) {	//Divisor is smaller than Dividend?
			quotient.zeroAt();	//=> Quotient = 0
			return this; }	//=> Remainder == Original
		final int divisor = arg.items[arg.numItems-1]; //The Divisor always stays the same
		int divisor1 = divisor +1;
		int carry = 0;
		for (int i = numItems, iq = quotient.numItems; --iq >= 0; ) { //
			if (carry != 0) { //carry holds the total Value, ...
				carry = carry*module + items[--i]; 
			} else { //independent of the Vector
				carry = items[--i];
			}
			if (iq == 0) { //avoid Super-Estimation, ...
				divisor1 = divisor; } //...except for the last Iteration!
			int quot = quotient.items[iq] = carry/divisor1;
			//This is the basic Polynom Division: Subtract the multiplied Polynom from this one...
			if (quot > 0) { //Check only useful/needed with whole Numbers.
				carry -= quot*divisor; //Subtract the Result multiplied by the Remainder
				VectorInt.ADD_PROD_AT(items, arg.items, -quot, 0, arg.numItems, iq);
				//correct Super-Estimation resulting from small Divisors (e.g. 19 or 29 with large following Digits!
				while ((carry += VectorInt.TRIM_AT(items, module, iq, i)) < 0) { //now only for the last Iteration!
					final int corr = 1-carry/(divisor+divisor+1); //to speed up Convergence, but don't overcompensate!
					carry += corr*divisor; //this limits this loop to two Iterations.
					quotient.items[iq]-=corr;
					VectorInt.ADD_PROD_AT(items, arg.items, corr, 0, arg.numItems, iq);
				} //
			}
		}
		canonicalizeAt();
		quotient.canonicalizeAt(); //numItems = arg.numItems; 
		//Handling the Signs: the Remainder keeps the Sign
		quotient.negative = (negative != arg.negative); //the Quotient is determined by Division  
		return this; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// Interface ICountAble
	////////////////////////////////////////////////////////////////////////////////

	/** @see function.ICountAble#getByte()	 */
	public byte getByte() { return (byte) getLong(); }

	/** @see function.ICountAble#getShort()	 */
	public short getShort() { return (short) getLong(); }

	/** @see function.ICountAble#getInt()	 */
	public int getInt() { return (int) getLong(); }

	/** @see function.ICountAble#getLong()	 */
	public long getLong() {
		checkInvariant();
		int i = numItems;
		if (numItems <= 0) {
			return 0; }
		long ret = items[--i]; 
		while(--i >= 0) {
			if (0 > (ret = ret*module + items[i])) {
				throw new RuntimeException("Numeric Overflow!"); }
		}
		if (negative) {
			return -ret; }
		return ret;
	}

	/** @see function.IMeasurAble#getDouble()	 */
	public double getDouble() { 
		//return getLong(); 
		checkInvariant();
		int i = numItems;
		if (numItems <= 0) {
			return 0; }
		double ret = items[--i]; 
		while(--i >= 0) {
			ret = ret*module + items[i];
		}
		if (negative) {
			return -ret; }
		return ret;
	}

	/** @see function.IMeasurAble#getFloat()	 */
	public float getFloat() { return (float) getDouble(); }

	////////////////////////////////////////////////////////////////////////////////
	/// Interface ICopyAble
	////////////////////////////////////////////////////////////////////////////////

	/** @see streamIO.copy.group.ring.metric.IIWellOrder#maxValueAt()	 */
	public ICopyAble copyAt(Object arg, int Depth) {
		return copyAt((BigInt) arg);
	}

	/** @see streamIO.copy.group.ring.metric.IIWellOrder#maxValueAt()	 */
	public BigInt copyAt(final BigInt arg) {
		setNumItems(arg.numItems);
		System.arraycopy(arg.items, 0, this.items, 0, arg.numItems);
		this.negative = arg.negative; 
		this.module = arg.module; 
		return this;
	}

	/** @see streamIO.copy.group.ring.metric.IIWellOrder#maxValueAt()	 */
	public boolean equals(final Object arg) {
		return equals((BigInt) arg); }

	/** @see streamIO.copy.group.ring.metric.IIWellOrder#maxValueAt()	 */
	public boolean equals(final BigInt arg) {
		return 0 == Position(arg);
	}

	/** Generic Implementation of the newInstance() Method, needn't be overwritten!
	  * Doesn't work for Arrays, but these can't be derived from ACopyAble anyway.
	  */
	public ICopyAble newInstance() { return new BigInt(module); } //
	
	////////////////////////////////////////////////////////////////////////////////
	/// Optimizations
	////////////////////////////////////////////////////////////////////////////////

	/** @see java.lang.Object#toString()	 */
	public String toString() {
		StringBuffer sb = new StringBuffer(4*numItems);
		sb.append(negative?'-':' ');
		for (int i = numItems; --i >= 0;) {
			if (module < 36) {
				char chr = (char)('0' + items[i]);
				if (chr > '9') {
					chr -='9'-'A'; }
				sb.append(chr); 
			} else {
				sb.append(items[i]).append(", ");
			}
		} 
		return sb.toString();
	}
	
	/**reads the Value from the String; 
	 * the Module / Radix is retained
	 * 
	 * TODO: you only need to copy the parsed Characters into the Array, 
	 * no Calculations are necessary! 
	 * @see ICopyAble#fromStringAt(String) 
	 */
	public ICopyAble fromStringAt(String value) {
		value = value.trim();
		int fraction = 0;  
		int pos = -1;  
		boolean negative = (value.charAt(0)=='-');
		if (negative || (value.charAt(0)=='+')) {
			++pos; }
		zeroAt();
		while(++pos < value.length()) {
			if (fraction != 0) {
				fraction*=module; }
			mulAt(module); //shifting is sufficient when using same Module
			char chr = value.charAt(pos); 
			if (chr < '0') {
			} else if (chr == '.') { if (fraction == 0) { fraction = 1; continue; }  
			} else if (chr <= '9') { addAt(chr-'0'); continue;  //copying is sufficient when using same Module!
			} else if (chr <  'A') { 
			} else if (chr <= 'Z') { addAt(chr-'A'+10); continue; 
			} else if (chr <  'a') {
			} else if (chr <= 'z') { addAt(chr-'a'+10); continue; 
			} else {
			}
			throw new RuntimeException("Invalid Argument:"+value+" at "+pos); //ParseException("Invalid Argument:"+value, pos);
		}
		//divAt(fraction);
		return this; 
	}

	/**
	 * @see streamIO.copy.group.IGroup#zeroAt()
	 * @return this set to 0 in Place 	 
	 */
	public IGroup zeroAt() {
		numItems = 0; 
		return this;
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////

	protected void checkInvariant() {
		for (int i = numItems; --i >= 0;) {
			if (items[i] < 0) {
				System.out.println("Should never happen!");
				throw new RuntimeException("Should never happen!");
			}
		}
	}

	/** tests all Methods of this Class	 */
	public static void testIt() {
		for(int i = 1; ++i < 1000;) {
			for(int j = 1; ++j < 1000;) {
				final int rndA = (int)(60000*(Math.random() - .5));//-17704;//
				final BigInt a = new BigInt(10, rndA);
				L.n(rndA+"\t"+a);
				Assert.EQUALS(a.getInt(), rndA); 

				int rndB; 
				while (0 == (rndB = (int)(60000*(Math.random() - .5))));//
				//rndB = 20882;//-19999;
				final BigInt b = new BigInt(10, rndB); 
				L.n(rndB+"\t"+b); 
				Assert.EQUALS(b.getInt(), rndB); 

				Assert.EQUALS(b, b.copy()); 
				Assert.EQUALS(rndA < rndB, a.less(b)); 
				Assert.EQUALS(rndA+rndB, ((BigInt)a.add(b)).getInt()); 
				Assert.EQUALS(rndA-rndB, ((BigInt)a.sub(b)).getInt());  

				BigInt c = a.mul(b); 
				Assert.EQUALS(rndA*rndB, c.getInt());

				BigInt quotient = new BigInt(10);
				BigInt remainder = c.ModAtDivAt(b, quotient); 
				Assert.IS_TRUE(remainder.isZero()); 
				L.n(quotient); 

				c = a.mul(b); 
				int rndC = (int)(60*(Math.random() - .5));//
				c.addAt(rndC); rndC = c.getInt(); L.n(c); 
				remainder = c.ModAtDivAt(b, quotient);
				final int quot = rndC/rndB;   
				Assert.EQUALS(quot, quotient.getInt()); 
				Assert.EQUALS(rndC-quot*rndB, remainder.getInt()); 

				System.out.println();
			}
		}
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) {
		Color color = Color.orange; //.CYAN; //.MAGENTA; //.PINK;
		//Object obj1 = 1; 
		//Object obj2 = 1; 
		System.out.println(color.getRed()); 
		System.out.println(color.getGreen()); 
		System.out.println(color.getBlue()); 
		testIt();
	}

}
