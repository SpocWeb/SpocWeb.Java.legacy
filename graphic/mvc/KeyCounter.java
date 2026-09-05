/*
 * File Name: KeyCounter.java
 * Created on: 13.12.2003
 *
 */
package graphic.mvc;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Title: KeyCounter<p>
 * Description:
 * Counts the Number of Times that a Key has been pressed. 
 *
 * Known SubClasses: <none>
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
 * mtime: 2026-09-05T12:43:01Z
 * digest: a8a22936b66ca3e988942aa148a479661fa88028cf6f32a282f3992d41494f28
 * stale: false
 * tags: [code/event_handling]
 * concepts: [Key-Press Counter]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class KeyCounter 
extends MultiPainter
implements KeyListener {

	/** Counter */
	public int counter;

	/** The virtual key code (see {@link KeyEvent#getKeyCode()}) this counter tracks. */
	public int keyCode;

	/**
	 * Initializing Constructor
	 * @param virtualKeyCode
	 */
	public KeyCounter(final int virtualKeyCode) {
		keyCode = virtualKeyCode;
	}

	/**
	 * Initializing Constructor
	 * @param virtualKeyCode
	 */
	public KeyCounter(final int virtualKeyCode, final int startValue) {
		keyCode = virtualKeyCode;
		counter = startValue;
	}

	/**High Level Key Event, only for Unicode Characters 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)	 */
	public void keyTyped(final KeyEvent e) {
	}

	/**Low Level Key Event 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)	 */
	public void keyPressed(KeyEvent e) {
		if (keyCode == e.getKeyCode()) {
			++counter;
			draw(null);
		}
	}

	/**Low Level Key Event  
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)	 */
	public void keyReleased(KeyEvent e) {
	}

}
