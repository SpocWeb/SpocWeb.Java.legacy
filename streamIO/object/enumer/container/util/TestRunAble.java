package streamIO.object.enumer.container.util;

import streamIO.Log;

/** Small Test Class that prints it's Name, Number of Runs, the Time and exits
 *
 * <!-- docstate
 * tags: [code/adapter, code/scheduling]
 * concepts: [Small Adapter and Scheduling Helper Classes]
 * facets: {layer: utility, status: legacy, complexity: high}
 * digest: 3b38cdbe49f9fc0b7ccf1f4730bb148f2d12a65c11cf0120d8a307364776f3d9
 * stale: false
 * -->
 */
class TestRunAble implements Runnable {

	/** Name of this Instance */
	protected String Name;

	/** Counter for this Instance */
	protected int Counter;

	/** Reference to the Logger Class	*/
	protected Log L;

	/** Initializing Constructor */
	public TestRunAble (Log L_, String Name_) {
		this.Name = Name_;
		this.L = L_; }

	/** Dummy Operation, prints it's Name, Number of Runs, the Time and exits */
	public void run() {
		if (L != null)
			L.n(Name + " Runs:" + ++Counter); // + " Time: " + new Date()); //time added by the Logger
//		throw new RuntimeException(); //just for proving that an Exception kills only the current Thread!
	}

	/** Renders this instance's Name.
	 * @return this instance's Name */
	public String toString() {
		return Name; }
//		return this.getClass().getName() + "." + Name; }

}
