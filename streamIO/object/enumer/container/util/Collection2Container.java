package streamIO.object.enumer.container.util;

import java.util.Collection;

import streamIO.IIStreamOut;
import streamIO.IReSetAble;
import streamIO.copy.ICopyAble;
import streamIO.copy.boole.Boole;
import streamIO.object.ModificationException;
import streamIO.object.enumer.Iterator2Enumerator;
import streamIO.object.enumer.container.AContainer;
import streamIO.object.enumer.container.Container;
/**
  * Title: Collection2Container<p>
  * Description:
  * Adapter Class that transforms Implementors of the Interface Collection
  * into the Interface Container. 
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-12-26, 03;03;41<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class Collection2Container 
extends AContainer {
//implements TODO {

	////////////////////////////////////////////////////////////////////////////////
	//  static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** TODO:	 */
	//final static public  ;
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Methods 
	////////////////////////////////////////////////////////////////////////////////
	
	/** TODO:	 */
	//final static public () ;
	
	////////////////////////////////////////////////////////////////////////////////
	//  Variables 
	////////////////////////////////////////////////////////////////////////////////
	
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		/** Reference to the actual Collection being used!	 */
		protected Collection coll;
	
	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////////
	//  Interface Copy: Implementation
	///////////////////////////////////////////////////////////////////////////////

	/** Creates an uninitalized new Instance of it's class.
	  * This can in VB also be achieved by 'CreateObjectFromInstance',
	  * which may be slower.
	  * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() {
		try {
			return new Collection2Container((Collection)
				coll.getClass().newInstance());
		} catch (Exception x) {
//		} catch (InstantiationException x) {
//		} catch (IllegalAccessException x) {
			throw new InstantiationError(x.toString());
		}
	}

	////////////////////////////////////////////////////////////////////////////
	//  Interface SemiGroup: abstract Methods
	////////////////////////////////////////////////////////////////////////////

	/** Addition in Place: +=
	  * This virtual Operation has to be implemented by each subclass.	 */
//	public abstract SemiGroup addAt(Object arg);// { return this; }

	////////////////////////////////////////////////////////////////////////////////
	//  public Set Methods these could also be added to the Boole Interface!
	////////////////////////////////////////////////////////////////////////////////
	
	/** Increases the capacity of this Array, if necessary, to ensure
	  * that it can hold at least the number of components specified by
	  * the minimum capacity argument.
	  *
	  * @param   minCapacity   the desired minimum Capacity.
	  * @return  the actual Capacity allocated for this Container */
	public int setCapacity(int minCapacity) {
		return 0; }
	
	/** Returns the current minimum capacity of this Array.
	  *
	  * @return  the current capacity of this Array.	 */
	public int getCapacity() {
		return 0; }
	

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() 
	////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor	 */
	protected Collection2Container(Collection coll_) {
		coll = coll_;
		reSet ();
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//  public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**Removes the current Object from the Container with this Enumerator knowing it.
	 * The remaining Problem is other Enumerators that concurrently work through this.
	 * @return this Enumerator to allow for concatenated Adding
	 * @param Object to be added to this Container / Enumerator
	 * @throws ModificationException when this Container is sorted or read only  */
	public IIStreamOut addItem(Object Item) { //throws ModificationException {
		coll.add(Item); return this; } //no guarantee about the Sequence

	/** Removes this Item from the Container
	  * This method does nothing if the Item is not in the HashContainer.
	  * Corresponds to subAt(), but retained, because it also returns Information
	  * whether the Container was changed.
	  * @param   Item   the Item that needs to be removed.
	  * @return the Item, if found, otherwise 'null' resp 'EOI'	 */
	public Object removeItem(Object Item) { //throws ModificationException {
		if (coll.remove(Item))
			return Item;
		return null; }

    /**Replaces the next Object from the streamIO with this Item.
     * It should also update the Minor Version (or let the Container update it)
     * to announce the Change to other Iterators.
	 * This Operation can be used to e.g. influence Parsers concurrently.
	 * This Operation is not supported in sorted Containers,
	 * because the next Item cannot be replaced.
     */
//	public Object replaceNext(Object Item) { // throws ModificationException {
//		throw new ReadOnlyException(Collection2Container.class.getName()); }

	/** Removes the next Object from the Set and Iteration,
	  * returns the removed Item. 
	  * This Behavior makes it necessary to define it separately,
	  * because it returns more Information: whether the Item was found or not!
	  */
//	public Object removeNext() { //throws ModificationException {
//		return mEnum.removeNext(); }

	/** Resets the Iterator to the last marked Position,
	  * done automatically on Instantiation
	  * By Default the Start of the Iterator is marked on Instantiation	 */
	public IReSetAble reSet(){ //throws NoSuchMethodException{
		enm = new Iterator2Enumerator(coll.iterator());
		return this; }

	/** Resets the Iterator to the given Position
	  * counted from the last marked Position.
	  * @return the Number of Positions actually skipped	 */
	public long reSet(long Position) { //throws    NoSuchMethodException {
		reSet (); if (Position == 0) return 0; return jump(Position);  }

	/** @return this, set to the Boolean Constant for the Representation of 'false' = 0
	  * i.e. not 'true'.
	  * For Conatainers this is equivalent to zeroAt() and clear()
	  * @see zeroAt()	 */
	public Boole FalseAt() {
		coll.clear(); 
		return this; }

	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods 
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + Collection2Container.class.getName());
		Container c = new Collection2Container(new java.util.ArrayList());
		c.addAt("Hallo");
		c.addItems("Welt");
		try {
			c.remove("Welt");
		} catch (ModificationException x) {
		}
		System.out.println(c);
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }
	
}
