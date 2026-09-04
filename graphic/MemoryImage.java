package graphic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;

/**
 * Implements a bitmapped RGB Graphics Object in Memory to enable Reading it.
 * java.awt.image.BufferedImage also allows to directly draw into it 
 * and even load Images into it, unlike this Class (see unimplemented drawImage() Method)
 * 
 * TODO: The highest Byte in the Integer can be used for storing the ZOrder. 
 * But equivalently another Array could be used for that!
 * Using gtrMode or lssMode you can control to either paint the fore- or the background.
 * Using the addMode you can paint semitransparent Pictures.
 * With 256 Values you can store either a linear Distance if the Box is well known
 * Or you can use a logarithmic Scale (e.g. Base 1.1) and reach a Range of e.g. 10^10
 * with only 10% Error in the Distances.
 *
 * Painting semitransparent, overlapping Graphics works best in OrMode.
 *
 * When using gtrMode or lssMode, you have to use setHeightColor instead of setColor
 * to set the painting Color or give the Color the highest Byte of 80.
 *
 * To use it, just create it and draw to it, using it's Methods.
 * To repaint it, just call the paint() Method in the Components paint() Method
 * To paint into Memory and Image in Parallel, just chain the Graphics Context!
 * 
 * Used in: 
 * @see graphic.example.AntHillInside
 * @see graphic.example.CellularAutomaton1D 
 * @see function.derive.neuron.KohonenGraph
 * @see graphic.implement.testGraph2D
 *
 * @see java.awt.image.BufferedImage is a new Java2D Class for the same Purpose,
 * but it's Memory is not as accessible as here.
 *
 * BufferedImage is used in: 
 * @see graphic.mvc.BaseApplet
 * @see graphic.ms3d.Ms3dTexture to store the Textures 
 * @see graphic.TexturePalette to store the Textures
 * 
 * @deprecated due to the Existence of java.awt.image.BufferedImage 
 * which allows also to draw Images.   
 */
