package streamIO.copy.boole;

import streamIO.copy.CCopyAble;
import streamIO.exception.ReadOnlyException;

/**
  * Title: CLattice<p>
  * Description:
  * Defines the Interface for a constant Lattice Type.
  * This is especially useful for
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-15-2002, 09:57 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class CLattice
extends CCopyAble
implements Lattice {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**Initializing Constructor		 */	public CLattice(Lattice cnst){ super(cnst); }

	/** @return this <= arg 	*/	public boolean SubEq  (Object arg) { return ((Lattice) inner).SubEq  (arg); }
	/** @return this >= arg 	*/	public boolean SuperEq(Object arg) { return ((Lattice) inner).SuperEq(arg); }
	/** @return this <  arg 	*/	public boolean Sub    (Object arg) { return ((Lattice) inner).Sub    (arg); }
	/** @return this >  arg 	*/	public boolean Super  (Object arg) { return ((Lattice) inner).Super  (arg); }

	/** AND  : &	 */	public Lattice AND	(Object arg) { return ((Lattice) inner).AND (arg); }
	/** OR   : |	 */	public Lattice OR 	(Object arg) { return ((Lattice) inner).OR  (arg); }
	/** DIFF : -	 */	public Lattice DIFF(Object arg) { return ((Lattice) inner).DIFF(arg); }
	/** XOR  : ^	 */	public Lattice XOR	(Object arg) { return ((Lattice) inner).XOR (arg); }

	/** AND  in Place: &=	 */	public Lattice ANDat	(Object arg) { throw new ReadOnlyException(strConst); }
	/** OR   in Place: |=	 */	public Lattice ORat	(Object arg) { throw new ReadOnlyException(strConst); }
	/** DIFF in Place: -=	 */	public Lattice DIFFat	(Object arg) { throw new ReadOnlyException(strConst); }
	/** XOR  in Place: ^=	 */	public Lattice XORat	(Object arg) { throw new ReadOnlyException(strConst); }

}

