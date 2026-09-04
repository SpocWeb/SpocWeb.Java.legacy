package structure;

/**Singleton.java
 * This Class is more of a Model for the Singletons,
 * since these are very easy to implement
 * and static Methods like 'Singleton()' can not be defined an Interface.
 *
 * Created on 28. Dezember 2000, 14:10
 *
 * @author  Matthias Heuer
 * @version
 */
public class Singleton {

	/**Local static Variable to store the single Instance	 */
	protected static Singleton SINGLETON; // = new Singleton();  //for delayed Instantiation

	/**Static Method to access the single Instance
	 * This Method has to be overwritten to instantiate the correct Subclass  */
	public static Singleton SINGLETON() {
		if (SINGLETON == null) SINGLETON = new Singleton(); //for delayed Instantiation
		return SINGLETON; }

	/**Protected Constructor to creates a Singleton */
    protected Singleton () { }

}
