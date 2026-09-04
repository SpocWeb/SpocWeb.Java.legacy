package streamIO.copy.boole;

import streamIO.copy.ICopyAble;

/**This Class is a Realization of Boole
 * with a single boolean Value (Bit).
 * The Name conflicts with java.lang.Boolean.	 */
final public class Boolean
extends ABoole {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Empty Constructor for newInstance	 */
	public Boolean () { super(null); self = this;}	//super(this);}	//not possible to use 'this' in the constructor call!

	/**Constructor for scalar Type boolean	 */
	public Boolean (boolean arg){this(); Value = arg;}

	/**Constructor for Object Types	 */
	public Boolean (Object arg){this(); Value = convertArg (arg);}

	/**Helper Routine to convert to long from any other numeric Type:
	 * RingLong, Number or countable.
	 * Uses ASemiGroup.getLong to do that.	 */
	private boolean convertArg (Object arg) {
		return (arg instanceof Boolean)? ((Boolean)arg).Value : ((java.lang.Boolean)arg).booleanValue();}

	/**This is the Value of this Boolean Element
	 * It is made public to allow for direct Manipulation
	 * and Transfer of ByRef Values. */
	public boolean Value;

	/**Boolean Constant for the Representation of 'true': 1	*/
	public Boole TrueAt(){Value = true; return this;}

	/**Boolean AND Operation in Place: &=, &&=	*/
	public Lattice ANDat	(Object arg){Value &= convertArg(arg); return this;}

	/**Boolean OR Operation in Place: |=, ||=	*/
	public Lattice ORat	(Object arg){Value |= convertArg(arg); return this;}

	/**Boolean NOT Operation in Place: ~=, != for single Bit	*/
	public Boole NOTat	(){Value = !Value; return this;}

	/**Boolean Constant for the Representation of 'false': 0	 */
	public Boole FalseAt(){Value = false; return this;}


	//////////////////////////////
	//	Replication CopyAble	//
	//////////////////////////////

	/**Does a shallow Copy of the Argument.
	 * I.e. both Instances will share their inner Components.
	 */
	public ICopyAble shallowCopyAt(Object arg){Value = convertArg(arg); return this;}

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * When overriding, use newInstance on all Components.
	 */
	public ICopyAble newInstance(){return new Boolean();}

	//////////////////////
	//	Optimizations	//
	//////////////////////

	/**Boolean XOR Operation in Place: ^=
	 * a XOR b = true <=> ((a = true) AND (b = false)) OR ((a = false) AND (b = true))*/
	public Lattice XORat	(Object arg){Value ^= convertArg(arg); return this;}

}
