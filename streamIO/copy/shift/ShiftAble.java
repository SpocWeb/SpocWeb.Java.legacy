package streamIO.copy.shift;

import streamIO.copy.ICopyAble;

/**Interface defining (arithmetic) Shifts left and right
 * as well as rotation left and right.
 *
 * A Default Implementation is done in 'absShiftable'.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:25Z
 * digest: 1846b6484c8acea8d8f9d2ff722bdda6c32153858ac07e48eb8050b72572bbcc
 * stale: false
 * tags: [code/in_place_operation, code/bit_manipulation]
 * concepts: [Shift and Rotate]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public interface ShiftAble
extends IShiftAble, ICopyAble {

	//Operations for one Position (Simulationg Default =1 by Overloading)

	//Changing Order:

	/**Reverting the Order of the Items:			*/	public ShiftAble reverse();
	/**Reverting the Order of the Items in Place:	*/	public ShiftAble reverseAt();

	/**Creates an Object appropriate for use with these Operations   */
	public Object createCarry();

	//Shifting:

	/**Shift left  by one Position: x<<1*/	public ShiftAble asl(Object Carry);
	/**Shift right by one Position: x>>1*/	public ShiftAble asr(Object Carry);
	/**Shift left  by one Position: x<<1*/	public ShiftAble asl();
	/**Shift right by one Position: x>>1*/	public ShiftAble asr();

	/**Arithmetic Shift right by one Position: x<<1	*/	public ShiftAble lsr(Object Carry);
	/**Arithmetic Shift right by one Position: x<<1	*/	public ShiftAble lsr();

	//Rotating:

	/**Rotate left  by one Position: x<<1	*/	public ShiftAble rol();
	/**Rotate right by one Position: x>>1	*/	public ShiftAble ror();
	/**Rotate left  by one Position: x<<1	*/	public ShiftAble rol(Object Carry);
	/**Rotate right by one Position: x>>1	*/	public ShiftAble ror(Object Carry);


	//Operations for several Positions (Simulationg Default =1 by Overloading)

	//Multiple Shifting:

	/**Arithmetic Shift left  by several Positions: x<<arg	*/	public ShiftAble asl(int arg, Object Carry);
	/**Arithmetic Shift right by several Positions: x>>arg	*/	public ShiftAble asr(int arg, Object Carry);
	/**Logical    Shift right by several Positions: x>>1	*/	public ShiftAble lsr(int arg, Object Carry);

	/**Arithmetic Shift left  by several Positions: x<<arg	*/	public ShiftAble asl(int arg);
	/**Arithmetic Shift right by several Positions: x>>arg	*/	public ShiftAble asr(int arg);
	/**Logical    Shift right by several Positions: x>>1	*/	public ShiftAble lsr(int arg);

	/**Arithmetic Shift left  by several Positions in Place: x<<=arg*/	public ShiftAble aslAt(int arg, Object Carry);
	/**Arithmetic Shift right by several Positions in Place: x>>=arg*/	public ShiftAble asrAt(int arg, Object Carry);
	/**Logical    Shift right by several Positions in Place: x<<1	*/	public ShiftAble lsrAt(int arg, Object Carry);

	/**Arithmetic Shift left  by several Positions in Place: x<<=arg*/	public ShiftAble aslAt(int arg);
	/**Arithmetic Shift right by several Positions in Place: x>>=arg*/	public ShiftAble asrAt(int arg);
	/**Logical    Shift right by several Positions in Place: x<<1	*/	public ShiftAble lsrAt(int arg);

	//Multiple Rotating:

	/**Rotate left  by several Positions: x<<arg	*/	public ShiftAble rol(int arg, Object Carry);
	/**Rotate right by several Positions: x>>arg	*/	public ShiftAble ror(int arg, Object Carry);

	/**Rotate left  by several Positions: x<<arg	*/	public ShiftAble rol(int arg);
	/**Rotate right by several Positions: x>>arg	*/	public ShiftAble ror(int arg);

	/**Rotate left  by several Positions in Place: x<<arg	*/	public ShiftAble rolAt(int arg, Object Carry);
	/**Rotate right by several Positions in Place: x<<arg	*/	public ShiftAble rorAt(int arg, Object Carry);

	/**Rotate left  by several Positions in Place: x<<arg	*/	public ShiftAble rolAt(int arg);
	/**Rotate right by several Positions in Place: x<<arg	*/	public ShiftAble rorAt(int arg);

}
