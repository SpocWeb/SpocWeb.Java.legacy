/*
 * Created on 30.03.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;

import java.io.IOException;

/**
 * Title: <p>
 * Description:
 * Purpose:
 *
 * Purpose / Responsibilities of this Class
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
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 */
public class AStreamIn_Char 
extends AStreamIn_Byte 
implements IStreamIn_Char {

	/* (non-Javadoc)
	 * @see streamIO.integer.IStreamIn_Byte#available()
	 */
	public int available() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see streamIO.IOrdered#getOrder()
	 */
	public byte getOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see streamIO.IAvailAble#getPosition()
	 */
	public long getPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see streamIO.IMarkAble#getMaxMarkSize()
	 */
	public long getMaxMarkSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see streamIO.integer.IStreamIn_Char#getStreamIn_Byte()
	 */
	public IStreamIn_Byte getStreamIn_Byte() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see streamIO.integer.IStreamIn_Char#nextChar()
	 */
	public char nextChar() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see streamIO.integer.IStreamIn_Byte#read()
	 */
	public int read() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see streamIO.integer.IStreamIn_Byte#close()
	 */
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	public static void main(final String[] args) throws Exception {
	}
}
