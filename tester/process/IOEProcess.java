package tester.process;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StreamTokenizer;

/**
 * IOEProcess.java
 * Demonstrates Inter- Process Communication via Input-, Output- and Error- Streams
 * which is faster than named Pipes, Sockets or shared Files.
 *
 * Created on 21. Februar 2001, 09:18
 *
 * The Runtime.exec methods create a native process
 * and return an instance of a subclass of Process
 * that can be used to control the process and obtain information about it.
 * The class Process provides methods for performing input from the process,
 * performing output to the process, waiting for the process to complete,
 * checking the exit status of the process, and destroying (killing) the process.
 *
 * The Runtime.exec methods may not work well for special processes on certain native platforms,
 * such as native windowing processes, daemon processes, Win16/DOS processes on Win32, or shell scripts.
 * The created subprocess does not have its own terminal or console.
 * All its standard io (i.e. stdin, stdout, stderr) operations will be redirected to the parent process through three streams
 * (Process.getOutputStream(), Process.getInputStream(), Process.getErrorStream()).
 * The parent process uses these streams to feed input to and get output from the subprocess.
 * Because some native platforms only provide limited buffer size for standard input and output streams,
 * failure to promptly write the input stream or read the output stream of the subprocess
 * may cause the subprocess to block, and even deadlock.
 * The subprocess is not killed when there are no more references to the Process object,
 * but rather the subprocess continues executing asynchronously.
 *
 * @author  Matthias Heuer
 * @version
 */
public class IOEProcess extends Object {

    /** Creates new IOEProcess */
//    public IOEProcess () { }

    /**
	 * This Main Program just listens to it's Input streamIO until the streamIO ends
	 * e.g. using a Stop Command Ctrl-Z comes.
	 * It parses the Input into words and returns each word as an answer.
     * @param args the command line arguments
     */
    public static void main (String[] args) throws IOException, InterruptedException {
//		System.out.println ("Starting IOEProcess"); //add a Separator...
		int Token;
		StreamTokenizer ST = new StreamTokenizer(System.in);
//		while(true) {
//			Thread.currentThread ().sleep (100);
		while ((Token = ST.nextToken ()) != StreamTokenizer.TT_EOF) {
			System.out.print (ST.sval); //just return the Input...
			System.out.println ("("+Token+")"); //add a Separator...
			System.out.flush ();
		}
//		System.out.println ("Stopping IOEProcess"); //add a Separator...
    }

	public static void testIt() throws IOException, InterruptedException {
//		System.err,	System.in, System.out can be redirected, since they are writeable
//		using setErr, setIn, setOut
		Runtime RT = Runtime.getRuntime();
		RT.traceInstructions(true);
		RT.traceMethodCalls (true);
//		Runtime.getRuntime().runFinalizersOnExit (true);
		Process Child = RT.exec("java Process.IOEProcess");
//		System.out.println ("Exit Value = " + Child.exitValue ()); //throws IllegalThreadStateException "process has not exited"
		PrintStream OUT = new PrintStream(Child.getOutputStream ());
		InputStream IN  = Child. getInputStream ();
//		InputStream ERR = Child. getErrorStream ();
		int Token;
		String strTest = null;
		byte[] Buffer = new byte[256];
		StreamTokenizer ST = new StreamTokenizer(IN);
		System.out.println ("Testing " + IOEProcess.class.getName());
		System.out.println ("Enter any String. It will be parsed by a different Process and output piecewise. To stop, just enter 'stop'." );
		System.out.println ("These are the Tokens returned from the StreamTokenizer:");
		System.out.println ("TT_EOF:" + StreamTokenizer.TT_EOF);
		System.out.println ("TT_EOL:" + StreamTokenizer.TT_EOL);
		System.out.println ("TT_NUMBER:" + StreamTokenizer.TT_NUMBER);
		System.out.println ("TT_WORD:" + StreamTokenizer.TT_WORD);
		do {
			int len = System.in.read (Buffer);
			if (len <= 0) continue;
			strTest = new String(Buffer, 0, len);
			strTest = strTest.trim (); //removes the Separator (CR/LF) for the test on "stop"
			OUT.println(strTest); OUT.flush(); //println() adds a Separator (CR/LF)
			System.out.println("Sending: '" + strTest + "'");
//			Thread.currentThread ().sleep (10); //wait a small while for the other thread to respond! very important to avoid busy waits.
			while (IN.available () > 1) { //(Token = ST2.nextToken ()) != StreamTokenizer.TT_EOF) {
				Token = ST.nextToken (); //this is an awkward protocol, because it doesn't define the Number of expected return Values!
				System.out.println ("Receiving: Token: " + Token + "'" + ST.sval + "'"); //just returns the Input...
			}
		} while (! strTest.equalsIgnoreCase ("STOP"));
	}

}
