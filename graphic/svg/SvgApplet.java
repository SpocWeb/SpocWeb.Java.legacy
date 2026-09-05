/*
 * File Name: SvgFrame.java
 * Created on: 04.07.2003
 *
 */
package graphic.svg;
import graphic.IGraphImage;
import graphic.IGraphText;
import graphic.Point2D;
import graphic.math2D.Coord2DMouseController;
import graphic.math2D.Coordinates2D;
import graphic.mvc.BaseApplet;
import graphic.mvc.IRepainter;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;

import math.vector.VectorString;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;

import streamIO.Log;
import technology.xml.SaxDispatcher;

/**
 * Title: SvgApplet<p>
 * Description:
 * Purpose:
 *
 * Instances of this Class can either act as Applets 
 * embedded in an AppletViewer or Browser
 * or as a standalone Frame if started via Main. 
 * It displays an SVG File based on its URL 
 * either given as the first Command Line Parameter 
 * or as an Applet Parameter named 'svgFile'
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
 * mtime: 2026-09-05T11:48:29Z
 * digest: cd5372a949813412e28c3269508728e2136d6d10dd580b31f5fe776a330059aa
 * stale: false
 * tags: [code/rendering, code/parsing]
 * concepts: [SVG Applet Renderer]
 * facets: {layer: utility, status: broken, complexity: high}
 * -->
 */
