package streamIO.integer;

import streamIO.integer.file.FileStreamByte;
import streamIO.object.parser.jdbc.ResultSetSep;

/**
  * Marker interface combining the {@link IStreamIn_Byte} and {@link IStreamOutByte}
  * contracts for streams that support synchronized, bidirectional byte I/O.
  *
  * @see FileStreamByte known implementor
  * @see ResultSetSep known use
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	12-22-2002, 03:18 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * tags: [code/stream_io, code/stream_input, code/stream_output, code/struct]
  * concepts: [Primitive and Structured Stream I/O Core Abstractions]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public interface IStreamByte
extends
IStreamIn_Byte,
IStreamOutByte {
}

