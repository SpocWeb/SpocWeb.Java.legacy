package streamIO.copy.groupM;

/**Implements Constants for all Types of GroupM Classes.
 * This Class inhibits the Use of ...At() Routines
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:25Z
 * digest: 655248ffb57ce6e21800f8796283987ba61c4301cc7fa72ecfa1bc0a43604296
 * stale: false
 * tags: [code/immutable_wrapper, code/delegation]
 * concepts: [Algebraic Group, Constant/Immutable Wrapper]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * but still supports all other Methods of the GroupM Class.	 */
public class CGroupM
extends CSemiGroupM
implements IGroupM {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**Local Cache for the Result of this Test	 */
	private boolean bolIsOne;

	/**Initializing Constructor
	 * Caches the Result of the Test isZero()	 */
	public CGroupM(IGroupM cnst) { super(cnst); bolIsOne = ((IGroupM) inner).isOne(); }

	/**Division in Place: /=	 */	public IGroupM divAt (Object arg){ throw new AbstractMethodError(strConst); }
	/**Inversion in Place: 1/x	 */	public IGroupM invAt()			{ throw new AbstractMethodError(strConst); }
	/**Setting to  1 in Place:	 */	public IGroupM oneAt()			{ throw new AbstractMethodError(strConst); }

	/**Division: /	 */				public IGroupM div(Object arg)	{ return ((IGroupM) inner).div(arg); }
	/**Inversion:  1/x	 */			public IGroupM inv()				{ return ((IGroupM) inner).inv(); }
	/**Setting to  1:	 */			public IGroupM one()				{ return ((IGroupM) inner).one(); }
	/**Testing for 1:	 */			public boolean isOne()			{ return bolIsOne; }
}
