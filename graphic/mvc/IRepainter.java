/*
 * File Name: IRepainter.java
 * Created on: 08.12.2003
 *
 */
package graphic.mvc;

/**
 * Title: IRepainter<p>
 * Description:
 * Defines the Interface for the repaint() Method
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
 * mtime: 2026-09-05T10:13:18Z
 * digest: 2685d5a4b1612e9c63f801587279f6d2755bc597420775c6fd12901eb1e25b84
 * stale: false
 * tags: [code/gui]
 * concepts: [Repaint Contract Interface]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public interface IRepainter {

	/** repaints the View, based on the Model
	 * necessary, because Flags (in the Model) are not sufficient
	 * to trigger a Repaint!  
	 * On the other Hand, each Viewer must be subscribed to and notified by the Model.
	 */
	public void repaint(); 

}
