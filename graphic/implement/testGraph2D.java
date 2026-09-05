package graphic.implement;

import graphic.AGraphText;
import graphic.Bar3D;
import graphic.Figures;
import graphic.Graph2D;
import graphic.JavaGraphic;
import graphic.Line2D;
import graphic.Marker;
import graphic.MemoryImage;
import graphic.PaletteRGB;
import graphic.Point2D;
import graphic.PolyTrigon;
import graphic.ScalarPlotNew;
import graphic.example.Fractal;

import java.awt.Color;
import java.awt.Event;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.Rectangle;

/**This class reads PARAM tags from its HTML host page and sets
 * the color and label properties of the applet. Program execution
 * begins with the init() method.
 * <p>Also serves as a standalone demo/test harness ({@link #main(String[])}) exercising
 * most drawing primitives across the {@code graphic} package.
 *
 * <h2>Collaborators</h2>
 * <table>
 * <tr><th>Type</th><th>Relationship</th></tr>
 * <tr><td>{@link Graph2D}</td><td>primary drawing context used by the paint demos</td></tr>
 * <tr><td>{@link JavaGraphic}</td><td>faster {@link Graph2D} implementation swapped in for double buffering</td></tr>
 * <tr><td>{@link Figures}</td><td>arrow/spline/Bezier demo drawing</td></tr>
 * <tr><td>{@link Bar3D}</td><td>3D bar demo drawing</td></tr>
 * <tr><td>{@link Marker}</td><td>marker-type gallery demo</td></tr>
 * <tr><td>{@link ScalarPlotNew}</td><td>scalar/height-plot demo</td></tr>
 * <tr><td>{@link MemoryImage}</td><td>in-memory Z-ordered bitmap used by {@link #paint2(Graphics)}</td></tr>
 * <tr><td>{@link PaletteRGB}</td><td>RGB/HSB color conversion and palette generation</td></tr>
 * <tr><td>{@link Fractal}</td><td>fractal-line and plasma demo drawing</td></tr>
 * <tr><td>{@link TextureGraphics}</td><td>texture-fill demo used by {@link #paint2(Graphics)} case 4</td></tr>
 * </table>
 * @see Graph2D
 * @see JavaGraphic
 * @see Figures
 * @see Bar3D
 * @see Marker
 * @see ScalarPlotNew
 * @see MemoryImage
 * @see PaletteRGB
 * @see Fractal
 * @see TextureGraphics
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:52:39Z
 * digest: 64a6dfbeaa0fbfb5b1dffda2f08c50499db0225ffd62b1dd604ccc475bb8f968
 * stale: false
 * tags: [code/graphics, code/testing]
 * concepts: [Graph2D Demo Harness]
 * facets: {layer: test, status: legacy, complexity: low}
 * -->
 */
public class testGraph2D extends Frame { //Applet {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Serialization version marker for this {@link Frame} subclass. */
	private static final long serialVersionUID = 1L;

	/** Size of the Graphic Area     */
	final static public int WIDTH = 1024;

