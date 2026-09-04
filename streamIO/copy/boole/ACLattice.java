package streamIO.copy.boole;

import streamIO.copy.CCopyAble;
import streamIO.exception.ReadOnlyException;

/**
  * Title: ACLattice<p>
  * Description:
  * Purpose:
  * abstract Base Class for constant Boolean Objects
  *
  * Design Decisions / Implementation Details:
  * @see CLattice, a constant Wrapper Class that also throws ReadOnlyExceptions!
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-15-2002, 10:58 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public abstract class ACLattice
implements Lattice {

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Lattice: abstract Methods
////////////////////////////////////////////////////////////////////////////////

	/** @return this <= arg 	*/	public abstract boolean SubEq  (Object arg);
	/** @return this >= arg 	*/	public abstract boolean SuperEq(Object arg);
	/** @return this <  arg 	*/	public abstract boolean Sub    (Object arg);
	/** @return this >  arg 	*/	public abstract boolean Super  (Object arg);

	/** AND  : &	 */	public abstract Lattice AND (Object arg);
	/** OR   : |	 */	public abstract Lattice OR  (Object arg);
	/** DIFF : -	 */	public abstract Lattice DIFF(Object arg);
	/** XOR  : ^	 */	public abstract Lattice XOR (Object arg);


////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Lattice: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** AND  in Place: &=	 */	public Lattice ANDat	(Object arg) { throw new ReadOnlyException(CCopyAble.strConst); }
	/** OR   in Place: |=	 */	public Lattice ORat	(Object arg) { throw new ReadOnlyException(CCopyAble.strConst); }
	/** DIFF in Place: -=	 */	public Lattice DIFFat	(Object arg) { throw new ReadOnlyException(CCopyAble.strConst); }
	/** XOR  in Place: ^=	 */	public Lattice XORat	(Object arg) { throw new ReadOnlyException(CCopyAble.strConst); }

}

