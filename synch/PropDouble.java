package synch;

import function.AOrderAble;
import function.ICountAble;
import function.IMeasurAble;
import function.byref.ByRefDouble;

/**This class is for transporting a double back from a method call
 * and for observing it's Value.
 * The Code is nearly identical to ByRefDouble, only the Value Property
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:43:52Z
 * digest: 5c4b8f3cb4e44a03996692b7023049f684025ed392ba200f04568ddea5af005b
 * stale: false
 * tags: [code/attached_property]
 * concepts: [Observable Double Property]
 * facets: {layer: domain, status: broken, complexity: low}
 * -->
 * is made private here. Subclassing is only possible in C++.   */
public class PropDouble
extends AOrderAble
implements
	ICountAble,
//	Externalizable,
	IMeasurAble
{
	/**Empty Constructor */
	public PropDouble() { super(null); self = this; }

	/**Initializing Constructor, just comfortable	 */
	public PropDouble(double Value_) { this(); Value = Value_; }

	/**The UniCast Subscriber,
	 * can be extended by a MultiCaster	 */
	public ISubscriber subscriber;

	/**This is the Value of the Object	 */
	private double Value;

	/**Returns the Value 	 */
	public double getValue() { return Value; }

	/**Sets the Value.
	 * Despite the {@link #subscriber} field, no Notification is actually sent here
	 * -- see the TODO below.	 */
	public void setValue(double arg) {
		// TODO: LOGIC: setValue() never calls subscriber.update(...), so the public
		// 'subscriber' field is dead: any code relying on PropDouble to notify its
		// Observer on a Value change (as ByRefDouble/UniCaster-style callers expect)
		// silently gets no notification at all.
		Value = arg; }

	//////////////////////
	//  Interface ICountAble
	//////////////////////

	/** Returns the Object Value represented by an 8 Bit Integer	 */
	public byte   getByte() { return (byte ) Value; }

	/**Returns the Object Value represented by a 16 Bit Integer	 */
	public short getShort() { return (short) Value; }

	/**Returns the Object Value represented by a 32 Bit Integer	 */
	public int     getInt() { return (int  ) Value; }

	/**Returns the Object Value represented by a 64 Bit Integer	 */
	public long   getLong() { return (long ) Value; }

	//////////////////////
	//  Interface IMeasurAble
	//////////////////////

	/**Returns the Object Value represented by a scalar Variable of Type double.
	 * It consists of an IEEE Number with 64 Bit (8 Byte):
	 * 52 Bit Mantissa, 11 Bit Exponent, 1 Bit Sign */
	public double getDouble() { return (double) Value; }

	/**Returns the Object Value represented by a scalar Variable of Type float.
	 * It consists of an IEEE Number with 32 Bit (4 Byte):
	 * 23 Bit Mantissa, 8 Bit Exponent, 1 Bit Sign	 */
	public float  getFloat() { return (float) Value; }

    /**Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommendedthat all subclasses override this method.
     * <p>
     * The <code>toString</code> method for class <code>Object</code>
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `<code>@</code>', and
     * the unsigned hexadecimal representation of the hash code of the
     * object.
     *
     * @return  a string representation of the object.
     * @since   JDK1.0
     */
	public String toString() { return Double.toString(Value); }

	//////////////////////
	//	intOrderable	//
	//////////////////////

	/**less: '<' Returns True, when 'Self' < arg	 */
	public boolean isLessThan (Object arg) { return Value < ByRefDouble.GET_DOUBLE(arg); } //((ByRefDouble)arg).Value;}

    /**Returns a hashcode for this Byte.     */
	public int hashCode() {
		long bits = Double.doubleToLongBits(Value);
		return (int)(bits ^ (bits >> 32)); }

    /**Compares this object to the specified object.
     *
     * @param obj	the object to compare with
     * @return 		true if the objects are equivalent; false otherwise.
     * @since   JDK1.1     */
    public boolean equals(Object obj) { return Value == ByRefDouble.GET_DOUBLE(obj); }


	//////////////////////////////////
	//	Interface 'Externalizable'	//
	//////////////////////////////////

	/**Reads the internal Value from a streamIO	 */
//	public void readExternal(ObjectInput In) throws IOException {
//		Value = In.readDouble(); }

	/**Writes the internal Value to the streamIO	 */
//	public void writeExternal(ObjectOutput Out) throws IOException {
//		Out.writeDouble(Value); }

}
