/*
 * File Name: IModel.java
 * Created on: 06.12.2003
 *
 */
package graphic.mvc;

/**
 * Marker interface for an MVC model that can notify its views to repaint themselves.
 *
 * <p>Adds no members of its own; it exists so model implementations are typed distinctly
 * from arbitrary {@link IRepainter} implementers.
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
 * mtime: 2026-09-05T12:42:01Z
 * digest: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
 * stale: false
 * tags: [code/model_state_management, code/observer_pattern]
 * concepts: [Model Interface]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public interface IModel extends IRepainter {
}
