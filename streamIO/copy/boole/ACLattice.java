package streamIO.copy.boole;

import streamIO.copy.CCopyAble;
import streamIO.exception.ReadOnlyException;

/**
  * Abstract base class for constant (read-only) lattice objects; concrete subclasses
  * implement the read-only comparisons while every in-place operation is left abstract.
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
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:45:07Z
  * digest: 3228981694aeb1ea818ad79cb9119bce9a1fa2f593953ded9c92b685b1bed192
  * stale: false
  * tags: [code/lattice_structure, code/abstract_base]
  * concepts: [Lattice]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public abstract class ACLattice
implements Lattice {

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Lattice: abstract Methods
////////////////////////////////////////////////////////////////////////////////

	/**Tests whether this is less than or a subset of arg. @return this <= arg 	*/	public abstract boolean SubEq  (Object arg);
	/**Tests whether this is greater than or a superset of arg. @return this >= arg 	*/	public abstract boolean SuperEq(Object arg);
	/**Tests whether this is a strict subset of arg. @return this <  arg 	*/	public abstract boolean Sub    (Object arg);
	/**Tests whether this is a strict superset of arg. @return this >  arg 	*/	public abstract boolean Super  (Object arg);

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

