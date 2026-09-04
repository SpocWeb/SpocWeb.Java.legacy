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
 */
public class KeyCounter 
extends MultiPainter
implements KeyListener {

	/** Counter */
	public int counter; 

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
