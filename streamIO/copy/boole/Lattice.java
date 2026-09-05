package streamIO.copy.boole;

import streamIO.copy.ICopyAble;

/**Interface for a Lattice (so far without NOT and 0 resp. 1)
 * But already here you can define the 'SubSet' Relation, an Order Relation using
 *
 * a 'less' b <=> a Sub	  b <=> a AND b == a
 * a 'grtr' b <=> a Super b <=> a OR  b == b
 *
 * The Definition of AND and OR based on an Order Relation is done in 'MinMaxLattice'
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 6ca5e3beee3e2edafa97a0500620d55bec7e0852a18857611619c04b15937a89
 * stale: false
 * tags: [code/lattice_structure]
 * concepts: [Lattice]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public interface Lattice
extends ILattice, ICopyAble
{

	/**Boolean AND Operation: &
	 * For Sets: Intersection	*/
	Lattice AND  (Object arg);

	/**Boolean OR Operation: |
	 * For Sets: Union
	 * Compare this to the '+' Operation unifying disjoint Sets.	*/
	Lattice OR   (Object arg);

	/**Boolean DIFF Operation: -
	 * a - b <=> (a AND NOT b) <=> NOT IMP
	 * For Sets:	Difference Set ; can also be defined without NOT!  */
	Lattice DIFF (Object arg);

	/**Boolean XOR Operation: ^
	 * a XOR b = true <=> (a AND ~b) OR (~a AND b) <=> NOT(a EQV b) <=> (a-b) OR (b-a)
	 * For Sets: Gives Set of all Elements, that are either in one or
	 * (exclusively) in the other	*/
	Lattice XOR  (Object arg);

	/**Boolean XOR Operation in Place: ^=
	 * a XOR b <=> (a AND NOT b) OR (NOT a AND b)
	 * For Sets: Gives Set of all Elements, that are either in one or the other 	*/
	Lattice XORat(Object arg);

	/**Determines, whether 'this' is less than or a SubSet of arg
	 * This is a real Order Relation and could also be called 'less'	*/
	boolean SubEq(Object arg);

	/**Determines, whether 'this' is more than or a SuperSet of arg
	 * This is a real Order Relation and could also be called 'grtr'	*/
	boolean SuperEq(Object arg);

	/**Determines, whether 'this' is less than or a real SubSet of arg
	 * This is a real Order Relation and could also be called 'less'	*/
	boolean Sub(Object arg);

	/**Determines, whether 'this' is more than or a real SuperSet of arg
	 * This is a real Order Relation and could also be called 'grtr'	*/
	boolean Super(Object arg);

}
