package streamIO.copy.group;

import streamIO.copy.CCopyAble;
import streamIO.exception.ReadOnlyException;

/**Implements Constants for all Types of SemiGroup Classes.
 * This Class inhibits the Use of ...At() Routines
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 4f761f1e2b37b9a68164d28d94d9ac37b89570a96c9cc796e2d7beda08630ea1
 * stale: false
 * tags: [code/group_algebra, code/date_time]
 * concepts: [Group/SemiGroup Algebra]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 * but still supports all other Methods of the SemiGroup Class.	 */
public class CSemiGroup
extends CCopyAble
implements ISemiGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**Initializing Constructor		 */	public CSemiGroup(ISemiGroup cnst){ super(cnst); }

	/**Addition in Place: +=		 */	public ISemiGroup addAt(Object arg)	{throw new ReadOnlyException(strConst);}
	/**Double in Place: x+=x		 */	public ISemiGroup dblAt ()			{throw new ReadOnlyException(strConst);}
	/**Triple in Place: x+=2x		 */	public ISemiGroup trplAt()			{throw new ReadOnlyException(strConst);}
	/**Quadruple in Place: 2(2x)	 */	public ISemiGroup quadAt()			{throw new ReadOnlyException(strConst);}
	/**Integer Multiplication: x*=n	 */	public ISemiGroup mulAt (int n)		{throw new ReadOnlyException(strConst);}
	/**Multiplication with an Integer Power of 2 in Place:	 */
										public ISemiGroup mul2PowAt(int n)	{throw new ReadOnlyException(strConst);}


	/**Addition: +					*/	public ISemiGroup add (Object arg){return ((ISemiGroup) inner).add(arg);}
	/**Double:   2x == x+x			*/	public ISemiGroup dbl (			){return ((ISemiGroup) inner).dbl(	  );}
	/**Triple: 3x == (2x)+=x		*/	public ISemiGroup trpl(			){return ((ISemiGroup) inner).trpl(  );}
	/**Quadruple: 4x == 2(2x)		*/	public ISemiGroup quad(			){return ((ISemiGroup) inner).quad(  );}
	/**Integer Multiplication: x* n	 */	public ISemiGroup mul (int n		){return ((ISemiGroup) inner).mul(  n);}
	/**Multiplication with an Integer Power of 2:	 */
										public ISemiGroup mul2Pow  (int n){return ((ISemiGroup) inner).mul2Pow(  n);}

}
