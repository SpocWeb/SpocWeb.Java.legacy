package structure;

/**
 * Models the Singleton Pattern with a static {@link #SINGLETON()} Accessor, meant to be
 * copied per concrete Singleton since the static Method itself cannot be declared in an
 * Interface.
 *
 * Created on 28. Dezember 2000, 14:10
 *
 * @author  Matthias Heuer
 * @version
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:17:16Z
 * digest: 39fced9bad0cc34957d22566dace4cb35a0c46ef5092ddacd69a74631b98239d
 * stale: false
 * tags: [code/singleton_pattern]
 * concepts: [Singleton Pattern]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class Singleton {

	/**Local static Variable to store the single Instance	 */
	protected static Singleton SINGLETON; // = new Singleton();  //for delayed Instantiation

	/**Static Method to access the single Instance
 * <!-- docstate
 * tags: [code/singleton_pattern]
 * concepts: [Singleton Accessor]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
	 * This Method has to be overwritten to instantiate the correct Subclass  */
	public static Singleton SINGLETON() {
		if (SINGLETON == null) SINGLETON = new Singleton(); //for delayed Instantiation
		return SINGLETON; }

	/**Protected Constructor to creates a Singleton
	 *
	 * <!-- docstate
	 * tags: [code/singleton_pattern]
	 * concepts: [Private Constructor]
	 * facets: {layer: utility, status: legacy, complexity: low}
	 * -->
	 */
    protected Singleton () { }

}
