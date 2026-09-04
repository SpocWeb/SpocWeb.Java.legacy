package streamIO.copy.group.ring;

import streamIO.copy.group.IGroup;
import streamIO.copy.groupM.ISemiGroupM;

/**Default Implementation of the Algebraic Ring (M,+,-,0,*):
 * Set of Objects with inner Operations +,-,* , where
 * 1) (M,+,-,0) form a commutative Group
 * 2) (M,*) form a SemiGroup
 * 3) and the Distributive Laws apply: a*(b+c)=a*b+a*b und (a+b)*c =a*c+b*c
 *
 * It can be proved that...
 * a=0 v b=0 => a*b=0 (from distributive Laws)
 *
 * There are several Examples for Pairs of Operations that fulfill the Ring:
 * (R,+,0,*,1)
 * ([a,b], Max, a, Min, b) (forms a boolean Lattice)
 * ({0,1}, OR , 0, AND, 1)
 * ([0,1], °, 0, *, 1) with the "probabilistic Sum" a°b = a+b-a*b does not quite commute
 * only if one of the Arguments is really 0 or 1.
 * (V, +, 0, *, 1)  with a VectorSpace V and the (non-commutative) linear Mapping °
 * 
 * A Ring with a commutative Multiplication is called a 'Field' 
 * i.e. (M-{0],*, 1) is a commutative Group.  
 */
public interface IRing
extends IGroup, ISemiGroupM {

	/**  Linear Mapping in Place: x*=a + y	*/	public IRing LinAt		(Object a, Object y);
	/**  Linear Mapping in Place: x+=a * y	*/	public IRing addProdAt	(Object a, Object y);
	/**  Linear Mapping in Place: x-=a * y	*/	public IRing subtProdAt	(Object a, Object y);
	/**BiLinear Mapping in Place: x*=a + y*b*/	public IRing BiLinAt		(Object a, Object y, Object b);
	/**  Linear Mapping: x * a + y			*/	public IRing Lin			(Object a, Object y);
	/**  Linear Mapping: x + a * y			*/	public IRing addProd		(Object a, Object y);
	/**  Linear Mapping: x - a * y			*/	public IRing subtProd	(Object a, Object y);
	/**BiLinear Mapping: x * a + y * b		*/	public IRing BiLin		(Object a, Object y, Object b);

	//////////////////////////////////////////
	//	Mapping for a Ring as an Argument:	//
	//////////////////////////////////////////

	//did not make sense: Java did not resolve the Ambiguity of Arguments
	//by using proximity in the inheritance tree!
	//So one would have to cast the arguments to Ring again!
	//Also the Methods without Arguments cannot be changed

	//////////////////////
	//	Optimizations	//
	//////////////////////

	//did not make sense: Java did not resolve the Ambiguity of Arguments
	//by using proximity in the inheritance tree!
	//Instead it threw the following Compile Error:
	//"Ambiguity between 'SemiGroupM gAdic.mulAt(Object)' and 'Ring ARing.mulAt(Ring)'"

	/**Addition in Place: +=	 */
//	public Ring addAt (Ring arg);

	/**Addition: +	 */
//	public Ring add	(Ring arg);


	/**Subtraction in Place: -=	 */
//	public Ring subAt (Ring arg);

	/**Subtraction: -	 */
//	public Ring subt (Ring arg);


	/**Multiplication in Place: *=	 */
//	public Ring mulAt (Ring arg);

	/**Multiplication: *	 */
//	public Ring mul (Ring arg);

}
