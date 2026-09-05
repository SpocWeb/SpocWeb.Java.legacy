package streamIO.copy.group.ring;

import streamIO.copy.group.CGroup;
import streamIO.copy.groupM.ISemiGroupM;
import streamIO.exception.ReadOnlyException;

/**Implements Constants for all Types of Ring Classes.
 * This Class inhibits the Use of ...At() Routines
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: b85799f343ff74d6eb45c04cee2f58d9fed2a55c10893c662039e6d4950463d7
 * stale: false
 * tags: [code/ring_theory, code/ode_solver]
 * concepts: [Ring Algebra and ODE Solvers]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 * but still supports all other Methods of the Ring Class.	 */
public class CRing
extends CGroup
implements IRing {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Initializing Constructor	 */	public CRing(IRing cnst){super(cnst);}

	//////////////////////////////
	//	SemiGroupM Interface	//
	//////////////////////////////

	/**Multiplication in Plc: *= */	public ISemiGroupM mulAt(Object arg)	{throw new ReadOnlyException(strConst);}
	/**Square in Place: x*=x	*/	public ISemiGroupM sqrAt (		)	{throw new ReadOnlyException(strConst);}
	/**Cubic in Place: x*=x^2	*/	public ISemiGroupM cbcAt (		)	{throw new ReadOnlyException(strConst);}
	/**Quad in Place: x^2^2		*/	public ISemiGroupM qadAt (		)	{throw new ReadOnlyException(strConst);}

	/**Integer Power: x^n	 */		public ISemiGroupM PowAt	(int n	)	{throw new ReadOnlyException(strConst);}
	/**Raised by an Integer Power of 2 in Place: x^=(2^n)	 */
									public ISemiGroupM Pow2PowAt(int n)	{throw new ReadOnlyException(strConst);}

	/**Multiplication: *		*/	public ISemiGroupM mul	(Object arg){return ((ISemiGroupM) inner).mul(arg);}
	/**Square: x^2 == x*x		*/	public ISemiGroupM sqr	(		)	{return ((ISemiGroupM) inner).sqr(	);}
	/**Cubic: x^3 == (x^2)*=x	*/	public ISemiGroupM cbc	(		)	{return ((ISemiGroupM) inner).cbc(	);}
	/**Quad: x^4 == (x^2)^2		*/	public ISemiGroupM qad	(		)	{return ((ISemiGroupM) inner).qad(	);}

	/**Integer Power: x^n	 */		public ISemiGroupM Pow	(int n	)	{return ((ISemiGroupM) inner).Pow(n	);}

	/**Raised by an Integer Power of 2: x^(2^n)	 */
									public ISemiGroupM Pow2Pow(int n	)	{return ((ISemiGroupM) inner).Pow2Pow(n);}

	//////////////////////
	//	New Operations:	//
	//////////////////////

	/**Linear Mapping in Place: x*=a + y	 */
	public IRing LinAt (Object a, Object y)				{throw new ReadOnlyException(strConst);}

	/**Bilinear Mapping in Place: x*=a + y*b	 */
	public IRing BiLinAt (Object a, Object y, Object b)	{throw new ReadOnlyException(strConst);}

	/**  Linear Mapping in Place: x+=a * y	*/
	public IRing addProdAt (Object a, Object y)			{throw new ReadOnlyException(strConst);}

	/**  Linear Mapping in Place: x-=a * y	*/
	public IRing subtProdAt (Object a, Object y)			{throw new ReadOnlyException(strConst);}


	//////////////////
	//	Delegation	//
	//////////////////

	/**  Linear Mapping: x + a*y			*/
	public IRing addProd   (Object a, Object y)	{return ((IRing)inner).addProd(a, y);}

	/**  Linear Mapping: x - a*y			*/
	public IRing subtProd   (Object a, Object y)	{return ((IRing)inner).subtProd(a, y);}

	/**Linear Mapping: x*a + y
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IRing Lin (Object a, Object y)		{return ((IRing)inner).Lin(a, y);}

	/**Bilinear Mapping: x*a + y*b
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IRing BiLin (Object a, Object y, Object b)	{return ((IRing)inner).BiLin(a, y, b);}

}
