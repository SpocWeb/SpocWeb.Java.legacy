package streamIO.copy.group.ring;

/**Interface for an ordinary Differential Equation (ODE).
 * Contains only the single Method to calculate the first Derivatives of y.
 * Also used to define Functions with two Arguments.
 *
 * Design Decisions:
 * Used 'IRing' instead of 'Tensor' for y, to facilitate both
 * scalar and Vector Differntial Equations.
 */
public interface IODE {

	/**Returns the 1st Derivative in x of all Coordinates of this Function at Point y
	 * For Time invariant Differential Equations x could be skipped.
	 * @param x The x Position at which to evaluate the DGL
	 * @param y The y Position at which to evaluate the DGL
	 * @param derivative1 The first Derivative at (x,y)
	 */
	void Funktion(IIntRing x, IIntRing y, IIntRing derivative1);

}
