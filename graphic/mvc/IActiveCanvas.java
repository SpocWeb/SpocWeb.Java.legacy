/*
 * File Name: IActiveCanvas.java
 * Created on: 07.12.2003
 *
 */
package graphic.mvc;


/**
 * Combines a passive {@link ICanvas} with an active {@link IController} into a single
 * canvas that both draws and reacts to input.
 *
 * Known Implementations: <none>
 * @see graphic.mvc.Point2D.BaseApplet
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
 * mtime: 2026-09-05T12:42:20Z
 * digest: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
 * stale: false
 * tags: [code/gui]
 * concepts: [Active Canvas Interface]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public interface IActiveCanvas extends IController, ICanvas {

}
