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
 */
public class Threading {

	/**
	 * Demonstrates that Thread Synchronization in Java is reentrant (unlike .NET!)
	 * @param i
	 */
	final static public synchronized void recourse(int i) {
		System.out.println("in:"+i);
		if (--i > 0)
			recourse(i); 
		System.out.println("out:"+i); 
	}
	
	public static void main(String[] args) {
		recourse(5); 
	}
}
