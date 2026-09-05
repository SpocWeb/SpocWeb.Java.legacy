package streamIO.copy.group;

/**HalfGroup (M,+):
 * Defines the most basic Interface necessary for an additive SemiGroup: '+='.
 * The Addition is associative (i.e. (a+b)+c == a+(b+c) )
 * and commutative (i.e. a+b == b+a)
 * All other operations are only Shortcuts and can be defined using '+='.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: b5a39a48b540d4186676e1223b52045cdb499187af69644812a8371f291a6fd6
 * stale: false
 * tags: [code/group_algebra, code/date_time]
 * concepts: [Group/SemiGroup Algebra]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 * Could also be called 'addable' */
public interface IISemiGroup {

	/**Addition in Place: +=
	 * This virtual Operation has to be implemented by each subclass. 
	 * This actually is an Optimization 
	 * allowing the Operation to be performed in Place (i.e. without creating new Objects). 
	 * 2003.11.11: The Implementation may decide that this is not possible 
	 * and instead of copying back the Result into this Object, 
	 * a new Object is handed back! 
	 * Thus the Result of this Operation should always be assigned 
	 * to the original Variable, if it is used for further Processing, like in 
	 * a = a.addAt(b); 
	 * instead of just calling 
	 * a.addAt(b);
	 * This is optimized and works counter to the .NET Implementation, 
	 * which performs a = a+b; when you write a+=b; 
	 */
	public ISemiGroup addAt(Object arg);

}
