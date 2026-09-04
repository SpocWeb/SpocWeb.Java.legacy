package streamIO.integer.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.filter.FilterByte;

/**
  * Title: FilterEnCrypt<p>
  * Description:
  * This class encrypts/decrypts an Input or Output streamIO.
  * Since it uses XOR password encryption, the same Algorithm can be used
  * for Encryption and Decryption.
  * The password is also encrypted as the string or file is processed.
  * This gives an additional level of security, because the XOR Factor changes.
  *
  * Known SubClasses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-02-23, 09;29;15<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class FilterCrypt
extends FilterByte {

////////////////////////////////////////////////////////////////////////////////
//  static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  static Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

	/** Index to the Password Array	 */
	int passWordIndex;

	/** Local Copy of the Password to protect it from Changes	 */
	protected byte[] passWord;

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Constructor
	  * @param streamOut IStreamOutByte Object being delegated to
	  * @param uuencode_ Flag determining whether to use base64 or uu encoding. */
	protected FilterCrypt(IStreamOutByte streamOut, byte[] passWord_) throws IOException {
		super(streamOut); init(passWord_); }

	/** Constructor
	  * @param streamOut OutputStream Object being delegated to
	  * @param uuencode_ Flag determining whether to use base64 or uu encoding. */
	protected FilterCrypt(OutputStream streamOut, byte[] passWord_) throws IOException {
		super(streamOut); init(passWord_); }

	/** Constructor
	  * @param streamOut IStreamOutByte Object being delegated to
	  * @param uuencode_ Flag determining whether to use base64 or uu encoding. */
	protected FilterCrypt(IStreamIn_Byte streamIn_, String passWord_) throws IOException {
		super(streamIn_); passWord = passWord_.getBytes(); }

	/** Constructor
	  * @param streamOut OutputStream Object being delegated to
	  * @param uuencode_ Flag determining whether to use base64 or uu encoding. */
	protected FilterCrypt(InputStream streamIn_, String passWord_) throws IOException {
		super(streamIn_); passWord = passWord_.getBytes(); }

	/** Constructor
	  * @param streamOut IStreamOutByte Object being delegated to
	  * @param uuencode_ Flag determining whether to use base64 or uu encoding. */
	protected FilterCrypt(IStreamIn_Byte streamIn_, byte[] passWord_) throws IOException {
		super(streamIn_); init(passWord_); }

	/** Constructor
	  * @param streamOut OutputStream Object being delegated to
	  * @param uuencode_ Flag determining whether to use base64 or uu encoding. */
	protected FilterCrypt(InputStream streamIn_, byte[] passWord_) throws IOException {
		super(streamIn_); init(passWord_); }

	/** Constructor
	  * @param streamOut IStreamOutByte Object being delegated to
	  * @param uuencode_ Flag determining whether to use base64 or uu encoding. */
	protected FilterCrypt(IStreamOutByte streamOut, String passWord_) throws IOException {
		super(streamOut); passWord = passWord_.getBytes(); }

	/** Constructor
	  * @param streamOut OutputStream Object being delegated to
	  * @param uuencode_ Flag determining whether to use base64 or uu encoding. */
	protected FilterCrypt(OutputStream streamOut, String passWord_) throws IOException {
		super(streamOut); passWord = passWord_.getBytes(); }

	/** Initialization of the Password Array	*/
	protected void init(byte[] passWord_) {
		this.passWord = new byte[passWord_.length];
		System.arraycopy(passWord_, 0, this.passWord, 0, passWord_.length);
	}

////////////////////////////////////////////////////////////////////////////////
//  public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

	/**
	  * Comments  : This function Encrypts one byte, and modifies the password.
	  *             Modifying the password as we encrypt makes the encyption
	  *             slightly harder to break.
	  * 			This Method is used for both en- and decryption.
	  * 			Cannot be called externally, because it changes the Password.
	  * Parameters: byteIn - The byte to encrypt. The encrypted byte is
	  *             returned in this parameter
	  * Returns   : The encrypted byte
	  */
	protected byte encryptByte(byte bytIn) {
		//Exclusive or the byte with the current password byte
		bytIn ^= passWord[passWordIndex];
		//Exclusive or the byte with the first character of the password
		//multiplied by the current index into the password. And the result with
		//256 to avoid possible overflow errors
		bytIn ^= passWord[passWordIndex] * passWordIndex;

		//Modify the password.
		if (passWordIndex < passWord.length-1) {
			//set the current byte in the password to the current byte plus the next byte.
			passWord[passWordIndex] += passWord[++passWordIndex]; //Increment the password index
		} else {
			//If the password length has been exceeded, wrap around to the beginning
			//set the current byte in the password to the current byte
			//plus the first byte. And the result with 256 to avoid possible overflow errors
			passWord[passWordIndex] += passWord[passWordIndex = 0];	//Reset the password index
		}

		//Assign the encrypted byte to the function return value
		return bytIn; }

////////////////////////////////////////////////////////////////////////////////
//  Interface IStreamIn_Byte: Implementation
////////////////////////////////////////////////////////////////////////////////

	/**
	  * Reads the next byte of data from the input stream.
	  * The value byte is returned as an int in the range 0 to 255.
	  * If no byte is available because the end of the stream has been reached,
	  * the value EOF is returned.
	  * This method blocks until input data is available,
	  * the end of the stream is detected, or an exception is thrown.
	  *
	  * A subclass must provide an implementation of this method.
	  *
	  * @return the next byte of data, or EOF if the end of the stream is reached.
	  * @throws IOException - if an I/O error occurs.
	  */
	public int read() throws IOException {
		return encryptByte((byte) streamIn.read()); }

////////////////////////////////////////////////////////////////////////////////
//  Interface StreamOutByte: abstract Methods
////////////////////////////////////////////////////////////////////////////////

	/**
	  * Writes the specified byte to this output stream.
	  * The general contract for write is that one byte is written to the output stream.
	  * The byte to be written is the eight low-order bits of the argument b.
	  * The 24 high-order bits of b are ignored.
	  *
	  * Subclasses of OutputStream must provide an implementation for this method.
	  *
	  * @param b - the byte.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the output stream has been closed.
	  */
	public void write(int b) throws IOException {
		streamOut.write(encryptByte((byte) b)); }

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + FilterCrypt.class.getName());
		String strTest = "One small step for [a] man; one giant leap for mankind";
		String strPassword = "password";
		FilterCrypt enc = new FilterCrypt ((IStreamOutByte)
						  new FilterCrypt (System.out, strPassword.getBytes()),
													   strPassword.getBytes());
		enc.write(strTest); //
		enc.close();    //
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
