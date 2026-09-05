package streamIO.integer.encoding;

import math.vector.AVector;
import streamIO.integer.pipe.PipeByte;

/**
  * Converts a StreamOutByte into a StreamIn_Byte
  * by buffering the Output and optionally triggering re-reads.
  *
  * nextItem() => cache oder blocks or delegiert an run();
  * dieses run() f�hrt aber auf mehrere addItem() Operationen
  * CachedFilter
  *
  * Design Decisions / Implementation Details:
  * @see streamIO.Byte.PipeByte performs fast, unsynchronized and unchecked writing.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	12-29-2002, 12:13 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T21:37:31Z
  * digest: affd69101579568c3fb620d6fa907199682ab8f71102c881faebcec8cbfa5883
  * stale: false
  * tags: [code/stream_filter, code/base64_encoding, code/crc, code/xor_cipher]
  * concepts: [Byte/Character Re-Encoding Filters - Base64 BinHex URL/Entity Escaping CRC XOR]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class SynchPipeByte
extends PipeByte {
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Default Value for the Capacity Increment
	 * when 0 no Increment happens
	 * when negative, multiplies the Size
	 */
	static int CAPACITY_INCREMENT = 10;
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Capacity Increment, when writing exceeds Size */
	public int capacityIncrement = CAPACITY_INCREMENT;

	/** TimeOut to wait for Reading
	 * negative Values switch waiting off
	 * zero waites infinitely
	 */
	public int TimeOutRead;

	/** TimeOut to wait for Writing
	 * negative Values switch waiting off
	 * zero waites infinitely
	 */
	public int TimeOutWrite;

	/** Object to call the run() Method when writing is necessary to fill up the Buffer
	 * null switches Trigger off
	 */
	public Runnable TriggerWrite;

	/** Object to call the run() Method when reading is necessary to deplete the Buffer
	 * null switches Trigger off
	 */
	public Runnable TriggerRead;

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/**Constructor allocating the Space	 */
	public SynchPipeByte() {
		super(); }

	/**Constructor allocating the Space	 */
	public SynchPipeByte(boolean stack_) {
		super(stack_); }

	/**Constructor allocating the Space	 */
	public SynchPipeByte(boolean stack_, int initialCapacity_) {
		super(stack_, initialCapacity_); }

	/**Constructor allocating the Space	 */
	public SynchPipeByte(boolean stack_, int initialCapacity_, int maxCapacity_) {
		super(stack_, initialCapacity_, maxCapacity_); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IStreamOutByte: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * When trying to write a full Buffer, it can either...
	 * -overrun and overwrite the already written Items
	 * -trigger an Action with the Reader
	 * -block until something is read
	 * -throw an Exception when the block times out
	 *
	 * @see streamIO.Byte.IStreamOutByte#addString(int)
	 */
	public synchronized void write(final int b) {
		if (this.isFull()) {
			setCapacity(AVector.ENLARGED_CAPACITY(getCapacity()+1, capacityIncrement)); }//enlarge the Array
		if (this.isFull()) {
			if (TriggerRead != null) {
				TriggerRead.run(); }
		}
		if (this.isFull()) {
			if (0   <=   TimeOutWrite) {
				try{wait(TimeOutWrite);
				} catch (InterruptedException x) {
				}
			}
		}
		if (this.isFull()) { //try the Trigger again...
			if (TriggerRead != null) {
				TriggerRead.run(); }
		}
		if (this.isFull()) {
			throw new RuntimeException("Timeout on writing: no more Space and no Reading takes place!"); }
		super.write(b); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IStreamIn_Byte: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * When trying to read an empty Buffer, it can either...
	 * -underrun and repeat the Items just read
	 * -trigger an Action with the Writer
	 * -block until something is written
	 * -throw an Exception when the block times out
	 *
	 * @see streamIO.Byte.IStreamIn_Byte#read()
	 */
	public synchronized int read() {
		if (isZero()) {
			if (TriggerWrite != null) {
				TriggerWrite.run(); }
		}
		if (isZero()) {
			if (0   <=   TimeOutRead) {
				try{wait(TimeOutRead);
				} catch (InterruptedException x) {
				}
			}
		}
		if (isZero()) { //try the Trigger again...
			if (TriggerWrite != null) {
				TriggerWrite.run(); }
		}
		if (isZero()) {
			throw new RuntimeException("Timeout on reading: no more Data and no Writing takes place!"); }
		return super.read(); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt() throws Exception {
		System.out.println("Testing " + SynchPipeByte.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		testIt(args); }

}

