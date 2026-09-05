/*
 * File Name: AModel.java
 * Created on: 06.12.2003
 *
 */
package graphic.mvc;


/**
 * Title: AModel<p>
 * Description:
 * Purpose:
 * Overhead for introducing the MVC: 
 * Variables and Constants have to be shared (focusPointIndex, pointRadius) 
 * Events have to be routed (like this refresh() as well as the Controller Events)
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
 * mtime: 2026-09-05T12:43:03Z
 * digest: 5c0d3f64f990cf91672080370fc9f72ebdcf0f62b8c389c64344150dd0dfce6f
 * stale: false
 * tags: [code/model_state_management, code/observer_pattern]
 * concepts: [MVC Model Base Class]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public class AModel 
extends MultiPainter 
implements IModel {

	/** Triggers a redraw of this model's registered painters via {@link #draw(IGraphText)}.
	 * @see graphic.mvc.Point2D.ICanvas#repaint()	 */
	public void repaint() {
		draw(null);
	}

}
