package streamIO.copy.groupM;

/**SemiGroupM (M,*):
 * This Interface must be kept completely synchronous to ISemiGroup
 * Defines the most basic Interface necessary for a multiplicative SemiGroup:'*='
 * All other operations are only Shortcuts and can be defined using '*='.
 * Could also be called 'multiplicable' */
public interface IISemiGroupM {

	/**Multiplication in Place: *=
	 * This virtual Operation has to be implemented by each subclass.	 */
	public ISemiGroupM mulAt(Object arg);

}
