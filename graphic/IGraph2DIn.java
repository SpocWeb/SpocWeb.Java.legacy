package graphic;

import java.awt.Color;

/**
 * Interface describing 2dim Graphics that can also be read.
 * Reading is random Access, not streaming!
 */
public interface IGraph2DIn {

	/**
	 * Reads the Color of the Pixel at this Position
	 * @return the Color of the Pixel as an int.
	 */
	public int getPixel(int x, int y);

	/**
	 * @see graphic.IGraphics#setPaintMode()
	 */
	public void setPaintMode();

	/**
	 * @see graphic.IGraphics#setXORMode(java.awt.Color)
	 */
	public void setXORMode(Color c1); 

	/**
	 * @see graphic.IGraphics#copyArea(int, int, int, int, int, int)
	 */
	public void copyArea(int x, int y, int width, int height, int dx, int dy);

}
