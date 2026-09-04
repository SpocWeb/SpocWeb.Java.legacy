package graphic;

import java.awt.Graphics;

/**
  * Title: drawAble<p>
  * Description:
  * Defines the common Interface for Objects that have a graphical Representation, 
  * which corresponds to an Object Oriented Design, instead of a Functional.
  * At a certain Point it doesn't make Sense anymore
  * to define Routines for specific Shapes in the Graph2D Object.
  * Only the most generic and possibly (hardware) accelerated Routines
  * should be present in the Graph2D Object.
  * Implementors are:
  * @see
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-05-2002, 09:24 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
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
