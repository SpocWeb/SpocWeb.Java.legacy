/*
 * File Name: IPaintEventSource.java
 * Created on: 12.12.2003
 *
 */
package graphic.mvc;

import graphic.IGraphText;

/**
 * Interface for a source that broadcasts paint events to a set of subscribed
 * {@link IPainter}s.
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
 * mtime: 2026-09-05T12:52:16Z
 * digest: f13f257f30fc2d6a29fe59e061434e3f4711886c376da510c85d5f55a0de9591
 * stale: false
 * tags: [code/event_dispatch, code/gui]
 * concepts: [Paint Event Source Interface]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public interface IPaintEventSource {

	/** Subscribes the given painter to receive future paint events.
	 * @see IPainter#paintFrame(IGraphText) adds the given Painter for the Events
	 *
	 * @param painter to be added
	 * @return true if the Painter was subscribed
	 */
	public abstract boolean addPainter(final IPainter painter);

	/** Unsubscribes the given painter from paint events.
	 * @see IPainter#paintFrame(IGraphText) removes the given Painter from the Events
	 *
	 * @param painter to be removed
	 * @return true if the Painter was unsubscribed
	 */
	public abstract boolean removePainter(final IPainter painter);

}