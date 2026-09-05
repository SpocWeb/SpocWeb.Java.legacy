/**
 * File  Name: IFloat.java
 * Created on: 27.10.2002
 */
package function.byref;

import function.IMeasurAble;

/**
 * Title: IFloat<p>
 * Description:
 * Purpose:
 *
 * Read/Write Access to the scalar internal Value; 
 * combines the Interfaces 
 * @see function.IMeasurAble and 
 * @see function.byref.IAdjustAble
 *
 * Known Implementors: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:12:24Z
 * digest: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
 * stale: false
 * tags: [code/function_wrapper, code/mathematical_constants]
 * concepts: [By-Reference Primitive Wrapper]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface IFloat extends IMeasurAble, IAdjustAble {}
