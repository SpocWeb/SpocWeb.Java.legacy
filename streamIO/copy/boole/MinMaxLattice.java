package streamIO.copy.boole;

//import Functions.OrderAble;
import streamIO.copy.order.IOrder;

/**
  * This Class realizes a distributive Lattice with False and True,
  * but without Complement.
  * The Set Operations can be defined on any fully ordered Set,
  * i.e. where any Element can be compared to any other Element.
  *
  * The Set is bounded by it's maximum Element maxEl.
  * and it's minimum Element minEl. who act as False and True.
  *
  * The AND Operation leaves only those Elements that are in both Sets
  * The OR  Operation adds  the larger Elements.
  * The NOT Operation cannot be defined,
  *     because it would require reversing the Representation
  *     and introduce Interval Arithmetics.
  *
  * This Definition makes 'Sub  ()' equivalent to 'less()'
  *                   and 'Super()' equivalent to 'grtr()'
  *
  * This class can take a variable number of parameters on the command
  * line. Program execution begins with the main() method. The class
  * constructor is not invoked unless an object of type 'HalfBoole'
  * created in the main() method.
  *
  * @see streamIO.Copy.Boole.Fuzzy which operates with float Numbers.
  */
public class MinMaxLattice
extends ALattice
implements Lattice {

	/**Representation of False.
	 * In this case the Minimum Value of all.	 */
//	protected static OrderAble False;

	/**Representation of True.
	 * In this case the Maximum Value of all.	 */
//	protected static OrderAble True;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Initializing Constructor	 */
	public MinMaxLattice(IOrder Value_) {
		super(); self = this;
		Value = Value_; }

	/**Reference to the Value of this Instance	 */
	protected IOrder Value;

	//////////////////////////
	//	interface intBoole	//
	//////////////////////////

	/**Boolean AND Operation in Place: &=, &&= for single Bit
	 * a AND b = true <=> (a = true) AND (b = true) 	 */
	public Lattice ANDat	(Object arg) { Value.MinAt(arg); return this; }

	/**Boolean OR Operation in Place: |=, ||= for single Bit
	 * a OR b = true <=> (a = true) OR (b = true) 	 */
	public Lattice ORat	(Object arg) { Value.MaxAt(arg); return this; }

	/**Boolean Constant for the Representation of 'false': =0
	 * Sets this Object to False, i.e. not 'true';
	 * with Vectors it sets all Elements to their respective Value of False*/
//	public Boole FalseAt(){Value.copyAt(False); return this;}

	/**Boolean Constant for the Representation of 'false': =0
	 * Sets this Object to False, i.e. not 'true';
	 * with Vectors it sets all Elements to their respective Value of False*/
//	public Boole TrueAt(){Value.copyAt(True); return this;}

	/**Boolean NOT Operation in Place: ~=, != for single Bit
	 * NOT a = true <=> (a = false)
	 * This Operation cannot be implemented by infinite Sets or limited Sets,
	 * Therefore you need other means to define some Operations.	 */
//	public Boole NOTat	(){throw new AbstractMethodError();}


	//////////////
	//	testing	//
	//////////////

	/**This Method tests all the Methods of this Class.	 */
	public static void testIt (String[] args) throws Exception
	{
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
		testIt(args);
	}

}
