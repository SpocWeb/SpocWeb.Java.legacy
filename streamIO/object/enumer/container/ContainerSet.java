package streamIO.object.enumer.container;

import streamIO.IIStreamIn;
import tester.IEquivalence;

/**
  * Title: Set<p>
  * Description:
  * This Class defines a Set of Objects,
  * i.e. a Container without Duplicates (Objects have 'Identity').
  * It wraps the Container Interface
  * so that only the relevant Methods are available.
  *
  * The Container Interface is not implemented for Simplicity.
  * It can not be changed via its Enumerator
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	04-14-2002, 01:06 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public class ContainerSet
implements streamIO.IIterAble {

	////////////////////////////////////////////////////////////////////////////////
	//  Member Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** The Members of the Set are stored in a HashContainer
	  * To protect the Set from illegal Operations it is completely wrapped	 */
	protected Container cnt;

	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** Looks up the Member of this Set that is equivalent to {@code arg}.
	  * @return the Item, equivalent to arg, contained in this Set	 */
	final public Object isSet(final Object arg) {
		try {
			return cnt.findFirst(arg);
		} catch (NoSuchMethodException x) { }
		return null; }

	/** sets the Object in this Set
	  * @return the previous Item, null if the Item was not set. 	 */
	final public Object set(final Object arg) {
/*		if (arg == null) {
			throw new IllegalArgumentException("null is not allowed in" + this.getClass().getName()); }
*/		return cnt.setItem(arg); }

	/** unsets (removes) the Object in this Set
	  * @return the previous Item, null if the Item was not set. 	 */
	final public Object unSet(final Object arg) {
		try {
			return cnt.removeItem(arg);
		} catch ( streamIO.object.ModificationException x) {
			return null;
		}
	}

	/** clears the whole Set */
	public void clear() { cnt.zeroAt(); }

	/** Returns how many Items this Set currently holds.
	  * @return the Number of Items in this Set */
	public int size() { return cnt.getInt(); }

	/** Returns a new Input streamIO of the Objects in this Container
	  * in exactly the same State as this one.
	  * If this Container does not support multiple concurrent Iterators, returns 'null'
	  * @return  a new Input streamIO of the Objects in this Container.
	  * @see     Math.Iterator     */
	public IIStreamIn Iterator() { return cnt.Iterator(); }

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor
	  * defaults the Container to a HashContainer 	 */
	public ContainerSet() { cnt = new HashContainer(); }

	/** Constructor
	  * defaults the Container to a HashContainer
	  * using the given Equivalence Relation. */
	public ContainerSet(final IEquivalence eq) { cnt = new HashContainer(eq); }

	/** Constructor taking a Container Instance from which a new Instance is created.
	  * Handing over a Container Instance directly would create the Risk of
	  * accessing the Container outside of this Class.   */
	public ContainerSet(final Container cnt_) {
		this.cnt = (Container) cnt_.newInstance(); }

	/** Constructor taking the Container Class
	  * Handing over a Container Instance directly would create the Risk of
	  * accessing the Container outside of this Class.   */
	public ContainerSet(final Class cnt_) {
		try {
			this.cnt = (Container) cnt_.newInstance();
		} catch (InstantiationException x) {
		} catch (IllegalAccessException x) {
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	//  public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) throws java.io.IOException {
		System.out.println("Testing " + ContainerSet.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws java.io.IOException {
		testIt(args); }

}

