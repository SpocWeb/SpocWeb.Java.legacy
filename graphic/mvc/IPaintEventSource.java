/*
 * File Name: IPaintEventSource.java
 * Created on: 12.12.2003
 *
 */
package graphic.mvc;

import graphic.IGraphText;

/**
 * Title: IPaintEventSource<p>
 * Description:
 * Interface defining the Methods of a IPaintEventSource
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
public interface IPaintEventSource {

	/** @see IPainter#paintFrame(IGraphText) adds the given Painter for the Events
	 * 
	 * @param painter to be added 
	 * @return true if the Painter was subscribed
	 */
	public abstract boolean addPainter(final IPainter painter);

	/** @see IPainter#paintFrame(IGraphText) removes the given Painter from the Events
	 * 
	 * @param painter to be removed 
	 * @return true if the Painter was unsubscribed
	 */
	public abstract boolean removePainter(final IPainter painter);

}