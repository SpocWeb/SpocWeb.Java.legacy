package graphic;

import java.awt.Color;

/**
 * Most basic Interface for Output only Graphics in 2 Dimensions.
 * The Methods reflect the Fact that the Color should not be handed over everytime,
 * for Performance and Readability Reasons.
 * Also the Handle to the actual Graphics Context is stored here. 
 * This is effectively an OutputStream for Coordinate Pairs.
 */
public interface IGraph2DOut {

	/** Sets a Pixel at the given Position (x, y) in the current color.  
	 * 
	 * @param x horizontal Coordinate of the Position
	 * @param y vertical   Coordinate of the Position
	 */
	public void setPixel(int x, int y);

	/** Sets a Pixel at the given Position (x, y) in the given color.  
	 * 
	 * @param x horizontal Coordinate of the Position
	 * @param y vertical   Coordinate of the Position
	 * @param color Color, in RGB Values, to paint the Pixel with. Does not affect the current Color. 
	 * The Alpha Value is defaulted to 255 (opaque)
	 */
	public void setPixel(int x, int y, int color);

	/** Sets a Pixel at the given Position (x, y) in the given color.  
	 * 
	 * @param x horizontal Coordinate of the Position
	 * @param y vertical   Coordinate of the Position
	 * @param color Color, in RGB Values, to paint the Pixel with. Does not affect the current Color. 
	 * @param hasalpha if false the Alpha Value is defaulted to 255 (opaque), otherwise it is the highest Byte in color
	 */
	public void setPixel(int x, int y, int color, boolean hasalpha);

	/** Sets a Pixel at the given Position (x, y) in the given color.  
	 * 
	 * @param x
	 * @param y
	 * @param color Color to paint the Pixel with. Does not affect the current Color. 
	 */
	public void setPixel(int x, int y, Color color);

	///additional Methods

	/**Sets a Pixel in the current Color at the current Position, requires a current Position!	 */
	public abstract void setPixel();

	/**Sets a Pixel in the current Color	 */
	public abstract void setPixel(Point2D P);

	/**Changes the Color and sets a Pixel at the current Position	 */
	public abstract void setPixel(Color color);

	/**Changes the Color and sets a Pixel at the given Position	 */
	public abstract void setPixel(Point2D P, Color color);

	///////////////////////////////////////////////////////////////////////////////////

	/** Sets the color for the next painting Action 
	 * 
	 * @param color_ the Default Painting Color to set
	 */
	public void  setColor(Color color_);

	/** Gets the current painting color  
	 * @return the current painting color
	 */
	public Color getColor();

}
