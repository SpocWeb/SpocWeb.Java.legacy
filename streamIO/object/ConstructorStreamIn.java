package streamIO.object;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import streamIO.IIStreamIn;
import streamIO.exception.BaseException;

/**
 * Filter that converts each incoming item into a new instance of a target class by invoking
 * that class's single-argument constructor reflectively.
 * <p>
 * ConstructorStreamIn.java
 *
 * This Class implements the StreamIn Interface
 * It converts ingoing Objects to other Objects by creating these dynamically.
 * It relies on the target class to have a single Argument (Type Object) Constructor.
 *
 * Created on 22. April 2001, 11:47
 *
 * @author  Matthias Heuer
 * @version
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:35:24Z
 * digest: 8fa2b1840f2aa0c96f25cf9cd4ef8059608540977b4e2dcd5aeebd275c8f37fc
 * stale: false
 * tags: [code/stream_processing, code/iterator]
 * concepts: [Object Stream Pipeline]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class ConstructorStreamIn
extends AFilterIn {

	/** Constructor used to create new Objects,	*/
	protected Constructor Input;

	protected Class Output;

	/**Returns the Parent's next Item	 */
	protected Object nextItemInternal() {
		try {
			final Object[] Params = {in.nextItem()};
//			if (Params[0] == null) return null;
			return Input.newInstance(Params);
		} catch (   InstantiationException e) { throw new InstantiationError(e.toString ());
		} catch (   IllegalAccessException e) { throw new IllegalAccessError(e.toString ());
		} catch (InvocationTargetException e) { throw new      BaseException(e.toString ());
	}
	}

	/** Creates new ConstructorIterator */
	public ConstructorStreamIn(IIStreamIn Enum, Class Input, Class Output) throws NoSuchMethodException {
		super (Enum);
		Class[] Params = {Input};
		this. Input = Output.getConstructor(Params);
		this.Output = Output; }

}
