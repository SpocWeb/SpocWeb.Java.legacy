package graphic.example;

import graphic.IRaster;
import graphic.Point2D;

/**
 * Title: Plasma<p>
 * Description:
 * Purpose:
 * Class encapsulating the Algorithm to generate a 2D Fractal scalar Distribution 
 * ("Plasma")
 * 
 * @see graphic.example.Erosion for a different Algorithm to generate 2D Height Maps. 
 *
 * Known SubClasses:
 *
 * Known Uses:
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	07-07-2002, 09:50 AM<p>
 * @author 	Matthias Heuer
 * @version	1.0
 */
public class Plasma
implements IRaster {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Storage of the Picture in the Plasma Diagram */
	protected int[][] Picture;

	/*  The currently calculated TestPoint */
	protected Point2D TP  = new Point2D();

	/*  Helper for storing the Step Sizes */
	protected Point2D MR  = new Point2D();

	/*  The total Width of the Raster */
	protected Point2D Width; // = new Point2D();

	/** Step Size of the current raster */
	protected int SR;

	/** Bit Mask of the current raster */
	protected int Mask;

	/** initial Scale for the Color in the Plasma Diagram */
	protected final int initScale;

	/** current Scale for the Color in the Plasma Diagram */
	protected int FarbStep;

	/** Flag whether to decrement the Random Scale with the Size Scale */
	boolean decScale = true;

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor	 */
	protected Plasma(final int initScale_, final boolean decScale_) {
		this.initScale = initScale_;
		this. decScale =  decScale_;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface : Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Called whenever the Raster is refined
	 * to synchronize (stateful) Calculation Routines.
	 * This Routine is actually restartable,
	 * so this Instance can be used on multiple different Samples
	 * but with the same initial Scale!
	 */
	public void setRaster(final int RasterSize, final int Mask_, final int[][] Picture) {//, Point2D Width_) {
		if (this.Picture != Picture) { //Neustart!
			initPicture(Picture); }
		if((FarbStep > 1) && decScale) { //linearly decrease Random Scale
			FarbStep--; } //Rest von 1 soll bleiben!
		this.SR    = RasterSize;
		this.Mask  = Mask_; }

	/** initializes the 2D Picture with a random Values 	*/
	private void initPicture(final int[][] Picture) {
		this.Picture  = Picture; //0. Pixel extra setzen,hier wichtig !
		this.Picture [0][0] = 1+(int) (Math.random()*(1 + FarbStep + FarbStep));
		this.FarbStep = this.initScale;
		this.Width =  new Point2D(
			Picture   .length,
			Picture[0].length);
	}

	/**
	 * Generates a 2-dimensional integer Array with pseudorandom integer Numbers (Fractal).
	 * Maintains the range of Values (Minimum in x, Maximum in y)
	 * This Algorithm can easily be extended to work in any number of Dimensions and with float Point Values.
	 * Useful for Generation of Pseudo-random Numbers in multiple Dimensions.
	 * @return the Color for the given 2D Plasma Point
	 */
	final public int getValue(final Point2D SF) {
		int farbe = 1+ (int) (Math.random()*(1 + FarbStep + FarbStep));	//Start Value is a random Number
		if (Mask >= 150) {	//Start InterPolation only within a certain Range.
			return farbe; }
		MR.setX(SR); if (((SF.getX() & Mask) == 0)) {
		MR.setX(MR.getX() + SR); } //nur die alten Punkte verwenden
		TP.setX(SF.getX()-MR.getX());     //=> kein Zieh-Effekt
		for (int i = 0 ; i <= 1; i++) { 	//Calculate the Average from the Rectangle around this Point
			if ((TP.getX() < 0            )) { TP.setX(TP.getX() + Width.getX()); } //periodischer Schluss
			if ((TP.getX() >= Width.getX())) { TP.setX(TP.getX() - Width.getX()); } //der x- Koordinate!
			MR.setY(SR); if (((SF.getY() & Mask) == 0)) { MR.setY(MR.getY() + SR); }
			TP.setY(SF.getY()-MR.getY());
			for (int j = 0 ; j <= 1; j++) {
				if ((TP.getY() < 0            )) { TP.setY(TP.getY() + Width.getY()); } //periodischer Schluss
				if ((TP.getY() >= Width.getY())) { TP.setY(TP.getY() - Width.getY()); } //der y- Koordinate!
				farbe += Picture [TP.getX()][TP.getY()]; //sum up all neighboring Colors
				TP.setY(TP.getY() + (MR.getY() << 1));
			}
			TP.setX(TP.getX() + (MR.getX() << 1));	//Use all four of the surrounding Points
		}
		return (farbe-FarbStep) >> 2; } //Mittelwert = Average = Sum / 4

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Plasma.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}