	/** Size of the Graphic Area     */
	final static public int HEIGHT = 768;

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** HSB equivalent of red, via {@link PaletteRGB#RGB2HSB(double, double, double)}. */
	double [] HSBred   = PaletteRGB.RGB2HSB(1,0,0);
	/** HSB equivalent of yellow, via {@link PaletteRGB#RGB2HSB(double, double, double)}. */
	double [] HSByell  = PaletteRGB.RGB2HSB(1,1,0);
	/** HSB equivalent of green, via {@link PaletteRGB#RGB2HSB(double, double, double)}. */
	double [] HSBgreen = PaletteRGB.RGB2HSB(0,1,0);
	/** HSB equivalent of cyan, via {@link PaletteRGB#RGB2HSB(double, double, double)}. */
	double [] HSBcyan  = PaletteRGB.RGB2HSB(0,1,1);
	/** HSB equivalent of blue, via {@link PaletteRGB#RGB2HSB(double, double, double)}. */
	double [] HSBblue  = PaletteRGB.RGB2HSB(0,0,1);
	/** HSB equivalent of magenta, via {@link PaletteRGB#RGB2HSB(double, double, double)}. */
	double [] HSBmagen = PaletteRGB.RGB2HSB(1,0,1);
	/** HSB equivalent of black, via {@link PaletteRGB#RGB2HSB(double, double, double)}. */
	double [] HSBblack = PaletteRGB.RGB2HSB(0,0,0);
	/** HSB equivalent of white, via {@link PaletteRGB#RGB2HSB(double, double, double)}. */
	double [] HSBwhite = PaletteRGB.RGB2HSB(1,1,1);

/*	System.out.print("HSB-red:  " + HSBred  [0] + HSBred  [1] + HSBred  [2]);
	System.out.print("HSB-green:" + HSBgreen[0] + HSBgreen[1] + HSBgreen[2]);
	System.out.print("HSB-blue: " + HSBblue [0] + HSBblue [1] + HSBblue [2]);
	System.out.print("HSB-black:" + HSBblack[0] + HSBblack[1] + HSBblack[2]);
	System.out.print("HSB-white:" + HSBwhite[0] + HSBwhite[1] + HSBwhite[2]);
*/
	/** Round-trips {@link #HSBred} back through {@link PaletteRGB#HSB2RGB(double, double, double)}, for verification. */
	double [] RGBred   = PaletteRGB.HSB2RGB(HSBred  [0], HSBred  [1], HSBred  [2]);
	/** Round-trips {@link #HSByell} back through {@link PaletteRGB#HSB2RGB(double, double, double)}, for verification. */
	double [] RGByell  = PaletteRGB.HSB2RGB(HSByell [0], HSByell [1], HSByell [2]);
	/** Round-trips {@link #HSBgreen} back through {@link PaletteRGB#HSB2RGB(double, double, double)}, for verification. */
	double [] RGBgreen = PaletteRGB.HSB2RGB(HSBgreen[0], HSBgreen[1], HSBgreen[2]);
	/** Round-trips {@link #HSBcyan} back through {@link PaletteRGB#HSB2RGB(double, double, double)}, for verification. */
	double [] RGBcyan  = PaletteRGB.HSB2RGB(HSBcyan [0], HSBcyan [1], HSBcyan [2]);
	/** Round-trips {@link #HSBblue} back through {@link PaletteRGB#HSB2RGB(double, double, double)}, for verification. */
	double [] RGBblue  = PaletteRGB.HSB2RGB(HSBblue [0], HSBblue [1], HSBblue [2]);
	/** Round-trips {@link #HSBmagen} back through {@link PaletteRGB#HSB2RGB(double, double, double)}, for verification. */
	double [] RGBmagen = PaletteRGB.HSB2RGB(HSBmagen[0], HSBmagen[1], HSBmagen[2]);
	/** Round-trips {@link #HSBblack} back through {@link PaletteRGB#HSB2RGB(double, double, double)}, for verification. */
	double [] RGBblack = PaletteRGB.HSB2RGB(HSBblack[0], HSBblack[1], HSBblack[2]);
	/** Round-trips {@link #HSBwhite} back through {@link PaletteRGB#HSB2RGB(double, double, double)}, for verification. */
	double [] RGBwhite = PaletteRGB.HSB2RGB(HSBwhite[0], HSBwhite[1], HSBwhite[2]);

	/**The entry point for the applet. 	 */
	public void init() {
		initForm();

		usePageParams();

/*		try{ //only for Appletx
			java.net.URL url;
			url = new java.net.URL("file:///G:/Media/BMP/TEXTURE/");
			url = this.getDocumentBase();
//			Pattern = this.getImage(url, "GRANITE.BMP");	//looks bad
			Pattern = this.getImage(url, "GRANITE.jpg");
			String str = url.toString();
		} catch (Exception x) {
			System.out.println(x.toString()); }
*/	}

	/** HTML host-page PARAM name carrying the label text, read by {@link #usePageParams()}. */
	private final static String labelParam = "label";
	/** HTML host-page PARAM name carrying the background color, read by {@link #usePageParams()}. */
	private final static String backgroundParam = "background";
	/** HTML host-page PARAM name carrying the foreground color, read by {@link #usePageParams()}. */
	private final static String foregroundParam = "foreground";

	/** Reads parameters from the applet's HTML Host Page and sets Applet properties.	 */
	private void usePageParams() {
		final String defaultLabel = "Default label";
		final String defaultBackground = "C0C0C0";
		final String defaultForeground = "000000";
		String labelValue = null;
		String backgroundValue = null;
		String foregroundValue = null;

		/**Read the <PARAM NAME="label" VALUE="some string">,
		 * <PARAM NAME="background" VALUE="rrggbb">,
		 * and <PARAM NAME="foreground" VALUE="rrggbb"> tags from
		 * the applet's HTML host.		 */
/*		     labelValue = getParameter(labelParam);
		backgroundValue = getParameter(backgroundParam);
		foregroundValue = getParameter(foregroundParam);
*/
		if ((labelValue == null) || (backgroundValue == null) ||
			(foregroundValue == null)) {
			/**There was something wrong with the HTML host tags.
			 * Generate default values.			 */
			labelValue = defaultLabel;
			backgroundValue = defaultBackground;
			foregroundValue = defaultForeground;
		}

		/**Set the applet's string label, background color, and foreground colors.		 */
		label1.setText(labelValue);
		label1.setBackground(PaletteRGB.getColor(backgroundValue));
		label1.setForeground(PaletteRGB.getColor(foregroundValue));
		this  .setBackground(PaletteRGB.getColor(backgroundValue));
		this  .setForeground(PaletteRGB.getColor(foregroundValue));
	}

	/** External interface used by design tools to show properties of an applet.	 */
	public String[][] getParameterInfo() {
		String[][] info = {
			{ labelParam, "String", "Label string to be displayed" },
			{ backgroundParam, "String", "Background color, format \"rrggbb\"" },
			{ foregroundParam, "String", "Foreground color, format \"rrggbb\"" },
		};
		return info; }

	/** Label component displaying the text/colors read by {@link #usePageParams()}. */
	Label label1 = new Label();

	/**
	 * Intializes values for the applet and its components
	 */
	void initForm() {
		this.setBackground(Color.lightGray);
		this.setForeground(Color.black);
		label1.setText("label1");
//		this.setLayout(new BorderLayout());
//		this.add("North",label1);
	}

	/** Last x-coordinate reported to {@link #mouseMove(Event, int, int)}, for erasing the previous marker. */
	int xOld;
	/** Last y-coordinate reported to {@link #mouseMove(Event, int, int)}, for erasing the previous marker. */
	int yOld;
	/** Bounds of the coordinate label last drawn by {@link #mouseMove(Event, int, int)}. */
	Rectangle TextBox = new Rectangle();
	/** Coordinate label text last drawn by {@link #mouseMove(Event, int, int)}. */
	String strOld;

	/** Width of the {@link MemoryImage} buffer used by {@link #paint2(Graphics)}. */
	private int width  = 1024;
	/** Height of the {@link MemoryImage} buffer used by {@link #paint2(Graphics)}. */
	private int height = 768;

	/** X-coordinates of the red demo polygon drawn by {@link #paint2(Graphics)}. */
	int[] xP1 = {100, 900, 900, 100};
	/** X-coordinates of the green demo polygon drawn by {@link #paint2(Graphics)}. */
	int[] xP2 = {200, 100, 600, 700};
	/** X-coordinates of the blue demo polygon drawn by {@link #paint2(Graphics)}. */
	int[] xP3 = {800, 900, 400, 300};

	/** Y-coordinates of the red demo polygon drawn by {@link #paint2(Graphics)}. */
	int[] yP1 = {400, 400, 500, 500};
	/** Y-coordinates of the green demo polygon drawn by {@link #paint2(Graphics)}. */
	int[] yP2 = {600, 500, 100, 200};
	/** Y-coordinates of the blue demo polygon drawn by {@link #paint2(Graphics)}. */
	int[] yP3 = {600, 500, 100, 200};

	/** X-coordinates of the red height-plot polygon used in {@link #paint2(Graphics)} case 2. */
	int[] xP4 = {100, 400, 500};
	/** X-coordinates of the green height-plot polygon used in {@link #paint2(Graphics)} case 2. */
	int[] xP5 = { 50, 600, 650};

	/** Y-coordinates of the red height-plot polygon used in {@link #paint2(Graphics)} case 2. */
	int[] yP4 = {500, 100, 600};
	/** Y-coordinates of the green height-plot polygon used in {@link #paint2(Graphics)} case 2. */
	int[] yP5 = {300, 400, 550};

	/** Per-vertex height (Z) values for {@link #xP4}/{@link #yP4}, packed into the color's high byte. */
	int[] zP4 = {- 1<<24, -64<<24, -64<<24};
	/** Per-vertex height (Z) values for {@link #xP5}/{@link #yP5}, packed into the color's high byte. */
	int[] zP5 = {-48<<24, -48<<24, -48<<24};

	//Works only if the Difference does not exceed the maximum int Range!
	/** Per-vertex height (Z) values for {@link #xP1}/{@link #yP1}, packed into the color's high byte. */
	int[] zP1 = { 0<<24,-62<<24,-62<<24, 0<<24};
	/** Per-vertex height (Z) values for {@link #xP2}/{@link #yP2}, packed into the color's high byte. */
	int[] zP2 = {-62<<24,-62<<24, 0<<24, 0<<24};
	/** Per-vertex height (Z) values for {@link #xP3}/{@link #yP3}, packed into the color's high byte. */
	int[] zP3 = { 0<<24, 0<<24,-62<<24,-62<<24};

	/** Counter for the key Presses */
	protected int state = 0;

	/** Increases the Counter for the key Presses */
	public boolean keyDown(Event evt, int key) {
		switch (key) {
			case 13: state++; break;	//CarriageReturn
		}
		this.repaint();
		return true; }

	/**Image to use for filling Polygons	 */
	protected Image Pattern;

	/**
	 * This Event has a Default Method inherited from Applet1,
	 * that does nothing but returning true.
	 * Writing this Routine overwrites that Default Method.
	 * Displays a Circle painted in XOR Mode
	 * and the Coordinates written to a Double Buffer.
	 */
	public boolean mouseMove(Event e, int x, int y) {
		Graphics g = this.getGraphics();
		if (g == null) { return true; }
		g.drawImage (img,
			TextBox.x, TextBox.y, TextBox.x+TextBox.width, TextBox.y-TextBox.height,
			TextBox.x, TextBox.y, TextBox.x+TextBox.width, TextBox.y-TextBox.height, this);
//		Color bufCol = g.getColor();
		g.setXORMode(Color.white);	//Maximum Contrast, except for 0x80, which flips with 0x7F
//		g.setColor (Color.cyan);
		g.drawOval(xOld-10, yOld-10, 8, 8); //Circle in XOR
		xOld = x;
		yOld = y;
		strOld = "(" + xOld + "," + yOld +")";
		TextBox.x = x;
		FontMetrics ftM = g.getFontMetrics();
		TextBox.y = y+ftM.getDescent();
		TextBox.height = ftM.getHeight();
		TextBox.width = ftM.stringWidth(strOld);
		g.drawOval(xOld-10, yOld-10, 8, 8);
		g.setPaintMode();
		g.drawString (strOld, xOld, yOld);	//drawString doesn't work in XOR-Mode!
		return true; }

	/**
	 * This Event has a Default Method inherited from Applet1,
	 * that does nothing but returning true.
	 * Writing this Routine overwrites that Default Method.
	 *
	 * Simulates the Graphics for a Button Press
	 */
	public boolean mouseDown(Event e, int x, int y) {
		Graphics g = getGraphics();
		if (g == null) { return true; }
		g.setColor(Color.lightGray);
		g.draw3DRect( 70, 110, 50, 20, false);
		g.fill3DRect(140, 110, 50, 20, false);
		return true; }

	/**This Event has a Default Method inherited from Applet1,
	 * that does nothing but returning true.
	 * Writing this Routine overwrites that Default Method.
	 */
	public boolean mouseUp(Event e, int x, int y) {
		Graphics g = getGraphics();
		if (g == null) { return true; }
		g.setColor(Color.lightGray);
		g.draw3DRect( 70, 110, 50, 20, true);
		g.fill3DRect(140, 110, 50, 20, true);

		//paints a fractal Line
		g.setColor(Color.black);
		g2D = new JavaGraphic(g);
		Figures f = new Figures(g2D);
		f.Waviness = Waviness;
		g2D.P.setLocation(ClipTL);
		f.drawFracLine(ClipBR);
		if (Waviness < 2.0) Waviness += 0.1;
		return true; }

	/** Marker demo used by {@link #paintSimple(Graphics)} to draw the marker-type gallery. */
	Marker Mk;
	/** Figures demo used by {@link #paintSimple(Graphics)} for arrows, splines and Bezier curves. */
	Figures Bx;	//Boxes
	/** 3D-bar demo used by {@link #paintSimple(Graphics)} to draw filled bars. */
	Bar3D B3D;	//3D Bars
	/** Current {@link Graph2D} drawing context, reassigned per paint call and demo section. */
	Graph2D g2D;

	/** Top-left clip corner used by {@link #mouseUp(Event, int, int)}'s fractal-line demo. */
	Point2D ClipTL = new Point2D( 60, 50);
	/** Bottom-right clip corner used by {@link #mouseUp(Event, int, int)}'s fractal-line demo. */
	Point2D ClipBR = new Point2D(260, 260);
	/** Fractal-line waviness, incremented on each {@link #mouseUp(Event, int, int)} up to 2.0. */
	double Waviness = 0.0;

	/** Cyclic 300-color palette shared by the scalar-plot demos, cached for reuse. */
	Color[] Palette = PaletteRGB.CYCLE_PALETTE(300); //cached for Reuse!
	/** Scalar-plot demo context created per {@link #paint(Graphics)} call. */
	ScalarPlotNew SP;

	/** Double-buffer image maintained by {@link #paintSimple(Graphics)} and drawn by {@link #paint2(Graphics)}. */
	Image img;
	/** The Clipping Bounds of the Window are determined e.g. 
	 * by the Height of it's Title Bar!	 */
	Rectangle Res;

	/**
     * Fundamental Painting Method for Frames and Applets
     * Just performs the Double Buffering here!
     */
	public void paint(Graphics g) {
		Res = g.getClipBounds();
//		g2D = new JavaGraphic(g, ClipBR, ClipTL);
//		g2D = new JavaGraphic(g);
		g2D = new Graph2D(g);
//		g2D.PenPattern = g2D.DashPenPattern(2);
//		g2D.BrushPattern = g2D.HBrushPattern(g2D.PenPattern);
		SP = new ScalarPlotNew(g2D, Palette);
		int gMode = 0;
		do paintSimple(g); while (++gMode <= 1);
		paintBars(g);
		Fractal.paintFractals(g2D);
		AGraphText.testPaintChars(this, g);	/**Output of Characters	 */
		try {
			System.out.println("Press Enter");
			System.in.read();
		} catch (Exception X){
			System.out.println(X);}
		paint2(g);
		Fractal.paintPlasma(SP);
		ScalarPlotNew.paintScalarObjects(g2D, Palette);
	}

	/**
	 * Demonstrates basic Routines for setting Colors
	 * and drawing/filling simple Shapes, Lines and Planes
	 */
	public void paintSimple(Graphics g) { //Test basic Routines for setting Colors and drawing/filling simple Shapes
//		Shape Shp = g.getClip(); //The ClipRect of g determines the Range to repaint!

		g.setColor(Color.cyan);
		g.drawRect(Res.x, Res.y, Res.width-1, Res.height-1);

//		g.setXORMode(Color.yellow);
		g2D.setColor (Color.red);
		g2D.drawRect (ClipTL, ClipBR);
		g2D.setColor (Color.black);
		g2D.drawLine ( 0, 50, 100, 0);
		g2D.drawHLine (50, 100, 50);
		g2D.drawVLine (50, 100, 0);
//		g2D.setColor (Color.red);
//		g2D.draw3DBar(30, 60, 90, 40);
		final int[] x = { 30,  10,  10,  30,  50, 50};
		final int[] y = { 20,  30,  50,  60,  50, 30};
		g2D.drawPolygon(x,y, true);	//draw the same Polygon
		g2D.drawPolygon(y,x, true);	//with x and y interchanged
		g2D.fillTriangle(200, 100, 300, 200, 300, 400);
		g2D.fillPolygon(PolyTrigon.convertPolygon(x, y));

		//try both Implementations of the Polygon Rendering Method. 
		final int[] x1 = { 100, 100, 200, 200};
		final int[] y1 = { 100, 200, 100, 200};
		g2D.fillPolygon(PolyTrigon.convertPolygon(x1, y1), Color.GREEN, Color.RED);
		g2D.fillPolygon(x1, y1, Color.GREEN, Color.RED);
		
		Point2D M = new Point2D(100,100);
		Point2D R = new Point2D(80,40);

		g2D.drawEllipse(M, R);
		R.setX(10); R.setY(20);
		Line2D L = new Line2D(new Point2D(240, 240), M);

		g2D.drawRoundRect(L, R);
		L.setCenter(M); g2D.drawRect(L);

		R.setX(60); R.setY(40);
		g2D.drawArc(M, R, new Point2D(20, 140));
//		g2D.drawSector(M, R, new Point2D(20, 140));

		g2D.setColor(Color.gray);
		g2D.fill3DRect(140, 110, 50, 20, true);
		g2D.draw3DRect( 70, 110, 50, 20, true);

		g2D.setColor(Color.darkGray);
		Mk = new Marker(g2D);
		for (Mk.MarkerType = 0; Mk.MarkerType <= Marker.CountMarker; Mk.MarkerType++)
		{Mk.mark(Mk.MarkerType * 15 + 60, Mk.MarkerType * 15 + 60);}

		Bx = new Figures(g2D);
		B3D = new Bar3D (g2D);
//		Bx.draw3DBar(40, 200, 80, 100);
		B3D.fill3DBar(40, 350, 80, 250);
		g2D.setColor(Color.gray);
		B3D.fill3DBar(115, 350, 155, 250);
		Point2D Center = new Point2D(200, 200);
		Point2D Radius = new Point2D(50, 50);
		Bx.drawArrow(Center, Radius);
		Radius.setY(-Radius.getY());
		Bx.drawArrow(Center, Radius);
		Radius.setX(-Radius.getX());
		Bx.drawArrow(Center, Radius);
		Radius.setY(-Radius.getY());
		Bx.drawArrow(Center, Radius);
		Bx.drawArrow(Center, new Point2D(25, 25));
		Radius.setX(0);
		Bx.drawArrow(Center, Radius);
		Radius.setY(-Radius.getY());
		Bx.drawArrow(Center, Radius);
		Radius.setX(Radius.getY()); Radius.setY(0);
		Bx.drawArrow(Center, Radius);
		Radius.setX(-Radius.getX());
		Bx.drawArrow(Center, Radius);

		int[] iPX = {300, 100, 100, 300, 500, 500};
		int[] iPY = {200, 300, 500, 600, 500, 300};
		Point2D[] iPP = {new Point2D(300,200), new Point2D(100,300), new Point2D(100,500),
						 new Point2D(300,600), new Point2D(500,500), new Point2D(500,300)};
		g.setColor(Color.cyan);
		iPX = Bx.periodicSpline (iPX);
		iPY = Bx.periodicSpline (iPY);
		Bx.Bezier  (iPX, iPY, 20);
		iPP = Bx.periodicSpline (iPP);
		g.setColor(Color.blue);
		Bx.Bezier  (iPP, 0, false);
		Bx.Bezier  (iPP, 1, false);
		Bx.Bezier  (iPP, 2, false);
		Bx.Bezier  (iPP, 3, false);
		Bx.Bezier  (iPP, 4, false);
		Bx.Bezier  (iPP, 5, false);
		Bx.Bezier  (iPP, 6, false);
//			g2D.drawPolygon (iPX, iPY, true);
//			Bx.Bezier2  (PX, PY, 20);

//		g2D.clearRect (80, 80, 160, 160);
//			g2D = new Graph2D(g);
		g2D = new JavaGraphic(g);	//switch to JavaGraphic with improved Speed
		img = g2D.doubleBuffer(this, img);	//do DoubleBuffer() -ing
	}

	/**Demonstrates Drawing of Histograms and 3D Bars and a 2D Raster
	 * Commented out for Dependency to Raster	 */
	private void paintBars(Graphics g) {
	}/*		g2D = new JavaGraphic(g);
		img = g2D.doubleBuffer(this, img);

		Point2D P;
		int yP[] = new int[16];
		g.setColor(Color.gray);
		Coordinates2D C2d = new Coordinates2D(-1.6f , +1.6f  , -1 , +1, g.getClipBounds());
		Mk.MarkerType = Mk.FenceMarker;
		float x = -1.6f; int i = -1;
		while (++i < yP.length) {
			float y = (float) Math.sin(x);
			P = C2d.map(x,y);
			Mk.Marker(P);
			yP[i] = P.y;
			x+=0.2; }
		B3D.Bar3DRaised	= true;
		B3D.Bar3DSide	= true;
		B3D.Bar3DTop	= true;
		B3D.Histogram(yP, Res.height >>1, true , true, true, true);
		B3D.Histogram(yP, Res.width  >>1, false, true, true, true);

		Bx.Ordinates2D(Res.width  >>1, Res.height >>1);
		float[] xR = MathGraph2.proposeRaster(-1.6f , +1.6f);
		float[] yR = MathGraph2.proposeRaster(-1 , +1);
		int[] xiR = C2d.MapX.map(xR);
		int[] yiR = C2d.MapY.map(yR);
		Bx.drawRaster(xiR, yiR);
	}
	*/

	/**
	 * Demonstrates the Capabilities of combining Colors of subsequent Drawings
	 * to simulate partial transparency as well as drawing by Height
	 * and filling with a Pattern/Texture.
	 */
	public void paint2(Graphics g) {
//		throws FileNotFoundException, IOException	//not possible!
		MemoryImage memImage = new MemoryImage(height,width);	//BitMap in Memory with ZOrder
		Image img = this.createImage(memImage.getImageSource());
		switch (state) {
			case 0:	//adds up the Colors, or-ing would be better though
				memImage.currentMode = MemoryImage.addMode;
				memImage.setColor (Color.red  ); memImage.fillPolygon(xP1, yP1);
				memImage.setColor (Color.green); memImage.fillPolygon(xP2, yP2);
				memImage.setColor (Color.blue ); memImage.fillPolygon(xP3, yP3);
				memImage.orOperation(MemoryImage.OPAQUE);
				g.drawImage(img,0,0,null);	//The highest Byte determines the Saturation with which the Color is drawn
				break;
			case 1:	//Drawing for Heights (only painted when higher/closer)
				memImage.currentMode = MemoryImage.lssMode;
				ScalarPlotNew SP = new ScalarPlotNew(memImage, null);
		//		memImage.orOperation(Integer.MIN_VALUE); //==0x80000000//if you are using lssMode and only negative z-Values, you don't even have to initialize the Color! Additionally you automatically skip all Items on the backside!
				memImage.setHeightColor (Color.red	); SP.ScalarPolygon (xP1, yP1, zP1);
				memImage.setHeightColor (Color.green); SP.ScalarPolygon (xP2, yP2, zP2);
				memImage.setHeightColor (Color.blue	); SP.ScalarPolygon (xP3, yP3, zP3);
				memImage.orOperation(MemoryImage.OPAQUE);	//necessary for opaque Drawing!
				g.drawImage(img,0,0,null);	//The highest Byte determines the vertical Position of the Color
				break;
			case 2:	//Drawing for Heights
				memImage.currentMode = MemoryImage.lssMode;
				SP = new ScalarPlotNew(memImage, null);
				memImage.setOperation(0);
		//		memImage.orOperation(Integer.MIN_VALUE); //==0x80000000//if you are using lssMode and only negative z-Values, you don't even have to initialize the Color! Additionally you automatically skip all Items on the backside!
				memImage.setHeightColor (Color.red	); SP.ScalarPolygon (xP5, yP5, zP5);
				memImage.setHeightColor (Color.green); SP.ScalarPolygon (xP4, yP4, zP4);
				memImage.orOperation(MemoryImage.OPAQUE);	//necessary for opaque Drawing!
				g.drawImage(img,0,0,null);	//The highest Byte determines the vertical Position of the Color
				break;
			case 3:	g.drawImage(Pattern,0,50,null);	//Draw the filling Image first
				break;
			case 4:	//use the Pattern to render the Polygon
				//convert is to a Bitmap in Memory (row-wise???)
				//using the PixelGrabber Object.
				//use it for setting Pixels
				//use it to draw whole Rows.
				TextureGraphics PG = new TextureGraphics(Pattern, g);
				PG.fillPolygon(xP1, yP1);
				break;
		}
	}

////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

/** Tests all Methods of this Class	 */
public static void testIt(String[] args) throws java.io.IOException {
	System.out.println("Testing " + testGraph2D.class.getName());
	Frame f = new testGraph2D(); //Frame();
	f.setSize(WIDTH, HEIGHT);
	f.show();
}

/**The main entry point for the application.
 *
 * @param args Array of parameters passed to the application
 * via the command line.	 */
public static void main (String[] args) throws java.io.IOException {
	testIt(args); }

}
