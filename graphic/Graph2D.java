package graphic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

/**
 * This Class is the minimal extension to AGraph2D,
 * by defining only the setPixel() Method.
 * It is basically a Testbed for all Methods defined in
 * @see AGraph2D
 * @see AGraphRead
 * @see AGraphText
 */
public class Graph2D 
extends AGraphImage { //implements IGraph2DIn {

	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////

	/** Graphics Context to write to	 */
	protected Graphics g;

	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**initializing Constructor with Default Clipping Area:	 */
	public Graph2D(Graphics g_) {
		this.g = g_;
		Rectangle R = g_.getClipBounds();
		if (R == null)
			return;
		setClipBounds(
			new Point2D(R.x, R.y),
			new Point2D(R.x + R.width, R.y + R.height));
	}

	/**Constructor that defines a Clippíng Area	 */
	public Graph2D(Graphics g_, Point2D ClipTL_, Point2D ClipBR_) {
		this.g = g_;
		setClipBounds(ClipTL_.getLocation(), ClipBR_.getLocation());
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**Sets the Clipping Bounds, taken out of the Constructor,
	 * because it is used from both	 
	 */
	private void setClipBounds(Point2D ClipTL_, Point2D ClipBR_) {
		P.setLocation(ClipTL_);
		orderedMethod(ClipBR_.getX(), ClipBR_.getY(), MethodSetClip);
		g.setClip(
			ClipTL_.getX(),
			ClipTL_.getY(),
			ClipBR_.getX() - ClipTL_.getX(),
			ClipBR_.getY() - ClipTL_.getY());
		ClipTL.copyAt(ClipTL_);
		ClipBR.copyAt(ClipBR_);
		clip = false;
		//Switch Clipping off, because it is done by the Graphics object g
	}

	/**
	 * Controls double buffering of the Component Contents.
	 * The Image is either null or the Contents from the previous Painting Action.
	 * If it fits into the range, it is painted and returned unchanged.
	 * In this Case a repainting is not necessary.
	 * If it doesn't fit, it is created anew and a repainting should take place.
	 * After the doubleBuffer() command drawing is done to the Image,
	 * no longer the Screen!!!
	 *
	 * Example Code for the Applet.Paint Method:
	 * Image img2 = img; if (img2 == (img =  g2D.doubleBuffer(this, img))) return;
	 */
	public Image doubleBuffer(Component cmp, Image img) {
		//		g = cmp.getGraphics(); //without this Line the Size is not adjusted
		Rectangle Res = g.getClipBounds();
		if ((img != null)
			&& //Image exists, draw it and return it
		 (
				img.getHeight(null) >= Res.height)
			&& (img.getWidth(null) >= Res.width)) { //fits into the ClipBounds
			g.drawImage(img, 0, 0, null);
		} else { //image doesn'T exist yet or is too small...
			img = cmp.createImage(Res.width, Res.height);
		}
		g = img.getGraphics();
		g.setClip(Res);
		return img;
	}

	protected final static int MethodSetClip = 0;
	protected final static int MethodDrawRect = 1;
	protected final static int MethodFillRect = 2;
	protected final static int MethodDrawRect3 = 3;
	protected final static int MethodFillRect3 = 4;
	protected final static int MethodDrawRRect = 5;
	protected final static int MethodFillRRect = 6;

	/**Only used for RoundRect in orderedMethod!	 */
	protected static int rx, ry;

	/** Orders the Coordinates, so Height and Width are positive for most Operations	 */
	protected void orderedMethod(int x1, int y1, int Method) {
		int Lx = P.getX();
		int Hx = x1;
		if (Hx < Lx) {
			Lx = x1;
			Hx = P.getX();
		};
		Hx -= Lx;
		int Ly = P.getY();
		int Hy = y1;
		if (Hy < Ly) {
			Ly = y1;
			Hy = P.getY();
		};
		Hy -= Ly;
		switch (Method) {
			case MethodSetClip :
				g.setClip(Lx, Ly, Hx, Hy);
				break; // exit the switch
			case MethodDrawRect :
				g.drawRect(Lx, Ly, Hx, Hy);
				break; // exit the switch
			case MethodFillRect :
				g.fillRect(Lx, Ly, Hx + 1, Hy + 1);
				break; // exit the switch
			case MethodDrawRect3 :
				g.draw3DRect(Lx, Ly, Hx, Hy, true);
				break; // not needed, but good style
			case MethodFillRect3 :
				g.fill3DRect(Lx, Ly, Hx + 1, Hy + 1, true);
				break; // not needed, but good style
			case MethodDrawRRect :
				g.drawRoundRect(Lx, Ly, Hx, Hy, rx << 1, ry << 1);
				break; // not needed, but good style
			case MethodFillRRect :
				g.fillRoundRect(Lx, Ly, Hx, Hy, rx << 1, ry << 1);
				break; // not needed, but good style
		}
		P.setX(x1);
		P.setY(y1);
	} //Now the Endpoint is the actual Coordinate

	//The Rest are Optimizations: Most of them are implemented in JavaGraphics

	/**Clears the specified rectangle by filling it with the background
	 * color of the current drawing surface. This operation does not
	 * use the current paint mode.
	 * <p>
	 * Beginning with Java&nbsp;1.1, the background color
	 * of offscreen images may be system dependent. Applications should
	 * use <code>setColor</code> followed by <code>fillRect</code> to
	 * ensure that an offscreen image is cleared to a specific color.
	 * @param       x the <i>x</i> coordinate of the rectangle to clear.
	 * @param       y the <i>y</i> coordinate of the rectangle to clear.
	 * @param       width the width of the rectangle to clear.
	 * @param       height the height of the rectangle to clear.
	 * @see         java.awt.Graphics#fillRect(int, int, int, int)
	 * @see         java.awt.Graphics#drawRect
	 * @see         java.awt.Graphics#setColor(java.awt.Color)
	 * @see         java.awt.Graphics#setPaintMode
	 * @see         java.awt.Graphics#setXORMode(java.awt.Color)
	 * @since       JDK1.0
	 */
	//	public void clearRect(int x, int y, int width, int height) {
	//		g.clearRect(x, y, width, height); }

	////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IGraph2DOut: Implementation
	////////////////////////////////////////////////////////////////////////////

	//Only the setPixel Method is replaced!
	
	/**
	 * Allows to optimize the Setting of the Color in setClippedPixel, 
	 * because always identical to the Color of the Delegate!
	 */
	private Color gColor; 

	/**Sets the color for the next painting Action	 */
	public void setColor(Color color_) {
		g.setColor(col = gColor = color_);
	}

	/**Sets a Pixel in the current Color at the current Position	 */
	public void setClippedPixel(final Color color_) {
		if ((gColor != color_) //Optimization!
			//&& !gColor.equals(color_) //maybe slower than actually setting it!
			) {
			g.setColor(gColor = color_);
		}
		g.drawLine(P.getX(), P.getY(), P.getX(), P.getY());
	}

	/**
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, int, int, int, int, int, int, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final ImageObserver observer) {
		// TODO Auto-generated method stub
		//throw new RuntimeException("Not implemented!");
		return true; 
	}

}
