package synch.property;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**This class can take a variable number of parameters on the command line.
 * Program execution begins with the main() method.
 * The class constructor is not invoked
 * unless an object of type 'testProperty' is created in the main() method.
 *
 * The Observer Interface here is more flexible than what is represented
 * in the java.utils Pair Observer / Observable more like the Event / Listener Pair
 * because you don't need to subclass Observable,
 * but can extend a simple UniCast Object to a MultiCast Object
 * by adding a MultiCaster, if necessary.
 * The Method addObserver
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:32Z
 * digest: a32f6471d3d5bb15d4d766c6c05b666248b80f41d1ac54a3286cc6661af5543a
 * stale: false
 * tags: [code/attached_property]
 * concepts: [Scratch Test Class]
 * facets: {layer: test, status: unfinished, complexity: low}
 * -->
 */
public class testProperty {

	/**
	 * The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.
	 *
	 * Tests all Methods in the Property Package.
	 * <!-- docstate
	 * tags: [code/attached_property]
	 * concepts: [Scratch Test Entry Point]
	 * facets: {layer: test, status: unfinished, complexity: low}
	 * -->
	 */
	public static void main (String[] args) throws IOException {
		Integer i = new Integer(44);
		FileOutputStream FO = new FileOutputStream("C:\\txt.txt");
		ObjectOutputStream OO = new ObjectOutputStream(FO);
		OO.writeObject(i);
//		System.out
	}

}
