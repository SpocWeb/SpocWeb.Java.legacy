package tools;

import java.io.File;
import java.io.IOException;

/**
  * Title: TransactFTP<p>
  * Description:
  * Purpose:
  * Transactional File Transfer Protocol
  * This Class describes and encapsulates the different States
  * of a File Transfer in progress.
  * It uses a second Flag File to extend its State Model
  * from 2 States to 4 States using Gray Codes:
  * 00 awaiting Transfer / Processing finished
  * 01 Transfer
  * 11 Transfer finished
  * 10 Processing running
  *
  * Design Decisions / Implementation Details:
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	10-25-2002, 01:06 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-04T16:35:47Z
  * digest: 2e4477d4ace538c947c36ad08ec404b1774c4fefde988f2d7202d53b0aac851f
  * stale: false
  * -->
  */
public class TransactFTP {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** The Data File Reference	 */
	protected File dataFile;

	/** The Flag File Reference	 */
	protected File flagFile;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor	 */
	public TransactFTP(String dataFile_, String flagFile_) {
		this(new File(dataFile_), new File(flagFile_)); }

	/** Initializing Constructor	 */
	public TransactFTP(File dataFile_, File flagFile_) {
		this.dataFile = dataFile_;
		this.flagFile = flagFile_;
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sends the given File to the transacted Location.
	 * Avoid concurrent Transfers to the same Destination by synchronizing.
	 */
	public synchronized boolean sendFile(File sourceFile, long timeout) throws IOException {
		//concurrent Transfers have to be coordinated by leaving their Name in the Flag File
		//check the Status of previous (failed) Transfers:
		if (timeout >= 0) {
			timeout += System.currentTimeMillis(); }
		while (flagFile.exists()) { //finished, possibly processing
			if (timeout >= System.currentTimeMillis()) {
				return false; }
			try { wait(1000); //polling, if no notify() is triggered!
			} catch (InterruptedException x) {
				return false; }
		}
		dataFile.delete(); //(re-)start right away
		sourceFile.renameTo(dataFile);
		flagFile.createNewFile(); //atomically checks for and optionally creates a File
//		sourceFile.delete(); //indicate Completion
		notify(); //notify a waiting or polling Thread
		return true; } //returns true only when it was newly created

	/**
	 * Receives the given File from the transacted Location.
	 * Avoid concurrent Transfers to the same Destination by synchronizing.
	 */
	public synchronized boolean receiveFile(File destFile, long timeout) {
		//concurrent Transfers have to be coordinated by leaving their Name in the Flag File
		//check the Status of previous (failed) Transfers:
		if (timeout >= 0) {
			timeout += System.currentTimeMillis(); }
		while (!flagFile.exists()) { //finished, possibly processing
			if (timeout >= System.currentTimeMillis()) {
				return false; }
			try { wait(1000); //polling, if no notify() is triggered!
			} catch (InterruptedException x) {
				return false; }
		}
		destFile.delete(); //(re-)start right away
		dataFile.renameTo(destFile); //a potentially long Operation
		flagFile.delete(); //atomic Operation
//		dataFile.delete(); //indicate Readiness to receive the next File
		notify(); //notify a waiting or polling Thread
		return true; }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws InterruptedException {
		System.out.println("Testing " + TransactFTP.class.getName());
		testMovingFiles();
	}

	/** Tests moving a File to and fro.
	 *  Result: the Oprerations are synchronized via the File
	 */
	public static void testMovingFiles() throws InterruptedException {
		System.out.println("Testing " + TransactFTP.class.getName());
		//test moving without synchronizing
		final File oldFile = new File("G:\\CDold\\Software\\xpsp1_en_x86.exe");
		final File newFile = new File("C:\\xpsp1_en_x86.exe");
		try {
//			oldFile.renameTo(newFile);
			new Thread() {
				public void run() {
					System.out.println("Copying to...");
					oldFile.renameTo(newFile);
					System.out.println("finished copying to...");
				}
			}.start(); //synchronous Operation though!
			Thread.sleep(100);
//			newFile.renameTo(oldFile);
			new Thread() {
				public void run() {
					System.out.println("Copying fro...");
					newFile.renameTo(oldFile);
					System.out.println("finished copying fro...");
				}
			}.start(); //synchronous Operation though!
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws InterruptedException {
		testIt(args); }

}

