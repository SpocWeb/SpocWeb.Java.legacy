package graphic.math2D;

import function.Projections;
import graphic.AGraphText;
import graphic.IGraphShape;
import graphic.IGraphText;
import graphic.JavaGraphic;
import graphic.Point2D;
import graphic.mvc.BaseApplet;
import graphic.mvc.IPainter;

import java.awt.Graphics;
import java.awt.Rectangle;

import streamIO.object.parser.jdbc.ResultSetSep;

/**This class reads PARAM tags from its HTML host page and sets
 * the color and label properties of the applet. Program execution
 * begins with the init() method.
 */
public class testMathGraph2 
implements IPainter { //

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////

	/** Size of the Graphic Area     */
	final static public int WIDTH = 1024;

	/** Size of the Graphic Area     */
	final static public int HEIGHT = 768;

	////////////////////////////////////////////////////////////////////////////
	/// #region : Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**The entry point for the Applet/Form. 	 */
	public void init() {
		// TODO: Add any constructor code after initForm call.
	}

	/**Callback for painting the Applet,
	 * try Block for catching the Exceptions
	 */
	public void paint(Graphics g) {
		try {
			paint1(g);
		} catch (Exception t) {
			System.out.println(t);
		}
	}

	/** State Counter for testing several Functions.     */
	private int state;

	/** Projecting Function to achieve different Maps.     */
	//	private IFunction Project;

	/** Actual painting Routine... delegated from the paint() Method 	 */
	private void paint1(Graphics g) {
		final AGraphText g2D = new JavaGraphic(g);
		draw(g2D); 
	}
	
	/** Projecting Function to achieve different Maps.     */
	//	private IFunction Project;
	
	/**Actual painting Routine... delegated from the paint() Method  
	 * @see graphic.mvc.IPainter#draw(graphic.IGraphText)	 */
	public void draw(final IGraphText g2D) {
		final Coordinates2D C2D 
		= new Coordinates2D(-3.15f, +3.15f, -1.6f, +1.6f, g2D.getClipBounds());
		//		g2D.setColor(Color.red); //not necessary, black by Default.
		Projections Project1 = new Projections(Projections.Deg2Rad);
		Projections Project2 = new Projections(Projections.Cyl_Netz); //Mollweide);
		String FileName;
		Point2D Pt;
		float[] NetPt2 = new float[2];
		//		Polygon3D Poly;
		switch (state) {
			case 1 :
				paintRaster(g2D, g2D.getClipBounds());
				break;
			case 0 :
				paintRaster(g2D, g2D.getClipBounds());
				//				Polygon2D P2D = null;
				boolean start = true;
				FileName = "../../Databases/Maps/Pol/AFRIKA0.POL";
				try {
					ResultSetSep RS = new ResultSetSep(FileName);
					RS.next();
					RS.next(); //skip Min and Max
					while (RS.next()) {
						NetPt2[0] = RS.getFloat(0);
						NetPt2[1] = RS.getFloat(1);
						NetPt2 = (float[]) Project1.Map(NetPt2);
						NetPt2 = (float[]) Project2.Map(NetPt2);
						Pt = C2D.mapPt(NetPt2);
						if (start) {
							g2D.setPixel(Pt);
							start = false;
							//System.out.println(NetPt2[0] + ", " + NetPt2[1] + ") (" + Pt.x + ", " + Pt.y);
						} else {
							g2D.drawLine(Pt);
						}
					}
					/*					ST = new StreamTokenizer(new FileInputStream(FileName));
										ST.eolIsSignificant(true);	//Otherwise it doesn't work Row-based!
										while (ST.ttype != ST.TT_EOF) {
											P3D = new Polygon3DGraph(ST, tmp);
											Object[] Pts = P3D.getPointsAt();
											Project.map(Pts);
											P2D = P3D.getPolygon2D(C2D);
											P2D.draw(g2D, false);
										}
					*/
				} catch (Exception t) {
					System.out.println(t.toString());
					t.printStackTrace();
				}
				break;
			case 2 : //Drawing the Stars
				paintRaster(g2D, g2D.getClipBounds()); //??
				FileName = "G:\\Personal\\Databases\\Astro\\FIXSTERN.DAT";
				/*				try{
									ST = new StreamTokenizer(new FileInputStream(FileName));
									ST.eolIsSignificant(true);	//Otherwise it doesn't work Row-based!
									Body3 = new Body3DGraph(ST, tmp, true, true);
								}catch (Exception t) {g.drawString(t.toString(), 100, 100);}
								Object[] Pts = Body3.getPointsAt();
								Project.map(Pts);	//scale the Points to the +- Pi Range
								Body2D B2D = Body3.getBody2D(C2D);
								B2D.mark(new Marker(g2D, Marker.StarMarker), false, false);
								B2D.drawWire(g2D, true, false);
				*/
				break;
		}

		if ((state > 9) || (state < 0)) {
			//			Body2D drawBody = Body3.getBody2D(c3D);
			//			drawBody.BorderColor = Color.black;
		}
	}

	private static final void paintRaster(final IGraphShape g2D, final Rectangle Bounds) {
		//		Coordinates2D C2D = new Coordinates2D(-180, +180, -90, +90, Bounds);
		Coordinates2D C2D = new Coordinates2D(-3.15f, +3.15f, -1.6f, +1.6f, Bounds);
		Projections Project1 = new Projections(Projections.Deg2Rad);
		Projections Project2 = new Projections(Projections.Cyl_Netz);
		//Albers); //Sinusoidal); //Polar_Stereograph); //Polar_Orthograph); //Polar_Netz); //Polar_Gnomonisch); //Polar_Azimuthal); //Cyl_Stereograph); //Cyl_Orthograph); //Cyl_Netz); //Cyl_Mercator); //Cyl_Gnomonisch); //Cyl_Azimuthal);
		double[] Net = { -180, -90 };
		double[] NetPt = new double[2];
		double[] NetPt2 = new double[2];
		NetPt[0] = Net[0] - 10;
		int l = -19;
		while (++l <= 18) {
			NetPt[0] += 10;
			NetPt[1] = Net[1] - 10;
			int b = -10;
			while (++b <= 9) {
				NetPt[1] += 10;
				System.arraycopy(NetPt, 0, NetPt2, 0, 2);
				NetPt2 = (double[]) Project1.Map(NetPt2);
				NetPt2 = (double[]) Project2.Map(NetPt2);
				Point2D Pt = C2D.mapPt(NetPt2);
				if (b > -9) {
					g2D.drawLine(Pt); //System.out.println(NetPt2[0] + ", " + NetPt2[1] + ") (" + Pt.x + ", " + Pt.y);
				} else {
					g2D.setPixel(Pt);
				}
			}
		}
		NetPt[1] = Net[1] - 10;
		l = -10;
		while (++l <= 9) {
			NetPt[1] += 10;
			NetPt[0] = Net[0] - 10;
			int b = -19;
			while (++b <= 18) {
				NetPt[0] += 10;
				System.arraycopy(NetPt, 0, NetPt2, 0, 2);
				NetPt2 = (double[]) Project1.Map(NetPt2);
				NetPt2 = (double[]) Project2.Map(NetPt2);
				Point2D Pt = C2D.mapPt(NetPt2);
				if (b > -18)
					g2D.drawLine(Pt);
				else
					g2D.setPixel(Pt);
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) { //throws Exception {
		BaseApplet.display(new testMathGraph2());
	}

}
