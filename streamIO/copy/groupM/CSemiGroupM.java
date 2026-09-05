package streamIO.copy.groupM;

import streamIO.copy.CCopyAble;

/**Implements Constants for all Types of SemiGroupM Classes.
 * This Class inhibits the Use of ...At() Routines
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:25Z
 * digest: cf0842b300c50f76ce57d5648e6e042f5fd654fc11dbc7f069911786378fc5fb
 * stale: false
 * tags: [code/immutable_wrapper, code/delegation]
 * concepts: [Algebraic SemiGroup, Constant/Immutable Wrapper]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * but still supports all other Methods of the SemiGroupM Class.	 */
public class CSemiGroupM
extends CCopyAble
implements ISemiGroupM {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Initializing Constructor		 */	public CSemiGroupM(ISemiGroupM cnst){super(cnst);}

	/**Multiplication in Plc: *= */	public ISemiGroupM mulAt(Object arg)	{ throw new AbstractMethodError(strConst); }
	/**Square in Place: x*=x	*/	public ISemiGroupM sqrAt (		)	{ throw new AbstractMethodError(strConst); }
	/**Cubic in Place: x*=x^2	*/	public ISemiGroupM cbcAt (		)	{ throw new AbstractMethodError(strConst); }
	/**Quad in Place: x^2^2		*/	public ISemiGroupM qadAt (		)	{ throw new AbstractMethodError(strConst); }
	/**Integer Power: x^n		*/	public ISemiGroupM PowAt	(int n	)	{ throw new AbstractMethodError(strConst); }
	/**Raised by an Integer Power of 2 in Place: x^=(2^n)	 */
									public ISemiGroupM Pow2PowAt(int n)	{ throw new AbstractMethodError(strConst); }

	/**Multiplication: *		*/	public ISemiGroupM mul	(Object arg){ return ((ISemiGroupM) inner).mul(arg); }
	/**Square: x^2 == x*x		*/	public ISemiGroupM sqr	(		)	{ return ((ISemiGroupM) inner).sqr(	); }
	/**Cubic: x^3 == (x^2)*=x	*/	public ISemiGroupM cbc	(		)	{ return ((ISemiGroupM) inner).cbc(	); }
	/**Quad: x^4 == (x^2)^2		*/	public ISemiGroupM qad	(		)	{ return ((ISemiGroupM) inner).qad(	); }
	/**Integer Power: x^n	 */		public ISemiGroupM Pow	(int n	)	{ return ((ISemiGroupM) inner).Pow(n	); }

	/**Raised by an Integer Power of 2: x^(2^n)	 */
									public ISemiGroupM Pow2Pow(int n	)	{ return ((ISemiGroupM) inner).Pow2Pow(n); }

}