public class SvgApplet 
extends BaseApplet 
implements IRepainter { // 

	/** Serialization version identifier for this class. */
	private static final long serialVersionUID = 1L;
	/** Local Logger Class */
	private final Log L = new Log(SvgApplet.class, 1);

	////////////////////////////////////////////////////////////////////////////////////
	/// Helper Methods extracted from their respective Classes
	////////////////////////////////////////////////////////////////////////////////////

	/** Character Constant */
	/*final static public char CHR_PERCENT = '%'; 

	final static public double PERCENT = 0.01;

	/**
	 * Parses the given String and returns it in ret. 
	 * @param str String to be parsed
	 * @param delims Characters to parse by
	 * @return an Array filled with the parsed Values 
	 * @see VectorString#PARSE(String)
	 */
	/*final static public String[] PARSE(final String str) {
		StringTokenizer tokenizer = new StringTokenizer(str);
		String[] ret = new String[tokenizer.countTokens()];
		if (ret.length != PARSE(ret, tokenizer)) {
			throw new ArrayIndexOutOfBoundsException("Error during Parsing!"); } 
		return ret;  
	}

	/**
	 * Parses the given String and returns it in ret. 
	 * @param ret Container to hold the parsed Strings
	 * @param str String to be parsed
	 * @param delims Characters to parse by
	 * @return the Number of Elements filled (from 0 to n-1)
	 * @see VectorString#PARSE(String[], StringTokenizer) 
	 */
	/*private static final int PARSE(String[] ret, StringTokenizer tokenizer) {
		int i = -1; 
		for(;++i < ret.length;) {
			if(!tokenizer.hasMoreElements()) {
				break; 
			}
			ret[i] = tokenizer.nextToken(); 
		}
		return i; 
	}

	/** Helper Method for decoding a String with absolute or Percentage Value 
	 * @see VectorString#STRING2DOUBLE(String, double) */
	/*final static public double STRING2DOUBLE(final String strValue, final double defaultWidth) {
		return STRING2DOUBLE(strValue, defaultWidth, 0); 
	}
	
	/** Helper Method for decoding a String with absolute or Percentage Value 
	 * @return 0 if null
	 * @see VectorString#STRING2DOUBLE(String, double, double) */
	/*final static public double STRING2DOUBLE(final String strValue, final double defaultWidth, final double defaultOffset) {
		if(strValue == null) { //assume 100%
			return 0; //defaultOffset+defaultWidth; 
		}
		final int strLen_1 = strValue.length()-1;
		if(strValue.charAt(strLen_1) == CHR_PERCENT) {
			return defaultOffset+defaultWidth*Float.parseFloat(strValue.substring(0, strLen_1))*PERCENT;
		}
		return Float.parseFloat(strValue); //absolute Value 
	}

	/** Helper Method for decoding a String with absolute or Percentage Value */
	/*final static public double[] STRING2DOUBLE(final String[] strValues) {
		double[] ret = new double[strValues.length];
		for (int i = ret.length; --i >= 0;) {
			ret[i] = Double.parseDouble(strValues[i]); 
		}
		return ret; 
	}
	*/	
	/** Helper Routine to print out the Attributes to a Stream */
	final static public void PRINT_ATTS(final Attributes atts, PrintStream stream) {
		for(int i = atts.getLength(); --i >= 0;) {
			stream.println(atts.getQName(i)+":"+atts.getValue(i));
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants for the XML Grammar
	////////////////////////////////////////////////////////////////////////////////
	/// Elements
	/** SVG element name {@code svg}, the root element. */
	final static public String STR_ELM_SVG = "svg";
	/** SVG element name {@code ellipse}. */
	final static public String STR_ELM_ELLIPSE = "ellipse";
	/** SVG element name {@code rect}. */
	final static public String STR_ELM_RECT = "rect";
	/** SVG element name {@code image}. */
	final static public String STR_ELM_IMAGE = "image";
	/** SVG element name {@code circle}. */
	final static public String STR_ELM_CIRCLE = "circle";
	/** SVG element name {@code line}. */
	final static public String STR_ELM_LINE = "line";
	/** SVG element name {@code text}. */
	final static public String STR_ELM_TEXT = "text";
	/** SVG element name {@code polygon}. */
	final static public String STR_ELM_POLYGON = "polygon";
	/** SVG element name {@code polyline}. */
	final static public String STR_ELM_POLYLINE = "polyline";
	/** SVG element name {@code path}. */
	final static public String STR_ELM_PATH = "path";
	/** SVG element name {@code a} (anchor/link). */
	final static public String STR_ELM_A = "a";
	/** SVG element name {@code g} (group). */
	final static public String STR_ELM_G = "g";
	/** SVG element name {@code desc} (description). */
	final static public String STR_ELM_DESC = "desc";
	/** Empty element-name placeholder. */
	final static public String STR_ELM_ = "";

	/// Attributes
	/** SVG attribute name {@code viewBox}. */
	final static public String STR_ATTR_VIEW_BOX = "viewBox";
	/** SVG attribute name {@code width}. */
	final static public String STR_ATTR_WIDTH = "width";
	/** SVG attribute name {@code height}. */
	final static public String STR_ATTR_HEIGHT = "height";
	/** SVG attribute name {@code x1}, the line start X coordinate. */
	final static public String STR_ATTR_X1 = "x1";
	/** SVG attribute name {@code x2}, the line end X coordinate. */
	final static public String STR_ATTR_X2 = "x2";
	/** SVG attribute name {@code y1}, the line start Y coordinate. */
	final static public String STR_ATTR_Y1 = "y1";
	/** SVG attribute name {@code y2}, the line end Y coordinate. */
	final static public String STR_ATTR_Y2 = "y2";
	/** SVG attribute name {@code x}. */
	final static public String STR_ATTR_X = "x";
	/** SVG attribute name {@code y}. */
	final static public String STR_ATTR_Y = "y";
	/** SVG attribute name {@code xlink:href}, the referenced resource URI. */
	final static public String STR_ATTR_HREF = "xlink:href";
	/** SVG attribute name {@code cx}, the ellipse/circle center X coordinate. */
	final static public String STR_ATTR_CX = "cx";
	/** SVG attribute name {@code cy}, the ellipse/circle center Y coordinate. */
	final static public String STR_ATTR_CY = "cy";
	/** SVG attribute name {@code rx}, the ellipse X radius. */
	final static public String STR_ATTR_RX = "rx";
	/** SVG attribute name {@code ry}, the ellipse Y radius. */
	final static public String STR_ATTR_RY = "ry";
	/** SVG attribute name {@code font-size}. */
	final static public String STR_ATTR_FONT_SIZE = "font-size";
	/** Empty attribute-name placeholder. */
	final static public String STR_ATTR_ = "";

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////

	/** intermediate Variable to save handing it down the Stack */
	IGraphImage graph;

	/** sets the Graphics Context for the current Operation */
	public void setGraph(IGraphImage graph_) {
		graph = graph_;
	}

	/** Whether the Applet's bounds are squared before mapping coordinates, so X and Y scale equally. */
	protected boolean equiScale = true;

	/** Current coordinate transform between SVG user space and Applet pixel space, or {@code null} before {@link #svg(Attributes)} sets it. */
	protected Coordinates2D trafo;

	/**
	 * Returns the current coordinate transform.
	 * @param bounds currently unused; the transform is returned regardless of this argument
	 */
	// TODO: LOGIC: the bounds parameter is ignored and the transform is never recomputed from it;
	// every caller passes null, so this is effectively a no-arg getter for #trafo.
	protected Coordinates2D getTrafo(final Rectangle bounds) {
		return trafo;
	}

	/** Sets the coordinate transform and installs a mouse controller for it on this Applet. */
	// TODO: LOGIC: adds a new Coord2DMouseController via addMouseListener/addMouseMotionListener
	// on every call without removing the listeners installed by a previous call, so repeated
	// invocations (this method is protected and callable by subclasses) accumulate duplicate listeners.
	protected void setTrafo(final Coordinates2D trafo_) {
		this.trafo = trafo_;
		Coord2DMouseController ctrl = new Coord2DMouseController(trafo, this);
		addMouseListener(ctrl);
		addMouseMotionListener(ctrl);
	}
	
	/** Reference to the Coordinates used */
	private double[] coords;
	
	/** Cache for the Applet Size */
	private Rectangle lastBounds;
		
	////////////////////////////////////////////////////////////////////////////////
	/// #region : private Methods 
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	/// #region : Graphics Model Methods
	////////////////////////////////////////////////////////////////////////////

	/** retrieves the relative or absolute Values from the Attributes */
	private final double[] getRectangle(final Attributes atts) {
		double[] rect = new double[4];
		GET_RECTANGLE(rect, coords, atts);
		return rect;
	}

	/** retrieves the relative or absolute Values from the Attributes */
	private static final void GET_POINT(
		final double[] ret,
		final double[] baseRect,
		final Attributes atts,
		final String suffix,
		final int offset) {
		ret[offset] =
			VectorString.STRING2DOUBLE(
				atts.getValue(STR_ATTR_X + suffix),
				baseRect[2],
				baseRect[0]);
		ret[offset + 1] =
		VectorString.STRING2DOUBLE(
				atts.getValue(STR_ATTR_Y + suffix),
				baseRect[3],
				baseRect[1]);
	}

	/** retrieves the relative or absolute Values from the Attributes */
	private static final void GET_RECTANGLE(
		final double[] ret,
		final double[] baseRect,
		final Attributes atts) {
		GET_POINT(ret, baseRect, atts, "", 0);
		GET_WIDTH(ret, baseRect, atts);
	}

	/** retrieves the relative or absolute Values from the Attributes */
	private static final void GET_WIDTH(
		final double[] ret,
		final double[] baseRect,
		final Attributes atts) {
		ret[2] =
			VectorString.STRING2DOUBLE(
				atts.getValue(STR_ATTR_WIDTH),
				baseRect[2]);
		//for relative Values use the Width!
		ret[3] =
			VectorString.STRING2DOUBLE(
				atts.getValue(STR_ATTR_HEIGHT),
				baseRect[3]);
		//for relative Values use the Heigth!
	}

	/** retrieves the relative or absolute Values from the Attributes */
	private static final void GET_LINE(
		final double[] ret,
		final double[] baseRect,
		final Attributes atts) {
		GET_POINT(ret, baseRect, atts, "1", 0);
		GET_POINT(ret, baseRect, atts, "2", 2);
	}

	////////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods 
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * empty Constructor for Applet Use
	 */
	public SvgApplet() {}
	
	/**
	 * Creates an instance that will load the SVG document at the given URI.
	 * @param svgUri_ the URI of the Graphics to paint
	 */
	public SvgApplet(final String svgUri_) {
		this.svgUri = svgUri_;
	}
	
	/** The Dispatcher to the Methods of this Class */
	private SaxDispatcher saxDispatcher = new SaxDispatcher(this, false);

	/** Reference to the URI to draw */
	private String svgUri;

	/** 
	 * Processes the given Document 
	 * which describes which Documents to load, merge transform and output. 
	 */
	protected void paintFrame(final IGraphText gText) {
		assert null!=L.traceStack().n("paintFrame");
		System.out.println("parsing File:"+svgUri);
		if (svgUri == null) {
			return; } 
		this.graph = (IGraphImage) gText;
		try {
			saxDispatcher.parse(svgUri);
		} catch (Exception x) {
			x.printStackTrace();
		}
		//super.paintFrame(gText);
	}

	/**Event- Callback for (re-) painting the Applet or Frame,
	 * triggered by Resizing or (un-)hiding Portions of the Window.  
	 * try Block for catching the Exceptions
	 */
	final public void paint(final Graphics g) {
		if (g == null) return;
		try {
			//if (this instanceof Applet) { }  //only necessary for Applet (lightweight)
			//clear(g);
			IGraphImage graph = getIGraphImage(g); //GraphicsAdapter(g); //Graph2D(g); //
			//painters.draw(graph);
			paintFrame(graph);
		} catch (Exception t) {
			System.err.println(
				"The following Exception occured during Painting:\n");
				t.printStackTrace(System.err);
		}
	}
		
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Element Names of the SVG Language Definition
	////////////////////////////////////////////////////////////////////////////////

	/** Root Element;
	 * Sets the Graphics Context, but only if not set yet!
	 * 
	 * @param atts Attrributes for Height, Width etc. 
	 */
	public void svg(final Attributes atts) {
		if (getTrafo(null) != null) { //
			return; //don't calculate the Coordinates anew!
		} //rather use the now manually modified Scaling
		final double[] rect = { -getWidth(), -getHeight(), getWidth(), getHeight()};
		if ((atts.getIndex(STR_ATTR_X) >= 0)
			|| (atts.getIndex(STR_ATTR_Y) >= 0)) {
			GET_POINT(rect, rect, atts, "", 0);
			setLocation((int) rect[0], (int) rect[1]);
		}
		if ((atts.getIndex(STR_ATTR_WIDTH) >= 0)
			|| (atts.getIndex(STR_ATTR_HEIGHT) >= 0)) {
			GET_WIDTH(rect, rect, atts);
			setSize((int) rect[2], (int) rect[3]);
		}
		Rectangle bounds = this.getBounds(); //graph.getClipBounds();
		if (lastBounds != null) {
			if ((bounds.width != lastBounds.width)
				|| (bounds.height != lastBounds.height)) {
				lastBounds = bounds; 
				this.repaint();
			}
		} 
		lastBounds = bounds;   
		if (equiScale) {
			bounds = this.getBounds(); 
			bounds.height = bounds.width = (bounds.height + bounds.width) >> 1;
		}
		String strViewBox = atts.getValue(STR_ATTR_VIEW_BOX); //
		if (strViewBox != null) { //mapped Coordinates
			coords = VectorString.STRING2DOUBLE(VectorString.SPLIT(strViewBox));
			setTrafo(
				new Coordinates2D(coords[0], coords[2], coords[1], coords[3], bounds));
			coords[2] -= coords[0]; //use widths form now on... 
			coords[3] -= coords[1];
		}
	}
	
	/**
	 * paints an Ellipse
	 * @param atts Attrributes for Height, Width etc. 
	 */
	public void ellipse(final Attributes atts) {
		//double[] rect = getRectangle(atts);
		double cx =
			VectorString.STRING2DOUBLE(
				atts.getValue(STR_ATTR_CX),
				coords[2],
				coords[0]);
		double cy =
			VectorString.STRING2DOUBLE(
				atts.getValue(STR_ATTR_CY),
				coords[3],
				coords[1]);
		double rx =
			VectorString.STRING2DOUBLE(atts.getValue(STR_ATTR_RX), coords[2]);
		double ry =
			VectorString.STRING2DOUBLE(atts.getValue(STR_ATTR_RY), coords[3]);
		Point2D M = getTrafo(null).mapPt(cx, cy);
		Point2D R = getTrafo(null).scale(rx, ry);
		R.setX(Math.abs(R.getX()));
		R.setY(Math.abs(R.getY()));
		graph.drawEllipse(M, R);
	}
	
	/** just to notify the User */
	public void circle(final Attributes atts) throws NoSuchMethodException {
		throw new NoSuchMethodException("use <ellipse> instead!");
	}
	
	/**
	 * paints a RectAngle
	 * @param atts Attrributes for Height, Width etc. 
	 */
	public void rect(final Attributes atts) {
		double[] rect = getRectangle(atts);
		Point2D P1 = getTrafo(null).mapPt(rect[0], rect[1]);
		Point2D P2 = getTrafo(null).mapPt(rect[0]+rect[2], rect[1]+rect[3]);
		graph.drawRect(P1, P2);
	}
	
	/**
	 * paints an Image Object
	 * @param atts Attrributes for Height, Width etc. 
	 */
	// TODO: SECURITY: the xlink:href attribute of an <image> element is untrusted content coming
	// from the parsed SVG document; it is concatenated into a URL and fetched below with no
	// validation (scheme allow-list, path-traversal check), letting a malicious SVG file make this
	// Applet fetch an arbitrary URL (SSRF-like) or read an arbitrary local file.
	public void image(final Attributes atts) throws MalformedURLException {
		final double[] rect = getRectangle(atts); //% are relative to the left, top Border
		final Point2D P1 = getTrafo(null).mapPt(rect[0]-coords[0], rect[1]);
		final Point2D P2 = getTrafo(null).scale(Math.abs(rect[2]), Math.abs(rect[3]));
		final Locator locator = saxDispatcher.getDocumentLocator();
		final String systemID = locator.getSystemId();
		int lastSlash = systemID.lastIndexOf('/');
		String URL = systemID.substring(0, lastSlash+1)+atts.getValue(STR_ATTR_HREF);
		URL url = new URL(URL);
		//getImage(url); is an Applet method. If you have an application, you can use: 
		drawImage(graph, P1.getX(), P1.getY()+P2.getY(), P2.getX(), -P2.getY(), url);
	}
	
	/**
	 * paints a Line
	 * @param atts Attrributes for Height, Width etc. 
	 */
	public void line(final Attributes atts) {
		double[] rect = new double[4];
		GET_LINE(rect, coords, atts);
		graph.drawLine(
			getTrafo(null).mapPt(rect[0], rect[1]),
			getTrafo(null).mapPt(rect[2], rect[3]));
	}
	
	/** only for transferring the Position to the second Event */
	private String textAnchor;
	
	/** only for transferring the Position to the second Event */
	private double[] textPoint = new double[2];
	
	/**
	 * paints an Image Object
	 * optionally sets the Text Attributes 
	 * caches the Start Position
	 * cannot cache the Attributes, because it is being reused
	 * @param atts Attrributes for Height, Width etc. 
	 */
	public void text(final Attributes atts) {
		GET_POINT(textPoint, coords, atts, "", 0);
		textAnchor = atts.getValue("text-anchor");
	}
	
	/** Actually draws the Text collected in the Element */
	public void text() {
		final Point2D pt = getTrafo(null).mapPt(textPoint);
		final String str = saxDispatcher.buffer.toString(); 
		L.l("text:").n(str);
		if (textAnchor == null) {
		} else if (textAnchor.equals("start")) { //Default
		} else if (textAnchor.equals("middle")) {
			FontMetrics metrics = graph.getFontMetrics();
			pt.x-=metrics.stringWidth(str)/2; 
		} else if (textAnchor.equals("end")) {
			FontMetrics metrics = graph.getFontMetrics();
			pt.x-=metrics.stringWidth(str); 
		} else if (textAnchor.equals("inherit")) { //inherit from Parent Object
		}
		graph.drawString(str, pt);
	}
	
	/**
	 * paints a Polygon 
	 * The Polygon is too tedious, a single Attribute has to be parsed
	 * @param atts Attrributes for Height, Width etc. 
	 */
	public void polygon(final Attributes atts) {
		;
	}
	
	/**
	 * paints a Polyline 
	 * The Polyline is too tedious, a single Attribute has to be parsed
	 * @param atts Attrributes for Height, Width etc. 
	 */
	public void polyline(final Attributes atts) {
		;
	}
	
	/**
	 * the Path is too tedious, a single Attribute has to be parsed
	 * It defines Operations like 
	 * M = moveto
	 * L = lineto
	 * H = horizontal lineto
	 * V = vertical lineto
	 * C = curveto
	 * S = smooth curveto
	 * Q = quadratic bezier curve
	 * T = smooth quadratic bezier curveto
	 * A = elliptical arc
	 * Z = closepath
	 * 
	 * @param atts
	 */
	public void path(final Attributes atts) {}
	
	/** 
	 * Description Element, is a NOP
	 * @param atts
	 */
	public void desc(final Attributes atts) {
		;
	}
	
	/**
	 * Anchor Elements, ignored for now...
	 * @param atts
	 */
	public void a(final Attributes atts) {
		;
	}
	
	/** 
	 * Grouping of Graphs for local Transformations
	 * @param atts
	 */
	public void g(final Attributes atts) {
		int index; 
		if (0 <= (index = atts.getIndex(STR_ATTR_FONT_SIZE))) {
			float fontSize = Float.parseFloat(atts.getValue(index));
			Font font = graph.getFont();
			if (font != null) {
				//FontMetrics metrics = graph.getFontMetrics();
				//L.l("FontHeight").l(metrics.getHeight()).n();
				L.l("SizeParam").l(fontSize).n();
				L.l("FontSize").l(font.getSize2D()).n();
				graph.setFont(
					font.deriveFont(fontSize*Math.abs(
						getTrafo(null).getScaleY()))); 
			} else {
				//graph.setFont()
			}
		} 
	}
	
	/** 
	 * for Usage as an Applet. 
	 * Unfortunately the XML Parser tries to read the forbidden Property user.dir 
	 * and the Web Server does not 
	 * @see java.applet.Applet#init()
	 */
	public void init() {
		if (svgUri == null) {
			svgUri = getParameter("svgFile");		
		}
		super.init();
	}
		
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Demonstrates streaming a parsed XML File
	 */
	final static public void testIt(final String[] args) throws Exception {
	}
	
	/**
	 * Launches this class as a standalone Frame displaying the SVG file given as {@code args[0]}.
	 * @param args URLs to indicate the Input(args[0]), TrafoXSL(args[1]), Output(args[2])
	 * The URLs can also be absolute or relative FileSystem Paths! ^
	 * e.g. java technology.xml.XslTrafo
	 */
	public static void main(final String[] args) throws Exception { //
		System.out.println("Displaying File:"+args[0]);
		//display()
		showApplet(new SvgApplet(args[0]));
		
	}

}
