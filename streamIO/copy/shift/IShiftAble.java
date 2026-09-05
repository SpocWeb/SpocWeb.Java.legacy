package streamIO.copy.shift;

/**Minimum Interface for Shifting Positions in a g-adic Number System.
 * I.e. a way to represent Numbers by a polynomic System
 * of Character representations mod g in Positions of Value g^n.
 * Shifting by one Position is equivalent to a Multiplication with / Division by g.
 *
 * There is a difference between arithmetic and logical Shifting
 * when using signed integers.
 * The highest bit has to retain it's value with arithmetic Shifting.
 *
 * Design Decisions:
 * since Carry has only meaning during an Operation,
 * and maintaining a Carry Relationship throughout the Lifetime of an Object
 * is too fragile, it can be externalized by adding the Carry as In-Parameter
 * and adding getCarry() for the Out-Parameter, since shifting returns itself.
 * Rotation is not affected, higher Rotations are simulated using Shifting
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:25Z
 * digest: 24216fad89cec4f8d90be8165e08e9208a8a209aca3ee11384c0bc92a877478b
 * stale: false
 * tags: [code/in_place_operation, code/bit_manipulation]
 * concepts: [g-adic Number Representation, Shift and Rotate]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * and transferring the Carry.	 */
public interface IShiftAble {

	//Operations for one Position (Simulationg Default =1 by Overloading)

	/**Returns the Number of Items for the shifting Operations.
	 * This is also the Period for Rotation and Reversion (for performance Reasons)*/
	public int getDim();

	/**Sets    the Number of Items for the shifting Operations	 */
	public int letGrad(int Grad, boolean preserve, boolean initialize);

	/**Clears the Carry Flag, necessary to prevent rotation on repetetive shifting.	 */
//	public void ClearCarry();

	/**Returns the Carry for a consecutive Shift, when not maintained in the Object.	 */
	public ShiftAble getCarry();

	/**Sets the Carry for a consecutive Shift, when not maintained in the Object.	 */
	public ShiftAble setCarry(ShiftAble Carry);

	//Shifting:

	/**Arithmetic Shift left by one position in Place: x<<=1	 */
	public ShiftAble aslAt();

	/**Arithmetic Shift right by one position: x>>=1	 */
	public ShiftAble asrAt();


	//Rotating, makes sense only for finite Length Data Types:

	/**Rotate left by one position in Place: x<<1	 */
	public ShiftAble rolAt();

	/**Rotate right by one position in Place: x>>1	 */
	public ShiftAble rorAt();

	//////////////////////////
	//	Methods with Carry	//
	//////////////////////////

	/**Arithmetic Shift left by one position in Place: x<<=1	 */
	public ShiftAble aslAt(Object Carry);

	/**Arithmetic Shift right by one position: x>>=1	 */
	public ShiftAble asrAt(Object Carry);


	//Rotating, makes sense only for finite Length Data Types:

	/**Rotate left by one position in Place: x<<1	 */
	public ShiftAble rolAt(Object Carry);

	/**Rotate right by one position in Place: x>>1	 */
	public ShiftAble rorAt(Object Carry);

	/**Logic Shift right by one position in Place: x>>>>=1	 */
	public ShiftAble lsrAt(Object Carry);


	/**Arithmetic shifting, makes sense only for
	 * signed finite Length Data Types
	 * and only when shifting right.	 */

	/**Logic Shift left by one position in Place: x<<=1
	 * Here: asl == lsl	 */
	//	public absShiftable aslAt();

	/**Logic Shift right by one position in Place: x>>>>=1	 */
	public ShiftAble lsrAt();

}