public class MemoryImage
extends AGraphRead {
	
	/** Static Constants for Setting Pixels	 */

	/**Simply sets the Pixel		 */	final static public int setMode = 0;
	/**Sets the Pixel in XOR Mode	 */	final static public int xorMode = 1;
	/**Sets the Pixel in OR Mode	 */	final static public int or_Mode = 2;
	/**Sets the Pixel in AND Mode	 */	final static public int andMode = 3;
	/**Sets the Pixel in add Mode	 */	final static public int addMode = 4;
	/**Sets the Pixel in greater Mode */final static public int gtrMode = 6;
	/**Sets the Pixel in less Mode	  */final static public int lssMode = 7;
	/**Sets the Pixel in subtraction Mode	 */
										final static public int subMode = 5;

	/** Constant for the highest Byte to indicate opaque Painting	 */
	final static public int OPAQUE = 0xFF000000;

	/**Constant for the highest Byte to indicate transparent Painting	 */
	final static public int TRANSPARENT = ~OPAQUE;

	/** Constant for filtering out the highest Byte to return only the RGB Values.	 */
	final static public int RGB_VALUES = TRANSPARENT;

	/**Constant for the highest Bit to indicate setting a Color in Height Painting	 */
	final static public int ColorFlag = 0x80000000;	//very improbable to reach this Value!

	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Reference to a backed Graphics Context to be filtered through
	 * for dynamic painting in both MemoryImage AND Graphics!
	 */
	protected Graphics gr;

	/** Actual Memory Image stored wrap around	 */
	protected int[] Pixels;

	/** Height of the Memory Image	 */
	protected int Height;

	/** Width of the Memory Image	 */
	protected int Width;

	/** Current Painting Color	 */
	protected int currentColor;

	/** Current Painting Color, when interpolating Height.	 */
	protected int HeightColor;

	/** Current Painting Mode	 */
	public int currentMode;

	/** Local ImageSource for Buffering, 
	 * mapped to a Memory Structure for direct Access	 */
	protected final MemoryImageSource myImageSource;

	////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Actual Painting Method, should be called by the Components in their paint Routine
	 * Actually this is a lot of Overhead.
	 */
	public void paint(final Component cmp, final boolean dynamic) {
		final Image img = cmp.createImage(this.getImageSource());
		//painting like this is quite ineffective,
		//because img creates another In Memory Image.
		//but unfortunately that Image cannot be 'read'!
		Graphics g; //for later dynamic Update!
		(g = cmp.getGraphics()).drawImage(img,0,0,null);
		if (dynamic) { gr = g; } //also draw synchronously
		//The highest Byte determines the Saturation with which the Color is drawn
	}

	/**Creates an ImageProducer from the Pixels in Memory
	 * With this ImageProducer you can paint the Image by calling
	 * Image img = createImage(ImageSource())
	 */
	public MemoryImageSource getImageSource() { return myImageSource; }

	/**Method to retrieve the current Painting Color	 */
	public Color getColor() { return new Color(currentColor); }

	/**Method to set the current Painting Color or the z-Coordinate	 */
	public void setColor(Color c) {
		setColor (c.getRGB()); //This returns all! Red, Green, Blue AND Alpha!
		if (gr != null) {
			gr.setColor(c); }
		}

	/**Method to set the current Painting Color or the z-Coordinate	 */
	public void setColor(int c)	{
		currentColor = c;	//can be either Height Information (highest Byte != 80) or real Color
		if ((c & OPAQUE) == ColorFlag)		//only special, very improbable Colors are allowed!
			HeightColor = c & TRANSPARENT;	//real Color, set it, else it is a z Information
		//TODO: is it really necessary to discern this? rather use different Methods
		//e.g. setPixel(x,y,z) for the z Component and setPixel(x,y) and setColor(col) for colors.
	}

	/** Method to set the current Painting Color when in gtrMode or lssMode	 */
	public void setHeightColor(Color c) { setHeightColor (c.getRGB()); }  //This returns all! Red, Green, Blue AND Alpha!

	/** Method to set the current Painting Color when in gtrMode or lssMode	 */
	public void setHeightColor(int c) {
		currentColor = c;
		HeightColor  = c & TRANSPARENT;
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Initializing Constructor	 */
	public MemoryImage(final int Height, final int Width) {
		this(Height, Width, null); }

	/**Initializing Constructor	taking a Graphics Context to paint to */
	public MemoryImage(final int Height, final int Width, final Graphics gr) {
		super(new Point2D(0,0), new Point2D(Width-1, Height-1));
		this.gr = gr;
		this.Height = Height;
		this.Width = Width;
		Pixels = new int[Height*Width];
		myImageSource = new MemoryImageSource(Width, Height, Pixels, 0, Width);
//		myImage = Component.createImage(myImageSource);
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Does automatic Clipping!
	 * Doesn't make sense to change the current Position.
	 *
	 * @return the RGB Value of a Pixel at the given Position.
	 *  0 if out of the Clipping Area.
	 */
	public int getPixel(int x, int y) {
		if ((x >= 0) && (x < Width) &&
			(y >= 0) && (y < Height))
			return Pixels[x+Width*y];
		return 0; }

	/** Changes the Color and sets a Pixel at the current Position	 */
	public void setClippedPixel(Color color) { 
		setColor(color); 
		setPixel (); }

	/**Sets a Pixel in the current Color at the current Position.
	 * Clipping is done by setClipPixel()!
	 * Design Decisions:
	 * Normally you would use Polymorphism for this (Template Method),
	 * but this would be too much Code.
	 */
	public void setPixel() {
		int Pos = P.getX() + Width*P.getY();
		switch (currentMode) {
			case setMode:		Pixels[Pos]  = currentColor; break;
			case xorMode:		Pixels[Pos] ^= currentColor; break;
			case or_Mode:		Pixels[Pos] |= currentColor; break;
			case andMode:		Pixels[Pos] &= currentColor; break;
			case addMode:		Pixels[Pos] += currentColor; break;
			case subMode:		Pixels[Pos] -= currentColor; break;
								//for this Color contains Height Information!
								//the actual Color is in HColor!
			case gtrMode:	if (Pixels[Pos]  < currentColor)	//Include the new Height Information
								Pixels[Pos]  = (HeightColor | (currentColor & OPAQUE)); break;
			case lssMode:	if (Pixels[Pos]  > currentColor)	//in the new Color Value
								Pixels[Pos]  = (HeightColor | (currentColor & OPAQUE)); break;
		}
		if (gr != null) {
			gr.drawLine(P.getX(), P.getY(), P.getX(), P.getY()); }
	}

	/** Performs the binary 'or' Operation on ALL Elements of the Picture	 */
	public void setOperation(int arg) {
		int i = Pixels.length;
		while (--i >= 0) {
			Pixels[i] = arg; }
	}

	/** Performs the binary 'or' Operation on ALL Elements of the Picture	 */
	public void orOperation(int arg) {
		int i = Pixels.length;
		while (--i >= 0) {
			Pixels[i] |= arg; }
	}

	/**Performs the binary 'or' Operation on ALL Elements of the Picture	 */
	public void andOperation(int arg) {
		int i = Pixels.length;
		while (--i >= 0) {
			Pixels[i] &= arg; }
	}

	//////////////////////
	//	Optimizations	//
	//////////////////////

	/**
	 * Draws a horizontal Line not using setPixel(),
	 * does not clip anymore!
	 * Optimized for linear Access.
	 */
	public void drawHLine(int x1) {
/*		Log.L.LogStack = true;
		Log.L.ThresholdLog = -200; //log deep Stacks too!
		Log.TraceStack(true);
*/		int xt = x1; if (P.getX() > x1) {x1 = P.getX(); P.setX(xt); }
		int Start = Width*P.getY();
		int Stop  = Start + x1;
		Start += P.getX() -1;	//one off because of pre-incrementing
		switch (currentMode) {
			case setMode: while (++Start <= Stop)   Pixels[Start]  = currentColor; break;
			case xorMode: while (++Start <= Stop)   Pixels[Start] ^= currentColor; break;
			case or_Mode: while (++Start <= Stop)   Pixels[Start] |= currentColor; break;
			case andMode: while (++Start <= Stop)   Pixels[Start] &= currentColor; break;
			case addMode: while (++Start <= Stop)   Pixels[Start] += currentColor; break;
			case subMode: while (++Start <= Stop)   Pixels[Start] -= currentColor; break;
			case gtrMode: while (++Start <= Stop)if(Pixels[Start]  < currentColor)	//Include the new Height Information
													Pixels[Start]  = (HeightColor | (currentColor & OPAQUE)); break;
			case lssMode: while (++Start <= Stop)if(Pixels[Start]  > currentColor)	//in the new Color Value
													Pixels[Start]  = (HeightColor | (currentColor & OPAQUE)); break;
		}
		if (gr != null) {
			gr.drawLine(P.getX(), P.getY(), xt, P.getY()); }
		P.setX(xt);	//Now the Endpoint is the actual Coordinate
	}

	/**Clears the whole Graphics Context with the current Color
	 * specified rectangle by filling it with the background color
	 * of the current drawing surface.
	 * This operation does not use the current paint mode.
	 * <p>
	 * Beginning with Java&nbsp;1.1, the background color
	 * of offscreen images may be system dependent. Applications should
	 * use <code>setColor</code> followed by <code>fillRect</code> to
	 * ensure that an offscreen image is cleared to a specific color.
	 * Clears the whole Graphics Context.
	 * @see java.awt.Graphics#fillRect(int, int, int, int)
	 * @see java.awt.Graphics#drawRect
	 * @see java.awt.Graphics#setColor(java.awt.Color)
	 * @see java.awt.Graphics#setPaintMode
	 * @see java.awt.Graphics#setXORMode(java.awt.Color)
	 * @since JDK1.0
	 */
	public void clear() {
		java.util.Arrays.fill(Pixels, BackColor.getRGB()); //This returns all! Red, Green, Blue AND Alpha!
		if (gr != null) { //
//			gr.clear();
//			gr.drawImage(img,0,0,null);
		}
	}

	/**
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, int, int, int, int, int, int, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final ImageObserver observer) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented!"); 
	}

}
