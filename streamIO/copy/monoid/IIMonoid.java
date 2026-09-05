package streamIO.copy.monoid;

/**Concatenative Group (M,�,\,Id):
 * This Interface must be kept completely synchronous to intGroup
 * Defines the Inverse of an Element x^-1, and it's Concatenation,
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:25Z
 * digest: c872a5e30f3b12fe88f9b37a1c3c70da42ee445d18efeebb8cfed149e92297d7
 * stale: false
 * tags: [code/concatenation]
 * concepts: [Monoid, Concatenation]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * x�x^-1 = Id concatenated with results in the Identity.  */
public interface IIMonoid {

	/**Inversion in Place: !=
	 * This virtual Operation has to be implemented by each concrete Subclass.		 */
//	public GroupM revAt();

	/**Right-Concatenation with the Inverse in Place: this�=!arg  this\=arg
	 * This is the Inverse Operation to catAt(), not to map()!
	 * This virtual Operation has to be implemented by each concrete Subclass.		 */
//	public Monoid tacAt (Object arg); // throws InstantiationException;

	/**Mapping / Left-Concat with !arg in Place: !this=�arg */
	public IMonoid pamAt(final Object arg);
	
}
