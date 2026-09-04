/*
 * File Name: MultiPainter.java
 * Created on: 12.12.2003
 *
 */
package graphic.mvc;

import graphic.IGraphText;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Title: MultiPainter<p>
 * Description:
 * Delegates Painting to a List of Painters. 
 * Since the Sequence may be important, 
 * a List is used instead of a simple Collection. 
 * Alternatively a linked List of Painters could be used, 
 * but since Painters should be independent of each other, 
 * rather use a separate Array! 
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
public class MultiPainter 
implements IPainter, IPaintEventSource {

	protected MultiPainter() {
	}
	
	/** Reference to the Painter used on the paint() Event. 
	 * Multiplexing several Painters can be done by a MultiPainter
	 * Since the Sequence may be important, 
	 * a List is used instead of a simple Collection. 
	 */
	private final List painters = new ArrayList(); 

	/** @see IPainter#paintFrame(IGraphText) adds the given Painter for the Events
	 * 
	 * @param painter to be added 
	 * @return true if the Painter was subscribed, false if it was already. 
	 */
	public boolean addPainter(final IPainter painter) {
		if (painters.contains(painter))
			return false; 
		painters.add(painter);
		return true;
	}

	/** 
	 * @see IPainter#paintFrame(IGraphText) removes the given Painter from the Events
	 * 
	 * @param painter to be removed 
	 * @return true if the Painter was unsubscribed
	 */
	public boolean removePainter(final IPainter painter) {
		return painters.remove(painter);
	}
	
	/**
	 * MultiPlexer; painting is delegated to a Set of Painters. 
	 * Therefore also the Sequence is important! 
	 * The very first Painter, e.g. clears the Picture. 
	 * @see graphic.mvc.IPainter#paintFrame(graphic.IGraphText)	
	 * 
	 * @param gText a Graphics Context, null to simply trigger a repaint()
	 */
	public void draw(final IGraphText gText) {
		for(final Iterator iter = painters.iterator(); iter.hasNext();) {
			final IPainter painter = (IPainter) iter.next(); 
			painter.draw(gText);
		}
	}

}
