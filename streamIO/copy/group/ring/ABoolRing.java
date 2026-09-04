package streamIO.copy.group.ring; //.Container;

import streamIO.copy.ICopyAble;
import streamIO.copy.boole.Boole;
import streamIO.copy.boole.Lattice;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ISemiGroup;
import streamIO.copy.groupM.ISemiGroupM;
import streamIO.object.AStreamSet;
import streamIO.object.enumer.container.AContainer;

/**
  * Title: ABoolRing.java<p>
  * Description:
  * Abstracts the independent Functionality of Rings and Boolean Groups
  * used by Containers and Streams like StreamSet.
  * It allows arithmetic AND Set Operations on thus ICountAble discrete Sets.
  *
  * Known SubClasses:
  * @see AStreamSet
  * @see AContainer
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-12, 12;14;36<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public abstract class ABoolRing 
extends ARing //ABoole // ALattice //
implements BoolRing { //Boole

	////////////////////////////////////////////////////////////////////////////////
	//  Interface ILattice: abstract Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/**Boolean AND Operation in Place: &=, &&= for single Bit	*/
	public abstract Lattice ANDat (Object arg);

	/**Boolean OR Operation in Place: |=, ||= for single Bit	*/
	public abstract Lattice ORat  (Object arg);

	////////////////////////////////////////////////////////////////////////////////
	//  Interface IGroup : abstract Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/**Addition in Place: += 	 */
	public abstract ISemiGroup addAt(Object arg);

	/** Subtraction in Place: -= 	 */
	public IGroup subAt (Object arg) {
        DIFFat(arg);
        return this; }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface IBoole: abstract Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Boolean NOT Operation in Place: ~=, != for single Bit	*/
	public abstract Boole NOTat	();

	/** Boolean Constant for the Representation of 'false' = 0
	  * i.e. not 'true'.
	  * For Conatainers this is equivalent to zeroAt() and clear()	 */
	public abstract Boole FalseAt();

	/**Boolean Constant for the Representation of 'true': 1
	 * i.e. NOT 'false'.
	 * For Sets: The Set of ALL Elements (only posssible for known finite SuperSets) */
	public abstract Boole TrueAt(); // { return ((Boole) this).FalseAt().NOTat(); }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface IGroupM: abstract Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Multiplication in Place: *=  	 */
	public abstract ISemiGroupM mulAt(Object arg);

	////////////////////////////////////////////////////////////////////////////////
	//  Interface ICopy: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itthis for further use.
	 * When overriding, use copyAt on all Components.	 */
	public ICopyAble copyAt(Object arg, int Depth) { throw new AbstractMethodError(); }

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() { throw new AbstractMethodError(); }

	/**Fills this Instance with the Contents read from the String.	 */
//	public abstract CopyAble fromStreamAt(StreamTokenizer arg);

	////////////////////////////////////////////////////////////////////////////
	//  Interface Lattice: Implementation
	////////////////////////////////////////////////////////////////////////////

	/**Boolean AND Operation: &, && for single Bit	*/
	public Lattice AND (Object arg){ return ((Lattice)this.copy()). ANDat(arg); }

	/**Boolean OR Operation: |, || for single Bit	*/
	public Lattice OR  (Object arg){ return ((Lattice)this.copy()).  ORat(arg); }

	/**Boolean DIFF Operation: -
	 * a - b <=> (a AND NOT b) <=> NOT IMP
	 * For Sets:	Difference Set ; can also be defined without NOT!  */
	public Lattice DIFF(Object arg) { return ((Lattice)this.copy()).DIFFat(arg); }

	/**Boolean XOR Operation: ^
	 * a XOR b = true <=> (a AND ~b) OR (~a AND b) <=> NOT(a EQV b) <=> (a-b) OR (b-a)
	 * For Sets: Gives Set of all Elements, that are either in one or
	 * (exclusively) in the other	*/
	public Lattice XOR  (Object arg) { return this.DIFF(arg).OR(((Lattice)arg).DIFF(this)); }

	/**Boolean XOR Operation in Place: ^=
	 * a XOR b <=> (a AND NOT b) OR (NOT a AND b)
	 * For Sets: Gives Set of all Elements, that are either in one or the other 	*/
	public Lattice XORat(Object arg) { this.shallowCopyAt(this.XOR(arg)); return this; }

	///////////////////////////////////////////////////////////////////////////////
	//  Interface ILattice: Implementation
	///////////////////////////////////////////////////////////////////////////////

	/** Determines, whether 'this' is less than or a SubSet of arg
	  * This is equivalent to the Boole.IMP Operation and to the contains() Function
	  * @param arg Object to be checked to be "smaller" or a "SubSet" of this
	  * @return true if arg is a "SubSet" or "smaller" than this. */
	public boolean SubEq(Object arg) { return this.equals(this.AND(arg)); } //not well apted for Streams!

	/** Determines, whether 'this' is less than or a real SubSet of arg
	  * This is equivalent to the Boole.IMP Operation. 	*/
	public boolean Sub  (Object arg) {
		return 	 ((Lattice) this).SubEq(arg ) &&
				!((Lattice) arg ).SubEq(this); } //better for Streams
