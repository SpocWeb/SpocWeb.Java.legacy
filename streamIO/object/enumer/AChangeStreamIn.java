package streamIO.object.enumer; //TODO: always define a Package

import java.io.IOException;

import streamIO.object.AStreamIn;

/**
  * Title: AModStreamIn.java<p>
  * Description:
  * TODO: Describes the Purpose / Responsibilities of this Class, not it's Implementation.
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-05, 10;03;23<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * @stereotype enumeration
  */
public abstract class AChangeStreamIn
extends AStreamIn
implements ChangeIterator {
	
	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/**Minor Version of the Container. At Creation a fast-fail Iterator should read this.
	 * and throw a ConcurrentModificationException instead of synchronizing
	 * all the Container Methods and thus blocking the Iterators.
	 * Must be updated on any Change of the Container to trigger fast-fail Enumerators.
	 * Can also be used to keep the Version if this is a Container.
	 * Counts the Number of simple Data Changes of this Container
	 */
	protected int minor;
	
	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////
	
	/** @return the current Version of the Container to support fast-fail Enumerators
	  * Should be incremented on each change of the Container
	  * and checked for the same Value on each Call of nextItem() or currItem()
	  * to warn the User (Client) of the Enumerator.
	  * Using int should be relatively safe,
	  * because Containers will at most contain about |int| Elements.
	  * Calling this Method additionally to nextItem is quite expensive,
	  * so the Enumerator should try to access the Field directly.
	  */
	final public int getMinor() { return minor; } //return 0; }
	
	/** @return the incremented current Version of the Container
	  * to indicate Modification to fast-fail Iterators.
	  * The Version should be incremented on each change of the Container
	  * and checked for the same Value on each Call of nextItem() or currItem()
	  * to warn the User (Client) of the Iterator.
	  * Using int should be large enough,
	  * because Containers will at most contain about |int| Elements.
	  */
	final public int incMinor() { return ++minor; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/**Constructor setting the current Version of the Container
	 * This "Version" must be checked on any nextItem() Operation.
	 * The currItem() Operation does not go back to the Container.
	 * @param _container a versioned container backing this Enumerator. Null allowed
	 */
	public AChangeStreamIn(final IChangeAble _container) { 
		this.resetVersion(_container); 
	}
	
	/** intended to reset the Version on versioned iterators 	 */
	protected void resetVersion(final IChangeAble _container) { 
		//super.resetVersion(_container);
		if (_container != null)
			minor = _container.getMinor(); 
	} 
		
	/** Empty Constructor	 */
	protected AChangeStreamIn() { }
	
	/** @return a new ChangeIterator with the same Position	 */
	public ChangeIterator ChangeIterator() {
		try { return (ChangeIterator) this.clone(); }
		catch (CloneNotSupportedException x) { return null; } } //throw new CloneNotSupportedError(x.toString); } }
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface IterAble: abstract Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** Returns an Iterator of the components in this Container.
	 *
	 * @return  an Enumerator of the components in this Container.
	 * @see     Math.Enumerator      */
	//public abstract IStreamIn Iterator();
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface ChangeAble: abstract Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** Returns a new Intstance of a ModStreamIn Iterator,
	  * which allows for changing the Data concurrently. */
	//public abstract ChangeStreamIn ChangeIterator();
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface ChangeStreamIn: abstract Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** Replaces the current Object in the Container with the given Item.
	  * One Problem is other Enumerators that concurrently work through this Container.
	  * Another Problem is that removing the Item may not be possible at all.
	  * In this Case the Exception is thrown.
	  * That is why this Method should throw an Exception if replacing is not allowed.
	  * It should also update the Minor Version (or let the Container update it)
	  * to announce the Change to other Iterators.
	  */
	//public abstract Object replaceCurr(Object Item)  throws    NoSuchMethodException;// {
	//    										throw new NoSuchMethodException(); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws IOException {
		System.out.println("Testing " + AChangeStreamIn.class.getName());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws IOException {
		testIt(args); }
	
}
