package synch;

/**Read only Property that can be changed only once.
 * This is usually implemented in Java by private Properties
 * that are set in the Constructor only.
 * In VB6 that has no Constructors this is sometimes necessary
 * to initialize read only Properties.
 * The structure is very close to class 'cachedProperty'
 * but you cannot derive one from the other because both have
 * a public Method (setValue() and invalidate()) that has to be made private
 */
public class writeOnceProperty {

	/**
	 * Determines, whether the Cache is valid
	 * Alternatively the Value 'null' could be used!
	 */
	private boolean changed = false;

	/**The actual cached Value	 */
	private Object Value; // = null;

	/**Returns the Cache directly,
	 * dangerous, because it might be modified!
	 * Should only be used by the Classes having this Property.	 */
	public Object getValue() { return Value; }

	/**The Value can be set only once.	 */
	public void setValue(Object Val) throws IllegalAccessError {
		if (changed) throw new IllegalAccessError();
			changed = true; Value = Val; }

}