//				!self.equals(arg); } //better for Scalars

	/** Determines, whether 'this' is equal or equivalent to arg
	  * This is a recursive Definition and must be coordinated with the HashCode Function.
	  * @param arg An Object to be compared with this one
	  * @return true when both Objects are equivalent	 */
	public boolean equals(final Object arg) {
		if (arg == this)
			return true; 
		return equals((Lattice)arg); } //better for Streams

	/** Determines, whether 'this' is equal or equivalent to arg
	  * This is a recursive Definition and must be coordinated with the HashCode Function.
	  * @param arg An Object to be compared with this one
	  * @return true when both Objects are equivalent	 */
	public boolean equals(final Lattice arg) {
		return 	this.SubEq(arg) && arg.SubEq(this); } //better for Streams

	/**Determines, whether 'this' is more than or a SuperSet of arg	*/
	public boolean SuperEq(Object arg) { return ((Boole)arg).SubEq(this); }

	/**Determines, whether 'this' is more than or a real SuperSet of arg	*/
	public boolean Super  (Object arg) { return ((Boole)arg).Sub(this); }

	/**Boolean DIFF Operation: -
	 * a - b <=> (a AND NOT b) <=> NOT IMP
	 * For Sets:	Difference Set, can also be defined as a fundamental Operation! */
//	public Lattice DIFF	(Object arg) { return (Boole) this.AND(((Boole)arg).NOT()); }

	/** Boolean IMP Operation: =>
	  * a => b == ~a | (a & b) == ~a | b
	  * This is equivalent to the Lattice.SubEq Test. 	*/
	public Boole IMP	(Object arg){
		return  (Boole) ((Boole) this).NOT().ORat(arg); }

	/**Boolean EQV Operation: <=>
	 * a <=> b == ~(a ^ b) */
	public Boole EQV	(Object arg) { return ((Boole) this.XOR(arg)).NOTat(); }

	///////////////////////////////////////////////////////////////////////////////
	//  Implementation Boole: Delegation
	///////////////////////////////////////////////////////////////////////////////

	/**Boolean Constant for the Representation of 'false' = 0	*/
	public Boole False() { return ((Boole)this.newInstance()).FalseAt(); }

	/**Boolean Constant for the Representation of 'true': 1		*/
	public Boole True () { return ((Boole)this.newInstance()).TrueAt(); }

	/**Returns true, when 'this' is False, or an empty Set	 */
	public boolean isFalse() { return this.equals(((Boole) this).False()); }

	/**Returns true, when 'this' is True, or a full Set	 */
	public boolean isTrue() { return this.equals(((Boole) this).True()); }

	/** Boolean NOT Operation: ~, ! for single Bit
	  * NOT a <=> TRUE - a  	*/
	public Boole NOT  () {
//		return ((Boole)this .copy()).NOTat(); }
		return (Boole) True().DIFFat(this); }	//second Possibility!

	//In Place Boole Operations

	/**Boolean DIFF Operation in Place: -=
	 * a - b <=> (a AND NOT b) <=> NOT IMP
	 * For Sets:	Difference Set*/
	public Lattice DIFFat	(Object arg) { ANDat(((Boole)arg).NOT()); return this; }

	/**Boolean IMP Operation in Place: =>
	 * a IMP b = true <=> (a = true) => (b = true)*/
	public Boole IMPat	(Object arg) {
        NOTat().ORat(arg); return this; }
//		return (Boole)shallowCopyAt(IMP(arg)); }

	/**Boolean EQV Operation in Place: <=>
	 * a EQV b = true <=> (a = b)*/
	public Boole EQVat	(final Object arg) {
		shallowCopyAt(EQV(arg)); return this; }

	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt() throws Exception {
		System.out.println("Testing " + ABoolRing.class.getName());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		testIt(); }
	
}
