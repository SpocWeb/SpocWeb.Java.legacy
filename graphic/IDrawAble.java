package graphic;

import java.awt.Graphics;

/**
  * Defines the common contract for objects that can draw and fill their own
  * graphical representation, favoring an object-oriented design over a purely
  * functional one.
  *
  * <p>At a certain point it doesn't make sense anymore to define routines for
  * specific shapes in the {@link Graph2D} object; only the most generic and
  * possibly (hardware) accelerated routines should live there.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-05-2002, 09:24 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * @see Graph2D
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:47:42Z
  * digest: 697237ae89fd00545916a47d1e7606d448ccd84823b7a59635cd50c14ee0066d
  * stale: false
  * tags: [code/graphics]
  * concepts: [Rendering Abstraction Interface]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public interface IDrawAble {

	/**
	 * This Method lets the Graphical Object draw itself
	 * on the given Graphics device g.
	 */
	public void draw(Graphics g);

	/**
	 * This Method lets the Graphical Object draw and fill itself
	 * on the given Graphics device g.
	 */
	public void fill(Graphics g);

}
