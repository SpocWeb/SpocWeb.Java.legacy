package graphic.math3D;

import graphic.IGraphShape;
import graphic.PaletteRGB;
import graphic.Point2D;

import java.awt.Color;

/*
import Ring.*;
import Metric.*;
import Vector.*;
import Graph2D.*;
*/
/**Class for drawing real 3D Columns (solid or wire)
 * and Points within this Column.	 */
public class Column3D {

	/**Graphics Context to paint to	 */
	protected IGraphShape g;

	/**Reference to the Coordinate System for Conversion	 */
	protected Coordinates3D C3D;

	/**Constructor, taking the Color Range and the Radius Factor,
	 * that control the coloring as well as the Sizing. 	 */
	public Column3D(IGraphShape g2D, Coordinates3D C3D_) {
		C3D = C3D_; g = g2D; }

	/////////////////////////////////////////////////////////////////////////////////////

	/**Draws the Projection Lines of the Normals for this Point P
	 * on the Column Surface defined by V1 and V2.
	 * @param P is the Point to display
	 * @param V1 is the 'left'  Corner of the (Hyper-) Cube to project on
	 * @param V2 is the 'right' Corner of the (Hyper-) Cube to project on
	 * @param project determines which Helper Lines are displayed:
	 * For each Dimension the corresponding Bit set determines the Projection Line
	 * @see Figures3D.drawPoint
	 * @see Column3D.drawPoint
	 */
	public void drawPoint(float[] P, float[] V1, float[] V2, int project) {
		Point2D P1;
		float[] T1 = new float[3];
		int i = -1, len = V1.length;
		while (++i < len) {	//12 = 3*2*2
		//	if((project == null) ||
			if((project & (1 << i)) != 0) {
				int j = i+1; if (j > 2) j = 0;
				int k = 3-i-j;	//No need for loops here, doesn't pay off with only 2 Values per Loop!
				T1[k] = P [k];
				T1[i] = V1[i]; //of course you have to put the two Vectors into an Array
				T1[j] = V1[j];             P1= C3D.mapPt(T1) ;
				T1[i] = V2[i]; g.drawLine (P1, C3D.mapPt(T1));
				T1[j] = V2[j]; g.drawLine (    C3D.mapPt(T1));
				T1[i] = V1[i]; g.drawLine (    C3D.mapPt(T1));
							   g.drawLine (P1);
			}
		}
	}

	/**Draws a 3dimensional Wire-Rectangle with the Corners V1 and V2
	 * in the current color. 	 */
	public void drawColumn3D (float[] V1, float[] V2) {
		float[] T1 = new float[3];
		float[] T2 = new float[3];
		int i = -1;
		while (++i <= 2) {	//12 = 3*2*2
			int j = i+1; if (j > 2) j = 0;
			int k = 3-i-j;	//No need for loops here, doesn't pay off with only 2 Values per Loop!
			T1[k] = V1[k];
			T2[k] = V2[k]; //This is where the two loops could start:
			T1[i] = T2[i] = V1[i]; //of course you have to put the two Vectors into an Array
			T1[j] = T2[j] = V1[j]; g.drawLine (C3D.mapPt(T1), C3D.mapPt(T2));
			T1[i] = T2[i] = V2[i]; g.drawLine (C3D.mapPt(T1), C3D.mapPt(T2));
			T1[j] = T2[j] = V2[j]; g.drawLine (C3D.mapPt(T1), C3D.mapPt(T2));
			T1[i] = T2[i] = V1[i]; g.drawLine (C3D.mapPt(T1), C3D.mapPt(T2));
		}
	}

	/**Fills a 3dimensional Rectangle with Corners V1 and V2 (hidden Planes)
	 * in the current color.
	 * The shading effect is created by using the standard darker / brighter Methods
	 * of the Color Class.	 */
	public void fillColumn3D (float[] V1, float[] V2) {
		Color[] c = PaletteRGB.SHADING_PALETTE(g.getColor());
		float[] V = new float[3];	//V is used to collect the Coordinates
		float[] Z = new float[3];	//Z is used to TODO
		Point2D[][] P = new Point2D[3][4]; //cache the Areas, only for projective Geometry!
		int Z1 = -1; float[] VP = C3D.getProjector().a[0];	//get the ViewPoint
		while (++Z1 < 3) { //{vorderste Ecke herausfinden}
			//keep the Configuration in V1, V2
			float V1a = V1[Z1];	//{nach Abstand zu ordnen ist uebertrieben}
			if  ((V1a < V2[Z1]) !=
				 (V1a > VP[Z1])) {//{nur Reihenfolge}
				V[Z1] = V2[Z1]; Z[Z1] = V1a;
			} else {
				Z[Z1] = V2[Z1]; V[Z1] = V1a; } //und gleich auch in V kopieren.
		}	//{In V steht am Ende der naechstliegende Punkt, in Z der entfernteste}
		P[0][0] = P[1][0] = P[2][0] = C3D.mapPt(V);
		float max = Float.NEGATIVE_INFINITY; int iMax = 0, jMax = 1; //, kMax = 2;	//search for the nearest Point
		float min = Float.NEGATIVE_INFINITY; int iMin = 0, jMin = 1; //, kMin = 2;	//= greatest inverse Distance
		int i = -1;	//exchange up to two Points in the front Vector.
		while (++i <= 2) {	//12 = 3*2*2
			//Sequence: Top (x,y), Left (y,z), Right (z,x)
			int j = i+1; if (j > 2) j = 0;
//			int k = 3-i-j;	//No need for loops here, doesn't pay off with only 2 Values per Loop!
			float iTmp = V[i]; //it can happen with projective Geometry,
			float jTmp = V[j];	//that the third Side is hidden!
			V[i] = Z[i]; P[i][1] = C3D.mapPt(V); float sum = (float) C3D.zCoordInv();
			V[j] = Z[j]; P[i][2] = C3D.mapPt(V); sum += C3D.zCoordInv();
			V[i] = iTmp; P[i][3] = C3D.mapPt(V); sum += C3D.zCoordInv();
			V[j] = jTmp; //V is restored!
			if (i == 0) { max = sum; min = sum;
			} else if (C3D.project) {
				if (max < sum) { max = sum; iMax = i; jMax = j; } //kMax = k; }
				if (min > sum) { min = sum; iMin = i; jMin = j; } //kMin = k; }
			}
//			if (  c3D.project && (P[0].AreaTriangle(P[1], P[2]) > 0)) continue;	//doesn't work, because Sides are not ordered
			if (! C3D.project) {g.setColor(c[j]); g.fillPolygon(P[i]);}
		}
		if (C3D.project) {	//To prevent the third (hidden) Side to pop up, paint the Polygon with the nearest Point last
			g.setColor(c[       jMin]); g.fillPolygon(P[        iMin]);
			g.setColor(c[3-jMax-jMin]); g.fillPolygon(P[3 -iMax-iMin]);
			g.setColor(c[       jMax]); g.fillPolygon(P[        iMax]);
		}
	}

}
