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
 * <!-- docstate
 * tags: [code/stream_io, code/stream_input, code/stream_output, code/struct]
 * concepts: [Primitive and Structured Stream I/O Core Abstractions]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public class AStreamIn_Char
extends AStreamIn_Byte
implements IStreamIn_Char {

	// TODO: LOGIC: this whole class is an IDE-generated stub - every overridden method below
	// is unimplemented and returns a hardcoded default (0/null) instead of delegating to a
	// real character stream or throwing UnsupportedOperationException. Any code that actually
	// uses an AStreamIn_Char instance will silently get wrong data instead of failing loudly.

	/** Always returns 0; not implemented.
	 * @see streamIO.integer.IStreamIn_Byte#available()
	 */
	public int available() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	/** Always returns 0; not implemented.
	 * @see streamIO.IOrdered#getOrder()
	 */
	public byte getOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

	/** Always returns 0; not implemented.
	 * @see streamIO.IAvailAble#getPosition()
	 */
	public long getPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	/** Always returns 0; not implemented.
	 * @see streamIO.IMarkAble#getMaxMarkSize()
	 */
	public long getMaxMarkSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	/** Always returns {@code null}; not implemented.
	 * @see streamIO.integer.IStreamIn_Char#getStreamIn_Byte()
	 */
	public IStreamIn_Byte getStreamIn_Byte() {
		// TODO Auto-generated method stub
		return null;
	}

	/** Always returns 0; not implemented.
	 * @see streamIO.integer.IStreamIn_Char#nextChar()
	 */
	public char nextChar() {
		// TODO Auto-generated method stub
		return 0;
	}

	/** Always returns 0; not implemented.
	 * @see streamIO.integer.IStreamIn_Byte#read()
	 */
	public int read() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	/** No-op; not implemented.
	 * @see streamIO.integer.IStreamIn_Byte#close()
	 */
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	/** Empty smoke-test entry point; performs no action. */
	public static void main(final String[] args) throws Exception {
	}
}
