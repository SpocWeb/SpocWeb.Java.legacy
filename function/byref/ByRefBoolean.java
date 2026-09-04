package function.byref;

//import Graph.ICopy;

/**
  * Title: ByRefBoolean<p>
  * Description:
  * This class is for transporting a boolean back from a Method Call.
  * It can also be used for generic Sorting Algorithms or as a Function,
  * since it implements OrderAble and ICountAble.
  *
  * You can also simply use boolean[] to return Values from Method Calls.
  *
  * Known SubClasses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-12-12, 01;52;04<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
final public class ByRefBoolean
//implements ICopy
{

	/**String Representation of true Value	 */
	public static String trueString = "True";

	/**String Representation of false Value	 */
	public static String falseString = "False";

	/**Empty Constructor */
	public ByRefBoolean(){}

	/**Initializing Constructor, just comfortable	 */
	public ByRefBoolean(boolean Value_) { Value = Value_; }

	/**This is the Value of the Object	 */
	public boolean Value;

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
	public java.lang.String toString() {
		if (Value)  {
            return  trueString; }
			return falseString;	}

}
