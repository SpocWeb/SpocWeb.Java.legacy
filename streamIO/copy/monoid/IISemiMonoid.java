package streamIO.copy.monoid;

/**ISemiMonoid (M,°):
 * This Interface must be kept completely synchronous to ISemiGroup
 * Defines the most basic Interface necessary for a concatenative SemiGroup:'()='
 * All other operations are only Shortcuts and can be defined using '()='.
 * Could also be called 'concatenable' or 'mappable / mapping' */
public interface IISemiMonoid {
	
	/**Mapping / Left-Concat in Place:  this=°arg
     * This Operation doesn't return 'this', but 'arg'!
     * so to concatenate Mappings use B.mapAt(A.mapAt(a))
     * which is more efficient for single Values than B.map(A.map(a))
	 * or B.map(A).map(a) or A.cat(B).map(a)           */
//   public Object       mapAt(Object     arg);

	/**Mapping / Left-Concat in Place:  this=°arg
     * This Operation doesn't modify and return 'this', but 'arg'!!!
     * so to concatenate Mappings use B.mapAt(A.mapAt(a))
     * which is more efficient for single Values than B.map(A.map(a))
	 * or even worse: B.map(A).map(a) or B.mapAt(A).mapAt(a) which works only once!              
	 */
     public ISemiMonoid mapAt(Object arg);

	/**Mapping / Right-Concatenation in Place: this°=arg
	 * This virtual Operation has to be implemented by each subclass.	 */
//	public SemiMonoid catAt(Object arg);

}
