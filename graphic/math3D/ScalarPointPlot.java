package graphic.math3D;

import function.IMeasurAble;
import graphic.Figures;
import graphic.IGraphShape;
import graphic.Point2D;

import java.awt.Color;
/**Helper Class that encapsulates Drawing Balls symbolizing scalar Values in 3 Dimensions.
 * This Class is supposed to be called by the recursive Stepper Routine.
 */
public class ScalarPointPlot
	extends ASpatial {

	/**Switches the Sizing of the Scalar Balls on	 */
	public boolean SizeMode;

	/**Switches the Coloring of the Scalar Balls on	 */
	public boolean ColorMode;

	/**Scaling for the Color the Arrows are drawn, using ColorMode = true. */
	public float ColorFactor = 255;

	/**Radius of the Points drawn, when using fixed Size,
	 * resp. Factor for Scaling	the Size when SizeMode = true.  */
	public float Radius = 5;

	/**palette for Scalar Plots. If the palette is empty (like at the Start),
	 * the Colors are generated from the Integer Ordinates directly.	 */
	public Color[] Palette;

	/**Offset for the Colors of this palette.
	 * This Offset is being added to each Ordinate giving the Index of the Color.	 */
	public int ColorOffset;

	/**Graphics Context to point to	 */
	private IGraphShape g;

	/**Reference to the Coordinate System for Conversion	 */
	private ICoordMapper CD;

	/**Constructor, taking the Color Range and the Radius Factor,
	 * that control the coloring as well as the Sizing.
	 */
	public ScalarPointPlot(IGraphShape g2D, ICoordMapper CD_) {
		CD = CD_; g = g2D; }

	/**Resets the given Coordinate of the Algorithm	 */
	public void reSet(int dim) {
		switch (dim) {
			case -3: break;	//should not happen
/*			case -2:  xIndex = 0; yIndex = -1; break;	//Resetting the (x) Row.
			case -1: connect = false; break;	//Resetting the (x) Row.
			case 0: xIndex = 0; yIndex++; break;	//Resetting the (x) Row.
			case 1: xIndex = 0; yIndex = -1; break;	//resetting the whole Plot Action, must happen last!
*/			case 2: break;	//should not happen
		}
	}

	/**draws the Scalar Value S at the Position V with the Parameters given in the Constructor	 */
	public void moveTo(float[] V, Object S) {
		//Raster zeichnen, in welches die Punkte eingebettet werden.
/*		IF{(P1[1] = 1) AND KStart [1] AND}
		   (P1[2] = 1) AND KStart [2] AND  //{nur einmal pro x-Wert}
		   (P1[3] = 1) AND KStart [3])
		 {
		  SetColor (Farben.Raster);
		  L_Raster3 (V [2],V [3],V3 [1],PM,BRaster [2],BRaster [3]); //{nur ein einziges Mal}
		 }
*/
		Point2D P1 = CD.mapPt(V);
		float R = Radius;
		if (SizeMode) {
			R *= Math.abs(((IMeasurAble)S).getFloat());	//Last Dimension has been inversed...
			if (((Coordinates3D) CD).project) {
				R *= ((Coordinates3D) CD).zCoordInv(); }	//and can be used to calculate Scaling
		}
		Color col = null;
//		Color colBuf = null;
		if (ColorMode) {
			int c = (int) (ColorFactor*((IMeasurAble)S).getFloat());
			if (Palette == null) {
				g.setColor(col = new Color(c + ColorOffset));
			} else {
				g.setColor(col = Palette[ (c + ColorOffset) % Palette.length]); }
		}
/*		if (BRaster [1]) {	//Redraw the Raster using the Point Cache
			SetColor (Farben.Raster);	//...that's why I skipped it!
			PZ1 = PunktSpeicher[MI (sT.Reihen,P1[2],P1[3])];
			if (! KStart [1]) { //This Cache can have one dimension less than the painted Area, ...
				Z1 = PZ1.x-Pu.x;
				Z2 = PZ1.y-Pu.y;
				Z3 = ABS (Z1)+ABS (Z2); //Maximums-Norm, egal
				if (Z3 > 0) {
					DEC (PZ1.x,(R*Z1) DIV Z3); //Korrektur wegen Volumen
					DEC (PZ1.y,(R*Z2) DIV Z3); //der Kugel,die den Anfang verdeckt
					Line (PZ1.x,PZ1.y,Pu.x,Pu.y); //connect the Points minus their Radius!
				}
			}
			PZ1 = Pu;
		}
*/		Figures.Ball3D(g, P1, (int) R, col); //R is positive!!!
	}
}
