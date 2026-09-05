package streamIO.object.enumer.container;

import java.security.InvalidParameterException;

import streamIO.IIStreamIn;
import streamIO.IIStreamOut;
import streamIO.copy.ICopyAble;
import streamIO.exception.ReadOnlyException;
import streamIO.object.ModificationException;

/**
  * This Container Class supports Debit Operations
  * and thus avoids possible Synchronization Problems in Containers,
  * where Items are removed before they are being added.
  * This Implementation is for illustrative Purposes only; 
  * rather use the Bag Class (see below)
  *
  * It also models Lending out Objects to different Entities.
  * It extends the Container/Bag (not the Set!) Idea
  * from only positive Numbers / Assets / Credit to Liabilities / Debit, 
  * by maintaining both Credit and Debit Side separately and thus "caching" Subtractions
  * until they can be safely performed, returning positive Integers.
  * This reflects the Invention of Bookkeeping and negative Numbers,
  * because in the End only the Assets are real and can be used!
  * The Rest are so-called Privativa, 
  * i.e. Things that don't exist in the Sense of Matter, 
  * but as a (human) Convention of Thinking or Configuration 
  * like 'hole' or 'debit' or, for that case, even 'ownership'/'credit'. 
  *
  * It thus works analogous to the Fraction class,
  * which extends Integer Numbers to Fractions
  * by maintaining both Numerator and Denominator and thus "caching" Divisions
  * until they can be safely performed, returning Integers.
  *
  * Set Operations really don't make very much sense here anymore!
  * |a  OR  b| <= |a| + |b|
  * |a DIFF b| >= |a| - |b|
  *
  * set/getCapacity and the Enumeration only works on the Credit Part
  * To work on the Debit Side, you have to get it directly.
  *
  * TODO: maybe derive this from ARing instead of AContainer.
  *
  * @see streamIO.object.enumer.container.Bag
  * It is similar to the Bag Class which is also capable of storing Debits, 
  * AND more effective (needs only a single Lookup)! 
  *
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public class DebitContainer
extends AContainer {

	////////////////////////////////////////////////////////////////////////////
	//  Member Variables
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Reference to the Debit Container collecting all Liabilities 	*/
	protected Container mDebit;

	/** Reference to the Credit Container collecting all Assets 	*/
	protected Container mCredit;

	////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Constructor taking the two Containers to subtract from each other.
	  * This is the typical Closure of a Mathematical Operation.
	  */
	public DebitContainer (Container Credit, Container Debit) {
		mCredit = Credit;
		mDebit  = Debit;
		enm = Credit.Enumerator(); }

	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////
	
	/** Returns the Container of not-yet-settled Liabilities.
	 * @return the Debit Container collecting all Liabilities 	*/
	public Container getDebit () { return mDebit; }

	/** Returns the Container of confirmed Assets.
	 * @return the Credit Container collecting all Assets 	*/
	public Container getCredit() { return mCredit; }

	/** Increases the capacity of this Array, if necessary, to ensure
	  * that it can hold at least the number of components specified by
	  * the minimum capacity argument.
	  *
	  * @param   minCapacity   the desired minimum Capacity.
	  * @return  the actual Capacity allocated for this Container */
	public int setCapacity(int minCapacity) {
		return mCredit.setCapacity(minCapacity); }

	/** Delegates to the Credit side's Capacity; the Debit side is not reported here.
	 * @return  the minimum current Capacity of this Container.	 */
	public int getCapacity() {
		return mCredit.getCapacity(); }

	////////////////////////////////////////////////////////////////////////////
	//  Interface CopyAble: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/** adds this Item to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
	  * before adding the Item to the Credit Side...
	  * ...first check whether it is missing on the Debit Side
	  * ...this corresponds to normalizing Fractions by shortening them
	  * since the Items are handled individually
	  * handling is slow anyway, but shortening incurs only small Overhead
	  * @throws java.security.InvalidParameterException when 'null' is added,
	  * because this really makes no sense.
	  * @see Order()
	  * @see nextItem()	 */
	public IIStreamOut addItem(Object arg) {
		try {
			if (arg == null) {
				throw new InvalidParameterException("'null' is not allowed!"); }
			++itemCount; //always works!
			if (IIStreamIn.EOI !=  mDebit.removeItem(arg)) { return this; }
								 mCredit.   addItem(arg);   return this; }
		catch (ModificationException x) { throw new ReadOnlyException(x.toString()); } }

	/** Removes this Item from the Container
	  * This method does nothing if the Item is not in the HashContainer.
	  * Corresponds to subAt(), but retained, because it also returns Information
	  * whether the Container was changed.
	  * @param   Item   the Item that needs to be removed.
	  * @return the Item, if found, otherwise 'null' resp 'EOI'	 */
	public Object removeItem(Object arg) { //throws ModificationException {
		Object ret;
		try {
			if (arg == null) {
				throw new InvalidParameterException("'null' is not allowed!"); }
			--itemCount; //always works!
			if (IIStreamIn.EOI != (ret = mCredit.removeItem(arg))){ return ret ; }
								        mDebit.    addItem(arg);   return null; }
		catch (ModificationException x) { throw new ReadOnlyException(x.toString()); } }

	/** Creates a new, empty DebitContainer with fresh Credit and Debit Containers of the
	 * same concrete Types as this one's.
	 * @return a new Instance of this Class	 */
	public ICopyAble newInstance() {
		return new DebitContainer (
			(Container) mCredit.newInstance(),
			(Container) mDebit .newInstance());
	}

	/** Renders both the Credit and Debit sides, e.g. "({credit} - {debit})".
	  * @return a String Representation of this Object.
	  * to indicate nested Brackets, different Types of Brackets can be used
	  */
	public String toString() {
		return "({" + mCredit.toString() + "} - {" + mDebit.toString() + "})"; }

}
