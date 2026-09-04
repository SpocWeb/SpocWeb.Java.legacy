package streamIO;


/**
 * FilterOut.java
 * The Filter is derived from the abstract Base Class AStreamOut
 * and delegates the abstract Operations to an Implementor of the Base Interface.
 * Any StreamOut can also be used as a DeMultiplexer
 * by just connecting several Processes, Threads etc. to it
 *
 * Created on 26. Mai 2001, 22:08
 *
 * @author  Matthias Heuer
 * @version
 */
public class FilterOut
extends AStreamOut {
	
	////////////////////////////////////////////////////////////////////////////
	// static Methods
	////////////////////////////////////////////////////////////////////////////

	private static final Class[] CONSTRUCTOR_FILTER_OUT = { IIStreamOut.class }; 
	
	/**
	 * returns a new FilterIn Instance of the given Class, if nothing fails
	 * useful to set up Tests with differing Filter Components 
	 * or to parameterize Filter Structures using textual Descriptions. 
	 * @param _class the Class to instantiate 
	 * @param arg the stream to append
	 * @return null otherwise
	 */
	final static public FilterOut CREATE_FILTER
	( final Class _class, final IIStreamOut arg)
	//throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException 
	{ return (FilterOut) CREATE_OBJECT(_class, arg); }
	
	/**
	 * returns a new FilterIn Instance of the given Class, if nothing fails
	 * useful to set up Tests with differing Filter Components 
	 * or to parameterize Filter Structures using textual Descriptions. 
	 * @param _class the Class to instantiate 
	 * @param arg the stream to append
	 * @return null otherwise
	 */
	final static public Object CREATE_OBJECT
	( final Class _class, final IIStreamOut arg)
	//throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException 
	{
		try {
			return _class.getConstructor(CONSTRUCTOR_FILTER_OUT).newInstance(new Object[] {arg});
		} catch (final Exception x) {
			return null; 
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	//  Members
	////////////////////////////////////////////////////////////////////////////

	/** Reference to the actual Store */
	protected IIStreamOut out;

	////////////////////////////////////////////////////////////////////////////
	//  Constructor
	////////////////////////////////////////////////////////////////////////////
	
	/** Creates new FilterOut Object */
	public FilterOut (final IIStreamOut _out) { this.out = _out; }

	////////////////////////////////////////////////////////////////////////////
	//  Delegation
	////////////////////////////////////////////////////////////////////////////

	/** adds this Item to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	  */
	public IIStreamOut addItem(final Object arg) {
		out.addItem(arg);
		return this; }

	////////////////////////////////////////////////////////////////////////////
	//  Optimizations
	////////////////////////////////////////////////////////////////////////////

	/** adds these Items to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	  */
	public IStreamOut add(final Object[] arg) {
		int i = arg.length;
		while (--i >= 0) //reverses the Order though!!!
			out.addItem(arg[i]);
		return this; }

	/** adds all Items from the Enumerator to the Store in Place: +=
	 * The Type of Item is not analyzed, i.e. Containers are added as is.	   */
	public IStreamOut add (final IIStreamIn iter) {
		STREAM(iter, this);
		//for (Object curr; (IStreamIn.EOI != (curr = iter.nextItem())) || iter.isValid();) {
		//	Store.addItem(curr); }
		return this; }

}
