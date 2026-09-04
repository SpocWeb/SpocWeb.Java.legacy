package streamIO.object.enumer.container.util;

import streamIO.Log;

/** Small Test Class that prints it's Name, Number of Runs, the Time and exits  */
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

	/** @return it's Class and Name */
	public String toString() {
		return Name; }
//		return this.getClass().getName() + "." + Name; }

}
