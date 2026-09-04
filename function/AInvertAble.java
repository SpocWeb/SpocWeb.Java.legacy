package function;

/**Title:        AInvertAble<p>
 * Description:  Defines Interfaces and Default Implementations for Functions. <p>
 * Copyright:    Copyright (c) Matthias Heuer<p>
 * Company:      personal<p>
 * @author Matthias Heuer
 * @version 1.0
 */
public abstract class AInvertAble
extends    AFunction
implements IInvertAble {

	/**Cache for the Inverse, could be defined here for all inheriting Classes	 */
	protected IInvertAble Inverse;

	/** Sets the Inverse from outside.
     * This can be done only once, after that an IllegalStateException is thrown.     */
	public void	setInverse(IInvertAble inverse) {
        if (this.Inverse   ==   inverse) return; //prevent Recursion
        if (this.Inverse   !=   null)
		if(!this.Inverse.equals(inverse)) throw new IllegalStateException();
        	this.Inverse    =   inverse;
			this.Inverse.setInverse (this);  }

	/**Inverse, cached for here for all inheriting Classes	 */
	public IInvertAble getInverse() {
        if (Inverse == null) throw new AbstractMethodError(); // realize early that an Error occurred!
        return Inverse; }

	/** Returns arg Mapped in Place by this Object: this.UnMapAt(arg) this°=arg
	  * This is the Function working on 'arg' defined by the implementing Class.
	  * The Class implementing this Method is the means of exchanging this Operation.	 */
	public Object UnMap(Object arg) {
		if (Inverse == null)
			getInverse(); //Optimization!
		return Inverse.UnMap(arg); }

	/**Returns arg Mapped in Place by this Object: this.MapAt(arg) this=°arg
     * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	 */
	public Object UnMapAt(Object arg) {
		if (Inverse == null)
			getInverse(); //Optimization!
		Inverse.UnMapAt(arg);
        return arg; }

	/**This applies the Function to each Item of an Array of Objects	 */
	public Object[] UnMap(Object[] arg) {
		if (Inverse == null)
			getInverse(); //Optimization!
		int Length = arg.length;
		Object[] Return = new Object[Length];
		while (--Length >= 0)
			Return[Length] = Inverse.Map(arg[Length]);
		return Return; }

}
