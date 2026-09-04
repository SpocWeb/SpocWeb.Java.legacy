package streamIO.copy.order;

import java.io.IOException;

import streamIO.copy.ACopyAble;
import function.IIOrderAble;
import function.IOrderAble;

/**Default Implementation of an order Relation.
 * Every Function is derived from the 'less' Function,
 * which itself is kept abstract.
 *
 * ternary Result:
 * When two Objects are compared you can have three (four) Results:
 * A < B, A > B, A == B (or an Equivalence Relation can be defined)
 * and A, B are incomparAble.
 *
 * Abstract Methods:
 * less	 */
public class AOrder
extends ACopyAble
implements IOrder {

	////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Returns the Maximum of this and the Operand, but not as a Copy!
	  * @param arg  : Object to compare to <CODE>this</CODE>
	  * @return
	  */
	public static IOrder Max (IOrder self, Object arg) { return self.isLessThan(arg) ? (IOrder) arg : self; }

	/** Returns the Minimum of this and the Operand, but not as a Copy!
	  * @param arg  : Object to compare to <CODE>this</CODE>
	  * @return
	  */
	public static IOrder Min (IOrder self, Object arg) { return self.isLessThan(arg) ? self : (IOrder) arg; }

	/** Returns the Maximum of this and the Operand in Place
	  * @param arg  : Object to compare to <CODE>this</CODE>
	  * @return
	  */
	public static IOrder MaxAt (IOrder self, Object arg){if (self.isLessThan(arg)) self.copyAt(arg); return self; }
	//Don't use shallowCopyAt here!

	/** Returns the Minimum of this and the Operand in Place
	  * @param arg  : Object to compare to <CODE>this</CODE>
	  * @return
	  */
	public static IOrder MinAt (IOrder self, Object arg){if (self.isMoreThan(arg)) self.copyAt(arg); return self; }
	//Don't use shallowCopyAt here!

	////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////

	/** Reference to the Delegate	 */
	protected IOrder self;

	////////////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////////////

	/**This Constructor is only used for direct Child Classes to call.
	 * They replace Self by the Child Object with it's overloaded Methods.
	 * Thus you cannot forget to call the correct Constructor
	 * and don't need to initialize   */
	protected AOrder(){ self = this; }//

	/** This Constructor is used for the Delegator Classes to call
	 * and replace Self by the Delegator Object with it's overloaded Methods.
	 * @param self_ : inner Object for Delegation.
	 */
	public AOrder(IOrder self_) { self = self_; }

	////////////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////////////

	/** Returns the Maximum of this and the Operand, but not as a Copy!
	  * @param arg  : Object to compare to <CODE>this</CODE>
	  * @return
	  */
	public IOrder Max (Object arg) { return self.isLessThan(arg) ? (IOrder)arg : self;}

	/** Returns the Minimum of this and the Operand, but not as a Copy!
	  * @param arg  : Object to compare to <CODE>this</CODE>
	  * @return
	  */
	public IOrder Min (Object arg) { return self.isLessThan(arg) ? self : (IOrder)arg;}

	/** Returns the Maximum of this and the Operand in Place
	  * @param arg  : Object to compare to <CODE>this</CODE>
	  * @return
	  */
	public IOrder MaxAt (Object arg) {
		if (self.isLessThan(arg)) self.copyAt(arg); return self;}
	//Don't use shallowCopyAt here!

	/** Returns the Minimum of this and the Operand in Place
	  * @param arg  : Object to compare to <CODE>this</CODE>
	  * @return
	  */
	public IOrder MinAt (Object arg) {
		if (self.isMoreThan(arg)) self.copyAt(arg); return self;}
	//Don't use shallowCopyAt here!

	////////////////////////////////////////////////////////////////////////////
	//  Interface OrderAble
	////////////////////////////////////////////////////////////////////////////

	/** between: returns True, when 'Self' is between arg1 and arg2
	  * @param arg1 : first Border to compare to <CODE>this</CODE>
	  * @param arg2 : second Border to compare to <CODE>this</CODE>
	  * @return
	  */
	public boolean isBetween (Object arg1, Object arg2) {
		return self.isLessThan(arg1) ^ self.isLessThan (arg2);}

	/** greater: '>' Returns True, when 'Self' > arg
		 * @param arg  : Object to compare to <CODE>this</CODE>
		 * @return
		 */
	public boolean isMoreThan (Object arg) { return !self.notMoreThan(arg); }

	/** greater or equal: '>=' Returns True, when 'Self' >= arg
	  * @param arg  : Object to compare to <CODE>this</CODE>
	  * @return
	  */
	public boolean notLessThan (Object arg) { return !((IIOrderAble)self).isLessThan(arg); }

	/** less or equal: '<=' Returns True, when 'Self' <= arg
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return
	 */
	public boolean notMoreThan (Object arg) { return (((IIOrderAble)self).isLessThan(arg) || self.equals(arg)); }

	/** Virtual Method!
	 * less: '<' Returns True, when 'Self' < arg
	 * Implemented only to make this class concrete for delegation.
	 * Should be overwritten!
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return
	 */
	public boolean isLessThan (Object arg) { return ! self.notLessThan(arg); }
//  {throw new AbstractMethodError();}

	/** Returns the Position of this Number relative to arg:
	 * -1 for smaller, otherwise +1
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return -1 when this.less(arg)
	 *         +1 else
	 */
	public int Position(Object arg)	{
		if (self.isLessThan(arg))	return -1;
		else				return +1; }

	/** Returns the exact Position of this Number relative to arg:
	  * -1 for smaller, 0 for equal, otherwise +1
	  * @param arg  : Object to compare to <CODE>this</CODE>
	  * @return -1 when this.less  (arg)
	  *          0 when this.equals(arg)
	  *         +1 else
	  */
	public int compareTo(Object arg) {
		if		(self.isLessThan	(arg))	return -1;
		else if (self.equals(arg))	return  0;
		else						return +1; }

	////////////////////////////////////////////////////////////////////////////
	//  Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////

	/** Method to test all Implementations in this class.
	 * @throws IOException raised by reading Keystrokes
	 */
	public static void testIt() throws java.io.IOException {
		IOrderAble a = (IOrderAble) testInstance;
		IOrderAble b = (IOrderAble) testInstance.copy();
		System.out.println("Testing AOrderable:");

		System.out.println (a + "< " + b  + " = " + a.isLessThan(b));
		System.out.println (a + "<=" + b  + " = " + a.notMoreThan(b));
		System.out.println (b + "<=" + a  + " = " + b.notMoreThan(a));
		System.out.println (a + "<=" + a  + " = " + a.notMoreThan(a));
		System.out.println (a + "> " + b  + " = " + a.isMoreThan(b));
		System.out.println (a + ">=" + a  + " = " + a.notLessThan(a));
		System.out.println (a + ">=" + b  + " = " + a.notLessThan(b));
		System.out.println (b + ">=" + a  + " = " + b.notLessThan(a));
		System.in.read(); System.in.read();
		tMaxiMini();
		tMaxMin();
	}

	/** Tests Max() and Min()
	 * @throws IOException raised by reading Keystrokes
	 */
	private static void tMaxMin() throws java.io.IOException {
/*		BodyDouble x1 = new BodyDouble();
		BodyDouble x2 = new BodyDouble();
		System.out.println ();
		System.out.println ("Test von Max und Min :");
		for (int z1 = 0; ++z1 <= 11;) {
			x1.Value = Math.random()-0.5;	// 1/2 == 0!!!;
			x2.Value = Math.random()-0.5;
			System.out.print ("Soll : ");
			if (x1.less(x2))System.out.println (x1 + "  " + x2);
			else			System.out.println (x2 + "  " + x1);
			System.out.println ("Ist  : " + x1.Min(x2) + "  " + x2.Max(x1));
		}
		System.in.read(); System.in.read();
*/	}

	/** Tests MaxAt() and MinAt()
	 * @throws IOException raised by reading Keystrokes
	 */
	private static void tMaxiMini() throws java.io.IOException {
/*		BodyDouble x1 = new BodyDouble();
		BodyDouble x2 = new BodyDouble();
		BodyDouble x3 = new BodyDouble();
		BodyDouble x4 = new BodyDouble();

		x3.Value = +1;
		x4.Value = -1;
		System.out.println ();
		System.out.println ("Test von MaxAt und MinAt :");
		System.out.println ("Der linke Wert sollte monoton steigen,der rechte monoton fallen !");
		for (int z1 = 1; ++z1 <= 10;) {
			x1.Value = Math.random()-0.5; x3.MinAt(x1);
			x2.Value = Math.random()-0.5; x4.MaxAt(x2);
			System.out.print ("Soll : ");
			if (x2.less(x4))System.out.print   (x4 + "  ");
			else			System.out.print   (x2 + "  ");
			if (x1.less(x3))System.out.println (x1)       ;
			else			System.out.println (x3);
			System.out.println ("Ist  : " + x4 + "  " + x3);
		}
		System.in.read(); System.in.read();
*/	}

}
