package tools;

import java.io.File;
import java.io.IOException;

/**
  * Hands Files between Producer and Consumer, using a second Flag File as the Handshake.
  *
  * <p>Purpose:
  * Transactional File Transfer Protocol
  * This Class describes and encapsulates the different States
  * of a File Transfer in progress.
  * It uses a second Flag File to extend its State Model
  * from 2 States to 4 States using Gray Codes:
  * <table>
  * <caption>State Model over the Data File and the Flag File</caption>
  * <tr><th>Code</th><th>Meaning</th></tr>
  * <tr><td>00</td><td>awaiting Transfer / Processing finished</td></tr>
  * <tr><td>01</td><td>Transfer</td></tr>
  * <tr><td>11</td><td>Transfer finished</td></tr>
  * <tr><td>10</td><td>Processing running</td></tr>
  * </table>
  *
  * <p>Design Decisions / Implementation Details:
  * The Flag File is the only Synchronization Primitive that survives across Processes, so
  * the Handshake is built on its atomic Creation and Deletion rather than on Monitors.
  *
  * <h2>Invariants</h2>
  *
  * <p>The Flag File's Existence means the Data File is complete and unclaimed: a Sender may
  * only write while it is absent, and a Receiver may only read while it is present. Both
  * Methods are {@code synchronized}, which serializes Callers within one JVM only; Callers
  * in different Processes rely entirely on the Flag File.
  *
  * <h2>Collaborators</h2>
  *
  * <table>
  * <caption>Types this Class works with</caption>
  * <tr><th>Type</th><th>Relationship</th></tr>
  * <tr><td>{@link java.io.File}</td>
  *     <td>Both the Data File moved and the Flag File whose Existence carries the State.</td></tr>
  * </table>
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
  * digest: 990a990957c885ea92580f7dc43ad12220b1907b26365313fa4d54c51e087ba2
  * stale: false
  * tags: [code/file_transfer, code/flag_file_handshake, code/timeout_handling]
  * concepts: [File Transfer, Interprocess Communication]
  * facets: {layer: io, status: broken, complexity: medium}
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

	/** Binds this Instance to the Data and Flag File named by the given Paths.
	 *
	 * @param dataFile_ Path of the File the Payload is moved to and from
	 * @param flagFile_ Path of the Flag File whose Existence signals a completed Transfer
	 */
	public TransactFTP(String dataFile_, String flagFile_) {
		this(new File(dataFile_), new File(flagFile_)); }

	/** Binds this Instance to the given Data and Flag File.
	 *
	 * @param dataFile_ the File the Payload is moved to and from
	 * @param flagFile_ the Flag File whose Existence signals a completed Transfer
	 */
	public TransactFTP(File dataFile_, File flagFile_) {
		this.dataFile = dataFile_;
		this.flagFile = flagFile_;
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Moves the given File into the transacted Location and raises the Flag File.
	 *
	 * <p>Sends the given File to the transacted Location.
	 * Avoid concurrent Transfers to the same Destination by synchronizing.
	 * Waits for any previous Transfer to be claimed before overwriting the Data File.
	 *
	 * @param sourceFile the File to move; it is renamed, not copied, so it disappears
	 * @param timeout Milliseconds to wait for the previous Transfer to be claimed;
	 *        negative waits indefinitely
	 * @return {@code true} when the Payload was placed and the Flag File newly created,
	 *         {@code false} when the Wait timed out or was interrupted
	 * @throws IOException when the Flag File cannot be created
	 */
	public synchronized boolean sendFile(File sourceFile, long timeout) throws IOException {
		//concurrent Transfers have to be coordinated by leaving their Name in the Flag File
		//check the Status of previous (failed) Transfers:
		if (timeout >= 0) {
			timeout += System.currentTimeMillis(); }
		while (flagFile.exists()) { //finished, possibly processing
			// TODO: LOGIC: comparison inverted - this reports a Timeout while the Deadline is
			// still in the FUTURE, so any Call that finds the Flag File present returns false
			// on the first Iteration and never waits at all. It should be
			// `timeout <= System.currentTimeMillis()`. Same Defect in receiveFile().
			if (timeout >= System.currentTimeMillis()) {
				return false; }
			try { wait(1000); //polling, if no notify() is triggered!
			} catch (InterruptedException x) {
				return false; }
		}
		dataFile.delete(); //(re-)start right away
		// TODO: LOGIC: renameTo's boolean Result is discarded - on a failed Rename (cross
		// Volume, Permission, Destination still locked) the Payload stays where it was, yet
		// the Flag File below is raised anyway, so the Receiver is told a Transfer completed
		// and then reads a stale or absent Data File.
		sourceFile.renameTo(dataFile);
		flagFile.createNewFile(); //atomically checks for and optionally creates a File
//		sourceFile.delete(); //indicate Completion
		notify(); //notify a waiting or polling Thread
		return true; } //returns true only when it was newly created

	/**
	 * Claims the transacted File by moving it to the given Destination and clearing the Flag.
	 *
	 * <p>Receives the given File from the transacted Location.
	 * Avoid concurrent Transfers to the same Destination by synchronizing.
	 * Waits for a Sender to raise the Flag File before taking the Payload.
	 *
	 * @param destFile the Destination the Payload is renamed to, replacing it if present
	 * @param timeout Milliseconds to wait for a Payload to appear; negative waits indefinitely
	 * @return {@code true} when a Payload was claimed, {@code false} when the Wait timed out
	 *         or was interrupted
	 */
	public synchronized boolean receiveFile(File destFile, long timeout) {
		//concurrent Transfers have to be coordinated by leaving their Name in the Flag File
		//check the Status of previous (failed) Transfers:
		if (timeout >= 0) {
			timeout += System.currentTimeMillis(); }
		while (!flagFile.exists()) { //finished, possibly processing
			// TODO: LOGIC: same inverted comparison as in sendFile() - reports a Timeout while
			// the Deadline is still in the future, so a Receiver that finds no Payload returns
			// false immediately instead of waiting for one.
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

	/** Runs this Class's Demonstrations, currently just the unsynchronized Move Experiment.
	 *
	 * @param args ignored; present so the Method matches the main() Signature
	 * @throws InterruptedException when the calling Thread is interrupted while waiting
	 */
	public static void testIt(String[] args) throws InterruptedException {
		System.out.println("Testing " + TransactFTP.class.getName());
		testMovingFiles();
	}

	/** Demonstrates that two concurrent Renames of the same File serialize on the File System.
	 *
	 * <p>Tests moving a File to and fro.
	 * Result: the Oprerations are synchronized via the File.
	 * Note that the Paths below are hard-coded absolute Windows Paths from the original
	 * Experiment, so this Method only does anything on the Machine it was written on.
	 *
	 * @throws InterruptedException when the calling Thread is interrupted between the Moves
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

