package streamIO.copy.monoid;

/**Concatenative Group (M,°,\,Id):
 * This Interface must be kept completely synchronous to intGroup
 * Defines the Inverse of an Element x^-1, and it's Concatenation,
 * x°x^-1 = Id concatenated with results in the Identity.  */
public interface IIMonoid {

	/**Inversion in Place: !=
	 * This virtual Operation has to be implemented by each concrete Subclass.		 */
//	public GroupM revAt();

	/**Right-Concatenation with the Inverse in Place: this°=!arg  this\=arg
	 * This is the Inverse Operation to catAt(), not to map()!
	 * This virtual Operation has to be implemented by each concrete Subclass.		 */
//	public Monoid tacAt (Object arg); // throws InstantiationException;

	/**Mapping / Left-Concat with !arg in Place: !this=°arg */
	public IMonoid pamAt(final Object arg);
	
}
