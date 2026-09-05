package streamIO.copy.groupM;

/**SemiGroupM (M,*):
 * This Interface must be kept completely synchronous to ISemiGroup
 * Defines the most basic Interface necessary for a multiplicative SemiGroup:'*='
 * All other operations are only Shortcuts and can be defined using '*='.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:25Z
 * digest: 527da231265d1ba61089cb6ea514df521d7f2c9d0b6eb556a3dac6ad5281c584
 * stale: false
 * tags: [code/multiplicative_semigroup]
 * concepts: [Algebraic SemiGroup]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * Could also be called 'multiplicable' */
public interface IISemiGroupM {

	/**Multiplication in Place: *=
	 * This virtual Operation has to be implemented by each subclass.	 */
	public ISemiGroupM mulAt(Object arg);

}
