import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
  * Title: RecourseCmd<p>
  * Description:
  * Purpose:
  * Recursively applies the same Command to every File in all Subdirectories
  * Purpose / Responsibilities of this Class
  *
  * Design Decisions / Implementation Details:
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: 
  * for executing the same (DOS-)Command to all Files in a Directory Hierarchy. 
  *
  * similar Classes:
  * @see streamIO.Object.Parser.FileSystem2Stream
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	10-30-2002, 10:57 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class RecourseCmd {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Recursively applies the same Command to every File in all Subdirectories
	 * @param cmd the Executable inclusive Path and all Parameters, except the File
	 * @param dir the Directory to start from and recourse into
	 * @param pattern the File Suffix to search for
	 */
	final static public void execRecursive(String cmd, File dir, String pattern, String cmd2) throws IOException, InterruptedException {
//		System.out.println("cmd='" + cmd + "'");
//		System.out.println("dir='" + dir + "'");
//		System.out.println("pattern='" + pattern + "'");
		//		File dir = new File(strDir);
		if (!dir.isDirectory()) {
			throw new RuntimeException("The given Path is not a Directory!"); }
		String[] files = dir.list();
		for (int i = files.length; --i >= 0;) {
			String fileName = dir.getPath() + "\\" + files[i];
//			System.out.println("testing " + fileName);
			File file = new File(fileName);
			if (file.isDirectory()) {
//				System.out.println("recursing " + fileName);
				execRecursive(cmd, file, pattern, cmd2);
			} else if (fileName.endsWith(pattern)) {
				int retVal = exec(cmd + " " + fileName + " " + cmd2, System.out, System.err, null).waitFor();
				if (retVal != 0) {
					System.err.println("retVal="+retVal+" with Command '"+cmd + " " + fileName+"'"); }
			}
		}
	}

	/** Synchronously executes the given Command
	  * and controls it via connecting its Output-, Error- and Input- Streams
	  * to the given Output-, Error- and Input- Streams.
	  */
/*	final static public int finish(Process proc) throws InterruptedException {
		return proc.waitFor(); //synch
/*		int retVal = proc.waitFor(); //synch
		out.close(); //probably
		in_.close(); //already
		err.close(); //closed
		System.out.println("retVal="+retVal);
		return retVal; }
	}
*/
	/** Synchronously executes the given Command
	  * and controls it via connecting its Output-, Error- and Input- Streams
	  * to the given Output-, Error- and Input- Streams.
	  */
	final static public Process exec(String command, OutputStream out_, OutputStream err_, InputStream in__) throws IOException, InterruptedException {
		System.out.println("executing " + command);
		Process proc = Runtime.getRuntime().exec(command);
		InputStream  out = proc.getInputStream();
		InputStream  err = proc.getErrorStream();
		OutputStream in_ = proc.getOutputStream();
		//cross-connect all Streams: but streaming requires independen Threads!
		if (err_ != null) { new streamIO.integer.pipe.ByteStreamerThread(err, err_).start(); }
		if (out_ != null) { new streamIO.integer.pipe.ByteStreamerThread(out, out_).start(); }
		if (in__ != null) { new streamIO.integer.pipe.ByteStreamerThread(in__, in_).start(); } //blocks indefinitely!
//		Stream.Byte.ByteStreamThread.stream(in__,  in_, 1); //blocks indefinitely, until ^Z & Enter is pressed!
		return proc; }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) { //throws java.io.IOException {
		//System.out.println("Hallo" + null);
		try {
			execRecursive(args[0], new File(args[1]), args[2], args[3]);
		} catch (Throwable x) {
			System.err.println("Usage: java "+RecourseCmd.class.getName()+" <CommandWithFullPathAndParams> <Path> <FileSuffix>");
			if (x instanceof IOException) {
				System.err.println("Make sure you use the full Path or the correct relative Path to the *.exe!"); }
			x.printStackTrace();
		}
	}

}
