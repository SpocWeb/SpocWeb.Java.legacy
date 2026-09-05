package tester.process;

import java.io.IOException;

/**
 * testProcess.java
 *
 * Created on 21. Februar 2001, 10:33
 *
 * @author  Matthias Heuer
 * @version
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:13:41Z
 * digest: 5b372aa3f3514d62662fed9c5b93d97a09ed9cca8faa6d579637af2945e4cd3f
 * stale: false
 * tags: [code/state_machine]
 * concepts: [Scratch Test Class]
 * facets: {layer: test, status: legacy, complexity: low}
 * -->
 */
public class testProcess extends Object {

    /** Runs {@link IOEProcess#testIt()} as this application's entry point.
    * @param args the command line arguments
    * <!-- docstate
    * tags: [code/state_machine]
    * concepts: [Scratch Test Entry Point]
    * facets: {layer: test, status: legacy, complexity: low}
    * -->
    */
    public static void main (String[] args) throws IOException, InterruptedException {
		IOEProcess.testIt();
    }

}
