package streamIO.copy.groupM;

/**multiplicative Group (M,*,/,1):
 * This Interface must be kept completely synchronous to intGroup
 * Defines the most basic Interface necessary for an additive Group: '/='.
 * All other operations are only Shortcuts and can be defined using '/='.
 *
 * Design Decisions:
 * Normally Inversion is sufficient to define anything else as an operation
 * Only for ONE you need any number (except 0) to define it.
 *
 * You can use either Inversion or Division to define the operations.
 * In any Case you better redefine both for Performance.
 * Could also be called 'divisible'
 */
public interface IIGroupM {

	/**Inversion in Place: 1/=
	 * This virtual Operation has to be implemented by each concrete Subclass.		 */
//	public GroupM invAt ();

	/**Division in Place: /=
	 * This virtual Operation has to be implemented by each concrete Subclass.		 */
	public IGroupM divAt (Object arg);

}
