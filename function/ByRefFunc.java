package function;

/**
 * ByRefFunc.java
 * Allows ByRef Transport of Function Objects
 * and returns the Function Object's Value in Map().
 * So this is practically a (faster) Concatenation with the Identity Function.
 *
 * Created on 26. Dezember 2000, 18:18
 *
 * @author  Matthias Heuer
 * @version
 */
public class ByRefFunc
extends AFunction { //absStatic {

	/**Actual Function that is evaluated on 'Map()'  */
	public IFunction Value; // = this; //leads to an infinite Recursion! 

	/** Returns arg Mapped by this Object: this.Map(arg) == this°arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	  */
	public Object Map(final Object arg) { return Value.Map (arg); }

}
