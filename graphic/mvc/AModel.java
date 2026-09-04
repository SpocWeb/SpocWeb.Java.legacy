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
 */
public class AModel 
extends MultiPainter 
implements IModel {

	/** @see graphic.mvc.Point2D.ICanvas#repaint()	 */
	public void repaint() {
		draw(null);
	}

}
