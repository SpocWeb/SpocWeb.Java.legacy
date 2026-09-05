package graphic;

import java.awt.Color;

/**
 * Interface describing 2dim Graphics that can also be read.
 * Reading is random Access, not streaming!
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:47:11Z
 * digest: 0d1cc27fa2bb7f28424cf65d02302bc551ff282bf57617eb0ea0b34145560377
 * stale: false
 * tags: [code/graphics, code/image_processing]
 * concepts: [Graphics Input Interface]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public interface IGraph2DIn {

	/**
	 * Reads the Color of the Pixel at this Position
	 * @return the Color of the Pixel as an int.
	 */
	public int getPixel(int x, int y);

	/**
	 * Switches subsequent drawing operations back to normal (overwrite) paint mode.
	 *
	 * @see graphic.IGraphics#setPaintMode()
	 */
	public void setPaintMode();

	/**
	 * Switches subsequent drawing operations to XOR mode against the given color.
	 *
	 * @see graphic.IGraphics#setXORMode(java.awt.Color)
	 */
	public void setXORMode(Color c1);

	/**
	 * Copies a rectangular pixel area to a new location offset by (dx, dy).
	 *
	 * @see graphic.IGraphics#copyArea(int, int, int, int, int, int)
	 */
	public void copyArea(int x, int y, int width, int height, int dx, int dy);

}
