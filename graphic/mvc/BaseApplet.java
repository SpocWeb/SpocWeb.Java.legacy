/*
 * File Name: BaseApplet.java
 * Created on: 07.12.2003
 *
 */
package graphic.mvc;

import graphic.IGraphImage;
import graphic.JavaGraphic;

import java.applet.Applet;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.net.URL;

import streamIO.Log;

/**
 * Title: BaseApplet<p>
 * Description:
 * Base Class for Applets and Forms which are able to draw complex Models.
 * Creates it's own Drawing Context, if not given one, by instantiating a Form.  
 * Implements no Double Buffering, but Abstraction from the Applet Interface 
 * by implementing IActiveCanvas 
 *
 * Known SubClasses: 
 * @see graphic.svg.SvgApplet
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class BaseApplet 
extends Applet //Frame //Applet has the Advantage of being embeddable!
implements IActiveCanvas {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Default Size for a new Frame2DPoint */
	public static char DEFAULT_WIDTH = 640;
	
	/** Default Size for a new Frame2DPoint */
	public static char DEFAULT_HEIGHT = 480;
	
	/** streamIO for Logging */
	private static final Log L = new Log(BaseApplet.class, 1);
	
	////////////////////////////////////////////////////////////////////////////
	/// static Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	final static public void showApplet(final Applet applet) {
		L.n("Loading " + applet.getClass().getName());
		final Frame f = new Frame("test");
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent e) {
				L.n("exiting Application");
				f.dispose(); 
				//System.exit(0);
			}
		});
		f.add("Center", applet);
		applet.init();
		f.pack();
		f.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		f.show();
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Utilities for loading Graphic Files into RAM 
	/////////////////////////////////////////////////////////////////////////////////////
	
	private static final String[] ALLOWED_SUFFIXES={".JPG", ".GIF", ".PNG", "JPEG"};

	/** @return null if the Suffix is ok for a Picture File, the Suffix otherwise! */
	final static public String getFaultySuffix(final String fileName) {
		final String suffix =  fileName.substring(fileName.length()-4);
		for (int i = ALLOWED_SUFFIXES.length; --i >= 0; ) {
			if (suffix.equalsIgnoreCase(ALLOWED_SUFFIXES[i])) {
				return null; }
		}
		return suffix; 
	}

	/** @throws RuntimeException if the Suffix is not ok for a Picture File! */
	final static public void checkFaultySuffix(final String fileName) {
		final String suffix = getFaultySuffix(fileName);
		if (suffix != null) {
			throw new RuntimeException(suffix+" is not allowed as an Image File Type in Path "+fileName); }
	}

	/** Utility to asynchronously read and return an Image Object from a File
	 * @see Applet#getImage(java.net.URL) fails due to getAppletContext */
	final static public Image getAsynchImage(final String fileName) {
		checkFaultySuffix(fileName);
		return Toolkit.getDefaultToolkit().getImage(fileName); }

	/** Utility to asynchronously read and return an Image Object from a URL
	 * @see Applet#getImage(java.net.URL) fails due to getAppletContext */
	final static public Image getAsynchImage(final URL url) {
		checkFaultySuffix(url.getPath());
		return Toolkit.getDefaultToolkit().getImage(url); //
	}

	/** Utility to synchronously read and return an Image Object from a File */
	final static public BufferedImage getSynchImage(final String fileName, final Component cmp) {
		return getSynchImage(getAsynchImage(fileName), cmp); }

	/** Utility to synchronously read and return an Image Object from a File */
	final static public BufferedImage getSynchImage(final String fileName) {
		return getSynchImage(getAsynchImage(fileName), null); }

	/** Utility to synchronously read and return an Image Object from a URL */
	final static public BufferedImage getSynchImage(final URL url) {
		return getSynchImage(getAsynchImage(url), null); }

	/** Utility to synchronously read and return an Image Object from a URL */
	final static public BufferedImage getSynchImage(final URL url, final Component cmp) {
		return getSynchImage(getAsynchImage(url), cmp); }

	/** Utility to synchronously read and return an Image Object */
	final static public BufferedImage getSynchImage(final Image img, Component cmp) {
		if (cmp == null) { //Just create ANY lightweight Component Instance
			cmp = new Canvas(); } //without Peer. 
		try {
			final MediaTracker tracker = new MediaTracker(cmp); 
			tracker.addImage(img, 0);
			if (! tracker.waitForID(0, 0)) {
				System.out.println(MediaTracker.ABORTED);
				System.out.println(MediaTracker.COMPLETE);
				System.out.println(MediaTracker.ERRORED);
				System.out.println(MediaTracker.LOADING);
				System.out.println(tracker.statusID(0, true));
				return null; } 
			if (tracker.isErrorAny()) {
				Object[] errors = tracker.getErrorsAny();
				for(int i = errors.length; --i >= 0;) {
					System.out.println(errors[i]);
				}
				return null; 
			}
		} catch ( final InterruptedException e ) {
			e.printStackTrace();
		}
		final int iw = img.getWidth(cmp);
		final int ih = img.getHeight(cmp);
		final BufferedImage ret = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_RGB);
		ret.createGraphics().drawImage(img,0,0,cmp);
		return ret;
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////

	/** Switches on indirect asynchronous repaint()ing */
	public boolean asynchRePaint = false;
	
	/** Switches on clearing before an (active) repaint() */
	public boolean clearBeforeRePaint = true;
	
	/** The Storage to hold (independent) Painters */
	final public MultiPainter painters = new MultiPainter();

	////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods 
	////////////////////////////////////////////////////////////////////////////

	/** @see graphic.mvc.IPaintEventSource#addPainter(graphic.mvc.IPainter)	 */
	public boolean addPainter(final IPainter painter) {
		return painters.addPainter(painter);
	}

	/** @see graphic.mvc.IPaintEventSource#removeRepaintListener(graphic.mvc.IPainter)	 */
	public boolean removePainter(final IPainter painter) {
		return painters.removePainter(painter);
	}

	/** @see graphic.mvc.ICanvas#getIGraphText()	 */
	public IGraphImage getIGraphImage() {
		final Graphics graphics = getGraphics(); 
		if (graphics == null) {
			show(); //load it 
			repaint(); //paint asynchronously
			return null;
		}
		return getIGraphImage(graphics);
	}

	/** @see graphic.mvc.ICanvas#getIGraphText()	 */
	public IGraphImage getIGraphImage(final Graphics g) {
		final IGraphImage graph = new JavaGraphic(g); //
		getClipBounds(g); //make sure it has bounds
		return graph;
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	/// #region : Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	/// #region : Applet Event Methods
	////////////////////////////////////////////////////////////////////////////

	/**
	 * These Events are called only for a Heavyweight Component (Frame). 
	 * Usually works asynchronously. 
	 * This repaint() method causes a call to this component's update method 
	 * 'as soon as possible'.
	 * update() clears the Background and then calls the paint() Method.   
	 * Redirection for logging and tracing the Event Flow. 
	 */
	final public void repaint() {
		assert null!=L.traceStack().n("repaint");
		if (!isDisplayable()) { //getGraphics() == null) {
			show(); //show it the first time
		} else {
			if (asynchRePaint) {
				super.repaint(); //asynchronous
			} else {
				final Graphics g = getGraphics();
				if (clearBeforeRePaint) {
					clear(g); }
				paint(g); //synchronous
			}
		}
	}
	
	/**
	 * These Events are called only for a Heavyweight Component (Frame). 
	 * Redirection for logging and tracing the Event Flow. 
	 * If this component is a lightweight component, 
	 * this method causes a call to this component's paint method as soon as possible. 
	 * Otherwise, this method clears the Background 
	 * and then calls the paint() Method.   
	 */
	final public void update(final Graphics g) {
		assert null!=L.traceStack().n("update");
		if (g == null) return;
		if (clearBeforeRePaint) {
			super.update(g); //possibly clears
		} else {
			paint(g); //skip clearing
		}
	}

	/** clears the Graphics Context */
	protected final void clear(final Graphics g) {
		Rectangle rect = getClipBounds(g);
		g.clearRect(rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * make sure g has Bounds!
	 * @param g Graphics Context possibly without ClipBounds
	 * @return a valid Rectangle Object 
	 */
	private Rectangle getClipBounds(final Graphics g) {
		Rectangle rect = g.getClipBounds();
		if (rect == null) { //null when not repaint()ed!
			rect = new Rectangle(this.getSize()); //ganzen Screen!
			//rect = this.getBounds(); //liefert Offset!
			g.setClip(rect); //set the Clip as if this was a regular repaint() Event
		} 
		return rect;
	}
		
	/**Event- Callback for (re-) painting the Applet or Frame,
	 * triggered by Resizing or (un-)hiding Portions of the Window.  
	 * try Block for catching the Exceptions
	 */
	public void paint(final Graphics g) {
		L.n("paint").l(g); 
		if (g == null) 
			return;
		try {
			//if (this instanceof Applet) { }  //only necessary for Applet (lightweight)
			//clear(g);
			final IGraphImage graph = getIGraphImage(g); //GraphicsAdapter(g); //Graph2D(g); //
			painters.draw(graph);
		} catch (final Exception x) {
			L.n("The following Exception occured during Painting:\n");
				x.printStackTrace(L);
		}
	}
	
	/**
	 * @deprecated due to the Fact that this Method is deprecated in java.awt.Component 
	 */
	public void show() {
		BaseApplet.showApplet(this);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Intercepting the asynch. loading of Images 
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * @see ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
	 * is implemented by Applet. 
	 * This method is called when information about an image, 
	 * which was previously requested using an asynchronous interface,
	 * becomes available. 
	 * To prevent repainting whenever some Bits come across the Line, 
	 * the super Implementation, which triggers a repaint() 
	 * is only called when the Image has finished loading!  
	 * If super. is never called, the Images are only (re-)drawn 
	 * on the (externally triggered) next repaint().
	 */
	public boolean imageUpdate(final Image img, final int infoflags, final int x, final int y, final int width, final int height) {
		boolean ret = true; 
		if (0 != (ImageObserver.ALLBITS & infoflags)) { //don't always call the super() Implementation 
			ret = super.imageUpdate(img, infoflags, x, y, width, height); //only when a whole Image arrived. 
			L.l(ret, 1).n();
		}
		return ret; 
	}
	
	/** asynchronously draws the Image indicated by the Filename onto the given Graphics Context 	 */
	public void drawImage(final IGraphImage g, final int x, final int y, final String fileName) {
		final Image img = Toolkit.getDefaultToolkit().getImage(fileName);
		g.drawImage(img, x, y, this); //also starts loading the Image asynchronously
	}

	/** asynchronously draws the Image indicated by the URL onto the given Graphics Context 	 */
	public void drawImage(final IGraphImage g, final int x, final int y, final URL url) {
		final Image img = getImage(url); //Toolkit.getDefaultToolkit().getImage(url);
		g.drawImage(img, x, y, this);
	}

	/** asynchronously draws the Image indicated by the Filename onto the given Graphics Context 	 */
	public void drawImage(final IGraphImage g, final int x, final int y, final int width, final int height, final String fileName) {
		final Image img = Toolkit.getDefaultToolkit().getImage(fileName);
		g.drawImage(img, x, y, width, height, this);
	}

	/** draws the Image from the given URL */
	public void drawImage(final IGraphImage g, final int x, final int y, final int width, final int height, final URL url) {
		final Image img = getSynchImage(url); //getAsynchImage(url); //getImage(url); 
		g.drawImage(img, x, y, width, height, this); //using 'this' causes a Repaint on finishing Loading of each single Picture!
		//g.drawImage(img, x, y, width, height, null);
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Applet Specific Helper Methods for evaluating Parameters
	////////////////////////////////////////////////////////////////////////////

	private	static final String labelParam = "label";
	private	static final String backgroundParam = "background";
	private	static final String foregroundParam = "foreground";

	/** Size of the Graphic Area     */
	final static public int WIDTH = 1024;

	/** Size of the Graphic Area     */
	final static public int HEIGHT = 768;

	/** Converts a string formatted as "rrggbb" to an awt.Color object	 */
	private static final Color stringToColor(String paramValue) {
		int red  = (Integer.decode("0x" + paramValue.substring(0,2))).intValue();
		int green= (Integer.decode("0x" + paramValue.substring(2,4))).intValue();
		int blue = (Integer.decode("0x" + paramValue.substring(4,6))).intValue();

		return new Color(red,green,blue); }

	/** External interface used by design tools to show properties of an applet.	 */
	final public String[][] getParameterInfo() {
		String[][] info = {
			{ labelParam, "String", "Label string to be displayed" },
			{ backgroundParam, "String", "Background color, format \"rrggbb\"" },
			{ foregroundParam, "String", "Foreground color, format \"rrggbb\"" },
		};
		return info;
	}

	/** Reads parameters from the applet's HTML host and sets applet properties.	 */
	protected void usePageParams() {
//		final String defaultLabel = "Default label";
		final String defaultBackground = "C0C0C0";
		final String defaultForeground = "000000";
//		String labelValue = defaultLabel;
		String backgroundValue = defaultBackground;
		String foregroundValue = defaultForeground;

		/**Read the <PARAM NAME="label" VALUE="some string">,
		 * <PARAM NAME="background" VALUE="rrggbb">,
		 * and <PARAM NAME="foreground" VALUE="rrggbb"> tags from
		 * the applet's HTML host.		 */
		//labelValue = getParameter(labelParam); //only for Applets
		backgroundValue = getParameter( backgroundParam);
		foregroundValue = getParameter(foregroundParam);

		/**Set the applet's background color,
		 * and foreground colors.		 */
		this.setBackground(stringToColor(backgroundValue));
		this.setForeground(stringToColor(foregroundValue));
	}

	/** The entry point for the applet. 	 */
	public void init() {
		L.n("init");
		//usePageParams();
	}

	/**
	 * @see java.applet.Applet#destroy()	 */
	public void destroy() {
		L.n("destroy");
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Low Level Event Callback Methods from the Applet, 
	/// replaced by Subscription of Controllers to an Event Source
	////////////////////////////////////////////////////////////////////////////

	/** Direct Applet Callback, only when Events are enabled 
	 * and unless a Listener is used
	 * @param evt full Key Event Information
	 */
	/* protected void processKeyEvent(final KeyEvent evt) {
		if (evt.getID() != KeyEvent.KEY_PRESSED) {
			//Pressed happens most often, also on Repeat Characters.
			return;
		}
	}
	*/
	/** Just for trapping the Close Command Event	 */
	/*	protected void processWindowEvent(final WindowEvent e) {
			L.n(e.toString());
			switch (e.getID()) {
				case WindowEvent.WINDOW_CLOSING :
					this.dispose();
					break;
				case WindowEvent.COMPONENT_RESIZED : //strange... only called once!
					lastBounds = this.getBounds(); //currBounds;
					break;
				default :
					break;
			}
			super.processWindowEvent(e); //hand over to the Standard Dispatcher, otherwise doesn't work properly. 
		}
	*/
	/** Store the Start Position	*/
	/*protected void processMouseEvent(MouseEvent evt) {
		L.n(evt);
		System.out.println("before mapPoints.getAt(6)=" + mapPoints.getAt(6));
		int x = evt.getX();
		int y = evt.getY();
		switch (evt.getID()) {
			//case MouseEvent.MOUSE_ :  pressedMouse(x, y); break;
			case MouseEvent.MOUSE_PRESSED :
				pressedMouse(x, y);
				break;
			case MouseEvent.MOUSE_RELEASED :
				if (evt.getClickCount() == 2) {
					doubleClickMouse(x, y);
				} else {
					releasedMouse(x, y);
				}
				break;
			default :
				return;
		}
		lastPosition.setX(x);
		lastPosition.setY(y);
		System.out.println("after mapPoints.getAt(6)=" + mapPoints.getAt(6));
		return;
	}
	*/
	/** Use the Difference to the Start Position to rotate the ViewPoint around 0	*/
	/*protected void processMouseMotionEvent(MouseEvent evt) {
		int x = evt.getX();
		int y = evt.getY();
		L.n("processMouseMotionEvent", -1).l(evt.getID(), -1);
		switch (evt.getID()) {
			case MouseEvent.MOUSE_PRESSED :
				break;
			case MouseEvent.MOUSE_DRAGGED :
				dragMouse(x, y);
				break;
			default :
				return;
		}
	}
	*/
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
		
	/** displays all given IPainter Instances (not null, already initialized)	 */
	public static void display(final IPainter[] painters) {
		final BaseApplet canvas = new BaseApplet();
		canvas.setSize(BaseApplet.WIDTH, BaseApplet.HEIGHT);
		for(int i = painters.length; --i >= 0;) {
			if (null != painters[i]) {
				L.n("Adding for Display: " + painters[i].getClass().getName());//testMathGraph2.class.getName()
				canvas.addPainter(painters[i]); //Frame();
			}
		}
		canvas.show();
	}
		
	/** displays the given IPainter Instances (not null, already initialized)	 */
	public static void display(final IPainter painter) {
		display(new IPainter[] {painter}); } 
		
	/** displays all given IPainter Classes	 */
	public static void display(final String[] painterClassNames) throws Exception {
		final IPainter[] painters = new IPainter[painterClassNames.length];
		for(int i = painterClassNames.length; --i >= 0;) {
			final Class cls = Class.forName(painterClassNames[i]);
			if (IPainter.class.isInstance(cls)) {
				painters[i] = (IPainter) cls.newInstance();
			} else {
				L.n("Ignored for Display (no IPainter Interface): " + painterClassNames[i]);//testMathGraph2.class.getName()
			}
		}
	}
		
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		display(args); }
		
}
