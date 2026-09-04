package streamIO.copy.group;

//import Stream.Copy.CCopyAble;
import streamIO.exception.ReadOnlyException;

/**Implements Constants for all Types of Group Classes.
 * This Class inhibits the Use of ...At() Routines
 * but still supports all other Methods of the Group Class.	 
 * 
 * TODO: instead of throwing Exceptions, 
 * I should return new Instances (Copy-On-Write!)
 */
public class CGroup
extends CSemiGroup
implements IGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Initializing Constructor
	 * Caches the Result of the Test isZero()	 */
	public CGroup(IGroup cnst){ super(cnst); }

	/**Local Cache for the Result of this Test
	 * This relies on the Compiler processing this Initialization
	 * after the Constructor.	 */
	private final boolean bolIsZero = ((IGroup) inner).isZero();

	/**Subtraction in Place: -=	*/	public IGroup subAt (Object arg){throw new ReadOnlyException(strConst);}
	/**Negation in Place: -=	*/	public IGroup negAt()			{throw new ReadOnlyException(strConst);}
	/**Setting to 0 in Place:	*/	public IGroup zeroAt()			{throw new ReadOnlyException(strConst);}

	/**Setting to 0:			*/	public IGroup zero()			 {return ((IGroup) inner).zero();}
	/**Negation: -				*/	public IGroup neg()			 {return ((IGroup) inner).neg ();}
	/**Subtraction: -			*/	public IGroup sub(Object arg){return ((IGroup) inner).sub(arg);}
	/**Testing for 0:			*/	public boolean isZero()	//	 {return ((Group) inner).isZero();}
																 {return bolIsZero;}

}
