package streamIO.copy.shift;

import streamIO.copy.ACopyAble;
import streamIO.copy.ICopyAble;

/**Default Implementation of Shifting Positions in a g-adic Number System.
 * I.e. a way to represent Numbers by a polynomic System
 * of Character representations mod g in Positions of Value g^n.
 * Shifting by one Position is equivalent to a Multiplication with / Division by g.
 *
 * There is a difference between arithmetic and logical Shifting
 * when using signed integers.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:29:09Z
 * digest: 97789ca44665bdbbf08b4128af1193ac15a40fc7c6b7ef8d3e7e490b9a74307f
 * stale: false
 * tags: [code/abstract_base, code/delegation, code/in_place_operation]
 * concepts: [Shift and Rotate, Delegation Pattern]
 * facets: {layer: utility, status: broken, complexity: medium}
 * -->
 * The highest bit has to retain it's value with arithmetic Shifting. */
public class AShiftAble
extends ACopyAble
implements ShiftAble {

	/**Clears the Carry Flag, necessary to prevent rotation on repetetive shifting.	 */
//	public void ClearCarry(){throw new AbstractMethodError();}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Returns the Carry for a consecutive Shift, when not maintained in the Object.	 */
	public ShiftAble getCarry(){throw new AbstractMethodError();}

	/**Sets the Carry for a consecutive Shift, when not maintained in the Object.	 */
	public ShiftAble setCarry(ShiftAble Carry){throw new AbstractMethodError();}

	/**Local Reference to the Self, initialized by concrete classes.
	 * Used for the Simulation of (multiple) Inheritance with Delegation.
	 * Must be a virtual Interface Type to be able to take any Implementation.	 */
	private ShiftAble self;

	/**This Constructor is only used in Initialize and Terminate of abstract Classes
	 * and should normally be marked as 'protected' or 'friend',
	 * but all these Routines are not within one Package.
	 * It is needed for the Child Classes to call
	 * and replace Self by the Child Object with it's overloaded Methods.	 */
	public AShiftAble(ShiftAble self_){ self = self_; }

	/**Reverting the Order of the first 'num' Items in Place:	 */
	public ShiftAble reverseAt() {
		int num = self.getDim();
		ShiftAble rev = (ShiftAble) self.copy();
		rev.setCarry(self.getCarry());	//copying the Carry Object too!
		int i=num; while (--i >= 0) {
			rev .asrAt();
			self.aslAt();
		} return self; }

	/**Reverting the Order of the first 'num' Items:	 */
	public ShiftAble reverse(){ return ((ShiftAble) self.copy()).reverseAt(); }

	//Operations for one Position (Simulationg Default =1 by Overloading)

	//////////////////
	//	Shifting:	//
	//////////////////

	/**Arithmetic Shift left by one Position: x<<1	 */
	public ShiftAble asl() { return ((ShiftAble) self.copy()).aslAt(); }

	/**Arithmetic Shift right by one Position: x>>1	 */
	public ShiftAble asr() { return ((ShiftAble) self.copy()).asrAt(); }

	/**Logic Shift right by one Position: x>>1	 */
	public ShiftAble lsr() { return ((ShiftAble) self.copy()).lsrAt(); }

	/**Arithmetic Shift left by one Position: x<<1	 */
	public ShiftAble asl(Object Carry) { return ((ShiftAble) self.copy()).aslAt(Carry); }

	/**Arithmetic Shift right by one Position: x>>1	 */
	public ShiftAble asr(Object Carry) { return ((ShiftAble) self.copy()).asrAt(Carry); }

	/**Logic Shift right by one Position: x>>1	 */
	public ShiftAble lsr(Object Carry) { return ((ShiftAble) self.copy()).lsrAt(Carry); }

	/**Rotating:
	 * This is the only case, where a Period is beneficial.
	 * Also a Carry Flag would be useful to dynamically build Numbers.
	 * but that could also be done by shifting, which is less expensive.	 */

	/**Rotate left by one Position: x<<1	 */
	public ShiftAble rol(Object Carry) { return ((ShiftAble) self.copy()).rolAt(Carry); }

	/**Rotate right by one Position: x>>1	 */
	public ShiftAble ror(Object Carry) { return ((ShiftAble) self.copy()).rorAt(Carry); }

	/**Rotating:
	 * This is the only case, where a Period is beneficial.
	 * Also a Carry Flag would be useful to dynamically build Numbers.
	 * but that could also be done by shifting, which is less expensive.	 */

	/**Rotate left by one Position: x<<1	 */
	public ShiftAble rol() { return ((ShiftAble) self.copy()).rolAt(); }

	/**Rotate right by one Position: x>>1	 */
	public ShiftAble ror() { return ((ShiftAble) self.copy()).rorAt(); }


	//Operations for several Positions (Simulationg Default =1 by Overloading)

	//////////////////////////
	//	Multiple Shifting	//
	//////////////////////////

	/**Arithmetic Shift left by several Positions: x<<arg	 */
	public ShiftAble asl(int arg) { return ((ShiftAble) self.copy()).aslAt(arg); }

	/**Arithmetic Shift right by several Positions: x>>arg	 */
	public ShiftAble asr(int arg) { return ((ShiftAble) self.copy()).asrAt(arg); }

	/**Logic Shift right by several Positions: x>>arg	 */
	public ShiftAble lsr(int arg) { return ((ShiftAble) self.copy()).lsrAt(arg); }

	/**Arithmetic Shift left by several Positions: x<<arg	 */
	public ShiftAble asl(int arg, Object Carry) { return ((ShiftAble) self.copy()).aslAt(arg, Carry); }

	/**Arithmetic Shift right by several Positions: x>>arg	 */
	public ShiftAble asr(int arg, Object Carry) { return ((ShiftAble) self.copy()).asrAt(arg, Carry); }

	/**Logic Shift right by several Positions: x>>arg	 */
	public ShiftAble lsr(int arg, Object Carry) { return ((ShiftAble) self.copy()).lsrAt(arg, Carry); }

	//////////////////////
	//	Implementations	//
	//////////////////////

	/**Arithmetic Shift left by several Positions in Place: x<<=arg	 */
	public ShiftAble aslAt(int arg)	{
		if (arg < 0) return asrAt(-arg);
		ShiftAble buf = self.getCarry(); self.setCarry(null);	//Clear the Carry to prevent rotation, because asl works recursive!
		while (--arg >= 0) {
			self.aslAt(); }
		self.setCarry(buf); 
		return self; }

	/**Arithmetic Shift left by several Positions in Place: x<<=arg	 */
	public ShiftAble aslAt(int arg, Object Carry)	{
 		if (arg < 0) return asrAt(-arg, Carry);
		self.setCarry((ShiftAble) Carry);	//the given Carry is the In-Parameter, the resulting Carry stays available via getCarry()
		while (--arg >= 0) {
			self.aslAt(); }
		return self; }

	/**Arithmetic Shift right by several Positions: x>>=arg	 */
	public ShiftAble asrAt(int arg)	{
		if (arg < 0) return aslAt(-arg);
		ShiftAble buf = self.getCarry(); self.setCarry(null);	//Clear the Carry to prevent rotation, because asl works recursive!
		int i=0; while (++i <= arg) self.asrAt();
		self.setCarry(buf);	//restore the Carry
		return self; }

	/**Arithmetic Shift right by several Positions: x>>=arg	 */
	public ShiftAble asrAt(int arg, Object Carry)	{
		if (arg < 0) return aslAt(-arg, Carry);
		self.setCarry((ShiftAble) Carry);	//the given Carry is the In-Parameter, the resulting Carry stays available via getCarry()
		int i=0; while (++i <= arg) self.asrAt();
		return self; }

	/**Logic Shift right by several Positions: x>>=arg	 */
	public ShiftAble lsrAt(int arg)	{
		if (arg < 0) return aslAt(-arg);	//lsl is equivalent to asl!
		ShiftAble buf = self.getCarry(); self.setCarry(null);	//Clear the Carry to prevent rotation, because asl works recursive!
		int i=0; while (++i <= arg) self.lsrAt();
		self.setCarry(buf);	//restore the Carry
		return self; }

	/**Logic Shift right by several Positions: x>>=arg	 */
	public ShiftAble lsrAt(int arg, Object Carry)	{
		if (arg < 0) return aslAt(-arg, Carry);	//lsl is equivalent to asl!
		self.setCarry((ShiftAble) Carry);	//the given Carry is the In-Parameter, the resulting Carry stays available via getCarry()
		int i=0; while (++i <= arg) self.lsrAt();
		return self; }


	//Multiple Rotating:

	/**Rotate left by several Positions: x<<arg	 */
	public ShiftAble rol(int arg) { return ((ShiftAble) self.copy()).rolAt(arg); }

	/**Rotate right by several Positions: x>>arg	 */
	public ShiftAble ror(int arg) { return ((ShiftAble) self.copy()).rorAt(arg); }

	/**Rotate left by several Positions in Place and Period p: x<<arg	 */
	public ShiftAble rolAt(int arg)	{
		if (arg < 0) return rorAt(-arg);
		int i = arg; while (--i >= 0) {
			self.rolAt(); }	//Rotation doesn't care about existing Carry Entries
		return self; }

	/**Rotate right by several Positions in Place and Period p: x>>arg	 */
	public ShiftAble rorAt(int arg)	{
		if (arg < 0) return rolAt(-arg);
		int i = arg; while (--i >= 0) {
			self.rorAt(); }	//Rotation doesn't care about existing Carry Entries
		return self; }

	/**Rotate left by several Positions: x<<arg	 */
	public ShiftAble rol(int arg, Object Carry) { return ((ShiftAble) self.copy()).rolAt(arg, Carry); }

	/**Rotate right by several Positions: x>>arg	 */
	public ShiftAble ror(int arg, Object Carry) { return ((ShiftAble) self.copy()).rorAt(arg, Carry); }

	/**Rotate left by several Positions in Place and Period p: x<<arg	 */
	public ShiftAble rolAt(int arg, Object Carry)	{
		if (arg < 0) return rorAt(-arg);
		int i = arg; while (--i >= 0)
			self.rolAt();	//Rotation doesn't care about existing Carry Entries
		return self; }

	/**Rotate right by several Positions in Place and Period p: x>>arg	 */
	public ShiftAble rorAt(int arg, Object Carry)	{
		if (arg < 0) return rolAt(-arg);
		int i = arg; while (--i >= 0)
			self.rorAt();	//Rotation doesn't care about existing Carry Entries
		return self; }

	//////////////////////////////////
	//	Replication IShiftAble	//
	//////////////////////////////////

	//Have to be implemented, because this class must be concrete
	//to be able to delegate to.
	//Operations for one Position (Simulationg Default =1 by Overloading)

	/**Returns the Number of Items for the shifting Operations.
	 * This is also Period for Rotation and Reversion (for performance Reasons)*/
	public int getDim(){throw new AbstractMethodError();}

	/**Sets    the Number of Items for the shifting Operations	 */
	public int letGrad(int Grad, boolean preserve, boolean initialize){throw new AbstractMethodError();}

	//Shifting:

	/**Returns the Carry for a consecutive Shift.			*///public ShiftAble getCarry(){throw new AbstractMethodError();}

	/**Arithmetic Shift left by one position in Place: x<<=1*/	public ShiftAble aslAt() { throw new AbstractMethodError();}
	/**Arithmetic Shift right by one position: x>>=1		*/	public ShiftAble asrAt() { throw new AbstractMethodError();}

	/**Logic Shift right by one position in Place: x>>>>=1	*/	public ShiftAble lsrAt() { throw new AbstractMethodError();}

	//Rotating, makes sense only for finite Length Data Types:

	/**Rotate left  by one position in Place: x<<1			*/	public ShiftAble rolAt() { throw new AbstractMethodError();}
	/**Rotate right by one position in Place: x>>1			*/	public ShiftAble rorAt() { throw new AbstractMethodError();}

	//////////////////////////
	//	Methods with Carry	//
	//////////////////////////

	/**Arithmetic Shift left by one position in Place: x<<=1	 */
	public ShiftAble aslAt(Object Carry) { throw new AbstractMethodError(); }

	/**Arithmetic Shift right by one position: x>>=1	 */
	public ShiftAble asrAt(Object Carry) { throw new AbstractMethodError(); }


	//Rotating, makes sense only for finite Length Data Types:

	/**Rotate left by one position in Place: x<<1	 */
	public ShiftAble rolAt(Object Carry) { throw new AbstractMethodError(); }

	/**Rotate right by one position in Place: x>>1	 */
	public ShiftAble rorAt(Object Carry) { throw new AbstractMethodError(); }

	/**Logic Shift right by one position in Place: x>>>>=1	 */
	public ShiftAble lsrAt(Object Carry) { throw new AbstractMethodError(); }

	//////////////////////////////
	//	Replication intCopyAble	//
	//////////////////////////////

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Components.	 */
	public ICopyAble copyAt(Object arg, int Depth) { throw new AbstractMethodError(); }

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() { throw new AbstractMethodError(); }

	/**Always throws, since a concrete carry representation is type-specific.	 */
	public Object createCarry() { throw new AbstractMethodError(); }

	/**Fills this Instance with the Contents read from the streamIO.
	 *
	 * Design Decisions:
	 * This could probably be implemented in an abstract way
	 * by shifting through the Carry Element, but I leave it here!	 */
	public ICopyAble fromStreamAt(java.io.StreamTokenizer arg) { throw new AbstractMethodError(); }

}
