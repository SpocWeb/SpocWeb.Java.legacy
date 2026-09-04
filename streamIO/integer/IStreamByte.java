package streamIO.integer;

import streamIO.integer.file.FileStreamByte;
import streamIO.object.parser.jdbc.ResultSetSep;

/**
  * Title: IStreamByte<p>
  * Description:
  * Defines the Interface for a synchronized mixed Input and Output streamIO
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors:
  * @see FileStreamByte
  *
  * Known Uses:
  * @see ResultSetSep
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	12-22-2002, 03:18 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface IStreamByte
extends
IStreamIn_Byte,
IStreamOutByte {
}

