package graphic.implement;

import graphic.Graph2D;
import graphic.Point2D;

import java.awt.Color;
import java.awt.Graphics;

/** Graphics Context that uses Dithering 
 */
public class GrayColor
extends Graph2D {
	
	int[][] grayMatrix;
	Color[] grayPalette;
	int grayShift;
	int grayMaske;

	/**Binary Value for masking the higher Bits of the x and y Coordinates	 */
	int koordMaske;

	/**Maximum Value for the GrayShift_:	 */
	byte maxGrayShift = 15;

	/**Initializing Constructor	with Default Clipping Area  */
	public GrayColor(final Graphics g){ super (g); }

	/**Constructor that defines a Clippíng Area		 */
	public GrayColor(final Graphics g_, final Point2D ClipTL_, final Point2D ClipBR_){
		super (g_, ClipTL_, ClipBR_); }

	/**Generates the Gray Matrix of given GrayShift_ <=4.	 */
	public void init(final Color[] GP, byte grayShift_) {
		if (grayShift_ > maxGrayShift ) {
			grayShift_ = maxGrayShift;} 
		grayShift  = grayShift_;
		koordMaske = (1 << grayShift)-1;
		grayMaske  = (1 << (grayShift << 1))-1;	//Always switching between two neighboring Colors
		grayPalette = GP;
		grayMatrix = new int[1+koordMaske][1+koordMaske];
		grayMatrix[0][0] = 0;
		int i = 0;
		int j = grayShift_-1;
		for (int Z1 = -1; ++Z1 <= j;) {
			for (int Z2 = 2; --Z2 >= 0;) { //Values 1 and 0 for the Rows
				for (int Z3 = 2; --Z3 >= 0;) {  //Values 1 and 0 for the Columns
					initPass(i, Z2 << Z1, Z3 << Z1, ((Z2 <<  1) + Z3*3) & 3);
				}
			}
			i = ((i+1) << 1)-1;	//extend the BitMask, so it contains 0001 0011 0111...
		}
	}

	/** @see #init(Color[], byte) uses this Method exclusively 	 */
	private void initPass(int i, final int x0, final int y0, final int increment) {
		for (int Z4 = -1; ++Z4 <= i;) {
			for (int Z5 = -1; ++Z5 <= i;) {
				grayMatrix[Z4+x0][Z5+y0] = grayMatrix[Z4][Z5] << 2 + increment;
			}
		}
	}

	int farbCode;
	int FarbMask;

	public void setColor(int farbe) {
		farbCode = farbe >>> (grayShift << 1);
		FarbMask = farbe &	  grayMaske;
	}

	public void setPixel() {
		int fCode = farbCode;
		if (FarbMask >= grayMatrix[P.getX() & koordMaske][P.getY() & koordMaske]) {
			++fCode; } 
		setColor (grayPalette[fCode]);
		super.setPixel ();
	}

}
