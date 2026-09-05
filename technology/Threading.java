/*
 * Created on 12.03.2005
 *
 * Demonstrates that Thread Synchronization in Java is reentrant (unlike .NET!)
 */
package technology;

/**
 * Demonstrates that Thread Synchronization in Java is reentrant (unlike .NET!)
 * @author heuerm
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:09:59Z
 * digest: da405cd27bd46defda54db866a9682ac6f1ac91839bb4651d4015ad1892a9b57
 * stale: false
 * tags: [code/thread_synchronization]
 * concepts: [Recursive Thread Demo]
 * facets: {layer: test, status: legacy, complexity: low}
 * -->
 */
public class Threading {

	/**
	 * Demonstrates that Thread Synchronization in Java is reentrant (unlike .NET!)
	 * @param i
	 * <!-- docstate
	 * tags: [code/thread_synchronization]
	 * concepts: [Recursive Depth Demo]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	final static public synchronized void recourse(int i) {
		System.out.println("in:"+i);
		if (--i > 0)
			recourse(i); 
		System.out.println("out:"+i); 
	}
	
	/**
	 * Runs {@link #recourse(int)} starting at 5 to demonstrate reentrant synchronization.
	 *
	 * @param args unused
	 * <!-- docstate
	 * tags: [code/thread_synchronization]
	 * concepts: [Demo Entry Point]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	public static void main(String[] args) {
		recourse(5);
	}
}
