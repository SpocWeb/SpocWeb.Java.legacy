/*
 * Created on 30.03.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;

import java.io.IOException;

/**
 * Unimplemented skeleton subclass of {@link AStreamIn_Byte} whose methods all throw
 * {@link UnsupportedOperationException} except {@link #close()}; concrete character-stream
 * implementations must subclass this and override the methods before use.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * @see AStreamIn_Byte superclass this skeleton leaves unimplemented
 * <!-- docstate
 * tags: [code/stream_io, code/stream_input, code/stream_output, code/struct]
 * concepts: [Primitive and Structured Stream I/O Core Abstractions]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public class AStreamIn_Char
extends AStreamIn_Byte
implements IStreamIn_Char {

	/** Message used by every unimplemented Method of this Skeleton. */
	private static final String STR_NOT_IMPLEMENTED =
		"AStreamIn_Char is an unimplemented Skeleton; subclass it and override this Method.";

	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.integer.IStreamIn_Byte#available()
	 */
	public int available() throws IOException {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}

	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.IOrdered#getOrder()
	 */
	public byte getOrder() {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}

	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.IAvailAble#getPosition()
	 */
	public long getPosition() {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}

	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.IMarkAble#getMaxMarkSize()
	 */
	public long getMaxMarkSize() {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}

	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.integer.IStreamIn_Char#getStreamIn_Byte()
	 */
	public IStreamIn_Byte getStreamIn_Byte() {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}

	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.integer.IStreamIn_Char#nextChar()
	 */
	public char nextChar() {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}

	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.integer.IStreamIn_Byte#read()
	 */
	public int read() throws IOException {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}

	/** No-op: this Skeleton holds no Resources.
	 * @see streamIO.integer.IStreamIn_Byte#close()
	 */
	public void close() throws IOException {
	}

	/** Empty smoke-test entry point; performs no action. */
	public static void main(final String[] args) throws Exception {
	}
}
